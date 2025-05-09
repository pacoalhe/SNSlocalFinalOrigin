package mx.ift.sns.dao.nng;

import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroBusquedaHistoricoSeries;
import mx.ift.sns.modelo.series.HistoricoSerieNng;

/**
 * Interfaz RangoSerie DAO.
 */
public interface IHistoricoSerieNngDAO {

    /**
     * Busqueda de historico de series.
     * @param pFiltros a buscar
     * @return el bloque de datos
     */
    List<HistoricoSerieNng> findAllHistoricoSeries(FiltroBusquedaHistoricoSeries pFiltros);

    /**
     * Total de registros de la busqueda de historico de series.
     * @param pFiltros a buscar
     * @return numero de resultados
     */
    int findAllHistoricoSeriesCount(FiltroBusquedaHistoricoSeries pFiltros);

}
