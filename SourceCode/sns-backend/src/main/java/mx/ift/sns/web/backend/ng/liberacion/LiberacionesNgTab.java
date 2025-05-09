package mx.ift.sns.web.backend.ng.liberacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroBusquedaRangos;
import mx.ift.sns.modelo.ng.LiberacionSolicitadaNg;
import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.ng.SolicitudLiberacionNg;
import mx.ift.sns.modelo.pst.TipoModalidad;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.series.IRangoSerie;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.modelo.solicitud.EstadoLiberacionSolicitada;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.utils.series.RangosSeriesUtil;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;
import mx.ift.sns.web.backend.lazymodels.RangoSerieNgLazyModel;
import mx.ift.sns.web.backend.lazymodels.multiseleccion.MultiSelectionOnLazyModelManager;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase de respaldo para el Tab/Pestaña de 'Liberación'.
 */
public class LiberacionesNgTab extends TabWizard {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(LiberacionesNgTab.class);

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_Liberaciones";

    /** Servicio de Numeración Geográfica. */
    private INumeracionGeograficaService ngService;

    /** Información de la petición de Solicitud. */
    private SolicitudLiberacionNg solicitud;

    /** Identificador de código de área. */
    private String abn;

    /** Identificador de código de región. */
    private String nir;

    /** Identificador de numeración. */
    private String sna;

    /** Tipo de Red seleccionada para la búsqueda. */
    private TipoRed tipoRedSeleccionada;

    /** Catálogo de tipos de red. */
    private List<TipoRed> listaTiposRed;

    /** Tipo de Modalidad seleccionada para la búsqueda. */
    private TipoModalidad tipoModalidadSeleccionada;

    /** Catálogo de tipos de modalidad. */
    private List<TipoModalidad> listaTiposModalidad;

    /** Lista de liberaciones solicitadas. */
    private List<LiberacionSolicitadaNg> listaLiberaciones;

    /** Rango seleccionado para fraccionar. */
    private RangoSerie rangoAFraccionar;

    /** Lista de fracciones sobre una serie o rango seleccionadas. */
    private List<IRangoSerie> listaRangosFracciones;

    /** Fracción de rango en la tabla de Fracciones. */
    private RangoSerie rangoFraccionado;

    /** Lista de fracciones sobre una serie o rangos resultado de las fracciones indicadas. */
    private List<RangoSerie> listaRangoFraccionados;

    /** Lista de Fracciones por Rango. */
    private HashMap<String, List<IRangoSerie>> fraccionesByRango;

    /** Indica si se desea fraccionar el rango o serie seleccionado. */
    private boolean usarFraccionamiento = false;

    /** Fecha de implementación de la liberación. */
    private Date fechaImplementacion;

    /** Indica la cantidad de rango que se ha de fraccionar a partir del número inicial. */
    private String cantidad = "1";

    /** Indica el primer número de la serie o rango a fraccionar. */
    private String numInicial = "";

    /** Indica el último numero reservado para la serie o rango fraccionado. */
    private int numFinal = 0;

    /** Filtros para búsqueda de Series y rangos. */
    private FiltroBusquedaRangos filtros = null;

    /** Modelo de datos para Lazy Loading en las tablas. */
    private RangoSerieNgLazyModel rangoSerieModel = null;

    /** Indica si estamos actualizando los campos al editar la solicitud. */
    private boolean actualizandoCampos = false;

    /** Indica si el panel de fraccionamiento está habilitado o no. */
    private boolean fraccionarHabilitado = true;

    /** Rango Seleccionado de la lista de Rangos. */
    private RangoSerie rangoSeleccionado;

    /** Indica si la implementación es inmediata o programada. */
    private boolean implementacionInmediata = true;

