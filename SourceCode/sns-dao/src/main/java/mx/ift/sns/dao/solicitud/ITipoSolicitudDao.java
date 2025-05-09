package mx.ift.sns.dao.solicitud;

import java.util.List;

import mx.ift.sns.modelo.solicitud.TipoSolicitud;

/**
 * Interfaz de definición de los métodos para base de datos de Tipos de Solicitud.
 */
public interface ITipoSolicitudDao {

    /**
     * Recupera el catálogo de tipos de solicitud NG.
     * @return List
     */
    List<TipoSolicitud> findAllTiposSolicitudNG();

    /**
     * Recupera el catálogo de tipos de solicitud NNG.
     * @return List
     */
    List<TipoSolicitud> findAllTiposSolicitudNNG();

    /**
     * Recupera el catálogo de tipos de solicitud CPSN.
     * @return List
     */
    List<TipoSolicitud> findAllTiposSolicitudCpsn();

    /**
     * Recupera el catálogo de tipos de solicitud CPSI.
     * @return List
     */
    List<TipoSolicitud> findAllTiposSolicitudCpsi();

    /**
     * Método que devuelve el tipo solicitud.
     * @param idTipoSolicitud idTipoSolicitud
     * @return TipoSolicitud
     */
    TipoSolicitud getTipoSolicitudById(Integer idTipoSolicitud);
}
