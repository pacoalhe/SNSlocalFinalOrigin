package mx.ift.sns.negocio.nng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroBusquedaHistoricoRangos;
import mx.ift.sns.modelo.filtros.FiltroBusquedaHistoricoSeries;
import mx.ift.sns.modelo.filtros.FiltroBusquedaRangos;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSeries;
import mx.ift.sns.modelo.nng.CesionSolicitadaNng;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.nng.DetalleRangoAsignadoNng;
import mx.ift.sns.modelo.nng.HistoricoRangoSerieNng;
import mx.ift.sns.modelo.nng.LiberacionSolicitadaNng;
import mx.ift.sns.modelo.nng.RangoSerieNng;
import mx.ift.sns.modelo.nng.RedistribucionSolicitadaNng;
import mx.ift.sns.modelo.nng.SerieNng;
import mx.ift.sns.modelo.nng.SerieNngPK;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.series.HistoricoSerieNng;

/**
 * Servicio de Series de Numeración No Geográfica.
 */
public interface ISeriesNngService {

    /**
     * Recupera los rangos solicitados en función de los parámetros facilitados.
     * @param pFiltros Filtros de Búsqueda
     * @return List
     */
    List<RangoSerieNng> findAllRangos(FiltroBusquedaRangos pFiltros);

    /**
     * Recupera el número rangos solicitados en función de los parámetros facilitados.
     * @param pFiltros Filtros de Búsqueda
     * @return int Número de registros
     */
    int findAllRangosCount(FiltroBusquedaRangos pFiltros);

    /**
     * Recupera un Rango en función de los parámetros dados.
     * @param pClaveServicio Identificador de la Clave de Servicio.
     * @param pSna Indentificador de la Serie.
     * @param pNumInicial Inicio del Rango.
     * @param pAsignatario Asignatario del rango.
     * @return RangoSerieNng
     */
    RangoSerieNng getRangoSerie(BigDecimal pClaveServicio, BigDecimal pSna, String pNumInicial, Proveedor pAsignatario);

    /**
     * Recupera el Rango original donde esta contenida la fracción indicada por los parámetros.
     * @param pClaveServicio Identificador de la Clave de Servicio.
     * @param pSna Indentificador de la Serie.
     * @param pNumInicial Inicio de Fracción
     * @param pNumFinal Fin de Fracción
     * @param pAsignatario Asignatario del rango.
     * @return RangoSerieNng
     */
    RangoSerieNng getRangoSerieByFraccion(BigDecimal pClaveServicio, BigDecimal pSna, String pNumInicial,
            String pNumFinal, Proveedor pAsignatario);

    /**
     * Devuelve un objeto EstadoRango por su codigo.
     * @param codigo codigo
     * @return EstadoRango
     */
    EstadoRango getEstadoRangoByCodigo(String codigo);

    /**
     * Obtiene la cantidad de series de numeración no geografiac segun filtros de busqueda.
     * @param filtros FiltroBusquedaSeries
     * @return total
     */
    Integer findAllSeriesCount(FiltroBusquedaSeries filtros);

    /**
     * Obtiene las series de numeración no geografica segun filtros de busqueda.
     * @param filtros FiltroBusquedaSeries
     * @return total
     */
    List<SerieNng> findAllSeries(FiltroBusquedaSeries filtros);

    /**
     * Persiste o Mergea un objeto RangoSerieNng.
     * @param pRangoSerieNng objeto RangoSerieNng a persistir.
     * @return RangoSerieNng
     */
    RangoSerieNng saveRangoSerie(RangoSerieNng pRangoSerieNng);

    /**
     * Elimina un objeto RangoSerieNng.
     * @param pRangoSerieNng objeto RangoSerieNng a eliminar.
     */
    void removeRangoSerie(RangoSerieNng pRangoSerieNng);

    /**
     * Obtiene la ocupacion sobre una serie.
     * @param serie serie
     * @return ocupacion
     */
    Integer getTotalOcupacionSerie(SerieNng serie);

    /**
     * Obtiene de forma aleatoria una cantidad de series por su clave de servicio.
     * @param clave claveServicio
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
     * Obtiene un rango por una clave de servicio que tenga un rango disponible con por lo menos una cantidad dada.
     * @param clave servicio
     * @param cantidad asignar
     * @return serie
     */
    RangoSerieNng getRandomRangoByClaveServicio(ClaveServicio clave, int cantidad);

