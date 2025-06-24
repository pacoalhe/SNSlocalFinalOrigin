package mx.ift.sns.negocio;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import mx.ift.sns.dao.tarea.IControlTareaDao;
import mx.ift.sns.modelo.tareas.ControlTarea;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.sched.IControlTareas;
import mx.ift.sns.utils.WeblogicNode;
import mx.ift.sns.utils.date.FechasUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio de control de tareas.
 * @author X36155QU
 */
@Stateless(mappedName = "ControlTareasService")
@Remote(IControlTareas.class)
public class ControlTareasSevice implements IControlTareas {

    /*
    //FECHA PROCESO
        Date FECHA_PROCESO = FechasUtils.getFechaHoy("dd.MM.yyyy");
    //termina Fecha Proceso}
     */

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ControlTareasSevice.class);

    /**
     * DAO del control de tareas.
     */
    @Inject
    private IControlTareaDao controlTareaDao;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void desbloquearTarea(String tarea) {
        LOGGER.info("Desbloqueamos la tarea {} en {}", tarea, WeblogicNode.getName());
        ControlTarea control = new ControlTarea();
        control.setTarea(tarea);
        control = controlTareaDao.reload(control);

         /** FJAH 08052025 Refactorización para evitar dejar con harcode de tareas a no ejecutarse.
                    * 0 = desbloqueado
                    * 1 = bloqueo automático (cron)
                    * 2 = bloqueo manual protegido
         */
        control.setBloqueado(0);
        //control.setBloqueado(false);
        controlTareaDao.update(control);

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean isAccesoPermitido(String tarea) {
        boolean resultado = false;
        ControlTarea control = new ControlTarea();
        //TODO desloquear para QA y Productivo
        Date fechaHoy = FechasUtils.getFechaHoy();
        //Date fechaHoy = FECHA_PROCESO; //FJAH 28.05.2025 Refactorizado

        LOGGER.info("Buscamos si la tarea {} en {} tiene acceso permitido para el día {}", tarea,
                WeblogicNode.getName(), FechasUtils.fechaToString(fechaHoy));

        try {
            control = controlTareaDao.getTareaNoBloqueada(tarea, fechaHoy);

            if (control != null) {
                /** FJAH 08052025 Refactorización para evitar dejar con harcode de tareas a no ejecutarse.
                 * 0 = desbloqueado
                 * 1 = bloqueo automático (cron)
                 * 2 = bloqueo manual protegido
                 */
                control.setBloqueado(1);
                //control.setBloqueado(true);
                LOGGER.info("La tarea encontrada tiene fecha: {}", FechasUtils.fechaToString(control.getFecha()));

                control.setFecha(fechaHoy);

                LOGGER.debug("Valores enviados al DAO:");
                LOGGER.debug("→ tarea: '{}'", tarea);
                LOGGER.debug("→ fecha: '{}'", FechasUtils.fechaToString(fechaHoy));
                LOGGER.debug("→ nodo: '{}'", WeblogicNode.getName());
                LOGGER.info("Vamos a Actualizar la tarea a fecha {}", FechasUtils.fechaToString(fechaHoy));

                controlTareaDao.update(control);
                resultado = true;
                LOGGER.info("Actualizamos la tarea a fecha {}", FechasUtils.fechaToString(fechaHoy));

            } else {
                LOGGER.warn("No se encontró la tarea '{}' en el nodo '{}' para la fecha '{}'", tarea,
                        WeblogicNode.getName(), FechasUtils.fechaToString(fechaHoy));
            }
        }  catch (RegistroModificadoException e) {
            LOGGER.info("La tarea en el nodo {} se esta ejecutando en otro nodo", WeblogicNode.getName());
            return false;
        } catch (Exception e) {
            LOGGER.error("FALLA isAccesoPermitido");
            LOGGER.error("Nodo: {}", WeblogicNode.getName());
            LOGGER.error("Tarea: {}", tarea);
            LOGGER.error("Fecha evaluada: {}", FechasUtils.fechaToString(fechaHoy));
            LOGGER.error("Tipo excepción: {}", e.getClass().getName());
            LOGGER.error("Mensaje excepción: {}", e.getMessage(), e);
            return false;
        }
        if (resultado) {
            LOGGER.info("Realizamos la tarea {} en {}", tarea, WeblogicNode.getName());
        }
        return resultado;

    }

    /**
     * FJAH 08052025 Refactorización para evitar dejar con harcode de tareas a no ejecutarse.
     * 0 = desbloqueado
     * 1 = bloqueo automático (cron)
     * 2 = bloqueo manual protegido
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void desbloqueoTareas() {
        int updated = controlTareaDao.desbloqueoTareas();
        LOGGER.info("Tareas desbloqueadas automáticamente: {}", updated);
    }
    /*
    @Override
    public void desbloqueoTareas() {
        controlTareaDao.desbloqueoTareas();

    }

     */

}
