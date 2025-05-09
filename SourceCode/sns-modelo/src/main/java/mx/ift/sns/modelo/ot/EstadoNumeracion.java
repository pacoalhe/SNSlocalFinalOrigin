package mx.ift.sns.modelo.ot;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Daniel
 */
public class EstadoNumeracion implements Serializable {

    /**
     * Serial ID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * @param numeracionAsignada numeracion asignada al estado.
     * @param estado estado.
     */
    public EstadoNumeracion(BigDecimal numeracionAsignada, Estado estado) {
        this.numeracionAsignada = numeracionAsignada;
        this.estado = estado;
    }

    /**
     * Numeracion asignada de un proveedor en el estado.
     */
    private BigDecimal numeracionAsignada;

    /**
     * Estado en la que el proveedor tiene numeracion asignada.
     */
    private Estado estado;

    /**
     * @return the estado
     */
    public Estado getEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(Estado estado) {
        this.estado = estado;
    }

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

}
