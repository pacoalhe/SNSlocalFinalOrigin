package mx.ift.sns.web.backend.nng.cesion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroBusquedaRangos;
import mx.ift.sns.modelo.nng.CesionSolicitadaNng;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.nng.RangoSerieNng;
import mx.ift.sns.modelo.nng.SolicitudCesionNng;
import mx.ift.sns.modelo.pst.ProveedorConvenio;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.series.IRangoSerie;
import mx.ift.sns.modelo.solicitud.EstadoCesionSolicitada;
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

/** Clase de soporte para la pestaña de 'Cesión Numeración' en los Wizard. */
public class CesionNngNumeracionTab extends TabWizard {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CesionNngNumeracionTab.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_CesionNumeracion";

    /** Facade de Servicios de Numeración No Geográfica. */
    private INumeracionNoGeograficaFacade nngFacade;

    /** Información de la petición de Solicitud de Cesión NNG. */
    private SolicitudCesionNng solicitud;

    /** Indica si se se ha de habilitar el botón de 'Guardar'. */
    private boolean salvarHabilitado = false;

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

    /** Indica si se desea fraccionar el rango o serie seleccionado. */
    private boolean usarFraccionamiento = false;

    /** Fecha de implementación de la cesión. */
    private Date fechaImplementacion;

    /** Indica la cantidad de rango que se ha de fraccionar a partir del número inicial. */
    private String cantidad = "1";

    /** Indica el primer número de la serie o rango a fraccionar. */
    private String numInicial = "";

    /** Indica el último numero reservado para la serie o rango fraccionado. */
    private int numFinal = 0;

    /** Lista de fracciones sobre una serie o rango seleccionadas. */
    private List<IRangoSerie> listaRangosFracciones;

    /** Rango seleccionado para fraccionar. */
    private RangoSerieNng rangoAFraccionar;

    /** Fracción de rango en la tabla de Fracciones. */
    private RangoSerieNng rangoFraccionado;

    /** Lista de fracciones sobre una serie o rangos resultado de las fracciones indicadas. */
    private List<RangoSerieNng> listaRangoFraccionados;

    /** Lista de Fracciones por Rango. */
    private HashMap<String, List<IRangoSerie>> fraccionesByRango;

    /** Indica si estamos actualizando los campos al editar la solicitud. */
    private boolean actualizandoCampos = false;

    /** Lista de Cesiones solicitadas por el Cedente para el Cesionario. */
    private List<CesionSolicitadaNng> listaCesiones = null;

    /** Indica si se ha de utilizar el ABC del Cesionario Concesionario para enrutar la numeración que recibe. */
    private boolean usarAbcCesionario = false;

    /** Indica si el Proveedor Cesionario es Concesionario o Ambos y se puede utilizar su IDO. */
    private boolean habilitarUsarAbc = true;

    /** Indica si se ha de utilizar el Convenio del Proveedor Cesionario con un Proveedor Concesionario. */
    private boolean usarConvenioCesionario = false;

    /** Lista de Convenios del Proveedor Cesionario. */
    private List<ProveedorConvenio> listaConveniosCesionario;

    /** Convenio del Proveedor Cesionario Seleccionado. */
    private ProveedorConvenio convenioConcesionarioSeleccionado;

    /** Lista de Tipos de Proveedor. */
    private List<TipoProveedor> listaTiposProveedor;

    /** Tipo de Proveedor Seleccionado. */
    private TipoProveedor tipoProveedorSeleccionado;

    /** Indica si el combo de selección de tipo de Proveedor está habilitado o no. */
    private boolean tipoProveedorHabilitado = false;

    /** Tipo de Proveedor Comercializadora. */
    private TipoProveedor pstComercializadora;

    /** Tipo de Proveedor Concesionario. */
    private TipoProveedor pstConcesionario;

    /** Indica si el panel de fraccionamiento está habilitado o no. */
    private boolean fraccionarHabilitado = true;

    /** Rango Seleccionado de la lista de Rangos. */
    private RangoSerieNng rangoSeleccionado;

    /** Indica si la implementación es inmediata o programada. */
    private boolean implementacionInmediata = true;

    /** Cesion Solicitada seleccionada de la tabla de Cesiones. */
    private CesionSolicitadaNng cesSolSeleccionada;

    /** Mensaje de información para cesión de rangos con arrendamiento. */
    private String rangoArrendadoMsg = "";

    /** Gestor de Selección Múltiple sobre la tabla Lazy. */
    private MultiSelectionOnLazyModelManager<RangoSerieNng> multiSelectionManager;

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'Cesión Numeración'.
     * @param pWizard Referencia a la clase Wizard que instancia este Tab
     * @param pNngFacade Facade de Servicios de Numeración No Geográfica.
     */
    public CesionNngNumeracionTab(Wizard pWizard, INumeracionNoGeograficaFacade pNngFacade) {
        // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
        setWizard(pWizard);

        // Asociamos la solicitud que usaremos en todo el Wizard
        solicitud = (SolicitudCesionNng) getWizard().getSolicitud();

        // Asociamos el Facade de Servicios
        nngFacade = pNngFacade;
    }

