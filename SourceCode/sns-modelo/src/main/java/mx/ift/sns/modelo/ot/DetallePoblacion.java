package mx.ift.sns.modelo.ot;

import java.io.Serializable;

/**
 * Detalle de la Población. Se añade campo que indica si tiene numeros asignados.
 * @author 66765439
 */
public class DetallePoblacion implements Serializable {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /** . */
    private Poblacion poblacion;

    /** Numeracion asignada (Si/No). */
    private String numeracionAsignada;

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

    /**
     * @return the numeracionAsignada
     */
    public String getNumeracionAsignada() {
        return numeracionAsignada;
    }

    /**
     * @param numeracionAsignada the numeracionAsignada to set
     */
    public void setNumeracionAsignada(String numeracionAsignada) {
        this.numeracionAsignada = numeracionAsignada;
    }
}
