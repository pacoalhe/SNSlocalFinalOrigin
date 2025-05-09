package mx.ift.sns.dao.bitacora;

import java.util.List;

import mx.ift.sns.modelo.bitacora.Bitacora;
import mx.ift.sns.modelo.filtros.FiltroBusquedaBitacoraLog;

/**
 * Interfaz del DAO de Bitacora.
 */
public interface IBitacoraDAO {

    /**
     * Graba el usuario en BBDD.
     * @param bitacora mensajea grabar
     * @return bitacora
     */
    Bitacora save(Bitacora bitacora);

    /**
     * Devuelve el numero de registros indicados.
     * @param filtro de busqueda
     * @return lista
     */
    List<Bitacora> findBitacora(FiltroBusquedaBitacoraLog filtro);

    /**
     * Devuelve el numero total de registros.
     * @param filtro de busqueda
     * @return numero de registros
     */
    int findBitacoraCount(FiltroBusquedaBitacoraLog filtro);
}
