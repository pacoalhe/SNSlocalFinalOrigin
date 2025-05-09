package mx.ift.sns.web.backend.sched;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import mx.ift.sns.negocio.sched.IScheduler;
import mx.ift.sns.web.common.App;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean de ejecución del planificador de tareas.
 */
@ManagedBean(name = "schedBean")
@ViewScoped
public class SchedBean implements Serializable {

    /** UID Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedBean.class);

    /** Servicio de Planificador de tareas. */
    @EJB(mappedName = "Scheduler")
    private IScheduler sched;

    /** Propiedades de la Aplicación. */
    // @ManagedProperty(value = "#{app}")
    private App app;

    /**
     * Inicializacion del bean.
     */
    @PostConstruct
    public void init() {
        LOGGER.debug("");

        FacesContext context = FacesContext.getCurrentInstance();
        app = context.getApplication().evaluateExpressionGet(context, "#{app}", App.class);

        LOGGER.debug("tokens {}", app.getTokens());
    }

    /** Ejecuta la Sincronización de ABD. */
    public void sincronizar() {
        LOGGER.info("inicio");

        sched.fireSincronizarABD();

        LOGGER.info("fin");
    }

    /** Inicia la Ejecución de Trámites Programados. */
    public void fireSolicitudesProgramadas() {
        LOGGER.info("inicio");

        sched.fireSolicitudesProgramadas();

        LOGGER.info("fin");
    }

    /** Ejecuta el borrado de Planes. */
    public void fireBorradoPlanes() {
        LOGGER.info("inicio");

        sched.fireBorradoPlanes();

        LOGGER.info("fin");
    }

    /** Inicia la Ejecución Planes. */
    public void fireGenerarPlanes() {
        LOGGER.info("inicio");

        sched.fireGenerarPlanes();

        LOGGER.info("fin");

    }

    /** Invoca los métodos de Liberación de Cuarentena del Planificador. */
    public void fireCuarentena() {
        LOGGER.info("inicio");
        sched.fireCuarentena();
        LOGGER.info("fin");
    }

}
