package mx.ift.sns.dao.cpsn.implementacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Named;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.cpsn.ISolicitudAsignacionCpsnDao;
import mx.ift.sns.modelo.cpsn.NumeracionAsignadaCpsn;
import mx.ift.sns.modelo.cpsn.NumeracionSolicitadaCpsn;
import mx.ift.sns.modelo.cpsn.SolicitudAsignacionCpsn;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCpsn;
import mx.ift.sns.modelo.oficios.Oficio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de Solicitudes de Asignación.
 */
@Named
public class SolicitudAsignacionCpsnDaoImpl extends BaseDAO<SolicitudAsignacionCpsn> implements
        ISolicitudAsignacionCpsnDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudAsignacionCpsnDaoImpl.class);

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
                return pCriteriaBuilder.lessThan(comparableDate, (java.util.Date) pFiltro.getValor());
            } else {
                return pCriteriaBuilder.equal(comparableDate, (java.util.Date) pFiltro.getValor());
            }
        } else {
            return pCriteriaBuilder.equal(pFrom.get(pFiltro.getCampo()), pFiltro.getValor());
        }
    }

    @Override
    public List<SolicitudAsignacionCpsn> findAllSolicitudesAsignacion() {
        String consulta = "SELECT sl FROM SolicitudAsignacionCpsn sl";
        TypedQuery<SolicitudAsignacionCpsn> query = getEntityManager().createQuery(consulta,
                SolicitudAsignacionCpsn.class);
        List<SolicitudAsignacionCpsn> result = query.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Solicitudes encontradas: " + result.size());
        }
        return result;
    }

    @Override
    public List<SolicitudAsignacionCpsn> findAllSolicitudesAsignacion(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud)
            throws Exception {

        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosSolicitudAsignacion();

        // Criteria Builder
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        // Query SELECT sl FROM SolicitudAsignacionCpsn
        CriteriaQuery<SolicitudAsignacionCpsn> cQueyData = cb.createQuery(SolicitudAsignacionCpsn.class);
        Root<SolicitudAsignacionCpsn> sl = cQueyData.from(SolicitudAsignacionCpsn.class);

        // Where
        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            wherePredicate = cb.and(wherePredicate, this.getPredicateFromFiltro(filtro, sl, cb));
        }

        // Query
        cQueyData.select(sl).where(wherePredicate).distinct(true);
        cQueyData.orderBy(cb.asc(sl.get("id")));

        TypedQuery<SolicitudAsignacionCpsn> tQuery = getEntityManager().createQuery(cQueyData);

        // Paginación
        if (pFiltrosSolicitud.isUsarPaginacion()) {
            tQuery.setFirstResult(pFiltrosSolicitud.getNumeroPagina());
            tQuery.setMaxResults(pFiltrosSolicitud.getResultadosPagina());
        }

        List<SolicitudAsignacionCpsn> result = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            StringBuffer sbTraza = new StringBuffer();
            sbTraza.append("Solicitudes encontradas: ").append(result.size());
            sbTraza.append(", paginación: ").append(pFiltrosSolicitud.isUsarPaginacion());
            if (pFiltrosSolicitud.isUsarPaginacion()) {
                sbTraza.append(" (Página: ").append(pFiltrosSolicitud.getNumeroPagina());
                sbTraza.append(", MaxResult: ").append(pFiltrosSolicitud.getResultadosPagina()).append(")");
            }
            LOGGER.debug(sbTraza.toString());
        }

        return result;

    }

    @Override
    public int findAllSolicitudesAsignacionCount(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud) throws Exception {
        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosSolicitudAsignacion();

        // Query SELECT COUNT(sl) FROM SolicitudAsignacionCpsn sl
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<SolicitudAsignacionCpsn> sl = cQuery.from(SolicitudAsignacionCpsn.class);

        // Where
        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            wherePredicate = cb.and(wherePredicate, this.getPredicateFromFiltro(filtro, sl, cb));
        }
        cQuery.where(wherePredicate);

        TypedQuery<Long> tQuery = getEntityManager().createQuery(cQuery.select(cb.countDistinct(sl)));
        int rowCount = tQuery.getSingleResult().intValue();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Solicitudes contadas: " + rowCount);
        }

        return rowCount;
    }

    @Override
    public SolicitudAsignacionCpsn getSolicitudAsignacionById(BigDecimal pConsecutivo) {
        return getEntityManager().find(SolicitudAsignacionCpsn.class, pConsecutivo);
    }

    @SuppressWarnings("unused")
    @Override
    public SolicitudAsignacionCpsn getSolicitudAsignacionEagerLoad(SolicitudAsignacionCpsn pSolicitud) {
        // Select SL From SolicitudAsignacion
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<SolicitudAsignacionCpsn> cQuery = criteriaBuilder.createQuery(SolicitudAsignacionCpsn.class);

        // Left Join
        Root<SolicitudAsignacionCpsn> sl = cQuery.from(SolicitudAsignacionCpsn.class);
        Fetch<SolicitudAsignacionCpsn, NumeracionAsignadaCpsn> joinNumAsig = sl.fetch("numeracionAsignadas",
                JoinType.LEFT);
        Fetch<SolicitudAsignacionCpsn, NumeracionSolicitadaCpsn> fetchNumSol = sl.fetch("numeracionSolicitadas",
                JoinType.LEFT);
        Fetch<SolicitudAsignacionCpsn, Oficio> fetchOfi = sl.fetch("oficios", JoinType.LEFT);

        // Where
        ParameterExpression<BigDecimal> idSol = criteriaBuilder.parameter(BigDecimal.class);
        cQuery.where(criteriaBuilder.equal(sl.get("id"), idSol)).distinct(true);

        TypedQuery<SolicitudAsignacionCpsn> typedQuery = getEntityManager().createQuery(cQuery.select(sl));
        typedQuery.setParameter(idSol, pSolicitud.getId());

        List<SolicitudAsignacionCpsn> results = typedQuery.getResultList();
        return results.get(0);
    }
}
