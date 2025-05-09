package mx.ift.sns.web.backend.cpsi.solicitud;

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
import mx.ift.sns.modelo.cpsi.SolicitudCpsiUit;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCPSI;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.negocio.cpsi.ICodigoCPSIFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.lazymodels.SolicitudCpsiUitConsultaLazyModel;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Bean asociado a la búsqueda de Solicitudes de CPSI a la UIT. */
@ManagedBean(name = "consultarSolicitudesCpsiUitBean")
@ViewScoped
public class ConsultarSolicitudesCpsiUitBean implements Serializable {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarSolicitudesCpsiUitBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_SolicitudCPSI";

    /** Facade de Servicios para Códigos de Puntos de Señalización Nacional. */
    @EJB(mappedName = "CodigoCPSIFacade")
    private ICodigoCPSIFacade cpsiFacade;

    /** Referencia al Bean de Solicitud de Asignación CPSI. */
    @ManagedProperty("#{solicitudCpsiUitBean}")
    private SolicitudCpsiUitBean solicitudCpsiUitBean;

    /** Identificador de solicitud. */
    private String consecutivo;

    /** Referencia de la solicitud. */
    private String referenciaSolicitud;

    /** Estatus de solicitud seleccionado para la búsqueda. */
    private EstadoSolicitud estatusSeleccionado;

    /** Catálogo de Estatus de solicitud. */
    private List<EstadoSolicitud> listaEstatus;

    /** Solicitud seleccionada en la tabla de búsqueda de solicitudes. */
    private DetalleConsultaSolicitudCpsiUit solicitudSeleccionada;

    /** Modelo Lazy para carga de solicitud CPSI. */
    private SolicitudCpsiUitConsultaLazyModel solicitudModel;

    /** Filtros de búsqueda de Solicitudes. */
    private FiltroBusquedaSolicitudesCPSI filtros;

    /** Rango Inicial Fecha de Solicitud. */
    private Date fechaSolDesde;

    /** Rango Final Fecha de Solicitud. */
    private Date fechaSolHasta;

    /** Búsqueda por oficio del proveedor solicitante. */
    private String oficioPstSolicitante = "";

    /** Prefijo para la búsqueda de oficios. */
    private String prefijoOficio;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Constructor, por defecto vacío. */
    public ConsultarSolicitudesCpsiUitBean() {
    }

