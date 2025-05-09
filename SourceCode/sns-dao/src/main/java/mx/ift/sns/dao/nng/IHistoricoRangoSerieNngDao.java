package mx.ift.sns.dao.nng;

import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroBusquedaHistoricoRangos;
import mx.ift.sns.modelo.nng.HistoricoRangoSerieNng;

/**
 * Interfaz de definición de los métodos para base de datos de Histórico de Rangos Serie NNG.
 */
public interface IHistoricoRangoSerieNngDao {

    /**
     * Recupera la lista de objetos HistoricoRangoSerieNng con la información de los movimientos sobre los Rangos Serie
     * NNG.
     * @param pFiltros Información de los filtros.
     * @return List
     */
    List<HistoricoRangoSerieNng> findAllHistoricActions(FiltroBusquedaHistoricoRangos pFiltros);

    /**
     * Recupera el número de movimientos históricos en función de los filtros indicados.
     * @param pFiltros Información de los filtros.
     * @return número de movimientos históricos
     */
    int findAllHistoricActionsCount(FiltroBusquedaHistoricoRangos pFiltros);

}
