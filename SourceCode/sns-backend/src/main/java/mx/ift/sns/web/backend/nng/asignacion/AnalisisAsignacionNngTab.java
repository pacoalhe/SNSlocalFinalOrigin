package mx.ift.sns.web.backend.nng.asignacion;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroBusquedaRangos;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSeries;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.nng.NumeracionAsignadaNng;
import mx.ift.sns.modelo.nng.NumeracionSolicitadaNng;
import mx.ift.sns.modelo.nng.RangoSerieNng;
import mx.ift.sns.modelo.nng.RangoSerieNngPK;
import mx.ift.sns.modelo.nng.SerieNng;
import mx.ift.sns.modelo.nng.SerieNngPK;
import mx.ift.sns.modelo.nng.SolicitudAsignacionNng;
import mx.ift.sns.modelo.nng.TipoAsignacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;
import mx.ift.sns.utils.number.NumerosUtils;
import mx.ift.sns.web.backend.ac.claveservicio.EdicionClaveServicioBean;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;
import mx.ift.sns.web.backend.lazymodels.multiseleccion.MultiSelectionOnLazyModelManager;
import mx.ift.sns.web.backend.ng.asignacion.manual.analisis.SugerenciaNumeracion;
import mx.ift.sns.web.backend.selectablemodels.NumeracionSeleccionadaNngModel;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que implenta la pestaña de Analisis de una Asignacion NNG.
 * @author X36155QU
 */
public class AnalisisAsignacionNngTab extends TabWizard {