    /** Método invocado al cargar terminar de instanciar el bean. */
    @PostConstruct
    public void init() {
        try {

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

            solicitudModel = new SolicitudCpsiUitConsultaLazyModel();
            solicitudModel.setFiltros(filtros);
            solicitudModel.setFacade(cpsiFacade);

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
            if (solicitudModel == null) {
                filtros = new FiltroBusquedaSolicitudesCPSI();
                solicitudModel = new SolicitudCpsiUitConsultaLazyModel();
                solicitudModel.setFiltros(filtros);
                solicitudModel.setFacade(cpsiFacade);
            }

            // Resetamos los filtros
            filtros.clear();
            solicitudModel.clear();

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
            filtros.setEstado(estatusSeleccionado);

            // Oficio del PST Solicitante
            if (!StringUtils.isEmpty(oficioPstSolicitante) && !oficioPstSolicitante.equals(prefijoOficio)) {
                filtros.setOficioPstSolicitante(oficioPstSolicitante);
            }

            PaginatorUtil.resetPaginacion("FRM_ConsultarSolicitudCPSI:TBL_SolicitudCPSI",
                    filtros.getResultadosPagina());

            // Si ha seleccionado fechas comprobamos que sean coherentes (VL1)
            if ((fechaSolDesde != null && fechaSolHasta != null)
                    && (fechaSolHasta.before(fechaSolDesde))) {
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
        this.referenciaSolicitud = null;
        this.estatusSeleccionado = null;
        this.fechaSolDesde = null;
        this.fechaSolHasta = null;
        this.oficioPstSolicitante = new String(prefijoOficio);
        this.solicitudModel = null;
        this.solicitudSeleccionada = null;
    }

    /** Método invocado al pulsar sobre el botón 'Cancelar' en la tabla de solicitudes. */
    public void eliminarSolicitud() {
        try {
            if (solicitudSeleccionada != null) {
                if (solicitudSeleccionada.isCancelarDisponible()) {
                    SolicitudCpsiUit solicitud = cpsiFacade.getSolicitudCpsiUitById(
                            new BigDecimal(solicitudSeleccionada.getConsecutivo()));

                    String consecutivo = solicitudSeleccionada.getConsecutivo();
                    PeticionCancelacion cancelacion = cpsiFacade.cancelSolicitud(solicitud);
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
            if (solicitudCpsiUitBean != null) {
                String consecutivo = pSolicitud.getId() != null ? pSolicitud.getId().toString() : "";
                PeticionCancelacion cancelacion =
                        cpsiFacade.cancelSolicitud((SolicitudCpsiUit) pSolicitud);
                if (cancelacion.isCancelacionPosible()) {
                    MensajesBean.addInfoMsg(pMensajes, "Solicitud " + consecutivo + " cancelada.", "");
                } else {
                    StringBuffer sbMensaje = new StringBuffer();
                    sbMensaje.append("No se ha podido cancelar la solicitud ").append(consecutivo).append(".  <br>");
                    sbMensaje.append(cancelacion.getMensajeError());
                    MensajesBean.addErrorMsg(pMensajes, sbMensaje.toString(), "");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(pMensajes, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar sobre el botón 'Editar' en la tabla de solicitudes. */
    public void editarSolicitud() {
        try {
            if (solicitudSeleccionada != null && solicitudCpsiUitBean != null) {
                SolicitudCpsiUit solicitud =
                        cpsiFacade.getSolicitudCpsiUitById(new BigDecimal(solicitudSeleccionada.getConsecutivo()));
                solicitudCpsiUitBean.cargarSolicitud(solicitud);
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
            if (solicitudCpsiUitBean != null) {
                solicitudCpsiUitBean.cargarSolicitud((SolicitudCpsiUit) pSolicitud);
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
     * Referencia al Bean de Solicitud de códigos.
     * @return SolicitudCpsiUitBean
     */
    public SolicitudCpsiUitBean getSolicitudCpsiUitBean() {
        return solicitudCpsiUitBean;
    }

    /**
     * Referencia al Bean de Solicitud de códigos.
     * @param solicitudCpsiUitBean SolicitudCpsiUitBean
     */
    public void setSolicitudCpsiUitBean(SolicitudCpsiUitBean solicitudCpsiUitBean) {
        this.solicitudCpsiUitBean = solicitudCpsiUitBean;
    }

    /**
     * Registro seleccionado en la tabla de consultas.
     * @return DetalleConsultaSolicitudCpsiUit
     */
    public DetalleConsultaSolicitudCpsiUit getSolicitudSeleccionada() {
        return solicitudSeleccionada;
    }

    /**
     * Registro seleccionado en la tabla de consultas.
     * @param solicitudSeleccionada DetalleConsultaSolicitudCpsiUit
     */
    public void setSolicitudSeleccionada(DetalleConsultaSolicitudCpsiUit solicitudSeleccionada) {
        this.solicitudSeleccionada = solicitudSeleccionada;
    }

    /**
     * Modelo Lazy para carga de solicitudes de códigos.
     * @return SolicitudCpsiUitConsultaLazyModel
     */
    public SolicitudCpsiUitConsultaLazyModel getSolicitudModel() {
        return solicitudModel;
    }

    /**
     * Modelo Lazy para carga de solicitudes de códigos.
     * @param solicitudModel SolicitudCpsiUitConsultaLazyModel
     */
    public void setSolicitudModel(SolicitudCpsiUitConsultaLazyModel solicitudModel) {
        this.solicitudModel = solicitudModel;
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
     * @return List<EstadoSolicitud>
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
