package mx.ift.sns.web.backend.cpsn.cesion;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import mx.ift.sns.modelo.cpsn.SolicitudCesionCPSN;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.cpsn.ICodigoCPSNFacade;
import mx.ift.sns.negocio.oficios.IOficiosFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.Wizard;
import mx.ift.sns.web.backend.ng.tabs.GeneracionOficiosTab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Backing Bean para el trámite de Solicitudes de Cesión de CPSN.
 */
@ManagedBean(name = "solicitudCesionCPSNBean")
@ViewScoped
public class SolicitudCesionCPSNBean extends Wizard implements Serializable {

    /** Control Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudCesionCPSNBean.class);

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_Wizard_SolicitudCesionCPSN";

    /** Tab/Pestaña de selección de información genérica de cesiones cpsn. */
    private GeneralesCesionCPSNTab tabGeneralesCesion;

    /** Tab/Pestaña de selección de información de cedente y cesionario. */
    private CesionCPSNDerechosTab tabCesionDerechos;

    /** Servicio de Catalogos. */
    @EJB(mappedName = "CodigoCPSNFacade")
    private ICodigoCPSNFacade codigoCPSNFacade;

    /** Facade de Generación de Oficios. */
    @EJB(mappedName = "OficiosFacade")
    private IOficiosFacade oficiosFacade;

    /** Tab/Pestaña de selección de información del CPSN. */
    private CesionCPSNTab tabCesionCPSN;

    /** Tab/Pestaña de selección de generación de oficios. */
    private GeneracionOficiosTab tabGenerarOficio;

    /** Constructor, inicializa el Wizard. */
    public SolicitudCesionCPSNBean() {
        setWizardMessagesId(MSG_ID);
    }

    /** Inicializa el Bean y la lista de pasos del Wizard. */
    @PostConstruct
    public void init() {
        // Pestaña 'Generales'
        tabGeneralesCesion = new GeneralesCesionCPSNTab(this, codigoCPSNFacade);
        tabGeneralesCesion.setId("Cesión Solicitudes - TabGenerales");
        getPasosWizard().add(tabGeneralesCesion);

        // Pestaña 'Cesión Derechos'
        tabCesionDerechos = new CesionCPSNDerechosTab(this, codigoCPSNFacade);
        tabCesionDerechos.setId("Cesión Solicitudes - TabCesionDerechos");
        getPasosWizard().add(tabCesionDerechos);

        // Pestaña 'Cesión CPSN'
        tabCesionCPSN = new CesionCPSNTab(this, codigoCPSNFacade);
        tabCesionCPSN.setId("Cesión Solicitudes - TabCesionCPSN");
        getPasosWizard().add(tabCesionCPSN);

        // Tab de Generación de Oficios
        tabGenerarOficio = new GeneracionOficiosTab(this, oficiosFacade);
        tabGenerarOficio.setId("Cesión Solicitudes CPSN - TabOficios");
        getPasosWizard().add(tabGenerarOficio);
    }

