package mx.ift.sns.dao.ot;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.abn.PoblacionAbn;
import mx.ift.sns.modelo.filtros.FiltroBusquedaMunicipios;
import mx.ift.sns.modelo.filtros.FiltroBusquedaPoblaciones;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.ot.VCatalogoPoblacion;

/**
 * Interfaz del DAO poblacion.
 * @author 67059279
 */
public interface IPoblacionDao extends IBaseDAO<Poblacion> {

    /**
     * Recupera una poblacion a partir del estado, municipio y poblacion.
     * @param inegi inegi
     * @return Poblacion
     */
    Poblacion getPoblacionByInegi(String inegi);

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
     * Método que devuelve el listado de poblaciones a partir de los parámetros dados.
     * @param estado estado
     * @param municipio municipio
     * @param abn abn
     * @return List<Poblacion>
     */
    List<Poblacion> findAllPoblacionByAbnUbicacion(String estado, String municipio, BigDecimal abn);

    /**
     * Obtiene una poblacion por su nombre.
     * @param name nombre
     * @return Poblacion
     */
    Poblacion getPoblacionByName(String name);

    /**
     * Método que devuelve una población a partir del abn y del codigo inegi.
     * @param codigoAbn codigoAbn
     * @param inegi inegi
     * @return Poblacion
     */
    Poblacion getPoblacionByAbnInegi(BigDecimal codigoAbn, String inegi);

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
     * Obtiene la poblacionAbn asociada a un inegi y un abn.
     * @param codigoAbn BigDecimal
     * @param inegi String
     * @return PoblacionAbn
     */
    PoblacionAbn getPoblacionAbnByAbnInegi(BigDecimal codigoAbn, String inegi);

    /**
     * Comprueba si existe una poblacion por su nombre.
     * @param nombre nombre
     * @return true/false
     */
    boolean existPoblacion(String nombre);

    /**
     * Busca las poblaciones con nombre como cadena (en MAYUSCULAS).
     * @param cadena nombre a buscar
     * @return lista
     */
    List<Poblacion> findAllPoblacionesLikeNombre(String cadena);

    /**
     * Busca los nombres de las poblaciones con nombre como cadena (en MAYUSCULAS).
     * @param cadena nombre a buscar
     * @return lista de nombres
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
     * Recupera el listado de Poblaciones que cumplen con el filtro de búsqueda.
     * @param pFiltros FiltroBusquedaPoblaciones
     * @return List<Poblacion>
     */
    List<Poblacion> findAllPoblaciones(FiltroBusquedaPoblaciones pFiltros);

    /**
     * Obtiene el número de Poblaciones que cumplen los filtros de búsqueda.
     * @param pFiltros FiltroBusquedaPoblaciones
     * @return int
     */
    int findAllPoblacionesCount(FiltroBusquedaPoblaciones pFiltros);

    /**
     * Recupera el listado de Poblaciones con el estado dado por parámetros.
     * @param estado estado
     * @param codAbn codAbn
     * @return List<Poblacion>
     */
    List<Poblacion> findAllPoblacionesByEstadoAbn(String estado, BigDecimal codAbn);

    /**
     * Obtiene todas las poblaciones pertenecientes a los muncipios que se obtienen por un filtro de busqueda de
     * municipios.
     * @param filtros FiltroBusquedaMunicipios
     * @return List<Poblacion>
     */
    List<Poblacion> findAllPoblacionesFilterMunicipio(FiltroBusquedaMunicipios filtros);

    /**
     * Obtiene el número de poblaciones de un estado.
     * @param estado String
     * @return String numeroPob
     */
    String countAllPoblacionesByEstado(String estado);

    /**
     * Comprueba que existe un codigo inegi.
     * @param inegi inegi
     * @return boolean
     */
    boolean existePoblacion(String inegi);

    /**
     * Combrueba que exista alguna poblabacion dado un abn.
     * @param inegi inegi
     * @param abn abn
     * @return boolean
     */
    boolean existPoblacionWithAbn(String inegi, String abn);

    /**
     * Método que se encarga de buscar todas las poblaciones a exportar.
     * @param pFiltros filtros
     * @return List<VCatalogoPoblacion> listado
     */
    List<VCatalogoPoblacion> findAllCatalogoPoblacion(FiltroBusquedaPoblaciones pFiltros);

    /**
     * Listado de poblaciones por estado con numeración asignada.
     * @param estado Estado
     * @return List<Poblacion>
     */
    List<Poblacion> findAllPoblacionesEstadoNumeracion(Estado estado);

    /**
     * Método que devuelve una población a partir del codigo inegi.
     * @param inegi inegi
     * @return VCatalogoPoblacion
     */
    VCatalogoPoblacion findPoblacion(String inegi);

    List<Poblacion> findPoblacionByNombreAndMunicipioAndEstado(String nombrePob, String codMun);

}
