package mx.ift.sns.dao.nng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.pst.Proveedor;

/**
 * Interfaz del DAO del Detalle de lineas activas.
 * @author X36155QU
 */
public interface IDetalleLineaActivaNngDao {

    /**
     * Guardado masivo de los registro del reporte de lineas activas.
     * @param idReporte reporte
     * @param listaDatos registros
     */
    void saveDetalleLineaActiva(List<String> listaDatos, BigDecimal idReporte);

    /**
     * Obtiene el total de la numeracion asignada por serie por clave de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @param idReporte reporte
     * @return total
     */
    Integer getNumeracionActivaSerie(String claveServicio, Proveedor proveedor, BigDecimal idReporte);

    /**
     * Obtiene el total de la numeracion asignada por serie por clave de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @param idReporte reporte
     * @return total
     */
    Integer getNumeracionActivaRango(String claveServicio, Proveedor proveedor, BigDecimal idReporte);

    /**
     * Obtiene el total de la numeracion asignada por serie por clave de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @param idReporte reporte
     * @return total
     */
    Integer getNumeracionActivaEspecifica(String claveServicio, Proveedor proveedor, BigDecimal idReporte);

}
