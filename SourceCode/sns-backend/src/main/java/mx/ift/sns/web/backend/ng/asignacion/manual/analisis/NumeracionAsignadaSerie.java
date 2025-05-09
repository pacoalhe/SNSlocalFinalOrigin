package mx.ift.sns.web.backend.ng.asignacion.manual.analisis;

import mx.ift.sns.modelo.ng.NumeracionSolicitada;
import mx.ift.sns.modelo.ng.RangoSerie;

/**
 * Clase que carga la tabla de rangos solicitados en una asignacion.
 */
public class NumeracionAsignadaSerie {

    /**
     * Constructor.
     */
    public NumeracionAsignadaSerie() {

        setNumeracionAsignada(new RangoSerie());

    }

    /**
     * Rango.
     */
    private RangoSerie numeracionAsignada;

    /**
     * Cantidad a asignar.
     */
    private Integer rentado;

    /**
     * Numeracion que solicita el rango.
     */
    private NumeracionSolicitada selectResumenNumeracion;

    /**
     * Obtiene Rango.
     * @return numeracionAsignada
     */
    public RangoSerie getNumeracionAsignada() {
        return numeracionAsignada;
    }

    /**
     * Carga Rango.
     * @param numeracionAsignada numeracionAsignada to set
     */
    public void setNumeracionAsignada(RangoSerie numeracionAsignada) {
        this.numeracionAsignada = numeracionAsignada;
    }

    /**
     * Obtiene Numeracion que solicita el rango.
     * @return selectResumenNumeracion
     */
    public NumeracionSolicitada getSelectResumenNumeracion() {
        return selectResumenNumeracion;
    }

    /**
     * Carga Numeracion que solicita el rango.
     * @param selectResumenNumeracion selectResumenNumeracion to set
     */
    public void setSelectResumenNumeracion(NumeracionSolicitada selectResumenNumeracion) {
        this.selectResumenNumeracion = selectResumenNumeracion;
    }

    /**
     * Obtiene Cantidad a asignar.
     * @return rentado
     */
    public Integer getRentado() {
        return rentado;
    }

    /**
     * Carga Cantidad a asignar.
     * @param rentado rentado to set
     */
    public void setRentado(Integer rentado) {
        this.rentado = rentado;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((numeracionAsignada == null) ? 0 : numeracionAsignada.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof NumeracionAsignadaSerie)) {
            return false;
        }

        return numeracionAsignada.equals(((NumeracionAsignadaSerie) obj).numeracionAsignada);

    }
}
