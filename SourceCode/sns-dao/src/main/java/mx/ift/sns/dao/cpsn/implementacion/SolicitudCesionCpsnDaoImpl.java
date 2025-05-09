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
import mx.ift.sns.dao.cpsn.ISolicitudCesionCPSNDao;
import mx.ift.sns.modelo.cpsn.CesionSolicitadaCPSN;
import mx.ift.sns.modelo.cpsn.SolicitudCesionCPSN;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCpsn;
import mx.ift.sns.modelo.oficios.Oficio;
import mx.ift.sns.modelo.solicitud.EstadoCesionSolicitada;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que implementa la interfaz de solicitudes de cesión cpsn.
 */
@Named
public class SolicitudCesionCpsnDaoImpl extends BaseDAO<SolicitudCesionCPSN> implements ISolicitudCesionCPSNDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudCesionCpsnDaoImpl.class);

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
    public List<SolicitudCesionCPSN> findAllSolicitudesCesionCPSN() {
        String consulta = "SELECT sl FROM SolicitudCesionCPSN sl";
        TypedQuery<SolicitudCesionCPSN> query = getEntityManager().createQuery(consulta, SolicitudCesionCPSN.class);
        List<SolicitudCesionCPSN> result = query.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Solicitudes encontradas: " + result.size());
        }
        return result;
    }

    @Override
    public List<SolicitudCesionCPSN> findAllSolicitudesCesionCPSN(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud) {
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosSolicitudCesionCpsn();

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        CriteriaQuery<SolicitudCesionCPSN> cQueyData = cb.createQuery(SolicitudCesionCPSN.class);
        Root<SolicitudCesionCPSN> sl = cQueyData.from(SolicitudCesionCPSN.class);
        Join<SolicitudCesionCPSN, CesionSolicitadaCPSN> cl = sl.join("cesionesSolicitadasCPSN", JoinType.LEFT);

        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = null;
            if (filtro.getPrefijo().equals("sl")) {
                pred = this.getPredicateFromFiltro(filtro, sl, cb);
            } else {
                pred = this.getPredicateFromFiltro(filtro, cl, cb);
            }
            wherePredicate = cb.and(wherePredicate, pred);
        }

        CriteriaQuery<BigDecimal> cQueryIds = cb.createQuery(BigDecimal.class);
        cQueryIds.where(wherePredicate).select(sl.<BigDecimal> get("id")).distinct(true);
        cQueryIds.orderBy(cb.asc(sl.get("id")));
        TypedQuery<BigDecimal> tQueryIds = getEntityManager().createQuery(cQueryIds);

        // Lista de ids de Solicitudes que cumplen los criterios de búsqueda.
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
            TypedQuery<SolicitudCesionCPSN> tQueryData = getEntityManager().createQuery(cQueyData);

            // Hacemos los fecth para mostrar la información de las tablas cruzadas, se haya buscado
            // algo en ellas o no.
            tQueryData.setHint("eclipselink.join-fetch", "sl.cesionesSolicitadasCPSN");

            List<SolicitudCesionCPSN> result = tQueryData.getResultList();
            if (LOGGER.isDebugEnabled()) {
                StringBuffer sbTraza = new StringBuffer();
                sbTraza.append("Solicitudes encontradas: ").append(result.size());
                sbTraza.append(", paginación: ").append(pFiltrosSolicitud.isUsarPaginacion());
                if (pFiltrosSolicitud.isUsarPaginacion()) {
                    sbTraza.append(" (Página: ").append(pFiltrosSolicitud.getNumeroPagina());
                    sbTraza.append(", MaxResult: ").append(pFiltrosSolicitud.getResultadosPagina()).append(")");
                }
                LOGGER.debug(sbTraza.toString());
                // LOGGER.debug(tQueryData.toString());
            }

            return result;
        } else {
            return new ArrayList<SolicitudCesionCPSN>(1);
        }

    }

    @Override
    public int findAllSolicitudesCesionCPSNCount(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud) {
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosSolicitudCesionCpsn();

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<SolicitudCesionCPSN> sl = cQuery.from(SolicitudCesionCPSN.class);
        Join<SolicitudCesionCPSN, CesionSolicitadaCPSN> cl = sl.join("cesionesSolicitadasCPSN", JoinType.LEFT);

        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = null;
            if (filtro.getPrefijo().equals("sl")) {
                pred = this.getPredicateFromFiltro(filtro, sl, cb);
            } else {
                pred = this.getPredicateFromFiltro(filtro, cl, cb);
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
    public SolicitudCesionCPSN getSolicitudCesionCPSNById(BigDecimal pConsecutivo) {
        return getEntityManager().find(SolicitudCesionCPSN.class, pConsecutivo);
    }

    @SuppressWarnings("unused")
    @Override
    public SolicitudCesionCPSN getSolicitudCesionCPSNEagerLoad(SolicitudCesionCPSN pSolicitud) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<SolicitudCesionCPSN> cQuery = criteriaBuilder.createQuery(SolicitudCesionCPSN.class);

        // Left Join
        Root<SolicitudCesionCPSN> sl = cQuery.from(SolicitudCesionCPSN.class);
        Fetch<SolicitudCesionCPSN, CesionSolicitadaCPSN> joinCesCPSNSol =
                sl.fetch("cesionesSolicitadasCPSN", JoinType.LEFT);
        Fetch<SolicitudCesionCPSN, Oficio> fetchOfi = sl.fetch("oficios", JoinType.LEFT);

        // Where
        ParameterExpression<BigDecimal> idSol = criteriaBuilder.parameter(BigDecimal.class);
        cQuery.where(criteriaBuilder.equal(sl.get("id"), idSol)).distinct(true);

        TypedQuery<SolicitudCesionCPSN> typedQuery = getEntityManager().createQuery(cQuery.select(sl));
        typedQuery.setParameter(idSol, pSolicitud.getId());

        // Distinct = true
        SolicitudCesionCPSN solicitud = typedQuery.getSingleResult();
        getEntityManager().refresh(solicitud);

        return solicitud;
    }

    @Override
    public boolean cpsnEnCesionPendiente(Integer cpsnMin, Integer cpsnMax, EstadoCesionSolicitada estatus, Date fecha) {
        String jpql = "SELECT count(cs.idCpsn) pendientes FROM CesionSolicitadaCPSN cs "
                + "WHERE cs.idCpsn BETWEEN :cpsnMin AND :cpsnMax "
                + "AND cs.estatus = :estatus AND cs.fechaImplementacion >= :fecha";
        TypedQuery<Long> tQuery = getEntityManager().createQuery(jpql, Long.class);

        tQuery.setParameter("cpsnMin", cpsnMin);
        tQuery.setParameter("cpsnMax", cpsnMax);
        tQuery.setParameter("estatus", estatus);
        tQuery.setParameter("fecha", fecha);

        return tQuery.getSingleResult() != 0;

    }
}
