package mx.ift.sns.dao.ng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.ng.SolicitudAsignacion;

/**
 * Clase interfaz solicitudes asignación.
 * @author 67059279
 */
public interface ISolicitudAsignacionDao extends IBaseDAO<SolicitudAsignacion> {

    /** Recupera la lista de Solicitudes de asignacion completa, sin filtros. */

    /**
     * Recupera la lista de Solicitudes de asignacion completa, sin filtros.
     * @return lista de solicitudes asignacion
     */
    List<SolicitudAsignacion> findAllSolicitudesAsignacion();

    /**
     * Recupera la lista de Asignación y Solicitud Asignación según filtros.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return lista de solicitudes de asignacion
     * @exception Exception exception
     */
    List<SolicitudAsignacion> findAllSolicitudesAsignacion(FiltroBusquedaSolicitudes pFiltrosSolicitud)
            throws Exception;

    /**
     * Recupera el número de solicitudes de asignación que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de asignación
     * @exception Exception exception
     */
    int findAllSolicitudesAsignacionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) throws Exception;

    /**
     * Recupera una solicitud de Asignación por su identificador.
     * @param pConsecutivo Identificador de la solicitud.
     * @return SolicitudAsignacion
     */
    SolicitudAsignacion getSolicitudAsignacionById(BigDecimal pConsecutivo);

}
