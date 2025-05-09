package mx.ift.sns.web.backend.ac.proveedor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.modelo.filtros.FiltroBusquedaProveedores;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.modelo.pst.TipoServicio;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.lazymodels.ProveedoresLazyModel;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestiona el buscador de proveedores de telecomunicaciones.
 */
@ManagedBean(name = "consultarCatalogoProveedorBean")
@ViewScoped
public class ConsultarCatalogoProveedorBean implements Serializable {
    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarCatalogoProveedorBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_Proveedores";

    /** Servicio de Numeración Geográfica. */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    /** Servicio de Catalogo. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade catalogoService;

    /** Modelo de datos para Lazy Loading en las tablas. */
    private ProveedoresLazyModel proveedoresLazyModel;

    /** Proveedor de Servicio. */
    private Proveedor proveedorSeleccionado;

    /** Tipo de Servicio. */
    private TipoServicio tipoServicioSeleccionado;

    /** Tipo de PST. */
    private TipoProveedor tipoProveedorSeleccionado;

    /** Tipo de Red. */
    private TipoRed tipoRedSeleccionada;

    /** Estatus del proveedor. */
    private Estatus estatusProveedorSeleccionado;

    /** Listado de Proveedores resultado. */
    private List<Proveedor> listadoProveedores;

    /** Listado de Tipos de Servicio. */
    private List<TipoServicio> listadoTiposServicio;

    /** Listado de Tipos de PST. */
    private List<TipoProveedor> listadoTipoPst;

    /** Listado de los Tipos de Red. */
    private List<TipoRed> listadoTiposRed;

    /** Listado de estatus del Proveedor. */
    private List<Estatus> listadoEstatus;

    /** Filtros de búsqueda de Proveedores. */
    private FiltroBusquedaProveedores filtros;

    /** Proveedor seleccionado en la tabla de búsqueda. */
    private Proveedor proveedorEdicionSeleccionado;

    /** Bean de la edición del proveedor. */
    @ManagedProperty("#{gestionProveedorBean}")
    private GestionProveedorBean gestionProveedorBean;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Activa el botón exportar si no hay resultados en la búsqueda. */
    private boolean emptySearch = true;

    /** Constructor vacio. */
    public ConsultarCatalogoProveedorBean() {
    }

