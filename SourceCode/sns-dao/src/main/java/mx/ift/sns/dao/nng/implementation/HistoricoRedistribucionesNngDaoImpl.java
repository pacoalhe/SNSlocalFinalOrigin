package mx.ift.sns.dao.nng.implementation;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Named;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.nng.IHistoricoRedistribucionNngDao;
import mx.ift.sns.modelo.nng.HistoricoRedistribucionNng;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que implementa la interfaz de Histórico de Redistribuciones.
 */
@Named
public class HistoricoRedistribucionesNngDaoImpl extends BaseDAO<HistoricoRedistribucionNng> implements
        IHistoricoRedistribucionNngDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoricoRedistribucionesNngDaoImpl.class);

    @Override
    public List<HistoricoRedistribucionNng> findAllHistoricActions(BigDecimal pIdRedistSol, String pCdgAction,
            String pCdgEstatusRedist) {

        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<HistoricoRedistribucionNng> cQuery = cb.createQuery(HistoricoRedistribucionNng.class);
        Root<HistoricoRedistribucionNng> histRed = cQuery.from(HistoricoRedistribucionNng.class);

        ParameterExpression<BigDecimal> idRedSol = cb.parameter(BigDecimal.class);
        ParameterExpression<String> idAction = cb.parameter(String.class);
        ParameterExpression<String> idEstatus = cb.parameter(String.class);

        Predicate wherePredicate = cb.conjunction();
        wherePredicate = cb.and(wherePredicate, cb.equal(histRed.get("redistribucionSolicitada").get("id"), idRedSol));
        if (pCdgAction != null) {
            wherePredicate = cb.and(wherePredicate, cb.equal(histRed.get("accion"), idAction));
        }
        if (pCdgEstatusRedist != null) {
            wherePredicate = cb.and(wherePredicate, cb.equal(histRed.get("estatus").get("codigo"), idEstatus));
        }

        cQuery.where(wherePredicate);
        cQuery.select(histRed);

        TypedQuery<HistoricoRedistribucionNng> tQuery = getEntityManager().createQuery(cQuery);
        tQuery.setParameter(idRedSol, pIdRedistSol);
        if (pCdgAction != null) {
            tQuery.setParameter(idAction, pCdgAction);
        }
        if (pCdgEstatusRedist != null) {
            tQuery.setParameter(idEstatus, pCdgEstatusRedist);
        }

        List<HistoricoRedistribucionNng> lista = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} movimientos históricos para la redistribución {}",
                    lista.size(), pIdRedistSol);
        }

        return lista;
    }
}
