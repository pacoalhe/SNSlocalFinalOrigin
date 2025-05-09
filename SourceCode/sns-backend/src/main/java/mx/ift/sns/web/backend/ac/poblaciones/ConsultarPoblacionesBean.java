package mx.ift.sns.web.backend.ac.poblaciones;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.modelo.filtros.FiltroBusquedaPoblaciones;
import mx.ift.sns.modelo.ot.DetallePoblacion;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.lazymodels.PoblacionesLazyModel;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Consulta del catálogo de Poblaciones.
 */
/** ManagedBean. */
@ManagedBean(name = "consultarPoblacionesBean")
@ViewScoped
public class ConsultarPoblacionesBean implements Serializable {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarPoblacionesBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Id de errores. */
    private static final String MSG_ID = "MSG_ConsultarPoblaciones";

    /** Filtros de búsqueda de Poblaciones. */
    private FiltroBusquedaPoblaciones filtros;

    /** Resultado para la paginación. */
    private PoblacionesLazyModel poblacionesLazyModel;

    /** Estado. */
    private Estado estadoSeleccionado;

    /** Municipio. */
    private Municipio municipioSeleccionado;

    /** ABN. */
    private Integer abnSeleccionado;

    /** Población. */
    private Poblacion poblacionSeleccionada;

    /** Catálogo de Estados. */
    private List<Estado> listaEstados;

    /** Listado de Municipios del estadoSeleccinado. */
    private List<Municipio> listaMunicipios;

    /** Catálogo de Poblaciones. */
    private List<Poblacion> listaPoblaciones;

    /** Poblacion seleccionada en la tabla de búsqueda. */
    private DetallePoblacion poblacionSeleccionadaEdicion;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Activa el botón exportar si no hay resultados en la búsqueda. */
    private boolean emptySearch = true;

    /** Constructor, por defecto vacío. */
    public ConsultarPoblacionesBean() {
    }

    /** Servicio de Numeración Geográfica. */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    /** Servicio de Organizacion Territorial. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade adminCatFacadeService;

    /** Referencia al Bean de Nueva Poblacion. */
    @ManagedProperty("#{nuevaPoblacionBean}")
    private NuevaPoblacionBean nuevaPoblacionBean;

    /** Referencia al Bean de Editar Poblacion. */
    @ManagedProperty("#{editarPoblacionBean}")
    private EditarPoblacionBean editarPoblacionBean;

    /**
     * Iniciamos la lista de Estados y cargamos el combo.
     **/
    @PostConstruct
    public void init() {
        try {
            // Listado de estados
            listaEstados = adminCatFacadeService.findAllEstados();
            // Inicialización del desplegable de municipios
            listaMunicipios = new ArrayList<Municipio>(1);

            // Filtros de búsqueda.
            filtros = new FiltroBusquedaPoblaciones();

            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(adminCatFacadeService,
                    Parametro.REGISTROS_POR_PAGINA_BACK));
            registroPorPagina = filtros.getResultadosPagina();

            // Carga Lazy
            poblacionesLazyModel = new PoblacionesLazyModel();
            poblacionesLazyModel.setService(adminCatFacadeService);
            poblacionesLazyModel.setFiltros(filtros);
            this.emptySearch = adminCatFacadeService.findAllPoblacionesCount(filtros) == 0;
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error inesperado: " + e.getMessage());
            }
            LOGGER.error("error", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);

