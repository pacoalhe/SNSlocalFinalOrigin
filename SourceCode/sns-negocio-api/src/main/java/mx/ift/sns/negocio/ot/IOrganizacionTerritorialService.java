package mx.ift.sns.negocio.ot;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.abn.EstadoAbn;
import mx.ift.sns.modelo.abn.PoblacionAbn;
import mx.ift.sns.modelo.abn.VCatalogoAbn;
import mx.ift.sns.modelo.filtros.FiltroBusquedaABNs;
import mx.ift.sns.modelo.filtros.FiltroBusquedaMunicipios;
import mx.ift.sns.modelo.filtros.FiltroBusquedaPoblaciones;
import mx.ift.sns.modelo.ng.AbnConsolidar;
import mx.ift.sns.modelo.ng.NirConsolidar;
import mx.ift.sns.modelo.ng.PoblacionConsolidar;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.EstadoArea;
import mx.ift.sns.modelo.ot.ExportMunicipio;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.MunicipioPK;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.ot.Region;
import mx.ift.sns.modelo.ot.VCatalogoPoblacion;
import mx.ift.sns.modelo.series.Nir;

/**
 * Interfaz del servicio de Organizcion Territorial.
 */
public interface IOrganizacionTerritorialService {

    /**
     * Lista de estado.
     * @return lista de estados
     * @throws Exception exepcion
     */
    List<Estado> findEstados() throws Exception;

    /**
     * Lista de estado.
     * @param first Paginación
     * @param pagesize Paginación
     * @return lista de estados
     * @throws Exception exepcion
     */
    List<Estado> findEstados(int first, int pagesize) throws Exception;

    /**
     * Devuelve un estado por su id.
     * @param id estado
     * @return estado
     * @throws Exception excepcion
     */
    Estado findEstadoById(String id) throws Exception;

    /**
     * Lista de municipios por estado.
     * @param estado estado
     * @return lista de municipios
     * @throws Exception exepcion
     */
    List<Municipio> findMunicipiosByEstado(String estado) throws Exception;

    /**
     * Devuelve un municipio por su id.
     * @param id estado y municipio
     * @return municipio
     * @throws Exception excepcion
     */
    Municipio findMunicipioById(MunicipioPK id) throws Exception;

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
     * Devuelve una poblacion por su id.
     * @param inegi inegi
     * @return poblacion poblacion
     * @throws Exception excepcion
     */
    Poblacion findPoblacionById(String inegi) throws Exception;

    /**
     * Recupera la lista de poblaciones de un abn.
     * @param pAbn Información del Abn.
     * @return List<Poblacion> poblaciones.
     */
    List<Poblacion> findAllPoblacionesByAbn(Abn pAbn);

    /**
     * Recupera la lista de poblaciones de un abn.
     * @param pAbn Información del Abn.
     * @param pUseCache Indica si se ha de almacenar la información en caché o no.
     * @return List<Poblacion> poblaciones.
     */
    List<Poblacion> findAllPoblacionesByAbn(Abn pAbn, boolean pUseCache);

    /**
     * Recupera la lista de municipios de un abn.
     * @param pAbn Información del Abn.
     * @return List<Municipio> municipios.
     */
    List<Municipio> findAllMunicipiosByAbn(Abn pAbn);

    /**
     * Recupera la lista de municipios de un abn.
     * @param pAbn Información del Abn.
     * @param pUseCache Indica si se ha de almacenar la información en caché o no.
     * @return List<Municipio> municipios.
     */
    List<Municipio> findAllMunicipiosByAbn(Abn pAbn, boolean pUseCache);

    /**
     * Método que devuelve todas las poblaciones a partir de los parámetros dados.
     * @param estado estado
     * @param municipio municipio
     * @param abn abn
     * @return List<Poblacion>
     * @throws Exception exception
     */
    List<Poblacion> findAllPoblacionByAbnUbicacion(String estado, String municipio, BigDecimal abn)
            throws Exception;

    /**
     * Método que devuelve la población a partir del abn y el inegi.
     * @param codigoAbn codigoAbn
     * @param inegi inegi
     * @return Poblacion
     */
    Poblacion getPoblacionByAbnInegi(BigDecimal codigoAbn, String inegi);

