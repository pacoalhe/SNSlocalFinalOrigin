package mx.ift.sns.dao.ot;

import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.filtros.FiltroBusquedaMunicipios;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.MunicipioPK;

/**
 * Interfaz de definición de los métodos para base de datos para Municipios.
 */
public interface IMunicipioDao extends IBaseDAO<Municipio> {

    /**
     * Recupera un municipio a partir del estado y del municipio.
     * @param estado estado
     * @param municipio municipio
     * @return boolean existe
     */
    Boolean getMunicipioByEstado(String estado, String municipio);

    /**
     * Devuelve un municipio a partir del id.
     * @param id pk
     * @return municipio
     */
    Municipio getMunicipioById(MunicipioPK id);

    /**
     * Devuelve una lista de municipio.
     * @param estado estado
     * @return lista de municipios por estado
     */
    List<Municipio> findMunicipio(String estado);

    /**
     * Devuelve los municipio con un mismo codigo.
     * @param codigo del municipio
     * @return lista de municipios
     */
    List<Municipio> findAllMunicipiosByCode(String codigo);

    /**
     * Busca los municipios por filtro.
     * @param filtro FiltroBusquedaMunicipios
     * @return lista municipios
     */
    List<Municipio> findAllMunicipios(FiltroBusquedaMunicipios filtro);

    /**
     * Obtiene el número de municipios por filtro.
     * @param filtro filtro
     * @return total
     */
    Integer findAllMunicipiosCount(FiltroBusquedaMunicipios filtro);

    Municipio findMunicipioByNombreAndEstado(String nombreMun, String nombreEst) throws Exception;

    /**
     * FJAH 27.06.2025
     * @param idZona
     * @return
     */
    List<Object[]> findMunicipiosByZona(Integer idZona);

    /**
     * FJAH 27.06.2025
     * @param idZona zona geográfica
     * @return total de municipios únicos en esa zona
     */
    Long countMunicipiosByZona(Integer idZona);

}
