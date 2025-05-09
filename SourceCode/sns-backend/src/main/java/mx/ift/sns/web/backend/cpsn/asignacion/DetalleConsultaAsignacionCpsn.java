package mx.ift.sns.web.backend.cpsn.asignacion;

import java.io.Serializable;

/** Representa la información de consulta de una solicitud de asignación cpsn adaptada al front. */
public class DetalleConsultaAsignacionCpsn implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador de la solicitud. */
    private String consecutivo = "";

    /** Fecha de solicitud. */
    private String fechaSolicitud = "";

    /** Fecha de asignacion. */
    private String fechaAsignacion = "";

    /** Estado de la solicitud. */
    private String estatus = "";

    /** Proveedor. */
    private String proveedor = "";

    /** Número de Oficio. */
    private String numOficio = "";

    /** Indica si es posible eliminar una solicitud de Liberación. */
    private boolean cancelarDisponible = false;

    // GETTERS & SETTERS

    /**
     * Identificador de solicitud.
     * @return String
     */
    public String getConsecutivo() {
        return consecutivo;
    }

    /**
     * Identificador de solicitud.
     * @param consecutivo String
     */
    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    /**
     * Indicador de la Fecha de la solicitud.
     * @return String
     */
    public String getFechaSolicitud() {
        return fechaSolicitud;
    }

    /**
     * Indicador de la Fecha de la solicitud.
     * @param fechaSolicitud String
     */
    public void setFechaSolicitud(String fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    /**
     * Indicador de la Fecha de asignación.
     * @return String
     */
    public String getFechaAsignacion() {
        return fechaAsignacion;
    }

    /**
     * Indicador de la Fecha de asignación.
     * @param fechaAsignacion String
     */
    public void setFechaAsignacion(String fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    /**
     * Indicador del proveedor.
     * @return String
     */
    public String getProveedor() {
        return proveedor;
    }

    /**
     * Indicador del proveedor.
     * @param proveedor String
     */
    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * Indicador del estado de la solicitud.
     * @return String
     */
    public String getEstatus() {
        return estatus;
    }

    /**
     * Indicador del estado de la solicitud.
     * @param estatus String
     */
    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    /**
     * Indicador del Número de Oficio de la solicitud.
     * @return String
     */
    public String getNumOficio() {
        return numOficio;
    }

    /**
     * Indicador del Número de Oficio de la solicitud.
     * @param numOficio String
     */
    public void setNumOficio(String numOficio) {
        this.numOficio = numOficio;
    }

    /**
     * Identifica si se puede cancelar o no.
     * @return boolean
     */
    public boolean isCancelarDisponible() {
        return cancelarDisponible;
    }

    /**
     * Identifica si se puede cancelar o no.
     * @param cancelarDisponible boolean
     */
    public void setCancelarDisponible(boolean cancelarDisponible) {
        this.cancelarDisponible = cancelarDisponible;
    }

}
