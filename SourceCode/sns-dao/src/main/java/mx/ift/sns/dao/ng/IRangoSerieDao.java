package mx.ift.sns.dao.ng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.filtros.FiltroBusquedaHistoricoSeries;
import mx.ift.sns.modelo.filtros.FiltroBusquedaRangos;
import mx.ift.sns.modelo.ng.DetalleRangoAsignadoNg;
import mx.ift.sns.modelo.ng.DetalleReporteAbd;
import mx.ift.sns.modelo.ng.NumeracionSolicitada;
import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.ng.SeriePK;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.EstadoNumeracion;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.NirNumeracion;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.ot.PoblacionNumeracion;
import mx.ift.sns.modelo.pnn.DetallePlanAbdPresuscripcion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.ProveedorConvenio;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.modelo.solicitud.Solicitud;

/**
 * Interfaz RangoSerie DAO.
 */
public interface IRangoSerieDao extends IBaseDAO<RangoSerie> {

    /**
     * Recupera las numeraciones asigandas por ABN, NIR y SNA.
     * @param abn abn
     * @param nir nir
     * @param sna sna
     * @return List<RangoSerie>
     */
    List<RangoSerie> findNumeracionesAsignadasSerie(Abn abn, Nir nir, BigDecimal sna);

    /**
     * . Recupera los rangos solicitadados sin asignar por una solicitud sin asignar.
     * @param codSol codigo solicitud
     * @return List<RangoSerie>
     */
    List<RangoSerie> findRangoSerieBySolicitudSinAsignar(BigDecimal codSol);

    /**
     * Recupera los rangos solicitadados por una solicitud.
     * @param codSol codigo solicitud
     * @return List<RangoSerie>
     */
    List<RangoSerie> findRangoSerieBySolicitud(BigDecimal codSol);

    /**
     * Recupera los rangos solicitados en función de los parámetros facilitados.
     * @param pFiltros Filtros de Búsqueda
     * @return List
     */
    List<RangoSerie> findAllRangos(FiltroBusquedaRangos pFiltros);

    /**
     * Recupera el número rangos solicitados en función de los parámetros facilitados.
     * @param pFiltros Filtros de Búsqueda
     * @return int Número de registros
     */
    int findAllRangosCount(FiltroBusquedaRangos pFiltros);

    /**
     * Recupera el total de numeraciones asgnada por solicitud .
     * @param codSol codigo solicitud
     * @return total
     */
    BigDecimal getTotalNumAsignadaSolicitud(BigDecimal codSol);

    /**
     * Recupera el total de numeraciones ocupadas en un ABN.
     * @param abn abn
     * @return BigDecimal
     */
    BigDecimal getTotalNumOcupadaAbn(Abn abn);

    /**
     * Recupera el total de numeraciones ocupadas en una Serie.
     * @param id SeriePK
     * @return BigDecimal
     */
    BigDecimal getTotalNumOcupadaSerie(SeriePK id);

    /**
     * Comprueba si existe un rango con los parámetros indicados.
     * @param pIdNir Identificador de Región.
     * @param pSna Identificador de Serie.
     * @param pNumInicial Inicio de Rango.
     * @param pAsignatario Asignatario del Rango.
     * @return True si el rango existe.
     */
    boolean existeRangoSerie(BigDecimal pIdNir, BigDecimal pSna,
            String pNumInicial, Proveedor pAsignatario);

    /**
     * Devuelve un rango asignado a una serie en función del Nir, Sna y Numero Inicial.
     * @param pIdNir Identificador de Nir
     * @param pSna Identificador de Serie
     * @param pNumInicial Número Inicial
     * @param pAsignatario Proveedor Asignatario del Rango
     * @return RangoSerie
     */
    RangoSerie getRangoSerie(BigDecimal pIdNir, BigDecimal pSna,
            String pNumInicial, Proveedor pAsignatario);

