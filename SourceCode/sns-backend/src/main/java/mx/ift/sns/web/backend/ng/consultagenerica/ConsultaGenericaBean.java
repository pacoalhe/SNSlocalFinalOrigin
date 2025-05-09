package mx.ift.sns.web.backend.ng.consultagenerica;

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
import mx.ift.sns.modelo.ng.SolicitudAsignacion;
import mx.ift.sns.modelo.ng.SolicitudRedistribucionNg;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.lazymodels.ConsultaGenericaLazyModel;
import mx.ift.sns.web.backend.ng.asignacion.ConsultarAsignacionesNgBean;
import mx.ift.sns.web.backend.ng.cesion.ConsultarCesionesNgBean;
import mx.ift.sns.web.backend.ng.consolidacion.ConsultarConsolidacionesBean;
import mx.ift.sns.web.backend.ng.liberacion.ConsultarLiberacionesNgBean;
import mx.ift.sns.web.backend.ng.redistribucion.ConsultarRedistribucionesNgBean;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Bean asociado a la búsqueda de todas las solicitudes de Numeración Geográfica. */
@ManagedBean(name = "consultaGenericaBean")
@ViewScoped
public class ConsultaGenericaBean implements Serializable {

    /** serialVersionUID. **/
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultaGenericaBean.class);

    /** Fuente inicial vacía. */
    // private static final String BLANK_SRC = "/errores/blank.xhtml";

    /** Fuente de la solicitud de asignación manual. */
    private static final String ASIGNACION_SRC =
            "/numeracion-geografica/asignacion/nueva-asignacion-manual-from-consulta-generica.xhtml";

    /** Fuente de la solicitud de cesión. */
    private static final String CESION_SRC = "/numeracion-geografica/cesion/solicitud-cesion.xhtml";

    /** Fuente de la solicitud de liberación. */
    private static final String LIBERACION_SRC = "/numeracion-geografica/liberacion/solicitud-liberacion.xhtml";

    /** Fuente de la solicitud de redistribución. */
    private static final String REDISTRIBUCION_SRC =
            "/numeracion-geografica/redistribucion/solicitud-redistribucion.xhtml";

    /** Fuente de la solicitud de consolidación de ABNs. */
    private static final String CONSOLIDACION_SRC = "/numeracion-geografica/consolidacion/nueva-consolidacion.xhtml";

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

    /** fechaIniAsignacion. **/
    private Date fechaIniAsignacion;

    /** fechaFinAsignacion. **/
    private Date fechaFinAsignacion;

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
    private ConsultaGenericaLazyModel solicitudesModel;

    /** Filtros de búsqueda de Solicitudes. */
    private FiltroBusquedaSolicitudes filtros;

    /** solicitud de consolidacion seleccionada. **/
    private Solicitud solicitudSeleccionada;

    /** Referencia al Bean de Consulta Solicitud de Asignacion. */
    @ManagedProperty("#{consultarSolicitudesBean}")
    private ConsultarAsignacionesNgBean consultarSolicitudesBean;

    /** Referencia al Bean de Consulta de Solicitud de Cesion. */
    @ManagedProperty("#{consultarCesionesNgBean}")
    private ConsultarCesionesNgBean consultarCesionesBean;

    /** Referencia al Bean de Consulta de Solicitud de Liberacion. */
    @ManagedProperty("#{consultarLiberacionesNgBean}")
    private ConsultarLiberacionesNgBean consultarLiberacionesBean;

    /** Referencia al Bean de Consulta de Solicitud de Redistribucion. */
    @ManagedProperty("#{consultarRedistribucionesNgBean}")
    private ConsultarRedistribucionesNgBean consultarRedistribucionesBean;

    /** Referencia al Bean de Consulta de Solicitud de Consolidacion. */
    @ManagedProperty("#{consultarConsolidacionesBean}")
    private ConsultarConsolidacionesBean consultarConsolidacionesBean;

    /** Fecha Solicitud Habilitada. */
    private boolean fechaSoliHabilitada = false;

    /** Fecha Asignada Habilitada. */
    private boolean fechaAsigHabilitada = false;

    /** Estado. */
    private Estado estadoMun;

    /** Municipio. */
    private Municipio municipio;

    /** Población. */
    private Poblacion poblacion;

    /** Lista Estado. */
    private List<Estado> listaEstados;

    /** Lista Municipio. */
    private List<Municipio> listaMunicipio;

    /** Lista Población. */
    private List<Poblacion> listaPoblacion;

    /** Búsqueda por oficio del proveedor solicitante. */
    private String oficioPstSolicitante = "";

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultaGenerica";

    /** Prefijo para la búsqueda de oficios. */
    private String prefijoOficio;

    /** Llamada a la clase de negocio. **/
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /**
     * Constructor vacío por defecto, la carga se hace en el método anotado 'PostConstruct'.
     */
    public ConsultaGenericaBean() {
    }

    /** Método invocado al cargar terminar de instanciar el bean. */
    @PostConstruct
    public void init() {
        try {
            // Listado de proveedores de servicio
            listaEstadosSolicidud = ngService.findAllEstadosSolicitud();

            // Listado de estados (ubicación)
            listaEstados = ngService.findAllEstados();

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

            solicitudesModel = new ConsultaGenericaLazyModel();
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
                solicitudesModel = new ConsultaGenericaLazyModel();
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

            filtros.setNumInicioFechaAsignacion(fechaIniAsignacion);
            if (fechaIniAsignacion != null && fechaFinAsignacion == null) {
                filtros.setNumFinalFechaAsignacion(fechaIniAsignacion);
            } else {
                filtros.setNumFinalFechaAsignacion(fechaFinAsignacion);
            }

            filtros.setEstado(estatusSeleccionado);
            if (estadoMun != null && municipio == null && poblacion == null) {
                filtros.setEstadoMun(estadoMun);
            } else if (estadoMun != null && municipio != null && poblacion == null) {
                filtros.setEstadoMun(estadoMun);
                filtros.setMunicipio(municipio);
            } else if (estadoMun != null && municipio != null && poblacion != null) {
                filtros.setPoblacion(poblacion);
            }

            if (tipoSolicitudSelecc != null) {
                filtros.setTipoSolicitud(tipoSolicitudSelecc);
            }

            PaginatorUtil.resetPaginacion("FRM_ConsultaGenerica:TBL_ConsultaSolicitudes",
                    filtros.getResultadosPagina());

            // Si ha seleccionado fechas comprobamos que sean coherentes (VL1)
            if ((fechaIniSolicitud != null && fechaFinSolicitud != null)
                    && (fechaFinSolicitud.before(fechaIniSolicitud))) {
                MensajesBean.addErrorMsg(MSG_ID,
                        "La fecha de inicio debe ser menor o igual a la fecha final", "");
            }
            if ((fechaIniAsignacion != null && fechaFinAsignacion != null)
                    && (fechaFinAsignacion.before(fechaIniAsignacion))) {
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
        this.fechaFinAsignacion = null;
        this.fechaFinSolicitud = null;
        this.fechaIniAsignacion = null;
        this.fechaIniSolicitud = null;
        this.proveedorSeleccionado = null;
        this.tipoSolicitudSelecc = null;
        this.estadoMun = null;
        this.municipio = null;
        this.poblacion = null;
        this.oficioPstSolicitante = new String(prefijoOficio);
        this.solicitudesModel = null;
        this.fechaSoliHabilitada = false;
        this.fechaAsigHabilitada = false;
        this.solicitudSeleccionada = null;
        this.filtros = new FiltroBusquedaSolicitudes();
        if (this.listaMunicipio != null) {
            this.listaMunicipio.clear();
        }
        if (this.listaPoblacion != null) {
            this.listaPoblacion.clear();
        }
    }

    /** Método invocado al pulsar el botón 'editar' de la solicitud. */
    public void editarSolicitud() {
        RequestContext context = null;
        context = RequestContext.getCurrentInstance();
        try {
            if (solicitudSeleccionada != null) {
                if (solicitudSeleccionada.getTipoSolicitud().getCdg().equals(TipoSolicitud.ASIGNACION)) {
                    consultarSolicitudesBean.editarSolicitud((SolicitudAsignacion) solicitudSeleccionada);
                    src = ASIGNACION_SRC;
                    header = MensajesBean.getTextoResource("cabecera.solicitud.asignacion.manual");
                } else if (solicitudSeleccionada.getTipoSolicitud().getCdg().equals(TipoSolicitud.CESION_DERECHOS)) {
                    consultarCesionesBean.editarSolicitud(solicitudSeleccionada);
                    src = CESION_SRC;
                    header = MensajesBean.getTextoResource("cabecera.solicitud.cesion");
                } else if (solicitudSeleccionada.getTipoSolicitud().getCdg().equals(TipoSolicitud.LIBERACION)) {
                    consultarLiberacionesBean.editarSolicitud(solicitudSeleccionada);
                    src = LIBERACION_SRC;
                    header = MensajesBean.getTextoResource("cabecera.solicitud.liberación");
                } else if (solicitudSeleccionada.getTipoSolicitud().getCdg().equals(TipoSolicitud.REDISTRIBUCION)) {
                    consultarRedistribucionesBean.editarSolicitud((SolicitudRedistribucionNg) solicitudSeleccionada);
                    src = REDISTRIBUCION_SRC;
                    header = MensajesBean.getTextoResource("cabecera.solicitud.redistribucion");
                } else {
                    consultarConsolidacionesBean.editarSolicitud(solicitudSeleccionada);
                    src = CONSOLIDACION_SRC;
                    header = MensajesBean.getTextoResource("cabecera.solicitud.consolidacion");
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
                if (solicitudSeleccionada.getTipoSolicitud().getCdg().equals(TipoSolicitud.ASIGNACION)) {
                    consultarSolicitudesBean.eliminarSolicitud((SolicitudAsignacion) solicitudSeleccionada, MSG_ID);
                } else if (solicitudSeleccionada.getTipoSolicitud().getCdg().equals(TipoSolicitud.CESION_DERECHOS)) {
                    consultarCesionesBean.eliminarSolicitud(solicitudSeleccionada, MSG_ID);
                } else if (solicitudSeleccionada.getTipoSolicitud().getCdg().equals(TipoSolicitud.LIBERACION)) {
                    consultarLiberacionesBean.eliminarSolicitud(solicitudSeleccionada, MSG_ID);
                } else if (solicitudSeleccionada.getTipoSolicitud().getCdg().equals(TipoSolicitud.REDISTRIBUCION)) {
                    consultarRedistribucionesBean.eliminarSolicitud(solicitudSeleccionada, MSG_ID);
                } else {
                    consultarConsolidacionesBean.eliminarSolicitud(solicitudSeleccionada, MSG_ID);
                }
            }
        } catch (Exception e) {
            MensajesBean.addErrorMsg(MSG_ID, "Error inesperado: " + e.getMessage(), "");
        }
    }

    /** Habilita la fecha Fin Solicitud al editar la fecha inicio. */
    public void habilitarFechaFinSoli() {
        if (fechaIniSolicitud != null) {
            fechaSoliHabilitada = true;
        } else {
            fechaSoliHabilitada = false;
            fechaFinSolicitud = null;
        }
    }

    /** Habilita la fecha Fin Asigignacion al editar la fecha inicio. */
    public void habilitarFechaFinAsig() {
        if (fechaIniAsignacion != null) {
            fechaAsigHabilitada = true;
        } else {
            fechaAsigHabilitada = false;
            fechaFinAsignacion = null;
        }
    }

    /**
     * Rellena la lista de municipios por Estado seleccionado.
     * @throws Exception excepcion
     */
    public void habilitarMunicipio() throws Exception {
        if (estadoMun != null) {
            listaMunicipio = ngService.findMunicipiosByEstado(estadoMun.getCodEstado());
        }

    }

    /**
     * Rellena la lista de poblaciones por estado y municipio.
     * @throws Exception excepcion
     */
    public void habilitarPoblacion() throws Exception {
        if (municipio != null) {
            listaPoblacion = ngService.findAllPoblaciones(estadoMun.getCodEstado(),
                    municipio.getId().getCodMunicipio());
        }
    }

    /**
     * Método que invoca el resetTabs de cada uno de los posibles beans asociados a los trámites consultados en la
     * consulta genérica.
     */
    public void resetTabs() {
        if (solicitudSeleccionada != null) {
            if (solicitudSeleccionada.getTipoSolicitud().getCdg().equals(TipoSolicitud.ASIGNACION)) {
                consultarSolicitudesBean.getAsignacionManualBean().resetTabs();
            } else if (solicitudSeleccionada.getTipoSolicitud().getCdg().equals(TipoSolicitud.CESION_DERECHOS)) {
                consultarCesionesBean.getSolicitudCesionNgBean().resetTabs();
            } else if (solicitudSeleccionada.getTipoSolicitud().getCdg().equals(TipoSolicitud.LIBERACION)) {
                consultarLiberacionesBean.getSolicitudLiberacionBean().resetTabs();
            } else if (solicitudSeleccionada.getTipoSolicitud().getCdg().equals(TipoSolicitud.REDISTRIBUCION)) {
                consultarRedistribucionesBean.getSolicitudRedistribucionBean().resetTabs();
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
     * Identificador de la fecha de inicio de asignacion.
     * @return the fechaIniAsignacion
     */
    public Date getFechaIniAsignacion() {
        return fechaIniAsignacion;
    }

    /**
     * Identificador de la fecha de inicio de asignacion.
     * @param fechaIniAsignacion the fechaIniAsignacion to set
     */
    public void setFechaIniAsignacion(Date fechaIniAsignacion) {
        this.fechaIniAsignacion = fechaIniAsignacion;
    }

    /**
     * Identificador de la fecha de fin de asignacion.
     * @return the fechaFinAsignacion
     */
    public Date getFechaFinAsignacion() {
        return fechaFinAsignacion;
    }

    /**
     * Identificador de la fecha de fin de asignacion.
     * @param fechaFinAsignacion the fechaFinAsignacion to set
     */
    public void setFechaFinAsignacion(Date fechaFinAsignacion) {
        this.fechaFinAsignacion = fechaFinAsignacion;
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
    public ConsultaGenericaLazyModel getSolicitudesModel() {
        return solicitudesModel;
    }

    /**
     * Modelo Lazy para carga de solicitudes.
     * @param solicitudesModel the solicitudesModel to set
     */
    public void setSolicitudesModel(ConsultaGenericaLazyModel solicitudesModel) {
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
     * @return the consultarSolicitudesBean
     */
    public ConsultarAsignacionesNgBean getConsultarSolicitudesBean() {
        return consultarSolicitudesBean;
    }

    /**
     * Referencia al Bean de Solicitud de asignación.
     * @param consultarSolicitudesBean the consultarSolicitudesBean to set
     */
    public void setConsultarSolicitudesBean(ConsultarAsignacionesNgBean consultarSolicitudesBean) {
        this.consultarSolicitudesBean = consultarSolicitudesBean;
    }

    /**
     * Referencia al Bean de Solicitud de cesión.
     * @return the consultarCesionesBean
     */
    public ConsultarCesionesNgBean getConsultarCesionesBean() {
        return consultarCesionesBean;
    }

    /**
     * Referencia al Bean de Solicitud de cesión.
     * @param consultarCesionesBean the consultarCesionesBean to set
     */
    public void setConsultarCesionesBean(ConsultarCesionesNgBean consultarCesionesBean) {
        this.consultarCesionesBean = consultarCesionesBean;
    }

    /**
     * Referencia al Bean de Solicitud de liberación.
     * @return the consultarLiberacionesBean
     */
    public ConsultarLiberacionesNgBean getConsultarLiberacionesBean() {
        return consultarLiberacionesBean;
    }

    /**
     * Referencia al Bean de Solicitud de liberación.
     * @param consultarLiberacionesBean the consultarLiberacionesBean to set
     */
    public void setConsultarLiberacionesBean(ConsultarLiberacionesNgBean consultarLiberacionesBean) {
        this.consultarLiberacionesBean = consultarLiberacionesBean;
    }

    /**
     * Referencia al Bean de Solicitud de redistribución.
     * @return the consultarRedistribucionesBean
     */
    public ConsultarRedistribucionesNgBean getConsultarRedistribucionesBean() {
        return consultarRedistribucionesBean;
    }

    /**
     * Referencia al Bean de Solicitud de redistribución.
     * @param consultarRedistribucionBean the consultarRedistribucionesBean to set
     */
    public void setConsultarRedistribucionesBean(ConsultarRedistribucionesNgBean consultarRedistribucionBean) {
        this.consultarRedistribucionesBean = consultarRedistribucionBean;
    }

    /**
     * Referencia al Bean de Solicitud de consolidación.
     * @return the consultarConsolidacionesBean
     */
    public ConsultarConsolidacionesBean getConsultarConsolidacionesBean() {
        return consultarConsolidacionesBean;
    }

    /**
     * Referencia al Bean de Solicitud de consolidación.
     * @param consultarConsolidacionesBean the consultarConsolidacionesBean to set
     */
    public void setConsultarConsolidacionesBean(ConsultarConsolidacionesBean consultarConsolidacionesBean) {
        this.consultarConsolidacionesBean = consultarConsolidacionesBean;
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
     * Identificador que habilita la fecha fin de asignación.
     * @return the fechaAsigHabilitada
     */
    public boolean isFechaAsigHabilitada() {
        return fechaAsigHabilitada;
    }

    /**
     * Identificador que habilita la fecha fin de asignación.
     * @param fechaAsigHabilitada the fechaAsigHabilitada to set
     */
    public void setFechaAsigHabilitada(boolean fechaAsigHabilitada) {
        this.fechaAsigHabilitada = fechaAsigHabilitada;
    }

    /**
     * Identificador del estado.
     * @return the estadoMun
     */
    public Estado getEstadoMun() {
        return estadoMun;
    }

    /**
     * Identificador del estado.
     * @param estadoMun the estadoMun to set
     */
    public void setEstadoMun(Estado estadoMun) {
        this.estadoMun = estadoMun;
    }

    /**
     * Identificador del municipio.
     * @return the municipio
     */
    public Municipio getMunicipio() {
        return municipio;
    }

    /**
     * Identificador del municipio.
     * @param municipio the municipio to set
     */
    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    /**
     * Identificador de la población.
     * @return the poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * Identificador de la población.
     * @param poblacion the poblacion to set
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * Identificador de la lista de estados.
     * @return the listaEstados
     */
    public List<Estado> getListaEstados() {
        return listaEstados;
    }

    /**
     * Identificador de la lista de estados.
     * @param listaEstados the listaEstados to set
     */
    public void setListaEstados(List<Estado> listaEstados) {
        this.listaEstados = listaEstados;
    }

    /**
     * Identificador de la lista de municipios.
     * @return the listaMunicipio
     */
    public List<Municipio> getListaMunicipio() {
        return listaMunicipio;
    }

    /**
     * Identificador de la lista de municipios.
     * @param listaMunicipio the listaMunicipio to set
     */
    public void setListaMunicipio(List<Municipio> listaMunicipio) {
        this.listaMunicipio = listaMunicipio;
    }

    /**
     * Identificador de la lista de poblaciones.
     * @return the listaPoblacion
     */
    public List<Poblacion> getListaPoblacion() {
        return listaPoblacion;
    }

    /**
     * Identificador de la lista de poblaciones.
     * @param listaPoblacion the listaPoblacion to set
     */
    public void setListaPoblacion(List<Poblacion> listaPoblacion) {
        this.listaPoblacion = listaPoblacion;
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
