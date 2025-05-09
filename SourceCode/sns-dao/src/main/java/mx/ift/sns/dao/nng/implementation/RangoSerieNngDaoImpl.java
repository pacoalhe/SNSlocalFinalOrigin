package mx.ift.sns.dao.nng.implementation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.nng.IRangoSerieNngDao;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaRangos;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.nng.DetalleRangoAsignadoNng;
import mx.ift.sns.modelo.nng.RangoSerieNng;
import mx.ift.sns.modelo.nng.SerieNng;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.series.EstadoRango;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos DAO para Rango Serie de Numeración No Geográfica.
 * @author X53490DE
 */
@Named
public class RangoSerieNngDaoImpl extends BaseDAO<RangoSerieNng> implements IRangoSerieNngDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RangoSerieNngDaoImpl.class);

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
            return pCriteriaBuilder.equal(getRangoSeriePath(pFiltro.getCampo(), pFrom), pFiltro.getValor());
        }
    }

    /**
     * Recupera la ruta de un campo específico de RangoSerie. Al usar criterias, no se puede acceder directamente al
     * atributo de un objeto compuesto (objeto.id.sna x ej). Para ello es necesario encadenar llamadas a cada atributo
     * usando un get que nos devuelva el Path correspondiente.
     * @param pFiled Atributo que se quiere obtener de un objeto JPA.
     * @param pFrom Contenedor
     * @return Path del atributo dentro de la clase JPA.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Path<Object> getRangoSeriePath(String pFiled, From pFrom) {
        // Cada campo de cada objeto viene separado por el "."
        String[] fields = pFiled.split("\\.");
        Path<Object> path = pFrom.get(fields[0]);

        // Recuperamos el path anidando las llamadas.
        for (int i = 1; i < fields.length; i++) {
            path = path.get(fields[i]);
        }

        return path;
    }

    // @Override
    // public RangoSerieNng saveRangoSerie(RangoSerieNng pRangoSerieNng) {
    // RangoSerieNng rsnng = null;
    // if (pRangoSerieNng.getId() == null) {
    // // Nueva PK para el Rango.
    // RangoSerieNngPK rangoPk = new RangoSerieNngPK();
    // rangoPk.setIdClaveServicio(pRangoSerieNng.getClaveServicio().getCodigo());
    // rangoPk.setSna(pRangoSerieNng.getSerie().getId().getSna());
    // pRangoSerieNng.setId(rangoPk);
    //
    // rsnng = pRangoSerieNng;
    // getEntityManager().persist(rsnng);
    // if (LOGGER.isDebugEnabled()) {
    // LOGGER.debug("Rango guardado. {}", rsnng.toString());
    // }
    // } else {
    // if (pRangoSerieNng.getId().getId() == null) {
    // rsnng = pRangoSerieNng;
    // getEntityManager().persist(rsnng);
    // if (LOGGER.isDebugEnabled()) {
    // LOGGER.debug("Rango guardado. {}", rsnng.toString());
    // }
    // } else {
    // rsnng = getEntityManager().merge(pRangoSerieNng);
    // if (LOGGER.isDebugEnabled()) {
    // LOGGER.debug("Rango actualizado. {}", rsnng.toString());
    // }
    // }
    // }
    // return rsnng;
    // }

    @Override
    public List<RangoSerieNng> findAllRangos(FiltroBusquedaRangos pFiltros) {

        // Filtros de Búsqueda definidos
        ArrayList<FiltroBusqueda> filtros = pFiltros.getListaFiltros();

        // Query SELECT rango FROM NNG_RANGO_SERIE sRango
        // LEFT JOIN CAT_SERIE_NNG sSerie
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<RangoSerieNng> cQuery = cb.createQuery(RangoSerieNng.class);
        Root<RangoSerieNng> rango = cQuery.from(RangoSerieNng.class);
        Join<RangoSerieNng, SerieNng> sSerie = rango.join("serie", JoinType.LEFT);

        // WHERE
        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = null;
            if (filtro.getPrefijo().equals("sRango")) {
                pred = this.getPredicateFromFiltro(filtro, rango, cb);
            } else { // } else if (filtro.getPrefijo().equals("sSerie")) {
                pred = this.getPredicateFromFiltro(filtro, sSerie, cb);
            }
            wherePredicate = cb.and(wherePredicate, pred);
        }
        cQuery.where(wherePredicate).distinct(true);

        // Ordenación de resultados.
        if (pFiltros.getOrdenCampos().isEmpty()) {
            cQuery.orderBy(cb.asc(rango.get("id"))); // Por Id, Clave de Servicio y Sna
        } else {
            List<Order> orderList = new ArrayList<Order>(pFiltros.getOrdenCampos().size() + 1);
            for (Entry<String, String> ordencampo : pFiltros.getOrdenCampos().entrySet()) {
                if (ordencampo.getValue().equals(FiltroBusquedaRangos.ORDEN_ASC)) {
                    orderList.add(cb.asc(getRangoSeriePath(ordencampo.getKey(), rango)));
                } else {
                    orderList.add(cb.desc(getRangoSeriePath(ordencampo.getKey(), rango)));
                }
            }
            // Siempre ordenamos or Id, Clave de Servicio y Sna después de los campos indicados.
            orderList.add(cb.asc(rango.get("id")));
            cQuery.orderBy(orderList);
        }

        TypedQuery<RangoSerieNng> tQuery = getEntityManager().createQuery(cQuery.select(rango));

        // Paginación
        if (pFiltros.isUsarPaginacion()) {
            tQuery.setFirstResult(pFiltros.getNumeroPagina());
            tQuery.setMaxResults(pFiltros.getResultadosPagina());
        }

        List<RangoSerieNng> result = tQuery.getResultList();

        if (LOGGER.isDebugEnabled()) {
            StringBuffer sbTraza = new StringBuffer();
            sbTraza.append("Rangos encontrados: ").append(result.size());
            sbTraza.append(", paginación: ").append(pFiltros.isUsarPaginacion());
            if (pFiltros.isUsarPaginacion()) {
                sbTraza.append(" (Página: ").append(pFiltros.getNumeroPagina());
                sbTraza.append(", MaxResult: ").append(pFiltros.getResultadosPagina()).append(")");
            }
            LOGGER.debug(sbTraza.toString());
            // LOGGER.debug(tQuery.toString());
        }

        return result;
    }

    @Override
    public int findAllRangosCount(FiltroBusquedaRangos pFiltros) {

        // Filtros de Búsqueda definidos
        ArrayList<FiltroBusqueda> filtros = pFiltros.getListaFiltros();

        // Query SELECT rango FROM NNG_RANGO_SERIE sRango
        // LEFT JOIN CAT_SERIE_NNG sSerie
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<RangoSerieNng> rango = cQuery.from(RangoSerieNng.class);
        Join<RangoSerieNng, SerieNng> sSerie = rango.join("serie", JoinType.LEFT);

        // WHERE
        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = null;
            if (filtro.getPrefijo().equals("sRango")) {
                pred = this.getPredicateFromFiltro(filtro, rango, cb);
            } else { // } else if (filtro.getPrefijo().equals("sSerie")) {
                pred = this.getPredicateFromFiltro(filtro, sSerie, cb);
            }
            wherePredicate = cb.and(wherePredicate, pred);
        }
        cQuery.where(wherePredicate).distinct(true);

        TypedQuery<Long> tQuery = getEntityManager().createQuery(cQuery.select(cb.count(rango)));
        int rowCount = tQuery.getSingleResult().intValue();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Rangos contados: " + rowCount);
        }

        return rowCount;
    }

    @Override
    public RangoSerieNng getRangoSerie(BigDecimal pClaveServicio, BigDecimal pSna, String pNumInicial,
            Proveedor pAsignatario) {

        StringBuffer sbQuery = new StringBuffer("SELECT r FROM RangoSerieNng r WHERE ");
        sbQuery.append("r.id.idClaveServicio = :claveServicio").append(" AND ");
        sbQuery.append("r.id.sna = :sna").append(" AND ");
        sbQuery.append("r.numInicio = :numInicio");
        if (pAsignatario != null) {
            sbQuery.append(" AND r.asignatario = :asignatario");
        }

        TypedQuery<RangoSerieNng> query = getEntityManager().createQuery(sbQuery.toString(), RangoSerieNng.class);
        query.setParameter("claveServicio", pClaveServicio);
        query.setParameter("sna", pSna);
        query.setParameter("numInicio", pNumInicial);
        if (pAsignatario != null) {
            query.setParameter("asignatario", pAsignatario);
        }

        RangoSerieNng rango = null;
        try {
            rango = query.getSingleResult();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("RangoNng encontrado: " + rango.toString());
            }
        } catch (NoResultException nre) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("RangoNng no encontrado encontrado: Pst {}, ClaveServicio {}, Sna {}, Ini {}",
                        pAsignatario.getCdgPst(), pClaveServicio, pSna, pNumInicial);
            }
        }

        return rango;
    }

    @Override
    public RangoSerieNng getRangoSerieByFraccion(BigDecimal pClaveServicio, BigDecimal pSna, String pNumInicial,
            String pNumFinal, Proveedor pAsignatario) {

        StringBuffer sbQuery = new StringBuffer("SELECT r FROM RangoSerieNng r WHERE ");
        sbQuery.append("r.id.idClaveServicio = :claveServicio").append(" AND ");
        sbQuery.append("r.id.sna = :sna").append(" AND ");
        sbQuery.append("CAST(r.numInicio AS NUMERIC(4,0)) <= :numInicio").append(" AND ");
        sbQuery.append("CAST(r.numFinal AS NUMERIC(4,0)) >= :numFinal");
        if (pAsignatario != null) {
            sbQuery.append(" AND r.asignatario = :asignatario");
        }
        TypedQuery<RangoSerieNng> query = getEntityManager().createQuery(sbQuery.toString(), RangoSerieNng.class);
        query.setParameter("claveServicio", pClaveServicio);
        query.setParameter("sna", pSna);
        query.setParameter("numInicio", Integer.valueOf(pNumInicial));
        query.setParameter("numFinal", Integer.valueOf(pNumFinal));
        if (pAsignatario != null) {
            query.setParameter("asignatario", pAsignatario);
        }
        RangoSerieNng rango = null;
        try {
            rango = query.getSingleResult();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("RangoNng encontrado: " + rango.toString());
            }
        } catch (NoResultException nre) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("RangoNng no encontrado encontrado: Pst {}, ClaveServicio {}, Sna {}, Ini {}, Fin {}",
                        pAsignatario.getCdgPst(), pClaveServicio, pSna, pNumInicial, pNumFinal);
            }
        }

        return rango;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Integer getTotalOcupacionSerie(SerieNng serie) {
        BigDecimal sum = new BigDecimal(0);

        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
            Root rango = query.from(RangoSerieNng.class);
            query.select(cb.sum(cb.sum(cb.diff(rango.<Number> get("numFinal"), rango.<Number> get("numInicio")), 1)));
            query.where(cb.equal(rango.get("serie"), serie));

            sum = getEntityManager().createQuery(query).getSingleResult();

            LOGGER.debug("Total numeración ocupada para la serie" + serie.getId().getSna().toString() + ": " + sum);
        } catch (NoResultException e) {

            return 0;
        }

        if (sum == null) {
            return 0;
        } else {
            return sum.intValue();
        }
    }

    @Override
    public RangoSerieNng getRandomRangoByClaveServicio(ClaveServicio clave, int cantidad) {

        RangoSerieNng rango = null;

        StringBuffer sbquery = new StringBuffer("SELECT r1 FROM RangoSerieNng r1");
        StringBuffer sbcount = new StringBuffer("SELECT COUNT(r1) FROM RangoSerieNng r1");
        StringBuffer sbwhere = new StringBuffer(" where r1.id.idClaveServicio = :clave ");

        // Comprobamos que la numeración disponible entre el siguiente rango o el final de las serie es menor que la
        // cantidad solicitada
        sbwhere.append(" AND ((FUNC('TO_NUMBER',(SELECT MIN(r2.numInicio) FROM RangoSerieNng r2 ");
        sbwhere.append("where r2.id.idClaveServicio = :clave and r2.id.sna = r1.id.sna and r2.numInicio ");
        sbwhere.append("> r1.numFinal)) - FUNC('TO_NUMBER',r1.numFinal) - 1) >= :cantidad");
        sbwhere.append(" or (r1.numInicio=(select max(r3.numInicio) from RangoSerieNng r3 ");
        sbwhere.append("where r3.id.idClaveServicio = :clave and r3.id.sna = r1.id.sna)) and ");
        sbwhere.append("(10000 - FUNC('TO_NUMBER',r1.numFinal) >= :cantidad))");

        try {
            // Obtenemos el total de registros que cumplen las condiciones para generar el random
            sbcount.append(sbwhere);
            TypedQuery<Long> tcount = getEntityManager().createQuery(sbcount.toString(), Long.class);
            tcount.setParameter("clave", clave.getCodigo());
            tcount.setParameter("cantidad", cantidad);
            long total = tcount.getSingleResult();

            if (total > 0) {
                // Obtenemos un rango aleatorio
                Random rnd = new Random();
                sbquery.append(sbwhere);
                TypedQuery<RangoSerieNng> tquery = getEntityManager().createQuery(sbquery.toString(),
                        RangoSerieNng.class);
                tquery.setParameter("clave", clave.getCodigo());
                tquery.setParameter("cantidad", cantidad);
                rango = tquery.setMaxResults(1).setFirstResult(rnd.nextInt((int) total)).getSingleResult();
            }
        } catch (NoResultException e) {
            return null;
        }

        return rango;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Integer getTotalNumeracionAsignadaByClaveServicio(ClaveServicio clave) {

        BigDecimal sum = new BigDecimal(0);

        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
            Root rango = query.from(RangoSerieNng.class);
            query.select(cb.sum(cb.sum(cb.diff(rango.<Number> get("numFinal"), rango.<Number> get("numInicio")), 1)));
            query.where(cb.equal(rango.get("claveServicio"), clave));

            sum = getEntityManager().createQuery(query).getSingleResult();

            LOGGER.debug("Total numeración asignada para la clave de servicio" + clave.getCodigo().toString() + ": "
                    + sum);
        } catch (NoResultException e) {

            return 0;
        }

        if (sum == null) {
            return 0;
        } else {
            return sum.intValue();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public List<RangoSerieNng> validateRango(BigDecimal clave, BigDecimal sna,
            String numeroInicial, String numeroFinal) {

        List<RangoSerieNng> resultado = new ArrayList<>();

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<RangoSerieNng> query = cb.createQuery(RangoSerieNng.class);
        Root rango = query.from(RangoSerieNng.class);

        query.where(
                cb.equal(rango.get("claveServicio").get("codigo"), clave),
                cb.equal(rango.get("id").get("sna"), sna),
                cb.or(
                        cb.between(rango.get("numInicio"), numeroInicial, numeroFinal),
                        cb.between(rango.get("numFinal"), numeroInicial, numeroFinal),
                        cb.and(
                                cb.lessThanOrEqualTo(rango.get("numInicio"),
                                        StringUtils.leftPad(numeroInicial, 4, '0')),
                                cb.greaterThanOrEqualTo(rango.get("numFinal"),
                                        StringUtils.leftPad(numeroFinal, 4, '0')))));

        resultado = getEntityManager().createQuery(query).getResultList();

        return resultado;
    }

    @Override
    public RangoSerieNng getRangoSeriePertenece(BigDecimal pClaveServicio, BigDecimal pSna, String pNumeracion) {

        StringBuffer sbQuery = new StringBuffer("SELECT r FROM RangoSerieNng r WHERE ");
        sbQuery.append("r.id.idClaveServicio = :claveServicio").append(" AND ");
        sbQuery.append("r.id.sna = :sna").append(" AND ");
        sbQuery.append("r.numInicio <= :num").append(" AND ");
        sbQuery.append("r.numFinal >= :num").append(" AND ");
        sbQuery.append("r.estatus.codigo = :status");

        TypedQuery<RangoSerieNng> query = getEntityManager().createQuery(sbQuery.toString(), RangoSerieNng.class);
        query.setParameter("claveServicio", pClaveServicio);
        query.setParameter("sna", pSna);
        query.setParameter("num", pNumeracion);
        query.setParameter("status", EstadoRango.ASIGNADO);
        RangoSerieNng rango = null;
        try {
            rango = query.getSingleResult();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("RangoNng encontrado: " + rango.toString());
            }
        } catch (NoResultException nre) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("RangoNng no encontrado encontrado: ClaveServicio {}, Sna {}, Num {}",
                        pClaveServicio, pSna, pNumeracion);
            }
        }

        return rango;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Integer getNumeracionAsignadaSerie(String claveServicio, Proveedor proveedor) {

        BigDecimal sum = new BigDecimal(0);

        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
            Root rango = query.from(RangoSerieNng.class);
            query.select(cb.sum(cb.sum(cb.diff(rango.<Number> get("numFinal"), rango.<Number> get("numInicio")), 1)));
            query.where(cb.equal(rango.get("claveServicio").get("codigo"), claveServicio),
                    cb.equal(rango.get("asignatario"), proveedor),
                    cb.equal(
                            cb.sum(cb.diff(
                                    rango.<Number> get("numFinal"), rango.<Number> get("numInicio")), 1), 10000),
                    cb.notEqual(rango.get("estatus").get("codigo"), EstadoRango.PENDIENTE));

            sum = getEntityManager().createQuery(query).getSingleResult();

            LOGGER.debug("Total numeración asignada por serie para la clave de servicio {} para {}: {}", claveServicio,
                    proveedor.getNombre(), sum);
        } catch (NoResultException e) {

            return 0;
        }

        if (sum == null) {
            return 0;
        } else {
            return sum.intValue();
        }

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Integer getNumeracionAsignadaRango(String claveServicio, Proveedor proveedor) {

        BigDecimal sum = new BigDecimal(0);

        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
            Root rango = query.from(RangoSerieNng.class);
            query.select(cb.sum(cb.sum(cb.diff(rango.<Number> get("numFinal"), rango.<Number> get("numInicio")), 1)));
            query.where(cb.equal(rango.get("claveServicio").get("codigo"), claveServicio),
                    cb.equal(rango.get("asignatario"), proveedor),
                    cb.lt(cb.sum(cb.diff(rango.<Number> get("numFinal"), rango.<Number> get("numInicio")), 1), 10000),
                    cb.isNull(rango.get("cliente")),
                    cb.notEqual(rango.get("estatus").get("codigo"), EstadoRango.PENDIENTE));

            sum = getEntityManager().createQuery(query).getSingleResult();

            LOGGER.debug("Total numeración asignada por rango para la clave de servicio {} para {}: {}", claveServicio,
                    proveedor.getNombre(), sum);
        } catch (NoResultException e) {

            return 0;
        }

        if (sum == null) {
            return 0;
        } else {
            return sum.intValue();
        }

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Integer getNumeracionAsignadaEspecifica(String claveServicio, Proveedor proveedor) {

        BigDecimal sum = new BigDecimal(0);

        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
            Root rango = query.from(RangoSerieNng.class);
            query.select(cb.sum(cb.sum(cb.diff(rango.<Number> get("numFinal"), rango.<Number> get("numInicio")), 1)));
            query.where(cb.equal(rango.get("claveServicio").get("codigo"), claveServicio),
                    cb.equal(rango.get("asignatario"), proveedor),
                    cb.lt(cb.sum(cb.diff(rango.<Number> get("numFinal"), rango.<Number> get("numInicio")), 1), 10000),
                    cb.isNotNull(rango.get("cliente")),
                    cb.notEqual(rango.get("estatus").get("codigo"), EstadoRango.PENDIENTE));

            sum = getEntityManager().createQuery(query).getSingleResult();

            LOGGER.debug("Total numeración asignada por especifica para la clave de servicio {} para {}: {}",
                    claveServicio, proveedor.getNombre(), sum);
        } catch (NoResultException e) {

            return 0;
        }

        if (sum == null) {
            return 0;
        } else {
            return sum.intValue();
        }

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Integer getTotalNumeracionAsignadaBySolicitud(BigDecimal id) {

        BigDecimal sum = new BigDecimal(0);

        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
            Root rango = query.from(RangoSerieNng.class);
            query.select(cb.sum(cb.sum(cb.diff(rango.<Number> get("numFinal"), rango.<Number> get("numInicio")), 1)));
            query.where(cb.equal(rango.get("consecutivoAsignacion"), id));

            sum = getEntityManager().createQuery(query).getSingleResult();

        } catch (NoResultException e) {

            return 0;
        }

        if (sum == null) {
            return 0;
        } else {
            return sum.intValue();
        }

    }

    @Override
    public boolean existeNumeracionAsignadaAlPst(Proveedor proveedor) {
        String hql = "SELECT count(1) FROM RangoSerieNng rs WHERE rs.asignatario = :proveedor "
                + "AND rs.estatus.codigo <> :estadoRango";

        TypedQuery<Long> tQuery = getEntityManager().createQuery(hql, Long.class);
        tQuery.setParameter("proveedor", proveedor);
        tQuery.setParameter("estadoRango", EstadoRango.PENDIENTE);

        Long resultado = tQuery.getSingleResult();
        if (resultado == null || resultado.longValue() == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public int findAllRangosAsignadosEspecificoCount() {

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<RangoSerieNng> rango = cQuery.from(RangoSerieNng.class);
        cQuery.select(cb.count(rango));
        cQuery.where(cb.notEqual(rango.get("estatus").get("codigo"), EstadoRango.PENDIENTE),
                cb.isNotNull(rango.get("cliente")));

        TypedQuery<Long> tQuery = getEntityManager().createQuery(cQuery);
        int rowCount = tQuery.getSingleResult().intValue();

        return rowCount;
    }

    @Override
    public List<RangoSerieNng> findAllRangosAsignadosEspecifico(int numPag, int tamPag) {

        StringBuffer sbQuery = new StringBuffer("SELECT r ");
        sbQuery.append("FROM RangoSerieNng r ");
        sbQuery.append("WHERE r.estatus.codigo != :pendiente ");
        sbQuery.append("AND r.cliente is not null");

        TypedQuery<RangoSerieNng> tquery = getEntityManager().createQuery(sbQuery.toString(), RangoSerieNng.class);
        tquery.setParameter("pendiente", EstadoRango.PENDIENTE);

        return tquery.setFirstResult(numPag).setMaxResults(tamPag).getResultList();
    }

    @Override
    public int findAllRangosAsignadosCount() {

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<RangoSerieNng> rango = cQuery.from(RangoSerieNng.class);
        cQuery.select(cb.count(rango));
        cQuery.where(cb.notEqual(rango.get("estatus").get("codigo"), EstadoRango.PENDIENTE));

        TypedQuery<Long> tQuery = getEntityManager().createQuery(cQuery);
        int rowCount = tQuery.getSingleResult().intValue();

        return rowCount;
    }

    @Override
    public List<RangoSerieNng> findAllRangosAsignados(int numPag, int tamPag) {

        StringBuffer sbQuery = new StringBuffer("SELECT r ");
        sbQuery.append("FROM RangoSerieNng r ");
        sbQuery.append("WHERE r.estatus.codigo != :pendiente");

        TypedQuery<RangoSerieNng> tquery = getEntityManager().createQuery(sbQuery.toString(), RangoSerieNng.class);
        tquery.setParameter("pendiente", EstadoRango.PENDIENTE);

        return tquery.setFirstResult(numPag).setMaxResults(tamPag).getResultList();
    }

    @Override
    public List<ClaveServicio> findAllClaveServicioAsignadas() {

        StringBuffer sbQuery = new StringBuffer("SELECT DISTINCT(r.claveServicio) ");
        sbQuery.append("FROM RangoSerieNng r ");
        sbQuery.append("WHERE r.estatus.codigo != :pendiente");

        TypedQuery<ClaveServicio> tquery = getEntityManager().createQuery(sbQuery.toString(), ClaveServicio.class);
        tquery.setParameter("pendiente", EstadoRango.PENDIENTE);

        return tquery.getResultList();
    }

    @Override
    public int findAllRangosAsignadosByClaveCount(ClaveServicio clave) {

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<RangoSerieNng> rango = cQuery.from(RangoSerieNng.class);
        cQuery.select(cb.count(rango));
        cQuery.where(cb.notEqual(rango.get("estatus").get("codigo"), EstadoRango.PENDIENTE),
                cb.equal(rango.get("claveServicio"), clave));

        TypedQuery<Long> tQuery = getEntityManager().createQuery(cQuery);
        int rowCount = tQuery.getSingleResult().intValue();

        return rowCount;
    }

    @Override
    public List<RangoSerieNng> findAllRangosByClaveAsignados(ClaveServicio clave, int numPag, int tamPag) {

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<RangoSerieNng> cQuery = cb.createQuery(RangoSerieNng.class);
        Root<RangoSerieNng> rango = cQuery.from(RangoSerieNng.class);

        cQuery.where(cb.notEqual(rango.get("estatus").get("codigo"), EstadoRango.PENDIENTE),
                cb.equal(rango.get("claveServicio"), clave));

        TypedQuery<RangoSerieNng> tQuery = getEntityManager().createQuery(cQuery);
        List<RangoSerieNng> resultado = tQuery.setFirstResult(numPag).setMaxResults(tamPag).getResultList();

        return resultado;
    }

    @Override
    public List<DetalleRangoAsignadoNng> findAllRangosAsignadosD(int numPag, int tamPag) {

        String sql = "SELECT det FROM DetalleRangoAsignadoNng det ORDER BY det.idSerie, det.numInicio, det.numFinal";

        Query query = getEntityManager().createQuery(sql).setFirstResult(numPag).setMaxResults(tamPag);

        @SuppressWarnings("unchecked")
        List<DetalleRangoAsignadoNng> detalle = query.getResultList();

        return detalle;
    }

    @Override
    public List<DetalleRangoAsignadoNng> findAllRangosAsignadosD(BigDecimal clave, int numPag, int tamPag) {

        String sql = "SELECT det FROM DetalleRangoAsignadoNng det WHERE det.idClaveServicio = :clave "
                + "ORDER BY det.idSerie, det.numInicio, det.numFinal";

        Query query = getEntityManager().createQuery(sql);
        query.setParameter("clave", clave);
        query.setFirstResult(numPag).setMaxResults(tamPag);

        @SuppressWarnings("unchecked")
        List<DetalleRangoAsignadoNng> detalle = query.getResultList();

        return detalle;
    }

    @Override
    public List<DetalleRangoAsignadoNng> findAllRangosAsignadosEspecificoD(int numPag, int tamPag) {

        String sql = "SELECT det FROM DetalleRangoAsignadoNng det WHERE det.cliente IS NOT NULL "
                + "ORDER BY det.idSerie, det.numInicio, det.numFinal";

        Query query = getEntityManager().createQuery(sql).setFirstResult(numPag).setMaxResults(tamPag);

        @SuppressWarnings("unchecked")
        List<DetalleRangoAsignadoNng> detalle = query.getResultList();

        return detalle;
    }
}
