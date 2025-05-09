package mx.ift.sns.web.backend.cpsn.consulta.generica;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.modelo.cpsn.SolicitudAsignacionCpsn;
import mx.ift.sns.modelo.cpsn.SolicitudCesionCPSN;
import mx.ift.sns.modelo.cpsn.SolicitudLiberacionCpsn;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCpsn;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.cpsn.DetalleConsultaGenerica;
import mx.ift.sns.negocio.cpsn.ICodigoCPSNFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.cpsn.asignacion.ConsultarAsignacionesCpsnBean;
import mx.ift.sns.web.backend.cpsn.cesion.ConsultarCesionesCPSNBean;
import mx.ift.sns.web.backend.cpsn.liberacion.ConsultarLiberacionesCpsnBean;
import mx.ift.sns.web.backend.lazymodels.ConsultaGenericaCpsnLazyModel;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Gestiona el buscador genérico de solicitudes de CPSN. */
@ManagedBean(name = "consultaGenericaCpsnBean")
@ViewScoped
public class ConsultaGenericaCpsnBean implements Serializable {
    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultaGenericaCpsnBean.class);

    /** Fuente inicial vacía. */
    private static final String BLANK_SRC = "/errores/blank.xhtml";

    /** Fuente de la solicitud de asignación manual. */
    private static final String ASIGNACION_SRC = "/cpsn/asignacion/solicitud-asignacion-cpsn.xhtml";

    /** Fuente de la solicitud de cesión. */
    private static final String CESION_SRC = "/cpsn/cesion/solicitud-cesion-cpsn.xhtml";

    /** Fuente de la solicitud de liberación. */
    private static final String LIBERACION_SRC = "/cpsn/liberacion/solicitud-liberacion-cpsn.xhtml";

    /** Atributo que almacena el fuente a mostrar en el diálogo. */
    private String src;

    /** Atributo que almacena la cabecera del dialogo. */
    private String header;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultaGenericaCpsn";

    /** Servicio de Cpsn. */
    @EJB(mappedName = "CodigoCPSNFacade")
    private ICodigoCPSNFacade codigoCPSNFacade;

    /** Filtros de búsqueda de Solicitudes. */
    private FiltroBusquedaSolicitudesCpsn filtros;

    /** Modelo Lazy para carga de solicitudes cpsn. */
    private ConsultaGenericaCpsnLazyModel solicitudesModel;

    /** Identificador de solicitud. */
    private String consecutivo;

    /** Proveedor seleccionado para la búsqueda. */
    private Proveedor pstOrigen;

    /** Catálogo de proveedores. */
    private List<Proveedor> listaProveedores;

    /** Proveedor cesionario seleccionado. */
    private Proveedor pstDestino;

    /** Búsqueda por oficio del proveedor solicitante. */
    private String oficioPstSolicitante = "";

    /** Prefijo para la búsqueda de oficios. */
    private String prefijoOficio;

    /** Rango inicial de búsqueda para fecha de Asignación de Solicitud. */
    private Date fechaDesdeAsignacion;

    /** Rango final de búsqueda para fecha de Asignación de Solicitud. */
    private Date fechaHastaAsignacion;

    /** Indica si el campo de búsqueda 'fecha hasta' está habilitado. */
    private boolean fechaHastaAsignacionHabilitada = false;

    /** Rango inicial de búsqueda para fecha de Implementación de Solicitud. */
    private Date fechaDesdeImpl;

    /** Rango final de búsqueda para fecha de Implementación de Solicitud. */
    private Date fechaHastaImpl;

    /** Indica si el campo de búsqueda 'fecha hasta' está habilitado. */
    private boolean fechaHastaImplHabilitada = false;

    /** Estatus de solicitud seleccionado para la búsqueda. */
    private EstadoSolicitud estatusSeleccionado;

    /** Catálogo de estatus de solicitud. */
    private List<EstadoSolicitud> listaEstatus;

    /** Referencia de la solicitud. */
    private String referenciaSolicitud;

    /** Lista para los estados solicitudes. */
    private List<TipoSolicitud> listaTipoSolicidud;

    /** Tipo de solicitud. */
    private TipoSolicitud tipoSolicitud;

    /** Solicitud a editar. */
    private DetalleConsultaGenerica solicitudSeleccionada;

    /** Referencia al Bean de Consulta Solicitud de Asignacion. */
    @ManagedProperty("#{consultarAsignacionesCpsnBean}")
    private ConsultarAsignacionesCpsnBean consultarAsignacionesCpsnBean;

    /** Referencia al Bean de Consulta de Solicitud de Cesion. */
    @ManagedProperty("#{consultarCesionesCPSNBean}")
    private ConsultarCesionesCPSNBean consultarCesionesCPSNBean;

    /** Referencia al Bean de Consulta de Solicitud de Liberacion. */
    @ManagedProperty("#{consultarLiberacionesCpsnBean}")
    private ConsultarLiberacionesCpsnBean consultarLiberacionesCpsnBean;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Método de inicialización del bean. */
    @PostConstruct
    public void init() {
        try {
            // Listado de proveedores de servicio
            listaProveedores = codigoCPSNFacade.findAllProveedores();
            pstOrigen = new Proveedor();

            // Listado de estado de solicitudes
            listaEstatus = codigoCPSNFacade.findAllEstadosSolicitud();
            estatusSeleccionado = new EstadoSolicitud();
            estatusSeleccionado.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);

            // Listado de tipos de solicitud
            listaTipoSolicidud = codigoCPSNFacade.findAllTiposSolicitud();
            tipoSolicitud = new TipoSolicitud();

            // Carga Lazy y Filtros de búsqueda.
            filtros = new FiltroBusquedaSolicitudesCpsn();
            filtros.setEstado(estatusSeleccionado);
            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(codigoCPSNFacade,
                    Parametro.REGISTROS_POR_PAGINA_BACK));
            registroPorPagina = filtros.getResultadosPagina();

            solicitudesModel = new ConsultaGenericaCpsnLazyModel();
            solicitudesModel.setFiltros(filtros);
            solicitudesModel.setService(codigoCPSNFacade);
            solicitudesModel.setMessagesId(MSG_ID);

            // Reseteamos el estatus para que no esté preseleccionado.
            estatusSeleccionado = null;

            // Plantilla de oficio PST Solicitante
            prefijoOficio = codigoCPSNFacade.getParamByName(Parametro.PREFIJO);
            oficioPstSolicitante = new String(prefijoOficio);

            src = BLANK_SRC;
            header = "";

        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
        }
    }

    /** Método invocado al pulsar el botón 'buscar' sobre el buscador de solicitudes. */
    public void realizarBusqueda() {
        try {
            if (solicitudesModel == null) {
                filtros = new FiltroBusquedaSolicitudesCpsn();
                solicitudesModel = new ConsultaGenericaCpsnLazyModel();
                solicitudesModel.setFiltros(filtros);
                solicitudesModel.setService(codigoCPSNFacade);
                solicitudesModel.setMessagesId(MSG_ID);
            }

            // Resetamos los filtros
            solicitudesModel.clear();

            crearFiltros();
            PaginatorUtil.resetPaginacion("FRM_ConsultaGenericaCPSN:TBL_ConsultaSolicitudes",
                    filtros.getResultadosPagina());

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método encargado de crear los filtros para la búsqueda de solicitudes. */
    private void crearFiltros() {
        filtros.clear();

        filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(codigoCPSNFacade,
                Parametro.REGISTROS_POR_PAGINA_BACK));

        // Filtro de valor de consecutivo
        if (!StringUtils.isEmpty(consecutivo)) {
            if (StringUtils.isNumeric(consecutivo) && (StringUtils.length(consecutivo) <= 9)) {
                filtros.setConsecutivo(consecutivo);
            } else {
                MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0007), "");
            }
        }

        // Filtro de Número de Oficio
        if (!StringUtils.isEmpty(referenciaSolicitud)) {
            if (StringUtils.length(referenciaSolicitud) <= 25) {
                filtros.setReferenciaSolicitud(referenciaSolicitud);
            } else {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("consultas.generales.error.numeroOficio"), "");
            }
        }

        // El resto de parámetros si no se han seleccionado son nulos
        filtros.setNumIniciofchSolicitud(fechaDesdeAsignacion);
        filtros.setNumFinalfchSolicitud(fechaHastaAsignacion);
        filtros.setFechaImplementacionDesde(fechaDesdeImpl);
        filtros.setFechaImplementacionHasta(fechaHastaImpl);

        filtros.setProveedor(pstOrigen);
        filtros.setEstado(estatusSeleccionado);
        filtros.setPstCesionario(pstDestino);

        if (tipoSolicitud != null) {
            filtros.setTipoSolicitud(tipoSolicitud);
        }

        // Oficio del PST Solicitante
        if (!StringUtils.isEmpty(oficioPstSolicitante) && !oficioPstSolicitante.equals(prefijoOficio)) {
            filtros.setOficioPstSolicitante(oficioPstSolicitante);
        }

        if ((fechaDesdeAsignacion != null && fechaHastaAsignacion != null)
                && (fechaHastaAsignacion.before(fechaDesdeAsignacion))) {
            MensajesBean.addErrorMsg(MSG_ID,
                    "La fecha final debe ser mayor o igual a la fecha de inicio", "");
        }

        if ((fechaDesdeImpl != null && fechaHastaImpl != null)
                && (fechaHastaImpl.before(fechaDesdeImpl))) {
            MensajesBean.addErrorMsg(MSG_ID,
                    "La fecha final debe ser mayor o igual a la fecha de inicio", "");
        }

    }

    /** Método invocado al pulsar el botón 'limpiar' sobre el buscador de solicitudes. */
    public void limpiarBusqueda() {
        this.consecutivo = null;
        this.pstOrigen = null;
        this.pstDestino = null;
        this.oficioPstSolicitante = new String(prefijoOficio);

        this.fechaDesdeAsignacion = null;
        this.fechaHastaAsignacion = null;
        this.fechaHastaAsignacionHabilitada = false;
        this.fechaDesdeImpl = null;
        this.fechaHastaImpl = null;
        this.fechaHastaImplHabilitada = false;
        this.tipoSolicitud = null;

        this.estatusSeleccionado = null;
        this.solicitudesModel = null;
    }

    /** Método invocado al pulsar el botón 'editar'. */
    public void editarSolicitud() {
        RequestContext context = null;
        context = RequestContext.getCurrentInstance();
        try {
            if (solicitudSeleccionada != null) {
                if (solicitudSeleccionada.getIdTipoSolicitud().equals(TipoSolicitud.ASIGNACION_CPSN)) {
                    SolicitudAsignacionCpsn sol = codigoCPSNFacade.getSolicitudAsignacionById(solicitudSeleccionada
                            .getConsecutivo());
                    consultarAsignacionesCpsnBean.editarSolicitud(sol);
                    src = ASIGNACION_SRC;
                    header = MensajesBean.getTextoResource("mainMenu.CPSN") + "/"
                            + MensajesBean.getTextoResource("mainMenu.Asignacion");
                } else if (solicitudSeleccionada.getIdTipoSolicitud().equals(TipoSolicitud.CESION_CPSN)) {
                    SolicitudCesionCPSN sol = codigoCPSNFacade.getSolicitudCesionById(solicitudSeleccionada
                            .getConsecutivo());
                    consultarCesionesCPSNBean.editarSolicitud(sol);
                    src = CESION_SRC;
                    header = MensajesBean.getTextoResource("mainMenu.CPSN") + "/"
                            + MensajesBean.getTextoResource("mainMenu.Cesion");
                } else if (solicitudSeleccionada.getIdTipoSolicitud().equals(TipoSolicitud.LIBERACION_CPSN)) {
                    SolicitudLiberacionCpsn sol = codigoCPSNFacade.getSolicitudLiberacionById(solicitudSeleccionada
                            .getConsecutivo());
                    consultarLiberacionesCpsnBean.editarSolicitud(sol);
                    src = LIBERACION_SRC;
                    header = MensajesBean.getTextoResource("mainMenu.CPSN") + "/"
                            + MensajesBean.getTextoResource("mainMenu.Liberacion");
                }
            }
            context.addCallbackParam("tipoSolicitud", solicitudSeleccionada.getIdTipoSolicitud());
        } catch (Exception e) {
            MensajesBean.addErrorMsg(MSG_ID, "Error inesperado: " + e.getMessage(), "");
        }
    }

    /** Método invocado al pulsar el botón 'Si' al eliminar una solicitud. */
    public void eliminarSolicitud() {
        try {
            if (solicitudSeleccionada != null) {
                if (solicitudSeleccionada.getIdTipoSolicitud().equals(TipoSolicitud.ASIGNACION_CPSN)) {
                    SolicitudAsignacionCpsn sol = codigoCPSNFacade.getSolicitudAsignacionById(solicitudSeleccionada
                            .getConsecutivo());
                    consultarAsignacionesCpsnBean.eliminarSolicitud(sol, MSG_ID);
                } else if (solicitudSeleccionada.getIdTipoSolicitud().equals(TipoSolicitud.CESION_CPSN)) {
                    SolicitudCesionCPSN sol = codigoCPSNFacade.getSolicitudCesionById(solicitudSeleccionada
                            .getConsecutivo());
                    consultarCesionesCPSNBean.eliminarSolicitud(sol, MSG_ID);
                } else if (solicitudSeleccionada.getIdTipoSolicitud().equals(TipoSolicitud.LIBERACION_CPSN)) {
                    SolicitudLiberacionCpsn sol = codigoCPSNFacade.getSolicitudLiberacionById(solicitudSeleccionada
                            .getConsecutivo());
                    consultarLiberacionesCpsnBean.eliminarSolicitud(sol, MSG_ID);
                }
            }
        } catch (Exception e) {
            MensajesBean.addErrorMsg(MSG_ID, "Error inesperado: " + e.getMessage(), "");
        }
    }

    /**
     * Método que invoca el resetTabs de cada uno de los posibles beans asociados a los trámites consultados en la
     * consulta genérica.
     */
    public void resetTabs() {
        if (solicitudSeleccionada != null) {
            if (solicitudSeleccionada.getIdTipoSolicitud().equals(TipoSolicitud.ASIGNACION_CPSN)) {
                consultarAsignacionesCpsnBean.getSolicitudAsignacionCpsnBean().resetTabs();
            } else if (solicitudSeleccionada.getIdTipoSolicitud().equals(TipoSolicitud.CESION_CPSN)) {
                consultarCesionesCPSNBean.getSolicitudCesionCPSNBean().resetTabs();
            } else if (solicitudSeleccionada.getIdTipoSolicitud().equals(TipoSolicitud.LIBERACION_CPSN)) {
                consultarLiberacionesCpsnBean.getSolicitudLiberacionBean().resetTabs();
            }
        }
    }

    /**
     * reset.
     */
    public void reset() {
        this.realizarBusqueda();
    }

    /** Habilita el campo de fecha Asignación hasta en función del campo fecha Asignación inicio. */
    public void habilitarFechaHastaAsignacion() {
        fechaHastaAsignacionHabilitada = (fechaDesdeAsignacion != null);
    }

    /** Habilita el campo de fecha Implementación hasta en función del campo fecha Implementación inicio. */
    public void habilitarFechaHastaImplementacion() {
        fechaHastaImplHabilitada = (fechaDesdeImpl != null);
    }

    // GETTERS & SETTERS

    /**
     * Modelo Lazy para carga de solicitudes cpsn.
     * @return ConsultaGenericaCpsnLazyModel
     */
    public ConsultaGenericaCpsnLazyModel getSolicitudesModel() {
        return solicitudesModel;
    }

    /**
     * Identificador de solicitud.
     * @return String
     */
    public String getConsecutivo() {
        return consecutivo;
    }

    /**
     * Identificador de solicitud.
     * @param consecutivo String
     */
    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    /**
     * @return the pstCedenteSeleccionado
     */
    public Proveedor getPstCedenteSeleccionado() {
        return pstOrigen;
    }

    /**
     * @param pstCedenteSeleccionado the pstCedenteSeleccionado to set
     */
    public void setPstCedenteSeleccionado(Proveedor pstCedenteSeleccionado) {
        this.pstOrigen = pstCedenteSeleccionado;
    }

    /**
     * @return the listaProveedores
     */
    public List<Proveedor> getListaProveedores() {
        return listaProveedores;
    }

    /**
     * @return the pstCesionarioSeleccionado
     */
    public Proveedor getPstCesionarioSeleccionado() {
        return pstDestino;
    }

    /**
     * @param pstCesionarioSeleccionado the pstCesionarioSeleccionado to set
     */
    public void setPstCesionarioSeleccionado(Proveedor pstCesionarioSeleccionado) {
        this.pstDestino = pstCesionarioSeleccionado;
    }

    /**
     * @return the oficioPstSolicitante
     */
    public String getOficioPstSolicitante() {
        return oficioPstSolicitante;
    }

    /**
     * @param oficioPstSolicitante the oficioPstSolicitante to set
     */
    public void setOficioPstSolicitante(String oficioPstSolicitante) {
        this.oficioPstSolicitante = oficioPstSolicitante;
    }

    /**
     * @return the prefijoOficio
     */
    public String getPrefijoOficio() {
        return prefijoOficio;
    }

    /**
     * @param prefijoOficio the prefijoOficio to set
     */
    public void setPrefijoOficio(String prefijoOficio) {
        this.prefijoOficio = prefijoOficio;
    }

    /**
     * @return the fechaDesdeAsignacion
     */
    public Date getFechaDesdeAsignacion() {
        return fechaDesdeAsignacion;
    }

    /**
     * @param fechaDesdeAsignacion the fechaDesdeAsignacion to set
     */
    public void setFechaDesdeAsignacion(Date fechaDesdeAsignacion) {
        this.fechaDesdeAsignacion = fechaDesdeAsignacion;
    }

    /**
     * @return the fechaHastaAsignacion
     */
    public Date getFechaHastaAsignacion() {
        return fechaHastaAsignacion;
    }

    /**
     * @param fechaHastaAsignacion the fechaHastaAsignacion to set
     */
    public void setFechaHastaAsignacion(Date fechaHastaAsignacion) {
        this.fechaHastaAsignacion = fechaHastaAsignacion;
    }

    /**
     * @return the fechaHastaAsignacionHabilitada
     */
    public boolean isFechaHastaAsignacionHabilitada() {
        return fechaHastaAsignacionHabilitada;
    }

    /**
     * @param fechaHastaAsignacionHabilitada the fechaHastaAsignacionHabilitada to set
     */
    public void setFechaHastaAsignacionHabilitada(boolean fechaHastaAsignacionHabilitada) {
        this.fechaHastaAsignacionHabilitada = fechaHastaAsignacionHabilitada;
    }

    /**
     * @return the fechaDesdeImpl
     */
    public Date getFechaDesdeImpl() {
        return fechaDesdeImpl;
    }

    /**
     * @param fechaDesdeImpl the fechaDesdeImpl to set
     */
    public void setFechaDesdeImpl(Date fechaDesdeImpl) {
        this.fechaDesdeImpl = fechaDesdeImpl;
    }

    /**
     * @return the fechaHastaImpl
     */
    public Date getFechaHastaImpl() {
        return fechaHastaImpl;
    }

    /**
     * @param fechaHastaImpl the fechaHastaImpl to set
     */
    public void setFechaHastaImpl(Date fechaHastaImpl) {
        this.fechaHastaImpl = fechaHastaImpl;
    }

    /**
     * @return the fechaHastaImplHabilitada
     */
    public boolean isFechaHastaImplHabilitada() {
        return fechaHastaImplHabilitada;
    }

    /**
     * @param fechaHastaImplHabilitada the fechaHastaImplHabilitada to set
     */
    public void setFechaHastaImplHabilitada(boolean fechaHastaImplHabilitada) {
        this.fechaHastaImplHabilitada = fechaHastaImplHabilitada;
    }

    /**
     * @return the estatusSeleccionado
     */
    public EstadoSolicitud getEstatusSeleccionado() {
        return estatusSeleccionado;
    }

    /**
     * @param estatusSeleccionado the estatusSeleccionado to set
     */
    public void setEstatusSeleccionado(EstadoSolicitud estatusSeleccionado) {
        this.estatusSeleccionado = estatusSeleccionado;
    }

    /**
     * @return the listaEstatus
     */
    public List<EstadoSolicitud> getListaEstatus() {
        return listaEstatus;
    }

    /**
     * @return the referenciaSolicitud
     */
    public String getReferenciaSolicitud() {
        return referenciaSolicitud;
    }

    /**
     * @param referenciaSolicitud the referenciaSolicitud to set
     */
    public void setReferenciaSolicitud(String referenciaSolicitud) {
        this.referenciaSolicitud = referenciaSolicitud;
    }

    /**
     * Proveedor seleccionado para la búsqueda.
     * @return Proveedor
     */
    public Proveedor getPstOrigen() {
        return pstOrigen;
    }

    /**
     * Proveedor seleccionado para la búsqueda.
     * @param pstOrigen Proveedor
     */
    public void setPstOrigen(Proveedor pstOrigen) {
        this.pstOrigen = pstOrigen;
    }

    /**
     * Proveedor cesionario seleccionado.
     * @return Proveedor
     */
    public Proveedor getPstDestino() {
        return pstDestino;
    }

    /**
     * Proveedor cesionario seleccionado.
     * @param pstDestino Proveedor
     */
    public void setPstDestino(Proveedor pstDestino) {
        this.pstDestino = pstDestino;
    }

    /**
     * Lista para los estados solicitudes.
     * @return List<TipoSolicitud>
     */
    public List<TipoSolicitud> getListaTipoSolicidud() {
        return listaTipoSolicidud;
    }

    /**
     * Tipo de solicitud.
     * @return TipoSolicitud
     */
    public TipoSolicitud getTipoSolicitud() {
        return tipoSolicitud;
    }

    /**
     * Tipo de solicitud.
     * @param tipoSolicitud TipoSolicitud
     */
    public void setTipoSolicitud(TipoSolicitud tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    /**
     * Atributo que almacena el fuente a mostrar en el diálogo.
     * @return String
     */
    public String getSrc() {
        return src;
    }

    /**
     * Atributo que almacena el fuente a mostrar en el diálogo.
     * @param src String
     */
    public void setSrc(String src) {
        this.src = src;
    }

    /**
     * Atributo que almacena la cabecera del dialogo.
     * @return String
     */
    public String getHeader() {
        return header;
    }

    /**
     * Atributo que almacena la cabecera del dialogo.
     * @param header String
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * Solicitud a editar.
     * @return DetalleConsultaGenerica
     */
    public DetalleConsultaGenerica getSolicitudSeleccionada() {
        return solicitudSeleccionada;
    }

    /**
     * Solicitud a editar.
     * @param solicitud DetalleConsultaGenerica
     */
    public void setSolicitudSeleccionada(DetalleConsultaGenerica solicitud) {
        this.solicitudSeleccionada = solicitud;
    }

    /**
     * Referencia al Bean de Consulta Solicitud de Asignacion.
     * @return ConsultarAsignacionesCpsnBean
     */
    public ConsultarAsignacionesCpsnBean getConsultarAsignacionesCpsnBean() {
        return consultarAsignacionesCpsnBean;
    }

    /**
     * Referencia al Bean de Consulta Solicitud de Asignacion.
     * @param consultarAsignacionesCpsnBean ConsultarAsignacionesCpsnBean
     */
    public void setConsultarAsignacionesCpsnBean(ConsultarAsignacionesCpsnBean consultarAsignacionesCpsnBean) {
        this.consultarAsignacionesCpsnBean = consultarAsignacionesCpsnBean;
    }

    /**
     * Referencia al Bean de Consulta de Solicitud de Cesion.
     * @return ConsultarCesionesCPSNBean
     */
    public ConsultarCesionesCPSNBean getConsultarCesionesCPSNBean() {
        return consultarCesionesCPSNBean;
    }

    /**
     * Referencia al Bean de Consulta de Solicitud de Cesion.
     * @param consultarCesionesCPSNBean ConsultarCesionesCPSNBean
     */
    public void setConsultarCesionesCPSNBean(ConsultarCesionesCPSNBean consultarCesionesCPSNBean) {
        this.consultarCesionesCPSNBean = consultarCesionesCPSNBean;
    }

    /**
     * Referencia al Bean de Consulta de Solicitud de Liberacion.
     * @return ConsultarLiberacionesCpsnBean
     */
    public ConsultarLiberacionesCpsnBean getConsultarLiberacionesCpsnBean() {
        return consultarLiberacionesCpsnBean;
    }

    /**
     * Referencia al Bean de Consulta de Solicitud de Liberacion.
     * @param consultarLiberacionesCpsnBean ConsultarLiberacionesCpsnBean
     */
    public void setConsultarLiberacionesCpsnBean(ConsultarLiberacionesCpsnBean consultarLiberacionesCpsnBean) {
        this.consultarLiberacionesCpsnBean = consultarLiberacionesCpsnBean;
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
