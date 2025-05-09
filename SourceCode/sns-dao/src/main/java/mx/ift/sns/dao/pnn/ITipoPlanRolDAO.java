package mx.ift.sns.dao.pnn;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.pnn.Plan;
import mx.ift.sns.modelo.pnn.TipoPlanRol;

/**
 * Interfaz de definición de los métodos para base de datos para planes.
 * @author X51461MO
 */
public interface ITipoPlanRolDAO extends IBaseDAO<TipoPlanRol> {
    /**
     * Deveulve la lista de tipos de planes para un rol.
     * @param idRol String
     * @return List<String>
     */
    List<String> findAllTipoPlanByRol(String idRol);

    /**
     * Devuelve los planes para un rol.
     * @param idRol String
     * @return List<Plan>
     */
    List<Plan> findAllPlanByRol(String idRol);

    /**
     * Obtiene el plan por tipo.
     * @param idTipo String
     * @return PLan plan
     */
    Plan getPlanByTipo(String idTipo);

    /**
     * Obtiene el plan por tipo y clave.
     * @param idTipo String
     * @param claveServicio BigDecimal
     * @return Plan
     */
    Plan getPlanByTipoAndClaveServicio(String idTipo, BigDecimal claveServicio);

}
