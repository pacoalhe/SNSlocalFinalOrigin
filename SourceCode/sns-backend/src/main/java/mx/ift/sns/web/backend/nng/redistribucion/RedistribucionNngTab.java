package mx.ift.sns.web.backend.nng.redistribucion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroBusquedaRangos;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.nng.RangoSerieNng;
import mx.ift.sns.modelo.nng.RedistribucionSolicitadaNng;
import mx.ift.sns.modelo.nng.SolicitudRedistribucionNng;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.series.IRangoSerie;
import mx.ift.sns.modelo.solicitud.EstadoRedistribucionSolicitada;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;
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
 * Clase de respaldo para el Tab/Pestaña de 'Redistribución' para Numeración No Geográfica.
 */
public class RedistribucionNngTab extends TabWizard {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RedistribucionNngTab.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_RedistribucionesNNG";

    /** Facade de Servicios de Numeración No Geográfica. */
    private INumeracionNoGeograficaFacade nngFacade;

    /** Solicitud de Redistribución NNG. */
    private SolicitudRedistribucionNng solicitud;

    /** Identificador clave de servicio. */
    private BigDecimal claveServicio;

    /** Listado de claves de servicio. */
    private List<ClaveServicio> listaClavesServicio;

    /** Identificador de numeración. */
    private String sna;

    /** Filtros para búsqueda de Series y rangos. */
    private FiltroBusquedaRangos filtros = null;

    /** Modelo de datos para Lazy Loading en las tablas. */
    private RangoSerieNngLazyModel rangoSerieModel = null;

    /** Rango seleccionado en la búsqueda de Series. */
    private RangoSerieNng rangoSeleccionado;

    /** Indica si se desea fraccionar el rango o serie seleccionado. */
    private boolean usarFraccionamiento = false;

    /** Indica la cantidad de rango que se ha de fraccionar a partir del número inicial. */
    private String cantidad = "1";

    /** Indica el primer número de la serie o rango a fraccionar. */
    private String numInicial = "";

    /** Indica el último numero reservado para la serie o rango fraccionado. */
    private int numFinal = 0;

    /** Lista de fracciones sobre una serie o rango seleccionadas. */
    private List<IRangoSerie> listaRangosFracciones;

    /** Fracción de rango en la tabla de Fracciones. */
    private RangoSerieNng rangoFraccionado;

    /** Rango a fraccionar. */
    private RangoSerieNng rangoAFraccionar;

    /** Lista de fracciones sobre una serie o rangos resultado de las fracciones indicadas. */
    private List<RangoSerieNng> listaRangoFraccionados;

    /** Lista de fracciones sobre una serie o rangos a fraccionar. */
    private List<IRangoSerie> listaRangosAFraccionar;

    /** Lista de Fracciones por Rango. */
    private HashMap<String, List<IRangoSerie>> fraccionesByRango;

    /** Habilita o deshabilita el botón de 'Guardar Cambios'. */
    private boolean salvarHabilitado = false;

    /** Flag que habilita la edición de la serie seleccionada. */
    private boolean edicionHabilitada = false;

    /** Flag que habilita la búsqueda de series. */
    private boolean busquedaHabilitada = true;

    /** Flag que habilita la edición de una redistribución solicitada de la tabla. */
    private boolean edicionRegistro = false;

    /** Indica si el panel de fraccionamiento está habilitado o no. */
    private boolean fraccionarHabilitado = true;

    /** Lista de Redistribuciones Solicitadas. */
    private List<RedistribucionSolicitadaNng> listaRedistSol;

    /** Redistribución Solicitada Seleccionada. */
    private RedistribucionSolicitadaNng redistSol;

    /** Lista de fracciones sobre una serie o rangos a fraccionar. */
    private List<IRangoSerie> listaRangosEstadoActual;

    /** Indica si estamos actualizando los campos al editar la solicitud. */
    private boolean actualizandoCampos = false;

    /** Listado de Proveedores Arrendatarios. */
    private List<Proveedor> listaArrendatarios;

    /** Proveedor Arrendatario Seleccionado. */
    private Proveedor pstArrendatarioSeleccionado;

    /** Proveedor Concesionario Seleccionado. */
    private Proveedor pstConcesionarioSeleccionado;

    /** Listado de Proveedores Concesionarios. */
    private List<Proveedor> listaConcesionarios;

    /** Indica si el proveedor solicitante del trámite es Concesionario. */
    private boolean pstConcesionario = true;

    /** Cliente indicado para redistribuir. */
    private String cliente = null;

    /** Mensaje de información para cesión de rangos con arrendamiento. */
    private String rangoArrendadoMsg = "";

    /** Indica si se ha de eliminar el valor de Cliente del rango. */
    private boolean eliminarCliente;

    /** Indica si se ha de eliminar el valor de Concesionario del rango. */
    private boolean eliminarConcesionario;

    /** Indica si se ha de eliminar el valor de Arrendatario del rango. */
    private boolean eliminarArrendatario;

    /** Gestor de Selección Múltiple sobre la tabla Lazy. */
    private MultiSelectionOnLazyModelManager<RangoSerieNng> multiSelectionManager;

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'Generales'.
     * @param pWizard Referencia a la clase Wizard que instancia este Tab
     * @param pNngFacade Facade de Servicios de Numeración No Geográfica.
     */
    public RedistribucionNngTab(Wizard pWizard, INumeracionNoGeograficaFacade pNngFacade) {
        // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
        setWizard(pWizard);

        // Asociamos la solicitud que usaremos en todo el Wizard.
        solicitud = (SolicitudRedistribucionNng) getWizard().getSolicitud();

        // Asociamos el Servicio
        nngFacade = pNngFacade;
    }

    @Override
    public void cargaValoresIniciales() {
        try {
            // Inicializaciones
            multiSelectionManager = new MultiSelectionOnLazyModelManager<RangoSerieNng>();
            fraccionesByRango = new HashMap<>(1);
            listaRangoFraccionados = new ArrayList<>();
            listaRangosAFraccionar = new ArrayList<>();
            listaRangosEstadoActual = new ArrayList<>();
            listaRedistSol = new ArrayList<>();
            listaArrendatarios = new ArrayList<>();
            listaConcesionarios = new ArrayList<>();

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
            usarFraccionamiento = false;
            edicionHabilitada = false;
            busquedaHabilitada = true;
            edicionRegistro = false;
            cantidad = "1";
            numInicial = "";
            numFinal = 0;
            listaRangoFraccionados.clear();
            listaRangosAFraccionar.clear();
            listaRangosEstadoActual.clear();
            listaRedistSol.clear();
            fraccionesByRango.clear();
            rangoAFraccionar = null;
            listaArrendatarios.clear();
            listaConcesionarios.clear();
            pstArrendatarioSeleccionado = null;
            pstConcesionarioSeleccionado = null;
            pstConcesionario = true;
            cliente = null;
            eliminarArrendatario = false;
            eliminarCliente = false;
            eliminarConcesionario = false;
            this.limpiarBusqueda();
        }
    }

