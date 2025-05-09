package mx.ift.sns.negocio.ng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.filtros.FiltroBusquedaHistoricoSeries;
import mx.ift.sns.modelo.filtros.FiltroBusquedaRangos;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSeries;
import mx.ift.sns.modelo.filtros.FiltroReporteadorNG;
import mx.ift.sns.modelo.ng.Cesion;
import mx.ift.sns.modelo.ng.CesionSolicitadaNg;
import mx.ift.sns.modelo.ng.DetalleRangoAsignadoNg;
import mx.ift.sns.modelo.ng.DetalleReporteAbd;
import mx.ift.sns.modelo.ng.Liberacion;
import mx.ift.sns.modelo.ng.LiberacionSolicitadaNg;
import mx.ift.sns.modelo.ng.NumeracionRedistribuida;
import mx.ift.sns.modelo.ng.NumeracionSolicitada;
import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.ng.RedistribucionSolicitadaNg;
import mx.ift.sns.modelo.ng.Serie;
import mx.ift.sns.modelo.ng.SeriePK;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.EstadoNumeracion;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.NirNumeracion;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.ot.PoblacionNumeracion;
import mx.ift.sns.modelo.pnn.DetallePlanAbdPresuscripcion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.ProveedorConvenio;
import mx.ift.sns.modelo.reporteabd.SerieArrendada;
import mx.ift.sns.modelo.reporteabd.SerieArrendadaPadre;
import mx.ift.sns.modelo.reporteador.NGReporteador;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.series.HistoricoSerie;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.modelo.series.VCatalogoSerie;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.negocio.num.model.Numero;

/**
 * Servicio de series.
 */
public interface ISeriesService {

    /**
     * Recupera las series ocupadas por un PST en un ABN.
     * @param pst pst
     * @param abn abn
     * @return listaOcupacion
     **/
    List<Serie> findSeriesPst(Proveedor pst, Abn abn);

    /**
     * Recupera el total de numeraciones ocupadas en una serie.
     * @param id SeriePK
     * @return total
     * @throws Exception en caso de error
     **/
    BigDecimal getTotalNumOcupadaSerie(SeriePK id) throws Exception;

    /**
     * Recupera las numeraciones asigandas por ABN, NIR y SNA.
     * @param abn abn de la serie
     * @param nir nir de la serie
     * @param sna identificador de la serie
     * @return lista de ocupaciones
     */
    List<RangoSerie> findNumeracionesAsiganadasSerie(Abn abn, Nir nir, BigDecimal sna);

    /**
     * Devuelve los rangos de una serie por solicitud.
     * @param codSol codigo solicitud
     * @return Ocupacion
     * @throws Exception escepción
     */
    List<RangoSerie> findNumeracionesSeleccionadas(BigDecimal codSol) throws Exception;

    /**
     * Devuelve las rangos sin asignar de una serie por solicitud.
     * @param codSol codigo solicitud
     * @return Ocupacion
     * @throws Exception escepción
     */
    List<RangoSerie> findNumeracionesSeleccionadasSinAsignar(BigDecimal codSol) throws Exception;

    /**
     * Recupera el total de numeraciones asignadas por solicitud.
     * @param codSol codigo de solicitud
     * @return total
     * @throws Exception en caso de error
     **/
    BigDecimal getTotalNumAsignadaSolicitud(BigDecimal codSol) throws Exception;

    /**
     * Recupera el total de numeraciones ocupadas en un ABN.
     * @param abn abn
     * @return total
     * @throws Exception en caso de error
     */
    BigDecimal getTotalNumOcupadaAbn(Abn abn) throws Exception;

    /**
     * Devuelve un objeto EstadoRango por su codigo.
     * @param codigo codigo
     * @return EstadoRango
     */
    EstadoRango getEstadoRangoByCodigo(String codigo);

    /**
     * Persite un objeto rangoSerie.
     * @param rangoSerie RangoSerie
     * @return RangoSerie
     * @throws Exception excepcion
     */
    RangoSerie saveRangoSerie(RangoSerie rangoSerie) throws Exception;

