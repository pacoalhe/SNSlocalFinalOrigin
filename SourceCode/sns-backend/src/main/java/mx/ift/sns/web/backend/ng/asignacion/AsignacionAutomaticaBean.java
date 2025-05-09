package mx.ift.sns.web.backend.ng.asignacion;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.modelo.ng.SolicitudAsignacion;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.negocio.oficios.IOficiosFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.Wizard;
import mx.ift.sns.web.backend.ng.tabs.GeneracionOficiosTab;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean asociado a la Asignación Automática.
 */
@ManagedBean(name = "asignacionAutomaticaBean")
@ViewScoped
public class AsignacionAutomaticaBean extends Wizard {

    /** Serialziación. */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AsignacionAutomaticaBean.class);

    /** Id del mensajes de error. */
    private static final String MSG_ID = "MSG_Wizard_Automatica";

    /** Facade de Numeración Geográfica. */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService numeracion;

    /** Facade de Generación de Oficios. */
    @EJB(mappedName = "OficiosFacade")
    private IOficiosFacade oficiosFacade;

    /** cargarFicheroTab. */
    private CargarFicheroTab cargarFicheroTab;

    /** cargarFicheroTab. */
    private GeneralesAsignacionNgTab tabGenerales;

    /** NumeracionTab. */
    private NumeracionAsignacionNgTab numeracionAsignada;

    /** AnalisTab. */
    private AnalisisNgTab analisis;

    /** AsignacionTab. */
    private AsignacionNgTab asignacion;

    /** Paso que contiene los datos de oficio. */
    private GeneracionOficiosTab oficios;

    /** Constructor. */
    public AsignacionAutomaticaBean() {
        // Pasamos el id del p:messages del Wizard para mostrar
        // los errores al pasar de un tab a otro.
        setWizardMessagesId("MSG_Wizard_Automatica");

        // Cargamos una Nueva solicitud
        setSolicitud(new SolicitudAsignacion());
    }

    /** Inicializa el Bean y la lista de pasos del Wizard. */
    @PostConstruct
    public void init() {
        // Importante: Cada tab del Wizard ha de ser agregado
        // en la lista 'pasosWizzard' en el mismo orden en el
        // que se agregan los tab en el xhtml.
        try {
            cargarFicheroTab = new CargarFicheroTab(this, numeracion);
            cargarFicheroTab.setId("Asignación Automática - TabCargaFichero");
            getPasosWizard().add(cargarFicheroTab);

            tabGenerales = new GeneralesAsignacionNgTab(this, numeracion);
            tabGenerales.setId("Asignación Automática - TabGenerales");
            getPasosWizard().add(tabGenerales);

            numeracionAsignada = new NumeracionAsignacionNgTab(this, numeracion);
            numeracionAsignada.setId("Asignación Automática - TabNumeración");
            getPasosWizard().add(numeracionAsignada);

            analisis = new AnalisisNgTab(this, numeracion);
            analisis.setId("Asignación Automática - TabAnálisis");
            getPasosWizard().add(analisis);

            asignacion = new AsignacionNgTab(this, numeracion);
            asignacion.setId("Asignación Automática - TabAsignación");
            getPasosWizard().add(asignacion);

            oficios = new GeneracionOficiosTab(this, oficiosFacade);
            oficios.setId("Asignación Automática - TabOficios");
            getPasosWizard().add(oficios);

            numeracionAsignada.setRegistroPorPagina(PaginatorUtil.getRegistrosPorPagina(numeracion,
                    Parametro.REGISTROS_POR_PAGINA_BACK_WIZ));
            analisis.setRegistroPorPagina(PaginatorUtil.getRegistrosPorPagina(numeracion,
                    Parametro.REGISTROS_POR_PAGINA_BACK_WIZ));
            asignacion.setRegistroPorPagina(PaginatorUtil.getRegistrosPorPagina(numeracion,
                    Parametro.REGISTROS_POR_PAGINA_BACK_WIZ));

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
                .findComponent("FORM_asignacionAutomatica:wizard_automatica");

        if (jsfWizard != null) {
            jsfWizard.setStep("tab_automatica_carga");
            this.resetNumPaso();
        }

        // Limpiamos la variables de cada tab
        super.resetTabs();

        // Generamos una nueva solicitud por si se se quiere crear una nueva
        this.setSolicitud(new SolicitudAsignacion());
        cargarFicheroTab.actualizaCampos();
        cargarFicheroTab.setTabHabilitado(true);
        tabGenerales.setTabHabilitado(true);
        numeracionAsignada.setTabHabilitado(true);
        analisis.setTabHabilitado(true);
        asignacion.setTabHabilitado(true);
        oficios.setTabHabilitado(true);
    }

    /**
     * Obtiene pestaña cargar fichero.
     * @return cargarFicheroTab
     */
    public CargarFicheroTab getCargarFicheroTab() {
        return cargarFicheroTab;
    }

    /**
     * carga pestaña cargar fichero.
     * @param cargarFicheroTab cargarFicheroTab to set
     */
    public void setCargarFicheroTab(CargarFicheroTab cargarFicheroTab) {
        this.cargarFicheroTab = cargarFicheroTab;
    }

    /**
     * Obtiene pestaña generales.
     * @return tabGenerales
     */
    public GeneralesAsignacionNgTab getTabGenerales() {
        return tabGenerales;
    }

    /**
     * Carga pestaña generales.
     * @param tabGenerales the tabGenerales to set
     */
    public void setTabGenerales(GeneralesAsignacionNgTab tabGenerales) {
        this.tabGenerales = tabGenerales;
    }

    /**
     * Obtiene pestaña de numeración.
     * @return numeracionAsignada
     */
    public NumeracionAsignacionNgTab getNumeracionAsignada() {
        return numeracionAsignada;
    }

    /**
     * carga pestaña de numeración.
     * @param numeracionAsignada numeracionAsignada to set
     */
    public void setNumeracionAsignada(NumeracionAsignacionNgTab numeracionAsignada) {
        this.numeracionAsignada = numeracionAsignada;
    }

    /**
     * Obtiene pestaña de anailis.
     * @return analisis
     */
    public AnalisisNgTab getAnalisis() {
        return analisis;
    }

    /**
     * Carga pestaña de analisis.
     * @param analisis analisis to set
     */
    public void setAnalisis(AnalisisNgTab analisis) {
        this.analisis = analisis;
    }

    /**
     * Obtiene pestaña de asignación.
     * @return asignacion
     */
    public AsignacionNgTab getAsignacion() {
        return asignacion;
    }

    /**
     * Carga pestaña de asignación.
     * @param asignacion asignacion to set
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
