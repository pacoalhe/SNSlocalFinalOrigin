package mx.ift.sns.modelo.ot;

import java.io.Serializable;
import java.math.BigDecimal;

import mx.ift.sns.modelo.series.Nir;

/**
 * Muestra la numeracion asignada de un proveedor en un nir.
 */
public class NirNumeracion implements Serializable {

    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * @param numeracionAsignada numeracion asignada.
     * @param nir nir.
     */
    public NirNumeracion(BigDecimal numeracionAsignada, Nir nir) {
        this.numeracionAsignada = numeracionAsignada;
        this.nir = nir;
    }

    /**
     * Numeracion asignada de un proveedor en el nir.
     */
    private BigDecimal numeracionAsignada;

    /**
     * Nir en la que el proveedor tiene numeracion asignada.
     */
    private Nir nir;

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
     * @return the nir
     */
    public Nir getNir() {
        return nir;
    }

    /**
     * @param nir the nir to set
     */
    public void setNir(Nir nir) {
        this.nir = nir;
    }
}
