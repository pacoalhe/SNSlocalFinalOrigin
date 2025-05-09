package mx.ift.sns.web.backend.ng.redistribucion;

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
import mx.ift.sns.modelo.ng.SolicitudRedistribucionNg;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.lazymodels.SolicitudRedistNgConsultaLazyModel;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Backing Bean para el buscador de Solicitudes de Redistribución de Numeración Geográfica.
 */
@ManagedBean(name = "consultarRedistribucionesNgBean")
@ViewScoped
public class ConsultarRedistribucionesNgBean implements Serializable {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarRedistribucionesNgBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultarRedist";

    /** Servicio de Numeración Geográfica. */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    /** Referencia al Bean de Solicitud de Redistribucion. */
    @ManagedProperty("#{solicitudRedistribucionNgBean}")
    private SolicitudRedistribucionNgBean solicitudRedistribucionBean;

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
    private DetalleConsultaRedistNg registroSeleccionado;

    /** Filtros de búsqueda de Solicitudes. */
    private FiltroBusquedaSolicitudes filtros;

    /** Modelo Lazy para carga de solicitudes de redistribucion. */
    private SolicitudRedistNgConsultaLazyModel solicitudesRedistribucionModel;

    /** Identificador de código de región. */
    private String cdgNir;

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

    /** Constructor, por defecto vacío. */
    public ConsultarRedistribucionesNgBean() {
    }

    /** JSR260, Método invocado al cargar terminar de instanciar el bean. */
    @PostConstruct
    public void init() {
        try {
            // Listado de proveedores de servicio
            listaProveedores = ngService.findAllProveedoresActivos();
            proveedorSeleccionado = new Proveedor();

            // Listado de estado de solicitudes
            listaEstados = ngService.findAllEstadosSolicitud();
            estadoSeleccionado = new EstadoSolicitud();
            estadoSeleccionado.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);

            // Carga Lazy y Filtros de búsqueda.
            filtros = new FiltroBusquedaSolicitudes();
            filtros.setEstado(estadoSeleccionado);

            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(ngService,
                    Parametro.REGISTROS_POR_PAGINA_BACK));
            registroPorPagina = filtros.getResultadosPagina();

            solicitudesRedistribucionModel = new SolicitudRedistNgConsultaLazyModel();
            solicitudesRedistribucionModel.setFiltros(filtros);
            solicitudesRedistribucionModel.setService(ngService);

            // Reseteamos el Estado para que no esté preseleccionado.
            estadoSeleccionado = null;

