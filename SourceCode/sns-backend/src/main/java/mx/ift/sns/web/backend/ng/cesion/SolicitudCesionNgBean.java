package mx.ift.sns.web.backend.ng.cesion;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.modelo.ng.SolicitudCesionNg;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
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
 * Bean asociado a la solicitud de Cesiones de Solicitudes de Numeración Geográfica.
 */
@ManagedBean(name = "solicitudCesionNgBean")
@ViewScoped
public class SolicitudCesionNgBean extends Wizard implements Serializable {

    /** Control Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudCesionNgBean.class);

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_Wizard_SolicitudCesion";

    /** Facade de Numeración Geográfica. */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    /** Facade de Generación de Oficios. */
    @EJB(mappedName = "OficiosFacade")
    private IOficiosFacade oficiosFacade;

    /** Tab/Pestaña de selección de información genérica de cesiones. */
    private GeneralesCesionNgTab tabGeneralesCesion;

    /** Tab/Pestaña de selección de información de cedente y cesionario. */
    private CesionNgDerechosTab tabCesionDerechos;

    /** Tab/Pestaña de selección de información la numeración cedida. */
    private CesionNgNumeracionTab tabCesionNumeracion;

    /** Tab/Pestaña de selección de generación de oficios. */
    private GeneracionOficiosTab tabGenerarOficio;

    /** Constructor, inicializa el Wizard. */
    public SolicitudCesionNgBean() {
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
        tabGeneralesCesion = new GeneralesCesionNgTab(this, ngService);
        tabGeneralesCesion.setId("Cesión Solicitudes - TabGenerales");
        getPasosWizard().add(tabGeneralesCesion);

        // Pestaña 'Cesión Derechos'
        tabCesionDerechos = new CesionNgDerechosTab(this, ngService);
        tabCesionDerechos.setId("Cesión Solicitudes - TabCesionDerechos");
        getPasosWizard().add(tabCesionDerechos);

        // Pestaña 'Cesión Numeración'
        tabCesionNumeracion = new CesionNgNumeracionTab(this, ngService);
        tabCesionNumeracion.setId("Cesión Solicitudes - TabCesionNumeracion");
        getPasosWizard().add(tabCesionNumeracion);

        // Tab de Generación de Oficios
        tabGenerarOficio = new GeneracionOficiosTab(this, oficiosFacade);
        tabGenerarOficio.setId("Cesión Solicitudes - TabOficios");
        getPasosWizard().add(tabGenerarOficio);

        tabCesionNumeracion.setRegistroPorPagina(PaginatorUtil.getRegistrosPorPagina(ngService,
                Parametro.REGISTROS_POR_PAGINA_BACK_WIZ));
    }

    /** Método invocado al pulsar sobre el botón de 'Nueva Cesión'. */
    public void nuevaSolicitud() {
        try {
            // Iniciamos el tipo de solicitud específico.
            this.setSolicitud(new SolicitudCesionNg());

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
     * Precarga una solicitud existente para su edición. Actualiza las referecnias del Wizard. para el resto de tabs.
     * @param pSolicitud SolicitudCesionNg
     */
    public void cargarSolicitud(SolicitudCesionNg pSolicitud) {
        try {
            // Cargamos las dependencias Lazy (Cesiones Solicitadas, Aplicadas y Oficios)
            SolicitudCesionNg solicitudLoaded = ngService.getSolicitudCesionEagerLoad(pSolicitud);

            // Asociamos la solicitud cargada en al Wizard
            this.setSolicitud(solicitudLoaded);

            // Actualizamos los campos de tabGenerales por ser el primer tab que aparece. El resto
            // de tabs se actualizarán al pulsar 'siguiente', el Wizard se encarga de invocar al
            // método 'actualizaCampos' de cada tab. Lo mismo ocurre con el método 'cargaValoresIniciales'
            if (!tabGeneralesCesion.isIniciado()) {
                tabGeneralesCesion.cargaValoresIniciales();
                tabGeneralesCesion.setIniciado(true);
            }
            tabGeneralesCesion.actualizaCampos();

            // Acciones en función del estado de la solicitud
            if (solicitudLoaded.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_EN_TRAMITE)) {
                tabGeneralesCesion.setTabHabilitado(true);
                tabCesionNumeracion.setTabHabilitado(true);
                tabCesionDerechos.setTabHabilitado(true);
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
                .findComponent("FORM_SolicitudCesion:WIZ_SolicitudCesion");

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
     * @return GeneralesCesionNgTab
     */
    public GeneralesCesionNgTab getTabGeneralesCesion() {
        return tabGeneralesCesion;
    }

    /**
     * Tab/Pestaña de selección de información genérica de cesiones.
     * @param tabGeneralesCesion GeneralesCesionNgTab
     */
    public void setTabGeneralesCesion(GeneralesCesionNgTab tabGeneralesCesion) {
        this.tabGeneralesCesion = tabGeneralesCesion;
    }

    /**
     * Tab/Pestaña de selección de información de cedente y cesionario.
     * @return CesionNgDerechosTab
     */
    public CesionNgDerechosTab getTabCesionDerechos() {
        return tabCesionDerechos;
    }

    /**
     * Tab/Pestaña de selección de información de cedente y cesionario.
     * @param tabCesionDerechos CesionNgDerechosTab
     */
    public void setTabCesionDerechos(CesionNgDerechosTab tabCesionDerechos) {
        this.tabCesionDerechos = tabCesionDerechos;
    }

    /**
     * Tab/Pestaña de selección de información la numeración cedida.
     * @return CesionNgNumeracionTab
     */
    public CesionNgNumeracionTab getTabCesionNumeracion() {
        return tabCesionNumeracion;
    }

    /**
     * Tab/Pestaña de selección de información la numeración cedida.
     * @param tabCesionNumeracion CesionNgNumeracionTab
     */
    public void setTabCesionNumeracion(CesionNgNumeracionTab tabCesionNumeracion) {
        this.tabCesionNumeracion = tabCesionNumeracion;
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
