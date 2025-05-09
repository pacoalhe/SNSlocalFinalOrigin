package mx.ift.sns.dao.nng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.lineas.DetalleLineaArrendadorNng;

/**
 * Interfaz del DAO de DetLineaArrendada.
 * @author X36155QU
 */
public interface IDetalleLineaArrendadorNngDao {

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
    List<DetalleLineaArrendadorNng> findAllDetLineaArrendadaByReporte(BigDecimal idReporte, int first, int pageSize);

}