    /** Método invocado al pulsar sobre el botón de 'Nueva Cesión'. */
    public void nuevaSolicitud() {
        try {
            // Iniciamos el tipo de solicitud específico.
            this.setSolicitud(new SolicitudCesionCPSN());

            // Actualizamos los campos de tabGenerales por ser el primer tab que aparece. El resto
            // de tabs se actualizarán al pulsar 'siguiente', el Wizard se encarga de invocar al
            // método 'actualizaCampos' de cada tab. Lo mismo ocurre con el método 'cargaValoresIniciales'
            if (!tabGeneralesCesion.isIniciado()) {
                tabGeneralesCesion.cargaValoresIniciales();
                tabGeneralesCesion.setIniciado(true);
            }
            tabGeneralesCesion.setTabHabilitado(true);
            tabCesionDerechos.setTabHabilitado(true);
            tabCesionCPSN.setTabHabilitado(true);
            tabGenerarOficio.setTabHabilitado(true);
            tabGeneralesCesion.actualizaCampos();
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Precarga una solicitud existente para su edición. Actualiza las referecnias del Wizard. para el resto de tabs.
     * @param pSolicitud SolicitudCesionCPSN
     */
    public void cargarSolicitud(SolicitudCesionCPSN pSolicitud) {
        try {
            // Cargamos las dependencias Lazy (Cesiones Solicitadas, Aplicadas y Oficios)
            SolicitudCesionCPSN solicitudLoaded = codigoCPSNFacade.getSolicitudCesionEagerLoad(pSolicitud);

            // Asociamos la solicitud cargada en al Wizard
            this.setSolicitud(solicitudLoaded);

            // Actualizamos los campos de tabGenerales por ser el primer tab que aparece.
            if (!tabGeneralesCesion.isIniciado()) {
                tabGeneralesCesion.cargaValoresIniciales();
                tabGeneralesCesion.setIniciado(true);
            }
            tabGeneralesCesion.actualizaCampos();

            // Acciones en función del estado de la solicitud
            if (solicitudLoaded.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_EN_TRAMITE)) {
                tabGeneralesCesion.setTabHabilitado(true);
                tabCesionCPSN.setTabHabilitado(true);
                tabCesionDerechos.setTabHabilitado(true);
                tabGenerarOficio.setTabHabilitado(true);
            } else if (solicitudLoaded.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_TERMINADA)) {
                // La solicitud ya está terminada, no se permite editar generales.
                tabGeneralesCesion.setTabHabilitado(false);
                tabCesionDerechos.setTabHabilitado(false);
                tabCesionCPSN.setTabHabilitado(true);
                tabGenerarOficio.setTabHabilitado(true);
            } else {
                // La solicitud está cancelada, no se permite hacer nada con ella.
                tabGeneralesCesion.setTabHabilitado(false);
                tabCesionDerechos.setTabHabilitado(false);
                tabCesionCPSN.setTabHabilitado(false);
                tabGenerarOficio.setTabHabilitado(false);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    @Override
    public void resetTabs() {

        // Deberíamos poder actualizar el Wizard directamente en JSF sin tener que traernos
        // directamente el componente, pero el Binding no funciona al ser diferentes vistas (dejan
        // de funcionar los callbacks).

        // El siguiente código resetea el Wizard asociado al xhtml asociado a su vez al Bean para
        // que se abra siempre la primera pestaña.

        // Restablecemos el Wizard en la primera pestaña
        org.primefaces.component.wizard.Wizard jsfWizard;
        jsfWizard = (org.primefaces.component.wizard.Wizard) FacesContext.getCurrentInstance().getViewRoot()
                .findComponent("FORM_SolicitudCesionCPSN:WIZ_SolicitudCesionCPSN");

        if (jsfWizard != null) {
            jsfWizard.setStep("WIZ_SolicitudCesionCPSN_TAB_Proveedor");
            this.resetNumPaso();
        }

        // Limpiamos la variables de cada tab
        super.resetTabs();
    }

    // GETTERS & SETTERS

    /**
     * Tab/Pestaña de selección de información genérica de cesiones cpsn.
     * @return GeneralesCesionCPSNTab
     */
    public GeneralesCesionCPSNTab getTabGeneralesCesion() {
        return tabGeneralesCesion;
    }

    /**
     * Tab/Pestaña de selección de información genérica de cesiones cpsn.
     * @param tabGeneralesCesion GeneralesCesionCPSNTab
     */
    public void setTabGeneralesCesion(GeneralesCesionCPSNTab tabGeneralesCesion) {
        this.tabGeneralesCesion = tabGeneralesCesion;
    }

    /**
     * Tab/Pestaña de selección de información de cedente y cesionario.
     * @return CesionCPSNDerechosTab
     */
    public CesionCPSNDerechosTab getTabCesionDerechos() {
        return tabCesionDerechos;
    }

    /**
     * Tab/Pestaña de selección de información de cedente y cesionario.
     * @param tabCesionDerechos CesionCPSNDerechosTab
     */
    public void setTabCesionDerechos(CesionCPSNDerechosTab tabCesionDerechos) {
        this.tabCesionDerechos = tabCesionDerechos;
    }

    /**
     * Tab/Pestaña de selección de información del CPSN.
     * @return CesionCPSNTab
     */
    public CesionCPSNTab getTabCesionCPSN() {
        return tabCesionCPSN;
    }

    /**
     * Tab/Pestaña de selección de información del CPSN.
     * @param tabCesionCPSN CesionCPSNTab
     */
    public void setTabCesionCPSN(CesionCPSNTab tabCesionCPSN) {
        this.tabCesionCPSN = tabCesionCPSN;
    }

    /**
     * Tab/Pestaña de selección de generación de oficios.
     * @return GeneracionOficiosTab
     */
    public GeneracionOficiosTab getTabGenerarOficio() {
        return tabGenerarOficio;
    }

    /**
     * Tab/Pestaña de selección de generación de oficios.
     * @param tabGenerarOficio GeneracionOficiosTab
     */
    public void setTabGenerarOficio(GeneracionOficiosTab tabGenerarOficio) {
        this.tabGenerarOficio = tabGenerarOficio;
    }

}
