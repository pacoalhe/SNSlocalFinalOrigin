package mx.ift.sns.negocio.sched;

/**
 * Interfaz de control de tareas.
 * @author X36155QU
 */
public interface IControlTareas {

    /**
     * Desbloquea una tarea.
     * @param tarea tarea
     */
    void desbloquearTarea(String tarea);

    /**
     * Comprueba si el timer tiene acceso a ejecutarse.
     * @param tarea identificador de la tarea
     * @return true/false
     */
    boolean isAccesoPermitido(String tarea);

    /**
     * Desbloquea todas las tareas.
     */
    void desbloqueoTareas();

}
