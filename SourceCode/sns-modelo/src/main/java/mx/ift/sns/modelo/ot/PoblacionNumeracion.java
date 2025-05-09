package mx.ift.sns.modelo.ot;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Poblacion con la numeracion asignada de un proveedor en ella.
 */
public class PoblacionNumeracion implements Serializable {

    /**
     * Serial ID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * @param numeracionAsignada numeracion asignada
     * @param poblacion poblacion
     */
    public PoblacionNumeracion(BigDecimal numeracionAsignada, Poblacion poblacion) {
        this.numeracionAsignada = numeracionAsignada;
        this.poblacion = poblacion;
    }

    /**
     * Numeracion asignada de un proveedor en la poblacion.
     */
    private BigDecimal numeracionAsignada;

    /**
     * Poblacion en la que el proveedor tiene numeracion asignada.
     */
    private Poblacion poblacion;

    /**
     * @return the numeracionAsignada
     */
    public BigDecimal getNumeracionAsignada() {
        return numeracionAsignada;
    }

    /**
     * @param numeracionAsignada the numeracionAsignada to set
     */
    public void setNumeracionAsignada(BigDecimal numeracionAsignada) {
        this.numeracionAsignada = numeracionAsignada;
    }

    /**
     * @return the poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * @param poblacion the poblacion to set
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }
}
