package mx.ift.sns.web.backend.cpsn.asignacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.modelo.cps.EstatusAsignacionCps;
import mx.ift.sns.modelo.cpsn.CodigoCPSN;
import mx.ift.sns.modelo.cpsn.EstatusCPSN;
import mx.ift.sns.modelo.cpsn.EstudioEquipoCPSN;
import mx.ift.sns.modelo.cpsn.NumeracionAsignadaCpsn;
import mx.ift.sns.modelo.cpsn.NumeracionSolicitadaCpsn;
import mx.ift.sns.modelo.cpsn.SolicitudAsignacionCpsn;
import mx.ift.sns.modelo.cpsn.TipoBloqueCPSN;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.negocio.cpsn.ICodigoCPSNFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.ac.cpsn.ConsultarCodigosCPSNBean;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase de soporte para la pestaña de 'Análisis' de Solicitudes de Asignación CPSN. */
public class AnalisisAsignacionCpsnTab extends TabWizard {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalisisAsignacionCpsnTab.class);

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_Analisis";

    /** Servicio de Códigos de CPSN. */
    private ICodigoCPSNFacade cpsnFacade;

    /** Campo solicitud asignación. */
    private SolicitudAsignacionCpsn solicitud;

    /** Seleccion multiple tabla resumen. */
    private NumeracionSolicitadaCpsn selectedNumeracionSolicitada;

    /** Datos del estudio de equipos de señalización. */
    private List<EstudioEquipoCPSN> listadoEstudio;

    /** Lista de códigos CPSN. */
    private List<CodigoCPSN> listaCodigo;

    /** Listado de Códigos CPSN seleccionados. */
    private List<CodigoCPSN> codigosCPSNSeleccionados;

    /** Listado de NumeracionAsignadaCpsn, añadidos a la tabla CPSN para asignacion. */
    private List<NumeracionAsignadaCpsn> numeracionesAsignadas;

    /** Instancia del bean de consulta de códigos CPSN. */
    private ConsultarCodigosCPSNBean consultarCodigosCPSNBean;

    /** booleano que activa o desactiva el botón de selección. */
    private boolean activarBotonSel = false;

    /** booleano que activa o desactiva el botón de guardar. */
    private boolean activarBotonGuardar = false;

    /** Boolean que comprueba si se ha guardado. */
    private boolean guardado = false;

    /** CPSN Asignado seleccionado. */
    private NumeracionAsignadaCpsn cpsnAsignadoSelecciondo;

    /** lista agrupada de las numeraciones solicitadas. */
    private List<NumeracionSolicitadaCpsn> listaAgrupadaNumSoli;

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'Análisis'..
     * @param pWizard Wizard.
     * @param facade fachada
     * @param consultarCodigosCPSNBean consultarCodigosCPSNBean
     */
    public AnalisisAsignacionCpsnTab(Wizard pWizard, ICodigoCPSNFacade facade,
            ConsultarCodigosCPSNBean consultarCodigosCPSNBean) {

        // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
        setWizard(pWizard);
        setIdMensajes(MSG_ID);

        // Asociamos el Servicio y Bean
        this.cpsnFacade = facade;
        this.consultarCodigosCPSNBean = consultarCodigosCPSNBean;

        // Inicializaciones
        numeracionesAsignadas = new ArrayList<NumeracionAsignadaCpsn>();
        codigosCPSNSeleccionados = new ArrayList<CodigoCPSN>();
        listaAgrupadaNumSoli = new ArrayList<NumeracionSolicitadaCpsn>();
    }

    @Override
    public void resetTab() {
        solicitud = new SolicitudAsignacionCpsn();
        activarBotonGuardar = false;
        activarBotonSel = false;
        listaCodigo = null;
        setMensajeError(null);
        listadoEstudio = new ArrayList<EstudioEquipoCPSN>();
        codigosCPSNSeleccionados = new ArrayList<CodigoCPSN>();
        guardado = false;
        listaAgrupadaNumSoli = new ArrayList<NumeracionSolicitadaCpsn>();
        selectedNumeracionSolicitada = null;
    }

    /**
     * resetea la información del tab al cerrarlo.
     */
    public void reset() {
        activarBotonGuardar = false;
        activarBotonSel = false;
        listaCodigo = null;
        setMensajeError(null);
        listadoEstudio = new ArrayList<EstudioEquipoCPSN>();
        codigosCPSNSeleccionados = new ArrayList<CodigoCPSN>();
        guardado = false;
        selectedNumeracionSolicitada = null;
    }

    @Override
    public boolean isAvanzar() {
        boolean continuar = false;
        if (guardado) {
            if (solicitud.getNumeracionAsignadas().size() > 0) {
                continuar = true;
            } else {
                continuar = false;
                MensajesBean.addErrorMsg(MSG_ID,
                        "No tiene registros en la tabla CPSN para asignación", "");
            }
        } else {
            continuar = false;
            MensajesBean.addErrorMsg(MSG_ID,
                    "Debe tener registros en la tabla CPSN para asignación y posteriormente guardar", "");
        }
        return continuar;
    }

    @Override
    public void actualizaCampos() {
        // Reseteamos el Tab por si se va y viene desde los botones de navegación del wizard.
        this.resetTab();

        // Asociamos la solicitud que usaremos en todo el Wizard
        solicitud = (SolicitudAsignacionCpsn) getWizard().getSolicitud();

        // Cuando la solicitud tiene códigos asignados, se puede avanzar
        if (solicitud.getNumeracionAsignadas().size() > 0) {
            guardado = true;
        }
    }

    /**
     * Método que genera el estudio del de los equipos de señalización de CPSN a partir del proveedor.
     */
    public void generarAnalisis() {
        int valor = 0;
        int total = 0;
        listadoEstudio = cpsnFacade.estudioEquipoCPSN(solicitud.getProveedorSolicitante());
        if (!listadoEstudio.isEmpty()) {
            for (EstudioEquipoCPSN estudio : listadoEstudio) {
                if (estudio.getTipoBloque().equals(TipoBloqueCPSN.TXT_BLOQUE_2048)) {
                    valor = 2048;
                    estudio.setTotalBloques(estudio.getNumAsignados() / valor);
                } else if (estudio.getTipoBloque().equals(TipoBloqueCPSN.TXT_BLOQUE_128)) {
                    valor = 128;
                    estudio.setTotalBloques(estudio.getNumAsignados() / valor);
                } else if (estudio.getTipoBloque().equals(TipoBloqueCPSN.TXT_BLOQUE_8)) {
                    valor = 8;
                    estudio.setTotalBloques(estudio.getNumAsignados() / valor);
                } else if (estudio.getTipoBloque().equals(TipoBloqueCPSN.TXT_BLOQUE_INDIVIDUAL)) {
                    valor = 1;
                    estudio.setTotalBloques(estudio.getNumAsignados() / valor);
                }
                total = total + estudio.getTotalBloques();
            }
            listadoEstudio.get(4).setTotalBloques(total);
        }
    }

    /** Método invocado al pulsar el botón de 'Catálogo'. */
    public void abrirCatalogo() {
        try {
            // consultarCodigosCPSNBean.setProveedorSeleccionado(this.solicitud.getProveedorSolicitante());
            consultarCodigosCPSNBean.realizarBusqueda();
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método encargado de reinicia las tablas al reseleccionar una numeracion solicitada.
     */
    public void selectNumeracionSolicitada() {
        // FiltroBusquedaCodigosCPSN filtro = new FiltroBusquedaCodigosCPSN();
        // filtro.setTipoBloqueCPSN(selectedNumeracionSolicitada.getTipoBloque());
        try {
            if (selectedNumeracionSolicitada != null) {
                listaCodigo = cpsnFacade.findAllCodigosCPSN(solicitud.getProveedorSolicitante(),
                        selectedNumeracionSolicitada.getTipoBloque());
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
        }
    }

    /** Método encargado de actualizar el mensaje a mostrar y mostrar el botón seleccionar. */
    public void seleccionarCodigo() {
        activarBotonSel = false;
        for (CodigoCPSN codigo : codigosCPSNSeleccionados) {
            if (EstatusCPSN.LIBRE.equals(codigo.getEstatusCPSN().getId())) {
                activarBotonSel = true;
            } else {
                MensajesBean.addInfoMsg(MSG_ID,
                        MensajesBean.getTextoResource("asignacion.cpsn.tab.analisis.CPSN.seleccionar.asignacion"),
                        "");
                activarBotonSel = false;
                break;
            }
        }
    }

    /** Método encargado de añadir los registros seleccionados a la tabla CPSN para Asignación. */
    public void anadirCpsnParaAsignacion() {
        guardado = false;
        try {
            for (CodigoCPSN codigo : codigosCPSNSeleccionados) {
                if (!arrayContains(solicitud.getNumeracionAsignadas(), codigo.getId().intValue())) {
                    solicitud.getNumeracionAsignadas().add(this.getCpsnAsignadoFromCodigoCpsn(codigo));
                }
            }
            if (solicitud.getNumeracionAsignadas().size() > 0) {
                activarBotonGuardar = true;
            } else {
                activarBotonGuardar = false;
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
        }
    }

    /**
     * Crea un objeto CpsiAsignado con la información de un código CPSN.
     * @param codigo codigo cpsn
     * @return NumeracionAsignadaCpsn
     */
    private NumeracionAsignadaCpsn getCpsnAsignadoFromCodigoCpsn(CodigoCPSN codigo) {
        NumeracionAsignadaCpsn numeracion = new NumeracionAsignadaCpsn();
        numeracion.setBinario(codigo.getBinario());
        numeracion.setDecimalDesde(codigo.getDecimalDesde());
        numeracion.setDecimalHasta(codigo.getDecimalHasta());
        numeracion.setDecimalRed(codigo.getDecimalRed());
        numeracion.setDecimalTotal(codigo.getDecimalTotal());
        numeracion.setTipoBloqueCpsn(codigo.getTipoBloqueCPSN());
        numeracion.setIdCpsn(codigo.getId());
        numeracion.setSolicitudAsignacion(solicitud);
        EstatusAsignacionCps estatus = new EstatusAsignacionCps();
        estatus.setCodigo(EstatusAsignacionCps.PENDIENTE);
        estatus.setDescripcion(EstatusAsignacionCps.TXT_PENDIENTE);
        numeracion.setEstatus(estatus);

        return numeracion;
    }

    /** Método invocado al pulsar el botón de 'Eliminar' en la tabla de Asignaciones. */
    public void eliminarAsignacion() {
        try {
            PeticionCancelacion checkCancelacion = cpsnFacade.cancelAsignacion(cpsnAsignadoSelecciondo, true);
            if (checkCancelacion.isCancelacionPosible()) {

                if (cpsnAsignadoSelecciondo.getEstatus().getCodigo().equals(EstatusAsignacionCps.ASIGNADO)) {
                    // Si la Asignación ya se había ejecutado marcamos la asignación como cancelada.
                    EstatusAsignacionCps estatusCancelada = new EstatusAsignacionCps();
                    estatusCancelada.setCodigo(EstatusAsignacionCps.CANCELADO);
                    cpsnAsignadoSelecciondo.setEstatus(estatusCancelada);
                } else {
                    // Si la asignación estaba como "Pendiente" directamente se elimina.
                    solicitud.getNumeracionAsignadas().remove(cpsnAsignadoSelecciondo);
                }

                // Actualizamos la solicitud
                this.guardarCambios();

                if (solicitud.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_TERMINADA)) {
                    // Si todas las asignaciones están canceladas el trámite pasa a estar cancelado también.
                    boolean asignacionesCanceladas = true;
                    for (NumeracionAsignadaCpsn cpsnAsig : solicitud.getNumeracionAsignadas()) {
                        asignacionesCanceladas = asignacionesCanceladas
                                && (cpsnAsig.getEstatus().getCodigo().equals(EstatusAsignacionCps.CANCELADO));
                    }

                    // Cambiamos el estado de la solicitud
                    if (asignacionesCanceladas) {
                        EstadoSolicitud statusCancelada = new EstadoSolicitud();
                        statusCancelada.setCodigo(EstadoSolicitud.SOLICITUD_CANCELADA);
                        solicitud.setEstadoSolicitud(statusCancelada);

                        MensajesBean.addInfoMsg(MSG_ID, "Se han cancelado todas las asignaciones."
                                + " El trámite pasa a estado 'Cancelado'", "");

                        // Guardamos los cambios
                        solicitud = cpsnFacade.saveSolicitudAsignacion(solicitud);
                        getWizard().setSolicitud(solicitud);
                    }
                }
            } else {
                MensajesBean.addErrorMsg(MSG_ID, checkCancelacion.getMensajeError(), "");
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0015), "");
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
        }
    }

    /**
     * Guarda los cambios realizados hasta el momento en el trámite.
     * @return 'True' si se ha podido guardar correctamente.
     */
    private boolean guardarCambios() {
        try {
            // Lista de Asignaciones Solicitadas a eliminar permanentemente
            ArrayList<NumeracionAsignadaCpsn> asigSolEliminar = new ArrayList<NumeracionAsignadaCpsn>();

            // Eliminamos de la solicitud las Asignaciones que se han eliminado de la tabla de resumen.
            for (NumeracionAsignadaCpsn cpsnAsig : solicitud.getNumeracionAsignadas()) {
                if (cpsnAsig.getEstatus().getCodigo().equals(EstatusAsignacionCps.PENDIENTE)) {
                    if ((!arrayContains(solicitud.getNumeracionAsignadas(), cpsnAsig.getIdCpsn().intValue()))) {
                        // No podemos eliminar una asignación solicitada de la lista mientras estamos
                        // recorriendo la misma lista (ConcurrentModificationException).
                        // Por lo tanto la añadimos a otra lista para eliminarla al salir del bucle.
                        asigSolEliminar.add(cpsnAsig);
                    }
                }
            }

            // Eliminamos de la solicitud los CpsiAsignado que se han eliminado de la tabla de resumen.
            for (NumeracionAsignadaCpsn cpsnAsig : asigSolEliminar) {
                // Al tener marcadas las liberacionesSolicitadas como @PrivateOwned en JPA se eliminan automáticamente
                // cuando se guardan los cambios en la solicitud ya que indica que nadie más las utiliza.
                solicitud.removeNumeracionAsignada(cpsnAsig);
            }

            // Agregamos las asignaciones que se han añadido
            for (NumeracionAsignadaCpsn cpsnAsig : solicitud.getNumeracionAsignadas()) {
                if (!arrayContains(solicitud.getNumeracionAsignadas(), cpsnAsig.getIdCpsn().intValue())) {
                    solicitud.addNumeracionAsignada(cpsnAsig);
                }
            }

            // Agreamos valores de asignación por defecto
            if (solicitud.getFechaAsignacion() == null) {
                solicitud.setFechaAsignacion(FechasUtils.getFechaHoy());
                solicitud.setDiasAplicacion(20); // 20 Por Defecto
                solicitud.setFechaIniUtilizacion(FechasUtils.calculaFecha(
                        solicitud.getFechaAsignacion(), solicitud.getDiasAplicacion(), 0, 0));
            }

            // Guardamos los cambios y generamos los nuevos registros
            solicitud = cpsnFacade.saveSolicitudAsignacion(solicitud);

            // Actualizamos la solicitud para todos los tabs
            getWizard().setSolicitud(solicitud);
            guardado = true;

        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
            guardado = false;
        } catch (Exception e) {
            LOGGER.error("error", e);
            MensajesBean.addErrorMsg(MSG_ID,
                    MensajesBean.getTextoResource("manual.generales.error.guardar"), "");
            guardado = false;
        }

        return guardado;
    }

    /**
     * Método invocado al pulsar sobre el botón 'Guardar'.
     */
    public void guardarCambiosManual() {
        // Guardamos los cambios realizados en la solicitud.
        if (this.guardarCambios()) {
            // Mensaje de información al usuario.
            StringBuffer sBuf = new StringBuffer();
            sBuf.append(MensajesBean.getTextoResource("manual.generales.exito.guardar")).append(" ");
            sBuf.append(solicitud.getId());
            MensajesBean.addInfoMsg(MSG_ID, sBuf.toString(), "");
        }
    }

    /**
     * Indica si una lista contiene un CPSN Asignado sin usar los métos Equals de Object.
     * @param list Lista de objetos CpsiAsignado
     * @param pCpsnId Identificador de código CPSN.
     * @return True si existe un objeto CpsiAsignado con el Id del CPSN esta en la lista
     * @throws Exception en caso de error
     */
    private boolean arrayContains(List<NumeracionAsignadaCpsn> list, int pCpsnId) throws Exception {

        // Cuando se guardan los cambios los NumeracionAsignadaCpsn tienen otra instancia diferente dada
        // por JPA y, por lo tanto, no se pueden comparar con las almacenadas en la lista interna. Es necesario
        // comparar por los campos clave para saber si nos estamos refiriendo a la misma.

        for (NumeracionAsignadaCpsn cpsiAsig : list) {
            if (cpsiAsig.getIdCpsn().intValue() == pCpsnId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Información de la solicitud.
     * @return SolicitudAsignacionCpsn
     */
    public SolicitudAsignacionCpsn getSolicitud() {
        return solicitud;
    }

    /**
     * Identificador de la seleccion tabla resumen.
     * @return NumeracionSolicitadaCpsn
     */
    public NumeracionSolicitadaCpsn getSelectedNumeracionSolicitada() {
        return selectedNumeracionSolicitada;
    }

    /**
     * Identificador de la seleccion tabla resumen.
     * @param selectedNumeracionSolicitada NumeracionSolicitadaCpsn
     */
    public void setSelectedNumeracionSolicitada(NumeracionSolicitadaCpsn selectedNumeracionSolicitada) {
        this.selectedNumeracionSolicitada = selectedNumeracionSolicitada;
    }

    /**
     * Identificador de los datos del estudio de equipos de señalización.
     * @return List<EstudioEquipoCPSN>
     */
    public List<EstudioEquipoCPSN> getListadoEstudio() {
        return listadoEstudio;
    }

    /**
     * Identificador de los datos del estudio de equipos de señalización.
     * @param listadoEstudio List<EstudioEquipoCPSN>
     */
    public void setListadoEstudio(List<EstudioEquipoCPSN> listadoEstudio) {
        this.listadoEstudio = listadoEstudio;
    }

    /**
     * Identificador de la lista de códigos CPSN.
     * @return List<CodigoCPSN>
     */
    public List<CodigoCPSN> getListaCodigo() {
        return listaCodigo;
    }

    /**
     * Identificador de la lista de códigos CPSN.
     * @param listaCodigo List<CodigoCPSN>
     */
    public void setListaCodigo(List<CodigoCPSN> listaCodigo) {
        this.listaCodigo = listaCodigo;
    }

    /**
     * Identificador del bean de consulta de códigos CPSN.
     * @return ConsultarCodigosCPSNBean
     */
    public ConsultarCodigosCPSNBean getConsultarCodigosCPSNBean() {
        return consultarCodigosCPSNBean;
    }

    /**
     * Identificador del bean de consulta de códigos CPSN.
     * @param consultarCodigosCPSNBean ConsultarCodigosCPSNBean
     */
    public void setConsultarCodigosCPSNBean(ConsultarCodigosCPSNBean consultarCodigosCPSNBean) {
        this.consultarCodigosCPSNBean = consultarCodigosCPSNBean;
    }

    /**
     * Identificador de Códigos CPSN seleccionados.
     * @return List<CodigoCPSN>
     */
    public List<CodigoCPSN> getCodigosCPSNSeleccionados() {
        return codigosCPSNSeleccionados;
    }

    /**
     * Identificador de Códigos CPSN seleccionados.
     * @param codigosCPSNSeleccionados List<CodigoCPSN>
     */
    public void setCodigosCPSNSeleccionados(List<CodigoCPSN> codigosCPSNSeleccionados) {
        this.codigosCPSNSeleccionados = codigosCPSNSeleccionados;
    }

    /**
     * Identificador de las NumeracionAsignadaCpsn añadidas a la tabla CPSN para asignacion.
     * @return List<NumeracionAsignadaCpsn>
     */
    public List<NumeracionAsignadaCpsn> getNumeracionesAsignadas() {
        return numeracionesAsignadas;
    }

    /**
     * Identificador de las NumeracionAsignadaCpsn añadidas a la tabla CPSN para asignacion.
     * @param numeracionesAsignadas List<NumeracionAsignadaCpsn>
     */
    public void setNumeracionesAsignadas(List<NumeracionAsignadaCpsn> numeracionesAsignadas) {
        this.numeracionesAsignadas = numeracionesAsignadas;
    }

    /**
     * Identificador que activa o desactiva el botón de selección.
     * @return boolean
     */
    public boolean isActivarBotonSel() {
        return activarBotonSel;
    }

    /**
     * Identificador que activa o desactiva el botón de selección.
     * @param activarBotonSel boolean
     */
    public void setActivarBotonSel(boolean activarBotonSel) {
        this.activarBotonSel = activarBotonSel;
    }

    /**
     * Identificador que activa o desactiva el botón de guardar.
     * @return boolean
     */
    public boolean isActivarBotonGuardar() {
        return activarBotonGuardar;
    }

    /**
     * Identificador que activa o desactiva el botón de guardar.
     * @param activarBotonGuardar boolean
     */
    public void setActivarBotonGuardar(boolean activarBotonGuardar) {
        this.activarBotonGuardar = activarBotonGuardar;
    }

    /**
     * Identificador si está guardado o no.
     * @return boolean
     */
    public boolean isGuardado() {
        return guardado;
    }

    /**
     * Identificador si está guardado o no.
     * @param guardado boolean
     */
    public void setGuardado(boolean guardado) {
        this.guardado = guardado;
    }

    /**
     * Identificador del código CPSN Asignado seleccionado.
     * @return NumeracionAsignadaCpsn
     */
    public NumeracionAsignadaCpsn getCpsnAsignadoSelecciondo() {
        return cpsnAsignadoSelecciondo;
    }

    /**
     * Identificador del código CPSN Asignado seleccionado.
     * @param cpsnAsignadoSelecciondo NumeracionAsignadaCpsn
     */
    public void setCpsnAsignadoSelecciondo(NumeracionAsignadaCpsn cpsnAsignadoSelecciondo) {
        this.cpsnAsignadoSelecciondo = cpsnAsignadoSelecciondo;
    }

    /**
     * Identificador de la lista agrupada de las numeraciones solicitadas.
     * @return List<NumeracionSolicitadaCpsn>
     */
    public List<NumeracionSolicitadaCpsn> getListaAgrupadaNumSoli() {
        int cant2048 = 0;
        int cant128 = 0;
        int cant8 = 0;
        int cantIndv = 0;
        boolean existeBloq2048 = false;
        boolean existeBloq128 = false;
        boolean existeBloq8 = false;
        boolean existeBloqIndv = false;
        NumeracionSolicitadaCpsn solicitada = null;
        TipoBloqueCPSN tipo = null;

        listaAgrupadaNumSoli = new ArrayList<NumeracionSolicitadaCpsn>();

        for (NumeracionSolicitadaCpsn numSoli : solicitud.getNumeracionSolicitadas()) {
            if (numSoli.getTipoBloque().getId().equals(TipoBloqueCPSN.BLOQUE_2048)) {
                cant2048 = cant2048 + numSoli.getCantSolicitada().intValue();
                existeBloq2048 = true;
            } else if (numSoli.getTipoBloque().getId().equals(TipoBloqueCPSN.BLOQUE_128)) {
                cant128 = cant128 + numSoli.getCantSolicitada().intValue();
                existeBloq128 = true;
            } else if (numSoli.getTipoBloque().getId().equals(TipoBloqueCPSN.BLOQUE_8)) {
                cant8 = cant8 + numSoli.getCantSolicitada().intValue();
                existeBloq8 = true;
            } else if (numSoli.getTipoBloque().getId().equals(TipoBloqueCPSN.INDIVIDUAL)) {
                cantIndv = cantIndv + numSoli.getCantSolicitada().intValue();
                existeBloqIndv = true;
            }
        }

        if (existeBloq2048) {
            solicitada = new NumeracionSolicitadaCpsn();
            tipo = new TipoBloqueCPSN(TipoBloqueCPSN.BLOQUE_2048);
            tipo.setDescripcion(TipoBloqueCPSN.TXT_BLOQUE_2048);
            solicitada.setTipoBloque(tipo);
            solicitada.setCantSolicitada(new BigDecimal(cant2048));
            listaAgrupadaNumSoli.add(solicitada);
        }
        if (existeBloq128) {
            solicitada = new NumeracionSolicitadaCpsn();
            tipo = new TipoBloqueCPSN(TipoBloqueCPSN.BLOQUE_128);
            tipo.setDescripcion(TipoBloqueCPSN.TXT_BLOQUE_128);
            solicitada.setTipoBloque(tipo);
            solicitada.setCantSolicitada(new BigDecimal(cant128));
            listaAgrupadaNumSoli.add(solicitada);
        }
        if (existeBloq8) {
            solicitada = new NumeracionSolicitadaCpsn();
            tipo = new TipoBloqueCPSN(TipoBloqueCPSN.BLOQUE_8);
            tipo.setDescripcion(TipoBloqueCPSN.TXT_BLOQUE_8);
            solicitada.setTipoBloque(tipo);
            solicitada.setCantSolicitada(new BigDecimal(cant8));
            listaAgrupadaNumSoli.add(solicitada);
        }
        if (existeBloqIndv) {
            solicitada = new NumeracionSolicitadaCpsn();
            tipo = new TipoBloqueCPSN(TipoBloqueCPSN.INDIVIDUAL);
            tipo.setDescripcion(TipoBloqueCPSN.TXT_BLOQUE_INDIVIDUAL);
            solicitada.setTipoBloque(tipo);
            solicitada.setCantSolicitada(new BigDecimal(cantIndv));
            listaAgrupadaNumSoli.add(solicitada);
        }
        return listaAgrupadaNumSoli;
    }

    /**
     * Identificador de la lista agrupada de las numeraciones solicitadas.
     * @param listaAgrupadaNumSoli List<NumeracionSolicitadaCpsn>
     */
    public void setListaAgrupadaNumSoli(List<NumeracionSolicitadaCpsn> listaAgrupadaNumSoli) {
        this.listaAgrupadaNumSoli = listaAgrupadaNumSoli;
    }

}
