package mx.ift.sns.dao.tarea.implementacion;

import java.util.Date;

import javax.inject.Named;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.tarea.IControlTareaDao;
import mx.ift.sns.modelo.tareas.ControlTarea;

/**
 * Implementecion del DAO de control de tareas planificadas.
 * @author X36155QU
 */
@Named
public class ControlTareaDao extends BaseDAO<ControlTarea> implements IControlTareaDao {

    @Override
    public ControlTarea getTareaNoBloqueada(String tarea, Date fecha) {

        StringBuffer sbQuery = new StringBuffer("SELECT ct FROM ControlTarea ct ");
        sbQuery.append("WHERE ct.bloqueado = :libre AND ct.fecha < :fecha AND ct.tarea = :tarea");

        try {
            TypedQuery<ControlTarea> tQuery = getEntityManager().createQuery(sbQuery.toString(), ControlTarea.class);
            tQuery.setParameter("libre", 0); //Valor entero, dejo de ser boolean
            tQuery.setParameter("fecha", fecha);
            tQuery.setParameter("tarea", tarea);

            // tQuery.setLockMode(LockModeType.PESSIMISTIC_WRITE);

            return tQuery.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * FJAH 08052025 Refactorización para evitar dejar con harcode de tareas a no ejecutarse.
     * 0 = desbloqueado
     * 1 = bloqueo automático (cron)
     * 2 = bloqueo manual protegido
     */
    @Override
    public int desbloqueoTareas() {
        String sQuery = "UPDATE ControlTarea SET bloqueado = 0 WHERE bloqueado = 1";
        Query query = getEntityManager().createQuery(sQuery);
        return query.executeUpdate();
    }
    /*
    @Override
    public void desbloqueoTareas() {
        String sQuery = "UPDATE ControlTarea SET bloqueado = :libre";

        Query query = getEntityManager().createQuery(sQuery);
        query.setParameter("libre", false);

        query.executeUpdate();
    }

     */
}
