package mx.ift.sns.dao.nng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.nng.HistoricoRedistribucionNng;

/**
 * Interfaz de definición de los métodos para base de datos de Histórico de Redistribuciones NNG.
 */
public interface IHistoricoRedistribucionNngDao {

    /**
     * Recupera la lista de objetos HistoricoRedistribucionNng con la información de los movimientos históricos de las
     * redistribuciones solicitadas.
     * @param pIdRedistSol Identificador de la redistribución solicitada.
     * @param pCdgAction Indentificador del tipo de acción. Puede ser nulo.
     * @param pCdgEstatusRedist Estatus de la redistribución solicitada. Puede ser nulo.
     * @return Lista de HistoricoRedistribucionNng
     */
    List<HistoricoRedistribucionNng> findAllHistoricActions(BigDecimal pIdRedistSol, String pCdgAction,
            String pCdgEstatusRedist);

}
