package mx.ift.sns.dao.cpsi.implementacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.cpsi.ICodigoCpsiDao;
import mx.ift.sns.modelo.cpsi.CodigoCPSI;
import mx.ift.sns.modelo.cpsi.EstatusCPSI;
import mx.ift.sns.modelo.cpsi.InfoCatCpsi;
import mx.ift.sns.modelo.cpsi.Linea1EstudioCPSI;
import mx.ift.sns.modelo.cpsi.Linea2EstudioCPSI;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCodigosCPSI;
import mx.ift.sns.modelo.pst.Proveedor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de codigod CPS internacionales.
 * @author X50880SA
 */
@Named
public class CodigoCpsiDaoImpl extends BaseDAO<CodigoCPSI> implements ICodigoCpsiDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CodigoCpsiDaoImpl.class);

    @Override
    public EstatusCPSI getEstatusCPSIById(String id) throws Exception {
        String query = "SELECT est FROM EstatusCPSI est WHERE est.id = :id";

        TypedQuery<EstatusCPSI> consulta = getEntityManager().createQuery(query, EstatusCPSI.class);
        consulta.setParameter("id", id);

        EstatusCPSI estatusCPSI = consulta.getSingleResult();
        return estatusCPSI;
    }

    @Override
    public List<EstatusCPSI> findAllEstatusCPSI() {
        String query = "SELECT est FROM EstatusCPSI est ORDER BY est.id";

        TypedQuery<EstatusCPSI> consulta = getEntityManager().createQuery(query, EstatusCPSI.class);
        List<EstatusCPSI> listado = consulta.getResultList();
        return listado;
    }

    @Override
    public List<InfoCatCpsi> findAllInfoCatCPSI(FiltroBusquedaCodigosCPSI pFiltro) {
        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltro.getFiltrosCodigosCPSI();
        List<InfoCatCpsi> resultado = null;

        StringBuilder query = new StringBuilder();
        query.append("SELECT new mx.ift.sns.modelo.cpsi.InfoCatCpsi( ");
        query.append("cpsi.id, ");
        query.append("cpsi.estatus, ");
        query.append("cpsi.proveedor, ");
        query.append("cpsi.fechaFinCuarentena, ");
        query.append("entregado.referenciaUit, ");
        query.append("entregado.solicitudUit) ");

        query.append("FROM CodigoCPSI cpsi ");
        query.append("LEFT JOIN CpsiUitEntregado entregado ");
        query.append("on cpsi.id = entregado.idCpsi ");
        query.append("and entregado.estatus.codigo = '").append("E' ");

        // Insertamos genericamente los filtros en la consulta
        // query.append("WHERE cpsi.proveedor = :proveedorVar");
        // query.append(" AND cpsi.estatus.id = :estatusVar");
        for (int i = 0; i < filtros.size(); i++) {

            if (i == 0) {
                query.append(" WHERE ");
            } else {
                query.append(" AND ");
            }

            FiltroBusqueda f = filtros.get(i);
            String aliasTabla = f.getPrefijo();
            String campo = f.getCampo();
            String campoVar = f.getCampo() + "Var";
            query.append(aliasTabla).append(".").append(campo).append("=:").append(campoVar);

        }

        query.append(" ORDER BY cpsi.id ");

        TypedQuery<InfoCatCpsi> tQuery = getEntityManager().createQuery(query.toString(),
                InfoCatCpsi.class);

        // Asignamos genericamente los valores que nos vienen en los filtros
        // tQuery.setParameter("proveedorVar", proveedor);
        // tQuery.setParameter("estatusVar", estado);
        for (FiltroBusqueda filtro : filtros) {
            tQuery.setParameter(filtro.getCampo() + "Var", filtro.getValor());
        }

        resultado = tQuery.getResultList();
        return resultado;

    }

    @Override
    public List<CodigoCPSI> findAllCodigosCPSI(FiltroBusquedaCodigosCPSI pFiltro) {
        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltro.getFiltrosCodigosCPSI();

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<CodigoCPSI> cQuery = cb.createQuery(CodigoCPSI.class);
        Root<CodigoCPSI> cpsi = cQuery.from(CodigoCPSI.class);

        if (filtros != null && !filtros.isEmpty()) {
            Predicate wherePredicate = cb.conjunction();
            for (FiltroBusqueda filtro : filtros) {
                Predicate pred = null;
                pred = this.getPredicateFromFiltro(filtro, cpsi, cb);
                wherePredicate = cb.and(wherePredicate, pred);
            }
            cQuery.where(wherePredicate);
        }

        cQuery.orderBy(cb.asc(cpsi.get("id")));
        TypedQuery<CodigoCPSI> tQuery = getEntityManager().createQuery(cQuery.select(cpsi));
        List<CodigoCPSI> result = tQuery.getResultList();

        return result;
    }

    @Override
    public int findAllCodigosCPSICount(FiltroBusquedaCodigosCPSI pFiltro) {
        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltro.getFiltrosCodigosCPSI();

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<CodigoCPSI> cpsi = cQuery.from(CodigoCPSI.class);

        if (filtros != null && !filtros.isEmpty()) {
            Predicate wherePredicate = cb.conjunction();
            for (FiltroBusqueda filtro : filtros) {
                Predicate pred = null;
                pred = this.getPredicateFromFiltro(filtro, cpsi, cb);
                wherePredicate = cb.and(wherePredicate, pred);
            }
            cQuery.where(wherePredicate);
        }

        TypedQuery<Long> tQuery = getEntityManager().createQuery(cQuery.select(cb.countDistinct(cpsi)));
        int rowCount = tQuery.getSingleResult().intValue();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Códigos contados: " + rowCount);
        }

        return rowCount;
    }

    @Override
    public List<CodigoCPSI> findAllCodigosCPSIForAnalisis(Proveedor pProveedor) {

        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<CodigoCPSI> cQuery = cb.createQuery(CodigoCPSI.class);
        Root<CodigoCPSI> codigo = cQuery.from(CodigoCPSI.class);

        ParameterExpression<String> idEstatusAsignado = cb.parameter(String.class);
        ParameterExpression<Proveedor> asignatario = cb.parameter(Proveedor.class);

        cQuery.where(cb.or(
                cb.and(cb.equal(codigo.get("proveedor"), asignatario),
                        cb.equal(codigo.get("estatus").get("id"), idEstatusAsignado)),
                cb.notEqual(codigo.get("estatus").get("id"), idEstatusAsignado)));

        cQuery.select(codigo).distinct(true);
        cQuery.orderBy(cb.asc(codigo.get("id")));

        TypedQuery<CodigoCPSI> tQuery = this.getEntityManager().createQuery(cQuery);
        tQuery.setParameter(idEstatusAsignado, EstatusCPSI.ASIGNADO);
        tQuery.setParameter(asignatario, pProveedor);

        List<CodigoCPSI> result = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado  {} Códigos CPSI para el análisis del Proveedor {}",
                    result.size(), pProveedor.getNombreCorto());
        }

        return result;
    }

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
                return pCriteriaBuilder.lessThanOrEqualTo(comparableDate, (java.util.Date) pFiltro.getValor());
            } else {
                return pCriteriaBuilder.equal(comparableDate, (java.util.Date) pFiltro.getValor());
            }
        } else {
            return pCriteriaBuilder.equal(pFrom.get(pFiltro.getCampo()), pFiltro.getValor());
        }
    }

    @Override
    public Linea1EstudioCPSI estudioCPSILinea1(Proveedor proveedor) {
        Linea1EstudioCPSI listado = null;

        try {
            StringBuilder query = new StringBuilder();
            query.append("SELECT subquery.nombre_corto, ");
            query.append("COUNT(subquery.id_cpsi) total, ");
            query.append("SUM(subquery.utilizados) utilizados ");
            query.append(" FROM ");

            query.append("(SELECT cpsi.id_cpsi,");
            query.append("COUNT(DISTINCT equipo.cps) utilizados,");
            query.append("pst.nombre_corto");
            query.append(" FROM ");
            query.append("cat_cpsi cpsi ");
            query.append("LEFT JOIN cat_equipo_senal_cpsi equipo ON cpsi.id_cpsi = equipo.cps ");
            query.append("AND cpsi.id_pst = equipo.id_pst ");
            query.append("LEFT JOIN cat_pst pst ON cpsi.id_pst = pst.id_pst");
            query.append(" WHERE ");
            query.append("cpsi.id_pst = ? AND cpsi.id_estatus_cpsi = 'A' ");
            query.append("GROUP BY cpsi.id_cpsi, pst.nombre_corto) subquery ");

            query.append("GROUP BY nombre_corto");

            Query consulta = getEntityManager().createNativeQuery(query.toString());

            consulta.setParameter(1, proveedor.getId());

            Object[] result = (Object[]) consulta.getSingleResult();

            // Mapeo manual del objeto
            listado = new Linea1EstudioCPSI();
            listado.setNombreProveedor(result[0].toString());
            listado.setTotalAsignados((BigDecimal) result[1]);
            listado.setTotalActivos((BigDecimal) result[2]);
        } catch (NoResultException e) {
            LOGGER.debug("_FIN_");
            listado.setTotalAsignados(new BigDecimal(0));
            listado.setTotalActivos(new BigDecimal(0));
            listado.setNombreProveedor("");
        }
        return listado;
    }

    @Override
    public Linea2EstudioCPSI estudioCPSILinea2() {
        Linea2EstudioCPSI resultado = null;

        StringBuilder query = new StringBuilder();
        query.append("SELECT new mx.ift.sns.modelo.cpsi.Linea2EstudioCPSI");
        query.append("(COUNT(cpsi.id), ");
        query.append("SUM( CASE cpsi.estatus.id  WHEN 'L' THEN 1 ELSE 0 END), ");
        query.append("SUM( CASE cpsi.estatus.id  WHEN 'R' THEN 1 ELSE 0 END), ");
        query.append("SUM( CASE cpsi.estatus.id  WHEN 'A' THEN 1 ELSE 0 END), ");
        query.append("SUM( CASE cpsi.estatus.id  WHEN 'P' THEN 1 ELSE 0 END), ");
        query.append("SUM( CASE cpsi.estatus.id  WHEN 'Q' THEN 1 ELSE 0 END) ) ");
        query.append("FROM CodigoCPSI cpsi");

        TypedQuery<Linea2EstudioCPSI> tQuery = getEntityManager().createQuery(query.toString(),
                Linea2EstudioCPSI.class);

        resultado = tQuery.getSingleResult();
        return resultado;
    }

    @Override
    public Linea1EstudioCPSI getEstudioCpsiProveedor(Proveedor pProveedor) {

        String queryTotal = "SELECT COUNT(cpsi.id) FROM CodigoCPSI cpsi WHERE cpsi.proveedor.id = :idProveedor";
        String queryActivos = "SELECT COUNT(eq.id) FROM EquipoSenalCpsi eq WHERE eq.proveedor.id = :idProveedor";

        TypedQuery<Long> tQueryTotal = this.getEntityManager().createQuery(queryTotal, Long.class);
        tQueryTotal.setParameter("idProveedor", pProveedor.getId());
        Long countTotal = tQueryTotal.getSingleResult();

        TypedQuery<Long> tQueryActivos = this.getEntityManager().createQuery(queryActivos, Long.class);
        tQueryActivos.setParameter("idProveedor", pProveedor.getId());
        Long countActivos = tQueryActivos.getSingleResult();

        Linea1EstudioCPSI estudioCpsi = new Linea1EstudioCPSI(
                new BigDecimal(countTotal),
                new BigDecimal(countActivos),
                pProveedor.getNombre(),
                pProveedor.getId());

        return estudioCpsi;
    }

    @Override
    public List<CodigoCPSI> getCodigosAsignadosAProveedor(Proveedor pst) {
        List<CodigoCPSI> listado = null;
        String query = "SELECT cpsi FROM CodigoCPSI cpsi WHERE cpsi.proveedor = :proveedor"
                + " AND cpsi.estatus.id = :estatusCPSI";

        TypedQuery<CodigoCPSI> tQuery = getEntityManager().createQuery(query, CodigoCPSI.class);
        tQuery.setParameter("proveedor", pst);
        tQuery.setParameter("estatusCPSI", EstatusCPSI.ASIGNADO);

        listado = tQuery.getResultList();
        return listado;
    }

    @Override
    public CodigoCPSI getCodigoCpsi(BigDecimal pIdCodigo, Proveedor pProveedor) {

        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("SELECT cpsi FROM CodigoCPSI cpsi WHERE cpsi.id = :idCodigo ");
        if (pProveedor != null) {
            sbQuery.append("AND cpsi.proveedor.id = :idProveedor");
        }

        TypedQuery<CodigoCPSI> tQuery = getEntityManager().createQuery(sbQuery.toString(), CodigoCPSI.class);
        tQuery.setParameter("idCodigo", pIdCodigo);
        if (pProveedor != null) {
            tQuery.setParameter("idProveedor", pProveedor.getId());
        }

        CodigoCPSI cpsi = null;
        try {
            cpsi = tQuery.getSingleResult();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Código CPSI encontrado: {}", cpsi);
            }
        } catch (NoResultException nre) {
            if (LOGGER.isDebugEnabled()) {
                if (pProveedor != null) {
                    LOGGER.debug("Código CPSI no encontrado. Identificador {}, Pst {}", pIdCodigo,
                            pProveedor.getNombre());
                } else {
                    LOGGER.debug("Código CPSI no encontrado. Identificador {}, Pst null", pIdCodigo);
                }
            }
        }

        return cpsi;
    }

}
