package mx.ift.sns.dao.ng.implementation;

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
import mx.ift.sns.dao.ng.ISolicitudConsolidacionDao;
import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.abn.PoblacionAbn;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.ng.AbnConsolidar;
import mx.ift.sns.modelo.ng.SolicitudConsolidacion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que implementa la interfaz de solicitudes de consolidación.
 */
@Named
public class SolicitudConsolidacionDaoImpl extends BaseDAO<SolicitudConsolidacion>
        implements ISolicitudConsolidacionDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudConsolidacionDaoImpl.class);

    /**
     * Traduce un filtro a un Predicado de CriteriaQuery.
     * @param pFiltro Información del Filtro
     * @param pFrom Tabla a la que se aplica el filtro
     * @param pCriteriaBuilder CriteriaBuilder en uso
     * @param sFrom Tabla a la que se aplica el filtro
     * @return Predicate
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Predicate getPredicateFromFiltro(FiltroBusqueda pFiltro, From pFrom, CriteriaBuilder pCriteriaBuilder,
            From sFrom) {

        if (pFiltro.getClase().equals(Date.class)) {
            Path<Date> comparableDate = pFrom.get(pFiltro.getCampo());
            if (pFiltro.isFechaDesde()) {
                return pCriteriaBuilder.greaterThanOrEqualTo(comparableDate, (java.util.Date) pFiltro.getValor());
            } else if (pFiltro.isFechaHasta()) {
                return pCriteriaBuilder.lessThan(comparableDate, (java.util.Date) pFiltro.getValor());
            } else {
                return pCriteriaBuilder.equal(comparableDate, (java.util.Date) pFiltro.getValor());
            }
        } else if (pFiltro.isDistinto()) {
            return pCriteriaBuilder.notEqual(pFrom.get(pFiltro.getCampo()), pFiltro.getValor());
        } else if (pFiltro.isPoblacion()) {
            return pCriteriaBuilder.or(pCriteriaBuilder.equal(pFrom.get(pFiltro.getCampo()), pFiltro.getValor()),
                    pCriteriaBuilder.equal(pFrom.get(pFiltro.getCampoSecundario()), pFiltro.getValorSecundario()));
        } else if (pFiltro.isEstadoMun()) {
            Predicate predicado = pCriteriaBuilder.equal(pFrom.get("inegi").get("municipio").get("estado"),
                    pFiltro.getValor());
            return pCriteriaBuilder.or(
                    pCriteriaBuilder.and(pCriteriaBuilder.equal(pFrom.get("abn"), sFrom.get("abnEntrega")), predicado),
                    pCriteriaBuilder.and(pCriteriaBuilder.equal(pFrom.get("abn"), sFrom.get("abnRecibe")), predicado));
        } else if (pFiltro.isMunicipio()) {
            Predicate predicado = pCriteriaBuilder.equal(pFrom.get("inegi").get("municipio"), pFiltro.getValor());
            return pCriteriaBuilder.or(
                    pCriteriaBuilder.and(pCriteriaBuilder.equal(pFrom.get("abn"), sFrom.get("abnEntrega")), predicado),
                    pCriteriaBuilder.and(pCriteriaBuilder.equal(pFrom.get("abn"), sFrom.get("abnRecibe")), predicado));
        } else {
            return pCriteriaBuilder.equal(pFrom.get(pFiltro.getCampo()), pFiltro.getValor());
        }
    }

    @Override
    public List<SolicitudConsolidacion> findAllSolicitudesConsolidacion() {
        String consulta = "SELECT sl FROM SolicitudConsolidacion sl";
        TypedQuery<SolicitudConsolidacion> query = getEntityManager()
                .createQuery(consulta, SolicitudConsolidacion.class);
        List<SolicitudConsolidacion> result = query.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Solicitudes encontradas: " + result.size());
        }
        return result;
    }

    @Override
    public List<SolicitudConsolidacion> findAllSolicitudesConsolidacion(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosSolicitudConsolidacion();

        // Query SELECT sl FROM SolicitudConsolidacion sl LEFT JOIN AbnConsolidar ac
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        CriteriaQuery<SolicitudConsolidacion> cQueyData = cb.createQuery(SolicitudConsolidacion.class);
        CriteriaQuery<BigDecimal> cQueryIds = cb.createQuery(BigDecimal.class);

        Root<SolicitudConsolidacion> sl = cQueyData.from(SolicitudConsolidacion.class);
        Root<PoblacionAbn> pa = cQueyData.from(PoblacionAbn.class);
        Join<SolicitudConsolidacion, AbnConsolidar> ac = sl.join("abnsConsolidados", JoinType.LEFT);
        Join<SolicitudConsolidacion, Abn> ea = sl.join("abnEntrega", JoinType.LEFT);
        Join<SolicitudConsolidacion, Abn> ear = sl.join("abnRecibe", JoinType.LEFT);

        // WHERE
        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = null;
            if (filtro.getPrefijo().equals("sl")) {
                pred = this.getPredicateFromFiltro(filtro, sl, cb, null);
            } else if (filtro.getPrefijo().equals("ea")) {
                pred = this.getPredicateFromFiltro(filtro, ea, cb, null);
            } else if (filtro.getPrefijo().equals("ear")) {
                pred = this.getPredicateFromFiltro(filtro, ear, cb, null);
            } else if (filtro.getPrefijo().equals("pa")) {
                pred = this.getPredicateFromFiltro(filtro, pa, cb, sl);
            } else {
                pred = this.getPredicateFromFiltro(filtro, ac, cb, null);
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
            TypedQuery<SolicitudConsolidacion> tQueryData = getEntityManager().createQuery(cQueyData);

            // Hacemos los fecth para mostrar la información de las tablas cruzadas, se haya buscado
            // algo en ellas o no.
            tQueryData.setHint("eclipselink.refresh", "True");
            tQueryData.setHint("eclipselink.join-fetch", "sl.abnsConsolidados");

            List<SolicitudConsolidacion> result = tQueryData.getResultList();

            if (LOGGER.isDebugEnabled()) {
                StringBuffer sbTraza = new StringBuffer();
                sbTraza.append("Solicitudes encontrados: ").append(result.size());
                sbTraza.append(", paginación: ").append(pFiltrosSolicitud.isUsarPaginacion());
                if (pFiltrosSolicitud.isUsarPaginacion()) {
                    sbTraza.append(" (Página: ").append(pFiltrosSolicitud.getNumeroPagina());
                    sbTraza.append(", MaxResult: ").append(pFiltrosSolicitud.getResultadosPagina()).append(")");
                }
                LOGGER.debug(sbTraza.toString());
            }

            return result;
        } else {
            return new ArrayList<SolicitudConsolidacion>(1);
        }

    }

    @Override
    public int findAllSolicitudesConsolidacionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosSolicitudConsolidacion();

        // Query SELECT sl FROM SolicitudConsolidacion sl LEFT JOIN AbnConsolidar ac
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<SolicitudConsolidacion> sl = cQuery.from(SolicitudConsolidacion.class);
        Root<PoblacionAbn> pa = cQuery.from(PoblacionAbn.class);
        Join<SolicitudConsolidacion, AbnConsolidar> ac = sl.join("abnsConsolidados", JoinType.LEFT);
        Join<SolicitudConsolidacion, Abn> ea = sl.join("abnEntrega", JoinType.LEFT);
        Join<SolicitudConsolidacion, Abn> ear = sl.join("abnRecibe", JoinType.LEFT);

        // WHERE
        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = null;
            if (filtro.getPrefijo().equals("sl")) {
                pred = this.getPredicateFromFiltro(filtro, sl, cb, null);
            } else if (filtro.getPrefijo().equals("ea")) {
                pred = this.getPredicateFromFiltro(filtro, ea, cb, null);
            } else if (filtro.getPrefijo().equals("ear")) {
                pred = this.getPredicateFromFiltro(filtro, ear, cb, null);
            } else if (filtro.getPrefijo().equals("pa")) {
                pred = this.getPredicateFromFiltro(filtro, pa, cb, sl);
            } else {
                pred = this.getPredicateFromFiltro(filtro, ac, cb, null);
            }
            wherePredicate = cb.and(wherePredicate, pred);
        }
        cQuery.where(wherePredicate);

        TypedQuery<Long> tQuery = getEntityManager().createQuery(cQuery.select(cb.countDistinct(sl)));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(tQuery.toString());
        }
        int rowCount = tQuery.getSingleResult().intValue();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Solicitudes contadas: " + rowCount);
        }

        return rowCount;
    }

    @SuppressWarnings("unused")
    @Override
    public SolicitudConsolidacion getSolicitudConsolidacionEagerLoad(SolicitudConsolidacion pSolicitud) {
        // Select SL From SolicitudConsolidacion
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<SolicitudConsolidacion> cQuery = criteriaBuilder.createQuery(SolicitudConsolidacion.class);

        // Left Join
        Root<SolicitudConsolidacion> sl = cQuery.from(SolicitudConsolidacion.class);
        Fetch<SolicitudConsolidacion, AbnConsolidar> j = sl.fetch("abnsConsolidados", JoinType.LEFT);

        // Where
        ParameterExpression<BigDecimal> idSol = criteriaBuilder.parameter(BigDecimal.class);
        cQuery.where(criteriaBuilder.equal(sl.get("id"), idSol)).distinct(true);

        TypedQuery<SolicitudConsolidacion> typedQuery = getEntityManager().createQuery(cQuery.select(sl));
        typedQuery.setParameter(idSol, pSolicitud.getId());

        SolicitudConsolidacion solicitud = typedQuery.getSingleResult();
        getEntityManager().refresh(solicitud);

        return solicitud;
    }

    @Override
    public SolicitudConsolidacion getSolicitudConsolidacionById(BigDecimal pConsecutivo) {
        return getEntityManager().find(SolicitudConsolidacion.class, pConsecutivo);
    }
}
