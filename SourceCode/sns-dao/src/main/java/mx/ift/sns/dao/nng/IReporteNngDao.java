package mx.ift.sns.dao.nng;

import java.math.BigDecimal;

import mx.ift.sns.modelo.lineas.ReporteNng;

/**
 * Interfaz del DAO de reportes NNG.
 * @author X36155QU
 */
public interface IReporteNngDao {

    /**
     * Obtiene el un reporte por su ID.
     * @param idReporte id
     * @return reporte
     */
    ReporteNng getReporteById(BigDecimal idReporte);

}
