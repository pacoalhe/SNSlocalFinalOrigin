package mx.ift.sns.web.backend.nng.asignacion;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.modelo.nng.SolicitudAsignacionNng;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;
import mx.ift.sns.negocio.oficios.IOficiosFacade;
import mx.ift.sns.web.backend.ApplicationException;
import mx.ift.sns.web.backend.ac.claveservicio.EdicionClaveServicioBean;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.Wizard;
import mx.ift.sns.web.backend.ng.tabs.GeneracionOficiosTab;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean de la solicitud de asignación de numeración no geografica.
 * @author X36155QU
 */
@ManagedBean(name = "solicitudAsignacionNngBean")
@ViewScoped
public class SolicitudAsignacionNngBean extends Wizard implements Serializable {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudAsignacionNngBean.class);

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_Wizard_SolicitudAsignacionNNG";

    /** Fachada de Numeración No Geográfica. */
    @EJB(mappedName = "NumeracionNoGeograficaFacade")
    private INumeracionNoGeograficaFacade nngFacade;

    /** Fachada de oficios. */
    @EJB(mappedName = "OficiosFacade")
    private IOficiosFacade oficiosFacade;

    /** Referencia al Bean de claves de servicio. */
    @ManagedProperty("#{edicionClaveServicioBean}")
    private EdicionClaveServicioBean claveServicioBean;

    /** Pestaña Datos Generales. */
    private GeneralesAsignacionNngTab tabGenerales;

    /** Pestaña de Analisis. */
    private AnalisisAsignacionNngTab tabAnalisis;

    /** Pestaña de Asignacion. */
    private AsignacionNngTab tabAsignacion;

    /** Pestaña de oficios. */
    private GeneracionOficiosTab tabOficios;

    /** Constructor, inicializa el Wizard. */
    public SolicitudAsignacionNngBean() {
        // Pasamos el id del p:messages del Wizard para mostrar
        // los errores al pasar de un tab a otro.
        setWizardMessagesId(MSG_ID);

        // Iniciamos el tipo de solicitud específico.
        setSolicitud(new SolicitudAsignacionNng());
    }

    /** Inicializa el Bean y la lista de pasos del Wizard. */
    @PostConstruct
    public void init() {
        try {
            // Importante: Cada tab del Wizard ha de ser agregado
            // en la lista 'pasosWizzard' en el mismo orden en el
            // que se agregan los tab en el xhtml.

            // Pestaña 'Generales'
            tabGenerales = new GeneralesAsignacionNngTab(this, nngFacade);
            tabGenerales.setId("Asignación Solicitudes NNG - TabGenerales");
            getPasosWizard().add(tabGenerales);

            // Pestaña 'Analisis'
            tabAnalisis = new AnalisisAsignacionNngTab(this, nngFacade, claveServicioBean);
            tabAnalisis.setId("Asignación Solicitudes NNG - TabAnalisis");
            getPasosWizard().add(tabAnalisis);

            // Pestaña 'Asignacion'
            tabAsignacion = new AsignacionNngTab(this, nngFacade);
            tabAsignacion.setId("Asignación Solicitudes NNG - TabAsignacion");
            getPasosWizard().add(tabAsignacion);

            // Pestaña 'Oficios'
            tabOficios = new GeneracionOficiosTab(this, oficiosFacade);
            tabOficios.setId("Asignación Solicitudes NNG - TabOficios");
            getPasosWizard().add(tabOficios);

            tabAnalisis.setRegistroPorPagina(PaginatorUtil.getRegistrosPorPagina(nngFacade,
                    Parametro.REGISTROS_POR_PAGINA_BACK_WIZ));
            tabAsignacion.setRegistroPorPagina(PaginatorUtil.getRegistrosPorPagina(nngFacade,
                    Parametro.REGISTROS_POR_PAGINA_BACK_WIZ));

        } catch (Exception e) {
            throw new ApplicationException();
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
                .findComponent("FORM_SolicitudAsignacionNNG:WIZ_SolicitudAsignacion");

        if (jsfWizard != null) {
            jsfWizard.setStep("tab_general_asignacion");
            this.resetNumPaso();
        }

        // Limpiamos la variables de cada tab
        super.resetTabs();

        // Generamos una nueva solicitud por si se se quiere crear una nueva
        setSolicitud(new SolicitudAsignacionNng());
        tabGenerales.actualizaCampos();
        tabGenerales.setTabHabilitado(true);

    }

    /**
     * Carga la solicitud de asignación.
     * @param pSolicitud solicitud
     */
    public void cargarSolicitud(SolicitudAsignacionNng pSolicitud) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("solicitud {}", pSolicitud);
        }

        try {
            // Cargamos las dependencias Lazy (Asignaciones Solicitadas, Aplicadas y Oficios)
            SolicitudAsignacionNng solicitudLoaded = nngFacade.getSolicitudAsignacionEagerLoad(pSolicitud);

            // Asociamos la solicitud cargada en al Wizard
            this.setSolicitud(solicitudLoaded);

            // Actualizamos los campos de tabGenerales por ser el primer tab que aparece. El resto
            // de tabs se actualizarán al pulsar 'siguiente', el Wizard se encarga de invocar al
            // método 'actualizaCampos' de cada tab.
            tabGenerales.actualizaCampos();

            // Acciones en función del estado de la solicitud
            if (solicitudLoaded.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_EN_TRAMITE)) {

                tabGenerales.setTabHabilitado(true);
                tabAnalisis.setTabHabilitado(true);
                // asignacion.setTabHabilitado(true);
                tabOficios.setTabHabilitado(true);

            } else if (solicitudLoaded.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_TERMINADA)) {

                // La solicitud ya está terminada, solamente se permiten editar los oficios.
                tabGenerales.setTabHabilitado(false);
                tabAnalisis.setTabHabilitado(false);
                // asignacion.setTabHabilitado(true);
                tabOficios.setTabHabilitado(true);

            } else {

                // La solicitud está cancelada, no se permite hacer nada con ella.
                tabGenerales.setTabHabilitado(false);
                tabAnalisis.setTabHabilitado(false);
                // asignacion.setTabHabilitado(false);
                tabOficios.setTabHabilitado(false);

            }
        } catch (Exception e) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }

    }

    // GETTERS & SETTERS
    /**
     * Pestaña Datos Generales.
     * @return tabGenerales
     */
    public GeneralesAsignacionNngTab getTabGenerales() {
        return tabGenerales;
    }

    /**
     * Pestaña Datos Generales.
     * @param tabGenerales tabGenerales to set
     */
    public void setTabGenerales(GeneralesAsignacionNngTab tabGenerales) {
        this.tabGenerales = tabGenerales;
    }

    /**
     * Pestaña de Analisis.
     * @return tabAnalisis
     */
    public AnalisisAsignacionNngTab getTabAnalisis() {
        return tabAnalisis;
    }

    /**
     * Pestaña de Analisis.
     * @param tabAnalisis tabAnalisis to set
     */
    public void setTabAnalisis(AnalisisAsignacionNngTab tabAnalisis) {
        this.tabAnalisis = tabAnalisis;
    }

    /**
     * Referencia al Bean de claves de servicio.
     * @return claveServicioBean
     */
    public EdicionClaveServicioBean getClaveServicioBean() {
        return claveServicioBean;
    }

    /**
     * Referencia al Bean de claves de servicio.
     * @param claveServicioBean claveServicioBean to set
     */
    public void setClaveServicioBean(EdicionClaveServicioBean claveServicioBean) {
        this.claveServicioBean = claveServicioBean;
    }

    /**
     * Pestaña de Asignacion.
     * @return tabAsignacion
     */
    public AsignacionNngTab getTabAsignacion() {
        return tabAsignacion;
    }

    /**
     * Pestaña de Asignacion.
     * @param tabAsignacion tabAsignacion to set
     */
    public void setTabAsignacion(AsignacionNngTab tabAsignacion) {
        this.tabAsignacion = tabAsignacion;
    }

    /**
     * Pestaña de oficios.
     * @return tabOficios
     */
    public GeneracionOficiosTab getTabOficios() {
        return tabOficios;
    }

    /**
     * Pestaña de oficios.
     * @param tabOficios tabOficios to set
     */
    public void setTabOficios(GeneracionOficiosTab tabOficios) {
        this.tabOficios = tabOficios;
    }

}