    /**
     * Elimina un objeto RangoSerie permanentemente.
     * @param pRangoSerie Objeto a eliminar
     * @throws Exception En caso de error.
     */
    void removeRangoSerie(RangoSerie pRangoSerie) throws Exception;

    /**
     * Recupera las series ocupadas por otros PSTs en un ABN.
     * @param pst pst
     * @param abn abn
     * @throws Exception excepción genérica
     * @return listaOcupacion
     */
    List<Serie> findSeriesOtrosPsts(Proveedor pst, Abn abn) throws Exception;

    /**
     * Recupera las series libres en un ABN.
     * @param abn abn
     * @return listaSerie
     * @throws Exception excepcion
     */
    List<Serie> findSeriesLibres(Abn abn) throws Exception;

    /**
     * Recupera las series en función de los filtros dados.
     * @param pFiltros Filtros de búsqueda
     * @return List
     * @throws Exception en caso de error
     */
    List<Serie> findAllSeries(FiltroBusquedaSeries pFiltros) throws Exception;

    /**
     * Recupera todas las series.
     * @param pFiltros pFiltros
     * @return List<VCatalogoSerie>
     * @throws Exception Exception
     */
    List<VCatalogoSerie> findAllCatalogoSeries(FiltroBusquedaSeries pFiltros) throws Exception;

    /**
     * Recupera el número de resultados de búsqueda de series/rangos en función de los filtros.
     * @param pFiltros Filtros de búsqueda
     * @return int número de series para series libres o de rangos para series no libres
     * @throws Exception Exception en caso de error
     */
    int findAllSeriesRangosCount(FiltroBusquedaSeries pFiltros) throws Exception;

    /**
     * Recupera el número de resultados de búsqueda de series.
     * @param pFiltros pFiltros
     * @return int
     * @throws Exception Exception
     */
    int findAllCatalogoSeriesCount(FiltroBusquedaSeries pFiltros) throws Exception;

    /**
     * Recupera los rangos solicitados en función de los parámetros facilitados.
     * @param pFiltros Filtros de Búsqueda
     * @return List
     * @throws Exception en caso de error.
     */
    List<RangoSerie> findAllRangos(FiltroBusquedaRangos pFiltros) throws Exception;

    /**
     * Recupera el número rangos solicitados en función de los parámetros facilitados.
     * @param pFiltros Filtros de Búsqueda
     * @return int Número de registros
     * @throws Exception en caso de error.
     */
    int findAllRangosCount(FiltroBusquedaRangos pFiltros) throws Exception;

    /**
     * Libera un rango sin fraccionar.
     * @param pLibSol Información de la liberación solicitada
     * @param pInmediata Indica si la fecha de implementación es inmediata (true) o programada (false)
     * @return List Lista de Liberaciones Generada
     * @throws Exception en caso de error.
     */
    List<Liberacion> liberarRangoCompleto(LiberacionSolicitadaNg pLibSol, boolean pInmediata) throws Exception;

    /**
     * Fracciona un rango aplicando las reservas y liberaciones solicitadas.
     * @param pLiberaciones Solicitudes de Liberacion de un mismo RangoSerie
     * @param pInmediata Indica si la fecha de implementación es inmediata (true) o programada (false)
     * @return Lista de Liberaciones generadas
     * @throws Exception en caso de error.
     */
    List<Liberacion> liberarFraccionesRango(List<LiberacionSolicitadaNg> pLiberaciones, boolean pInmediata)
            throws Exception;

    /**
     * Cede un rango de un Cedente a un Cesionario.
     * @param pCesionSolicitada Información de la cesión
     * @param pInmediata Indica si la fecha de implementación es inmediata (true) o programada (false)
     * @return List<Cesion> Lista de Cesiones Generadas
     * @throws Exception en caso de error.
     */
    List<Cesion> cederRangoCompleto(CesionSolicitadaNg pCesionSolicitada, boolean pInmediata) throws Exception;

