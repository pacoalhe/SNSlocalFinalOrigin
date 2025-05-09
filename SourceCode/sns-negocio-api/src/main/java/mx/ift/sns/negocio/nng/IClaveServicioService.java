package mx.ift.sns.negocio.nng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroBusquedaClaveServicio;
import mx.ift.sns.modelo.nng.ClaveServicio;

/**
 * Interfaz para los servicios de la clave de servicio.
 * @author X23016PE
 */
public interface IClaveServicioService {
    /**
     * Obtiene todas las claves de servicio estean activas o inactivas.
     * @return List<ClaveServicio>
     */
    List<ClaveServicio> findAllClaveServicio();

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
     * Función que guarda la clave de servicio.
     * @param claveServicio ClaveServicio
     * @param modoEdicion boolean
     * @return claveServicio ClaveServicio
     */
    ClaveServicio guardarClaveServicio(ClaveServicio claveServicio, boolean modoEdicion);

    /**
     * Función que valida que los datos a guardar de la clave de servicio.
     * @param claveServicio ClaveServicio
     * @param modoEdicion boolean
     * @return claveServicio ClaveServicio
     */
    ClaveServicio validaClaveServicio(ClaveServicio claveServicio, boolean modoEdicion);

    /**
     * Busca todas las claves de servicio activas.
     * @return List<ClaveServicio>
     */
    List<ClaveServicio> findAllClaveServicioActivas();

    /**
     * Obtiene la clave servicio por su codigo.
     * @param codigo codigo
     * @return clave servicio
     */
    ClaveServicio getClaveServicioByCodigo(BigDecimal codigo);

    /**
     * Busca todas las claves de servicio activas en la web.
     * @return List<ClaveServicio>
     */
    List<ClaveServicio> findAllClaveServicioActivasWeb();
}
