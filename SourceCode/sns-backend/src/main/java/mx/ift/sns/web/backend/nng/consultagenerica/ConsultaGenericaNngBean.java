package mx.ift.sns.web.backend.nng.consultagenerica;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.nng.SolicitudAsignacionNng;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.lazymodels.ConsultaGenericaNngLazyModel;
import mx.ift.sns.web.backend.nng.asignacion.ConsultarAsignacionesNngBean;
import mx.ift.sns.web.backend.nng.cesion.ConsultarCesionesNngBean;
import mx.ift.sns.web.backend.nng.liberacion.ConsultarLiberacionesNngBean;
import mx.ift.sns.web.backend.nng.redistribucion.ConsultarRedistribucionesNngBean;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Bean asociado a la búsqueda de todas las solicitudes de Numeración No Geográfica. */
@ManagedBean(name = "consultaGenericaNngBean")
@ViewScoped
public class ConsultaGenericaNngBean implements Serializable {

    /** serialVersionUID. **/
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultaGenericaNngBean.class);

    /** Fuente inicial vacía. */
    // private static final String BLANK_SRC = "/errores/blank.xhtml";

    /** Fuente de la solicitud de asignación manual. */
    private static final String ASIGNACION_SRC = "/numeracion-no-geografica/asignacion/solicitud-asignacion-nng.xhtml";

    /** Fuente de la solicitud de cesión. */
    private static final String CESION_SRC = "/numeracion-no-geografica/cesion/solicitud-cesion-nng.xhtml";

    /** Fuente de la solicitud de liberación. */
    private static final String LIBERACION_SRC = "/numeracion-no-geografica/liberacion/solicitud-liberacion-nng.xhtml";

    /** Fuente de la solicitud de redistribución. */
    private static final String REDISTRIBUCION_SRC =
            "/numeracion-no-geografica/redistribuciones/solicitud-redistribucion-nng.xhtml";

    /** Atributo que almacena el fuente a mostrar en el diálogo. */
    private String src;

    /** Atributo que almacena la cabecera del dialogo. */
    private String header;

    /** consecutivo. **/
    private String consecutivo;

    /** fechaIniSolicitud. **/
    private Date fechaIniSolicitud;

    /** fechaFinSolicitud. **/
    private Date fechaFinSolicitud;

    /** fechaIniTramitacion. **/
    private Date fechaIniTramitacion;

    /** fechaFinTramitacion. **/
    private Date fechaFinTramitacion;

    /** Catálogo de proveedores. */
    private List<Proveedor> listaProveedores;

    /** Proveedor seleccionado para la búsqueda. */
    private Proveedor proveedorSeleccionado;

    /** Lista para los estados solicitudes. **/
    private List<EstadoSolicitud> listaEstadosSolicidud;

    /** EstadoSolicitud. **/
    private EstadoSolicitud estatusSeleccionado;

    /** Lista para los estados solicitudes. **/
    private List<TipoSolicitud> listaTipoSolicidud;

    /** Tipo de solicitud. **/
    private TipoSolicitud tipoSolicitudSelecc;

    /** Modelo Lazy para carga de solicitudes de consolidaciones. */
    private ConsultaGenericaNngLazyModel solicitudesModel;

    /** Filtros de búsqueda de Solicitudes. */
    private FiltroBusquedaSolicitudes filtros;

    /** solicitud de consolidacion seleccionada. **/
    private Solicitud solicitudSeleccionada;

    /** Referencia al Bean de Consulta Solicitud de Asignacion NNG. */
    @ManagedProperty("#{consultarAsignacionesNngBean}")
    private ConsultarAsignacionesNngBean consultarAsignacionesNngBean;

    /** Referencia al Bean de Consulta de Solicitud de Cesion NNG. */
    @ManagedProperty("#{consultarCesionesNngBean}")
    private ConsultarCesionesNngBean consultarCesionesNngBean;

    /** Referencia al Bean de Consulta de Solicitud de Liberacion NNG. */
    @ManagedProperty("#{consultarLiberacionesNngBean}")
    private ConsultarLiberacionesNngBean consultarLiberacionesNngBean;

    /** Referencia al Bean de Consulta de Solicitud de Redistribucion NNG. */
    @ManagedProperty("#{consultarRedistribucionesNngBean}")
    private ConsultarRedistribucionesNngBean consultarRedistribucionesNngBean;

    /** Fecha Solicitud Habilitada. */
    private boolean fechaSoliHabilitada = false;

    /** Fecha Tramitación Habilitada. */
    private boolean fechaTramHabilitada = false;

    /** Búsqueda por oficio del proveedor solicitante. */
    private String oficioPstSolicitante = "";

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultaGenericaNNG";

    /** Prefijo para la búsqueda de oficios. */
    private String prefijoOficio;

    /** Llamada a la clase de negocio. **/
    @EJB(mappedName = "NumeracionNoGeograficaFacade")
    private INumeracionNoGeograficaFacade ngService;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /**
     * Constructor vacío por defecto, la carga se hace en el método anotado 'PostConstruct'.
     */
    public ConsultaGenericaNngBean() {
    }

    /** Método invocado al cargar terminar de instanciar el bean. */
    @PostConstruct
    public void init() {
        try {
            // Listado de proveedores de servicio
            listaEstadosSolicidud = ngService.findAllEstadosSolicitud();

            // Listado de proveedores de servicio
            listaProveedores = ngService.findAllProveedoresActivos();
            proveedorSeleccionado = new Proveedor();

            // Listado de tipos de solicitud
            listaTipoSolicidud = ngService.findAllTiposSolicitud();
            tipoSolicitudSelecc = new TipoSolicitud();

            EstadoSolicitud estado = new EstadoSolicitud();
            estado.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);

            filtros = new FiltroBusquedaSolicitudes();
            filtros.setEstado(estado);

            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(ngService,
                    Parametro.REGISTROS_POR_PAGINA_BACK));
            registroPorPagina = filtros.getResultadosPagina();

            solicitudesModel = new ConsultaGenericaNngLazyModel();
            solicitudesModel.setFiltros(filtros);
            solicitudesModel.setService(ngService);
            solicitudesModel.setMessagesId(MSG_ID);

            // Plantilla de oficio PST Solicitante
            prefijoOficio = ngService.getParamByName(Parametro.PREFIJO);
            oficioPstSolicitante = new String(prefijoOficio);
            src = ASIGNACION_SRC;
            header = "";

        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error inesperado: " + e.getMessage());
            }
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
        }
    }

    /**
     * Acción invocada al pulsar sobre el botón 'Búsqueda' del formulario.
     */
    public void realizarBusqueda() {
        try {
            if (solicitudesModel == null) {
                solicitudesModel = new ConsultaGenericaNngLazyModel();
                solicitudesModel.setFiltros(filtros);
                solicitudesModel.setService(ngService);
            }

            // Resetamos los filtros
            filtros.clear();
            solicitudesModel.clear();

            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(ngService,
                    Parametro.REGISTROS_POR_PAGINA_BACK));

            // Filtro de valor de consecutivo
            if (!StringUtils.isEmpty(consecutivo)) {
                if (StringUtils.isNumeric(consecutivo)) {
                    if (StringUtils.length(consecutivo) <= 9) {
                        filtros.setConsecutivo(consecutivo);
                    } else {
                        MensajesBean.addErrorMsg(MSG_ID,
                                "El campo 'consecutivo' ha de tener un formato máximo de 9 dígitos", "");
                    }
                } else {
                    MensajesBean.addErrorMsg(MSG_ID,
                            "El campo 'Consecutivo' ha de ser un valor numérico", "");
                }
            }

            // Oficio del PST Solicitante
            if (!StringUtils.isEmpty(oficioPstSolicitante) && !oficioPstSolicitante.equals(prefijoOficio)) {
                filtros.setOficioPstSolicitante(oficioPstSolicitante);
            }

            filtros.setProveedor(proveedorSeleccionado);

            filtros.setNumIniciofchSolicitud(fechaIniSolicitud);
            if (fechaIniSolicitud != null && fechaFinSolicitud == null) {
                filtros.setNumFinalfchSolicitud(fechaIniSolicitud);
            } else {
                filtros.setNumFinalfchSolicitud(fechaFinSolicitud);
            }

            filtros.setFechaIniTramitacion(fechaIniTramitacion);
            if (fechaIniTramitacion != null && fechaFinTramitacion == null) {
                filtros.setFechaFinTramitacion(fechaIniTramitacion);
            } else {
                filtros.setFechaFinTramitacion(fechaFinTramitacion);
            }

            filtros.setEstado(estatusSeleccionado);

            if (tipoSolicitudSelecc != null) {
                filtros.setTipoSolicitud(tipoSolicitudSelecc);
            }

            PaginatorUtil.resetPaginacion("FRM_ConsultaGenericaNNG:TBL_ConsultaSolicitudes",
                    filtros.getResultadosPagina());

            // Si ha seleccionado fechas comprobamos que sean coherentes (VL1)
            if ((fechaIniSolicitud != null && fechaFinSolicitud != null)
                    && (fechaFinSolicitud.before(fechaIniSolicitud))) {
                MensajesBean.addErrorMsg(MSG_ID,
                        "La fecha de inicio debe ser menor o igual a la fecha final", "");
            }
            if ((fechaIniTramitacion != null && fechaFinTramitacion != null)
                    && (fechaFinTramitacion.before(fechaIniTramitacion))) {
                MensajesBean.addErrorMsg(MSG_ID,
                        "La fecha de inicio debe ser menor o igual a la fecha final", "");
            }
        } catch (Exception e) {
            MensajesBean.addErrorMsg(MSG_ID, "Error inesperado: " + e.getMessage(), "");
        }
    }

    /** Método invocado al pulsar el botón 'limpiar' sobre el buscador de solicitudes. */
    public void limpiarBusqueda() {
        this.consecutivo = null;
        this.estatusSeleccionado = null;
        this.fechaFinTramitacion = null;
        this.fechaFinSolicitud = null;
        this.fechaIniTramitacion = null;
        this.fechaIniSolicitud = null;
        this.proveedorSeleccionado = null;
        this.tipoSolicitudSelecc = null;
        this.oficioPstSolicitante = new String(prefijoOficio);
        this.solicitudesModel = null;
        this.fechaSoliHabilitada = false;
        this.fechaTramHabilitada = false;
        this.solicitudSeleccionada = null;
        this.filtros = new FiltroBusquedaSolicitudes();
    }

    /** Método invocado al pulsar el botón 'editar' de la solicitud. */
    public void editarSolicitud() {
        RequestContext context = null;
        context = RequestContext.getCurrentInstance();
        try {
            if (solicitudSeleccionada != null) {
                if (solicitudSeleccionada.getTipoSolicitud().getCdg().equals(TipoSolicitud.ASIGNACION_NNG)) {
                    consultarAsignacionesNngBean.editarSolicitud(solicitudSeleccionada);
                    src = ASIGNACION_SRC;
                    header = MensajesBean.getTextoResource("cabecera.solicitud.asignacion.manual");
                } else if (solicitudSeleccionada.getTipoSolicitud().getCdg()
                        .equals(TipoSolicitud.CESION_DERECHOS_NNG)) {
                    consultarCesionesNngBean.editarSolicitud(solicitudSeleccionada);
                    src = CESION_SRC;
                    header = MensajesBean.getTextoResource("cabecera.solicitud.cesion");
                } else if (solicitudSeleccionada.getTipoSolicitud().getCdg().equals(TipoSolicitud.LIBERACION_NNG)) {
                    consultarLiberacionesNngBean.editarSolicitud(solicitudSeleccionada);
                    src = LIBERACION_SRC;
                    header = MensajesBean.getTextoResource("cabecera.solicitud.liberación");
                } else {
                    consultarRedistribucionesNngBean.editarSolicitud(solicitudSeleccionada);
                    src = REDISTRIBUCION_SRC;
                    header = MensajesBean.getTextoResource("cabecera.solicitud.redistribucion");
                }
            }
            context.addCallbackParam("tipoSolicitud", solicitudSeleccionada.getTipoSolicitud().getCdg());
        } catch (Exception e) {
            MensajesBean.addErrorMsg(MSG_ID, "Error inesperado: " + e.getMessage(), "");
        }
    }

    /** Método invocado al pulsar el botón 'Si' al eliminar una solicitud. */
    public void eliminarSolicitud() {
        try {
            if (solicitudSeleccionada != null) {
                if (solicitudSeleccionada.getTipoSolicitud().getCdg().equals(TipoSolicitud.ASIGNACION_NNG)) {
                    consultarAsignacionesNngBean.eliminarSolicitud((SolicitudAsignacionNng) solicitudSeleccionada,
                            MSG_ID);
                } else if (solicitudSeleccionada.getTipoSolicitud().
                        getCdg().equals(TipoSolicitud.CESION_DERECHOS_NNG)) {
                    consultarCesionesNngBean.eliminarSolicitud(solicitudSeleccionada, MSG_ID);
                } else if (solicitudSeleccionada.getTipoSolicitud().getCdg().equals(TipoSolicitud.LIBERACION_NNG)) {
                    consultarLiberacionesNngBean.eliminarSolicitud(solicitudSeleccionada, MSG_ID);
                } else {
                    consultarRedistribucionesNngBean.eliminarSolicitud(solicitudSeleccionada, MSG_ID);
                }
            }
        } catch (Exception e) {
            MensajesBean.addErrorMsg(MSG_ID, "Error inesperado: " + e.getMessage(), "");
        }
    }

    /** Habilita la fecha Fin al editar la fecha inicio. */
    public void habilitarFechaFinSoli() {
        if (fechaIniSolicitud != null) {
            fechaSoliHabilitada = true;
        } else {
            fechaSoliHabilitada = false;
            fechaFinSolicitud = null;
        }
    }

    /** Habilita la fecha Fin al editar la fecha inicio. */
    public void habilitarFechaFinTram() {
        if (fechaIniTramitacion != null) {
            fechaTramHabilitada = true;
        } else {
            fechaTramHabilitada = false;
            fechaFinTramitacion = null;
        }
    }

    /**
     * Método que invoca el resetTabs de cada uno de los posibles beans asociados a los trámites consultados en la
     * consulta genérica.
     */
    public void resetTabs() {
        if (solicitudSeleccionada != null) {
            if (solicitudSeleccionada.getTipoSolicitud().getCdg().equals(TipoSolicitud.ASIGNACION_NNG)) {
                consultarAsignacionesNngBean.getSolicitudAsignacionNngBean().resetTabs();
            } else if (solicitudSeleccionada.getTipoSolicitud().getCdg().equals(TipoSolicitud.CESION_DERECHOS_NNG)) {
                consultarCesionesNngBean.getSolicitudCesionNngBean().resetTabs();
            } else if (solicitudSeleccionada.getTipoSolicitud().getCdg().equals(TipoSolicitud.LIBERACION_NNG)) {
                consultarLiberacionesNngBean.getSolicitudLiberacionBean().resetTabs();
            } else {
                consultarRedistribucionesNngBean.getSolicitudRedistribucionBean().resetTabs();
            }
        }
    }

    /**
     * reset.
     */
    public void reset() {
        this.realizarBusqueda();
    }

    /**
     * Identificador de la solicitud.
     * @return the consecutivo
     */
    public String getConsecutivo() {
        return consecutivo;
    }

    /**
     * Identificador de la solicitud.
     * @param consecutivo the consecutivo to set
     */
    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    /**
     * Identificador de la fecha inicio de la solicitud.
     * @return the fechaIniSolicitud
     */
    public Date getFechaIniSolicitud() {
        return fechaIniSolicitud;
    }

    /**
     * Identificador de la fecha inicio de la solicitud.
     * @param fechaIniSolicitud the fechaIniSolicitud to set
     */
    public void setFechaIniSolicitud(Date fechaIniSolicitud) {
        this.fechaIniSolicitud = fechaIniSolicitud;
    }

    /**
     * Identificador de la fecha fin de la solicitud.
     * @return the fechaFinSolicitud
     */
    public Date getFechaFinSolicitud() {
        return fechaFinSolicitud;
    }

    /**
     * Identificador de la fecha fin de la solicitud.
     * @param fechaFinSolicitud the fechaFinSolicitud to set
     */
    public void setFechaFinSolicitud(Date fechaFinSolicitud) {
        this.fechaFinSolicitud = fechaFinSolicitud;
    }

    /**
     * Identificador de los estados de la solicitud.
     * @return the listaEstadosSolicidud
     */
    public List<EstadoSolicitud> getListaEstadosSolicidud() {
        return listaEstadosSolicidud;
    }

    /**
     * Identificador de los estados de la solicitud.
     * @param listaEstadosSolicidud the listaEstadosSolicidud to set
     */
    public void setListaEstadosSolicidud(List<EstadoSolicitud> listaEstadosSolicidud) {
        this.listaEstadosSolicidud = listaEstadosSolicidud;
    }

    /**
     * Identificador del estado seleccionado.
     * @return the estatusSeleccionado
     */
    public EstadoSolicitud getEstatusSeleccionado() {
        return estatusSeleccionado;
    }

    /**
     * Identificador del estado seleccionado.
     * @param estatusSeleccionado the estatusSeleccionado to set
     */
    public void setEstatusSeleccionado(EstadoSolicitud estatusSeleccionado) {
        this.estatusSeleccionado = estatusSeleccionado;
    }

    /**
     * Identificador de la solicitud seleccionada.
     * @return the solicitudSeleccionada
     */
    public Solicitud getSolicitudSeleccionada() {
        return solicitudSeleccionada;
    }

    /**
     * Identificador de la solicitud seleccionada.
     * @param solicitudSeleccionada the solicitudSeleccionada to set
     */
    public void setSolicitudSeleccionada(Solicitud solicitudSeleccionada) {
        this.solicitudSeleccionada = solicitudSeleccionada;
    }

    /**
     * Identificador de la fecha de inicio de tramitación.
     * @return the fechaIniTramitacion
     */
    public Date getFechaIniTramitacion() {
        return fechaIniTramitacion;
    }

    /**
     * Identificador de la fecha de inicio de tramitación.
     * @param fechaIniTramitacion the fechaIniTramitacion to set
     */
    public void setFechaIniTramitacion(Date fechaIniTramitacion) {
        this.fechaIniTramitacion = fechaIniTramitacion;
    }

    /**
     * Identificador de la fecha de fin de tramitación.
     * @return the fechaFinTramitacion
     */
    public Date getFechaFinTramitacion() {
        return fechaFinTramitacion;
    }

    /**
     * Identificador de la fecha de fin de tramitación.
     * @param fechaFinTramitacion the fechaFinTramitacion to set
     */
    public void setFechaFinTramitacion(Date fechaFinTramitacion) {
        this.fechaFinTramitacion = fechaFinTramitacion;
    }

    /**
     * Identificador del proveedor seleccionado.
     * @return the proveedorSeleccionado
     */
    public Proveedor getProveedorSeleccionado() {
        return proveedorSeleccionado;
    }

    /**
     * Identificador del proveedor seleccionado.
     * @param proveedorSeleccionado the proveedorSeleccionado to set
     */
    public void setProveedorSeleccionado(Proveedor proveedorSeleccionado) {
        this.proveedorSeleccionado = proveedorSeleccionado;
    }

    /**
     * Identificador del tipo de solicitud.
     * @return the tipoSolicitudSelecc
     */
    public TipoSolicitud getTipoSolicitudSelecc() {
        return tipoSolicitudSelecc;
    }

    /**
     * Identificador del tipo de solicitud.
     * @param tipoSolicitudSelecc the tipoSolicitudSelecc to set
     */
    public void setTipoSolicitudSelecc(TipoSolicitud tipoSolicitudSelecc) {
        this.tipoSolicitudSelecc = tipoSolicitudSelecc;
    }

    /**
     * Identificador de la lista de los proveedores.
     * @return the listaProveedores
     */
    public List<Proveedor> getListaProveedores() {
        return listaProveedores;
    }

    /**
     * Identificador de la lista de los proveedores.
     * @param listaProveedores the listaProveedores to set
     */
    public void setListaProveedores(List<Proveedor> listaProveedores) {
        this.listaProveedores = listaProveedores;
    }

    /**
     * Modelo Lazy para carga de solicitudes.
     * @return the solicitudesModel
     */
    public ConsultaGenericaNngLazyModel getSolicitudesModel() {
        return solicitudesModel;
    }

    /**
     * Modelo Lazy para carga de solicitudes.
     * @param solicitudesModel the solicitudesModel to set
     */
    public void setSolicitudesModel(ConsultaGenericaNngLazyModel solicitudesModel) {
        this.solicitudesModel = solicitudesModel;
    }

    /**
     * Identificador de la lista de tipos de solicitud.
     * @return the listaTipoSolicidud
     */
    public List<TipoSolicitud> getListaTipoSolicidud() {
        return listaTipoSolicidud;
    }

    /**
     * Identificador de la lista de tipos de solicitud.
     * @param listaTipoSolicidud the listaTipoSolicidud to set
     */
    public void setListaTipoSolicidud(List<TipoSolicitud> listaTipoSolicidud) {
        this.listaTipoSolicidud = listaTipoSolicidud;
    }

    /**
     * Referencia al Bean de Solicitud de asignación.
     * @return the consultarAsignacionesNngBean
     */
    public ConsultarAsignacionesNngBean getConsultarAsignacionesNngBean() {
        return consultarAsignacionesNngBean;
    }

    /**
     * Referencia al Bean de Solicitud de asignación.
     * @param consultarAsignacionesNngBean the consultarAsignacionesNngBean to set
     */
    public void setConsultarAsignacionesNngBean(ConsultarAsignacionesNngBean consultarAsignacionesNngBean) {
        this.consultarAsignacionesNngBean = consultarAsignacionesNngBean;
    }

    /**
     * Referencia al Bean de Solicitud de cesión.
     * @return the consultarCesionesNngBean
     */
    public ConsultarCesionesNngBean getConsultarCesionesNngBean() {
        return consultarCesionesNngBean;
    }

    /**
     * Referencia al Bean de Solicitud de cesión.
     * @param consultarCesionesNngBean the consultarCesionesNngBean to set
     */
    public void setConsultarCesionesNngBean(ConsultarCesionesNngBean consultarCesionesNngBean) {
        this.consultarCesionesNngBean = consultarCesionesNngBean;
    }

    /**
     * Referencia al Bean de Solicitud de liberación.
     * @return the consultarLiberacionesNngBean
     */
    public ConsultarLiberacionesNngBean getConsultarLiberacionesNngBean() {
        return consultarLiberacionesNngBean;
    }

    /**
     * Referencia al Bean de Solicitud de liberación.
     * @param consultarLiberacionesNngBean the consultarLiberacionesNngBean to set
     */
    public void setConsultarLiberacionesNngBean(ConsultarLiberacionesNngBean consultarLiberacionesNngBean) {
        this.consultarLiberacionesNngBean = consultarLiberacionesNngBean;
    }

    /**
     * Referencia al Bean de Solicitud de redistribución.
     * @return the consultarRedistribucionesNngBean
     */
    public ConsultarRedistribucionesNngBean getConsultarRedistribucionesNngBean() {
        return consultarRedistribucionesNngBean;
    }

    /**
     * Referencia al Bean de Solicitud de redistribución.
     * @param consultarRedistribucionesNngBean the consultarRedistribucionesNngBean to set
     */
    public void setConsultarRedistribucionesNngBean(ConsultarRedistribucionesNngBean consultarRedistribucionesNngBean) {
        this.consultarRedistribucionesNngBean = consultarRedistribucionesNngBean;
    }

    /**
     * Identificador que habilita la fecha fin de solicitud.
     * @return the fechaSoliHabilitada
     */
    public boolean isFechaSoliHabilitada() {
        return fechaSoliHabilitada;
    }

    /**
     * Identificador que habilita la fecha fin de solicitud.
     * @param fechaSoliHabilitada the fechaSoliHabilitada to set
     */
    public void setFechaSoliHabilitada(boolean fechaSoliHabilitada) {
        this.fechaSoliHabilitada = fechaSoliHabilitada;
    }

    /**
     * Identificador que habilita la fecha fin de tramitación.
     * @return the fechaTramHabilitada
     */
    public boolean isFechaTramHabilitada() {
        return fechaTramHabilitada;
    }

    /**
     * Identificador que habilita la fecha fin de tramitación.
     * @param fechaTramHabilitada the fechaTramHabilitada to set
     */
    public void setFechaTramHabilitada(boolean fechaTramHabilitada) {
        this.fechaTramHabilitada = fechaTramHabilitada;
    }

    /**
     * Número de oficio del PST Solicitante para las búsquedas.
     * @return String
     */
    public String getOficioPstSolicitante() {
        return oficioPstSolicitante;
    }

    /**
     * Número de oficio del PST Solicitante para las búsquedas.
     * @param oficioPstSolicitante String
     */
    public void setOficioPstSolicitante(String oficioPstSolicitante) {
        this.oficioPstSolicitante = oficioPstSolicitante;
    }

    /**
     * Atributo que almacena el fuente a mostrar en el diálogo.
     * @return the src
     */
    public String getSrc() {
        return src;
    }

    /**
     * Atributo que almacena el fuente a mostrar en el diálogo.
     * @param src the src to set
     */
    public void setSrc(String src) {
        this.src = src;
    }

    /**
     * Atributo que almacena la cabecera del dialogo.
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * Atributo que almacena la cabecera del dialogo.
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * Obtiene el valor del número de elementos por página de la tabla de resultados.
     * @return the registroPorPagina
     */
    public int getRegistroPorPagina() {
        return registroPorPagina;
    }

    /**
     * Establece el valor del número de elementos por página de la tabla de resultados.
     * @param registroPorPagina the registroPorPagina to set
     */
    public void setRegistroPorPagina(int registroPorPagina) {
        this.registroPorPagina = registroPorPagina;
    }
}
