package mx.ift.sns.dao.cpsi.implementacion;

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
import mx.ift.sns.dao.cpsi.ISolicitudAsignacionCpsiDao;
import mx.ift.sns.modelo.cpsi.CpsiAsignado;
import mx.ift.sns.modelo.cpsi.SolicitudAsignacionCpsi;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCPSI;
import mx.ift.sns.modelo.oficios.Oficio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que implementa la interfaz de solicitudes de asignación CPSI.
 */
@Named
public class SolicitudAsignacionCpsiDaoImpl extends BaseDAO<SolicitudAsignacionCpsi> implements
        ISolicitudAsignacionCpsiDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudAsignacionCpsiDaoImpl.class);

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
    public List<SolicitudAsignacionCpsi> findAllSolicitudesAsignacion() {
        String query = "SELECT sl FROM SolicitudAsignacionCpsi sl ORDER BY sl.id";
        TypedQuery<SolicitudAsignacionCpsi> tQuery =
                this.getEntityManager().createQuery(query, SolicitudAsignacionCpsi.class);
        List<SolicitudAsignacionCpsi> result = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} solicitudes de asignación de CPSI.", result.size());
        }

        return result;
    }

    @Override
    public List<SolicitudAsignacionCpsi> findAllSolicitudesAsignacion(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud) {

        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosSolicitudAsignacionCpsi();

        // Criteria Builder
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        // Query SELECT sl FROM SolicitudAsignacionCpsi
        CriteriaQuery<SolicitudAsignacionCpsi> cQueyData = cb.createQuery(SolicitudAsignacionCpsi.class);
        Root<SolicitudAsignacionCpsi> sl = cQueyData.from(SolicitudAsignacionCpsi.class);

        // Where
        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            wherePredicate = cb.and(wherePredicate, this.getPredicateFromFiltro(filtro, sl, cb));
        }

        // Query
        cQueyData.select(sl).where(wherePredicate).distinct(true);
        cQueyData.orderBy(cb.asc(sl.get("id")));

        TypedQuery<SolicitudAsignacionCpsi> tQuery = getEntityManager().createQuery(cQueyData);

        // Paginación
        if (pFiltrosSolicitud.isUsarPaginacion()) {
            tQuery.setFirstResult(pFiltrosSolicitud.getNumeroPagina());
            tQuery.setMaxResults(pFiltrosSolicitud.getResultadosPagina());
        }

        List<SolicitudAsignacionCpsi> result = tQuery.getResultList();
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
    public int findAllSolicitudesAsignacionCount(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud) {
        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosSolicitudAsignacionCpsi();

        // Query SELECT COUNT(sl) FROM SolicitudAsignacionCpsi sl
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<SolicitudAsignacionCpsi> sl = cQuery.from(SolicitudAsignacionCpsi.class);

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
    public SolicitudAsignacionCpsi getSolicitudAsignacionById(BigDecimal pConsecutivo) {
        return this.getEntityManager().find(SolicitudAsignacionCpsi.class, pConsecutivo);
    }

    @SuppressWarnings("unused")
    @Override
    public SolicitudAsignacionCpsi getSolicitudAsignacionEagerLoad(SolicitudAsignacionCpsi pSolicitud) {
        // Select SL From SolicitudAsignacionCpsi
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<SolicitudAsignacionCpsi> cQuery = criteriaBuilder.createQuery(SolicitudAsignacionCpsi.class);

        // Left Join
        Root<SolicitudAsignacionCpsi> sl = cQuery.from(SolicitudAsignacionCpsi.class);
        Fetch<SolicitudAsignacionCpsi, CpsiAsignado> joinLibSol = sl.fetch("cpsiAsignados", JoinType.LEFT);
        Fetch<SolicitudAsignacionCpsi, Oficio> fetchOfi = sl.fetch("oficios", JoinType.LEFT);

        // Where
        ParameterExpression<BigDecimal> idSol = criteriaBuilder.parameter(BigDecimal.class);
        cQuery.where(criteriaBuilder.equal(sl.get("id"), idSol)).distinct(true);
        cQuery.select(sl);

        TypedQuery<SolicitudAsignacionCpsi> typedQuery = getEntityManager().createQuery(cQuery);
        typedQuery.setParameter(idSol, pSolicitud.getId());

        // Distinct = true
        SolicitudAsignacionCpsi solicitud = typedQuery.getSingleResult();
        getEntityManager().refresh(solicitud);

        return solicitud;
    }

}
