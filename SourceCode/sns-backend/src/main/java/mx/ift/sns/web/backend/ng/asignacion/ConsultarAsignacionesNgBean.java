package mx.ift.sns.web.backend.ng.asignacion;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.ng.SolicitudAsignacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.lazymodels.SolicitudAsignacionNgLazyModel;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** ManagedBean. */
@ManagedBean(name = "consultarSolicitudesBean")
@ViewScoped
public class ConsultarAsignacionesNgBean implements Serializable {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarAsignacionesNgBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Id de errores. */
    private static final String MSG_ID = "MSG_ConsultarAsignaciones";

    /** Número de Consecutivo. */
    private String consecutivo;

    /** Proveedor seleccionado para la búsqueda. */
    private Proveedor proveedorSeleccionado;

    /** Catálogo de proveedores. */
    private List<Proveedor> listaProveedores;

    /** Número de oficio para la búsqueda. */
    private String numeroOficio;

    /** Estado de solicitud seleccionado para la búsqueda. */
    private EstadoSolicitud estadoSeleccionado;

    /** Catálogo de estados de solicitud. */
    private List<EstadoSolicitud> listaEstados;

    /** Listado de solicitudes de asignación. **/
    private List<SolicitudAsignacion> listaAsignacion;

    /** Rango inicio fecha Solicitud. */
    private Date numIniciofchSolicitud;

    /** Rango final fecha Solicitud. */
    private Date numFinalfchSolicitud;

    /** Rango inicio fecha inicio Utilizacion. */
    private Date numIniciofchIniUtilizacion;

    /** Rango final fecha inicio Utilizacion. **/
    private Date numFinalfchIniUtilizacion;

    /** Referencia de la solicitud. */
    private String referenciaSolicitud;

    /** Modelo Lazy para carga de solicitudes de asignación. */
    private SolicitudAsignacionNgLazyModel solicitudesAsignacionModel;

    /** Filtros de búsqueda de Solicitudes. */
    private FiltroBusquedaSolicitudes filtros;

    /** Solicitud seleccionada en la tabla de búsqueda de solicitudes. */
    private SolicitudAsignacion solicitudSeleccionada;

    /** Fecha Habilitada. */
    private boolean fechaHabilitada = false;

    /** Prefijo de los oficios. */
    private String prefijoOficio;

    /** Indica que se está procesando una asignación automática. */
    private boolean asignacionAutomatica = false;

    /** Constructor, por defecto vacío. */
    public ConsultarAsignacionesNgBean() {
    }

    /** Referencia al Bean de Solicitud de Asignaciones manual. */
    @ManagedProperty("#{asignacionManualBean}")
    private AsignacionManualBean asignacionManualBean;

    /** Referencia al Bean de Solicitud de Asignaciones automatica. */
    @ManagedProperty("#{asignacionAutomaticaBean}")
    private AsignacionAutomaticaBean asignacionAutomaticaBean;

