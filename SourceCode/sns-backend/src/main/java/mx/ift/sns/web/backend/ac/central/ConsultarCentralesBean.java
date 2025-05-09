package mx.ift.sns.web.backend.ac.central;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.central.Central;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.central.VCatalogoCentral;
import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCentrales;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.web.backend.ac.estado.ConsultarEstadosBean;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.lazymodels.CentralLazyModel;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** ManagedBean. */
@ManagedBean(name = "consultarCentralesBean")
@ViewScoped
public class ConsultarCentralesBean implements Serializable {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarEstadosBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Id de errores. */
    private static final String MSG_ID = "MSG_ConsultarCentrales";

    /** Estado. */
    private Estado estadoMun;

    /** Municipio. */
    private Municipio municipio;

    /** Población. */
    private Poblacion poblacion;

    /** Lista Estado. */
    private List<Estado> listaEstados;

    /** Lista Municipio. */
    private List<Municipio> listaMunicipio;

    /** Lista Población. */
    private List<Poblacion> listaPoblacion;

    /** Proveedor seleccionado. */
    private Proveedor proveedorSeleccionado;

    /** abn. **/
    private String abn;

    /** Lista de proveedores disponibles para seleccionar. */
    private List<Proveedor> listaProveedores;

    /** Central. */
    private Central central;

    /** Resultado para la paginación. */
    private CentralLazyModel centralModel;

    /** Central seleccionada en la tabla de búsqueda. */
    private VCatalogoCentral centralSeleccionadaEdicion;

    /** Filtros para realizar la búsqueda. */
    private FiltroBusquedaCentrales filtros;

    /** Servicio de Organizacion Territorial. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade adminCatFacadeService;

    /** Referencia al Bean de nueva central. */
    @ManagedProperty("#{nuevaCentralBean}")
    private NuevaCentralBean nuevaCentralBean;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Activa el botón exportar si no hay resultados en la búsqueda. */
    private boolean emptySearch = true;

    /** Constructor, por defecto vacío. */
    public ConsultarCentralesBean() {
    }

    /**
     * Iniciamos la listaAsignacion y la lista proveedores y cargamos los combos.
     * @throws Exception error en inicio
     **/
    @PostConstruct
    public void init() throws Exception {
        try {
            // Listado de estados
            listaEstados = adminCatFacadeService.findAllEstados();

            // Catálogo de Proveedores
            listaProveedores = adminCatFacadeService.findAllProveedoresActivos();

            // Carga Lazy y Filtros de búsqueda
            filtros = new FiltroBusquedaCentrales();

            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(adminCatFacadeService,
                    Parametro.REGISTROS_POR_PAGINA_BACK));
            registroPorPagina = filtros.getResultadosPagina();

            centralModel = new CentralLazyModel();
            centralModel.setService(adminCatFacadeService);
            centralModel.setFiltros(filtros);
            centralModel.setMessagesId(MSG_ID);
            this.emptySearch = adminCatFacadeService.findAllCentralesCount(filtros) == 0;
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error inesperado: " + e.getMessage());
            }
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Rellena la lista de municipios por Estado seleccionado.
     * @throws Exception excepcion
     */
    public void habilitarMunicipio() throws Exception {
        if (estadoMun != null) {
            listaMunicipio = adminCatFacadeService.findMunicipiosByEstado(estadoMun.getCodEstado());
        } else {
            listaMunicipio = null;
            listaPoblacion = null;
        }
    }

    /**
     * Rellena la lista de poblaciones por estado y municipio.
     * @throws Exception excepcion
     */
    public void habilitarPoblacion() throws Exception {
        if (municipio != null) {
            listaPoblacion = adminCatFacadeService.findAllPoblaciones(estadoMun.getCodEstado(),
                    municipio.getId().getCodMunicipio());
        } else {
            listaPoblacion = null;
        }
    }

    /**
     * Método que realiza la búsqueda de las centrales.
     */
    public void realizarBusqueda() {
        try {
            if (centralModel == null) {
                filtros = new FiltroBusquedaCentrales();
                centralModel = new CentralLazyModel();
                centralModel.setFiltros(filtros);
                centralModel.setService(adminCatFacadeService);
                centralModel.setMessagesId(MSG_ID);
            }

            centralModel.clear();
            creaFiltros();

            PaginatorUtil.resetPaginacion("FRM_ConsultarCentrales:TBL_Centrales",
                    filtros.getResultadosPagina());
            this.emptySearch = adminCatFacadeService.findAllCentralesCount(filtros) == 0;
        } catch (Exception e) {
            MensajesBean.addErrorMsg(MSG_ID, "Error: " + e.getMessage(), "");
        }
    }

