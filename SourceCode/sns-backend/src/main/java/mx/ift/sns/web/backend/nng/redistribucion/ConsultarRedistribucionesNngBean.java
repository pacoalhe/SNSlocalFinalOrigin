package mx.ift.sns.web.backend.nng.redistribucion;

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
import mx.ift.sns.modelo.nng.SolicitudRedistribucionNng;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.lazymodels.SolicitudRedistNngConsultaLazyModel;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestiona el buscador de Solicitudes de Redistribución de Numeración no Geográfica.
 */
@ManagedBean(name = "consultarRedistribucionesNngBean")
@ViewScoped
public class ConsultarRedistribucionesNngBean implements Serializable {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarRedistribucionesNngBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultarRedistNNG";

    /** Servicio de Numeración No Geográfica. */
    @EJB(mappedName = "NumeracionNoGeograficaFacade")
    private INumeracionNoGeograficaFacade nngFacade;

    /** Referencia al Bean de Solicitud de Redistribucion. */
    @ManagedProperty("#{solicitudRedistribucionNngBean}")
    private SolicitudRedistribucionNngBean solicitudRedistribucionBean;

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
    private DetalleConsultaRedistNng registroSeleccionado;

    /** Filtros de búsqueda de Solicitudes. */
    private FiltroBusquedaSolicitudes filtros;

    /** Modelo Lazy para carga de solicitudes de redistribucion. */
    private SolicitudRedistNngConsultaLazyModel solicitudesRedistribucionModel;

    /** Identificador clave de servicio. */
    private BigDecimal claveServicio;

    /** Listado de claves de servicio. */
    private List<ClaveServicio> listaClavesServicio;

    /** Identificador de numeración de serie. */
    private String sna;

    /** Rango inicio fecha de Solicitud. */
    private Date fechaRedistDesde;

    /** Rango final fecha de Solicitud. */
    private Date fechaRedistHasta;

    /** Indica si el campo 'Fecha Hasta' de Redistribución está habilitado. */
    private boolean fechaRedistHastaHabilitada = false;

    /** Rango inicio fecha de Redistribución. */
    private Date fechaSolDesde;

    /** Rango final fecha de Redistribución. */
    private Date fechaSolHasta;

    /** Indica si el campo 'Fecha Hasta' de Solicitud está habilitado. */
    private boolean fechaSolHastaHabilitado = false;

    /** Búsqueda por oficio del proveedor solicitante. */
    private String oficioPstSolicitante = "";

    /** Prefijo para la búsqueda de oficios. */
    private String prefijoOficio;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Default constructor. */
    public ConsultarRedistribucionesNngBean() {
    }

