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
import mx.ift.sns.dao.ng.ISolicitudCesionNgDao;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.ng.Cesion;
import mx.ift.sns.modelo.ng.CesionSolicitadaNg;
import mx.ift.sns.modelo.ng.SolicitudCesionNg;
import mx.ift.sns.modelo.oficios.Oficio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que implementa la interfaz de solicitudes de cesión.
 */
@Named
public class SolicitudCesionNgDaoImpl extends BaseDAO<SolicitudCesionNg> implements ISolicitudCesionNgDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudCesionNgDaoImpl.class);

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
    public List<SolicitudCesionNg> findAllSolicitudesCesion() {
        String consulta = "SELECT sl FROM SolicitudCesionNg sl";
        TypedQuery<SolicitudCesionNg> query = getEntityManager().createQuery(consulta, SolicitudCesionNg.class);
        List<SolicitudCesionNg> result = query.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Solicitudes encontradas: " + result.size());
        }
        return result;
    }

    @Override
    public List<SolicitudCesionNg> findAllSolicitudesCesion(FiltroBusquedaSolicitudes pFiltrosSolicitud) {

        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosSolicitudCesionNg();

        // Criteria Builder
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        // Query SELECT sl FROM SolicitudCesionNg sl LEFT JOIN CesionesSolicita cl
        CriteriaQuery<SolicitudCesionNg> cQueyData = cb.createQuery(SolicitudCesionNg.class);
        CriteriaQuery<BigDecimal> cQueryIds = cb.createQuery(BigDecimal.class);
        Root<SolicitudCesionNg> sl = cQueyData.from(SolicitudCesionNg.class);
        Join<SolicitudCesionNg, CesionSolicitadaNg> cl = sl.join("cesionesSolicitadas", JoinType.LEFT);

        // WHERE
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
            TypedQuery<SolicitudCesionNg> tQueryData = getEntityManager().createQuery(cQueyData);

            // Hacemos los fecth para mostrar la información de las tablas cruzadas, se haya buscado
            // algo en ellas o no.
            tQueryData.setHint("eclipselink.join-fetch", "sl.cesionesSolicitadas");

            List<SolicitudCesionNg> result = tQueryData.getResultList();
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
            return new ArrayList<SolicitudCesionNg>(1);
        }
    }

    @Override
    public int findAllSolicitudesCesionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosSolicitudCesionNg();

        // Query SELECT sl FROM SolicitudCesionNg sl LEFT JOIN CesionesSolicita cl
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<SolicitudCesionNg> sl = cQuery.from(SolicitudCesionNg.class);
        Join<SolicitudCesionNg, CesionSolicitadaNg> cl = sl.join("cesionesSolicitadas", JoinType.LEFT);

        // WHERE
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
    public SolicitudCesionNg getSolicitudCesionById(BigDecimal pConsecutivo) {
        return getEntityManager().find(SolicitudCesionNg.class, pConsecutivo);
    }

    @SuppressWarnings("unused")
    @Override
    public SolicitudCesionNg getSolicitudCesionEagerLoad(SolicitudCesionNg pSolicitud) {
        // Select SL From SolicitudCesionNg
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<SolicitudCesionNg> cQuery = criteriaBuilder.createQuery(SolicitudCesionNg.class);

        // Left Join
        Root<SolicitudCesionNg> sl = cQuery.from(SolicitudCesionNg.class);
        Fetch<SolicitudCesionNg, CesionSolicitadaNg> joinLibSol = sl.fetch("cesionesSolicitadas", JoinType.LEFT);
        Fetch<SolicitudCesionNg, Cesion> fetchLib = sl.fetch("cesionesAplicadas", JoinType.LEFT);
        Fetch<SolicitudCesionNg, Oficio> fetchOfi = sl.fetch("oficios", JoinType.LEFT);

        // Where
        ParameterExpression<BigDecimal> idSol = criteriaBuilder.parameter(BigDecimal.class);
        cQuery.where(criteriaBuilder.equal(sl.get("id"), idSol)).distinct(true);

        TypedQuery<SolicitudCesionNg> typedQuery = getEntityManager().createQuery(cQuery.select(sl));
        typedQuery.setParameter(idSol, pSolicitud.getId());

        // Distinct = true
        SolicitudCesionNg solicitud = typedQuery.getSingleResult();
        getEntityManager().refresh(solicitud);

        return solicitud;
    }
}
