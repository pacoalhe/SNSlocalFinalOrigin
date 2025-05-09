package mx.ift.sns.web.backend.nng.cesion;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import mx.ift.sns.modelo.nng.SolicitudCesionNng;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;
import mx.ift.sns.negocio.oficios.IOficiosFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.Wizard;
import mx.ift.sns.web.backend.ng.tabs.GeneracionOficiosTab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean asociado a la solicitud de Cesiones de Solicitudes de Numeración No Geográfica.
 */
@ManagedBean(name = "solicitudCesionNngBean")
@ViewScoped
public class SolicitudCesionNngBean extends Wizard implements Serializable {

    /** Control Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudCesionNngBean.class);

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_Wizard_SolicitudCesionNNG";

    /** Facade de Numeración No Geográfica. */
    @EJB(mappedName = "NumeracionNoGeograficaFacade")
    private INumeracionNoGeograficaFacade nngFacade;

    /** Facade de Generación de Oficios. */
    @EJB(mappedName = "OficiosFacade")
    private IOficiosFacade oficiosFacade;

    /** Tab/Pestaña de selección de información genérica de cesiones. */
    private GeneralesCesionNngTab tabGeneralesCesion;

    /** Tab/Pestaña de selección de información de cedente y cesionario. */
    private CesionNngDerechosTab tabCesionDerechos;

    /** Tab/Pestaña de selección de información de numeración a ceder. */
    private CesionNngNumeracionTab tabCesionNumeracion;

    /** Tab/Pestaña de selección de generación de oficios. */
    private GeneracionOficiosTab tabGenerarOficio;

    /** Constructor, inicializa el Wizard. */
    public SolicitudCesionNngBean() {
        // Pasamos el id del p:messages del Wizard para mostrar
        // los errores al pasar de un tab a otro.
        setWizardMessagesId(MSG_ID);
    }

    /** Inicializa el Bean y la lista de pasos del Wizard. */
    @PostConstruct
    public void init() {
        // Importante: Cada tab del Wizard ha de ser agregado
        // en la lista 'pasosWizzard' en el mismo orden en el
        // que se agregan los tab en el xhtml.

        // Pestaña 'Generales'
        tabGeneralesCesion = new GeneralesCesionNngTab(this, nngFacade);
        tabGeneralesCesion.setId("Cesión Solicitudes NNG - TabGenerales");
        getPasosWizard().add(tabGeneralesCesion);

        // Pestaña 'Cesión Derechos'
        tabCesionDerechos = new CesionNngDerechosTab(this, nngFacade);
        tabCesionDerechos.setId("Cesión Solicitudes NNG - TabCesionDerechos");
        getPasosWizard().add(tabCesionDerechos);

        // Pestaña 'Cesión Numeración'
        tabCesionNumeracion = new CesionNngNumeracionTab(this, nngFacade);
        tabCesionNumeracion.setId("Cesión Solicitudes NNG - TabCesionNumeracion");
        getPasosWizard().add(tabCesionNumeracion);

        // Tab de Generación de Oficios
        tabGenerarOficio = new GeneracionOficiosTab(this, oficiosFacade);
        tabGenerarOficio.setId("Cesión Solicitudes NNG - TabOficios");
        getPasosWizard().add(tabGenerarOficio);
    }

    /** Método invocado al pulsar sobre el botón de 'Nueva Cesión'. */
    public void nuevaSolicitud() {
        try {
            // Iniciamos el tipo de solicitud específico.
            this.setSolicitud(new SolicitudCesionNng());

            // Actualizamos los campos de tabGenerales por ser el primer tab que aparece. El resto
            // de tabs se actualizarán al pulsar 'siguiente', el Wizard se encarga de invocar al
            // método 'actualizaCampos' de cada tab. Lo mismo ocurre con el método 'cargaValoresIniciales'
            if (!tabGeneralesCesion.isIniciado()) {
                tabGeneralesCesion.cargaValoresIniciales();
                tabGeneralesCesion.setIniciado(true);
            }
            tabGeneralesCesion.setTabHabilitado(true);
            tabCesionDerechos.setTabHabilitado(true);
            tabCesionNumeracion.setTabHabilitado(true);
            tabGenerarOficio.setTabHabilitado(true);
            tabGeneralesCesion.actualizaCampos();
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Precarga una solicitud existente para su edición. Actualiza las referecnias del Wizard para el resto de tabs.
     * @param pSolicitud SolicitudCesionNng
     */
    public void cargarSolicitud(SolicitudCesionNng pSolicitud) {
        try {
            // Cargamos las dependencias Lazy (Cesiones Solicitadas y Oficios)
            SolicitudCesionNng solicitudLoaded = nngFacade.getSolicitudCesionEagerLoad(pSolicitud);

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
                tabCesionDerechos.setTabHabilitado(true);
                tabCesionNumeracion.setTabHabilitado(true);
                tabGenerarOficio.setTabHabilitado(true);
            } else if (solicitudLoaded.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_TERMINADA)) {
                // La solicitud ya está terminada, no se permite editar generales.
                tabGeneralesCesion.setTabHabilitado(false);
                tabCesionDerechos.setTabHabilitado(false);
                tabCesionNumeracion.setTabHabilitado(true);
                tabGenerarOficio.setTabHabilitado(true);
            } else {
                // La solicitud está cancelada, no se permite hacer nada con ella.
                tabGeneralesCesion.setTabHabilitado(false);
                tabCesionDerechos.setTabHabilitado(false);
                tabCesionNumeracion.setTabHabilitado(false);
                tabGenerarOficio.setTabHabilitado(false);
            }
        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    @Override
    public void resetTabs() {
        // El siguiente código resetea el Wizard asociado al xhtml asociado a su vez al Bean para
        // que se abra siempre la primera pestaña.

        // Restablecemos el Wizard en la primera pestaña
        org.primefaces.component.wizard.Wizard jsfWizard;
        jsfWizard = (org.primefaces.component.wizard.Wizard) FacesContext.getCurrentInstance().getViewRoot()
                .findComponent("FORM_SolicitudCesionNNG:WIZ_SolicitudCesion");

        if (jsfWizard != null) {
            jsfWizard.setStep("WIZ_SolicitudCesion_TAB_Proveedor");
            this.resetNumPaso();
        }

        // Limpiamos la variables de cada tab
        super.resetTabs();
    }

    // GETTERS & SETTERS

    /**
     * Tab/Pestaña de selección de información genérica de cesiones.
     * @return GeneralesCesionNngTab
     */
    public GeneralesCesionNngTab getTabGeneralesCesion() {
        return tabGeneralesCesion;
    }

    /**
     * Tab/Pestaña de selección de información de cedente y cesionario.
     * @return CesionNngDerechosTab
     */
    public CesionNngDerechosTab getTabCesionDerechos() {
        return tabCesionDerechos;
    }

    /**
     * Tab/Pestaña de selección de información de numeración a ceder.
     * @return CesionNngNumeracionTab
     */
    public CesionNngNumeracionTab getTabCesionNumeracion() {
        return tabCesionNumeracion;
    }

    /**
     * Tab/Pestaña de selección de generación de oficios.
     * @return GeneracionOficiosTab
     */
    public GeneracionOficiosTab getTabGenerarOficio() {
        return tabGenerarOficio;
    }
}