    /** JSR260, Método invocado al cargar terminar de instanciar el bean. */
    @PostConstruct
    public void init() {
        try {
            // Listado de proveedores de servicio
            listaProveedores = nngFacade.findAllProveedoresActivos();
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

            solicitudesRedistribucionModel = new SolicitudRedistNngConsultaLazyModel();
            solicitudesRedistribucionModel.setFiltros(filtros);
            solicitudesRedistribucionModel.setFacade(nngFacade);

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
            if (solicitudesRedistribucionModel == null) {
                filtros = new FiltroBusquedaSolicitudes();
                solicitudesRedistribucionModel = new SolicitudRedistNngConsultaLazyModel();
                solicitudesRedistribucionModel.setFiltros(filtros);
                solicitudesRedistribucionModel.setFacade(nngFacade);
            }

            // Resetamos los filtros
            filtros.clear();
            solicitudesRedistribucionModel.clear();

            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(nngFacade,
                    Parametro.REGISTROS_POR_PAGINA_BACK));

            // Filtro de valor de consecutivo
            if (!StringUtils.isEmpty(consecutivo)) {
                if (StringUtils.isNumeric(consecutivo) && (StringUtils.length(consecutivo) <= 9)) {
                    filtros.setConsecutivo(consecutivo);
                } else {
                    MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0007), "");
                }
            }

            // Oficio del PST Solicitante
            if (!StringUtils.isEmpty(oficioPstSolicitante) && !oficioPstSolicitante.equals(prefijoOficio)) {
                filtros.setOficioPstSolicitante(oficioPstSolicitante);
            }

            // El resto de parámetros si no se han seleccionado son nulos
            filtros.setProveedor(proveedorSeleccionado);
            filtros.setNumIniciofchSolicitud(fechaSolDesde);
            filtros.setNumFinalfchSolicitud(fechaSolHasta);
            filtros.setFechaRedistribucionDesde(fechaRedistDesde);
            filtros.setFechaRedistribucionHasta(fechaRedistHasta);
            filtros.setProveedor(proveedorSeleccionado);
            filtros.setEstado(estadoSeleccionado);
            filtros.setIdClaveServicio(claveServicio);
            if (!StringUtils.isEmpty(sna)) {
                filtros.setSna(new BigDecimal(sna));
            }

            PaginatorUtil.resetPaginacion("FRM_ConsultarSolicitudesNNG:TBL_Solicitudes",
                    filtros.getResultadosPagina());

            // Si ha seleccionado fechas comprobamos que sean coherentes (VL1)
            if ((fechaSolDesde != null && fechaSolHasta != null)
                    && (fechaSolHasta.before(fechaSolDesde))) {
                MensajesBean.addErrorMsg(MSG_ID,
                        "La fecha final debe ser mayor o igual a la fecha de inicio", "");
            }

            if ((fechaRedistDesde != null && fechaRedistHasta != null)
                    && (fechaRedistHasta.before(fechaRedistDesde))) {
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
        this.claveServicio = null;
        this.sna = null;
        this.fechaRedistDesde = null;
        this.fechaRedistHasta = null;
        this.fechaRedistHastaHabilitada = false;
        this.fechaSolDesde = null;
        this.fechaSolHasta = null;
        this.fechaSolHastaHabilitado = false;
        this.oficioPstSolicitante = new String(prefijoOficio);
        this.registroSeleccionado = null;
        this.solicitudesRedistribucionModel = null;
    }

    /** Método invocado al pulsar sobre el botón 'Cancelar' en la tabla de solicitudes. */
    public void eliminarSolicitud() {
        try {
            if (registroSeleccionado != null) {
                if (registroSeleccionado.isCancelarDisponible()) {
                    SolicitudRedistribucionNng solicitud = nngFacade.getSolicitudRedistribucionById(
                            new BigDecimal(registroSeleccionado.getConsecutivo()));

                    String consecutivo = registroSeleccionado.getConsecutivo();
                    PeticionCancelacion cancelacion = nngFacade.cancelSolicitudRedistribucion(solicitud);
                    if (cancelacion.isCancelacionPosible()) {
                        MensajesBean.addInfoMsg(MSG_ID, "Solicitud " + consecutivo + " cancelada.", "");
                    } else {
                        StringBuffer sbMensaje = new StringBuffer();
                        sbMensaje.append("No se ha podido cancelar la solicitud ").append(consecutivo).append(". ");
                        sbMensaje.append(cancelacion.getMensajeError());
                        MensajesBean.addErrorMsg(MSG_ID, sbMensaje.toString(), "");
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
     * Método invocado al pulsar sobre el botón 'Cancelar' de la consulta general de solicitudes.
     * @param pSolicitud pSolicitud
     * @param pMensajes pMensajes
     */
    public void eliminarSolicitud(Solicitud pSolicitud, String pMensajes) {
        try {
            if (solicitudRedistribucionBean != null) {
                PeticionCancelacion cancelacion = nngFacade
                        .cancelSolicitudRedistribucion((SolicitudRedistribucionNng) pSolicitud);
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
            if (registroSeleccionado != null && solicitudRedistribucionBean != null) {
                SolicitudRedistribucionNng solicitud =
                        nngFacade.getSolicitudRedistribucionById(new BigDecimal(registroSeleccionado.getConsecutivo()));
                solicitudRedistribucionBean.cargarSolicitud(solicitud);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al pulsar sobre el botón 'Editar' de la consulta general de solicitudes.
     * @param pSolicitud pSolicitud
     */
    public void editarSolicitud(Solicitud pSolicitud) {
        try {
            if (solicitudRedistribucionBean != null) {
                solicitudRedistribucionBean.cargarSolicitud((SolicitudRedistribucionNng) pSolicitud);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Habilita el campo 'Fecha Hasta' de Solicitud si procede. */
    public void habilitarFechaSolicitudHasta() {
        fechaSolHastaHabilitado = (fechaSolDesde != null);
    }

    /** Habilita el campo 'Fecha Hasta' de Solicitud si procede. */
    public void habilitarFechaRedistHasta() {
        fechaRedistHastaHabilitada = (fechaRedistDesde != null);
    }

    // GETTERS & SETTERS

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
     * Registro seleccionado en la tabla de resuletados.
     * @return DetalleConsultaRedistNng
     */
    public DetalleConsultaRedistNng getRegistroSeleccionado() {
        return registroSeleccionado;
    }

    /**
     * Registro seleccionado en la tabla de resuletados.
     * @param registroSeleccionado DetalleConsultaRedistNng
     */
    public void setRegistroSeleccionado(DetalleConsultaRedistNng registroSeleccionado) {
        this.registroSeleccionado = registroSeleccionado;
    }

    /**
     * Modelo Lazy de Consulta de Redistribuciones.
     * @return the solicitudesRedistribucionModel
     */
    public SolicitudRedistNngConsultaLazyModel getSolicitudesRedistribucionModel() {
        return solicitudesRedistribucionModel;
    }

    /**
     * Rango inicio fecha de Solicitud.
     * @return Date
     */
    public Date getFechaRedistDesde() {
        return fechaRedistDesde;
    }

    /**
     * Rango inicio fecha de Solicitud.
     * @param fechaRedistDesde Date
     */
    public void setFechaRedistDesde(Date fechaRedistDesde) {
        this.fechaRedistDesde = fechaRedistDesde;
    }

    /**
     * Rango final fecha de Solicitud.
     * @return Date
     */
    public Date getFechaRedistHasta() {
        return fechaRedistHasta;
    }

    /**
     * Rango final fecha de Solicitud.
     * @param fechaRedistHasta Date
     */
    public void setFechaRedistHasta(Date fechaRedistHasta) {
        this.fechaRedistHasta = fechaRedistHasta;
    }

    /**
     * Indica si el campo 'Fecha Hasta' de Redistribución está habilitado.
     * @return boolean
     */
    public boolean isFechaRedistHastaHabilitada() {
        return fechaRedistHastaHabilitada;
    }

    /**
     * Rango inicio fecha de Redistribución.
     * @return Date
     */
    public Date getFechaSolDesde() {
        return fechaSolDesde;
    }

    /**
     * Rango inicio fecha de Redistribución.
     * @param fechaSolDesde Date
     */
    public void setFechaSolDesde(Date fechaSolDesde) {
        this.fechaSolDesde = fechaSolDesde;
    }

    /**
     * Rango final fecha de Redistribución.
     * @return Date
     */
    public Date getFechaSolHasta() {
        return fechaSolHasta;
    }

    /**
     * Rango final fecha de Redistribución.
     * @param fechaSolHasta Date
     */
    public void setFechaSolHasta(Date fechaSolHasta) {
        this.fechaSolHasta = fechaSolHasta;
    }

    /**
     * Indica si el campo 'Fecha Hasta' de Solicitud está habilitado.
     * @return boolean
     */
    public boolean isFechaSolHastaHabilitado() {
        return fechaSolHastaHabilitado;
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
     * Referencia al Bean de Solicitud de Redistribucion.
     * @return SolicitudRedistribucionNngBean
     */
    public SolicitudRedistribucionNngBean getSolicitudRedistribucionBean() {
        return solicitudRedistribucionBean;
    }

    /**
     * Referencia al Bean de Solicitud de Redistribucion.
     * @param solicitudRedistribucionBean SolicitudRedistribucionNngBean
     */
    public void setSolicitudRedistribucionBean(SolicitudRedistribucionNngBean solicitudRedistribucionBean) {
        this.solicitudRedistribucionBean = solicitudRedistribucionBean;
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
