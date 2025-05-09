package mx.ift.sns.web.backend.cpsi.liberacion;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import mx.ift.sns.modelo.cpsi.SolicitudLiberacionCpsi;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.cpsi.ICodigoCPSIFacade;
import mx.ift.sns.negocio.oficios.IOficiosFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.Wizard;
import mx.ift.sns.web.backend.ng.tabs.GeneracionOficiosTab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Bean asociado a los trámites de Liberación de CPSI. */
@ManagedBean(name = "solicitudLiberacionCpsiBean")
@ViewScoped
public class SolicitudLiberacionCpsiBean extends Wizard implements Serializable {

    /** Control Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudLiberacionCpsiBean.class);

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_Wizard_SolicitudLiberacionCPSI";

    /** Facade de Servicios para Códigos de Puntos de Señalización Internacional. */
    @EJB(mappedName = "CodigoCPSIFacade")
    private ICodigoCPSIFacade cpsiFacade;

    /** Facade de Generación de Oficios. */
    @EJB(mappedName = "OficiosFacade")
    private IOficiosFacade oficiosFacade;

    /** Tab/Pestaña de selección de información genérica de solicitud. */
    private GeneralesLiberacionCpsiTab tabGenerales;

    /** Tab/Pestaña de selección de liberación de numeraciones. */
    private LiberacionesCpsiTab tabLiberacion;

    /** Tab/Pestaña de selección de generación de oficios. */
    private GeneracionOficiosTab tabGenerarOficio;

    /** Constructor, inicializa el Wizard. */
    public SolicitudLiberacionCpsiBean() {
        // Pasamos el id del p:messages del Wizard para mostrar
        // los errores al pasar de un tab a otro.
        setWizardMessagesId(MSG_ID);
    }

    /**
     * Inicializa el Bean y la lista de pasos del Wizard.
     * @throws Exception En caso de error
     */
    @PostConstruct
    public void init() throws Exception {
        // Importante: Cada tab del Wizard ha de ser agregado
        // en la lista 'pasosWizzard' en el mismo orden en el
        // que se agregan los tab en el xhtml.

        // Pestaña 'Generales'
        tabGenerales = new GeneralesLiberacionCpsiTab(this, cpsiFacade);
        tabGenerales.setId("Liberación Solicitudes CPSI - TabGenerales");
        getPasosWizard().add(tabGenerales);

        // Pestaña 'Liberación'
        tabLiberacion = new LiberacionesCpsiTab(this, cpsiFacade);
        tabLiberacion.setId("Liberación Solicitudes CPSI - TabLiberacion");
        getPasosWizard().add(tabLiberacion);

        // Tab de Generación de Oficios
        tabGenerarOficio = new GeneracionOficiosTab(this, oficiosFacade);
        tabGenerarOficio.setId("Liberación Solicitudes CPSI - TabOficios");
        getPasosWizard().add(tabGenerarOficio);
    }

    /** Método invocado al pulsar sobre el botón de 'Nueva Liberación'. */
    public void nuevaSolicitud() {
        try {
            // Iniciamos el tipo de solicitud específico.
            this.setSolicitud(new SolicitudLiberacionCpsi());

            // Actualizamos los campos de tabGenerales por ser el primer tab que aparece. El resto
            // de tabs se actualizarán al pulsar 'siguiente', el Wizard se encarga de invocar al
            // método 'actualizaCampos' de cada tab. Lo mismo ocurre con el método 'cargaValoresIniciales'
            if (!tabGenerales.isIniciado()) {
                tabGenerales.cargaValoresIniciales();
                tabGenerales.setIniciado(true);
            }
            tabGenerales.setTabHabilitado(true);
            tabLiberacion.setTabHabilitado(true);
            tabGenerarOficio.setTabHabilitado(true);
            tabGenerales.actualizaCampos();
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Precarga una solicitud existente para su edición. Actualiza las referecnias del Wizard. para el resto de tabs.
     * @param pSolicitud SolicitudLiberacionCpsi
     */
    public void cargarSolicitud(SolicitudLiberacionCpsi pSolicitud) {
        try {
            // Cargamos las dependencias Lazy
            SolicitudLiberacionCpsi solicitudLoaded = cpsiFacade.getSolicitudLiberacionEagerLoad(pSolicitud);

            // Asociamos la solicitud cargada en al Wizard
            this.setSolicitud(solicitudLoaded);

            // Actualizamos los campos de tabGenerales por ser el primer tab que aparece.
            if (!tabGenerales.isIniciado()) {
                tabGenerales.cargaValoresIniciales();
                tabGenerales.setIniciado(true);
            }
            tabGenerales.actualizaCampos();

            // Acciones en función del estado de la solicitud
            if (solicitudLoaded.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_EN_TRAMITE)) {
                tabGenerales.setTabHabilitado(true);
                tabLiberacion.setTabHabilitado(true);
                tabGenerarOficio.setTabHabilitado(true);
            } else if (solicitudLoaded.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_TERMINADA)) {
                // La solicitud ya está terminada, no se permite modificar el tab de generales
                tabGenerales.setTabHabilitado(false);
                tabLiberacion.setTabHabilitado(true);
                tabGenerarOficio.setTabHabilitado(true);
            } else {
                // La solicitud está cancelada, no se permite hacer nada con ella.
                tabGenerales.setTabHabilitado(false);
                tabLiberacion.setTabHabilitado(false);
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
                .findComponent("FORM_SolicitudLiberacionCPSI:WIZ_SolicitudLiberacion");

        if (jsfWizard != null) {
            jsfWizard.setStep("WIZ_SolicitudLiberacion_TAB_Proveedor");
            this.resetNumPaso();
        }

        // Limpiamos la variables de cada tab
        super.resetTabs();
    }

    // GETTERS & SETTERS

    /**
     * Tab/Pestaña de selección de información genérica de solicitud.
     * @return GeneralesLiberacionCpsiTab
     */
    public GeneralesLiberacionCpsiTab getTabGenerales() {
        return tabGenerales;
    }

    /**
     * Tab/Pestaña de selección de información genérica de solicitud.
     * @param tabGenerales GeneralesLiberacionCpsiTab
     */
    public void setTabGenerales(GeneralesLiberacionCpsiTab tabGenerales) {
        this.tabGenerales = tabGenerales;
    }

    /**
     * Tab/Pestaña de selección de generación de oficios.
     * @return TabOficios
     */
    public GeneracionOficiosTab getTabGenerarOficio() {
        return tabGenerarOficio;
    }

    /**
     * Tab/Pestaña de selección de generación de oficios.
     * @param tabGenerarOficio TabOficios
     */
    public void setTabGenerarOficio(GeneracionOficiosTab tabGenerarOficio) {
        this.tabGenerarOficio = tabGenerarOficio;
    }

    /**
     * Tab/Pestaña de selección de liberación de CPSI.
     * @return LiberacionesCpsiTab
     */
    public LiberacionesCpsiTab getTabLiberacion() {
        return tabLiberacion;
    }

    /**
     * Tab/Pestaña de selección de liberación de CPSI.
     * @param tabLiberacion LiberacionesCpsiTab
     */
    public void setTabLiberacion(LiberacionesCpsiTab tabLiberacion) {
        this.tabLiberacion = tabLiberacion;
    }
}
