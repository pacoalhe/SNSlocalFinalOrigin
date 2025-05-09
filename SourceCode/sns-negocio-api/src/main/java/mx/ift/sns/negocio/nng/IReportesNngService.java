package mx.ift.sns.negocio.nng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroBusquedaLineasActivas;
import mx.ift.sns.modelo.lineas.DetalleLineaArrendadorNng;
import mx.ift.sns.modelo.lineas.DetalleLineaArrendatarioNng;
import mx.ift.sns.modelo.lineas.DetalleReporteNng;
import mx.ift.sns.modelo.lineas.ReporteLineaActivaDetNng;
import mx.ift.sns.modelo.lineas.ReporteLineaActivaNng;
import mx.ift.sns.modelo.lineas.ReporteLineaArrendadorNng;
import mx.ift.sns.modelo.lineas.ReporteLineaArrendatarioNng;
import mx.ift.sns.modelo.lineas.TipoReporte;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.pst.Proveedor;

/**
 * Interfaz del servicio de reportes NNG.
 * @author X36155QU
 */
public interface IReportesNngService {

    /**
     * Obtiene todos los tipo de reporte.
     * @return TipoReporte
     */
    List<TipoReporte> findAllTiposReporte();

    /**
     * Guarda un reporte de linea activas de arrendatario.
     * @param reporte reporte
     * @return reporte
     */
    ReporteLineaArrendatarioNng saveLineaArrendatario(ReporteLineaArrendatarioNng reporte);

    /**
     * Guarda los detalles del reporte de lineas del arrendatario.
     * @param listaDatos detalles
     * @param id reporte
     */
    void saveDetLineaArrendatario(List<String> listaDatos, BigDecimal id);

    /**
     * Guarda un reporte de linea activas de arrendador.
     * @param reporte reporte
     * @return reporte
     */
    ReporteLineaArrendadorNng saveLineaArrendada(ReporteLineaArrendadorNng reporte);

    /**
     * Guarda los detalles del reporte de lineas del arrendador.
     * @param listaDatos detalles
     * @param id reporte
     */
    void saveDetLineaArrendada(List<String> listaDatos, BigDecimal id);

    /**
     * Guarda un reporte de linea activas detallada.
     * @param reporte reporte
     * @return reporte
     */
    ReporteLineaActivaDetNng saveLineaActivaDet(ReporteLineaActivaDetNng reporte);

    /**
     * Guarda los detalles del reporte de lineas activas detallada.
     * @param listaDatos detalles
     * @param id reporte
     */
    void saveDetLineaActivaDet(List<String> listaDatos, BigDecimal id);

    /**
     * Guarda un reporte de linea activas.
     * @param lineaActiva reporte
     * @return reporte
     */
    ReporteLineaActivaNng saveLineaActiva(ReporteLineaActivaNng lineaActiva);

    /**
     * Guarda los detalles del reporte de lineas activas.
     * @param listaDatos detalles
     * @param id reporte
     */
    void saveDetLineaActiva(List<String> listaDatos, BigDecimal id);

    /**
     * Obtiene los detalles de los reportes de lineas activas.
     * @param filtro busqueda
     * @return lista detalles
     */
    List<DetalleReporteNng> findAllDetalleReporte(FiltroBusquedaLineasActivas filtro);

    /**
     * Obtiene el total de detalles de los reportes de lineas activas.
     * @param filtro busqueda
     * @return total
     */
    int findAllDetalleReporteCount(FiltroBusquedaLineasActivas filtro);

    /**
     * Genera un fichero excel con los filtros de una cosulta de lineas activas.
     * @param filtro busqueda
     * @return fichero
     * @throws Exception error
     */
    byte[] getExportConsultaLineaActiva(FiltroBusquedaLineasActivas filtro) throws Exception;

    /**
     * Obtiene los detalles de un reporte de lineas arrendadas.
     * @param idReporte reporte
     * @param first pagina
     * @param pageSize filas
     * @return detalles
     */
    List<DetalleLineaArrendadorNng> findAllDetLineaArrendadaByReporte(BigDecimal idReporte, int first, int pageSize);

    /**
     * Obtiene los detalles de un reporte de lineas del arrendatario.
     * @param idReporte reporte
     * @param first pagina
     * @param pageSize filas
     * @return detalles
     */
    List<DetalleLineaArrendatarioNng> findAllDetLineaArrendatarioByReporte(BigDecimal idReporte,
            int first, int pageSize);

    /**
     * Obtiene el total de la numeracion asignada por serie por clave de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @param idReporte reporte
     * @return total
     */
    Integer getNumeracionActivaSerie(ClaveServicio claveServicio, Proveedor proveedor, BigDecimal idReporte);

    /**
     * Obtiene el total de la numeracion asignada por rango por clave de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @param idReporte reporte
     * @return total
     */
    Integer getNumeracionActivaRango(ClaveServicio claveServicio, Proveedor proveedor, BigDecimal idReporte);

    /**
     * Obtiene el total de la numeracion asignada por especifica por clave de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @param idReporte reporte
     * @return total
     */
    Integer getNumeracionActivaEspecifica(ClaveServicio claveServicio, Proveedor proveedor, BigDecimal idReporte);

    /**
     * Obtiene el total de la numeracion asignada por serie por clave de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @param idReporte reporte
     * @return total
     */
    Integer getNumeracionActivaDetSerie(ClaveServicio claveServicio, Proveedor proveedor, BigDecimal idReporte);

    /**
     * Obtiene el total de la numeracion asignada por rango por clave de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @param idReporte reporte
     * @return total
     */
    Integer getNumeracionActivaDetRango(ClaveServicio claveServicio, Proveedor proveedor, BigDecimal idReporte);

    /**
     * Obtiene el total de la numeracion asignada por especifica por clave de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @param idReporte reporte
     * @return total
     */
    Integer getNumeracionActivaDetEspecifica(ClaveServicio claveServicio, Proveedor proveedor, BigDecimal idReporte);

}
