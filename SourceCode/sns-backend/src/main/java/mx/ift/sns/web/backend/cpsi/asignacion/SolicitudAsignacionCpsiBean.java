package mx.ift.sns.web.backend.cpsi.asignacion;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import mx.ift.sns.modelo.cpsi.SolicitudAsignacionCpsi;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.cpsi.ICodigoCPSIFacade;
import mx.ift.sns.negocio.oficios.IOficiosFacade;
import mx.ift.sns.web.backend.ac.cpsi.ConsultarCodigosCPSIBean;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.Wizard;
import mx.ift.sns.web.backend.ng.tabs.GeneracionOficiosTab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Bean asociado a los trámites de Asignación de CPSI. */
@ManagedBean(name = "solicitudAsignacionCpsiBean")
@ViewScoped
public class SolicitudAsignacionCpsiBean extends Wizard implements Serializable {

    /** Control Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudAsignacionCpsiBean.class);

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_Wizard_SolicitudAsignacionCPSI";

    /** Servicio de Catalogos. */
    @EJB(mappedName = "CodigoCPSIFacade")
    private ICodigoCPSIFacade codigoCpsiFacade;

    /** Facade de Generación de Oficios. */
    @EJB(mappedName = "OficiosFacade")
    private IOficiosFacade oficiosFacade;

    /** Referencia al Bean de Catálogo de CPSI. */
    @ManagedProperty("#{consultarCodigosCPSIBean}")
    private ConsultarCodigosCPSIBean catalogoCpsiBean;

    /** Tab/Pestaña de selección de información genérica de asignaciones. */
    private GeneralesAsignacionCpsiTab tabGeneralesAsignacion;

    /** Tab/Pestaña de análisis de asignaciones. */
    private AnalisisAsignacionCpsiTab tabAnalisisAsignacion;

    /** Tab/Pestaña de asignaciones de CPSI. */
    private AsignacionCpsiTab tabAsignacion;

    /** Tab/Pestaña de selección de generación de oficios. */
    private GeneracionOficiosTab tabGenerarOficio;

    /** Constructor, inicializa el Wizard. */
    public SolicitudAsignacionCpsiBean() {
        setWizardMessagesId(MSG_ID);
    }

