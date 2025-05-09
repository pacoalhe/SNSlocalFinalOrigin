package mx.ift.sns.web.backend.cpsi.asignacion;

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
import mx.ift.sns.modelo.cpsi.SolicitudAsignacionCpsi;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCPSI;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.negocio.cpsi.ICodigoCPSIFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.lazymodels.SolicitudAsignacionCpsiConsultaLazyModel;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Bean asociado a la búsqueda de Solicitudes de Asignación de CPSI. */
@ManagedBean(name = "consultarAsignacionesCpsiBean")
@ViewScoped
public class ConsultarAsignacionesCpsiBean implements Serializable {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarAsignacionesCpsiBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_AsignacionesCPSI";

    /** Facade de Servicios para Códigos de Puntos de Señalización Nacional. */
    @EJB(mappedName = "CodigoCPSIFacade")
    private ICodigoCPSIFacade cpsiFacade;

    /** Referencia al Bean de Solicitud de Asignación CPSI. */
    @ManagedProperty("#{solicitudAsignacionCpsiBean}")
    private SolicitudAsignacionCpsiBean solicitudAsignacionBean;

    /** Identificador de solicitud. */
    private String consecutivo;

    /** Proveedor seleccionado para la búsqueda. */
    private Proveedor proveedorSeleccionado;

    /** Catálogo de proveedores. */
    private List<Proveedor> listaProveedores;

    /** Referencia de la solicitud. */
    private String referenciaSolicitud;

    /** Estatus de solicitud seleccionado para la búsqueda. */
    private EstadoSolicitud estatusSeleccionado;

    /** Catálogo de Estatus de solicitud. */
    private List<EstadoSolicitud> listaEstatus;

    /** Solicitud seleccionada en la tabla de búsqueda de solicitudes. */
    private DetalleConsultaAsignacionCpsi solicitudSeleccionada;

    /** Modelo Lazy para carga de solicitudes de Asignación CPSI. */
    private SolicitudAsignacionCpsiConsultaLazyModel solicitudesAsignacionModel;

    /** Filtros de búsqueda de Solicitudes. */
    private FiltroBusquedaSolicitudesCPSI filtros;

    /** Rango Inicial Fecha de Solicitud. */
    private Date fechaSolDesde;

    /** Rango Final Fecha de Solicitud. */
    private Date fechaSolHasta;

    /** Rango Inicial Fecha de Asignación. */
    private Date fechaAsigDesde;

    /** Rango Final Fecha de Asignación. */
    private Date fechaAsigHasta;

    /** Búsqueda por oficio del proveedor solicitante. */
    private String oficioPstSolicitante = "";

    /** Prefijo para la búsqueda de oficios. */
    private String prefijoOficio;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Constructor, por defecto vacío. */
    public ConsultarAsignacionesCpsiBean() {
    }

    /** JSR260, Método invocado al cargar terminar de instanciar el bean. */
    @PostConstruct
    public void init() {
        try {
            // Listado de proveedores de servicio
            listaProveedores = cpsiFacade.findAllProveedores();
            proveedorSeleccionado = new Proveedor();

            // Listado de estado de solicitudes
            listaEstatus = cpsiFacade.findAllEstadosSolicitud();
            estatusSeleccionado = new EstadoSolicitud();
            estatusSeleccionado.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);

            // Carga Lazy y Filtros de búsqueda.
            filtros = new FiltroBusquedaSolicitudesCPSI();
            filtros.setEstado(estatusSeleccionado);

            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(cpsiFacade,
                    Parametro.REGISTROS_POR_PAGINA_BACK));
            registroPorPagina = filtros.getResultadosPagina();

            solicitudesAsignacionModel = new SolicitudAsignacionCpsiConsultaLazyModel();
            solicitudesAsignacionModel.setFiltros(filtros);
            solicitudesAsignacionModel.setFacade(cpsiFacade);

            // Reseteamos el Estado para que no esté preseleccionado.
            estatusSeleccionado = null;