    /**
     * Cede un rango de un Cedente a un Cesionario.
     * @param pCesionSolicitada Información de la cesión
     * @param pInmediata Indica si la fecha de implementación es inmediata (true) o programada (false)
     * @return boolean 'True' si se ha podido ceder el rango.
     * @throws Exception en caso de error.
     */
    boolean cederRangoCompleto(CesionSolicitadaNng pCesionSolicitada, boolean pInmediata) throws Exception;

    /**
     * Cede las fracciones solicitadas de un rango de un Cedente a un Cesionario.
     * @param pCesionesSolicitadas Lista de cesiones con la información de las fracciones.
     * @param pInmediata Indica si la fecha de implementación es inmediata (true) o programada (false)
     * @return boolean 'True' si se han podido ceder las fracciones.
     * @throws Exception en caso de error.
     */
    boolean cederFraccionesRango(List<CesionSolicitadaNng> pCesionesSolicitadas, boolean pInmediata) throws Exception;

    /**
     * Recupera una serie por Clave de Servicio y SNA.
     * @param pIdClaveServicio BigDecimal
     * @param pSna BigDecimal
     * @return SerieNng
     */
    SerieNng getSerie(BigDecimal pIdClaveServicio, BigDecimal pSna);

    /**
     * Obtiene el total de numeraciones asignadas por una clave de servicio.
     * @param clave servicio
     * @return total
     */
    Integer getTotalNumeracionAsignadaByClaveServicio(ClaveServicio clave);

    /**
     * Libera un rango sin fraccionar.
     * @param pLibSol Información de la liberación solicitada
     * @param pInmediata Indica si la fecha de implementación es inmediata (true) o programada (false)
     * @return Código de EstadoLiberacionSolicitada indicando el nuevo estado de la liberación solicitada.
     * @throws Exception en caso de error.
     */
    String liberarRangoCompleto(LiberacionSolicitadaNng pLibSol, boolean pInmediata) throws Exception;

    /**
     * Fracciona un rango aplicando las reservas y liberaciones solicitadas.
     * @param pLiberaciones Solicitudes de Liberacion de un mismo RangoSerie
     * @param pInmediata Indica si la fecha de implementación es inmediata (true) o programada (false)
     * @return Código de EstadoLiberacionSolicitada indicando el nuevo estado de la liberación solicitada.
     * @throws Exception en caso de error.
     */
    String liberarFraccionesRango(List<LiberacionSolicitadaNng> pLiberaciones, boolean pInmediata) throws Exception;

    /**
     * Valida una numeracion. Si existen rango para esta los retorna.
     * @param clave BigDecimal
     * @param sna BigDecimal
     * @param numeroInicial inicio
     * @param numeroFinal final
     * @return lista rango
     */
    List<RangoSerieNng> validateRango(BigDecimal clave, BigDecimal sna,
            String numeroInicial, String numeroFinal);

    /**
     * Recupera una serie por id.
     * @param id SerieNngPK
     * @return SerieNng
     */
    SerieNng getSerieById(SerieNngPK id);

    /**
     * Obtiene el rango serie de una numeración en concreto que tenga estatus "Asignado".
     * @param pClaveServicio BigDecimal
     * @param pSna BigDecimal
     * @param pNumeracion String
     * @return RangoSerieNng
     */
    RangoSerieNng getRangoSeriePertenece(BigDecimal pClaveServicio, BigDecimal pSna, String pNumeracion);

    /**
     * Actualiza un RangoSerieNng con la información de la solicitud de redistribución.
     * @param pRango Rango a modificar.
     * @param pRedistSol Información de la redistribución del rango.
     */
    void redistribuirRango(RangoSerieNng pRango, RedistribucionSolicitadaNng pRedistSol);

    /**
     * Redistribuye las fracciones de un RangoSerieNng con la información de la solicitud de redistribución.
     * @param pRedistribuciones Lista de fracciones sobre un RangoSerieNng
     * @throws Exception en caso de error.
     */
    void redistribuirFraccionesRango(List<RedistribucionSolicitadaNng> pRedistribuciones) throws Exception;

    /**
     * Dada una fracción de un Rango, devuelve la información del rango original del que se obtuvo la fraccción.
     * @param pFraccion Información de la fracción del rango.
     * @param pIdSolicitud Identificador del trámite que realizó el fraccionamiento.
     * @param pAsignatarioInicial Asignatario del rango original
     * @return RangoSerie con la información de la numeración original.
     * @throws Exception en caso de error.
     */
    RangoSerieNng getRangoSerieOriginalByFraccion(RangoSerieNng pFraccion, BigDecimal pIdSolicitud,
            Proveedor pAsignatarioInicial) throws Exception;

