package mx.ift.sns.dao.solicitud.implementation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Named;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.solicitud.ISolicitudDao;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.ng.Serie;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que implementa la interfaz de solicitudes.
 */
@Named
public class SolicitudDaoImpl extends BaseDAO<Solicitud> implements ISolicitudDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudDaoImpl.class);

    /** Tipo de solicitudes de Numeración Geografica. */
    @SuppressWarnings("unused")
    private static final List<Integer> SOLICITUDES_NG = Arrays.asList(TipoSolicitud.ASIGNACION,
            TipoSolicitud.CESION_DERECHOS,
            TipoSolicitud.CONSOLIDACION,
            TipoSolicitud.LIBERACION,
            TipoSolicitud.LINEAS_ACTIVAS,
            TipoSolicitud.REDISTRIBUCION);

    /** Tipo de solicitudes de Numeración No Geografica. */
    private static final List<Integer> SOLICITUDES_NNG = Arrays.asList(TipoSolicitud.ASIGNACION_NNG,
            TipoSolicitud.CESION_DERECHOS_NNG,
            TipoSolicitud.LIBERACION_NNG,
            TipoSolicitud.REDISTRIBUCION_NNG);

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
        } else if (pFiltro.isDistinto()) {
            return pCriteriaBuilder.notEqual(pFrom.get(pFiltro.getCampo()), pFiltro.getValor());
        } else {
            return pCriteriaBuilder.equal(pFrom.get(pFiltro.getCampo()), pFiltro.getValor());
        }
    }

    @Override
    public List<Solicitud> findAllSolicitudes(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosConsultaGenerica();

        String parameterKeyDate = null;

        StringBuilder sbQuery = new StringBuilder(
                "SELECT distinct(vsp.solicitud) FROM VSolicitudPoblacion vsp ");
        if (!filtros.isEmpty()) {
            sbQuery.append("WHERE ");
        }
        for (int i = 0; i < filtros.size(); i++) {
            if (i > 0) {
                sbQuery.append(" AND ");
            }

            FiltroBusqueda filter = filtros.get(i);
            parameterKeyDate = filter.getCampo().replace("-", "");

            if (filter.getCampo().equals("solicitud")) {
                sbQuery.append(filter.getPrefijo()).append(".").append(filter.getCampo()).append(".id")
                        .append(" = :")
                        .append(filter.getCampo());
            } else if (filter.getValor() instanceof Date) {
                if (filter.getCampo().contains("-")) {
                    if (filter.getCampo().split("-")[0].equals("desde")) {
                        sbQuery.append(filter.getPrefijo()).append(".").append(filter.getCampo().split("-")[1])
                                .append(" >=:")
                                .append(parameterKeyDate);
                    } else if (filter.getCampo().split("-")[0].equals("hasta")) {
                        sbQuery.append(filter.getPrefijo()).append(".").append(filter.getCampo().split("-")[1])
                                .append(" <:")
                                .append(parameterKeyDate);
                    } else {
                        sbQuery.append(filter.getPrefijo()).append(".").append(filter.getCampo().split("-")[1])
                                .append(" =:")
                                .append(filter.getCampo());
                    }
                }
            } else {
                sbQuery.append(filter.getPrefijo()).append(".").append(filter.getCampo()).append(" =:")
                        .append(filter.getCampo());
            }
        }
        LOGGER.debug(sbQuery.toString());

        TypedQuery<Solicitud> query = getEntityManager().createQuery(sbQuery.toString(), Solicitud.class);

        if (pFiltrosSolicitud.isUsarPaginacion()) {
            query.setFirstResult(pFiltrosSolicitud.getNumeroPagina()).setMaxResults(
                    pFiltrosSolicitud.getResultadosPagina());
        }

        for (FiltroBusqueda filter : filtros) {
            parameterKeyDate = filter.getCampo().replace("-", "");

            if (filter.getValor() instanceof Date) {
                query.setParameter(parameterKeyDate, (Date) filter.getValor(), TemporalType.DATE);
            } else {
                query.setParameter(filter.getCampo(), filter.getValor());
            }
        }

        List<Solicitud> lista = query.getResultList();

        return lista;

    }

    @Override
    public int findAllSolicitudesCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosConsultaGenerica();

        String parameterKeyDate = null;
        StringBuilder sbQuery = new StringBuilder(
                "SELECT COUNT(COUNT(1)) FROM VSolicitudPoblacion  vsp ");
        if (!filtros.isEmpty()) {
            sbQuery.append("WHERE ");
        }
        for (int i = 0; i < filtros.size(); i++) {

            if (i > 0) {
                sbQuery.append(" AND ");
            }

            FiltroBusqueda filter = filtros.get(i);
            parameterKeyDate = filter.getCampo().replace("-", "");

            if (filter.getCampo().equals("solicitud")) {
                sbQuery.append(filter.getPrefijo()).append(".").append(filter.getCampo()).append(".id").append(" = :")
                        .append(filter.getCampo());
            } else if (filter.getClase().equals(Date.class)) {
                if (filter.getCampo().contains("-")) {
                    if (filter.getCampo().split("-")[0].equals("desde")) {
                        sbQuery.append(filter.getPrefijo()).append(".").append(filter.getCampo().split("-")[1])
                                .append(" >=:")
                                .append(parameterKeyDate);
                    } else if (filter.getCampo().split("-")[0].equals("hasta")) {
                        sbQuery.append(filter.getPrefijo()).append(".").append(filter.getCampo().split("-")[1])
                                .append(" <:")
                                .append(parameterKeyDate);
                    } else {
                        sbQuery.append(filter.getPrefijo()).append(".").append(filter.getCampo().split("-")[1])
                                .append(" =:")
                                .append(filter.getCampo());
                    }
                }
            } else {
                sbQuery.append(filter.getPrefijo()).append(".").append(filter.getCampo()).append(" = :")
                        .append(filter.getCampo());
            }
        }
        sbQuery.append(" GROUP BY vsp.solicitud");
        LOGGER.debug(sbQuery.toString());

        Query query = getEntityManager().createQuery(sbQuery.toString());

        for (FiltroBusqueda filter : filtros) {
            parameterKeyDate = filter.getCampo().replace("-", "");

            if (filter.getValor() instanceof Date) {
                query.setParameter(parameterKeyDate, (Date) filter.getValor(), TemporalType.DATE);
            } else {
                query.setParameter(filter.getCampo(), filter.getValor());
            }
        }

        Long numResultados = (Long) query.getSingleResult();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Solicitudes contadas: " + numResultados);
        }

        return numResultados.intValue();
    }

    @Override
    public TipoSolicitud getTipoSolicitudById(BigDecimal pConsecutivo) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Buscando el tipo de solicitud para el consecutivo: {}", pConsecutivo);
        }

        String strQuery = "SELECT s.tipoSolicitud FROM Solicitud s WHERE s.id = :idSolicitud";
        TypedQuery<TipoSolicitud> tQuery = getEntityManager().createQuery(strQuery, TipoSolicitud.class);
        TipoSolicitud tipoSolicitud = tQuery.getSingleResult();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Solicitud {} pertenece al tipo: {}", pConsecutivo, tipoSolicitud.getDescripcion());
        }

        return tipoSolicitud;
    }

    @Override
    public List<Solicitud> findAllSolicitudesNng(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosConsultaGenericaNng();

        // Criteria Builder
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        CriteriaQuery<Solicitud> cQueyData = cb.createQuery(Solicitud.class);
        CriteriaQuery<BigDecimal> cQueryIds = cb.createQuery(BigDecimal.class);
        Root<Solicitud> ss = cQueyData.from(Solicitud.class);

        // WHERE
        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = null;
            if (filtro.getPrefijo().equals("ss")) {
                pred = this.getPredicateFromFiltro(filtro, ss, cb);
            }
            wherePredicate = cb.and(wherePredicate, pred);
        }

        if (pFiltrosSolicitud.getTipoSolicitud() == null) {
            Predicate pred = null;
            pred = ss.get("tipoSolicitud").get("cdg").in(SOLICITUDES_NNG);
            wherePredicate = cb.and(wherePredicate, pred);
        }

        // Si hay que cruzar por varias tablas la paginación falla ya que se haría un
        // distinct sobre el resultado de la paginación y el resultado no sería válido.
        // Por eso, buscamos primero la lista de id's que cuadran y sobre ellos
        // hacemos la paginación para traer la información.

        // Primero seleccionamos todos los Identificadores de Solicitud que cumplen el WHERE
        cQueryIds.where(wherePredicate).select(ss.<BigDecimal> get("id")).distinct(true);
        cQueryIds.orderBy(cb.asc(ss.get("id")));
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
            cQueyData.select(ss).distinct(true);
            cQueyData.where(wherePredicate).where(ss.get("id").in(listaIds));
            cQueyData.orderBy(cb.asc(ss.get("id")));
            TypedQuery<Solicitud> tQueryData = getEntityManager().createQuery(cQueyData);

            // Hacemos los fecth para mostrar la información de las tablas cruzadas, se haya buscado
            // algo en ellas o no.
            // tQueryData.setHint("eclipselink.join-fetch", "sl.numeracionesSolicitadas");

            List<Solicitud> result = tQueryData.getResultList();

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
            return new ArrayList<Solicitud>(1);
        }

    }

    @Override
    public int findAllSolicitudesNngCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltrosSolicitud.getListaFiltrosConsultaGenericaNng();

        // Query SELECT sl FROM SolicitudAsignacion sl
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<Solicitud> ss = cQuery.from(Solicitud.class);

        // WHERE
        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = null;
            if (filtro.getPrefijo().equals("ss")) {
                pred = this.getPredicateFromFiltro(filtro, ss, cb);
            }
            wherePredicate = cb.and(wherePredicate, pred);
        }

        if (pFiltrosSolicitud.getTipoSolicitud() == null) {
            Predicate pred = null;
            pred = ss.get("tipoSolicitud").get("cdg").in(SOLICITUDES_NNG);
            wherePredicate = cb.and(wherePredicate, pred);
        }

        cQuery.where(wherePredicate);

        TypedQuery<Long> tQuery = getEntityManager().createQuery(cQuery.select(cb.countDistinct(ss)));
        int rowCount = tQuery.getSingleResult().intValue();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Solicitudes contadas: " + rowCount);
        }

        return rowCount;
    }

    @Override
    public boolean isSolicitudWithRangos(Solicitud solicitud) {

        String squery = "SELECT COUNT(r) FROM RangoSerieNng r WHERE r.solicitud = :solicitud";
        TypedQuery<Long> query = getEntityManager().createQuery(squery, Long.class);
        query.setParameter("solicitud", solicitud);

        Long resultado = query.getSingleResult();

        return (resultado != null && resultado > 0);
    }

    @Override
    public boolean isSolicitudPendieteByNir(BigDecimal idNir) {
        String sQuery =
                "SELECT COUNT(vss) "
                        + "FROM VSolicitudSerie  vss "
                        + "WHERE vss.nir.id = :nir "
                        + "and vss.estatus.codigo = :estadoSolicitud";
        TypedQuery<Long> query = getEntityManager().createQuery(sQuery, Long.class);
        query.setParameter("nir", idNir);
        query.setParameter("estadoSolicitud", EstadoSolicitud.SOLICITUD_EN_TRAMITE);

        Long resultado = query.getSingleResult();
        return resultado != null && resultado > 0;
    }
}
