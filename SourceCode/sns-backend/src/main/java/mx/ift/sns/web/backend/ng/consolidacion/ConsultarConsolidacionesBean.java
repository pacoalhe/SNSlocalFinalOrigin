package mx.ift.sns.web.backend.ng.consolidacion;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.abn.EstadoAbn;
import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.ng.SolicitudConsolidacion;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.lazymodels.SolicitudConsolidacionConsultaLazyModel;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Bean asociado a la búsqueda de Solicitudes de consolidación. */
@ManagedBean(name = "consultarConsolidacionesBean")
@ViewScoped
public class ConsultarConsolidacionesBean implements Serializable {

    /** serialVersionUID. **/
    private static final long serialVersionUID = 1L;

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarConsolidacionesBean.class);

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultarConsolidaciones";

    /** consecutivo. **/
    private String consecutivo;

    /** abnEntrega. **/
    private String abnEntrega;

    /** abnRecibe. **/
    private String abnRecibe;

    /** fechaIniConsolidacion. **/
    private Date fechaIniConsolidacion;

    /** fechaFinConsolidacion. **/
    private Date fechaFinConsolidacion;

    /** Lista para los estados solicitudes. **/
    private List<EstadoSolicitud> listaEstadosSolicidud;

    /** Estado. */
    private Estado estadoMun;

    /** Municipio. */
    private Municipio municipio;

    /** Población. */
    private Poblacion poblacion;

    /** EstadoSolicitud. **/
    private EstadoSolicitud estatusSeleccionado;

    /** Modelo Lazy para carga de solicitudes de consolidaciones. */
    private SolicitudConsolidacionConsultaLazyModel solicitudesConsolidacionModel;

    /** Filtros de búsqueda de Solicitudes. */
    private FiltroBusquedaSolicitudes filtros;

    /** solicitud de consolidacion seleccionada. **/
    private DetalleConsultaConsolidacion registroSeleccionado;

    /** Lista Estado. */
    private List<Estado> listaEstados;

    /** Lista Municipio. */
    private List<Municipio> listaMunicipio;

    /** Lista Población. */
    private List<Poblacion> listaPoblacion;

    /** Referencia al Bean de Solicitud de Liberacion. */
    @ManagedProperty("#{solicitudConsolidacionBean}")
    private SolicitudConsolidacionBean solicitudConsolidacionBean;

    /** Fecha Habilitada. */
    private boolean fechaHabilitada = false;

    /**
     * Llamada a la clase de negocio.
     **/
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /**
     * Constructor vacío por defecto, la carga se hace en el método anotado 'PostConstruct'.
     */
    public ConsultarConsolidacionesBean() {
    }

    /**
     * Iniciamos la lista EstadosSolicidud y la lista Estados y cargamos los combos.
     **/
    @PostConstruct
    public void init() {
        try {
            // Listado de proveedores de servicio
            listaEstadosSolicidud = ngService.findAllEstadosSolicitud();
            listaEstados = ngService.findAllEstados();

            EstadoSolicitud estado = new EstadoSolicitud();
            estado.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);

            filtros = new FiltroBusquedaSolicitudes();
            filtros.setEstado(estado);

            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(ngService,
                    Parametro.REGISTROS_POR_PAGINA_BACK));
            registroPorPagina = filtros.getResultadosPagina();

            solicitudesConsolidacionModel = new SolicitudConsolidacionConsultaLazyModel();
            solicitudesConsolidacionModel.setFiltros(filtros);
            solicitudesConsolidacionModel.setService(ngService);

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Acción invocada al pulsar sobre el botón 'Búsqueda' del formulario.
     */
    public void realizarBusqueda() {
        try {
            if (solicitudesConsolidacionModel == null) {
                filtros = new FiltroBusquedaSolicitudes();
                solicitudesConsolidacionModel = new SolicitudConsolidacionConsultaLazyModel();
                solicitudesConsolidacionModel.setFiltros(filtros);
                solicitudesConsolidacionModel.setService(ngService);
            }

            // Resetamos los filtros
            filtros.clear();
            solicitudesConsolidacionModel.clear();

            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(ngService,
                    Parametro.REGISTROS_POR_PAGINA_BACK));

            // Filtro de valor de consecutivo
            if (!StringUtils.isEmpty(consecutivo)) {
                if (StringUtils.isNumeric(consecutivo)) {
                    if (StringUtils.length(consecutivo) <= 9) {
                        filtros.setConsecutivo(consecutivo);
                    } else {
                        MensajesBean.addInfoMsg(MSG_ID,
                                "El campo 'consecutivo' ha de tener un formato máximo de 9 dígitos", "");
                    }
                } else {
                    MensajesBean.addInfoMsg(MSG_ID,
                            "El campo 'Consecutivo' ha de ser un valor numérico", "");
                }
            }

            if (abnEntrega != null && !abnEntrega.isEmpty()) {
                if (isNumeric(abnEntrega)) {
                    filtros.setAbnEntrega(abnEntrega);
                } else {
                    MensajesBean.addErrorMsg(MSG_ID,
                            MensajesBean.getTextoResource("catalogo.centrales.abn"), "");
                    return;
                }
            }

            if (abnRecibe != null && !abnRecibe.isEmpty()) {
                if (isNumeric(abnRecibe)) {
                    filtros.setAbnRecibe(abnRecibe);
                } else {
                    MensajesBean.addErrorMsg(MSG_ID,
                            MensajesBean.getTextoResource("catalogo.centrales.abn"), "");
                    return;
                }
            }

            filtros.setFechaIniConsolidacion(fechaIniConsolidacion);
            if (fechaIniConsolidacion != null && fechaFinConsolidacion == null) {
                filtros.setFechaFinConsolidacion(fechaIniConsolidacion);
            } else {
                filtros.setFechaFinConsolidacion(fechaFinConsolidacion);
            }

            filtros.setEstado(estatusSeleccionado);
            if (estadoMun != null && municipio == null && poblacion == null) {
                filtros.setEstadoMun(estadoMun);
            } else if (estadoMun != null && municipio != null && poblacion == null) {
                filtros.setEstadoMun(estadoMun);
                filtros.setMunicipio(municipio);
            } else if (estadoMun != null && municipio != null && poblacion != null) {
                filtros.setPoblacion(poblacion);
            }

            PaginatorUtil.resetPaginacion("FRM_BuscarConsolidaciones:TBL_Consolidaciones",
                    filtros.getResultadosPagina());

            // Si ha seleccionado fechas comprobamos que sean coherentes (VL1)
            if ((fechaIniConsolidacion != null && fechaFinConsolidacion != null)
                    && (fechaFinConsolidacion.before(fechaIniConsolidacion))) {
                MensajesBean.addErrorMsg(MSG_ID,
                        "La fecha de inicio debe ser mayor o igual a la fecha final", "");
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método para saber si es numerica positiva la cadena.
     * @param cadena cadena de entrada.
     * @return boolean true/false.
     */
    private static boolean isNumeric(String cadena) {
        try {
            if (Integer.parseInt(cadena) > 0) {
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /** Habilita la fecha Fin al editar la fecha inicio. */
    public void habilitarFechaFin() {
        if (fechaIniConsolidacion != null) {
            fechaHabilitada = true;
        } else {
            fechaHabilitada = false;
        }
    }

    /** Método invocado al pulsar el botón 'limpiar' sobre el buscador de solicitudes. */
    public void limpiarBusqueda() {
        this.consecutivo = null;
        this.abnEntrega = null;
        this.abnRecibe = null;
        this.fechaIniConsolidacion = null;
        this.fechaFinConsolidacion = null;
        this.estatusSeleccionado = null;
        this.estadoMun = null;
        this.municipio = null;
        this.poblacion = null;
        this.registroSeleccionado = null;
        this.solicitudesConsolidacionModel = null;
        this.fechaHabilitada = false;
        if (this.listaMunicipio != null) {
            this.listaMunicipio.clear();
        }
        if (this.listaPoblacion != null) {
            this.listaPoblacion.clear();
        }
    }

    /** Método invocado al pulsar el botón 'Nueva'. */
    public void nuevaSolicitud() {
        solicitudConsolidacionBean.nuevaSolicitud();
    }

    /** Método invocado al pulsar el botón 'editar'. */
    public void editarSolicitud() {
        try {
            if (registroSeleccionado != null && solicitudConsolidacionBean != null) {
                SolicitudConsolidacion solicitud =
                        ngService.getSolicitudConsolidacionById(new BigDecimal(registroSeleccionado.getConsecutivo()));
                solicitudConsolidacionBean.cargarSolicitud(solicitud);
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
            if (solicitudConsolidacionBean != null) {
                solicitudConsolidacionBean.cargarSolicitud((SolicitudConsolidacion) pSolicitud);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar el botón 'Si' al Cancelar una solicitud. */
    public void eliminarSolicitud() {
        try {
            if (registroSeleccionado != null) {
                SolicitudConsolidacion solicitud =
                        ngService.getSolicitudConsolidacionById(new BigDecimal(registroSeleccionado.getConsecutivo()));
                if (solicitud.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_EN_TRAMITE)) {
                    solicitud.getEstadoSolicitud().setCodigo(EstadoSolicitud.SOLICITUD_CANCELADA);

                    // Se cambia el estado del AbnConsolidar a "CANCELADO"
                    // AbnConsolidar abnConso = ngService.getAbnConsolidarByIdSol(solicitud.getId());
                    // abnConso.getEstado().setCodigo(EstadoAbnConsolidar.CANCELADO);
                    // ngService.saveAbnConsolidar(abnConso);

                    // Se cambia el estado del Abn de entrega a "ACTIVO"
                    Abn abnEntrega = ngService.getAbnById(solicitud.getAbnEntrega().getCodigoAbn());
                    abnEntrega.getEstadoAbn().setCodigo(EstadoAbn.ACTIVO);
                    ngService.saveAbn(abnEntrega);

                    ngService.saveSolicitudConsolidacion(solicitud);
                    this.realizarBusqueda();
                } else {
                    MensajesBean.addErrorMsg(MSG_ID,
                            "No se puede cancelar una solicitud en estado "
                                    + solicitud.getEstadoSolicitud().getDescripcion(), "");
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
            if (solicitudConsolidacionBean != null) {
                if (pSolicitud.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_EN_TRAMITE)) {
                    pSolicitud.getEstadoSolicitud().setCodigo(EstadoSolicitud.SOLICITUD_CANCELADA);
                    SolicitudConsolidacion solicitudConso = ngService
                            .getSolicitudConsolidacionEagerLoad((SolicitudConsolidacion) pSolicitud);

                    // Se cambia el estado del AbnConsolidar a "CANCELADO"
                    // AbnConsolidar abnConso = ngService.getAbnConsolidarByIdSol(solicitudConso.getId());
                    // abnConso.getEstado().setCodigo(EstadoAbnConsolidar.CANCELADO);
                    // ngService.saveAbnConsolidar(abnConso);

                    // Se cambia el estado del Abn de entrega a "ACTIVO"
                    Abn abnEntrega = ngService.getAbnById(solicitudConso.getAbnEntrega().getCodigoAbn());
                    abnEntrega.getEstadoAbn().setCodigo(EstadoAbn.ACTIVO);
                    ngService.saveAbn(abnEntrega);

                    ngService.saveSolicitudConsolidacion(solicitudConso);
                    this.realizarBusqueda();
                } else {
                    MensajesBean.addErrorMsg(pMensajes,
                            "No se puede cancelar una solicitud en estado "
                                    + pSolicitud.getEstadoSolicitud().getDescripcion(), "");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Rellena la lista de municipios por Estado seleccionado.
     * @throws Exception excepcion
     */
    public void habilitarMunicipio() throws Exception {
        if (estadoMun != null) {
            listaMunicipio = ngService.findMunicipiosByEstado(estadoMun.getCodEstado());
        }

    }

    /**
     * Rellena la lista de poblaciones por estado y municipio.
     * @throws Exception excepcion
     */
    public void habilitarPoblacion() throws Exception {
        if (municipio != null) {
            listaPoblacion = ngService.findAllPoblaciones(estadoMun.getCodEstado(),
                    municipio.getId().getCodMunicipio());
        }
    }

    /**
     * Identificador de la solicitud.
     * @return the consecutivo
     */
    public String getConsecutivo() {
        return consecutivo;
    }

    /**
     * Identificador de la solicitud.
     * @param consecutivo the consecutivo to set
     */
    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    /**
     * Identificador de los estados de la solicitud.
     * @return the listaEstadosSolicidud
     */
    public List<EstadoSolicitud> getListaEstadosSolicidud() {
        return listaEstadosSolicidud;
    }

    /**
     * Identificador de los estados de la solicitud.
     * @param listaEstadosSolicidud the listaEstadosSolicidud to set
     */
    public void setListaEstadosSolicidud(List<EstadoSolicitud> listaEstadosSolicidud) {
        this.listaEstadosSolicidud = listaEstadosSolicidud;
    }

    /**
     * Identificador del estado seleccionado.
     * @return the estatusSeleccionado
     */
    public EstadoSolicitud getEstatusSeleccionado() {
        return estatusSeleccionado;
    }

    /**
     * Identificador del estado seleccionado.
     * @param estatusSeleccionado the estatusSeleccionado to set
     */
    public void setEstatusSeleccionado(EstadoSolicitud estatusSeleccionado) {
        this.estatusSeleccionado = estatusSeleccionado;
    }

    /**
     * Identificador del Abn que entrega.
     * @return the abnEntrega
     */
    public String getAbnEntrega() {
        return abnEntrega;
    }

    /**
     * Identificador del Abn de entrega.
     * @param abnEntrega the abnEntrega to set
     */
    public void setAbnEntrega(String abnEntrega) {
        this.abnEntrega = abnEntrega;
    }

    /**
     * Identificador del Abn que recive.
     * @return the abnRecibe
     */
    public String getAbnRecibe() {
        return abnRecibe;
    }

    /**
     * Identificador del Abn que recive.
     * @param abnRecibe the abnRecibe to set
     */
    public void setAbnRecibe(String abnRecibe) {
        this.abnRecibe = abnRecibe;
    }

    /**
     * Identificador de la fecha de Inicio de consolidación.
     * @return the fechaIniConsolidacion
     */
    public Date getFechaIniConsolidacion() {
        return fechaIniConsolidacion;
    }

    /**
     * Identificador de la fecha de Inicio de consolidación.
     * @param fechaIniConsolidacion the fechaIniConsolidacion to set
     */
    public void setFechaIniConsolidacion(Date fechaIniConsolidacion) {
        this.fechaIniConsolidacion = fechaIniConsolidacion;
    }

    /**
     * Identificador de la fecha de Fin de consolidación.
     * @return the fechaFinConsolidacion
     */
    public Date getFechaFinConsolidacion() {
        return fechaFinConsolidacion;
    }

    /**
     * Identificador de la fecha de Fin de consolidación.
     * @param fechaFinConsolidacion the fechaFinConsolidacion to set
     */
    public void setFechaFinConsolidacion(Date fechaFinConsolidacion) {
        this.fechaFinConsolidacion = fechaFinConsolidacion;
    }

    /**
     * Identificador del Estado seleccionado.
     * @return the estadoMun
     */
    public Estado getEstadoMun() {
        return estadoMun;
    }

    /**
     * Identificador del Estado seleccionado.
     * @param estadoMun the estadoMun to set
     */
    public void setEstadoMun(Estado estadoMun) {
        this.estadoMun = estadoMun;
    }

    /**
     * Identificador del Municipio seleccionado.
     * @return the municipio
     */
    public Municipio getMunicipio() {
        return municipio;
    }

    /**
     * Identificador del Municipio seleccionado.
     * @param municipio the municipio to set
     */
    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    /**
     * Identificador de la población seleccionado.
     * @return the poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * Identificador de la población seleccionado.
     * @param poblacion the poblacion to set
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * Identificador de los Estados.
     * @return the listaEstados
     */
    public List<Estado> getListaEstados() {
        return listaEstados;
    }

    /**
     * Identificador de los Estados.
     * @param listaEstados the listaEstados to set
     */
    public void setListaEstados(List<Estado> listaEstados) {
        this.listaEstados = listaEstados;
    }

    /**
     * Identificador de los municipios.
     * @return the listaMunicipio
     */
    public List<Municipio> getListaMunicipio() {
        return listaMunicipio;
    }

    /**
     * Identificador de los municipios.
     * @param listaMunicipio the listaMunicipio to set
     */
    public void setListaMunicipio(List<Municipio> listaMunicipio) {
        this.listaMunicipio = listaMunicipio;
    }

    /**
     * Identificador de las poblaciones.
     * @return the listaPoblacion
     */
    public List<Poblacion> getListaPoblacion() {
        return listaPoblacion;
    }

    /**
     * Identificador de las poblaciones.
     * @param listaPoblacion the listaPoblacion to set
     */
    public void setListaPoblacion(List<Poblacion> listaPoblacion) {
        this.listaPoblacion = listaPoblacion;
    }

    /**
     * Modelo Lazy para carga de solicitudes de consolidación.
     * @return the solicitudesConsolidacionModel
     */
    public SolicitudConsolidacionConsultaLazyModel getSolicitudesConsolidacionModel() {
        return solicitudesConsolidacionModel;
    }

    /**
     * Modelo Lazy para carga de solicitudes de consolidación.
     * @param solicitudesConsolidacionModel the solicitudesConsolidacionModel to set.
     */
    public void setSolicitudesConsolidacionModel(SolicitudConsolidacionConsultaLazyModel
            solicitudesConsolidacionModel) {
        this.solicitudesConsolidacionModel = solicitudesConsolidacionModel;
    }

    /**
     * Identificador fechaHabilitada.
     * @return fechaHabilitada
     */
    public boolean isFechaHabilitada() {
        return fechaHabilitada;
    }

    /**
     * Identificador fechaHabilitada.
     * @param fechaHabilitada fecha
     */
    public void setFechaHabilitada(boolean fechaHabilitada) {
        this.fechaHabilitada = fechaHabilitada;
    }

    /**
     * Referencia al Bean de Solicitud de consolidación.
     * @return the solicitudConsolidacionBean
     */
    public SolicitudConsolidacionBean getSolicitudConsolidacionBean() {
        return solicitudConsolidacionBean;
    }

    /**
     * Referencia al Bean de Solicitud de consolidación.
     * @param solicitudConsolidacionBean the solicitudConsolidacionBean to set
     */
    public void setSolicitudConsolidacionBean(SolicitudConsolidacionBean solicitudConsolidacionBean) {
        this.solicitudConsolidacionBean = solicitudConsolidacionBean;
    }

    /**
     * Registro seleccionado en la tabla de consultas.
     * @return the registroSeleccionado
     */
    public DetalleConsultaConsolidacion getRegistroSeleccionado() {
        return registroSeleccionado;
    }

    /**
     * Registro seleccionado en la tabla de consultas.
     * @param registroSeleccionado the registroSeleccionado to set
     */
    public void setRegistroSeleccionado(DetalleConsultaConsolidacion registroSeleccionado) {
        this.registroSeleccionado = registroSeleccionado;
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
