package mx.ift.sns.web.backend.ng.cesion;

import java.io.Serializable;

/**
 * Representa la información de consulta de una solicitud de cesión adaptada al front.
 */
public class DetalleConsultaCesionNg implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador de la solicitud. */
    private String consecutivo = "";

    /** Nombre proveedor cedente. */
    private String cedente = "";

    /** Nombre proveedor cesionario. */
    private String cesionario = "";

    /** Fecha de creación de la solicitud. */
    private String fechaSolicitud = "";

    /** Fecha de implementación de la solicitud. */
    private String fechaImplementacion = "";

    /** Fecha de asignación de la solicitud. */
    private String fechaAsignacion = "";

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
     * Nombre proveedor cedente.
     * @return String
     */
    public String getCedente() {
        return cedente;
    }

    /**
     * Nombre proveedor cedente.
     * @param cedente String
     */
    public void setCedente(String cedente) {
        this.cedente = cedente;
    }

    /**
     * Nombre proveedor cesionario.
     * @return String
     */
    public String getCesionario() {
        return cesionario;
    }

    /**
     * Nombre proveedor cesionario.
     * @param cesionario String
     */
    public void setCesionario(String cesionario) {
        this.cesionario = cesionario;
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
     * Fecha de implementación de la solicitud.
     * @return String
     */
    public String getFechaImplementacion() {
        return fechaImplementacion;
    }

    /**
     * Fecha de implementación de la solicitud.
     * @param fechaImplementacion String
     */
    public void setFechaImplementacion(String fechaImplementacion) {
        this.fechaImplementacion = fechaImplementacion;
    }

    /**
     * Fecha de asignación de la solicitud.
     * @return String
     */
    public String getFechaAsignacion() {
        return fechaAsignacion;
    }

    /**
     * Fecha de asignación de la solicitud.
     * @param fechaAsignacion String
     */
    public void setFechaAsignacion(String fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
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
     * Indica si es posible eliminar una solicitud de Cesión.
     * @return True si es posible.
     */
    public boolean isCancelarDisponible() {
        return cancelarDisponible;
    }

    /**
     * Indica si es posible eliminar una solicitud de Cesión.
     * @param cancelarDisponible boolean
     */
    public void setCancelarDisponible(boolean cancelarDisponible) {
        this.cancelarDisponible = cancelarDisponible;
    }
}
