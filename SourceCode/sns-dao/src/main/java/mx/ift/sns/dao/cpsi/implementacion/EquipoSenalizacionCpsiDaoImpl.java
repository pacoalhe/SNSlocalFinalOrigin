package mx.ift.sns.dao.cpsi.implementacion;

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
import mx.ift.sns.dao.cpsi.IEquipoSenalizacionCpsiDao;
import mx.ift.sns.modelo.cpsi.CPSIUtils;
import mx.ift.sns.modelo.cpsi.EquipoSenalCpsi;
import mx.ift.sns.modelo.cpsi.EquipoSenalCpsiWarn;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaEquipoSenal;
import mx.ift.sns.modelo.pst.Proveedor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de equipos de señalización.
 */
@Named
public class EquipoSenalizacionCpsiDaoImpl extends BaseDAO<EquipoSenalCpsi> implements IEquipoSenalizacionCpsiDao {
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EquipoSenalizacionCpsiDaoImpl.class);

    @Override
    public List<EquipoSenalCpsi> findAllEquiposSenal(FiltroBusquedaEquipoSenal pFiltros)
            throws Exception {

        String where = "";
        String query = "SELECT distinct esi FROM EquipoSenalCpsi esi ";

        if (!pFiltros.getFiltrosEquipoSenalCpsi().isEmpty()) {
            where = generaPredicado(pFiltros);
            query += where;
        }

        query += " ORDER BY esi.nombre";

        TypedQuery<EquipoSenalCpsi> tQuery = getEntityManager().createQuery(query, EquipoSenalCpsi.class);
        if (!pFiltros.getFiltrosEquipoSenalCpsi().isEmpty()) {
            tQuery = estableceParametros(tQuery, pFiltros);
        }
        return (List<EquipoSenalCpsi>) tQuery.getResultList();
    }

    @Override
    public boolean existeEquipo(EquipoSenalCpsi equipoSenal) {
        String query = "SELECT count(1) FROM EquipoSenalCpsi esi WHERE "
                + "esi.nombre = :nombre AND esi.longitud = :longitud AND esi.latitud = :latitud "
                + "AND esi.cps = :cps AND esi.proveedor = :proveedor";

        if (equipoSenal.getId() != null) {
            query += " AND esi.id <> :id";
        }
        TypedQuery<Long> tQuery = getEntityManager().createQuery(query, Long.class);
        if (equipoSenal.getId() != null) {
            tQuery.setParameter("id", equipoSenal.getId());
        }
        tQuery.setParameter("proveedor", equipoSenal.getProveedor());
        tQuery.setParameter("nombre", equipoSenal.getNombre());
        tQuery.setParameter("longitud", equipoSenal.getLongitud());
        tQuery.setParameter("latitud", equipoSenal.getLatitud());
        tQuery.setParameter("cps", equipoSenal.getCps());

        Long resultado = (Long) tQuery.getSingleResult();
        if (resultado == null || resultado.longValue() == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public List<EquipoSenalCpsi> obtenerEquiposAActualizar(EquipoSenalCpsi equipoTemp) {

        String query = "SELECT esi FROM EquipoSenalCpsi esi WHERE "
                + "esi.nombre = :nombre AND esi.longitud = :longitud AND esi.latitud = :latitud "
                + "AND esi.proveedor = :proveedor";

        if (equipoTemp.getId() != null) {
            query += " AND esi.id <> :id";
        }
        TypedQuery<EquipoSenalCpsi> tQuery = getEntityManager().createQuery(query, EquipoSenalCpsi.class);
        if (equipoTemp.getId() != null) {
            tQuery.setParameter("id", equipoTemp.getId());
        }
        tQuery.setParameter("proveedor", equipoTemp.getProveedor());
        tQuery.setParameter("nombre", equipoTemp.getNombre());
        tQuery.setParameter("longitud", equipoTemp.getLongitud());
        tQuery.setParameter("latitud", equipoTemp.getLatitud());

        return (List<EquipoSenalCpsi>) tQuery.getResultList();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<EquipoSenalCpsi> findAllEquiposSenalExp(FiltroBusquedaEquipoSenal pFiltros) {
        String where = "";
        String query = "SELECT distinct esi FROM EquipoSenalCpsi esi left join fetch "
                + "esi.warnings ";

        if (!pFiltros.getFiltrosEquipoSenalCpsi().isEmpty()) {
            where = generaPredicado(pFiltros);
            query += where;
        }

        TypedQuery<EquipoSenalCpsi> tQuery = getEntityManager().createQuery(query, EquipoSenalCpsi.class);
        if (!pFiltros.getFiltrosEquipoSenalCpsi().isEmpty()) {
            tQuery = estableceParametros(tQuery, pFiltros);
        }
        return (List<EquipoSenalCpsi>) tQuery.getResultList();
    }

    /**
     * Método que genera el predicado de la consulta.
     * @param pFiltros filtros de la consulta
     * @return String predicado
     */
    private String generaPredicado(FiltroBusquedaEquipoSenal pFiltros) {
        String where = "";
        String condicion = "";

        for (FiltroBusqueda filtro : pFiltros.getFiltrosEquipoSenalCpsi()) {
            if (filtro.getCampo().equals(CPSIUtils.FILTRO_CODIGO_BINARIO)
                    || filtro.getCampo().equals(CPSIUtils.FILTRO_FORMATO_DECIMAL)) {
                condicion = filtro.getPrefijo() + "." + CPSIUtils.FILTRO_CODIGO_DECIMAL + " BETWEEN :"
                        + CPSIUtils.FILTRO_CODIGO_MINIMO + " AND :" + CPSIUtils.FILTRO_CODIGO_MAXIMO;
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
    private TypedQuery<EquipoSenalCpsi> estableceParametros(TypedQuery<EquipoSenalCpsi> tQuery,
            FiltroBusquedaEquipoSenal pFiltros) {
        for (FiltroBusqueda filtro : pFiltros.getFiltrosEquipoSenalCpsi()) {
            if (filtro.getCampo().equals(CPSIUtils.FILTRO_CODIGO_BINARIO)
                    || filtro.getCampo().equals(CPSIUtils.FILTRO_FORMATO_DECIMAL)) {
                tQuery.setParameter(CPSIUtils.FILTRO_CODIGO_MINIMO, filtro.getValor());
                tQuery.setParameter(CPSIUtils.FILTRO_CODIGO_MAXIMO, filtro.getValorSecundario());
            } else {
                tQuery.setParameter(filtro.getCampo(), filtro.getValor());
            }
        }

        return tQuery;
    }

    @Override
    public List<EquipoSenalCpsi> getEquiposByProveedor(Proveedor pst) {

        String query = "SELECT esi FROM EquipoSenalCpsi esi WHERE "
                + "esi.proveedor = :proveedor";

        TypedQuery<EquipoSenalCpsi> tQuery = getEntityManager().createQuery(query, EquipoSenalCpsi.class);
        tQuery.setParameter("proveedor", pst);

        return (List<EquipoSenalCpsi>) tQuery.getResultList();
    }

    @SuppressWarnings("unused")
    @Override
    public EquipoSenalCpsi getEquipoSenalEagerLoad(EquipoSenalCpsi equipo) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<EquipoSenalCpsi> cQuery = criteriaBuilder.createQuery(EquipoSenalCpsi.class);

        Root<EquipoSenalCpsi> sl = cQuery.from(EquipoSenalCpsi.class);
        Fetch<EquipoSenalCpsi, EquipoSenalCpsiWarn> join = sl.fetch("warnings", JoinType.LEFT);

        // Where
        ParameterExpression<BigDecimal> idEquipo = criteriaBuilder.parameter(BigDecimal.class);
        cQuery.where(criteriaBuilder.equal(sl.get("id"), idEquipo));

        TypedQuery<EquipoSenalCpsi> typedQuery = getEntityManager().createQuery(cQuery.select(sl).distinct(true));
        typedQuery.setParameter(idEquipo, equipo.getId());

        // Distinct = true
        equipo = typedQuery.getSingleResult();
        return equipo;
    }

    @Override
    public void eliminarEquiposByPst(Proveedor pst) {
        String query = "SELECT esi FROM EquipoSenalCpsi esi WHERE esi.proveedor = :proveedor";

        TypedQuery<EquipoSenalCpsi> typedQuery = getEntityManager().createQuery(query, EquipoSenalCpsi.class);
        typedQuery.setParameter("proveedor", pst);

        List<EquipoSenalCpsi> equipos = typedQuery.getResultList();

        if (!equipos.isEmpty()) {
            for (EquipoSenalCpsi equipo : equipos) {
                getEntityManager().remove(equipo);
            }
        }

        int registrosEliminados = equipos.size();
        LOGGER.info("Equipos eliminados: " + registrosEliminados);

    }

}