            // Plantilla de oficio PST Solicitante
            prefijoOficio = ngService.getParamByName(Parametro.PREFIJO);
            oficioPstSolicitante = new String(prefijoOficio);

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
                solicitudesRedistribucionModel = new SolicitudRedistNgConsultaLazyModel();
                solicitudesRedistribucionModel.setFiltros(filtros);
                solicitudesRedistribucionModel.setService(ngService);
            }

            // Resetamos los filtros
            filtros.clear();
            solicitudesRedistribucionModel.clear();

            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(ngService,
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

            // Las búsquedas se hacen por el ID, no por el código.
            if (!StringUtils.isEmpty(cdgNir)) {
                Nir n = ngService.getNirByCodigo(Integer.parseInt(cdgNir));
                if (n != null) {
                    filtros.setIdNir(n.getId());
                } else {
                    MensajesBean.addErrorMsg(MSG_ID, "Nir inexistente en el sistema", "");
                    // Ponemos un valor de NIR inexistente para que no muestre resultados en la búsqueda.
                    filtros.setIdNir(new BigDecimal(-1));
                }
            }

            // El resto de parámetros si no se han seleccionado son nulos
            filtros.setProveedor(proveedorSeleccionado);
            filtros.setNumIniciofchSolicitud(fechaSolDesde);
            filtros.setNumFinalfchSolicitud(fechaSolHasta);
            filtros.setFechaRedistribucionDesde(fechaRedistDesde);
            filtros.setFechaRedistribucionHasta(fechaRedistHasta);
            filtros.setProveedor(proveedorSeleccionado);
            filtros.setEstado(estadoSeleccionado);
            if (!StringUtils.isEmpty(sna)) {
                filtros.setSna(new BigDecimal(sna));
            }

            PaginatorUtil.resetPaginacion("FRM_ConsultarSolicitudes:TBL_Solicitudes",
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
        this.cdgNir = null;
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

                    SolicitudRedistribucionNg solicitud = ngService.getSolicitudRedistribucionById(
                            new BigDecimal(registroSeleccionado.getConsecutivo()));

                    String consecutivo = registroSeleccionado.getConsecutivo();
                    PeticionCancelacion cancelacion = ngService.cancelSolicitudRedistribucion(solicitud);
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
                String consecutivo = pSolicitud.getId().toString();
                PeticionCancelacion cancelacion = ngService
                        .cancelSolicitudRedistribucion((SolicitudRedistribucionNg) pSolicitud);
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
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar sobre el botón 'Editar' en la tabla de solicitudes. */
    public void editarSolicitud() {
        try {
            if (registroSeleccionado != null && solicitudRedistribucionBean != null) {
                SolicitudRedistribucionNg solicitud =
                        ngService.getSolicitudRedistribucionById(new BigDecimal(registroSeleccionado.getConsecutivo()));
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
                solicitudRedistribucionBean.cargarSolicitud((SolicitudRedistribucionNg) pSolicitud);
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
     * Catálogo de proveedores.
     * @param listaProveedores List
     */
    public void setListaProveedores(List<Proveedor> listaProveedores) {
        this.listaProveedores = listaProveedores;
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
     * Catálogo de estados de solicitud.
     * @param listaEstados List
     */
    public void setListaEstados(List<EstadoSolicitud> listaEstados) {
        this.listaEstados = listaEstados;
    }

    /**
     * Identificador de código de región.
     * @return String
     */
    public String getCdgNir() {
        return cdgNir;
    }

    /**
     * Identificador de código de región.
     * @param cdgNir String
     */
    public void setCdgNir(String cdgNir) {
        this.cdgNir = cdgNir;
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
     * Referencia al Bean de Solicitud de Redistribucion.
     * @return solicitudRedistribucionBean
     */
    public SolicitudRedistribucionNgBean getSolicitudRedistribucionBean() {
        return solicitudRedistribucionBean;
    }

    /**
     * Referencia al Bean de Solicitud de Redistribucion.
     * @param solicitudRedistribucionBean solicitudRedistribucionBean to set
     */
    public void setSolicitudRedistribucionBean(SolicitudRedistribucionNgBean solicitudRedistribucionBean) {
        this.solicitudRedistribucionBean = solicitudRedistribucionBean;
    }

    /**
     * Solicitud seleccionada en la tabla de búsqueda de solicitudes.
     * @return registroSeleccionado
     */
    public DetalleConsultaRedistNg getRegistroSeleccionado() {
        return registroSeleccionado;
    }

    /**
     * Solicitud seleccionada en la tabla de búsqueda de solicitudes.
     * @param registroSeleccionado registroSeleccionado to set
     */
    public void setRegistroSeleccionado(DetalleConsultaRedistNg registroSeleccionado) {
        this.registroSeleccionado = registroSeleccionado;
    }

    /**
     * Modelo Lazy para carga de solicitudes de redistribucion.
     * @return solicitudesRedistribucionModel
     */
    public SolicitudRedistNgConsultaLazyModel getSolicitudesRedistribucionModel() {
        return solicitudesRedistribucionModel;
    }

    /**
     * Modelo Lazy para carga de solicitudes de redistribucion.
     * @param solicitudesRedistribucionModel solicitudesRedistribucionModel to set
     */
    public void setSolicitudesRedistribucionModel(SolicitudRedistNgConsultaLazyModel solicitudesRedistribucionModel) {
        this.solicitudesRedistribucionModel = solicitudesRedistribucionModel;
    }

    /**
     * Rango inicio fecha de Solicitud.
     * @return fechaRedistDesde
     */
    public Date getFechaRedistDesde() {
        return fechaRedistDesde;
    }

    /**
     * Rango inicio fecha de Solicitud.
     * @param fechaRedistDesde fechaRedistDesde to set
     */
    public void setFechaRedistDesde(Date fechaRedistDesde) {
        this.fechaRedistDesde = fechaRedistDesde;
    }

    /**
     * Rango final fecha de Solicitud.
     * @return fechaRedistHasta
     */
    public Date getFechaRedistHasta() {
        return fechaRedistHasta;
    }

    /**
     * Rango final fecha de Solicitud.
     * @param fechaRedistHasta fechaRedistHasta to set
     */
    public void setFechaRedistHasta(Date fechaRedistHasta) {
        this.fechaRedistHasta = fechaRedistHasta;
    }

    /**
     * Indica si el campo 'Fecha Hasta' de Redistribución está habilitado.
     * @return fechaRedistHastaHabilitada
     */
    public boolean isFechaRedistHastaHabilitada() {
        return fechaRedistHastaHabilitada;
    }

    /**
     * Indica si el campo 'Fecha Hasta' de Redistribución está habilitado.
     * @param fechaRedistHastaHabilitada fechaRedistHastaHabilitada to set
     */
    public void setFechaRedistHastaHabilitada(boolean fechaRedistHastaHabilitada) {
        this.fechaRedistHastaHabilitada = fechaRedistHastaHabilitada;
    }

    /**
     * Rango inicio fecha de Redistribución.
     * @return fechaSolDesde
     */
    public Date getFechaSolDesde() {
        return fechaSolDesde;
    }

    /**
     * Rango inicio fecha de Redistribución.
     * @param fechaSolDesde fechaSolDesde to set
     */
    public void setFechaSolDesde(Date fechaSolDesde) {
        this.fechaSolDesde = fechaSolDesde;
    }

    /**
     * Rango final fecha de Redistribución.
     * @return fechaSolHasta
     */
    public Date getFechaSolHasta() {
        return fechaSolHasta;
    }

    /**
     * Rango final fecha de Redistribución.
     * @param fechaSolHasta fechaSolHasta to set
     */
    public void setFechaSolHasta(Date fechaSolHasta) {
        this.fechaSolHasta = fechaSolHasta;
    }

    /**
     * Indica si el campo 'Fecha Hasta' de Solicitud está habilitado.
     * @return fechaSolHastaHabilitado
     */
    public boolean isFechaSolHastaHabilitado() {
        return fechaSolHastaHabilitado;
    }

    /**
     * Indica si el campo 'Fecha Hasta' de Solicitud está habilitado.
     * @param fechaSolHastaHabilitado fechaSolHastaHabilitado to set
     */
    public void setFechaSolHastaHabilitado(boolean fechaSolHastaHabilitado) {
        this.fechaSolHastaHabilitado = fechaSolHastaHabilitado;
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
