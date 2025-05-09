package mx.ift.sns.dao.ng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.lineas.DetLineaArrendatario;

/**
 * Interfaz del DAO de DetLineaArrendatario.
 * @author X36155QU
 */
public interface IDetLineaArrendatarioDao {

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

}