    /**
     * Devuelve el rango que contiene la numeracion indicada por parámetros.
     * @param pIdNir Identificador de Región
     * @param pSna Identificador de Serie
     * @param pNumInicial Inicio de Fracción
     * @param pNumFinal Fin de Fracción
     * @param pAsignatario Proveedor Asignatario del Rango
     * @return Rango del proveedor que contiene la numeración
     */
    RangoSerie getRangoSerieByFraccion(BigDecimal pIdNir, BigDecimal pSna,
            String pNumInicial, String pNumFinal, Proveedor pAsignatario);

    /**
     * Devuelve el rango al que pertenece nir, sna, numeracion inicial y final.
     * @param nir nir
     * @param sna sna
     * @param numInicial numeracion
     * @param numFinal numeracion
     * @return Rango
     */
    RangoSerie getRangoPertenece(Nir nir, BigDecimal sna, String numInicial, String numFinal);

    /**
     * Devuelve el rango al que pertenece nir, sna y numeracion.
     * @param nir nir
     * @param sna sna
     * @param num numeracion
     * @return Rango
     */
    RangoSerie getRangoPerteneceNumeracion(Nir nir, BigDecimal sna, String num);

    /**
     * Obtiene el total de rangos asignados a un PST.
     * @param tipoRed String
     * @param tipoModalidad String
     * @param proveedor Proveedor
     * @return total
     */
    BigDecimal getTotalRangosAsignadosByPst(String tipoRed, String tipoModalidad, Proveedor proveedor);

    /**
     * Obtiene el total de numeraciones en los rangos asignados a un PST por poblacion.
     * @param tipoRed Tipo de Red
     * @param tipoModalidad Tipo de Modalidad
     * @param proveedor Proveedor Asignatario
     * @param poblacion Población
     * @return Número de rangos asignados por población.
     */
    BigDecimal getTotalNumRangosAsignadosByPoblacion(String tipoRed, String tipoModalidad, Proveedor proveedor,
            String poblacion);

    /**
     * Obtiene el total de numeraciones en los rangos arrendados.
     * @param arrendador Proveedor Arrendador
     * @param arrendadatario Proveedor Arrendatario
     * @return Número de rangos arrendados
     */
    BigDecimal getTotalNumRangosArrendados(Proveedor arrendador, Proveedor arrendadatario);

    /**
     * Busqueda de historico de series.
     * @param filtro a buscar
     * @param first primer campo
     * @param pagesize tamaño
     * @return el bloque de datos
     */
    List<RangoSerie> findHistoricoSeries(FiltroBusquedaHistoricoSeries filtro, int first, int pagesize);

    /**
     * Total de registros de la busqueda de historico de series.
     * @param filtro a buscar
     * @return numero de resultados
     */
    int findHistoricoSeriesCount(FiltroBusquedaHistoricoSeries filtro);

    /**
     * Calcula el total de numeraciones asignadas en un abn.
     * @param tipoRed tipoRed
     * @param tipoModalidad tipoModalidad
     * @param proveedor proveedor
     * @param abn abn
     * @return total num
     */
    BigDecimal getTotalNumRangosAsignadosByAbn(String tipoRed, String tipoModalidad,
            BigDecimal abn, Proveedor proveedor);

    /**
     * Obtiene los rangos creado a partir de una numeracion solicitada.
     * @param numeracionSolicitada numeracionSolicitada
     * @return lista rangos
     */
    List<RangoSerie> findAllRangosByNumSol(NumeracionSolicitada numeracionSolicitada);

    /**
     * @param abn Abn.
     * @return Obtiene la poblacion con mayor numeracion asignada de un abn.
     */
    Poblacion getPoblacionWithMaxNumAsignadaByAbn(Abn abn);

    /**
     * Devuelve el numero de registros asignados.
     * @return num
     */
    BigDecimal getNumRangosAsignados();

    /**
     * Devuelve el numero de rangos asignados indicado en pageSize.
     * @param first primer registro
     * @param pageSize cantidad de registros
     * @return lista de registros
     */
    List<RangoSerie> getRangosAsignados(int first, int pageSize);

