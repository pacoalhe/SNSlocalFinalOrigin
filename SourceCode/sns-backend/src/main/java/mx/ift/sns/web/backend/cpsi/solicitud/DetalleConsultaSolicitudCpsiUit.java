package mx.ift.sns.web.backend.cpsi.solicitud;

import java.io.Serializable;

/** Representa la información de consulta de una solicitud de cpsi a la UIT adaptada al front. */
public class DetalleConsultaSolicitudCpsiUit implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador de la solicitud. */
    private String consecutivo = "";

    /** Fecha de creación de la solicitud. */
    private String fechaSolicitud = "";

    /** Número de oficio del Proveedor Solicitante. */
    private String numOficio = "";

    /** Estado de la solicitud. */
    private String estatus = "";

    /** Cantidad de códigos CPSI que se solicitan a la UIT. */
    private Integer cantidadSolicitada;

    /** Cantidad de códigos CPSI que se entregan. */
    private Integer cantidadEntregada;

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
     * Indicador del Número de Oficio de la solicitud.
     * @return the numOficio
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
     * Identificador del estado de la solicitud.
     * @return String
     */
    public String getEstatus() {
        return estatus;
    }

    /**
     * Identificador del estado de la solicitud.
     * @param estatus String
     */
    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    /**
     * Identificador de la cantidad de códigos solicitados.
     * @return Integer
     */
    public Integer getCantidadSolicitada() {
        return cantidadSolicitada;
    }

    /**
     * Identificador de la cantidad de códigos solicitados.
     * @param cantidadSolicitada Integer
     */
    public void setCantidadSolicitada(Integer cantidadSolicitada) {
        this.cantidadSolicitada = cantidadSolicitada;
    }

    /**
     * Identificador de la cantidad de códigos entregados.
     * @return Integer
     */
    public Integer getCantidadEntregada() {
        return cantidadEntregada;
    }

    /**
     * Identificador de la cantidad de códigos entregados.
     * @param cantidadEntregada Integer
     */
    public void setCantidadEntregada(Integer cantidadEntregada) {
        this.cantidadEntregada = cantidadEntregada;
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
