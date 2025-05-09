package mx.ift.sns.web.backend.ng.redistribucion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mx.ift.sns.modelo.central.Central;
import mx.ift.sns.modelo.filtros.FiltroBusquedaRangos;
import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.ng.RedistribucionSolicitadaNg;
import mx.ift.sns.modelo.ng.SolicitudRedistribucionNg;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.series.IRangoSerie;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.modelo.solicitud.EstadoRedistribucionSolicitada;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
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
 * Clase de respaldo para el Tab/Pestaña de 'Redistribución'.
 */
public class RedistribucionNgTab extends TabWizard {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RedistribucionNgTab.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_Redistribuciones";

    /** Servicio del Bean. */
    private INumeracionGeograficaService ngService;

    /** Campo solicitud redistribucion. */
    private SolicitudRedistribucionNg solicitud;

    /** Identificador de código de región. */
    private String nir;

    /** Identificador de numeración. */
    private String sna;

    /** Filtros para búsqueda de Series y rangos. */
    private FiltroBusquedaRangos filtros = null;

    /** Rango seleccionado en la búsqueda de Series. */
    private RangoSerie rangoSeleccionado;

    /** Modelo de datos para Lazy Loading en las tablas. */
    private RangoSerieNgLazyModel rangoSerieModel = null;

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

    /** Indica si se desea fraccionar el rango o serie seleccionado. */
    private boolean usarFraccionamiento = false;

    /** Indica la cantidad de rango que se ha de fraccionar a partir del número inicial. */
    private String cantidad = "1";

    /** Indica el primer número de la serie o rango a fraccionar. */
    private String numInicial = "";

    /** Indica el último numero reservado para la serie o rango fraccionado. */
    private int numFinal = 0;

    /** Lista de fracciones sobre una serie o rangos resultado de las fracciones indicadas. */
    private List<RangoSerie> listaRangoFraccionados;

    /** Lista de fracciones sobre una serie o rangos a fraccionar. */
    private List<IRangoSerie> listaRangosAFraccionar;

    /** Rango a fraccionar. */
    private RangoSerie rangoAFraccionar;

    /** Fracción de rango en la tabla de Fracciones. */
    private RangoSerie rangoFraccionado;

    /** Información del formulario de búsqueda. */
    private DatosFormRedistribucion infoBusqueda = null;

    /** Información del formulario de edición. */
    private DatosFormRedistribucion infoEdicion = null;

    /** Lista de Fracciones por Rango. */
    private HashMap<String, List<IRangoSerie>> fraccionesByRango;

    /** Lista de Redistribuciones Solicitadas. */
    private List<RedistribucionSolicitadaNg> listaRedistSol;

    /** Redistribución Solicitada Seleccionada. */
    private RedistribucionSolicitadaNg redistSol;

    /** Lista de fracciones sobre una serie o rangos a fraccionar. */
    private List<IRangoSerie> listaRangosEstadoActual;

    /** Indica si estamos actualizando los campos al editar la solicitud. */
    private boolean actualizandoCampos = false;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Gestor de Selección Múltiple sobre la tabla Lazy. */
    private MultiSelectionOnLazyModelManager<RangoSerie> multiSelectionManager;

    /** Constructor DatosRedistribucionTab. */
    public RedistribucionNgTab() {
    }

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'Generales'.
     * @param pWizard Referencia a la clase Wizard que instancia este Tab
     * @param pNgService Servicio de Numeración Geográfica
     */
    public RedistribucionNgTab(Wizard pWizard, INumeracionGeograficaService pNgService) {
        // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
        setWizard(pWizard);

        // Asociamos la solicitud que usaremos en todo el Wizard. Es posible que
        // la referencia del Wizzard padre cambie en función de los pasos previos,
        // por eso hay que actualizar la referencia al llegar a éste tab usando
        // el método actualizaCampos();
        solicitud = (SolicitudRedistribucionNg) getWizard().getSolicitud();

        // Asociamos el Servicio
        ngService = pNgService;
    }

