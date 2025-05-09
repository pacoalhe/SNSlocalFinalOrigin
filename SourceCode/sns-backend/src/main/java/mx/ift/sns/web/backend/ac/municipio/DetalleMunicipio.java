package mx.ift.sns.web.backend.ac.municipio;

import java.io.Serializable;

import mx.ift.sns.modelo.ot.Municipio;

/**
 * Detalle del Municipio. Se añade campo que indica si tiene numeros asignados.
 * @author X36155QU
 */
public class DetalleMunicipio implements Serializable {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /** Municipio. */
    private Municipio municipio;

    /** Numeracion asignada (Si/No). */
    private String numeracionAsignada;

    /**
     * Obtiene Municipio.
     * @return Municipio
     */
    public Municipio getMunicipio() {
        return municipio;
    }

    /**
     * Carga Municipio.
     * @param municipio Municipio
     */
    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    /**
     * Obtiene Numeracion asignada (Si/No).
     * @return String
     */
    public String getNumeracionAsignada() {
        return numeracionAsignada;
    }

    /**
     * Carga Numeracion asignada (Si/No).
     * @param numeracionAsignada String
     */
    public void setNumeracionAsignada(String numeracionAsignada) {
        this.numeracionAsignada = numeracionAsignada;
    }
}
