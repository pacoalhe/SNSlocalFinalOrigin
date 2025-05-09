package mx.ift.sns.negocio.sched;

/**
 * Interfaz del Scheduler.
 */
public interface IScheduler {

    /** Ejecuta las acciones de solicitudes programadas. */
    void fireSolicitudesProgramadas();

    /** Ejecuta las acciones de borrado de planes. */
    void fireBorradoPlanes();

    /** Ejecuta las acciones de generación de planes. */
    void fireGenerarPlanes();

    /** Ejecuta las acciones de sincronización de ABD. */
    void fireSincronizarABD();

    /** Ejecuta las acciones de liberación de Cuarentena. */
    void fireCuarentena();
}
