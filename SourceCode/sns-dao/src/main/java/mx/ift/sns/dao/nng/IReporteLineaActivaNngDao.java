package mx.ift.sns.dao.nng;

import mx.ift.sns.modelo.lineas.ReporteLineaActivaNng;

/**
 * Interfaz del DAO de Lineas Activas.
 * @author X36155QU
 */
public interface IReporteLineaActivaNngDao {

    /**
     * Salva una Linea Activa.
     * @param lineaActiva lineaActiva
     * @return LineaActiva
     */
    ReporteLineaActivaNng saveLineaActiva(ReporteLineaActivaNng lineaActiva);

}
