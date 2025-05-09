package mx.ift.sns.dao.ng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroBusquedaLineasActivas;
import mx.ift.sns.modelo.filtros.FiltroReporteadorNG;
import mx.ift.sns.modelo.lineas.DetalleReporte;
import mx.ift.sns.modelo.pst.Proveedor;

/**
 * Interfaz del DAO de la vista de DetalleReporte.
 * @author X36155QU
 */
public interface IDetalleReporteDao {

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
     * Obtiene el detalle de una poblacion del ultimo reporte de un proveedor.
     * @param proveedor proveedor
     * @param poblacion inegi
     * @return detalle
     */
    DetalleReporte getLastDetalleReporteByPoblacion(Proveedor proveedor, String poblacion);

    /**
     * Obtiene los totales por ABN de los reportes de linia activa.
     * @param proveedor proveedor
     * @param abn abn
     * @return totales
     */
    Object[] getTotalDetalleReporteByAbn(Proveedor proveedor, BigDecimal abn);

    /**
     * Obtine los totales de Lineas activas hasta una fecha determinada.
     * @param filtros reporte
     * @return totales
     */
    DetalleReporte findTotalesActivosDetalleReporte(FiltroReporteadorNG filtros);

}