    /**
     * Guarda una lista de poblaciones nuevas.
     * @param listaPoblaciones Poblaciones
     * @throws Exception errr
     */
    void guardaPoblaciones(List<Poblacion> listaPoblaciones) throws Exception;

    /**
     * Método para guardar la población.
     * @param poblacion poblacion
     * @return Poblacion
     */
    Poblacion savePoblacion(Poblacion poblacion);

    /**
     * Método para guardar el estado.
     * @param estado estado
     * @return Estado
     */
    Estado saveEstado(Estado estado);

    /**
     * Numero Total de estados.
     * @return numero estados
     * @throws Exception exepcion
     */
    int findAllEstadosCount();

    /**
     * Método que devuelve cuantos hay.
     * @param codigo codigo
     * @return int
     */
    int findAllPoblacionesCount(BigDecimal codigo);

    /**
     * Método que devuelve cuantos hay.
     * @param codigo codigo
     * @return int
     */
    int findAllMunicipiosCount(BigDecimal codigo);

    /**
     * Método que devuelve la poblaciónAbn a partir del abn y el inegi.
     * @param codigoAbn codigoAbn
     * @param inegi inegi
     * @return PoblacionAbn
     */
    PoblacionAbn getPoblacionAbnByAbnInegi(BigDecimal codigoAbn, String inegi);

    /**
     * Método para guardar la poblaciónAbn.
     * @param poblacionAbn poblacionAbn
     * @return PoblacionAbn
     */
    PoblacionAbn savePoblacionAbn(PoblacionAbn poblacionAbn);

    /**
     * Busca las poblaciones con nombre como cadena.
     * @param cadena nombre a buscar
     * @return lista
     */
    List<Poblacion> findAllPoblacionesLikeNombre(String cadena);

    /**
     * Busca los nombres de poblaciones con nombre como cadena.
     * @param cadena nombre a buscar
     * @return String
     */
    List<String> findAllPoblacionesNameLikeNombre(String cadena);

    /**
     * Comprueba si el abn está asociado a la poblacion.
     * @param poblacion Poblacion
     * @param abn Abn
     * @return boolean
     */
    boolean existePoblacionEnAbn(Poblacion poblacion, Abn abn);

    /**
     * Actualiza la relación de poblaciones de un ABN.
     * @param pCodAbn Identificador del ABN.
     * @param pPoblacionesAbn Lista de poblaciones asociadas al ABN.
     */
    void updatePoblacionesAbn(BigDecimal pCodAbn, List<PoblacionAbn> pPoblacionesAbn);

    /**
     * @param codigoNir codigoNir
     * @return Abn abn.
     */
    Abn getAbnByCodigoNir(String codigoNir);

    /**
     * Obtiene la region por su id.
     * @param id region
     * @return region
     */
    Region getRegionById(BigDecimal id);

    /**
     * Obtiene la lista de Regiones.
     * @return lista region
     */
    List<Region> findAllRegiones();

    /**
     * Busca los municipios por filtro.
     * @param filtros filtros
     * @return lista municipios
     */
    List<Municipio> findAllMunicipios(FiltroBusquedaMunicipios filtros);

    /**
     * Recupera el listado de Poblaciones que cumplen con el filtro de búsqueda.
     * @param pFiltros FiltroBusquedaPoblaciones
     * @return List<Poblacion>
     */
    List<Poblacion> findAllPoblaciones(FiltroBusquedaPoblaciones pFiltros);

    /**
     * Busca las poblaciones por filtro.
     * @param filtros filtros
     * @return int
     */
    int findAllPoblacionesCount(FiltroBusquedaPoblaciones filtros);

    /**
     * Obtiene el número de municipios por filtro.
     * @param filtros filtro
     * @return total
     */
    Integer findAllMunicipiosCount(FiltroBusquedaMunicipios filtros);

    /**
     * Recupera un municipio a partir del estado y del municipio.
     * @param codEstado estado
     * @param codMunicipio municipio
     * @return boolean existe
     */
    boolean getMunicipioByEstado(String codEstado, String codMunicipio);

