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
import mx.ift.sns.dao.nng.IHistoricoRangoSerieNngDao;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaHistoricoRangos;
import mx.ift.sns.modelo.nng.HistoricoRangoSerieNng;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que implementa la interfaz de Histórico de Series NNG.
 */
@Named
public class HistoricoRangoSerieNngDaoImpl extends BaseDAO<HistoricoRangoSerieNng>
        implements IHistoricoRangoSerieNngDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoricoRangoSerieNngDaoImpl.class);

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
                return pCriteriaBuilder.equal(comparableDate, (java.util.Date) pFiltro.getValor());
            }
        } else {
            if (pFiltro.isDistinto()) {
                return pCriteriaBuilder.notEqual(pFrom.get(pFiltro.getCampo()), pFiltro.getValor());
            } else {
                return pCriteriaBuilder.equal(pFrom.get(pFiltro.getCampo()), pFiltro.getValor());
            }
        }
    }

    @Override
    public List<HistoricoRangoSerieNng> findAllHistoricActions(FiltroBusquedaHistoricoRangos pFiltros) {

        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltros.getListaFiltros();

        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<HistoricoRangoSerieNng> cQuery = cb.createQuery(HistoricoRangoSerieNng.class);
        Root<HistoricoRangoSerieNng> hrs = cQuery.from(HistoricoRangoSerieNng.class);

        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            wherePredicate = cb.and(wherePredicate, this.getPredicateFromFiltro(filtro, hrs, cb));
        }

        cQuery.where(wherePredicate);
        if (pFiltros.getOrderType() != null) {
            if (pFiltros.getOrderType().equals(FiltroBusquedaHistoricoRangos.ORDEN_ASC)) {
                cQuery.orderBy(cb.asc(hrs.get("id")));
            } else {
                cQuery.orderBy(cb.desc(hrs.get("id")));
            }
        } else {
            cQuery.orderBy(cb.asc(hrs.get("id")));
        }
        cQuery.select(hrs);

        TypedQuery<HistoricoRangoSerieNng> tQuery = this.getEntityManager().createQuery(cQuery);
        if (pFiltros.isUsarPaginacion()) {
            tQuery.setFirstResult(pFiltros.getNumeroPagina());
            tQuery.setMaxResults(pFiltros.getResultadosPagina());
        }

        List<HistoricoRangoSerieNng> result = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} movimientos históricos sobre RangosNng con los filtros {}.",
                    result.size(), pFiltros.toString());
        }

        return result;
    }

    @Override
    public int findAllHistoricActionsCount(FiltroBusquedaHistoricoRangos pFiltros) {

        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltros.getListaFiltros();

        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<HistoricoRangoSerieNng> hrs = cQuery.from(HistoricoRangoSerieNng.class);

        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            wherePredicate = cb.and(wherePredicate, this.getPredicateFromFiltro(filtro, hrs, cb));
        }

        cQuery.where(wherePredicate);
        cQuery.orderBy(cb.asc(hrs.get("id")));
        cQuery.select(cb.count(hrs));

        TypedQuery<Long> tQuery = this.getEntityManager().createQuery(cQuery);
        int rowCount = tQuery.getSingleResult().intValue();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han contado {} movimientos históricos sobre RangosNng con los filtros {}.",
                    rowCount, pFiltros.toString());
        }

        return rowCount;
    }
}
