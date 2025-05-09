package mx.ift.sns.dao.nng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.filtros.FiltroBusquedaRangos;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.nng.DetalleRangoAsignadoNng;
import mx.ift.sns.modelo.nng.RangoSerieNng;
import mx.ift.sns.modelo.nng.SerieNng;
import mx.ift.sns.modelo.pst.Proveedor;

/**
 * Interfaz RangoSerie Numeración No Geográfica DAO.
 */
public interface IRangoSerieNngDao extends IBaseDAO<RangoSerieNng> {

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
     * Obtiene la ocupacion sobre una serie.
     * @param serie serie
     * @return ocupacion
     */
    Integer getTotalOcupacionSerie(SerieNng serie);

    /**
     * Obtiene un rango por una clave de servicio que tenga un rango disponible con por lo menos una cantidad dada.
     * @param clave servicio
     * @param cantidad asignar
     * @return serie
     */
    RangoSerieNng getRandomRangoByClaveServicio(ClaveServicio clave, int cantidad);

    /**
     * Obtiene el total de numeraciones asignadas por una clave de servicio.
     * @param clave servicio
     * @return total
     */
    Integer getTotalNumeracionAsignadaByClaveServicio(ClaveServicio clave);

    /**
     * Valida una numeracion. Si existen rangos para esta los retorna.
     * @param clave servicio
     * @param sna serie
     * @param numeroInicial inicio
     * @param numeroFinal final
     * @return lista rango
     */
    List<RangoSerieNng> validateRango(BigDecimal clave, BigDecimal sna, String numeroInicial, String numeroFinal);

    /**
     * Obtiene el rango serie de una numeración en concreto que tenga estatus "Asignado".
     * @param pClaveServicio BigDecimal
     * @param pSna BigDecimal
     * @param pNumeracion String
     * @return RangoSerieNng
     */
    RangoSerieNng getRangoSeriePertenece(BigDecimal pClaveServicio, BigDecimal pSna, String pNumeracion);

    /**
     * Obtiene el total de la numeracion asignada por serie por clave de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @return total
     */
    Integer getNumeracionAsignadaSerie(String claveServicio, Proveedor proveedor);

    /**
     * Obtiene el total de la numeracion asignada por rango por clave de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @return total
     */
    Integer getNumeracionAsignadaRango(String claveServicio, Proveedor proveedor);

    /**
     * Obtiene el total de la numeracion asignada por especifica por clave de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @return total
     */
    Integer getNumeracionAsignadaEspecifica(String claveServicio, Proveedor proveedor);

    /**
     * Función que comprueba si existe una numeración asignada al Pst.
     * @param proveedor Proveedor
     * @return boolean existe
     */
    boolean existeNumeracionAsignadaAlPst(Proveedor proveedor);

    /**
     * Obtiene el total de numeraciones asignadas por una solicitud.
     * @param id solicitud
     * @return total
     */
    Integer getTotalNumeracionAsignadaBySolicitud(BigDecimal id);

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
     * Obtiene el totall de rangos asignados por clave de servicio.
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

}
