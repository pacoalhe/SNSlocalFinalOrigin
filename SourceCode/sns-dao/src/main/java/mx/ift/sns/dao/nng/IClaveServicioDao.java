package mx.ift.sns.dao.nng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.filtros.FiltroBusquedaClaveServicio;
import mx.ift.sns.modelo.nng.ClaveServicio;

/**
 * Interfaz DAO ClaveServicio.
 * @author X36155QU
 */
public interface IClaveServicioDao extends IBaseDAO<ClaveServicio> {

    /**
     * Obtiene todas las claves de servicio estean activas o inactivas.
     * @return List<ClaveServicio>
     */
    List<ClaveServicio> findAllClaveServicio();

    /**
     * Recupera una Clave de Servicio por su código.
     * @param pCodigo Código de Clave de Servicio.
     * @return ClaveServicio
     */
    ClaveServicio getClaveServicioByCodigo(BigDecimal pCodigo);

    /**
     * Recupera el listado de Claves de Servicio que cumplen con el filtro de búsqueda.
     * @param pFiltros FiltroBusquedaClaveServicio
     * @return List<ClaveServicio>
     * @throws Exception ex
     */
    List<ClaveServicio> findAllClaveServicio(FiltroBusquedaClaveServicio pFiltros) throws Exception;

    /**
     * Obtiene el número de claves de servicio que cumplen los filtros de búsqueda.
     * @param pFiltros FiltroBusquedaClaveServicio
     * @return int
     * @throws Exception ex
     */
    int findAllClaveServicioCount(FiltroBusquedaClaveServicio pFiltros) throws Exception;

    /**
     * Busca todas las claves de servicio activas.
     * @return List<ClaveServicio>
     */
    List<ClaveServicio> findAllClaveServicioActivas();

    /**
     * Indica si existe una clave.
     * @param clave a buscar
     * @return true si existe
     */
    boolean exists(String clave);

    /**
     * Busca todas las claves de servicio activas en la web.
     * @return List<ClaveServicio>
     */
    List<ClaveServicio> findAllClaveServicioActivasWeb();
}
