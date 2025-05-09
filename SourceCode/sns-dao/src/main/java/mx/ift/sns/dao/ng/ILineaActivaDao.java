package mx.ift.sns.dao.ng;

import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.filtros.FiltroBusquedaLineasActivas;
import mx.ift.sns.modelo.lineas.DetalleLineaActiva;
import mx.ift.sns.modelo.lineas.ReporteLineasActivas;
import mx.ift.sns.modelo.ng.SolicitudLineasActivas;
import mx.ift.sns.modelo.pst.Proveedor;

/**
 * Interfaz del DAO de Lineas Activas.
 * @author X36155QU
 */
public interface ILineaActivaDao extends IBaseDAO<ReporteLineasActivas> {

    /**
     * Recupera el número de solicitudes de liberación que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de liberación
     */
    int findAllSolicitudesLineasActivasCount(FiltroBusquedaLineasActivas pFiltrosSolicitud);

    /**
     * Recupera la lista de solicitudes de liberación en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return lista de solicitudes de liberación filtrada
     */
    List<DetalleLineaActiva> findAllSolicitudesLineasActivas(FiltroBusquedaLineasActivas pFiltrosSolicitud);

    /**
     * Recupera la lista de solicitudes de lineas activas en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return lista de solicitudes de lineas activas filtrada
     */
    List<SolicitudLineasActivas> findAllSolicitudesLineasActivasConsulta(FiltroBusquedaLineasActivas pFiltrosSolicitud);

    /**
     * Recupera el número de solicitudes de liberación que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de liberación
     */
    int findAllSolicitudesLineasActivasConsultaCount(FiltroBusquedaLineasActivas pFiltrosSolicitud);

    /**
     * Obtiene el ultimo reporte de lineas activas de un proveedor.
     * @param proveedor pst
     * @return reporte lineas activas
     */
    ReporteLineasActivas getLastReporteLineasActiva(Proveedor proveedor);
}
