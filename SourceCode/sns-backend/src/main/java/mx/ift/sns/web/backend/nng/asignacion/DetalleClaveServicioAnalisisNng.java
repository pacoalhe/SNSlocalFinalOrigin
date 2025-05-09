package mx.ift.sns.web.backend.nng.asignacion;

import java.io.Serializable;

import mx.ift.sns.modelo.nng.ClaveServicio;

/**
 * Detalle del porcentaje de uso de una clave de servicio.
 * @author X36155QU
 */
public class DetalleClaveServicioAnalisisNng implements Serializable {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /** Clave de servicio. */
    private ClaveServicio claveServicio;

    /** Total numeracion asignada. */
    private Integer totalNumeracionAsignada;

    /** Total numeracion disponible. */
    private Integer totalNumeracionDisponible;

    /** Total series con asigacion. */
    private Integer totalSeriesOcupadas;

    /** Total series disponibles. */
    private Integer totalSeriesDisponibles;

    /** Porcentaje utilización numeracion. */
    private String porcentajeUsoNumeracion;

    /** Porcentaje en serie. */
    private String porcentajeUsoSerie;

    /**
     * Clave de servicio.
     * @return claveServicio
     */
    public ClaveServicio getClaveServicio() {
        return claveServicio;
    }

    /**
     * Clave de servicio.
     * @param claveServicio claveServicio to set
     */
    public void setClaveServicio(ClaveServicio claveServicio) {
        this.claveServicio = claveServicio;
    }

    /**
     * Total numeracion asignada.
     * @return totalNumeracionAsignada
     */
    public Integer getTotalNumeracionAsignada() {
        return totalNumeracionAsignada;
    }

    /**
     * Total numeracion asignada.
     * @param totalNumeracionAsignada totalNumeracionAsignada to set
     */
    public void setTotalNumeracionAsignada(Integer totalNumeracionAsignada) {
        this.totalNumeracionAsignada = totalNumeracionAsignada;
    }

    /**
     * Total numeracion disponible.
     * @return totalNumeracionDisponible
     */
    public Integer getTotalNumeracionDisponible() {
        return totalNumeracionDisponible;
    }

    /**
     * Total numeracion disponible.
     * @param totalNumeracionDisponible totalNumeracionDisponible to set
     */
    public void setTotalNumeracionDisponible(Integer totalNumeracionDisponible) {
        this.totalNumeracionDisponible = totalNumeracionDisponible;
    }

    /**
     * Total series con asigacion.
     * @return totalSeriesOcupadas
     */
    public Integer getTotalSeriesOcupadas() {
        return totalSeriesOcupadas;
    }

    /**
     * Total series con asigacion.
     * @param totalSeriesOcupadas totalSeriesOcupadas to set
     */
    public void setTotalSeriesOcupadas(Integer totalSeriesOcupadas) {
        this.totalSeriesOcupadas = totalSeriesOcupadas;
    }

    /**
     * Total series disponibles.
     * @return totalSeriesDisponibles
     */
    public Integer getTotalSeriesDisponibles() {
        return totalSeriesDisponibles;
    }

    /**
     * Total series disponibles.
     * @param totalSeriesDisponibles totalSeriesDisponibles to set
     */
    public void setTotalSeriesDisponibles(Integer totalSeriesDisponibles) {
        this.totalSeriesDisponibles = totalSeriesDisponibles;
    }

    /**
     * Porcentaje utilización numeracion.
     * @return porcentajeUsoNumeracion
     */
    public String getPorcentajeUsoNumeracion() {
        return porcentajeUsoNumeracion;
    }

    /**
     * Porcentaje utilización numeracion.
     * @param porcentajeUsoNumeracion porcentajeUsoNumeracion to set
     */
    public void setPorcentajeUsoNumeracion(String porcentajeUsoNumeracion) {
        this.porcentajeUsoNumeracion = porcentajeUsoNumeracion;
    }

    /**
     * Porcentaje en serie.
     * @return porcentajeUsoSerie
     */
    public String getPorcentajeUsoSerie() {
        return porcentajeUsoSerie;
    }

    /**
     * Porcentaje en serie.
     * @param porcentajeUsoSerie porcentajeUsoSerie to set
     */
    public void setPorcentajeUsoSerie(String porcentajeUsoSerie) {
        this.porcentajeUsoSerie = porcentajeUsoSerie;
    }

}
