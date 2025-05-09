package mx.ift.sns.dao.pnn;

import java.util.Date;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.pnn.Plan;
import mx.ift.sns.modelo.pnn.TipoPlan;

/**
 * Dao Plan.
 */
public interface IPlanDAO extends IBaseDAO<Plan> {

    /**
     * Borra los planes mas viejos que la fecha dada.
     * @param fecha fecha
     */
    void deleteOlderThan(Date fecha);

    /**
     * Devuelve los planes mas viejos que la fecha dada.
     * @param fecha fecha
     * @param tipoPlan tipo de plan
     * @return lista
     */
    List<Plan> findOlderThan(Date fecha, TipoPlan tipoPlan);
}