    /** Servicio de Numeración Geográfica. */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /**
     * Iniciamos la listaAsignacion y la lista proveedores y cargamos los combos.
     * @throws Exception error en inicio
     **/
    @PostConstruct
    public void init() throws Exception {
        LOGGER.debug("");
        try {
            // Listado de proveedores de servicio
            LOGGER.debug(">> Llamando ngService.findAllProveedoresActivos()");
            listaProveedores = ngService.findAllProveedoresActivos();
            LOGGER.debug("<< Retorno de ngService.findAllProveedoresActivos");
            proveedorSeleccionado = new Proveedor();

            // Se inicializan las fechas
            numIniciofchSolicitud = null;
            numFinalfchSolicitud = null;
            numIniciofchIniUtilizacion = null;
            numFinalfchIniUtilizacion = null;

            // Listado de estado de solicitudes
            LOGGER.debug(">> Llamando ngService.findAllEstadosSolicitud()");
            listaEstados = ngService.findAllEstadosSolicitud();
            LOGGER.debug("<< Retorno de ngService.findAllEstadosSolicitud");
            EstadoSolicitud estadoSeleccionadoAux = new EstadoSolicitud();
            estadoSeleccionadoAux.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
            estadoSeleccionado = null;

            // Carga Lazy y Filtros de búsqueda
            filtros = new FiltroBusquedaSolicitudes();
            filtros.setEstado(estadoSeleccionadoAux);

            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(ngService,
                    Parametro.REGISTROS_POR_PAGINA_BACK));
            registroPorPagina = filtros.getResultadosPagina();

            solicitudesAsignacionModel = new SolicitudAsignacionNgLazyModel();
            solicitudesAsignacionModel.setFiltros(filtros);
            solicitudesAsignacionModel.setService(ngService);

            // Plantilla de oficio PST Solicitante
            LOGGER.debug(">> Llamando ngService.getParamByName(Parametro.PREFIJO)");
            prefijoOficio = ngService.getParamByName(Parametro.PREFIJO);
            LOGGER.debug("<< Retorno de ngService.getParamByName");
            referenciaSolicitud = new String(prefijoOficio);

        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error inesperado: " + e.getMessage());
            }
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al pulsar el botón 'buscar' sobre el buscador de solicitudes.
     */
    public void realizarBusqueda() {
        LOGGER.debug("");
        try {
            if (solicitudesAsignacionModel == null) {
                filtros = new FiltroBusquedaSolicitudes();
                solicitudesAsignacionModel = new SolicitudAsignacionNgLazyModel();
                solicitudesAsignacionModel.setFiltros(filtros);
                solicitudesAsignacionModel.setService(ngService);

            }

            // Resetamos los filtros
            filtros.clear();
            solicitudesAsignacionModel.clear();

            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(ngService,
                    Parametro.REGISTROS_POR_PAGINA_BACK));

            // Filtro de valor de consecutivo
            if (!StringUtils.isEmpty(consecutivo)) {
                if (StringUtils.isNumeric(consecutivo)) {
                    if (StringUtils.length(consecutivo) <= 9) {
                        filtros.setConsecutivo(consecutivo);
                    } else {
                        MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0007);
                    }
                } else {
                    MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0012);
                }
            }

            // Oficio del PST Solicitante
            if (!StringUtils.isEmpty(referenciaSolicitud) && !referenciaSolicitud.equals(prefijoOficio)) {
                filtros.setOficioPstSolicitante(referenciaSolicitud);
            }

            // El resto de parámetros si no se han seleccionado son nulos
            filtros.setNumIniciofchSolicitud(numIniciofchSolicitud);
            if (numIniciofchSolicitud != null && numFinalfchSolicitud == null) {
                filtros.setNumFinalfchSolicitud(numIniciofchSolicitud);
            } else {
                filtros.setNumFinalfchSolicitud(numFinalfchSolicitud);
            }

            filtros.setNumInicioFechaAsignacion(numIniciofchIniUtilizacion);
            if (numIniciofchIniUtilizacion != null && numFinalfchIniUtilizacion == null) {
                filtros.setNumFinalFechaAsignacion(numIniciofchIniUtilizacion);
            } else {
                filtros.setNumFinalFechaAsignacion(numFinalfchIniUtilizacion);
            }

            filtros.setProveedor(proveedorSeleccionado);
            filtros.setEstado(estadoSeleccionado);

            PaginatorUtil.resetPaginacion("FRM_ConsultarSolicitudes:TBLGN_resumen",
                    filtros.getResultadosPagina());

            // Comprobamos fechas de inicio y final
            if ((numIniciofchSolicitud != null && numFinalfchSolicitud != null)
                    && (numFinalfchSolicitud.before(numIniciofchSolicitud))) {
                MensajesBean.addErrorMsg(MSG_ID, "La fecha final debe ser mayor o igual a la fecha de inicio", "");
            } else if ((numIniciofchIniUtilizacion != null && numFinalfchIniUtilizacion != null)
                    && (numFinalfchIniUtilizacion.before(numIniciofchIniUtilizacion))) {
                MensajesBean.addErrorMsg(MSG_ID, "La fecha final debe ser mayor o igual a la fecha de inicio", "");
            }

        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error inesperado: " + e.getMessage());
            }
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al pulsar el botón 'limpiar' sobre el buscador de solicitudes.
     */
    public void limpiarBusqueda() {

        LOGGER.debug("");

        this.consecutivo = null;
        this.proveedorSeleccionado = null;
        this.numeroOficio = null;
        this.numIniciofchSolicitud = null;
        this.numFinalfchSolicitud = null;
        this.numIniciofchIniUtilizacion = null;
        this.numFinalfchIniUtilizacion = null;
        this.estadoSeleccionado = null;
        this.referenciaSolicitud = new String(prefijoOficio);
        filtros.clear();
        this.solicitudesAsignacionModel = null;

    }

    /**
     * Método invocado al pulsar sobre el botón 'Editar' en la tabla de solicitudes.
     * @param solicitud solicitud
     */
    public void editarSolicitud(SolicitudAsignacion solicitud) {
        try {
            if (solicitud != null && asignacionManualBean != null) {
                asignacionManualBean.cargarSolicitud(solicitud);
                asignacionAutomatica = false;
            }
        } catch (Exception e) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al pulsar sobre el botón 'Cancelar' en la tabla de solicitudes.
     * @param solicitud solicitud
     */
    public void eliminarSolicitud(SolicitudAsignacion solicitud) {
        try {
            List<RangoSerie> rangos = ngService.findNumeracionesSeleccionadas(solicitud.getId());

            if (rangos.isEmpty()) {

                EstadoSolicitud estadoSolicitud = new EstadoSolicitud();
                estadoSolicitud.setCodigo(EstadoSolicitud.SOLICITUD_CANCELADA);

                solicitud.setEstadoSolicitud(estadoSolicitud);

                solicitud = ngService.saveSolicitudAsignacion(solicitud);

                if (estadoSeleccionado == null) {
                    EstadoSolicitud estado = new EstadoSolicitud();
                    estado.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
                    estadoSeleccionado = estado;
                }
                // realizarBusqueda();
            } else {
                MensajesBean
                        .addErrorMsg(MSG_ID,
                                "No se pueden cancelar solicitudes de asignación"
                                        + " con numeraciones en estado asignada o pendiente.", "");
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);

        } catch (Exception e) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al pulsar sobre el botón 'Cancelar' en la tabla de solicitudes.
     * @param pSolicitud solicitud
     * @param pMensajes pMensajes
     */
    public void eliminarSolicitud(SolicitudAsignacion pSolicitud, String pMensajes) {
        try {
            List<RangoSerie> rangos = ngService.findNumeracionesSeleccionadas(pSolicitud.getId());

            if (rangos.isEmpty()) {

                EstadoSolicitud estadoSolicitud = new EstadoSolicitud();
                estadoSolicitud.setCodigo(EstadoSolicitud.SOLICITUD_CANCELADA);

                pSolicitud.setEstadoSolicitud(estadoSolicitud);

                pSolicitud = ngService.saveSolicitudAsignacion(pSolicitud);

                if (estadoSeleccionado == null) {
                    EstadoSolicitud estado = new EstadoSolicitud();
                    estado.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
                    estadoSeleccionado = estado;
                }
                // realizarBusqueda();
            } else {
                MensajesBean
                        .addErrorMsg(pMensajes,
                                "No se pueden cancelar solicitudes de asignación"
                                        + " con numeraciones en estado asignada o pendiente.", "");
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(pMensajes, Errores.ERROR_0015);

        } catch (Exception e) {
            MensajesBean.addErrorMsg(pMensajes, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al salir de la modal de Edición de Proveedores para actualizar el formulario con los cambios.
     */
    public void actualizarEdicionProveedor() {
        if (asignacionAutomatica) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Actualizando formulario de Asignación Automática.");
            }
            asignacionAutomaticaBean.getTabGenerales().actualizarEdicionProveedor();
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Actualizando formulario de Asignación Manual.");
            }
            asignacionManualBean.getGenerales().actualizarEdicionProveedor();
        }
    }

    /**
     * Obtiene Número de Consecutivo.
     * @return the consecutivo
     */
    public String getConsecutivo() {
        return consecutivo;
    }

    /**
     * Carga Número de Consecutivo.
     * @param consecutivo consecutivo to set
     */
    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    /**
     * Obtiene Proveedor seleccionado para la búsqueda.
     * @return proveedorSeleccionado
     */
    public Proveedor getProveedorSeleccionado() {
        return proveedorSeleccionado;
    }

    /**
     * Carga Proveedor seleccionado para la búsqueda.
     * @param proveedorSeleccionado proveedorSeleccionado to set
     */
    public void setProveedorSeleccionado(Proveedor proveedorSeleccionado) {
        this.proveedorSeleccionado = proveedorSeleccionado;
    }

    /**
     * Obtiene Catálogo de proveedores.
     * @return the listaProveedores
     */
    public List<Proveedor> getListaProveedores() {
        return listaProveedores;
    }

    /**
     * Carga Catálogo de proveedores.
     * @param listaProveedores the listaProveedores to set
     */
    public void setListaProveedores(List<Proveedor> listaProveedores) {
        this.listaProveedores = listaProveedores;
    }

    /**
     * Obtiene Número de oficio para la búsqueda.
     * @return numeroOficio
     */
    public String getNumeroOficio() {
        return numeroOficio;
    }

    /**
     * Carga Número de oficio para la búsqueda.
     * @param numeroOficio numeroOficio to set
     */
    public void setNumeroOficio(String numeroOficio) {
        this.numeroOficio = numeroOficio;
    }

    /**
     * Obtiene Estado de solicitud seleccionado para la búsqueda.
     * @return estadoSeleccionado
     */
    public EstadoSolicitud getEstadoSeleccionado() {
        return estadoSeleccionado;
    }

    /**
     * Carga Estado de solicitud seleccionado para la búsqueda.
     * @param estadoSeleccionado estadoSeleccionado to set
     */
    public void setEstadoSeleccionado(EstadoSolicitud estadoSeleccionado) {
        this.estadoSeleccionado = estadoSeleccionado;
    }

    /**
     * Obtiene Catálogo de estados de solicitud.
     * @return listaEstados
     */
    public List<EstadoSolicitud> getListaEstados() {
        return listaEstados;
    }

    /**
     * Carga Catálogo de estados de solicitud.
     * @param listaEstados listaEstados to set
     */
    public void setListaEstados(List<EstadoSolicitud> listaEstados) {
        this.listaEstados = listaEstados;
    }

    /**
     * Obtiene Listado de solicitudes de asignación.
     * @return the listaAsignacion
     */
    public List<SolicitudAsignacion> getListaAsignacion() {
        return listaAsignacion;
    }

    /**
     * Carga Listado de solicitudes de asignación.
     * @param listaAsignacion listaAsignacion to set
     */
    public void setListaAsignacion(List<SolicitudAsignacion> listaAsignacion) {
        this.listaAsignacion = listaAsignacion;
    }

    /**
     * Obtiene Rango inicio fecha Solicitud.
     * @return the numIniciofchSolicitud
     */
    public Date getNumIniciofchSolicitud() {
        return numIniciofchSolicitud;
    }

    /**
     * Carga Rango inicio fecha Solicitud.
     * @param numIniciofchSolicitud the numIniciofchSolicitud to set
     */
    public void setNumIniciofchSolicitud(Date numIniciofchSolicitud) {
        this.numIniciofchSolicitud = numIniciofchSolicitud;
    }

    /**
     * Obtiene Rango final fecha Solicitud.
     * @return numFinalfchSolicitud
     */
    public Date getNumFinalfchSolicitud() {
        return numFinalfchSolicitud;
    }

    /**
     * Carga Rango final fecha Solicitud.
     * @param numFinalfchSolicitud numFinalfchSolicitud to set
     */
    public void setNumFinalfchSolicitud(Date numFinalfchSolicitud) {
        this.numFinalfchSolicitud = numFinalfchSolicitud;
    }

    /**
     * Obtiene Rango inicio fecha inicio Utilizacion.
     * @return the numIniciofchIniUtilizacion
     */
    public Date getNumIniciofchIniUtilizacion() {
        return numIniciofchIniUtilizacion;
    }

    /**
     * Carga Rango inicio fecha inicio Utilizacion.
     * @param numIniciofchIniUtilizacion the numIniciofchIniUtilizacion to set
     */
    public void setNumIniciofchIniUtilizacion(Date numIniciofchIniUtilizacion) {
        this.numIniciofchIniUtilizacion = numIniciofchIniUtilizacion;
    }

    /**
     * Obtiene Rango final fecha inicio Utilizacion.
     * @return the numFinalfchIniUtilizacion
     */
    public Date getNumFinalfchIniUtilizacion() {
        return numFinalfchIniUtilizacion;
    }

    /**
     * Carga Rango final fecha inicio Utilizacion.
     * @param numFinalfchIniUtilizacion the numFinalfchIniUtilizacion to set
     */
    public void setNumFinalfchIniUtilizacion(Date numFinalfchIniUtilizacion) {
        this.numFinalfchIniUtilizacion = numFinalfchIniUtilizacion;
    }

    /**
     * Obtiene Referencia de la solicitud.
     * @return the referenciaSolicitud
     */
    public String getReferenciaSolicitud() {
        return referenciaSolicitud;
    }

    /**
     * Carga Referencia de la solicitud.
     * @param referenciaSolicitud the referenciaSolicitud to set
     */
    public void setReferenciaSolicitud(String referenciaSolicitud) {
        this.referenciaSolicitud = referenciaSolicitud;
    }

    /**
     * Obtiene Modelo Lazy para carga de solicitudes de asignación.
     * @return solicitudesAsignacionModel
     */
    public SolicitudAsignacionNgLazyModel getSolicitudesAsignacionModel() {
        return solicitudesAsignacionModel;
    }

    /**
     * Carga Modelo Lazy para carga de solicitudes de asignación.
     * @param solicitudesAsignacionModel the solicitudesAsignacionModel to set
     */
    public void setSolicitudesAsignacionModel(SolicitudAsignacionNgLazyModel solicitudesAsignacionModel) {
        this.solicitudesAsignacionModel = solicitudesAsignacionModel;
    }

    /**
     * Obtiene Filtros de búsqueda de Solicitudes.
     * @return the filtros
     */
    public FiltroBusquedaSolicitudes getFiltros() {
        return filtros;
    }

    /**
     * Carga Filtros de búsqueda de Solicitudes.
     * @param filtros the filtros to set
     */
    public void setFiltros(FiltroBusquedaSolicitudes filtros) {
        this.filtros = filtros;
    }

    /**
     * Obtiene Solicitud seleccionada en la tabla de búsqueda de solicitudes.
     * @return solicitudSeleccionada
     */
    public SolicitudAsignacion getSolicitudSeleccionada() {
        return solicitudSeleccionada;
    }

    /**
     * Carga Solicitud seleccionada en la tabla de búsqueda de solicitudes.
     * @param solicitudSeleccionada solicitudSeleccionada to set
     */
    public void setSolicitudSeleccionada(SolicitudAsignacion solicitudSeleccionada) {
        this.solicitudSeleccionada = solicitudSeleccionada;
    }

    /**
     * Indica si Fecha Habilitada.
     * @return the fechaHabilitada
     */
    public boolean isFechaHabilitada() {
        return fechaHabilitada;
    }

    /**
     * Obtiene Referencia al Bean de Solicitud de Asignaciones manual.
     * @return the asignacionManualBean
     */
    public AsignacionManualBean getAsignacionManualBean() {
        return asignacionManualBean;
    }

    /**
     * Carga Referencia al Bean de Solicitud de Asignaciones manual.
     * @param asignacionManualBean the asignacionManualBean to set
     */
    public void setAsignacionManualBean(AsignacionManualBean asignacionManualBean) {
        this.asignacionManualBean = asignacionManualBean;
    }

    /**
     * Obtiene Referencia al Bean de Solicitud de Asignaciones automatica.
     * @return the asignacionAutomaticaBean
     */
    public AsignacionAutomaticaBean getAsignacionAutomaticaBean() {
        return asignacionAutomaticaBean;
    }

    /**
     * Carga Referencia al Bean de Solicitud de Asignaciones automatica.
     * @param asignacionAutomaticaBean the asignacionAutomaticaBean to set
     */
    public void setAsignacionAutomaticaBean(AsignacionAutomaticaBean asignacionAutomaticaBean) {
        this.asignacionAutomaticaBean = asignacionAutomaticaBean;
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

    /**
     * Indica que se está procesando una asignación automática.
     * @return boolean
     */
    public boolean isAsignacionAutomatica() {
        return asignacionAutomatica;
    }

    /**
     * Indica que se está procesando una asignación automática.
     * @param asignacionAutomatica boolean
     */
    public void setAsignacionAutomatica(boolean asignacionAutomatica) {
        this.asignacionAutomatica = asignacionAutomatica;
    }

}
