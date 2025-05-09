package mx.ift.sns.dao.nng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroBusquedaSeries;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.nng.SerieNng;

/**
 * Interfaz de definición de los métodos para base de datos de Series.
 */
public interface ISerieNngDao {

    /**
     * Recupera las series en función de los parámetros dados.
     * @param pFiltros Filtros de búsqueda
     * @return List
     */
    List<SerieNng> findAllSeries(FiltroBusquedaSeries pFiltros);

    /**
     * Recupera el número de resultados de búsqueda de series en función de los filtros.
     * @param pFiltros Filtros de búsqueda
     * @return int número de series
     */
    Integer findAllSeriesCount(FiltroBusquedaSeries pFiltros);

    /**
     * Obtiene de forma aleatoria una cantidad de series por su clave de servicio.
     * @param clave ClaveServicio
     * @param n numero de series a obtener
     * @return lista series
     */
    List<SerieNng> findRandomSeriesLibreByClaveServicio(ClaveServicio clave, int n);

    /**
     * Obtiene una serie por una clave de servicio que tenga disponible por lo menos una cantidad dada.
     * @param clave servicio
     * @param cantidad asignar
     * @return serie
     */
    SerieNng getRandomSerieOcupadaByClaveServicio(ClaveServicio clave, BigDecimal cantidad);

    /**
     * Recupera una serie por Clave de Servicio y SNA.
     * @param pIdClaveServicio BigDecimal
     * @param pSna BigDecimal
     * @return SerieNng
     */
    SerieNng getSerie(BigDecimal pIdClaveServicio, BigDecimal pSna);

}
