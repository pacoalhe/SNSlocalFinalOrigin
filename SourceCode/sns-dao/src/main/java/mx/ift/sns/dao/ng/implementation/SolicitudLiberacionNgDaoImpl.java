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
import mx.ift.sns.dao.ng.ISolicitudLiberacionNgDao;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.ng.Liberacion;
import mx.ift.sns.modelo.ng.LiberacionSolicitadaNg;
import mx.ift.sns.modelo.ng.SolicitudLiberacionNg;
import mx.ift.sns.modelo.oficios.Oficio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que implementa la interfaz de solicitudes de liberación.
 */
@Named
public class SolicitudLiberacionNgDaoImpl extends BaseDAO<SolicitudLiberacionNg> implements ISolicitudLiberacionNgDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudLiberacionNgDaoImpl.class);

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
    public List<SolicitudLiberacionNg> findAllSolicitudesLiberacion() {

        String consulta = "SELECT sl FROM SolicitudLiberacionNg sl";
        TypedQuery<SolicitudLiberacionNg> query = getEntityManager().createQuery(consulta, SolicitudLiberacionNg.class);

        List<SolicitudLiberacionNg> retult = query.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Solicitudes encontradas: " + retult.size());
        }

        return retult;
    }

    @Override
    public List<SolicitudLiberacionNg> findAllSolicitudesLiberacion(FiltroBusquedaSolicitudes pFiltrosSolicitud) {

        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosSolicitudLiberacionNg();

        // Criteria Builder
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        // Query SELECT sl FROM SolicitudLiberacionNg sl LEFT JOIN LiberacionSolicitadaNg ls
        CriteriaQuery<SolicitudLiberacionNg> cQueyData = cb.createQuery(SolicitudLiberacionNg.class);
        CriteriaQuery<BigDecimal> cQueryIds = cb.createQuery(BigDecimal.class);
        Root<SolicitudLiberacionNg> sl = cQueyData.from(SolicitudLiberacionNg.class);
        Join<SolicitudLiberacionNg, LiberacionSolicitadaNg> ls = sl.join("liberacionesSolicitadas", JoinType.LEFT);

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
            TypedQuery<SolicitudLiberacionNg> tQueryData = getEntityManager().createQuery(cQueyData);

            // Hacemos los fecth para mostrar la información de las tablas cruzadas, se haya buscado
            // algo en ellas o no.
            tQueryData.setHint("eclipselink.join-fetch", "sl.liberacionesSolicitadas");

            List<SolicitudLiberacionNg> result = tQueryData.getResultList();
            if (LOGGER.isDebugEnabled()) {
                StringBuffer sbTraza = new StringBuffer();
                sbTraza.append("Solicitudes NG encontradas: ").append(result.size());
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
            return new ArrayList<SolicitudLiberacionNg>(1);
        }
    }

    @Override
    public int findAllSolicitudesLiberacionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosSolicitudLiberacionNg();

        // Query SELECT sl FROM SolicitudLiberacionNg sl LEFT JOIN LiberacionSolicitadaNg ls
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<SolicitudLiberacionNg> sl = cQuery.from(SolicitudLiberacionNg.class);
        Join<SolicitudLiberacionNg, LiberacionSolicitadaNg> ls = sl.join("liberacionesSolicitadas", JoinType.LEFT);

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

    // @Override
    // public void removeSolicitudLiberacionCascadeForced(SolicitudLiberacionNg pSolicitud) {
    // String queryLib = "DELETE FROM Liberacion l WHERE l.solicitudLiberacion = :solLib";
    // String queryLibSol = "DELETE FROM LiberacionSolicitadaNg ls WHERE ls.solicitudLiberacion = :solLib";
    // String queryOfi = "DELETE FROM Oficio o WHERE o.solicitud = :solLib";
    // String querySolLib = "DELETE FROM SolicitudLiberacionNg sl WHERE sl.id = :idSolLib";
    //
    // TypedQuery<Liberacion> tQueryLib = getEntityManager().createQuery(queryLib, Liberacion.class);
    // tQueryLib.setParameter("solLib", pSolicitud);
    //
    // TypedQuery<LiberacionSolicitadaNg> tQueryLibSol = getEntityManager().createQuery(queryLibSol,
    // LiberacionSolicitadaNg.class);
    // tQueryLibSol.setParameter("solLib", pSolicitud);
    //
    // TypedQuery<Oficio> tQueryOfi = getEntityManager().createQuery(queryOfi, Oficio.class);
    // tQueryOfi.setParameter("solLib", pSolicitud);
    //
    // TypedQuery<SolicitudLiberacionNg> tQuerySolLib = getEntityManager().createQuery(querySolLib,
    // SolicitudLiberacionNg.class);
    // tQuerySolLib.setParameter("idSolLib", pSolicitud.getId());
    //
    // int rows = tQueryLib.executeUpdate();
    // if (LOGGER.isDebugEnabled()) {
    // LOGGER.debug("Filas Liberacion eliminadas: " + rows);
    // }
    //
    // rows = tQueryLibSol.executeUpdate();
    // if (LOGGER.isDebugEnabled()) {
    // LOGGER.debug("Filas LiberacionSolicitadaNg eliminadas: " + rows);
    // }
    //
    // rows = tQueryOfi.executeUpdate();
    // if (LOGGER.isDebugEnabled()) {
    // LOGGER.debug("Filas Oficio eliminadas: " + rows);
    // }
    //
    // rows = tQuerySolLib.executeUpdate();
    // if (LOGGER.isDebugEnabled()) {
    // LOGGER.debug("Filas SolicitudLiberacionNg eliminadas: " + rows);
    // }
    // }

    @Override
    public SolicitudLiberacionNg getSolicitudLiberacionById(BigDecimal pConsecutivo) {
        return getEntityManager().find(SolicitudLiberacionNg.class, pConsecutivo);
    }

    @SuppressWarnings("unused")
    @Override
    public SolicitudLiberacionNg getSolicitudLiberacionEagerLoad(SolicitudLiberacionNg pSolicitud) {
        // Select SL From SolicitudLiberacionNg
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<SolicitudLiberacionNg> cQuery = criteriaBuilder.createQuery(SolicitudLiberacionNg.class);

        // Left Join
        Root<SolicitudLiberacionNg> sl = cQuery.from(SolicitudLiberacionNg.class);
        Fetch<SolicitudLiberacionNg, LiberacionSolicitadaNg> joinLibSol =
                sl.fetch("liberacionesSolicitadas", JoinType.LEFT);
        Fetch<SolicitudLiberacionNg, Liberacion> fetchLib = sl.fetch("liberacionesAplicadas", JoinType.LEFT);
        Fetch<SolicitudLiberacionNg, Oficio> fetchOfi = sl.fetch("oficios", JoinType.LEFT);

        // Where
        ParameterExpression<BigDecimal> idSol = criteriaBuilder.parameter(BigDecimal.class);
        cQuery.where(criteriaBuilder.equal(sl.get("id"), idSol)).distinct(true);

        TypedQuery<SolicitudLiberacionNg> typedQuery = getEntityManager().createQuery(cQuery.select(sl));
        typedQuery.setParameter(idSol, pSolicitud.getId());

        // Distinct = true
        SolicitudLiberacionNg solicitud = typedQuery.getSingleResult();
        getEntityManager().refresh(solicitud);

        return solicitud;
    }

}
