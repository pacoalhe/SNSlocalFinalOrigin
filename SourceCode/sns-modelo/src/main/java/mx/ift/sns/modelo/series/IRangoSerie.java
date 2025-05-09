package mx.ift.sns.modelo.series;

import java.math.BigDecimal;

/**
 * Interfaz de métodos comunes a RangosSerie de Numeración Geográfica y No Geográfica.
 * @author X53490DE
 */
public interface IRangoSerie {

    /**
     * Devuelve el final de rango como un valor int.
     * @return int
     * @throws Exception si no se puede parsear el número.
     */
    int getNumInicioAsInt() throws Exception;

    /**
     * Devuelve el final de rango como un valor int.
     * @return int
     * @throws Exception si no se puede parsear el número.
     */
    int getNumFinalAsInt() throws Exception;

    /**
     * Recuepera el indentificador inequívoco del rango.
     * @return String
     */
    String getIdentificadorRango();

    /**
     * Recupera el identificador de Serie.
     * @return BigDecimal
     */
    BigDecimal getSna();

}