    /**
     * Cede las fracciones solicitadas de un rango de un Cedente a un Cesionario.
     * @param rangoOriginal
     * @param pCesionesSolicitadas Lista de cesiones con la información de las fracciones.
     * @param pInmediata Indica si la fecha de implementación es inmediata (true) o programada (false)
     * @return List<Cesion> Lista de Cesiones Generadas
     * @throws Exception en caso de error.
     */
    List<Cesion> cederFraccionesRango(RangoSerie rangoOriginal, List<CesionSolicitadaNg> pCesionesSolicitadas,
            boolean pInmediata)
            throws Exception;

    // /**
    // * Cede un rango de un Cedente a un Cesionario durante una Fusión de Proveedores.
    // * @param pCesionSolicitada Información del Rango y Proveedores
    // * @return Cesion Generada
    // * @throws Exception en caso de error.
    // */
    // Cesion cederRangoEnFusion(CesionSolicitadaNg pCesionSolicitada) throws Exception;

    /**
     * Recupera una serie por SNA y NIR.
     * @param pIdSna BigDecimal
     * @param pIdNir BigDecimal
     * @return Serie
     * @throws Exception en caso de error.
     */
    Serie getSerie(BigDecimal pIdSna, BigDecimal pIdNir) throws Exception;

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
     * @throws Exception en caso de error.
     */
    RangoSerie getRangoSerie(BigDecimal pIdNir, BigDecimal pSna,
            String pNumInicial, Proveedor pAsignatario) throws Exception;

    /**
     * Devuelve el rango que contiene la numeracion indicada por parámetros.
     * @param pIdNir Identificador de Región
     * @param pSna Identificador de Serie
     * @param pNumInicial Inicio de Fracción
     * @param pNumFinal Fin de Fracción
     * @param pAsignatario Proveedor Asignatario del Rango
     * @return Rango del proveedor que contiene la numeración
     * @throws Exception en caso de error
     */
    RangoSerie getRangoSerieByFraccion(BigDecimal pIdNir, BigDecimal pSna,
            String pNumInicial, String pNumFinal, Proveedor pAsignatario) throws Exception;

    /**
     * Dada una fracción de un Rango, devuelve la información del rango original del que se obtuvo la fraccción.
     * @param pFraccion Información de la fracción del rango.
     * @param pIdSolicitud Identificador del trámite que realizó el fraccionamiento.
     * @param pAsignatarioInicial Asignatario del rango original
     * @return RangoSerie con la información de la numeración original.
     * @throws Exception en caso de error.
     */
    RangoSerie getRangoSerieOriginalByFraccion(RangoSerie pFraccion, BigDecimal pIdSolicitud,
            Proveedor pAsignatarioInicial) throws Exception;

    /**
     * Indica si el rango entre los numeros dados esta arrendado por el arrendador.
     * @param from numero inicial
     * @param to numero final
     * @param arrendador proveedor que arrienda la numeracion
     * @return true si esta arrendado
     */
    boolean estaArrendadoRango(Numero from, Numero to, Proveedor arrendador);

    /**
     * Comprueba si los dos numeros pertenecen a la misma serie.
     * @param n0 numero
     * @param n1 numero
     * @return true si pertenecen a la misma serie, false eoc
     */
    boolean pertenecenMismaSerie(Numero n0, Numero n1);

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
     * Devuelve el rango al que pertenece el numero indicado.
     * @param numero numero
     * @return Rango
     */
    RangoSerie getRangoPertenece(Numero numero);

    /**
     * Crea una serie arrendada.
     * @param serie arrendada.
     */
    void create(SerieArrendada serie);

    /**
     * @see mx.ift.sns.dao.ng.ISerieArrendadaDAO#deleteAll()
     */
    void deleteAllSeriesArrendadas();

    /**
     * Fracciona un rango aplicando las reservas y liberaciones solicitadas.
     * @param pRedistribuciones Solicitudes de Redistribución de un mismo RangoSerie
     * @return Lista de Liberaciones generadas
     * @throws Exception en caso de error.
     */
    List<NumeracionRedistribuida> fraccionarRangoFromRedistribuciones(
            List<RedistribucionSolicitadaNg> pRedistribuciones) throws Exception;

