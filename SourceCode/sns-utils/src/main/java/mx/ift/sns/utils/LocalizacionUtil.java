package mx.ift.sns.utils;

/**
 * Clase encargada de realizar validaciones asociadas a la localización.
 * @author X23016PE
 */
public final class LocalizacionUtil {
    /**
     * Expresion regular que valida el formato y valores de la latitud.
     * <ul>
     * <li>Mínimo: 14.000° 0' 0"</li>
     * <li>Máximo: 32.999° 60' 60"
     * </ul>
     */
    private static final String REGEX_LATITUD = "^(((1[4-9])|(2[0-9])|(3[0-2]))(\\.[0-9]{1,3})?)°\\s"
            + "(((([0-9])|([1-5][0-9]))(\\.[0-9]{1,3})?))\'\\s"
            + "((((([0-9])|([1-5][0-9]))(\\.[0-9]{1,3})?)))\"$";

    /**
     * Expresion regular que valida el formato y valores de la longitud.
     * <ul>
     * <li>Mínimo: 86.000° 0' 0"</li>
     * <li>Máximo: 117.999° 60' 60"
     * </ul>
     */
    private static final String REGEX_LONGITUD = "^(((8[6-9])|(9[0-9])|(1[0-1][0-7]))(\\.[0-9]{1,3})?)°\\s"
            + "(((([0-9])|([1-5][0-9]))(\\.[0-9]{1,3})?))\'\\s"
            + "((((([0-9])|([1-5][0-9]))(\\.[0-9]{1,3})?)))\"$";

    /** Constructor. */
    private LocalizacionUtil() {
    }

    /**
     * Función que valida el formato y valor de la latitud.
     * @param latitud String
     * @return boolean formato correcto o no
     */
    public static boolean validarLatitud(String latitud) {
        if (!reemplazarGrado(latitud).matches(REGEX_LATITUD)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Función que valida el formato y valor de la longitud.
     * @param longitud String
     * @return boolean formato correcto o no
     */
    public static boolean validarLongitud(String longitud) {
        if (!reemplazarGrado(longitud).matches(REGEX_LONGITUD)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Función que cambia el grado º por el grado °.
     * @param dato String
     * @return String
     */
    public static String reemplazarGrado(String dato) {
        if (dato.contains("º")) {
            return dato.replace("º", "°");
        }
        return dato;
    }
}
