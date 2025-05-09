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
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.cpsn.ISolicitudCpsnDao;
import mx.ift.sns.modelo.cpsn.VConsultaGenericaCpsn;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCpsn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que implementa la interfaz de solicitudes CPSN.
 */
@Named
public class SolicitudCpsnDaoImpl extends BaseDAO<VConsultaGenericaCpsn> implements ISolicitudCpsnDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudCpsnDaoImpl.class);

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
    public List<VConsultaGenericaCpsn> findAllSolicitudes(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud) {
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosConsultaGenericaCpsn();

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        CriteriaQuery<VConsultaGenericaCpsn> cQueyData = cb.createQuery(VConsultaGenericaCpsn.class);
        Root<VConsultaGenericaCpsn> sl = cQueyData.from(VConsultaGenericaCpsn.class);

        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = null;
            if (filtro.getPrefijo().equals("sl")) {
                pred = this.getPredicateFromFiltro(filtro, sl, cb);
            }
            wherePredicate = cb.and(wherePredicate, pred);
        }

        // Si hay que cruzar por varias tablas la paginación falla ya que se haría un
        // distinct sobre el resultado de la paginación y el resultado no sería válido.
        // Por eso, buscamos primero la lista de id's que cuadran y sobre ellos
        // hacemos la paginación para traer la información.

        // Primero seleccionamos todos los Identificadores de Solicitud que cumplen el WHERE
        CriteriaQuery<BigDecimal> cQueryIds = cb.createQuery(BigDecimal.class);
        cQueryIds.where(wherePredicate).select(sl.<BigDecimal> get("consecutivo")).distinct(true);
        cQueryIds.orderBy(cb.asc(sl.get("consecutivo")));
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

            cQueyData.select(sl).distinct(true);
            cQueyData.where(wherePredicate).where(sl.get("consecutivo").in(listaIds));
            cQueyData.orderBy(cb.asc(sl.get("consecutivo")));
            TypedQuery<VConsultaGenericaCpsn> tQueryData = getEntityManager().createQuery(cQueyData);

            List<VConsultaGenericaCpsn> result = tQueryData.getResultList();
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
            return new ArrayList<VConsultaGenericaCpsn>(1);
        }

    }

    @Override
    public List<VConsultaGenericaCpsn> findAllSolicitudesCpsn() {
        String consulta = "SELECT sl FROM VConsultaGenericaCpsn sl";
        TypedQuery<VConsultaGenericaCpsn> query = getEntityManager().createQuery(consulta, VConsultaGenericaCpsn.class);
        List<VConsultaGenericaCpsn> result = query.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Solicitudes encontradas: " + result.size());
        }
        return result;
    }

    @Override
    public Integer findAllSolicitudesCount(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud) {
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosConsultaGenericaCpsn();

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<VConsultaGenericaCpsn> sl = cQuery.from(VConsultaGenericaCpsn.class);

        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = null;
            if (filtro.getPrefijo().equals("sl")) {
                pred = this.getPredicateFromFiltro(filtro, sl, cb);
            }

            wherePredicate = cb.and(wherePredicate, pred);
        }
        cQuery.where(wherePredicate);

        TypedQuery<Long> tQuery = getEntityManager().
                createQuery(cQuery.select(cb.countDistinct(sl.get("consecutivo"))));
        int rowCount = tQuery.getSingleResult().intValue();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Solicitudes contadas: " + rowCount);
        }

        return rowCount;
    }
}
