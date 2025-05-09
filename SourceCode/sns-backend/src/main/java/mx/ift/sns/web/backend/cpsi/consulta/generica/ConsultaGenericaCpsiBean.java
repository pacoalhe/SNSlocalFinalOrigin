package mx.ift.sns.web.backend.cpsi.consulta.generica;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.modelo.cpsi.SolicitudAsignacionCpsi;
import mx.ift.sns.modelo.cpsi.SolicitudCesionCpsi;
import mx.ift.sns.modelo.cpsi.SolicitudCpsiUit;
import mx.ift.sns.modelo.cpsi.SolicitudLiberacionCpsi;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCPSI;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.cpsi.DetalleConsultaGenerica;
import mx.ift.sns.negocio.cpsi.ICodigoCPSIFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.cpsi.asignacion.ConsultarAsignacionesCpsiBean;
import mx.ift.sns.web.backend.cpsi.cesion.ConsultarCesionesCpsiBean;
import mx.ift.sns.web.backend.cpsi.liberacion.ConsultarLiberacionesCpsiBean;
import mx.ift.sns.web.backend.cpsi.solicitud.ConsultarSolicitudesCpsiUitBean;
import mx.ift.sns.web.backend.lazymodels.ConsultaGenericaCpsiLazyModel;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Gestiona el buscador genérico de solicitudes de Cpsi. */
@ManagedBean(name = "consultaGenericaCpsiBean")
@ViewScoped
public class ConsultaGenericaCpsiBean implements Serializable {
    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultaGenericaCpsiBean.class);

    /** Fuente inicial vacía. */
    private static final String BLANK_SRC = "/errores/blank.xhtml";

    /** Fuente de la solicitud de asignación manual. */
    private static final String ASIGNACION_SRC = "/cpsi/asignacion/solicitud-asignacion-cpsi.xhtml";

    /** Fuente de la solicitud de cesión. */
    private static final String CESION_SRC = "/cpsi/cesion/solicitud-cesion-cpsi.xhtml";

    /** Fuente de la solicitud de liberación. */
    private static final String LIBERACION_SRC = "/cpsi/liberacion/solicitud-liberacion-cpsi.xhtml";

    /** Fuente de la solicitud a UIT. */
    private static final String SOLICITUD_UIT_SRC = "/cpsi/solicitud/solicitud-cpsi-uit.xhtml";

    /** Atributo que almacena el fuente a mostrar en el diálogo. */
    private String src;

    /** Atributo que almacena la cabecera del dialogo. */
    private String header;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultaGenericaCpsi";

    /** Servicio de Cpsi. */
    @EJB(mappedName = "CodigoCPSIFacade")
    private ICodigoCPSIFacade codigoCpsiFacade;

    /** Filtros de búsqueda de Solicitudes. */
    private FiltroBusquedaSolicitudesCPSI filtros;

    /** Modelo Lazy para carga de solicitudes Cpsi. */
    private ConsultaGenericaCpsiLazyModel solicitudesModel;

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

    /** Lista para los estados solicitudes. **/
    private List<TipoSolicitud> listaTipoSolicidud;

    /** Tipo de solicitud. **/
    private TipoSolicitud tipoSolicitud;

    /** Solicitud a editar. */
    private DetalleConsultaGenerica solicitudSeleccionada;

    /** Referencia al Bean de Consulta Solicitud de Asignacion. */
    @ManagedProperty("#{consultarAsignacionesCpsiBean}")
    private ConsultarAsignacionesCpsiBean consultarAsignacionesCpsiBean;

    /** Referencia al Bean de Consulta de Solicitud de Cesion. */
    @ManagedProperty("#{consultarCesionesCpsiBean}")
    private ConsultarCesionesCpsiBean consultarCesionesCpsiBean;

    /** Referencia al Bean de Consulta de Solicitud de Liberacion. */
    @ManagedProperty("#{consultarLiberacionesCpsiBean}")
    private ConsultarLiberacionesCpsiBean consultarLiberacionesCpsiBean;

    /** Referencia al Bean de Consulta de Solicitud de UIT. */
    @ManagedProperty("#{consultarSolicitudesCpsiUitBean}")
    private ConsultarSolicitudesCpsiUitBean consultarSolicitudesCpsiUitBean;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Constructor. */
    public ConsultaGenericaCpsiBean() {
    }

    /** Método de inicialización del bean. */
    @PostConstruct
    public void init() {
        try {
            // Listado de proveedores de servicio
            listaProveedores = codigoCpsiFacade.findAllProveedores();
            pstOrigen = new Proveedor();

            // Listado de estado de solicitudes
            listaEstatus = codigoCpsiFacade.findAllEstadosSolicitud();
            estatusSeleccionado = new EstadoSolicitud();
            estatusSeleccionado.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);

            // Listado de tipos de solicitud
            listaTipoSolicidud = codigoCpsiFacade.findAllTiposSolicitud();
            tipoSolicitud = new TipoSolicitud();

            // Carga Lazy y Filtros de búsqueda.
            filtros = new FiltroBusquedaSolicitudesCPSI();
            filtros.setEstado(estatusSeleccionado);
            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(codigoCpsiFacade,
                    Parametro.REGISTROS_POR_PAGINA_BACK));
            registroPorPagina = filtros.getResultadosPagina();

            solicitudesModel = new ConsultaGenericaCpsiLazyModel();
            solicitudesModel.setFiltros(filtros);
            solicitudesModel.setService(codigoCpsiFacade);
            solicitudesModel.setMessagesId(MSG_ID);

            // Reseteamos el estatus para que no esté preseleccionado.
            estatusSeleccionado = null;

            // Plantilla de oficio PST Solicitante
            prefijoOficio = codigoCpsiFacade.getParamByName(Parametro.PREFIJO);
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
                filtros = new FiltroBusquedaSolicitudesCPSI();
                solicitudesModel = new ConsultaGenericaCpsiLazyModel();
                solicitudesModel.setFiltros(filtros);
                solicitudesModel.setService(codigoCpsiFacade);
                solicitudesModel.setMessagesId(MSG_ID);
            }

            // Resetamos los filtros
            solicitudesModel.clear();
            crearFiltros();
            PaginatorUtil.resetPaginacion("FRM_ConsultaGenericaCpsi:TBL_ConsultaSolicitudes",
                    filtros.getResultadosPagina());

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método encargado de crear los filtros para la búsqueda de solicitudes. */
    private void crearFiltros() {
        filtros.clear();

        filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(codigoCpsiFacade,
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
                if (solicitudSeleccionada.getIdTipoSolicitud().equals(TipoSolicitud.ASIGNACION_CPSI)) {
                    SolicitudAsignacionCpsi sol = codigoCpsiFacade.getSolicitudAsignacionById(solicitudSeleccionada
                            .getConsecutivo());
                    consultarAsignacionesCpsiBean.editarSolicitud(sol);
                    src = ASIGNACION_SRC;
                    header = MensajesBean.getTextoResource("mainMenu.CPSI") + "/"
                            + MensajesBean.getTextoResource("mainMenu.Asignacion");
                } else if (solicitudSeleccionada.getIdTipoSolicitud().equals(TipoSolicitud.CESION_CPSI)) {
                    SolicitudCesionCpsi sol = codigoCpsiFacade.getSolicitudCesionById(solicitudSeleccionada
                            .getConsecutivo());
                    consultarCesionesCpsiBean.editarSolicitud(sol);
                    src = CESION_SRC;
                    header = MensajesBean.getTextoResource("mainMenu.CPSI") + "/"
                            + MensajesBean.getTextoResource("mainMenu.Cesion");
                } else if (solicitudSeleccionada.getIdTipoSolicitud().equals(TipoSolicitud.LIBERACION_CPSI)) {
                    SolicitudLiberacionCpsi sol = codigoCpsiFacade.getSolicitudLiberacionById(solicitudSeleccionada
                            .getConsecutivo());
                    consultarLiberacionesCpsiBean.editarSolicitud(sol);
                    src = LIBERACION_SRC;
                    header = MensajesBean.getTextoResource("mainMenu.CPSI") + "/"
                            + MensajesBean.getTextoResource("mainMenu.Liberacion");
                } else if (solicitudSeleccionada.getIdTipoSolicitud().equals(TipoSolicitud.SOLICITUD_CPSI_UIT)) {
                    SolicitudCpsiUit sol = codigoCpsiFacade.getSolicitudCpsiUitById(solicitudSeleccionada
                            .getConsecutivo());
                    consultarSolicitudesCpsiUitBean.editarSolicitud(sol);
                    src = SOLICITUD_UIT_SRC;
                    header = MensajesBean.getTextoResource("mainMenu.CPSI") + "/"
                            + MensajesBean.getTextoResource("mainMenu.Solicitud");
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
                if (solicitudSeleccionada.getIdTipoSolicitud().equals(TipoSolicitud.ASIGNACION_CPSI)) {
                    SolicitudAsignacionCpsi sol = codigoCpsiFacade.getSolicitudAsignacionById(solicitudSeleccionada
                            .getConsecutivo());
                    consultarAsignacionesCpsiBean.eliminarSolicitud(sol, MSG_ID);
                } else if (solicitudSeleccionada.getIdTipoSolicitud().equals(TipoSolicitud.CESION_CPSI)) {
                    SolicitudCesionCpsi sol = codigoCpsiFacade.getSolicitudCesionById(solicitudSeleccionada
                            .getConsecutivo());
                    consultarCesionesCpsiBean.eliminarSolicitud(sol, MSG_ID);
                } else if (solicitudSeleccionada.getIdTipoSolicitud().equals(TipoSolicitud.LIBERACION_CPSI)) {
                    SolicitudLiberacionCpsi sol = codigoCpsiFacade.getSolicitudLiberacionById(solicitudSeleccionada
                            .getConsecutivo());
                    consultarLiberacionesCpsiBean.eliminarSolicitud(sol, MSG_ID);
                } else if (solicitudSeleccionada.getIdTipoSolicitud().equals(TipoSolicitud.SOLICITUD_CPSI_UIT)) {
                    SolicitudCpsiUit sol = codigoCpsiFacade.getSolicitudCpsiUitById(solicitudSeleccionada
                            .getConsecutivo());
                    consultarSolicitudesCpsiUitBean.eliminarSolicitud(sol, MSG_ID);
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
            if (solicitudSeleccionada.getIdTipoSolicitud().equals(TipoSolicitud.ASIGNACION_CPSI)) {
                consultarAsignacionesCpsiBean.getSolicitudAsignacionBean().resetTabs();
            } else if (solicitudSeleccionada.getIdTipoSolicitud().equals(TipoSolicitud.CESION_CPSI)) {
                consultarCesionesCpsiBean.getSolicitudCesionBean().resetTabs();
            } else if (solicitudSeleccionada.getIdTipoSolicitud().equals(TipoSolicitud.LIBERACION_CPSI)) {
                consultarLiberacionesCpsiBean.getSolicitudLiberacionBean().resetTabs();
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
     * Modelo Lazy para carga de solicitudes Cpsi.
     * @return ConsultaGenericaCpsiLazyModel
     */
    public ConsultaGenericaCpsiLazyModel getSolicitudesModel() {
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
     * @return ConsultarAsignacionesCpsiBean
     */
    public ConsultarAsignacionesCpsiBean getConsultarAsignacionesCpsiBean() {
        return consultarAsignacionesCpsiBean;
    }

    /**
     * Referencia al Bean de Consulta Solicitud de Asignacion.
     * @param consultarAsignacionesCpsiBean ConsultarAsignacionesCpsiBean
     */
    public void setConsultarAsignacionesCpsiBean(ConsultarAsignacionesCpsiBean consultarAsignacionesCpsiBean) {
        this.consultarAsignacionesCpsiBean = consultarAsignacionesCpsiBean;
    }

    /**
     * Referencia al Bean de Consulta de Solicitud de Cesion.
     * @return ConsultarCesionesCpsiBean
     */
    public ConsultarCesionesCpsiBean getConsultarCesionesCpsiBean() {
        return consultarCesionesCpsiBean;
    }

    /**
     * Referencia al Bean de Consulta de Solicitud de Cesion.
     * @param consultarCesionesCpsiBean ConsultarCesionesCpsiBean
     */
    public void setConsultarCesionesCpsiBean(ConsultarCesionesCpsiBean consultarCesionesCpsiBean) {
        this.consultarCesionesCpsiBean = consultarCesionesCpsiBean;
    }

    /**
     * Referencia al Bean de Consulta de Solicitud de Liberacion.
     * @return ConsultarLiberacionesCpsiBean
     */
    public ConsultarLiberacionesCpsiBean getConsultarLiberacionesCpsiBean() {
        return consultarLiberacionesCpsiBean;
    }

    /**
     * Referencia al Bean de Consulta de Solicitud de Liberacion.
     * @param consultarLiberacionesCpsiBean ConsultarLiberacionesCpsiBean
     */
    public void setConsultarLiberacionesCpsiBean(ConsultarLiberacionesCpsiBean consultarLiberacionesCpsiBean) {
        this.consultarLiberacionesCpsiBean = consultarLiberacionesCpsiBean;
    }

    /**
     * Referencia al Bean de Consulta de Solicitud de UIT.
     * @return ConsultarSolicitudesCpsiUitBean
     */
    public ConsultarSolicitudesCpsiUitBean getConsultarSolicitudesCpsiUitBean() {
        return consultarSolicitudesCpsiUitBean;
    }

    /**
     * Referencia al Bean de Consulta de Solicitud de UIT.
     * @param consultarSolicitudesCpsiUitBean ConsultarSolicitudesCpsiUitBean
     */
    public void setConsultarSolicitudesCpsiUitBean(ConsultarSolicitudesCpsiUitBean consultarSolicitudesCpsiUitBean) {
        this.consultarSolicitudesCpsiUitBean = consultarSolicitudesCpsiUitBean;
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