    @Override
    public void cargaValoresIniciales() {
        try {
            // Inicializaciones para cargar los campos de Faces sin errores
            listaCesiones = new ArrayList<>();
            listaRangosFracciones = new ArrayList<>();
            listaRangoFraccionados = new ArrayList<>();
            fechaImplementacion = new Date();
            fraccionesByRango = new HashMap<>(1);
            multiSelectionManager = new MultiSelectionOnLazyModelManager<>();

            // Tipos de Proveedor para el combo de selección de Tipo de Proveedor
            pstComercializadora = nngFacade.getTipoProveedorByCdg(TipoProveedor.COMERCIALIZADORA);
            pstConcesionario = nngFacade.getTipoProveedorByCdg(TipoProveedor.CONCESIONARIO);

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
            listaCesiones.clear();
            usarFraccionamiento = false;
            listaRangosFracciones.clear();
            listaRangoFraccionados.clear();
            fraccionesByRango.clear();
            listaConveniosCesionario = null;
            listaTiposProveedor = null;
            implementacionInmediata = true;
            fechaImplementacion = new Date();
            salvarHabilitado = false;
            usarConvenioCesionario = false;
            usarAbcCesionario = false;
            habilitarUsarAbc = false;
            tipoProveedorHabilitado = false;
            tipoProveedorSeleccionado = null;
            convenioConcesionarioSeleccionado = null;
        }
    }