    /**
     * Actualiza un rango con la información seleccionada por el usuario en redistribución.
     * @param pRango Rango para actualizar.
     * @param pRedSol Información de los nuevos valores del rango.
     * @return NumeracionRedistribuida con la información de la redistribución.
     * @throws Exception En caso de error.
     */
    NumeracionRedistribuida redistribuirRango(RangoSerie pRango, RedistribucionSolicitadaNg pRedSol) throws Exception;

    /**
     * Función que busca las series arrendadas.
     * @param first int
     * @param pageSize int
     * @return List<SerieArrendada> Series arrendadas
     */
    List<SerieArrendada> findSeriesArrendadas(int first, int pageSize);

    /**
     * @return int
     */
    int getSeriesArrendadasCount();

    /**
     * Indica si un rango tiene arrendamiento.
     * @param rango Informaciónd el rango.
     * @return True si existe arrendamiento en el rango.
     */
    boolean estaArrendadoRango(RangoSerie rango);

    /**
     * Indica si existen arrendamiento entre dos proveedores para un rango determinado.
     * @param rango Información del Rango.
     * @param arrendador Proveedor Arrendador.
     * @param arendatario Proveedor Arrendatario.
     * @return True si existe arrendamiento.
     */
    boolean hayArrendamiento(RangoSerie rango, Proveedor arrendador, Proveedor arendatario);

    /**
     * Recupera la serie más alta o más baja de un Nir.
     * @param pCdgNir Nir al que pertenece la serie.
     * @param pMaxValue 'True' recupera el valor de serie más alto. 'False' el valor de serie más bajo.
     * @return int
     */
    int getMaxMinSerieFromNir(int pCdgNir, boolean pMaxValue);

    /**
     * Indica si el rango es una serie completa.
     * @param rango rango a comprobar
     * @return true si es serie completa
     */
    boolean isSerieCompleta(RangoSerie rango);

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
     * @param tipoRed Información del Tipo de Red.
     * @param tipoModalidad Información del Tipo de Modalidad..
     * @param proveedor Información del Proveedor.
     * @param poblacion Información de la población.
     * @return total
     */
    BigDecimal getTotalNumRangosAsignadosByPoblacion(String tipoRed, String tipoModalidad, Proveedor proveedor,
            Poblacion poblacion);

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
     * Obtiene el histórico de las series que muestran los informes del reporteador.
     * @param filtro a buscar
     * @return el bloque de datos
     * @throws Exception Exception
     */
    List<NGReporteador> findHistoricoSeries(FiltroReporteadorNG filtro) throws Exception;

    /**
     * Obtiene el histórico de las series Asignadas que muestran los informes del reporteador.
     * @param filtro a buscar
     * @return el bloque de datos
     */
    List<NGReporteador> findHistoricoSeriesAsignadas(FiltroReporteadorNG filtro);

    /**
     * Comprueba si el numero dado existe en el PNN.
     * @param num numero
     * @return true si existe
     */
    boolean existeNumeracion(Numero num);

    /**
     * Obtiene los rangos creado a partir de una numeracion solicitada.
     * @param numeracionSolicitada numeracionSolicitada
     * @return lista rangos
     */
    List<RangoSerie> findAllRangosByNumSol(NumeracionSolicitada numeracionSolicitada);

    /**
     * Indica si el pst tiene la serie completa indicada por nir y sna.
     * @param idNir id nir
     * @param sna sna
     * @param pst proveedor
     * @return true si tiene la serie completa
     */
    boolean tieneSerieCompleta(BigDecimal idNir, BigDecimal sna, Proveedor pst);

    /**
     * Obtiene el total de numeraciones en los rangos asignados a un PST por abn.
     * @param tipoRed String
     * @param tipoModalidad String
     * @param abn Abn
     * @param proveedor Proveedor
     * @return total
     */
    BigDecimal getTotalNumRangosAsignadosByAbn(String tipoRed, String tipoModalidad, Abn abn, Proveedor proveedor);

