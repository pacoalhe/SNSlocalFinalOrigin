package mx.ift.sns.dao.nng;

import mx.ift.sns.modelo.lineas.ReporteLineaActivaDetNng;

/**
 * Interfaz del DAO de Lineas Activas Detallada.
 * @author X36155QU
 */
public interface IReporteLineaActivaDetNngDao {

    /**
     * Salva una Linea Activa.
     * @param lineaActivaDet lineaActivaDet
     * @return LineaActiva
     */
    ReporteLineaActivaDetNng saveLineaActivaDet(ReporteLineaActivaDetNng lineaActivaDet);

}
