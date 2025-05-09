package mx.ift.sns.dao.nng;

import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.nng.SolicitudAsignacionNng;

/**
 * Interfaz del DAO de SolicitudAsignacionNng.
 * @author X36155QU
 */
public interface ISolicitudAsignacionNngDao extends IBaseDAO<SolicitudAsignacionNng> {

    /**
     * Obtiene todas las solicitudes de asignacion no geagraficas.
     * @return lista solicitudes
     */
    List<SolicitudAsignacionNng> findAllSolicitudesAsignacion();

    /**
     * Obtiene las solicitudes de asignacion no geagraficas.
     * @param filtros busqueda
     * @return lista solicitudes
     * @throws Exception error
     */
    List<SolicitudAsignacionNng> findAllSolicitudesAsignacion(FiltroBusquedaSolicitudes filtros) throws Exception;

    /**
     * Obiente el numero de solicitudes de asignacion no geagraficas.
     * @param filtros busqueda
     * @return total
     * @throws Exception error
     */
    Integer findAllSolicitudesAsignacionCount(FiltroBusquedaSolicitudes filtros) throws Exception;

    /**
     * Recupera una Solicitud de Asignacion NNG con todas sus dependencias cargadas: Lista de Numeraciones Solicitadas,
     * Rangos Asignados y Oficios.
     * @param pSolicitud solicitud
     * @return solicitud
     */
    SolicitudAsignacionNng getSolicitudAsignacionEagerLoad(SolicitudAsignacionNng pSolicitud);

}