    /**
     * Método que setea los filtros de búsqueda.
     */
    private void creaFiltros() {
        // Resetamos los filtros
        filtros.clear();

        filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(adminCatFacadeService,
                Parametro.REGISTROS_POR_PAGINA_BACK));

        if (abn != null && !abn.isEmpty()) {
            if (isNumeric(abn)) {
                filtros.setAbns(new BigDecimal(abn));
            } else {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("catalogo.centrales.abn"), "");
                return;
            }
        }
        if (estadoMun != null && municipio == null && poblacion == null) {
            filtros.setEstadoMun(estadoMun);
        } else if (estadoMun != null && municipio != null && poblacion == null) {
            filtros.setEstadoMun(estadoMun);
            filtros.setMunicipio(municipio);
        } else if (estadoMun != null && municipio != null && poblacion != null) {
            filtros.setPoblacion(poblacion);
        }
        if (central != null) {
            filtros.setNombre(central.getNombre());
        }
        filtros.setProveedor(proveedorSeleccionado);

    }

    /**
     * Método para saber si es numerica la cadena.
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

    /**
     * Método que realiza que limpia el formulario.
     */
    public void limpiarBusqueda() {
        this.emptySearch = true;
        this.estadoMun = null;
        this.municipio = null;
        this.poblacion = null;
        this.abn = null;
        this.proveedorSeleccionado = null;
        this.centralModel = null;
        this.central = null;
        if (this.listaMunicipio != null) {
            listaMunicipio.clear();
        }
        if (this.listaPoblacion != null) {
            listaPoblacion.clear();
        }
    }

    /**
     * Método que elimina la central seleccionada.
     */
    public void eliminarCentral() {
        try {
            if (centralSeleccionadaEdicion != null) {
                Central centralAux = adminCatFacadeService.findCentralById(centralSeleccionadaEdicion.getId());
                if (centralAux != null) {
                    if (centralAux.getEstatus().getCdg().equals(Estatus.ACTIVO)) {
                        adminCatFacadeService.bajaCentral(centralAux);
                        MensajesBean.addInfoMsg(MSG_ID, "Central dada de baja.", "");
                    } else {
                        MensajesBean.addInfoMsg(MSG_ID, "No se puede dar de baja una central en estado "
                                + centralAux.getEstatus().getDescripcion(), "");
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004.toString(), "");
        }
    }

    /**
     * Método que edita los datos de la central seleccionada.
     */
    public void editarCentral() {
        try {
            if (centralSeleccionadaEdicion != null) {
                nuevaCentralBean.cargarCentral(centralSeleccionadaEdicion.getId());
                nuevaCentralBean.setEditar(true);
            }
        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004.toString(), "");
        }
    }

    /**
     * Autocomplete del combo de centrales.
     * @param query nombre central
     * @return lista central
     * @throws Exception error
     */
    public List<Central> completeCentral(String query) throws Exception {

        List<Central> result = new ArrayList<Central>();

        result = adminCatFacadeService.findAllCentralesByName(query);

        return result;

    }

    /**
     * Método que realiza el export a excel de los resultados de la consulta.
     * @return excel
     * @throws Exception Exception
     */
    public StreamedContent getExportarConsultaCentrales() throws Exception {

        creaFiltros();
        InputStream stream = new
                ByteArrayInputStream(adminCatFacadeService.getExportConsultaCatalogoCentrales(filtros));
        String docName = "Catálogo_Centrales";

        docName = docName.concat(".xlsx");

        stream.close();

        LOGGER.debug("docname {}", docName);
        return new DefaultStreamedContent(stream,
                "application/vnd.ms-excel", docName);
    }

    /**
     * Reset.
     */
    public void reset() {
        nuevaCentralBean.resetTab();
    }

    /**
     * Lista de proveedores disponibles para seleccionar.
     * @return List<Proveedor>
     */
    public List<Proveedor> getListaProveedores() {
        return listaProveedores;
    }

    /**
     * Lista de proveedores disponibles para seleccionar.
     * @param listaProveedores List<Proveedor>
     */
    public void setListaProveedores(List<Proveedor> listaProveedores) {
        this.listaProveedores = listaProveedores;
    }

    /**
     * Estado.
     * @return Estado
     */
    public Estado getEstadoMun() {
        return estadoMun;
    }

    /**
     * Estado.
     * @param estadoMun Estado
     */
    public void setEstadoMun(Estado estadoMun) {
        this.estadoMun = estadoMun;
    }

    /**
     * Municipio.
     * @return Municipio
     */
    public Municipio getMunicipio() {
        return municipio;
    }

    /**
     * Municipio.
     * @param municipio Municipio
     */
    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    /**
     * Población.
     * @return Poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * Población.
     * @param poblacion Poblacion
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * Lista Estado.
     * @return List<Estado>
     */
    public List<Estado> getListaEstados() {
        return listaEstados;
    }

    /**
     * Lista Estado.
     * @param listaEstados List<Estado>
     */
    public void setListaEstados(List<Estado> listaEstados) {
        this.listaEstados = listaEstados;
    }

    /**
     * Lista Municipio.
     * @return List<Municipio>
     */
    public List<Municipio> getListaMunicipio() {
        return listaMunicipio;
    }

    /**
     * Lista Municipio.
     * @param listaMunicipio List<Municipio>
     */
    public void setListaMunicipio(List<Municipio> listaMunicipio) {
        this.listaMunicipio = listaMunicipio;
    }

    /**
     * Lista Población.
     * @return List<Poblacion>
     */
    public List<Poblacion> getListaPoblacion() {
        return listaPoblacion;
    }

    /**
     * Lista Población.
     * @param listaPoblacion List<Poblacion>
     */
    public void setListaPoblacion(List<Poblacion> listaPoblacion) {
        this.listaPoblacion = listaPoblacion;
    }

    /**
     * abn.
     * @return String
     */
    public String getAbn() {
        return abn;
    }

    /**
     * abn.
     * @param abn String
     */
    public void setAbn(String abn) {
        this.abn = abn;
    }

    /**
     * Servicio de Organizacion Territorial.
     * @return IAdminCatalogosFacade
     */
    public IAdminCatalogosFacade getAdminCatFacadeService() {
        return adminCatFacadeService;
    }

    /**
     * Servicio de Organizacion Territorial.
     * @param adminCatFacadeService IAdminCatalogosFacade
     */
    public void setAdminCatFacadeService(IAdminCatalogosFacade adminCatFacadeService) {
        this.adminCatFacadeService = adminCatFacadeService;
    }

    /**
     * Id de errores.
     * @return String
     */
    public static String getMsgId() {
        return MSG_ID;
    }

    /**
     * Proveedor seleccionado.
     * @return Proveedor
     */
    public Proveedor getProveedorSeleccionado() {
        return proveedorSeleccionado;
    }

    /**
     * Proveedor seleccionado.
     * @param proveedorSeleccionado Proveedor
     */
    public void setProveedorSeleccionado(Proveedor proveedorSeleccionado) {
        this.proveedorSeleccionado = proveedorSeleccionado;
    }

    /**
     * Central.
     * @return Central
     */
    public Central getCentral() {
        return central;
    }

    /**
     * Central.
     * @param central Central
     */
    public void setCentral(Central central) {
        this.central = central;
    }

    /**
     * Resultado para la paginación.
     * @return CentralLazyModel
     */
    public CentralLazyModel getCentralModel() {
        return centralModel;
    }

    /**
     * Resultado para la paginación.
     * @param centralModel CentralLazyModel
     */
    public void setCentralModel(CentralLazyModel centralModel) {
        this.centralModel = centralModel;
    }

    /**
     * Central seleccionada en la tabla de búsqueda.
     * @return VCatalogoCentral
     */
    public VCatalogoCentral getCentralSeleccionadaEdicion() {
        return centralSeleccionadaEdicion;
    }

    /**
     * Central seleccionada en la tabla de búsqueda.
     * @param centralSeleccionadaEdicion VCatalogoCentral
     */
    public void setCentralSeleccionadaEdicion(VCatalogoCentral centralSeleccionadaEdicion) {
        this.centralSeleccionadaEdicion = centralSeleccionadaEdicion;
    }

    /**
     * Filtros para realizar la búsqueda.
     * @return FiltroBusquedaCentrales
     */
    public FiltroBusquedaCentrales getFiltros() {
        return filtros;
    }

    /**
     * Filtros para realizar la búsqueda.
     * @param filtros FiltroBusquedaCentrales
     */
    public void setFiltros(FiltroBusquedaCentrales filtros) {
        this.filtros = filtros;
    }

    /**
     * Referencia al Bean de nueva central.
     * @return NuevaCentralBean
     */
    public NuevaCentralBean getNuevaCentralBean() {
        return nuevaCentralBean;
    }

    /**
     * Referencia al Bean de nueva central.
     * @param nuevaCentralBean NuevaCentralBean
     */
    public void setNuevaCentralBean(NuevaCentralBean nuevaCentralBean) {
        this.nuevaCentralBean = nuevaCentralBean;
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
     * Activa el botón exportar si no hay resultados en la búsqueda. @return the emptySearch
     */
    public boolean isEmptySearch() {
        return emptySearch;
    }

}
