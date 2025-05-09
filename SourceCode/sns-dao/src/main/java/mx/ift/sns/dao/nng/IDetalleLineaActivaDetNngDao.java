package mx.ift.sns.dao.nng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.pst.Proveedor;

/**
 * Interfaz DAO DetLineaActivaDet.
 * @author X36155QU
 */
public interface IDetalleLineaActivaDetNngDao {

    /**
     * Carga masiva de registros de un reporte de lineas activas detallada.
     * @param listaDatos registros
     * @param idReporte reporte
     */
    void saveDetLineaActivaDet(List<String> listaDatos, BigDecimal idReporte);

    /**
     * Obtiene el total de la numeracion asignada por serie por clave de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @param idReporte reporte
     * @return total
     */
    Integer getNumeracionActivaDetSerie(String claveServicio, Proveedor proveedor, BigDecimal idReporte);

    /**
     * Obtiene el total de la numeracion asignada por rango por clave de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @param idReporte reporte
     * @return total
     */
    Integer getNumeracionActivaDetRango(String claveServicio, Proveedor proveedor, BigDecimal idReporte);

    /**
     * Obtiene el total de la numeracion asignada por especifica por clave de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @param idReporte reporte
     * @return total
     */
    Integer getNumeracionActivaDetEspecifica(String claveServicio, Proveedor proveedor, BigDecimal idReporte);

}
