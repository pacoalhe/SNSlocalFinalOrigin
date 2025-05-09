package mx.ift.sns.web.backend.ng.consolidacion;

import java.io.Serializable;
import java.util.ArrayList;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.ng.AbnConsolidar;
import mx.ift.sns.modelo.ng.SolicitudConsolidacion;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Bean asociado a los trámites de Consolidación de ABN's. */
@ManagedBean(name = "solicitudConsolidacionBean")
@ViewScoped
public class SolicitudConsolidacionBean implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudConsolidacionBean.class);

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultarConsolidaciones";

    /** Gestor de Consolidaciones. */
    private GeneralesConsolidacion consolidacion;

    /** Servicio de Numeración Geográfica. */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    /** Constructor. Vacío por defecto. */
    public SolicitudConsolidacionBean() {

    }

    // @PostConstruct
    // public void init() {}

    /**
     * Crea una nueva solicitud de Consolidación y precarga el interfaz de consolidaciones.
     */
    public void nuevaSolicitud() {
        // Nuevo interfaz de Consolidaciones
        SolicitudConsolidacion solicitud = new SolicitudConsolidacion();
        solicitud.setAbnEntrega(new Abn());
        solicitud.setAbnRecibe(new Abn());
        solicitud.setAbnsConsolidados(new ArrayList<AbnConsolidar>());

        consolidacion = new GeneralesConsolidacion(solicitud, ngService);
        consolidacion.setInterfazHabilitado(true);
    }

    /**
     * Precarga una solicitud existente para su edición. Actualiza las referecnias del Wizard. para el resto de tabs.
     * @param pSolicitud SolicitudLiberacionNg
     */
    public void cargarSolicitud(SolicitudConsolidacion pSolicitud) {
        try {
            // Cargamos las dependencias Lazy
            SolicitudConsolidacion solicitudLoaded = ngService.getSolicitudConsolidacionEagerLoad(pSolicitud);

            // Nuevo interfaz de Consolidaciones
            consolidacion = new GeneralesConsolidacion(solicitudLoaded, ngService);
            consolidacion.setInterfazHabilitado(solicitudLoaded.getEstadoSolicitud().getCodigo()
                    .equals(EstadoSolicitud.SOLICITUD_EN_TRAMITE));

            // Actualizamos los Abns y Nirs consolidados
            solicitudLoaded.getAbnEntrega().setNirs(
                    ngService.findAllNirByAbn(solicitudLoaded.getAbnEntrega().getCodigoAbn()));

            solicitudLoaded.getAbnRecibe().setNirs(
                    ngService.findAllNirByAbn(solicitudLoaded.getAbnRecibe().getCodigoAbn()));

            for (AbnConsolidar abnConso : solicitudLoaded.getAbnsConsolidados()) {
                abnConso.setNirConsolidar(ngService.findNirConsolidarById(abnConso.getId()));
                abnConso.setPoblacionConsolidar(ngService.findPoblacionConsolidarById(abnConso.getId()));
            }

            if (solicitudLoaded.getTipoConsolidacion().equals(SolicitudConsolidacion.CONSOLIDACION_TOTAL)) {
                consolidacion.cargarEdicionConsolidacionTotalABNs();
            } else {
                consolidacion.cargarEdicionConsolidacionParcialABNs();
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al cerrar el interfaz de Consolidaciones. Como se mantiene la estructura hecha para interfaces
     * con wizard, siempre se invoca a un método al cerrar la modal. Invocamos un método vacío para evitar problemas.
     */
    public void doNothing() {
    }

    // GETTERS & SETTERS

    /**
     * Gestor de Consolidaciones.
     * @return GeneralesConsolidacion
     */
    public GeneralesConsolidacion getConsolidacion() {
        return consolidacion;
    }

    /**
     * Gestor de Consolidaciones.
     * @param consolidacion GeneralesConsolidacion
     */
    public void setConsolidacion(GeneralesConsolidacion consolidacion) {
        this.consolidacion = consolidacion;
    }

}
