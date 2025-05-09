package mx.ift.sns.dao.nng.implementation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.nng.ISolicitudAsignacionNngDao;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.nng.NumeracionAsignadaNng;
import mx.ift.sns.modelo.nng.NumeracionSolicitadaNng;
import mx.ift.sns.modelo.nng.RangoSerieNng;
import mx.ift.sns.modelo.nng.SolicitudAsignacionNng;
import mx.ift.sns.modelo.oficios.Oficio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación del DAO de SolicitudAsignacionNng.
 * @author X36155QU
 */
public class SolicitudAsignacionNngDaoImpl extends BaseDAO<SolicitudAsignacionNng> implements
        ISolicitudAsignacionNngDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudAsignacionNngDaoImpl.class);

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
                return pCriteriaBuilder.equal(comparableDate, pFiltro.getValor());
            }
        } else {
            return pCriteriaBuilder.equal(pFrom.get(pFiltro.getCampo()), pFiltro.getValor());
        }
    }

    @Override
    public List<SolicitudAsignacionNng> findAllSolicitudesAsignacion() {
        String consulta = "SELECT sl FROM SolicitudAsignacionNng sl";
        TypedQuery<SolicitudAsignacionNng> query = getEntityManager().createQuery(consulta,
                SolicitudAsignacionNng.class);
        List<SolicitudAsignacionNng> result = query.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Solicitudes encontradas: " + result.size());
        }
        return result;

    }

    @Override
    public List<SolicitudAsignacionNng> findAllSolicitudesAsignacion(FiltroBusquedaSolicitudes filtros)
            throws Exception {

        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> pFiltros = filtros.getListaFiltrosSolicitudAsignacionNng();

        // Si hay que cruzar por varias tablas la paginación falla ya que se haría un
        // distinct sobre el resultado de la paginación y el resultado no sería válido.
        // Por eso, buscamos primero la lista de id's que cuadran y sobre ellos
        // hacemos la paginación para traer la información.

        // Criteria Builder
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<SolicitudAsignacionNng> cQueyData = cb.createQuery(SolicitudAsignacionNng.class);

        if (pFiltros.isEmpty()) {
            TypedQuery<SolicitudAsignacionNng> tquery = getEntityManager().createQuery(cQueyData);
            if (filtros.isUsarPaginacion()) {
                tquery.setFirstResult(filtros.getNumeroPagina()).setMaxResults(filtros.getResultadosPagina());
            }
            return tquery.getResultList();
        } else {

            CriteriaQuery<BigDecimal> cQueryIds = cb.createQuery(BigDecimal.class);
            Root<SolicitudAsignacionNng> sl = cQueyData.from(SolicitudAsignacionNng.class);
            Join<SolicitudAsignacionNng, NumeracionSolicitadaNng> ns = sl
                    .join("numeracionesSolicitadas", JoinType.LEFT);
            // WHERE
            Predicate wherePredicate = cb.conjunction();
            for (FiltroBusqueda filtro : pFiltros) {
                Predicate pred = null;
                if (filtro.getPrefijo().equals("sl")) {
                    pred = this.getPredicateFromFiltro(filtro, sl, cb);
                } else if (filtro.getPrefijo().equals("ns")) {
                    pred = this.getPredicateFromFiltro(filtro, ns, cb);
                }
                // else if (filtro.getPrefijo().equals("rs")) {
                // pred = this.getPredicateFromFiltro(filtro, rs, cb);
                // }
                wherePredicate = cb.and(wherePredicate, pred);
            }
            // Primero seleccionamos todos los Identificadores de Solicitud que cumplen el WHERE
            cQueryIds.where(wherePredicate).select(sl.<BigDecimal> get("id")).distinct(true);
            cQueryIds.orderBy(cb.asc(sl.get("id")));
            TypedQuery<BigDecimal> tQueryIds = getEntityManager().createQuery(cQueryIds);
            // Lista de Solicitudes que cumplen los criterios de búsqueda.
            List<BigDecimal> listaIds = tQueryIds.getResultList();
            if (!listaIds.isEmpty()) {
                if (filtros.isUsarPaginacion()) {
                    // Seleccionamos los Ids de solicitud que entran dentro de la paginación.
                    int from = filtros.getNumeroPagina();
                    int to = (from + filtros.getResultadosPagina());
                    BigDecimal[] idsPaginados = Arrays.copyOfRange(
                            listaIds.toArray(new BigDecimal[listaIds.size()]), from, to);
                    listaIds = Arrays.asList(idsPaginados);
                }

                // Recogemos la información de las N solicitudes que cumplen el WHERE
                cQueyData.select(sl).distinct(true);
                cQueyData.where(wherePredicate).where(sl.get("id").in(listaIds));
                cQueyData.orderBy(cb.asc(sl.get("id")));
                TypedQuery<SolicitudAsignacionNng> tQueryData = getEntityManager().createQuery(cQueyData);

                List<SolicitudAsignacionNng> result = tQueryData.getResultList();

                if (LOGGER.isDebugEnabled()) {
                    StringBuffer sbTraza = new StringBuffer();
                    sbTraza.append("Solicitudes encontrados: ").append(result.size());
                    sbTraza.append(", paginación: ").append(filtros.isUsarPaginacion());
                    if (filtros.isUsarPaginacion()) {
                        sbTraza.append(" (Página: ").append(filtros.getNumeroPagina());
                        sbTraza.append(", MaxResult: ").append(filtros.getResultadosPagina()).append(")");
                    }
                    LOGGER.debug(sbTraza.toString());

                }

                return result;
            } else {
                return new ArrayList<SolicitudAsignacionNng>(1);
            }
        }
    }

    @Override
    public Integer findAllSolicitudesAsignacionCount(FiltroBusquedaSolicitudes filtros) throws Exception {

        ArrayList<FiltroBusqueda> listaFiltros = filtros.getListaFiltrosSolicitudAsignacionNng();

        // Query SELECT sl FROM SolicitudAsignacion sl
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<SolicitudAsignacionNng> sl = cQuery.from(SolicitudAsignacionNng.class);
        Join<SolicitudAsignacionNng, NumeracionSolicitadaNng> ns = sl.join("numeracionesSolicitadas", JoinType.LEFT);

        // WHERE
        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : listaFiltros) {
            Predicate pred = null;
            if (filtro.getPrefijo().equals("sl")) {
                pred = this.getPredicateFromFiltro(filtro, sl, cb);
            } else if (filtro.getPrefijo().equals("ns")) {
                pred = this.getPredicateFromFiltro(filtro, ns, cb);
            }

            wherePredicate = cb.and(wherePredicate, pred);
        }
        cQuery.where(wherePredicate);

        TypedQuery<Long> tQuery = getEntityManager().createQuery(cQuery.select(cb.countDistinct(sl)));
        int rowCount = tQuery.getSingleResult().intValue();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Solicitudes contadas: " + rowCount);
        }

        return rowCount;

    }

    @SuppressWarnings("unused")
    @Override
    public SolicitudAsignacionNng getSolicitudAsignacionEagerLoad(SolicitudAsignacionNng pSolicitud) {

        // Select SL From SolicitudAsignacionNng
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<SolicitudAsignacionNng> cQuery = criteriaBuilder.createQuery(SolicitudAsignacionNng.class);

        // Left Join
        Root<SolicitudAsignacionNng> sl = cQuery.from(SolicitudAsignacionNng.class);
        Fetch<SolicitudAsignacionNng, RangoSerieNng> fetchRango = sl.fetch("rangosNng", JoinType.LEFT);
        Fetch<SolicitudAsignacionNng, NumeracionSolicitadaNng> fetchNumSol = sl.fetch("numeracionesSolicitadas",
                JoinType.LEFT);
        Fetch<NumeracionSolicitadaNng, NumeracionAsignadaNng> fetchNumAsig = fetchNumSol.fetch("numeracionesAsignadas",
                JoinType.LEFT);
        Fetch<SolicitudAsignacionNng, Oficio> fetchOfi = sl.fetch("oficios", JoinType.LEFT);

        // Where
        ParameterExpression<BigDecimal> idSol = criteriaBuilder.parameter(BigDecimal.class);
        cQuery.where(criteriaBuilder.equal(sl.get("id"), idSol)).distinct(true);

        TypedQuery<SolicitudAsignacionNng> typedQuery = getEntityManager().createQuery(cQuery.select(sl));
        typedQuery.setParameter(idSol, pSolicitud.getId());

        List<SolicitudAsignacionNng> results = typedQuery.getResultList();
        return results.get(0);

    }
}
