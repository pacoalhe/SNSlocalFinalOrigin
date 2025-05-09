package mx.ift.sns.port.full;

/**
 * Logger a consola.
 */
public final class ConsoleLogger {

    /** debug activado o no. */
    private static final boolean IS_DEBUG_ON = false;

    /**
     * Constructor privado.
     */
    private ConsoleLogger() {
    }

    /**
     * @param c clase
     */
    private ConsoleLogger(Class<?> c) {
    }

    /**
     * Devuelve la instancia.
     * @param c clase
     * @return instancia
     */
    public static ConsoleLogger getLogger(Class<?> c) {
        return new ConsoleLogger();
    }

    /**
     * Escribe un mensaje nivel debug.
     * @param s mensaje
     */
    public void debug(String s) {
        if (IS_DEBUG_ON) {
            System.out.println(s);
        }
    }

    /**
     * Escribe un mensaje nivel info.
     * @param s mensaje
     */

    public void info(String s) {
        System.out.println(s);
    }

    /**
     * Escribe un mensaje nivel error.
     * @param s mensaje
     * @param e excepcion
     */
    public void error(String s, Throwable e) {
        System.out.println(s);
        System.out.println(e);
    }

    /**
     * Indica si esta activo el nivel debug.
     * @return true si lo esta
     */
    public boolean isDebugEnabled() {
        return ConsoleLogger.IS_DEBUG_ON;
    }
}
