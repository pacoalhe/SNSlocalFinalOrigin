package mx.ift.sns.negocio.exceptions;

import javax.ejb.ApplicationException;

/**
 * Se ha modificado el registro por otro usuario.
 */
@ApplicationException(rollback = true)
public class RegistroModificadoException extends RuntimeException {
    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Cosntructor.
     */
    public RegistroModificadoException() {
    }
}