            MensajesBean.addInfoMsg(MSG_ID,
                    MensajesBean.getTextoResource("errorGenerico"));
        }
    }

    /** Método invocado al seleccionar un estado del combo de estados. */
    public void seleccionEstado() {
        try {
            if (estadoSeleccionado != null) {
                // Municipios del estado seleccionado
                listaMunicipios = ngService
                        .findMunicipiosByEstado(estadoSeleccionado.getCodEstado());
                this.municipioSeleccionado = null;
                this.poblacionSeleccionada = null;
                this.listaPoblaciones = new ArrayList<Poblacion>(1);
            } else {
                // Inicialización de los desplegables de municipios y poblaciones
                listaMunicipios = new ArrayList<Municipio>(1);
                listaPoblaciones = new ArrayList<Poblacion>(1);
            }
        } catch (Exception ex) {
            LOGGER.error("error", ex);
        }
    }

    /** Método invocado al seleccionar un municipio del combo de municipios. */
    public void seleccionMunicipio() {

        try {
            if (municipioSeleccionado != null) {
                // Poblaciones del municipio seleccionado
                listaPoblaciones = adminCatFacadeService.findAllPoblaciones(
                        estadoSeleccionado.getCodEstado(), municipioSeleccionado.getId().getCodMunicipio());
                poblacionSeleccionada = null;
            } else {
                listaPoblaciones = null;
            }
        } catch (Exception ex) {
            LOGGER.error("error", ex);
        }
    }

    /**
     * Método invocado al pulsar el botón 'buscar' sobre el buscador de poblaciones.
     */
    public void realizarBusqueda() {
        try {
            if (poblacionesLazyModel == null) {
                filtros = new FiltroBusquedaPoblaciones();
                poblacionesLazyModel = new PoblacionesLazyModel();
                poblacionesLazyModel.setService(adminCatFacadeService);
                poblacionesLazyModel.setFiltros(filtros);
                poblacionesLazyModel.setMessagesId(MSG_ID);
            }
            poblacionesLazyModel.clear();
            creaFiltros();

            PaginatorUtil.resetPaginacion("FRM_ConsultarPoblaciones:TBL_Poblaciones",
                    filtros.getResultadosPagina());
            this.emptySearch = adminCatFacadeService.findAllPoblacionesCount(filtros) == 0;
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error inesperado: " + e.getMessage());
            }
            MensajesBean.addInfoMsg(MSG_ID,
                    MensajesBean.getTextoResource("errorGenerico"));
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

        // Filtro del estado
        if (estadoSeleccionado != null) {
            filtros.setEstado(estadoSeleccionado);
        }
        // Filtro del municipio
        if (municipioSeleccionado != null) {
            filtros.setMunicipio(municipioSeleccionado);
        }
        // Filtro de ABN
        if (abnSeleccionado != null) {
            filtros.setIdAbn(abnSeleccionado);
        }
        // Filtro de población
        if (poblacionSeleccionada != null) {
            filtros.setPoblacion(poblacionSeleccionada);
        }
    }

    /**
     * Método invocado al pulsar el botón 'limpiar' sobre el buscador de poblaciones.
     */
    public void limpiarBusqueda() {
        // this.filtros.clear();
        this.estadoSeleccionado = null;
        this.municipioSeleccionado = null;
        this.abnSeleccionado = null;
        this.poblacionSeleccionada = null;
        this.poblacionesLazyModel = null;
        this.emptySearch = true;
        // Inicialización de los desplegables de municipios y poblaciones
        listaMunicipios = new ArrayList<Municipio>(1);
        listaPoblaciones = new ArrayList<Poblacion>(1);
    }

    /**
     * Método que edita los datos de la población seleccionada.
     */
    public void editarPoblacion() {
        try {
            if (poblacionSeleccionadaEdicion != null) {
                editarPoblacionBean.cargarPoblacion(poblacionSeleccionadaEdicion.getPoblacion().getInegi());
            }
        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método que da de baja la poblacion seleccionada.
     */
    public void bajaPoblacion() {
        try {
            if (!poblacionSeleccionadaEdicion.getNumeracionAsignada().equals("SI")) {
                Poblacion poblacion = poblacionSeleccionadaEdicion.getPoblacion();

                Estatus status = new Estatus();
                status.setCdg(Estatus.INACTIVO);
                poblacion.setEstatus(status);
                poblacion = adminCatFacadeService.savePoblacion(poblacion);
                MensajesBean.addInfoMsg(MSG_ID, MensajesBean.getTextoResource("catalogo.poblaciones.baja.info"));
            } else {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("catalogo.poblaciones.numeraciones.asignadas.error"));
            }
        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Exporta los datos de la Consulta en un fichero Excel.
     * @return StreamedContent fichero con el listado de poblaciones
     */
    public StreamedContent getExportConsultaPoblaciones() {
        try {

            LOGGER.debug("getExportConsultaPoblaciones");
            creaFiltros();

            InputStream stream = new ByteArrayInputStream(
                    adminCatFacadeService.getExportConsultaCatalogoPoblaciones(filtros));

            String docName = "Catálogo_Poblaciones";

            docName = docName.concat(".xlsx");

            stream.close();

            LOGGER.debug("docname {}", docName);
            return new DefaultStreamedContent(stream,
                    "application/vnd.ms-excel", docName);
        } catch (Exception e) {
            LOGGER.error("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
        return null;
    }

    /**
     * Reset.
     */
    public void reset() {
        nuevaPoblacionBean.resetCampos();
        editarPoblacionBean.resetCampos();
    }

    /**
     * Estado.
     * @return Estado
     */
    public Estado getEstadoSeleccionado() {
        return estadoSeleccionado;
    }

    /**
     * Estado.
     * @param estadoSeleccionado Estado
     */
    public void setEstadoSeleccionado(Estado estadoSeleccionado) {
        this.estadoSeleccionado = estadoSeleccionado;
    }

    /**
     * Municipio.
     * @return Municipio
     */
    public Municipio getMunicipioSeleccionado() {
        return municipioSeleccionado;
    }

    /**
     * Municipio.
     * @param municipioSeleccionado Municipio
     */
    public void setMunicipioSeleccionado(Municipio municipioSeleccionado) {
        this.municipioSeleccionado = municipioSeleccionado;
    }

    /**
     * ABN.
     * @return Integer
     */
    public Integer getAbnSeleccionado() {
        return abnSeleccionado;
    }

    /**
     * ABN.
     * @param abnSeleccionado Integer
     */
    public void setAbnSeleccionado(Integer abnSeleccionado) {
        this.abnSeleccionado = abnSeleccionado;
    }

    /**
     * Población.
     * @return Poblacion
     */
    public Poblacion getPoblacionSeleccionada() {
        return poblacionSeleccionada;
    }

    /**
     * Población.
     * @param poblacionSeleccionada Poblacion
     */
    public void setPoblacionSeleccionada(Poblacion poblacionSeleccionada) {
        this.poblacionSeleccionada = poblacionSeleccionada;
    }

    /**
     * Poblacion seleccionada en la tabla de búsqueda.
     * @return DetallePoblacion
     */
    public DetallePoblacion getPoblacionSeleccionadaEdicion() {
        return poblacionSeleccionadaEdicion;
    }

    /**
     * Poblacion seleccionada en la tabla de búsqueda.
     * @param poblacionSeleccionadaEdicion DetallePoblacion
     */
    public void setPoblacionSeleccionadaEdicion(DetallePoblacion poblacionSeleccionadaEdicion) {
        this.poblacionSeleccionadaEdicion = poblacionSeleccionadaEdicion;
    }

    /**
     * Catálogo de Estados.
     * @return List<Estado>
     */
    public List<Estado> getListaEstados() {
        return listaEstados;
    }

    /**
     * Catálogo de Estados.
     * @param listaEstados List<Estado>
     */
    public void setListaEstados(List<Estado> listaEstados) {
        this.listaEstados = listaEstados;
    }

    /**
     * Listado de Municipios del estadoSeleccinado.
     * @return List<Municipio>
     */
    public List<Municipio> getListaMunicipios() {
        return listaMunicipios;
    }

    /**
     * Listado de Municipios del estadoSeleccinado.
     * @param listaMunicipios List<Municipio>
     */
    public void setListaMunicipios(List<Municipio> listaMunicipios) {
        this.listaMunicipios = listaMunicipios;
    }

    /**
     * Catálogo de Poblaciones.
     * @return List<Poblacion>
     */
    public List<Poblacion> getListaPoblaciones() {
        return listaPoblaciones;
    }

    /**
     * Catálogo de Poblaciones.
     * @param listaPoblaciones List<Poblacion>
     */
    public void setListaPoblaciones(List<Poblacion> listaPoblaciones) {
        this.listaPoblaciones = listaPoblaciones;
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
     * Resultado para la paginación.
     * @return PoblacionesLazyModel
     */
    public PoblacionesLazyModel getPoblacionesModel() {
        return poblacionesLazyModel;
    }

    /**
     * Resultado para la paginación.
     * @param poblacionesModel PoblacionesLazyModel
     */
    public void setPoblacionesModel(PoblacionesLazyModel poblacionesModel) {
        this.poblacionesLazyModel = poblacionesModel;
    }

    /**
     * Referencia al Bean de Nueva Poblacion.
     * @return NuevaPoblacionBean
     */
    public NuevaPoblacionBean getNuevaPoblacionBean() {
        return nuevaPoblacionBean;
    }

    /**
     * Referencia al Bean de Nueva Poblacion.
     * @param nuevaPoblacionBean NuevaPoblacionBean
     */
    public void setNuevaPoblacionBean(NuevaPoblacionBean nuevaPoblacionBean) {
        this.nuevaPoblacionBean = nuevaPoblacionBean;
    }

    /**
     * Referencia al Bean de Editar Poblacion.
     * @return EditarPoblacionBean
     */
    public EditarPoblacionBean getEditarPoblacionBean() {
        return editarPoblacionBean;
    }

    /**
     * Referencia al Bean de Editar Poblacion.
     * @param editarPoblacionBean EditarPoblacionBean
     */
    public void setEditarPoblacionBean(EditarPoblacionBean editarPoblacionBean) {
        this.editarPoblacionBean = editarPoblacionBean;
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
