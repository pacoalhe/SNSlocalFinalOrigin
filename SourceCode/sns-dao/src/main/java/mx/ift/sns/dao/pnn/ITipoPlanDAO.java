package mx.ift.sns.dao.pnn;

import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.pnn.TipoPlan;

/**
 * Dao Tipo Plan.
 */
public interface ITipoPlanDAO extends IBaseDAO<TipoPlan> {

    /**
     * Método que devuelve los tipos de red.
     * @return List
     */
    List<TipoPlan> findAll();

    /**
     * Método que devuelve el tipo plan.
     * @param id String
     * @return id
     */
    TipoPlan getTipoPlanById(String id);
}
