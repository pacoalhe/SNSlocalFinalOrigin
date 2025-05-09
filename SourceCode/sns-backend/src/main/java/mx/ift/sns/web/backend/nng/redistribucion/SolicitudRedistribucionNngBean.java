package mx.ift.sns.web.backend.nng.redistribucion;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import mx.ift.sns.modelo.nng.SolicitudRedistribucionNng;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;
import mx.ift.sns.negocio.oficios.IOficiosFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.Wizard;
import mx.ift.sns.web.backend.ng.tabs.GeneracionOficiosTab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Bean asociado a la solicitud de redistribucion de numeración no geográfica. */
@ManagedBean(name = "solicitudRedistribucionNngBean")
@ViewScoped
public class SolicitudRedistribucionNngBean extends Wizard implements Serializable {

    /** Serializar. **/
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudRedistribucionNngBean.class);

    /** Id del componente de mensajes de PrimeFaces. */
    private static final String MSG_ID = "MSG_Wizard_SolicitudRedistribucionNNG";

    /** Facade de Numeración No Geográfica. */
    @EJB(mappedName = "NumeracionNoGeograficaFacade")
    private INumeracionNoGeograficaFacade nngFacade;

    /** Facade de Generación de Oficios. */
    @EJB(mappedName = "OficiosFacade")
    private IOficiosFacade oficiosFacade;

    /** Tab/Pestaña de selección de información genérica de solicitud. */
    private GeneralesRedistribucionNngTab tabGenerales;

    /** Tab/Pestaña de selección de redistribucion de numeraciones. */
    private RedistribucionNngTab tabRedistribucion;

    /** Tab/Pestaña de selección de generación de oficios. */
    private GeneracionOficiosTab tabGenerarOficio;

    /** Constructor, inicializa el Wizard. */
    public SolicitudRedistribucionNngBean() {
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
        tabGenerales = new GeneralesRedistribucionNngTab(this, nngFacade);
        tabGenerales.setId("Redistribucion NNG - TabGenerales");
        getPasosWizard().add(tabGenerales);

        // Pestaña 'Redistribucion'
        tabRedistribucion = new RedistribucionNngTab(this, nngFacade);
        tabRedistribucion.setId("Redistribucion NNG - TabRedistribucion");
        getPasosWizard().add(tabRedistribucion);

        // Tab de Generación de Oficios
        tabGenerarOficio = new GeneracionOficiosTab(this, oficiosFacade);
        tabGenerarOficio.setId("Redistribucion NNG - TabOficios");
        getPasosWizard().add(tabGenerarOficio);
    }

    /** Método invocado al pulsar sobre el botón de 'Nueva Redistribución'. */
    public void nuevaSolicitud() {
        try {
            // Iniciamos el tipo de solicitud específico.
            this.setSolicitud(new SolicitudRedistribucionNng());

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
     * @param pSolicitud SolicitudRedistribucionNng
     */
    public void cargarSolicitud(SolicitudRedistribucionNng pSolicitud) {
        try {
            // Cargamos las dependencias Lazy (Redistribuciones Solicitadas y Oficios)
            SolicitudRedistribucionNng solicitudLoaded = nngFacade.getSolicitudRedistribucionEagerLoad(pSolicitud);

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
                .findComponent("FORM_SolicitudRedistribucionNNG:WIZ_SolicitudRedistribucionNNG");

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
     * @return GeneralesRedistribucionNngTab
     */
    public GeneralesRedistribucionNngTab getTabGenerales() {
        return tabGenerales;
    }

    /**
     * Tab/Pestaña de selección de información genérica de redistribuciones.
     * @param tabGenerales GeneralesRedistribucionNgTab
     */
    public void setTabGenerales(GeneralesRedistribucionNngTab tabGenerales) {
        this.tabGenerales = tabGenerales;
    }

    /**
     * Tab/Pestaña de selección de redistribucion de numeraciones.
     * @return RedistribucionNngTab
     */
    public RedistribucionNngTab getTabRedistribucion() {
        return tabRedistribucion;
    }

    /**
     * Tab/Pestaña de selección de redistribucion de numeraciones.
     * @param tabRedistribucion RedistribucionNngTab
     */
    public void setTabRedistribucion(RedistribucionNngTab tabRedistribucion) {
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