    /**
     * Guarda una lista de muncipios nuevos.
     * @param listaMunicipios Municipios
     * @throws Exception errr
     */
    void guardaMunicipios(List<Municipio> listaMunicipios) throws Exception;

    /**
     * Persiste un Municipio.
     * @param municipio municipio
     * @return municipio municipio
     */
    Municipio saveMunicipio(Municipio municipio);

    /**
     * Recupera el catálogo de estados de ABN.
     * @return List
     */
    List<EstadoAbn> findAllEstadosAbn();

    /**
     * Genera un Documento Excel con la información de los ABNs pasados como parámetro.
     * @param pFiltros Filtros de búsqueda seleccionados en el interfaz del catálogo de ABNs.
     * @return Documento Excel Serializado
     * @throws Exception En caso de error.
     */
    byte[] getExportConsultaCatalogoABNs(FiltroBusquedaABNs pFiltros) throws Exception;

    /**
     * Recupera un EstadoABN por su código.
     * @param pCodigo Código de EstadoABN
     * @return EstadoAbn
     */
    EstadoAbn getEstadoAbnByCodigo(String pCodigo);

    /**
     * Recupera el catálogo de áreas de numeración.
     * @return List
     * @throws Exception en caso de error
     */
    List<Abn> findAllAbns() throws Exception;

    /**
     * Recupera los ABNs que concuerdan con los fltros seleccionados.
     * @param pFiltros Filtros de Búsqueda.
     * @return List
     */
    List<Abn> findAllAbns(FiltroBusquedaABNs pFiltros);

    /**
     * Recupera el número de ABNs que concuerdan con los fltros seleccionados.
     * @param pFiltros Filtros de Búsqueda.
     * @return Número de ABNs que concuerdan con los fltros seleccionados.
     */
    int findAllAbnsCount(FiltroBusquedaABNs pFiltros);

    /**
     * Recupera un ABN por código.
     * @param pCodigo BigDecimal
     * @return Abn
     * @throws Exception en caso de error
     */
    Abn getAbnById(BigDecimal pCodigo) throws Exception;

    /**
     * Recupera la poblacion ancla de un ABN.
     * @param codigo abn
     * @return poblacion ancla
     * @throws Exception en caso de error
     */
    Poblacion getPoblacionAnclaByCodigoAbn(BigDecimal codigo) throws Exception;

    /**
     * Actualiza un abn.
     * @param abn abn
     * @return Abn
     * @throws Exception exception
     */
    Abn saveAbn(Abn abn) throws Exception;

    /**
     * Cambia el Código de un ABN actualizando todas las tablas que hacen referencia a ése ABN.
     * @param pAbn Información del ABN a modificar.
     * @param pNewCode Nuevo código de ABN.
     * @return Abn modificado.
     * @throws Exception en caso de error.
     */
    Abn changeAbnCode(Abn pAbn, BigDecimal pNewCode) throws Exception;

    /**
     * Recupera un nir.
     * @param id id del nir
     * @return el nir
     */
    Nir getNir(BigDecimal id);

    /**
     * Recupera un nir por su codigo.
     * @param cdgNir codigo nir
     * @return el nir
     */
    Nir getNirByCodigo(int cdgNir);

    /**
     * Devuelve la lista de nirs a partir del ABN.
     * @param numAbn numAbn
     * @return List<Nir>
     */
    List<Nir> findAllNirByAbn(BigDecimal numAbn);

    /**
     * Recupera la lista de NIRs cuyos primeros dígitos coinciden con el código dado.
     * @param pCodigoNir Parte del Código NIR
     * @return Lista de Nirs
     */
    List<Nir> findAllNirByParteCodigo(int pCodigoNir);

    /**
     * Guarda un Nir en la BD.
     * @param nir nir
     * @return Nir
     */
    Nir saveNir(Nir nir);

    /**
     * Elimina un municipio.
     * @param municipio Municipio
     */
    void removeMunicipio(Municipio municipio);

    /**
     * Lista de poblaciones por estado.
     * @param estado estado
     * @param codAbn codAbn
     * @return lista de poblaciones
     */
    List<Poblacion> findAllPoblacionesByEstadoAbn(String estado, BigDecimal codAbn);

