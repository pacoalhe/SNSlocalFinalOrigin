package mx.ift.sns.web.backend.ac.claveservicio;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.modelo.filtros.FiltroBusquedaClaveServicio;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.lazymodels.ClaveServicioLazyModel;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Bean asociado al catálogo de claves de servicio. */
@ManagedBean(name = "consultarClaveServicioBean")
@ViewScoped
public class ConsultarClaveServicioBean implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = -8691473859707494039L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarClaveServicioBean.class);

    /** Facade de administración de catálogos. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade adminCatFacadeService;

    /** Identificador del componente de mensajes JSF para la visualización del os mensajes. */
    private static final String MSG_ID = "MSG_ConsultarClaveServicio";

    /** Listado de claves de servicio. */
    private List<ClaveServicio> listaClavesServicio;

    /** Clave de servicio seleccionada. */
    private BigDecimal claveServicioSeleccionada;

    /** Listado de estatus de la clave de servicio. */
    private List<Estatus> listadoEstatus;

    /** Estatus de la clave de servicio seleccionado. */
    private Estatus estatusClaveServicioSeleccionado;

    /** Modelo de datos para Lazy Loading en las tablas. */
    private ClaveServicioLazyModel claveServicioLazyModel;

    /** Filtros de búsqueda de Claves de Servicio. */
    private FiltroBusquedaClaveServicio filtros;

    /** Bean de la edición de la clave de servicio. */
    @ManagedProperty("#{edicionClaveServicioBean}")
    private EdicionClaveServicioBean edicionClaveServicioBean;

    /** Clave de servicio a editar. */
    private ClaveServicio claveServicioEdicion;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** JSR260, Método invocado al cargar el bean para terminar su instanciación. */
    @PostConstruct
    public void init() {
        claveServicioSeleccionada = null;
        claveServicioEdicion = null;
        listaClavesServicio = adminCatFacadeService.findAllClaveServicio();
        listadoEstatus = adminCatFacadeService.findAllEstatus();

        // Carga Lazy y Filtros de búsqueda.
        filtros = new FiltroBusquedaClaveServicio();
        filtros.setPreCarga(true);
        filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(adminCatFacadeService,
                Parametro.REGISTROS_POR_PAGINA_BACK));
        registroPorPagina = filtros.getResultadosPagina();

        claveServicioLazyModel = new ClaveServicioLazyModel();
        claveServicioLazyModel.setFiltros(filtros);
        claveServicioLazyModel.setService(adminCatFacadeService);
        claveServicioLazyModel.setMessagesId("MSG_ConsultarClaveServicio");
    }

    /**
     * Método encargado de realizar la búsqueda de claves de servicio según los criterios de búsqueda introducidos.
     */
    public void realizarBusqueda() {
        try {
            if (claveServicioLazyModel == null) {
                claveServicioLazyModel = new ClaveServicioLazyModel();
                claveServicioLazyModel.setFiltros(filtros);
                claveServicioLazyModel.setService(adminCatFacadeService);
                claveServicioLazyModel.setMessagesId("MSG_ConsultarClaveServicio");
            }

            claveServicioLazyModel.clear();
            creaFiltros();

            PaginatorUtil.resetPaginacion("FRM_ConsultarClaveServicio:TBL_ClaveServicio",
                    filtros.getResultadosPagina());

        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error inesperado: " + e.getMessage());
            }
            MensajesBean.addInfoMsg("MSG_ConsultarClaveServicio", MensajesBean.getTextoResource("errorGenerico"));
        }
    }

    /**
     * Método encargado de establecer los filtros a utilizar en la búsqueda de claves de servicio.
     */
    private void creaFiltros() {
        // Resetamos los filtros
        filtros.clear();

        filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(adminCatFacadeService,
                Parametro.REGISTROS_POR_PAGINA_BACK));

        // Filtro del clave de servicio
        if (claveServicioSeleccionada != null) {
            filtros.setCodigo(claveServicioSeleccionada);
        }
        // Filtro del estatus de la clave de servicio
        if (estatusClaveServicioSeleccionado != null) {
            filtros.setEstatus(estatusClaveServicioSeleccionado);
        }
    }

    /** Método encargado de editar la clave de servicio seleccionada. */
    public void editarClaveServicio() {
        try {
            if (claveServicioEdicion != null && edicionClaveServicioBean != null) {
                edicionClaveServicioBean.cargarClaveServicio(claveServicioEdicion);
            }
        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método encargado de actualizar el listado de claves de servicio. */
    public void recargarListadoClavesServicio() {
        try {
            listaClavesServicio = adminCatFacadeService.findAllClaveServicio();
        } catch (Exception ex) {
            LOGGER.debug("Error al recargar el listado de claves de servicio.");
            MensajesBean.addErrorMsg(MSG_ID, "Error al recargar el listado de claves de servicio.", "");
        }

    }

    /**
     * Método encargado de limpiar los filtros de búsqueda de claves de servicio..
     */
    public void limpiarBusqueda() {
        claveServicioSeleccionada = null;
        estatusClaveServicioSeleccionado = null;
        claveServicioLazyModel = null;
    }

    /**
     * Método que devuelve el listado de claves de servicio.
     * @return List<ClaveServicio>
     */
    public List<ClaveServicio> getListaClavesServicio() {
        return listaClavesServicio;
    }

    /**
     * Método que establece el listado de claves de servicio.
     * @param listaClavesServicio List<ClaveServicio>
     */
    public void setListaClavesServicio(List<ClaveServicio> listaClavesServicio) {
        this.listaClavesServicio = listaClavesServicio;
    }

    /**
     * Método que devuelve la clave de servicio seleccionada.
     * @return BigDecimal
     */
    public BigDecimal getClaveServicioSeleccionada() {
        return claveServicioSeleccionada;
    }

    /**
     * Método que establece la clave de servicio seleccionada.
     * @param claveServicioSeleccionada BigDecimal
     */
    public void setClaveServicioSeleccionada(BigDecimal claveServicioSeleccionada) {
        this.claveServicioSeleccionada = claveServicioSeleccionada;
    }

    /**
     * Método que devuelve el estatus de la clave de servicio del filtro de búsqueda.
     * @return Estatus
     */
    public Estatus getEstatusClaveServicioSeleccionado() {
        return estatusClaveServicioSeleccionado;
    }

    /**
     * Método que establece el estatus de la clave de servicio del filtro de búsqueda.
     * @param estatusClaveServicioSeleccionado Estatus
     */
    public void setEstatusClaveServicioSeleccionado(Estatus estatusClaveServicioSeleccionado) {
        this.estatusClaveServicioSeleccionado = estatusClaveServicioSeleccionado;
    }

    /**
     * Método que devuelve el listado de estatus de las claves de servicio del filtro de búsqueda.
     * @return List<Estatus>
     */
    public List<Estatus> getListadoEstatus() {
        return listadoEstatus;
    }

    /**
     * Método que establece el listado de estatus de las claves de servicio del filtro de búsqueda.
     * @param listadoEstatus List<Estatus>
     */
    public void setListadoEstatus(List<Estatus> listadoEstatus) {
        this.listadoEstatus = listadoEstatus;
    }

    /**
     * Método que devuelve el modelo encargado de la búsqueda de claves de servicio.
     * @return ClaveServicioLazyModel
     */
    public ClaveServicioLazyModel getClaveServicioLazyModel() {
        return claveServicioLazyModel;
    }

    /**
     * Método que establece el modelo de la búsqueda de claves de servicio.
     * @param claveServicioLazyModel ClaveServicioLazyModel
     */
    public void setClaveServicioLazyModel(ClaveServicioLazyModel claveServicioLazyModel) {
        this.claveServicioLazyModel = claveServicioLazyModel;
    }

    /**
     * Método que devuelve los filtros de búsqueda de claves de servicio.
     * @return FiltroBusquedaClaveServicio
     */
    public FiltroBusquedaClaveServicio getFiltros() {
        return filtros;
    }

    /**
     * Método que establece los filtros de búsqueda de claves de servicio.
     * @param filtros FiltroBusquedaClaveServicio
     */
    public void setFiltros(FiltroBusquedaClaveServicio filtros) {
        this.filtros = filtros;
    }

    /**
     * Método que devuelve el bean asociado a la edición y creación de claves de servicio.
     * @return EdicionClaveServicioBean
     */
    public EdicionClaveServicioBean getEdicionClaveServicioBean() {
        return edicionClaveServicioBean;
    }

    /**
     * Método que establece el bean asociado a la edición y creación de claves de servicio.
     * @param edicionClaveServicioBean EdicionClaveServicioBean
     */
    public void setEdicionClaveServicioBean(EdicionClaveServicioBean edicionClaveServicioBean) {
        this.edicionClaveServicioBean = edicionClaveServicioBean;
    }

    /**
     * Método que devuelve la clave de servicio a editar.
     * @return ClaveServicio
     */
    public ClaveServicio getClaveServicioEdicion() {
        return claveServicioEdicion;
    }

    /**
     * Método que establece la clave de servicio a editar.
     * @param claveServicioEdicion ClaveServicio
     */
    public void setClaveServicioEdicion(ClaveServicio claveServicioEdicion) {
        this.claveServicioEdicion = claveServicioEdicion;
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
