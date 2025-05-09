package mx.ift.sns.dao.cpsn;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.cpsn.SolicitudAsignacionCpsn;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCpsn;

/**
 * Clase interfaz solicitudes asignación de códigos de señalización nacional.
 */
public interface ISolicitudAsignacionCpsnDao extends IBaseDAO<SolicitudAsignacionCpsn> {

    /**
     * Recupera la lista de Solicitudes de asignacion completa, sin filtros.
     * @return lista de solicitudes asignacion
     */
    List<SolicitudAsignacionCpsn> findAllSolicitudesAsignacion();

    /**
     * Recupera la lista de Asignación y Solicitud Asignación según filtros.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return lista de solicitudes de asignacion CPSN
     * @exception Exception exception
     */
    List<SolicitudAsignacionCpsn> findAllSolicitudesAsignacion(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud)
            throws Exception;

    /**
     * Recupera el número de solicitudes de asignación que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de asignación
     * @exception Exception exception
     */
    int findAllSolicitudesAsignacionCount(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud) throws Exception;

    /**
     * Recupera una solicitud de Asignación por su identificador.
     * @param pConsecutivo Identificador de la solicitud.
     * @return SolicitudAsignacionCpsn
     */
    SolicitudAsignacionCpsn getSolicitudAsignacionById(BigDecimal pConsecutivo);

    /**
     * Recupera una Solicitud de Asignacion con todas sus dependencias cargadas: Lista de Asignacion Solicitadas,
     * Aplicadas y Oficios.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudAsignacionCpsn
     */
    SolicitudAsignacionCpsn getSolicitudAsignacionEagerLoad(SolicitudAsignacionCpsn pSolicitud);
}
