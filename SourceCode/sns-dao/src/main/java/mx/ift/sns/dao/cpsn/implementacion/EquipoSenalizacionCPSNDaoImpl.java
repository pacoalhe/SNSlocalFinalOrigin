package mx.ift.sns.dao.cpsn.implementacion;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Named;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.cpsn.IEquipoSenalizacionCPSNDao;
import mx.ift.sns.modelo.cpsn.CPSNUtils;
import mx.ift.sns.modelo.cpsn.EquipoSenalCPSN;
import mx.ift.sns.modelo.cpsn.EquipoSenalCPSNWarn;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaEquipoSenal;
import mx.ift.sns.modelo.pst.Proveedor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de equipos de señalización.
 */
@Named
public class EquipoSenalizacionCPSNDaoImpl extends BaseDAO<EquipoSenalCPSN> implements IEquipoSenalizacionCPSNDao {
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EquipoSenalizacionCPSNDaoImpl.class);

    @Override
    public List<EquipoSenalCPSN> findAllEquiposSenal(FiltroBusquedaEquipoSenal pFiltros)
            throws Exception {

        String where = "";
        String query = "SELECT distinct esn FROM EquipoSenalCPSN esn ";

        if (!pFiltros.getFiltrosEquipoSenal().isEmpty()) {
            where = generaPredicado(pFiltros);
            query += where;
        }

        query += " ORDER BY esn.nombre";

        TypedQuery<EquipoSenalCPSN> tQuery = getEntityManager().createQuery(query, EquipoSenalCPSN.class);
        if (!pFiltros.getFiltrosEquipoSenal().isEmpty()) {
            tQuery = estableceParametros(tQuery, pFiltros);
        }
        return (List<EquipoSenalCPSN>) tQuery.getResultList();
    }

    @Override
    public boolean existeEquipoCPSN(EquipoSenalCPSN equipoSenalCPSN) {
        String query = "SELECT count(1) FROM EquipoSenalCPSN esn WHERE "
                + "esn.nombre = :nombre AND esn.longitud = :longitud AND esn.latitud = :latitud "
                + "AND esn.cps = :cps AND esn.proveedor = :proveedor";

        if (equipoSenalCPSN.getId() != null) {
            query += " AND esn.id <> :id";
        }
        TypedQuery<Long> tQuery = getEntityManager().createQuery(query, Long.class);
        if (equipoSenalCPSN.getId() != null) {
            tQuery.setParameter("id", equipoSenalCPSN.getId());
        }
        tQuery.setParameter("proveedor", equipoSenalCPSN.getProveedor());
        tQuery.setParameter("nombre", equipoSenalCPSN.getNombre());
        tQuery.setParameter("longitud", equipoSenalCPSN.getLongitud());
        tQuery.setParameter("latitud", equipoSenalCPSN.getLatitud());
        tQuery.setParameter("cps", equipoSenalCPSN.getCps());

        Long resultado = (Long) tQuery.getSingleResult();
        if (resultado == null || resultado.longValue() == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void eliminarEquipo(EquipoSenalCPSN equipo) {
        equipo = getEntityManager().find(EquipoSenalCPSN.class, equipo.getId());
        getEntityManager().remove(equipo);
    }

    @Override
    public List<EquipoSenalCPSN> obtenerEquiposAActualizar(EquipoSenalCPSN equipoTemp) {

        String query = "SELECT esn FROM EquipoSenalCPSN esn WHERE "
                + "esn.nombre = :nombre AND esn.longitud = :longitud AND esn.latitud = :latitud "
                + "AND esn.proveedor = :proveedor";

        if (equipoTemp.getId() != null) {
            query += " AND esn.id <> :id";
        }
        TypedQuery<EquipoSenalCPSN> tQuery = getEntityManager().createQuery(query, EquipoSenalCPSN.class);
        if (equipoTemp.getId() != null) {
            tQuery.setParameter("id", equipoTemp.getId());
        }
        tQuery.setParameter("proveedor", equipoTemp.getProveedor());
        tQuery.setParameter("nombre", equipoTemp.getNombre());
        tQuery.setParameter("longitud", equipoTemp.getLongitud());
        tQuery.setParameter("latitud", equipoTemp.getLatitud());

        return (List<EquipoSenalCPSN>) tQuery.getResultList();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<EquipoSenalCPSN> findAllEquiposSenalExp(FiltroBusquedaEquipoSenal pFiltros) {
        String where = "";
        String query = "SELECT distinct esn FROM EquipoSenalCPSN esn left join fetch "
                + "esn.warnings ";

        if (!pFiltros.getFiltrosEquipoSenal().isEmpty()) {
            where = generaPredicado(pFiltros);
            query += where;
        }

        TypedQuery<EquipoSenalCPSN> tQuery = getEntityManager().createQuery(query, EquipoSenalCPSN.class);
        if (!pFiltros.getFiltrosEquipoSenal().isEmpty()) {
            tQuery = estableceParametros(tQuery, pFiltros);
        }
        return (List<EquipoSenalCPSN>) tQuery.getResultList();
    }

    /**
     * Método que genera el predicado de la consulta.
     * @param pFiltros filtros de la consulta
     * @return String predicado
     */
    private String generaPredicado(FiltroBusquedaEquipoSenal pFiltros) {
        String where = "";
        String condicion = "";

        for (FiltroBusqueda filtro : pFiltros.getFiltrosEquipoSenal()) {
            if (filtro.getCampo().equals(CPSNUtils.FILTRO_CODIGO_BINARIO)) {
                condicion = filtro.getPrefijo() + "." + CPSNUtils.FILTRO_CODIGO_DECIMAL + " BETWEEN :"
                        + CPSNUtils.FILTRO_CODIGO_MINIMO + " AND :" + CPSNUtils.FILTRO_CODIGO_MAXIMO;
            } else {
                condicion = filtro.getPrefijo() + "." + filtro.getCampo() + " = :" + filtro.getCampo();
            }
            where += (where.isEmpty()) ? "WHERE " + condicion : " AND " + condicion;

        }

        return where;
    }

    /**
     * Método que establece los valores del filtro de consulta.
     * @param pFiltros filtros de la consulta
     * @param tQuery consulta
     * @return String predicado
     */
    private TypedQuery<EquipoSenalCPSN> estableceParametros(TypedQuery<EquipoSenalCPSN> tQuery,
            FiltroBusquedaEquipoSenal pFiltros) {
        for (FiltroBusqueda filtro : pFiltros.getFiltrosEquipoSenal()) {
            if (filtro.getCampo().equals(CPSNUtils.FILTRO_CODIGO_BINARIO)) {
                tQuery.setParameter(CPSNUtils.FILTRO_CODIGO_MINIMO, filtro.getValor());
                tQuery.setParameter(CPSNUtils.FILTRO_CODIGO_MAXIMO, filtro.getValorSecundario());
            } else {
                tQuery.setParameter(filtro.getCampo(), filtro.getValor());
            }
        }

        return tQuery;
    }

    @Override
    public List<EquipoSenalCPSN> getEquiposCPSNByProveedor(Proveedor pst) {

        String query = "SELECT esn FROM EquipoSenalCPSN esn WHERE "
                + "esn.proveedor = :proveedor";

        TypedQuery<EquipoSenalCPSN> tQuery = getEntityManager().createQuery(query, EquipoSenalCPSN.class);
        tQuery.setParameter("proveedor", pst);

        return (List<EquipoSenalCPSN>) tQuery.getResultList();
    }

    @Override
    public EquipoSenalCPSN getEquipoSenalCPSNEagerLoad(EquipoSenalCPSN equipo) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<EquipoSenalCPSN> cQuery = criteriaBuilder.createQuery(EquipoSenalCPSN.class);

        Root<EquipoSenalCPSN> sl = cQuery.from(EquipoSenalCPSN.class);
        @SuppressWarnings("unused")
        Fetch<EquipoSenalCPSN, EquipoSenalCPSNWarn> join = sl.fetch("warnings", JoinType.LEFT);

        // Where
        ParameterExpression<BigDecimal> idEquipo = criteriaBuilder.parameter(BigDecimal.class);
        cQuery.where(criteriaBuilder.equal(sl.get("id"), idEquipo));

        TypedQuery<EquipoSenalCPSN> typedQuery = getEntityManager().createQuery(cQuery.select(sl).distinct(true));
        typedQuery.setParameter(idEquipo, equipo.getId());

        // Distinct = true
        equipo = typedQuery.getSingleResult();
        return equipo;
    }

    @Override
    public void eliminarEquiposByPst(Proveedor pst) {
        String query = "SELECT esn FROM EquipoSenalCPSN esn WHERE esn.proveedor = :proveedor";

        TypedQuery<EquipoSenalCPSN> typedQuery = getEntityManager().createQuery(query, EquipoSenalCPSN.class);
        typedQuery.setParameter("proveedor", pst);

        List<EquipoSenalCPSN> equipos = typedQuery.getResultList();

        if (!equipos.isEmpty()) {
            for (EquipoSenalCPSN equipo : equipos) {
                getEntityManager().remove(equipo);
            }
        }

        int registrosEliminados = equipos.size();
        LOGGER.info("Equipos eliminados: " + registrosEliminados);

    }
}
