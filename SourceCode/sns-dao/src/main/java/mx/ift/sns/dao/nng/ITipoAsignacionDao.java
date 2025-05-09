package mx.ift.sns.dao.nng;

import java.util.List;

import mx.ift.sns.modelo.nng.TipoAsignacion;

/**
 * Interfaz del DAO de tipo de asignacion.
 * @author X36155QU
 */
public interface ITipoAsignacionDao {

    /**
     * Obtiene el tipo de asignacion por codigo.
     * @param cdg codigo
     * @return tipo asignacion
     */
    TipoAsignacion getTipoAsignacionById(String cdg);

    /**
     * Obtiene los tipo de asignacion.
     * @return tipos
     */
    List<TipoAsignacion> findAllTipoAsignacion();

}
