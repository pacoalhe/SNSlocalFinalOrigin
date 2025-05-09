package mx.ift.sns.dao.solicitud;

import java.util.List;

import mx.ift.sns.modelo.solicitud.EstadoSolicitud;

/**
 * Interfaz de definición de los métodos para base de datos de estados de solicitud.
 */
public interface IEstadoSolicitudDao {

    /**
     * Recupera el catálogo de estados de solicitud.
     * @return List
     */
    List<EstadoSolicitud> findAllEstadosSolicitud();

    /**
     * Recupera un estado de solicitud por su identificador.
     * @param pIdSolicitud String
     * @return EstadoSolicitud
     */
    EstadoSolicitud getEstadoSolicitudById(String pIdSolicitud);

}