    /**
     * Marca un Nir y todas sus Series como Desactivadas.
     * @param pNir Información del Nir
     * @throws Exception en caso de error.
     */
    void desactivarNir(Nir pNir) throws Exception;

    /**
     * Crea un nuevo Nir para un ABN existente con los SNA indicados.
     * @param pAbn Información del ABN
     * @param pCdgNir Código del Nuevo NIR.
     * @param pListaSnas Identificador de los SNA (nuevos o existentes)
     * @return Nuevo NIR.
     * @throws Exception En caso de error.
     */
    Nir createNir(Abn pAbn, int pCdgNir, List<String> pListaSnas) throws Exception;

    /**
     * Genera un excel de municipio con los filtros de busqueda.
     * @param listaDatos busqueda
     * @return fichero
     * @throws Exception error
     */
    byte[] generarListadoMunicipios(List<ExportMunicipio> listaDatos) throws Exception;

    /**
     * Genera la lista exportable de municipios con sus poblaciones.
     * @param filtros FiltroBusquedaMunicipios
     * @return lista datos
     */
    List<ExportMunicipio> findAllExportMunicipio(FiltroBusquedaMunicipios filtros);

    /**
     * Obtiene el total exportable de municipios con sus poblaciones.
     * @param filtros FiltroBusquedaMunicipios
     * @return total datos
     */
    Integer findAllExportMunicipioCount(FiltroBusquedaMunicipios filtros);

    /**
     * Devuelve el número de nirs.
     * @param abn abn
     * @return int
     * @throws Exception Exception
     */
    int findAllNirByAbnCount(BigDecimal abn);

    /**
     * Función que obtiene el listado de Nirs Activos.
     * @return List<Nir>
     * @throws Exception exception
     */
    List<Nir> findAllNirs() throws Exception;

    /**
     * Obtiene la poblacion por un inegi.
     * @param inegi inegi
     * @return Poblacion
     */
    Poblacion getPoblacionByInegi(String inegi);

    /**
     * Comprueba si exite un nir.
     * @param nir String
     * @return boolean
     */
    boolean existsNir(String nir);

    /**
     * Método que devuelve todos los Nirs de tres cifras que cuyas dos primeras cifras coinciden con el código pasado
     * por parámetros.
     * @param cdgNir cdgNir
     * @return List<Nir>
     */
    List<Nir> findNirsByDigitos(int cdgNir);

    /**
     * Devuelve el número de poblaciones de un estado.
     * @param estado String
     * @return String
     */
    String countAllPoblacionesByEstado(String estado);

    /**
     * Devuelve los ABN de un estado.
     * @param estado String
     * @return List<Abn>
     */
    List<Abn> findAbnInEstado(String estado);

    /**
     * Devuelve los ABN de un municipio.
     * @param municipio String
     * @param estado String
     * @return List<Abn>
     */
    List<Abn> findAbnInMunicipio(String municipio, String estado);

    /**
     * Obtiene la lista de Nir de un municipio.
     * @param municipio String
     * @param estado String
     * @return List<Nir>
     */
    List<Nir> finAllNirInMunicipio(String municipio, String estado);

    /**
     * Recupera la lista de PoblacionesAbn según los filtros dados.
     * @param pFiltros Condiciones de búsqueda.
     * @return List<PoblacionAbn> Lista de PoblacionesAbn.
     */
    List<PoblacionAbn> findAllPoblacionesAbn(FiltroBusquedaABNs pFiltros);

    /**
     * Recupera el número de PoblacionesAbn según los filtros dados.
     * @param pFiltros Condiciones de búsqueda.
     * @return List<PoblacionAbn> Lista de PoblacionesAbn.
     */
    int findAllPoblacionesAbnCount(FiltroBusquedaABNs pFiltros);

    /**
     * Recupera los ABNs que concuerdan con los fltros seleccionados.
     * @param pFiltros Filtros de Búsqueda.
     * @return List VCatalogoAbn
     */
    List<VCatalogoAbn> findAllAbnsForCatalog(FiltroBusquedaABNs pFiltros);

