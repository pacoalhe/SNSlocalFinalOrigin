package mx.ift.sns.web.backend.nng.liberacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroBusquedaRangos;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.nng.LiberacionSolicitadaNng;
import mx.ift.sns.modelo.nng.RangoSerieNng;
import mx.ift.sns.modelo.nng.SolicitudLiberacionNng;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.series.IRangoSerie;
import mx.ift.sns.modelo.solicitud.EstadoLiberacionSolicitada;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.utils.series.RangosSeriesUtil;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;
import mx.ift.sns.web.backend.lazymodels.RangoSerieNngLazyModel;
import mx.ift.sns.web.backend.lazymodels.multiseleccion.MultiSelectionOnLazyModelManager;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase de respaldo para el Tab/Pestaña de 'Liberación' de Numeración No Geográfica.
 */
public class LiberacionesNngTab extends TabWizard {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(LiberacionesNngTab.class);

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_LiberacionesNNG";

    /** Facade de Servicios de Numeración No Geográfica. */
    private INumeracionNoGeograficaFacade nngFacade;

    /** Información de la petición de Solicitud. */
    private SolicitudLiberacionNng solicitud;

    /** Identificador de Calve de servicio. */
    private BigDecimal claveServicio;

    /** Listado de claves de servicio. */
    private List<ClaveServicio> listaClavesServicio;

    /** Identificador de numeración. */
    private String sna;

    /** Lista de liberaciones solicitadas. */
    private List<LiberacionSolicitadaNng> listaLiberaciones;

    /** Rango seleccionado para fraccionar. */
    private RangoSerieNng rangoAFraccionar;

    /** Lista de fracciones sobre una serie o rango seleccionadas. */
    private List<IRangoSerie> listaRangosFracciones;

    /** Fracción de rango en la tabla de Fracciones. */
    private RangoSerieNng rangoFraccionado;

    /** Lista de fracciones sobre una serie o rangos resultado de las fracciones indicadas. */
    private List<RangoSerieNng> listaRangoFraccionados;

    /** Lista de Fracciones por Rango. */
    private HashMap<String, List<IRangoSerie>> fraccionesByRango;

    /** Indica si se desea fraccionar el rango o serie seleccionado. */
    private boolean usarFraccionamiento = false;

    /** Fecha de implementación de la liberación. */
    private Date fechaImplementacion;

    /** Habilita o deshabilita el botón de 'Guardar Cambios'. */
    private boolean salvarHabilitado;

    /** Indica la cantidad de rango que se ha de fraccionar a partir del número inicial. */
    private String cantidad = "1";

    /** Indica el primer número de la serie o rango a fraccionar. */
    private String numInicial = "";

    /** Indica el último numero reservado para la serie o rango fraccionado. */
    private int numFinal = 0;

    /** Filtros para búsqueda de Series y rangos. */
    private FiltroBusquedaRangos filtros = null;

    /** Modelo de datos para Lazy Loading en las tablas. */
    private RangoSerieNngLazyModel rangoSerieModel = null;

    /** Indica si estamos actualizando los campos al editar la solicitud. */
    private boolean actualizandoCampos = false;

    /** Indica si el panel de fraccionamiento está habilitado o no. */
    private boolean fraccionarHabilitado = true;

    /** Rango Seleccionado de la lista de Rangos. */
    private RangoSerieNng rangoSeleccionado;

    /** Indica si la implementación es inmediata o programada. */
    private boolean implementacionInmediata = true;

    /** Liberacion Solicitada seleccionada en la tabla de liberaciones. */
    private LiberacionSolicitadaNng libSolSeleccionada;

    /** Tipo de Periodo de Reserva (Días / Meses). */
    private String tipoPeriodoReserva;

    /** Perido de reserva de la numeración. */
    private int periodoReserva = 0;

    /** Fecha de Finalización del Perido de Reserva. */
    private Date fechaFinReserva;

    /** Mensaje de información para liberación de rangos con arrendamiento. */
    private String rangoArrendadoMsg = "";

    /** Gestor de Selección Múltiple sobre la tabla Lazy. */
    private MultiSelectionOnLazyModelManager<RangoSerieNng> multiSelectionManager;

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'Liberaciones'.
     * @param pWizard Referencia a la clase Wizard que instancia este Tab
     * @param pNngFacade Facade de Servicios de Numeración No Geográfica.
     */
    public LiberacionesNngTab(Wizard pWizard, INumeracionNoGeograficaFacade pNngFacade) {
        // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
        setWizard(pWizard);

        // Asociamos la solicitud que usaremos en todo el Wizard. Es posible que
        // la referencia del Wizzard padre cambie en función de los pasos previos,
        // por eso hay que actualizar la referencia al llegar a éste tab usando
        // el método actualizaCampos();
        solicitud = (SolicitudLiberacionNng) getWizard().getSolicitud();

        // Asociamos el Servicio
        nngFacade = pNngFacade;
    }

    @Override
    public void cargaValoresIniciales() {
        try {
            // Inicializaciones para cargar los campos de Faces sin errores
            listaLiberaciones = new ArrayList<>();
            listaRangosFracciones = new ArrayList<>();
            listaRangoFraccionados = new ArrayList<>();
            fechaImplementacion = new Date();
            fraccionesByRango = new HashMap<>(1);
            tipoPeriodoReserva = "0"; // Días
            fechaFinReserva = new Date();
            multiSelectionManager = new MultiSelectionOnLazyModelManager<>();

            // Lista de claves de servicio activas.
            listaClavesServicio = nngFacade.findAllClaveServicioActivas();
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            this.setSummaryError(Errores.getDescripcionError(Errores.ERROR_0004));
        }
    }

    @Override
    public void resetTab() {
        if (this.isIniciado()) {
            this.limpiarBusqueda();
            this.seleccionUsarFraccionamiento();
            listaLiberaciones.clear();
            implementacionInmediata = true;
            fechaImplementacion = new Date();
            salvarHabilitado = false;
            usarFraccionamiento = false;
            listaRangosFracciones.clear();
            listaRangoFraccionados.clear();
            fraccionesByRango.clear();
            periodoReserva = 0;
            tipoPeriodoReserva = "0"; // Días
            fechaFinReserva = new Date();
        }
    }

