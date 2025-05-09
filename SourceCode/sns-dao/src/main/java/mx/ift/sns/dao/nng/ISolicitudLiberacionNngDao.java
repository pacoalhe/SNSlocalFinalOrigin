package mx.ift.sns.dao.nng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.nng.SolicitudLiberacionNng;

/**
 * Interfaz DAO para Solicitud de Liberación de Numeración No Geográfica.
 * @author X53490DE
 */
public interface ISolicitudLiberacionNngDao extends IBaseDAO<SolicitudLiberacionNng> {

    /**
     * Recupera la lista de Solicitudes de liberación completa, sin filtros.
     * @return lista de solicitudes de liberación
     */
    List<SolicitudLiberacionNng> findAllSolicitudesLiberacion();

    /**
     * Recupera la lista de solicitudes de liberación en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return lista de solicitudes de liberación filtrada
     */
    List<SolicitudLiberacionNng> findAllSolicitudesLiberacion(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Recupera el número de solicitudes de liberación que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de liberación
     */
    int findAllSolicitudesLiberacionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Recupera una solicitud de liberación por su identificador.
     * @param pConsecutivo Identificador de la solicitud.
     * @return SolicitudLiberacionNng
     */
    SolicitudLiberacionNng getSolicitudLiberacionById(BigDecimal pConsecutivo);

    /**
     * Recupera una Solicitud de Liberacion con todas sus dependencias cargadas: Lista de Liberaciones Solicitadas y
     * Oficios.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudLiberacionNg
     */
    SolicitudLiberacionNng getSolicitudLiberacionEagerLoad(SolicitudLiberacionNng pSolicitud);

}
