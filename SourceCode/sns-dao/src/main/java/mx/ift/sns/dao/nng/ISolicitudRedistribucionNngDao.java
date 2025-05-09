package mx.ift.sns.dao.nng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.nng.SolicitudRedistribucionNng;

/**
 * Interfaz DAO para Solicitud de Redistribución de Numeración No Geográfica.
 * @author X53490DE
 */
public interface ISolicitudRedistribucionNngDao extends IBaseDAO<SolicitudRedistribucionNng> {

    /**
     * Recupera la lista de Solicitudes completa, sin filtros.
     * @return lista de solicitudes
     */
    List<SolicitudRedistribucionNng> findAllSolicitudesRedistribucion();

    /**
     * Recupera la lista de solicitudes de redistribución en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de búsqueda
     * @return lista de solicitudes de redistribución filtrada
     */
    List<SolicitudRedistribucionNng> findAllSolicitudesRedistribucion(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Recupera el número de solicitudes de redistribucion que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de redistribucion
     */
    int findAllSolicitudesRedistribucionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Recupera una solicitud de redistribución por su identificador.
     * @param pConsecutivo Identificador de la solicitud.
     * @return SolicitudRedistribucionNng
     */
    SolicitudRedistribucionNng getSolicitudRedistribucionById(BigDecimal pConsecutivo);

    /**
     * Recupera una Solicitud de redistribución con todas sus dependencias cargadas: Lista de Redistribuciones
     * Solicitadas, Aplicadas y Oficios.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudRedistribucionNng
     */
    SolicitudRedistribucionNng getSolicitudRedistribucionEagerLoad(SolicitudRedistribucionNng pSolicitud);

}