    /** Cantidad maxima que se puede asignar en una serie. */
    private static final int CANTIDAD_MAX = 10000;

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalisisAsignacionNngTab.class);

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_Analisis";

    /** Fachada de Numeración No Geográfica. */
    private INumeracionNoGeograficaFacade nngFacade;

    /** Referencia al Bean de claves de servicio. */
    private EdicionClaveServicioBean claveServicioBean;

    /** Solicitud de asignación. */
    private SolicitudAsignacionNng solicitud;

    /** Numeracion Solicitada. */
    private NumeracionSolicitadaNng numeracionSolicitada;

    /** Lista de claves de servicio. */
    private List<ClaveServicio> listaClavesServicio;

    /** Lista de concesionarios. */
    private List<Proveedor> listaConcesionarios;

    /** Lista de arrendatarios. */
    private List<Proveedor> listaArrendatarios;

    /** Model de la tabla de numeraciones solicitadas. */
    private NumeracionSeleccionadaNngModel numeracionesSolicitadasModel;

    /** Seleccion de la tabla de numeraciones solicitadas. */
    private NumeracionSolicitadaNng selectedNumeracionSolicitada;

    /** Model tabla series ocupadas. */
    private DetalleSerieAnalisisNngLazyModel serieOcupadaModel;

    /** Model tabla series libres. */
    private DetalleSerieAnalisisNngLazyModel serieLibreModel;

    /** Serie ocupada seleccionada. */
    private DetalleSerieAnalisisNng selectedSerieOcupada;

    /** Series libres seleccionadas. */
    private MultiSelectionOnLazyModelManager<DetalleSerieAnalisisNng> selectedSerieLibre;

    /** Rangos seleccionados para asignación. */
    private List<NumeracionAsignadaNng> listaRangos;

    /** Rangos de una serie seleccionada. */
    private List<RangoSerieNng> listaRangosSeries;

    /** Rango ya asignados para una numeración solicitada. */
    private List<RangoSerieNng> listaRangosAsignados;

    /** Numeración a asignar. */
    private String cantidadAsignar;

    /** Numeracion inicial del nuevo rango. */
    private String numeracionInicial;

    /** Solicitud es de tipo Especifica. */
    private boolean especifica;

    /** Proveedor Solicitante es Concesionario. */
    private boolean pstConcesionario;

    /** Proveedor Solicitante es Comercializador. */
    private boolean pstComercializador;

    /** Proveedor Solicitante es de ampos tipos. */
    private boolean pstAmbos;

    /** Habilita la edicion de la numeracion. */
    private boolean editarNumeracion = false;

    /** Lista detalle uso clave servicio. */
    private List<DetalleClaveServicioAnalisisNng> listaDetalleUsoClaves;

    /** Lista sugerencia de rangos seleccionados. */
    private List<SugerenciaNumeracion> sugerenciaNumeracionList;

    /** Sugerencia numeracion seleccionada. */
    private SugerenciaNumeracion sugerenciaNumeracionSelect;

    /** Cabecera de los rango que se van a asiganar. Depende del tipo de asignacion. */
    private String headerTablaRangos;

    /** SNA de la numeracion solicitada. Se mapea aparte para validarlo. */
    private String sna;

    /** Cantidad solicitada de la numeracion. */
    private String cantidadSolicitada;

    /** Habilita seleccion serie libre manual. */
    private boolean habilitaSeleccionManualLibre = false;

    /** Estado Pendiente. */
    private EstadoRango pendiente;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /**
     * Constructor.
     * @param pWizard Wizard.
     * @param facade fachada
     * @param claveServicioBean bean
     */
    public AnalisisAsignacionNngTab(Wizard pWizard, INumeracionNoGeograficaFacade facade,
            EdicionClaveServicioBean claveServicioBean) {

        // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
        setWizard(pWizard);
        setIdMensajes(MSG_ID);

        // Asociamos el Servicio y Bean
        nngFacade = facade;
        this.claveServicioBean = claveServicioBean;

        listaConcesionarios = new ArrayList<Proveedor>();
        listaArrendatarios = new ArrayList<Proveedor>();

        serieLibreModel = new DetalleSerieAnalisisNngLazyModel();
        serieLibreModel.setFacadeServicios(nngFacade);
        serieOcupadaModel = new DetalleSerieAnalisisNngLazyModel();
        serieOcupadaModel.setFacadeServicios(nngFacade);

        pendiente = nngFacade.getEstadoRangoByCodigo(EstadoRango.PENDIENTE);

        numeracionesSolicitadasModel = new NumeracionSeleccionadaNngModel(new ArrayList<NumeracionSolicitadaNng>(0));
        listaRangosAsignados = new ArrayList<RangoSerieNng>();
        listaRangos = new ArrayList<NumeracionAsignadaNng>();

        selectedSerieLibre = new MultiSelectionOnLazyModelManager<DetalleSerieAnalisisNng>();
        serieLibreModel.setMultiSelectionManager(selectedSerieLibre);

        // Cargamos combo de claves de servicio
        listaClavesServicio = nngFacade.findAllClaveServicioActivas();

        // Limpiamos los campos de la numeracion
        numeracionSolicitada = new NumeracionSolicitadaNng();
        sna = null;
        cantidadSolicitada = null;
        editarNumeracion = false;

    }

    /**
     * Agrega numeraciones solicitadas.
     */
    public void agregarNumeracionSolicitada() {

        // Seteamos datos necesarios
        /*if (!pstAmbos) {

            if (numeracionSolicitada.getConcesionario() != null) {
                numeracionSolicitada.setBcd(numeracionSolicitada.getConcesionario().getBcd());
            } else {
                numeracionSolicitada.setBcd(solicitud.getProveedorSolicitante().getBcd());
            }
        }*/

        numeracionSolicitada.setCantidadAsignada(new BigDecimal(0));
        if (especifica) {
            numeracionSolicitada.setCantidadSolicitada(new BigDecimal(Integer.parseInt(numeracionSolicitada
                    .getNumeroFinal()) - Integer.parseInt(numeracionSolicitada.getNumeroInicial()) + 1));
        }
        //numeracionSolicitada.setBcd(solicitud.getProveedorSolicitante().getBcd());
        
        if ((pstConcesionario || pstAmbos) && numeracionSolicitada.getConcesionario() != null) {
            numeracionSolicitada.setBcd(numeracionSolicitada.getConcesionario().getBcd());
        } else {
            numeracionSolicitada.setBcd(solicitud.getProveedorSolicitante().getBcd());
        }

        if (validarNumeracionSolicitada()) {

            // Limpiado el id
            numeracionSolicitada.setId(null);

            solicitud.addNumeracionSolicitada(numeracionSolicitada);
            solicitud.getEstadoSolicitud().setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
            // Cargamos la tablas de numeracion solicitada y asignada
            cargarTablasNumeracion();

            // Limpiamos los campos de la numeracion
            limpiarNumeracionSolicitada();

            setTabHabilitado(true);

        }

    }

    /**
     * Limpia los datos de la numeracion.
     */
    private void limpiarNumeracionSolicitada() {
        numeracionSolicitada = new NumeracionSolicitadaNng();
        sna = null;
        cantidadSolicitada = null;
        editarNumeracion = false;

        // Para provedor solicitante de tipo ambos por defecto el ABC es el propio
        if (pstAmbos) {
            numeracionSolicitada.setAbc(solicitud.getProveedorSolicitante().getAbc());
        }
    }

    /**
     * Edita una numeracion.
     */

    public void editarNumeracion() {
        if (selectedNumeracionSolicitada.getNumeracionesAsignadas().isEmpty()) {
            if (validarNumeracionSolicitada()) {
                int index = solicitud.getNumeracionesSolicitadas().indexOf(selectedNumeracionSolicitada);
                solicitud.getNumeracionesSolicitadas().set(index, numeracionSolicitada);
                solicitud.getEstadoSolicitud().setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
                cargarTablasNumeracion();

                // Limpiamos los campos de la numeracion
                limpiarNumeracionSolicitada();

                setTabHabilitado(true);
            }

        } else {
            MensajesBean.addErrorMsg(MSG_ID,
                    "La numeración solicitada tiene rangos asociados. Por favor cancelelos antes de editarla");
        }
    }

    /**
     * Cancela una numeración solicitada.
     * @param numSol NumeracionSolicitada
     */
    public void cancelarNumeracionSolicitada(NumeracionSolicitadaNng numSol) {

        try {
            if (numSol.getNumeracionesAsignadas().isEmpty()) {

                EstadoSolicitud estadoSolicitud = new EstadoSolicitud();
                estadoSolicitud.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
                // Guardamos y actualizamos las tablas
                solicitud.setEstadoSolicitud(estadoSolicitud);
                // Eliminamos la numeracion solicita
                solicitud.removeNumeracionSolicitada(numSol);

                nngFacade.saveSolicitudAsignacion(solicitud);

                // Limpiamos los campos de la numeracion
                limpiarNumeracionSolicitada();

                setTabHabilitado(true);

            } else {
                MensajesBean.addErrorMsg(MSG_ID,
                        "La numeración solicitada tiene rangos asociados. Por favor cancelelos antes de eliminarla", "");
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0015), "");

        } catch (Exception e) {
            LOGGER.error("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");

        } finally {
            solicitud = nngFacade.getSolicitudAsignacionEagerLoad(solicitud);
            // Recargamos las tablas.
            cargarTablasNumeracion();

        }

    }

    /**
     * Valida una numeracion solicitada especifica.
     */
    public void validarNumeracion() {

        listaRangosAsignados = new ArrayList<RangoSerieNng>();

        try {
            if (selectedNumeracionSolicitada.getValida() == null) {
                SerieNngPK idSerie = new SerieNngPK();
                idSerie.setIdClaveServicio(selectedNumeracionSolicitada.getClaveServicio().getCodigo());
                idSerie.setSna(selectedNumeracionSolicitada.getSna());
                SerieNng serie = nngFacade.getSerieById(idSerie);
                if (serie != null) {

                    // Comprobamos si la numeracion solicitada no entra en conflicto con algun rango ya asignado
                    List<RangoSerieNng> listaRangosAux = new ArrayList<RangoSerieNng>();
                    listaRangosAux = nngFacade.validateRango(
                            selectedNumeracionSolicitada.getClaveServicio().getCodigo(),
                            selectedNumeracionSolicitada.getSna(), selectedNumeracionSolicitada.getNumeroInicial(),
                            selectedNumeracionSolicitada.getNumeroFinal());

                    // Es necesario guardar la numeracion solicitada para generar su id
                    solicitud = nngFacade.saveSolicitudAsignacion(solicitud);
                    numeracionesSolicitadasModel = new NumeracionSeleccionadaNngModel(
                            solicitud.getNumeracionesSolicitadas());
                    NumeracionSolicitadaNng numeracionSolicitada = numeracionesSolicitadasModel
                            .getRowData((String) numeracionesSolicitadasModel
                                    .getRowKey(selectedNumeracionSolicitada));

                    int index = findNumeracionInList(numeracionSolicitada);
                    //index = solicitud.getNumeracionesSolicitadas().indexOf(numeracionSolicitada);

                    for (RangoSerieNng rangoAux : listaRangosAux) {
                        if (!listaRangos.contains(rangoAux)) {
                            listaRangosAsignados.add(rangoAux);
                        }
                    }

                    if (listaRangosAux.isEmpty()) {

                        solicitud.getNumeracionesSolicitadas().get(index)
                                .setCantidadAsignada(selectedNumeracionSolicitada
                                        .getCantidadSolicitada());
                        solicitud.getNumeracionesSolicitadas().get(index).setValida(true);
                        // Creamos los rangos en estado Pendiente
                        RangoSerieNng rango = new RangoSerieNng();

                        // Seteamos el id del rango y estado
                        rango.setId(new RangoSerieNngPK());
                        rango.getId().setIdClaveServicio(selectedNumeracionSolicitada.getClaveServicio().getCodigo());
                        rango.getId().setSna(selectedNumeracionSolicitada.getSna());
                        rango.setEstatus(pendiente);
                        rango.setSerie(serie);

                        // Seteamos los datos de la numeracion solicitada seleccionada
                        rango.setNumeracionSolicitada(solicitud.getNumeracionesSolicitadas().get(index));
                        rango.setAbc(selectedNumeracionSolicitada.getAbc());
                        rango.setArrendatario(selectedNumeracionSolicitada.getArrendatario());
                        rango.setConcesionario(selectedNumeracionSolicitada.getConcesionario());
                        rango.setAsignatario(solicitud.getProveedorSolicitante());
                        rango.setBcd(selectedNumeracionSolicitada.getBcd());
                        rango.setClaveServicio(selectedNumeracionSolicitada.getClaveServicio());
                        rango.setNumInicio(
                                StringUtils.leftPad(selectedNumeracionSolicitada.getNumeroInicial(), 4, '0'));
                        rango.setNumFinal(StringUtils.leftPad(selectedNumeracionSolicitada.getNumeroFinal(), 4, '0'));
                        rango.setCliente(selectedNumeracionSolicitada.getCliente());

                        // Creamos la numeracion asignada y la asociamos a la solicitada
                        NumeracionAsignadaNng numeracionAsignada = new NumeracionAsignadaNng(rango);
                        solicitud.getNumeracionesSolicitadas().get(index).addNumeracionAsignada(numeracionAsignada);
                        listaRangos.add(numeracionAsignada);

                        solicitud.addRangoNng(rango);
                        solicitud.getEstadoSolicitud().setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);

                        setTabHabilitado(true);

                    } else {
                        solicitud.getNumeracionesSolicitadas().get(index).setValida(false);

                        solicitud.getNumeracionesSolicitadas().get(index).setCantidadAsignada(new BigDecimal(0));
                        MensajesBean.addErrorMsg(MSG_ID,
                                "Ya existen numeraciones asignadas para la numeración que está solicitando", "");
                    }

                    // Salvamos la solicitud

                    solicitud = nngFacade.saveSolicitudAsignacion(solicitud);

                }
            } else {

                if (!selectedNumeracionSolicitada.isValida()) {
                    // Aunque la numeracion este validada mostramos las asignaciones conflictivas
                    MensajesBean.addErrorMsg(MSG_ID,
                            "La numeración solicitada ya ha sido validada y "
                                    + "existen numeraciones asignadas para la numeración que está solicitando", "");
                    List<RangoSerieNng> listaRangosAux = new ArrayList<RangoSerieNng>();
                    listaRangosAux = nngFacade.validateRango(
                            selectedNumeracionSolicitada.getClaveServicio().getCodigo(),
                            selectedNumeracionSolicitada.getSna(), selectedNumeracionSolicitada.getNumeroInicial(),
                            selectedNumeracionSolicitada.getNumeroFinal());

                    for (RangoSerieNng rangoAux : listaRangosAux) {
                        if (!listaRangos.contains(rangoAux)) {
                            listaRangosAsignados.add(rangoAux);
                        }
                    }
                } else {
                    MensajesBean.addErrorMsg(MSG_ID, "La numeración solicitada ya ha sido validada.", "");
                }
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0015), "");

        } catch (Exception e) {
            LOGGER.error("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
        } finally {
            // Recargamos las tablas.
            cargarTablasNumeracion();
        }
    }

    private int findNumeracionInList(NumeracionSolicitadaNng numeracionSolicitada){
        int index = 0;
        for( NumeracionSolicitadaNng numeracionSolicitadaNng : solicitud.getNumeracionesSolicitadas() ){

            if( numeracionSolicitadaNng.getId().compareTo(numeracionSolicitada.getId()) == 0 ){
                return index;
            }
            index++;
        }

        return -1;
    }

    /**
     * Carga las tablas de series .
     */
    public void buscarRangos() {
        if (validaCantidadAsignar()) {
            cargaTablasSeries();
        }
    }

    /**
     * Reinicia las tablas de series al reseleccionar una numeracion solicitada.
     */
    public void selectNumeracionSolicitada() {
        listaRangosAsignados = new ArrayList<RangoSerieNng>();
        try {
            if (selectedNumeracionSolicitada == null
                    || (serieLibreModel.getFiltros() != null && !serieLibreModel.getFiltros().getClave()
                            .equals(selectedNumeracionSolicitada.getClaveServicio().getCodigo()))) {
                serieLibreModel = new DetalleSerieAnalisisNngLazyModel();
                serieLibreModel.setFacadeServicios(nngFacade);
                serieLibreModel.setMultiSelectionManager(selectedSerieLibre);
                serieOcupadaModel = new DetalleSerieAnalisisNngLazyModel();
                serieOcupadaModel.setFacadeServicios(nngFacade);
                selectedSerieLibre = new MultiSelectionOnLazyModelManager<DetalleSerieAnalisisNng>();
                serieLibreModel.setMultiSelectionManager(selectedSerieLibre);
                selectedSerieOcupada = null;
                // Deshabilitamos la seleccion manual libre
                habilitaSeleccionManualLibre = false;
            }

            editarNumeracion = true;

            numeracionSolicitada = selectedNumeracionSolicitada.clone();

            // numeracionSolicitada.getNumeracionesAsignadas().clear();

            numeracionSolicitada.setId(null);
            numeracionSolicitada.setValida(null);
            if (numeracionSolicitada.getSna() != null) {
                sna = numeracionSolicitada.getSnaAsString();
            }
            cantidadSolicitada = numeracionSolicitada.getCantidadSolicitada().toString();

            if (selectedNumeracionSolicitada.getValida() != null && !selectedNumeracionSolicitada.isValida()) {
                // Aunque la numeracion este validada mostramos las asignaciones conflictivas
                List<RangoSerieNng> listaRangosAux = new ArrayList<RangoSerieNng>();
                listaRangosAux = nngFacade.validateRango(selectedNumeracionSolicitada.getClaveServicio().getCodigo(),
                        selectedNumeracionSolicitada.getSna(), selectedNumeracionSolicitada.getNumeroInicial(),
                        selectedNumeracionSolicitada.getNumeroFinal());

                for (RangoSerieNng rangoAux : listaRangosAux) {
                    if (!listaRangos.contains(rangoAux)) {
                        listaRangosAsignados.add(rangoAux);
                    }
                }
            }
        } catch (CloneNotSupportedException e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
        }
    }

    /**
     * Carga el detalle de una serie ocupada al seleccionarla.
     */
    public void selectSerieOcupada() {
        FiltroBusquedaRangos filtro = new FiltroBusquedaRangos();
        filtro.setClaveServicio(selectedSerieOcupada.getSerie().getClaveServicio().getCodigo());
        filtro.setIdSna(selectedSerieOcupada.getSerie().getId().getSna());
        filtro.setOrdernarPor("numInicio", FiltroBusquedaRangos.ORDEN_ASC);
        listaRangosSeries = nngFacade.findAllRangos(filtro);

        selectedSerieLibre = new MultiSelectionOnLazyModelManager<DetalleSerieAnalisisNng>();
        serieLibreModel.setMultiSelectionManager(selectedSerieLibre);

        // Deshabilitamos la seleccion manual libre
        habilitaSeleccionManualLibre = false;
    }

    /**
     * Accion al selecionar una serie libre.
     */
    public void selectSerieLibre() {
        selectedSerieOcupada = null;
    }

    /**
     * Método invocado al seleccionar todas las filas de la tabla Series Libres mediante el checkbox de cabecera.
     * @param event ToggleSelectEvent
     */
    public void seleccionPaginaSerieLibre(ToggleSelectEvent event) {
        try {
            // Actualizamos la selección múltiple en paginación
            int pagina = ((DataTable) event.getSource()).getPage();
            selectedSerieLibre.toggleSelecction(pagina, event.isSelected());

            selectedSerieOcupada = null;

            // Habilitamos la seleccion manual libre si hay series seleccionadas
            habilitaSeleccionManualLibre = !selectedSerieLibre.getSeleccionTabla().isEmpty();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
        }
    }

    /**
     * Método invocado al seleccionar una fila de la tabla de Series Libres.
     * @param event SelectEvent
     */
    public void seleccionSerieLibre(SelectEvent event) {
        try {
            // Actualizamos la selección múltiple en paginación
            DetalleSerieAnalisisNng serieLibre = (DetalleSerieAnalisisNng) event.getObject();
            int pagina = ((DataTable) event.getSource()).getPage();
            selectedSerieLibre.updateSelection(serieLibre, pagina, true);

            selectedSerieOcupada = null;

            // Habilitamos la seleccion manual libre si hay series seleccionadas
            habilitaSeleccionManualLibre = !selectedSerieLibre.getSeleccionTabla().isEmpty();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
        }
    }

    /**
     * Método invocado al deseleccionar una fila de la tabla de Series Libres.
     * @param event SelectEvent
     */
    public void deSeleccionSerieLibre(UnselectEvent event) {
        try {
            // Actualizamos la selección múltiple en paginación
            DetalleSerieAnalisisNng serieLibre = (DetalleSerieAnalisisNng) event.getObject();
            int pagina = ((DataTable) event.getSource()).getPage();
            selectedSerieLibre.updateSelection(serieLibre, pagina, false);

            selectedSerieOcupada = null;

            // Habilitamos la seleccion manual libre si hay series seleccionadas
            habilitaSeleccionManualLibre = !selectedSerieLibre.getSeleccionTabla().isEmpty();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
        }
    }

    /**
     * Resetea la informacion del modal de claves de servicio.
     */
    public void reset() {
        // Limpiamos los campos de la numeracion
        limpiarNumeracionSolicitada();
        claveServicioBean.resetValues();
        listaClavesServicio = nngFacade.findAllClaveServicioActivas();

    }

    /**
     * Selecciona alearotariamente series libres consecutivas a las que asignar rango.
     */
    public void seleccionAleatoriaLibre() {

        try {
            if (validaCantidadAsignar()) {
                // Calculamos el numero de series a obtener aleatoriamente
                int n = (Integer.parseInt(cantidadAsignar) / CANTIDAD_MAX);
                if ((Integer.parseInt(cantidadAsignar) % CANTIDAD_MAX) > 0) {
                    n++;
                }

                List<SerieNng> listaSeries = new ArrayList<SerieNng>();
                listaSeries = nngFacade
                        .findRandomSeriesLibreByClaveServicio(selectedNumeracionSolicitada.getClaveServicio(), n);
                creaRangos(listaSeries);

                setTabHabilitado(true);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
        }

    }

    /**
     * Seleccion manual de series libres para asignar rangos.
     */
    public void seleccionManualLibre() {
        try {
            if (validaCantidadAsignar()) {
                List<SerieNng> listaSeries = new ArrayList<SerieNng>();
                for (DetalleSerieAnalisisNng detalle : selectedSerieLibre.getRegistrosSeleccionados()) {
                    listaSeries.add(detalle.getSerie());
                }
                creaRangos(listaSeries);
                setTabHabilitado(true);
            }
        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
        }
    }

    /**
     * Selecciona una serie aleatoriamente con capacidad suficiente para asignar un rango por una cantida de
     * numeraciones.
     */
    public void seleccionAleatoriaOcupado() {

        try {
            if (validaCantidadAsignar()) {
                RangoSerieNng rango = nngFacade.getRandomRangoByClaveServicio(
                        selectedNumeracionSolicitada.getClaveServicio(), Integer.parseInt(cantidadAsignar));

                if (rango != null && Integer.parseInt(cantidadAsignar) <= CANTIDAD_MAX) {
                    // Es necesario guardar la numeracion solicitada para generar su id
                    solicitud = nngFacade.saveSolicitudAsignacion(solicitud);
                    numeracionesSolicitadasModel = new NumeracionSeleccionadaNngModel(
                            solicitud.getNumeracionesSolicitadas());
                    NumeracionSolicitadaNng numeracionSolicitada = numeracionesSolicitadasModel
                            .getRowData((String) numeracionesSolicitadasModel
                                    .getRowKey(selectedNumeracionSolicitada));
                    BigDecimal cantidadAsignada = selectedNumeracionSolicitada.getCantidadAsignada();
                    int numSolIndex = solicitud.getNumeracionesSolicitadas().indexOf(numeracionSolicitada);
                    selectedNumeracionSolicitada.setCantidadAsignada(cantidadAsignada
                            .add(new BigDecimal(cantidadAsignar)));
                    solicitud.getNumeracionesSolicitadas().get(numSolIndex)
                            .setCantidadAsignada(cantidadAsignada.add(new BigDecimal(cantidadAsignar)));

                    // Creamos el rango en estado Pendiente
                    RangoSerieNng rangoConsecutivo = new RangoSerieNng();
                    // Seteamos el id del rango y estado
                    rangoConsecutivo.setId(new RangoSerieNngPK());
                    rangoConsecutivo.getId().setIdClaveServicio(rango.getId().getIdClaveServicio());
                    rangoConsecutivo.getId().setSna(rango.getId().getSna());
                    rangoConsecutivo.setEstatus(pendiente);

                    rangoConsecutivo.setSerie(rango.getSerie());
                    // Seteamos los datos de la numeracion solicitada seleccionada
                    rangoConsecutivo.setNumeracionSolicitada(solicitud.getNumeracionesSolicitadas().get(numSolIndex));
                    rangoConsecutivo.setAbc(solicitud.getNumeracionesSolicitadas().get(numSolIndex).getAbc());
                    rangoConsecutivo.setArrendatario(solicitud.getNumeracionesSolicitadas().get(numSolIndex)
                            .getArrendatario());
                    rangoConsecutivo.setConcesionario(solicitud.getNumeracionesSolicitadas().get(numSolIndex)
                            .getConcesionario());
                    rangoConsecutivo.setAsignatario(solicitud.getNumeracionesSolicitadas().get(numSolIndex)
                            .getSolicitudAsignacion()
                            .getProveedorSolicitante());
                    rangoConsecutivo.setBcd(solicitud.getNumeracionesSolicitadas().get(numSolIndex).getBcd());
                    rangoConsecutivo.setClaveServicio(solicitud.getNumeracionesSolicitadas().get(numSolIndex)
                            .getClaveServicio());

                    rangoConsecutivo.setNumInicio(String.format("%04d", rango.getNumFinalAsInt() + 1));

                    rangoConsecutivo.setNumFinal(String.format("%04d",
                            rango.getNumFinalAsInt() + Integer.parseInt(cantidadAsignar)));

                    // Creamos la numeracion asignada y la asociamos a la solicitada
                    NumeracionAsignadaNng numeracionAsignada = new NumeracionAsignadaNng(rangoConsecutivo);
                    solicitud.getNumeracionesSolicitadas().get(numSolIndex).addNumeracionAsignada(numeracionAsignada);
                    listaRangos.add(numeracionAsignada);

                    solicitud.addRangoNng(rangoConsecutivo);
                    solicitud.getEstadoSolicitud().setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
                    // Salvamos la solicitud
                    solicitud = nngFacade.saveSolicitudAsignacion(solicitud);

                    setTabHabilitado(true);
                } else {
                    MensajesBean.addErrorMsg(MSG_ID, "No hay rangos disponibles para la cantidad solicitada.", "");
                }
            }
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0015), "");

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
        } finally {

            cargarTablasNumeracion();
            cargaTablasSeries();
        }
    }

    /**
     * Guarda los cambios realizados hasta el momento en el trámite.
     * @return 'True' si se ha podido guardar correctamente.
     */
    private boolean guardarCambios() {
        try {
            solicitud = nngFacade.saveSolicitudAsignacion(solicitud);

            // Actualizamos la nueva instancia en el Wizard padre.
            getWizard().setSolicitud(solicitud);

            return true;

        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0015), "");
            return false;
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
            return false;
        } finally {
            solicitud = nngFacade.getSolicitudAsignacionEagerLoad(solicitud);
            cargarTablasNumeracion();
        }
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
     * Selecciona el rango con la numeracion inicial y final introducidas.
     */
    public void seleccionarRango() {

        if (numeracionInicial == null) {
            MensajesBean.addErrorMsg(MSG_ID, "Ingrese un valor en Numeración a asignar", "");

        } else if (!StringUtils.isNumeric(numeracionInicial)) {
            MensajesBean.addErrorMsg(MSG_ID, "Numeración a asignar invalida", "");
        } else if (Integer.parseInt(numeracionInicial) < 1) {
            MensajesBean.addErrorMsg(MSG_ID, "Numeración a asignar invalida", "");
        } else if (validarNumeracionInicial()) {
            try {
                // Es necesario guardar la numeracion solicitada para generar su id
                solicitud = nngFacade.saveSolicitudAsignacion(solicitud);
                numeracionesSolicitadasModel = new NumeracionSeleccionadaNngModel(
                        solicitud.getNumeracionesSolicitadas());
                NumeracionSolicitadaNng numeracionSolicitada = numeracionesSolicitadasModel
                        .getRowData((String) numeracionesSolicitadasModel
                                .getRowKey(selectedNumeracionSolicitada));
                BigDecimal cantidadAsignada = selectedNumeracionSolicitada.getCantidadAsignada();
                int numSolIndex = solicitud.getNumeracionesSolicitadas().indexOf(numeracionSolicitada);

                selectedNumeracionSolicitada.setCantidadAsignada(cantidadAsignada.add(new BigDecimal(cantidadAsignar)));
                solicitud.getNumeracionesSolicitadas().get(numSolIndex)
                        .setCantidadAsignada(cantidadAsignada.add(new BigDecimal(cantidadAsignar)));

                RangoSerieNng rango = new RangoSerieNng();
                // Seteamos el id del rango y estado
                rango.setId(new RangoSerieNngPK());
                rango.getId().setIdClaveServicio(selectedSerieOcupada.getSerie().getId().getIdClaveServicio());
                rango.getId().setSna(selectedSerieOcupada.getSerie().getId().getSna());
                rango.setEstatus(pendiente);
                rango.setSerie(selectedSerieOcupada.getSerie());

                // Seteamos los datos de la numeracion solicitada seleccionada
                rango.setNumeracionSolicitada(solicitud.getNumeracionesSolicitadas().get(numSolIndex));
                rango.setAbc(solicitud.getNumeracionesSolicitadas().get(numSolIndex).getAbc());
                rango.setArrendatario(solicitud.getNumeracionesSolicitadas().get(numSolIndex).getArrendatario());
                rango.setConcesionario(solicitud.getNumeracionesSolicitadas().get(numSolIndex).getConcesionario());
                rango.setAsignatario(solicitud.getNumeracionesSolicitadas().get(numSolIndex).getSolicitudAsignacion()
                        .getProveedorSolicitante());
                rango.setBcd(solicitud.getNumeracionesSolicitadas().get(numSolIndex).getBcd());
                rango.setClaveServicio(solicitud.getNumeracionesSolicitadas().get(numSolIndex).getClaveServicio());
                rango.setNumInicio(String.format("%04d", Integer.parseInt(numeracionInicial)));
                rango.setNumFinal(String.format("%04d",
                        Integer.parseInt(numeracionInicial) + Integer.parseInt(cantidadAsignar) - 1));

                // Creamos la numeracion asignada y la asociamos a la solicitada
                NumeracionAsignadaNng numeracionAsignada = new NumeracionAsignadaNng(rango);
                solicitud.getNumeracionesSolicitadas().get(numSolIndex).addNumeracionAsignada(numeracionAsignada);
                listaRangos.add(numeracionAsignada);

                solicitud.addRangoNng(rango);
                solicitud.getEstadoSolicitud().setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
                // Salvamos la solicitud
                solicitud = nngFacade.saveSolicitudAsignacion(solicitud);

                setTabHabilitado(true);

            } catch (RegistroModificadoException rme) {
                MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0015), "");

            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
            } finally {
                solicitud = nngFacade.getSolicitudAsignacionEagerLoad(solicitud);
                // Recargamos las tablas.
                cargarTablasNumeracion();
                cargaTablasSeries();
            }
        }
    }

    /**
     * Crear una lista de sugerencias de rangos.
     */
    public void sugerirRango() {

        Integer numIni = 0;
        sugerenciaNumeracionList = new ArrayList<SugerenciaNumeracion>();
        SugerenciaNumeracion sugerencia = new SugerenciaNumeracion();
        try {
            if (numIni + Integer.parseInt(cantidadAsignar) - 1 < listaRangosSeries.get(0).getNumInicioAsInt()) {
                sugerencia.setNumInicial(new BigDecimal(numIni));
                sugerencia.setNumFinal(new BigDecimal(numIni + Integer.parseInt(cantidadAsignar) - 1));
                sugerenciaNumeracionList.add(sugerencia);
            }
            numIni = listaRangosSeries.get(0).getNumFinalAsInt() + 1;
            for (int i = 0; i < listaRangosSeries.size() - 1; i++) {
                if (numIni > listaRangosSeries.get(i).getNumFinalAsInt()
                        && numIni + Integer.parseInt(cantidadAsignar) - 1 < listaRangosSeries.get(i + 1)
                                .getNumInicioAsInt()) {
                    sugerencia = new SugerenciaNumeracion();
                    sugerencia.setNumInicial(new BigDecimal(numIni));
                    sugerencia.setNumFinal(new BigDecimal(numIni + Integer.parseInt(cantidadAsignar) - 1));
                    sugerenciaNumeracionList.add(sugerencia);
                }
                numIni = listaRangosSeries.get(i + 1).getNumFinalAsInt() + 1;
            }
            if (numIni > listaRangosSeries.get(listaRangosSeries.size() - 1).getNumFinalAsInt()
                    && numIni + Integer.parseInt(cantidadAsignar) - 1 < CANTIDAD_MAX) {
                sugerencia = new SugerenciaNumeracion();
                sugerencia.setNumInicial(new BigDecimal(numIni));
                sugerencia.setNumFinal(new BigDecimal(numIni + Integer.parseInt(cantidadAsignar) - 1));
                sugerenciaNumeracionList.add(sugerencia);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
        }

    }

    /**
     * Cancela un rango.
     * @param rango RangoSerieNng
     */
    public void cancelarRango(NumeracionAsignadaNng numeracionAsignada) {
        try {
            // Recalculamos la cantidad asignada de la numeracion solicitada.
            int numSolIndex = findNumeracionInList(numeracionAsignada.getNumeracionSolicitada()); //solicitud.getNumeracionesSolicitadas().indexOf(numeracionAsignada.getNumeracionSolicitada());

            // Comprobamos que la numeracion tiene un rango correspondiente o si la numeracion esta libre
            StringBuffer sbAviso = new StringBuffer();
            RangoSerieNng rango = nngFacade.getRangoSerie(numeracionAsignada.getClaveServicio().getCodigo(),
                    numeracionAsignada.getSna(), numeracionAsignada.getInicioRango(),
                    solicitud.getProveedorSolicitante());
            boolean rangoOk = false;
            if (rango != null && solicitud.getId().compareTo(rango.getSolicitud().getId()) == 0) {
                listaRangos.remove(numeracionAsignada);
                NumeracionSolicitadaNng numSol = solicitud.getNumeracionesSolicitadas().get(numSolIndex);
                numSol.removeNumeracionAsignada(numeracionAsignada);

                rangoOk = true;
            } else if (rango == null
                    && nngFacade.validateRango(numeracionAsignada.getClaveServicio().getCodigo(),
                            numeracionAsignada.getSna(), numeracionAsignada.getInicioRango(),
                            numeracionAsignada.getFinRango()).isEmpty()) {
                // Si la numeracion esta libre tambien es correcta
                listaRangos.remove(numeracionAsignada);
                NumeracionSolicitadaNng numSol = solicitud.getNumeracionesSolicitadas().get(numSolIndex);
                numSol.removeNumeracionAsignada(numeracionAsignada);
                rangoOk = true;
            } else {
                // Si la numeracion no es correcta avisamos

                sbAviso.append(" Clave de Servicio: ").append(numeracionAsignada.getClaveServicio().getCodigo());
                sbAviso.append(", SNA: ").append(numeracionAsignada.getSna());
                sbAviso.append(", Inicio: ").append(numeracionAsignada.getInicioRango());
                sbAviso.append(", Final: ").append(numeracionAsignada.getFinRango());
                rangoOk = false;

            }

            // Si el rango se puede eliminar modificamos la numeracion solicitada asociada a el.
            if (rangoOk) {
                BigDecimal cantidadAsignada = solicitud.getNumeracionesSolicitadas().get(numSolIndex)
                        .getCantidadAsignada();
                solicitud
                        .getNumeracionesSolicitadas()
                        .get(numSolIndex)
                        .setCantidadAsignada(
                                new BigDecimal(
                                        cantidadAsignada.intValue()
                                                - (numeracionAsignada.getFinRangoAsInt()
                                                        - numeracionAsignada.getInicioRangoAsInt() + 1)));
                solicitud.getNumeracionesSolicitadas().get(numSolIndex).setValida(null);
                solicitud.getNumeracionesSolicitadas().get(numSolIndex).removeNumeracionAsignada(numeracionAsignada);
                EstadoSolicitud estadoSolicitud = new EstadoSolicitud();
                estadoSolicitud.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);

                if (rango != null) {
                    // Eliminamos el rango de la BD.
                    solicitud.removeRango(rango);
                    nngFacade.removeRango(rango);
                }
                // Guardamos la solicitud
                solicitud.setEstadoSolicitud(estadoSolicitud);
                solicitud = nngFacade.saveSolicitudAsignacion(solicitud);
                setTabHabilitado(true);
            } else {
                MensajesBean.addErrorMsg(MSG_ID,
                        "Las siguiente numeración ha sido cedida o redistribuida y no se puede cancelar:");
                MensajesBean.addErrorMsg(MSG_ID, sbAviso.toString(), "");
            }

        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0015), "");

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
        } finally {
            solicitud = nngFacade.getSolicitudAsignacionEagerLoad(solicitud);
            // Recargamos las tablas.
            cargarTablasNumeracion();
            cargaTablasSeries();
        }

    }

    /**
     * Accion al seleccionar un rango sugerido.
     * @param event seleccion
     */
    public void sugerenciaOnRowSelect(SelectEvent event) {

        // Añadimos la sugerencia a la tabla de rangos seleccionados
        sugerenciaNumeracionSelect = (SugerenciaNumeracion) event.getObject();

        try {
            // Es necesario guardar la numeracion solicitada para generar su id
            solicitud = nngFacade.saveSolicitudAsignacion(solicitud);
            numeracionesSolicitadasModel = new NumeracionSeleccionadaNngModel(
                    solicitud.getNumeracionesSolicitadas());
            NumeracionSolicitadaNng numeracionSolicitada = numeracionesSolicitadasModel
                    .getRowData((String) numeracionesSolicitadasModel
                            .getRowKey(selectedNumeracionSolicitada));
            BigDecimal cantidadAsignada = selectedNumeracionSolicitada.getCantidadAsignada();
            int numSolIndex = solicitud.getNumeracionesSolicitadas().indexOf(numeracionSolicitada);
            solicitud.getNumeracionesSolicitadas().get(numSolIndex)
                    .setCantidadAsignada(cantidadAsignada.add(new BigDecimal(cantidadAsignar)));

            RangoSerieNng rango = new RangoSerieNng();
            // Seteamos el id del rango y estado
            rango.setId(new RangoSerieNngPK());
            rango.getId().setIdClaveServicio(selectedSerieOcupada.getSerie().getId().getIdClaveServicio());
            rango.getId().setSna(selectedSerieOcupada.getSerie().getId().getSna());
            rango.setEstatus(pendiente);
            rango.setSerie(selectedSerieOcupada.getSerie());

            // Seteamos los datos de la numeracion solicitada seleccionada
            rango.setNumeracionSolicitada(solicitud.getNumeracionesSolicitadas().get(numSolIndex));
            rango.setAbc(solicitud.getNumeracionesSolicitadas().get(numSolIndex).getAbc());
            rango.setArrendatario(solicitud.getNumeracionesSolicitadas().get(numSolIndex).getArrendatario());
            rango.setConcesionario(solicitud.getNumeracionesSolicitadas().get(numSolIndex).getConcesionario());
            rango.setAsignatario(solicitud.getNumeracionesSolicitadas().get(numSolIndex).getSolicitudAsignacion()
                    .getProveedorSolicitante());
            rango.setBcd(solicitud.getNumeracionesSolicitadas().get(numSolIndex).getBcd());
            rango.setClaveServicio(solicitud.getNumeracionesSolicitadas().get(numSolIndex).getClaveServicio());
            rango.setNumInicio(String.format("%04d", sugerenciaNumeracionSelect.getNumInicial().intValue()));
            rango.setNumFinal(String.format("%04d", sugerenciaNumeracionSelect.getNumFinal().intValue()));

            // Creamos la numeracion asignada y la asociamos a la solicitada
            NumeracionAsignadaNng numeracionAsignada = new NumeracionAsignadaNng(rango);
            solicitud.getNumeracionesSolicitadas().get(numSolIndex).addNumeracionAsignada(numeracionAsignada);
            listaRangos.add(numeracionAsignada);

            solicitud.addRangoNng(rango);
            solicitud.getEstadoSolicitud().setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
            // Salvamos y recargamos las tablas.
            solicitud = nngFacade.saveSolicitudAsignacion(solicitud);

            setTabHabilitado(true);

        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0015), "");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
        } finally {
            solicitud = nngFacade.getSolicitudAsignacionEagerLoad(solicitud);
            // Recargamos las tablas.
            cargarTablasNumeracion();
            cargaTablasSeries();
        }

    }

    /**
     * Valida si la numeracion seleccionada se puede elegir.
     * @return boolean
     * @throws Exception error
     */
    private boolean validarNumeracionInicial() {
        boolean valida = false;
        try {
            if (Integer.parseInt(numeracionInicial) + Integer.parseInt(cantidadAsignar) - 1 > 9999) {
                valida = false;
            } else if (listaRangosSeries.isEmpty()) {
                valida = true;
            } else if (Integer.parseInt(numeracionInicial) + Integer.parseInt(cantidadAsignar) - 1 < listaRangosSeries
                    .get(0)
                    .getNumInicioAsInt()) {
                valida = true;
            } else if (Integer.parseInt(numeracionInicial) > listaRangosSeries.get(listaRangosSeries.size() - 1)
                    .getNumFinalAsInt()) {
                valida = true;
            } else {

                for (int i = 0; i < listaRangosSeries.size() - 1; i++) {
                    if (Integer.parseInt(numeracionInicial) > listaRangosSeries.get(i).getNumFinalAsInt()
                            && Integer.parseInt(numeracionInicial)
                                    + Integer.parseInt(cantidadAsignar) - 1
                                    < listaRangosSeries.get(i + 1).getNumInicioAsInt()) {
                        valida = true;
                        break;
                    }

                }
            }
            if (!valida) {
                MensajesBean.addErrorMsg(MSG_ID, "No es posible seleccionar el rango", "");
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
        return valida;
    }

    /**
     * Valida la cantidad a asignar.
     * @return boolean
     */
    private boolean validaCantidadAsignar() {
        if (cantidadAsignar == null) {
            MensajesBean.addErrorMsg(MSG_ID, "Ingrese un valor en Numeración a asignar", "");
            return false;
        } else if (!StringUtils.isNumeric(cantidadAsignar.toString()) || Integer.parseInt(cantidadAsignar) < 1) {
            MensajesBean.addErrorMsg(MSG_ID, "Numeración a asignar invalida", "");
            return false;
        } else if (Integer.parseInt(cantidadAsignar) > selectedNumeracionSolicitada.getCantidadSolicitada().intValue()
                - selectedNumeracionSolicitada.getCantidadAsignada().intValue()) {
            MensajesBean.addErrorMsg(MSG_ID,
                    "La numeración a asignar no se puede ser mayor a la numeración solicitada pendiente de asignar", "");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Crea los rango para asignar.
     * @param listaSeries series
     */
    private void creaRangos(List<SerieNng> listaSeries) {

        try {
            int n = (Integer.parseInt(cantidadAsignar) / CANTIDAD_MAX);
            if ((Integer.parseInt(cantidadAsignar) % CANTIDAD_MAX) > 0) {
                n++;
            }

            solicitud = nngFacade.saveSolicitudAsignacion(solicitud);
            numeracionesSolicitadasModel = new NumeracionSeleccionadaNngModel(
                    solicitud.getNumeracionesSolicitadas());
            NumeracionSolicitadaNng numeracionSolicitada = numeracionesSolicitadasModel
                    .getRowData((String) numeracionesSolicitadasModel
                            .getRowKey(selectedNumeracionSolicitada));

            BigDecimal cantidadAsignada = selectedNumeracionSolicitada.getCantidadAsignada();

            int numSolIndex =  findNumeracionInList(numeracionSolicitada);//solicitud.getNumeracionesSolicitadas().indexOf(numeracionSolicitada);
            solicitud.getNumeracionesSolicitadas().get(numSolIndex)
                    .setCantidadAsignada(cantidadAsignada.add(new BigDecimal(cantidadAsignar)));
            selectedNumeracionSolicitada.setCantidadAsignada(cantidadAsignada.add(new BigDecimal(cantidadAsignar)));

            int cont = 1;
            for (SerieNng serie : listaSeries) {
                if (cont <= n) {
                    RangoSerieNng rango = new RangoSerieNng();
                    // Seteamos el id del rango y estado
                    rango.setId(new RangoSerieNngPK());
                    rango.getId().setIdClaveServicio(serie.getId().getIdClaveServicio());
                    rango.getId().setSna(serie.getId().getSna());
                    rango.setEstatus(pendiente);
                    rango.setSerie(serie);
                    // Seteamos los datos de la numeracion solicitada seleccionada
                    rango.setNumeracionSolicitada(solicitud.getNumeracionesSolicitadas().get(numSolIndex));
                    rango.setAbc(solicitud.getNumeracionesSolicitadas().get(numSolIndex).getAbc());
                    rango.setArrendatario(solicitud.getNumeracionesSolicitadas().get(numSolIndex).getArrendatario());
                    rango.setConcesionario(solicitud.getNumeracionesSolicitadas().get(numSolIndex).getConcesionario());
                    rango.setAsignatario(solicitud.getNumeracionesSolicitadas().get(numSolIndex)
                            .getSolicitudAsignacion()
                            .getProveedorSolicitante());
                    rango.setBcd(solicitud.getNumeracionesSolicitadas().get(numSolIndex).getBcd());
                    rango.setClaveServicio(solicitud.getNumeracionesSolicitadas().get(numSolIndex).getClaveServicio());
                    rango.setNumInicio(RangoSerieNng.NUM_MIN);
                    if (cont < n) {
                        rango.setNumFinal(RangoSerieNng.NUM_MAX);
                    } else {
                        if ((Integer.parseInt(cantidadAsignar) % CANTIDAD_MAX) > 0) {
                            rango.setNumFinal(String.format("%04d",
                                    (Integer.parseInt(cantidadAsignar) % CANTIDAD_MAX) - 1));
                        } else {
                            rango.setNumFinal(RangoSerieNng.NUM_MAX);
                        }
                    }

                    // Creamos la numeracion asignada y la asociamos a la solicitada
                    NumeracionAsignadaNng numeracionAsignada = new NumeracionAsignadaNng(rango);
                    solicitud.getNumeracionesSolicitadas().get(numSolIndex).addNumeracionAsignada(numeracionAsignada);
                    listaRangos.add(numeracionAsignada);

                    cont++;
                    solicitud.addRangoNng(rango);

                }
            }
            // Salvamos la solicitud
            if (!listaSeries.isEmpty()) {
                solicitud.getEstadoSolicitud().setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
            }
            solicitud = nngFacade.saveSolicitudAsignacion(solicitud);
        } catch (NumberFormatException e) {
            LOGGER.error("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");

        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0015), "");

        } catch (Exception e) {
            LOGGER.error("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
        } finally {

            cargarTablasNumeracion();
            cargaTablasSeries();
        }
    }

    /**
     * Carga las tablas de series .
     */
    private void cargaTablasSeries() {

        if (selectedNumeracionSolicitada != null) {
            // Cargamos datos para series libres
            serieLibreModel.clear();
            FiltroBusquedaSeries filtroLibre = new FiltroBusquedaSeries();
            filtroLibre.setSerieLibre(true);
            filtroLibre.setRangosDisponibles(true);
            filtroLibre.setClave(selectedNumeracionSolicitada.getClaveServicio().getCodigo());
            serieLibreModel.setFiltros(filtroLibre);
            serieLibreModel.setFacadeServicios(nngFacade);

            // Cargamos datos para series ocupadas
            serieOcupadaModel.clear();
            FiltroBusquedaSeries filtroOcupada = new FiltroBusquedaSeries();
            filtroOcupada.setSerieLibre(false);
            filtroOcupada.setRangosDisponibles(true);
            filtroOcupada.setClave(selectedNumeracionSolicitada.getClaveServicio().getCodigo());
            serieOcupadaModel.setFiltros(filtroOcupada);
            serieOcupadaModel.setFacadeServicios(nngFacade);

            // Si esta seleccionada una serie ocupada cargamos el detalle de sus rangos
            if (selectedSerieOcupada != null) {
                FiltroBusquedaRangos filtro = new FiltroBusquedaRangos();
                filtro.setClaveServicio(selectedSerieOcupada.getSerie().getClaveServicio().getCodigo());
                filtro.setIdSna(selectedSerieOcupada.getSerie().getId().getSna());
                filtro.setOrdernarPor("numInicio", FiltroBusquedaRangos.ORDEN_ASC);
                listaRangosSeries = nngFacade.findAllRangos(filtro);
            }
        }

    }

    /**
     * Valida los datos de una numeracion solicitada.
     * @return boolean
     */
    private boolean validarNumeracionSolicitada() {
        boolean valido = true;
        if (numeracionSolicitada.getClaveServicio() == null) {
            valido = false;
            MensajesBean.addErrorMsg(MSG_ID, "El campo 'Clave de Servicio' es obligatorio", "");
        }

        if (pstComercializador && numeracionSolicitada.getConcesionario() == null) {
            valido = false;
            MensajesBean.addErrorMsg(MSG_ID, "El campo 'Concesionario de Red' es obligatorio", "");
        }
        if (especifica) {

            if (!StringUtils.isEmpty(sna)) {
                if (StringUtils.isNumeric(sna)) {
                    if (Integer.parseInt(sna) >= SerieNng.MIN_SERIE_NNG
                            && Integer.parseInt(sna) <= SerieNng.MAX_SERIE_NNG) {
                        numeracionSolicitada.setSna(new BigDecimal(sna));
                    } else {
                        valido = false;
                        MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0013), "");
                    }

                } else {
                    valido = false;
                    MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0013), "");
                }
            } else {
                valido = false;
                MensajesBean.addErrorMsg(MSG_ID, "El campo 'Serie' es obligatorio", "");
            }

            if (numeracionSolicitada.getNumeroInicial() != null) {
                if (!StringUtils.isNumeric(numeracionSolicitada.getNumeroInicial())) {
                    valido = false;
                    MensajesBean.addErrorMsg(MSG_ID, "El campo 'Numeracion Inicial' es invalido", "");
                }
            } else {
                valido = false;
                MensajesBean.addErrorMsg(MSG_ID, "El campo 'Numeracion Inicial' es obligatorio", "");
            }
            if (numeracionSolicitada.getNumeroFinal() != null) {
                if (!StringUtils.isNumeric(numeracionSolicitada.getNumeroFinal())) {
                    valido = false;
                    MensajesBean.addErrorMsg(MSG_ID, "El campo 'Numeracion Final' es invalido", "");
                }
            } else {
                valido = false;
                MensajesBean.addErrorMsg(MSG_ID, "El campo 'Numeracion Final' es obligatorio", "");
            }
            if (StringUtils.isEmpty(numeracionSolicitada.getCliente())) {
                valido = false;
                MensajesBean.addErrorMsg(MSG_ID, "El campo 'Cliente' es obligatorio", "");
            }
            if (valido) {
                if (Integer.parseInt(numeracionSolicitada.getNumeroInicial()) > Integer.parseInt(numeracionSolicitada
                        .getNumeroFinal())) {
                    valido = false;
                    MensajesBean.addErrorMsg(MSG_ID, "La numeración inicial no puede ser mayor que la final", "");
                }
            }
        } else {
            if (!StringUtils.isEmpty(cantidadSolicitada)) {
                if (StringUtils.isNumeric(cantidadSolicitada)) {
                    if (Integer.parseInt(cantidadSolicitada) > 0 && Integer.parseInt(cantidadSolicitada) < 1000000) {
                        numeracionSolicitada.setCantidadSolicitada(new BigDecimal(cantidadSolicitada));
                    } else {
                        valido = false;
                        MensajesBean
                                .addErrorMsg(MSG_ID,
                                        "El campo 'Cantidad numeraciones' "
                                                + "invalido, tiene que tener un valor "
                                                + "numerico comprendido entre 0 y 999999", "");
                    }

                } else {
                    valido = false;
                    MensajesBean
                            .addErrorMsg(MSG_ID,
                                    "El campo 'Cantidad numeraciones' invalido, "
                                            + "tiene que tener un valor numerico comprendido entre 0 y 999999", "");

                }
            } else {
                valido = false;
                MensajesBean.addErrorMsg(MSG_ID, "El campo 'Cantidad numeraciones' es obligatorio", "");
            }
        }
        return valido && !numeracionRepetida();
    }

    /**
     * Comprueba si una numeracion solicitada esta repetida.
     * @return true/false
     */
    private boolean numeracionRepetida() {
        LOGGER.debug("");

        String rowKey = (String) numeracionesSolicitadasModel.getRowKey(numeracionSolicitada);
        if (numeracionesSolicitadasModel.getRowData(rowKey) != null) {
            MensajesBean.addErrorMsg("MSG_Numeracion",
                    "La numeración solicitada esta repetida. Por favor, seleccione otros datos", "");
            return true;
        }

        return false;
    }

    /**
     * Carga la tablas de numeracion solicitada y asignada.
     */
    private void cargarTablasNumeracion() {

        // Obtenemos las numeraciones asignadas
        listaRangos = nngFacade.findAllNumeracionAsignadaBySolicitud(solicitud);

        numeracionesSolicitadasModel = new NumeracionSeleccionadaNngModel(solicitud.getNumeracionesSolicitadas());

        if (!especifica) {

            ClaveServicio claveAux = new ClaveServicio();
            List<ClaveServicio> listaClaveServicioAux = new ArrayList<ClaveServicio>();
            listaDetalleUsoClaves = new ArrayList<DetalleClaveServicioAnalisisNng>();
            for (NumeracionSolicitadaNng numeracionSolicitada : solicitud.getNumeracionesSolicitadas()) {
                claveAux = numeracionSolicitada.getClaveServicio();
                if (!listaClaveServicioAux.contains(claveAux)) {

                    listaClaveServicioAux.add(claveAux);

                    DetalleClaveServicioAnalisisNng detalle = new DetalleClaveServicioAnalisisNng();
                    detalle.setClaveServicio(claveAux);
                    detalle.setTotalNumeracionAsignada(nngFacade.getTotalNumeracionAsignadaByClaveServicio(claveAux));

                    FiltroBusquedaSeries filtroOcupada = new FiltroBusquedaSeries();
                    filtroOcupada.setSerieLibre(false);
                    filtroOcupada.setRangosDisponibles(false);
                    filtroOcupada.setClave(claveAux.getCodigo());
                    Integer totalSeriesOcupadas = nngFacade.findAllSeriesCount(filtroOcupada);
                    FiltroBusquedaSeries filtroLibre = new FiltroBusquedaSeries();
                    filtroLibre.setSerieLibre(true);
                    filtroLibre.setRangosDisponibles(false);
                    filtroLibre.setClave(claveAux.getCodigo());
                    Integer totalSeriesLibres = nngFacade.findAllSeriesCount(filtroLibre);

                    detalle.setTotalNumeracionDisponible((totalSeriesOcupadas + totalSeriesLibres) * CANTIDAD_MAX);

                    detalle.setPorcentajeUsoNumeracion(NumerosUtils.calcularPorcentajeAsString(
                            detalle.getTotalNumeracionAsignada(),
                            detalle.getTotalNumeracionDisponible()));

                    detalle.setTotalSeriesOcupadas(totalSeriesOcupadas);
                    detalle.setTotalSeriesDisponibles(totalSeriesLibres + totalSeriesOcupadas);

                    detalle.setPorcentajeUsoSerie(NumerosUtils.calcularPorcentajeAsString(
                            detalle.getTotalSeriesOcupadas(),
                            detalle.getTotalSeriesDisponibles()));

                    listaDetalleUsoClaves.add(detalle);
                }
            }
        }
    }

    @Override
    public boolean isAvanzar() {
        boolean continuar = !listaRangos.isEmpty();

        if (!continuar) {
            setSummaryError("No se ha solicitado ninguna numeración valida para asignar. "
                    + "Solicite alguna numeración para continuar.");
        }

        return continuar;
    }

    @Override
    public void actualizaCampos() {

        // Asociamos la solicitud que usaremos en todo el Wizard
        solicitud = (SolicitudAsignacionNng) getWizard().getSolicitud();

        TipoSolicitud tipoSol = new TipoSolicitud();
        tipoSol.setCdg(TipoSolicitud.ASIGNACION_NNG);

        solicitud.setTipoSolicitud(tipoSol);

        // Cargamos combo de Arrendatarios
        listaArrendatarios = nngFacade.findAllArrendatarios();
        listaArrendatarios.remove(solicitud.getProveedorSolicitante());

        // Cargamos el combo de concesionarios de red
        listaConcesionarios = nngFacade.findAllConcesionarioConvenio(solicitud.getProveedorSolicitante());

        // Iniciamos renderizaciones
        especifica = solicitud.getTipoAsignacion().getCdg().equals(TipoAsignacion.ESPECIFICA);
        pstAmbos = solicitud.getProveedorSolicitante().getTipoProveedor().getCdg().equals(TipoProveedor.AMBOS);
        pstComercializador = solicitud.getProveedorSolicitante().getTipoProveedor().getCdg()
                .equals(TipoProveedor.COMERCIALIZADORA);
        pstConcesionario = solicitud.getProveedorSolicitante().getTipoProveedor().getCdg()
                .equals(TipoProveedor.CONCESIONARIO);

        // Cargamos el ABC propio del asignatario por defecto en la numeracion
        numeracionSolicitada.setAbc(solicitud.getProveedorSolicitante().getAbc());

        // Cargamos la tablas de numeracion solicitada y asignada
        cargarTablasNumeracion();

        // Limpiamos los campos de la numeracion
        limpiarNumeracionSolicitada();

        if (especifica) {
            headerTablaRangos = MensajesBean.getTextoResource("asignacion.nng.numeracion.solicitada");
        } else {
            headerTablaRangos = MensajesBean.getTextoResource("asignacion.nng.series.seleccionadas");
        }

    }

    @Override
    public void resetTab() {

        numeracionSolicitada = new NumeracionSolicitadaNng();
        cantidadAsignar = null;
        selectedNumeracionSolicitada = null;

        selectedSerieOcupada = null;

        serieLibreModel = new DetalleSerieAnalisisNngLazyModel();
        serieOcupadaModel = new DetalleSerieAnalisisNngLazyModel();

        solicitud = new SolicitudAsignacionNng();

        listaRangosAsignados = new ArrayList<RangoSerieNng>();
        listaRangos = new ArrayList<NumeracionAsignadaNng>();

        selectedSerieLibre = new MultiSelectionOnLazyModelManager<DetalleSerieAnalisisNng>();

    }

    /**
     * Getter del para el fileDownload.
     * @return analisisNumeracion
     * @throws Exception error
     */
    public StreamedContent getAnalisisNumeracion() throws Exception {

        InputStream stream = nngFacade.generarAnalisisNumeracion(solicitud.getProveedorSolicitante(),
                solicitud.getNumeracionesSolicitadas());
        String docName = "ANALISIS_NUMERACION";
        docName = docName.concat("_").concat(solicitud.getProveedorSolicitante().getNombreCorto());

        Calendar fechaActual = Calendar.getInstance();
        docName = docName.concat("_").concat(Integer.toString(fechaActual.get(Calendar.DAY_OF_MONTH)))
                .concat(Integer.toString(fechaActual.get(Calendar.MONTH) + 1))
                .concat(Integer.toString(fechaActual.get(Calendar.YEAR)));

        docName = docName.concat(".xlsx");

        stream.close();

        LOGGER.debug("docname {}", docName);
        return new DefaultStreamedContent(stream,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", docName);
    }

    /**
     * Fachada de Numeración No Geográfica.
     * @return nngFacade
     */
    public INumeracionNoGeograficaFacade getNngFacade() {
        return nngFacade;
    }

    /**
     * Fachada de Numeración No Geográfica.
     * @param nngFacade nngFacade to set
     */
    public void setNngFacade(INumeracionNoGeograficaFacade nngFacade) {
        this.nngFacade = nngFacade;
    }

    /**
     * Solicitud de asignación.
     * @return solicitud
     */
    public SolicitudAsignacionNng getSolicitud() {
        return solicitud;
    }

    /**
     * Solicitud de asignación.
     * @param solicitud solicitud to set
     */
    public void setSolicitud(SolicitudAsignacionNng solicitud) {
        this.solicitud = solicitud;
    }

    /**
     * Numeracion Solicitada.
     * @return numeracionSolicitada
     */
    public NumeracionSolicitadaNng getNumeracionSolicitada() {
        return numeracionSolicitada;
    }

    /**
     * Numeracion Solicitada.
     * @param numeracionSolicitada numeracionSolicitada to set
     */
    public void setNumeracionSolicitada(NumeracionSolicitadaNng numeracionSolicitada) {
        this.numeracionSolicitada = numeracionSolicitada;
    }

    /**
     * Referencia al Bean de claves de servicio.
     * @return claveServicioBean
     */
    public EdicionClaveServicioBean getClaveServicioBean() {
        return claveServicioBean;
    }

    /**
     * Referencia al Bean de claves de servicio.
     * @param claveServicioBean claveServicioBean to set
     */
    public void setClaveServicioBean(EdicionClaveServicioBean claveServicioBean) {
        this.claveServicioBean = claveServicioBean;
    }

    /**
     * Lista de claves de servicio.
     * @return listaClavesServicio
     */
    public List<ClaveServicio> getListaClavesServicio() {
        return listaClavesServicio;
    }

    /**
     * Lista de claves de servicio.
     * @param listaClavesServicio listaClavesServicio to set
     */
    public void setListaClavesServicio(List<ClaveServicio> listaClavesServicio) {
        this.listaClavesServicio = listaClavesServicio;
    }

    /**
     * Solicitud es de tipo Especifica.
     * @return especifica
     */
    public boolean isEspecifica() {
        return especifica;
    }

    /**
     * Solicitud es de tipo Especifica.
     * @param especifica especifica to set
     */
    public void setEspecifica(boolean especifica) {
        this.especifica = especifica;
    }

    /**
     * Proveedor Solicitante es Concesionario.
     * @return pstConcesionario
     */
    public boolean isPstConcesionario() {
        return pstConcesionario;
    }

    /**
     * Proveedor Solicitante es Concesionario.
     * @param pstConcesionario pstConcesionario to set
     */
    public void setPstConcesionario(boolean pstConcesionario) {
        this.pstConcesionario = pstConcesionario;
    }

    /**
     * Proveedor Solicitante es Comercializador.
     * @return pstComercializador
     */
    public boolean isPstComercializador() {
        return pstComercializador;
    }

    /**
     * Proveedor Solicitante es Comercializador.
     * @param pstComercializador pstComercializador to set
     */
    public void setPstComercializador(boolean pstComercializador) {
        this.pstComercializador = pstComercializador;
    }

    /**
     * Proveedor Solicitante es de ampos tipos.
     * @return pstAmbos
     */
    public boolean isPstAmbos() {
        return pstAmbos;
    }

    /**
     * Proveedor Solicitante es de ampos tipos.
     * @param pstAmbos pstAmbos to set
     */
    public void setPstAmbos(boolean pstAmbos) {
        this.pstAmbos = pstAmbos;
    }

    /**
     * Lista de concesionarios.
     * @return listaConcesionarios
     */
    public List<Proveedor> getListaConcesionarios() {
        return listaConcesionarios;
    }

    /**
     * Lista de concesionarios.
     * @param listaConcesionarios listaConcesionarios to set
     */
    public void setListaConcesionarios(List<Proveedor> listaConcesionarios) {
        this.listaConcesionarios = listaConcesionarios;
    }

    /**
     * Lista de arrendatarios.
     * @return listaArrendatarios
     */
    public List<Proveedor> getListaArrendatarios() {
        return listaArrendatarios;
    }

    /**
     * Lista de arrendatarios.
     * @param listaArrendatarios listaArrendatarios to set
     */
    public void setListaArrendatarios(List<Proveedor> listaArrendatarios) {
        this.listaArrendatarios = listaArrendatarios;
    }

    /**
     * Model de la tabla de numeraciones solicitadas.
     * @return numeracionesSolicitadasModel
     */
    public NumeracionSeleccionadaNngModel getNumeracionesSolicitadasModel() {
        return numeracionesSolicitadasModel;
    }

    /**
     * Model de la tabla de numeraciones solicitadas.
     * @param numeracionesSolicitadasModel numeracionesSolicitadasModel to set
     */
    public void setNumeracionesSolicitadasModel(NumeracionSeleccionadaNngModel numeracionesSolicitadasModel) {
        this.numeracionesSolicitadasModel = numeracionesSolicitadasModel;
    }

    /**
     * Seleccion de la tabla de numeraciones solicitadas.
     * @return selectedNumeracionSolicitada
     */
    public NumeracionSolicitadaNng getSelectedNumeracionSolicitada() {
        return selectedNumeracionSolicitada;
    }

    /**
     * Seleccion de la tabla de numeraciones solicitadas.
     * @param selectedNumeracionSolicitada selectedNumeracionSolicitada to set
     */
    public void setSelectedNumeracionSolicitada(NumeracionSolicitadaNng selectedNumeracionSolicitada) {
        this.selectedNumeracionSolicitada = selectedNumeracionSolicitada;
    }

    /**
     * Devuelve la cantidad a asignar como entero.
     * @return cantidadAsignar as int
     */
    public int getCantidadAsignarAsInt() {
        if (StringUtils.isNumeric(cantidadAsignar)) {
            return Integer.parseInt(cantidadAsignar);
        } else {
            return 0;
        }
    }

    /**
     * Numeración a asignar.
     * @return cantidadAsignar
     */
    public String getCantidadAsignar() {
        return cantidadAsignar;
    }

    /**
     * Numeración a asignar.
     * @param cantidadAsignar cantidadAsignar to set
     */
    public void setCantidadAsignar(String cantidadAsignar) {
        this.cantidadAsignar = cantidadAsignar;
    }

    /**
     * Series libres seleccionadas.
     * @return selectedSerieLibre
     */
    public MultiSelectionOnLazyModelManager<DetalleSerieAnalisisNng> getSelectedSerieLibre() {
        return selectedSerieLibre;
    }

    /**
     * Series libres seleccionadas.
     * @param selectedSerieLibre selectedSerieLibre to set
     */
    public void setSelectedSerieLibre(MultiSelectionOnLazyModelManager<DetalleSerieAnalisisNng> selectedSerieLibre) {
        this.selectedSerieLibre = selectedSerieLibre;
    }

    /**
     * Model tabla series ocupadas.
     * @return serieOcupadaModel
     */
    public DetalleSerieAnalisisNngLazyModel getSerieOcupadaModel() {
        return serieOcupadaModel;
    }

    /**
     * Model tabla series ocupadas.
     * @param serieOcupadaModel serieOcupadaModel to set
     */
    public void setSerieOcupadaModel(DetalleSerieAnalisisNngLazyModel serieOcupadaModel) {
        this.serieOcupadaModel = serieOcupadaModel;
    }

    /**
     * Model tabla series libres.
     * @return serieLibreModel
     */
    public DetalleSerieAnalisisNngLazyModel getSerieLibreModel() {
        return serieLibreModel;
    }

    /**
     * Model tabla series libres.
     * @param serieLibreModel serieLibreModel to set
     */
    public void setSerieLibreModel(DetalleSerieAnalisisNngLazyModel serieLibreModel) {
        this.serieLibreModel = serieLibreModel;
    }

    /**
     * Serie ocupada seleccionada.
     * @return selectedSerieOcupada
     */
    public DetalleSerieAnalisisNng getSelectedSerieOcupada() {
        return selectedSerieOcupada;
    }

    /**
     * Serie ocupada seleccionada.
     * @param selectedSerieOcupada selectedSerieOcupada to set
     */
    public void setSelectedSerieOcupada(DetalleSerieAnalisisNng selectedSerieOcupada) {
        this.selectedSerieOcupada = selectedSerieOcupada;
    }

    /**
     * Rangos seleccionados para asignación.
     * @return listaRangos
     */
    public List<NumeracionAsignadaNng> getListaRangos() {
        return listaRangos;
    }

    /**
     * Rangos seleccionados para asignación.
     * @param listaRangos listaRangos to set
     */
    public void setListaRangos(List<NumeracionAsignadaNng> listaRangos) {
        this.listaRangos = listaRangos;
    }

    /**
     * Rangos de una serie seleccionada.
     * @return listaRangosSeries
     */
    public List<RangoSerieNng> getListaRangosSeries() {
        return listaRangosSeries;
    }

    /**
     * Rangos de una serie seleccionada.
     * @param listaRangosSeries listaRangosSeries to set
     */
    public void setListaRangosSeries(List<RangoSerieNng> listaRangosSeries) {
        this.listaRangosSeries = listaRangosSeries;
    }

    /**
     * Numeracion inicial del nuevo rango.
     * @return numeracionInicial
     */
    public String getNumeracionInicial() {
        return numeracionInicial;
    }

    /**
     * Devuelve la numeracion inicial como un entero.
     * @return numeracionInicial as int
     */
    public int getNumeracionInicialAsInt() {
        if (StringUtils.isNumeric(numeracionInicial)) {
            return Integer.parseInt(numeracionInicial);
        } else {
            return 0;
        }
    }

    /**
     * Numeracion inicial del nuevo rango.
     * @param numeracionInicial numeracionInicial to set
     */
    public void setNumeracionInicial(String numeracionInicial) {
        this.numeracionInicial = numeracionInicial;
    }

    /**
     * Lista sugerencia de rangos seleccionados.
     * @return sugerenciaNumeracionList
     */
    public List<SugerenciaNumeracion> getSugerenciaNumeracionList() {
        return sugerenciaNumeracionList;
    }

    /**
     * Lista sugerencia de rangos seleccionados.
     * @param sugerenciaNumeracionList sugerenciaNumeracionList to set
     */
    public void setSugerenciaNumeracionList(List<SugerenciaNumeracion> sugerenciaNumeracionList) {
        this.sugerenciaNumeracionList = sugerenciaNumeracionList;
    }

    /**
     * Sugerencia numeracion seleccionada.
     * @return sugerenciaNumeracionSelect
     */
    public SugerenciaNumeracion getSugerenciaNumeracionSelect() {
        return sugerenciaNumeracionSelect;
    }

    /**
     * Sugerencia numeracion seleccionada.
     * @param sugerenciaNumeracionSelect sugerenciaNumeracionSelect to set
     */
    public void setSugerenciaNumeracionSelect(SugerenciaNumeracion sugerenciaNumeracionSelect) {
        this.sugerenciaNumeracionSelect = sugerenciaNumeracionSelect;
    }

    /**
     * Lista detalle uso clave servicio.
     * @return listaDetalleUsoClaves
     */
    public List<DetalleClaveServicioAnalisisNng> getListaDetalleUsoClaves() {
        return listaDetalleUsoClaves;
    }

    /**
     * Lista detalle uso clave servicio.
     * @param listaDetalleUsoClaves listaDetalleUsoClaves to set
     */
    public void setListaDetalleUsoClaves(List<DetalleClaveServicioAnalisisNng> listaDetalleUsoClaves) {
        this.listaDetalleUsoClaves = listaDetalleUsoClaves;
    }

    /**
     * Rango ya asignados para una numeración solicitada.
     * @return listaRangosAsignados
     */
    public List<RangoSerieNng> getListaRangosAsignados() {
        return listaRangosAsignados;
    }

    /**
     * Rango ya asignados para una numeración solicitada.
     * @param listaRangosAsignados listaRangosAsignados to set
     */
    public void setListaRangosAsignados(List<RangoSerieNng> listaRangosAsignados) {
        this.listaRangosAsignados = listaRangosAsignados;
    }

    /**
     * @return headerTablaRangos
     */
    public String getHeaderTablaRangos() {
        return headerTablaRangos;
    }

    /**
     * @param headerTablaRangos headerTablaRangos to set
     */
    public void setHeaderTablaRangos(String headerTablaRangos) {
        this.headerTablaRangos = headerTablaRangos;
    }

    /**
     * @return editarNumeracion
     */
    public boolean isEditarNumeracion() {
        return editarNumeracion;
    }

    /**
     * @param editarNumeracion editarNumeracion to set
     */
    public void setEditarNumeracion(boolean editarNumeracion) {
        this.editarNumeracion = editarNumeracion;
    }

    /**
     * Cabecera de los rango que se van a asiganar. Depende del tipo de asignacion.
     * @return headerTablaRangos
     */
    public String geaderTablaRangos() {
        return headerTablaRangos;
    }

    /**
     * Cabecera de los rango que se van a asiganar. Depende del tipo de asignacion.
     * @param headerTablaRangos headerTablaRangos to set
     */
    public void seaderTablaRangos(String headerTablaRangos) {
        this.headerTablaRangos = headerTablaRangos;
    }

    /**
     * SNA de la numeracion solicitada. Se mapea aparte para validarlo.
     * @return sna
     */
    public String getSna() {
        return sna;
    }

    /**
     * SNA de la numeracion solicitada. Se mapea aparte para validarlo.
     * @param sna sna to set
     */
    public void setSna(String sna) {
        this.sna = sna;
    }

    /**
     * Cantidad solicitada de la numeracion.
     * @return cantidadSolicitada
     */
    public String getCantidadSolicitada() {
        return cantidadSolicitada;
    }

    /**
     * Cantidad solicitada de la numeracion.
     * @param cantidadSolicitada cantidadSolicitada to set
     */
    public void setCantidadSolicitada(String cantidadSolicitada) {
        this.cantidadSolicitada = cantidadSolicitada;
    }

    /**
     * @return the habilitaSeleccionManualLibre
     */
    public boolean isHabilitaSeleccionManualLibre() {
        return habilitaSeleccionManualLibre;
    }

    /**
     * @param habilitaSeleccionManualLibre the habilitaSeleccionManualLibre to set
     */
    public void setHabilitaSeleccionManualLibre(boolean habilitaSeleccionManualLibre) {
        this.habilitaSeleccionManualLibre = habilitaSeleccionManualLibre;
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

}
