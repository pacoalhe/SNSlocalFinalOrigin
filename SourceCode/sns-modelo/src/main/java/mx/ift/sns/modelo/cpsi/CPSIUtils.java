package mx.ift.sns.modelo.cpsi;

import mx.ift.sns.modelo.cpsn.TipoBloqueCPSN;

import org.apache.commons.lang3.StringUtils;

/** Clase de utilidades de para los Códigos de Puntos de Señalización Internacionales. */
public final class CPSIUtils {

    /** Constante para el campo proveedor. */
    public static final String FILTRO_PROVEEDOR = "proveedor";

    /** Constante para el campo codigo cpsn binario. */
    public static final String FILTRO_CODIGO_BINARIO = "cpsBinario";

    /** Constante para el campo codigo cpsn decimal. */
    public static final String FILTRO_CODIGO_DECIMAL = "cps";

    /** Constante para el campo codigo cpsn binario minimo. */
    public static final String FILTRO_CODIGO_MINIMO = "numMin";

    /** Constante para el campo codigo cpsn binario maximo. */
    public static final String FILTRO_CODIGO_MAXIMO = "numMax";

    /** Constante para el alias de la tabla de proveedores. */
    public static final String FILTRO_ALIAS_TABLA_CPSI = "esi";

    /** Constante para el campo formato decimal cpsi . */
    public static final String FILTRO_FORMATO_DECIMAL = "formatoDecimal";

    /** Bloque Individual. */
    public static final int TAM_INDIVIDUAL = 14;

    /** Constructor. */
    private CPSIUtils() {
    }

    /**
     * Método encargado de obtener el valor mínimo del cps del bloque.
     * @param bloque valor binario del bloque
     * @return valor minimo del bloque
     */
    public static int valorMinBloque(String bloque) {
        return Integer.valueOf(StringUtils.rightPad(bloque, TAM_INDIVIDUAL, '0'), 2);
    }

    /**
     * Método encargado de obtener el valor máximo del cps del bloque.
     * @param bloque valor binario del bloque
     * @return valor maximo del bloque
     */
    public static int valorMaxBloque(String bloque) {
        return Integer.valueOf(StringUtils.rightPad(bloque, TAM_INDIVIDUAL, '1'), 2);
    }

    /**
     * Método encargado de obtener el valor binario del valor entero indicado.
     * @param pValor Valor entero del cps
     * @return binario formateado a 14 dígitos
     */
    public static String valorBinario(Integer pValor) {
        return StringUtils.leftPad(
                Integer.toBinaryString(pValor), TipoBloqueCPSN.TAM_INDIVIDUAL, '0');
    }

    /**
     * Método al que se le pasa el formato decimal y lo transforma en binario.
     * @param formato formatodecimal
     * @return String
     */
    public static String valorFormatoDecimal(String formato) {
        String[] array = formato.split("-");
        String valor1 = StringUtils.leftPad(Integer.toBinaryString(Integer.valueOf(array[0])), 3, '0');
        String valor2 = StringUtils.leftPad(Integer.toBinaryString(Integer.valueOf(array[1])), 8, '0');
        String valor3 = StringUtils.leftPad(Integer.toBinaryString(Integer.valueOf(array[2])), 3, '0');
        return valor1 + valor2 + valor3;
    }

}
