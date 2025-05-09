package mx.ift.sns.dao.cpsn;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.cpsn.SolicitudCesionCPSN;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCpsn;
import mx.ift.sns.modelo.solicitud.EstadoCesionSolicitada;

/**
 * Clase interfaz solicitudes de cesion cpsn.
 */
public interface ISolicitudCesionCPSNDao extends IBaseDAO<SolicitudCesionCPSN> {

    /**
     * Recupera la lista de Solicitudes de cesión de cpsn completa, sin filtros.
     * @return lista de solicitudes de cesión
     */
    List<SolicitudCesionCPSN> findAllSolicitudesCesionCPSN();

    /**
     * Recupera la lista de solicitudes de cesión cpsn en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return lista de solicitudes de cesión filtrada
     */
    List<SolicitudCesionCPSN> findAllSolicitudesCesionCPSN(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud);

    /**
     * Recupera el número de solicitudes de cesión que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de cesión
     */
    int findAllSolicitudesCesionCPSNCount(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud);

    /**
     * Recupera una solicitud de cesión cpsn por su identificador.
     * @param pConsecutivo Identificador de la solicitud.
     * @return SolicitudCesionCPSN
     */
    SolicitudCesionCPSN getSolicitudCesionCPSNById(BigDecimal pConsecutivo);

    /**
     * Recupera una Solicitud de Cesión con todas sus dependencias cargadas: Lista de Cesiones Solicitadas, Aplicadas y
     * Oficios.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudCesionCPSN
     */
    SolicitudCesionCPSN getSolicitudCesionCPSNEagerLoad(SolicitudCesionCPSN pSolicitud);

    /**
     * Método que comprueba si un cpsn está en un trámite de cesion pendiente.
     * @param cpsnMin a validar
     * @param cpsnMax a validar
     * @param estatus de la cesion solicitada
     * @param fecha de implementación
     * @return boolean
     */
    boolean cpsnEnCesionPendiente(Integer cpsnMin, Integer cpsnMax, EstadoCesionSolicitada estatus, Date fecha);
}
