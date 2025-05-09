package mx.ift.sns.web.backend.ng.asignacion;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.modelo.ng.SolicitudAsignacion;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.negocio.oficios.IOficiosFacade;
import mx.ift.sns.web.backend.ApplicationException;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.Wizard;
import mx.ift.sns.web.backend.ng.tabs.GeneracionOficiosTab;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * bean para recoger valores de la pagina de asignacion manual.
 * @author Jose Luis Martin
 * @version 1.0
 */
@ManagedBean(name = "asignacionManualBean")
@ViewScoped
public class AsignacionManualBean extends Wizard implements Serializable {

    /** Serial uid. */
    private static final long serialVersionUID = -6977772487921944690L;

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AsignacionManualBean.class);

    /** Id del mensajes de error. */
    private static final String MSG_ID = "MSG_Wizard_SolicitudAsignacion";

    /** Cabecera titulo. variable que se utiliza para asignar el texto Numeracion manual o Ediccion manual. */
    private String cabecera = "";

    /** Facade de Numeración Geográfica. */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService numeracionGeograficaService;

    /** Facade de Generación de Oficios. */
    @EJB(mappedName = "OficiosFacade")
    private IOficiosFacade oficiosFacade;

    /** Paso que contiene los datos generales. */
    private GeneralesAsignacionNgTab generales;

    /** Paso que contiene los datos de numeración. */
    private NumeracionAsignacionNgTab numeracion;

    /** Paso que contiene los datos de analisis. */
    private AnalisisNgTab analisis;

    /** Paso que contiene los datos de asignacion. */
    private AsignacionNgTab asignacion;

    /** Paso que contiene los datos de oficio. */
    private GeneracionOficiosTab oficios;

    /**
     * Constructor.
     */
    public AsignacionManualBean() {
        // Pasamos el id del p:messages del Wizard para mostrar
        // los errores al pasar de un tab a otro.
        setWizardMessagesId(MSG_ID);

        // Iniciamos el tipo de solicitud específico.
        setSolicitud(new SolicitudAsignacion());
    }

    /**
     * Constructor.
     * @param pMensajesId messages
     */
    public AsignacionManualBean(String pMensajesId) {
        setWizardMessagesId(pMensajesId);
    }

    /**
     * Carga forzada al inicio de vida del bean.
     * @throws Exception excepcion
     */
    @PostConstruct
    public void init() throws Exception {
        try {
            cabecera = MensajesBean.getTextoResource("asignacion.manual");

            // Importante: Cada tab del Wizard ha de ser agregado
            // en la lista 'pasosWizzard' en el mismo orden en el
            // que se agregan los tab en el xhtml.

            generales = new GeneralesAsignacionNgTab(this, numeracionGeograficaService);
            generales.setId("Asignación Manual - TabGenerales");
            getPasosWizard().add(generales);

            numeracion = new NumeracionAsignacionNgTab(this, numeracionGeograficaService);
            numeracion.setId("Asignación Manual - TabNumeración");
            getPasosWizard().add(numeracion);

            analisis = new AnalisisNgTab(this, numeracionGeograficaService);
            analisis.setId("Asignación Manual - TabAnálisis");
            getPasosWizard().add(analisis);

            asignacion = new AsignacionNgTab(this, numeracionGeograficaService);
            asignacion.setId("Asignación Manual - TabAsignación");
            getPasosWizard().add(asignacion);

            oficios = new GeneracionOficiosTab(this, oficiosFacade);
            oficios.setId("Asignación Manual - TabOficios");
            getPasosWizard().add(oficios);

            numeracion.setRegistroPorPagina(PaginatorUtil.getRegistrosPorPagina(numeracionGeograficaService,
                    Parametro.REGISTROS_POR_PAGINA_BACK_WIZ));
            analisis.setRegistroPorPagina(PaginatorUtil.getRegistrosPorPagina(numeracionGeograficaService,
                    Parametro.REGISTROS_POR_PAGINA_BACK_WIZ));
            asignacion.setRegistroPorPagina(PaginatorUtil.getRegistrosPorPagina(numeracionGeograficaService,
                    Parametro.REGISTROS_POR_PAGINA_BACK_WIZ));

        } catch (Exception e) {
            throw new ApplicationException();
        }
    }

    /**
     * Precarga una solicitud existente para su edición. Actualiza las referecnias del Wizard. para el resto de tabs.
     * @param pSolicitud SolicitudLiberacionNg
     */
    public void cargarSolicitud(SolicitudAsignacion pSolicitud) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("solicitud {}", pSolicitud);
        }

        try {
            // Cargamos las dependencias Lazy (Asignaciones Solicitadas, Aplicadas y Oficios)
            SolicitudAsignacion solicitudLoaded = numeracionGeograficaService
                    .getSolicitudAsignacionEagerLoad(pSolicitud);

            // Asociamos la solicitud cargada en al Wizard
            this.setSolicitud(solicitudLoaded);

            // Actualizamos los campos de tabGenerales por ser el primer tab que aparece. El resto
            // de tabs se actualizarán al pulsar 'siguiente', el Wizard se encarga de invocar al
            // método 'actualizaCampos' de cada tab.
            generales.actualizaCampos();

            // Acciones en función del estado de la solicitud
            if (solicitudLoaded.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_EN_TRAMITE)) {

                generales.setTabHabilitado(true);
                numeracion.setTabHabilitado(true);
                analisis.setTabHabilitado(true);
                asignacion.setTabHabilitado(true);
                oficios.setTabHabilitado(true);

            } else if (solicitudLoaded.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_TERMINADA)) {

                // La solicitud ya está terminada, solamente se permiten editar los oficios.
                generales.setTabHabilitado(false);
                numeracion.setTabHabilitado(false);
                analisis.setTabHabilitado(false);
                asignacion.setTabHabilitado(true);
                oficios.setTabHabilitado(true);

            } else {

                // La solicitud está cancelada, no se permite hacer nada con ella.
                generales.setTabHabilitado(false);
                numeracion.setTabHabilitado(false);
                analisis.setTabHabilitado(false);
                asignacion.setTabHabilitado(false);
                oficios.setTabHabilitado(false);

            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    @Override
    public void resetTabs() {

        LOGGER.debug("");

        // Deberíamos poder actualizar el Wizard directamente en JSF sin tener que traernos
        // directamente el componente, pero el Binding no funciona al ser diferentes vistas (dejan
        // de funcionar los callbacks).

        // El siguiente código resetea el Wizard asociado al xhtml asociado a su vez al Bean para
        // que se abra siempre la primera pestaña.

        // Restablecemos el Wizard en la primera pestaña
        org.primefaces.component.wizard.Wizard jsfWizard;
        jsfWizard = (org.primefaces.component.wizard.Wizard) FacesContext.getCurrentInstance().getViewRoot()
                .findComponent("FORM_asignacionManual:wizard_manual");

        if (jsfWizard != null) {
            jsfWizard.setStep("tab_manual_general");
            this.resetNumPaso();
        }

        // Limpiamos la variables de cada tab
        super.resetTabs();

        // Generamos una nueva solicitud por si se se quiere crear una nueva
        setSolicitud(new SolicitudAsignacion());
        generales.actualizaCampos();
        generales.setTabHabilitado(true);
        numeracion.setTabHabilitado(true);
        analisis.setTabHabilitado(true);
        asignacion.setTabHabilitado(true);
        oficios.setTabHabilitado(true);
    }

    /**
     * Cabecera titulo.
     * @return cabecera
     */
    public String getCabecera() {
        return cabecera;
    }

    /**
     * Cabecera titulo.
     * @param cabecera String
     */
    public void setCabecera(String cabecera) {
        this.cabecera = cabecera;
    }

    /**
     * Paso que contiene los datos generales.
     * @return generales
     */
    public GeneralesAsignacionNgTab getGenerales() {
        return generales;
    }

    /**
     * Paso que contiene los datos generales.
     * @param generales GeneralesAsignacionNgTab
     */
    public void setGenerales(GeneralesAsignacionNgTab generales) {
        this.generales = generales;
    }

    /**
     * Paso que contiene los datos de numeración.
     * @return numeracion
     */
    public NumeracionAsignacionNgTab getNumeracion() {
        return numeracion;
    }

    /**
     * Paso que contiene los datos de numeración.
     * @param numeracion NumeracionAsignacionNgTab
     */
    public void setNumeracion(NumeracionAsignacionNgTab numeracion) {
        this.numeracion = numeracion;
    }

    /**
     * Paso que contiene los datos de analisis.
     * @return analisis
     */
    public AnalisisNgTab getAnalisis() {
        return analisis;
    }

    /**
     * Paso que contiene los datos de analisis.
     * @param analisis AnalisisNgTab
     */
    public void setAnalisis(AnalisisNgTab analisis) {
        this.analisis = analisis;
    }

    /**
     * Paso que contiene los datos de asignacion.
     * @return asignacion
     */
    public AsignacionNgTab getAsignacion() {
        return asignacion;
    }

    /**
     * Paso que contiene los datos de asignacion.
     * @param asignacion AsignacionNgTab
     */
    public void setAsignacion(AsignacionNgTab asignacion) {
        this.asignacion = asignacion;
    }

    /**
     * Paso que contiene los datos de oficio.
     * @return oficios
     */
    public GeneracionOficiosTab getOficios() {
        return oficios;
    }

    /**
     * Paso que contiene los datos de oficio.
     * @param oficios oficios to set
     */
    public void setOficios(GeneracionOficiosTab oficios) {
        this.oficios = oficios;
    }

}
