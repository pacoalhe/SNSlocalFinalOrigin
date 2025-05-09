package mx.ift.sns.web.backend.cpsn.asignacion;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import mx.ift.sns.modelo.cpsn.SolicitudAsignacionCpsn;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.cpsn.ICodigoCPSNFacade;
import mx.ift.sns.negocio.oficios.IOficiosFacade;
import mx.ift.sns.web.backend.ac.cpsn.ConsultarCodigosCPSNBean;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.Wizard;
import mx.ift.sns.web.backend.ng.tabs.GeneracionOficiosTab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Bean asociado a la solicitud de asignación de códigos CPSN. */
@ManagedBean(name = "solicitudAsignacionCpsnBean")
@ViewScoped
public class SolicitudAsignacionCpsnBean extends Wizard implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudAsignacionCpsnBean.class);

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_Wizard_SolicitudAsignacionCPSN";

    /** Fachada de Códigos CPSN. */
    @EJB(mappedName = "CodigoCPSNFacade")
    private ICodigoCPSNFacade cpsnFacade;

    /** Fachada de oficios. */
    @EJB(mappedName = "OficiosFacade")
    private IOficiosFacade oficiosFacade;

    /** Referencia al Bean de proveedor. */
    @ManagedProperty("#{consultarCodigosCPSNBean}")
    private ConsultarCodigosCPSNBean consultarCodigosCPSNBean;

    /** Pestaña Datos Generales. */
    private GeneralesAsignacionCpsnTab tabGenerales;

    /** Pestaña Códigos Solicitados. */
    private NumeracionAsignacionCpsnTab tabNumeracion;

    /** Pestaña de Analisis. */
    private AnalisisAsignacionCpsnTab tabAnalisis;

    /** Pestaña de Analisis. */
    private AsignacionCpsnTab tabAsignacion;

    /** Tab/Pestaña de selección de generación de oficios. */
    private GeneracionOficiosTab tabGenerarOficio;

    /** Constructor, inicializa el Wizard. */
    public SolicitudAsignacionCpsnBean() {
        // Pasamos el id del p:messages del Wizard para mostrar
        // los errores al pasar de un tab a otro.
        setWizardMessagesId(MSG_ID);
    }

    /** Inicializa el Bean y la lista de pasos del Wizard. */
    @PostConstruct
    public void init() {
        // Pestaña 'Generales'
        tabGenerales = new GeneralesAsignacionCpsnTab(this, cpsnFacade);
        tabGenerales.setId("Asignación Solicitudes CPSN - TabGenerales");
        getPasosWizard().add(tabGenerales);

        // Pestaña 'Código Solicitado'
        tabNumeracion = new NumeracionAsignacionCpsnTab(this, cpsnFacade);
        tabNumeracion.setId("Asignación Solicitudes CPSN - TabNumeración");
        getPasosWizard().add(tabNumeracion);

        // Pestaña 'Analisis'
        tabAnalisis = new AnalisisAsignacionCpsnTab(this, cpsnFacade, consultarCodigosCPSNBean);
        tabAnalisis.setId("Asignación Solicitudes CPSN - TabAnalisis");
        getPasosWizard().add(tabAnalisis);

        // Pestaña 'Asignacion'
        tabAsignacion = new AsignacionCpsnTab(this, cpsnFacade);
        tabAsignacion.setId("Asignación Solicitudes CPSN - TabAsignacion");
        getPasosWizard().add(tabAsignacion);

        // Tab de Generación de Oficios
        tabGenerarOficio = new GeneracionOficiosTab(this, oficiosFacade);
        tabGenerarOficio.setId("Solicitud Asignación CPSI - TabOficios");
        getPasosWizard().add(tabGenerarOficio);
    }

    /** Método invocado al pulsar sobre el botón de 'Nueva Asignación'. */
    public void nuevaSolicitud() {
        try {
            // Iniciamos el tipo de solicitud específico.
            this.setSolicitud(new SolicitudAsignacionCpsn());

            // Actualizamos los campos de tabGenerales por ser el primer tab que aparece. El resto
            // de tabs se actualizarán al pulsar 'siguiente', el Wizard se encarga de invocar al
            // método 'actualizaCampos' de cada tab. Lo mismo ocurre con el método 'cargaValoresIniciales'
            if (!tabGenerales.isIniciado()) {
                tabGenerales.cargaValoresIniciales();
                tabGenerales.setIniciado(true);
            }
            tabGenerales.setTabHabilitado(true);
            tabNumeracion.setTabHabilitado(true);
            tabAnalisis.setTabHabilitado(true);
            tabAsignacion.setTabHabilitado(true);
            tabGenerarOficio.setTabHabilitado(true);
            tabGenerales.actualizaCampos();
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Carga la solicitud de asignación de códigos CPSN.
     * @param pSolicitud solicitud
     */
    public void cargarSolicitud(SolicitudAsignacionCpsn pSolicitud) {
        try {
            // Cargamos las dependencias Lazy (Asignaciones Solicitadas, Aplicadas y Oficios)
            SolicitudAsignacionCpsn solicitudLoaded = cpsnFacade.getSolicitudAsignacionEagerLoad(pSolicitud);

            // Asociamos la solicitud cargada en al Wizard
            this.setSolicitud(solicitudLoaded);

            if (solicitudLoaded.getNumeracionAsignadas().size() > 0) {
                this.getTabAnalisis().setGuardado(true);
            }

            // Actualizamos los campos de tabGenerales por ser el primer tab que aparece.
            if (!tabGenerales.isIniciado()) {
                tabGenerales.cargaValoresIniciales();
                tabGenerales.setIniciado(true);
            }
            tabGenerales.actualizaCampos();

            // Acciones en función del estado de la solicitud
            if (solicitudLoaded.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_EN_TRAMITE)) {
                tabGenerales.setTabHabilitado(true);
                tabNumeracion.setTabHabilitado(true);
                tabAnalisis.setTabHabilitado(true);
                tabAsignacion.setTabHabilitado(true);
                tabGenerarOficio.setTabHabilitado(true);
            } else if (solicitudLoaded.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_TERMINADA)) {
                // La solicitud ya está terminada, solamente se permiten editar los oficios.
                tabGenerales.setTabHabilitado(false);
                tabNumeracion.setTabHabilitado(true);
                tabAnalisis.setTabHabilitado(true);
                tabAsignacion.setTabHabilitado(true);
                tabGenerarOficio.setTabHabilitado(true);
            } else {
                // La solicitud está cancelada, no se permite hacer nada con ella.
                tabGenerales.setTabHabilitado(false);
                tabNumeracion.setTabHabilitado(false);
                tabAnalisis.setTabHabilitado(false);
                tabAsignacion.setTabHabilitado(false);
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
                .findComponent("FORM_SolicitudAsignacionCPSN:WIZ_SolicitudAsignacion");

        if (jsfWizard != null) {
            jsfWizard.setStep("tab_general_asignacion");
            this.resetNumPaso();
        }

        // Limpiamos la variables de cada tab
        super.resetTabs();
    }

    // GETTERS & SETTERS

    /**
     * Tab/Pestaña de selección de generación de generales.
     * @return the tabGenerales
     */
    public GeneralesAsignacionCpsnTab getTabGenerales() {
        return tabGenerales;
    }

    /**
     * Tab/Pestaña de selección de generación de generales.
     * @param tabGenerales the tabGenerales to set
     */
    public void setTabGenerales(GeneralesAsignacionCpsnTab tabGenerales) {
        this.tabGenerales = tabGenerales;
    }

    /**
     * Tab/Pestaña de selección de Codigo solicitado.
     * @return the tabNumeracion
     */
    public NumeracionAsignacionCpsnTab getTabNumeracion() {
        return tabNumeracion;
    }

    /**
     * Tab/Pestaña de selección de Codigo solicitado.
     * @param tabNumeracion the tabNumeracion to set
     */
    public void setTabNumeracion(NumeracionAsignacionCpsnTab tabNumeracion) {
        this.tabNumeracion = tabNumeracion;
    }

    /**
     * Tab/Pestaña de selección de Análisis.
     * @return the tabAnalisis
     */
    public AnalisisAsignacionCpsnTab getTabAnalisis() {
        return tabAnalisis;
    }

    /**
     * Tab/Pestaña de selección de Análisis.
     * @param tabAnalisis the tabAnalisis to set
     */
    public void setTabAnalisis(AnalisisAsignacionCpsnTab tabAnalisis) {
        this.tabAnalisis = tabAnalisis;
    }

    /**
     * Tab/Pestaña de selección de Asignación.
     * @return the tabAsignacion
     */
    public AsignacionCpsnTab getTabAsignacion() {
        return tabAsignacion;
    }

    /**
     * Tab/Pestaña de selección de Asignación.
     * @param tabAsignacion the tabAsignacion to set
     */
    public void setTabAsignacion(AsignacionCpsnTab tabAsignacion) {
        this.tabAsignacion = tabAsignacion;
    }

    /**
     * Identificador del bean de la consulta de códigos CPSN.
     * @return the consultarCodigosCPSNBean
     */
    public ConsultarCodigosCPSNBean getConsultarCodigosCPSNBean() {
        return consultarCodigosCPSNBean;
    }

    /**
     * Identificador del bean de la consulta de códigos CPSN.
     * @param consultarCodigosCPSNBean the consultarCodigosCPSNBean to set
     */
    public void setConsultarCodigosCPSNBean(ConsultarCodigosCPSNBean consultarCodigosCPSNBean) {
        this.consultarCodigosCPSNBean = consultarCodigosCPSNBean;
    }

    /**
     * Tab/Pestaña de selección de Oficio.
     * @return the tabGenerarOficio
     */
    public GeneracionOficiosTab getTabGenerarOficio() {
        return tabGenerarOficio;
    }

    /**
     * Tab/Pestaña de selección de Oficio.
     * @param tabGenerarOficio the tabGenerarOficio to set
     */
    public void setTabGenerarOficio(GeneracionOficiosTab tabGenerarOficio) {
        this.tabGenerarOficio = tabGenerarOficio;
    }

}
