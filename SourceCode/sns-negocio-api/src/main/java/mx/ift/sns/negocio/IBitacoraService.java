package mx.ift.sns.negocio;

import java.util.List;

import mx.ift.sns.modelo.bitacora.Bitacora;
import mx.ift.sns.modelo.filtros.FiltroBusquedaBitacoraLog;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.modelo.usu.Usuario;

/**
 * Interfaz del Servicio de Bitacora.
 */
public interface IBitacoraService {

    /**
     * Devuelve los registros indicados de la bitacora.
     * @param filtro de busqueda
     * @return lista
     */
    List<Bitacora> findBitacora(FiltroBusquedaBitacoraLog filtro);

    /**
     * Devuelve el numero de registros de la bitacora.
     * @param filtro de busqueda
     * @return numero
     */
    int findBitacoraCount(FiltroBusquedaBitacoraLog filtro);

    /**
     * Almacena un msg en bitacora.
     * @param msg mensaje
     */
    void add(String msg);

    /**
     * Graba el registro indicado de la bitacora.
     * @param usuario usuario
     * @param msg mensaje
     */
    void add(Usuario usuario, String msg);

    /**
     * Graba el registro indicado de la bitacora. No esta implementado.
     * @param sol solicitud
     * @param msg mensaje
     */
    void add(Solicitud sol, String msg);
}