    @Override
    public void cargaValoresIniciales() {
        try {
            // Inicializaciones
            multiSelectionManager = new MultiSelectionOnLazyModelManager<RangoSerie>();
            infoBusqueda = new DatosFormRedistribucion(ngService);
            infoEdicion = new DatosFormRedistribucion(ngService);
            fraccionesByRango = new HashMap<>(1);
            listaRangoFraccionados = new ArrayList<>();
            listaRangosAFraccionar = new ArrayList<>();
            listaRangosEstadoActual = new ArrayList<>();
            listaRedistSol = new ArrayList<>();
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
            multiSelectionManager.clear();
            infoEdicion.clear();
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
                rangoSerieModel = new RangoSerieNgLazyModel();
                rangoSerieModel.setFiltros(filtros);
                rangoSerieModel.setService(ngService);
                rangoSerieModel.setMultiSelectionManager(multiSelectionManager);
            }

            // Reiniciamos los filtros
            filtros.clear();
            rangoSerieModel.clear();

            filtros.setResultadosPagina(registroPorPagina);

            // Info de la Serie
            if (!StringUtils.isEmpty(infoBusqueda.getAbnUbicacion())) {
                filtros.setIdAbn(new BigDecimal(infoBusqueda.getAbnUbicacion()));
            }

            if (!StringUtils.isEmpty(sna)) {
                filtros.setIdSna(new BigDecimal(sna));
            }

            // Info del Rango
            filtros.setTipoRed(infoBusqueda.getTipoRed());
            filtros.setTipoModalidad(infoBusqueda.getTipoModalidad());
            filtros.setAsignatario(solicitud.getProveedorSolicitante());
            filtros.setConcesionario(infoBusqueda.getPstConcesionario());
            filtros.setArrendatario(infoBusqueda.getPstArrendatario());
            filtros.setCentralOrigen(infoBusqueda.getCentralOrigen());
            filtros.setCentralDestino(infoBusqueda.getCentralDestino());
            filtros.setPoblacion(infoBusqueda.getPoblacion());
            filtros.setEstadoOt(infoBusqueda.getEstado());
            filtros.setMunicipio(infoBusqueda.getMunicipio());

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

            PaginatorUtil.resetPaginacion("FORM_SolicitudRedistribucion:TAB_Datos:TBL_Series",
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
        nir = null;
        sna = null;
        infoBusqueda.clear();
        infoEdicion.clear();
        rangoSerieModel = null;
        multiSelectionManager.clear();
        rangoSeleccionado = null;
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
            solicitud = (SolicitudRedistribucionNg) getWizard().getSolicitud();

            // Si la solicitud no está cancelada sigue siendo editable.
            if (!solicitud.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_CANCELADA)) {
                // Estamos en modo actualización. Evita algunas validaciones.
                actualizandoCampos = true;

                Proveedor pstSolicitante = solicitud.getProveedorSolicitante();

                // Listado de PSTS Concesionarios en función del tipo de solicitante
                if (pstSolicitante.getTipoProveedor().getCdg().equals(TipoProveedor.COMERCIALIZADORA)) {
                    // listado de concesionarios de red con los cuales el PST de la solicitud tenga convenio
                    // para utilizar su red en caso que sea comercializadora
                    List<Proveedor> pstConvenios = ngService.findAllConcesionariosFromConvenios(
                            pstSolicitante, pstSolicitante.getTipoRed());
                    infoBusqueda.setListaConcesionarios(pstConvenios);
                    infoEdicion.setListaConcesionarios(pstConvenios);
                } else {
                    // de lo contrario mostrara todos los PST’s registrados en el SNS.
                    infoBusqueda.setListaConcesionarios(ngService.findAllProveedoresActivos());

                    // Los cesionarios que por catálogo ofrezcan la modalidad del tipo de numeración a redistribuir
                    // Tipo Proveedor Concesionario
                    TipoProveedor pstTipoConcesionario = new TipoProveedor();
                    pstTipoConcesionario.setCdg(TipoProveedor.CONCESIONARIO);
                    infoEdicion.setListaConcesionarios(ngService.findAllProveedoresByServicio(
                            pstTipoConcesionario,
                            pstSolicitante.getTipoRed(),
                            pstSolicitante.getId()));
                }
                // Proveedores Arrendatarios para búsqueda de Series
                infoBusqueda.setListaArrendatarios(ngService.findAllProveedoresActivos());
                // Todos los tipos de red para las búsquedas de Series
                infoBusqueda.setListaTiposRed(ngService.findAllTiposRed());
                // Tipo de Red Soportado por el Solicitante para Edición.
                if (solicitud.getProveedorSolicitante().getTipoRed().getCdg().equals(TipoRed.AMBAS)) {
                    infoEdicion.setListaTiposRed(ngService.findAllTiposRed());
                    System.out.println("1 .- Lista red");
                    for(int i=0;i<infoEdicion.getListaTiposRed().size();i++) {
                    	System.out.println("---: "+infoEdicion.getListaTiposRed().get(i));
                    }
                } else {
                    List<TipoRed> redSolicitante = new ArrayList<TipoRed>(1);
                    
                    redSolicitante.add(solicitud.getProveedorSolicitante().getTipoRed());
                    //infoEdicion.setListaTiposRed(redSolicitante);
                    infoEdicion.setListaTiposRed(ngService.findAllTiposRed());
                    System.out.println("2 .- Lista red");
                    for(int i=0;i<infoEdicion.getListaTiposRed().size();i++) {
                    	System.out.println("---: "+infoEdicion.getListaTiposRed().get(i));
                    }
                }

                // Actualizamos las tablas de fracciones y resúmenes
                listaRedistSol.clear();
                listaRangosEstadoActual.clear();
                listaRangosAFraccionar.clear();
                listaRangoFraccionados.clear();
                fraccionesByRango.clear();

                List<String> avisosRangosCambiados = new ArrayList<String>();
                boolean existeRango = true;
                for (RedistribucionSolicitadaNg redSol : solicitud.getRedistribucionesSolicitadas()) {
                    listaRedistSol.add(redSol);
                    existeRango = true;

                    if (redSol.getFraccionamientoRango().equals("N")) {
                        if (ngService.existeRangoSerie(
                                redSol.getIdNir(), redSol.getSna(), redSol.getNumInicio(),
                                redSol.getProveedorSolicitante())) {

                            listaRangosEstadoActual.add(ngService.getRangoSerie(
                                    redSol.getIdNir(), redSol.getSna(), redSol.getNumInicio(),
                                    redSol.getProveedorSolicitante()));
                        } else {
                            existeRango = false;
                        }
                    } else {
                        // Rango seleccionado para ceder completo o para fraccionar
                        RangoSerie rangoOriginal = ngService.getRangoSerieByFraccion(
                                redSol.getIdNir(), redSol.getSna(), redSol.getNumInicio(),
                                redSol.getNumFinal(), redSol.getProveedorSolicitante());

                        existeRango = (rangoOriginal != null);
                        if (redSol.getEstado().getCodigo().equals(EstadoRedistribucionSolicitada.PENDIENTE)) {
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
                        sbAviso.append(", Nir: ").append(redSol.getCdgNir());
                        sbAviso.append(", Sna: ").append(redSol.getSna());
                        sbAviso.append(", Inicio: ").append(redSol.getNumInicio());
                        sbAviso.append(", Final: ").append(redSol.getNumFinal()).append("<br>");
                        avisosRangosCambiados.add(sbAviso.toString());
                    }
                }

                salvarHabilitado = (!listaRedistSol.isEmpty());
                usarFraccionamiento = (!listaRangosAFraccionar.isEmpty());
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
                SolicitudRedistribucionNg solRed = ngService.applyRedistribucionesSolicitadas(solicitud);

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
            ArrayList<RedistribucionSolicitadaNg> redistSolEliminar = new ArrayList<RedistribucionSolicitadaNg>();
            for (RedistribucionSolicitadaNg redistSol : solicitud.getRedistribucionesSolicitadas()) {
                // Procesamos solo las redistribuciones en estado pendiente. Las que estan en estado Cedida
                // ya se han ejecutado o se borrar al cancelarlas.
                if (redistSol.getEstado().getCodigo().equals(EstadoRedistribucionSolicitada.PENDIENTE)) {
                    if ((!arrayContains(listaRedistSol, redistSol))) {
                        // No podemos eliminar una redistribución solicitada de la lista mientras estamos
                        // recorriendo la misma lista (ConcurrentModificationException).
                        // Por lo tanto la añadimos a otra lista para eliminarla al salir del bucle.
                        redistSolEliminar.add(redistSol);
                    }
                }
            }

            // Eliminamos las RedistribucionesSolicitadas que se han cancelado en la tabla resumen.
            for (RedistribucionSolicitadaNg redistSol : redistSolEliminar) {
                solicitud.removeRedistribucionSolicitada(redistSol);
            }

            // Agregamos a la solicitud las RedistribucionesSolicitadas agregadas a la tabla resumen
            // y las persistimos
            
            for (RedistribucionSolicitadaNg redistSol : listaRedistSol) {
                if ((!arrayContains(solicitud.getRedistribucionesSolicitadas(), redistSol))) {
                    solicitud.addRedistribucionSolicitada(redistSol);
                }
            }

         // Guardamos los cambios y generamos los nuevos registros
            solicitud = ngService.saveSolicitudRedistribucion(solicitud);

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
            this.actualizarArrendatarios();

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
     */
    public void seleccionRango(SelectEvent event) {
        try {
            // Actualizamos la selección múltiple en paginación
            rangoSeleccionado = (RangoSerie) event.getObject();
            System.out.println("Seleccionaste "+ rangoSeleccionado.getNumInicio() +" " + rangoSeleccionado.getNumFinal() + " " + rangoSeleccionado.getTipoRed() +" "+ rangoSeleccionado.getTipoModalidad());
            int pagina = ((DataTable) event.getSource()).getPage();
            multiSelectionManager.updateSelection(rangoSeleccionado, pagina, true);

            this.habilitarEdicion();
            this.habilitarFraccionamiento();
            this.actualizarArrendatarios();
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

            this.habilitarEdicion();
            this.habilitarFraccionamiento();
            this.actualizarArrendatarios();

            // Devuelve el último registro seleccionado o null
            rangoSeleccionado = multiSelectionManager.getLastRegisterSelected();
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Actualiza la lista de arrendatarios según el tipo de red de los rangos seleccionados.
     * @throws Exception en caso de error.
     */
    private void actualizarArrendatarios() throws Exception {
        if (!multiSelectionManager.isEmpty()) {
            TipoRed tipoRedRangos = null;
            for (RangoSerie rango : multiSelectionManager.getRegistrosSeleccionados()) {
                if (tipoRedRangos == null) {
                    tipoRedRangos = rango.getTipoRed();
                } else {
                    if (!tipoRedRangos.getCdg().equals(rango.getTipoRed().getCdg())) {
                        // Si hay varios tipos de red ponemos directamente Ambos
                        tipoRedRangos = new TipoRed();
                        tipoRedRangos.setCdg(TipoRed.AMBAS);
                        break;
                    }
                }
            }

            // los PST (concesionarios y comercializadoras) con los cuales el PST solicitante
            // les puede arrendar numeraciones, es decir, aquellos que por catálogo tengan
            // definido prestar el servicio asociado al tipo numeración
            TipoProveedor tipoProveedor = new TipoProveedor();
            tipoProveedor.setCdg(TipoProveedor.AMBOS);
            infoEdicion.setListaArrendatarios(ngService.findAllProveedoresByServicio(
                    tipoProveedor,
                    tipoRedRangos,
                    this.solicitud.getProveedorSolicitante().getId()));
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

    /** Método encargado de generar la redistribucion. */
    public void agregarRedistribucion() {
        try {
            // Primero comprobamos que se hayan seleccionado cambios para redistribuir.
            if (infoEdicion.hayInfoRedistribucion()) {
                // Validamos el tipo de red seleccionado y el Concesionario
                if (validaTipoRed() && validaConcesionario(multiSelectionManager.getRegistrosSeleccionados())) {
                    // Redistribuimos
                    if (usarFraccionamiento) {
                        if (RangosSeriesUtil.arrayContains(listaRangosEstadoActual, rangoAFraccionar)) {
                            MensajesBean.addErrorMsg(MSG_ID, "El rango ya está agregado para su redistribución.", "");
                        } else {
                            // Validamos si la población seleccionada pertenece al rango
                            List<RangoSerie> listaSimple = new ArrayList<RangoSerie>(1);
                            listaSimple.add(rangoAFraccionar);
                            if (validaCambioPoblacion(listaSimple)) {
                                this.redistribuirFracciones();
                                this.seleccionUsarFraccionamiento();
                                this.limpiarEdicion();
                            }
                        }
                    } else {
                        // Validamos que no estemos agregando rangos ya incluídos en el resumen o en estado no válido.
                        boolean validacionOk =
                                this.validaRangosSinFraccion(multiSelectionManager.getRegistrosSeleccionados());
                        if (validacionOk) {
                            // Si existen varios rangos seleccionados y se aplican cambios de ubicación es necesario
                            // que todos los rangos pertenezcan al mismo ABN
                            if (multiSelectionManager.size() > 1) {
                                validacionOk = (validaUbicacionRangos());
                            }
                            // Se valida que población seleccionada para los rangos esté dentro del ABN
                            if (validacionOk) {
                                validacionOk = validaCambioPoblacion(multiSelectionManager.getRegistrosSeleccionados());
                            }
                            // Si todo va bien, agregamos el rango a las redistribuciones y limpiamos los campos.
                            if (validacionOk) {
                                this.redistribuirRangos();
                                this.limpiarEdicion();
                            }
                        }
                    }
                }
            } else {
                MensajesBean.addErrorMsg(MSG_ID, "Es necesario seleccionar almenos una valor para redistribuir.", "");
            }

            // Habilitamos/Deshabilitamos el botón de guardado.
            salvarHabilitado = (!listaRedistSol.isEmpty());
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Autocomplete del combo de centrales de origen.
     * @param query nombre central
     * @return lista central
     * @throws Exception error
     */
    public List<Central> completeCentralOrigen(String query) throws Exception {
        List<Central> result = new ArrayList<Central>();
        result = ngService.findAllCentralesByProveedor(solicitud.getProveedorSolicitante(), query);
        return result;
    }

    /**
     * Autocomplete del combo de centrales destino.
     * @param query nombre central
     * @return lista central
     * @throws Exception error
     */
    public List<Central> completeCentralDestino(String query) throws Exception {
        List<Central> result = new ArrayList<Central>();
        if(!query.isEmpty() || query.compareTo("") != 0)
            result = ngService.findAllCentralesByName(query);
        return result;
    }

    /** Limpia los datos de edición. */
    private void limpiarEdicion() {
        rangoSeleccionado = null;
        multiSelectionManager.clear();
        infoEdicion.clear();
        usarFraccionamiento = false;
        this.habilitarEdicion();
        this.habilitarFraccionamiento();
    }

    /**
     * Crea las Redistribuciones solicitadas en función de los rangos seleccionados.
     * @throws Exception en caso de error
     */
    private void redistribuirRangos() throws Exception {
        // Creamos los objetos RedistribucionSolicitadaNg
        for (RangoSerie rango : multiSelectionManager.getRegistrosSeleccionados()) {
            listaRedistSol.add(this.getRedistSolicitadaFromRango(rango, infoEdicion));
            listaRangosEstadoActual.add(rango);
        }
    }

    /**
     * Crea las Redistribuciones solicitadas en función de las fracciones seleccionados.
     * @throws Exception en caso de error
     */
    private void redistribuirFracciones() throws Exception {
        // Agregamos una cesión solicitada por fracción
        for (RangoSerie fraccion : listaRangoFraccionados) {
            // Ignoramos los huecos temporales y agregamos las peticiones fracción
            if (fraccion.getEstadoRango().getCodigo().equals(EstadoRango.AFECTADO)) {
                // Estado Actual del Rango
                RangoSerie rangoOriginal =
                        (RangoSerie) RangosSeriesUtil.getRangoInicial(fraccion, listaRangosAFraccionar);
                if (!listaRangosEstadoActual.contains(rangoOriginal)) {
                    listaRangosEstadoActual.add(rangoOriginal);
                }
                // Redistribución Solicitada
                RedistribucionSolicitadaNg redistSol = this.getRedistSolicitadaFromRango(rangoOriginal, infoEdicion);
                redistSol.setNumInicio(fraccion.getNumInicio());
                redistSol.setNumFinal(fraccion.getNumFinal());
                listaRedistSol.add(redistSol);
            }
        }
    }

    /**
     * Método que valida las reglas de negocio asociadas a la ubicación de los rangos.
     * @return boolean si se cumple la regla
     */
    private boolean validaUbicacionRangos() {
        // RN027: Si se seleccionan varios rangos todos han de tener el mismo ABN
        if (infoEdicion.getPoblacion() != null) {
            // Solo aplica si se ha seleccionado población para modificar.
            boolean checkNR27 = true;
            boolean checkAbn = true;
            BigDecimal cdgAbn = null;
            for (RangoSerie rango : multiSelectionManager.getRegistrosSeleccionados()) {
                if (cdgAbn == null) {
                    cdgAbn = rango.getPoblacion().getAbn().getCodigoAbn();
                } else {
                    if (rango.getPoblacion().getAbn() == null) {
                        // Es posible que existan poblaciones sin ABN asociado según está definido el modelo
                        checkAbn = false;
                        break;
                    } else {
                        if (cdgAbn.intValue() != rango.getPoblacion().getAbn().getCodigoAbn().intValue()) {
                            checkNR27 = false;
                            break;
                        }
                    }
                }
            }
            if (!checkNR27) {
                MensajesBean.addErrorMsg(MSG_ID, "Los rangos seleccionados han de pertenecer al mismo ABN.", "");
                return false;
            }
            if (!checkAbn) {
                MensajesBean.addErrorMsg(MSG_ID, "Es necesario que todas las poblaciones "
                        + "estén asociadas a un ABN.", "");
                return false;
            }
        }
        return true;
    }

    /**
     * Realiza validaciones sobre el tipo de Red seleccionado y tipo de Red del Concesionario seleccionado.
     * @return boolean si se cumple la regla
     */
    private boolean validaTipoRed() {
        // Validación de Tipo de Modalidad
        if (infoEdicion.getTipoRed() != null) {
            if (infoEdicion.getTipoRed().getCdg().equals(TipoRed.MOVIL) && infoEdicion.getTipoModalidad() == null) {
                MensajesBean.addErrorMsg(MSG_ID,
                        "Es obligatorio seleccionar un tipo de modalidad para redes móviles", "");
                return false;
            }
        }
        return true;
    }

    /**
     * Si se selecciona un Concesionario para Redistribuir, se comprueba que sea del tipo correcto con IDO válido y que
     * soporte el tipo de numeración.
     * @param pContenedorRangos Lista de Rangos a Comprobar.
     * @return True si el Concesionario es correcto y soporta el tipo de red de la numeración.
     */
    private boolean validaConcesionario(List<RangoSerie> pContenedorRangos) {
        // Primero comprobamos si el IDO del concesionario es válido
        if (!validaIdo()) {
            // El método ya pinta las trazas de error.
            return false;
        }

        if (infoEdicion.getPstConcesionario() != null) {
            // Segundo se comprueba que soporte el tipo de red de la numeración.
            List<RangoSerie> avisosRed = new ArrayList<RangoSerie>();
            for (RangoSerie rango : multiSelectionManager.getRegistrosSeleccionados()) {
                String tipoRedNumeracion = rango.getTipoRed().getCdg();
                String tipoRedConcesionario = infoEdicion.getPstConcesionario().getTipoRed().getCdg();

                // Ignoramos numeraciones con tipo de red Ambas
                if (!tipoRedNumeracion.equals(TipoRed.AMBAS)) {
                    // Ignoramos Proveedores con tipo de red Ambas
                    if (!tipoRedConcesionario.equals(TipoRed.AMBAS)) {
                        if (!tipoRedNumeracion.equals(tipoRedConcesionario)) {
                            avisosRed.add(rango);
                        }
                    }
                }
            }

            // Rangos que ya se han agregado a redistribuir.
            if (!avisosRed.isEmpty()) {
                StringBuffer sbAvisos = new StringBuffer();
                sbAvisos.append("Los siguientes rangos no pueden ser arrendados a un concesionario con tipo de red  ");
                sbAvisos.append(infoEdicion.getPstConcesionario().getTipoRed().getDescripcion()).append(":<br>");
                for (RangoSerie rango : avisosRed) {
                    sbAvisos.append("Nir: ").append(rango.getSerie().getNir().getCodigo());
                    sbAvisos.append(", Serie: ").append(rango.getId().getSna());
                    sbAvisos.append(", Inicio: ").append(rango.getNumInicio()).append("<br>");
                }
                MensajesBean.addErrorMsg(MSG_ID, sbAvisos.toString(), "");
                return false;
            }
        }

        return true;
    }

    /**
     * Si se selecciona un Concesionario para Redistribuir al editar una redistribución, se comprueba que soporte el
     * tipo de red de la numeración.
     * @return True si el Concesionario soporta el tipo de red de la numeración.
     */
    private boolean validaConcesionario() {
        // Validación de Concesionar válido
        if (infoEdicion.getPstConcesionario() != null) {
            String tipoRedNumeracion = redistSol.getTipoRed().getCdg();
            String tipoRedPst = infoEdicion.getPstConcesionario().getTipoRed().getCdg();
            // Ignoramos numeraciones con tipo de red Ambas
            if (!tipoRedNumeracion.equals(TipoRed.AMBAS)) {
                // Ignoramos Proveedores con tipo de red Ambas
                if (!tipoRedPst.equals(TipoRed.AMBAS)) {
                    // El tipo de red de la redistSol ha de ser el mismo tipo de red del concesionario
                    if (!tipoRedNumeracion.equals(tipoRedPst)) {
                        MensajesBean.addErrorMsg(MSG_ID,
                                "El concesionario de red seleccionado no soporta el tipo de red de la numeración.", "");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Comprueba las validaciones RN111 y RN112 para el IDO en proveedores concesionarios seleccionados.
     * @return true si el proveedor concesionario a redistribuir es correcto.
     */
    private boolean validaIdo() {
        // Tipo de Proveedor Comercializadora
        boolean pstTipoComer = (solicitud.getProveedorSolicitante().getTipoProveedor().getCdg()
                .equals(TipoProveedor.COMERCIALIZADORA));

        if (infoEdicion.getPstConcesionario() != null) {
            // Tipo de Proveedor Ambos
            boolean pstTipoAmbos = false;
            if (!pstTipoComer) {
                pstTipoAmbos = (solicitud.getProveedorSolicitante().getTipoProveedor().getCdg()
                        .equals(TipoProveedor.AMBOS));
            }

            // RN111: El concesionario seleccionado ha de tener IDO
            if (pstTipoComer || pstTipoAmbos) {
                if (infoEdicion.getPstConcesionario().getIdo() == null) {
                    MensajesBean.addErrorMsg(MSG_ID,
                            "El Proveedor Concesionario de Red seleccionado debe tener IDO", "");
                    return false;
                }
            }
        } else {
            // RN111: Para una tramite de redistribución donde el solicitante es una comercializadora las
            // asignaciones de numeración obligan a definir un concesionario de red, por lo tanto el SNS no debe
            // permitir eliminar el concesionario de red para una redistribución.
            if (pstTipoComer) {
                MensajesBean.addErrorMsg(MSG_ID, "El Proveedor Concesionario de Red es obligatorio "
                        + "para numeraciones asignadas a un Proveedor Comercializadora", "");
                return false;
            }
        }

        return true;
    }

    /**
     * Método que valida las reglas de negocio asociadas al cambio de población.
     * @param pContenedorRangos Lista de Rangos a validar
     * @return boolean 'True' si se cumple la regla
     */
    private boolean validaCambioPoblacion(List<RangoSerie> pContenedorRangos) {
        // RN028: Si se modifica la población de un rango ésta ha de pertenecer al mismo ABN que la numeración.
        boolean checkNR28 = true;
        boolean checkAbn = true;
        if (infoEdicion.getPoblacion() != null) {
            for (RangoSerie rango : pContenedorRangos) {
                // Se valida también que la población del rango esté asociada a un ABN
                if (rango.getPoblacion().getAbn() != null) {
                    checkNR28 = checkNR28 && ngService.existePoblacionEnAbn(
                            infoEdicion.getPoblacion(),
                            rango.getPoblacion().getAbn());
                } else {
                    checkAbn = false;
                    break;
                }
            }
        }
        if (!checkNR28) {
            MensajesBean.addErrorMsg(MSG_ID, "No se puede modificar la ubicación a una población "
                    + "no asociada al ABN del rango.", "");
            return false;
        }
        if (!checkAbn) {
            MensajesBean.addErrorMsg(MSG_ID, "Es necesario que todas las poblaciones "
                    + "estén asociadas a un ABN.", "");
            return false;
        }
        return true;
    }

    /**
     * Método que valida los rangos propuestos para agregar a redistribución sin fraccionar.
     * @param pContenedorRangos Lista de Rangos a validar
     * @return boolean 'True' si se cumple la regla
     * @throws Exception en caso de error
     */
    private boolean validaRangosSinFraccion(List<RangoSerie> pContenedorRangos) throws Exception {
        if (pContenedorRangos.isEmpty()) {
            MensajesBean.addErrorMsg(MSG_ID, "Es necesario seleccionar almenos una serie para redistribuir.", "");
            return false;
        }

        List<RangoSerie> rangosDuplicados = new ArrayList<RangoSerie>();
        List<RangoSerie> rangosNoAsignados = new ArrayList<RangoSerie>();
        for (RangoSerie rango : pContenedorRangos) {
            if (RangosSeriesUtil.arrayContains(listaRangosEstadoActual, rango)) {
                rangosDuplicados.add(rango);
            }
            if (!rango.getEstadoRango().getCodigo().equals(EstadoRango.ASIGNADO)) {
                rangosNoAsignados.add(rango);
            }
        }

        // Rangos que ya se han agregado a redistribuir.
        if (!rangosDuplicados.isEmpty()) {
            StringBuffer sbAvisos = new StringBuffer();
            sbAvisos.append("Los siguientes rangos ya están agregados para redistribuir:<br>");
            for (RangoSerie rango : rangosDuplicados) {
                sbAvisos.append("Nir: ").append(rango.getSerie().getNir().getCodigo());
                sbAvisos.append(", Serie: ").append(rango.getId().getSna());
                sbAvisos.append(", Inicio: ").append(rango.getNumInicio()).append("<br>");
            }
            MensajesBean.addErrorMsg(MSG_ID, sbAvisos.toString(), "");
        }

        // Rangos con estado diferente a "Asignado"
        if (!rangosNoAsignados.isEmpty()) {
            StringBuffer sbAvisos = new StringBuffer();
            sbAvisos.append("Los siguientes rangos no están disponibles:<br>");
            for (RangoSerie rango : rangosNoAsignados) {
                sbAvisos.append("Nir: ").append(rango.getSerie().getNir().getCodigo());
                sbAvisos.append(", Serie: ").append(rango.getId().getSna());
                sbAvisos.append(", Inicio: ").append(rango.getNumInicio());
                sbAvisos.append(", Estado: ").append(rango.getEstadoRango().getDescripcion()).append("<br>");
            }
            MensajesBean.addErrorMsg(MSG_ID, sbAvisos.toString(), "");
        }

        return (rangosDuplicados.isEmpty() && rangosNoAsignados.isEmpty());
    }

    /**
     * Valida que se pueda realizar la edición de un registro con los campos seleccionados.
     * @return True si los campos de redistribución son correctos.
     */
    private boolean validaEdicionRegistro() {
        if (!infoEdicion.hayInfoRedistribucion()) {
            MensajesBean.addErrorMsg(MSG_ID, "Es necesario seleccionar almenos una valor para redistribuir.", "");
            return false;
        }
        // Validamos si la población seleccionada pertenece al rango (RN028)
        if (redistSol.getPoblacion().getAbn() != null) {
            if (!ngService.existePoblacionEnAbn(infoEdicion.getPoblacion(), redistSol.getPoblacion().getAbn())) {
                MensajesBean.addErrorMsg(MSG_ID, "No se puede modificar la ubicación a una población "
                        + "no asociada al abn del rango.", "");
                return false;
            }
        } else {
            MensajesBean.addErrorMsg(MSG_ID, "La población seleccionada ha de estar asociada a un ABN.", "");
            return false;
        }
        if (!checkCamposObligatorios(infoEdicion)) { // El método ya pinta las trazas de error.
            return false;
        }
        if (!validaTipoRed()) { // El método ya pinta las trazas de error.
            return false;
        }
        if (!validaConcesionario()) { // El método ya pinta las trazas de error.
            return false;
        }
        if (!validaIdo()) { // El método ya pinta las trazas de error.
            return false;
        }
        return true;
    }

    /**
     * Método invocado al seleccionar un rango a fraccionar en la tabla de rangos para fraccionar.
     * @param event SelectEvent
     */
    public void seleccionRangoAFraccionar(SelectEvent event) {
        try {
            if (rangoAFraccionar != null) {
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
                // Solo es posible ceder Rangos asignados
                if (rangoSeleccionado.getEstadoRango().getCodigo().equals(EstadoRango.ASIGNADO)) {
                    // El rango no se reserva hasta que no se añadan cesiones sobre él.
                    listaRangosAFraccionar.add(rangoSeleccionado);

                    // Seleccionamos la fila recién añadida a la tabla.
                    rangoAFraccionar = rangoSeleccionado;
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

    /** Método invocado al pulsar sobre el botón de 'Eliminar' en la tabla de redistribuciones. */
    public void eliminarRedistribucion() {
        try {
            if (redistSol.getEstado().getCodigo().equals(EstadoRedistribucionSolicitada.PENDIENTE)) {
                // La redistribución aún no se ha efectuado, basta con eliminar la RedistribuciónSolicitada

                RangoSerie rangoInicial = null;
                List<RangoSerie> rangosEstatusActual = RangosSeriesUtil.castListaRangoSerieNG(listaRangosEstadoActual);
                for (RangoSerie rango : rangosEstatusActual) {
                    if ((rango.getId().getIdNir().intValue() == redistSol.getIdNir().intValue())
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
                PeticionCancelacion checkCancelacion = ngService.cancelRedistribucion(redistSol, true);
                if (checkCancelacion.isCancelacionPosible()) {
                    EstadoRedistribucionSolicitada ers = new EstadoRedistribucionSolicitada();
                    ers.setCodigo(EstadoRedistribucionSolicitada.CANCELADO);
                    redistSol.setEstado(ers);

                    // Guardamos los cambios y actualizamos la solicitud y redistribuciones solicitadas
                    // Al guardar, los cambios se propagan a las RedistribucionesSolicitadas en cascada
                    this.guardarCambios();

                    if (solicitud.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_TERMINADA)) {
                        // RN058: Si todas las redistribuciones están canceladas el trámite pasa a estar
                        // cancelado también.
                        boolean redistCanceladas = true;
                        for (RedistribucionSolicitadaNg redSol : solicitud.getRedistribucionesSolicitadas()) {
                            redistCanceladas = redistCanceladas
                                    && (redSol.getEstado().getCodigo().equals(
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
                            solicitud = ngService.saveSolicitudRedistribucion(solicitud);
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
        try {
            // Rehacemos la lista de Arrendatarios para el Rango que se agregó a redistribución
            TipoProveedor tipoProveedor = new TipoProveedor();
            tipoProveedor.setCdg(this.solicitud.getProveedorSolicitante().getTipoProveedor().getCdg());
            infoEdicion.setListaArrendatarios(ngService.findAllProveedoresByServicioArrendar(
                    tipoProveedor,
                    redistSol.getTipoRed(),
                    redistSol.getProveedorSolicitante().getId()));

            // Actualizamos los combos de edición con los valores de la redistribución solicitada
            this.updateInfoEdicionFromRedistSol(infoEdicion, redistSol);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar sobre el botón de 'Guardar' en la edición de registros de la tabla resumen. */
    public void guardarEdicionRegistro() {
        if (validaEdicionRegistro()) {
            // Actualizamos la RedistribucionSolicitadaNg seleccionada
            this.updateRedistSolicitadaFromEdicion(infoEdicion, redistSol);
            // Actualizamos el formulario
            this.cancelarEdicionRegistro();
        }
    }

    /** Método invocado al pulsar sobre el botón de 'Cancelar' en la edición de registros de la tabla resumen. */
    public void cancelarEdicionRegistro() {
        edicionHabilitada = false;
        busquedaHabilitada = true;
        edicionRegistro = false;
        infoEdicion.clear();
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
                                MensajesBean.addErrorMsg(MSG_ID, "La numeración a ceder"
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

    /** Habilita/Deshabilita la edición en función de la selcción de series. */
    private void habilitarEdicion() {
        edicionHabilitada = (multiSelectionManager.size() > 0);
    }

    /** Habilita/Deshabilita el fraccionamiento en función de la selcción de series. */
    private void habilitarFraccionamiento() {
        fraccionarHabilitado = (multiSelectionManager.size() == 1);
    }

    /**
     * Crea una Redistribución Solicitada con la información del Rango y los campos editados.
     * @param pRango Información del Rango
     * @param pInfoEdicion Información de la Redistribución
     * @return RedistribucionSolicitadaNg
     */
    private RedistribucionSolicitadaNg getRedistSolicitadaFromRango(
            RangoSerie pRango, DatosFormRedistribucion pInfoEdicion) {

        RedistribucionSolicitadaNg redistSol = new RedistribucionSolicitadaNg();
        redistSol.setProveedorSolicitante(solicitud.getProveedorSolicitante());
        redistSol.setSolicitudRedistribucion(solicitud);
        redistSol.setNumFinal(pRango.getNumFinal());
        redistSol.setNumInicio(pRango.getNumInicio());
        redistSol.setIdNir(pRango.getId().getIdNir());
        redistSol.setCdgNir(pRango.getSerie().getNir().getCodigo());
        redistSol.setSna(pRango.getId().getSna());
        redistSol.setIdSerieInicial(pRango.getId().getSna());
        redistSol.setIdAbn(pRango.getSerie().getNir().getAbn().getCodigoAbn());
        redistSol.setIdaPnn(pRango.getIdaPnn());
        redistSol.setConsecutivoAsignacion(pRango.getConsecutivoAsignacion());

        // Indicamos si se ha fraccionado la numeración con la cesión
        if (usarFraccionamiento) {
            redistSol.setFraccionamientoRango("S");
        } else {
            redistSol.setFraccionamientoRango("N");
        }

        if (pInfoEdicion.getCentralDestino() != null) {
            redistSol.setCentralDestino(pInfoEdicion.getCentralDestino());
        } else {
            redistSol.setCentralDestino(pRango.getCentralDestino());
        }

        if (pInfoEdicion.getCentralOrigen() != null) {
            redistSol.setCentralOrigen(pInfoEdicion.getCentralOrigen());
        } else {
            redistSol.setCentralOrigen(pRango.getCentralOrigen());
        }

        if (pInfoEdicion.getPstArrendatario() != null) {
            redistSol.setProveedorArrendatario(pInfoEdicion.getPstArrendatario());
        } else {
            redistSol.setProveedorArrendatario(pRango.getArrendatario());
        }

        if (pInfoEdicion.getPstConcesionario() != null) {
            redistSol.setProveedorConcesionario(pInfoEdicion.getPstConcesionario());
        } else {
            redistSol.setProveedorConcesionario(pRango.getConcesionario());
        }

        if (pInfoEdicion.getTipoRed() != null) {
            redistSol.setTipoRed(pInfoEdicion.getTipoRed());
        } else {
            redistSol.setTipoRed(pRango.getTipoRed());
        }
        
        redistSol.setTipoModalidad(pInfoEdicion.getTipoModalidad());
        /*
        if (pInfoEdicion.getTipoModalidad() != null) {
            redistSol.setTipoModalidad(pInfoEdicion.getTipoModalidad());
        } else {
            redistSol.setTipoModalidad(pRango.getTipoModalidad());
        }
        */
        
        
        if (pInfoEdicion.getPoblacion() != null) {
            redistSol.setPoblacion(pInfoEdicion.getPoblacion());
        } else {
            redistSol.setPoblacion(pRango.getPoblacion());
        }

        // RN111 y RN112
        if (solicitud.getProveedorSolicitante().getTipoProveedor().getCdg().equals(TipoProveedor.COMERCIALIZADORA)) {
            // RN111: Para una redistribución donde el concesionario de red cambie en la asignación para el rango o
            // serie que se redistribuye el IDO de red debe cambiar.

            // El Proveedor Concesionario es oblogatorio al ser un PST de tipo Comercializadora. El ABC de la numeración
            // ha de llevar el IDO del Proveedor Concesionario Seleccionado.
            redistSol.setIdoPnn(infoEdicion.getPstConcesionario().getIdo());

        } else if (solicitud.getProveedorSolicitante().getTipoProveedor().getCdg().equals(TipoProveedor.AMBOS)) {
            // RN112: Para una redistribución donde el concesionario de red cambie o en este caso se elimine (por ser
            // PST de tipo ambos es posible) en la asignación para el rango o serie que se redistribuye el IDO de red
            // debe cambiar

            // Caso 1: El Proveedor Concesionario Cambia
            if (infoEdicion.getPstConcesionario() != null) {
                redistSol.setIdoPnn(infoEdicion.getPstConcesionario().getIdo());
            } else {
                if (pRango.getConcesionario() != null) {
                    // Caso 2: El rango ya tiene concesionario y no se modifica.
                    redistSol.setIdoPnn(pRango.getIdoPnn());
                } else {
                    // Caso 3: El Proveedor Concesionario se elimina o no existe
                    redistSol.setIdoPnn(solicitud.getProveedorSolicitante().getIdo());
                }
            }
        } else {
            redistSol.setIdoPnn(pRango.getIdoPnn());
        }

        // Estado de la Redistribución
        EstadoRedistribucionSolicitada status = new EstadoRedistribucionSolicitada();
        status.setCodigo(EstadoRedistribucionSolicitada.PENDIENTE);
        redistSol.setEstado(status);

        return redistSol;
    }

    /**
     * Actualiza la información de una redistribución solicitada ya creada (persistida o no) con nuevos valores de
     * redistribución.
     * @param pInfoEdicion Información de los valores de redistribución.
     * @param pRedisSol RedistribucionSolicitadaNg a actualizar
     */
    private void updateRedistSolicitadaFromEdicion(DatosFormRedistribucion pInfoEdicion,
            RedistribucionSolicitadaNg pRedisSol) {

        // Cuando se edita un registro, pInfoEdicion se precarga con toda la información
        // de la RedistribucionSolicitadaNg seleccionada. Si algún campo se queda a null es que
        // se quiere eliminar esa información.
        pRedisSol.setCentralDestino(pInfoEdicion.getCentralDestino());
        pRedisSol.setCentralOrigen(pInfoEdicion.getCentralOrigen());
        pRedisSol.setProveedorArrendatario(pInfoEdicion.getPstArrendatario());
        pRedisSol.setProveedorConcesionario(pInfoEdicion.getPstConcesionario());
        pRedisSol.setTipoRed(pInfoEdicion.getTipoRed());
        System.out.println("3.- Se enviará a guardar "+ pInfoEdicion.getTipoModalidad());
        pRedisSol.setTipoModalidad(pInfoEdicion.getTipoModalidad());
        pRedisSol.setPoblacion(pInfoEdicion.getPoblacion());

        // RN111 y RN112
        if (solicitud.getProveedorSolicitante().getTipoProveedor().getCdg().equals(TipoProveedor.COMERCIALIZADORA)) {
            pRedisSol.setIdoPnn(pInfoEdicion.getPstConcesionario().getIdo());
        } else if (solicitud.getProveedorSolicitante().getTipoProveedor().getCdg().equals(TipoProveedor.AMBOS)) {
            if (pInfoEdicion.getPstConcesionario() != null) {
                // Modificamos el IDO si se cambia el Concesionario, si so se quedan los valores que estaban.
                pRedisSol.setIdoPnn(pInfoEdicion.getPstConcesionario().getIdo());
            }
        }
    }

    /**
     * Actualiza las selecciones de edición con la información de la RedistribucionSolicitadaNg.
     * @param pInfoEdicion Información de Edición.
     * @param pRedisSol Información de la RedistribucionSolicitadaNg
     * @throws Exception en caso de error
     */
    private void updateInfoEdicionFromRedistSol(DatosFormRedistribucion pInfoEdicion,
            RedistribucionSolicitadaNg pRedisSol) throws Exception {

        if (redistSol.getCentralDestino() != null) {
            pInfoEdicion.setCentralDestino(redistSol.getCentralDestino());
        }
        if (redistSol.getCentralOrigen() != null) {
            pInfoEdicion.setCentralOrigen(redistSol.getCentralOrigen());
        }
        if (redistSol.getProveedorArrendatario() != null) {
            pInfoEdicion.setPstArrendatario(redistSol.getProveedorArrendatario());
        }
        if (redistSol.getProveedorConcesionario() != null) {
            pInfoEdicion.setPstConcesionario(redistSol.getProveedorConcesionario());
        }
        if (redistSol.getTipoRed() != null) {
            pInfoEdicion.setTipoRed(redistSol.getTipoRed());
        }
        if (redistSol.getTipoModalidad() != null) { // Actualizamos el combo de Modalidad
            pInfoEdicion.seleccionTipoRed();
            System.out.println("Pase por aqui tambien "+redistSol.getTipoModalidad());
            pInfoEdicion.setTipoModalidad(redistSol.getTipoModalidad());
        }
        if (redistSol.getPoblacion() != null) {
            pInfoEdicion.setPoblacion(redistSol.getPoblacion());
            pInfoEdicion.actualizarUbicacionFromPoblacion(redistSol.getPoblacion());
        }
    }

    /**
     * Comprueba si todos los campos obligatorios de una RedistribucionSolicitadaNg están completados.
     * @param pInfoEdicion Información de las compos de Edición
     * @return 'True' si los campos obligatorios de una RedistribucionSolicitadaNg están completados
     */
    private boolean checkCamposObligatorios(DatosFormRedistribucion pInfoEdicion) {

        ArrayList<String> campos = new ArrayList<String>();
        if (pInfoEdicion.getCentralDestino() == null) {
            campos.add("Central Destino");
        }
        if (pInfoEdicion.getCentralOrigen() == null) {
            campos.add("Central Origen");
        }
        if (pInfoEdicion.getTipoRed() == null) {
            campos.add("Tipo de Red");
        }
        if (pInfoEdicion.getTipoRed() != null) {
        	System.out.println("Pase por aqui "+pInfoEdicion.getTipoModalidad());
            if (pInfoEdicion.getTipoRed().getCdg().equals(TipoRed.MOVIL) && pInfoEdicion.getTipoModalidad() == null) {
                campos.add("Tipo de Modalidad");
            }
        }
        if (pInfoEdicion.getPoblacion() == null) {
            campos.add("Población");
        }

        if (campos.isEmpty()) {
            return true;
        } else {
            StringBuffer sbAvisos = new StringBuffer();
            sbAvisos.append("Los siguientes campos son obligatorios:<br>");
            for (String aviso : campos) {
                sbAvisos.append(aviso).append("<br>");
            }
            MensajesBean.addErrorMsg(MSG_ID, sbAvisos.toString(), "");
            return false;
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
            for (RedistribucionSolicitadaNg redSol : solicitud.getRedistribucionesSolicitadas()) {
                existeRango = true;
                statusAsignado = true;

                if (redSol.getEstado().getCodigo().equals(EstadoRedistribucionSolicitada.PENDIENTE)) {
                    if (redSol.getFraccionamientoRango().equals("N")) {
                        if (ngService.existeRangoSerie(
                                redSol.getIdNir(), redSol.getSna(), redSol.getNumInicio(),
                                redSol.getProveedorSolicitante())) {

                            RangoSerie rango = ngService.getRangoSerie(
                                    redSol.getIdNir(), redSol.getSna(), redSol.getNumInicio(),
                                    redSol.getProveedorSolicitante());

                            if (!rango.getEstadoRango().getCodigo().equals(EstadoRango.ASIGNADO)) {
                                statusAsignado = false;
                                status = rango.getEstadoRango().getDescripcion();
                            }
                        } else {
                            existeRango = false;
                        }
                    } else {
                        // Rango seleccionado para ceder completo o para fraccionar
                        RangoSerie rangoOriginal = ngService.getRangoSerieByFraccion(
                                redSol.getIdNir(), redSol.getSna(), redSol.getNumInicio(),
                                redSol.getNumFinal(), redSol.getProveedorSolicitante());

                        if (rangoOriginal != null) {
                            if (!rangoOriginal.getEstadoRango().getCodigo().equals(EstadoRango.ASIGNADO)) {
                                statusAsignado = false;
                                status = rangoOriginal.getEstadoRango().getDescripcion();
                            }
                        } else {
                            existeRango = false;
                        }
                    }
                }

                if (!existeRango) {
                    StringBuffer sbAviso = new StringBuffer();
                    sbAviso.append("Asignatario: ").append(redSol.getProveedorSolicitante().getNombreCorto());
                    sbAviso.append(", Nir: ").append(redSol.getCdgNir());
                    sbAviso.append(", Sna: ").append(redSol.getSna());
                    sbAviso.append(", Inicio: ").append(redSol.getNumInicio());
                    sbAviso.append(", Final: ").append(redSol.getNumFinal()).append("<br>");
                    avisosDisponibilidad.add(sbAviso.toString());
                }

                if (!statusAsignado) {
                    StringBuffer sbAviso = new StringBuffer();
                    sbAviso.append("Asignatario: ").append(redSol.getProveedorSolicitante().getNombreCorto());
                    sbAviso.append(", Nir: ").append(redSol.getCdgNir());
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
    private boolean arrayContains(List<RedistribucionSolicitadaNg> pList, RedistribucionSolicitadaNg pRedSol)
            throws Exception {
        // Cuando se guardan los cambios las RedistribucionSolicitadaNg tienen otra instancia diferente dada
        // por JPA y, por lo tanto, no se pueden comparar con las almacenadas en la lista interna. Es necesario
        // comparar por los campos clave para saber si nos estamos refiriendo a la misma.
        for (RedistribucionSolicitadaNg rs : pList) {
            if (rs.getIdNir().equals(pRedSol.getIdNir())
                    && rs.getSna().equals(pRedSol.getSna())
                    && rs.getNumInicio().equals(pRedSol.getNumInicio())) {
                return true;
            }
        }
        return false;
    }

    // GETTERS & SETTERS

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
     * Rango seleccionado en la búsqueda de Series.
     * @return RangoSerie
     */
    public RangoSerie getRangoSeleccionado() {
        return rangoSeleccionado;
    }

    /**
     * Rango seleccionado en la búsqueda de Series.
     * @param rangoSeleccionado RangoSerie
     */
    public void setRangoSeleccionado(RangoSerie rangoSeleccionado) {
        this.rangoSeleccionado = rangoSeleccionado;
    }

    /**
     * Modelo de datos para Lazy Loading en las tablas.
     * @return RangoSerieNgLazyModel
     */
    public RangoSerieNgLazyModel getRangoSerieModel() {
        return rangoSerieModel;
    }

    /**
     * Habilita o deshabilita el botón de 'Guardar Cambios'.
     * @return boolean
     */
    public boolean isSalvarHabilitado() {
        return salvarHabilitado;
    }

    /**
     * Habilita o deshabilita el botón de 'Guardar Cambios'.
     * @param salvarHabilitado boolean
     */
    public void setSalvarHabilitado(boolean salvarHabilitado) {
        this.salvarHabilitado = salvarHabilitado;
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
     * Lista de fracciones sobre una serie o rangos resultado de las fracciones indicadas.
     * @return List<RangoSerie>
     */
    public List<RangoSerie> getListaRangoFraccionados() {
        return listaRangoFraccionados;
    }

    /**
     * Lista de fracciones sobre una serie o rangos resultado de las fracciones indicadas.
     * @param listaRangoFraccionados List<RangoSerie>
     */
    public void setListaRangoFraccionados(List<RangoSerie> listaRangoFraccionados) {
        this.listaRangoFraccionados = listaRangoFraccionados;
    }

    /**
     * Lista de fracciones sobre una serie o rangos a fraccionar.
     * @return List<IRangoSerie>
     */
    public List<IRangoSerie> getListaRangosAFraccionar() {
        return listaRangosAFraccionar;
    }

    /**
     * Lista de fracciones sobre una serie o rangos a fraccionar.
     * @param listaRangosAFraccionar List<IRangoSerie>
     */
    public void setListaRangosAFraccionar(List<IRangoSerie> listaRangosAFraccionar) {
        this.listaRangosAFraccionar = listaRangosAFraccionar;
    }

    /**
     * Rango a fraccionar.
     * @return RangoSerie
     */
    public RangoSerie getRangoAFraccionar() {
        return rangoAFraccionar;
    }

    /**
     * Rango a fraccionar.
     * @param rangoAFraccionar RangoSerie
     */
    public void setRangoAFraccionar(RangoSerie rangoAFraccionar) {
        this.rangoAFraccionar = rangoAFraccionar;
    }

    /**
     * Información del formulario de búsqueda.
     * @return DatosFormRedistribucion
     */
    public DatosFormRedistribucion getInfoBusqueda() {
        return infoBusqueda;
    }

    /**
     * Información del formulario de búsqueda.
     * @param infoBusqueda DatosFormRedistribucion
     */
    public void setInfoBusqueda(DatosFormRedistribucion infoBusqueda) {
        this.infoBusqueda = infoBusqueda;
    }

    /**
     * Información del formulario de edición.
     * @return DatosFormRedistribucion
     */
    public DatosFormRedistribucion getInfoEdicion() {
        return infoEdicion;
    }

    /**
     * Información del formulario de edición.
     * @param infoEdicion DatosFormRedistribucion
     */
    public void setInfoEdicion(DatosFormRedistribucion infoEdicion) {
        this.infoEdicion = infoEdicion;
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
     * Lista de Redistribuciones Solicitadas.
     * @return List<RedistribucionSolicitadaNg>
     */
    public List<RedistribucionSolicitadaNg> getListaRedistSol() {
        return listaRedistSol;
    }

    /**
     * Lista de Redistribuciones Solicitadas.
     * @param listaRedistSol List<RedistribucionSolicitadaNg>
     */
    public void setListaRedistSol(List<RedistribucionSolicitadaNg> listaRedistSol) {
        this.listaRedistSol = listaRedistSol;
    }

    /**
     * Redistribución Solicitada Seleccionada.
     * @return RedistribucionSolicitadaNg
     */
    public RedistribucionSolicitadaNg getRedistSol() {
        return redistSol;
    }

    /**
     * Redistribución Solicitada Seleccionada.
     * @param redistSol RedistribucionSolicitadaNg
     */
    public void setRedistSol(RedistribucionSolicitadaNg redistSol) {
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
     * Lista de fracciones sobre una serie o rangos a fraccionar.
     * @param listaRangosEstadoActual List<IRangoSerie>
     */
    public void setListaRangosEstadoActual(List<IRangoSerie> listaRangosEstadoActual) {
        this.listaRangosEstadoActual = listaRangosEstadoActual;
    }

    /**
     * Flag que habilita la búsqueda de series.
     * @return 'True' si es posible realizar búsquedas de series.
     */
    public boolean isBusquedaHabilitada() {
        return busquedaHabilitada;
    }

    /**
     * Flag que habilita la búsqueda de series.
     * @param busquedaHabilitada 'True' si es posible realizar búsquedas de series.
     */
    public void setBusquedaHabilitada(boolean busquedaHabilitada) {
        this.busquedaHabilitada = busquedaHabilitada;
    }

    /**
     * Flag que habilita la edición de una redistribución solicitada de la tabla.
     * @return 'True' Para mostrar los botones de edición de registro.
     */
    public boolean isEdicionRegistro() {
        return edicionRegistro;
    }

    /**
     * Flag que habilita la edición de una redistribución solicitada de la tabla.
     * @param edicionRegistro Para mostrar los botones de edición de registro.
     */
    public void setEdicionRegistro(boolean edicionRegistro) {
        this.edicionRegistro = edicionRegistro;
    }

    /**
     * Información de la solcitud de redistribución.
     * @return SolicitudRedistribucionNg
     */
    public SolicitudRedistribucionNg getSolicitud() {
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