    /**
     * Genera un Documento Excel con la información de las poblaciones pasadas como parámetro.
     * @param pFiltros FiltroBusquedaPoblaciones
     * @return Documento Excel Serielizado
     * @throws Exception En caso de error.
     */
    byte[] getExportConsultaCatalogoPoblaciones(FiltroBusquedaPoblaciones pFiltros) throws Exception;

    /**
     * Método que devuelve todas las poblaciones a consolidar para un abnConsolidar.
     * @param pId id
     * @return List<PoblacionConsolidar>
     */
    List<PoblacionConsolidar> findPoblacionConsolidarById(BigDecimal pId);

    /**
     * Método que devuelve todas los Nirs a consolidar para un abnConsolidar.
     * @param pId id
     * @return List<NirConsolidar>
     */
    List<NirConsolidar> findNirConsolidarById(BigDecimal pId);

    /**
     * Método que devuelve el abnConsolidar dependiendo del Id de la Solicitud.
     * @param pIdSol id de la solicitud
     * @return AbnConsolidar
     */
    AbnConsolidar getAbnConsolidarByIdSol(BigDecimal pIdSol);

    /**
     * Listado de poblaciones por estado con numeración asignada.
     * @param estado Estado
     * @return List<Poblacion>
     */
    List<Poblacion> findAllPoblacionesEstadoNumeracion(Estado estado);

    /**
     * Método que devuelve los nirs asociados a una población.
     * @param poblacion Poblacion
     * @return List<Nir>
     */
    List<Nir> findAllNirByPoblacion(Poblacion poblacion);

    /***
     * Método que devuelve el abnConsolidar dependiendo del abn del rango serie.
     * @param rangoAbn Abn el Abn que se pretende buscar la fecha de consolidacion.
     * @return AbnConsolidar.
     */
    String getFechaConsolidacionByRangoAbn(Abn rangoAbn);

    /**
     * Obtiene el Abn de una poblacion.
     * @param poblacion Poblacion
     * @return Abn
     */
    Abn getAbnByPoblacion(Poblacion poblacion);

    /**
     * Busca las áreas de un estado.
     * @param estado Estado
     * @return List<EstadoArea>
     */
    List<EstadoArea> findAllAreaEstadoByEstado(Estado estado);

    /**
     * Obtiene la fecha de consolidación de una poblacion.
     * @param poblacion poblacion
     * @return fecha consolidacio
     */
    Date getFechaConsolidacionPoblacion(Poblacion poblacion);

    /**
     * Recupera los municipios vinculados al nir de un abn.
     * @param nir Nir
     * @param pUseCache boolean
     * @return List<Municipio>
     */
    List<Municipio> findAllMunicipiosByNir(Nir nir, boolean pUseCache);

    /**
     * Método que devuelve los nirs asociados a un estado.
     * @param estado Estado
     * @return List<Nir>
     */
    List<Nir> findAllNirByEstado(Estado estado);

    /**
     * Recupera una poblacion.
     * @param inegi Codigo de la Poblacion a consultar
     * @return Poblacion
     */
    VCatalogoPoblacion findPoblacion(String inegi);

    /**
     * Recupera el número de Municipios según los filtros dados.
     * @param pFiltros filtros
     * @return int
     */
    int findAllMunicipiosByAbnAndEstadoCount(FiltroBusquedaABNs pFiltros);

    /**
     * Recupera los municipios vinculados al abn y al estado.
     * @param pFiltros filtros
     * @return List<Municipio>
     */
    List<Municipio> findAllMunicipiosByAbnAndEstado(FiltroBusquedaABNs pFiltros);

    /**
     * Recupera la lista de poblaciones Abn de un abn.
     * @param pAbn Información del Abn.
     * @return List<Poblacion> poblaciones.
     */
    List<PoblacionAbn> findAllPoblacionAbnByAbn(Abn pAbn);

    Municipio findMunicipioByNombreAndEstado(String nombreMun, String nombreEst) throws Exception;

    List<Poblacion> findPoblacionByNombreAndMunicipioAndEstado(String nombrePob, Municipio mun);

}
