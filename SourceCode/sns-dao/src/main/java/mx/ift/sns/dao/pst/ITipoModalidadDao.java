package mx.ift.sns.dao.pst;

import java.util.List;

import mx.ift.sns.modelo.pst.TipoModalidad;

/**
 * Dao Tipo modalidad.
 * @author 67059279
 */
public interface ITipoModalidadDao {

    /**
     * Método que devuelve los tipos modalidad.
     * @return List
     */
    List<TipoModalidad> findAllTiposModalidad();

    /**
     * Método que devuelve el tipo modalidad.
     * @param idtipoModalidad String
     * @return TipoModalidad
     */
    TipoModalidad getTipoModalidadById(String idtipoModalidad);
}
