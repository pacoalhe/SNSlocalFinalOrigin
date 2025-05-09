package mx.ift.sns.dao.nng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.nng.SolicitudCesionNng;

/**
 * Interfaz DAO para Solicitud de Cesión de Numeración No Geográfica.
 * @author X53490DE
 */
public interface ISolicitudCesionNngDao extends IBaseDAO<SolicitudCesionNng> {

    /**
     * Recupera todas las solicitudes de Cesión NNG de Base de Datos.
     * @return List
     */
    List<SolicitudCesionNng> findAllSolicitudesCesion();

    /**
     * Recupera la lista de solicitudes de Cesión NNG en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return lista de solicitudes de cesión filtrada
     */
    List<SolicitudCesionNng> findAllSolicitudesCesion(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Recupera el número de solicitudes de Cesión NNG que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de cesión
     */
    int findAllSolicitudesCesionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Recupera una Solicitud de Cesión NNG existente por su identificador.
     * @param pConsecutivo Identificador de la solicitud de cesión nng.
     * @return SolicitudCesionNng
     */
    SolicitudCesionNng getSolicitudCesionById(BigDecimal pConsecutivo);

    /**
     * Recupera una Solicitud de Cesión NNG con todas sus dependencias cargadas: Lista de Cesiones Solicitadas y
     * Oficios.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudCesionNng
     */
    SolicitudCesionNng getSolicitudCesionEagerLoad(SolicitudCesionNng pSolicitud);

}
