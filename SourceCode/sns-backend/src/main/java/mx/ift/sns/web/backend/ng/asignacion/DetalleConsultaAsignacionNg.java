package mx.ift.sns.web.backend.ng.asignacion;

import java.io.Serializable;

import mx.ift.sns.modelo.ng.SolicitudAsignacion;

/**
 * Representa la información de consulta de una solicitud de liberación adaptada al front.
 */
public class DetalleConsultaAsignacionNg implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador de la solicitud. */
    private String consecutivo = "";

    /** Nombre proveedor solicitante. */
    private String solicitante;

    /** Fecha del Oficio del Pst Solicitante. */
    private String fechaOficio = "";

    /** Fecha de creación de la solicitud. */
    private String fechaSolicitud = "";

    /** Fecha de asignacion de la solicitud. */
    private String fechaAsignacion = "";

    /** Número de oficio del Proveedor Solicitante. */
    private String numOficio = "";

    /** Estado de la solicitud. */
    private String estatus = "";

    /** Solicitud. */
    private SolicitudAsignacion solicitud;

    // GETTERS & SETTERS

    /**
     * Obtiene Identificador de la solicitud.
     * @return consecutivo
     */
    public String getConsecutivo() {
        return consecutivo;
    }

    /**
     * Carga Identificador de la solicitud.
     * @param consecutivo consecutivo to set
     */
    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    /**
     * Obtiene Fecha de creación de la solicitud.
     * @return the fechaSolicitud
     */
    public String getFechaSolicitud() {
        return fechaSolicitud;
    }

    /**
     * Carga Fecha de creación de la solicitud.
     * @param fechaSolicitud the fechaSolicitud to set
     */
    public void setFechaSolicitud(String fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    /**
     * obtiene Número de oficio del Proveedor Solicitante.
     * @return numOficio
     */
    public String getNumOficio() {
        return numOficio;
    }

    /**
     * Carga Número de oficio del Proveedor Solicitante.
     * @param numOficio numOficio to set
     */
    public void setNumOficio(String numOficio) {
        this.numOficio = numOficio;
    }

    /**
     * Obtiene Estado de la solicitud.
     * @return the estatus
     */
    public String getEstatus() {
        return estatus;
    }

    /**
     * Carga Estado de la solicitud.
     * @param estatus the estatus to set
     */
    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    /**
     * Obtiene nombre proveedor solicitante.
     * @return solicitante
     */
    public String getSolicitante() {
        return solicitante;
    }

    /**
     * Carga nombre proveedor solicitante.
     * @param solicitante solicitante to set
     */
    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    /**
     * Obtiene Fecha del Oficio del Pst Solicitante.
     * @return fechaOficio
     */
    public String getFechaOficio() {
        return fechaOficio;
    }

    /**
     * Carga Fecha del Oficio del Pst Solicitante.
     * @param fechaOficio fechaOficio to set
     */
    public void setFechaOficio(String fechaOficio) {
        this.fechaOficio = fechaOficio;
    }

    /**
     * Obtiene Fecha de asignacion de la solicitud.
     * @return fechaAsignacion
     */
    public String getFechaAsignacion() {
        return fechaAsignacion;
    }

    /**
     * Carga Fecha de asignacion de la solicitud.
     * @param fechaAsignacion fechaAsignacion to set
     */
    public void setFechaAsignacion(String fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    /**
     * Obtiene Solicitud.
     * @return solicitud
     */
    public SolicitudAsignacion getSolicitud() {
        return solicitud;
    }

    /**
     * Carga Solicitud.
     * @param solicitud solicitud to set
     */
    public void setSolicitud(SolicitudAsignacion solicitud) {
        this.solicitud = solicitud;
    }
}
