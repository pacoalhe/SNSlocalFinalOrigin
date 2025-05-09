package mx.ift.sns.dao.solicitud;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.ng.Serie;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;

/**
 * Clase interfaz solicitudes.
 */
public interface ISolicitudDao {

    /**
     * Recupera la lista de solicitudes en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return List<Solicitud>
     */
    List<Solicitud> findAllSolicitudes(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Recupera el número de solicitudes que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de liberación
     */
    int findAllSolicitudesCount(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    // /**
    // * Recupera una Solicitud de Consolidacion con todas sus dependencias cargadas.
    // * @param pSolicitud Solicitud a cargar
    // * @return SolicitudLiberacionNg
    // */
    // Solicitud getSolicitudEagerLoad(Solicitud pSolicitud);

    /**
     * Recupera el tipo de solicitud al que pertenece la solicitud con el consecutivo indicado.
     * @param pConsecutivo Identificador de la solicitud.
     * @return TipoSolicitud
     */
    TipoSolicitud getTipoSolicitudById(BigDecimal pConsecutivo);

    /**
     * Recupera la lista de solicitudes NNG en función de los filtros dados.
     * @param pFiltrosSolicitud filtros
     * @return List<Solicitud>
     */
    List<Solicitud> findAllSolicitudesNng(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Recupera el número de solicitudes NNG que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de liberación
     */
    int findAllSolicitudesNngCount(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Comprueba si una solictud tiene rangos.
     * @param solicitud solictud
     * @return true/false
     */
    boolean isSolicitudWithRangos(Solicitud solicitud);

    /**
     * Compueba si existen solicitudes en trámite para el nir dado.
     * @param idNir Nir
     * @return boolean
     */

    boolean isSolicitudPendieteByNir(BigDecimal idNir);
}
