package mx.ift.sns.web.backend.nng.asignacion;

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
import mx.ift.sns.modelo.nng.SolicitudAsignacionNng;
import mx.ift.sns.modelo.nng.TipoAsignacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.lazymodels.SolicitudAsignacionNngLazyModel;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestiona el buscador de solicitudes de Asignación de Numeración No Geográfica.
 */
@ManagedBean(name = "consultarAsignacionesNngBean")
@ViewScoped
public class ConsultarAsignacionesNngBean implements Serializable {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarAsignacionesNngBean.class);

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultarAsignacionesNNG";

    /** Servicio de Numeración No Geográfica. */
    @EJB(mappedName = "NumeracionNoGeograficaFacade")
    private INumeracionNoGeograficaFacade nngFacade;

    /** Referencia al Bean de Solicitud de Asignaciones . */
    @ManagedProperty("#{solicitudAsignacionNngBean}")
    private SolicitudAsignacionNngBean solicitudAsignacionNngBean;

    /** Consecutivo seleccionado. */
    private String consecutivo;

    /** Proveedor seleccionado. */
    private Proveedor proveedor;

    /** Numero de Oficio. */
    private String numeroOficio;

    /** Clave de servicio. */
    private ClaveServicio claveServicio;

    /** Prefijo del Numero de Oficio. */
    private String prefijo;

    /** Lista de proveedores. */
    private List<Proveedor> listaProveedores;

    /** Lista de Claves de Servicio. */
    private List<ClaveServicio> listaClaves;

    /**
     * Lista estados de la solicitud.
     */
    private List<EstadoSolicitud> listaEstados;

    /** Lista de tipo de asignción. */
    private List<TipoAsignacion> listaTipoAsignacion;

    /** Model de la tabla de solicitudes. */
    private SolicitudAsignacionNngLazyModel solicitudesAsignacionModel;

    /** Fecha asignación desde. */
    private Date fechaDesdeAsignacion;

    /** Fecha asignación hasta. */
    private Date fechaHastaAsignacion;

    /** Fecha solicitud desde. */
    private Date fechaDesdeSolicitud;

    /** Fecha solicitud hasta. */
    private Date fechaHastaSolicitud;

    /** SNA de la serie. */
    private String sna;

    /** Estado de la solicitud. */
    private EstadoSolicitud estadoSeleccionado;

    /** Tipo de asignación. */
    private TipoAsignacion tipoAsignacion;

    /** Cliente. */
    private String cliente;

    /** Activa el campo cliente. */
    private boolean disableCliente = true;

    /** Filtros de busqueda. */
    private FiltroBusquedaSolicitudes filtros;

    /** Registro seleccionado. */
    private SolicitudAsignacionNng registroSeleccionado;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Constructor, por defecto vacío. */
    public ConsultarAsignacionesNngBean() {

    }

    /** JSR260, Método invocado al cargar terminar de instanciar el bean. */
    @PostConstruct
    public void init() {

        try {
            // Iniciamos valores
            consecutivo = null;
            fechaDesdeAsignacion = null;
            fechaHastaAsignacion = null;
            fechaDesdeSolicitud = null;
            fechaHastaSolicitud = null;
            cliente = null;

            // Cargamos el combo de proveedores
            listaProveedores = nngFacade.findAllProveedores();
            proveedor = null;

            // Cargamos el prefijo del numero de oficion
            prefijo = nngFacade.getParamByName(Parametro.PREFIJO);
            numeroOficio = new String(prefijo);

            // Cargamos el combo de claves de servicio
            listaClaves = nngFacade.findAllClaveServicioActivas();
            claveServicio = null;

            // Cargamos el combo de estados de la solicitud
            listaEstados = nngFacade.findAllEstadosSolicitud();
            estadoSeleccionado = null;

            EstadoSolicitud estadoSeleccionadoAux = new EstadoSolicitud();
            estadoSeleccionadoAux.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);

            // Carga Lazy y Filtros de búsqueda
            filtros = new FiltroBusquedaSolicitudes();
            filtros.setEstado(estadoSeleccionadoAux);

            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(nngFacade,
                    Parametro.REGISTROS_POR_PAGINA_BACK));
            registroPorPagina = filtros.getResultadosPagina();

            solicitudesAsignacionModel = new SolicitudAsignacionNngLazyModel();
            solicitudesAsignacionModel.setFiltros(filtros);
            solicitudesAsignacionModel.setService(nngFacade);

            // Cargamos el combo de tipos de asignacion
            listaTipoAsignacion = nngFacade.findAllTipoAsignacion();
            tipoAsignacion = null;
            limpiarBusqueda();

        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Limpia los datos de busqueda.
     */
    public void limpiarBusqueda() {

        consecutivo = null;
        fechaDesdeAsignacion = null;
        fechaHastaAsignacion = null;
        fechaDesdeSolicitud = null;
        fechaHastaSolicitud = null;
        sna = null;
        proveedor = null;
        numeroOficio = new String(prefijo);
        claveServicio = null;
        estadoSeleccionado = null;
        tipoAsignacion = null;
        disableCliente = true;
        cliente = null;

        filtros.clear();
        this.solicitudesAsignacionModel = null;

    }

    /** Carga la busqueda de solicitudes de asignacion. */
    public void realizarBusqueda() {

        LOGGER.debug("");
        try {
            if (solicitudesAsignacionModel == null) {
                filtros = new FiltroBusquedaSolicitudes();
                solicitudesAsignacionModel = new SolicitudAsignacionNngLazyModel();
                solicitudesAsignacionModel.setFiltros(filtros);
                solicitudesAsignacionModel.setService(nngFacade);
            }

            // Resetamos los filtros
            filtros.clear();
            solicitudesAsignacionModel.clear();

            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(nngFacade,
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
            if (!StringUtils.isEmpty(numeroOficio) && !numeroOficio.equals(prefijo)) {
                filtros.setOficioPstSolicitante(numeroOficio);
            }

            // El resto de parámetros si no se han seleccionado son nulos
            filtros.setNumIniciofchSolicitud(fechaDesdeSolicitud);
            if (fechaDesdeSolicitud != null && fechaHastaSolicitud == null) {
                filtros.setNumFinalfchSolicitud(fechaDesdeSolicitud);
            } else {
                filtros.setNumFinalfchSolicitud(fechaHastaSolicitud);
            }

            filtros.setNumInicioFechaAsignacion(fechaDesdeAsignacion);
            if (fechaDesdeAsignacion != null && fechaHastaAsignacion == null) {
                filtros.setNumFinalFechaAsignacion(fechaDesdeAsignacion);
            } else {
                filtros.setNumFinalFechaAsignacion(fechaHastaAsignacion);
            }

            filtros.setProveedor(proveedor);
            filtros.setEstado(estadoSeleccionado);
            filtros.setTipoAsignacion(tipoAsignacion);
            filtros.setClaveServicio(claveServicio);

            if (!StringUtils.isEmpty(cliente)) {
                filtros.setCliente(cliente);
            }

            if (!StringUtils.isEmpty(sna)) {
                if (StringUtils.isNumeric(sna)) {
                    filtros.setSna(new BigDecimal(sna));
                } else {
                    MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0013);
                }
            }

            PaginatorUtil.resetPaginacion("FRM_ConsultarAsignacionesNNG:TBL_SolicitudesAsignacion",
                    filtros.getResultadosPagina());

            // Comprobamos las fechas de inicio y final
            if ((fechaDesdeSolicitud != null && fechaHastaSolicitud != null)
                    && (fechaHastaSolicitud.before(fechaDesdeSolicitud))) {
                MensajesBean.addErrorMsg(MSG_ID, "La fecha final debe ser mayor o igual a la fecha de inicio", "");
            } else if ((fechaDesdeAsignacion != null && fechaHastaAsignacion != null)
                    && (fechaHastaAsignacion.before(fechaDesdeAsignacion))) {
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
     * Habilita el cliente para tipo de asignacion especifica.
     */
    public void tipoAsignacionChange() {
        if (tipoAsignacion != null && tipoAsignacion.getCdg().equals(TipoAsignacion.ESPECIFICA)) {
            disableCliente = false;
        } else {
            disableCliente = true;
        }
        // Cada vez que se cambia el tipo de asignacion se limpia el cliente
        cliente = null;
    }

    /**
     * Edita el registro seleccionado.
     */
    public void editarSolicitud() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{}", registroSeleccionado);
        }

        try {
            if (registroSeleccionado != null && solicitudAsignacionNngBean != null) {
                solicitudAsignacionNngBean.cargarSolicitud(registroSeleccionado);

            }
        } catch (Exception e) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al pulsar sobre el botón 'Editar' en la tabla de solicitudes.
     * @param pSolicitud solicitud
     */
    public void editarSolicitud(Solicitud pSolicitud) {
        try {
            if (solicitudAsignacionNngBean != null) {
                solicitudAsignacionNngBean.cargarSolicitud((SolicitudAsignacionNng) pSolicitud);
            }
        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al pulsar sobre el botón 'Cancelar' en la tabla de solicitudes.
     */
    public void eliminarSolicitud() {
        eliminarSolicitud(registroSeleccionado);
    }

    /**
     * Método invocado al pulsar sobre el botón 'Cancelar' en la tabla de solicitudes.
     * @param solicitud solicitud
     */
    public void eliminarSolicitud(SolicitudAsignacionNng solicitud) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{}", solicitud);
        }

        try {

            if (!nngFacade.isSolicitudWithRangos(solicitud)) {

                EstadoSolicitud estadoSolicitud = new EstadoSolicitud();
                estadoSolicitud.setCodigo(EstadoSolicitud.SOLICITUD_CANCELADA);

                solicitud.setEstadoSolicitud(estadoSolicitud);

                solicitud = nngFacade.saveSolicitudAsignacion(solicitud);

                if (estadoSeleccionado == null) {
                    EstadoSolicitud estado = new EstadoSolicitud();
                    estado.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
                    estadoSeleccionado = estado;
                }

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
     * Método invocado al pulsar sobre el botón 'Cancelar' en la tabla de solicitudes en consulta general.
     * @param solicitud solicitud
     * @param message id mensaje
     */
    public void eliminarSolicitud(SolicitudAsignacionNng solicitud, String message) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{}", solicitud);
        }

        try {

            if (!nngFacade.isSolicitudWithRangos(solicitud)) {

                EstadoSolicitud estadoSolicitud = new EstadoSolicitud();
                estadoSolicitud.setCodigo(EstadoSolicitud.SOLICITUD_CANCELADA);

                solicitud.setEstadoSolicitud(estadoSolicitud);

                solicitud = nngFacade.saveSolicitudAsignacion(solicitud);

                if (estadoSeleccionado == null) {
                    EstadoSolicitud estado = new EstadoSolicitud();
                    estado.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
                    estadoSeleccionado = estado;
                }

            } else {
                MensajesBean
                        .addErrorMsg(message,
                                "No se pueden cancelar solicitudes de asignación"
                                        + " con numeraciones en estado asignada o pendiente.", "");
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(message, Errores.ERROR_0015);

        } catch (Exception e) {
            MensajesBean.addErrorMsg(message, Errores.ERROR_0004);
        }
    }

    /**
     * Consecutivo seleccionado.
     * @return consecutivo
     */
    public String getConsecutivo() {
        return consecutivo;
    }

    /**
     * Consecutivo seleccionado.
     * @param consecutivo consecutivo to set
     */
    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    /**
     * Proveedor seleccionado.
     * @return proveedor
     */
    public Proveedor getProveedor() {
        return proveedor;
    }

    /**
     * Proveedor seleccionado.
     * @param proveedor proveedor to set
     */
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * Lista de proveedores.
     * @return listaProveedores
     */
    public List<Proveedor> getListaProveedores() {
        return listaProveedores;
    }

    /**
     * Lista de proveedores.
     * @param listaProveedores listaProveedores to set
     */
    public void setListaProveedores(List<Proveedor> listaProveedores) {
        this.listaProveedores = listaProveedores;
    }

    /**
     * Numero de Oficio.
     * @return numeroOficio
     */
    public String getNumeroOficio() {
        return numeroOficio;
    }

    /**
     * Numero de Oficio.
     * @param numeroOficio numeroOficio to set
     */
    public void setNumeroOficio(String numeroOficio) {
        this.numeroOficio = numeroOficio;
    }

    /**
     * Prefijo del Numero de Oficio.
     * @return prefijo
     */
    public String getPrefijo() {
        return prefijo;
    }

    /**
     * Prefijo del Numero de Oficio.
     * @param prefijo prefijo to set
     */
    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    /**
     * Fecha asignación desde.
     * @return fechaDesdeAsignacion
     */
    public Date getFechaDesdeAsignacion() {
        return fechaDesdeAsignacion;
    }

    /**
     * Fecha asignación desde.
     * @param fechaDesdeAsignacion fechaDesdeAsignacion to set
     */
    public void setFechaDesdeAsignacion(Date fechaDesdeAsignacion) {
        this.fechaDesdeAsignacion = fechaDesdeAsignacion;
    }

    /**
     * Fecha asignación hasta.
     * @return fechaHastaAsignacion
     */
    public Date getFechaHastaAsignacion() {
        return fechaHastaAsignacion;
    }

    /**
     * Fecha asignación hasta.
     * @param fechaHastaAsignacion fechaHastaAsignacion to set
     */
    public void setFechaHastaAsignacion(Date fechaHastaAsignacion) {
        this.fechaHastaAsignacion = fechaHastaAsignacion;
    }

    /**
     * Clave de servicio.
     * @return claveServicio
     */
    public ClaveServicio getClaveServicio() {
        return claveServicio;
    }

    /**
     * Clave de servicio.
     * @param claveServicio claveServicio to set
     */
    public void setClaveServicio(ClaveServicio claveServicio) {
        this.claveServicio = claveServicio;
    }

    /**
     * Fecha solicitud desde.
     * @return fechaDesdeSolicitud
     */
    public Date getFechaDesdeSolicitud() {
        return fechaDesdeSolicitud;
    }

    /**
     * Fecha solicitud desde.
     * @param fechaDesdeSolicitud fechaDesdeSolicitud to set
     */
    public void setFechaDesdeSolicitud(Date fechaDesdeSolicitud) {
        this.fechaDesdeSolicitud = fechaDesdeSolicitud;
    }

    /**
     * Fecha solicitud hasta.
     * @return fechaHastaSolicitud
     */
    public Date getFechaHastaSolicitud() {
        return fechaHastaSolicitud;
    }

    /**
     * Fecha solicitud hasta.
     * @param fechaHastaSolicitud fechaHastaSolicitud to set
     */
    public void setFechaHastaSolicitud(Date fechaHastaSolicitud) {
        this.fechaHastaSolicitud = fechaHastaSolicitud;
    }

    /**
     * SNA de la serie.
     * @return sna
     */
    public String getSna() {
        return sna;
    }

    /**
     * SNA de la serie.
     * @param sna sna to set
     */
    public void setSna(String sna) {
        this.sna = sna;
    }

    /**
     * Tipo de asignación.
     * @return tipoAsignacion
     */
    public TipoAsignacion getTipoAsignacion() {
        return tipoAsignacion;
    }

    /**
     * Tipo de asignación.
     * @param tipoAsignacion tipoAsignacion to set
     */
    public void setTipoAsignacion(TipoAsignacion tipoAsignacion) {
        this.tipoAsignacion = tipoAsignacion;
    }

    /**
     * Lista de tipo de asignción.
     * @return listaTipoAsignacion
     */
    public List<TipoAsignacion> getListaTipoAsignacion() {
        return listaTipoAsignacion;
    }

    /**
     * Lista de tipo de asignción.
     * @param listaTipoAsignacion listaTipoAsignacion to set
     */
    public void setListaTipoAsignacion(List<TipoAsignacion> listaTipoAsignacion) {
        this.listaTipoAsignacion = listaTipoAsignacion;
    }

    /**
     * Cliente.
     * @return cliente
     */
    public String getCliente() {
        return cliente;
    }

    /**
     * Cliente.
     * @param cliente cliente to set
     */
    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    /**
     * Model de la tabla de solicitudes.
     * @return solicitudesAsignacionModel
     */
    public SolicitudAsignacionNngLazyModel getSolicitudesAsignacionModel() {
        return solicitudesAsignacionModel;
    }

    /**
     * Model de la tabla de solicitudes.
     * @param solicitudesAsignacionModel solicitudesAsignacionModel to set
     */
    public void setSolicitudesAsignacionModel(SolicitudAsignacionNngLazyModel solicitudesAsignacionModel) {
        this.solicitudesAsignacionModel = solicitudesAsignacionModel;
    }

    /**
     * Filtros de busqueda.
     * @return filtros
     */
    public FiltroBusquedaSolicitudes getFiltros() {
        return filtros;
    }

    /**
     * Filtros de busqueda.
     * @param filtros filtros to set
     */
    public void setFiltros(FiltroBusquedaSolicitudes filtros) {
        this.filtros = filtros;
    }

    /**
     * Activa el campo cliente.
     * @return disableCliente
     */
    public boolean isDisableCliente() {
        return disableCliente;
    }

    /**
     * Activa el campo cliente.
     * @param disableCliente disableCliente to set
     */
    public void setDisableCliente(boolean disableCliente) {
        this.disableCliente = disableCliente;
    }

    /**
     * Lista estados de la solicitud.
     * @return listaEstados
     */
    public List<EstadoSolicitud> getListaEstados() {
        return listaEstados;
    }

    /**
     * Lista estados de la solicitud.
     * @param listaEstados listaEstados to set
     */
    public void setListaEstados(List<EstadoSolicitud> listaEstados) {
        this.listaEstados = listaEstados;
    }

    /**
     * Estado de la solicitud.
     * @return estadoSeleccionado
     */
    public EstadoSolicitud getEstadoSeleccionado() {
        return estadoSeleccionado;
    }

    /**
     * Estado de la solicitud.
     * @param estadoSeleccionado estadoSeleccionado to set
     */
    public void setEstadoSeleccionado(EstadoSolicitud estadoSeleccionado) {
        this.estadoSeleccionado = estadoSeleccionado;
    }

    /**
     * Lista de Claves de Servicio.
     * @return listaClaves
     */
    public List<ClaveServicio> getListaClaves() {
        return listaClaves;
    }

    /**
     * Lista de Claves de Servicio.
     * @param listaClaves listaClaves to set
     */
    public void setListaClaves(List<ClaveServicio> listaClaves) {
        this.listaClaves = listaClaves;
    }

    /**
     * Registro seleccionado.
     * @return registroSeleccionado
     */
    public SolicitudAsignacionNng getRegistroSeleccionado() {
        return registroSeleccionado;
    }

    /**
     * Registro seleccionado.
     * @param registroSeleccionado registroSeleccionado to set
     */
    public void setRegistroSeleccionado(SolicitudAsignacionNng registroSeleccionado) {
        this.registroSeleccionado = registroSeleccionado;
    }

    /**
     * Referencia al Bean de Solicitud de Asignaciones.
     * @return solicitudAsignacionNngBean
     */
    public SolicitudAsignacionNngBean getSolicitudAsignacionNngBean() {
        return solicitudAsignacionNngBean;
    }

    /**
     * Referencia al Bean de Solicitud de Asignaciones.
     * @param solicitudAsignacionNngBean solicitudAsignacionNngBean to set
     */
    public void setSolicitudAsignacionNngBean(SolicitudAsignacionNngBean solicitudAsignacionNngBean) {
        this.solicitudAsignacionNngBean = solicitudAsignacionNngBean;
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
