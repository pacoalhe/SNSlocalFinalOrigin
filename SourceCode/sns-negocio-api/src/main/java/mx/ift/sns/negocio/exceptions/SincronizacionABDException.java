package mx.ift.sns.negocio.exceptions;

/**
 * Error en la sincronización ABD.
 */
public class SincronizacionABDException extends Exception {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Mensaje de error. */
    private String mensajeSync;

    /**
     * Mensage private String mensaje; /** Constructor.
     */
    public SincronizacionABDException() {
    }

    /**
     * Constructor con el mensaje de error.
     * @param mensaje error
     */
    public SincronizacionABDException(String mensaje) {
        this.mensajeSync = mensaje;
    }

    /**
     * Devuelve el mensaje de error.
     * @return mensaje
     */
    public String getMensajeSync() {
        return this.mensajeSync;
    }

}
