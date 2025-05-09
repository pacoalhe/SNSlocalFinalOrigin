package mx.ift.sns.modelo.cpsn;

import org.apache.commons.lang3.StringUtils;

/** Clase de utilidades de para los códigos nacionales. */
public final class CPSNUtils {

    /** Constante para el alias de la tabla de proveedores. */
    public static final String FILTRO_ALIAS_TABLA = "esn";
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

    /** Constructor. */
    private CPSNUtils() {
    }

    /**
     * Método encargado de obtener el valor mínimo del cps del bloque.
     * @param bloque valor binario del bloque
     * @return valor minimo del bloque
     */
    public static int valorMinBloque(String bloque) {
        return Integer.valueOf(StringUtils.rightPad(bloque, TipoBloqueCPSN.TAM_INDIVIDUAL, '0'), 2);
    }

    /**
     * Método encargado de obtener el valor máximo del cps del bloque.
     * @param bloque valor binario del bloque
     * @return valor maximo del bloque
     */
    public static int valorMaxBloque(String bloque) {
        return Integer.valueOf(StringUtils.rightPad(bloque, TipoBloqueCPSN.TAM_INDIVIDUAL, '1'), 2);
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
     * Método encargado de obtener el valor cps mínimos del bloque solicitado.
     * @param cps código
     * @param tipoBloque tipo de bloque
     * @return int cps mínimo
     */
    public static int valorMinimoBloque(Integer cps, String tipoBloque) {
        String bloque = null;

        String codBinarioIndividual = StringUtils.leftPad(
                Integer.toBinaryString(cps), TipoBloqueCPSN.TAM_INDIVIDUAL, '0');

        if (TipoBloqueCPSN.INDIVIDUAL.equals(tipoBloque)) {
            bloque = codBinarioIndividual;
        } else if (TipoBloqueCPSN.BLOQUE_8.equals(tipoBloque)) {
            // Obtenemos el binario asociado al bloque de red de bloque 8
            bloque = codBinarioIndividual.substring(0, TipoBloqueCPSN.TAM_BLOQUE_8);
        } else if (TipoBloqueCPSN.BLOQUE_128.equals(tipoBloque)) {
            // Obtenemos el binario asociado al bloque de red de bloque 128
            bloque = codBinarioIndividual.substring(0, TipoBloqueCPSN.TAM_BLOQUE_128);
        } else if (TipoBloqueCPSN.BLOQUE_2048.equals(tipoBloque)) {
            // Obtenemos el binario asociado al bloque de red de bloque 2048
            bloque = codBinarioIndividual.substring(0, TipoBloqueCPSN.TAM_BLOQUE_2048);
        }

        return valorMinBloque(bloque);
    }
}
