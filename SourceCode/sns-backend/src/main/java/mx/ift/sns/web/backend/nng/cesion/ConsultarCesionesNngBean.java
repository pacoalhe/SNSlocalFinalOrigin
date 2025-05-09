package mx.ift.sns.web.backend.nng.cesion;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.nng.SolicitudCesionNng;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.lazymodels.SolicitudCesionNngConsultaLazyModel;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestiona el buscador de solicitudes de Cesión de Numeración No Geográfica.
 */
@ManagedBean(name = "consultarCesionesNngBean")
@ViewScoped
public class ConsultarCesionesNngBean implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarCesionesNngBean.class);

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultarCesionesNNG";

    /** Identificador de solicitud. */
    private String consecutivo;

    /** Proveedor seleccionado para la búsqueda. */
    private Proveedor proveedorSeleccionado;

    /** Catálogo de proveedores. */
    private List<Proveedor> listaProveedores;

    /** Estado de solicitud seleccionado para la búsqueda. */
    private EstadoSolicitud estadoSeleccionado;

    /** Catálogo de estados de solicitud. */
    private List<EstadoSolicitud> listaEstados;

    /** Solicitud seleccionada en la tabla de búsqueda de solicitudes. */
    private DetalleConsultaCesionNng registroSeleccionado;

    /** Servicio de Numeración No Geográfica. */
    @EJB(mappedName = "NumeracionNoGeograficaFacade")
    private INumeracionNoGeograficaFacade nngFacade;

    /** Referencia al Bean de Solicitud de Cesión de Numeración No Geográfica. */
    @ManagedProperty("#{solicitudCesionNngBean}")
    private SolicitudCesionNngBean solicitudCesionNngBean;

    /** Modelo Lazy para carga de solicitudes de cesion de numeración no geográfica. */
    private SolicitudCesionNngConsultaLazyModel solicitudesCesionNngModel;

    /** Filtros de búsqueda de Solicitudes. */
    private FiltroBusquedaSolicitudes filtros;

    /** Proveedor cesionario seleccionado. */
    private Proveedor pstCesionario;

    /** Identificador de numeración de serie. */
    private String sna;

    /** Identificador clave de servicio. */
    private BigDecimal claveServicio;

    /** Listado de claves de servicio. */
    private List<ClaveServicio> listaClavesServicio;

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

    /** Búsqueda por oficio del proveedor solicitante. */
    private String oficioPstSolicitante = "";

    /** Prefijo para la búsqueda de oficios. */
    private String prefijoOficio;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Constructor, por defecto vacío. */
    public ConsultarCesionesNngBean() {

    }

    /** JSR260, Método invocado al cargar terminar de instanciar el bean. */
    @PostConstruct
    public void init() {
        try {
            // Listado de proveedores de servicio
            listaProveedores = nngFacade.findAllProveedores();
            proveedorSeleccionado = new Proveedor();

            // Listado de estado de solicitudes
            listaEstados = nngFacade.findAllEstadosSolicitud();
            estadoSeleccionado = new EstadoSolicitud();
            estadoSeleccionado.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);

            // Carga Lazy y Filtros de búsqueda.
            filtros = new FiltroBusquedaSolicitudes();
            filtros.setEstado(estadoSeleccionado);

            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(nngFacade,
                    Parametro.REGISTROS_POR_PAGINA_BACK));
            registroPorPagina = filtros.getResultadosPagina();

            solicitudesCesionNngModel = new SolicitudCesionNngConsultaLazyModel();
            solicitudesCesionNngModel.setFiltros(filtros);
            solicitudesCesionNngModel.setNngFacade(nngFacade);

            // Reseteamos el Estado para que no esté preseleccionado.
            estadoSeleccionado = null;

            // Plantilla de oficio PST Solicitante
            prefijoOficio = nngFacade.getParamByName(Parametro.PREFIJO);
            oficioPstSolicitante = new String(prefijoOficio);

            // Lista de claves de servicio activas.
            listaClavesServicio = nngFacade.findAllClaveServicioActivas();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar el botón 'buscar' sobre el buscador de solicitudes. */
    public void realizarBusqueda() {
        try {
            if (solicitudesCesionNngModel == null) {
                filtros = new FiltroBusquedaSolicitudes();
                solicitudesCesionNngModel = new SolicitudCesionNngConsultaLazyModel();
                solicitudesCesionNngModel.setFiltros(filtros);
                solicitudesCesionNngModel.setNngFacade(nngFacade);
            }

            // Resetamos los filtros
            filtros.clear();
            solicitudesCesionNngModel.clear();

            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(nngFacade,
                    Parametro.REGISTROS_POR_PAGINA_BACK));

            // Filtro de valor de consecutivo
            if (!StringUtils.isEmpty(consecutivo)) {
                if (StringUtils.isNumeric(consecutivo) && (StringUtils.length(consecutivo) <= 9)) {
                    filtros.setConsecutivo(consecutivo);
                } else {
                    MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0007);
                }
            }

            // El resto de parámetros si no se han seleccionado son nulos
            filtros.setNumIniciofchSolicitud(fechaDesdeAsignacion);
            filtros.setNumFinalfchSolicitud(fechaHastaAsignacion);
            filtros.setFechaImplementacionDesde(fechaDesdeImpl);
            filtros.setFechaImplementacionHasta(fechaHastaImpl);

            filtros.setProveedor(proveedorSeleccionado);
            filtros.setEstado(estadoSeleccionado);
            filtros.setPstCesionario(pstCesionario);
            filtros.setIdClaveServicio(claveServicio);
            if (!StringUtils.isEmpty(sna)) {
                filtros.setSna(new BigDecimal(sna));
            }

            // Oficio del PST Solicitante
            if (!StringUtils.isEmpty(oficioPstSolicitante) && !oficioPstSolicitante.equals(prefijoOficio)) {
                filtros.setOficioPstSolicitante(oficioPstSolicitante);
            }

            PaginatorUtil.resetPaginacion("FRM_ConsultarCesionesNNG:TBL_SolicitudesCesion",
                    filtros.getResultadosPagina());

            // Si ha seleccionado fechas comprobamos que sean coherentes (VL1)
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

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar el botón 'limpiar' sobre el buscador de solicitudes. */
    public void limpiarBusqueda() {
        this.consecutivo = null;
        this.proveedorSeleccionado = null;
        this.estadoSeleccionado = null;
        this.pstCesionario = null;
        this.sna = null;
        this.claveServicio = null;
        this.fechaDesdeAsignacion = null;
        this.fechaDesdeImpl = null;
        this.fechaHastaAsignacion = null;
        this.fechaHastaAsignacionHabilitada = false;
        this.fechaHastaImpl = null;
        this.fechaHastaImplHabilitada = false;
        this.oficioPstSolicitante = new String(prefijoOficio);
        this.registroSeleccionado = null;
        this.solicitudesCesionNngModel = null;
    }

    /** Método invocado al pulsar sobre el botón 'Cancelar' en la tabla de solicitudes. */
    public void eliminarSolicitud() {
        try {
            if (registroSeleccionado != null) {
                if (registroSeleccionado.isCancelarDisponible()) {
                    SolicitudCesionNng solicitud =
                            nngFacade.getSolicitudCesionById(new BigDecimal(registroSeleccionado.getConsecutivo()));

                    String consecutivo = registroSeleccionado.getConsecutivo();
                    PeticionCancelacion cancelacion = nngFacade.cancelSolicitudCesion(solicitud);
                    if (cancelacion.isCancelacionPosible()) {
                        MensajesBean.addInfoMsg(MSG_ID, "Solicitud " + consecutivo + " cancelada.", "");
                    } else {
                        MensajesBean.addErrorMsg(MSG_ID, cancelacion.getMensajeError(), "");
                    }

                } else {
                    MensajesBean.addErrorMsg(MSG_ID, "No se puede cancelar una solicitud en estado: "
                            + registroSeleccionado.getEstatus(), "");
                }
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al pulsar sobre el botón 'Cancelar' en la consulta general de solicitudes.
     * @param pSolicitud solicitud
     * @param pMensajes pMensajes
     */
    public void eliminarSolicitud(Solicitud pSolicitud, String pMensajes) {
        try {
            if (solicitudCesionNngBean != null) {
                PeticionCancelacion cancelacion = nngFacade.cancelSolicitudCesion((SolicitudCesionNng) pSolicitud);
                if (cancelacion.isCancelacionPosible()) {
                    MensajesBean.addInfoMsg(pMensajes, "Solicitud " + consecutivo + " cancelada.", "");
                } else {
                    StringBuffer sbMensaje = new StringBuffer();
                    sbMensaje.append("No se ha podido cancelar la solicitud ").append(consecutivo).append(". ");
                    sbMensaje.append(cancelacion.getMensajeError());
                    MensajesBean.addErrorMsg(pMensajes, sbMensaje.toString(), "");
                }
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(pMensajes, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar sobre el botón 'Editar' en la tabla de solicitudes. */
    public void editarSolicitud() {
        try {
            if (registroSeleccionado != null && solicitudCesionNngBean != null) {
                SolicitudCesionNng solicitud =
                        nngFacade.getSolicitudCesionById(new BigDecimal(registroSeleccionado.getConsecutivo()));
                solicitudCesionNngBean.cargarSolicitud(solicitud);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al pulsar sobre el botón 'Editar' en la tabla de solicitudes.
     * @param pSolicitud solicitud
     */
    public void editarSolicitud(Solicitud pSolicitud) {
        try {
            if (solicitudCesionNngBean != null) {
                solicitudCesionNngBean.cargarSolicitud((SolicitudCesionNng) pSolicitud);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
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
     * Proveedor seleccionado para la búsqueda.
     * @return Proveedor
     */
    public Proveedor getProveedorSeleccionado() {
        return proveedorSeleccionado;
    }

    /**
     * Proveedor seleccionado para la búsqueda.
     * @param proveedorSeleccionado Proveedor
     */
    public void setProveedorSeleccionado(Proveedor proveedorSeleccionado) {
        this.proveedorSeleccionado = proveedorSeleccionado;
    }

    /**
     * Catálogo de proveedores.
     * @return List
     */
    public List<Proveedor> getListaProveedores() {
        return listaProveedores;
    }

    /**
     * Estado de solicitud seleccionado para la búsqueda.
     * @return EstadoSolicitud
     */
    public EstadoSolicitud getEstadoSeleccionado() {
        return estadoSeleccionado;
    }

    /**
     * Estado de solicitud seleccionado para la búsqueda.
     * @param estadoSeleccionado EstadoSolicitud
     */
    public void setEstadoSeleccionado(EstadoSolicitud estadoSeleccionado) {
        this.estadoSeleccionado = estadoSeleccionado;
    }

    /**
     * Catálogo de estados de solicitud.
     * @return List
     */
    public List<EstadoSolicitud> getListaEstados() {
        return listaEstados;
    }

    /**
     * Proveedor cesionario seleccionado.
     * @return Proveedor
     */
    public Proveedor getPstCesionario() {
        return pstCesionario;
    }

    /**
     * Proveedor cesionario seleccionado.
     * @param pstCesionario Proveedor
     */
    public void setPstCesionario(Proveedor pstCesionario) {
        this.pstCesionario = pstCesionario;
    }

    /**
     * Identificador de numeración de serie.
     * @return String
     */
    public String getSna() {
        return sna;
    }

    /**
     * Identificador de numeración de serie.
     * @param sna String
     */
    public void setSna(String sna) {
        this.sna = sna;
    }

    /**
     * Rango inicial de búsqueda para fecha de Asignación de Solicitud.
     * @return Date
     */
    public Date getFechaDesdeAsignacion() {
        return fechaDesdeAsignacion;
    }

    /**
     * Rango inicial de búsqueda para fecha de Asignación de Solicitud.
     * @param fechaDesdeAsignacion Date
     */
    public void setFechaDesdeAsignacion(Date fechaDesdeAsignacion) {
        this.fechaDesdeAsignacion = fechaDesdeAsignacion;
    }

    /**
     * Rango final de búsqueda para fecha de Asignación de Solicitud.
     * @return Date
     */
    public Date getFechaHastaAsignacion() {
        return fechaHastaAsignacion;
    }

    /**
     * Rango final de búsqueda para fecha de Asignación de Solicitud.
     * @param fechaHastaAsignacion Date
     */
    public void setFechaHastaAsignacion(Date fechaHastaAsignacion) {
        this.fechaHastaAsignacion = fechaHastaAsignacion;
    }

    /**
     * Indica si el campo de búsqueda 'fecha hasta' está habilitado.
     * @return boolean
     */
    public boolean isFechaHastaAsignacionHabilitada() {
        return fechaHastaAsignacionHabilitada;
    }

    /**
     * Rango inicial de búsqueda para fecha de Implementación de Solicitud.
     * @return Date
     */
    public Date getFechaDesdeImpl() {
        return fechaDesdeImpl;
    }

    /**
     * Rango inicial de búsqueda para fecha de Implementación de Solicitud.
     * @param fechaDesdeImpl Date
     */
    public void setFechaDesdeImpl(Date fechaDesdeImpl) {
        this.fechaDesdeImpl = fechaDesdeImpl;
    }

    /**
     * Rango final de búsqueda para fecha de Implementación de Solicitud.
     * @return Date
     */
    public Date getFechaHastaImpl() {
        return fechaHastaImpl;
    }

    /**
     * Rango final de búsqueda para fecha de Implementación de Solicitud.
     * @param fechaHastaImpl Date
     */
    public void setFechaHastaImpl(Date fechaHastaImpl) {
        this.fechaHastaImpl = fechaHastaImpl;
    }

    /**
     * Indica si el campo de búsqueda 'fecha hasta' está habilitado.
     * @return boolean
     */
    public boolean isFechaHastaImplHabilitada() {
        return fechaHastaImplHabilitada;
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
     * Registro seleccionado en la tabla de consultas.
     * @return DetalleConsultaCesionNng
     */
    public DetalleConsultaCesionNng getRegistroSeleccionado() {
        return registroSeleccionado;
    }

    /**
     * Registro seleccionado en la tabla de consultas.
     * @param registroSeleccionado DetalleConsultaCesionNng
     */
    public void setRegistroSeleccionado(DetalleConsultaCesionNng registroSeleccionado) {
        this.registroSeleccionado = registroSeleccionado;
    }

    /**
     * Modelo Lazy para carga de solicitudes de cesion de numeración no geográfica.
     * @return SolicitudCesionNngConsultaLazyModel
     */
    public SolicitudCesionNngConsultaLazyModel getSolicitudesCesionNngModel() {
        return solicitudesCesionNngModel;
    }

    /**
     * Modelo Lazy para carga de solicitudes de cesion de numeración no geográfica.
     * @param solicitudesCesionNngModel SolicitudCesionNngConsultaLazyModel
     */
    public void setSolicitudesCesionNngModel(SolicitudCesionNngConsultaLazyModel solicitudesCesionNngModel) {
        this.solicitudesCesionNngModel = solicitudesCesionNngModel;
    }

    /**
     * Referencia al Bean de Solicitud de Cesión de Numeración No Geográfica.
     * @return SolicitudCesionNngBean
     */
    public SolicitudCesionNngBean getSolicitudCesionNngBean() {
        return solicitudCesionNngBean;
    }

    /**
     * Referencia al Bean de Solicitud de Cesión de Numeración No Geográfica.
     * @param solicitudCesionNngBean SolicitudCesionNngBean
     */
    public void setSolicitudCesionNngBean(SolicitudCesionNngBean solicitudCesionNngBean) {
        this.solicitudCesionNngBean = solicitudCesionNngBean;
    }

    /**
     * Identificador de la Clave de Servicio.
     * @return BigDecimal
     */
    public BigDecimal getClaveServicio() {
        return claveServicio;
    }

    /**
     * Identificador de la Clave de Servicio.
     * @param claveServicio BigDecimal
     */
    public void setClaveServicio(BigDecimal claveServicio) {
        this.claveServicio = claveServicio;
    }

    /**
     * Listado de claves de servicio.
     * @return List<ClaveServicio>
     */
    public List<ClaveServicio> getListaClavesServicio() {
        return listaClavesServicio;
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
