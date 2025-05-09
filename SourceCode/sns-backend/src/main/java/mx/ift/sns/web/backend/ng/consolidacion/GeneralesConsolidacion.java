package mx.ift.sns.web.backend.ng.consolidacion;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.abn.EstadoAbn;
import mx.ift.sns.modelo.abn.PoblacionAbn;
import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.modelo.filtros.FiltroBusquedaABNs;
import mx.ift.sns.modelo.ng.AbnConsolidar;
import mx.ift.sns.modelo.ng.PoblacionConsolidar;
import mx.ift.sns.modelo.ng.SolicitudConsolidacion;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.lazymodels.MunicipioAbnLazyModel;
import mx.ift.sns.web.backend.lazymodels.PoblacionAbnNgLazyModel;
import mx.ift.sns.web.backend.lazymodels.multiseleccion.MultiSelectionOnLazyModelWithFiltersManager;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase de soporte para la pestaña de 'Datos Generales' de Solicitudes de Consolidación. */
public class GeneralesConsolidacion implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralesConsolidacion.class);

    /** ngFacade. */
    private INumeracionGeograficaService ngFacade;

    /** Estado seleccionado. */
    private Estado estadoMun;

    /** Municipio Seleccionado. */
    private Municipio municipio;

    /** Poblacion Seleccionada. */
    private PoblacionAbn poblacion;

    /** Lista Estado. */
    private List<Estado> listaEstados;

    /** Modelo Lazy para carga los municipios dependiendo del Abn. */
    private MunicipioAbnLazyModel municipioAbnModel;

    /** Filtros para Municipio Abn. */
    private FiltroBusquedaABNs filtros;

    /** Lista Población. */
    private List<PoblacionAbn> listaPoblac;

    /** lista poblacion. **/
    private List<Poblacion> listaPoblacion;

    /** Modelo Lazy para carga los municipios dependiendo del Abn. */
    private PoblacionAbnNgLazyModel poblacionAbnNgModel;

    /** Información de la petición de Solicitud . */
    private SolicitudConsolidacion solicitud;

    /** Indica si se ha de habilitar el 'Resumen'. */
    private boolean resumenHabilitado = false;

    /** Indica cuando una consolidación es total. */
    private boolean consolidacionTotal = false;

    /** Indica cuando una consolidación es parcial. */
    private boolean consolidacionParcial = false;

    /** lista de Nirs. **/
    private List<Nir> listaNir;

    /** Fecha de consolidación. */
    private Date fechaConsolidacion;

    /** lista Abn entrega. **/
    private Abn abnEntrega;

    /** lista Abn recibe. **/
    private Abn abnRecibe;

    /** Numero de municipios del Abn que Recibe antes de la consolidacion. */
    private int municipioRecAntes;

    /** Numero de poblaciones del Abn que Recibe antes de la consolidacion. */
    private int poblacionesRecAntes;

    /** Numero de municipios del Abn que Entrega antes de la consolidacion. */
    private int municipioEntAntes;

    /** Numero de poblaciones del Abn que Entrega antes de la consolidacion. */
    private int poblacionesEntAntes;

    /** Numero de municipios del Abn que Recibe despues de la consolidacion. */
    private int municipioRecDespues;

    /** Numero de poblaciones del Abn que Recibe despues de la consolidacion. */
    private int poblacionesRecDespues;

    /** Numero de municipios del Abn que Entrega despues de la consolidacion. */
    private int municipioEntDespues;

    /** Numero de poblaciones del Abn que Entrega despues de la consolidacion. */
    private int poblacionesEntDespues;

    /** Numero de registros del Abn que Recibe antes de la consolidacion. */
    private int numRegAbnRecAntes;

    /** Numero de registros del Abn que Entrada antes de la consolidacion. */
    private int numRegAbnEntAntes;

    /** Numero de registros del Abn que Recibe despues de la consolidacion. */
    private int numRegAbnRecDespues;

    /** Numero de registros del Abn que Entrega despues de la consolidacion. */
    private int numRegAbnEntDespues;

    /** Nir's del Abn que recibe antes de la consolidación. */
    private int nirRecAntes;

    /** Nir's del Abn que recibe despues de la consolidación. */
    private int nirRecDespues;

    /** Nir's del Abn que entrega antes de la consolidación. */
    private int nirEntAntes;

    /** Nir's del Abn que entrega despues de la consolidación. */
    private int nirEntDespues;

    /** Lista de poblaciones no seleccionadas. */
    private List<PoblacionAbn> listaNoSelec;

    /** Indica si es una nueva consolidación o se edita una ya existente. */
    private boolean modoEdicion = false;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "FORM_nuevaConsolidacion:MSG_DatosGenerales";

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_FECHA_ID = "FORM_nuevaConsolidacion:MSG_Fecha";

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Lista que contiene todas las poblaciones seleccionas. */
    private List<PoblacionAbn> listaCompletaPoblaciones;

    /** Código del abn entrega introducido en el input. */
    private String codigoAbnEntrega;

    /** Código del abn recibe introducido en el input. */
    private String codigoAbnRecibe;

    /** Gestor de Selección Múltiple sobre la tabla Lazy con Filtros de Columna. */
    private MultiSelectionOnLazyModelWithFiltersManager<PoblacionAbn> listaEstadoMunicipioPoblacion;

    /** Gestor de Selección Múltiple sobre la tabla Lazy con Filtros de Columna.. */
    private MultiSelectionOnLazyModelWithFiltersManager<Municipio> listaEstadoMunicipio;

    /** Contenedor de Poblaciones Seleccionadas por Municipio. */
    private HashMap<Municipio, MultiSelectionOnLazyModelWithFiltersManager<PoblacionAbn>> mapPoblaciones;

    /** Contenedor de objetos Multiselection de Municipios organizados por estado. */
    private HashMap<Estado, MultiSelectionOnLazyModelWithFiltersManager<Municipio>> mapEstadoMunicipios;

    /** Organización de Poblaciones por Municipio para las tablas de Edición. */
    private HashMap<Municipio, List<PoblacionAbn>> mapPoblacionesEdicion;

    /** Lista de Poblaciones para mostrar en las tablas de Edición. */
    private List<PoblacionAbn> listaPoblacionesAbnEdicion;

    /** Lista de Municipios para mostrar en las tablas de Edición. */
    private List<Municipio> listaMunicipiosEdicion;

    /** Indica si los controles del interfaz de consolidaciones están habilitados o no. */
    private boolean interfazHabilitado = false;

    /**
     * Constructor.
     * @param pSolicitud Solicitud de Consolidación nueva o a Editar.
     * @param pNgFacade Facade de Servicios de Numeración Geográfica.
     */
    public GeneralesConsolidacion(SolicitudConsolidacion pSolicitud, INumeracionGeograficaService pNgFacade) {
        try {
            // Asociamos el Facade De Numeración Geográfica.
            ngFacade = pNgFacade;

            // Asociamos la solicitud que usaremos en todo el Wizard
            solicitud = pSolicitud;

            // Número de registros por página a mostrar por las tablas.
            registroPorPagina = PaginatorUtil.getRegistrosPorPagina(ngFacade, Parametro.REGISTROS_POR_PAGINA_BACK);

            filtros = new FiltroBusquedaABNs();
            filtros.setResultadosPagina(registroPorPagina);
            mapPoblaciones = new HashMap<>();
            mapEstadoMunicipios = new HashMap<>();
            listaEstadoMunicipioPoblacion = new MultiSelectionOnLazyModelWithFiltersManager<PoblacionAbn>();
            listaEstadoMunicipio = new MultiSelectionOnLazyModelWithFiltersManager<Municipio>();
            listaCompletaPoblaciones = new ArrayList<>();

            resumenHabilitado = false;
            consolidacionTotal = false;
            consolidacionParcial = false;
            modoEdicion = false;
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Acción invocada desde el el método consolidación total o consolidación parcial para que verifique los ABN's.
     * @return boolean
     */
    private boolean comprobarABNs() {
        boolean encontrado = false;
        try {
            // Los validadores del View impiden que los valores sean nulos o valores no numéricos
            BigDecimal codigoAbnRecibeBigDecimal = new BigDecimal(this.codigoAbnRecibe);
            BigDecimal codigoAbnEntregaBigDecimal = new BigDecimal(this.codigoAbnEntrega);

            if (codigoAbnRecibe.equals(codigoAbnEntrega)) {
                MensajesBean.addErrorMsg(MSG_ID, "El ABN de entrega no puede ser el mismo que el ABN que recibe.", "");
                return false;
            }

            abnRecibe = ngFacade.getAbnById(codigoAbnRecibeBigDecimal);
            this.solicitud.setAbnRecibe(abnRecibe);
            if (abnRecibe != null) {
                if (abnRecibe.getEstadoAbn().getCodigo().equals(EstadoAbn.ACTIVO)) {
                    abnEntrega = ngFacade.getAbnById(codigoAbnEntregaBigDecimal);
                    this.solicitud.setAbnEntrega(abnEntrega);
                    if (abnEntrega != null) {
                        if (abnEntrega.getEstadoAbn().getCodigo().equals(EstadoAbn.ACTIVO)) {
                            encontrado = true;
                        } else {
                            MensajesBean.addErrorMsg(MSG_ID, "El ABN de Entrega no está en estado ACTIVO.", "");
                            encontrado = false;
                        }
                    } else {
                        MensajesBean.addErrorMsg(MSG_ID, "El ABN de Entrega no existe en el SNS.", "");
                        encontrado = false;
                    }
                } else {
                    MensajesBean.addErrorMsg(MSG_ID, "El ABN Receptor no está en estado ACTIVO.", "");
                    encontrado = false;
                }
            } else {
                MensajesBean.addErrorMsg(MSG_ID, "El ABN Receptor no existe en el SNS.", "");
                encontrado = false;
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            encontrado = false;
        }
        return encontrado;
    }

    /**
     * Método que se desencadena al pulsar el botón de consolidación total.
     */
    public void consolidacionTotal() {
        try {
            mapPoblaciones.clear();
            mapEstadoMunicipios.clear();

            listaEstados = null;
            listaPoblac = null;
            consolidacionTotal = true;
            consolidacionParcial = false;
            resumenHabilitado = false;

            if (this.comprobarABNs()) {
                listaPoblacion = ngFacade.findAllPoblacionesByAbn(abnEntrega);
                this.resumenConsolidacion();
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método que se desencadena al pulsar el botón de consolidación parcial.
     */
    public void consolidacionParcial() {
        try {
            mapPoblaciones.clear();
            mapEstadoMunicipios.clear();
            listaEstadoMunicipioPoblacion.clear();
            listaEstadoMunicipio.clear();
            listaEstados = null;
            listaPoblac = null;
            estadoMun = null;

            filtros.clear();
            filtros.setResultadosPagina(registroPorPagina);

            municipioAbnModel = null;
            poblacionAbnNgModel = null;
            consolidacionParcial = true;
            consolidacionTotal = false;
            resumenHabilitado = false;

            if (this.comprobarABNs()) {
                this.cargarListaEstados();
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Carga la lista de Estados en función de las Poblaciones del ABN.
     * @throws Exception en caso de error.
     */
    private void cargarListaEstados() throws Exception {
        listaPoblacion = ngFacade.findAllPoblacionesByAbn(this.solicitud.getAbnEntrega());

        HashMap<String, Estado> mapEstadosAbn = new HashMap<String, Estado>();
        for (Poblacion poblacion : listaPoblacion) {
            mapEstadosAbn.put(poblacion.getMunicipio().getEstado().getCodEstado(),
                    poblacion.getMunicipio().getEstado());
        }

        List<Estado> lista = new ArrayList<Estado>();
        for (Entry<String, Estado> estados : mapEstadosAbn.entrySet()) {
            lista.add(estados.getValue());
            mapEstadoMunicipios.put(estados.getValue(),
                    new MultiSelectionOnLazyModelWithFiltersManager<Municipio>());
        }
        listaEstados = lista;
    }

    /**
     * Método que realiza el conteo de los nirs, municipios,poblaciones y plan de numeracion de los abns.
     * @throws Exception exception
     */
    public void resumenConsolidacion() {
        resumenHabilitado = true;
        try {
            // Estas 8 variables se van a calcular igual, independientemente del tipo de consolidación
            nirRecAntes = ngFacade.findAllNirByAbnCount(solicitud.getAbnRecibe().getCodigoAbn());
            municipioRecAntes = ngFacade.findAllMunicipiosCount(solicitud.getAbnRecibe().getCodigoAbn());
            poblacionesRecAntes = ngFacade.findAllPoblacionesCount(solicitud.getAbnRecibe().getCodigoAbn());
            numRegAbnRecAntes = ngFacade.getTotalNumOcupadaAbn(solicitud.getAbnRecibe()).intValue();

            nirEntAntes = ngFacade.findAllNirByAbnCount(solicitud.getAbnEntrega().getCodigoAbn());
            municipioEntAntes = ngFacade.findAllMunicipiosCount(solicitud.getAbnEntrega().getCodigoAbn());
            poblacionesEntAntes = ngFacade.findAllPoblacionesCount(solicitud.getAbnEntrega().getCodigoAbn());
            numRegAbnEntAntes = ngFacade.getTotalNumOcupadaAbn(solicitud.getAbnEntrega()).intValue();

            // Si es una consolidación total
            if (consolidacionTotal) {
                // Realizar el conteo DESPUES de la consolidación
                nirRecDespues = nirRecAntes + nirEntAntes;
                nirEntDespues = nirEntAntes - nirEntAntes;
                municipioRecDespues = municipioRecAntes + municipioEntAntes;
                poblacionesRecDespues = poblacionesRecAntes + poblacionesEntAntes;
                municipioEntDespues = municipioEntAntes - municipioEntAntes;
                poblacionesEntDespues = poblacionesEntAntes - poblacionesEntAntes;
                numRegAbnRecDespues = numRegAbnRecAntes + numRegAbnEntAntes;
                numRegAbnEntDespues = numRegAbnEntAntes - numRegAbnEntAntes;

            } else if (consolidacionParcial) {
                // Es una consolidación parcial
                // Realizar el conteo del ABN de Enbtrega ANTES de la consolidación
                int nirs = 0;
                int numeraciones = 0;

                nirEntDespues = nirEntAntes;
                nirRecDespues = nirRecAntes;
                numRegAbnEntDespues = numRegAbnEntAntes;
                numRegAbnRecDespues = numRegAbnRecAntes;

                // Poblaciones Seleccionadas
                listaCompletaPoblaciones = new ArrayList<PoblacionAbn>();
                for (MultiSelectionOnLazyModelWithFiltersManager<PoblacionAbn> munltiPoblacionAbn : mapPoblaciones
                        .values()) {
                    listaCompletaPoblaciones.addAll(munltiPoblacionAbn.getRegistrosSeleccionados());
                }

                for (PoblacionAbn poblacion : listaCompletaPoblaciones) {
                    nirs = ngFacade.getNirsByPoblacion(poblacion.getInegi());
                    nirEntDespues = nirEntDespues - nirs;
                    if (nirEntDespues < 0) {
                        nirEntDespues = 0;
                    }
                    nirRecDespues = nirRecDespues + nirs;
                    numeraciones = ngFacade.getTotalNumRangosAsignadosByPoblacion("", "", null,
                            poblacion.getInegi()).intValue();
                    numRegAbnEntDespues = numRegAbnEntDespues - numeraciones;
                    numRegAbnRecDespues = numRegAbnRecDespues + numeraciones;
                }

                // Se elimina los municipios repetidos, ya que al seleccionar varias poblaciones, se añade el mismo
                // municipio varias veces
                HashMap<String, Municipio> mapMunicipios = new HashMap<String, Municipio>();
                for (Municipio municipio : listaEstadoMunicipio.getRegistrosSeleccionados()) {
                    mapMunicipios.put(municipio.getId().getCodMunicipio(), municipio);
                }
                List<Municipio> lista = new ArrayList<Municipio>();
                for (Entry<String, Municipio> municip : mapMunicipios.entrySet()) {
                    lista.add(municip.getValue());
                }

                municipioRecDespues = municipioRecAntes + lista.size();
                poblacionesRecDespues = poblacionesRecAntes + listaCompletaPoblaciones.size();
                municipioEntDespues = municipioEntAntes - lista.size();
                poblacionesEntDespues = poblacionesEntAntes - listaCompletaPoblaciones.size();
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Carga la lista de Municipios según el Estado seleccionado.
     */
    public void cargarMunicipios() {
        try {
            if (estadoMun != null) {
                // Asociamos la clase que contendrá los filtros en cada búsqueda
                municipioAbnModel = new MunicipioAbnLazyModel();
                municipioAbnModel.setFiltros(filtros);
                municipioAbnModel.setService(ngFacade);

                listaEstadoMunicipio = mapEstadoMunicipios.get(estadoMun);
                municipioAbnModel.setMultiSelectionManager(listaEstadoMunicipio);

                // Carga Lazy y Filtros de búsqueda.
                filtros.clear();
                filtros.setResultadosPagina(registroPorPagina);
                filtros.setEstado(estadoMun);
                filtros.setCodigoAbn(abnEntrega.getCodigoAbn());
            } else {
                municipioAbnModel = null;
            }
            // Reseteamos siempre la lista de Poblaciones
            poblacionAbnNgModel = null;
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Carga la lista de Poblaciones según el Municipio seleccionado.
     */
    public void cargarPoblaciones() {
        try {
            // Lazy Model de Poblaciones ABN
            poblacionAbnNgModel = new PoblacionAbnNgLazyModel();
            poblacionAbnNgModel.setFiltros(filtros);
            poblacionAbnNgModel.setService(ngFacade);
            poblacionAbnNgModel.setMultiSelectionManager(listaEstadoMunicipioPoblacion);

            // Resetamos los filtros
            filtros.clear();
            filtros.setResultadosPagina(registroPorPagina);
            filtros.setEstado(estadoMun);
            filtros.setCodigoAbn(abnEntrega.getCodigoAbn());
            filtros.setMunicipio(municipio);

            // Reseteamos la paginación de la tabla para volver a la página 0
            PaginatorUtil.resetPaginacion("FORM_nuevaConsolidacion:TBL_Municipios", filtros.getResultadosPagina());

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al seleccionar una fila de la tabla de Municipios.
     * @param event SelectEvent Información del Municipio seleccionado.
     */
    public void seleccionMunicipio(SelectEvent event) {
        try {
            // Paginación en Municipios.
            municipio = (Municipio) event.getObject();
            int pagina = ((DataTable) event.getSource()).getPage();
            listaEstadoMunicipio.updateSelection(municipio, pagina, true);

            // Paginación en Poblaciones.
            if (!mapPoblaciones.containsKey(municipio)) {
                mapPoblaciones.put(municipio, new MultiSelectionOnLazyModelWithFiltersManager<PoblacionAbn>());
            }
            listaEstadoMunicipioPoblacion = mapPoblaciones.get(municipio);

            // Lista de Poblaciones del Municipio
            this.cargarPoblaciones();

            // Reseteamos la paginación de la tabla para volver a la página 0
            PaginatorUtil.resetPaginacion("FORM_nuevaConsolidacion:TBL_Poblaciones", filtros.getResultadosPagina());

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al seleccionar una fila de la tabla de Municipios de Edición.
     * @param event SelectEvent Información del Municipio seleccionado.
     */
    public void seleccionMunicipioEdicion(SelectEvent event) {
        try {
            // Poblaciones asociadas al municipio
            municipio = (Municipio) event.getObject();
            listaPoblacionesAbnEdicion = mapPoblacionesEdicion.get(municipio);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al deseleccionar una fila de la tabla de Municipios.
     * @param event SelectEvent
     */
    public void deSeleccionMunicipio(UnselectEvent event) {
        try {
            // Eliminamos el Municipio de la selección
            municipio = (Municipio) event.getObject();
            int pagina = ((DataTable) event.getSource()).getPage();
            listaEstadoMunicipio.updateSelection(municipio, pagina, false);

            // Dejamos la tabla de Poblaciones en blanco hasta que se vuelva a seleccionar un Municipio
            listaEstadoMunicipioPoblacion = mapPoblaciones.get(municipio);
            listaEstadoMunicipioPoblacion.clear();
            poblacionAbnNgModel = null;

            // Actualizamos el resumen de Consolidación
            this.resumenConsolidacion();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al seleccionar todas las filas de la tabla mediante el checkbox de cabecera.
     * @param event ToggleSelectEvent
     */
    public void seleccionPaginaMunicipios(ToggleSelectEvent event) {
        try {
            if (!event.isSelected()) {
                // Eliminamos la selección de Poblaciones de los Municipios de la página actual.
                for (Municipio item : listaEstadoMunicipio.getSeleccionTablaBackup()) {
                    if (mapPoblaciones.containsKey(item)) {
                        mapPoblaciones.get(item).clear();
                    }
                }
            }

            // Actualizamos la selección múltiple en paginación
            int pagina = ((DataTable) event.getSource()).getPage();
            listaEstadoMunicipio.toggleSelecction(pagina, event.isSelected());

            if (!event.isSelected()) {
                // Actualizamos el resumen de Consolidación
                this.resumenConsolidacion();

                // Dejamos la tabla de Poblaciones en blanco hasta que se vuelva a seleccionar un Municipio
                poblacionAbnNgModel = null;
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al seleccionar una fila de la tabla de Poblaciones.
     * @param event SelectEvent Información de la Población seleccionada.
     */
    public void seleccionPoblacion(SelectEvent event) {
        try {
            // Actualizamos la selección múltiple en paginación
            poblacion = (PoblacionAbn) event.getObject();
            int pagina = ((DataTable) event.getSource()).getPage();
            listaEstadoMunicipioPoblacion.updateSelection(poblacion, pagina, true);

            // Actualizamos el resumen de Consolidación
            this.resumenConsolidacion();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al deseleccionar una fila de la tabla de Municipios.
     * @param event SelectEvent
     */
    public void deSeleccionPoblacion(UnselectEvent event) {
        try {
            // Actualizamos la selección múltiple en paginación
            poblacion = (PoblacionAbn) event.getObject();
            int pagina = ((DataTable) event.getSource()).getPage();
            listaEstadoMunicipioPoblacion.updateSelection(poblacion, pagina, false);

            // Actualizamos el resumen de Consolidación
            this.resumenConsolidacion();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al seleccionar todas las filas de la tabla mediante el checkbox de cabecera.
     * @param event ToggleSelectEvent
     */
    public void seleccionPaginaPoblacion(ToggleSelectEvent event) {
        try {
            // Actualizamos la selección múltiple en paginación
            int pagina = ((DataTable) event.getSource()).getPage();
            listaEstadoMunicipioPoblacion.toggleSelecction(pagina, event.isSelected());

            // Actualizamos el ressumen de Consolidación
            this.resumenConsolidacion();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Comprueba que es posible realizar la consolidación.
     * @return boolean
     */
    private boolean comprobarConsolidacion() {

        // Check de Fecha de Consolidación
        if (fechaConsolidacion == null) {
            MensajesBean.addErrorMsg(MSG_FECHA_ID, "Debe seleccionar una fecha de consolidación", "");
            return false;
        } else {
            if (fechaConsolidacion.before(FechasUtils.getFechaHoy())) {
                MensajesBean.addErrorMsg(MSG_FECHA_ID,
                        "La fecha de Consolidación no puede ser menor que la fecha actual", "");
                return false;
            }
        }

        // Check de Poblaciones
        if (consolidacionParcial && listaCompletaPoblaciones.isEmpty()) {
            MensajesBean.addErrorMsg(MSG_FECHA_ID, "Debe seleccionar poblaciones para la consolidación", "");
            return false;
        }

        return true;
    }

    /**
     * Método que realiza la consolidación del abn.
     */
    public void consolidarAbn() {
        try {
            if (modoEdicion && consolidacionParcial) {
                solicitud.getAbnsConsolidados().clear();
            }

            // Se comprueban los Nirs
            if (this.comprobarNirs() && this.comprobarConsolidacion()) {
                // Se crea la solicitud en trámite
                EstadoSolicitud estado = new EstadoSolicitud();
                estado.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
                solicitud.setEstadoSolicitud(estado);
                solicitud.setFechaSolicitud(new Date());

                if (solicitud.getTipoSolicitud() == null) {
                    TipoSolicitud tipo = new TipoSolicitud();
                    tipo.setCdg(TipoSolicitud.CONSOLIDACION);
                    solicitud.setTipoSolicitud(tipo);
                }

                if (consolidacionTotal) {
                    solicitud.setTipoConsolidacion(SolicitudConsolidacion.CONSOLIDACION_TOTAL);
                } else {
                    solicitud.setTipoConsolidacion(SolicitudConsolidacion.CONSOLIDACION_PARCIAL);
                }

                // Se cambia el estado del Abn de entrega a "Trámite consolidación"
                EstadoAbn estadoAbn = new EstadoAbn();
                estadoAbn.setCodigo(EstadoAbn.TRAMITE_CONSOLIDACION);

                abnEntrega.setEstadoAbn(estadoAbn);
                solicitud.setAbnEntrega(ngFacade.saveAbn(abnEntrega));

                // Se crea el AbnConsolidar con las Poblaciones y Nirs, dependiendo del tipo de la consolidación
                if (consolidacionTotal) {
                    listaPoblac = ngFacade.findAllPoblacionAbnByAbn(abnEntrega);
                    listaNir = ngFacade.findAllNirByAbn(abnEntrega.getCodigoAbn());
                    solicitud = ngFacade.applyAbnConsolidar(listaPoblac,
                            listaNir, fechaConsolidacion, solicitud);
                } else {
                    solicitud = ngFacade.applyAbnConsolidar(listaCompletaPoblaciones,
                            listaNir, fechaConsolidacion, solicitud);
                }

                // Actualizamos las referencias de ABN Entrega y ABN Recibe
                abnEntrega = solicitud.getAbnEntrega();
                abnRecibe = solicitud.getAbnRecibe();

                MensajesBean.addInfoMsg(MSG_ID,
                        "Guardada la Solicitud de Consolidación con id: " + solicitud.getId(), "");

                if (fechaConsolidacion.compareTo(FechasUtils.getFechaHoy()) == 0) {
                    MensajesBean.addInfoMsg(MSG_ID, "Consolidación realizada con éxito", "");
                }

                // limpiamos todo para poder realizar más consolidaciones
                this.resetTab();
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.debug("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método que realiza la regla de negocio RN079.
     * @return True si se ha validado correctamente la RN079
     */
    private boolean comprobarNirs() {
        try {
            if (consolidacionParcial) {
                boolean encontrado = false;
                HashMap<BigDecimal, Nir> mapNirSelec = new HashMap<BigDecimal, Nir>();
                HashMap<BigDecimal, Nir> mapNirNoSelec = new HashMap<BigDecimal, Nir>();
                listaNoSelec = new ArrayList<PoblacionAbn>();
                listaPoblac = new ArrayList<PoblacionAbn>();
                for (Municipio mun : mapPoblaciones.keySet()) {
                    // Carga Lazy y Filtros de búsqueda.
                    filtros.clear();
                    filtros.setResultadosPagina(registroPorPagina);
                    filtros.setEstado(estadoMun);
                    filtros.setCodigoAbn(abnEntrega.getCodigoAbn());
                    filtros.setMunicipio(mun);
                    listaPoblac.addAll(ngFacade.findAllPoblacionesAbn(filtros));
                }

                listaNoSelec.addAll(listaPoblac);
                for (PoblacionAbn poblacion : listaPoblac) {
                    for (PoblacionAbn pob : listaCompletaPoblaciones) {
                        if (poblacion.getInegi().equals(pob.getInegi())) {
                            listaNoSelec.remove(poblacion);
                        }
                    }
                }

                // lista de Nirs de las poblaciones seleccionadas
                listaNir = new ArrayList<Nir>();
                for (PoblacionAbn poblacion : listaCompletaPoblaciones) {
                    List<Nir> listaNirAux = ngFacade.findAllNirsByPoblacion(poblacion.getInegi());
                    if (listaNirAux != null && !listaNirAux.isEmpty()) {
                        for (Nir nir : listaNirAux) {
                            mapNirSelec.put(nir.getId(), nir);
                        }
                    }
                }
                for (Entry<BigDecimal, Nir> nirs : mapNirSelec.entrySet()) {
                    listaNir.add(nirs.getValue());
                }

                // lista de Nirs de las poblaciones no seleccionadas
                List<Nir> listaNirsNoSelec = new ArrayList<Nir>();
                for (PoblacionAbn poblacion : listaNoSelec) {
                    List<Nir> listaNirAux = ngFacade.findAllNirsByPoblacion(poblacion.getInegi());
                    if (listaNirAux != null && !listaNirAux.isEmpty()) {
                        for (Nir nir : listaNirAux) {
                            mapNirNoSelec.put(nir.getId(), nir);
                        }
                    }
                }
                for (Entry<BigDecimal, Nir> nirs : mapNirNoSelec.entrySet()) {
                    listaNirsNoSelec.add(nirs.getValue());
                }

                for (Nir nirSelec : listaNir) {
                    for (Nir nirNoSelec : listaNirsNoSelec) {
                        if (nirSelec.equals(nirNoSelec)) {
                            encontrado = true;
                            break;
                        }
                    }
                }

                if (encontrado) {
                    // RN079
                    StringBuilder sbMsg = new StringBuilder();
                    sbMsg.append("Existen poblaciones que no se van a consolidar pero están relacionados a Nir's");
                    sbMsg.append(" que se pretenden consolidar, por favor libere estas poblaciones de las");
                    sbMsg.append(" asignaciones de numeración para poder continuar.");
                    MensajesBean.addErrorMsg(MSG_ID, sbMsg.toString(), "");
                    return false;
                } else if (listaPoblacion.size() == listaEstadoMunicipioPoblacion.getRegistrosSeleccionados().size()) {
                    // RN035
                    MensajesBean.addInfoMsg(MSG_ID, "El Abn que entrega se quedará sin registros.", "");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            return false;
        }
        return true;
    }

    /**
     * Resetea las variables para una nueva consolidación.
     */
    public void resetTab() {
        solicitud = new SolicitudConsolidacion();
        solicitud.setAbnEntrega(new Abn());
        solicitud.setAbnRecibe(new Abn());
        solicitud.setAbnsConsolidados(new ArrayList<AbnConsolidar>());

        codigoAbnRecibe = null;
        codigoAbnEntrega = null;
        abnEntrega = null;
        abnRecibe = null;

        listaEstadoMunicipioPoblacion.clear();
        listaEstadoMunicipio.clear();
        mapPoblaciones.clear();
        mapEstadoMunicipios.clear();
        listaCompletaPoblaciones = new ArrayList<>();

        modoEdicion = false;
        listaPoblacion = null;
        fechaConsolidacion = null;
        resumenHabilitado = false;
        consolidacionTotal = false;
        consolidacionParcial = false;
        listaEstados = null;
        estadoMun = null;
        municipio = null;
        poblacion = null;
        listaPoblac = null;

        municipioAbnModel = null;
        poblacionAbnNgModel = null;

        municipioRecAntes = 0;
        poblacionesRecAntes = 0;
        municipioEntAntes = 0;
        poblacionesEntAntes = 0;
        municipioRecDespues = 0;
        poblacionesRecDespues = 0;
        municipioEntDespues = 0;
        poblacionesEntDespues = 0;
        numRegAbnRecAntes = 0;
        numRegAbnEntAntes = 0;
        numRegAbnRecDespues = 0;
        numRegAbnEntDespues = 0;
        nirRecAntes = 0;
        nirRecDespues = 0;
        nirEntAntes = 0;
        nirEntDespues = 0;
    }

    /**
     * Carga el formulario con la información de las poblaciones seleccionadas en la Consolidación Total Terminada.
     */
    public void cargarEdicionConsolidacionTotalABNs() {
        try {
            abnEntrega = solicitud.getAbnEntrega();
            abnRecibe = solicitud.getAbnRecibe();
            codigoAbnEntrega = abnEntrega.getCodigoAbn().toString();
            codigoAbnRecibe = abnRecibe.getCodigoAbn().toString();
            modoEdicion = true;
            consolidacionParcial = false;
            consolidacionTotal = true;
            fechaConsolidacion = (solicitud.getAbnsConsolidados().get(0).getFechaConsolidacion());

            this.resumenConsolidacion();
            this.setResumenHabilitado(true);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Carga el formulario con la información de las poblaciones seleccionadas en la Consolidación Parcial Terminada.
     */
    public void cargarEdicionConsolidacionParcialABNs() {
        try {
            abnEntrega = solicitud.getAbnEntrega();
            abnRecibe = solicitud.getAbnRecibe();
            codigoAbnEntrega = abnEntrega.getCodigoAbn().toString();
            codigoAbnRecibe = abnRecibe.getCodigoAbn().toString();
            modoEdicion = true;
            consolidacionParcial = true;
            consolidacionTotal = false;
            fechaConsolidacion = (solicitud.getAbnsConsolidados().get(0).getFechaConsolidacion());
            listaCompletaPoblaciones = new ArrayList<PoblacionAbn>();
            mapPoblaciones = new HashMap<>();

            if (interfazHabilitado) {
                // La consolidación aún no se ha efectuado.
                // Cargamos la lista de Estados del Abn que Entrega y las selecciones hechas por el usuario.
                this.cargarListaEstados();

                // Cargamos la Lista de Estados y Municipios de donde se han consolidado poblaciones
                // Los objetos MultiSelectionOnLazyModelWithFiltersManager no utilizan la página, por lo
                // podemos pasarle un 0.
                for (AbnConsolidar abnConsolidado : solicitud.getAbnsConsolidados()) {
                    for (PoblacionConsolidar pobCons : abnConsolidado.getPoblacionConsolidar()) {

                        Estado estado = pobCons.getPoblacionAbn().getInegi().getMunicipio().getEstado();
                        Municipio municipio = pobCons.getPoblacionAbn().getInegi().getMunicipio();

                        // Lista de Estados que no se han cargado previamente.
                        if (!listaEstados.contains(estado)) {
                            listaEstados.add(estado);
                            mapEstadoMunicipios.put(estado,
                                    new MultiSelectionOnLazyModelWithFiltersManager<Municipio>());
                        }

                        // Lista de Municipios por Estado
                        if (!mapPoblaciones.containsKey(municipio)) {
                            mapPoblaciones.put(municipio,
                                    new MultiSelectionOnLazyModelWithFiltersManager<PoblacionAbn>());
                        }
                        mapEstadoMunicipios.get(estado).updateSelection(municipio, 0, true);

                        // Lista de Poblaciones por Municipio y Estado.
                        mapPoblaciones.get(municipio).updateSelection(pobCons.getPoblacionAbn(), 0, true);

                        // Lista total de poblaciones consolidadas.
                        listaCompletaPoblaciones.add(pobCons.getPoblacionAbn());
                    }
                }
            } else {
                // La Consolidación ya se ha efectuado. Mostramos la lista de Estados, municipios y poblaciones que
                // se han consolidado
                mapPoblacionesEdicion = new HashMap<>();
                listaMunicipiosEdicion = new ArrayList<>();

                for (AbnConsolidar abnConsolidado : solicitud.getAbnsConsolidados()) {
                    for (PoblacionConsolidar pobCons : abnConsolidado.getPoblacionConsolidar()) {
                        // Estado estado = pobCons.getPoblacionAbn().getInegi().getMunicipio().getEstado();
                        Municipio municipioEd = pobCons.getPoblacionAbn().getInegi().getMunicipio();
                        if (!listaMunicipiosEdicion.contains(municipioEd)) {
                            listaMunicipiosEdicion.add(municipioEd);
                        }

                        // Lista de Poblaciones por Municipio y Estado
                        if (!mapPoblacionesEdicion.containsKey(municipioEd)) {
                            mapPoblacionesEdicion.put(municipioEd, new ArrayList<PoblacionAbn>());
                        }
                        mapPoblacionesEdicion.get(municipioEd).add(pobCons.getPoblacionAbn());

                        // Lista total de poblaciones consolidadas.
                        listaCompletaPoblaciones.add(pobCons.getPoblacionAbn());
                    }
                }

                // Preseleccionamos el primer municipio de la lista y sus poblaciones
                if (!listaMunicipiosEdicion.isEmpty()) {
                    this.municipio = listaMunicipiosEdicion.get(0);
                    listaPoblacionesAbnEdicion = mapPoblacionesEdicion.get(municipio);
                }
            }

            // Actualizamos el resumen de Consolidación
            this.resumenConsolidacion();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    // GETTERS & SETTERS

    /**
     * Identificador de los estados.
     * @return the listaEstados
     */
    public List<Estado> getListaEstados() {
        return listaEstados;
    }

    /**
     * Identificador de las poblaciones.
     * @return the listaPoblac
     */
    public List<PoblacionAbn> getListaPoblac() {
        return listaPoblac;
    }

    /**
     * Información de la solicitud.
     * @return the solicitud
     */
    public SolicitudConsolidacion getSolicitud() {
        return solicitud;
    }

    /**
     * Identifica si esta activo o no el resumen de las consolidaciones.
     * @return the resumenHabilitado
     */
    public boolean isResumenHabilitado() {
        return resumenHabilitado;
    }

    /**
     * Identifica si esta activo o no el resumen de las consolidaciones.
     * @param resumenHabilitado the resumenHabilitado to set
     */
    public void setResumenHabilitado(boolean resumenHabilitado) {
        this.resumenHabilitado = resumenHabilitado;
    }

    /**
     * Identificado de la fecha de consolidación.
     * @return the fechaConsolidacion
     */
    public Date getFechaConsolidacion() {
        return fechaConsolidacion;
    }

    /**
     * Identificado de la fecha de consolidación.
     * @param fechaConsolidacion the fechaConsolidacion to set
     */
    public void setFechaConsolidacion(Date fechaConsolidacion) {
        this.fechaConsolidacion = fechaConsolidacion;
    }

    /**
     * Identificador del Abn que entrega.
     * @return the abnEntrega
     */
    public Abn getAbnEntrega() {
        return abnEntrega;
    }

    /**
     * Identificador del Abn que entrega.
     * @param abnEntrega the abnEntrega to set
     */
    public void setAbnEntrega(Abn abnEntrega) {
        this.abnEntrega = abnEntrega;
    }

    /**
     * Identificador del Abn que recibe.
     * @return the abnRecibe
     */
    public Abn getAbnRecibe() {
        return abnRecibe;
    }

    /**
     * Identificador del Abn que recibe.
     * @param abnRecibe the abnRecibe to set
     */
    public void setAbnRecibe(Abn abnRecibe) {
        this.abnRecibe = abnRecibe;
    }

    /**
     * Identificador de cantidad de municipios del abn recibe antes de la consolidación.
     * @return the municipioRecAntes
     */
    public int getMunicipioRecAntes() {
        return municipioRecAntes;
    }

    /**
     * Identificador de cantidad de poblaciones del abn recibe antes de la consolidación.
     * @return the poblacionesRecAntes
     */
    public int getPoblacionesRecAntes() {
        return poblacionesRecAntes;
    }

    /**
     * Identificador de cantidad de municipios del abn entrega antes de la consolidación.
     * @return the municipioEntAntes
     */
    public int getMunicipioEntAntes() {
        return municipioEntAntes;
    }

    /**
     * Identificador de cantidad de poblaciones del abn entrega antes de la consolidación.
     * @return the poblacionesEntAntes
     */
    public int getPoblacionesEntAntes() {
        return poblacionesEntAntes;
    }

    /**
     * Identificador de cantidad de municipios del abn recibe despues de la consolidación.
     * @return the municipioRecDespues
     */
    public int getMunicipioRecDespues() {
        return municipioRecDespues;
    }

    /**
     * Identificador de cantidad de poblaciones del abn recibe despues de la consolidación.
     * @return the poblacionesRecDespues
     */
    public int getPoblacionesRecDespues() {
        return poblacionesRecDespues;
    }

    /**
     * Identificador de cantidad de municipios del abn entrega despues de la consolidación.
     * @return the municipioEntDespues
     */
    public int getMunicipioEntDespues() {
        return municipioEntDespues;
    }

    /**
     * Identificador de cantidad de poblaciones del abn entrega despues de la consolidación.
     * @return the poblacionesEntDespues
     */
    public int getPoblacionesEntDespues() {
        return poblacionesEntDespues;
    }

    /**
     * Identificador de cantidad de numeraciones ocupadas del abn recibe antes de la consolidación.
     * @return the numRegAbnRecAntes
     */
    public int getNumRegAbnRecAntes() {
        return numRegAbnRecAntes;
    }

    /**
     * Identificador de cantidad de numeraciones ocupadas del abn entrega antes de la consolidación.
     * @return the numRegAbnEntAntes
     */
    public int getNumRegAbnEntAntes() {
        return numRegAbnEntAntes;
    }

    /**
     * Identificador de cantidad de numeraciones ocupadas del abn recibe despues de la consolidación.
     * @return the numRegAbnRecDespues
     */
    public int getNumRegAbnRecDespues() {
        return numRegAbnRecDespues;
    }

    /**
     * Identificador de cantidad de numeraciones ocupadas del abn entrega despues de la consolidación.
     * @return the numRegAbnEntDespues
     */
    public int getNumRegAbnEntDespues() {
        return numRegAbnEntDespues;
    }

    /**
     * Identificador de cantidad de nirs del abn recibe antes de la consolidación.
     * @return the nirRecAntes
     */
    public int getNirRecAntes() {
        return nirRecAntes;
    }

    /**
     * Identificador de cantidad de nirs del abn recibe despues de la consolidación.
     * @return the nirRecDespues
     */
    public int getNirRecDespues() {
        return nirRecDespues;
    }

    /**
     * Identificador de cantidad de nirs del abn entrega antes de la consolidación.
     * @return the nirEntAntes
     */
    public int getNirEntAntes() {
        return nirEntAntes;
    }

    /**
     * Identificador de cantidad de nirs del abn entrega despues de la consolidación.
     * @return the nirEntDespues
     */
    public int getNirEntDespues() {
        return nirEntDespues;
    }

    /**
     * Identificador del estado seleccionado.
     * @return the estadoMun
     */
    public Estado getEstadoMun() {
        return estadoMun;
    }

    /**
     * Identificador del estado seleccionado.
     * @param estadoMun the estadoMun to set
     */
    public void setEstadoMun(Estado estadoMun) {
        this.estadoMun = estadoMun;
    }

    /**
     * Identificador del municipio seleccionado.
     * @return the municipio
     */
    public Municipio getMunicipio() {
        return municipio;
    }

    /**
     * Identificador del municipio seleccionado.
     * @param municipio the municipio to set
     */
    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    /**
     * Identificador de la poblacion seleccionada.
     * @return the poblacion
     */
    public PoblacionAbn getPoblacion() {
        return poblacion;
    }

    /**
     * Identificador de la poblacion seleccionada.
     * @param poblacion the poblacion to set
     */
    public void setPoblacion(PoblacionAbn poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * Identificador de consolidación total.
     * @return the consolidacionTotal
     */
    public boolean isConsolidacionTotal() {
        return consolidacionTotal;
    }

    /**
     * Identificador de consolidación total.
     * @param consolidacionTotal the consolidacionTotal to set
     */
    public void setConsolidacionTotal(boolean consolidacionTotal) {
        this.consolidacionTotal = consolidacionTotal;
    }

    /**
     * Identificador de consolidación parcial.
     * @return the consolidacionParcial
     */
    public boolean isConsolidacionParcial() {
        return consolidacionParcial;
    }

    /**
     * Identificador de consolidación parcial.
     * @param consolidacionParcial the consolidacionParcial to set
     */
    public void setConsolidacionParcial(boolean consolidacionParcial) {
        this.consolidacionParcial = consolidacionParcial;
    }

    /**
     * Indicador de los Nirs.
     * @return the listaNir
     */
    public List<Nir> getListaNir() {
        return listaNir;
    }

    /**
     * Indicador de los Nirs.
     * @param listaNir the listaNir to set
     */
    public void setListaNir(List<Nir> listaNir) {
        this.listaNir = listaNir;
    }

    /**
     * Indica si es una nueva consolidación o se edita una ya existente.
     * @return boolean
     */
    public boolean isModoEdicion() {
        return modoEdicion;
    }

    /**
     * Indica si es una nueva consolidación o se edita una ya existente.
     * @param modoEdicion boolean
     */
    public void setModoEdicion(boolean modoEdicion) {
        this.modoEdicion = modoEdicion;
    }

    /**
     * @return the listaEstadoMunicipio
     */
    public MultiSelectionOnLazyModelWithFiltersManager<Municipio> getListaEstadoMunicipio() {
        return listaEstadoMunicipio;
    }

    /**
     * @return the listaEstadoMunicipioPoblacion
     */
    public MultiSelectionOnLazyModelWithFiltersManager<PoblacionAbn> getListaEstadoMunicipioPoblacion() {
        return listaEstadoMunicipioPoblacion;
    }

    /**
     * Obtiene el valor del número de elementos por página de la tabla de resultados.
     * @return the registroPorPagina
     */
    public int getRegistroPorPagina() {
        return registroPorPagina;
    }

    /**
     * @return the municipioAbnModel
     */
    public MunicipioAbnLazyModel getMunicipioAbnModel() {
        return municipioAbnModel;
    }

    /**
     * @return the poblacionAbnNgModel
     */
    public PoblacionAbnNgLazyModel getPoblacionAbnNgModel() {
        return poblacionAbnNgModel;
    }

    /**
     * @return the listaCompletaPoblaciones
     */
    public List<PoblacionAbn> getListaCompletaPoblaciones() {
        return listaCompletaPoblaciones;
    }

    /**
     * @param listaCompletaPoblaciones the listaCompletaPoblaciones to set
     */
    public void setListaCompletaPoblaciones(List<PoblacionAbn> listaCompletaPoblaciones) {
        this.listaCompletaPoblaciones = listaCompletaPoblaciones;
    }

    /**
     * Código del abn recibe introducido en el input.
     * @return the codigoAbnEntrega
     */
    public String getCodigoAbnEntrega() {
        return codigoAbnEntrega;
    }

    /**
     * Código del abn recibe introducido en el input.
     * @return the codigoAbnRecibe.
     */
    public String getCodigoAbnRecibe() {
        return codigoAbnRecibe;
    }

    /**
     * Código del abn recibe introducido en el input.
     * @param codigoAbnEntrega the codigoAbnEntrega to set
     */
    public void setCodigoAbnEntrega(String codigoAbnEntrega) {
        this.codigoAbnEntrega = codigoAbnEntrega;
    }

    /**
     * Código del abn recibe introducido en el input.
     * @param codigoAbnRecibe the codigoAbnRecibe to set
     */
    public void setCodigoAbnRecibe(String codigoAbnRecibe) {
        this.codigoAbnRecibe = codigoAbnRecibe;
    }

    /**
     * Indica si los controles del interfaz de consolidaciones están habilitados o no.
     * @return boolean
     */
    public boolean isInterfazHabilitado() {
        return interfazHabilitado;
    }

    /**
     * Indica si los controles del interfaz de consolidaciones están habilitados o no.
     * @param interfazHabilitado boolean
     */
    public void setInterfazHabilitado(boolean interfazHabilitado) {
        this.interfazHabilitado = interfazHabilitado;
    }

    /**
     * Lista de Poblaciones para mostrar en las tablas de Edición.
     * @return List
     */
    public List<PoblacionAbn> getListaPoblacionesAbnEdicion() {
        return listaPoblacionesAbnEdicion;
    }

    /**
     * Lista de Municipios para mostrar en las tablas de Edición.
     * @return List
     */
    public List<Municipio> getListaMunicipiosEdicion() {
        return listaMunicipiosEdicion;
    }

}
