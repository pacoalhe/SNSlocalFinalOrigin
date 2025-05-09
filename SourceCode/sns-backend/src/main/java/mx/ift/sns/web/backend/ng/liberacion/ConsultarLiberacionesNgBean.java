package mx.ift.sns.web.backend.ng.liberacion;

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
import mx.ift.sns.modelo.ng.SolicitudLiberacionNg;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.lazymodels.SolicitudLiberacionNgConsultaLazyModel;
import mx.ift.sns.web.backend.ng.cesion.ConsultarCesionesNgBean;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Backing Bean para el buscador de Solicitudes de Liberación de Numeración Geográfica.
 */
@ManagedBean(name = "consultarLiberacionesNgBean")
@ViewScoped
public class ConsultarLiberacionesNgBean implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarCesionesNgBean.class);

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultarLiberaciones";

    /** Servicio de Numeración Geográfica. */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    /** Referencia al Bean de Solicitud de Liberacion. */
    @ManagedProperty("#{solicitudLiberacionNgBean}")
    private SolicitudLiberacionNgBean solicitudLiberacionBean;

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
    private DetalleConsultaLiberacionNg solicitudSeleccionada;

    /** Modelo Lazy para carga de solicitudes de liberación. */
    private SolicitudLiberacionNgConsultaLazyModel solicitudesLiberacionModel;

    /** Filtros de búsqueda de Solicitudes. */
    private FiltroBusquedaSolicitudes filtros;

    /** Rango Inicial Fecha de Solicitud. */
    private Date fechaSolDesde;

    /** Rango Final Fecha de Solicitud. */
    private Date fechaSolHasta;

    /** Indica si el campo 'Fecha Hasta' de Solicitud esta habilitado. */
    private boolean fechaSolHastaHabilitado;

    /** Rango Inicial Fecha de Liberación. */
    private Date fechaLibDesde;

    /** Rango Final Fecha de Liberación. */
    private Date fechaLibHasta;

    /** Indica si el campo 'Fecha Hasta' de Liberación esta habilitado. */
    private boolean fechaLibHastaHabilitado;

    /** Identificador de código de región. */
    private String cdgNir;

    /** Identificador de numeración de serie. */
    private String sna;

    /** Búsqueda por oficio del proveedor solicitante. */
    private String oficioPstSolicitante = "";

    /** Prefijo para la búsqueda de oficios. */
    private String prefijoOficio;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Constructor, por defecto vacío. */
    public ConsultarLiberacionesNgBean() {
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

            solicitudesLiberacionModel = new SolicitudLiberacionNgConsultaLazyModel();
            solicitudesLiberacionModel.setFiltros(filtros);
            solicitudesLiberacionModel.setService(ngService);

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
            if (solicitudesLiberacionModel == null) {
                filtros = new FiltroBusquedaSolicitudes();
                solicitudesLiberacionModel = new SolicitudLiberacionNgConsultaLazyModel();
                solicitudesLiberacionModel.setFiltros(filtros);
                solicitudesLiberacionModel.setService(ngService);
            }

            // Resetamos los filtros
            filtros.clear();
            solicitudesLiberacionModel.clear();

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
            filtros.setNumIniciofchSolicitud(fechaSolDesde);
            filtros.setNumFinalfchSolicitud(fechaSolHasta);
            filtros.setFechaLiberacionDesde(fechaLibDesde);
            filtros.setFechaLiberacionHasta(fechaLibHasta);
            filtros.setProveedor(proveedorSeleccionado);
            filtros.setEstado(estadoSeleccionado);
            if (!StringUtils.isEmpty(sna)) {
                filtros.setSna(new BigDecimal(sna));
            }

            // Oficio del PST Solicitante
            if (!StringUtils.isEmpty(oficioPstSolicitante) && !oficioPstSolicitante.equals(prefijoOficio)) {
                filtros.setOficioPstSolicitante(oficioPstSolicitante);
            }

            PaginatorUtil.resetPaginacion("FRM_ConsultarLiberaciones:TBL_SolicitudesLiberacion",
                    filtros.getResultadosPagina());

            // Si ha seleccionado fechas comprobamos que sean coherentes (VL1)
            if ((fechaSolDesde != null && fechaSolHasta != null)
                    && (fechaSolHasta.before(fechaSolDesde))) {
                MensajesBean.addErrorMsg(MSG_ID, "La fecha final debe ser mayor o igual a la fecha de inicio", "");
            }

            if ((fechaLibDesde != null && fechaLibHasta != null)
                    && (fechaLibHasta.before(fechaLibDesde))) {
                MensajesBean.addErrorMsg(MSG_ID, "La fecha final debe ser mayor o igual a la fecha de inicio", "");
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
        this.fechaLibDesde = null;
        this.fechaLibHasta = null;
        this.fechaLibHastaHabilitado = false;
        this.fechaSolDesde = null;
        this.fechaSolHasta = null;
        this.fechaSolHastaHabilitado = false;
        this.oficioPstSolicitante = new String(prefijoOficio);
        this.solicitudesLiberacionModel = null;
        this.solicitudSeleccionada = null;
    }

    /** Método invocado al pulsar sobre el botón 'Cancelar' en la tabla de solicitudes. */
    public void eliminarSolicitud() {
        try {
            if (solicitudSeleccionada != null) {
                if (solicitudSeleccionada.isCancelarDisponible()) {
                    SolicitudLiberacionNg solicitud = ngService.getSolicitudLiberacionById(
                            new BigDecimal(solicitudSeleccionada.getConsecutivo()));

                    String consecutivo = solicitudSeleccionada.getConsecutivo();
                    PeticionCancelacion cancelacion = ngService.cancelSolicitudLiberacion(solicitud);
                    if (cancelacion.isCancelacionPosible()) {
                        MensajesBean.addInfoMsg(MSG_ID, "Solicitud " + consecutivo + " cancelada.", "");
                    } else {
                        MensajesBean.addErrorMsg(MSG_ID, cancelacion.getMensajeError(), "");
                    }
                } else {
                    MensajesBean.addErrorMsg(MSG_ID, "No se puede cancelar una solicitud en estado: "
                            + solicitudSeleccionada.getEstatus(), "");
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
     * @param pSolicitud solicitud
     * @param pMensajes pMensajes
     */
    public void eliminarSolicitud(Solicitud pSolicitud, String pMensajes) {
        try {
            if (solicitudLiberacionBean != null) {
                String consecutivo = pSolicitud.getId().toString();
                PeticionCancelacion cancelacion = ngService
                        .cancelSolicitudLiberacion((SolicitudLiberacionNg) pSolicitud);
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
            if (solicitudSeleccionada != null && solicitudLiberacionBean != null) {
                SolicitudLiberacionNg solicitud =
                        ngService.getSolicitudLiberacionById(new BigDecimal(solicitudSeleccionada.getConsecutivo()));
                solicitudLiberacionBean.cargarSolicitud(solicitud);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al pulsar sobre el botón 'Editar' de la consulta general de solicitudes.
     * @param pSolicitud solicitud
     */
    public void editarSolicitud(Solicitud pSolicitud) {
        try {
            if (solicitudLiberacionBean != null) {
                solicitudLiberacionBean.cargarSolicitud((SolicitudLiberacionNg) pSolicitud);
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
    public void habilitarFechaLiberacionHasta() {
        fechaLibHastaHabilitado = (fechaLibDesde != null);
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
     * Referencia al Bean de Solicitud de Liberacion.
     * @return SolicitudLiberacionNgBean
     */
    public SolicitudLiberacionNgBean getSolicitudLiberacionBean() {
        return solicitudLiberacionBean;
    }

    /**
     * Referencia al Bean de Solicitud de Liberacion.
     * @param solicitudLiberacionBean SolicitudLiberacionNgBean
     */
    public void setSolicitudLiberacionBean(SolicitudLiberacionNgBean solicitudLiberacionBean) {
        this.solicitudLiberacionBean = solicitudLiberacionBean;
    }

    /**
     * Modelo Lazy para carga de solicitudes de liberación.
     * @return SolicitudLiberacionNgConsultaLazyModel
     */
    public SolicitudLiberacionNgConsultaLazyModel getSolicitudesLiberacionModel() {
        return solicitudesLiberacionModel;
    }

    /**
     * Modelo Lazy para carga de solicitudes de liberación.
     * @param solicitudesLiberacionModel SolicitudLiberacionNgConsultaLazyModel
     */
    public void setSolicitudesLiberacionModel(SolicitudLiberacionNgConsultaLazyModel solicitudesLiberacionModel) {
        this.solicitudesLiberacionModel = solicitudesLiberacionModel;
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
     * Rango Inicial Fecha de Solicitud.
     * @return Date
     */
    public Date getFechaSolDesde() {
        return fechaSolDesde;
    }

    /**
     * Rango Inicial Fecha de Solicitud.
     * @param fechaSolDesde Date
     */
    public void setFechaSolDesde(Date fechaSolDesde) {
        this.fechaSolDesde = fechaSolDesde;
    }

    /**
     * Rango Final Fecha de Solicitud.
     * @return Date
     */
    public Date getFechaSolHasta() {
        return fechaSolHasta;
    }

    /**
     * Rango Final Fecha de Solicitud.
     * @param fechaSolHasta Date
     */
    public void setFechaSolHasta(Date fechaSolHasta) {
        this.fechaSolHasta = fechaSolHasta;
    }

    /**
     * Indica si el campo 'Fecha Hasta' de Solicitud esta habilitado.
     * @return boolean
     */
    public boolean isFechaSolHastaHabilitado() {
        return fechaSolHastaHabilitado;
    }

    /**
     * Indica si el campo 'Fecha Hasta' de Solicitud esta habilitado.
     * @param fechaSolHastaHabilitado boolean
     */
    public void setFechaSolHastaHabilitado(boolean fechaSolHastaHabilitado) {
        this.fechaSolHastaHabilitado = fechaSolHastaHabilitado;
    }

    /**
     * Rango Inicial Fecha de Liberación.
     * @return Date
     */
    public Date getFechaLibDesde() {
        return fechaLibDesde;
    }

    /**
     * Rango Inicial Fecha de Liberación.
     * @param fechaLibDesde Date
     */
    public void setFechaLibDesde(Date fechaLibDesde) {
        this.fechaLibDesde = fechaLibDesde;
    }

    /**
     * Rango Final Fecha de Liberación.
     * @return Date
     */
    public Date getFechaLibHasta() {
        return fechaLibHasta;
    }

    /**
     * Rango Final Fecha de Liberación.
     * @param fechaLibHasta Date
     */
    public void setFechaLibHasta(Date fechaLibHasta) {
        this.fechaLibHasta = fechaLibHasta;
    }

    /**
     * Indica si el campo 'Fecha Hasta' de Liberación esta habilitado.
     * @return boolean
     */
    public boolean isFechaLibHastaHabilitado() {
        return fechaLibHastaHabilitado;
    }

    /**
     * Indica si el campo 'Fecha Hasta' de Liberación esta habilitado.
     * @param fechaLibHastaHabilitado boolean
     */
    public void setFechaLibHastaHabilitado(boolean fechaLibHastaHabilitado) {
        this.fechaLibHastaHabilitado = fechaLibHastaHabilitado;
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
     * @return DetalleConsultaLiberacionNg
     */
    public DetalleConsultaLiberacionNg getSolicitudSeleccionada() {
        return solicitudSeleccionada;
    }

    /**
     * Registro seleccionado en la tabla de consultas.
     * @param solicitudSeleccionada DetalleConsultaLiberacionNg
     */
    public void setSolicitudSeleccionada(DetalleConsultaLiberacionNg solicitudSeleccionada) {
        this.solicitudSeleccionada = solicitudSeleccionada;
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
