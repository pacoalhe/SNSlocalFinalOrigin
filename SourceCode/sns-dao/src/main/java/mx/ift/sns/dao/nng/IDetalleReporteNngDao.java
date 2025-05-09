package mx.ift.sns.dao.nng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroBusquedaLineasActivas;
import mx.ift.sns.modelo.lineas.DetalleReporteNng;
import mx.ift.sns.modelo.pst.Proveedor;

/**
 * Interfaz del DAO de la vista de DetalleReporte.
 * @author X36155QU
 */
public interface IDetalleReporteNngDao {

    /**
     * Consulta generica a la vista de detalle de reporte.
     * @param filtros FiltroBusquedaLineasActivas
     * @return lista detalles
     */
    List<DetalleReporteNng> findAllDetalleReporte(FiltroBusquedaLineasActivas filtros);

    /**
     * Obtiene el total de una consulta generica a la vista de detalle de reporte.
     * @param filtros FiltroBusquedaLineasActivas
     * @return total
     */
    Integer findAllDetalleReporteCount(FiltroBusquedaLineasActivas filtros);

    /**
     * Obtiene el consecutivo del ultimo reporte de un provedoor sobre un clave de servicio.
     * @param clave servicio
     * @param proveedor pst
     * @return consecutivo
     */
    BigDecimal getLastConsecutivoReporte(String clave, Proveedor proveedor);

}
