/**
 * 
 */
package mx.ift.sns.dao.tarea;

import java.util.Date;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.tareas.ControlTarea;

/**
 * Interfaz del DAO de control de tareas.
 * @author X36155QU
 */
public interface IControlTareaDao extends IBaseDAO<ControlTarea> {

    /**
     * Obtiene el control de tarea si esta bloqueada.
     * @param tarea identificador de la tarea
     * @param fecha fecha de la tarea
     * @return bloqueada
     */
    ControlTarea getTareaNoBloqueada(String tarea, Date fecha);

    /**
     * FJAH 08052025 Refactorización para evitar dejar con harcode de tareas a no ejecutarse.
     * 0 = desbloqueado
     * 1 = bloqueo automático (cron)
     * 2 = bloqueo manual protegido
     */
    /**
     * Desbloquea todas las tareas.
     */
    int desbloqueoTareas();
    //void desbloqueoTareas();

}
