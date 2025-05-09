package mx.ift.sns.web.backend.nng.redistribucion;

import java.io.Serializable;

/**
 * Representa la información de consulta de una solicitud de redistribución adaptada al front.
 */
public class DetalleConsultaRedistNng implements Serializable {

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

    /** Fecha de redistribución de la numeración. */
    private String fechaRedist = "";

    /** Número de oficio del Proveedor Solicitante. */
    private String numOficio = "";

    /** Estado de la solicitud. */
    private String estatus = "";

    /** Indica si es posible eliminar una solicitud de Liberación. */
    private boolean cancelarDisponible = false;

    // GETTERS & SETTERS

    /**
     * Identificador de la solicitud.
     * @return String
     */
    public String getConsecutivo() {
        return consecutivo;
    }

    /**
     * Identificador de la solicitud.
     * @param consecutivo String
     */
    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    /**
     * Fecha de creación de la solicitud.
     * @return String
     */
    public String getFechaSolicitud() {
        return fechaSolicitud;
    }

    /**
     * Fecha de creación de la solicitud.
     * @param fechaSolicitud String
     */
    public void setFechaSolicitud(String fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    /**
     * Número de oficio del Proveedor Solicitante.
     * @return String
     */
    public String getNumOficio() {
        return numOficio;
    }

    /**
     * Número de oficio del Proveedor Solicitante.
     * @param numOficio String
     */
    public void setNumOficio(String numOficio) {
        this.numOficio = numOficio;
    }

    /**
     * Estado de la solicitud.
     * @return String
     */
    public String getEstatus() {
        return estatus;
    }

    /**
     * Estado de la solicitud.
     * @param estatus String
     */
    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    /**
     * Nombre proveedor solicitante.
     * @return String
     */
    public String getSolicitante() {
        return solicitante;
    }

    /**
     * Nombre proveedor solicitante.
     * @param solicitante String
     */
    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    /**
     * Fecha del Oficio del Pst Solicitante.
     * @return String
     */
    public String getFechaOficio() {
        return fechaOficio;
    }

    /**
     * Fecha del Oficio del Pst Solicitante.
     * @param fechaOficio String
     */
    public void setFechaOficio(String fechaOficio) {
        this.fechaOficio = fechaOficio;
    }

    /**
     * Indica si es posible eliminar la solicitud.
     * @return True si es posible.
     */
    public boolean isCancelarDisponible() {
        return cancelarDisponible;
    }

    /**
     * Indica si es posible eliminar la solicitud.
     * @param cancelarDisponible boolean
     */
    public void setCancelarDisponible(boolean cancelarDisponible) {
        this.cancelarDisponible = cancelarDisponible;
    }

    /**
     * Fecha de redistribución de la numeración.
     * @return String
     */
    public String getFechaRedist() {
        return fechaRedist;
    }

    /**
     * Fecha de redistribución de la numeración.
     * @param fechaRedist String
     */
    public void setFechaRedist(String fechaRedist) {
        this.fechaRedist = fechaRedist;
    }
}
