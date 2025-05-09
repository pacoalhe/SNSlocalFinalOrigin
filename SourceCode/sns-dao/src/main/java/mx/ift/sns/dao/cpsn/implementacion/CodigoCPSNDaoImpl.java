package mx.ift.sns.dao.cpsn.implementacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.cpsn.ICodigoCPSNDao;
import mx.ift.sns.modelo.cpsn.CodigoCPSN;
import mx.ift.sns.modelo.cpsn.EstatusCPSN;
import mx.ift.sns.modelo.cpsn.TipoBloqueCPSN;
import mx.ift.sns.modelo.cpsn.VEstudioCPSN;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCodigosCPSN;
import mx.ift.sns.modelo.pst.Proveedor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de codigod CPS Nacionales.
 */
@Named
public class CodigoCPSNDaoImpl extends BaseDAO<CodigoCPSN> implements ICodigoCPSNDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CodigoCPSNDaoImpl.class);

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
    public TipoBloqueCPSN getTipoBloqueCPSNById(String id) throws Exception {
        String query = "SELECT tb FROM TipoBloqueCPSN tb WHERE tb.id = :id";

        TypedQuery<TipoBloqueCPSN> consulta = getEntityManager().createQuery(query, TipoBloqueCPSN.class);
        consulta.setParameter("id", id);

        TipoBloqueCPSN tipoBloqueCPSN = consulta.getSingleResult();
        return tipoBloqueCPSN;
    }

    @Override
    public CodigoCPSN getCodigoCPSNById(String id) {
        String query = "SELECT c FROM CodigoCPSN c WHERE c.id = :id";

        TypedQuery<CodigoCPSN> consulta = getEntityManager().createQuery(query, CodigoCPSN.class);
        consulta.setParameter("id", BigDecimal.valueOf(Long.parseLong(id)));

        CodigoCPSN codigo = consulta.getSingleResult();
        return codigo;
    }

    @Override
    public EstatusCPSN getEstatusCPSNById(String id) throws Exception {
        String query = "SELECT est FROM EstatusCPSN est WHERE est.id = :id";

        TypedQuery<EstatusCPSN> consulta = getEntityManager().createQuery(query, EstatusCPSN.class);
        consulta.setParameter("id", id);

        EstatusCPSN estatusCPSN = consulta.getSingleResult();
        return estatusCPSN;
    }

    @Override
    public List<TipoBloqueCPSN> findAllTiposBloqueCPSN() {
        String query = "SELECT tb FROM TipoBloqueCPSN tb ORDER BY tb.id";

        TypedQuery<TipoBloqueCPSN> consulta = getEntityManager().createQuery(query, TipoBloqueCPSN.class);
        List<TipoBloqueCPSN> listado = consulta.getResultList();
        return listado;
    }

    @Override
    public List<EstatusCPSN> findAllEstatusCPSN() {
        String query = "SELECT est FROM EstatusCPSN est ORDER BY est.id";

        TypedQuery<EstatusCPSN> consulta = getEntityManager().createQuery(query, EstatusCPSN.class);
        List<EstatusCPSN> listado = consulta.getResultList();
        return listado;
    }

    @Override
    public List<CodigoCPSN> findCodigosCPSN(FiltroBusquedaCodigosCPSN pFiltro) {
        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltro.getFiltrosCodigosCPSN();

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<CodigoCPSN> cQuery = cb.createQuery(CodigoCPSN.class);
        Root<CodigoCPSN> cpsn = cQuery.from(CodigoCPSN.class);

        if (filtros != null && !filtros.isEmpty()) {
            Predicate wherePredicate = cb.conjunction();
            for (FiltroBusqueda filtro : filtros) {
                Predicate pred = null;
                pred = this.getPredicateFromFiltro(filtro, cpsn, cb);
                wherePredicate = cb.and(wherePredicate, pred);
            }

            cQuery.where(wherePredicate);
        }

        cQuery.orderBy(cb.asc(cpsn.get("id")));
        TypedQuery<CodigoCPSN> tQuery = getEntityManager().createQuery(cQuery.select(cpsn));
        List<CodigoCPSN> result = tQuery.getResultList();

        return result;
    }

    @Override
    public boolean permitirAgrupar(int numMin, int numMax, Proveedor proveedor) {
        String query = "SELECT count(cpsn.id) FROM CodigoCPSN cpsn WHERE cpsn.id BETWEEN :numMin AND :numMax"
                + " AND ((cpsn.estatusCPSN.id != :libre AND cpsn.estatusCPSN.id != :reservado) ";

        if (proveedor == null) {
            query += " OR cpsn.proveedor IS NOT NULL)";
        } else {
            query += " OR cpsn.proveedor.id <> :idProveedor)";
        }

        TypedQuery<Long> tQuery = getEntityManager().createQuery(query, Long.class);
        tQuery.setParameter("numMin", numMin);
        tQuery.setParameter("numMax", numMax);
        tQuery.setParameter("libre", EstatusCPSN.LIBRE);
        tQuery.setParameter("reservado", EstatusCPSN.RESERVADO);
        if (proveedor != null) {
            tQuery.setParameter("idProveedor", proveedor.getId());
        }

        long result = tQuery.getSingleResult();
        return result == 0;
    }

    @Override
    public List<CodigoCPSN> getCodigosAAgrupar(int numMin, int numMax) {
        List<CodigoCPSN> listado = null;
        String query = "SELECT cpsn FROM CodigoCPSN cpsn WHERE cpsn.id BETWEEN :numMin AND :numMax";

        TypedQuery<CodigoCPSN> tQuery = getEntityManager().createQuery(query, CodigoCPSN.class);
        tQuery.setParameter("numMin", numMin);
        tQuery.setParameter("numMax", numMax);

        listado = tQuery.getResultList();
        return listado;
    }

    @Override
    public List<VEstudioCPSN> estudioCPSN() {
        List<VEstudioCPSN> listado = null;
        String query = "SELECT ves FROM VEstudioCPSN ves ORDER BY ves.id DESC";

        TypedQuery<VEstudioCPSN> tQuery = getEntityManager().createQuery(query, VEstudioCPSN.class);

        listado = tQuery.getResultList();
        return listado;
    }

    @Override
    public CodigoCPSN getCpsnByIdBloqueAsignado(Integer cps, TipoBloqueCPSN tipoBloque) {
        String query = "SELECT cpsn FROM CodigoCPSN cpsn WHERE cpsn.id = :idCpsn "
                + "AND cpsn.tipoBloqueCPSN = :tipoBloque AND cpsn.estatusCPSN.id = :estatusCPSN";

        TypedQuery<CodigoCPSN> tQuery = getEntityManager().createQuery(query, CodigoCPSN.class);
        tQuery.setParameter("idCpsn", cps);
        tQuery.setParameter("tipoBloque", tipoBloque);
        tQuery.setParameter("estatusCPSN", EstatusCPSN.ASIGNADO);

        List<CodigoCPSN> codigos = tQuery.getResultList();
        if (!codigos.isEmpty()) {
            return codigos.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<CodigoCPSN> getCodigosAsignadosAProveedor(Proveedor pst) {
        List<CodigoCPSN> listado = null;
        String query = "SELECT cpsn FROM CodigoCPSN cpsn WHERE cpsn.proveedor = :proveedor"
                + " AND cpsn.estatusCPSN.id = :estatusCPSN";

        TypedQuery<CodigoCPSN> tQuery = getEntityManager().createQuery(query, CodigoCPSN.class);
        tQuery.setParameter("proveedor", pst);
        tQuery.setParameter("estatusCPSN", EstatusCPSN.ASIGNADO);

        listado = tQuery.getResultList();
        return listado;
    }

    @Override
    public CodigoCPSN getCodigoCpsn(String pIdTipoBloque, BigDecimal pIdCodigo, Proveedor pProveedor) {

        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("SELECT cpsn FROM CodigoCPSN cpsn WHERE cpsn.tipoBloqueCPSN.id = :idTipoBloque ");
        sbQuery.append("AND cpsn.id = :idCodigo ");
        if (pProveedor != null) {
            sbQuery.append("AND cpsn.proveedor.id = :idProveedor");
        }

        TypedQuery<CodigoCPSN> tQuery = getEntityManager().createQuery(sbQuery.toString(), CodigoCPSN.class);
        tQuery.setParameter("idTipoBloque", pIdTipoBloque);
        tQuery.setParameter("idCodigo", pIdCodigo);
        if (pProveedor != null) {
            tQuery.setParameter("idProveedor", pProveedor.getId());
        }

        CodigoCPSN cpsn = null;
        try {
            cpsn = tQuery.getSingleResult();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Código CPSN encontrado: {}", cpsn);
            }
        } catch (NoResultException nre) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Código CPSN no encontrado. Identificador {}, Bloque {}, Pst {}",
                        pIdCodigo, pIdTipoBloque, pProveedor.getNombre());
            }
        }

        return cpsn;
    }

    @Override
    public List<CodigoCPSN> findAllCodigosCPSN(Proveedor pProveedor, TipoBloqueCPSN tipoBloque) {

        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<CodigoCPSN> cQuery = cb.createQuery(CodigoCPSN.class);
        Root<CodigoCPSN> codigo = cQuery.from(CodigoCPSN.class);

        ParameterExpression<String> idEstatusAsignado = cb.parameter(String.class);
        ParameterExpression<Proveedor> asignatario = cb.parameter(Proveedor.class);

        cQuery.where(cb.and(cb.equal(codigo.get("tipoBloqueCPSN"), tipoBloque), cb.or(
                cb.and(cb.equal(codigo.get("proveedor"), asignatario),
                        cb.equal(codigo.get("estatusCPSN").get("id"), idEstatusAsignado)),
                cb.notEqual(codigo.get("estatusCPSN").get("id"), idEstatusAsignado))));

        cQuery.select(codigo).distinct(true);
        cQuery.orderBy(cb.desc(codigo.get("id")));

        TypedQuery<CodigoCPSN> tQuery = this.getEntityManager().createQuery(cQuery);
        tQuery.setParameter(idEstatusAsignado, EstatusCPSN.ASIGNADO);
        tQuery.setParameter(asignatario, pProveedor);

        List<CodigoCPSN> result = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado  {} Códigos CPSN para el análisis del Proveedor {}",
                    result.size(), pProveedor.getNombreCorto());
        }

        return result;
    }
}
