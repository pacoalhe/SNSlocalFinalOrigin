package mx.ift.sns.dao.cpsi.implementacion;

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
import mx.ift.sns.dao.cpsi.ISolicitudLiberacionCpsiDao;
import mx.ift.sns.modelo.cpsi.LiberacionSolicitadaCpsi;
import mx.ift.sns.modelo.cpsi.SolicitudLiberacionCpsi;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCPSI;
import mx.ift.sns.modelo.oficios.Oficio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que implementa la interfaz de solicitudes de liberación CPSI.
 */
@Named
public class SolicitudLiberacionCpsiDaoImpl extends BaseDAO<SolicitudLiberacionCpsi> implements
        ISolicitudLiberacionCpsiDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudLiberacionCpsiDaoImpl.class);

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
    public List<SolicitudLiberacionCpsi> findAllSolicitudesLiberacion() {
        String query = "SELECT sl FROM SolicitudLiberacionCpsi sl ORDER BY sl.id";
        TypedQuery<SolicitudLiberacionCpsi> tQuery =
                this.getEntityManager().createQuery(query, SolicitudLiberacionCpsi.class);
        List<SolicitudLiberacionCpsi> result = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} solicitudes de liberación de CPSI.", result.size());
        }

        return result;
    }

    @Override
    public List<SolicitudLiberacionCpsi> findAllSolicitudesLiberacion(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud) {
        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosSolicitudLiberacionCpsi();

        // Criteria Builder
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        // Query SELECT sl FROM SolicitudLiberacionCpsi sl LEFT JOIN LiberacionSolicitadaCpsi ls
        CriteriaQuery<SolicitudLiberacionCpsi> cQueyData = cb.createQuery(SolicitudLiberacionCpsi.class);
        CriteriaQuery<BigDecimal> cQueryIds = cb.createQuery(BigDecimal.class);
        Root<SolicitudLiberacionCpsi> sl = cQueyData.from(SolicitudLiberacionCpsi.class);
        Join<SolicitudLiberacionCpsi, LiberacionSolicitadaCpsi> ls = sl.join("liberacionesSolicitadas",
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
            TypedQuery<SolicitudLiberacionCpsi> tQueryData = getEntityManager().createQuery(cQueyData);

            // Hacemos los fecth para mostrar la información de las tablas cruzadas, se haya buscado
            // algo en ellas o no.
            tQueryData.setHint("eclipselink.join-fetch", "sl.liberacionesSolicitadas");

            List<SolicitudLiberacionCpsi> result = tQueryData.getResultList();
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
            // Lista vacía.
            return new ArrayList<SolicitudLiberacionCpsi>(1);
        }
    }

    @Override
    public int findAllSolicitudesLiberacionCount(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud) {
        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosSolicitudLiberacionCpsi();

        // Query SELECT COUNT(sl) FROM SolicitudLiberacionCpsi sl LEFT JOIN LiberacionSolicitadaCpsi ls
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<SolicitudLiberacionCpsi> sl = cQuery.from(SolicitudLiberacionCpsi.class);
        Join<SolicitudLiberacionCpsi, LiberacionSolicitadaCpsi> ls = sl.join("liberacionesSolicitadas", JoinType.LEFT);

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
    public SolicitudLiberacionCpsi getSolicitudLiberacionById(BigDecimal pConsecutivo) {
        return this.getEntityManager().find(SolicitudLiberacionCpsi.class, pConsecutivo);
    }

    @SuppressWarnings("unused")
    @Override
    public SolicitudLiberacionCpsi getSolicitudLiberacionEagerLoad(SolicitudLiberacionCpsi pSolicitud) {
        // Select SL From SolicitudLiberacionCpsi
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<SolicitudLiberacionCpsi> cQuery = criteriaBuilder.createQuery(SolicitudLiberacionCpsi.class);

        // Left Join
        Root<SolicitudLiberacionCpsi> sl = cQuery.from(SolicitudLiberacionCpsi.class);
        Fetch<SolicitudLiberacionCpsi, LiberacionSolicitadaCpsi> joinLibSol = sl.fetch("liberacionesSolicitadas",
                JoinType.LEFT);
        Fetch<SolicitudLiberacionCpsi, Oficio> fetchOfi = sl.fetch("oficios", JoinType.LEFT);

        // Where
        ParameterExpression<BigDecimal> idSol = criteriaBuilder.parameter(BigDecimal.class);
        cQuery.where(criteriaBuilder.equal(sl.get("id"), idSol)).distinct(true);
        cQuery.select(sl);

        TypedQuery<SolicitudLiberacionCpsi> typedQuery = getEntityManager().createQuery(cQuery);
        typedQuery.setParameter(idSol, pSolicitud.getId());

        // Distinct = true
        SolicitudLiberacionCpsi solicitud = typedQuery.getSingleResult();
        getEntityManager().refresh(solicitud);

        return solicitud;
    }

}
