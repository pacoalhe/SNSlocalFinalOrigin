package mx.ift.sns.dao.ng.implementation;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

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
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.IRangoSerieDao;
import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.abn.PoblacionAbn;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaHistoricoSeries;
import mx.ift.sns.modelo.filtros.FiltroBusquedaRangos;
import mx.ift.sns.modelo.ng.DetalleRangoAsignadoNg;
import mx.ift.sns.modelo.ng.DetalleReporteAbd;
import mx.ift.sns.modelo.ng.NumeracionSolicitada;
import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.ng.Serie;
import mx.ift.sns.modelo.ng.SeriePK;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.EstadoNumeracion;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.NirNumeracion;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.ot.PoblacionNumeracion;
import mx.ift.sns.modelo.pnn.DetallePlanAbdPresuscripcion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.ProveedorConvenio;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.modelo.solicitud.Solicitud;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de RangoSerie.
 */
@Named
public class RangoSerieDaoImpl extends BaseDAO<RangoSerie> implements IRangoSerieDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RangoSerieDaoImpl.class);

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

    /**
     * Recupera la ruta de un campo específico de RangoSerie. Al usar criterias, no se puede acceder directamente al
     * atributo de un objeto compuesto (objeto.id.sna x ej). Para ello es necesario encadenar llamadas a cada atributo
     * usando un get que nos devuelva el Path correspondiente.
     * @param pFiled Atributo que se quiere obtener de un objeto JPA.
     * @param pRoot Contenedor
     * @return Path del atributo dentro de la clase JPA.
     */
    private Path<Object> getRangoSeriePath(String pFiled, Root<RangoSerie> pRoot) {
        // Cada campo de cada objeto viene separado por el "."
        String[] fields = pFiled.split("\\.");
        Path<Object> path = pRoot.get(fields[0]);

        // Recuperamos el path anidando las llamadas.
        for (int i = 1; i < fields.length; i++) {
            path = path.get(fields[i]);
        }

        return path;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public BigDecimal getTotalNumOcupadaAbn(Abn abn) {

        BigDecimal sum = new BigDecimal(0);

        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);

            Root rango = query.from(RangoSerie.class);
            Root poblacion = query.from(Poblacion.class);
            Root poblacionAbn = query.from(PoblacionAbn.class);

            Predicate rangoPoblacion = cb.and(cb.equal(rango.get("poblacion").get("inegi"), poblacion.get("inegi")));
            Predicate rangoPoblacionAbn = cb.and(cb.equal(rango.get("poblacion").get("inegi"), poblacionAbn
                    .get("inegi")
                    .get("inegi")));
            Predicate pAbn = cb.and(cb.equal(poblacionAbn.get("abn"), abn));

            query.select(cb.sum(cb.sum(cb.diff(rango.<Number> get("numFinal"), rango.<Number> get("numInicio")), 1)));
            query.where(rangoPoblacion, rangoPoblacionAbn, pAbn);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Total numeración ocupada para el ABN " + abn.getCodigoAbn().toString() + ": "
                        + sum.toString());
            }

            sum = getEntityManager().createQuery(query).getSingleResult();
        } catch (NoResultException e) {

            return new BigDecimal(0);
        }

        if (sum == null) {
            return new BigDecimal(0);
        } else {
            return sum;
        }

    }

    @SuppressWarnings({"unchecked", "unused"})
    @Override
    public BigDecimal getTotalNumOcupadaSerie(SeriePK id) {

        BigDecimal sum = new BigDecimal(0);

        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
            @SuppressWarnings("rawtypes")
            Root rango = query.from(RangoSerie.class);
            query.select(cb.sum(cb.sum(cb.diff(rango.<Number> get("numFinal"), rango.<Number> get("numInicio")), 1)));
            query.where(cb.equal(rango.get("serie").get("id"), id));

            sum = getEntityManager().createQuery(query).getSingleResult();

            LOGGER.debug("Total numeración ocupada para la serie" + id.getSna() + ": "
                    + sum.toString());
        } catch (NoResultException e) {

            return new BigDecimal(0);
        }

        if (sum == null) {
            return new BigDecimal(0);
        } else {
            return sum;
        }
    }

    @Override
    public List<RangoSerie> findNumeracionesAsignadasSerie(Abn abn, Nir nir, BigDecimal sna) {

        String strQuery = "SELECT r FROM RangoSerie r where r.serie.nir.abn = :abn "
                + "and r.serie.nir = :nir and r.id.sna = :sna order by r.numInicio asc";
        Query query = getEntityManager().createQuery(strQuery);
        query.setParameter("abn", abn);
        query.setParameter("nir", nir);
        query.setParameter("sna", sna);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: " + query);
        }

        @SuppressWarnings("unchecked")
        List<RangoSerie> list = query.getResultList();
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<RangoSerie> findRangoSerieBySolicitudSinAsignar(BigDecimal codSol) {

        String strQuery = "SELECT r FROM RangoSerie r where r.estadoRango.codigo = :status "
                + "and r.solicitud.id = :codSol";
        List<RangoSerie> list = new ArrayList<RangoSerie>();

        Query query = getEntityManager().createQuery(strQuery);
        query.setParameter("codSol", codSol);
        query.setParameter("status", EstadoRango.PENDIENTE);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: " + query);
        }

        list = query.getResultList();

        return list;
    }

    @Override
    public List<RangoSerie> findRangoSerieBySolicitud(BigDecimal codSol) {
        String strQuery = "SELECT r FROM RangoSerie r where r.solicitud.id = :codSol";
        List<RangoSerie> list = new ArrayList<RangoSerie>();

        TypedQuery<RangoSerie> tQuery = getEntityManager().createQuery(strQuery, RangoSerie.class);
        tQuery.setParameter("codSol", codSol);

        list = tQuery.getResultList();
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public BigDecimal getTotalNumAsignadaSolicitud(BigDecimal codSol) {

        BigDecimal sum = new BigDecimal(0);

        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
            @SuppressWarnings("rawtypes")
            Root rango = query.from(RangoSerie.class);
            query.select(cb.sum(cb.sum(cb.diff(rango.<Number> get("numFinal"), rango.<Number> get("numInicio")), 1)));
            query.where(cb.equal(rango.get("solicitud").get("id"), codSol),
                    cb.notEqual(rango.get("estadoRango").get("codigo"), EstadoRango.PENDIENTE));

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Query: " + query);
            }

            sum = getEntityManager().createQuery(query).getSingleResult();
        } catch (NoResultException e) {

            return new BigDecimal(0);
        }
        if (sum == null) {
            return new BigDecimal(0);
        } else {
            return sum;
        }
    }

    @Override
    public RangoSerie getRangoSerie(BigDecimal pIdNir, BigDecimal pSna,
            String pNumInicial, Proveedor pAsignatario) {

        // El Asignatario siempre es el que esta comercializando (rentando) el rango

        StringBuffer sbQuery = new StringBuffer("SELECT r FROM RangoSerie r WHERE ");
        sbQuery.append("r.id.idNir = :nir").append(" AND ");
        sbQuery.append("r.id.sna = :sna").append(" AND ");
        sbQuery.append("r.numInicio = :numInicio").append(" AND ");
        sbQuery.append("r.asignatario = :asignatario");

        TypedQuery<RangoSerie> query = getEntityManager().createQuery(sbQuery.toString(), RangoSerie.class);
        query.setParameter("nir", pIdNir);
        query.setParameter("sna", pSna);
        query.setParameter("numInicio", pNumInicial);
        query.setParameter("asignatario", pAsignatario);

        RangoSerie rango = null;
        try {
            rango = query.getSingleResult();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Rango encontrado: " + rango.toString());
            }
        } catch (NoResultException nre) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Rango no encontrado encontrado: Pst {}, Nir {}, Sna {}, Ini {}",
                        pAsignatario.getCdgPst(), pIdNir, pSna, pNumInicial);
            }
        }

        return rango;
    }

    @Override
    public boolean existeRangoSerie(BigDecimal pIdNir, BigDecimal pSna,
            String pNumInicial, Proveedor pAsignatario) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Comprobando existencia rango: idNir: {}, idSna: {}, inicio: {}, idAsignatario: {}",
                    pIdNir.toString(), pSna.toString(), pNumInicial, pAsignatario.getId().toString());
        }

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<RangoSerie> r = cQuery.from(RangoSerie.class);

        ParameterExpression<BigDecimal> idNir = cb.parameter(BigDecimal.class);
        ParameterExpression<BigDecimal> idSna = cb.parameter(BigDecimal.class);
        ParameterExpression<String> numInicio = cb.parameter(String.class);
        ParameterExpression<Proveedor> asignatario = cb.parameter(Proveedor.class);

        cQuery.select(cb.countDistinct(r)).where(
                cb.equal(r.get("id").get("idNir"), idNir),
                cb.equal(r.get("id").get("sna"), idSna),
                cb.equal(r.get("numInicio"), numInicio),
                cb.equal(r.get("asignatario"), asignatario));

        TypedQuery<Long> tQuery = getEntityManager().createQuery(cQuery);
        tQuery.setParameter(idNir, pIdNir);
        tQuery.setParameter(idSna, pSna);
        tQuery.setParameter(numInicio, pNumInicial);
        tQuery.setParameter(asignatario, pAsignatario);

        int rowCount = tQuery.getSingleResult().intValue();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Rango existente: {}", (rowCount > 0));
        }

        return (rowCount > 0);
    }

    @Override
    public RangoSerie getRangoSerieByFraccion(BigDecimal pIdNir, BigDecimal pSna,
            String pNumInicial, String pNumFinal, Proveedor pAsignatario) {

        // El Asignatario siempre es el que esta comercializando (rentando) el rango

        StringBuffer sbQuery = new StringBuffer("SELECT r FROM RangoSerie r WHERE ");
        sbQuery.append("r.id.idNir = :nir").append(" AND ");
        sbQuery.append("r.id.sna = :sna").append(" AND ");
        sbQuery.append("CAST(r.numInicio AS NUMERIC(4,0)) <= :numInicio").append(" AND ");
        sbQuery.append("CAST(r.numFinal AS NUMERIC(4,0)) >= :numFinal").append(" AND ");
        sbQuery.append("r.asignatario = :asignatario");

        TypedQuery<RangoSerie> query = getEntityManager().createQuery(sbQuery.toString(), RangoSerie.class);
        query.setParameter("nir", pIdNir);
        query.setParameter("sna", pSna);
        query.setParameter("numInicio", Integer.valueOf(pNumInicial));
        query.setParameter("numFinal", Integer.valueOf(pNumFinal));
        query.setParameter("asignatario", pAsignatario);

        RangoSerie rango = null;
        try {
            rango = query.getSingleResult();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Rango encontrado: " + rango.toString());
            }
        } catch (NoResultException nre) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Rango no encontrado encontrado. Pst {}, idNir {}, Sna {}, Ini {}, Fin {}",
                        pAsignatario.getCdgPst(), pIdNir, pSna, pNumInicial, pNumFinal);
            }
        }

        return rango;
    }

    @Override
    public List<RangoSerie> findAllRangos(FiltroBusquedaRangos pFiltros) {

        // Filtros de Búsqueda definidos
        ArrayList<FiltroBusqueda> filtros = pFiltros.getListaFiltros();

        // Query SELECT rango FROM RANGO_SERIE sRango LEFT JOIN CAT_SERIE sSerie LEFT JOIN ABN_NIR sNir
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<RangoSerie> cQuery = cb.createQuery(RangoSerie.class);
        Root<RangoSerie> rango = cQuery.from(RangoSerie.class);
        Join<RangoSerie, Serie> sSerie = rango.join("serie", JoinType.LEFT);
        Join<Serie, Nir> sNir = sSerie.join("nir", JoinType.LEFT);

        // WHERE
        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = null;
            if (filtro.getPrefijo().equals("sRango")) {
                pred = this.getPredicateFromFiltro(filtro, rango, cb);
            } else if (filtro.getPrefijo().equals("sSerie")) {
                pred = this.getPredicateFromFiltro(filtro, sSerie, cb);
            } else {
                pred = this.getPredicateFromFiltro(filtro, sNir, cb);
            }
            wherePredicate = cb.and(wherePredicate, pred);
        }
        cQuery.where(wherePredicate).distinct(true);

        // Ordenación de resultados.
        if (pFiltros.getOrdenCampos().isEmpty()) {
            cQuery.orderBy(cb.asc(rango.get("id"))); // Por Id, Sna e idNir
        } else {
            List<Order> orderList = new ArrayList<Order>(pFiltros.getOrdenCampos().size() + 1);
            for (Entry<String, String> ordencampo : pFiltros.getOrdenCampos().entrySet()) {
                if (ordencampo.getValue().equals(FiltroBusquedaRangos.ORDEN_ASC)) {
                    orderList.add(cb.asc(getRangoSeriePath(ordencampo.getKey(), rango)));
                } else {
                    orderList.add(cb.desc(getRangoSeriePath(ordencampo.getKey(), rango)));
                }
            }
            // Siempre ordenamos or Id, Sna e idNir después de los campos indicados.
            orderList.add(cb.asc(rango.get("id")));
            cQuery.orderBy(orderList);
        }

        TypedQuery<RangoSerie> tQuery = getEntityManager().createQuery(cQuery.select(rango));

        // Paginación
        if (pFiltros.isUsarPaginacion()) {
            tQuery.setFirstResult(pFiltros.getNumeroPagina());
            tQuery.setMaxResults(pFiltros.getResultadosPagina());
        }

        List<RangoSerie> result = tQuery.getResultList();

        if (LOGGER.isDebugEnabled()) {
            StringBuffer sbTraza = new StringBuffer();
            sbTraza.append("Rangos encontrados: ").append(result.size());
            sbTraza.append(", paginación: ").append(pFiltros.isUsarPaginacion());
            if (pFiltros.isUsarPaginacion()) {
                sbTraza.append(" (Página: ").append(pFiltros.getNumeroPagina());
                sbTraza.append(", MaxResult: ").append(pFiltros.getResultadosPagina()).append(")");
            }
            LOGGER.debug(sbTraza.toString());

        }

        return result;
    }

    @Override
    public int findAllRangosCount(FiltroBusquedaRangos pFiltros) {

        // Filtros de Búsqueda definidos
        ArrayList<FiltroBusqueda> filtros = pFiltros.getListaFiltros();

        // Query SELECT rango FROM RANGO_SERIE sRango LEFT JOIN CAT_SERIE sSerie LEFT JOIN ABN_NIR sNir
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<RangoSerie> rango = cQuery.from(RangoSerie.class);
        Join<RangoSerie, Serie> sSerie = rango.join("serie", JoinType.LEFT);
        Join<Serie, Nir> sNir = sSerie.join("nir", JoinType.LEFT);

        // WHERE
        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = null;
            if (filtro.getPrefijo().equals("sRango")) {
                pred = this.getPredicateFromFiltro(filtro, rango, cb);
            } else if (filtro.getPrefijo().equals("sSerie")) {
                pred = this.getPredicateFromFiltro(filtro, sSerie, cb);
            } else {
                pred = this.getPredicateFromFiltro(filtro, sNir, cb);
            }
            wherePredicate = cb.and(wherePredicate, pred);
        }
        cQuery.where(wherePredicate).distinct(true);

        TypedQuery<Long> tQuery = getEntityManager().createQuery(cQuery.select(cb.count(rango)));
        int rowCount = tQuery.getSingleResult().intValue();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Rangos contados: " + rowCount);
        }

        // if (LOGGER.isDebugEnabled()) {
        // // Específico para EclipseLink. Hay que habilitar la dependencia con la libería en el POM
        // String sqlString = tQuery.unwrap(org.eclipse.persistence.jpa.JpaQuery.class).getDatabaseQuery()
        // .getSQLString();
        // LOGGER.debug("Criteria SqlString: " + sqlString);
        // }

        return rowCount;
    }

    @Override
    public RangoSerie getRangoPertenece(Nir nir, BigDecimal sna, String numInicio, String numFinal) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("nir {} sna {} numInicio {} numFinal {}", nir.getId(), sna, numInicio, numFinal);
        }

        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<RangoSerie> criteria = cb.createQuery(RangoSerie.class);

            Root<RangoSerie> rango = criteria.from(RangoSerie.class);
            criteria.select(rango);

            criteria.where(cb.equal(rango.get("id").get("idNir"), nir.getId()),
                    cb.equal(rango.get("id").get("sna"), sna),
                    cb.ge(rango.<Number> get("numInicio"), Integer.parseInt(numInicio)),
                    cb.le(rango.<Number> get("numFinal"), Integer.parseInt(numFinal)));

            TypedQuery<RangoSerie> query = getEntityManager().createQuery(criteria);

            RangoSerie res = query.getSingleResult();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("nir={}, sna={}, numIni={} numFin={} res={}", nir.getId(), sna, numInicio, numFinal, res);
            }

            return res;

        } catch (NoResultException e) {
            return null;
        }

    }

    @Override
    public RangoSerie getRangoPerteneceNumeracion(Nir nir, BigDecimal sna, String num) {

        try {

            String strQuery = "SELECT r FROM RangoSerie r "
                    + "where  r.id.idNir = :nir "
                    + "and r.id.sna = :sna "
                    + "and FUNC('TO_NUMBER',r.numInicio) <= FUNC('TO_NUMBER',:n) "
                    + "and FUNC('TO_NUMBER',r.numFinal) >= FUNC('TO_NUMBER',:n)";
            TypedQuery<RangoSerie> query = getEntityManager().createQuery(strQuery, RangoSerie.class);
            query.setParameter("nir", nir.getId());
            query.setParameter("sna", sna);
            query.setParameter("n", num);

            RangoSerie rango = query.getSingleResult();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("nir={}, sna={}, num={} res={}", nir.getId(), sna, num, rango);
            }

            return rango;

        } catch (NoResultException e) {
            return null;
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public BigDecimal getTotalRangosAsignadosByPst(String tipoRed, String tipoModalidad, Proveedor proveedor) {

        BigDecimal sum = new BigDecimal(0);

        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
            @SuppressWarnings("rawtypes")
            Root rango = query.from(RangoSerie.class);
            query.select(cb.sum(cb.sum(cb.diff(rango.<Number> get("numFinal"), rango.<Number> get("numInicio")), 1)));
            query.where(cb.notEqual(rango.get("estadoRango").get("codigo"), EstadoRango.PENDIENTE));

            List<Predicate> predicados = new ArrayList<Predicate>();

            if (proveedor != null) {
                predicados.add(cb.equal(rango.get("asignatario"), proveedor));
            }

            if (!tipoRed.equals("")) {
                predicados.add(cb.equal(rango.get("tipoRed").get("cdg"), tipoRed));
            }

            if (!tipoModalidad.equals("")) {
                predicados.add(cb.equal(rango.get("tipoModalidad").get("cdg"), tipoModalidad));
            }

            if (predicados.size() > 0) {
                Predicate[] arreglo = new Predicate[predicados.size()];
                for (int i = 0; i < predicados.size(); i++) { // los pasamos a un arreglo para que el CB pueda leerlo
                    arreglo[i] = predicados.get(i);
                }
                query.where(arreglo);
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Query: " + query);
            }

            sum = getEntityManager().createQuery(query).getSingleResult();
        } catch (NoResultException e) {

            return new BigDecimal(0);
        }

        if (sum == null) {
            return new BigDecimal(0);
        } else {
            return sum;
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public BigDecimal getTotalNumRangosAsignadosByPoblacion(String tipoRed, String tipoModalidad, Proveedor proveedor,
            String poblacion) {
        BigDecimal sum = new BigDecimal(0);

        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
            @SuppressWarnings("rawtypes")
            Root rango = query.from(RangoSerie.class);
            query.select(cb.sum(cb.sum(cb.diff(rango.<Number> get("numFinal"), rango.<Number> get("numInicio")), 1)));

            List<Predicate> predicados = new ArrayList<Predicate>();

            if (proveedor != null) {
                predicados.add(cb.equal(rango.get("asignatario"), proveedor));
            }

            predicados.add(cb.equal(rango.get("poblacion").get("inegi"), poblacion));

            predicados.add(cb.notEqual(rango.get("estadoRango").get("codigo"), EstadoRango.PENDIENTE));

            if (!tipoRed.equals("")) {
                predicados.add(cb.equal(rango.get("tipoRed").get("cdg"), tipoRed));
            }

            if (!tipoModalidad.equals("")) {
                predicados.add(cb.equal(rango.get("tipoModalidad").get("cdg"), tipoModalidad));
            }

            if (predicados.size() > 0) {
                Predicate[] arreglo = new Predicate[predicados.size()];
                for (int i = 0; i < predicados.size(); i++) { // los pasamos a un arreglo para que el CB pueda leerlo
                    arreglo[i] = predicados.get(i);
                }
                query.where(arreglo);
            }

            sum = getEntityManager().createQuery(query).getSingleResult();
        } catch (NoResultException e) {

            return new BigDecimal(0);
        }
        if (sum == null) {
            return new BigDecimal(0);
        } else {
            return sum;
        }
    }

    @Override
    public Poblacion getPoblacionWithMaxNumAsignadaByAbn(Abn abn) {

        try {
            String query = "SELECT p.poblacion FROM VPoblacionAbnMaxNumeracion p WHERE p.abn.codigoAbn =:codigoAbn";

            Query consulta = getEntityManager().createQuery(query);

            consulta.setParameter("codigoAbn", abn.getCodigoAbn());

            Poblacion poblacionAux = (Poblacion) consulta.getSingleResult();

            return poblacionAux;

        } catch (NoResultException e) {

            return null;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public BigDecimal getTotalNumRangosAsignadosByAbn(
            String tipoRed, String tipoModalidad, BigDecimal abn, Proveedor proveedor) {
        BigDecimal sum = new BigDecimal(0);

        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
            Root rango = query.from(RangoSerie.class);
            query.select(cb.sum(cb.sum(cb.diff(rango.<Number> get("numFinal"), rango.<Number> get("numInicio")), 1)));

            List<Predicate> predicados = new ArrayList<Predicate>();

            if (proveedor != null) {
                predicados.add(cb.equal(rango.get("asignatario"), proveedor));
            }

            Root poblacion = query.from(Poblacion.class);
            Root poblacionAbn = query.from(PoblacionAbn.class);

            predicados.add(cb.and(cb.equal(rango.get("poblacion").get("inegi"), poblacion.get("inegi"))));
            predicados.add(cb.and(cb.equal(rango.get("poblacion").get("inegi"), poblacionAbn.get("inegi")
                    .get("inegi"))));
            predicados.add(cb.and(cb.equal(poblacionAbn.get("abn").get("codigoAbn"), abn)));

            predicados.add(cb.notEqual(rango.get("estadoRango").get("codigo"), EstadoRango.PENDIENTE));

            if (!tipoRed.equals("")) {
                predicados.add(cb.equal(rango.get("tipoRed").get("cdg"), tipoRed));
            }

            if (!tipoModalidad.equals("")) {
                predicados.add(cb.equal(rango.get("tipoModalidad").get("cdg"), tipoModalidad));
            }

            if (predicados.size() > 0) {
                Predicate[] arreglo = new Predicate[predicados.size()];
                for (int i = 0; i < predicados.size(); i++) { // los pasamos a un arreglo para que el CB pueda leerlo
                    arreglo[i] = predicados.get(i);
                }
                query.where(arreglo);
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Query: " + query);
            }

            sum = getEntityManager().createQuery(query).getSingleResult();
        } catch (NoResultException e) {

            return new BigDecimal(0);
        }
        if (sum == null) {
            return new BigDecimal(0);
        } else {
            return sum;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public BigDecimal getTotalNumRangosArrendados(Proveedor arrendador, Proveedor arrendadatario) {
        BigDecimal sum = new BigDecimal(0);

        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
            @SuppressWarnings("rawtypes")
            Root rango = query.from(RangoSerie.class);
            query.select(cb.sum(cb.sum(cb.diff(rango.<Number> get("numFinal"), rango.<Number> get("numInicio")), 1)));

            List<Predicate> predicados = new ArrayList<Predicate>();

            predicados.add(cb.notEqual(rango.get("estadoRango").get("codigo"), EstadoRango.PENDIENTE));

            if (arrendador != null) {
                predicados.add(cb.equal(rango.get("asignatario"), arrendador));
            }

            if (arrendadatario != null) {
                predicados.add(cb.equal(rango.get("arrendatario"), arrendadatario));
            }

            if (predicados.size() > 0) {
                Predicate[] arreglo = new Predicate[predicados.size()];
                for (int i = 0; i < predicados.size(); i++) { // los pasamos a un arreglo para que el CB pueda leerlo
                    arreglo[i] = predicados.get(i);
                }
                query.where(arreglo);
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Query: " + query);
            }

            sum = getEntityManager().createQuery(query).getSingleResult();
        } catch (NoResultException e) {

            return new BigDecimal(0);
        }
        if (sum == null) {
            return new BigDecimal(0);
        } else {
            return sum;
        }
    }

    /**
     * Construye las condiciones a partir del filtro.
     * @param filtro de busqueda
     * @return cadena jpa
     */
    private String contruirFiltro(FiltroBusquedaHistoricoSeries filtro) {
        StringBuilder b = new StringBuilder();
        if (filtro.getIdAbn() != null) {
            b.append(" and rango.poblacion.poblacionAbn.abn.codigoAbn=");
            // b.append(" and rango.poblacion.inegi=pa.inegi.inegi and pa.abn.codigoAbn='");
            b.append(filtro.getIdAbn());
            // b.append("'");
        }

        if (filtro.getIdNir() != null) {
            b.append(" and rango.serie.nir.codigo=");
            b.append(filtro.getIdNir());
        }

        if (filtro.getIdSna() != null) {
            b.append(" and rango.id.sna=");
            b.append(filtro.getIdSna());
        }

        if (filtro.getNumIni() != null) {
            b.append(" and rango.numInicio=");
            b.append(filtro.getNumIni());
        }

        if (filtro.getNumFin() != null) {
            b.append(" and rango.numFinal=");
            b.append(filtro.getNumFin());
        }

        if (filtro.getEstado() != null) {
            b.append(" and rango.poblacion.municipio.estado.codEstado=");
            b.append(filtro.getEstado().getCodEstado());
        }

        if (filtro.getMunicipio() != null) {
            b.append(" and rango.poblacion.municipio.id.codMunicipio=");
            b.append(filtro.getMunicipio().getId().getCodMunicipio());
        }

        if (filtro.getPoblacion() != null) {
            b.append(" and rango.poblacion.cdgPoblacion=");
            b.append(filtro.getPoblacion().getCdgPoblacion());
        }

        if (filtro.getTipoRed() != null) {
            b.append(" and rango.tipoRed.cdg='");
            b.append(filtro.getTipoRed().getCdg());
            b.append("'");
        }

        if (filtro.getTipoModalidad() != null) {
            b.append(" and rango.tipoModalidad.cdg='");
            b.append(filtro.getTipoModalidad().getCdg());
            b.append("'");
        }

        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy");

        if (filtro.getFechaInicio() != null) {
            b.append(" and rango.solicitud.fechaAsignacion>= FUNCTION('TO_DATE','");
            b.append(dt.format(filtro.getFechaInicio()));
            b.append("','dd-MM-yyyy')");
        }

        if (filtro.getFechaFin() != null) {
            b.append(" and rango.solicitud.fechaAsignacion<= FUNCTION('TO_DATE','");
            b.append(dt.format(filtro.getFechaFin()));
            b.append("','dd-MM-yyyy')");
        }

        String r = b.toString();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("filtro '{}'", r);
        }

        return r;
    }

    @Override
    public List<RangoSerie> findHistoricoSeries(FiltroBusquedaHistoricoSeries filtro, int first, int pagesize) {

        LOGGER.debug("firt {} pagesize {}", first, pagesize);

        StringBuilder b = new StringBuilder();

        b.append("select distinct rango from RangoSerie rango join rango.solicitud sol, sol.oficio so where (1=1) ");

        String f = contruirFiltro(filtro);
        b.append(f);
        b.append(" order by rango.solicitud.id");

        String q = b.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: {}", q);
        }

        Query query = getEntityManager().createQuery(q);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: " + query);
        }

        query.setFirstResult(first);
        query.setMaxResults(pagesize);

        @SuppressWarnings("unchecked")
        List<RangoSerie> list = query.getResultList();

        if (LOGGER.isDebugEnabled()) {
            if (list != null) {
                LOGGER.debug("#n {}", list.size());
            }
        }
        return list;
    }

    @Override
    public int findHistoricoSeriesCount(FiltroBusquedaHistoricoSeries filtro) {

        StringBuilder b = new StringBuilder();
        // b.append("select count(distinct rango) from RangoSerie rango , Solicitud  sol,  Oficio so ,PoblacionAbn pa  "
        // + "where rango.id.id=sol.id and so.id=sol.id");
        b.append("select count(distinct rango) from RangoSerie rango join rango.solicitud sol, sol.oficio so ");
        b.append("where (1=1) ");
        String f = contruirFiltro(filtro);
        b.append(f);

        String q = b.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: {}", q);
        }

        Query query = getEntityManager().createQuery(q);

        int l = ((Long) query.getSingleResult()).intValue();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("#={} Query: {}", l, query);
        }

        return l;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<RangoSerie> findAllRangosByNumSol(NumeracionSolicitada numeracionSolicitada) {
        List<RangoSerie> lista = new ArrayList<RangoSerie>();
        String sQuery = "SELECT r FROM RangoSerie r WHERE r.numSolicitada = :numSolicitada";
        Query query = getEntityManager().createQuery(sQuery);
        query.setParameter("numSolicitada", numeracionSolicitada);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: " + query);
        }

        lista = query.getResultList();
        return lista;
    }

    @Override
    public BigDecimal getNumRangosAsignados() {

        String sql = "SELECT count(r) FROM RangoSerie r WHERE r.estadoRango.codigo = :estado"
                + " or r.estadoRango.codigo = :migrado"
        		+ " or r.estadoRango.codigo = :reservado";
        Query query = getEntityManager().createQuery(sql);
        query.setParameter("estado", EstadoRango.ASIGNADO);
        query.setParameter("migrado", EstadoRango.MIGRADO);
        query.setParameter("reservado", EstadoRango.RESERVADO);

        int total = 0;
        try {
            total = ((Long) query.getSingleResult()).intValue();
        } catch (NoResultException e) {
            total = 0;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: {} num={}", query, total);
        }

        return new BigDecimal(total);
    }

    @Override
    public List<RangoSerie> getRangosAsignados(int first, int pageSize) {

        String sql = "SELECT r FROM RangoSerie r WHERE r.estadoRango.codigo = :estado"
                + " or r.estadoRango.codigo = :migrado"
                + " or r.estadoRango.codigo = :reservado"
                + " order by r.id.sna asc, r.numInicio asc, r.numFinal asc";
        Query query = getEntityManager().createQuery(sql);
        query.setParameter("estado", EstadoRango.ASIGNADO);
        query.setParameter("migrado", EstadoRango.MIGRADO);
        query.setParameter("reservado", EstadoRango.RESERVADO);

        query.setFirstResult(first);
        query.setMaxResults(pageSize);
        @SuppressWarnings("unchecked")
        List<RangoSerie> list = query.getResultList();

        return list;
    }

    @Override
    public List<Nir> findNirsNumeroLocal(BigDecimal sna, String num) {

        String sql = "SELECT distinct(n) FROM RangoSerie r, Nir n WHERE r.estadoRango.codigo = :estado "
                + " and r.id.idNir=n.id"
                + " and r.id.sna=:sna "
                + " and FUNC('TO_NUMBER',:num) >= FUNC('TO_NUMBER',r.numInicio)"
                + " and FUNC('TO_NUMBER',:num) <= FUNC('TO_NUMBER',r.numFinal)"
                + " order by n.codigo asc";
        Query query = getEntityManager().createQuery(sql);
        query.setParameter("estado", EstadoRango.ASIGNADO);
        query.setParameter("sna", sna);
        query.setParameter("num", num);

        @SuppressWarnings("unchecked")
        List<Nir> list = query.getResultList();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: {} num={}", query, list.size());
        }

        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Integer getTotalNumRangosAsignadosByMunicipio(Municipio municipio) {
        BigDecimal sum = new BigDecimal(0);

        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
            @SuppressWarnings("rawtypes")
            Root rango = query.from(RangoSerie.class);
            query.select(cb.sum(cb.sum(cb.diff(rango.<Number> get("numFinal"), rango.<Number> get("numInicio")), 1)));

            List<Predicate> predicados = new ArrayList<Predicate>();

            predicados.add(cb.equal(rango.get("poblacion").get("municipio"), municipio));

            predicados.add(cb.notEqual(rango.get("estadoRango").get("codigo"), EstadoRango.PENDIENTE));

            if (predicados.size() > 0) {
                Predicate[] arreglo = new Predicate[predicados.size()];
                for (int i = 0; i < predicados.size(); i++) { // los pasamos a un arreglo para que el CB pueda leerlo
                    arreglo[i] = predicados.get(i);
                }
                query.where(arreglo);
            }

            sum = getEntityManager().createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No se han encontrado rangos asignados al municipio {}",
                        municipio.getId().getCodMunicipio());
            }
            return new Integer(0);
        }
        if (sum == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No se han encontrado rangos asignados al municipio {}",
                        municipio.getId().getCodMunicipio());
            }
            return new Integer(0);
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Se han encontrado {} rangos asignados al municipio {}",
                        sum.intValue(), municipio.getId().getCodMunicipio());
            }
            return sum.intValue();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Integer getTotalNumRangosAsignadosByEstado(Estado estado) {
        BigDecimal sum = new BigDecimal(0);

        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
            @SuppressWarnings("rawtypes")
            Root rango = query.from(RangoSerie.class);
            query.select(cb.sum(cb.sum(cb.diff(rango.<Number> get("numFinal"), rango.<Number> get("numInicio")), 1)));
            Predicate wherePredicado = cb.conjunction();
            Predicate predEqual = cb.equal(rango.get("poblacion").get("municipio").get("estado"), estado);
            Predicate predNotEqual = cb.notEqual(rango.get("estadoRango").get("codigo"), EstadoRango.PENDIENTE);
            wherePredicado = cb.and(predEqual, predNotEqual);
            query.where(wherePredicado);
            sum = getEntityManager().createQuery(query).getSingleResult();

        } catch (NoResultException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No se han encontrado rangos asignados al estado {}",
                        estado.getCodEstado());
            }
            return new Integer(0);
        }
        if (sum == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No se han encontrado rangos asignados al estado {}",
                        estado.getCodEstado());
            }
            return new Integer(0);
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Se han encontrado {} rangos asignados al estado {}",
                        sum.intValue(), estado.getCodEstado());
            }
            return sum.intValue();
        }
    }

    @Override
    public int getNirsByPoblacion(Poblacion poblacion) {
        String query = "SELECT COUNT(DISTINCT r.id.idNir) "
                + "FROM RangoSerie r WHERE r.poblacion.inegi=:inegi";
        TypedQuery<Long> nativeQuery = getEntityManager().createQuery(query, Long.class);
        nativeQuery.setParameter("inegi", poblacion.getInegi());
        int rowCount = nativeQuery.getSingleResult().intValue();
        if (LOGGER.isDebugEnabled()) {
            StringBuffer sbTrazas = new StringBuffer();
            sbTrazas.append("Se han contado ").append(rowCount).append(" nirs ");
            sbTrazas.append("con código ABN: ").append(poblacion.getInegi());
        }
        return rowCount;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Nir> findAllNirsByPoblacion(Poblacion poblacion) {
        String strQuery = "SELECT DISTINCT r.serie.nir "
                + "FROM RangoSerie r WHERE r.poblacion.inegi=:inegi";
        List<Nir> list = new ArrayList<Nir>();

        Query query = getEntityManager().createQuery(strQuery);
        query.setParameter("inegi", poblacion.getInegi());

        // if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug("Query: " + query);
        // }

        list = query.getResultList();

        return list;
    }

    @Override
    public BigDecimal getNumeracionPoblacionByProveedor(Proveedor proveedorServ, Poblacion poblacion) {

        String sql = "SELECT SUM(FUNCTION('TO_NUMBER',r.numFinal)-FUNCTION('TO_NUMBER',r.numInicio)) "
                + " FROM RangoSerie r WHERE r.poblacion = :poblacion AND  "
                + " r.proveedor = :proveedor";

        Query query = getEntityManager().createQuery(sql);
        query.setParameter("proveedor", proveedorServ);
        query.setParameter("poblacion", poblacion);
        try {
            BigDecimal numeracion = (BigDecimal) query.getSingleResult();
            return numeracion;
        } catch (NoResultException e) {

            return new BigDecimal(0);
        }
    }

    @Override
    public List<PoblacionNumeracion> getPoblacionesNumeracionByProveedor(Proveedor proveedorServ) {
        String sql = "SELECT NEW mx.ift.sns.modelo.ot.PoblacionNumeracion"
                + "(SUM(FUNCTION('TO_NUMBER',r.numFinal)-FUNCTION('TO_NUMBER',r.numInicio)),r.poblacion) "
                + " FROM RangoSerie r WHERE r.arrendatario = :proveedor "
                + " or (r.asignatario =:proveedor and r.arrendatario is null) GROUP BY r.poblacion";

        Query query = getEntityManager().createQuery(sql);
        query.setParameter("proveedor", proveedorServ);

        @SuppressWarnings("unchecked")
        List<PoblacionNumeracion> poblacionesNumeracion = query.getResultList();

        return poblacionesNumeracion;
    }

    @Override
    public List<EstadoNumeracion> getEstadosNumeracionByProveedor(Proveedor proveedorServ) {
        String sql = "SELECT NEW mx.ift.sns.modelo.ot.EstadoNumeracion"
                + "(SUM(FUNCTION('TO_NUMBER',r.numFinal)-FUNCTION('TO_NUMBER',r.numInicio)),"
                + "r.poblacion.municipio.estado) "
                + " FROM RangoSerie r WHERE r.arrendatario = :proveedor "
                + " or (r.asignatario =:proveedor and r.arrendatario is null) GROUP BY r.poblacion.municipio.estado";

        Query query = getEntityManager().createQuery(sql);
        query.setParameter("proveedor", proveedorServ);

        @SuppressWarnings("unchecked")
        List<EstadoNumeracion> estadosNumeracion = query.getResultList();

        return estadosNumeracion;
    }

    @Override
    public List<NirNumeracion> getNirsNumeracionByProveedor(Proveedor proveedorServ) {
        String sql = "SELECT NEW mx.ift.sns.modelo.ot.NirNumeracion"
                + "(SUM(FUNCTION('TO_NUMBER',r.numFinal)-FUNCTION('TO_NUMBER',r.numInicio)),r.serie.nir) "
                + " FROM RangoSerie r WHERE r.arrendatario = :proveedor "
                + " or (r.asignatario =:proveedor and r.arrendatario is null) GROUP BY r.serie.nir";

        Query query = getEntityManager().createQuery(sql);
        query.setParameter("proveedor", proveedorServ);

        @SuppressWarnings("unchecked")
        List<NirNumeracion> nirsNumeracion = query.getResultList();

        return nirsNumeracion;
    }

    @Override
    public boolean existeNumeracionAsignadaAlPstByConvenio(ProveedorConvenio proveedorConvenio) {
        String hql = "SELECT count(1) FROM RangoSerie rs WHERE rs.asignatario = :proveedor "
                + "AND rs.concesionario = :pstArrendador AND rs.estadoRango.codigo <> :estadoRango";

        TypedQuery<Long> tQuery = getEntityManager().createQuery(hql, Long.class);
        tQuery.setParameter("proveedor", proveedorConvenio.getProveedorConvenio());
        tQuery.setParameter("pstArrendador", proveedorConvenio.getProveedorConcesionario());
        tQuery.setParameter("estadoRango", EstadoRango.PENDIENTE);

        Long resultado = tQuery.getSingleResult();
        if (resultado == null || resultado.longValue() == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean existeNumeracionAsignadaAlPst(Proveedor proveedor) {
        String hql = "SELECT count(1) FROM RangoSerie rs WHERE rs.asignatario = :proveedor "
                + "AND rs.estadoRango.codigo <> :estadoRango";

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

    @SuppressWarnings("unchecked")
    @Override
    public Integer getTotalNumeracionAginadaProveedor(BigDecimal idPst) {
        BigDecimal sum = new BigDecimal(0);

        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
            @SuppressWarnings("rawtypes")
            Root rango = query.from(RangoSerie.class);
            query.select(cb.sum(cb.sum(cb.diff(rango.<Number> get("numFinal"), rango.<Number> get("numInicio")), 1)));
            Predicate wherePredicado = cb.conjunction();
            Predicate predEqual = cb.equal(rango.get("asignatario").get("id"), idPst);
            Predicate predNotEqual = cb.notEqual(rango.get("estadoRango").get("codigo"), EstadoRango.PENDIENTE);
            wherePredicado = cb.and(predEqual, predNotEqual);
            query.where(wherePredicado);
            sum = getEntityManager().createQuery(query).getSingleResult();

        } catch (NoResultException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No se han encontrado rangos asignados al proveedor con id {}",
                        idPst);
            }
            return new Integer(0);
        }
        if (sum == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No se han encontrado rangos asignados al proveedor con id {}",
                        idPst);
            }
            return new Integer(0);
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Se han encontrado {} rangos asignados al proveedor con id {}",
                        sum.intValue(), idPst);
            }
            return sum.intValue();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PoblacionNumeracion>
            findALLPoblacionesNumeracionByProveedorEstado(Proveedor proveedorServ, Estado estado) {
        String sql = "SELECT NEW mx.ift.sns.modelo.ot.PoblacionNumeracion"
                + "(SUM(FUNCTION('TO_NUMBER',r.numFinal)-FUNCTION('TO_NUMBER',r.numInicio)),r.poblacion) "
                + " FROM RangoSerie r WHERE r.arrendatario = :proveedor"
                + " or (r.asignatario =:proveedor and r.arrendatario is null"
                + " AND r.poblacion.municipio.estado = :estado) GROUP BY r.poblacion";

        Query query = getEntityManager().createQuery(sql);
        query.setParameter("proveedor", proveedorServ);
        query.setParameter("estado", estado);

        List<PoblacionNumeracion> poblacionesNumeracion = query.getResultList();

        return poblacionesNumeracion;
    }

    @Override
    public List<PoblacionNumeracion>
            findALLPoblacionesNumeracionByProveedorNir(Proveedor proveedorServ, Nir nir) {
        String sql = "SELECT NEW mx.ift.sns.modelo.ot.PoblacionNumeracion"
                + "(SUM(FUNCTION('TO_NUMBER',r.numFinal)-FUNCTION('TO_NUMBER',r.numInicio)),r.poblacion) "
                + " FROM RangoSerie r WHERE r.arrendatario = :proveedor"
                + " or (r.asignatario =:proveedor and r.arrendatario is null"
                + " AND r.serie.nir = :nir) GROUP BY r.poblacion";

        Query query = getEntityManager().createQuery(sql);
        query.setParameter("proveedor", proveedorServ);
        query.setParameter("nir", nir);

        @SuppressWarnings("unchecked")
        List<PoblacionNumeracion> poblacionesNumeracion = query.getResultList();

        return poblacionesNumeracion;
    }

    @Override
    public int findAllRangosAsignadosFijosCount() {

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<RangoSerie> rango = cQuery.from(RangoSerie.class);
        cQuery.select(cb.count(rango));
        cQuery.where(cb.notEqual(rango.get("estadoRango").get("codigo"), EstadoRango.PENDIENTE),
                cb.equal(rango.get("tipoRed").get("cdg"), TipoRed.FIJA));

        TypedQuery<Long> tQuery = getEntityManager().createQuery(cQuery);
        int rowCount = tQuery.getSingleResult().intValue();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Rangos contados: " + rowCount);
        }

        return rowCount;
    }

    @Override
    public List<RangoSerie> findAllRangosAsignadosFijos(int numPage, int pageSize) {

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<RangoSerie> cQuery = cb.createQuery(RangoSerie.class);
        Root<RangoSerie> rango = cQuery.from(RangoSerie.class);

        cQuery.where(cb.notEqual(rango.get("estadoRango").get("codigo"), EstadoRango.PENDIENTE),
                cb.equal(rango.get("tipoRed").get("cdg"), TipoRed.FIJA));

        TypedQuery<RangoSerie> tQuery = getEntityManager().createQuery(cQuery);
        List<RangoSerie> result = tQuery.setFirstResult(numPage).setMaxResults(pageSize).getResultList();

        return result;
    }

    @Override
    public List<Proveedor> findAllPrestadoresServicioBy(Nir nir, Abn abn, Poblacion poblacion, Municipio municipio,
            Estado estado) {

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Proveedor> query = cb.createQuery(Proveedor.class);
        Root<RangoSerie> rango = query.from(RangoSerie.class);

        // Obtenemos lista de arrendatarios
        CriteriaQuery<Proveedor> subQuery = cb.createQuery(Proveedor.class);
        subQuery.select(rango.<Proveedor> get("arrendatario")).distinct(true);
        Path<Object> path = null;
        Object value = null;
        if (nir != null) {
            path = rango.get("serie").get("nir");
            value = nir;
        } else if (abn != null) {
            path = rango.get("serie").get("nir").get("abn");
            value = abn;
        } else if (poblacion != null) {
            path = rango.get("poblacion");
            value = poblacion;
        } else if (municipio != null) {
            path = rango.get("poblacion").get("municipio");
            value = municipio;
        } else if (estado != null) {
            path = rango.get("poblacion").get("municipio").get("estado");
            value = estado;
        }
        subQuery.where(
                cb.equal(path, value),
                cb.notEqual(rango.get("estadoRango").get("codigo"), EstadoRango.PENDIENTE)
                );
        subQuery.orderBy(cb.asc(rango.get("asignatario").get("nombreCorto")));
        List<Proveedor> idArrendatarios = getEntityManager().createQuery(subQuery).getResultList();

        // Obtenemos lista de asignatarios
        subQuery.select(rango.<Proveedor> get("asignatario")).distinct(true);
        subQuery.where(
                cb.equal(path, value),
                cb.notEqual(rango.get("estadoRango").get("codigo"), EstadoRango.PENDIENTE)
                );
        subQuery.orderBy(cb.asc(rango.get("asignatario").get("nombreCorto")));
        List<Proveedor> idAsignatarios = getEntityManager().createQuery(subQuery).getResultList();

        // Lista con todos los proveedores
        List<Proveedor> proveedorPrestador = new ArrayList<Proveedor>();
        proveedorPrestador.addAll(idArrendatarios);
        proveedorPrestador.addAll(idAsignatarios);
        if (LOGGER.isDebugEnabled()) {
            if (nir != null) {
                LOGGER.debug("Se han encontrado {} proveedores para el Nir: {}",
                        proveedorPrestador.size(), nir.getCodigo());
            } else if (abn != null) {
                LOGGER.debug("Se han encontrado {} proveedores para el Abn: {}",
                        proveedorPrestador.size(), abn.getCodigoAbn());
            } else if (poblacion != null) {
                LOGGER.debug("Se han encontrado {} proveedores para la Población: {}",
                        proveedorPrestador.size(), poblacion.getNombre());
            } else if (municipio != null) {
                LOGGER.debug("Se han encontrado {} proveedores para el Municipio: {}",
                        proveedorPrestador.size(), municipio.getNombre());
            } else if (estado != null) {
                LOGGER.debug("Se han encontrado {} proveedores para el Estado: {}",
                        proveedorPrestador.size(), estado.getNombre());
            }
        }
        return proveedorPrestador;
    }

    @Override
    public List<Poblacion> findALLPoblacionesNumeracionAsignadaByNir(Nir nir) {
        String sql = "SELECT DISTINCT(r.poblacion)"
                + " FROM RangoSerie r WHERE r.estadoRango.codigo <> :estadoRango"
                + " AND r.serie.nir = :nir"
                + " ORDER BY r.poblacion.nombre";

        Query query = getEntityManager().createQuery(sql);
        query.setParameter("estadoRango", EstadoRango.PENDIENTE);
        query.setParameter("nir", nir);

        @SuppressWarnings("unchecked")
        List<Poblacion> poblacionesNumeracion = query.getResultList();

        return poblacionesNumeracion;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int findNumeracionesAsignadasNir(Nir nir) {

        BigDecimal sum = new BigDecimal(0);

        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
            @SuppressWarnings("rawtypes")
            Root rango = query.from(RangoSerie.class);
            query.select(cb.sum(cb.sum(cb.diff(rango.<Number> get("numFinal"), rango.<Number> get("numInicio")), 1)));
            Predicate wherePredicado = cb.conjunction();
            Predicate predEqual = cb.equal(rango.get("serie").get("nir"), nir);
            Predicate predNotEqual = cb.notEqual(rango.get("estadoRango").get("codigo"), EstadoRango.PENDIENTE);
            wherePredicado = cb.and(predEqual, predNotEqual);
            query.where(wherePredicado);
            sum = getEntityManager().createQuery(query).getSingleResult();

        } catch (NoResultException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No se han encontrado rangos asignados al nir {}",
                        nir.getCodigo());
            }
            return new Integer(0);
        }
        if (sum == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No se han encontrado rangos asignados al nir {}",
                        nir.getCodigo());
            }
            return new Integer(0);
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Se han encontrado {} rangos asignados al nir {}",
                        sum.intValue(), nir.getCodigo());
            }
            return sum.intValue();
        }
    }

    @Override
    public List<Poblacion> findALLPoblacionesNumeracionAsignadaByMunicipio(Municipio municipio) {
        String sql = "SELECT DISTINCT(r.poblacion)"
                + " FROM RangoSerie r WHERE r.estadoRango.codigo <> :estadoRango"
                + " AND r.poblacion.municipio = :municipio"
                + " ORDER BY r.poblacion.nombre";

        Query query = getEntityManager().createQuery(sql);
        query.setParameter("estadoRango", EstadoRango.PENDIENTE);
        query.setParameter("municipio", municipio);

        @SuppressWarnings("unchecked")
        List<Poblacion> poblacionesNumeracion = query.getResultList();

        return poblacionesNumeracion;
    }

    @Override
    public boolean existeNumeracionAsignada(Nir nir, BigDecimal sna, String num) {

        try {

            String strQuery = "SELECT COUNT(1) FROM RangoSerie r WHERE r.id.idNir = :nir "
                    + "AND r.id.sna = :sna AND :num BETWEEN r.numInicio AND r.numFinal "
                    + "AND r.estadoRango.codigo = :estatusRango";

            TypedQuery<Long> query = getEntityManager().createQuery(strQuery, Long.class);
            query.setParameter("nir", nir.getId());
            query.setParameter("sna", sna);
            query.setParameter("num", num);
            query.setParameter("estatusRango", EstadoRango.ASIGNADO);

            Long numRangos = query.getSingleResult();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("nir={}, sna={}, num={} res={}", nir.getId(), sna, num, numRangos);
            }

            return numRangos > 0;

        } catch (NoResultException e) {
            return false;
        }

    }

    @Override
    public List<RangoSerie> findAllRangosBySolicitud(Solicitud pSolicitud) {
        String strQuery = "SELECT r FROM RangoSerie r WHERE r.solicitud = :solicitud";
        TypedQuery<RangoSerie> tQuery = getEntityManager().createQuery(strQuery, RangoSerie.class);
        tQuery.setParameter("solicitud", pSolicitud);
        return tQuery.getResultList();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public boolean isRangoLibre(BigDecimal nir, BigDecimal sna, String inicioRango, String finRango) {
        Long resultado = null;

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root rango = query.from(RangoSerie.class);

        query.select(cb.count(rango));
        query.where(
                cb.equal(rango.get("id").get("idNir"), nir),
                cb.equal(rango.get("id").get("sna"), sna),
                cb.or(
                        cb.between(rango.get("numInicio"), inicioRango, finRango),
                        cb.between(rango.get("numFinal"), inicioRango, finRango),
                        cb.and(
                                cb.lessThanOrEqualTo(rango.get("numInicio"),
                                        StringUtils.leftPad(inicioRango, 4, '0')),
                                cb.greaterThanOrEqualTo(rango.get("numFinal"),
                                        StringUtils.leftPad(finRango, 4, '0')))));

        resultado = getEntityManager().createQuery(query).getSingleResult();

        return resultado == 0;
    }

    @Override
    public List<DetallePlanAbdPresuscripcion> findAllRangosAsignadosFijosD(int numPage, int pageSize) {

        String sql = "SELECT NEW mx.ift.sns.modelo.pnn.DetallePlanAbdPresuscripcion("
                + "nir.codigo, ser.id.sna, "
                + "rs.numInicio, rs.numFinal, rs.poblacion.inegi, pob.nombre, est.nombre,"
                + "rs.idoPnn, pst.nombre, rs.fechaAsignacion,  abn.codigoAbn, tred.descripcion, rs.idaPnn) "
                + "FROM RangoSerie rs, Nir nir, Serie ser, Poblacion pob, Estado est, "
                + "Proveedor pst, Abn abn, TipoRed tred "
                + "WHERE "
                + " rs.tipoRed.cdg = 'F' and rs.estadoRango.codigo <> 'P' and "
                + "rs.serie.id.idNir = ser.id.idNir and rs.serie.id.sna = ser.id.sna "
                + " and ser.id.idNir = nir.id and rs.poblacion.inegi = pob.inegi and "
                + "pob.municipio.estado.codEstado = est.codEstado "
                + " and rs.asignatario.id = pst.id and nir.abn.codigoAbn = abn.codigoAbn and rs.tipoRed.cdg = tred.cdg";

        Query query = getEntityManager().createQuery(sql).setFirstResult(numPage).setMaxResults(pageSize);

        @SuppressWarnings("unchecked")
        List<DetallePlanAbdPresuscripcion> detalle = query.getResultList();

        return detalle;
    }

    @Override
    public List<DetalleRangoAsignadoNg> getRangosAsignadosD(int first, int pageSize) {

        //String sql = "SELECT det FROM DetalleRangoAsignadoNg det ORDER BY det.idSerie, det.numInicio, det.numFinal";
        String sql = "SELECT det FROM DetalleRangoAsignadoNg det ORDER BY det.id asc";
        Query query = getEntityManager().createQuery(sql).setFirstResult(first).setMaxResults(pageSize);

        @SuppressWarnings("unchecked")
        List<DetalleRangoAsignadoNg> detalle = query.getResultList();

        return detalle;
    }

    @Override
    public boolean isRangosPentientesByNir(Nir nir) {
        String sQuery =
                "SELECT COUNT(r) "
                        + "FROM RangoSerie r "
                        + "WHERE r.serie.nir.codigo = :codigoNir "
                        + "and r.estadoRango.codigo= :estado";
        TypedQuery<Long> query = getEntityManager().createQuery(sQuery, Long.class);
        query.setParameter("codigoNir", nir.getCodigo());
        query.setParameter("estado", EstadoRango.PENDIENTE);

        Long resultado = query.getSingleResult();
        return resultado != null && resultado > 0;
    }

    @Override
    public List<RangoSerie> getRangoAsignadoByIdo(String codigoNir, String sna, String ido) {
        try {
            String strQuery = "SELECT r FROM RangoSerie r WHERE r.serie.nir.codigo = :nir "
                    + " AND r.id.sna = :sna AND r.estadoRango.codigo = :estatus AND r.asignatario.ido = :ido "
                    + "ORDER BY r.numInicio asc, r.numFinal asc";
            TypedQuery<RangoSerie> query = getEntityManager().createQuery(strQuery, RangoSerie.class);
            query.setParameter("nir", Integer.valueOf(codigoNir));
            query.setParameter("sna", new BigDecimal(sna));
            query.setParameter("estatus", EstadoRango.ASIGNADO);
            query.setParameter("ido", new BigDecimal(ido));

            return query.getResultList();
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public List<RangoSerie> getRangoAsignados(String codigoNir, String sna) {
        try {
            String strQuery = "SELECT r FROM RangoSerie r WHERE r.serie.nir.codigo = :nir AND r.id.sna = :sna "
                    + " AND r.estadoRango.codigo = :estatus ORDER BY r.numInicio asc, r.numFinal asc";
            TypedQuery<RangoSerie> query = getEntityManager().createQuery(strQuery, RangoSerie.class);
            query.setParameter("nir", Integer.valueOf(codigoNir));
            query.setParameter("sna", new BigDecimal(sna));
            query.setParameter("estatus", EstadoRango.ASIGNADO);

            return query.getResultList();
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public List<DetalleReporteAbd> getDetalleReporteAbd(int first, int pageSize) {

        String sql = "SELECT rep FROM DetalleReporteAbd rep ORDER BY rep.id, rep.numFinal";

        Query query = getEntityManager().createQuery(sql).setFirstResult(first).setMaxResults(pageSize);

        @SuppressWarnings("unchecked")
        List<DetalleReporteAbd> detalle = query.getResultList();

        return detalle;
    }

    @Override
    public Long getDetalleReporteAbdCount() {

        String sql = "SELECT count(rep) FROM DetalleReporteAbd rep";

        Query query = getEntityManager().createQuery(sql);

        long total = 0;
        try {
            total = ((Long) query.getSingleResult()).longValue();
        } catch (NoResultException e) {
            total = 0;
        }

        return total;
    }

    @Override
    public void generarPnnAux() {
        Query query = getEntityManager().createNamedQuery("generarPnnAux");
        query.executeUpdate();
    }

    /**
     * Metodo que recupera el total de NIR asignados a traves de la Zona.
     */
    @Override
    public Integer getTotalNumeracionAsignadaPorZona(Integer idZona) {
        String squery =
                "SELECT SUM((rs.N_FINAL - rs.N_INICIO) + 1) " +
                        "FROM RANGO_SERIE rs " +
                        "JOIN CAT_NIR nir ON rs.ID_NIR = nir.ID_NIR " +
                        "WHERE rs.ID_STATUS_RANGO = 'A' " +
                        "AND nir.ZONA = ?1";

        Object result = getEntityManager()
                .createNativeQuery(squery)
                .setParameter(1, idZona)
                .getSingleResult();

        return result != null ? ((Number) result).intValue() : 0;
    }

    /**
     * FJAH 01.07.2025
     * Reemplazo total con agrupamiento por NOMBRE_CORTO
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Proveedor> findAllPrestadoresServicioByZona(Integer idZona) {
        String sql =
                "SELECT p.NOMBRE_CORTO, MIN(p.NOMBRE) AS NOMBRE " +
                        "FROM RANGO_SERIE rs " +
                        "JOIN CAT_NIR n ON rs.ID_NIR = n.ID_NIR " +
                        "JOIN CAT_PST p ON p.ID_PST = rs.ID_PST_ARRENDATARIO " +
                        "WHERE n.ZONA = ? " +
                        "  AND rs.ID_STATUS_RANGO = 'A' " +
                        "GROUP BY p.NOMBRE_CORTO " +
                        "UNION ALL " +
                        "SELECT p.NOMBRE_CORTO, MIN(p.NOMBRE) AS NOMBRE " +
                        "FROM RANGO_SERIE rs " +
                        "JOIN CAT_NIR n ON rs.ID_NIR = n.ID_NIR " +
                        "JOIN CAT_PST p ON p.ID_PST = rs.ID_PST_ASIGNATARIO " +
                        "WHERE n.ZONA = ? " +
                        "  AND rs.ID_STATUS_RANGO = 'A' " +
                        "GROUP BY p.NOMBRE_CORTO";

        List<Object[]> rows = getEntityManager()
                .createNativeQuery(sql)
                .setParameter(1, idZona)
                .setParameter(2, idZona)
                .getResultList();

        List<Proveedor> resultado = new ArrayList<>();
        for (Object[] row : rows) {
            Proveedor p = new Proveedor();
            p.setNombreCorto((String) row[0]);
            p.setNombre((String) row[1]);
            resultado.add(p);
        }

        return resultado;
    }




}