    /** Liberacion Solicitada seleccionada en la tabla de liberaciones. */
    private LiberacionSolicitadaNg libSolSeleccionada;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Gestor de Selección Múltiple sobre la tabla Lazy. */
    private MultiSelectionOnLazyModelManager<RangoSerie> multiSelectionManager;

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'Generales'.
     * @param pWizard Referencia a la clase Wizard que instancia este Tab
     * @param pNgService Servicio de Numeración Geográfica
     */
    public LiberacionesNgTab(Wizard pWizard, INumeracionGeograficaService pNgService) {
        // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
        setWizard(pWizard);

        // Asociamos la solicitud que usaremos en todo el Wizard. Es posible que
        // la referencia del Wizzard padre cambie en función de los pasos previos,
        // por eso hay que actualizar la referencia al llegar a éste tab usando
        // el método actualizaCampos();
        solicitud = (SolicitudLiberacionNg) getWizard().getSolicitud();

        // Asociamos el Servicio
        ngService = pNgService;
    }

    @Override
    public void cargaValoresIniciales() {
        try {
            listaTiposRed = ngService.findAllTiposRed();
            listaTiposModalidad = new ArrayList<TipoModalidad>();
            listaLiberaciones = new ArrayList<LiberacionSolicitadaNg>();
            listaRangosFracciones = new ArrayList<IRangoSerie>();
            listaRangoFraccionados = new ArrayList<RangoSerie>();
            fechaImplementacion = new Date();
            fraccionesByRango = new HashMap<String, List<IRangoSerie>>(1);
            multiSelectionManager = new MultiSelectionOnLazyModelManager<>();
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
            usarFraccionamiento = false;
            listaRangosFracciones.clear();
            listaRangoFraccionados.clear();
            tipoRedSeleccionada = null;
            tipoModalidadSeleccionada = null;
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
                rangoSerieModel = new RangoSerieNgLazyModel();
                rangoSerieModel.setFiltros(filtros);
                rangoSerieModel.setService(ngService);
                rangoSerieModel.setMultiSelectionManager(multiSelectionManager);
            }

            // Reiniciamos los filtros
            filtros.clear();
            rangoSerieModel.clear();

            filtros.setResultadosPagina(registroPorPagina);

            // Buscamos siempre por el Asignatario que es el poseedor del rango.
            filtros.setAsignatario(solicitud.getProveedorSolicitante());

            filtros.setTipoRed(tipoRedSeleccionada);
            filtros.setTipoModalidad(tipoModalidadSeleccionada);

            if (!StringUtils.isEmpty(sna)) {
                filtros.setIdSna(new BigDecimal(sna));
            }

            if (!StringUtils.isEmpty(abn)) {
                filtros.setIdAbn(new BigDecimal(abn));
            }

            // Las búsquedas se hacen por el ID, no por el código.
            if (!StringUtils.isEmpty(nir)) {
                Nir n = ngService.getNirByCodigo(Integer.parseInt(nir));
                if (n == null) {
                    StringBuilder sbNir = new StringBuilder();
                    sbNir.append("El código de NIR introducido (").append(nir.toString());
                    sbNir.append(") no es válido.");
                    MensajesBean.addErrorMsg(MSG_ID, sbNir.toString(), "");

                    // Ponemos un valor de NIR inexistente para que no muestre resultados en la búsqueda.
                    nir = null;
                    filtros.setIdNir(new BigDecimal(-1));
                } else {
                    filtros.setIdNir(n.getId());
                }
            }

            PaginatorUtil.resetPaginacion("FORM_SolicitudLiberacion:TAB_Liberacion:TBL_Series",
                    filtros.getResultadosPagina());

            // Limpiamos la selección previa
            multiSelectionManager.clear();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar el botón 'limpiar' sobre el buscador de solicitudes. */
    public void limpiarBusqueda() {
        abn = null;
        nir = null;
        sna = null;
        rangoSerieModel = null;
        multiSelectionManager.clear();
        rangoSeleccionado = null;
    }

    /** Método invocado al pulsar el botón 'Agregar'. */
    public void agregarLiberacion() {
        try {
            // Validaciones genéricas.
            boolean todoOk = true;

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
                } else {
                    if (validarLiberacionExistente()) {
                        this.liberarRangosSeleccionados();
                    }
                }
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
                if (rangoSeleccionado.getEstadoRango().getCodigo().equals(EstadoRango.ASIGNADO)) {
                    // Comprobamos que no exista ya la cesión solicitada
                    if (validarLiberacionExistente()) {
                        listaRangosFracciones.add(rangoSeleccionado);
                        rangoAFraccionar = rangoSeleccionado;
                    }
                } else {
                    MensajesBean.addErrorMsg(MSG_ID, "El rango no esta disponible, estado: "
                            + rangoSeleccionado.getEstadoRango().getDescripcion(), "");
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
        for (RangoSerie fraccion : listaRangoFraccionados) {
            // Ignoramos los huecos temporales y agregamos las peticiones fracción
            if (fraccion.getEstadoRango().getCodigo().equals(EstadoRango.AFECTADO)) {
                RangoSerie rangoOriginal =
                        (RangoSerie) RangosSeriesUtil.getRangoInicial(fraccion, listaRangosFracciones);
                LiberacionSolicitadaNg ls = getLiberacionSolicitadaFromRango(rangoOriginal);
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
            for (RangoSerie rango : multiSelectionManager.getRegistrosSeleccionados()) {
                LiberacionSolicitadaNg ls = getLiberacionSolicitadaFromRango(rango);
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
            PeticionCancelacion checkCancelacion = ngService.cancelLiberacion(libSolSeleccionada, true);
            if (checkCancelacion.isCancelacionPosible()) {
                listaLiberaciones.remove(libSolSeleccionada);
                this.guardarCambios();
            } else {
                MensajesBean.addErrorMsg(MSG_ID, checkCancelacion.getMensajeError(), "");
            }
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
                    listaRangoFraccionados = new ArrayList<RangoSerie>(1);
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

    /** Método invocado al seleccionar un tipo de red en el combo. */
    public void seleccionTipoRed() {
        try {
            // Tipos de Modalidad en función del tipo de red
            if (tipoRedSeleccionada != null) {
                String red = tipoRedSeleccionada.getCdg();
                if (red.equals(TipoRed.MOVIL) || red.equals(TipoRed.AMBAS)) {
                    listaTiposModalidad = ngService.findAllTiposModalidad();
                } else {
                    listaTiposModalidad.clear();
                }
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

        // Lista de Cesiones Solicitadas a eliminar permanentemente
        ArrayList<LiberacionSolicitadaNg> libSolEliminar = new ArrayList<LiberacionSolicitadaNg>();

        try {
            // Eliminamos las liberaciones que se han deseleccionado
            for (LiberacionSolicitadaNg libsol : solicitud.getLiberacionesSolicitadas()) {
                // Ignoramos las liberaciones que ya se hayan ejecutado
                if (libsol.getEstado().getCodigo().equals(EstadoLiberacionSolicitada.PENDIENTE)) {
                    if ((!arrayContains(listaLiberaciones, libsol))) {
                        // No podemos eliminar una liberación solicitada de la lista mientras estamos
                        // recorriendo la misma lista (ConcurrentModificationException).
                        // Por lo tanto la añadimos a otra lista para eliminarla al salir del bucle.
                        libSolEliminar.add(libsol);
                    }
                }
            }

            // Eliminamos de la solicitud las LiberacionesSolicitadas que se han eliminado de la tabla de Liberaciones.
            for (LiberacionSolicitadaNg libsol : libSolEliminar) {
                // Al tener marcadas las liberacionesSolicitadas como @PrivateOwned en JPA se eliminan automáticamente
                // cuando se guardan los cambios en la solicitud ya que indica que nadie más las utiliza.
                solicitud.removeLiberacionSolicitada(libsol);
            }

            // Agregamos las liberaciones que se han añadido
            for (LiberacionSolicitadaNg libSol : listaLiberaciones) {
                if (!arrayContains(solicitud.getLiberacionesSolicitadas(), libSol)) {
                    solicitud.addLiberacionSolicitada(libSol);
                }
            }

            // Guardamos los cambios y generamos los nuevos registros
            solicitud = ngService.saveSolicitudLiberacion(solicitud);

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
                    RangoSerie fraccion = new RangoSerie();
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
                                RangoSerie rangoActualizado = null;
                                if (!actualizandoCampos) {
                                    // Volvemos a comprobar el estado del rango ya que no se modifica hasta que
                                    // no se agrega un fraccionamiento y mientras tanto ha podido ser reservado
                                    // por otro agente.
                                    rangoActualizado = ngService.getRangoSerie(
                                            rangoAFraccionar.getId().getIdNir(),
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
                                if (rangoActualizado.getEstadoRango().getCodigo().equals(EstadoRango.ASIGNADO)
                                        || actualizandoCampos) {

                                    // Clonamos el rango original para mantener los mismos valores.
                                    fraccion = (RangoSerie) rangoAFraccionar.clone();

                                    // Usamos el mismo id para que nos dé la misma Key en la hashmap de fracciones.
                                    fraccion.getId().setId(rangoAFraccionar.getId().getId());

                                    // Al clonar hemos vuelto a los valores inicial y final del origianl, los rehacemos.
                                    fraccion.setNumInicio(StringUtils.leftPad(String.valueOf(numInicial), 4, '0'));
                                    fraccion.setNumFinal(StringUtils.leftPad(String.valueOf(numFinal), 4, '0'));
                                    fraccion.setEstadoRango(ngService.getEstadoRangoByCodigo(EstadoRango.AFECTADO));
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
                                            + rangoActualizado.getEstadoRango().getDescripcion(), "");
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
            solicitud = (SolicitudLiberacionNg) getWizard().getSolicitud();

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

                for (LiberacionSolicitadaNg libSol : listaLiberaciones) {
                    existeRango = true;
                    if (libSol.getEstado().getCodigo().equals(EstadoLiberacionSolicitada.PENDIENTE)) {
                        if (libSol.getFraccionamientoRango().equals("N")) {
                            existeRango = ngService.existeRangoSerie(
                                    libSol.getIdNir(),
                                    libSol.getSna(),
                                    libSol.getNumInicio(),
                                    libSol.getProveedorCesionario());
                        } else {
                            // Rango seleccionado para fraccionar
                            RangoSerie rangoOriginal = ngService.getRangoSerieByFraccion(
                                    libSol.getIdNir(),
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
                        sbAviso.append(", Nir: ").append(libSol.getCdgNir());
                        sbAviso.append(", Sna: ").append(libSol.getSna());
                        sbAviso.append(", Inicio: ").append(libSol.getNumInicio());
                        sbAviso.append(", Final: ").append(libSol.getNumFinal()).append("<br>");
                        avisosRangosCambiados.add(sbAviso.toString());
                    }
                }

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
            rangoSeleccionado = (RangoSerie) event.getObject();
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
            rangoSeleccionado = (RangoSerie) event.getObject();
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
            fechaImplementacion = new Date();
        } else {
            // La fecha de implementación programada siempre es a partir de mañana.
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            fechaImplementacion = calendar.getTime();
        }
    }

    /** Valida la fecha de Implementación. */
    public void validarFechaImpl() {
        try {
            // La fecha de implementación programada siempre es a partir de mañana.
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 1);

            if (fechaImplementacion == null) {
                fechaImplementacion = calendar.getTime();
            } else {
                if (fechaImplementacion.before(calendar.getTime())) {
                    MensajesBean.addErrorMsg(MSG_ID,
                            "La fecha de implementación programada no puede ser igual o inferior al día actual.", "");
                    fechaImplementacion = calendar.getTime();
                }
            }
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
                // Aplicamos la liberaciones solicitadas y reservamos los rangos si es necesario.
                SolicitudLiberacionNg solLib = ngService.applyLiberacionesSolicitadas(solicitud);

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
    private List<RangoSerie> ajustarFraccionesRango(RangoSerie pRango) throws Exception {

        // Lista de fracciones sobre un mismo rango
        List<IRangoSerie> fraccionesSerie = fraccionesByRango.get(pRango.getIdentificadorRango());

        // Estatus Asignado para los rangos Ficticios
        EstadoRango statusAsignado = ngService.getEstadoRangoByCodigo(EstadoRango.ASIGNADO);

        if (!fraccionesSerie.isEmpty()) {
            List<IRangoSerie> fraccionesIRango = RangosSeriesUtil.getFracciones(
                    fraccionesSerie, pRango, statusAsignado);

            List<RangoSerie> fracciones = RangosSeriesUtil.castListaRangoSerieNG(fraccionesIRango);

            // Añadimos id's ficticios para poder hacer selección en las tablas
            int counter = 0;
            for (RangoSerie rango : fracciones) {
                rango.getId().setId(new BigDecimal(counter));
                counter++;
            }
            return fracciones;
        } else {
            return new ArrayList<RangoSerie>();
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
        ArrayList<RangoSerie> rangosReservados = new ArrayList<RangoSerie>();
        List<RangoSerie> fracciones = RangosSeriesUtil.castListaRangoSerieNG(listaRangosFracciones);
        for (RangoSerie rangoEnFracciones : fracciones) {
            for (RangoSerie rangoSeleccionado : multiSelectionManager.getRegistrosSeleccionados()) {
                if (rangoEnFracciones.getId().getId().intValue() == rangoSeleccionado.getId().getId().intValue()) {
                    rangosReservados.add(rangoSeleccionado);
                }
            }
        }

        if (!rangosReservados.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            sb.append("No es posible agregar los rangos seleccionados. ");
            sb.append("Lo siguientes rangos tienen fracciones aplicadas:<br>");
            for (RangoSerie rango : rangosReservados) {
                sb.append("Serie: ").append(rango.getId().getSna()).append(", Num.Inicio: ");
                sb.append(rango.getNumInicio()).append(", Estatus: ").append(rango.getEstadoRango().getDescripcion());
                sb.append("<br>");
            }
            MensajesBean.addErrorMsg(MSG_ID, sb.toString(), "");
            return false;
        }

        // Al ceder una serie completa es necesario que todos los rangos de la serie
        // que pertenecen al proveedor esten en estado 'ASIGNADO'
        rangosReservados = new ArrayList<RangoSerie>();
        for (RangoSerie rango : multiSelectionManager.getRegistrosSeleccionados()) {
            if (!rango.getEstadoRango().getCodigo().equals(EstadoRango.ASIGNADO)) {
                rangosReservados.add(rango);
            }
        }

        // Solo es posible ceder rangos libres
        if (!rangosReservados.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            sb.append("No es posible agregar los rangos seleccionados. ");
            sb.append("Lo siguientes rangos no están disponibles:<br>");
            for (RangoSerie rango : rangosReservados) {
                sb.append("Serie: ").append(rango.getId().getSna()).append(", Num.Inicio: ");
                sb.append(rango.getNumInicio()).append(", Estatus: ");
                sb.append(rango.getEstadoRango().getDescripcion());
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

        for (LiberacionSolicitadaNg libSol : solicitud.getLiberacionesSolicitadas()) {
            existeRango = true;
            statusAsignado = true;

            if (libSol.getEstado().getCodigo().equals(EstadoLiberacionSolicitada.PENDIENTE)) {
                if (libSol.getFraccionamientoRango().equals("N")) {
                    if (ngService.existeRangoSerie(
                            libSol.getIdNir(), libSol.getSna(), libSol.getNumInicio(),
                            libSol.getProveedorCesionario())) {

                        RangoSerie rango = ngService.getRangoSerie(
                                libSol.getIdNir(), libSol.getSna(), libSol.getNumInicio(),
                                libSol.getProveedorCesionario());

                        if (!rango.getEstadoRango().getCodigo().equals(EstadoRango.ASIGNADO)) {
                            // Si esta Reservado puede ser de ésta misma solicitud por ser una implementación
                            // no inmediata
                            if (rango.getEstadoRango().getCodigo().equals(EstadoRango.RESERVADO)) {
                                if (rango.getSolicitud().getId().intValue() != solicitud.getId().intValue()) {
                                    statusAsignado = false;
                                    status = rango.getEstadoRango().getDescripcion();
                                }
                            } else {
                                statusAsignado = false;
                                status = rango.getEstadoRango().getDescripcion();
                            }
                        }
                    } else {
                        existeRango = false;
                    }
                } else {
                    // Rango seleccionado para ceder completo o para fraccionar
                    RangoSerie rangoOriginal = ngService.getRangoSerieByFraccion(
                            libSol.getIdNir(), libSol.getSna(), libSol.getNumInicio(),
                            libSol.getNumFinal(), libSol.getProveedorCesionario());

                    if (rangoOriginal != null) {
                        if (!rangoOriginal.getEstadoRango().getCodigo().equals(EstadoRango.ASIGNADO)) {
                            // Si esta Reservado puede ser de ésta misma solicitud por ser una implementación
                            // no inmediata
                            if (rangoOriginal.getEstadoRango().getCodigo().equals(EstadoRango.RESERVADO)) {
                                if (rangoOriginal.getSolicitud().getId().intValue()
                                != solicitud.getId().intValue()) {
                                    statusAsignado = false;
                                    status = rangoOriginal.getEstadoRango().getDescripcion();
                                }
                            } else {
                                statusAsignado = false;
                                status = rangoOriginal.getEstadoRango().getDescripcion();
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
                sbAviso.append(", Nir: ").append(libSol.getCdgNir());
                sbAviso.append(", Sna: ").append(libSol.getSna());
                sbAviso.append(", Inicio: ").append(libSol.getNumInicio());
                sbAviso.append(", Final: ").append(libSol.getNumFinal()).append("<br>");
                avisosDisponibilidad.add(sbAviso.toString());
            }

            if (!statusAsignado) {
                StringBuffer sbAviso = new StringBuffer();
                sbAviso.append("Asignatario: ").append(libSol.getProveedorCesionario().getNombreCorto());
                sbAviso.append(", Nir: ").append(libSol.getCdgNir());
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
        List<RangoSerie> rangosEnLiberacion = new ArrayList<RangoSerie>();
        if (usarFraccionamiento) {
            LiberacionSolicitadaNg libSolTemp = this.getLiberacionSolicitadaFromRango(rangoSeleccionado);
            if (arrayContains(listaLiberaciones, libSolTemp)) {
                rangosEnLiberacion.add(rangoSeleccionado);
            }
        } else {
            for (RangoSerie rango : multiSelectionManager.getRegistrosSeleccionados()) {
                LiberacionSolicitadaNg libSolTemp = this.getLiberacionSolicitadaFromRango(rango);
                if (arrayContains(listaLiberaciones, libSolTemp)) {
                    rangosEnLiberacion.add(rango);
                }
            }
        }

        if (!rangosEnLiberacion.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            sb.append("No es posible agregar los rangos seleccionados. ");
            sb.append("Lo siguientes rangos ya existen para liberación:<br>");
            for (RangoSerie rango : rangosEnLiberacion) {
                sb.append("Nir: ").append(rango.getSerie().getNir().getCodigo());
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
     * Crea un objeto LiberacionSolicitadaNg en base a la información de un Rango.
     * @param pRango Rango con la información de la liberación.
     * @return LiberacionSolicitadaNg
     * @throws Exception en caso de error.
     */
    private LiberacionSolicitadaNg getLiberacionSolicitadaFromRango(RangoSerie pRango) throws Exception {
        LiberacionSolicitadaNg ls = new LiberacionSolicitadaNg();

        // Liberacion Solicitada
        ls.setId(null); // Hasta que no se guarda no se obtiene un Id
        ls.setProveedorCesionario(pRango.getAsignatario());
        ls.setProveedorConcesionario(pRango.getConcesionario());
        ls.setProveedorArrendatario(pRango.getArrendatario());
        ls.setIdNir(pRango.getSerie().getNir().getId());
        ls.setCdgNir(pRango.getSerie().getNir().getCodigo());
        ls.setIdAbn(pRango.getSerie().getNir().getAbn().getCodigoAbn());
        ls.setSna(pRango.getSerie().getId().getSna());
        ls.setIdSerieInicial(pRango.getSerie().getId().getSna());
        ls.setCentralDestino(pRango.getCentralDestino());
        ls.setCentralOrigen(pRango.getCentralOrigen());
        ls.setNumInicio(pRango.getNumInicio().toString());
        ls.setNumFinal(pRango.getNumFinal().toString());
        ls.setPoblacion(pRango.getPoblacion());
        ls.setSolicitudLiberacion(solicitud);
        ls.setTipoModalidad(pRango.getTipoModalidad());
        ls.setTipoRed(pRango.getTipoRed());
        ls.setIdoPnn(pRango.getIdoPnn());
        ls.setIdaPnn(pRango.getIdaPnn());

        // Información de la solicitud de asignación.
        ls.setNumOficioRango(pRango.getOficioAsignacion());
        ls.setFechaAsignacion(pRango.getFechaAsignacion());
        ls.setConsecutivoAsignacion(pRango.getConsecutivoAsignacion());

        // Indicamos si se ha fraccionado la numeración con la liberación
        if (usarFraccionamiento) {
            ls.setFraccionamientoRango("S");
        } else {
            ls.setFraccionamientoRango("N");
        }

        // Fecha de Implementación
        ls.setFechaLiberacion(FechasUtils.parseFecha(fechaImplementacion));

        // Estado Reservado para su posterior liberación.
        EstadoLiberacionSolicitada els = new EstadoLiberacionSolicitada();
        els.setCodigo(EstadoLiberacionSolicitada.PENDIENTE);
        ls.setEstado(els);

        return ls;
    }

    /**
     * Indica si una lista contiene una LiberacionSolicitadaNg sin usar los métos Equals de Object.
     * @param list Lista de objetos LiberacionSolicitadaNg
     * @param ls LiberacionSolicitadaNg a comparar
     * @return True si la LiberacionSolicitadaNg esta en la lista
     * @throws Exception en caso de error
     */
    private boolean arrayContains(List<LiberacionSolicitadaNg> list, LiberacionSolicitadaNg ls) throws Exception {

        // Cuando se guardan los cambios las LiberacionesSolicitadas tienen otra instancia diferente dada
        // por JPA y, por lo tanto, no se pueden comparar con las almacenadas en la lista interna. Es necesario
        // comparar por los campos clave para saber si nos estamos refiriendo a la misma.

        for (LiberacionSolicitadaNg libSol : list) {
            if (libSol.getIdNir().equals(ls.getIdNir())
                    && libSol.getSna().equals(ls.getSna())
                    && libSol.getNumInicio().equals(ls.getNumInicio())) {
                return true;
            }
        }
        return false;
    }

    // GETTERS & SETTERS

    /**
     * Identificador de código de área.
     * @return Identificador
     */
    public String getAbn() {
        return abn;
    }

    /**
     * Identificador de código de área.
     * @param abn Identificador
     */
    public void setAbn(String abn) {
        this.abn = abn;
    }

    /**
     * Identificador de código de región.
     * @return Identificador
     */
    public String getNir() {
        return nir;
    }

    /**
     * Identificador de código de región.
     * @param nir Identificador
     */
    public void setNir(String nir) {
        this.nir = nir;
    }

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
     * Tipo de Red seleccionada para la búsqueda.
     * @return Tipo de Red
     */
    public TipoRed getTipoRedSeleccionada() {
        return tipoRedSeleccionada;
    }

    /**
     * Tipo de Red seleccionada para la búsqueda.
     * @param tipoRedSeleccionada Tipo De Red
     */
    public void setTipoRedSeleccionada(TipoRed tipoRedSeleccionada) {
        this.tipoRedSeleccionada = tipoRedSeleccionada;
    }

    /**
     * Catálogo de tipos de red.
     * @return Lista de Tipo de Red
     */
    public List<TipoRed> getListaTiposRed() {
        return listaTiposRed;
    }

    /**
     * Catálogo de tipos de red.
     * @param listaTiposRed Lista de Tipo de Red
     */
    public void setListaTiposRed(List<TipoRed> listaTiposRed) {
        this.listaTiposRed = listaTiposRed;
    }

    /**
     * Tipo de Modalidad seleccionada para la búsqueda.
     * @return Tipo Modalidad
     */
    public TipoModalidad getTipoModalidadSeleccionada() {
        return tipoModalidadSeleccionada;
    }

    /**
     * Tipo de Modalidad seleccionada para la búsqueda.
     * @param tipoModalidadSeleccionada Tipo Modalidad
     */
    public void setTipoModalidadSeleccionada(TipoModalidad tipoModalidadSeleccionada) {
        this.tipoModalidadSeleccionada = tipoModalidadSeleccionada;
    }

    /**
     * Catálogo de tipos de modalidad.
     * @return Lista de Tipo Modalidad
     */
    public List<TipoModalidad> getListaTiposModalidad() {
        return listaTiposModalidad;
    }

    /**
     * Catálogo de tipos de modalidad.
     * @param listaTiposModalidad Lista de Tipo Modalidad
     */
    public void setListaTiposModalidad(List<TipoModalidad> listaTiposModalidad) {
        this.listaTiposModalidad = listaTiposModalidad;
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
        return (!listaLiberaciones.isEmpty());
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
     * Lista de fracciones sobre una serie o rango seleccionadas.
     * @param listaRangosFracciones List
     */
    public void setListaRangosFracciones(List<IRangoSerie> listaRangosFracciones) {
        this.listaRangosFracciones = listaRangosFracciones;
    }

    /**
     * Lista de fracciones sobre una serie o rangos resultado de las fracciones indicadas.
     * @return List
     */
    public List<RangoSerie> getListaRangoFraccionados() {
        return listaRangoFraccionados;
    }

    /**
     * Lista de fracciones sobre una serie o rangos resultado de las fracciones indicadas.
     * @param listaRangoFraccionados List
     */
    public void setListaRangoFraccionados(List<RangoSerie> listaRangoFraccionados) {
        this.listaRangoFraccionados = listaRangoFraccionados;
    }

    /**
     * Lista de liberaciones solicitadas.
     * @return List
     */
    public List<LiberacionSolicitadaNg> getListaLiberaciones() {
        return listaLiberaciones;
    }

    /**
     * Lista de liberaciones solicitadas.
     * @param listaLiberaciones List
     */
    public void setListaLiberaciones(List<LiberacionSolicitadaNg> listaLiberaciones) {
        this.listaLiberaciones = listaLiberaciones;
    }

    /**
     * Lazy Model para carga de resultados en la tabla de Series / Rangos.
     * @return RangoSerieNgLazyModel
     */
    public RangoSerieNgLazyModel getRangoSerieModel() {
        return rangoSerieModel;
    }

    /**
     * Lazy Model para carga de resultados en la tabla de Series / Rangos.
     * @param rangoSerieModel RangoSerieNgLazyModel
     */
    public void setRangoSerieModel(RangoSerieNgLazyModel rangoSerieModel) {
        this.rangoSerieModel = rangoSerieModel;
    }

    /**
     * Rango seleccionado para fraccionar.
     * @return RangoSerie
     */
    public RangoSerie getRangoAFraccionar() {
        return rangoAFraccionar;
    }

    /**
     * Rango seleccionado para fraccionar.
     * @param rangoAFraccionar RangoSerie
     */
    public void setRangoAFraccionar(RangoSerie rangoAFraccionar) {
        this.rangoAFraccionar = rangoAFraccionar;
    }

    /**
     * Fracción de rango en la tabla de Fracciones.
     * @return RangoSerie
     */
    public RangoSerie getRangoFraccionado() {
        return rangoFraccionado;
    }

    /**
     * Fracción de rango en la tabla de Fracciones.
     * @param rangoFraccionado RangoSerie
     */
    public void setRangoFraccionado(RangoSerie rangoFraccionado) {
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
     * @return LiberacionSolicitadaNg
     */
    public LiberacionSolicitadaNg getLibSolSeleccionada() {
        return libSolSeleccionada;
    }

    /**
     * Liberacion Solicitada seleccionada en la tabla de liberaciones.
     * @param libSolSeleccionada LiberacionSolicitadaNg
     */
    public void setLibSolSeleccionada(LiberacionSolicitadaNg libSolSeleccionada) {
        this.libSolSeleccionada = libSolSeleccionada;
    }

    /**
     * Información de la Solicitud de liberación.
     * @return the solicitud
     */
    public SolicitudLiberacionNg getSolicitud() {
        return solicitud;
    }

    /**
     * @return the registroPorPagina
     */
    public int getRegistroPorPagina() {
        return registroPorPagina;
    }

    /**
     * @param registroPorPagina the registroPorPagina to set
     */
    public void setRegistroPorPagina(int registroPorPagina) {
        this.registroPorPagina = registroPorPagina;
    }

    /**
     * Gestor de Selección Múltiple sobre la tabla Lazy.
     * @return MultiSelectionOnLazyModelManager
     */
    public MultiSelectionOnLazyModelManager<RangoSerie> getMultiSelectionManager() {
        return multiSelectionManager;
    }

}
