package mx.ift.sns.dao.ng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.ng.SolicitudConsolidacion;

/**
 * Clase interfaz solicitudes de consolidación.
 */
public interface ISolicitudConsolidacionDao extends IBaseDAO<SolicitudConsolidacion> {

    /**
     * Recupera la lista de consolidaciones de abn´s completa, sin filtros.
     * @return lista de SolicitudConsolidacion
     */
    List<SolicitudConsolidacion> findAllSolicitudesConsolidacion();

    /**
     * Recupera la lista de consolidaciones de abn´s en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return lista de SolicitudConsolidacion filtrada
     */
    List<SolicitudConsolidacion> findAllSolicitudesConsolidacion(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Recupera el número de solicitudes de consolidacion que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de liberación
     */
    int findAllSolicitudesConsolidacionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Recupera una Solicitud de Consolidacion con todas sus dependencias cargadas.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudLiberacionNg
     */
    SolicitudConsolidacion getSolicitudConsolidacionEagerLoad(SolicitudConsolidacion pSolicitud);

    /**
     * Recupera una solicitud de consolidación por su identificador.
     * @param pConsecutivo Identificador de la solicitud.
     * @return SolicitudConsolidacion
     */
    SolicitudConsolidacion getSolicitudConsolidacionById(BigDecimal pConsecutivo);
}
