package mx.ift.sns.utils.number;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

/**
 * Clase que implementa utilidades para numeros.
 * @author X36155QU
 */
public final class NumerosUtils {

    /** Constructor privado,vacio por defecto. */
    private NumerosUtils() {

    }

    /**
     * Calcula el porcentaje entre dos numeros.
     * @param valorMenor Integer
     * @param valorMayor Integer
     * @return porcentaje
     */
    public static Float calcularPorcentaje(Integer valorMenor, Integer valorMayor) {

        Float porcentaje = new Float(0);
        if (valorMayor > 0) {
            porcentaje = (valorMenor.floatValue() / valorMayor.floatValue()) * 100;
        }
        return porcentaje = (float) (Math.rint(porcentaje * 1000000) / 1000000);
    }

    /**
     * Calcula el porcentaje entre dos numeros.
     * @param valorMenor Integer
     * @param valorMayor Integer
     * @return porcentaje
     */
    public static Float calcularPorcentaje(Long valorMenor, Long valorMayor) {

        Float porcentaje = new Float(0);
        if (valorMayor > 0) {
            porcentaje = (valorMenor.floatValue() / valorMayor.floatValue()) * 100;
        }
        return porcentaje = (float) (Math.rint(porcentaje * 1000000) / 1000000);
    }

    /**
     * Calcula el porcentaje entre dos numeros.
     * @param valorMenor Integer
     * @param valorMayor Integer
     * @return porcentaje
     */
    public static Float calcularPorcentaje(BigDecimal valorMenor, BigDecimal valorMayor) {

        Float porcentaje = new Float(0);
        if (valorMayor.intValue() > 0) {
            porcentaje = (valorMenor.floatValue() / valorMayor.floatValue()) * 100;
        }
        return porcentaje = (float) (Math.rint(porcentaje * 1000000) / 1000000);
    }

    /**
     * Calcula el porcentaje entre dos numeros con un precision en decimales dada.
     * @param valorMenor Integer
     * @param valorMayor Integer
     * @param precision decimales
     * @return porcentaje
     */
    public static Float calcularPorcentaje(Integer valorMenor, Integer valorMayor, int precision) {

        Float porcentaje = new Float(0);
        if (valorMayor > 0) {
            porcentaje = (valorMenor.floatValue() / valorMayor.floatValue()) * 100;
        }
        return porcentaje = (float) (Math.rint(porcentaje * 100 * precision) / 100 * precision);
    }

    /**
     * Calcula el porcentaje entre dos numeros.
     * @param valorMenor Integer
     * @param valorMayor Integer
     * @return porcentaje
     */
    public static String calcularPorcentajeAsString(Integer valorMenor, Integer valorMayor) {

        return (valorMayor == 0 || valorMenor == 0)
                ? "0 %" : String.format("%.6f", valorMenor * 100.0 / valorMayor) + " %";
    }

    /**
     * Calcula el porcentaje entre dos numeros.
     * @param valorMenor Long
     * @param valorMayor LOng
     * @return porcentaje
     */
    public static String calcularPorcentajeAsString(Long valorMenor, Long valorMayor) {

        return (valorMayor == 0 || valorMenor == 0)
                ? "0 %" : String.format("%.2f", valorMenor * 100.0 / valorMayor) + " %";
    }

    /**
     * Calcula el porcentaje entre dos numeros.
     * @param valorMenor Integer
     * @param valorMayor Integer
     * @return porcentaje
     */
    public static String calcularPorcentajeAsString(BigDecimal valorMenor, BigDecimal valorMayor) {

        return (valorMayor.intValue() == 0 || valorMenor.intValue() == 0)
                ? "0 %" : String.format("%.2f", valorMenor.intValue() * 100.0 / valorMayor.intValue()) + " %";
    }

    /**
     * Calcula el porcentaje entre dos numeros con un precision en decimales dada.
     * @param valorMenor Integer
     * @param valorMayor Integer
     * @param precision decimales
     * @return porcentaje
     */
    public static String calcularPorcentajeAsString(Integer valorMenor, Integer valorMayor, int precision) {

        Float porcentaje = new Float(0);
        if (valorMayor > 0) {
            porcentaje = (valorMenor.floatValue() / valorMayor.floatValue()) * 100;
        }
        porcentaje = (float) (Math.rint(porcentaje * 100 * precision) / 100 * precision);
        return porcentaje.toString();
    }

    /**
     * Devuelve el valor binario de 14 bits del decimal recibido.
     * @param decimal a convertir
     * @return decimal en binario
     */
    public static String decimalABinario(String decimal) {
        String binario = null;
        if (decimal == null || decimal.isEmpty()) {
            return "";
        } else {
            binario = StringUtils.leftPad(Integer.toBinaryString(Integer.parseInt(decimal)), 14, '0');
        }

        return binario;
    }

}