    /**
     * Recupera la lista de objetos HistoricoRangoSerieNng con la información de los movimientos sobre los Rangos Serie
     * NNG.
     * @param pFiltros Información de los filtros.
     * @return List
     */
    List<HistoricoRangoSerieNng> findAllHistoricActionsFromRangos(FiltroBusquedaHistoricoRangos pFiltros);

    /**
     * Recupera el número de movimientos históricos en función de los filtros indicados.
     * @param pFiltros Información de los filtros.
     * @return número de movimientos históricos
     */
    int findAllHistoricActionsFromRangosCount(FiltroBusquedaHistoricoRangos pFiltros);

    /**
     * Obtiene el total de la numeracion asignada por serie por clave de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @return total
     */
    Integer getNumeracionAsignadaSerie(ClaveServicio claveServicio, Proveedor proveedor);

    /**
     * Obtiene el total de la numeracion asignada por rango por clave de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @return total
     */
    Integer getNumeracionAsignadaRango(ClaveServicio claveServicio, Proveedor proveedor);

    /**
     * Obtiene el total de la numeracion asignada por especifica por clave de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @return total
     */
    Integer getNumeracionAsignadaEspecifica(ClaveServicio claveServicio, Proveedor proveedor);

    /**
     * Obtiene el historico de las series NNG.
     * @param pFiltros busqueda
     * @return historico
     */
    List<HistoricoSerieNng> findAllHistoricoSeries(FiltroBusquedaHistoricoSeries pFiltros);

    /**
     * Obtiene total de historicos de las series NNG.
     * @param pFiltros busqueda
     * @return total
     */
    int findAllHistoricoSeriesCount(FiltroBusquedaHistoricoSeries pFiltros);

    /**
     * Obtiene el total de numeraciones asignadas por una solicitud.
     * @param id solicitud
     * @return total
     */
    Integer getTotalNumeracionAsignadaBySolicitud(BigDecimal id);

    /**
     * Función que comprueba si existe una numeración asignada al Pst.
     * @param proveedor Proveedor
     * @return boolean existe
     */
    boolean existeNumeracionAsignadaAlPst(Proveedor proveedor);

    /**
     * Obtiene el total de rangos asignados especificamente.
     * @return total
     */
    int findAllRangosAsignadosEspecificoCount();

    /**
     * Obtiene los rangos asignados especificamente.
     * @param numPag numero pagina
     * @param tamPag tamaño pagina
     * @return rangos
     */
    List<RangoSerieNng> findAllRangosAsignadosEspecifico(int numPag, int tamPag);

    /**
     * Obtiene los rangos asignados especificamente.
     * @param numPag numero pagina
     * @param tamPag tamaño pagina
     * @return rangos
     */
    List<DetalleRangoAsignadoNng> findAllRangosAsignadosEspecificoD(int numPag, int tamPag);

    /**
     * Obtiene el total de rangos asignados.
     * @return total
     */
    int findAllRangosAsignadosCount();

    /**
     * Obtiene los rangos asignados.
     * @param numPag numero pagina
     * @param tamPag tamaño pagina
     * @return rangos
     */
    List<RangoSerieNng> findAllRangosAsignados(int numPag, int tamPag);

    /**
     * Obtiene los rangos asignados.
     * @param numPag numero pagina
     * @param tamPag tamaño pagina
     * @return rangos
     */
    List<DetalleRangoAsignadoNng> findAllRangosAsignadosD(int numPag, int tamPag);

    /**
     * Obtiene los rangos asignados.
     * @param clave de servicio
     * @param numPag numero pagina
     * @param tamPag tamaño pagina
     * @return rangos
     */
    List<DetalleRangoAsignadoNng> findAllRangosAsignadosD(BigDecimal clave, int numPag, int tamPag);

    /**
     * Obtiene toda las claves de servicio que tienen rangos asignados.
     * @return claves
     */
    List<ClaveServicio> findAllClaveServicioAsignadas();

    /**
     * Obtiene el total de rangos asignados por clave de servicio.
     * @param clave servicio
     * @return total
     */
    int findAllRangosAsignadosByClaveCount(ClaveServicio clave);

    /**
     * Obtiene los rangos asignados por clave de servicio.
     * @param clave servicio
     * @param numPag numero pagina
     * @param tamPag tamaño pagina
     * @return rangos
     */
    List<RangoSerieNng> findAllRangosByClaveAsignados(ClaveServicio clave, int numPag, int tamPag);

    /**
     * Genera un fichero excel con los filtros de una cosulta de historico de series.
     * @param filtro busqueda
     * @return fichero
     * @throws Exception error
     */
    byte[] getExportHistoricoSeries(FiltroBusquedaHistoricoSeries filtro) throws Exception;

}
