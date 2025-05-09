package mx.ift.sns.negocio.ng.model;

import java.io.Serializable;

/**
 * Resultado de la validacion de un CSV.
 */
public class ResultadoValidacionCSV implements Serializable {

    /** serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** Fichero ok. */
    public static final int VALIDACION_OK = 0;

    /** Error accediendo al fichero. */
    public static final int ERROR_FICHERO = 1;

    /** Error en cabecera del fichero. */
    public static final int ERROR_CABECERA = 2;

    /** Error fichero vacio. */
    public static final int ERROR_FICHERO_VACIO = 3;

    /** Error fichero incorrecto. */
    public static final int ERROR_FORMATO_FICHERO = 4;

    /** Error en la validacion. */
    private int error;

    /**
     * @return the error
     */
    public int getError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(int error) {
        this.error = error;
    }
}
