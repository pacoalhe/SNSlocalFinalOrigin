package mx.ift.sns.dao.ng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.lineas.DetLineaActivaDet;
import mx.ift.sns.modelo.lineas.Reporte;

/**
 * Interfaz DAO DetLineaActivaDet.
 * @author X36155QU
 */
public interface IDetLineaActivaDetDao {

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

}
