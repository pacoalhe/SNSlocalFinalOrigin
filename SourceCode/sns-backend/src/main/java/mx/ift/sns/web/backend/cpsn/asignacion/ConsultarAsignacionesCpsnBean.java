package mx.ift.sns.web.backend.cpsn.asignacion;

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
import mx.ift.sns.modelo.cpsn.SolicitudAsignacionCpsn;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCpsn;
import mx.ift.sns.modelo.ng.SolicitudAsignacion;
import mx.ift.sns.modelo.oficios.Oficio;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.negocio.cpsn.ICodigoCPSNFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.lazymodels.SolicitudAsignacionCpsnConsultaLazyModel;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Bean asociado a la búsqueda de Solicitudes de asignación de códigos CPSN. */
@ManagedBean(name = "consultarAsignacionesCpsnBean")
@ViewScoped
public class ConsultarAsignacionesCpsnBean implements Serializable {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarAsignacionesCpsnBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Id de errores. */
    private static final String MSG_ID = "MSG_ConsultarAsignaciones";

    /** Número de Consecutivo. */
    private String consecutivo;

    /** Proveedor seleccionado. */
    private Proveedor proveedor;

    /** Catálogo de proveedores. */
    private List<Proveedor> listaProveedores;

    /** Número de oficio para la búsqueda. */
    private String numeroOficio;

    /** Estado de solicitud seleccionado para la búsqueda. */
    private EstadoSolicitud estadoSeleccionado;

    /** Catálogo de estados de solicitud. */
    private List<EstadoSolicitud> listaEstados;

    /** Listado de las asignaciones. **/
    private List<SolicitudAsignacion> listaAsignacion;

    /** Fecha asignación desde. */
    private Date fechaDesdeAsignacion;

    /** Fecha asignación hasta. */
    private Date fechaHastaAsignacion;

    /** Fecha solicitud desde. */
    private Date fechaDesdeSolicitud;

    /** Fecha solicitud hasta. */
    private Date fechaHastaSolicitud;

    /** Prefijo del Numero de Oficio. */
    private String prefijo;

    /** Referencia de la solicitud. */
    private String referenciaSolicitud;

    /** Modelo Lazy para carga de solicitudes de liberación. */
    private SolicitudAsignacionCpsnConsultaLazyModel solicitudesAsignacionModel;

    /** Filtros de búsqueda de Solicitudes. */
    private FiltroBusquedaSolicitudesCpsn filtros;

    /** Solicitud seleccionada en la tabla de búsqueda de solicitudes. */
    private DetalleConsultaAsignacionCpsn registroSeleccionado;

    /** Solicitud seleccionada en la tabla de búsqueda de solicitudes. */
    private SolicitudAsignacion solicitudSeleccionada;

    /** Fecha Habilitada. */
    private boolean fechaHabilitada = false;

    /** Servicio de CodigoCPSN. */
    @EJB(mappedName = "CodigoCPSNFacade")
    private ICodigoCPSNFacade cpsnFacade;

    /** Referencia al Bean de Solicitud de Asignaciones . */
    @ManagedProperty("#{solicitudAsignacionCpsnBean}")
    private SolicitudAsignacionCpsnBean solicitudAsignacionCpsnBean;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Constructor, por defecto vacío. */
    public ConsultarAsignacionesCpsnBean() {
    }

    /**
     * Iniciamos la listaAsignacion y la lista proveedores y cargamos los combos.
     * @throws Exception error en inicio
     **/
    @PostConstruct
    public void init() throws Exception {
        try {
            // Iniciamos valores
            consecutivo = null;
            fechaDesdeAsignacion = null;
            fechaHastaAsignacion = null;
            fechaDesdeSolicitud = null;
            fechaHastaSolicitud = null;

            // Cargamos el combo de proveedores
            listaProveedores = cpsnFacade.findAllProveedores();
            proveedor = null;

            // Cargamos el prefijo del numero de oficion
            prefijo = cpsnFacade.getParamByName(Parametro.PREFIJO);
            numeroOficio = new String(prefijo);

            // Cargamos el combo de estados de la solicitud
            listaEstados = cpsnFacade.findAllEstadosSolicitud();
            estadoSeleccionado = null;

            EstadoSolicitud estadoSeleccionadoAux = new EstadoSolicitud();
            estadoSeleccionadoAux.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);

            // Carga Lazy y Filtros de búsqueda
            filtros = new FiltroBusquedaSolicitudesCpsn();
            filtros.setEstado(estadoSeleccionadoAux);
            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(cpsnFacade,
                    Parametro.REGISTROS_POR_PAGINA_BACK));
            registroPorPagina = filtros.getResultadosPagina();

