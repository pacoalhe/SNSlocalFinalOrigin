package mx.ift.sns.dao.pst;

import java.util.List;

import mx.ift.sns.modelo.pst.TipoContacto;

/**
 * Interfaz de definición de los métodos para base de datos de Tipos de Contacto.
 */
public interface ITipoContactoDao {

    /**
     * Recupera el catálogo de tipos de contacto.
     * @return List
     */
    List<TipoContacto> findAllTiposContacto();

    /**
     * Obtiene el tipo de contacto por el cdg.
     * @param pCdgTipo String
     * @return TipoContacto
     */
    TipoContacto getTipoContactoByCdg(String pCdgTipo);
}
