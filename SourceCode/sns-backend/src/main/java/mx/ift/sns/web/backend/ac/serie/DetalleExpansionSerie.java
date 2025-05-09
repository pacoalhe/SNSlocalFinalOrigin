package mx.ift.sns.web.backend.ac.serie;

import java.io.Serializable;

/**
 * Representa la información de consulta de una solicitud de cesión adaptada al front.
 */
public class DetalleExpansionSerie implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** código del nir. */
    private String nir = "";

    /** Sna Inicial. */
    private String snaInicial = "";

    /** Sna Final. */
    private String snaFinal = "";

    // GETTERS & SETTERS

    /**
     * código del nir.
     * @return String
     */
    public String getNir() {
        return nir;
    }

    /**
     * código del nir.
     * @param nir String
     */
    public void setNir(String nir) {
        this.nir = nir;
    }

    /**
     * Sna Inicial.
     * @return String
     */
    public String getSnaInicial() {
        return snaInicial;
    }

    /**
     * Sna Inicial.
     * @param snaInicial String
     */
    public void setSnaInicial(String snaInicial) {
        this.snaInicial = snaInicial;
    }

    /**
     * Sna Final.
     * @return String
     */
    public String getSnaFinal() {
        return snaFinal;
    }

    /**
     * Sna Final.
     * @param snaFinal String
     */
    public void setSnaFinal(String snaFinal) {
        this.snaFinal = snaFinal;
    }
}