    /** Método invocado al seleccionar un tipo de proveedor en el combo. */
    public void seleccionTipoProveedor() {
        try {
            // Solamente se permite seleccionar el tipo de proveedor según actuará el cesionario
            // cuando el cesionario es de tipo "Ambos".
            if (tipoProveedorSeleccionado != null) {
                if (tipoProveedorSeleccionado.getCdg().equals(TipoProveedor.CONCESIONARIO)) {
                    // Los convenios del cesionario tienen que ser para el mismo tipo de red
                    // que provee el Cedente o de tipo de red Ambos
                    listaConveniosCesionario = null;
                    convenioConcesionarioSeleccionado = null;

                    // Si el Cesionario tiene el mismo tipo de red puede utilizar su propio ABC
                    usarAbcCesionario = true;
                    habilitarUsarAbc = false;
                    usarConvenioCesionario = false;
                } else {
                    // Comercializadora: El Cesionario ha de tener convenios con Concesionarios
                    // para el mismo tipo de red del cendente.
                    listaConveniosCesionario = nngFacade.findAllConveniosByProveedor(
                            solicitud.getProveedorCesionario(),
                            solicitud.getProveedorSolicitante().getTipoRed());

                    usarAbcCesionario = false;
                    habilitarUsarAbc = true;
                    usarConvenioCesionario = true;
                }
            } else {
                usarAbcCesionario = false;
                habilitarUsarAbc = false;
                usarConvenioCesionario = false;
                convenioConcesionarioSeleccionado = null;
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
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
        rangoSeleccionado = null;
    }

    /** Método invocado al cambiar el checkbox de 'Fraccionar'. */
    public void seleccionUsarFraccionamiento() {
        cantidad = "1";
        numInicial = "0";
        numFinal = 0;
    }

    /** Agrega un rango para su fraccionamiento. */
    public void agregarFraccionamiento() {
        try {
            if (rangoSeleccionado != null) {
                if (validarCesionConFraccionamiento()) {
                    listaRangosFracciones.add(rangoSeleccionado);
                    rangoAFraccionar = rangoSeleccionado;
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
                                MensajesBean.addErrorMsg(MSG_ID, "La numeración a ceder "
                                        + "ya existe en otra fracción.", "");
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

    /**
     * Aplica las cesiones seleccionadas por el usuario.
     * @return True si se han aplicado correctamente las cesiones.
     */
    private boolean aplicarCesiones() {
        try {
            if (validadRangosACeder()) {
                // Aplicamos las cesiones solicitadas y reservamos los rangos si es necesario
                SolicitudCesionNng solCes = nngFacade.applyCesionesSolicitadas(solicitud);

                // Asociamos la nueva instancia de solicitud al Wizard
                this.getWizard().setSolicitud(solCes);

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

    @Override
    public boolean isAvanzar() {
        boolean resultado = true;
        if (this.isTabHabilitado()) {
            // Continuamos siempre y cuando haya cesiones por procesar
            if (listaCesiones.isEmpty()) {
                MensajesBean.addErrorMsg(MSG_ID, MensajesBean.getTextoResource("cesion.errores.almenosUna"), "");
                resultado = false;
            } else {
                resultado = (this.guardarCambios() && this.aplicarCesiones());
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
            // desde la página de cesión y se vuelve a ella.
            this.limpiarBusqueda();
            tipoProveedorSeleccionado = null;

            // La solicitud del Wizard ha cambiado de instancia desde que se generó en
            // el constructor. Es necesario actualizar la referecnia en el tab.
            solicitud = (SolicitudCesionNng) getWizard().getSolicitud();

            // Mientras una solicitud no esté cancelada sigue siendo editable.
            if (!solicitud.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_CANCELADA)) {
                // Estamos en modo actualización. Evita algunas validaciones.
                actualizandoCampos = true;

                // Actualizamos las solicitudes pendientes
                listaCesiones.clear();
                if (solicitud.getCesionesSolicitadas() != null) {
                    listaCesiones.addAll(solicitud.getCesionesSolicitadas());
                }

                // Actualizamos las tablas
                listaRangosFracciones.clear();
                listaRangoFraccionados.clear();
                fraccionesByRango.clear();

                List<String> avisosRangosCambiados = new ArrayList<String>();
                boolean existeRango = true;

                for (CesionSolicitadaNng cesSol : listaCesiones) {
                    existeRango = true;
                    if (cesSol.getEstatus().getCodigo().equals(EstadoCesionSolicitada.PENDIENTE)) {
                        if (cesSol.getFraccionamientoRango().equals("N")) {
                            RangoSerieNng rangoOriginal = nngFacade.getRangoSerie(
                                    cesSol.getIdClaveServicio(),
                                    cesSol.getSna(),
                                    cesSol.getNumInicio(),
                                    cesSol.getProveedorCedente());
                            existeRango = (rangoOriginal != null);
                        } else {
                            // Rango seleccionado para fraccionar
                            RangoSerieNng rangoOriginal = nngFacade.getRangoSerieByFraccion(
                                    cesSol.getIdClaveServicio(),
                                    cesSol.getSna(),
                                    cesSol.getNumInicio(),
                                    cesSol.getNumFinal(),
                                    cesSol.getProveedorCedente());

                            existeRango = (rangoOriginal != null);
                            if (existeRango) {
                                // Añadimos el rango a la lista de rangos fraccionados
                                if (!RangosSeriesUtil.arrayContains(listaRangosFracciones, rangoOriginal)) {
                                    listaRangosFracciones.add(rangoOriginal);
                                }

                                // Agregamos las fracciones a la tabla de fracciones.
                                this.rangoAFraccionar = rangoOriginal;
                                this.numInicial = cesSol.getNumInicio();
                                this.numFinal = cesSol.getNumFinalAsInt();
                                this.aplicarFraccionamiento();
                            }
                        }
                    }

                    if (!existeRango) {
                        StringBuffer sbAviso = new StringBuffer();
                        sbAviso.append("Asignatario: ").append(cesSol.getProveedorCedente().getNombreCorto());
                        sbAviso.append(", Clave Servicio: ").append(cesSol.getIdClaveServicio());
                        sbAviso.append(", Sna: ").append(cesSol.getSna());
                        sbAviso.append(", Inicio: ").append(cesSol.getNumInicio());
                        sbAviso.append(", Final: ").append(cesSol.getNumFinal()).append("<br>");
                        avisosRangosCambiados.add(sbAviso.toString());
                    }
                }

                salvarHabilitado = (!listaCesiones.isEmpty());
                usarFraccionamiento = (!listaRangosFracciones.isEmpty());
                this.rangoAFraccionar = null;
                this.numInicial = "0";
                this.numFinal = 0;

                // Habilitamos opciones en función del cesionario
                if (solicitud.getProveedorCesionario() != null) {
                    if (solicitud.getProveedorCesionario().getTipoProveedor().getCdg()
                            .equals(TipoProveedor.CONCESIONARIO)) {
                        // Cedente y cesionario tienen el mismo tipo de servicio o el cesionario
                        // tiene ambos tipos y se ha de utilizar el IDO del cesionario concesionario.
                        // Si no tuviesen el mismo tipo de red o ambos no se hubiese podido seleccionar.
                        // Los filtros se hacen en la query.
                        tipoProveedorSeleccionado = pstConcesionario;
                        tipoProveedorHabilitado = false;
                        usarAbcCesionario = true;
                        habilitarUsarAbc = false;
                        usarConvenioCesionario = false;

                    } else if (solicitud.getProveedorCesionario().getTipoProveedor().getCdg()
                            .equals(TipoProveedor.AMBOS)) {
                        // Cedente y cesionario tienen el mismo tipo de servicio o el cesionario
                        // puede ofrecer ambos tipos de servicio. Es opcional utilizar un convenio
                        // del cesionario si existe.

                        // Habilitamos el Combo de tipo de Proveedor para que el Usuario seleccione
                        // si quiere que el cesionario actúe como Comercializadora o Concesionario
                        tipoProveedorHabilitado = true;
                        habilitarUsarAbc = true;
                        usarConvenioCesionario = false;
                    } else {
                        // Si el cesionario es comercializadora y se nos ha permitido seleccionarlo
                        // es por que tiene convenios con Concesionarios con el mismo tipo de red o
                        // ambos tipos de red

                        listaConveniosCesionario = nngFacade.findAllConveniosByProveedor(
                                solicitud.getProveedorCesionario(),
                                solicitud.getProveedorSolicitante().getTipoRed());
                        tipoProveedorSeleccionado = pstComercializadora;
                        tipoProveedorHabilitado = false;
                        usarAbcCesionario = false;
                        habilitarUsarAbc = false;
                        usarConvenioCesionario = true;
                    }
                    // Si el combo de tipo proveedor está habilitado lo cargamos con las opciones
                    // disponibles para el PST Cesionario.
                    if (tipoProveedorHabilitado) {
                        this.actualizarComboTiposProveedor();
                    }
                }

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
                // Para Cesiones Terminadas o Canceladas mostramos la información de las Cesiones realizadas
                listaCesiones.clear();
                listaCesiones.addAll(solicitud.getCesionesSolicitadas());
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        } finally {
            actualizandoCampos = false;
        }
    }

    /**
     * Método invocado al pulsar el botón 'Agregar'.
     * @param pCheckArrendamiento True para comprobar si existe arrendamiento sobre el rango seleccionado.
     */
    public void agregarCesion(boolean pCheckArrendamiento) {
        try {
            // Validaciones antes de crear la CesionSolicitadaNg
            boolean todoOk = true;

            // Validación de rango arrendado.
            todoOk = !(pCheckArrendamiento && validarArrendamiento());

            // todoOk es false si no se quiere comprobar el arrendamiento de un rango (por que ya se
            // haya aceptado la modal de aviso) o por que no exista arrendamiento en el rango.
            if (todoOk) {
                // Si no se utiliza el ABC del Cesionario hay que seleccionar un Convenio
                if (!usarAbcCesionario && convenioConcesionarioSeleccionado == null) {
                    MensajesBean.addErrorMsg(MSG_ID,
                            "Es necesario seleccionar un convenio o el ABC del Cesionario", "");
                    todoOk = false;
                }

                // El Cesionario seleccionado de convenio ha de tener ABC
                if (!usarAbcCesionario && convenioConcesionarioSeleccionado != null) {
                    if (convenioConcesionarioSeleccionado.getProveedorConcesionario().getIdo() == null) {
                        MensajesBean.addErrorMsg(MSG_ID,
                                "El Proveedor Concesionario del convenio seleccionado debe tener ABC.", "");
                        todoOk = false;
                    }
                }

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
                        this.cederFracciones();
                        this.seleccionUsarFraccionamiento();
                    } else {
                        if (validarCesionExistente()) {
                            this.cederRangosSeleccionados();
                        }
                    }
                }

                // Habilitamos el botón de guardar siempre y cuando haya cesiones solicitadas.
                salvarHabilitado = (!listaCesiones.isEmpty());
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Agrega fracciones de rangos para su cesión.
     * @throws Exception en caso de error.
     */
    private void cederFracciones() throws Exception {
        // Fracciones de la lista "Rangos Fraccionados" sobre un mismo rango.
        for (RangoSerieNng fraccion : listaRangoFraccionados) {
            // Ignoramos los huecos temporales y agregamos las peticiones fracción
            if (fraccion.getEstatus().getCodigo().equals(EstadoRango.AFECTADO)) {
                RangoSerieNng rangoOriginal = (RangoSerieNng) RangosSeriesUtil.getRangoInicial(
                        fraccion, listaRangosFracciones);
                CesionSolicitadaNng cs = getCesionSolicitadaFromRango(rangoOriginal);
                cs.setNumInicio(fraccion.getNumInicio());
                cs.setNumFinal(fraccion.getNumFinal());
                listaCesiones.add(cs);
            }
        }
    }

    /**
     * Agrega los rangos seleccionados para Ceder sin fraccionamiento.
     * @throws Exception en caso de error.
     */
    private void cederRangosSeleccionados() throws Exception {
        if (validarCesionSinFraccionamiento()) {
            for (RangoSerieNng rango : multiSelectionManager.getRegistrosSeleccionados()) {
                CesionSolicitadaNng cs = getCesionSolicitadaFromRango(rango);
                listaCesiones.add(cs);
            }
        }
    }

    /** Método invocado al pulsar sobre el botón 'Eliminar' en la tabla de Liberaciones seleccionadas. */
    public void eliminarCesion() {
        try {
            PeticionCancelacion checkCancelacion = nngFacade.cancelCesion(cesSolSeleccionada, true);
            if (checkCancelacion.isCancelacionPosible()) {
                listaCesiones.remove(cesSolSeleccionada);
                this.guardarCambios();
            } else {
                MensajesBean.addErrorMsg(MSG_ID, checkCancelacion.getMensajeError(), "");
            }

            // Habilitamos el botón de guardar siempre y cuando haya cesiones solicitadas.
            salvarHabilitado = (!listaCesiones.isEmpty());

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

    /**
     * Realiza validaciones para comprobar que se puede agregar el rango seleccionado para cesión sin fraccionamiento.
     * @return True si se puede agregar el rango a la tabla rangos a ceder.
     * @throws Exception en caso de error.
     */
    private boolean validarCesionSinFraccionamiento() throws Exception {
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
        // que pertenecen al proveedor esten en estado 'ASIGNADO' y la numeración sea
        // soportada por el cesionario.
        rangosReservados = new ArrayList<RangoSerieNng>();
        for (RangoSerieNng rango : multiSelectionManager.getRegistrosSeleccionados()) {
            // Comprobamos el estado del rango
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
     * Realiza validaciones para comprobar que se puede agregar el rango seleccionado para cesión con fraccionamiento.
     * @return True si se puede agregar el rango a la tabla rangos a ceder.
     * @throws Exception en caso de error.
     */
    private boolean validarCesionConFraccionamiento() throws Exception {
        // Solo es posible ceder Rangos asignados
        if (!rangoSeleccionado.getEstatus().getCodigo().equals(EstadoRango.ASIGNADO)) {
            MensajesBean.addErrorMsg(MSG_ID, "El rango no esta disponible, estado: "
                    + rangoSeleccionado.getEstatus().getDescripcion(), "");
            return false;
        }

        // Comprobamos que no exista ya la cesión solicitada
        if (!validarCesionExistente()) {
            // El método ya pinta las trazas
            return false;
        }

        return true;
    }

    /**
     * Comprueba que el rango seleccionado para agregar como cesion (fraccionada o no) no exista ya en la tabla de
     * cesiones solicitadas.
     * @return True si se puede agregar el rango para cesion
     * @throws Exception en caso de error
     */
    private boolean validarCesionExistente() throws Exception {
        List<RangoSerieNng> rangosEnCesion = new ArrayList<RangoSerieNng>();
        if (usarFraccionamiento) {
            CesionSolicitadaNng cesSolTemp = this.getCesionSolicitadaFromRango(rangoSeleccionado);
            if (arrayContains(listaCesiones, cesSolTemp)) {
                rangosEnCesion.add(rangoSeleccionado);
            }
        } else {
            for (RangoSerieNng rango : multiSelectionManager.getRegistrosSeleccionados()) {
                CesionSolicitadaNng cesSolTemp = this.getCesionSolicitadaFromRango(rango);
                if (arrayContains(listaCesiones, cesSolTemp)) {
                    rangosEnCesion.add(rango);
                }
            }
        }

        if (!rangosEnCesion.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            sb.append("No es posible agregar los rangos seleccionados. ");
            sb.append("Lo siguientes rangos ya existen para cesión:<br>");
            for (RangoSerieNng rango : rangosEnCesion) {
                sb.append("Clave Servicio: ").append(rango.getClaveServicio().getCodigo());
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
     * Comprueba que los rangos seleccionados para ceder existen y tienen estatus disponible.
     * @return True si se pueden aplicar las cesiones.
     * @throws Exception En caso de error.
     */
    private boolean validadRangosACeder() throws Exception {
        // Antes de aplicar las cesiones comprobamos que todos los rangos sigan con status 'Asignado'
        List<String> avisosDisponibilidad = new ArrayList<String>();
        List<String> avisosEstatus = new ArrayList<String>();
        boolean existeRango = true;
        boolean statusAsignado = true;
        String status = null;
        for (CesionSolicitadaNng cesSol : solicitud.getCesionesSolicitadas()) {
            existeRango = true;
            statusAsignado = true;

            if (cesSol.getEstatus().getCodigo().equals(EstadoCesionSolicitada.PENDIENTE)) {
                if (cesSol.getFraccionamientoRango().equals("N")) {

                    // Estado actual del rango
                    RangoSerieNng rango = nngFacade.getRangoSerie(cesSol.getIdClaveServicio(), cesSol.getSna(),
                            cesSol.getNumInicio(),
                            cesSol.getProveedorCedente());

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
                    // Rango seleccionado para ceder completo o para fraccionar
                    RangoSerieNng rangoOriginal = nngFacade.getRangoSerieByFraccion(
                            cesSol.getIdClaveServicio(), cesSol.getSna(), cesSol.getNumInicio(),
                            cesSol.getNumFinal(), cesSol.getProveedorCedente());

                    if (rangoOriginal != null) {
                        if (!rangoOriginal.getEstatus().getCodigo().equals(EstadoRango.ASIGNADO)) {
                            // Si esta Reservado puede ser de ésta misma solicitud por ser una implementación
                            // no inmediata
                            if (rangoOriginal.getEstatus().getCodigo().equals(EstadoRango.RESERVADO)) {
                                if (rangoOriginal.getSolicitud().getId().intValue()
                                != solicitud.getId().intValue()) {
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
                sbAviso.append("Asignatario: ").append(cesSol.getProveedorCedente().getNombreCorto());
                sbAviso.append(", Clave Servicio: ").append(cesSol.getIdClaveServicio());
                sbAviso.append(", Sna: ").append(cesSol.getSna());
                sbAviso.append(", Inicio: ").append(cesSol.getNumInicio());
                sbAviso.append(", Final: ").append(cesSol.getNumFinal()).append("<br>");
                avisosDisponibilidad.add(sbAviso.toString());
            }

            if (!statusAsignado) {
                StringBuffer sbAviso = new StringBuffer();
                sbAviso.append("Asignatario: ").append(cesSol.getProveedorCedente().getNombreCorto());
                sbAviso.append(", Clave Servicio: ").append(cesSol.getIdClaveServicio());
                sbAviso.append(", Sna: ").append(cesSol.getSna());
                sbAviso.append(", Inicio: ").append(cesSol.getNumInicio());
                sbAviso.append(", Final: ").append(cesSol.getNumFinal());
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
     * Crea un objeto CesionSolicitadaNng en base a la información de un Rango.
     * @param pRango RangoSerieNng con la información de la cesión.
     * @return CesionSolicitadaNng
     * @throws Exception en caso de error.
     */
    private CesionSolicitadaNng getCesionSolicitadaFromRango(RangoSerieNng pRango) throws Exception {
        CesionSolicitadaNng cs = new CesionSolicitadaNng();

        // Cesión Solicitada
        cs.setId(null); // Hasta que no se guarda no se obtiene un Id
        cs.setIdClaveServicio(pRango.getClaveServicio().getCodigo());
        cs.setSna(pRango.getSerie().getId().getSna());
        cs.setNumInicio(pRango.getNumInicio().toString());
        cs.setNumFinal(pRango.getNumFinal().toString());
        cs.setSolicitudCesion(solicitud);
        cs.setProveedorCedente(solicitud.getProveedorSolicitante());
        cs.setProveedorCesionario(solicitud.getProveedorCesionario());
        cs.setProveedorConcesionario(pRango.getConcesionario());
        cs.setProveedorArrendatario(pRango.getArrendatario());
        cs.setCliente(pRango.getCliente());
        cs.setAbc(pRango.getAbc());
        cs.setBcd(pRango.getBcd());

        // Información para el Oficio.
        cs.setIda(solicitud.getProveedorSolicitante().getIda());

        // Información de la solicitud de asignación.
        cs.setNumOficioAsigRango(pRango.getOficioAsignacion());
        cs.setFechaAsignacion(pRango.getFechaAsignacion());
        cs.setConsecutivoAsignacion(pRango.getConsecutivoAsignacion());

        // Indicamos si se ha fraccionado la numeración con la cesión
        if (usarFraccionamiento) {
            cs.setFraccionamientoRango("S");
        } else {
            cs.setFraccionamientoRango("N");
        }

        // Fecha de Implementación
        cs.setFechaCesion(FechasUtils.parseFecha(fechaImplementacion));

        // Estado Reservado para su posterior cesión.
        EstadoCesionSolicitada ecs = new EstadoCesionSolicitada();
        ecs.setCodigo(EstadoCesionSolicitada.PENDIENTE);
        cs.setEstatus(ecs);

        return cs;
    }

    /**
     * Indica si una lista contiene una CesionSolicitadaNng sin usar los métos Equals de Object.
     * @param pList Lista de objetos CesionSolicitadaNng
     * @param pCesSol CesionSolicitadaNng a comparar
     * @return True si la CesionSolicitadaNg esta en la lista
     * @throws Exception en caso de error
     */
    private boolean arrayContains(List<CesionSolicitadaNng> pList, CesionSolicitadaNng pCesSol) throws Exception {

        // Cuando se guardan los cambios las CesionSolicitadaNng tienen otra instancia diferente dada
        // por JPA y, por lo tanto, no se pueden comparar con las almacenadas en la lista interna. Es necesario
        // comparar por los campos clave para saber si nos estamos refiriendo a la misma.

        for (CesionSolicitadaNng cs : pList) {
            if (cs.getIdClaveServicio().intValue() == pCesSol.getIdClaveServicio().intValue()
                    && cs.getSna().intValue() == pCesSol.getSna().intValue()
                    && cs.getNumInicio().equals(pCesSol.getNumInicio())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Guarda los cambios realizados hasta el momento en el trámite.
     * @return 'True' si se ha podido guardar correctamente.
     */
    private boolean guardarCambios() {
        try {
            // Buscamos las cesiones que se han deseleccionado
            ArrayList<CesionSolicitadaNng> cesSolEliminar = new ArrayList<CesionSolicitadaNng>();
            for (CesionSolicitadaNng cesSol : solicitud.getCesionesSolicitadas()) {
                // Ignoramos las cesiones que ya se hayan ejecutado
                if (cesSol.getEstatus().getCodigo().equals(EstadoCesionSolicitada.PENDIENTE)) {
                    if ((!arrayContains(listaCesiones, cesSol))) {
                        // No podemos eliminar una cesión solicitada de la lista mientras estamos
                        // recorriendo la misma lista (ConcurrentModificationException).
                        // Por lo tanto la añadimos a otra lista para eliminarla al salir del bucle.
                        cesSolEliminar.add(cesSol);
                    }
                }
            }

            // Eliminamos de la solicitud las CesionSolicitadas que se han eliminado de la tabla de cesiones.
            for (CesionSolicitadaNng cesSol : cesSolEliminar) {
                solicitud.removeCesionSolicitada(cesSol);
            }

            // Agregamos las cesiones que se han añadido
            for (CesionSolicitadaNng cesSol : listaCesiones) {
                if (!arrayContains(solicitud.getCesionesSolicitadas(), cesSol)) {
                    if (usarAbcCesionario) {
                        cesSol.setUsarAbcCesionario("S");
                        cesSol.setUsarConvenioCesionario("N");
                        cesSol.setConvenioCesionario(null);
                    } else {
                        cesSol.setUsarAbcCesionario("N");
                        cesSol.setUsarConvenioCesionario("S");
                        cesSol.setConvenioCesionario(convenioConcesionarioSeleccionado);
                    }
                    // Agregamos la CesionSolicitadaNg a la Solicitud
                    solicitud.addCesionSolicitada(cesSol);
                }
            }

            // Guardamos los cambios y generamos los nuevos registros
            solicitud = nngFacade.saveSolicitudCesion(solicitud);

            // Actualizamos la solicitud para todos los tabs
            getWizard().setSolicitud(solicitud);

            // Una vez persistidas las CesionesSolicitadas rehacemos la tabla de cesiones para que todas
            // tengan el id ya generado
            listaCesiones.clear();
            listaCesiones.addAll(solicitud.getCesionesSolicitadas());

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

    /** Actualiza las opciones del combo de selección de tipo Proveedor en función del Proveedor Cesionario. */
    private void actualizarComboTiposProveedor() {
        // Lista de Tipos de Proveedor.
        listaTiposProveedor = new ArrayList<TipoProveedor>(2);

        String tipoRedCesionario = this.solicitud.getProveedorCesionario().getTipoRed().getCdg();
        if (tipoRedCesionario.equals(TipoRed.AMBAS)) {
            // Si el Cesionario soporta todos los tipos de red puede actuar como Concesionario
            // o comercializador
            listaTiposProveedor.add(pstComercializadora);
            listaTiposProveedor.add(pstConcesionario);
        } else {
            String tipoRedCedente = this.solicitud.getProveedorSolicitante().getTipoRed().getCdg();
            if (tipoRedCesionario.equals(tipoRedCedente)) {
                // Si cedente y cesionario tienen el mismo tipo de red el cesionario puede actuar
                // como Concesionario o como Comercialziador
                listaTiposProveedor.add(pstConcesionario);
                listaTiposProveedor.add(pstComercializadora);
            } else {
                // Si cedente y cesionario no tienen el mismo tipo de red el cesionario solo puede
                // actuar como comercializadora
                listaTiposProveedor.add(pstComercializadora);
            }
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
            sbAviso.insert(0, "La asignación que desea ceder hace parte de una asignación con arrendamiento. "
                    + "Sus datos son:<br><br>");

            rangoArrendadoMsg = sbAviso.toString();

            // Mostramos la modal.
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('DLG_ArrendatarioCesNng').show()");
        }

        return rangosArrendados;
    }

    // GETTERS & SETTERS

    /**
     * Identificador de numeración.
     * @return String
     */
    public String getSna() {
        return sna;
    }

    /**
     * Identificador de numeración.
     * @param sna String
     */
    public void setSna(String sna) {
        this.sna = sna;
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
     * Lazy Model para carga de resultados en la tabla de Series / Rangos.
     * @return RangoSerieNngLazyModel
     */
    public RangoSerieNngLazyModel getRangoSerieModel() {
        return rangoSerieModel;
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
     * Fecha de implementación de la cesión.
     * @return Fecha
     */
    public Date getFechaImplementacion() {
        return fechaImplementacion;
    }

    /**
     * Fecha de implementación de la cesión.
     * @param fechaImplementacion Fecha
     */
    public void setFechaImplementacion(Date fechaImplementacion) {
        this.fechaImplementacion = fechaImplementacion;
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
     * Rango seleccionado para fraccionar.
     * @return RangoSerieNng
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
     * Lista de fracciones sobre una serie o rangos resultado de las fracciones indicadas.
     * @return List
     */
    public List<RangoSerieNng> getListaRangoFraccionados() {
        return listaRangoFraccionados;
    }

    /**
     * Lista de Cesiones solicitadas por el Cedente para el Cesionario.
     * @return List
     */
    public List<CesionSolicitadaNng> getListaCesiones() {
        return listaCesiones;
    }

    /**
     * Fracción de rango en la tabla de Fracciones.
     * @return RangoSerie
     */
    public RangoSerieNng getRangoFraccionado() {
        return rangoFraccionado;
    }

    /**
     * Fracción de rango en la tabla de Fracciones.
     * @param rangoFraccionado RangoSerie
     */
    public void setRangoFraccionado(RangoSerieNng rangoFraccionado) {
        this.rangoFraccionado = rangoFraccionado;
    }

    /**
     * Indica si se ha de utilizar el ABC del Cesionario Concesionario para enrutar la numeración que recibe.
     * @return the usarAbcCesionario
     */
    public boolean isUsarAbcCesionario() {
        return usarAbcCesionario;
    }

    /**
     * Indica si se ha de utilizar el IDO del Cesionario Concesionario para enrutar la numeración que recibe.
     * @param usarAbcCesionario the usarAbcCesionario to set
     */
    public void setUsarAbcCesionario(boolean usarAbcCesionario) {
        this.usarAbcCesionario = usarAbcCesionario;
    }

    /**
     * Indica si se ha de utilizar el Convenio del Proveedor Cesionario con un Proveedor Concesionario.
     * @return boolean
     */
    public boolean isUsarConvenioCesionario() {
        return usarConvenioCesionario;
    }

    /**
     * Indica si se ha de utilizar el Convenio del Proveedor Cesionario con un Proveedor Concesionario.
     * @param usarConvenioCesionario boolean
     */
    public void setUsarConvenioCesionario(boolean usarConvenioCesionario) {
        this.usarConvenioCesionario = usarConvenioCesionario;
    }

    /**
     * Lista de Convenios del Proveedor Cesionario.
     * @return List
     */
    public List<ProveedorConvenio> getListaConveniosCesionario() {
        return listaConveniosCesionario;
    }

    /**
     * Convenio del Proveedor Cesionario Seleccionado.
     * @return ProveedorConvenio
     */
    public ProveedorConvenio getConvenioConcesionarioSeleccionado() {
        return convenioConcesionarioSeleccionado;
    }

    /**
     * Convenio del Proveedor Cesionario Seleccionado.
     * @param convenioConcesionarioSeleccionado ProveedorConvenio
     */
    public void setConvenioConcesionarioSeleccionado(ProveedorConvenio convenioConcesionarioSeleccionado) {
        this.convenioConcesionarioSeleccionado = convenioConcesionarioSeleccionado;
    }

    /**
     * Indica si el Proveedor Cesionario es Concesionario o Ambos y se puede utilizar su ABC.
     * @return the habilitarUsarAbc
     */
    public boolean isHabilitarUsarAbc() {
        return habilitarUsarAbc;
    }

    /**
     * Indica si el Proveedor Cesionario es Concesionario o Ambos y se puede utilizar su ABC.
     * @param habilitarUsarAbc the habilitarUsarAbc to set
     */
    public void setHabilitarUsarAbc(boolean habilitarUsarAbc) {
        this.habilitarUsarAbc = habilitarUsarAbc;
    }

    /**
     * Lista de Tipos de Proveedor.
     * @return the listaTiposProveedor
     */
    public List<TipoProveedor> getListaTiposProveedor() {
        return listaTiposProveedor;
    }

    /**
     * Tipo de Proveedor Seleccionado..
     * @return the tipoProveedorSeleccionado
     */
    public TipoProveedor getTipoProveedorSeleccionado() {
        return tipoProveedorSeleccionado;
    }

    /**
     * Tipo de Proveedor Seleccionado..
     * @param tipoProveedorSeleccionado the tipoProveedorSeleccionado to set
     */
    public void setTipoProveedorSeleccionado(TipoProveedor tipoProveedorSeleccionado) {
        this.tipoProveedorSeleccionado = tipoProveedorSeleccionado;
    }

    /**
     * Indica si el combo de selección de tipo de Proveedor está habilitado o no.
     * @return the tipoProveedorHabilitado
     */
    public boolean isTipoProveedorHabilitado() {
        return tipoProveedorHabilitado;
    }

    /**
     * Indica si el combo de selección de tipo de Proveedor está habilitado o no.
     * @param tipoProveedorHabilitado the tipoProveedorHabilitado to set
     */
    public void setTipoProveedorHabilitado(boolean tipoProveedorHabilitado) {
        this.tipoProveedorHabilitado = tipoProveedorHabilitado;
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
     * Cesion Solicitada seleccionada de la tabla de Cesiones.
     * @return CesionSolicitadaNng
     */
    public CesionSolicitadaNng getCesSolSeleccionada() {
        return cesSolSeleccionada;
    }

    /**
     * Cesion Solicitada seleccionada de la tabla de Cesiones.
     * @param cesSolSeleccionada CesionSolicitadaNng
     */
    public void setCesSolSeleccionada(CesionSolicitadaNng cesSolSeleccionada) {
        this.cesSolSeleccionada = cesSolSeleccionada;
    }

    /**
     * Información de la solicitud de cesión.
     * @return SolicitudCesionNng
     */
    public SolicitudCesionNng getSolicitud() {
        return solicitud;
    }

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
