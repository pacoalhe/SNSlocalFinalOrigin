package mx.ift.sns.dao.cpsn.implementacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Named;
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
import mx.ift.sns.dao.cpsn.ISolicitudLiberacionCpsnDao;
import mx.ift.sns.modelo.cpsn.LiberacionSolicitadaCpsn;
import mx.ift.sns.modelo.cpsn.SolicitudLiberacionCpsn;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCpsn;
import mx.ift.sns.modelo.oficios.Oficio;
import mx.ift.sns.modelo.solicitud.EstadoLiberacionSolicitada;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que implementa la interfaz de solicitudes de liberación CPSN.
 */
@Named
public class SolicitudLiberacionCpsnDaoImpl extends BaseDAO<SolicitudLiberacionCpsn> implements
        ISolicitudLiberacionCpsnDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudLiberacionCpsnDaoImpl.class);

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
    public List<SolicitudLiberacionCpsn> findAllSolicitudesLiberacion() {
        String query = "SELECT sl FROM SolicitudLiberacionCpsn sl ORDER BY sl.id";
        TypedQuery<SolicitudLiberacionCpsn> tQuery =
                this.getEntityManager().createQuery(query, SolicitudLiberacionCpsn.class);
        List<SolicitudLiberacionCpsn> result = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} solicitudes de liberación de CPSN.", result.size());
        }

        return result;
    }

    @Override
    public List<SolicitudLiberacionCpsn> findAllSolicitudesLiberacion(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud) {

        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosSolicitudLiberacionCpsn();

        // Criteria Builder
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        // Query SELECT sl FROM SolicitudLiberacionCpsn sl LEFT JOIN LiberacionSolicitadaCpsn ls
        CriteriaQuery<SolicitudLiberacionCpsn> cQueyData = cb.createQuery(SolicitudLiberacionCpsn.class);
        CriteriaQuery<BigDecimal> cQueryIds = cb.createQuery(BigDecimal.class);
        Root<SolicitudLiberacionCpsn> sl = cQueyData.from(SolicitudLiberacionCpsn.class);
        Join<SolicitudLiberacionCpsn, LiberacionSolicitadaCpsn> ls = sl.join("liberacionesSolicitadas",
                JoinType.LEFT);

        // WHERE
        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = null;
            if (filtro.getPrefijo().equals("sl")) {
                pred = this.getPredicateFromFiltro(filtro, sl, cb);
            } else {
                pred = this.getPredicateFromFiltro(filtro, ls, cb);
            }
            wherePredicate = cb.and(wherePredicate, pred);
        }

        // Si hay que cruzar por varias tablas la paginación falla ya que se haría un
        // distinct sobre el resultado de la paginación y el resultado no sería válido.
        // Por eso, buscamos primero la lista de id's que cuadran y sobre ellos
        // hacemos la paginación para traer la información.

        // Primero seleccionamos todos los Identificadores de Solicitud que cumplen el WHERE
        cQueryIds.where(wherePredicate).select(sl.<BigDecimal> get("id")).distinct(true);
        cQueryIds.orderBy(cb.asc(sl.get("id")));
        TypedQuery<BigDecimal> tQueryIds = getEntityManager().createQuery(cQueryIds);

        // Lista de Solicitudes que cumplen los criterios de búsqueda.
        List<BigDecimal> listaIds = tQueryIds.getResultList();

        if (!listaIds.isEmpty()) {
            if (pFiltrosSolicitud.isUsarPaginacion()) {
                // Seleccionamos los Ids de solicitud que entran dentro de la paginación.
                int from = pFiltrosSolicitud.getNumeroPagina();
                int to = (from + pFiltrosSolicitud.getResultadosPagina());
                BigDecimal[] idsPaginados = Arrays.copyOfRange(
                        listaIds.toArray(new BigDecimal[listaIds.size()]), from, to);
                listaIds = Arrays.asList(idsPaginados);
            }

            // Recogemos la información de las N solicitudes que cumplen el WHERE
            cQueyData.select(sl).distinct(true);
            cQueyData.where(wherePredicate).where(sl.get("id").in(listaIds));
            cQueyData.orderBy(cb.asc(sl.get("id")));
            TypedQuery<SolicitudLiberacionCpsn> tQueryData = getEntityManager().createQuery(cQueyData);

            // Hacemos los fecth para mostrar la información de las tablas cruzadas, se haya buscado
            // algo en ellas o no.
            tQueryData.setHint("eclipselink.join-fetch", "sl.liberacionesSolicitadas");

            List<SolicitudLiberacionCpsn> result = tQueryData.getResultList();
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
        } else {
            return new ArrayList<SolicitudLiberacionCpsn>(1);
        }

    }

    @Override
    public int findAllSolicitudesLiberacionCount(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud) {
        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosSolicitudLiberacionCpsn();

        // Query SELECT COUNT(sl) FROM SolicitudLiberacionCpsn sl LEFT JOIN LiberacionSolicitadaCpsn ls
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<SolicitudLiberacionCpsn> sl = cQuery.from(SolicitudLiberacionCpsn.class);
        Join<SolicitudLiberacionCpsn, LiberacionSolicitadaCpsn> ls = sl.join("liberacionesSolicitadas", JoinType.LEFT);

        // WHERE
        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = null;
            if (filtro.getPrefijo().equals("sl")) {
                pred = this.getPredicateFromFiltro(filtro, sl, cb);
            } else {
                pred = this.getPredicateFromFiltro(filtro, ls, cb);
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

    @Override
    public SolicitudLiberacionCpsn getSolicitudLiberacionById(BigDecimal pConsecutivo) {
        return this.getEntityManager().find(SolicitudLiberacionCpsn.class, pConsecutivo);
    }

    @SuppressWarnings("unused")
    @Override
    public SolicitudLiberacionCpsn getSolicitudLiberacionEagerLoad(SolicitudLiberacionCpsn pSolicitud) {
        // Select SL From SolicitudLiberacionNng
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<SolicitudLiberacionCpsn> cQuery = criteriaBuilder.createQuery(SolicitudLiberacionCpsn.class);

        // Left Join
        Root<SolicitudLiberacionCpsn> sl = cQuery.from(SolicitudLiberacionCpsn.class);
        Fetch<SolicitudLiberacionCpsn, LiberacionSolicitadaCpsn> joinLibSol = sl.fetch("liberacionesSolicitadas",
                JoinType.LEFT);
        Fetch<SolicitudLiberacionCpsn, Oficio> fetchOfi = sl.fetch("oficios", JoinType.LEFT);

        // Where
        ParameterExpression<BigDecimal> idSol = criteriaBuilder.parameter(BigDecimal.class);
        cQuery.where(criteriaBuilder.equal(sl.get("id"), idSol)).distinct(true);
        cQuery.select(sl);

        TypedQuery<SolicitudLiberacionCpsn> typedQuery = getEntityManager().createQuery(cQuery);
        typedQuery.setParameter(idSol, pSolicitud.getId());

        // Distinct = true
        SolicitudLiberacionCpsn solicitud = typedQuery.getSingleResult();
        getEntityManager().refresh(solicitud);

        return solicitud;
    }

    @Override
    public boolean cpsnEnLiberacionPendiente(Integer cpsnMin, Integer cpsnMax, EstadoLiberacionSolicitada estatusP,
            EstadoLiberacionSolicitada estatusR, Date fecha) {
        String jpql = "SELECT count(ls.idCpsn) pendientes FROM LiberacionSolicitadaCpsn ls "
                + "WHERE ls.idCpsn BETWEEN :cpsnMin AND :cpsnMax "
                + "AND (ls.estatus = :estatusP OR ls.estatus = :estatusR) AND ls.fechaImplementacion >= :fecha";
        TypedQuery<Long> tQuery = getEntityManager().createQuery(jpql, Long.class);

        tQuery.setParameter("cpsnMin", cpsnMin);
        tQuery.setParameter("cpsnMax", cpsnMax);
        tQuery.setParameter("estatusP", estatusP);
        tQuery.setParameter("estatusR", estatusR);
        tQuery.setParameter("fecha", fecha);

        return tQuery.getSingleResult() != 0;

    }

}
