package mx.ift.sns.web.backend.ng.redistribucion;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.modelo.ng.SolicitudRedistribucionNg;
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

/** Bean asociado a la solicitud de redistribucion. */
@ManagedBean(name = "solicitudRedistribucionNgBean")
@ViewScoped
public class SolicitudRedistribucionNgBean extends Wizard implements Serializable {

    /** Serializar. **/
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudRedistribucionNgBean.class);

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_Wizard_SolicitudRedistribucion";

    /** Facade de Numeración Geográfica. */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    /** Facade de Generación de Oficios. */
    @EJB(mappedName = "OficiosFacade")
    private IOficiosFacade oficiosFacade;

    /** Tab/Pestaña de selección de información genérica de solicitud. */
    private GeneralesRedistribucionNgTab tabGenerales;

    /** Tab/Pestaña de selección de redistribucion de numeraciones. */
    private RedistribucionNgTab tabRedistribucion;

    /** Tab/Pestaña de selección de generación de oficios. */
    private GeneracionOficiosTab tabGenerarOficio;

    /** Constructor, inicializa el Wizard. */
    public SolicitudRedistribucionNgBean() {
        // Pasamos el id del p:messages del Wizard para mostrar
        // los errores al pasar de un tab a otro.
        setWizardMessagesId(MSG_ID);
    }

    /**
     * Inicializa el Bean y la lista de pasos del Wizard.
     * @throws Exception en caso de que no se puedan inicializar los tabs.
     */
    @PostConstruct
    public void init() throws Exception {
        // Importante: Cada tab del Wizard ha de ser agregado
        // en la lista 'pasosWizzard' en el mismo orden en el
        // que se agregan los tab en el xhtml.

        // Pestaña 'Generales'
        tabGenerales = new GeneralesRedistribucionNgTab(this, ngService);
        tabGenerales.setId("Redistribucion - TabGenerales");
        getPasosWizard().add(tabGenerales);

        // Pestaña 'Redistribucion'
        tabRedistribucion = new RedistribucionNgTab(this, ngService);
        tabRedistribucion.setId("Redistribucion - TabRedistribucion");
        getPasosWizard().add(tabRedistribucion);

        // Tab de Generación de Oficios
        tabGenerarOficio = new GeneracionOficiosTab(this, oficiosFacade);
        tabGenerarOficio.setId("Redistribucion - TabOficios");
        getPasosWizard().add(tabGenerarOficio);

        tabRedistribucion.setRegistroPorPagina(PaginatorUtil.getRegistrosPorPagina(ngService,
                Parametro.REGISTROS_POR_PAGINA_BACK_WIZ));
    }

    /** Método invocado al pulsar sobre el botón de 'Nueva Redistribución'. */
    public void nuevaSolicitud() {
        try {
            // Iniciamos el tipo de solicitud específico.
            this.setSolicitud(new SolicitudRedistribucionNg());

            // Actualizamos los campos de tabGenerales por ser el primer tab que aparece. El resto
            // de tabs se actualizarán al pulsar 'siguiente', el Wizard se encarga de invocar al
            // método 'actualizaCampos' de cada tab. Lo mismo ocurre con el método 'cargaValoresIniciales'
            if (!tabGenerales.isIniciado()) {
                tabGenerales.cargaValoresIniciales();
                tabGenerales.setIniciado(true);
            }
            tabGenerales.setTabHabilitado(true);
            tabRedistribucion.setTabHabilitado(true);
            tabGenerarOficio.setTabHabilitado(true);
            tabGenerales.actualizaCampos();
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Precarga una solicitud existente para su edición. Actualiza las referecnias del Wizard. para el resto de tabs.
     * @param pSolicitud SolicitudRedistribucionNg
     */
    public void cargarSolicitud(SolicitudRedistribucionNg pSolicitud) {
        try {
            // Cargamos las dependencias Lazy (Redistribuciones Solicitadas, Aplicadas y Oficios)
            SolicitudRedistribucionNg solicitudLoaded = ngService.getSolicitudRedistribucionEagerLoad(pSolicitud);

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
                tabRedistribucion.setTabHabilitado(true);
                tabGenerarOficio.setTabHabilitado(true);
            } else if (solicitudLoaded.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_TERMINADA)) {
                // La solicitud ya está terminada, no se permite editar generales
                tabGenerales.setTabHabilitado(false);
                tabRedistribucion.setTabHabilitado(true);
                tabGenerarOficio.setTabHabilitado(true);
            } else {
                // La solicitud está cancelada, no se permite hacer nada con ella.
                tabGenerales.setTabHabilitado(false);
                tabRedistribucion.setTabHabilitado(false);
                tabGenerarOficio.setTabHabilitado(false);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    @Override
    public void resetTabs() {
        // Restablecemos el Wizard en la primera pestaña
        org.primefaces.component.wizard.Wizard jsfWizard;
        jsfWizard = (org.primefaces.component.wizard.Wizard) FacesContext.getCurrentInstance().getViewRoot()
                .findComponent("FORM_SolicitudRedistribucion:WIZ_SolicitudRedistribucion");

        if (jsfWizard != null) {
            jsfWizard.setStep("WIZ_SolicitudRedist_TAB_Proveedor");
            this.resetNumPaso();
        }

        // Limpiamos la variables de cada tab
        super.resetTabs();
    }

    // GETTERS & SETTERS

    /**
     * Tab/Pestaña de selección de información genérica de redistribuciones.
     * @return GeneralesRedistribucionNgTab
     */
    public GeneralesRedistribucionNgTab getTabGenerales() {
        return tabGenerales;
    }

    /**
     * Tab/Pestaña de selección de información genérica de redistribuciones.
     * @param tabGenerales GeneralesRedistribucionNgTab
     */
    public void setTabGenerales(GeneralesRedistribucionNgTab tabGenerales) {
        this.tabGenerales = tabGenerales;
    }

    /**
     * Tab/Pestaña con la información de la numeración a redistribuir.
     * @return RedistribucionNgTab
     */
    public RedistribucionNgTab getTabRedistribucion() {
        return tabRedistribucion;
    }

    /**
     * Tab/Pestaña con la información de la numeración a redistribuir.
     * @param tabRedistribucion RedistribucionNgTab
     */
    public void setTabRedistribucion(RedistribucionNgTab tabRedistribucion) {
        this.tabRedistribucion = tabRedistribucion;
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
