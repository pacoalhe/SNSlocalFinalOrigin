package mx.ift.sns.dao.nng.implementation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Named;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.nng.IHistoricoSerieNngDAO;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaHistoricoSeries;
import mx.ift.sns.modelo.series.HistoricoSerieNng;

/**
 * Implementacion del DAO de la vista de Historico Serie.
 * @author X36155QU
 */
@Named
public class HistorioSerieNngDaoImpl extends BaseDAO<HistoricoSerieNng> implements IHistoricoSerieNngDAO {

    /**
     * Traduce un filtro a un Predicado de CriteriaQuery.
     * @param pFiltro Información del Filtro
     * @param pFrom Tabla a la que se aplica el filtro
     * @param pCriteriaBuilder CriteriaBuilder en uso
     * @return Predicate
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Predicate getPredicateFromFiltro(FiltroBusqueda pFiltro, From pFrom, CriteriaBuilder pCriteriaBuilder) {
        if (pFiltro.getClase().equals(Date.class)) {
            Path<Date> comparableDate = pFrom.get(pFiltro.getCampo());
            if (pFiltro.isFechaDesde()) {
                return pCriteriaBuilder.greaterThanOrEqualTo(comparableDate, (java.util.Date) pFiltro.getValor());
            } else if (pFiltro.isFechaHasta()) {
                return pCriteriaBuilder.lessThanOrEqualTo(comparableDate, (java.util.Date) pFiltro.getValor());
            } else {
                return pCriteriaBuilder.equal(comparableDate, pFiltro.getValor());
            }
        } else {
            return pCriteriaBuilder.equal(pFrom.get(pFiltro.getCampo()), pFiltro.getValor());
        }
    }

    @Override
    public List<HistoricoSerieNng> findAllHistoricoSeries(FiltroBusquedaHistoricoSeries pFiltros) {

        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltros.getListaHitoricoSeriesNng();

        // Criteria Builder
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        // Query SELECT
        CriteriaQuery<HistoricoSerieNng> cQueyData = cb.createQuery(HistoricoSerieNng.class);
        Root<HistoricoSerieNng> hs = cQueyData.from(HistoricoSerieNng.class);

        // WHERE
        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = null;
            pred = this.getPredicateFromFiltro(filtro, hs, cb);
            wherePredicate = cb.and(wherePredicate, pred);
        }
        cQueyData.where(wherePredicate);

        TypedQuery<HistoricoSerieNng> tQuery = getEntityManager().createQuery(cQueyData);

        if (pFiltros.isUsarPaginacion()) {
            tQuery.setFirstResult(pFiltros.getNumeroPagina());
            tQuery.setMaxResults(pFiltros.getResultadosPagina());
        }

        List<HistoricoSerieNng> result = tQuery.getResultList();

        return result;

    }

    @Override
    public int findAllHistoricoSeriesCount(FiltroBusquedaHistoricoSeries pFiltros) {

        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltros.getListaHitoricoSeriesNng();

        // Criteria Builder
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        // Query SELECT
        CriteriaQuery<Long> cQueyData = cb.createQuery(Long.class);
        Root<HistoricoSerieNng> hs = cQueyData.from(HistoricoSerieNng.class);
        cQueyData.select(cb.count(hs));

        // WHERE
        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = null;
            pred = this.getPredicateFromFiltro(filtro, hs, cb);
            wherePredicate = cb.and(wherePredicate, pred);
        }
        cQueyData.where(wherePredicate);

        TypedQuery<Long> tQuery = getEntityManager().createQuery(cQueyData);

        Integer result = tQuery.getSingleResult().intValue();

        return result;

    }
}