    /**
     * Devuelve el numero de rangos asignados indicado en pageSize.
     * @param first primer registro
     * @param pageSize cantidad de registros
     * @return lista de registros
     */
    List<DetalleRangoAsignadoNg> getRangosAsignadosD(int first, int pageSize);

    /**
     * Busca los nirs posibles para el numero local (7 u 8 digitos).
     * @param sna sna
     * @param num numero
     * @return list nir.
     */
    List<Nir> findNirsNumeroLocal(BigDecimal sna, String num);

    /**
     * Obtiene el total de numeracion asignada en un municipio.
     * @param municipio municipio
     * @return total asignados
     */
    Integer getTotalNumRangosAsignadosByMunicipio(Municipio municipio);

    /**
     * Obtiene el total de numeración asignada en un estado.
     * @param estado Estado
     * @return total asignados
     */
    Integer getTotalNumRangosAsignadosByEstado(Estado estado);

    /**
     * Método que obtiene el número de nirs a partir de las poblaciones.
     * @param poblacion poblacion
     * @return int
     */
    int getNirsByPoblacion(Poblacion poblacion);

    /**
     * Método que obtiene los Nirs de una poblacion.
     * @param poblacion poblacion
     * @return List<Nir>
     */
    List<Nir> findAllNirsByPoblacion(Poblacion poblacion);

    /**
     * Obtiene las poblaciones y la numeracion asignada en cada una de un proveedor.
     * @param proveedorServ proveedor.
     * @return list poblacion y numeracion
     */
    List<PoblacionNumeracion> getPoblacionesNumeracionByProveedor(Proveedor proveedorServ);

    /**
     * Obtiene los estados y la numeracion asignada en cada uno de un proveedor.
     * @param proveedorServ proveedor.
     * @return list estado y numeracion
     */
    List<EstadoNumeracion> getEstadosNumeracionByProveedor(Proveedor proveedorServ);

    /**
     * Obtiene los nirs y la numeracion asignada en cada uno de un proveedor.
     * @param proveedorServ proveedor.
     * @return list nir y numeracion
     */
    List<NirNumeracion> getNirsNumeracionByProveedor(Proveedor proveedorServ);

    /**
     * Función que comprueba si existe una numeración asignada al Pst.
     * @param proveedor Proveedor
     * @return boolean existe
     */
    boolean existeNumeracionAsignadaAlPst(Proveedor proveedor);

    /**
     * Función que comprueba si existe una numeración asignada al Pst a través de un convenio.
     * @param proveedorConvenio ProveedorConvenio
     * @return boolean existe
     */
    boolean existeNumeracionAsignadaAlPstByConvenio(ProveedorConvenio proveedorConvenio);

    /**
     * Busca la numeración asignada a un proveedor en una población.
     * @param proveedorServ proveedor.
     * @param poblacion poblacion.
     * @return Bigdecimal numeracion.
     */
    BigDecimal getNumeracionPoblacionByProveedor(Proveedor proveedorServ, Poblacion poblacion);

    /**
     * Obtiene todo la numeración asignada a un proveedor.
     * @param idPst BigDecimal
     * @return Integer
     */
    Integer getTotalNumeracionAginadaProveedor(BigDecimal idPst);

    /**
     * Obtiene una lista de población numeració filtrada por proveedor y estado.
     * @param proveedorServ Proveedor
     * @param estado Estado
     * @return List<PoblacionNumeracin>
     */
    List<PoblacionNumeracion> findALLPoblacionesNumeracionByProveedorEstado(Proveedor proveedorServ, Estado estado);

    /**
     * Obtiene una lista de población numeració filtrada por proveedor y nir.
     * @param proveedorServ Proveedor
     * @param nir Nir
     * @return List<PoblacionNumeracion>
     */
    List<PoblacionNumeracion> findALLPoblacionesNumeracionByProveedorNir(Proveedor proveedorServ, Nir nir);

