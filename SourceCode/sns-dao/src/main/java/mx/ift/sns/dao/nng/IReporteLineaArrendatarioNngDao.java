package mx.ift.sns.dao.nng;

import mx.ift.sns.modelo.lineas.ReporteLineaArrendatarioNng;

/**
 * Interfaz del DAO de linea arrendatario.
 * @author X36155QU
 */
public interface IReporteLineaArrendatarioNngDao {

    /**
     * Guarda una linea arrendatario.
     * @param lineaArrendatario lineaArrendatario
     * @return LineaArrendatario
     */
    ReporteLineaArrendatarioNng saveLineaArrendatario(ReporteLineaArrendatarioNng lineaArrendatario);

}
