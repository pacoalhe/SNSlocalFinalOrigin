package mx.ift.sns.dao.ng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.ng.SolicitudCesionNg;

/**
 * Clase interfaz solicitudes de liberación.
 */
public interface ISolicitudCesionNgDao extends IBaseDAO<SolicitudCesionNg> {

    /**
     * Recupera la lista de Solicitudes de cesión completa, sin filtros.
     * @return lista de solicitudes de cesión
     */
    List<SolicitudCesionNg> findAllSolicitudesCesion();

    /**
     * Recupera la lista de solicitudes de cesión en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return lista de solicitudes de cesión filtrada
     */
    List<SolicitudCesionNg> findAllSolicitudesCesion(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Recupera el número de solicitudes de cesión que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de cesión
     */
    int findAllSolicitudesCesionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Recupera una solicitud de cesión por su identificador.
     * @param pConsecutivo Identificador de la solicitud.
     * @return SolicitudCesionNg
     */
    SolicitudCesionNg getSolicitudCesionById(BigDecimal pConsecutivo);

    /**
     * Recupera una Solicitud de Cesión con todas sus dependencias cargadas: Lista de Cesiones Solicitadas, Aplicadas y
     * Oficios.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudCesionNg
     */
    SolicitudCesionNg getSolicitudCesionEagerLoad(SolicitudCesionNg pSolicitud);

}
