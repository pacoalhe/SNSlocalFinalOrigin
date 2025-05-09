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
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.ISolicitudAsignacionDao;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.ng.NumeracionSolicitada;
import mx.ift.sns.modelo.ng.SolicitudAsignacion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de Solicitudes de Asignación.
 */
@Named
public class SolicitudAsignacionDaoImpl extends BaseDAO<SolicitudAsignacion> implements ISolicitudAsignacionDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudAsignacionDaoImpl.class);

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
    public List<SolicitudAsignacion> findAllSolicitudesAsignacion() {
        String consulta = "SELECT sl FROM SolicitudAsignacion sl";
        TypedQuery<SolicitudAsignacion> query = getEntityManager().createQuery(consulta, SolicitudAsignacion.class);
        List<SolicitudAsignacion> result = query.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Solicitudes encontradas: " + result.size());
        }
        return result;
    }

    @Override
    public List<SolicitudAsignacion> findAllSolicitudesAsignacion(FiltroBusquedaSolicitudes pFiltrosSolicitud)
            throws Exception {

        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> pFiltros = pFiltrosSolicitud.getListaFiltrosSolicitudAsignacionNg();

        // Criteria Builder
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        // Query SELECT sl FROM SolicitudAsignacion sl
        CriteriaQuery<SolicitudAsignacion> cQueyData = cb.createQuery(SolicitudAsignacion.class);

        if (pFiltros.isEmpty()) {
            TypedQuery<SolicitudAsignacion> tquery = getEntityManager().createQuery(cQueyData);
            if (pFiltrosSolicitud.isUsarPaginacion()) {
                tquery.setFirstResult(pFiltrosSolicitud.getNumeroPagina()).setMaxResults(
                        pFiltrosSolicitud.getResultadosPagina());
            }
            return tquery.getResultList();

        } else {

            CriteriaQuery<BigDecimal> cQueryIds = cb.createQuery(BigDecimal.class);
            Root<SolicitudAsignacion> sl = cQueyData.from(SolicitudAsignacion.class);
            Join<SolicitudAsignacion, NumeracionSolicitada> ns = sl.join("numeracionSolicitadas", JoinType.LEFT);

            // WHERE
            Predicate wherePredicate = cb.conjunction();
            for (FiltroBusqueda filtro : pFiltros) {
                Predicate pred = null;
                if (filtro.getPrefijo().equals("sl")) {
                    pred = this.getPredicateFromFiltro(filtro, sl, cb);
                } else {
                    pred = this.getPredicateFromFiltro(filtro, ns, cb);
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
                TypedQuery<SolicitudAsignacion> tQueryData = getEntityManager().createQuery(cQueyData);

                // Hacemos los fecth para mostrar la información de las tablas cruzadas, se haya buscado
                // algo en ellas o no.
                tQueryData.setHint("eclipselink.join-fetch", "sl.numeracionSolicitadas");
                // tQueryData.setHint("eclipselink.join-fetch", "sl.oficios");

                List<SolicitudAsignacion> result = tQueryData.getResultList();

                if (LOGGER.isDebugEnabled()) {
                    StringBuffer sbTraza = new StringBuffer();
                    sbTraza.append("Solicitudes encontrados: ").append(result.size());
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
                return new ArrayList<SolicitudAsignacion>(1);
            }
        }
    }

    @Override
    public int findAllSolicitudesAsignacionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) throws Exception {
        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosSolicitudAsignacionNg();

        // Query SELECT sl FROM SolicitudAsignacion sl
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<SolicitudAsignacion> sl = cQuery.from(SolicitudAsignacion.class);
        Join<SolicitudAsignacion, NumeracionSolicitada> ns = sl.join("numeracionSolicitadas", JoinType.LEFT);
        // Join<SolicitudAsignacion, Oficio> of = sl.join("oficios", JoinType.LEFT);

        // WHERE
        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = null;
            if (filtro.getPrefijo().equals("sl")) {
                pred = this.getPredicateFromFiltro(filtro, sl, cb);
            } else {
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

    @Override
    public SolicitudAsignacion getSolicitudAsignacionById(BigDecimal pConsecutivo) {
        return getEntityManager().find(SolicitudAsignacion.class, pConsecutivo);
    }
}
