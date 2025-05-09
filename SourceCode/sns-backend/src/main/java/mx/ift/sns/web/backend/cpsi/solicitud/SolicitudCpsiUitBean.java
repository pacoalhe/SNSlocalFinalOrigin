package mx.ift.sns.web.backend.cpsi.solicitud;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import mx.ift.sns.modelo.cpsi.SolicitudCpsiUit;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.cpsi.ICodigoCPSIFacade;
import mx.ift.sns.negocio.oficios.IOficiosFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.Wizard;
import mx.ift.sns.web.backend.ng.tabs.GeneracionOficiosTab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Bean asociado a la solicitud de códigos de CPSI a la UIT. */
@ManagedBean(name = "solicitudCpsiUitBean")
@ViewScoped
public class SolicitudCpsiUitBean extends Wizard implements Serializable {

    /** Control Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudCpsiUitBean.class);

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_Wizard_SolicitudCPSI";

    /** Servicio de Catalogos. */
    @EJB(mappedName = "CodigoCPSIFacade")
    private ICodigoCPSIFacade codigoCpsiFacade;

    /** Facade de Generación de Oficios. */
    @EJB(mappedName = "OficiosFacade")
    private IOficiosFacade oficiosFacade;

    /** Tab/Pestaña de selección de información genérica de asignaciones. */
    private GeneralesCpsiUitTab tabGenerales;

    /** Tab/Pestaña de Entregados de CPSI. */
    private EntregadoCpsiTab tabEntregados;

    /** Tab/Pestaña de selección de generación de oficios. */
    private GeneracionOficiosTab tabGenerarOficio;

    /** Constructor, inicializa el Wizard. */
    public SolicitudCpsiUitBean() {
        setWizardMessagesId(MSG_ID);
        setSolicitud(new SolicitudCpsiUit());
    }

    /** Inicializa el Bean y la lista de pasos del Wizard. */
    @PostConstruct
    public void init() {
        // Pestaña 'Generales'
        tabGenerales = new GeneralesCpsiUitTab(this, codigoCpsiFacade);
        tabGenerales.setId("Solicitud de CPSI a la UIT - TabGenerales");
        getPasosWizard().add(tabGenerales);

        // Pestaña 'Entregados'
        tabEntregados = new EntregadoCpsiTab(this, codigoCpsiFacade);
        tabEntregados.setId("Solicitud de CPSI a la UIT - TabEntregados");
        getPasosWizard().add(tabEntregados);

        // Tab de Generación de Oficios
        tabGenerarOficio = new GeneracionOficiosTab(this, oficiosFacade);
        tabGenerarOficio.setId("Solicitud de CPSI a la UIT - TabOficios");
        getPasosWizard().add(tabGenerarOficio);
    }

    /**
     * Precarga una solicitud existente para su edición y actualiza las referecnias del Wizard para el resto de tabs.
     * @param pSolicitud SolicitudAsignacionCpsi
     */
    public void cargarSolicitud(SolicitudCpsiUit pSolicitud) {
        try {
            // Cargamos las dependencias Lazy (Cesiones Solicitadas, Aplicadas y Oficios)
            SolicitudCpsiUit solicitudLoaded = codigoCpsiFacade.getSolicitudCpsiUitEagerLoad(pSolicitud);

            if (solicitudLoaded.getCpsiUitEntregado() != null && !solicitudLoaded.getCpsiUitEntregado().isEmpty()) {
                this.getTabEntregados().setActivarBotonTerminar(true);
            }

            // Asociamos la solicitud cargada en al Wizard
            this.setSolicitud(solicitudLoaded);

            // Actualizamos los campos de tabGenerales por ser el primer tab que aparece. El resto
            // de tabs se actualizarán al pulsar 'siguiente', el Wizard se encarga de invocar al
            // método 'actualizaCampos' de cada tab.
            tabGenerales.actualizaCampos();

            // Acciones en función del estado de la solicitud
            if (solicitudLoaded.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_EN_TRAMITE)) {
                tabGenerales.setTabHabilitado(true);
                tabEntregados.setTabHabilitado(true);
                tabGenerarOficio.setTabHabilitado(true);
            } else if (solicitudLoaded.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_TERMINADA)) {
                // La solicitud ya está terminada, no se permite editar generales.
                tabGenerales.setTabHabilitado(false);
                tabEntregados.setTabHabilitado(true);
                tabGenerarOficio.setTabHabilitado(true);
            } else {
                // La solicitud está cancelada, no se permite hacer nada con ella.
                tabGenerales.setTabHabilitado(false);
                tabEntregados.setTabHabilitado(false);
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
                .findComponent("FORM_SolicitudCPSI:WIZ_Solicitud");

        if (jsfWizard != null) {
            jsfWizard.setStep("WIZ_Solicitud_TAB_Generales");
            this.resetNumPaso();
        }

        // Limpiamos la variables de cada tab
        super.resetTabs();

        // Generamos una nueva solicitud por si se se quiere crear una nueva
        setSolicitud(new SolicitudCpsiUit());
        tabGenerales.actualizaCampos();
        tabGenerales.setTabHabilitado(true);
        tabEntregados.setTabHabilitado(true);
        tabGenerarOficio.setTabHabilitado(true);
    }

    // GETTERS & SETTERS

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

    /**
     * Tab/Pestaña de selección de generación de generales.
     * @return GeneralesCpsiUitTab
     */
    public GeneralesCpsiUitTab getTabGenerales() {
        return tabGenerales;
    }

    /**
     * Tab/Pestaña de selección de generación de generales.
     * @param tabGenerales GeneralesCpsiUitTab
     */
    public void setTabGenerales(GeneralesCpsiUitTab tabGenerales) {
        this.tabGenerales = tabGenerales;
    }

    /**
     * Tab/Pestaña de selección de generación de códigos entregados.
     * @return EntregadoCpsiTab
     */
    public EntregadoCpsiTab getTabEntregados() {
        return tabEntregados;
    }

    /**
     * Tab/Pestaña de selección de generación de códigos entregados.
     * @param tabEntregados EntregadoCpsiTab
     */
    public void setTabEntregados(EntregadoCpsiTab tabEntregados) {
        this.tabEntregados = tabEntregados;
    }

}
