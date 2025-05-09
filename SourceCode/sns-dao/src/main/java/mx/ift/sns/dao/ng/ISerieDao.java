package mx.ift.sns.dao.ng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSeries;
import mx.ift.sns.modelo.ng.Serie;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.series.VCatalogoSerie;

/**
 * Interfaz de definición de los métodos para base de datos de Series.
 */
public interface ISerieDao extends IBaseDAO<Serie> {

    /**
     * Recupera las series libres en un ABN.
     * @param abn Abn
     * @return List
     */
    List<Serie> findSeriesLibres(Abn abn);

    /**
     * Recupera las series en función de los parámetros dados.
     * @param pFiltros Filtros de búsqueda
     * @return List
     */
    List<Serie> findAllSeries(FiltroBusquedaSeries pFiltros);

    /**
     * Recupera el número de resultados de búsqueda de series en función de los filtros.
     * @param pFiltros Filtros de búsqueda
     * @return int número de series
     */
    int findAllSeriesCount(FiltroBusquedaSeries pFiltros);

    /**
     * Recupera una serie por SNA y NIR.
     * @param pIdSna BigDecimal
     * @param pIdNir BigDecimal
     * @return Serie
     */
    Serie getSerie(BigDecimal pIdSna, BigDecimal pIdNir);

    /**
     * Recupera las series ocupadas por un PST en un ABN.
     * @param pst pst
     * @param abn abn
     * @return List<Serie>
     */
    List<Serie> findSeriesPst(Proveedor pst, Abn abn);

    /**
     * Recupera las series ocupadas por otros PSTs en un ABN.
     * @param pst pst
     * @param abn abn
     * @return List<Serie>
     */
    List<Serie> findSeriesOtrosPsts(Proveedor pst, Abn abn);

    /**
     * Recupera la serie más alta o más baja de un Nir.
     * @param pCdgNir Nir al que pertenece la serie.
     * @param pMaxValue 'True' recupera el valor de serie más alto. 'False' el valor de serie más bajo.
     * @return int
     */
    int getMaxMinSerieFromNir(int pCdgNir, boolean pMaxValue);

    /**
     * Marca todas las series de un mismo nir como desactivadas.
     * @param pIdNir Identificador de NIR
     * @return Número de series desactivadas.
     */
    int desactivarSeriesByNir(BigDecimal pIdNir);

    /**
     * Marca las series indicadas del mismo nir como activadas.
     * @param pIdNir Identificador de NIR
     * @param pInicioSerie Inicio de Serie.
     * @param pFinalSerie Final de Serie.
     * @return Número de series activadas.
     */
    int activarSeriesByNir(BigDecimal pIdNir, int pInicioSerie, int pFinalSerie);

    /**
     * Comprueba que una serie exite con un nir dado.
     * @param sna nir
     * @param nir nir
     * @return boolean
     */
    boolean existSerieWithNir(String sna, String nir);

    /**
     * Devuelve todas las series.
     * @param pFiltros pFiltros
     * @return List<VCatalogoSerie>
     */
    List<VCatalogoSerie> findAllCatalogoSeries(FiltroBusquedaSeries pFiltros);

    /**
     * Devuelve el número de series.
     * @param pFiltros pFiltros
     * @return int
     */
    int findAllCatalogoSeriesCount(FiltroBusquedaSeries pFiltros);

    /**
     * Comprueba que una serie exite con un nir y un abn dado.
     * @param abn abn
     * @param nir nir
     * @return boolean
     */
    boolean existSerieWithNirAbn(String abn, String nir);

    /**
     * Busca las series ocupadas segun filtro.
     * @param pFiltros FiltroBusquedaSeries
     * @return series
     */
    List<Object[]> findAllSeriesOcupadas(FiltroBusquedaSeries pFiltros);

    /**
     * Método que realiza la llamada a un procedimiento de BD para actualizar las series.
     * @param serieOriginal serieOriginal
     * @param nirOriginal nirOriginal
     * @param serieNueva serieNueva
     * @param usuario usuario
     * @return String
     */
    String actualizaSerie(BigDecimal serieOriginal, BigDecimal nirOriginal, BigDecimal serieNueva, BigDecimal usuario);
}