    /** Inicializa el Bean y la lista de pasos del Wizard. */
    @PostConstruct
    public void init() {
        // Pestaña 'Generales'
        tabGeneralesAsignacion = new GeneralesAsignacionCpsiTab(this, codigoCpsiFacade);
        tabGeneralesAsignacion.setId("Solicitud Asignación CPSI - TabGenerales");
        getPasosWizard().add(tabGeneralesAsignacion);

        // Pestaña 'Análisis'
        tabAnalisisAsignacion = new AnalisisAsignacionCpsiTab(this, codigoCpsiFacade, catalogoCpsiBean);
        tabAnalisisAsignacion.setId("Solicitud Asignación CPSI - TabAnálisis");
        getPasosWizard().add(tabAnalisisAsignacion);

        // Pestaña 'Asignación'
        tabAsignacion = new AsignacionCpsiTab(this, codigoCpsiFacade);
        tabAsignacion.setId("Solicitud Asignación CPSI - TabAsignación");
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
            this.setSolicitud(new SolicitudAsignacionCpsi());

            // Actualizamos los campos de tabGenerales por ser el primer tab que aparece. El resto
            // de tabs se actualizarán al pulsar 'siguiente', el Wizard se encarga de invocar al
            // método 'actualizaCampos' de cada tab. Lo mismo ocurre con el método 'cargaValoresIniciales'
            if (!tabGeneralesAsignacion.isIniciado()) {
                tabGeneralesAsignacion.cargaValoresIniciales();
                tabGeneralesAsignacion.setIniciado(true);
            }
            tabGeneralesAsignacion.setTabHabilitado(true);
            tabAnalisisAsignacion.setTabHabilitado(true);
            tabAsignacion.setTabHabilitado(true);
            tabGenerarOficio.setTabHabilitado(true);
            tabGeneralesAsignacion.actualizaCampos();
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Precarga una solicitud existente para su edición y actualiza las referecnias del Wizard para el resto de tabs.
     * @param pSolicitud SolicitudAsignacionCpsi
     */
    public void cargarSolicitud(SolicitudAsignacionCpsi pSolicitud) {
        try {
            // Cargamos las dependencias Lazy (Cesiones Solicitadas, Aplicadas y Oficios)
            SolicitudAsignacionCpsi solicitudLoaded = codigoCpsiFacade.getSolicitudAsignacionEagerLoad(pSolicitud);

            // Asociamos la solicitud cargada en al Wizard
            this.setSolicitud(solicitudLoaded);

            // Actualizamos los campos de tabGenerales por ser el primer tab que aparece.
            if (!tabGeneralesAsignacion.isIniciado()) {
                tabGeneralesAsignacion.cargaValoresIniciales();
                tabGeneralesAsignacion.setIniciado(true);
            }
            tabGeneralesAsignacion.actualizaCampos();

            // Acciones en función del estado de la solicitud
            if (solicitudLoaded.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_EN_TRAMITE)) {
                tabGeneralesAsignacion.setTabHabilitado(true);
                tabAnalisisAsignacion.setTabHabilitado(true);
                tabAsignacion.setTabHabilitado(true);
                tabGenerarOficio.setTabHabilitado(true);
            } else if (solicitudLoaded.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_TERMINADA)) {
                // La solicitud ya está terminada, no se permite editar generales.
                tabGeneralesAsignacion.setTabHabilitado(false);
                tabAnalisisAsignacion.setTabHabilitado(true);
                tabAsignacion.setTabHabilitado(true);
                tabGenerarOficio.setTabHabilitado(true);
            } else {
                // La solicitud está cancelada, no se permite hacer nada con ella.
                tabGeneralesAsignacion.setTabHabilitado(false);
                tabAnalisisAsignacion.setTabHabilitado(false);
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
                .findComponent("FORM_SolicitudAsignacionCPSI:WIZ_SolicitudAsignacion");

        if (jsfWizard != null) {
            jsfWizard.setStep("WIZ_SolicitudAsignacion_TAB_Proveedor");
            this.resetNumPaso();
        }

        // Limpiamos la variables de cada tab
        super.resetTabs();
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
     * Tab/Pestaña de selección de información genérica de asignaciones.
     * @return GeneralesAsignacionCpsiTab
     */
    public GeneralesAsignacionCpsiTab getTabGeneralesAsignacion() {
        return tabGeneralesAsignacion;
    }

    /**
     * Tab/Pestaña de selección de información genérica de asignaciones.
     * @param tabGeneralesAsignacion GeneralesAsignacionCpsiTab
     */
    public void setTabGeneralesAsignacion(GeneralesAsignacionCpsiTab tabGeneralesAsignacion) {
        this.tabGeneralesAsignacion = tabGeneralesAsignacion;
    }

    /**
     * Tab/Pestaña de análisis de asignaciones.
     * @return AnalisisAsignacionCpsiTab
     */
    public AnalisisAsignacionCpsiTab getTabAnalisisAsignacion() {
        return tabAnalisisAsignacion;
    }

    /**
     * Tab/Pestaña de análisis de asignaciones.
     * @param tabAnalisisAsignacion AnalisisAsignacionCpsiTab
     */
    public void setTabAnalisisAsignacion(AnalisisAsignacionCpsiTab tabAnalisisAsignacion) {
        this.tabAnalisisAsignacion = tabAnalisisAsignacion;
    }

    /**
     * Tab/Pestaña de asignaciones de CPSI.
     * @return AsignacionCpsiTab
     */
    public AsignacionCpsiTab getTabAsignacion() {
        return tabAsignacion;
    }

    /**
     * Tab/Pestaña de asignaciones de CPSI.
     * @param tabAsignacion AsignacionCpsiTab
     */
    public void setTabAsignacion(AsignacionCpsiTab tabAsignacion) {
        this.tabAsignacion = tabAsignacion;
    }

    /**
     * Referencia al Bean de Catálogo de CPSI.
     * @return tConsultarCodigosCPSIBean
     */
    public ConsultarCodigosCPSIBean getCatalogoCpsiBean() {
        return catalogoCpsiBean;
    }

    /**
     * Referencia al Bean de Catálogo de CPSI.
     * @param catalogoCpsiBean ConsultarCodigosCPSIBean
     */
    public void setCatalogoCpsiBean(ConsultarCodigosCPSIBean catalogoCpsiBean) {
        this.catalogoCpsiBean = catalogoCpsiBean;
    }

}
