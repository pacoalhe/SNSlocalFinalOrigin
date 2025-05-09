package mx.ift.sns.negocio.pnn;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import mx.ift.sns.modelo.pnn.Plan;
import mx.ift.sns.modelo.pnn.TipoPlan;

/**
 * Interfaz del Servicio de planes de numeracion.
 */
public interface IPlanNumeracionService {

    /**
     * Crea un plan nuevo.
     * @param plan a crear.
     */
    void create(Plan plan);

    /**
     * Devuelve el ultimo plan del tipo dado.
     * @param tipoPlan tipo de plan
     * @return el plan
     */
    Plan getUltimoPlan(TipoPlan tipoPlan);

    /**
     * Devuelve el nombre del tipo substituyendo la fecha.
     * @param tipoPlan tipo plan
     * @param fecha fecha
     * @return nombre con los campos reeemplazados
     */
    String getNombre(TipoPlan tipoPlan, Calendar fecha);

    /**
     * Devuelve el tipo de plan por su identificador.
     * @param id identificador de tipo de plan
     * @return tipo de plan
     */
    TipoPlan getTipoPlanbyId(String id);

    /**
     * Borra los planes que han sobrepasado el periodo de retencion.
     */
    void deletePlanesViejos();

    /**
     * Devuelve la lista de tipo de plan por rol.
     * @param idRol String
     * @return List<String>
     */
    List<String> findAllTipoPlanByRol(String idRol);

    /**
     * Devuelve la lista de planes para un rol.
     * @param idRol String
     * @return List<Plan>
     */
    List<Plan> findAllPlanByRol(String idRol);

    /**
     * Busca el plan por tipo.
     * @param idTipo String
     * @return Plan
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
