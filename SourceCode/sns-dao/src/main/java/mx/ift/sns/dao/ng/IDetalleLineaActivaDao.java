package mx.ift.sns.dao.ng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.lineas.DetalleLineaActiva;
import mx.ift.sns.modelo.lineas.Reporte;
import mx.ift.sns.modelo.pst.Proveedor;

/**
 * Interfaz del DAO del Detalle de lineas activas.
 * @author X36155QU
 */
public interface IDetalleLineaActivaDao {

    /**
     * Obtiene el detalle de la ultima linea reportada por poblacion.
     * @param pst Proveedor
     * @param poblacion poblacion
     * @return DetalleLineaActiva detalle
     */
    DetalleLineaActiva getLastDetalleLineaActivaByPoblacion(Proveedor pst, String poblacion);

    /**
     * Obtiene el detalle de la ultima linea reportada por ABN.
     * @param proveedor proveedor
     * @param abn abn
     * @return DetalleLineaActiva detalle
     */
    DetalleLineaActiva getLastDetalleLineaActivaByAbn(Proveedor proveedor, BigDecimal abn);

    /**
     * Obtiene el total de los detalles de las lineas reportadas en un ABN.
     * @param abn abn
     * @return DetalleLineaActiva detalle
     */
    DetalleLineaActiva getTotalDetalleLineaActivaByAbn(BigDecimal abn);

    /**
     * Guardado masivo de los registro del reporte de lineas activas.
     * @param idReporte reporte
     * @param listaDatos registros
     */
    void saveDetalleLineaActiva(List<String> listaDatos, BigDecimal idReporte);

    /**
     * Obtiene todos los registros de un reporte de lineas activas cuyo total asignacion no coincide en el SNS.
     * @param reporte reporte
     * @return registros
     */
    List<String> findAllAvisoAsignacionDetalleLineaActiva(Reporte reporte);

}
