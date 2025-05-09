package mx.ift.sns.web.backend;

import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.common.error.WebApplicationException;

/**
 * Excepcion generica lanzada desde la parte web.
 */
public class ApplicationException extends WebApplicationException {

    /** Serial ID. */
    private static final long serialVersionUID = 1L;

    /** Error asociado. */
    private Errores error;

    /**
     * Constructor vacio.
     */
    public ApplicationException() {
    }

    /**
     * Constructor.
     * @param error error generado
     */
    public ApplicationException(Errores error) {
        this.error = error;
    }

    /**
     * Error asociado.
     * @return Errores
     */
    public Errores getError() {
        return error;
    }

    /**
     * Error asociado.
     * @param error Errores
     */
    public void setError(Errores error) {
        this.error = error;
    }
}
