package mx.ift.sns.dao.ng;

import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroBusquedaHistoricoSeries;
import mx.ift.sns.modelo.filtros.FiltroReporteadorNG;
import mx.ift.sns.modelo.reporteador.NGReporteador;
import mx.ift.sns.modelo.series.HistoricoSerie;

/**
 * Interfaz RangoSerie DAO.
 */
public interface IHistoricoSerieDAO {

    /**
     * Busqueda de historico de series.
     * @param filtro a buscar
     * @param first primer campo
     * @param pagesize tamaño
     * @return el bloque de datos
     */
    List<HistoricoSerie> findHistoricoSeries(FiltroBusquedaHistoricoSeries filtro, int first, int pagesize);

    /**
     * Total de registros de la busqueda de historico de series.
     * @param filtro a buscar
     * @return numero de resultados
     */
    int findHistoricoSeriesCount(FiltroBusquedaHistoricoSeries filtro);

    /**
     * Busqueda de historico de series para el reporteador.
     * @param filtro a buscar
     * @return el bloque de datos
     */
    List<NGReporteador> findHistoricoSeries(FiltroReporteadorNG filtro);

    /**
     * Busqueda de totales de solicitudes por fechas.
     * @param filtro a buscar
     * @return lista de resultados
     */
    List<Object[]> getTotalesTramitesPorFechas(FiltroReporteadorNG filtro);

    /**
     * Busqueda de historico de series de Asignadas para el reporteador.
     * @param filtro a buscar
     * @return el bloque de datos
     */
    List<NGReporteador> findHistoricoSeriesTotalesAsignadas(FiltroReporteadorNG filtro);

    /**
     * Busqueda de historico de series que son cedidas por los PST para el reporteador.
     * @param filtro a buscar
     * @return el bloque de datos
     */
    List<NGReporteador> findHistoricoSeriesCedidas(FiltroReporteadorNG filtro);
}
