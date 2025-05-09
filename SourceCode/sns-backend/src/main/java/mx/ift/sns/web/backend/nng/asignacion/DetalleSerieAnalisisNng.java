package mx.ift.sns.web.backend.nng.asignacion;

import java.io.Serializable;

import mx.ift.sns.modelo.nng.SerieNng;

/**
 * Detalle de las series en el analisis de NNG.
 */
public class DetalleSerieAnalisisNng implements Serializable {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /** Serie. */
    private SerieNng serie;

    /** Ocupacion. */
    private Integer ocupacion;

    /** disponible. */
    private Integer disponible;

    /**
     * Ocupacion.
     * @return ocupacion
     */
    public Integer getOcupacion() {
        return ocupacion;
    }

    /**
     * Ocupacion.
     * @param ocupacion ocupacion to set
     */
    public void setOcupacion(Integer ocupacion) {
        this.ocupacion = ocupacion;
    }

    /**
     * disponible.
     * @return disponible
     */
    public Integer getDisponible() {
        return disponible;
    }

    /**
     * disponible.
     * @param disponible disponible to set
     */
    public void setDisponible(Integer disponible) {
        this.disponible = disponible;
    }

    /**
     * Serie.
     * @return serie
     */
    public SerieNng getSerie() {
        return serie;
    }

    /**
     * Serie.
     * @param serie serie to set
     */
    public void setSerie(SerieNng serie) {
        this.serie = serie;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((serie == null) ? 0 : serie.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DetalleSerieAnalisisNng)) {
            return false;
        }
        return this.serie.equals(((DetalleSerieAnalisisNng) obj).getSerie());
    }

}