    /**
     * Obtiene el total de todos los rangos fijos.
     * @return total
     */
    int findAllRangosAsignadosFijosCount();

    /**
     * Obtiene todos los rangos asignados fijos.
     * @param numPage pagina
     * @param pageSize tamaño
     * @return lista
     */
    List<RangoSerie> findAllRangosAsignadosFijos(int numPage, int pageSize);

    /**
     * Obtiene la lista de proveedores que prestan servicio por: nir, abn, poblacion, municipio ó estado.
     * @param nir Nir
     * @param abn Abn
     * @param poblacion Poblacion
     * @param municipio Municipio
     * @param estado Estado
     * @return List<Proveedor>
     */
    List<Proveedor> findAllPrestadoresServicioBy(Nir nir, Abn abn, Poblacion poblacion, Municipio municipio,
            Estado estado);

    /**
     * Obtiene las poblaciones con numeración asignada de un NIR.
     * @param nir Nir
     * @return List<Poblacion>
     */
    List<Poblacion> findALLPoblacionesNumeracionAsignadaByNir(Nir nir);

    /**
     * Obtiene la numeración asignada a un nir.
     * @param nir NIr
     * @return int
     */
    int findNumeracionesAsignadasNir(Nir nir);

    /**
     * Obtiene las poblaciones con numeración asignada de un municipio.
     * @param municipio Municipio
     * @return List<Poblacion>
     */
    List<Poblacion> findALLPoblacionesNumeracionAsignadaByMunicipio(Municipio municipio);

    /**
     * Método que comprueba si existe numeración asignada para un NIR, SERIE y número.
     * @param nir de la numeración
     * @param sna de la numeración
     * @param num de la numeración
     * @return si existe o no numeración asignada
     */
    boolean existeNumeracionAsignada(Nir nir, BigDecimal sna, String num);

    /**
     * Obtiene todo los rangos creados por una solicitud.
     * @param pSolicitud solicitud
     * @return lista rangos
     */
    List<RangoSerie> findAllRangosBySolicitud(Solicitud pSolicitud);

    /**
     * Comprueba si un rango esta libre
     * @param nir nir
     * @param sna sna
     * @param inicioRango inicioRango
     * @param finRango finRango
     * @return true/false
     */
    boolean isRangoLibre(BigDecimal nir, BigDecimal sna, String inicioRango, String finRango);

    /**
     * Método que obtiene el listado de detalles de planes de rangos asignados.
     * @param numPage de inicio
     * @param pageSize del listado
     * @return listado de detalles
     */
    List<DetallePlanAbdPresuscripcion> findAllRangosAsignadosFijosD(int numPage, int pageSize);

    /**
     * Método encargado de obtener los rangos asignados para una serie e ido del pst asignatario.
     * @param codigoNir de la serie
     * @param sna de la serie
     * @param ido del asignatario
     * @return listado de rangos
     */
    List<RangoSerie> getRangoAsignadoByIdo(String codigoNir, String sna, String ido);

    /**
     * Método encargado de obtener los rangos asignados para una serie.
     * @param codigoNir de la serie
     * @param sna de la serie
     * @return listado de rangos
     */
    List<RangoSerie> getRangoAsignados(String codigoNir, String sna);

    /**
     * Método encargado de obtener el número de registros del reporte abd.
     * @return número de registros del reporte
     */
    Long getDetalleReporteAbdCount();

    /**
     * Método encargado de obtener el detalle del reporte abd.
     * @param first elemento del reporte
     * @param pageSize número de registros a obtener
     * @return listado de registros del reporte
     */
    List<DetalleReporteAbd> getDetalleReporteAbd(int first, int pageSize);

    /**
     * Método encargado de crear los registros auxiliares para la creación del plan ABD.
     */
    void generarPnnAux();

    /**
     * true si hay rangos en estado pendiente para un nir seleccionado.
     * @param nir Nir
     * @return boolean
     */
    boolean isRangosPentientesByNir(Nir nir);
}
