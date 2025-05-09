package mx.ift.sns.dao.pnn;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.pnn.PlanMaestroDetalle;

/**
 * Dao Tipo Plan.
 */
public interface IPlanMaestroDAO extends IBaseDAO<PlanMaestroDetalle> {

    /**
     * Método que devuelve el detalle del plan.
     * 
     * @param numeroInicial Long
     * @param numeroFinal   Long
     * @return planMaestroDetalle PlanMaestroDetalle
     */
    PlanMaestroDetalle getDetalleNumero(Long numeroInicial, Long numeroFinal);

    /**
     * Método que actualiza un registro del plan maestro.
     * 
     * @param numeroInicial       Long
     * @param numeroFinal         Long
     * @param registroActualizado PlanMaestroDetalle
     * @return planMaestroDetalle PlanMaestroDetalle
     */
    PlanMaestroDetalle actualizaNumero(Long numeroInicial, Long numeroFinal, PlanMaestroDetalle registroActualizado);

    /**
     * Método que elimina un registro del plan maestro.
     * 
     * @param ido           Integer
     * @param numeroInicial Long
     * @param numeroFinal   Long
     * @return eliminado Boolean
     */
    Boolean eliminaNumero(Long numeroInicial, Long numeroFinal);
}