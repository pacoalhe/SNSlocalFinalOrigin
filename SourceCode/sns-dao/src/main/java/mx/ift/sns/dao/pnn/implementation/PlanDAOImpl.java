package mx.ift.sns.dao.pnn.implementation;

import java.util.Date;
import java.util.List;

import javax.inject.Named;
import javax.persistence.Query;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.pnn.IPlanDAO;
import mx.ift.sns.modelo.pnn.Plan;
import mx.ift.sns.modelo.pnn.TipoPlan;

/**
 * Implementación de los métodos para base de datos de planes.
 */
@Named
public class PlanDAOImpl extends BaseDAO<Plan> implements IPlanDAO {

    @Override
    public void deleteOlderThan(Date fecha) {
        final String strQuery = "DELETE FROM Plan p where p.fechaGeneracion < :fecha";

        Query query = getEntityManager().createQuery(strQuery);
        query.setParameter("fecha", fecha);

        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Plan> findOlderThan(Date fecha, TipoPlan tipoPlan) {
        final String strQuery = "SELECT p FROM Plan p where p.tipoPlan = :tipo and p.fechaGeneracion < :fecha";

        Query query = getEntityManager().createQuery(strQuery);
        query.setParameter("fecha", fecha);
        query.setParameter("tipo", tipoPlan);

        return query.getResultList();
    }
}