            solicitudesAsignacionModel = new SolicitudAsignacionCpsnConsultaLazyModel();
            solicitudesAsignacionModel.setFiltros(filtros);
            solicitudesAsignacionModel.setFacade(cpsnFacade);

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al pulsar el botón 'buscar' sobre el buscador de solicitudes.
     */
    public void realizarBusqueda() {
        try {
            if (solicitudesAsignacionModel == null) {
                filtros = new FiltroBusquedaSolicitudesCpsn();
                solicitudesAsignacionModel = new SolicitudAsignacionCpsnConsultaLazyModel();
                solicitudesAsignacionModel.setFiltros(filtros);
                solicitudesAsignacionModel.setFacade(cpsnFacade);
            }

            // Resetamos los filtros
            filtros.clear();
            solicitudesAsignacionModel.clear();

            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(cpsnFacade,
                    Parametro.REGISTROS_POR_PAGINA_BACK));

            // Filtro de valor de consecutivo
            if (!StringUtils.isEmpty(consecutivo)) {
                if (StringUtils.isNumeric(consecutivo)) {
                    if (StringUtils.length(consecutivo) <= 9) {
                        filtros.setConsecutivo(consecutivo);
                    } else {
                        MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0007), "");
                    }
                } else {
                    MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0007), "");
                }
            }

            // Filtro de Número de Oficio
            if (!StringUtils.isEmpty(numeroOficio)) {
                if (numeroOficio.equals(prefijo)) {
                    // no hacemos nada
                    LOGGER.debug("oficio = prefijo");
                } else if (StringUtils.length(numeroOficio) <= Oficio.LONGITUD_NUM_OFICIO) {

                    filtros.setOficioPstSolicitante(numeroOficio);
                } else {
                    MensajesBean.addErrorMsg(MSG_ID,
                            "El campo 'Número de oficio' ha de tener un formato máximo de 30 dígitos", "");
                }
            }

            // El resto de parámetros si no se han seleccionado son nulos
            filtros.setFechaSolicitudDesde(fechaDesdeSolicitud);
            filtros.setFechaSolicitudHasta(fechaHastaSolicitud);
            filtros.setFechaAsignacionDesde(fechaDesdeAsignacion);
            filtros.setFechaAsignacionHasta(fechaHastaAsignacion);
            filtros.setProveedor(proveedor);
            filtros.setEstado(estadoSeleccionado);

            PaginatorUtil.resetPaginacion("FRM_ConsultarAsignacionesCPSN:TBL_SolicitudesAsignacion",
                    filtros.getResultadosPagina());

            if ((fechaDesdeSolicitud != null && fechaHastaSolicitud != null)
                    && (fechaHastaSolicitud.before(fechaDesdeSolicitud))) {
                MensajesBean.addErrorMsg(MSG_ID,
                        "La fecha final de solicitud debe ser mayor o igual a la fecha de inicio", "");
            }

            if ((fechaDesdeAsignacion != null && fechaHastaAsignacion != null)
                    && (fechaHastaAsignacion.before(fechaDesdeAsignacion))) {
                MensajesBean.addErrorMsg(MSG_ID,
                        "La fecha final debe de asignación ser mayor o igual a la fecha de inicio", "");
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }

    }

    /**
     * Método invocado al pulsar el botón 'limpiar' sobre el buscador de solicitudes.
     */
    public void limpiarBusqueda() {
        this.consecutivo = null;
        this.proveedor = null;
        this.fechaDesdeAsignacion = null;
        this.fechaHastaAsignacion = null;
        this.fechaDesdeSolicitud = null;
        this.fechaHastaSolicitud = null;
        this.estadoSeleccionado = null;
        this.numeroOficio = new String(prefijo);
        this.solicitudesAsignacionModel = null;
    }

    /**
     * Método invocado al pulsar sobre el botón 'Editar' en la tabla de solicitudes.
     */
    public void editarSolicitud() {
        try {
            if (registroSeleccionado != null && solicitudAsignacionCpsnBean != null) {
                SolicitudAsignacionCpsn solicitud =
                        cpsnFacade.getSolicitudAsignacionById(new BigDecimal(registroSeleccionado.getConsecutivo()));
                solicitudAsignacionCpsnBean.cargarSolicitud(solicitud);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al pulsar sobre el botón 'Editar' en la tabla de solicitudes de la consulta generica.
     * @param pSolicitud solicitud a cargar
     */
    public void editarSolicitud(Solicitud pSolicitud) {
        try {
            if (solicitudAsignacionCpsnBean != null) {
                solicitudAsignacionCpsnBean.cargarSolicitud((SolicitudAsignacionCpsn) pSolicitud);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al pulsar sobre el botón 'Cancelar' en la tabla de solicitudes.
     */
    public void eliminarSolicitud() {
        try {
            if (registroSeleccionado != null) {
                if (registroSeleccionado.isCancelarDisponible()) {
                    SolicitudAsignacionCpsn solicitud = cpsnFacade.getSolicitudAsignacionById(new BigDecimal(
                            registroSeleccionado.getConsecutivo()));

                    String consecutivo = registroSeleccionado.getConsecutivo();
                    PeticionCancelacion cancelacion = cpsnFacade.cancelSolicitudAsignacion(solicitud);
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
     * Método invocado al pulsar sobre el botón 'Cancelar' en la tabla de solicitudes de la consulta genérica.
     * @param pSolicitud solicitud
     * @param pMensajes pMensajes
     */
    public void eliminarSolicitud(Solicitud pSolicitud, String pMensajes) {
        try {
            if (solicitudAsignacionCpsnBean != null) {
                String consecutivo = pSolicitud.getId() != null ? pSolicitud.getId().toString() : "";
                PeticionCancelacion cancelacion =
                        cpsnFacade.cancelSolicitudAsignacion((SolicitudAsignacionCpsn) pSolicitud);
                if (cancelacion.isCancelacionPosible()) {
                    MensajesBean.addInfoMsg(pMensajes, "Solicitud " + consecutivo + " cancelada.", "");
                } else {
                    StringBuffer sbMensaje = new StringBuffer();
                    sbMensaje.append("No se ha podido cancelar la solicitud ").append(consecutivo).append(".  <br>");
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
     * Identificador de los proveedores.
     * @return List<Proveedor>
     */
    public List<Proveedor> getListaProveedores() {
        return listaProveedores;
    }

    /**
     * Identificador de los proveedores.
     * @param listaProveedores List<Proveedor>
     */
    public void setListaProveedores(List<Proveedor> listaProveedores) {
        this.listaProveedores = listaProveedores;
    }

    /**
     * Identificador del número de oficio.
     * @return String
     */
    public String getNumeroOficio() {
        return numeroOficio;
    }

    /**
     * Identificador del número de oficio.
     * @param numeroOficio String
     */
    public void setNumeroOficio(String numeroOficio) {
        this.numeroOficio = numeroOficio;
    }

    /**
     * Identificador del estado seleccionado.
     * @return EstadoSolicitud
     */
    public EstadoSolicitud getEstadoSeleccionado() {
        return estadoSeleccionado;
    }

    /**
     * Identificador del estado seleccionado.
     * @param estadoSeleccionado EstadoSolicitud
     */
    public void setEstadoSeleccionado(EstadoSolicitud estadoSeleccionado) {
        this.estadoSeleccionado = estadoSeleccionado;
    }

    /**
     * Identificador de los estados de la solicitud.
     * @return List<EstadoSolicitud>
     */
    public List<EstadoSolicitud> getListaEstados() {
        return listaEstados;
    }

    /**
     * Identificador de los estados de la solicitud.
     * @param listaEstados List<EstadoSolicitud>
     */
    public void setListaEstados(List<EstadoSolicitud> listaEstados) {
        this.listaEstados = listaEstados;
    }

    /**
     * Identificador de las solicitudes de asignación.
     * @return List<SolicitudAsignacion>
     */
    public List<SolicitudAsignacion> getListaAsignacion() {
        return listaAsignacion;
    }

    /**
     * Identificador de las solicitudes de asignación.
     * @param listaAsignacion List<SolicitudAsignacion>
     */
    public void setListaAsignacion(List<SolicitudAsignacion> listaAsignacion) {
        this.listaAsignacion = listaAsignacion;
    }

    /**
     * Identificador de la Fecha de asignación desde.
     * @return Date
     */
    public Date getFechaDesdeAsignacion() {
        return fechaDesdeAsignacion;
    }

    /**
     * Identificador de la Fecha de asignación desde.
     * @param fechaDesdeAsignacion Date
     */
    public void setFechaDesdeAsignacion(Date fechaDesdeAsignacion) {
        this.fechaDesdeAsignacion = fechaDesdeAsignacion;
    }

    /**
     * Identificador de la Fecha de asignación hasta.
     * @return Date
     */
    public Date getFechaHastaAsignacion() {
        return fechaHastaAsignacion;
    }

    /**
     * Identificador de la Fecha de asignación hasta.
     * @param fechaHastaAsignacion Date
     */
    public void setFechaHastaAsignacion(Date fechaHastaAsignacion) {
        this.fechaHastaAsignacion = fechaHastaAsignacion;
    }

    /**
     * Identificador de la Fecha de solicitud desde.
     * @return Date
     */
    public Date getFechaDesdeSolicitud() {
        return fechaDesdeSolicitud;
    }

    /**
     * Identificador de la Fecha de solicitud desde.
     * @param fechaDesdeSolicitud Date
     */
    public void setFechaDesdeSolicitud(Date fechaDesdeSolicitud) {
        this.fechaDesdeSolicitud = fechaDesdeSolicitud;
    }

    /**
     * Identificador de la Fecha de solicitud hasta.
     * @return Date
     */
    public Date getFechaHastaSolicitud() {
        return fechaHastaSolicitud;
    }

    /**
     * Identificador de la Fecha de solicitud hasta.
     * @param fechaHastaSolicitud Date
     */
    public void setFechaHastaSolicitud(Date fechaHastaSolicitud) {
        this.fechaHastaSolicitud = fechaHastaSolicitud;
    }

    /**
     * Identificador de la referencia de la solicitud.
     * @return String
     */
    public String getReferenciaSolicitud() {
        return referenciaSolicitud;
    }

    /**
     * Identificador de la referencia de la solicitud.
     * @param referenciaSolicitud String
     */
    public void setReferenciaSolicitud(String referenciaSolicitud) {
        this.referenciaSolicitud = referenciaSolicitud;
    }

    /**
     * Modelo Lazy para carga de solicitudes de asignaciones.
     * @return SolicitudAsignacionCpsnConsultaLazyModel
     */
    public SolicitudAsignacionCpsnConsultaLazyModel getSolicitudesAsignacionModel() {
        return solicitudesAsignacionModel;
    }

    /**
     * Modelo Lazy para carga de solicitudes de asignaciones.
     * @param solicitudesAsignacionModel SolicitudAsignacionCpsnConsultaLazyModel
     */
    public void setSolicitudesAsignacionModel(SolicitudAsignacionCpsnConsultaLazyModel solicitudesAsignacionModel) {
        this.solicitudesAsignacionModel = solicitudesAsignacionModel;
    }

    /**
     * Identificador de la solicitud seleccionada.
     * @return SolicitudAsignacion
     */
    public SolicitudAsignacion getSolicitudSeleccionada() {
        return solicitudSeleccionada;
    }

    /**
     * Identificador de la solicitud seleccionada.
     * @param solicitudSeleccionada SolicitudAsignacion
     */
    public void setSolicitudSeleccionada(SolicitudAsignacion solicitudSeleccionada) {
        this.solicitudSeleccionada = solicitudSeleccionada;
    }

    /**
     * Identificador de fecha habilitada.
     * @return boolean
     */
    public boolean isFechaHabilitada() {
        return fechaHabilitada;
    }

    /**
     * Identificador de fecha habilitada.
     * @param fechaHabilitada boolean
     */
    public void setFechaHabilitada(boolean fechaHabilitada) {
        this.fechaHabilitada = fechaHabilitada;
    }

    /**
     * Identificador del proveedor seleccionado.
     * @return Proveedor
     */
    public Proveedor getProveedor() {
        return proveedor;
    }

    /**
     * Identificador del proveedor seleccionado.
     * @param proveedor Proveedor
     */
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * Registro seleccionado en la tabla de consultas.
     * @return DetalleConsultaAsignacionCpsn
     */
    public DetalleConsultaAsignacionCpsn getRegistroSeleccionado() {
        return registroSeleccionado;
    }

    /**
     * Registro seleccionado en la tabla de consultas.
     * @param registroSeleccionado DetalleConsultaAsignacionCpsn
     */
    public void setRegistroSeleccionado(DetalleConsultaAsignacionCpsn registroSeleccionado) {
        this.registroSeleccionado = registroSeleccionado;
    }

    /**
     * Referencia al Bean de Solicitud de asignación de códigos.
     * @return SolicitudAsignacionCpsnBean
     */
    public SolicitudAsignacionCpsnBean getSolicitudAsignacionCpsnBean() {
        return solicitudAsignacionCpsnBean;
    }

    /**
     * Referencia al Bean de Solicitud de asignación de códigos.
     * @param solicitudAsignacionCpsnBean SolicitudAsignacionCpsnBean
     */
    public void setSolicitudAsignacionCpsnBean(SolicitudAsignacionCpsnBean solicitudAsignacionCpsnBean) {
        this.solicitudAsignacionCpsnBean = solicitudAsignacionCpsnBean;
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
