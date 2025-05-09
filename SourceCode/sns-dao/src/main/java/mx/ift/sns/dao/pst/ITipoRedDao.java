package mx.ift.sns.dao.pst;

import java.util.List;

import mx.ift.sns.modelo.pst.TipoRed;

/**
 * Dao Tipo red.
 * @author 67059279
 */
public interface ITipoRedDao {

    /**
     * Método que devuelve los tipos de red.
     * @return List
     */
    List<TipoRed> findAllTiposRed();

    /**
     * Método que devuelve el tipo red.
     * @param idTipoRed String
     * @return TipoRed
     */
    TipoRed getTipoRedById(String idTipoRed);

    /**
     * Método que devuelve los tipos de red válidos (desconocido no se muestra).
     * @return List
     */
    List<TipoRed> findAllTiposRedValidos();

    /**
     * Método que devuelve los tipos de red válidos que soportaan el tipo de red especificado.
     * @param tipoRed TipoRed
     * @return List
     */
    List<TipoRed> findAllTiposRedValidos(TipoRed tipoRed);
}