    @Override
    public boolean isAvanzar() {
        boolean resultado = true;
        if (this.isTabHabilitado()) {
            // Continuamos siempre y cuando haya cesiones por procesar
            if (listaRedistSol.isEmpty()) {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("redistribucion.errores.almenosUna"), "");
                resultado = false;
            } else {
                resultado = (this.guardarCambios() && this.aplicarRedistribuciones());
            }
        } else {
            // El tab no se puede editar, permitimos pasar directamente a la siguiente pestaña.
            resultado = true;
        }
        if (!resultado) {
            // Al no permitirse avanzar se muestra en el Wizard el mensaje indicado por el TabWizard
            this.setSummaryError(MensajesBean.getTextoResource("error.avanzar"));
        }
        return resultado;
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
            // Para cesiones el proveedor solicitante es el cedente.
            filtros.setAsignatario(solicitud.getProveedorSolicitante());
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
        this.habilitarEdicion();
        this.habilitarFraccionamiento();
    }

    @Override
    public void actualizaCampos() {
        try {
            // Limpiamos los datos de búsqueda por si se ha retrocedido
            // desde la página de redistribución y se vuelve a ella.
            this.limpiarBusqueda();

            // La solicitud del Wizard ha cambiado de instancia desde que se generó en
            // el constructor. Es necesario actualizar la referecnia en el tab.
            solicitud = (SolicitudRedistribucionNng) getWizard().getSolicitud();

            // Identificamos el tipo de proveedor solicitante
            pstConcesionario = (solicitud.getProveedorSolicitante().getTipoProveedor().getCdg()
                    .equals(TipoProveedor.CONCESIONARIO));

            // Si la solicitud no está cancelada sigue siendo editable.
            if (!solicitud.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_CANCELADA)) {
                // Estamos en modo actualización. Evita algunas validaciones.
                actualizandoCampos = true;
                Proveedor pstSolicitante = solicitud.getProveedorSolicitante();
                if (pstConcesionario) {
                    // PST (concesionarios y comercializadoras) con los cuales el PST solicitante les puede
                    // arrendar numeraciones, es decir, aquellos que por catálogo tengan definido u ABC
                    // (en caso de ser comercializadoras que tenga un convenio con un PST con ABC)
                    listaArrendatarios = nngFacade.findAllArrendatariosByAbc(pstSolicitante);
                } else { // -> Comercializadoras y Ambos
                    // concesionarios de red con los cuales el PST solicitante tiene convenio de uso de red.
                    // Es decir aquellos concesionario que tiene configurado ABC en catálogo.
                    listaConcesionarios = nngFacade.findAllConcesionariosFromConveniosByAbc(pstSolicitante);
                    // PST (concesionarios y comercializadoras) con los cuales el PST
                    // solicitante les puede arrendar numeraciones, es decir, aquellos que por catálogo tengan
                    // definido u ABC (en caso de ser comercializadoras que tenga un convenio con un PST con ABC).
                    listaArrendatarios = nngFacade.findAllArrendatariosByAbc(pstSolicitante);
                }

                // Actualizamos las tablas de fracciones y resúmenes
                listaRedistSol.clear();
                listaRangosEstadoActual.clear();
                listaRangosAFraccionar.clear();
                listaRangoFraccionados.clear();
                fraccionesByRango.clear();

                List<String> avisosRangosCambiados = new ArrayList<String>();
                boolean existeRango = true;
                for (RedistribucionSolicitadaNng redSol : solicitud.getRedistribucionesSolicitadas()) {
                    listaRedistSol.add(redSol);
                    existeRango = true;

                    if (redSol.getFraccionamientoRango().equals("N")) {
                        RangoSerieNng rangoOriginal = nngFacade.getRangoSerie(
                                redSol.getIdClaveServicio(), redSol.getSna(), redSol.getNumInicio(),
                                redSol.getProveedorSolicitante());

                        existeRango = (rangoOriginal != null);

                        if (existeRango) {
                            listaRangosEstadoActual.add(nngFacade.getRangoSerie(
                                    redSol.getIdClaveServicio(), redSol.getSna(), redSol.getNumInicio(),
                                    redSol.getProveedorSolicitante()));
                        }
                    } else {
                        // Rango seleccionado para ceder completo o para fraccionar
                        RangoSerieNng rangoOriginal = nngFacade.getRangoSerieByFraccion(
                                redSol.getIdClaveServicio(), redSol.getSna(), redSol.getNumInicio(),
                                redSol.getNumFinal(), redSol.getProveedorSolicitante());

                        existeRango = (rangoOriginal != null);

                        if (redSol.getEstatus().getCodigo().equals(EstadoRedistribucionSolicitada.PENDIENTE)) {
                            if (existeRango) {
                                // Añadimos el rango a la lista de rangos fraccionados
                                if (!RangosSeriesUtil.arrayContains(listaRangosAFraccionar, rangoOriginal)) {
                                    listaRangosAFraccionar.add(rangoOriginal);
                                }

                                // Agregamos las fracciones a la tabla de fracciones.
                                this.rangoAFraccionar = rangoOriginal;
                                this.numInicial = redSol.getNumInicio();
                                this.numFinal = redSol.getNumFinalAsInt();
                                this.aplicarFraccionamiento();
                            }
                        }

                        // Agregamos el rango original a la lista de rangos originales (lista de estado actual)
                        if (existeRango && (!listaRangosEstadoActual.contains(rangoOriginal))) {
                            listaRangosEstadoActual.add(rangoOriginal);
                        }
                    }

                    if (!existeRango) {
                        StringBuffer sbAviso = new StringBuffer();
                        sbAviso.append("Asignatario: ").append(redSol.getProveedorSolicitante().getNombreCorto());
                        sbAviso.append(", Clave Servicio: ").append(redSol.getIdClaveServicio());
                        sbAviso.append(", Sna: ").append(redSol.getSna());
                        sbAviso.append(", Inicio: ").append(redSol.getNumInicio());
                        sbAviso.append(", Final: ").append(redSol.getNumFinal()).append("<br>");
                        avisosRangosCambiados.add(sbAviso.toString());
                    }
                }

                // Habilitamos el botón de guardar siempre y cuando haya liberaciones solicitadas.
                salvarHabilitado = (!listaRedistSol.isEmpty());
                // Mostramos las tablas de Fraccionamiento si existen fracciones pendientes
                usarFraccionamiento = (!listaRangosAFraccionar.isEmpty());
                // Reiniciamos los campos de cantidad de numeración
                this.rangoAFraccionar = null;
                this.numInicial = "0";
                this.numFinal = 0;

                // Avisos de rangos que ya no existen y no se pueden redistribuir
                if (!avisosRangosCambiados.isEmpty()) {
                    StringBuffer sbAviso = new StringBuffer();
                    sbAviso.append("Los siguientes rangos han sido modificados o desasignados:<br>");
                    for (String aviso : avisosRangosCambiados) {
                        sbAviso.append(aviso);
                    }
                    MensajesBean.addErrorMsg(MSG_ID, sbAviso.toString(), "");
                }
            } else {
                // Para Cesiones Terminadas o Canceladas mostramos la información de las Redistribuciones realizadas
                listaRedistSol.clear();
                listaRedistSol.addAll(solicitud.getRedistribucionesSolicitadas());
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        } finally {
            actualizandoCampos = false;
        }
    }

    /**
     * Aplica las redistribuciones seleccionadas por el usuario.
     * @return True si se han aplicado correctamente las redistribuciones.
     */
    private boolean aplicarRedistribuciones() {
        try {
            if (validaRangosARedistribuir()) {
                // Aplicamos las redistribuciones solicitadas
                SolicitudRedistribucionNng solRed = nngFacade.applyRedistribucionesSolicitadas(solicitud);
                // Asociamos la nueva instancia de solicitud al Wizard
                this.getWizard().setSolicitud(solRed);
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
     * Guarda los cambios realizados hasta el momento en el trámite.
     * @return 'True' si se ha podido guardar correctamente.
     */
    private boolean guardarCambios() {
        try {
            // Buscamos las redistribuciones que se han deseleccionado/eliminado del resumen de redistribuciones
            // para eliminarlas de base de datos si se habían almacenado anteriormente.
            ArrayList<RedistribucionSolicitadaNng> redistSolEliminar = new ArrayList<RedistribucionSolicitadaNng>();
            for (RedistribucionSolicitadaNng redistSol : solicitud.getRedistribucionesSolicitadas()) {
                // Procesamos solo las redistribuciones en estado pendiente. Las que estan en estado Cedida
                // ya se han ejecutado o se borrar al cancelarlas.
                if (redistSol.getEstatus().getCodigo().equals(EstadoRedistribucionSolicitada.PENDIENTE)) {
                    if ((!arrayContains(listaRedistSol, redistSol))) {
                        // No podemos eliminar una redistribución solicitada de la lista mientras estamos
                        // recorriendo la misma lista (ConcurrentModificationException).
                        // Por lo tanto la añadimos a otra lista para eliminarla al salir del bucle.
                        redistSolEliminar.add(redistSol);
                    }
                }
            }

            // Eliminamos las RedistribucionesSolicitadas que se han cancelado en la tabla resumen.
            for (RedistribucionSolicitadaNng redistSol : redistSolEliminar) {
                solicitud.removeRedistribucionSolicitada(redistSol);
            }

            // Agregamos a la solicitud las RedistribucionesSolicitadas agregadas a la tabla resumen
            // y las persistimos
            for (RedistribucionSolicitadaNng redistSol : listaRedistSol) {
                if ((!arrayContains(solicitud.getRedistribucionesSolicitadas(), redistSol))) {
                    solicitud.addRedistribucionSolicitada(redistSol);
                }
            }

            // Guardamos los cambios y generamos los nuevos registros
            solicitud = nngFacade.saveSolicitudRedistribucion(solicitud);

            // Actualizamos la solicitud para todos los tabs
            getWizard().setSolicitud(solicitud);

            // Una vez persistidas las RedistribucionesSolicitadas rehacemos la tabla resumen para que todas
            // tengan el id ya generado
            listaRedistSol.clear();
            listaRedistSol.addAll(solicitud.getRedistribucionesSolicitadas());

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

    /**
     * Método invocado al seleccionar todas las filas de la tabla mediante el checkbox de cabecera.
     * @param event ToggleSelectEvent
     */
    public void seleccionPagina(ToggleSelectEvent event) {
        try {
            // Actualizamos la selección múltiple en paginación
            int pagina = ((DataTable) event.getSource()).getPage();
            multiSelectionManager.toggleSelecction(pagina, event.isSelected());

            this.habilitarEdicion();
            this.habilitarFraccionamiento();
            this.actualizarCheckBoxesEliminar();

            if (event.isSelected()) {
                rangoSeleccionado = multiSelectionManager.getLastRegisterSelected();
            } else {
                rangoSeleccionado = null;
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al seleccionar una fila de la tabla de Rangos.
     * @param event SelectEvent
     * @throws Exception ex
     */
    public void seleccionRango(SelectEvent event) {
        try {
            rangoSeleccionado = (RangoSerieNng) event.getObject();

            // Actualizamos la selección múltiple en paginación
            int pagina = ((DataTable) event.getSource()).getPage();
            multiSelectionManager.updateSelection(rangoSeleccionado, pagina, true);

            this.habilitarEdicion();
            this.habilitarFraccionamiento();

            // Si se selecciona un solo rango se autocomplentan los combos de edición
            if (multiSelectionManager.size() == 1) {
                cliente = rangoSeleccionado.getCliente();
                pstArrendatarioSeleccionado = rangoSeleccionado.getArrendatario();
                pstConcesionarioSeleccionado = rangoSeleccionado.getConcesionario();
            } else {
                cliente = null;
                pstArrendatarioSeleccionado = null;
                pstConcesionarioSeleccionado = null;
            }

            // Actualizamos los botones de eliminar.
            this.actualizarCheckBoxesEliminar();

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

            this.habilitarEdicion();
            this.habilitarFraccionamiento();
            this.actualizarCheckBoxesEliminar();

            // Devuelve el último registro seleccionado o null
            rangoSeleccionado = multiSelectionManager.getLastRegisterSelected();

            // Si se selecciona un solo rango se autocomplentan los combos de edición
            if (multiSelectionManager.size() == 1) {
                cliente = rangoSeleccionado.getCliente();
                pstArrendatarioSeleccionado = rangoSeleccionado.getArrendatario();
                pstConcesionarioSeleccionado = rangoSeleccionado.getConcesionario();
            } else {
                cliente = null;
                pstArrendatarioSeleccionado = null;
                pstConcesionarioSeleccionado = null;
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

    /**
     * Método invocado al pulsar el botón 'Agregar'.
     * @param pCheckArrendamiento True para comprobar si existe arrendamiento sobre el rango seleccionado.
     */
    public void agregarRedistribucion(boolean pCheckArrendamiento) {
        try {
            // Validaciones antes de crear la CesionSolicitadaNg
            boolean todoOk = true;

            // Validación de rango arrendado.
            todoOk = !(pCheckArrendamiento && validarArrendamiento());

            // Validaciones RN107 y RN108
            todoOk = todoOk && validarCliente() && validarConcesionario();

            // todoOk es false si no se quiere comprobar el arrendamiento de un rango (por que ya se
            // haya aceptado la modal de aviso) o por que no exista arrendamiento en el rango.
            if (todoOk) {
                // Primero comprobamos que se hayan seleccionado cambios para redistribuir.
                if (hayInfoRedistribucion()) {
                    if (usarFraccionamiento) {
                        if (RangosSeriesUtil.arrayContains(listaRangosEstadoActual, rangoAFraccionar)) {
                            MensajesBean.addErrorMsg(MSG_ID, "El rango ya está agregado para su redistribución.", "");
                        } else {
                            this.redistribuirFracciones();
                            this.seleccionUsarFraccionamiento();
                            this.limpiarEdicion();
                        }
                    } else {
                        // Validamos que no estemos agregando rangos ya incluídos en el resumen o en estado no válido.
                        boolean validacionOk =
                                this.validaRangosSinFraccion(multiSelectionManager.getRegistrosSeleccionados());
                        if (validacionOk) {
                            this.redistribuirRangos();
                            this.limpiarEdicion();
                        }
                    }
                } else {
                    MensajesBean.addErrorMsg(MSG_ID,
                            "Es necesario seleccionar almenos un cambio para redistribuir.", "");
                }

                // Habilitamos/Deshabilitamos el botón de guardado.
                salvarHabilitado = (!listaRedistSol.isEmpty());
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Indica si se han seleccionado campos para redistribuir.
     * @return True si hay algún campo informado.
     */
    public boolean hayInfoRedistribucion() {
        return (pstConcesionarioSeleccionado != null
                || pstArrendatarioSeleccionado != null
                || (!StringUtils.isEmpty(cliente))
                || eliminarArrendatario
                || eliminarCliente
                || eliminarConcesionario);
    }

    /** Limpia los datos de edición. */
    private void limpiarEdicion() {
        rangoSeleccionado = null;
        multiSelectionManager.clear();
        pstArrendatarioSeleccionado = null;
        pstConcesionarioSeleccionado = null;
        usarFraccionamiento = false;
        eliminarArrendatario = false;
        eliminarCliente = false;
        eliminarConcesionario = false;
        this.habilitarEdicion();
        this.habilitarFraccionamiento();
    }

    /**
     * Crea las Redistribuciones solicitadas en función de los rangos seleccionados.
     * @throws Exception en caso de error
     */
    private void redistribuirRangos() throws Exception {
        // Creamos los objetos RedistribucionSolicitadaNg
        for (RangoSerieNng rango : multiSelectionManager.getRegistrosSeleccionados()) {
            listaRedistSol.add(this.getRedistSolicitadaFromRango(rango));
            listaRangosEstadoActual.add(rango);
        }
    }

    /**
     * Crea las Redistribuciones solicitadas en función de las fracciones seleccionados.
     * @throws Exception en caso de error
     */
    private void redistribuirFracciones() throws Exception {
        // Agregamos una cesión solicitada por fracción
        for (RangoSerieNng fraccion : listaRangoFraccionados) {
            // Ignoramos los huecos temporales y agregamos las peticiones fracción
            if (fraccion.getEstatus().getCodigo().equals(EstadoRango.AFECTADO)) {
                // Estado Actual del Rango
                RangoSerieNng rangoOriginal =
                        (RangoSerieNng) RangosSeriesUtil.getRangoInicial(fraccion, listaRangosAFraccionar);
                if (!listaRangosEstadoActual.contains(rangoOriginal)) {
                    listaRangosEstadoActual.add(rangoOriginal);
                }

                // Redistribución Solicitada
                RedistribucionSolicitadaNng redistSol = this.getRedistSolicitadaFromRango(rangoOriginal);
                redistSol.setNumInicio(fraccion.getNumInicio());
                redistSol.setNumFinal(fraccion.getNumFinal());
                listaRedistSol.add(redistSol);
            }
        }
    }

    /**
     * Método que valida los rangos propuestos para agregar a redistribución sin fraccionar.
     * @param pContenedorRangos Lista de Rangos a validar
     * @return boolean 'True' si se cumple la regla
     * @throws Exception en caso de error
     */
    private boolean validaRangosSinFraccion(List<RangoSerieNng> pContenedorRangos) throws Exception {

        if (pContenedorRangos.isEmpty()) {
            MensajesBean.addErrorMsg(MSG_ID, "Es necesario seleccionar almenos una serie para redistribuir.", "");
            return false;
        }

        List<RangoSerieNng> rangosDuplicados = new ArrayList<RangoSerieNng>();
        List<RangoSerieNng> rangosNoAsignados = new ArrayList<RangoSerieNng>();
        for (RangoSerieNng rango : pContenedorRangos) {
            if (RangosSeriesUtil.arrayContains(listaRangosEstadoActual, rango)) {
                rangosDuplicados.add(rango);
            }
            if (!rango.getEstatus().getCodigo().equals(EstadoRango.ASIGNADO)) {
                rangosNoAsignados.add(rango);
            }
        }

        // Rangos que ya se han agregado a redistribuir.
        if (!rangosDuplicados.isEmpty()) {
            StringBuffer sbAvisos = new StringBuffer();
            sbAvisos.append("Los siguientes rangos ya están agregados para redistribuir:<br>");
            for (RangoSerieNng rango : rangosDuplicados) {
                sbAvisos.append("Clave Servicio: ").append(rango.getClaveServicio().getCodigo());
                sbAvisos.append(", Serie: ").append(rango.getId().getSna());
                sbAvisos.append(", Inicio: ").append(rango.getNumInicio()).append("<br>");
            }
            MensajesBean.addErrorMsg(MSG_ID, sbAvisos.toString(), "");
        }

        // Rangos con estado diferente a "Asignado"
        if (!rangosNoAsignados.isEmpty()) {
            StringBuffer sbAvisos = new StringBuffer();
            sbAvisos.append("Los siguientes rangos no están disponibles:<br>");
            for (RangoSerieNng rango : rangosNoAsignados) {
                sbAvisos.append("Clave Servicio: ").append(rango.getClaveServicio().getCodigo());
                sbAvisos.append(", Serie: ").append(rango.getId().getSna());
                sbAvisos.append(", Inicio: ").append(rango.getNumInicio());
                sbAvisos.append(", Estado: ").append(rango.getEstatus().getDescripcion()).append("<br>");
            }
            MensajesBean.addErrorMsg(MSG_ID, sbAvisos.toString(), "");
        }

        return (rangosDuplicados.isEmpty() && rangosNoAsignados.isEmpty());
    }

    /**
     * Método invocado al seleccionar un rango a fraccionar en la tabla de rangos para fraccionar.
     * @param event SelectEvent
     */
    public void seleccionRangoAFraccionar(SelectEvent event) {
        try {
            if (rangoAFraccionar != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Rango Seleccionado: " + rangoAFraccionar.toString());
                }
                String key = rangoAFraccionar.getIdentificadorRango();
                if (!fraccionesByRango.containsKey(key)) {
                    fraccionesByRango.put(key, new ArrayList<IRangoSerie>(1));
                }

                listaRangoFraccionados = ajustarFraccionesRango(rangoAFraccionar);
            } else {
                MensajesBean.addErrorMsg(MSG_ID, "Es necesario seleccionar un rango.", "");
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
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Rango Seleccionado: " + rangoSeleccionado.toString());
                }
                //Validacion que el rango seleccionado no exista ya en la tabla a fraccionar.               
                if(!listaRangosAFraccionar.contains(rangoSeleccionado)){
                	
                	// Solo es posible ceder Rangos asignados
                    if (rangoSeleccionado.getEstatus().getCodigo().equals(EstadoRango.ASIGNADO)) {
                        // El rango no se reserva hasta que no se añadan cesiones sobre él.
                        listaRangosAFraccionar.add(rangoSeleccionado);

                        // Seleccionamos la fila recién añadida a la tabla.
                        rangoAFraccionar = rangoSeleccionado;
                    } else {
                        MensajesBean.addErrorMsg(MSG_ID, "El rango no esta disponible, estado: "
                                + rangoSeleccionado.getEstatus().getDescripcion(), "");
                    }	
                
                }else{
                	 MensajesBean.addErrorMsg(MSG_ID, "El rango actual ya fue seleccionado, por favor elegir otro", "");
                }
                               
            } else {
                MensajesBean.addErrorMsg(MSG_ID, "Es necesario seleccionar un rango.", "");
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar sobre el botón 'Eliminar' en la tabla de Rangos a fraccionar. */
    public void eliminarRangoAFraccionar() {
        try {
            if (rangoAFraccionar != null) {
                if (listaRangosAFraccionar.contains(rangoAFraccionar)) {
                    // Eliminamos el rango de la tabla
                    listaRangosAFraccionar.remove(rangoAFraccionar);

                    // Eliminamos las fracciones asociadas al rango
                    String key = rangoAFraccionar.getIdentificadorRango();
                    if (fraccionesByRango.containsKey(key)) {
                        fraccionesByRango.get(key).clear();
                    }
                    listaRangoFraccionados = new ArrayList<RangoSerieNng>(1);
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

    /** Método invocado al pulsar sobre el botón de 'Eliminar' en la tabla de redistribuciones. */
    public void eliminarRedistribucion() {
        try {
            if (redistSol.getEstatus().getCodigo().equals(EstadoRedistribucionSolicitada.PENDIENTE)) {
                // La redistribución aún no se ha efectuado, basta con eliminar la RedistribuciónSolicitada

                RangoSerieNng rangoInicial = null;
                List<RangoSerieNng> rangosEstatusActual = RangosSeriesUtil
                        .castListaRangoSerieNNG(listaRangosEstadoActual);

                for (RangoSerieNng rango : rangosEstatusActual) {
                    if ((rango.getId().getIdClaveServicio().intValue() == redistSol.getIdClaveServicio().intValue())
                            && (rango.getId().getSna().intValue() == redistSol.getSna().intValue())
                            && (rango.getNumInicioAsInt() <= redistSol.getNumInicioAsInt())
                            && (rango.getNumFinalAsInt() >= redistSol.getNumFinalAsInt())) {
                        rangoInicial = rango;
                        break;
                    }
                }

                // Si no tiene ID, es que aún no se ha persistido y no existe en la solicitud.
                listaRedistSol.remove(redistSol);
                // Actualizamos la tabla de Rangos en Estado Actual.
                listaRangosEstadoActual.remove(rangoInicial);
                // Si ya se habían guardado los cambios, la RedistribuciónSeleccionada existe en la
                // solicitud. Hay que eliminarla.
                if (redistSol.getId() != null) {
                    // Guardamos los cambios y actualizamos la solicitud y redistribuciones solicitadas
                    this.guardarCambios();
                }
                MensajesBean.addInfoMsg(MSG_ID, "Redistribución cancelada correctamente.", "");

            } else {
                // La redistribución ya se ha efectuado, hay que revertir los cambios de redistribución
                PeticionCancelacion checkCancelacion = nngFacade.cancelRedistribucion(redistSol, true);
                if (checkCancelacion.isCancelacionPosible()) {

                    EstadoRedistribucionSolicitada ers = new EstadoRedistribucionSolicitada();
                    ers.setCodigo(EstadoRedistribucionSolicitada.CANCELADO);
                    redistSol.setEstatus(ers);

                    // Guardamos los cambios y actualizamos la solicitud y redistribuciones solicitadas
                    // Al guardar, los cambios se propagan a las RedistribucionesSolicitadas en cascada
                    this.guardarCambios();
                    if (solicitud.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_TERMINADA)) {
                        // RN058: Si todas las redistribuciones están canceladas el trámite pasa a estar
                        // cancelado también.
                        boolean redistCanceladas = true;
                        for (RedistribucionSolicitadaNng redSol : solicitud.getRedistribucionesSolicitadas()) {
                            redistCanceladas = redistCanceladas
                                    && (redSol.getEstatus().getCodigo().equals(
                                            EstadoRedistribucionSolicitada.CANCELADO));
                        }

                        // Cambiamos el estado de la solicitud
                        if (redistCanceladas) {
                            EstadoSolicitud statusCancelada = new EstadoSolicitud();
                            statusCancelada.setCodigo(EstadoSolicitud.SOLICITUD_CANCELADA);
                            solicitud.setEstadoSolicitud(statusCancelada);

                            MensajesBean.addInfoMsg(MSG_ID,
                                    "Se han cancelado todas las redistribuciones."
                                            + " El trámite pasa a estado 'Cancelado'", "");

                            // Guardamos los cambios
                            solicitud = nngFacade.saveSolicitudRedistribucion(solicitud);
                            getWizard().setSolicitud(solicitud);
                        } else {
                            MensajesBean.addInfoMsg(MSG_ID, "Redistribución cancelada correctamente.", "");
                        }
                    } else {
                        MensajesBean.addInfoMsg(MSG_ID, "Redistribución cancelada correctamente.", "");
                    }
                } else {
                    MensajesBean.addErrorMsg(MSG_ID,
                            "No ha sido posible cancelar la redistribución: " + checkCancelacion.getMensajeError(), "");
                }
            }

            // Acabamos de eliminar una redistribución solicitada de la tabla de resúmen. Habilitamos
            // el botón de guardar para salvar los cambios.
            salvarHabilitado = true;
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar sobre el botón de 'Editar' en la tabla de redistribuciones. */
    public void editarRedistribucion() {
        // Habilitamos los combos de edición
        edicionHabilitada = true;
        edicionRegistro = true;
        // Deshabilitamos los combos de Búsqueda y fraccionamiento
        busquedaHabilitada = false;
        fraccionarHabilitado = false;
        usarFraccionamiento = false;
        // Actualizamos los combos de edición con los valores de la redistribución solicitada
        this.updateInfoEdicionFromRedistSol(redistSol);
    }

    /** Método invocado al pulsar sobre el botón de 'Guardar' en la edición de registros de la tabla resumen. */
    public void guardarEdicionRegistro() {
        try {
            if (hayInfoRedistribucion()) {
                if (validarConcesionario()) {
                    // Actualizamos la RedistribucionSolicitadaNg seleccionada
                    this.updateRedistSolicitadaFromEdicion(redistSol);
                    // Actualizamos el formulario
                    this.cancelarEdicionRegistro();
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar sobre el botón de 'Cancelar' en la edición de registros de la tabla resumen. */
    public void cancelarEdicionRegistro() {
        edicionHabilitada = false;
        busquedaHabilitada = true;
        edicionRegistro = false;
        eliminarArrendatario = false;
        eliminarConcesionario = false;
        eliminarCliente = false;
        cliente = null;
        pstArrendatarioSeleccionado = null;
        pstConcesionarioSeleccionado = null;
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
            return new ArrayList<RangoSerieNng>();
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
                                MensajesBean.addErrorMsg(MSG_ID, "La numeración a ceder"
                                        + " ya existe en otra fracción.", "");
                            } else {

                                // Si estamos actualizando campos no es necesario volver a cargar la fracción.
                                RangoSerieNng rangoActualizado = null;
                                if (!actualizandoCampos) {
                                    // Volvemos a comprobar el estado del rango ya que no se modifica hasta que
                                    // no se agrega un fraccionamiento y mientras tanto ha podido ser reservado
                                    // por otro agente.
                                    rangoActualizado = nngFacade.getRangoSerie(
                                            rangoAFraccionar.getClaveServicio().getCodigo(),
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
                                    MensajesBean.addInfoMsg(MSG_ID, "El rango no esta disponible, estado: "
                                            + rangoActualizado.getEstatus().getDescripcion(), "");
                                }
                            }
                        }
                    } else {
                        MensajesBean.addErrorMsg(MSG_ID,
                                "La numeración introducida no corresponde a la franja del rango.", "");
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

    /** Habilita/Deshabilita la edición en función de la selcciónd de series. */
    private void habilitarEdicion() {
        edicionHabilitada = (multiSelectionManager.size() > 0);
    }

    /** Habilita/Deshabilita el fraccionamiento en función de la selcciónd de series. */
    private void habilitarFraccionamiento() {
        fraccionarHabilitado = (multiSelectionManager.size() == 1);
    }

    /**
     * Crea una Redistribución Solicitada con la información del Rango y los campos editados.
     * @param pRango Información del Rango
     * @return RedistribucionSolicitadaNng
     */
    private RedistribucionSolicitadaNng getRedistSolicitadaFromRango(RangoSerieNng pRango) {

        RedistribucionSolicitadaNng redistSol = new RedistribucionSolicitadaNng();
        redistSol.setProveedorSolicitante(solicitud.getProveedorSolicitante());
        redistSol.setSolicitudRedistribucion(solicitud);
        redistSol.setNumFinal(pRango.getNumFinal());
        redistSol.setNumInicio(pRango.getNumInicio());
        redistSol.setIdClaveServicio(pRango.getClaveServicio().getCodigo());
        redistSol.setSna(pRango.getId().getSna());
        redistSol.setBcd(pRango.getBcd());
        redistSol.setConsecutivoAsignacion(pRango.getConsecutivoAsignacion());

        // Información para el Oficio.
        redistSol.setIda(solicitud.getProveedorSolicitante().getIda());

        // Indicamos si se ha fraccionado la numeración con la cesión
        if (usarFraccionamiento) {
            redistSol.setFraccionamientoRango("S");
        } else {
            redistSol.setFraccionamientoRango("N");
        }

        if (eliminarArrendatario) {
            redistSol.setProveedorArrendatario(null);
        } else {
            if (pstArrendatarioSeleccionado != null) {
                redistSol.setProveedorArrendatario(pstArrendatarioSeleccionado);
            } else {
                redistSol.setProveedorArrendatario(pRango.getArrendatario());
            }
        }

        if (eliminarConcesionario) {
            redistSol.setProveedorConcesionario(null);
        } else {
            if (pstConcesionarioSeleccionado != null) {
                redistSol.setProveedorConcesionario(pstConcesionarioSeleccionado);
            } else {
                redistSol.setProveedorConcesionario(pRango.getConcesionario());
            }
        }

        if (eliminarCliente) {
            redistSol.setCliente(null);
        } else {
            if (!StringUtils.isEmpty(cliente)) {
                redistSol.setCliente(cliente);
            } else {
                redistSol.setCliente(pRango.getCliente());
            }
        }

        // RN108 y RN109
        if (solicitud.getProveedorSolicitante().getTipoProveedor().getCdg().equals(TipoProveedor.COMERCIALIZADORA)) {
            // RN108: Para una redistribución donde el concesionario de red cambie en la asignación para el rango o
            // serie que se redistribuye el ABC de red debe cambiar.

            // El Proveedor Concesionario es oblogatorio al ser un PST de tipo Comercializadora. El ABC de la numeración
            // ha de llevar el ABC del Proveedor Concesionario Seleccionado.
            redistSol.setAbc(pstConcesionarioSeleccionado.getAbc());

        } else if (solicitud.getProveedorSolicitante().getTipoProveedor().getCdg().equals(TipoProveedor.AMBOS)) {
            // RN109: Para una redistribución donde el concesionario de red cambie o en este caso se elimine (por ser
            // PST de tipo ambos es posible) en la asignación para el rango o serie que se redistribuye el ABC de red
            // debe cambiar.

            // Caso 1: El Proveedor Concesionario Cambia
            if (pstConcesionarioSeleccionado != null) {
                redistSol.setAbc(pstConcesionarioSeleccionado.getAbc());
            } else if (eliminarConcesionario) {
                // Caso 2: El Proveedor Concesionario se elimina
                redistSol.setAbc(solicitud.getProveedorSolicitante().getAbc());
            } else {
                // Caso 3: No ha variado el proveedor concesionario.
                redistSol.setAbc(pRango.getAbc());
            }
        } else {
            redistSol.setAbc(pRango.getAbc());
        }

        // Estado de la Redistribución
        EstadoRedistribucionSolicitada status = new EstadoRedistribucionSolicitada();
        status.setCodigo(EstadoRedistribucionSolicitada.PENDIENTE);
        redistSol.setEstatus(status);

        return redistSol;
    }

    /**
     * Actualiza la información de una redistribución solicitada ya creada (persistida o no) con nuevos valores de
     * redistribución.
     * @param pRedisSol RedistribucionSolicitadaNng a actualizar
     */
    private void updateRedistSolicitadaFromEdicion(RedistribucionSolicitadaNng pRedisSol) {
        // Si algún campo se queda a null es que se quiere eliminar esa información.
        pRedisSol.setProveedorArrendatario(pstArrendatarioSeleccionado);
        pRedisSol.setProveedorConcesionario(pstConcesionarioSeleccionado);
        pRedisSol.setCliente(cliente);

        // RN108 y RN109
        if (solicitud.getProveedorSolicitante().getTipoProveedor().getCdg().equals(TipoProveedor.COMERCIALIZADORA)) {
            pRedisSol.setAbc(pstConcesionarioSeleccionado.getAbc());
        } else if (solicitud.getProveedorSolicitante().getTipoProveedor().getCdg().equals(TipoProveedor.AMBOS)) {
            if (pstConcesionarioSeleccionado != null) {
                // Modificamos el IDO si se cambia el Concesionario, si so se quedan los valores que estaban.
                pRedisSol.setAbc(pstConcesionarioSeleccionado.getAbc());
            }
        }
    }

    /**
     * Actualiza las selecciones de edición con la información de la RedistribucionSolicitadaNg.
     * @param pRedisSol Información de la RedistribucionSolicitadaNg
     */
    private void updateInfoEdicionFromRedistSol(RedistribucionSolicitadaNng pRedisSol) {
        if (redistSol.getProveedorArrendatario() != null) {
            pstArrendatarioSeleccionado = redistSol.getProveedorArrendatario();
            eliminarArrendatario = false;
        } else {
            eliminarArrendatario = true;
        }

        if (redistSol.getProveedorConcesionario() != null) {
            pstConcesionarioSeleccionado = redistSol.getProveedorConcesionario();
            eliminarConcesionario = false;
        } else {
            eliminarConcesionario = true;
        }

        if (redistSol.getCliente() != null) {
            cliente = redistSol.getCliente();
            eliminarCliente = false;
        } else {
            eliminarCliente = true;
        }
    }

    /**
     * Comprueba que los rangos seleccionados para redistribuir existan y estén en estatus 'Asignado'.
     * @return 'True' si los rangos pueden ser redistribuidos.
     */
    private boolean validaRangosARedistribuir() {
        try {
            // Antes de aplicar las redistribuciones comprobamos que todos los rangos sigan
            // con status 'Asignado'
            List<String> avisosDisponibilidad = new ArrayList<String>();
            List<String> avisosEstatus = new ArrayList<String>();
            boolean existeRango = true;
            boolean statusAsignado = true;
            String status = null;
            for (RedistribucionSolicitadaNng redSol : solicitud.getRedistribucionesSolicitadas()) {
                existeRango = true;
                statusAsignado = true;

                if (redSol.getEstatus().getCodigo().equals(EstadoRedistribucionSolicitada.PENDIENTE)) {
                    if (redSol.getFraccionamientoRango().equals("N")) {
                        RangoSerieNng rangoOriginal = nngFacade.getRangoSerie(
                                redSol.getIdClaveServicio(), redSol.getSna(), redSol.getNumInicio(),
                                redSol.getProveedorSolicitante());

                        existeRango = (rangoOriginal != null);
                        if (existeRango) {
                            if (!rangoOriginal.getEstatus().getCodigo().equals(EstadoRango.ASIGNADO)) {
                                statusAsignado = false;
                                status = rangoOriginal.getEstatus().getDescripcion();
                            }
                        }
                    } else {
                        // Rango seleccionado para ceder completo o para fraccionar
                        RangoSerieNng rangoOriginal = nngFacade.getRangoSerieByFraccion(
                                redSol.getIdClaveServicio(), redSol.getSna(), redSol.getNumInicio(),
                                redSol.getNumFinal(), redSol.getProveedorSolicitante());

                        existeRango = (rangoOriginal != null);
                        if (existeRango) {
                            if (!rangoOriginal.getEstatus().getCodigo().equals(EstadoRango.ASIGNADO)) {
                                statusAsignado = false;
                                status = rangoOriginal.getEstatus().getDescripcion();
                            }
                        }
                    }
                }

                if (!existeRango) {
                    StringBuffer sbAviso = new StringBuffer();
                    sbAviso.append("Asignatario: ").append(redSol.getProveedorSolicitante().getNombreCorto());
                    sbAviso.append(", Clave Servicio: ").append(redSol.getIdClaveServicio());
                    sbAviso.append(", Sna: ").append(redSol.getSna());
                    sbAviso.append(", Inicio: ").append(redSol.getNumInicio());
                    sbAviso.append(", Final: ").append(redSol.getNumFinal()).append("<br>");
                    avisosDisponibilidad.add(sbAviso.toString());
                }

                if (!statusAsignado) {
                    StringBuffer sbAviso = new StringBuffer();
                    sbAviso.append("Asignatario: ").append(redSol.getProveedorSolicitante().getNombreCorto());
                    sbAviso.append(", Clave Servicio: ").append(redSol.getIdClaveServicio());
                    sbAviso.append(", Sna: ").append(redSol.getSna());
                    sbAviso.append(", Inicio: ").append(redSol.getNumInicio());
                    sbAviso.append(", Final: ").append(redSol.getNumFinal());
                    sbAviso.append(", Estatus: ").append(status).append("<br>");
                    avisosEstatus.add(sbAviso.toString());
                }
            }

            // Avisos de rangos que ya no existen y no se pueden redistribuir
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

            // Si no hay avisos, continuamos con la redistribución
            return (avisosEstatus.isEmpty() && avisosDisponibilidad.isEmpty());
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
        return false;
    }

    /**
     * Indica si una lista contiene una RedistribucionSolicitadaNg sin usar los métos Equals de Object.
     * @param pList Lista de objetos RedistribucionSolicitadaNg
     * @param pRedSol RedistribucionSolicitadaNg a comparar
     * @return True si la RedistribucionSolicitadaNg esta en la lista
     * @throws Exception en caso de error
     */
    private boolean arrayContains(List<RedistribucionSolicitadaNng> pList, RedistribucionSolicitadaNng pRedSol)
            throws Exception {

        // Cuando se guardan los cambios las RedistribucionSolicitadaNg tienen otra instancia diferente dada
        // por JPA y, por lo tanto, no se pueden comparar con las almacenadas en la lista interna. Es necesario
        // comparar por los campos clave para saber si nos estamos refiriendo a la misma.

        for (RedistribucionSolicitadaNng rs : pList) {
            if (rs.getIdClaveServicio().intValue() == pRedSol.getIdClaveServicio().intValue()
                    && rs.getSna().intValue() == pRedSol.getSna().intValue()
                    && rs.getNumInicio().equals(pRedSol.getNumInicio())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Comprueba si el rango seleccionado esta arrendado.
     * @return True si el rango seleccionado tiene arrendador.
     * @throws Exception en caso de error.
     */
    private boolean validarArrendamiento() throws Exception {
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
            sbAviso.insert(0, "La asignación que desea redistribuir hace parte de una asignación con arrendamiento. "
                    + "Sus datos son:<br><br>");

            rangoArrendadoMsg = sbAviso.toString();

            // Mostramos la modal.
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('DLG_ArrendatarioRedNng').show()");
        }

        return rangosArrendados;
    }

    /**
     * RN107: Validaciones sobre el campo cliente.
     * @return True se el campo cliente de los rangos es válido
     * @throws Exception en caso de error.
     */
    private boolean validarCliente() throws Exception {
        StringBuilder sbEspecifica = new StringBuilder();
        StringBuilder sbSerie = new StringBuilder();
        boolean errorEspecifica = false;
        boolean errorSerie = false;
        for (RangoSerieNng rango : multiSelectionManager.getRegistrosSeleccionados()) {
            // RN107: Valida que un rango asignado de forma específica (Con cliente) mantenga el cliente.
            if ((!StringUtils.isEmpty(rango.getCliente())) && (StringUtils.isEmpty(this.cliente))) {
                errorEspecifica = true;
                sbEspecifica.append("Clave Servicio: ").append(rango.getClaveServicio().getCodigo()).append(", ");
                sbEspecifica.append("Serie: ").append(rango.getSna()).append(", ");
                sbEspecifica.append("Número Inicial: ").append(rango.getNumInicio()).append(", ");
                sbEspecifica.append("Número Final: ").append(rango.getNumFinal()).append(", ");
                sbEspecifica.append("Cliente: ").append(rango.getCliente()).append("<br>");
            }

            // RN107: Valida que un rango asignado de forma serie (Sin Cliente) no permita agregar un cliente.
            if ((StringUtils.isEmpty(rango.getCliente())) && (!StringUtils.isEmpty(this.cliente))) {
                errorSerie = true;
                sbSerie.append("Clave Servicio: ").append(rango.getClaveServicio().getCodigo()).append(", ");
                sbSerie.append("Serie: ").append(rango.getSna()).append(", ");
                sbSerie.append("Número Inicial: ").append(rango.getNumInicio()).append(", ");
                sbSerie.append("Número Final: ").append(rango.getNumFinal()).append("<br>");
            }
        }

        if (errorEspecifica) {
            sbEspecifica.insert(0,
                    "Los siguientes rangos fueron asignados de forma específica y deben tener un cliente:<br>");
        }

        if (errorSerie) {
            sbSerie.insert(0,
                    "Los siguientes rangos fueron asignados en serie y no deben tener un cliente:<br>");
        }

        if (errorEspecifica && errorSerie) {
            StringBuilder sbCliente = new StringBuilder(sbEspecifica.toString());
            sbCliente.append("<br><br>").append(sbSerie.toString());
            MensajesBean.addErrorMsg(MSG_ID, sbCliente.toString(), "");
            return false;
        } else if (errorEspecifica) {
            MensajesBean.addErrorMsg(MSG_ID, sbEspecifica.toString(), "");
            return false;
        } else if (errorSerie) {
            MensajesBean.addErrorMsg(MSG_ID, sbSerie.toString(), "");
            return false;
        }

        return true;
    }

    /**
     * @return True si la validación es correcta
     * @throws Exception en caso de error
     */
    private boolean validarConcesionario() throws Exception {
        boolean validaTipoComer = true;
        boolean validaTipoAmbos = true;
        if (solicitud.getProveedorSolicitante().getTipoProveedor().getCdg().equals(TipoProveedor.COMERCIALIZADORA)) {
            // RN108: Para una tramite de redistribución donde el solicitante es una comercializadora las asignaciones
            // de numeración obligan a definir un concesionario de red, por lo tanto el SNS no debe permitir eliminar el
            // concesionario de red para una redistribución.
            validaTipoComer = (this.pstConcesionarioSeleccionado != null);
            if (!validaTipoComer) {
                MensajesBean.addErrorMsg(MSG_ID, "El Proveedor Concesionario de Red es obligatorio "
                        + "para numeraciones asignadas a un Proveedor Comercializadora", "");
            } else {
                validaTipoComer = (this.pstConcesionarioSeleccionado.getAbc() != null);
                if (!validaTipoComer) {
                    MensajesBean.addErrorMsg(MSG_ID,
                            "El Proveedor Concesionario de Red seleccionado debe tener ABC", "");
                }
            }
        } else if (solicitud.getProveedorSolicitante().getTipoProveedor().getCdg().equals(TipoProveedor.AMBOS)) {
            if (this.pstConcesionarioSeleccionado != null) {
                // RN109: Si se selecciona un Concesionario para redistribuir la numeración, es necesario que tenga ABC
                validaTipoAmbos = (this.pstConcesionarioSeleccionado.getAbc() != null);
                if (!validaTipoComer) {
                    MensajesBean.addErrorMsg(MSG_ID,
                            "El Proveedor Concesionario de Red seleccionado debe tener ABC", "");
                }
            }
        }

        return (validaTipoComer && validaTipoAmbos);
    }

    /** Método invocado al seleccionar valores de edición. */
    public void actualizarCheckBoxesEliminar() {
        // Reestablece los valores correctos de los botones de eliminar
        if (eliminarCliente) {
            eliminarCliente = StringUtils.isEmpty(cliente);
        }
        if (eliminarArrendatario) {
            eliminarArrendatario = (pstArrendatarioSeleccionado == null);
        }
        if (eliminarConcesionario) {
            eliminarConcesionario = (pstConcesionarioSeleccionado == null);
        }
    }

    /**
     * Indica si el cliente introducido es nulo.
     * @return True si el cliente introducido es nulo.
     */
    public boolean isClienteNull() {
        return StringUtils.isEmpty(cliente);
    }

    // GETTERS & SETTERS

    /**
     * Identificador clave de servicio.
     * @return BigDecimal
     */
    public BigDecimal getClaveServicio() {
        return claveServicio;
    }

    /**
     * Identificador clave de servicio.
     * @param claveServicio BigDecimal
     */
    public void setClaveServicio(BigDecimal claveServicio) {
        this.claveServicio = claveServicio;
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
     * Rango seleccionado en la búsqueda de Series.
     * @return RangoSerieNng
     */
    public RangoSerieNng getRangoSeleccionado() {
        return rangoSeleccionado;
    }

    /**
     * Rango seleccionado en la búsqueda de Series.
     * @param rangoSeleccionado RangoSerieNng
     */
    public void setRangoSeleccionado(RangoSerieNng rangoSeleccionado) {
        this.rangoSeleccionado = rangoSeleccionado;
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
     * Flag que habilita la edición de la serie seleccionada.
     * @return boolean
     */
    public boolean isEdicionHabilitada() {
        return edicionHabilitada;
    }

    /**
     * Flag que habilita la edición de la serie seleccionada.
     * @param edicionHabilitada boolean
     */
    public void setEdicionHabilitada(boolean edicionHabilitada) {
        this.edicionHabilitada = edicionHabilitada;
    }

    /**
     * Flag que habilita la búsqueda de series.
     * @return boolean
     */
    public boolean isBusquedaHabilitada() {
        return busquedaHabilitada;
    }

    /**
     * Flag que habilita la búsqueda de series.
     * @param busquedaHabilitada boolean
     */
    public void setBusquedaHabilitada(boolean busquedaHabilitada) {
        this.busquedaHabilitada = busquedaHabilitada;
    }

    /**
     * Flag que habilita la edición de una redistribución solicitada de la tabla.
     * @return boolean
     */
    public boolean isEdicionRegistro() {
        return edicionRegistro;
    }

    /**
     * Flag que habilita la edición de una redistribución solicitada de la tabla.
     * @param edicionRegistro boolean
     */
    public void setEdicionRegistro(boolean edicionRegistro) {
        this.edicionRegistro = edicionRegistro;
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
     * Cliente indicado para redistribuir.
     * @return String
     */
    public String getCliente() {
        return cliente;
    }

    /**
     * Cliente indicado para redistribuir.
     * @param cliente String
     */
    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    /**
     * Solicitud de Redistribución NNG.
     * @return SolicitudRedistribucionNng
     */
    public SolicitudRedistribucionNng getSolicitud() {
        return solicitud;
    }

    /**
     * Modelo de datos para Lazy Loading en las tablas.
     * @return RangoSerieNngLazyModel
     */
    public RangoSerieNngLazyModel getRangoSerieModel() {
        return rangoSerieModel;
    }

    /**
     * Indica si se desea fraccionar el rango o serie seleccionado.
     * @return boolean
     */
    public boolean isUsarFraccionamiento() {
        return usarFraccionamiento;
    }

    /**
     * Indica si se desea fraccionar el rango o serie seleccionado.
     * @param usarFraccionamiento boolean
     */
    public void setUsarFraccionamiento(boolean usarFraccionamiento) {
        this.usarFraccionamiento = usarFraccionamiento;
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
     * Lista de fracciones sobre una serie o rango seleccionadas.
     * @return List<IRangoSerie>
     */
    public List<IRangoSerie> getListaRangosFracciones() {
        return listaRangosFracciones;
    }

    /**
     * Lista de fracciones sobre una serie o rangos resultado de las fracciones indicadas.
     * @return List<RangoSerieNng>
     */
    public List<RangoSerieNng> getListaRangoFraccionados() {
        return listaRangoFraccionados;
    }

    /**
     * Lista de fracciones sobre una serie o rangos a fraccionar.
     * @return List<IRangoSerie>
     */
    public List<IRangoSerie> getListaRangosAFraccionar() {
        return listaRangosAFraccionar;
    }

    /**
     * Habilita o deshabilita el botón de 'Guardar Cambios'.
     * @return boolean
     */
    public boolean isSalvarHabilitado() {
        return salvarHabilitado;
    }

    /**
     * Lista de Redistribuciones Solicitadas.
     * @return List<RedistribucionSolicitadaNng>
     */
    public List<RedistribucionSolicitadaNng> getListaRedistSol() {
        return listaRedistSol;
    }

    /**
     * Redistribución Solicitada Seleccionada.
     * @return RedistribucionSolicitadaNng
     */
    public RedistribucionSolicitadaNng getRedistSol() {
        return redistSol;
    }

    /**
     * Redistribución Solicitada Seleccionada.
     * @param redistSol RedistribucionSolicitadaNng
     */
    public void setRedistSol(RedistribucionSolicitadaNng redistSol) {
        this.redistSol = redistSol;
    }

    /**
     * Lista de fracciones sobre una serie o rangos a fraccionar.
     * @return List<IRangoSerie>
     */
    public List<IRangoSerie> getListaRangosEstadoActual() {
        return listaRangosEstadoActual;
    }

    /**
     * Listado de Proveedores Arrendatarios.
     * @return List<Proveedor>
     */
    public List<Proveedor> getListaArrendatarios() {
        return listaArrendatarios;
    }

    /**
     * Listado de Proveedores Concesionarios.
     * @return List<Proveedor>
     */
    public List<Proveedor> getListaConcesionarios() {
        return listaConcesionarios;
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
     * @param rangoFraccionado RangoSerieNngt
     */
    public void setRangoFraccionado(RangoSerieNng rangoFraccionado) {
        this.rangoFraccionado = rangoFraccionado;
    }

    /**
     * Rango a fraccionar.
     * @return RangoSerieNng
     */
    public RangoSerieNng getRangoAFraccionar() {
        return rangoAFraccionar;
    }

    /**
     * Rango a fraccionar.
     * @param rangoAFraccionar RangoSerieNng
     */
    public void setRangoAFraccionar(RangoSerieNng rangoAFraccionar) {
        this.rangoAFraccionar = rangoAFraccionar;
    }

    /**
     * Proveedor Arrendatario Seleccionado.
     * @return tProveedor
     */
    public Proveedor getPstArrendatarioSeleccionado() {
        return pstArrendatarioSeleccionado;
    }

    /**
     * Proveedor Arrendatario Seleccionado.
     * @param pstArrendatarioSeleccionado Proveedor
     */
    public void setPstArrendatarioSeleccionado(Proveedor pstArrendatarioSeleccionado) {
        this.pstArrendatarioSeleccionado = pstArrendatarioSeleccionado;
    }

    /**
     * Proveedor Concesionario Seleccionado.
     * @return Proveedor
     */
    public Proveedor getPstConcesionarioSeleccionado() {
        return pstConcesionarioSeleccionado;
    }

    /**
     * Proveedor Concesionario Seleccionado.
     * @param pstConcesionarioSeleccionado Proveedor
     */
    public void setPstConcesionarioSeleccionado(Proveedor pstConcesionarioSeleccionado) {
        this.pstConcesionarioSeleccionado = pstConcesionarioSeleccionado;
    }

    /**
     * Indica si el proveedor solicitante del trámite es Concesionario.
     * @return boolean
     */
    public boolean isPstConcesionario() {
        return pstConcesionario;
    }

    /**
     * Listado de claves de servicio.
     * @return List<ClaveServicio>
     */
    public List<ClaveServicio> getListaClavesServicio() {
        return listaClavesServicio;
    }

    /**
     * Mensaje de información para cesión de rangos con arrendamiento.
     * @return String
     */
    public String getRangoArrendadoMsg() {
        return rangoArrendadoMsg;
    }

    /**
     * Indica si se ha de eliminar el valor de Cliente del rango.
     * @return boolean
     */
    public boolean isEliminarCliente() {
        return eliminarCliente;
    }

    /**
     * Indica si se ha de eliminar el valor de Cliente del rango.
     * @param eliminarCliente boolean
     */
    public void setEliminarCliente(boolean eliminarCliente) {
        this.eliminarCliente = eliminarCliente;
    }

    /**
     * Indica si se ha de eliminar el valor de Concesionario del rango.
     * @return boolean
     */
    public boolean isEliminarConcesionario() {
        return eliminarConcesionario;
    }

    /**
     * Indica si se ha de eliminar el valor de Concesionario del rango.
     * @param eliminarConcesionario boolean
     */
    public void setEliminarConcesionario(boolean eliminarConcesionario) {
        this.eliminarConcesionario = eliminarConcesionario;
    }

    /**
     * Indica si se ha de eliminar el valor de Arrendatario del rango.
     * @return boolean
     */
    public boolean isEliminarArrendatario() {
        return eliminarArrendatario;
    }

    /**
     * Indica si se ha de eliminar el valor de Arrendatario del rango.
     * @param eliminarArrendatario boolean
     */
    public void setEliminarArrendatario(boolean eliminarArrendatario) {
        this.eliminarArrendatario = eliminarArrendatario;
    }

    /**
     * Gestor de Selección Múltiple sobre la tabla Lazy.
     * @return MultiSelectionOnLazyModelManager
     */
    public MultiSelectionOnLazyModelManager<RangoSerieNng> getMultiSelectionManager() {
        return multiSelectionManager;
    }

}
