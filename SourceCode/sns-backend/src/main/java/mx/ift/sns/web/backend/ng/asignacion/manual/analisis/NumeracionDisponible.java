package mx.ift.sns.web.backend.ng.asignacion.manual.analisis;

import mx.ift.sns.modelo.ng.Serie;

/**
 * Clase para formar las tablas de numeraciones disponibles en analisis de asignacion.
 */
public class NumeracionDisponible {

    /**
     * Constructor.
     */
    public NumeracionDisponible() {

    }

    /**
     * Serie disponible.
     */
    private Serie serie;

    /**
     * Ocupacion en la serie.
     */
    private Integer ocupacion;

    /**
     * Disponibilidad en la serie.
     */
    private Integer disponible;

    /**
     * Obtiene Serie disponible.
     * @return serie
     */
    public Serie getSerie() {
        return serie;
    }

    /**
     * carga Serie disponible.
     * @param serie serie to set
     */
    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    /**
     * Obtiene Ocupacion en la serie.
     * @return ocupacion
     */
    public Integer getOcupacion() {
        return ocupacion;
    }

    /**
     * Carga Ocupacion en la serie.
     * @param ocupacion ocupacion to set
     */
    public void setOcupacion(Integer ocupacion) {
        this.ocupacion = ocupacion;
    }

    /**
     * Obtiene Disponibilidad en la serie.
     * @return disponible
     */
    public Integer getDisponible() {
        return disponible;
    }

    /**
     * Carga Disponibilidad en la serie.
     * @param disponible disponible to set
     */
    public void setDisponible(Integer disponible) {
        this.disponible = disponible;
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
        if (!(obj instanceof NumeracionDisponible)) {
            return false;
        }
        return this.serie.equals(((NumeracionDisponible) obj).getSerie());
    }

}
