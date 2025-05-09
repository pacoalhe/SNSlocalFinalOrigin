package mx.ift.sns.dao.nng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.lineas.DetalleLineaArrendatarioNng;
import mx.ift.sns.modelo.pst.Proveedor;

/**
 * Interfaz del DAO de DetLineaArrendatario.
 * @author X36155QU
 */
public interface IDetalleLineaArrendatarioNngDao {

    /**
     * Carga masiva de registros de un reporte de lineas activas arrendatario.
     * @param listaDatos registros
     * @param id reporte
     */
    void saveDetLineaArrendatario(List<String> listaDatos, BigDecimal id);

    /**
     * Obtiene los registro de un reporte de lineas arrendatario.
     * @param idReporte reporte
     * @param first pagina
     * @param pageSize registros
     * @return lista detalles
     */
    List<DetalleLineaArrendatarioNng> findAllDetLineaArrendatarioByReporte(BigDecimal idReporte,
            int first, int pageSize);

    /**
     * Obtiene el total de la numeracion activa por serie por clave de servicio y proveedor.
     * @param clave servicio
     * @param proveedor pst
     * @param idReporte reporte
     * @return total
     */
    Integer getNumeracionActivaArrendatarioSerie(String clave, Proveedor proveedor, BigDecimal idReporte);

    /**
     * Obtiene el total de la numeracion activa por Rango por clave de servicio y proveedor.
     * @param clave servicio
     * @param proveedor pst
     * @param idReporte reporte
     * @return total
     */
    Integer getNumeracionActivaArrendatarioRango(String clave, Proveedor proveedor, BigDecimal idReporte);

    /**
     * Obtiene el total de la numeracion activa por especifico por clave de servicio y proveedor.
     * @param clave servicio
     * @param proveedor pst
     * @param idReporte reporte
     * @return total
     */
    Integer getNumeracionActivaArrendatarioEspecifica(String clave, Proveedor proveedor, BigDecimal idReporte);
}
