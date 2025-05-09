package mx.ift.sns.negocio;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroBusquedaRangos;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSeries;
import mx.ift.sns.modelo.filtros.FiltroReporteadorCentrales;
import mx.ift.sns.modelo.filtros.FiltroReporteadorNG;
import mx.ift.sns.modelo.lineas.DetalleReporte;
import mx.ift.sns.modelo.nng.RangoSerieNng;
import mx.ift.sns.modelo.nng.SerieNng;
import mx.ift.sns.modelo.oficios.Oficio;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.reporteador.NGReporteador;

/**
 * Interfaz de Adminstración de Catalogos.
 */
public interface IReporteadorFacade {

    /**
     * Lista de estado.
     * @return lista de estados
     * @throws Exception exepcion
     */
    List<Estado> findAllEstados() throws Exception;

    /**
     * Lista de municipios por estado.
     * @param estado estado
     * @return lista de municipios
     * @throws Exception exepcion
     */
    List<Municipio> findMunicipiosByEstado(String estado) throws Exception;

    /**
     * Devuelve lista de poblacion de un estado y municipio concreto.
     * @param estado Código de Estado
     * @param municipio Código de Municipio
     * @return Lista de poblaciones por estado y municipio
     */
    List<Poblacion> findAllPoblaciones(String estado, String municipio);

    /**
     * Devuelve lista de poblacion de un estado y municipio concreto.
     * @param estado Código de Estado
     * @param municipio Código de Municipio
     * @param pUseCache Indica si se han de cachear los resultados para agilizar.
     * @return Lista de poblaciones por estado y municipio
     */
    List<Poblacion> findAllPoblaciones(String estado, String municipio, boolean pUseCache);

    /**
     * Recupera la lista de proveedores activos completa sin filtros.
     * @return List<Proveedor>
     * @throws Exception en caso de error.
     */
    List<Proveedor> findAllProveedoresActivos() throws Exception;

    /**
     * Obtiene el histórico de las series que muestran los informes del reporteador.
     * @param filtro filtro
     * @return List<HistoricoSerie>
     * @throws Exception Excepción en caso de error
     */
    List<NGReporteador> findHistoricoSeries(FiltroReporteadorNG filtro) throws Exception;

    /**
     * Obtiene el histórico de las series Asignadas que muestran los informes del reporteador.
     * @param filtro filtro
     * @return List<HistoricoSerie>
     */
    List<NGReporteador> findHistoricoSeriesAsignadas(FiltroReporteadorNG filtro);

    /**
     * Genera un Documento Excel con la información de las numeraciones asignadas y activas a fecha actual.
     * @param pFiltro FiltroReporteadorNG
     * @return Documento Excel Serielizado
     * @throws Exception En caso de error.
     */
    byte[] getReporteNGFechaActual(FiltroReporteadorNG pFiltro) throws Exception;

    /**
     * Totale de lineas activas.
     * @param filtro FiltroReporteadorNG
     * @return lista de totales Activas
     */
    DetalleReporte findTotalesActivosDetalleReporte(FiltroReporteadorNG filtro);

    /**
     * Genera un Documento Excel con la información de las numeraciones asignadas y activas por fechas.
     * @param pFiltro FiltroReporteadorNG
     * @return Documento Excel Serielizado
     * @throws Exception En caso de error.
     */
    byte[] getReporteNGPorFechas(FiltroReporteadorNG pFiltro) throws Exception;

    /**
     * Genera un Documento Excel con la información de la cantidad de tramites entre fechas y PST.
     * @param pFiltro FiltroReporteadorNG
     * @return Documento Excel Serielizado
     * @throws Exception En caso de error.
     */
    byte[] getReporteNGTramites(FiltroReporteadorNG pFiltro) throws Exception;

    /**
     * Busca los totales de tramites por fecha y PST.
     * @param filtro filtros
     * @return totales(fecha, tipotramite,numTramites, volumen numeros, volumen registros)
     */
    List<Object[]> findTotalTramites(FiltroReporteadorNG filtro);

    /**
     * Genera un Documento Excel con la información de las claves y series de la NNG.
     * @param pst Filtro del provvedor
     * @return Documento Excel Serielizado
     * @throws Exception En caso de error.
     */
    byte[] getReporteNNG(Proveedor pst) throws Exception;

    /**
     * Recurera los rangos correspondientes al filtro.
     * @param pFiltros pFiltros
     * @return lista de rangos
     */
    List<RangoSerieNng> findAllRangosNNG(FiltroBusquedaRangos pFiltros);

    /**
     * Recupera los oficios de una solicitud.
     * @param id idSolicitud
     * @return lista de oficios
     */
    List<Oficio> getOficiosBySolicitud(BigDecimal id);

    /**
     * Recupera las series correspondientes al filtro.
     * @param pFiltros pFiltros
     * @return lista de series
     */
    List<SerieNng> findAllSeriesNNG(FiltroBusquedaSeries pFiltros);

    /**
     * Reporte de centrales por poblacion.
     * @param filtro filtro
     * @return Documento Excel Serielizado
     * @throws Exception En caso de error.
     */
    byte[] getExportCentrales(FiltroReporteadorCentrales filtro) throws Exception;

}