            // Plantilla de oficio PST Solicitante
            prefijoOficio = cpsiFacade.getParamByName(Parametro.PREFIJO);
            oficioPstSolicitante = new String(prefijoOficio);

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar el botón 'buscar' sobre el buscador de solicitudes. */
    public void realizarBusqueda() {
        try {
            if (solicitudesAsignacionModel == null) {
                filtros = new FiltroBusquedaSolicitudesCPSI();
                solicitudesAsignacionModel = new SolicitudAsignacionCpsiConsultaLazyModel();
                solicitudesAsignacionModel.setFiltros(filtros);
                solicitudesAsignacionModel.setFacade(cpsiFacade);
            }

            // Resetamos los filtros
            filtros.clear();
            solicitudesAsignacionModel.clear();

            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(cpsiFacade,
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
            filtros.setFechaSolicitudDesde(fechaSolDesde);
            filtros.setFechaSolicitudHasta(fechaSolHasta);
            filtros.setFechaAsignacionDesde(fechaAsigDesde);
            filtros.setFechaAsignacionHasta(fechaAsigHasta);
            filtros.setProveedor(proveedorSeleccionado);
            filtros.setEstado(estatusSeleccionado);

            // Oficio del PST Solicitante
            if (!StringUtils.isEmpty(oficioPstSolicitante) && !oficioPstSolicitante.equals(prefijoOficio)) {
                filtros.setOficioPstSolicitante(oficioPstSolicitante);
            }

            PaginatorUtil.resetPaginacion("FRM_ConsultarAsignacionesCPSI:TBL_SolicitudesAsignacionCPSI",
                    filtros.getResultadosPagina());

            // Si ha seleccionado fechas comprobamos que sean coherentes (VL1)
            if ((fechaSolDesde != null && fechaSolHasta != null)
                    && (fechaSolHasta.before(fechaSolDesde))) {
                MensajesBean.addErrorMsg(MSG_ID, "La fecha final debe ser mayor o igual a la fecha de inicio", "");
            }

            if ((fechaAsigDesde != null && fechaAsigHasta != null)
                    && (fechaAsigHasta.before(fechaAsigDesde))) {
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
        this.referenciaSolicitud = null;
        this.estatusSeleccionado = null;
        this.fechaAsigHasta = null;
        this.fechaAsigDesde = null;
        this.fechaSolDesde = null;
        this.fechaSolHasta = null;
        this.oficioPstSolicitante = new String(prefijoOficio);
        this.solicitudesAsignacionModel = null;
        this.solicitudSeleccionada = null;
    }

    /** Método invocado al pulsar sobre el botón 'Cancelar' en la tabla de solicitudes. */
    public void eliminarSolicitud() {
        try {
            if (solicitudSeleccionada != null) {
                if (solicitudSeleccionada.isCancelarDisponible()) {
                    SolicitudAsignacionCpsi solicitud = cpsiFacade.getSolicitudAsignacionById(
                            new BigDecimal(solicitudSeleccionada.getConsecutivo()));

                    String consecutivo = solicitudSeleccionada.getConsecutivo();
                    PeticionCancelacion cancelacion = cpsiFacade.cancelSolicitudAsignacion(solicitud);
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
            if (solicitudAsignacionBean != null) {
                String consecutivo = pSolicitud.getId() != null ? pSolicitud.getId().toString() : "";
                PeticionCancelacion cancelacion =
                        cpsiFacade.cancelSolicitudAsignacion((SolicitudAsignacionCpsi) pSolicitud);
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
            MensajesBean.addErrorMsg(pMensajes, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar sobre el botón 'Editar' en la tabla de solicitudes. */
    public void editarSolicitud() {
        try {
            if (solicitudSeleccionada != null && solicitudAsignacionBean != null) {
                SolicitudAsignacionCpsi solicitud =
                        cpsiFacade.getSolicitudAsignacionById(new BigDecimal(solicitudSeleccionada.getConsecutivo()));
                solicitudAsignacionBean.cargarSolicitud(solicitud);
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
            if (solicitudAsignacionBean != null) {
                solicitudAsignacionBean.cargarSolicitud((SolicitudAsignacionCpsi) pSolicitud);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
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
     * Identificador de la referencia de la solicitud.
     * @return referenciaSolicitud
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
     * @return DetalleConsultaAsignacionCpsi
     */
    public DetalleConsultaAsignacionCpsi getSolicitudSeleccionada() {
        return solicitudSeleccionada;
    }

    /**
     * Registro seleccionado en la tabla de consultas.
     * @param solicitudSeleccionada DetalleConsultaAsignacionCpsi
     */
    public void setSolicitudSeleccionada(DetalleConsultaAsignacionCpsi solicitudSeleccionada) {
        this.solicitudSeleccionada = solicitudSeleccionada;
    }

    /**
     * @return the solicitudAsignacionBean
     */
    public SolicitudAsignacionCpsiBean getSolicitudAsignacionBean() {
        return solicitudAsignacionBean;
    }

    /**
     * @param solicitudAsignacionBean the solicitudAsignacionBean to set
     */
    public void setSolicitudAsignacionBean(SolicitudAsignacionCpsiBean solicitudAsignacionBean) {
        this.solicitudAsignacionBean = solicitudAsignacionBean;
    }

    /**
     * Rango Final Fecha de Asignación.
     * @return Date
     */
    public Date getFechaAsigDesde() {
        return fechaAsigDesde;
    }

    /**
     * Rango Final Fecha de Asignación.
     * @param fechaAsigDesde Date
     */
    public void setFechaAsigDesde(Date fechaAsigDesde) {
        this.fechaAsigDesde = fechaAsigDesde;
    }

    /**
     * Rango Final Fecha de Asignación.
     * @return Date
     */
    public Date getFechaAsigHasta() {
        return fechaAsigHasta;
    }

    /**
     * Rango Final Fecha de Asignación.
     * @param fechaAsigHasta Date
     */
    public void setFechaAsigHasta(Date fechaAsigHasta) {
        this.fechaAsigHasta = fechaAsigHasta;
    }

    /**
     * Modelo Lazy para carga de solicitudes de Asignación CPSI.
     * @return SolicitudAsignacionCpsiConsultaLazyModel
     */
    public SolicitudAsignacionCpsiConsultaLazyModel getSolicitudesAsignacionModel() {
        return solicitudesAsignacionModel;
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
     * Catálogo de Estatus de solicitud.
     * @return List
     */
    public List<EstadoSolicitud> getListaEstatus() {
        return listaEstatus;
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