    /**
     * Método que inicializa los combos del buscador de proveedores.
     */
    @PostConstruct
    public void init() {
        try {
            // Listado de proveedores de servicio
            listadoProveedores = ngService.findAllProveedores();
            proveedorSeleccionado = new Proveedor();

            // LIstado de tipos de servicio
            listadoTiposServicio = ngService.findAllTiposServicio();
            tipoServicioSeleccionado = new TipoServicio();

            // LIstado de Tipos de PST
            listadoTipoPst = ngService.findAllTiposProveedor();
            tipoProveedorSeleccionado = new TipoProveedor();

            // Listado de Tipos de Red
            listadoTiposRed = ngService.findAllTiposRed();
            tipoRedSeleccionada = new TipoRed();

            // Listado de Estados del PST
            listadoEstatus = ngService.findAllEstatus();
            estatusProveedorSeleccionado = new Estatus();

            // Carga Lazy y Filtros de búsqueda.
            filtros = new FiltroBusquedaProveedores();
            filtros.setPreCarga(true);
            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(catalogoService,
                    Parametro.REGISTROS_POR_PAGINA_BACK));
            registroPorPagina = filtros.getResultadosPagina();

            proveedoresLazyModel = new ProveedoresLazyModel();
            proveedoresLazyModel.setFiltros(filtros);
            proveedoresLazyModel.setService(ngService);
            proveedoresLazyModel.setMessagesId("MSG_Proveedores");
            emptySearch = ngService.findAllProveedoresCount(filtros) == 0;
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error inesperado: " + e.getMessage());
            }
            MensajesBean.addInfoMsg("MSG_Proveedores", MensajesBean.getTextoResource("errorGenerico"));
        }
    }

    /** Método invocado al pulsar el botón 'buscar'. */
    public void realizarBusqueda() {
        try {
            if (proveedoresLazyModel == null) {
                proveedoresLazyModel = new ProveedoresLazyModel();
                proveedoresLazyModel.setFiltros(filtros);
                proveedoresLazyModel.setService(ngService);
                proveedoresLazyModel.setMessagesId("MSG_Proveedores");
            }

            proveedoresLazyModel.clear();
            creaFiltros(true);

            PaginatorUtil.resetPaginacion("FRM_ConsultarProveedores:TBL_Proveedores",
                    filtros.getResultadosPagina());
            emptySearch = ngService.findAllProveedoresCount(filtros) == 0;
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error inesperado: " + e.getMessage());
            }
            MensajesBean.addInfoMsg("MSG_Proveedores", MensajesBean.getTextoResource("errorGenerico"));
        }
    }

    /**
     * Método que setea los filtros de búsqueda.
     * @param sinFiltro Indica si se han de usar filtros o no para la precarga.
     */
    private void creaFiltros(boolean sinFiltro) {
        // Resetamos los filtros
        filtros.clear();

        filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(catalogoService,
                Parametro.REGISTROS_POR_PAGINA_BACK));

        // Filtro del proveedor
        if (proveedorSeleccionado != null) {
            filtros.setProveedor(proveedorSeleccionado);
        }
        // Filtro del tipo de servicio
        if (tipoServicioSeleccionado != null) {
            filtros.setTipoServicio(tipoServicioSeleccionado);
        }
        // Filtro del tipo de pst
        if (tipoProveedorSeleccionado != null) {
            filtros.setTipoProveedor(tipoProveedorSeleccionado);
        }
        // Filtro del tipo de red
        if (tipoRedSeleccionada != null) {
            filtros.setTipoRed(tipoRedSeleccionada);
        }
        // Filtro del estado del pst
        if (estatusProveedorSeleccionado != null) {
            filtros.setEstado(estatusProveedorSeleccionado);
        }

        // Comprobamos que haya agregado algún parámetro de búsqueda
        if (filtros.getFiltrosProveedor().isEmpty() && !sinFiltro) {
            MensajesBean.addInfoMsg("MSG_Proveedores",
                    MensajesBean.getTextoResource("consultas.generales.error.sinFiltros"));
        }

        filtros.setPreCarga(sinFiltro);
    }

    /** Método invocado al pulsar el botón 'limpiar' sobre el buscador de proveedores. */
    public void limpiarBusqueda() {
        emptySearch = true;
        proveedorSeleccionado = null;
        tipoServicioSeleccionado = null;
        tipoProveedorSeleccionado = null;
        tipoRedSeleccionada = null;
        estatusProveedorSeleccionado = null;
        proveedoresLazyModel = null;
    }

    /** Función que edita el proveedor seleccionado. */
    public void editarProveedor() {
        try {
            if (proveedorEdicionSeleccionado != null && gestionProveedorBean != null) {
                gestionProveedorBean.cargarProveedor(proveedorEdicionSeleccionado);
            }
        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Función que da de baja el proveedor seleccionado. */
    public void bajaProveedor() {
        try {
            if (proveedorEdicionSeleccionado != null && gestionProveedorBean != null) {
                if (catalogoService.bajaProveedor(proveedorEdicionSeleccionado) == null) {
                    MensajesBean.addErrorMsg(MSG_ID,
                            MensajesBean.getTextoResource("catalogo.proveedores.validacion.error.pst.baja"), "");
                    return;
                }
            }
        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Función que activa el proveedor seleccionado. */
    public void activarProveedor() {
        try {
            if (proveedorEdicionSeleccionado != null && gestionProveedorBean != null) {
                if (catalogoService.activarProveedor(proveedorEdicionSeleccionado) == null) {
                    MensajesBean.addErrorMsg(MSG_ID,
                            MensajesBean.getTextoResource("catalogo.proveedores.validacion.error.pst.activar"), "");
                    return;
                }
            }
        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Función encargada de actualizar el listado de proveedores. */
    public void recargarListadoProveedores() {
        try {
            listadoProveedores = ngService.findAllProveedores();
        } catch (Exception ex) {
            LOGGER.debug("Error al recargar el listado de proveedores.");
            MensajesBean.addErrorMsg(MSG_ID, "Error al recargar el listado de proveedores.", "");
        }

    }

    /**
     * Genera el excel de datos generales de los proveedores.
     * @return excel
     * @throws Exception e
     */
    public StreamedContent getExportarDatosGenerales() throws Exception {

        creaFiltros(true);
        InputStream stream = new ByteArrayInputStream(ngService.exportarDatosGenerales(filtros));
        String docName = "Datos Generales Proveedores";

        docName = docName.concat(".xlsx");

        stream.close();

        LOGGER.debug("docname {}", docName);
        return new DefaultStreamedContent(stream,
                "application/vnd.ms-excel", docName);
    }

    /**
     * Genera el excel de datos de contactos de los proveedores.
     * @return excel
     * @throws Exception e
     */
    public StreamedContent getExportarDatosContactos() throws Exception {

        creaFiltros(true);
        InputStream stream = new ByteArrayInputStream(ngService.exportarDatosContactos(filtros));
        String docName = "Contactos";

        docName = docName.concat(".xlsx");

        stream.close();

        LOGGER.debug("docname {}", docName);
        return new DefaultStreamedContent(stream,
                "application/vnd.ms-excel", docName);
    }

    /**
     * Genera el excel de datos de convenios de los proveedores.
     * @return excel
     * @throws Exception e
     */
    public StreamedContent getExportarDatosConvenios() throws Exception {

        creaFiltros(true);
        InputStream stream = new ByteArrayInputStream(ngService.exportarDatosConvenios(filtros));
        String docName = "Convenios";

        docName = docName.concat(".xlsx");

        stream.close();

        LOGGER.debug("docname {}", docName);
        return new DefaultStreamedContent(stream,
                "application/vnd.ms-excel", docName);
    }

    /**
     * Proveedor de Servicio.
     * @return Proveedor
     */
    public Proveedor getProveedorSeleccionado() {
        return proveedorSeleccionado;
    }

    /**
     * Proveedor de Servicio.
     * @param proveedorSeleccionado Proveedor
     */
    public void setProveedorSeleccionado(Proveedor proveedorSeleccionado) {
        this.proveedorSeleccionado = proveedorSeleccionado;
    }

    /**
     * Tipo de Servicio.
     * @return TipoServicio
     */
    public TipoServicio getTipoServicioSeleccionado() {
        return tipoServicioSeleccionado;
    }

    /**
     * Tipo de Servicio.
     * @param tipoServicioSeleccionado TipoServicio
     */
    public void setTipoServicioSeleccionado(TipoServicio tipoServicioSeleccionado) {
        this.tipoServicioSeleccionado = tipoServicioSeleccionado;
    }

    /**
     * Tipo de PST.
     * @return TipoProveedor
     */
    public TipoProveedor getTipoProveedorSeleccionado() {
        return tipoProveedorSeleccionado;
    }

    /**
     * Tipo de PST.
     * @param tipoProveedorSeleccionado TipoProveedor
     */
    public void setTipoProveedorSeleccionado(TipoProveedor tipoProveedorSeleccionado) {
        this.tipoProveedorSeleccionado = tipoProveedorSeleccionado;
    }

    /**
     * Tipo de Red.
     * @return TipoRed
     */
    public TipoRed getTipoRedSeleccionada() {
        return tipoRedSeleccionada;
    }

    /**
     * Tipo de Red.
     * @param tipoRedSeleccionada TipoRed
     */
    public void setTipoRedSeleccionada(TipoRed tipoRedSeleccionada) {
        this.tipoRedSeleccionada = tipoRedSeleccionada;
    }

    /**
     * Estatus del proveedor.
     * @return Estatus
     */
    public Estatus getEstatusProveedorSeleccionado() {
        return estatusProveedorSeleccionado;
    }

    /**
     * Estatus del proveedor.
     * @param estatusProveedorSeleccionado Estatus
     */
    public void setEstatusProveedorSeleccionado(Estatus estatusProveedorSeleccionado) {
        this.estatusProveedorSeleccionado = estatusProveedorSeleccionado;
    }

    /**
     * Listado de Proveedores resultado.
     * @return List<Proveedor>
     */
    public List<Proveedor> getListadoProveedores() {
        return listadoProveedores;
    }

    /**
     * Listado de Proveedores resultado.
     * @param listadoProveedores List<Proveedor>
     */
    public void setListadoProveedores(List<Proveedor> listadoProveedores) {
        this.listadoProveedores = listadoProveedores;
    }

    /**
     * Listado de Tipos de Servicio.
     * @return List<TipoServicio>
     */
    public List<TipoServicio> getListadoTiposServicio() {
        return listadoTiposServicio;
    }

    /**
     * Listado de Tipos de Servicio.
     * @param listadoTiposServicio List<TipoServicio>
     */
    public void setListadoTiposServicio(List<TipoServicio> listadoTiposServicio) {
        this.listadoTiposServicio = listadoTiposServicio;
    }

    /**
     * Listado de Tipos de PST.
     * @return List<TipoProveedor>
     */
    public List<TipoProveedor> getListadoTipoPst() {
        return listadoTipoPst;
    }

    /**
     * Listado de Tipos de PST.
     * @param listadoTipoPst List<TipoProveedor>
     */
    public void setListadoTipoPst(List<TipoProveedor> listadoTipoPst) {
        this.listadoTipoPst = listadoTipoPst;
    }

    /**
     * Listado de los Tipos de Red.
     * @return List<TipoRed>
     */
    public List<TipoRed> getListadoTiposRed() {
        return listadoTiposRed;
    }

    /**
     * Listado de los Tipos de Red.
     * @param listadoTiposRed List<TipoRed>
     */
    public void setListadoTiposRed(List<TipoRed> listadoTiposRed) {
        this.listadoTiposRed = listadoTiposRed;
    }

    /**
     * Listado de estatus del Proveedor.
     * @return List<Estatus>
     */
    public List<Estatus> getListadoEstatus() {
        return listadoEstatus;
    }

    /**
     * Listado de estatus del Proveedor.
     * @param listadoEstatus List<Estatus>
     */
    public void setListadoEstatus(List<Estatus> listadoEstatus) {
        this.listadoEstatus = listadoEstatus;
    }

    /**
     * Modelo de datos para Lazy Loading en las tablas.
     * @return ProveedoresLazyModel
     */
    public ProveedoresLazyModel getProveedoresLazyModel() {
        return proveedoresLazyModel;
    }

    /**
     * Modelo de datos para Lazy Loading en las tablas.
     * @param proveedoresLazyModel ProveedoresLazyModel
     */
    public void setProveedoresLazyModel(ProveedoresLazyModel proveedoresLazyModel) {
        this.proveedoresLazyModel = proveedoresLazyModel;
    }

    /**
     * Filtros de búsqueda de Proveedores.
     * @return FiltroBusquedaProveedores
     */
    public FiltroBusquedaProveedores getFiltros() {
        return filtros;
    }

    /**
     * Filtros de búsqueda de Proveedores.
     * @param filtros FiltroBusquedaProveedores
     */
    public void setFiltros(FiltroBusquedaProveedores filtros) {
        this.filtros = filtros;
    }

    /**
     * Proveedor seleccionado en la tabla de búsqueda.
     * @return Proveedor
     */
    public Proveedor getProveedorEdicionSeleccionado() {
        return proveedorEdicionSeleccionado;
    }

    /**
     * Proveedor seleccionado en la tabla de búsqueda.
     * @param proveedorEdicionSeleccionado Proveedor
     */
    public void setProveedorEdicionSeleccionado(Proveedor proveedorEdicionSeleccionado) {
        this.proveedorEdicionSeleccionado = proveedorEdicionSeleccionado;
    }

    /**
     * Bean de la edición del proveedor.
     * @return GestionProveedorBean
     */
    public GestionProveedorBean getGestionProveedorBean() {
        return gestionProveedorBean;
    }

    /**
     * Bean de la edición del proveedor.
     * @param gestionProveedorBean GestionProveedorBean
     */
    public void setGestionProveedorBean(GestionProveedorBean gestionProveedorBean) {
        this.gestionProveedorBean = gestionProveedorBean;
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
     * Activa el botón exportar si no hay resultados en la búsqueda.
     * @return the emptySearch
     */
    public boolean isEmptySearch() {
        return emptySearch;
    }

}
