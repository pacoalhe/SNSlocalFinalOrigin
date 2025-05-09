package mx.ift.sns.negocio.ng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroBusquedaLineasActivas;
import mx.ift.sns.modelo.filtros.FiltroReporteadorNG;
import mx.ift.sns.modelo.lineas.DetLineaActivaDet;
import mx.ift.sns.modelo.lineas.DetLineaArrendada;
import mx.ift.sns.modelo.lineas.DetLineaArrendatario;
import mx.ift.sns.modelo.lineas.DetalleLineaActiva;
import mx.ift.sns.modelo.lineas.DetalleReporte;
import mx.ift.sns.modelo.lineas.LineaActivaDet;
import mx.ift.sns.modelo.lineas.LineaArrendada;
import mx.ift.sns.modelo.lineas.LineaArrendatario;
import mx.ift.sns.modelo.lineas.Reporte;
import mx.ift.sns.modelo.lineas.ReporteLineasActivas;
import mx.ift.sns.modelo.lineas.TipoReporte;
import mx.ift.sns.modelo.ng.SolicitudLineasActivas;
import mx.ift.sns.modelo.pst.Proveedor;

/**
 * Interfaz del servicio de Reportes.
 * @author X36155QU
 */
public interface IReportesService {

    /**
     * Salva una Linea Activa.
     * @param lineaActiva lineaActiva
     * @return LineaActiva
     */
    ReporteLineasActivas saveLineaActiva(ReporteLineasActivas lineaActiva);

    /**
     * Salva una Linea Activa Detallada.
     * @param lineaActivaDet lineaActivaDet
     * @return lineaActivaDet
     */
    LineaActivaDet saveLineaActivaDet(LineaActivaDet lineaActivaDet);

    /**
     * Guarda una linea arrendatario.
     * @param lineaArrendatario lineaArrendatario
     * @return LineaArrendatario
     */
    LineaArrendatario saveLineaArrendatario(LineaArrendatario lineaArrendatario);

    /**
     * Guarda una linea arrendada.
     * @param lineaArrendada lineaArrendada
     * @return lineaArrendada
     */
    LineaArrendada saveLineaArrendada(LineaArrendada lineaArrendada);

    /**
     * Recupera la lista de solicitudes de lineas activas en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return lista de solicitudes de lineas activas filtrada
     */
    List<DetalleLineaActiva> findAllSolicitudesLineasActivas(FiltroBusquedaLineasActivas pFiltrosSolicitud);

    /**
     * Recupera el número de solicitudes de consolidación que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de consolidación
     */
    int findAllSolicitudesLineasActivasCount(FiltroBusquedaLineasActivas pFiltrosSolicitud);

    /**
     * Recupera la lista de solicitudes de lineas activas en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return lista de solicitudes de lineas activas filtrada
     */
    List<SolicitudLineasActivas> findAllSolicitudesLineasActivasConsulta(FiltroBusquedaLineasActivas pFiltrosSolicitud);

    /**
     * Recupera el número de solicitudes de consolidación que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de consolidación
     */
    int findAllSolicitudesLineasActivasConsultaCount(FiltroBusquedaLineasActivas pFiltrosSolicitud);

    /**
     * Obtiene el ultimo reporte de lineas activas de un proveedor.
     * @param proveedor pst
     * @return reporte lineas activas
     */
    ReporteLineasActivas getLastReporteLineasActiva(Proveedor proveedor);

    /**
     * Guardado masivo de los registro del reporte de lineas activas.
     * @param listaDatos registros
     * @param idReporte reporte
     */
    void saveDetalleLineaActiva(List<String> listaDatos, BigDecimal idReporte);

    /**
     * Obtiene todos los registros de un reporte de lineas activas cuyo total asignacion no coincide en el SNS.
     * @param reporte reporte
     * @return registros
     */
    List<String> findAllAvisoAsignacionDetalleLineaActiva(Reporte reporte);

    /**
     * Obtiene los registro de un reporte de lineas activas detallado.
     * @param idReporte reporte
     * @param first pagina
     * @param pageSize registros
     * @return lista detalles
     */
    List<DetLineaActivaDet> findAllDetLineaActivaDetByReporte(BigDecimal idReporte, int first, int pageSize);

    /**
     * Carga masiva de registros de un reporte de lineas activas detallada.
     * @param listaDatos registros
     * @param idReporte reporte
     */
    void saveDetLineaActivaDet(List<String> listaDatos, BigDecimal idReporte);

    /**
     * Comprueba el datos de total asignaciones de los registros de un reporte de lineas activas detallada.
     * @param reporte reporte
     * @return avisos
     */
    List<String> findAllAvisoAsignacionDetalleLineaActivaDet(Reporte reporte);

    /**
     * Carga masiva de registros de un reporte de lineas activas arrendatario.
     * @param listaDatos registros
     * @param idReporte reporte
     */
    void saveDetLineaArrendatario(List<String> listaDatos, BigDecimal idReporte);

    /**
     * Obtiene los registro de un reporte de lineas arrendatario.
     * @param idReporte reporte
     * @param first pagina
     * @param pageSize registros
     * @return lista detalles
     */
    List<DetLineaArrendatario> findAllDetLineaArrendatarioByReporte(BigDecimal idReporte, int first, int pageSize);

    /**
     * Carga masiva de registros de un reporte de lineas arredadas.
     * @param listaDatos registros
     * @param idReporte reporte
     */
    void saveDetLineaArrendada(List<String> listaDatos, BigDecimal idReporte);

    /**
     * Obtiene los registro de un reporte de lineas arrendadas.
     * @param idReporte reporte
     * @param first pagina
     * @param pageSize registros
     * @return lista detalles
     */
    List<DetLineaArrendada> findAllDetLineaArrendadaByReporte(BigDecimal idReporte, int first, int pageSize);

    /**
     * Consulta generica a la vista de detalle de reporte.
     * @param filtros FiltroBusquedaLineasActivas
     * @return lista detalles
     */
    List<DetalleReporte> findAllDetalleReporte(FiltroBusquedaLineasActivas filtros);

    /**
     * Obtiene el total de una consulta generica a la vista de detalle de reporte.
     * @param filtros FiltroBusquedaLineasActivas
     * @return total
     */
    Integer findAllDetalleReporteCount(FiltroBusquedaLineasActivas filtros);

    /**
     * Genera un fichero excel con los filtros de una cosulta de lineas activas.
     * @param filtro busqueda
     * @return fichero
     * @throws Exception error
     */
    byte[] getExportConsultaLineaActiva(FiltroBusquedaLineasActivas filtro) throws Exception;

    /**
     * Totales de lineas Activas.
     * @param filtro busqueda
     * @return totales
     */
    DetalleReporte findTotalesActivosDetalleReporte(FiltroReporteadorNG filtro);

    /**
     * Obtiene todos los tipo de reporte.
     * @return TipoReporte
     */
    List<TipoReporte> findAllTiposReporte();
}
