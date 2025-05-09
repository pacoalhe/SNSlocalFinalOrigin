package mx.ift.sns.web.backend.ng.consolidacion;

import java.io.Serializable;

/** Representa la información de consulta de una solicitud de consolidación adaptada al front. */
public class DetalleConsultaConsolidacion implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador de la solicitud. */
    private String consecutivo = "";

    /** Abn que entrega. */
    private String abnEntrega;

    /** Abn que recibe. */
    private String abnRecibe;

    /** Fecha de consolidación de la solicitud. */
    private String fechaConsolidacion = "";

    /** Estado de la solicitud. */
    private String estatus = "";

    /** Indica si es posible eliminar una solicitud de consolidación. */
    private boolean cancelarDisponible = false;

    // GETTERS & SETTERS

    /**
     * Identificador de la solicitud.
     * @return the consecutivo
     */
    public String getConsecutivo() {
        return consecutivo;
    }

    /**
     * Identificador de la solicitud.
     * @param consecutivo the consecutivo to set
     */
    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    /**
     * Identificador del Abn que entrega.
     * @return the abnEntrega
     */
    public String getAbnEntrega() {
        return abnEntrega;
    }

    /**
     * Identificador del Abn que entrega.
     * @param abnEntrega the abnEntrega to set
     */
    public void setAbnEntrega(String abnEntrega) {
        this.abnEntrega = abnEntrega;
    }

    /**
     * Identificador del Abn que recibe.
     * @return the abnRecibe
     */
    public String getAbnRecibe() {
        return abnRecibe;
    }

    /**
     * Identificador del Abn que recibe.
     * @param abnRecibe the abnRecibe to set
     */
    public void setAbnRecibe(String abnRecibe) {
        this.abnRecibe = abnRecibe;
    }

    /**
     * Identificador de la fecha de consolidación.
     * @return the fechaConsolidacion
     */
    public String getFechaConsolidacion() {
        return fechaConsolidacion;
    }

    /**
     * Identificador de la fecha de consolidación.
     * @param fechaConsolidacion the fechaConsolidacion to set
     */
    public void setFechaConsolidacion(String fechaConsolidacion) {
        this.fechaConsolidacion = fechaConsolidacion;
    }

    /**
     * Identificador del estado de la solicitud.
     * @return the estatus
     */
    public String getEstatus() {
        return estatus;
    }

    /**
     * Identificador del estado de la solicitud.
     * @param estatus the estatus to set
     */
    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    /**
     * Indica si es posible eliminar una solicitud de Liberación.
     * @return True si es posible.
     */
    public boolean isCancelarDisponible() {
        return cancelarDisponible;
    }

    /**
     * Indica si es posible eliminar una solicitud de Liberación.
     * @param cancelarDisponible boolean
     */
    public void setCancelarDisponible(boolean cancelarDisponible) {
        this.cancelarDisponible = cancelarDisponible;
    }
}
