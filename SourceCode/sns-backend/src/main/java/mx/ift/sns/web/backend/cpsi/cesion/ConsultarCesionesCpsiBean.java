package mx.ift.sns.web.backend.cpsi.cesion;

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
import mx.ift.sns.modelo.cpsi.SolicitudCesionCpsi;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCPSI;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.negocio.cpsi.ICodigoCPSIFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.lazymodels.SolicitudCesionCpsiConsultaLazyModel;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Gestiona el buscador de solicitudes de Cesión de CPSI. */
@ManagedBean(name = "consultarCesionesCpsiBean")
@ViewScoped
public class ConsultarCesionesCpsiBean implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarCesionesCpsiBean.class);

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultarCesionesCPSI";

    /** Servicio de Catalogos. */
    @EJB(mappedName = "CodigoCPSIFacade")
    private ICodigoCPSIFacade codigoCpsiFacade;

    /** Filtros de búsqueda de Solicitudes. */
    private FiltroBusquedaSolicitudesCPSI filtros;

    /** Modelo Lazy para carga de solicitudes de cesion. */
    private SolicitudCesionCpsiConsultaLazyModel solicitudesCesionModel;

    /** Identificador de solicitud. */
    private String consecutivo;

    /** Proveedor seleccionado para la búsqueda. */
    private Proveedor pstCedenteSeleccionado;

    /** Catálogo de proveedores. */
    private List<Proveedor> listaProveedores;

    /** Proveedor cesionario seleccionado. */
    private Proveedor pstCesionarioSeleccionado;

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

    /** Solicitud seleccionada en la tabla de búsqueda de solicitudes. */
    private DetalleConsultaCesionCpsi registroSeleccionado;

    /** Referencia al Bean de Solicitud de Cesión CPSI. */
    @ManagedProperty("#{solicitudCesionCpsiBean}")
    private SolicitudCesionCpsiBean solicitudCesionBean;

    /** Referencia de la solicitud. */
    private String referenciaSolicitud;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Constructor. */
    public ConsultarCesionesCpsiBean() {
    }

    /** JSR260, Método invocado al cargar terminar de instanciar el bean. */
    @PostConstruct
    public void init() {
        try {
            // Listado de proveedores de servicio
            listaProveedores = codigoCpsiFacade.findAllProveedores();
            pstCedenteSeleccionado = new Proveedor();

            // Listado de estado de solicitudes
            listaEstatus = codigoCpsiFacade.findAllEstadosSolicitud();
            estatusSeleccionado = new EstadoSolicitud();
            estatusSeleccionado.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);

            // Carga Lazy y Filtros de búsqueda.
            filtros = new FiltroBusquedaSolicitudesCPSI();
            filtros.setEstado(estatusSeleccionado);

            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(codigoCpsiFacade,
                    Parametro.REGISTROS_POR_PAGINA_BACK));
            registroPorPagina = filtros.getResultadosPagina();

            solicitudesCesionModel = new SolicitudCesionCpsiConsultaLazyModel();
            solicitudesCesionModel.setFiltros(filtros);
            solicitudesCesionModel.setFacade(codigoCpsiFacade);

            // Reseteamos el estatus para que no esté preseleccionado.
            estatusSeleccionado = null;

            // Plantilla de oficio PST Solicitante
            prefijoOficio = codigoCpsiFacade.getParamByName(Parametro.PREFIJO);
            oficioPstSolicitante = new String(prefijoOficio);

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar el botón 'buscar' sobre el buscador de solicitudes. */
    public void realizarBusqueda() {
        try {
            if (solicitudesCesionModel == null) {
                filtros = new FiltroBusquedaSolicitudesCPSI();
                solicitudesCesionModel = new SolicitudCesionCpsiConsultaLazyModel();
                solicitudesCesionModel.setFiltros(filtros);
                solicitudesCesionModel.setFacade(codigoCpsiFacade);
            }

            // Resetamos los filtros
            filtros.clear();
            solicitudesCesionModel.clear();

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
            filtros.setProveedor(pstCedenteSeleccionado);
            filtros.setEstado(estatusSeleccionado);
            filtros.setPstCesionario(pstCesionarioSeleccionado);

            // Oficio del PST Solicitante
            if (!StringUtils.isEmpty(oficioPstSolicitante) && !oficioPstSolicitante.equals(prefijoOficio)) {
                filtros.setOficioPstSolicitante(oficioPstSolicitante);
            }

            PaginatorUtil.resetPaginacion("FRM_ConsultarCesionesCPSI:TBL_SolicitudesCesionCPSI",
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
        this.pstCedenteSeleccionado = null;
        this.pstCesionarioSeleccionado = null;
        this.oficioPstSolicitante = new String(prefijoOficio);
        this.fechaDesdeAsignacion = null;
        this.fechaHastaAsignacion = null;
        this.fechaHastaAsignacionHabilitada = false;
        this.fechaDesdeImpl = null;
        this.fechaHastaImpl = null;
        this.fechaHastaImplHabilitada = false;
        this.estatusSeleccionado = null;
        this.registroSeleccionado = null;
        this.solicitudesCesionModel = null;
    }

    /** Habilita el campo de fecha Asignación hasta en función del campo fecha Asignación inicio. */
    public void habilitarFechaHastaAsignacion() {
        fechaHastaAsignacionHabilitada = (fechaDesdeAsignacion != null);
    }

    /** Habilita el campo de fecha Implementación hasta en función del campo fecha Implementación inicio. */
    public void habilitarFechaHastaImplementacion() {
        fechaHastaImplHabilitada = (fechaDesdeImpl != null);
    }

    /** Método invocado al pulsar sobre el botón 'Editar' en la tabla de solicitudes. */
    public void editarSolicitud() {
        try {
            if (registroSeleccionado != null && solicitudCesionBean != null) {
                SolicitudCesionCpsi solicitud =
                        codigoCpsiFacade.getSolicitudCesionById(new BigDecimal(registroSeleccionado.getConsecutivo()));
                solicitudCesionBean.cargarSolicitud(solicitud);
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
            if (solicitudCesionBean != null) {
                solicitudCesionBean.cargarSolicitud((SolicitudCesionCpsi) pSolicitud);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar sobre el botón 'Cancelar' en la tabla de solicitudes. */
    public void eliminarSolicitud() {
        try {
            if (registroSeleccionado != null) {
                if (registroSeleccionado.isCancelarDisponible()) {
                    SolicitudCesionCpsi solicitud =
                            codigoCpsiFacade.getSolicitudCesionById(
                                    new BigDecimal(registroSeleccionado.getConsecutivo()));

                    String consecutivo = registroSeleccionado.getConsecutivo();
                    PeticionCancelacion cancelacion = codigoCpsiFacade.cancelSolicitudCesion(solicitud);
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
            if (solicitudCesionBean != null) {
                String consecutivo = pSolicitud.getId() != null ? pSolicitud.getId().toString() : "";
                PeticionCancelacion cancelacion = codigoCpsiFacade
                        .cancelSolicitudCesion((SolicitudCesionCpsi) pSolicitud);
                if (cancelacion.isCancelacionPosible()) {
                    MensajesBean.addInfoMsg(pMensajes, "Solicitud " + consecutivo + " cancelada.", "");
                } else {
                    StringBuffer sbMensaje = new StringBuffer();
                    sbMensaje.append("No se ha podido cancelar la solicitud ").append(consecutivo).append(". <br>");
                    sbMensaje.append(cancelacion.getMensajeError());
                    MensajesBean.addErrorMsg(pMensajes, sbMensaje.toString(), "");
                }
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    // GETTERS & SETTERS

    /**
     * Filtros de búsqueda de Solicitudes.
     * @return the filtros
     */
    public FiltroBusquedaSolicitudesCPSI getFiltros() {
        return filtros;
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
     * Proveedor seleccionado para la búsqueda.
     * @return Proveedor
     */
    public Proveedor getPstCedenteSeleccionado() {
        return pstCedenteSeleccionado;
    }

    /**
     * Proveedor seleccionado para la búsqueda.
     * @param pstCedenteSeleccionado Proveedor
     */
    public void setPstCedenteSeleccionado(Proveedor pstCedenteSeleccionado) {
        this.pstCedenteSeleccionado = pstCedenteSeleccionado;
    }

    /**
     * Catálogo de proveedores.
     * @return List
     */
    public List<Proveedor> getListaProveedores() {
        return listaProveedores;
    }

    /**
     * Proveedor cesionario seleccionado.
     * @return Proveedor
     */
    public Proveedor getPstCesionarioSeleccionado() {
        return pstCesionarioSeleccionado;
    }

    /**
     * Proveedor cesionario seleccionado.
     * @param pstCesionarioSeleccionado Proveedor
     */
    public void setPstCesionarioSeleccionado(Proveedor pstCesionarioSeleccionado) {
        this.pstCesionarioSeleccionado = pstCesionarioSeleccionado;
    }

    /**
     * Búsqueda por oficio del proveedor solicitante.
     * @return String
     */
    public String getOficioPstSolicitante() {
        return oficioPstSolicitante;
    }

    /**
     * Búsqueda por oficio del proveedor solicitante.
     * @param oficioPstSolicitante String
     */
    public void setOficioPstSolicitante(String oficioPstSolicitante) {
        this.oficioPstSolicitante = oficioPstSolicitante;
    }

    /**
     * Prefijo para la búsqueda de oficios.
     * @return String
     */
    public String getPrefijoOficio() {
        return prefijoOficio;
    }

    /**
     * Prefijo para la búsqueda de oficios.
     * @param prefijoOficio String
     */
    public void setPrefijoOficio(String prefijoOficio) {
        this.prefijoOficio = prefijoOficio;
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
     * Indica si el campo de búsqueda 'fecha hasta' está habilitado.
     * @param fechaHastaAsignacionHabilitada boolean
     */
    public void setFechaHastaAsignacionHabilitada(boolean fechaHastaAsignacionHabilitada) {
        this.fechaHastaAsignacionHabilitada = fechaHastaAsignacionHabilitada;
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
     * Indica si el campo de búsqueda 'fecha hasta' está habilitado.
     * @param fechaHastaImplHabilitada boolean
     */
    public void setFechaHastaImplHabilitada(boolean fechaHastaImplHabilitada) {
        this.fechaHastaImplHabilitada = fechaHastaImplHabilitada;
    }

    /**
     * Estatus de solicitud seleccionado para la búsqueda.
     * @return EstadoSolicitud
     */
    public EstadoSolicitud getEstatusSeleccionado() {
        return estatusSeleccionado;
    }

    /**
     * Estatus de solicitud seleccionado para la búsqueda.
     * @param estatusSeleccionado EstadoSolicitud
     */
    public void setEstatusSeleccionado(EstadoSolicitud estatusSeleccionado) {
        this.estatusSeleccionado = estatusSeleccionado;
    }

    /**
     * Catálogo de estatus de solicitud.
     * @return List
     */
    public List<EstadoSolicitud> getListaEstatus() {
        return listaEstatus;
    }

    /**
     * Solicitud seleccionada en la tabla de búsqueda de solicitudes.
     * @return DetalleConsultaCesionCpsi
     */
    public DetalleConsultaCesionCpsi getRegistroSeleccionado() {
        return registroSeleccionado;
    }

    /**
     * Solicitud seleccionada en la tabla de búsqueda de solicitudes.
     * @param registroSeleccionado DetalleConsultaCesionCpsi
     */
    public void setRegistroSeleccionado(DetalleConsultaCesionCpsi registroSeleccionado) {
        this.registroSeleccionado = registroSeleccionado;
    }

    /**
     * Referencia de la solicitud.
     * @return String
     */
    public String getReferenciaSolicitud() {
        return referenciaSolicitud;
    }

    /**
     * Referencia de la solicitud.
     * @param referenciaSolicitud String
     */
    public void setReferenciaSolicitud(String referenciaSolicitud) {
        this.referenciaSolicitud = referenciaSolicitud;
    }

    /**
     * Referencia al Bean de Solicitud de Cesión CPSI.
     * @return SolicitudCesionCpsiBean
     */
    public SolicitudCesionCpsiBean getSolicitudCesionBean() {
        return solicitudCesionBean;
    }

    /**
     * Referencia al Bean de Solicitud de Cesión CPSI.
     * @param solicitudCesionBean SolicitudCesionCpsiBean
     */
    public void setSolicitudCesionBean(SolicitudCesionCpsiBean solicitudCesionBean) {
        this.solicitudCesionBean = solicitudCesionBean;
    }

    /**
     * Modelo Lazy para carga de solicitudes de cesion.
     * @return SolicitudCesionCpsiConsultaLazyModel
     */
    public SolicitudCesionCpsiConsultaLazyModel getSolicitudesCesionModel() {
        return solicitudesCesionModel;
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
