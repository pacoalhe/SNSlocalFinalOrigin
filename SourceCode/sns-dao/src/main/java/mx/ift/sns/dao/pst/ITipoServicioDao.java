package mx.ift.sns.dao.pst;

import java.util.List;

import mx.ift.sns.modelo.pst.TipoServicio;

/**
 * Interfaz de definición de los métodos para base de datos de Tipos de Servicio.
 */
public interface ITipoServicioDao {

    /**
     * Recupera el catálogo de tipos de servicio.
     * @return List
     */
    List<TipoServicio> findAllTiposServicio();

    /**
     * Médotodo que obtiene el tipo de servicio a través de su id.
     * @param id String
     * @return TipoServicio
     * @throws Exception ex
     */
    TipoServicio getTipoServicioById(String id) throws Exception;

}