    /** Método invocado al pulsar el botón 'buscar' sobre el buscador de Series. */
    public void realizarBusqueda() {
        try {
            // Cargamos el modelo y filtros bajo petición
            if (rangoSerieModel == null) {
                // Filtros de búsqueda. Se asocia con el modelo de datos.
                filtros = new FiltroBusquedaRangos();

                // Asociamos la clase que contendrá los filtros en cada búsqueda
                rangoSerieModel = new RangoSerieNngLazyModel();
                rangoSerieModel.setFiltros(filtros);
                rangoSerieModel.setFacadeServicios(nngFacade);
                rangoSerieModel.setMultiSelectionManager(multiSelectionManager);
            }

            // Reiniciamos los filtros
            filtros.clear();
            rangoSerieModel.clear();

            // Buscamos siempre por el Asignatario que es el poseedor del rango.
            filtros.setAsignatario(solicitud.getProveedorSolicitante());

            // Info de la Serie
            filtros.setClaveServicio(claveServicio);
            if (!StringUtils.isEmpty(sna)) {
                filtros.setIdSna(new BigDecimal(sna));
            }

            // Limpiamos la selección previa
            multiSelectionManager.clear();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar el botón 'limpiar' sobre el buscador de solicitudes. */
    public void limpiarBusqueda() {
        claveServicio = null;
        sna = null;
        rangoSerieModel = null;
        multiSelectionManager.clear();
        rangoSeleccionado = null;
    }

    /**
     * Método invocado al pulsar el botón 'Agregar'.
     * @param pCheckArrendamiento True para comprobar si existe arrendamiento sobre el rango seleccionado.
     */
    public void agregarLiberacion(boolean pCheckArrendamiento) {
        try {
            // Validación de rango arrendado.
            boolean todoOk = !(pCheckArrendamiento && validarArrendamiento());

            // todoOk es false si no se quiere comprobar el arrendamiento de un rango (por que ya se
            // haya aceptado la modal de aviso) o por que no exista arrendamiento en el rango.
            if (todoOk) {
                if (multiSelectionManager.isEmpty()) {
                    MensajesBean.addErrorMsg(MSG_ID, "Es necesario seleccionar un rango o serie.", "");
                    todoOk = false;
                }

                if (usarFraccionamiento && listaRangoFraccionados.isEmpty()) {
                    MensajesBean.addErrorMsg(MSG_ID, "Es necesario agregar almenos una fracción sobre un rango.", "");
                    todoOk = false;
                }

                if (todoOk) {
                    if (usarFraccionamiento) {
                        this.liberarFracciones();
                        this.seleccionUsarFraccionamiento();
                        this.limpiarPeriodoReserva();
                    } else {
                        if (validarLiberacionExistente()) {
                            this.liberarRangosSeleccionados();
                            this.limpiarPeriodoReserva();
                        }
                    }
                }

                // Habilitamos el botón de guardar siempre y cuando haya liberaciones solicitadas.
                salvarHabilitado = (!listaLiberaciones.isEmpty());
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Agrega un rango para su fraccionamiento. */
    public void agregarFraccionamiento() {
        try {
            if (rangoSeleccionado != null) {
                // Solo es posible liberar Rangos libres
                if (rangoSeleccionado.getEstatus().getCodigo().equals(EstadoRango.ASIGNADO)) {
                    // Comprobamos que no exista ya la cesión solicitada
                    if (validarLiberacionExistente()) {
                        listaRangosFracciones.add(rangoSeleccionado);
                        rangoAFraccionar = rangoSeleccionado;
                    }
                } else {
                    MensajesBean.addErrorMsg(MSG_ID, "El rango no esta disponible, estado: "
                            + rangoSeleccionado.getEstatus().getDescripcion(), "");
                }

            } else {
                MensajesBean.addErrorMsg(MSG_ID, "Es necesario seleccionar un rango.", "");
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Agrega fracciones de rangos para su Liberación.
     * @throws Exception En caso de error
     */
    private void liberarFracciones() throws Exception {
        // Fracciones de la lista "Rangos Fraccionados" sobre un mismo rango.
        for (RangoSerieNng fraccion : listaRangoFraccionados) {
            // Ignoramos los huecos temporales y agregamos las peticiones fracción
            if (fraccion.getEstatus().getCodigo().equals(EstadoRango.AFECTADO)) {
                RangoSerieNng rangoOriginal =
                        (RangoSerieNng) RangosSeriesUtil.getRangoInicial(fraccion, listaRangosFracciones);
                LiberacionSolicitadaNng ls = getLiberacionSolicitadaFromRango(rangoOriginal);
                ls.setNumInicio(fraccion.getNumInicio());
                ls.setNumFinal(fraccion.getNumFinal());
                listaLiberaciones.add(ls);
            }
        }
    }

    /**
     * Agrega los rangos seleccionados para su liberación sin fraccionamiento.
     * @throws Exception En caso de error.
     */
    private void liberarRangosSeleccionados() throws Exception {
        if (validarLiberacionSinFraccionamiento()) {
            for (RangoSerieNng rango : multiSelectionManager.getRegistrosSeleccionados()) {
                LiberacionSolicitadaNng ls = getLiberacionSolicitadaFromRango(rango);
                listaLiberaciones.add(ls);
            }
        }
    }

    /** Método invocado al modificar los campos de texto 'Cantidad' y 'Num.Incial'. */
    public void calcularFinalRango() {
        try {
            // Los validadores de TXT_Cantidad y TXT_NumInicial en Client-Side nos aseguran valores correctos.
            int cantidad = (Integer.valueOf(this.cantidad));
            int inicio = (Integer.valueOf(numInicial));
            numFinal = (cantidad + inicio) - 1; // Restamos siempre 1 por que el cero cuenta
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            cantidad = "1";
            numFinal = 0;
        }
    }

    /** Método invocado al pulsar sobre el botón 'Eliminar' en la tabla de Liberaciones seleccionadas. */
    public void eliminarLiberacion() {
        try {
            PeticionCancelacion checkCancelacion = nngFacade.cancelLiberacion(libSolSeleccionada, true);
            if (checkCancelacion.isCancelacionPosible()) {
                listaLiberaciones.remove(libSolSeleccionada);
                this.guardarCambios();
            } else {
                MensajesBean.addErrorMsg(MSG_ID, checkCancelacion.getMensajeError(), "");
            }

            // Habilitamos el botón de guardar siempre y cuando haya liberaciones solicitadas.
            salvarHabilitado = (!listaLiberaciones.isEmpty());

        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar sobre el botón 'Eliminar' en la tabla de Rangos a fraccionar. */
    public void eliminarRangoAFraccionar() {
        try {
            if (rangoAFraccionar != null) {
                if (listaRangosFracciones.contains(rangoAFraccionar)) {
                    // Eliminamos el rango de la tabla
                    listaRangosFracciones.remove(rangoAFraccionar);

                    // Eliminamos las fracciones asociadas al rango
                    String key = rangoAFraccionar.getIdentificadorRango();
                    if (fraccionesByRango.containsKey(key)) {
                        fraccionesByRango.get(key).clear();
                    }
                    listaRangoFraccionados.clear();
                    rangoAFraccionar = null;
                }
            } else {
                MensajesBean.addErrorMsg(MSG_ID, "Seleccione un rango para eliminar", "");
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al cambiar el checkbox de 'Fraccionar'. */
    public void seleccionUsarFraccionamiento() {
        cantidad = "1";
        numInicial = "0";
        numFinal = 0;
    }

    /** Resetea los campos del periodo de reserva. */
    private void limpiarPeriodoReserva() {
        periodoReserva = 0;
        this.actualzarFechaFinReserva();
    }

    /**
     * Método invocado al seleccionar un rango a fraccionar en la tabla de rangos para fraccionar.
     * @param event SelectEvent
     */
    public void seleccionRangoAFraccionar(SelectEvent event) {
        try {
            String key = rangoAFraccionar.getIdentificadorRango();
            if (!fraccionesByRango.containsKey(key)) {
                fraccionesByRango.put(key, new ArrayList<IRangoSerie>(1));
            }

            listaRangoFraccionados = ajustarFraccionesRango(rangoAFraccionar);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Guarda los cambios realizados hasta el momento en el trámite.
     * @return 'True' si se ha podido guardar correctamente.
     */
    private boolean guardarCambios() {

        // Lista de Liberaciones Solicitadas a eliminar permanentemente
        ArrayList<LiberacionSolicitadaNng> libSolEliminar = new ArrayList<LiberacionSolicitadaNng>();

        try {
            // Eliminamos las liberaciones que se han deseleccionado
            for (LiberacionSolicitadaNng libsol : solicitud.getLiberacionesSolicitadas()) {
                // Ignoramos las liberaciones que ya se hayan ejecutado
                if (libsol.getEstatus().getCodigo().equals(EstadoLiberacionSolicitada.PENDIENTE)) {
                    if ((!arrayContains(listaLiberaciones, libsol))) {
                        // No podemos eliminar una liberación solicitada de la lista mientras estamos
                        // recorriendo la misma lista (ConcurrentModificationException).
                        // Por lo tanto la añadimos a otra lista para eliminarla al salir del bucle.
                        libSolEliminar.add(libsol);
                    }
                }
            }

            // Eliminamos de la solicitud las LiberacionesSolicitadas que se han eliminado de la tabla de Liberaciones.
            for (LiberacionSolicitadaNng libsol : libSolEliminar) {
                // Al tener marcadas las liberacionesSolicitadas como @PrivateOwned en JPA se eliminan automáticamente
                // cuando se guardan los cambios en la solicitud ya que indica que nadie más las utiliza.
                solicitud.removeLiberacionSolicitada(libsol);
            }

            // Agregamos las liberaciones que se han añadido
            for (LiberacionSolicitadaNng libSol : listaLiberaciones) {
                if (!arrayContains(solicitud.getLiberacionesSolicitadas(), libSol)) {
                    solicitud.addLiberacionSolicitada(libSol);
                }
            }

            // Guardamos los cambios y generamos los nuevos registros
            solicitud = nngFacade.saveSolicitudLiberacion(solicitud);

            // Actualizamos la solicitud para todos los tabs
            getWizard().setSolicitud(solicitud);

            // Una vez persistidas las LiberacionesSolicitadas rehacemos la tabla de liberaciones para que todas
            // tengan el id ya generado
            listaLiberaciones.clear();
            listaLiberaciones.addAll(solicitud.getLiberacionesSolicitadas());

            return true;

        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
        return false;
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

    /** Método invocado al pulsar el botón 'Fraccionar'. */
    public void aplicarFraccionamiento() {
        try {
            if (rangoAFraccionar != null) {
                if (numFinal != 0) {
                    RangoSerieNng fraccion = new RangoSerieNng();
                    fraccion.setNumInicio(StringUtils.leftPad(String.valueOf(numInicial), 4, '0'));
                    fraccion.setNumFinal(StringUtils.leftPad(String.valueOf(numFinal), 4, '0'));
                    fraccion.setSerie(rangoAFraccionar.getSerie());
                    fraccion.setId(rangoAFraccionar.getId());

                    // Comprobamos que la numeración esté dentro del rango
                    if ((Integer.valueOf(numInicial).intValue() >= rangoAFraccionar.getNumInicioAsInt())
                            && (numFinal <= rangoAFraccionar.getNumFinalAsInt())) {

                        // Comprobamos que no se intente hacer una fracción con toda la franja del rango
                        if ((Integer.valueOf(numInicial).intValue() == rangoAFraccionar.getNumInicioAsInt())
                                && (numFinal == rangoAFraccionar.getNumFinalAsInt())) {
                            MensajesBean.addErrorMsg(MSG_ID, "No se puede realizar una fracción de "
                                    + "la totalidad del rango.", "");

                        } else {
                            // Comprobamos que la franja no este ocupada ya por otra fracción del mismo rango
                            if (RangosSeriesUtil.fraccionRangoOcupado(fraccion, fraccionesByRango)) {
                                MensajesBean.addErrorMsg(MSG_ID, "La numeración a liberar"
                                        + " ya existe en otra fracción.", "");
                            } else {

                                // Si estamos actualizando campos no es necesario volver a cargar la fracción.
                                RangoSerieNng rangoActualizado = null;
                                if (!actualizandoCampos) {
                                    // Volvemos a comprobar el estado del rango ya que no se modifica hasta que
                                    // no se agrega un fraccionamiento y mientras tanto ha podido ser reservado
                                    // por otro agente.
                                    rangoActualizado = nngFacade.getRangoSerie(
                                            rangoAFraccionar.getId().getIdClaveServicio(),
                                            rangoAFraccionar.getId().getSna(),
                                            rangoAFraccionar.getNumInicio(),
                                            rangoAFraccionar.getAsignatario());
                                } else {
                                    // Si estamos actualizando campos el Rango ya está actualizado.
                                    rangoActualizado = rangoAFraccionar;
                                }

                                // Si estamos actualizando campos los rangos sí estarán reservados, pero se trata
                                // solo de volver a pintarlos en la tabla. Si no, se trata de un fraccionamiento
                                // nuevo que hay que validar.
                                if (rangoActualizado.getEstatus().getCodigo().equals(EstadoRango.ASIGNADO)
                                        || actualizandoCampos) {

                                    // Clonamos el rango original para mantener los mismos valores.
                                    fraccion = (RangoSerieNng) rangoAFraccionar.clone();

                                    // Usamos el mismo id para que nos dé la misma Key en la hashmap de fracciones.
                                    fraccion.getId().setId(rangoAFraccionar.getId().getId());

                                    // Al clonar hemos vuelto a los valores inicial y final del origianl, los rehacemos.
                                    fraccion.setNumInicio(StringUtils.leftPad(String.valueOf(numInicial), 4, '0'));
                                    fraccion.setNumFinal(StringUtils.leftPad(String.valueOf(numFinal), 4, '0'));
                                    fraccion.setEstatus(nngFacade.getEstadoRangoByCodigo(EstadoRango.AFECTADO));
                                    fraccion.setSolicitud(rangoAFraccionar.getSolicitud());

                                    // Agregamos la fracción por su serie
                                    String key = fraccion.getIdentificadorRango();
                                    if (!fraccionesByRango.containsKey(key)) {
                                        fraccionesByRango.put(key, new ArrayList<IRangoSerie>(1));
                                    }
                                    fraccionesByRango.get(key).add(fraccion);

                                    // Organizamos las fracciones de la tabla.
                                    listaRangoFraccionados = ajustarFraccionesRango(rangoAFraccionar);
                                } else {
                                    MensajesBean.addErrorMsg(MSG_ID, "El rango no esta disponible, estado: "
                                            + rangoActualizado.getEstatus().getDescripcion(), "");
                                }
                            }
                        }
                    } else {
                        MensajesBean.addErrorMsg(MSG_ID, "La numeración introducida "
                                + "no corresponde a la franja del rango.", "");
                    }
                } else {
                    MensajesBean.addErrorMsg(MSG_ID, "Introduzca la numeración a fraccionar", "");
                }
            } else {
                MensajesBean.addErrorMsg(MSG_ID, "Seleccione un rango para fraccionar", "");
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    @Override
    public boolean isAvanzar() {
        boolean resultado = true;
        if (this.isTabHabilitado()) {
            // Continuamos siempre y cuando haya liberaciones por procesar
            if (listaLiberaciones.isEmpty()) {
                MensajesBean.addErrorMsg(MSG_ID, MensajesBean.getTextoResource("liberacion.almenosUna"), "");
                resultado = false;
            } else {
                resultado = (this.guardarCambios() && this.aplicarLiberaciones());
            }
        } else {
            // El tab no se puede editar, permitimos pasar directamente a la siguiente
            // pestaña.
            resultado = true;
        }

        if (!resultado) {
            // Al no permitirse avanzar se muestra en el Wizard el mensaje indicado por el TabWizard
            this.setSummaryError(MensajesBean.getTextoResource("error.avanzar"));
        }
        return resultado;
    }

    @Override
    public void actualizaCampos() {
        try {
            // Limpiamos los datos de búsqueda por si se ha retrocedido
            // desde la página de liberación y se vuelve a ella.
            this.limpiarBusqueda();

            // La solicitud del Wizard ha cambiado de instancia desde que se generó en
            // el constructor. Es necesario actualizar la referecnia en el tab.
            solicitud = (SolicitudLiberacionNng) getWizard().getSolicitud();

            // Mientras una solicitud no esté cancelada sigue siendo editable.
            if (!solicitud.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_CANCELADA)) {
                // Estamos en modo actualización. Evita algunas validaciones.
                actualizandoCampos = true;

                // Actualizamos las solicitudes pendientes
                listaLiberaciones.clear();
                if (solicitud.getLiberacionesSolicitadas() != null) {
                    listaLiberaciones.addAll(solicitud.getLiberacionesSolicitadas());
                }

                // Actualizamos las tablas
                listaRangosFracciones.clear();
                listaRangoFraccionados.clear();
                fraccionesByRango.clear();

                List<String> avisosRangosCambiados = new ArrayList<String>();
                boolean existeRango = true;

                for (LiberacionSolicitadaNng libSol : listaLiberaciones) {
                    existeRango = true;
                    if (libSol.getEstatus().getCodigo().equals(EstadoLiberacionSolicitada.PENDIENTE)) {
                        if (libSol.getFraccionamientoRango().equals("N")) {
                            RangoSerieNng rangoOriginal = nngFacade.getRangoSerie(
                                    libSol.getIdClaveServicio(),
                                    libSol.getSna(),
                                    libSol.getNumInicio(),
                                    libSol.getProveedorCesionario());
                            existeRango = (rangoOriginal != null);
                        } else {
                            // Rango seleccionado para fraccionar
                            RangoSerieNng rangoOriginal = nngFacade.getRangoSerieByFraccion(
                                    libSol.getIdClaveServicio(),
                                    libSol.getSna(),
                                    libSol.getNumInicio(),
                                    libSol.getNumFinal(),
                                    libSol.getProveedorCesionario());

                            existeRango = (rangoOriginal != null);
                            if (existeRango) {
                                // Añadimos el rango a la lista de rangos fraccionados
                                if (!RangosSeriesUtil.arrayContains(listaRangosFracciones, rangoOriginal)) {
                                    listaRangosFracciones.add(rangoOriginal);
                                }

                                // Agregamos las fracciones a la tabla de fracciones.
                                this.rangoAFraccionar = rangoOriginal;
                                this.numInicial = libSol.getNumInicio();
                                this.numFinal = libSol.getNumFinalAsInt();
                                this.aplicarFraccionamiento();
                            }
                        }
                    }

                    if (!existeRango) {
                        StringBuffer sbAviso = new StringBuffer();
                        sbAviso.append("Asignatario: ").append(libSol.getProveedorCesionario().getNombreCorto());
                        sbAviso.append(", Clave Serv.: ").append(libSol.getIdClaveServicio());
                        sbAviso.append(", Sna: ").append(libSol.getSna());
                        sbAviso.append(", Inicio: ").append(libSol.getNumInicio());
                        sbAviso.append(", Final: ").append(libSol.getNumFinal()).append("<br>");
                        avisosRangosCambiados.add(sbAviso.toString());
                    }
                }

                // Habilitamos el botón de guardar siempre y cuando haya liberaciones solicitadas.
                salvarHabilitado = (!listaLiberaciones.isEmpty());

                // Mostramos las tablas de Fraccionamiento si existen fracciones pendientes
                usarFraccionamiento = (!listaRangosFracciones.isEmpty());

                // Reiniciamos los campos de cantidad de numeración
                this.rangoAFraccionar = null;
                this.numInicial = "0";
                this.numFinal = 0;

                // Avisos de rangos que ya no existen y no se pueden ceder
                if (!avisosRangosCambiados.isEmpty()) {
                    StringBuffer sbAviso = new StringBuffer();
                    sbAviso.append("Los siguientes rangos han sido modificados o desasignados:<br>");
                    for (String aviso : avisosRangosCambiados) {
                        sbAviso.append(aviso);
                    }
                    MensajesBean.addErrorMsg(MSG_ID, sbAviso.toString(), "");
                }

            } else {
                // Para Canceladas mostramos la información de las Liberaciones realizadas
                listaLiberaciones.clear();
                listaLiberaciones.addAll(solicitud.getLiberacionesSolicitadas());
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        } finally {
            actualizandoCampos = false;
        }
    }

    /**
     * Método invocado al seleccionar todas las filas de la tabla mediante el checkbox de cabecera.
     * @param event ToggleSelectEvent
     */
    public void seleccionPagina(ToggleSelectEvent event) {
        try {
            // Actualizamos la selección múltiple en paginación
            int pagina = ((DataTable) event.getSource()).getPage();
            multiSelectionManager.toggleSelecction(pagina, event.isSelected());

            // Fraccionamiento habilitado solamente si no hay multiselección.
            fraccionarHabilitado = !(multiSelectionManager.size() > 1);
            usarFraccionamiento = (usarFraccionamiento && fraccionarHabilitado);

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al seleccionar una fila de la tabla de Rangos.
     * @param event SelectEvent
     */
    public void seleccionRango(SelectEvent event) {
        try {
            // Actualizamos la selección múltiple en paginación
            rangoSeleccionado = (RangoSerieNng) event.getObject();
            int pagina = ((DataTable) event.getSource()).getPage();
            multiSelectionManager.updateSelection(rangoSeleccionado, pagina, true);

            // Fraccionamiento habilitado solamente si no hay multiselección.
            fraccionarHabilitado = !(multiSelectionManager.size() > 1);
            usarFraccionamiento = (usarFraccionamiento && fraccionarHabilitado);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al deseleccionar una fila de la tabla de Rangos.
     * @param event SelectEvent
     */
    public void deSeleccionRango(UnselectEvent event) {
        try {
            // Actualizamos la selección múltiple en paginación
            rangoSeleccionado = (RangoSerieNng) event.getObject();
            int pagina = ((DataTable) event.getSource()).getPage();
            multiSelectionManager.updateSelection(rangoSeleccionado, pagina, false);

            // Fraccionamiento habilitado solamente si no hay multiselección.
            fraccionarHabilitado = !(multiSelectionManager.size() > 1);
            usarFraccionamiento = (usarFraccionamiento && fraccionarHabilitado);

            // Devuelve el último registro seleccionado o null
            rangoSeleccionado = multiSelectionManager.getLastRegisterSelected();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar sobre fecha de implementación. */
    public void cambiarFechaImplementacion() {
        if (implementacionInmediata) {
            // La fecha de implementación inmediata siempre es el día actual.
            fechaImplementacion = FechasUtils.getFechaHoy();
        } else {
            // La fecha de implementación programada siempre es a partir de mañana.
            fechaImplementacion = FechasUtils.calculaFecha(1, 0, 0);
        }
        // Actualizamos las fechas del Periodo de Reserva.
        this.actualzarFechaFinReserva();
    }

    /** Valida la fecha de Implementación. */
    public void validarFechaImpl() {
        try {
            // La fecha de implementación programada siempre es a partir de mañana.
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 1);

            if (fechaImplementacion == null) {
                fechaImplementacion = FechasUtils.calculaFecha(1, 0, 0);
            } else {
                if (fechaImplementacion.before(calendar.getTime())) {
                    MensajesBean.addErrorMsg(MSG_ID,
                            "La fecha de implementación programada no puede ser igual o inferior al día actual.", "");
                    fechaImplementacion = calendar.getTime();
                }
            }
            // Actualizamos las fechas del Periodo de Reserva.
            this.actualzarFechaFinReserva();
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Aplica las liberaciones seleccionadas por el usuario.
     * @return True si se han aplicado correctamente las liberaciones.
     */
    private boolean aplicarLiberaciones() {
        try {
            if (validadRangosALiberar()) {
                // Aplicamos la liberaciones solicitadas y reserva de rangos si es necesario.
                SolicitudLiberacionNng solLib = nngFacade.applyLiberacionesSolicitadas(solicitud);

                // Asociamos la nueva instancia de solicitud al Wizard
                this.getWizard().setSolicitud(solLib);

                // Continuamos a la siguiente pestaña
                return true;
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }

        return false;
    }

    /**
     * Organiza las fracciones hechas sobre un rango.
     * @param pRango Rango original fraccionado
     * @return Lista de fracciones sobre el rango original
     * @throws Exception en caso de error.
     */
    private List<RangoSerieNng> ajustarFraccionesRango(RangoSerieNng pRango) throws Exception {

        // Lista de fracciones sobre un mismo rango
        List<IRangoSerie> fraccionesSerie = fraccionesByRango.get(pRango.getIdentificadorRango());

        // Estatus Asignado para los rangos Ficticios
        EstadoRango statusAsignado = nngFacade.getEstadoRangoByCodigo(EstadoRango.ASIGNADO);

        if (!fraccionesSerie.isEmpty()) {
            List<IRangoSerie> fraccionesIRango = RangosSeriesUtil.getFracciones(
                    fraccionesSerie, pRango, statusAsignado);

            List<RangoSerieNng> fracciones = RangosSeriesUtil.castListaRangoSerieNNG(fraccionesIRango);

            // Añadimos id's ficticios para poder hacer selección en las tablas
            int counter = 0;
            for (RangoSerieNng rango : fracciones) {
                rango.getId().setId(new BigDecimal(counter));
                counter++;
            }
            return fracciones;
        } else {
            return new ArrayList<RangoSerieNng>(1);
        }
    }

    /**
     * Realiza validaciones para comprobar que se puede agregar el rango seleccionado para liberación sin
     * fraccionamiento.
     * @return True si se puede agregar el rango a la tabla rangos a liberar.
     * @throws Exception en caso de error.
     */
    private boolean validarLiberacionSinFraccionamiento() throws Exception {
        // Comprobamos que los rangos seleccionados no estén ya seleccionados
        // para fraccionamiento.
        ArrayList<RangoSerieNng> rangosReservados = new ArrayList<RangoSerieNng>();
        List<RangoSerieNng> fracciones = RangosSeriesUtil.castListaRangoSerieNNG(listaRangosFracciones);
        for (RangoSerieNng rangoEnFracciones : fracciones) {
            for (RangoSerieNng rangoSeleccionado : multiSelectionManager.getRegistrosSeleccionados()) {
                if (rangoEnFracciones.getId().getId().intValue() == rangoSeleccionado.getId().getId().intValue()) {
                    rangosReservados.add(rangoSeleccionado);
                }
            }
        }

        if (!rangosReservados.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            sb.append("No es posible agregar los rangos seleccionados. ");
            sb.append("Lo siguientes rangos tienen fracciones aplicadas:<br>");
            for (RangoSerieNng rango : rangosReservados) {
                sb.append("Serie: ").append(rango.getId().getSna()).append(", Num.Inicio: ");
                sb.append(rango.getNumInicio()).append(", Estatus: ").append(rango.getEstatus().getDescripcion());
                sb.append("<br>");
            }
            MensajesBean.addErrorMsg(MSG_ID, sb.toString(), "");
            return false;
        }

        // Al ceder una serie completa es necesario que todos los rangos de la serie
        // que pertenecen al proveedor esten en estado 'ASIGNADO'
        rangosReservados = new ArrayList<RangoSerieNng>();
        for (RangoSerieNng rango : multiSelectionManager.getRegistrosSeleccionados()) {
            if (!rango.getEstatus().getCodigo().equals(EstadoRango.ASIGNADO)) {
                rangosReservados.add(rango);
            }
        }

        // Solo es posible ceder rangos libres
        if (!rangosReservados.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            sb.append("No es posible agregar los rangos seleccionados. ");
            sb.append("Lo siguientes rangos no están disponibles:<br>");
            for (RangoSerieNng rango : rangosReservados) {
                sb.append("Serie: ").append(rango.getId().getSna()).append(", Num.Inicio: ");
                sb.append(rango.getNumInicio()).append(", Estatus: ");
                sb.append(rango.getEstatus().getDescripcion());
                sb.append("<br>");
            }
            MensajesBean.addErrorMsg(MSG_ID, sb.toString(), "");
            return false;
        }

        return true;
    }

    /**
     * Comprueba que los rangos seleccionados para liberar existen y tienen estatus disponible.
     * @return True si se pueden aplicar las liberaciones.
     * @throws Exception En caso de error.
     */
    private boolean validadRangosALiberar() throws Exception {

        // Antes de aplicar las liberaciones comprobamos que todos los rangos sigan
        // con status 'Asignado'
        List<String> avisosDisponibilidad = new ArrayList<String>();
        List<String> avisosEstatus = new ArrayList<String>();
        boolean existeRango = true;
        boolean statusAsignado = true;
        String status = null;

        for (LiberacionSolicitadaNng libSol : solicitud.getLiberacionesSolicitadas()) {
            existeRango = true;
            statusAsignado = true;

            if (libSol.getEstatus().getCodigo().equals(EstadoLiberacionSolicitada.PENDIENTE)) {
                if (libSol.getFraccionamientoRango().equals("N")) {
                    // Rango seleccionado para ceder completo
                    RangoSerieNng rango = nngFacade.getRangoSerie(
                            libSol.getIdClaveServicio(), libSol.getSna(), libSol.getNumInicio(),
                            libSol.getProveedorCesionario());

                    if (rango != null) {
                        if (!rango.getEstatus().getCodigo().equals(EstadoRango.ASIGNADO)) {
                            // Si esta Reservado puede ser de ésta misma solicitud por ser una implementación
                            // no inmediata
                            if (rango.getEstatus().getCodigo().equals(EstadoRango.RESERVADO)) {
                                if (rango.getSolicitud().getId().intValue() != solicitud.getId().intValue()) {
                                    statusAsignado = false;
                                    status = rango.getEstatus().getDescripcion();
                                }
                            } else {
                                statusAsignado = false;
                                status = rango.getEstatus().getDescripcion();
                            }
                        }
                    } else {
                        existeRango = false;
                    }
                } else {
                    // Rango seleccionado para fraccionar
                    RangoSerieNng rangoOriginal = nngFacade.getRangoSerieByFraccion(
                            libSol.getIdClaveServicio(), libSol.getSna(), libSol.getNumInicio(),
                            libSol.getNumFinal(), libSol.getProveedorCesionario());

                    if (rangoOriginal != null) {
                        if (!rangoOriginal.getEstatus().getCodigo().equals(EstadoRango.ASIGNADO)) {
                            // Si esta Reservado puede ser de ésta misma solicitud por ser una implementación
                            // no inmediata
                            if (rangoOriginal.getEstatus().getCodigo().equals(EstadoRango.RESERVADO)) {
                                if (rangoOriginal.getSolicitud().getId().intValue() != solicitud.getId().intValue()) {
                                    statusAsignado = false;
                                    status = rangoOriginal.getEstatus().getDescripcion();
                                }
                            } else {
                                statusAsignado = false;
                                status = rangoOriginal.getEstatus().getDescripcion();
                            }
                        }
                    } else {
                        existeRango = false;
                    }
                }
            }

            if (!existeRango) {
                StringBuffer sbAviso = new StringBuffer();
                sbAviso.append("Asignatario: ").append(libSol.getProveedorCesionario().getNombreCorto());
                sbAviso.append(", Clave Serv.: ").append(libSol.getIdClaveServicio());
                sbAviso.append(", Sna: ").append(libSol.getSna());
                sbAviso.append(", Inicio: ").append(libSol.getNumInicio());
                sbAviso.append(", Final: ").append(libSol.getNumFinal()).append("<br>");
                avisosDisponibilidad.add(sbAviso.toString());
            }

            if (!statusAsignado) {
                StringBuffer sbAviso = new StringBuffer();
                sbAviso.append("Asignatario: ").append(libSol.getProveedorCesionario().getNombreCorto());
                sbAviso.append(", Clave Serv.: ").append(libSol.getIdClaveServicio());
                sbAviso.append(", Sna: ").append(libSol.getSna());
                sbAviso.append(", Inicio: ").append(libSol.getNumInicio());
                sbAviso.append(", Final: ").append(libSol.getNumFinal());
                sbAviso.append(", Estatus: ").append(status).append("<br>");
                avisosEstatus.add(sbAviso.toString());
            }
        }

        // Avisos de rangos que ya no existen y no se pueden ceder
        if (!avisosDisponibilidad.isEmpty()) {
            StringBuffer sbAviso = new StringBuffer();
            sbAviso.append("Los siguientes rangos han sido modificados o desasignados:<br>");
            for (String aviso : avisosDisponibilidad) {
                sbAviso.append(aviso);
            }
            MensajesBean.addErrorMsg(MSG_ID, sbAviso.toString(), "");
        }

        // Avisos de rangos que ya no tienen estatus 'Asignado'
        if (!avisosEstatus.isEmpty()) {
            StringBuffer sbAviso = new StringBuffer();
            sbAviso.append("Los siguientes rangos han cambiado de estatus:<br>");
            for (String aviso : avisosEstatus) {
                sbAviso.append(aviso);
            }
            MensajesBean.addErrorMsg(MSG_ID, sbAviso.toString(), "");
        }

        // Si no hay avisos, continuamos con la Cesión
        return (avisosEstatus.isEmpty() && avisosDisponibilidad.isEmpty());
    }

    /**
     * Comprueba que el rango seleccionado para agregar como liberación (fraccionada o no) no exista ya en la tabla de
     * liberaciones solicitadas.
     * @return True si se puede agregar el rango para liberación
     * @throws Exception en caso de error
     */
    private boolean validarLiberacionExistente() throws Exception {
        List<RangoSerieNng> rangosEnLiberacion = new ArrayList<RangoSerieNng>();
        if (usarFraccionamiento) {
            LiberacionSolicitadaNng libSolTemp = this.getLiberacionSolicitadaFromRango(rangoSeleccionado);
            if (arrayContains(listaLiberaciones, libSolTemp)) {
                rangosEnLiberacion.add(rangoSeleccionado);
            }
        } else {
            for (RangoSerieNng rango : multiSelectionManager.getRegistrosSeleccionados()) {
                LiberacionSolicitadaNng libSolTemp = this.getLiberacionSolicitadaFromRango(rango);
                if (arrayContains(listaLiberaciones, libSolTemp)) {
                    rangosEnLiberacion.add(rango);
                }
            }
        }

        if (!rangosEnLiberacion.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            sb.append("No es posible agregar los rangos seleccionados. ");
            sb.append("Lo siguientes rangos ya existen para liberación:<br>");
            for (RangoSerieNng rango : rangosEnLiberacion) {
                sb.append("Clave Serv.: ").append(rango.getId().getIdClaveServicio());
                sb.append(", Sna: ").append(rango.getId().getSna());
                sb.append(", Inicio: ").append(rango.getNumInicio());
                sb.append(", Final: ").append(rango.getNumFinal()).append("<br>");
            }
            MensajesBean.addErrorMsg(MSG_ID, sb.toString(), "");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Crea un objeto LiberacionSolicitadaNng en base a la información de un Rango.
     * @param pRango Rango con la información de la liberación.
     * @return LiberacionSolicitadaNng
     * @throws Exception en caso de error.
     */
    private LiberacionSolicitadaNng getLiberacionSolicitadaFromRango(RangoSerieNng pRango) throws Exception {
        LiberacionSolicitadaNng ls = new LiberacionSolicitadaNng();

        // Liberacion Solicitada
        ls.setId(null); // Hasta que no se guarda no se obtiene un Id
        ls.setProveedorCesionario(pRango.getAsignatario());
        ls.setProveedorConcesionario(pRango.getConcesionario());
        ls.setProveedorArrendatario(pRango.getArrendatario());
        ls.setIdClaveServicio(pRango.getId().getIdClaveServicio());
        ls.setSna(pRango.getSerie().getId().getSna());
        ls.setNumInicio(pRango.getNumInicio().toString());
        ls.setNumFinal(pRango.getNumFinal().toString());
        ls.setSolicitudLiberacion(solicitud);
        ls.setAbc(pRango.getAbc());
        ls.setBcd(pRango.getBcd());
        ls.setCliente(pRango.getCliente());

        // Información para el Oficio.
        ls.setIda(solicitud.getProveedorSolicitante().getIda());

        // Información de la solicitud de asignación.
        ls.setNumOficioAsigRango(pRango.getOficioAsignacion());
        ls.setFechaAsignacion(pRango.getFechaAsignacion());
        ls.setConsecutivoAsignacion(pRango.getConsecutivoAsignacion());

        // Indicamos si se ha fraccionado la numeración con la liberación
        if (usarFraccionamiento) {
            ls.setFraccionamientoRango("S");
        } else {
            ls.setFraccionamientoRango("N");
        }

        // Periodo de Reserva
        ls.setFechaFinReserva(fechaFinReserva);
        StringBuilder sbReserva = new StringBuilder();
        sbReserva.append(periodoReserva).append(" ");
        if (tipoPeriodoReserva.equals("0")) {
            sbReserva.append("Día/s");
        } else {
            sbReserva.append("Mes/s");
        }
        ls.setPeriodoReserva(sbReserva.toString());

        // Fecha de Implementación
        ls.setFechaLiberacion(FechasUtils.parseFecha(fechaImplementacion));

        // Estado Reservado para su posterior liberación.
        EstadoLiberacionSolicitada els = new EstadoLiberacionSolicitada();
        els.setCodigo(EstadoLiberacionSolicitada.PENDIENTE);
        ls.setEstatus(els);

        return ls;
    }

    /**
     * Indica si una lista contiene una LiberacionSolicitadaNng sin usar los métos Equals de Object.
     * @param list Lista de objetos LiberacionSolicitadaNng
     * @param ls LiberacionSolicitadaNng a comparar
     * @return True si la LiberacionSolicitadaNng esta en la lista
     * @throws Exception en caso de error
     */
    private boolean arrayContains(List<LiberacionSolicitadaNng> list, LiberacionSolicitadaNng ls) throws Exception {

        // Cuando se guardan los cambios las LiberacionesSolicitadas tienen otra instancia diferente dada
        // por JPA y, por lo tanto, no se pueden comparar con las almacenadas en la lista interna. Es necesario
        // comparar por los campos clave para saber si nos estamos refiriendo a la misma.

        for (LiberacionSolicitadaNng libSol : list) {
            if (libSol.getIdClaveServicio().intValue() == ls.getIdClaveServicio().intValue()
                    && libSol.getSna().intValue() == ls.getSna().intValue()
                    && libSol.getNumInicio().equals(ls.getNumInicio())) {
                return true;
            }
        }
        return false;
    }

    /** Actualiza la fecha del fin del periodo de reserva. */
    public void actualzarFechaFinReserva() {
        if (periodoReserva == 0) {
            fechaFinReserva = fechaImplementacion;
        } else if (periodoReserva > 0) {
            if (tipoPeriodoReserva.equals("0")) {
                // Perido de reserva indicado en días.
                fechaFinReserva = FechasUtils.calculaFecha(fechaImplementacion, periodoReserva, 0, 0);
            } else {
                // Perido de reserva indicado en meses.
                fechaFinReserva = FechasUtils.calculaFecha(fechaImplementacion, 0, periodoReserva, 0);
            }
        } else {
            MensajesBean.addErrorMsg(MSG_ID, "El periodo de reserva indicado no es válido.", "");
            fechaFinReserva = fechaImplementacion;
            periodoReserva = 0;
        }
    }

    /**
     * Comprueba si el rango seleccionado esta arrendado.
     * @return True si el rango seleccionado tiene arrendador.
     */
    private boolean validarArrendamiento() {
        StringBuilder sbAviso = new StringBuilder();
        boolean rangosArrendados = false;
        for (RangoSerieNng rango : multiSelectionManager.getRegistrosSeleccionados()) {
            if (rango.getArrendatario() != null) {
                rangosArrendados = true;
                sbAviso.append("Clave Servicio: ").append(rango.getClaveServicio().getCodigo()).append(", ");
                sbAviso.append("Serie: ").append(rango.getSna()).append(", ");
                sbAviso.append("Número Inicial: ").append(rango.getNumInicio()).append(", ");
                sbAviso.append("Número Final: ").append(rango.getNumFinal()).append(", ");
                sbAviso.append("PST Arrendatario: ").append(rango.getArrendatario().getId()).append(", ");
                sbAviso.append("Nombre PST Arrendatario: ").append(rango.getArrendatario().getNombre()).append(", ");
                sbAviso.append("Consecutivo Asignación: ").append(rango.getSolicitud().getId()).append("<br><br>");
            }
        }

        if (rangosArrendados) {
            sbAviso.insert(0, "La asignación que desea liberar hace parte de una asignación con arrendamiento. "
                    + "Sus datos son:<br><br>");

            rangoArrendadoMsg = sbAviso.toString();

            // Mostramos la modal.
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('DLG_ArrendatarioLibNng').show()");
        }

        return rangosArrendados;
    }

    // GETTERS & SETTERS

    /**
     * Identificador de numeración.
     * @return Identificador
     */
    public String getSna() {
        return sna;
    }

    /**
     * Identificador de numeración.
     * @param sna Identificador
     */
    public void setSna(String sna) {
        this.sna = sna;
    }

    /**
     * Indica si se desea fraccionar el rango o serie seleccionado.
     * @return True para fraccionar
     */
    public boolean isUsarFraccionamiento() {
        return usarFraccionamiento;
    }

    /**
     * Indica si se desea fraccionar el rango o serie seleccionado.
     * @param usarFraccionamiento True para fraccionar
     */
    public void setUsarFraccionamiento(boolean usarFraccionamiento) {
        this.usarFraccionamiento = usarFraccionamiento;
    }

    /**
     * Fecha de implementación de la liberación.
     * @return Fecha
     */
    public Date getFechaImplementacion() {
        return fechaImplementacion;
    }

    /**
     * Fecha de implementación de la liberación.
     * @param fechaImplementacion Fecha
     */
    public void setFechaImplementacion(Date fechaImplementacion) {
        this.fechaImplementacion = fechaImplementacion;
    }

    /**
     * Habilita o deshabilita el botón de 'Guardar Cambios'.
     * @return True para habilitar el botón
     */
    public boolean isSalvarHabilitado() {
        return salvarHabilitado;
    }

    /**
     * Habilita o deshabilita el botón de 'Guardar Cambios'.
     * @param salvarHabilitado True para habilitar el botón
     */
    public void setSalvarHabilitado(boolean salvarHabilitado) {
        this.salvarHabilitado = salvarHabilitado;
    }

    /**
     * Indica la cantidad de rango que se ha de fraccionar a partir del número inicial.
     * @return String valor
     */
    public String getCantidad() {
        return cantidad;
    }

    /**
     * Indica la cantidad de rango que se ha de fraccionar a partir del número inicial.
     * @param cantidad String valor
     */
    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Indica el primer número de la serie o rango a fraccionar.
     * @return String valor
     */
    public String getNumInicial() {
        return numInicial;
    }

    /**
     * Indica el primer número de la serie o rango a fraccionar.
     * @param numInicial String valor
     */
    public void setNumInicial(String numInicial) {
        this.numInicial = numInicial;
    }

    /**
     * Indica el último numero reservado para la serie o rango fraccionado.
     * @return Valor
     */
    public int getNumFinal() {
        return numFinal;
    }

    /**
     * Indica el último numero reservado para la serie o rango fraccionado.
     * @param numFinal Valor
     */
    public void setNumFinal(int numFinal) {
        this.numFinal = numFinal;
    }

    /**
     * Lista de fracciones sobre una serie o rango seleccionadas.
     * @return List
     */
    public List<IRangoSerie> getListaRangosFracciones() {
        return listaRangosFracciones;
    }

    /**
     * Lista de fracciones sobre una serie o rangos resultado de las fracciones indicadas.
     * @return List
     */
    public List<RangoSerieNng> getListaRangoFraccionados() {
        return listaRangoFraccionados;
    }

    /**
     * Lista de liberaciones solicitadas.
     * @return List
     */
    public List<LiberacionSolicitadaNng> getListaLiberaciones() {
        return listaLiberaciones;
    }

    /**
     * Lazy Model para carga de resultados en la tabla de Series / Rangos.
     * @return RangoSerieNngLazyModel
     */
    public RangoSerieNngLazyModel getRangoSerieModel() {
        return rangoSerieModel;
    }

    /**
     * Rango seleccionado para fraccionar.
     * @return RangoSerie
     */
    public RangoSerieNng getRangoAFraccionar() {
        return rangoAFraccionar;
    }

    /**
     * Rango seleccionado para fraccionar.
     * @param rangoAFraccionar RangoSerieNng
     */
    public void setRangoAFraccionar(RangoSerieNng rangoAFraccionar) {
        this.rangoAFraccionar = rangoAFraccionar;
    }

    /**
     * Fracción de rango en la tabla de Fracciones.
     * @return RangoSerieNng
     */
    public RangoSerieNng getRangoFraccionado() {
        return rangoFraccionado;
    }

    /**
     * Fracción de rango en la tabla de Fracciones.
     * @param rangoFraccionado RangoSerieNng
     */
    public void setRangoFraccionado(RangoSerieNng rangoFraccionado) {
        this.rangoFraccionado = rangoFraccionado;
    }

    /**
     * Indica si el panel de fraccionamiento está habilitado o no.
     * @return boolean
     */
    public boolean isFraccionarHabilitado() {
        return fraccionarHabilitado;
    }

    /**
     * Indica si el panel de fraccionamiento está habilitado o no.
     * @param fraccionarHabilitado boolean
     */
    public void setFraccionarHabilitado(boolean fraccionarHabilitado) {
        this.fraccionarHabilitado = fraccionarHabilitado;
    }

    /**
     * Indica si la implementación es inmediata o programada.
     * @return boolean
     */
    public boolean isImplementacionInmediata() {
        return implementacionInmediata;
    }

    /**
     * Indica si la implementación es inmediata o programada.
     * @param implementacionInmediata boolean
     */
    public void setImplementacionInmediata(boolean implementacionInmediata) {
        this.implementacionInmediata = implementacionInmediata;
    }

    /**
     * Liberacion Solicitada seleccionada en la tabla de liberaciones.
     * @return LiberacionSolicitadaNng
     */
    public LiberacionSolicitadaNng getLibSolSeleccionada() {
        return libSolSeleccionada;
    }

    /**
     * Liberacion Solicitada seleccionada en la tabla de liberaciones.
     * @param libSolSeleccionada LiberacionSolicitadaNng
     */
    public void setLibSolSeleccionada(LiberacionSolicitadaNng libSolSeleccionada) {
        this.libSolSeleccionada = libSolSeleccionada;
    }

    /**
     * Identificador de la Clave de Servicio.
     * @return BigDecimal
     */
    public BigDecimal getClaveServicio() {
        return claveServicio;
    }

    /**
     * Identificador de la Clave de Servicio.
     * @param claveServicio BigDecimal
     */
    public void setClaveServicio(BigDecimal claveServicio) {
        this.claveServicio = claveServicio;
    }

    /**
     * Perido de reserva de la numeración.
     * @return int
     */
    public int getPeriodoReserva() {
        return periodoReserva;
    }

    /**
     * Perido de reserva de la numeración.
     * @param periodoReserva int
     */
    public void setPeriodoReserva(int periodoReserva) {
        this.periodoReserva = periodoReserva;
    }

    /**
     * Tipo de Periodo de Reserva (Días / Meses).
     * @return String
     */
    public String getTipoPeriodoReserva() {
        return tipoPeriodoReserva;
    }

    /**
     * Tipo de Periodo de Reserva (Días / Meses).
     * @param tipoPeriodoReserva String
     */
    public void setTipoPeriodoReserva(String tipoPeriodoReserva) {
        this.tipoPeriodoReserva = tipoPeriodoReserva;
    }

    /**
     * Fecha de Finalización del Perido de Reserva.
     * @return Date
     */
    public Date getFechaFinReserva() {
        return fechaFinReserva;
    }

    /**
     * Información de la Solicitud de liberación.
     * @return the solicitud
     */
    public SolicitudLiberacionNng getSolicitud() {
        return solicitud;
    }

    /**
     * Mensaje de información para cesión de rangos con arrendamiento.
     * @return String
     */
    public String getRangoArrendadoMsg() {
        return rangoArrendadoMsg;
    }

    /**
     * Listado de claves de servicio.
     * @return List
     */
    public List<ClaveServicio> getListaClavesServicio() {
        return listaClavesServicio;
    }

    /**
     * Gestor de Selección Múltiple sobre la tabla Lazy.
     * @return MultiSelectionOnLazyModelManager
     */
    public MultiSelectionOnLazyModelManager<RangoSerieNng> getMultiSelectionManager() {
        return multiSelectionManager;
    }

}
