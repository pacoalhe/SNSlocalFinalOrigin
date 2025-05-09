package mx.ift.sns.dao.cpsn;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.cpsn.SolicitudLiberacionCpsn;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCpsn;
import mx.ift.sns.modelo.solicitud.EstadoLiberacionSolicitada;

/**
 * Interfaz DAO para Solicitud de Liberación de CPSN.
 * @author X53490DE
 */
public interface ISolicitudLiberacionCpsnDao extends IBaseDAO<SolicitudLiberacionCpsn> {

    /**
     * Recupera la lista de Solicitudes de liberación completa, sin filtros.
     * @return lista de solicitudes de liberación
     */
    List<SolicitudLiberacionCpsn> findAllSolicitudesLiberacion();

    /**
     * Recupera la lista de solicitudes de liberación en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return lista de solicitudes de liberación filtrada
     */
    List<SolicitudLiberacionCpsn> findAllSolicitudesLiberacion(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud);

    /**
     * Recupera el número de solicitudes de liberación que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de liberación
     */
    int findAllSolicitudesLiberacionCount(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud);

    /**
     * Recupera una solicitud de liberación por su identificador.
     * @param pConsecutivo Identificador de la solicitud.
     * @return SolicitudLiberacionCpsn
     */
    SolicitudLiberacionCpsn getSolicitudLiberacionById(BigDecimal pConsecutivo);

    /**
     * Recupera una Solicitud de Liberacion con todas sus dependencias cargadas.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudLiberacionCpsn
     */
    SolicitudLiberacionCpsn getSolicitudLiberacionEagerLoad(SolicitudLiberacionCpsn pSolicitud);

    /**
     * Método que valida si el cpsn está en alguna liberación pendiente de ejecutarse.
     * @param cpsnMin a validar
     * @param cpsnMax a validar
     * @param estatusP del cpsn pendiente
     * @param estatusR del cpsn en periodo de reserva
     * @param fecha de implementación
     * @return booleano si existe
     */
    boolean cpsnEnLiberacionPendiente(Integer cpsnMin, Integer cpsnMax, EstadoLiberacionSolicitada estatusP,
            EstadoLiberacionSolicitada estatusR, Date fecha);
}