    /**
     * Comprueba si es valido un nir.
     * @param nir nir
     * @return true false
     */
    boolean isValidoNir(String nir);

    /**
     * Comprueba si es valido un nir.
     * @param nir nir
     * @return true false
     */
    boolean isValidoNir(Integer nir);

    /**
     * Comprueba si es valido un sna.
     * @param sna sna
     * @return true false
     */
    boolean isValidoSNA(String sna);

    /**
     * Comprueba si es valido un sna.
     * @param sna sna
     * @return true false
     */
    boolean isValidoSNA(Integer sna);

    /**
     * @param abn Abn.
     * @return poblacion con mayor numeracion asignada de un abn.
     */

    Poblacion getPoblacionWithMaxNumAsignadaByAbn(Abn abn);

    /**
     * Devuelve el numero de registros asignados.
     * @return numero de registros en la tabla, 0 si está vacia.
     */
    BigDecimal getNumRangosAsignados();

    /**
     * Devuelve el numero de rangos asignados indicado en pageSize.
     * @param num numero
     * @param pageSize tamaño pagina
     * @return lista de rangos
     */
    List<RangoSerie> getRangosAsignados(int num, int pageSize);

    /**
     * Busca los nirs posibles para el numero local (7 u 8 digitos).
     * @param numeroLocal numero local
     * @return lista de nirs
     */
    List<Nir> findNirsNumeroLocal(Numero numeroLocal);

    /**
     * Obtiene el total de numeracion asignada en un municipio.
     * @param municipio municipio
     * @return total asignados
     */
    Integer getTotalNumRangosAsignadosByMunicipio(Municipio municipio);

    /**
     * Obtiene el total de numeración asignada en un muinicipio.
     * @param estado Estado
     * @return total asignados
     */
    Integer getTotalNumRangosAsignadosByEstado(Estado estado);

    /**
     * Recupera el catálogo estados de rangos.
     * @return List
     */
    List<EstadoRango> findAllEstadosRango();

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
     * Devuelve una lista de poblaciones y numeracion asignada donde un proveedor presta servicio.
     * @param proveedorServ Proveedor
     * @return List poblaciones y numeracion.
     */
    List<PoblacionNumeracion> getPoblacionesNumeracionByProveedor(Proveedor proveedorServ);

    /**
     * Devuelve una lista de estados y numeracion asignada donde un proveedor presta servicio.
     * @param proveedorServ Proveedor
     * @return List estados y numeracion.
     */
    List<EstadoNumeracion> getEstadosNumeracionByProveedor(Proveedor proveedorServ);

    /**
     * Devuelve una lista de nirs y numeracion asignada donde un proveedor presta servicio.
     * @param proveedorServ Proveedor
     * @return List nirs y numeracion.
     */
    List<NirNumeracion> getNirsNumeracionByProveedor(Proveedor proveedorServ);

    /**
     * Recupera una Clave de Servicio por su código.
     * @param pCodigo Código de Clave de Servicio.
     * @return ClaveServicio
     */
    ClaveServicio getClaveServicioByCodigo(BigDecimal pCodigo);

    /**
     * Recupera un Nir por su identificador.
     * @param pIdNir BigDecimal
     * @return Nir
     * @throws Exception en caso de error.
     */
    Nir getNirById(BigDecimal pIdNir) throws Exception;

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
     * Devuelve la numeracion asignada a un proveedor en una población.
     * @param proveedorServ proveedor.
     * @param poblacion poblacion.
     * @return Bigdecimal numeracion.
     */
    BigDecimal getNumeracionPoblacionByProveedor(Proveedor proveedorServ, Poblacion poblacion);

    /**
     * Persiste una Serie y sus cambios.
     * @param pSerie Información de la Serie.
     * @return Serie persistida.
     */
    Serie saveSerie(Serie pSerie);

    /**
     * Crea una nueva serie para el Nir indicado.
     * @param pNir Información del Nir.
     * @param pSna Identificador de Serie.
     * @return Serie
     */
    Serie createSerie(Nir pNir, BigDecimal pSna);

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
     * Exporta los datos de la serie.
     * @param pfiltro pfiltro
     * @return byte[]
     * @throws Exception exception
     */
    byte[] getExportConsultaCatalogoSeries(FiltroBusquedaSeries pfiltro) throws Exception;

