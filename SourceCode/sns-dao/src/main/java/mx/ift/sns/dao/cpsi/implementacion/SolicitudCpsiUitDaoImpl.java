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
import mx.ift.sns.dao.cpsi.ISolicitudCpsiUitDao;
import mx.ift.sns.modelo.cpsi.CpsiUitDocumento;
import mx.ift.sns.modelo.cpsi.CpsiUitEntregado;
import mx.ift.sns.modelo.cpsi.SolicitudCpsiUit;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCPSI;
import mx.ift.sns.modelo.oficios.Oficio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que implementa la interfaz de solicitudes de códigos CPSI a la UIT.
 */
@Named
public class SolicitudCpsiUitDaoImpl extends BaseDAO<SolicitudCpsiUit> implements
        ISolicitudCpsiUitDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudCpsiUitDaoImpl.class);

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
    public List<SolicitudCpsiUit> findAllSolicitudesCpsiUit() {
        String query = "SELECT sl FROM SolicitudAsignacionCpsi sl ORDER BY sl.id";
        TypedQuery<SolicitudCpsiUit> tQuery =
                this.getEntityManager().createQuery(query, SolicitudCpsiUit.class);
        List<SolicitudCpsiUit> result = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} solicitudes de códigos de CPSI.", result.size());
        }

        return result;
    }

    @SuppressWarnings("unused")
    @Override
    public List<SolicitudCpsiUit> findAllSolicitudesCpsiUit(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud) {

        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosSolicitudCpsiUit();

        // Criteria Builder
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        // Query SELECT sl FROM SolicitudnCpsiUit
        CriteriaQuery<SolicitudCpsiUit> cQueyData = cb.createQuery(SolicitudCpsiUit.class);
        CriteriaQuery<BigDecimal> cQueryIds = cb.createQuery(BigDecimal.class);
        Root<SolicitudCpsiUit> sl = cQueyData.from(SolicitudCpsiUit.class);
        Join<SolicitudCpsiUit, CpsiUitEntregado> joinSol = sl.join("cpsiUitEntregado", JoinType.LEFT);

        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = null;
            pred = this.getPredicateFromFiltro(filtro, sl, cb);
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
            TypedQuery<SolicitudCpsiUit> tQuery = getEntityManager().createQuery(cQueyData);

            // Hacemos los fecth para mostrar la información de las tablas cruzadas, se haya buscado
            // algo en ellas o no.
            tQuery.setHint("eclipselink.join-fetch", "sl.cpsiUitEntregado");

            List<SolicitudCpsiUit> result = tQuery.getResultList();
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
            return new ArrayList<SolicitudCpsiUit>(1);
        }
    }

    @Override
    public int findAllSolicitudesCpsiUitCount(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud) {
        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosSolicitudCpsiUit();

        // Query SELECT COUNT(sl) FROM SolicitudAsignacionCpsi sl
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<SolicitudCpsiUit> sl = cQuery.from(SolicitudCpsiUit.class);

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
    public SolicitudCpsiUit getSolicitudCpsiUitById(BigDecimal pConsecutivo) {
        return this.getEntityManager().find(SolicitudCpsiUit.class, pConsecutivo);
    }

    @SuppressWarnings("unused")
    @Override
    public SolicitudCpsiUit getSolicitudCpsiUitEagerLoad(SolicitudCpsiUit pSolicitud) {
        // Select SL From SolicitudAsignacionCpsi
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<SolicitudCpsiUit> cQuery = criteriaBuilder.createQuery(SolicitudCpsiUit.class);

        // Left Join
        Root<SolicitudCpsiUit> sl = cQuery.from(SolicitudCpsiUit.class);
        Fetch<SolicitudCpsiUit, CpsiUitEntregado> joinSol = sl.fetch("cpsiUitEntregado", JoinType.LEFT);
        Fetch<SolicitudCpsiUit, CpsiUitDocumento> joinSolDoc = sl.fetch("cpsiUitDocumentos", JoinType.LEFT);
        Fetch<SolicitudCpsiUit, Oficio> fetchOfi = sl.fetch("oficios", JoinType.LEFT);

        // Where
        ParameterExpression<BigDecimal> idSol = criteriaBuilder.parameter(BigDecimal.class);
        cQuery.where(criteriaBuilder.equal(sl.get("id"), idSol)).distinct(true);
        cQuery.select(sl);

        TypedQuery<SolicitudCpsiUit> typedQuery = getEntityManager().createQuery(cQuery);
        typedQuery.setParameter(idSol, pSolicitud.getId());

        // Distinct = true
        SolicitudCpsiUit solicitud = typedQuery.getSingleResult();
        getEntityManager().refresh(solicitud);

        return solicitud;
    }

    @Override
    public int findSolicitudCpsiUitByCodAndEstatusCount(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud) {
        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosSolicitudCpsiUit();

        // Query SELECT COUNT(sl) FROM SolicitudAsignacionCpsi sl
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<SolicitudCpsiUit> sl = cQuery.from(SolicitudCpsiUit.class);
        Join<SolicitudCpsiUit, CpsiUitEntregado> ce = sl.join("cpsiUitEntregado", JoinType.LEFT);

        // WHERE
        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = null;
            if (filtro.getPrefijo().equals("sl")) {
                pred = this.getPredicateFromFiltro(filtro, sl, cb);
            } else {
                pred = this.getPredicateFromFiltro(filtro, ce, cb);
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

}
