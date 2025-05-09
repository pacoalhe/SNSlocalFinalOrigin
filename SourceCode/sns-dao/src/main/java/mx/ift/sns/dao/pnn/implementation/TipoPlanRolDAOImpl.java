package mx.ift.sns.dao.pnn.implementation;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.pnn.ITipoPlanRolDAO;
import mx.ift.sns.modelo.pnn.Plan;
import mx.ift.sns.modelo.pnn.TipoPlanRol;
/**
 * Implementación de los métodos para las relaciones plan/rol.
 * @author X51461MO
 *
 */
public class TipoPlanRolDAOImpl extends BaseDAO<TipoPlanRol> implements ITipoPlanRolDAO {


    @Override
    public List<String> findAllTipoPlanByRol(String idRol) {
        String query = "SELECT p.id.idTipoPlan FROM TipoPlanRol p where p.id.idRol =:idRol";
        TypedQuery<String> tQuery = getEntityManager().createQuery(query, String.class);
        tQuery.setParameter("idRol", idRol);
        List<String> planRol = null;
        try {
        planRol = tQuery.getResultList();
        } catch (NoResultException nr) {
            return null;
        }
        return planRol;
    }

    @Override
    public List<Plan> findAllPlanByRol(String idRol) {
        String query = "SELECT p FROM Plan p, TipoPlanRol t "
                + "where t.id.idRol =:idRol "
                + "and t.id.idTipoPlan = p.tipoPlan.id";
        TypedQuery<Plan> tQuery = getEntityManager().createQuery(query, Plan.class);
        tQuery.setParameter("idRol", idRol);
        List<Plan> plan = null;
        try {
            plan = tQuery.getResultList();
        } catch (NoResultException nre) {
            return null;
        }
        return plan;
    }

    @Override
    public Plan getPlanByTipo(String idTipo) {
        String query = "SELECT p FROM Plan p "
                + "WHERE p.tipoPlan.id = :idTipo "
                + "AND p.fechaGeneracion = ( SELECT MAX(a.fechaGeneracion) "
                + "FROM Plan a "
                + "WHERE a.tipoPlan.id = :idTipo )";
        TypedQuery<Plan> tQuery = getEntityManager().createQuery(query, Plan.class);
        tQuery.setParameter("idTipo", idTipo);
        Plan plan = null;
        try {
        plan = tQuery.getSingleResult();
        } catch (NoResultException nre) {

            return null;
        }
        return plan;

    }
    @Override
    public Plan getPlanByTipoAndClaveServicio(String idTipo, BigDecimal claveServicio) {
        String query = "SELECT p FROM Plan p "
                + "WHERE p.tipoPlan.id = :idTipo "
                + "AND p.claveServicio.codigo = :claveServicio "
                + "AND p.fechaGeneracion = ( SELECT MAX(a.fechaGeneracion) "
                + "FROM Plan a "
                + "WHERE a.tipoPlan.id = :idTipo "
                + "AND a.claveServicio.codigo = :claveServicio)";
        TypedQuery<Plan> tQuery = getEntityManager().createQuery(query, Plan.class);
        tQuery.setParameter("idTipo", idTipo);
        tQuery.setParameter("claveServicio", claveServicio);
        Plan plan = null;
        try {
        plan = tQuery.getSingleResult();
        } catch (NoResultException nre) {

            return null;
        }
        return plan;
    }

}
