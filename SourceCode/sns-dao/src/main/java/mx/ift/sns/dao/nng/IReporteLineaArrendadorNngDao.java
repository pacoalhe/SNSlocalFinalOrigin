package mx.ift.sns.dao.nng;

import mx.ift.sns.modelo.lineas.ReporteLineaArrendadorNng;

/**
 * Interfaz del DAO de linea arrendada.
 * @author X36155QU
 */
public interface IReporteLineaArrendadorNngDao {

    /**
     * Guarda una linea arrendatario.
     * @param lineaArrendada lineaArrendada
     * @return lineaArrendada
     */
    ReporteLineaArrendadorNng saveLineaArrendada(ReporteLineaArrendadorNng lineaArrendada);

}