    /**
     * Comprueba que una serie exite con un nir y un abn dado.
     * @param abn abn
     * @param nir nir
     * @return boolean
     */
    boolean existSerieWithNirAbn(String abn, String nir);

    /**
     * Busca las series ocupadas segun filtro.
     * @param filtros FiltroBusquedaSeries
     * @return series
     */
    List<Object[]> findAllSeriesOcupadas(FiltroBusquedaSeries filtros);

    /**
     * Busca los totales de tramites por fecha y PST.
     * @param filtro filtro
     * @return totales(fecha, tipotramite,numTramites, volumen numeros, volumen registros)
     */
    List<Object[]> findTotalesTramites(FiltroReporteadorNG filtro);

    /**
     * Obtiene todo la numeración asignada a un proveedor.
     * @param idPst BigDecimal
     * @return Integer
     */
    Integer getTotalNumeracionAginadaProveedor(BigDecimal idPst);

    /**
     * Recupera un nir por su codigo.
     * @param cdgNir codigo nir
     * @return el nir
     */
    Nir getNirByCodigo(int cdgNir);

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
     * Método que realiza la llamada a un procedimiento de BD para actualizar las series.
     * @param serieOriginal serieOriginal
     * @param nirOriginal nirOriginal
     * @param serieNueva serieNueva
     * @param usuario usuario
     * @return String
     */
    String actualizaSerie(BigDecimal serieOriginal, BigDecimal nirOriginal, BigDecimal serieNueva,
            BigDecimal usuario);

    /**
     * Obtiene la lista de elementos del reporte con las operaciones intermedias realizadas.
     * @param lista1 lista con las asignaciones positivas
     * @param lista2 lista con las asignaciones negativas
     * @param filtro elementos de filtrado por parte del usuario
     * @return List <NGReporteador> lista con el resultado de la operación
     * @throws Exception Excepción en caso de error
     */
    List<NGReporteador> getListaResultado(List<NGReporteador> lista1, List<NGReporteador> lista2,
            FiltroReporteadorNG filtro) throws Exception;

    /**
     * Genera un fichero excel con los filtros de una cosulta de historico de series.
     * @param filtro busqueda
     * @return fichero
     * @throws Exception error
     */
    byte[] getExportHistoricoSeries(FiltroBusquedaHistoricoSeries filtro) throws Exception;

    /**
     * Obtiene todo los rangos creados por una solicitud.
     * @param pSolicitud solicitud
     * @return lista rangos
     */
    List<RangoSerie> findAllRangosBySolicitud(Solicitud pSolicitud);

    /**
     * Comprueba si un rango esta libre.
     * @param nir nir
     * @param sna sna
     * @param inicioRango inicioRango
     * @param finRango finRango
     * @return true/false
     */
    boolean isRangoLibre(BigDecimal nir, BigDecimal sna, String inicioRango, String finRango);

    /**
     * Obtiene todos los rangos asignados fijos.
     * @param numPage pagina
     * @param pageSize tamaño
     * @return lista
     */
    List<DetallePlanAbdPresuscripcion> findAllRangosAsignadosFijosD(int numPage, int pageSize);

    /**
     * Devuelve el numero de rangos asignados indicado en pageSize.
     * @param num numero
     * @param pageSize tamaño pagina
     * @return lista de rangos
     */
    List<DetalleRangoAsignadoNg> getRangosAsignadosD(int num, int pageSize);

    /**
     * Función que busca las series arrendadas padre.
     * @param first int
     * @param pageSize int
     * @return List<SerieArrendada> Series arrendadas
     */
    List<SerieArrendadaPadre> findSeriesArrendadasPadre(int first, int pageSize);

    /**
     * Función que busca el número de series arrendadas padre.
     * @return número de series arrendadas padre
     */
    int findSeriesArrendadasPadreCount();

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
