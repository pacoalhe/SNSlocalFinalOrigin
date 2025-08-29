package mx.ift.sns.negocio;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.tareas.ControlTarea;
import mx.ift.sns.negocio.cpsi.ICodigoCPSIService;
import mx.ift.sns.negocio.cpsi.ISolicitudesCpsiService;
import mx.ift.sns.negocio.cpsn.ICodigoCPSNService;
import mx.ift.sns.negocio.cpsn.ISolicitudesCpsnService;
import mx.ift.sns.negocio.ng.ISolicitudesService;
import mx.ift.sns.negocio.nng.ISeriesNngService;
import mx.ift.sns.negocio.nng.ISolicitudesNngService;
import mx.ift.sns.negocio.pnn.IPlanMaestroService;
import mx.ift.sns.negocio.pnn.IPlanNumeracionJob;
import mx.ift.sns.negocio.pnn.IPlanNumeracionService;
import mx.ift.sns.negocio.port.IPortabilidadService;
import mx.ift.sns.negocio.sched.IControlTareas;
import mx.ift.sns.negocio.sched.IScheduler;
import mx.ift.sns.negocio.usu.IUsuariosService;
import mx.ift.sns.utils.WeblogicNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tareas programadas del sistema.
 */
@Stateless(name= "Scheduler", mappedName = "Scheduler")
@Remote(IScheduler.class)
public class Scheduler implements IScheduler {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);

    /** Servicio de planes. */
    @EJB(name = "PlanNumeracionService")
    private IPlanNumeracionService planService;

    @EJB(name = "PlanMaestroService")
    private IPlanMaestroService planMaestroService;

    /** Servicio de planes Job. */
    @EJB(name = "PlanNumeracionJob")
    private IPlanNumeracionJob planServiceJob;

    /** Servicio de Solicitudes Numeración Geográfica. */
    @EJB(name = "SolicitudesService")
    private ISolicitudesService solicitudesService;

    /** Servicio de Solicitudes Numeración No Geográfica. */
    @EJB(name = "SolicitudesNngService")
    private ISolicitudesNngService solicitudesNngService;

    /** Servicio de Portabilidad. */
    @EJB(name = "PortabilidadService")
    private IPortabilidadService portabilidadService;

    /** Servicio de Códigos CPSN. */
    @EJB(name = "CodigoCPSNService")
    private ICodigoCPSNService cpsnService;

    /** Servicio de Códigos CPSI. */
    @EJB(name = "CodigoCPSIService")
    private ICodigoCPSIService cpsiService;

    /** Servicio series NNG. */
    @EJB(name = "SeriesNngService")
    private ISeriesNngService seriesNngService;

    /** Servicio de Solicitudes de Códigos CPSI. */
    @EJB(name = "SolicitudesCpsiService")
    private ISolicitudesCpsiService solicitudesCpsiService;

    /** Servicio de Solicitudes de Códigos CPSN. */
    @EJB(name = "SolicitudesCpsnService")
    private ISolicitudesCpsnService solicitudesCpsnService;

    /** Servicio de Bitácora. */
    @EJB(name = "BitacoraService")
    private IBitacoraService bitacoraService;

    /** Servicio de Usuario. */
    @EJB(name = "UsuariosService")
    private IUsuariosService usuariosSerive;

    /** Servicio de control de tareas. */
    @EJB(name = "ControlTareasService")
    private IControlTareas controlTareasService;

    /**
     * Init del servicio.
     */
    @PostConstruct
    public void init() {
        LOGGER.info(WeblogicNode.getName() + ": Inicio Scheduler - Tareas Planificadas. User: {}",
                usuariosSerive.getCurrentUser());
    }

    /**
     * Timer para desbloquear todas las tareas antes que se ejecuten.
     */
    @Schedule(hour = "22", minute = "00", persistent = true)
    void timeoutDesbloqueoTareas() {
        LOGGER.info("Inicio del timer timeoutDesbloqueoTareas");
        ejecutarDesbloqueoDeTareas();
        LOGGER.info("Fin del timer timeoutDesbloqueoTareas");
    }

    /**
     * Timer para acciones a ejecutar a las 23:00.
     */
    @Schedule(hour = "23", minute = "00", persistent = false)
    void timeoutSolicitudesProgramadas() {
        if (controlTareasService.isAccesoPermitido(ControlTarea.SOLICITUDES_PROGRAMADAS)) {
            LOGGER.info("Ejecutando tareas planificadas 23:00h");
            try {
                bitacoraService.add("Ejecutando Cesiones NG planificadas...");
                solicitudesService.applyCesionesPendientes();
            } catch (Exception e) {
                LOGGER.error("Error ejecutando Cesiones NG pendientes.", e);
                bitacoraService.add("Error ejecutando Cesiones NG pendientes.");
            }
            try {
                bitacoraService.add("Ejecutando Liberaciones NG planificadas...");
                solicitudesService.applyLiberacionesPendientes();
            } catch (Exception e) {
                LOGGER.error("Error ejecutando Liberaciones NG pendientes.", e);
                bitacoraService.add("Error ejecutando Liberaciones NG pendientes.");
            }
            try {
                bitacoraService.add("Ejecutando Cesiones NNG planificadas...");
                solicitudesNngService.applyCesionesPendientes();
            } catch (Exception e) {
                LOGGER.error("Error ejecutando Cesiones NNG pendientes.", e);
                bitacoraService.add("Error ejecutando Cesiones NNG pendientes.");
            }
            try {
                bitacoraService.add("Ejecutando Liberaciones NNG planificadas...");
                solicitudesNngService.applyLiberacionesPendientes();
            } catch (Exception e) {
                LOGGER.error("Error ejecutando Liberaciones NNG pendientes.", e);
                bitacoraService.add("Error ejecutando Liberaciones NNG pendientes.");
            }
            try {
                bitacoraService.add("Ejecutando Cesiones de CPSN planificadas...");
                solicitudesCpsnService.applyCesionesPendientes();
            } catch (Exception e) {
                LOGGER.error("Error ejecutando Cesiones de CPSN  pendientes.", e);
                bitacoraService.add("Error ejecutando Cesiones de CPSN  pendientes.");
            }
            try {
                bitacoraService.add("Ejecutando Liberaciones de CPSN planificadas...");
                solicitudesCpsnService.applyLiberacionesPendientes();
            } catch (Exception e) {
                LOGGER.error("Error ejecutando Liberaciones de CPSN pendientes.", e);
                bitacoraService.add("Error ejecutando Liberaciones de CPSN pendientes.");
            }
            try {
                bitacoraService.add("Ejecutando Cesiones de CPSI planificadas...");
                solicitudesCpsiService.applyCesionesPendientes();
            } catch (Exception e) {
                LOGGER.error("Error ejecutando Cesiones de CPSI  pendientes.", e);
                bitacoraService.add("Error ejecutando Cesiones de CPSI  pendientes.");
            }
            try {
                bitacoraService.add("Ejecutando Liberaciones de CPSI planificadas...");
                solicitudesCpsiService.applyLiberacionesPendientes();
            } catch (Exception e) {
                LOGGER.error("Error ejecutando Liberaciones de CPSI pendientes.", e);
                bitacoraService.add("Error ejecutando Liberaciones de CPSI pendientes.");
            }
            controlTareasService.desbloquearTarea(ControlTarea.SOLICITUDES_PROGRAMADAS);
        }
    }

    /**
     * Timer para acciones a ejecutar a las 23:10.
     */
    @Schedule(hour = "23", minute = "10", persistent = false)
    void timeoutCuarentena() {
        if (controlTareasService.isAccesoPermitido(ControlTarea.CUARENTENA)) {
            LOGGER.info("Ejecutando tareas planificadas 23:10h");
            try {
                bitacoraService.add("Ejecutando Liberaciones de CPSN en cuarentena...");
                cpsnService.liberarCuarentena();
            } catch (Exception e) {
                LOGGER.error("Error ejecutando Liberaciones de CPSN cuarentena.", e);
                bitacoraService.add("Error ejecutando Liberaciones de CPSN cuarentena.");
            }
            try {
                bitacoraService.add("Ejecutando Liberaciones de CPSI en cuarentena...");
                cpsiService.liberarCuarentena();
            } catch (Exception e) {
                LOGGER.error("Error ejecutando Liberaciones de CPSI cuarentena.", e);
                bitacoraService.add("Error ejecutando Liberaciones de CPSI cuarentena.");
            }
            controlTareasService.desbloquearTarea(ControlTarea.CUARENTENA);
        }
    }

    /**
     * Timer para acciones a ejecutar a las 23:15.
     */
    @Schedule(hour = "23", minute = "15", persistent = false)
    void timeoutConsolidaciones() {
        if (controlTareasService.isAccesoPermitido(ControlTarea.CONSOLIDACIONES)) {
            LOGGER.info("Ejecutando tareas planificadas 23:15h");
            try {
                bitacoraService.add("Ejecutando Consolidaciones planificadas...");
                solicitudesService.applyConsolidacionesPendientes();
            } catch (Exception e) {
                LOGGER.error("Error ejecutando Consolidaciones pendientes.", e);
                bitacoraService.add("Error ejecutando Consolidaciones pendientes.");
            }
            controlTareasService.desbloquearTarea(ControlTarea.CONSOLIDACIONES);
        }
    }

    /**
     * Timer para generar el plan abd.
     */
    @Schedule(hour = "23", minute = "20", persistent = false)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    void timeoutPlanABD() {
        if (controlTareasService.isAccesoPermitido(ControlTarea.PLAN_ABD)) {
            LOGGER.info("Ejecutando tarea planificada 23:20h: Plan de Numeración ABD");
            try {
                planServiceJob.generarPlanNumeracionABDPresuscripcionD();
            } catch (Exception e) {
                LOGGER.error("Error ejecutando Plan de Numeración ABD.", e);
            }
            LOGGER.info("Finalizada tarea planificada 23:20h: Plan de Numeración ABD");
            controlTareasService.desbloquearTarea(ControlTarea.PLAN_ABD);
        }
    }

    /**
     * Timer para generar el Reporte abd.
     */
    @Schedule(hour = "23", minute = "25", persistent = false)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    void timeoutReporteABD() {

        if (controlTareasService.isAccesoPermitido(ControlTarea.REPORTE_ABD)) {
            LOGGER.info("Ejecutando tarea planificada 23:25h: Reporte ABD.");
            try {
                planServiceJob.generarReporteABDPortabilidad();
            } catch (Exception e) {
                LOGGER.error("Error ejecutando Reporte ABD.", e);
            }
            LOGGER.info("Finalizada tarea planificada 23:25h: Reporte ABD.");
            controlTareasService.desbloquearTarea(ControlTarea.REPORTE_ABD);
        }
    }

    /**
     * Timer para generar el Plan NNG Especifica.
     */
    @Schedule(hour = "23", minute = "30", persistent = false)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    void timeoutPlanNngEspecifica() {
        if (controlTareasService.isAccesoPermitido(ControlTarea.PLAN_NNG_ESPECIFICA)) {
            LOGGER.info("Ejecutando tarea planificada 23:30h: Plan NNG Especifica.");
            try {
                planServiceJob.generarPlanNngEspecifica();
            } catch (Exception e) {
                LOGGER.error("Error ejecutando Plan NNG Especifica.", e);
            }
            LOGGER.info("Finalizada tarea planificada 23:30h: Plan NNG Especifica.");
            controlTareasService.desbloquearTarea(ControlTarea.PLAN_NNG_ESPECIFICA);
        }
    }

    /**
     * Timer para generar el Plan NNG Especifica PST.
     */
    @Schedule(hour = "23", minute = "35", persistent = false)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    void timeoutPlanNngEspecificaPst() {
        if (controlTareasService.isAccesoPermitido(ControlTarea.PLAN_NNG_ESPECIFICA_PST)) {
            LOGGER.info("Ejecutando tarea planificada 23:31h: Plan NNG Especifica PST.");
            try {
                planServiceJob.generarPlanNngEspecificaPst();
            } catch (Exception e) {
                LOGGER.error("Error ejecutando Plan NNG Especifica PST.", e);
            }
            LOGGER.info("Finalizada tarea planificada 23:31h: Plan NNG Especifica PST.");
            controlTareasService.desbloquearTarea(ControlTarea.PLAN_NNG_ESPECIFICA_PST);
        }
    }

    /**
     * Timer para generar el Plan IFT.
     */
    @Schedule(hour = "23", minute = "40", persistent = false) //Cambio hora
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    void timeoutPlanIFT() {
        if (controlTareasService.isAccesoPermitido(ControlTarea.PLAN_IFT)) {
            LOGGER.info("Ejecutando tarea planificada 23:40h: Plan IFT.");
            try {
                planServiceJob.generarPlanIFTD();
            } catch (Exception e) {
                LOGGER.error("Error ejecutando Plan IFT.", e);
            }
            LOGGER.info("Finalizada tarea planificada 23:40h: Plan IFT.");
            controlTareasService.desbloquearTarea(ControlTarea.PLAN_IFT);
        }
    }

    /**
     * Timer para generar el Plan NNG.
     */
    @Schedule(hour = "23", minute = "45", persistent = false) //Cambio hora
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    void timeoutPlanNng() {
        if (controlTareasService.isAccesoPermitido(ControlTarea.PLAN_NNG)) {
            LOGGER.info("Ejecutando tarea planificada 23:45h: Plan NNG.");
            try {
                planServiceJob.generarPlanNngPst();
                List<ClaveServicio> listaClaves = seriesNngService.findAllClaveServicioAsignadas();
                for (ClaveServicio clave : listaClaves) {
                    planServiceJob.generarPlanNngPublico(clave);
                }
                planServiceJob.generarPlanNngIFT();
            } catch (Exception e) {
                LOGGER.error("Error ejecutando Plan NNG.", e);
            }
            LOGGER.info("Finalizada tarea planificada 23:45h: Plan NNG.");
            controlTareasService.desbloquearTarea(ControlTarea.PLAN_NNG);
        }
    }

    /**
     * Timer para generar el Plan NG.
     */
    @Schedule(hour = "23", minute = "50", persistent = false)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    void timeoutPlanNg() {
        if (controlTareasService.isAccesoPermitido(ControlTarea.PLAN_NG)) {
            LOGGER.info("Ejecutando tarea planificada 23:50h: Plan NG.");
            try {
                planServiceJob.generarPlanNumeracionGeograficaPST();
                planServiceJob.generarPlanNumeracionGeograficaPublico();
            } catch (Exception e) {
                LOGGER.error("Error ejecutando Ejecutando Plan NG.", e);
            }
            LOGGER.info("Finalizada tarea planificada 23:50h: Plan NG.");
            controlTareasService.desbloquearTarea(ControlTarea.PLAN_NG);
        }
    }

    /**
     * Timer para generar los identificadores de operadores.
     */
    /*@Schedule(hour = "23", minute = "45", persistent = false)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    void timeoutIdentificadoresOperadores() {
        if (controlTareasService.isAccesoPermitido(ControlTarea.IDENTIFICADORES)) {
            LOGGER.info("Ejecutando tarea planificada 23:45h: Identificadores de Operadores.");
            try {
                planServiceJob.generarIdentificadoresOperadores();
            } catch (Exception e) {
                LOGGER.error("Error ejecutando Identificadores de Operadores.", e);
            }
            LOGGER.info("Finalizada tarea planificada 23:45h: Identificadores de Operadores.");
            controlTareasService.desbloquearTarea(ControlTarea.IDENTIFICADORES);
        }
    }*/



    /**
     * Timer para generar el Plan NNG Especifica IFT.
     */
    @Schedule(hour = "23", minute = "55", persistent = false)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    void timeoutPlanNngEspecificaIFT() {
        if (controlTareasService.isAccesoPermitido(ControlTarea.PLAN_NNG_ESPECIFICA_IFT)) {
            LOGGER.info("Ejecutando tarea planificada 23:55h: Plan NNG Especifica IFT.");
            try {
                planServiceJob.generarPlanNngEspecificaIFT();
            } catch (Exception e) {
                LOGGER.error("Error ejecutando Plan NNG Especifica IFT.", e);
            }
            LOGGER.info("Finalizada tarea planificada 23:55h: Plan NNG Especifica IFT.");
            controlTareasService.desbloquearTarea(ControlTarea.PLAN_NNG_ESPECIFICA_IFT);
        }
    }

    /**
     * Timer para borrar los planes. 1.00am
     */
    @Schedule(hour = "1", minute = "00", persistent = false)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    void timeoutBorradoPlanes() {
        if (controlTareasService.isAccesoPermitido(ControlTarea.BORRADO_PLANES)) {
            LOGGER.info("Ejecutando tareas planificadas 01:00h");
            try {
                LOGGER.info("Ejecutando borrado Planes...");
                planService.deletePlanesViejos();
            } catch (Exception e) {
                LOGGER.error("Error ejecutando borrado Planes.", e);
            }
            controlTareasService.desbloquearTarea(ControlTarea.BORRADO_PLANES);
        }
    }

    /**
     * Timer para sincronizar la BDD de portabilidad.
     */
    @Schedule(hour = "2", minute = "0", persistent = false)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    void timeoutABD() {
        LOGGER.info("Inicio timer portabilidad ABD 02:00h"); //FJAH 25Mar2025
	if (controlTareasService.isAccesoPermitido(ControlTarea.PRIMER_ABD)) {
	    LOGGER.info("Ejecutando tareas planificadas 02:00h");
	    try {
		LOGGER.info("Ejecutando Sincronización ABD...");
		portabilidadService.syncBDDPortabilidad();

		LOGGER.info("Finaliza el proceso de Sincronización ABD...");
	    } catch (Exception e) {
		LOGGER.error("Error ejecutando Sincronización ABD.", e);
		LOGGER.error("Detalle error", e.getCause());
	    }
	    controlTareasService.desbloquearTarea(ControlTarea.PRIMER_ABD);
        LOGGER.info("Fin timer portabilidad ABD 02:00h"); //FJAH 25Mar2025
	}

    }

    /**
     * Timer para sincronizar la BDD de portabilidad.
     */
    @Schedule(hour = "3", minute = "0", persistent = false)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    void timeoutABD2() {
        LOGGER.info("Inicio timer portabilidad ABD2 03:00h"); //FJAH 25Mar2025
	if (controlTareasService.isAccesoPermitido(ControlTarea.SEGUNDO_ABD)) {
	    LOGGER.info("Ejecutando tareas planificadas 03:00h");
	    try {
		LOGGER.info("Ejecutando Sincronización ABD 2...");
		portabilidadService.syncBDDPortabilidad();

		LOGGER.info("Finaliza el proceso de Sincronización ABD 2...");
	    } catch (Exception e) {
		LOGGER.error("Error ejecutando Sincronización ABD 2.", e);
		LOGGER.error("Detalle error", e.getCause());
	    }
	    controlTareasService.desbloquearTarea(ControlTarea.SEGUNDO_ABD);
        LOGGER.info("Fin timer portabilidad ABD2 04:00h"); //FJAH 25Mar2025
	}
    }

    /**
     * Timer para sincronizar la BDD de portabilidad.
     */
    @Schedule(hour = "4", minute = "0", persistent = false)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    void timeoutABD3() {
        LOGGER.info("Inicio timer portabilidad ABD3 04:00h"); //FJAH 25Mar2025
	if (controlTareasService.isAccesoPermitido(ControlTarea.TERCER_ABD)) {
	    LOGGER.info("Ejecutando tareas planificadas 04:00h");
	    try {
		LOGGER.info("Ejecutando Sincronización ABD 3...");
		portabilidadService.syncBDDPortabilidad();

		LOGGER.info("Finaliza el proceso de Sincronización ABD 3...");
	    } catch (Exception e) {
		LOGGER.error("Error ejecutando Sincronización ABD 3.", e);
		LOGGER.error("Detalle error", e.getCause());
	    }
	    controlTareasService.desbloquearTarea(ControlTarea.TERCER_ABD);
        LOGGER.info("Fin timer portabilidad ABD3 04:00h"); //FJAH 25Mar2025
	}
    }

    @Schedule(hour = "5", minute = "30", persistent = false)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    void portacionSNS() {
        LOGGER.info("Inicio timer PMN SNS 05:30h"); //FJAH 25Mar2025
        LOGGER.info("Fecha inicio de actualización PMN SNS. " + new Date().toString());
        // if (controlTareasService.isAccesoPermitido(ControlTarea.PORTACION_SNS)) {
        try {
            planMaestroService.syncPlanMaestroAsync();
        } catch (Exception e) {
            LOGGER.error("Error ejecutando actualización del PMN ", e);
        }
        // controlTareasService.desbloquearTarea(ControlTarea.PORTACION_SNS);
        LOGGER.info("Fecha terminacion de actualización PMN SNS. " + new Date().toString());
        LOGGER.info("Fin timer PMN SNS 05:30h"); //FJAH 25Mar2025
        // }
    }


    @Schedule(hour = "13", minute = "30", persistent = false)
    @TransactionAttribute( TransactionAttributeType.NOT_SUPPORTED)
    void portacionManual() {

        if (controlTareasService.isAccesoPermitido(ControlTarea.PORTACION_MANUAL)) {
            LOGGER.info("Ejecutando tareas planificadas 13:00h");
            try {
                LOGGER.info("Ejecutando Sincronización ABD...");
                portabilidadService.syncManualPortabilidad();
            } catch (Exception e) {
                LOGGER.error("Error ejecutando Sincronización ABD.", e);
            }
            controlTareasService.desbloquearTarea(ControlTarea.PORTACION_MANUAL);
            LOGGER.error("Fecha terminacion."+new Date().toString());
        }

    }

    @Override
    public void fireSolicitudesProgramadas() {
        timeoutSolicitudesProgramadas();
        timeoutCuarentena();
        timeoutConsolidaciones();
    }

    @Override
    public void fireBorradoPlanes() {
        timeoutBorradoPlanes();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void fireGenerarPlanes() {
        timeoutPlanABD();
        timeoutPlanIFT();
        timeoutPlanNg();
        timeoutPlanNng();
        timeoutPlanNngEspecifica();
        timeoutPlanNngEspecificaIFT();
        timeoutPlanNngEspecificaPst();
        timeoutReporteABD();
        //timeoutIdentificadoresOperadores();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void fireSincronizarABD() {
        try {
            timeoutABD();
            // timeoutABD2();
            // timeoutABD3();
            // portabilidadService.syncBDDPortabilidad();
        } catch (Exception e) {
            LOGGER.error("error ejecutando sync ABD.", e);
        }
    }

    @Override
    public void fireCuarentena() {
        timeoutDesbloqueoTareas();
        timeoutCuarentena();
    }


    /**
     * Timer para ejecutar tareas a la hora requerida
     * FJAH
     */
    private boolean testMode = true; // TODO FJAH: Cambiar a true para habilitar pruebas

    @Schedule(hour = "15", minute = "11", persistent = false)

    void timeoutPruebas() {
        LOGGER.info("Inicio del timer timeoutPruebas");

        // Si no estamos en modo test, no se ejecuta el timer de prueba
        if (!testMode) {
            LOGGER.info("Modo test deshabilitado, se omite ejecución de timeoutPruebas");
            return;
        }

        try {
            testGenerarPlanes(); // FJAH 26MAR2025
        } catch (Exception e) {
            LOGGER.error("Error en la ejecución de testGenerarPlanes", e);
            logBitacora("Error en testGenerarPlanes: " + e.getMessage());
        }

        LOGGER.info("Fin del timer timeoutPruebas");
    }
    /**
     * Método de prueba para ejecutar todos los planes secuencialmente.
     * FJAH 26MAR2025
     */
    public void testGenerarPlanes() {
        LOGGER.info("Inicio de testGenerarPlanes");

        try {
            timeoutDesbloqueoTareas();
            //FJAH 26032025 Modificación a logBitacora para persistincia
            logBitacora("Ejecucion Prueba desbloqueo de tareas: timeoutABD");
        } catch (Exception e) {
            LOGGER.error("Error en desbloqueo durante testGenerarPlanes: ", e);
        }
/*


        try {
            timeoutReporteABD();
            //FJAH 26032025 Modificación a logBitacora para persistincia
            logBitacora("Ejecucion Prueba testGenerarPlanes: timeoutReporteABD()");
        } catch (Exception e) {
            LOGGER.error("Error en timeoutReporteABD() durante testGenerarPlanes: ", e);
        }

        try {
			timeoutPlanABD();
			//FJAH 26032025 Modificación a logBitacora para persistincia
			logBitacora("Ejecucion Prueba testGenerarPlanes: timeoutPlanABD FJAH 26MAR2025.");
		} catch (Exception e) {
			LOGGER.error("Error en timeoutPlanABD durante testGenerarPlanes: FJAH 26MAR2025", e);
		}
*/
        try {
            portacionSNS();
            //FJAH 26032025 Modificación a logBitacora para persistincia plan maestro
            logBitacora("Ejecucion Prueba testGenerarPlanes: portacionSNS");
        } catch (Exception e) {
            LOGGER.error("Error en portacionSNS durante testGenerarPlanes: ", e);
        }

		try {
			timeoutABD();
			//FJAH 26032025 Modificación a logBitacora para persistincia
			logBitacora("Ejecucion Prueba testGenerarPlanes: timeoutPlanABD FJAH 26MAR2025.");
		} catch (Exception e) {
			LOGGER.error("Error en timeoutPlanABD durante testGenerarPlanes: FJAH 26MAR2025", e);
		}
/*
		try {
			timeoutABD2();
			//FJAH 26032025 Modificación a logBitacora para persistincia
			logBitacora("Ejecucion Prueba testGenerarPlanes: timeoutPlanABD FJAH 26MAR2025.");
		} catch (Exception e) {
			LOGGER.error("Error en timeoutPlanABD durante testGenerarPlanes: FJAH 26MAR2025", e);
		}

		try {
			timeoutABD3();
			//FJAH 26032025 Modificación a logBitacora para persistincia
			logBitacora("Ejecucion Prueba testGenerarPlanes: timeoutPlanABD FJAH 26MAR2025.");
		} catch (Exception e) {
			LOGGER.error("Error en timeoutPlanABD durante testGenerarPlanes: FJAH 26MAR2025", e);
		}

		try {
			timeoutPlanIFT();
			//FJAH 26032025 Modificación a logBitacora para persistincia
			logBitacora("Ejecucion Prueba testGenerarPlanes: timeoutPlanIFT FJAH 26MAR2025.");
		} catch (Exception e) {
			LOGGER.error("Error en timeoutPlanIFT durante testGenerarPlanes: FJAH 26MAR2025", e);
		}

		try {
			timeoutPlanNg();
			//FJAH 26032025 Modificación a logBitacora para persistincia
			logBitacora("Ejecucion Prueba testGenerarPlanes: timeoutPlanNg FJAH 26MAR2025.");
		} catch (Exception e) {
			LOGGER.error("Error en timeoutPlanNg durante testGenerarPlanes: FJAH 26MAR2025", e);
		}

		try {
			timeoutPlanNng();
			//FJAH 26032025 Modificación a logBitacora para persistincia
			logBitacora("Ejecucion Prueba testGenerarPlanes: timeoutPlanNng FJAH 26MAR2025.");
		} catch (Exception e) {
			LOGGER.error("Error en timeoutPlanNng durante testGenerarPlanes: FJAH 26MAR2025", e);
		}

		try {
			timeoutPlanNngEspecifica();
			//FJAH 26032025 Modificación a logBitacora para persistincia
			logBitacora("Ejecucion Prueba testGenerarPlanes: timeoutPlanNngEspecifica FJAH 26MAR2025.");
		} catch (Exception e) {
			LOGGER.error("Error en timeoutPlanNngEspecifica durante testGenerarPlanes: FJAH 26MAR2025", e);
		}

		try {
			timeoutPlanNngEspecificaIFT();
			//FJAH 26032025 Modificación a logBitacora para persistincia
			logBitacora("Ejecucion Prueba testGenerarPlanes: timeoutPlanNngEspecificaIFT FJAH 26MAR2025.");
		} catch (Exception e) {
			LOGGER.error("Error en timeoutPlanNngEspecificaIFT durante testGenerarPlanes: FJAH 26MAR2025", e);
		}

		try {
			timeoutPlanNngEspecificaPst();
			//FJAH 26032025 Modificación a logBitacora para persistincia
			logBitacora("Ejecucion Prueba testGenerarPlanes: timeoutPlanNngEspecificaPst FJAH 26MAR2025.");
		} catch (Exception e) {
			LOGGER.error("Error en timeoutPlanNngEspecificaPst durante testGenerarPlanes: FJAH 26MAR2025", e);
		}

		try {
            timeoutConsolidaciones();
            //FJAH 26032025 Modificación a logBitacora para persistincia
            logBitacora("Ejecucion Prueba testGenerarPlanes: timeoutConsolidaciones FJAH 13.06.2025.");
        } catch (Exception e) {
            LOGGER.error("Error en timeoutConsolidaciones durante testGenerarPlanes: FJAH 13.06.2025", e);
        }

        LOGGER.info("Fin de testGenerarPlanes FJAH");
 */
    }

    private void ejecutarDesbloqueoDeTareas() {
        LOGGER.info("Iniciando el proceso de desbloqueo de tareas.");

        if (bitacoraService != null) {
            logBitacora("Inicio de cron job: Desbloqueo de tareas.");
        }

        try {
            controlTareasService.desbloqueoTareas();

            LOGGER.info("Tareas desbloqueadas exitosamente.");
            if (bitacoraService != null) {
                logBitacora("Tareas desbloqueadas exitosamente.");
            }
        } catch (Exception e) {
            LOGGER.error("Error ejecutando el desbloqueo de tareas", e);
            if (bitacoraService != null) {
                logBitacora("Error ejecutando desbloqueo de tareas: " + e.getMessage());
            }
        }

    }


    // FJAH 26MAR2025: Método auxiliar para registrar en la bitácora en una nueva transacción
    private void logBitacora(String s) {
        bitacoraService.add(s);
    }

}
