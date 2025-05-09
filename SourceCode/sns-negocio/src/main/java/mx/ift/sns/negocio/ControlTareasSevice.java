package mx.ift.sns.negocio;

import java.util.Date;

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
        control.setBloqueado(false);
        controlTareaDao.update(control);

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean isAccesoPermitido(String tarea) {

        boolean resultado = false;
        ControlTarea control = new ControlTarea();
        Date fechaHoy = FechasUtils.getFechaHoy();

        LOGGER.info("Buscamos si la tarea {} en {} tiene acceso permitido para el día {}", tarea,
                WeblogicNode.getName(), FechasUtils.fechaToString(fechaHoy));

        try {
            control = controlTareaDao.getTareaNoBloqueada(tarea, fechaHoy);
            if (control != null) {
                control.setBloqueado(true);
                LOGGER.info("La tarea encontrada tiene fecha: {}", FechasUtils.fechaToString(control.getFecha()));

                control.setFecha(fechaHoy);

                controlTareaDao.update(control);
                resultado = true;
                LOGGER.info("Actualizamos la tarea a fecha {}", FechasUtils.fechaToString(fechaHoy));

            } else {
                LOGGER.info("No se ha encotrado la tarea solicitada");//, WeblogicNode.getName());
            }
        } catch (RegistroModificadoException e) {
            LOGGER.info("La tarea en el nodo {} se esta ejecutando en otro nodo", WeblogicNode.getName());
            return false;
        }
        if (resultado) {
            LOGGER.info("Realizamos la tarea {} en {}", tarea, WeblogicNode.getName());
        }
        return resultado;

    }

    @Override
    public void desbloqueoTareas() {
        controlTareaDao.desbloqueoTareas();

    }

}
