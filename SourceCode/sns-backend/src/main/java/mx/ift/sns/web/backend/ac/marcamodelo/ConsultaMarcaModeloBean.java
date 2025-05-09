package mx.ift.sns.web.backend.ac.marcamodelo;

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

import mx.ift.sns.modelo.central.Marca;
import mx.ift.sns.modelo.central.Modelo;
import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.modelo.filtros.FiltroBusquedaMarcaModelo;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.lazymodels.MarcaModeloLazyModel;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase de soporte para el catálogo de Marcas y Modelos. */
@ManagedBean(name = "consultaMarcaModeloBean")
@ViewScoped
public class ConsultaMarcaModeloBean implements Serializable {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultaMarcaModeloBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_Generales";

    /** Facade de servicios Administración de catálogo. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade adminCatalogosFacade;

    /** Referencia al Bean de Solicitud de Asignaciones. */
    @ManagedProperty("#{marcaBean}")
    private MarcaBean marcaBean;

    /** Marca seleccionada. */
    private BigDecimal marcaSeleccionada;

    /** Modelo seleccionado. */
    private BigDecimal modeloSeleccionado;

    /** Lista de marcas disponibles para seleccionar. */
    private List<Marca> listaMarcas;

    /** Lista de modelos disponibles para seleccionar. */
    private List<Modelo> listaModelos;

    /** Resultado para la paginación. */
    private MarcaModeloLazyModel marcasModel;

    /** Filtros de búsqueda. */
    private FiltroBusquedaMarcaModelo filtros;

    /** MarcaModeloSeleccionada. */
    private Modelo marcaModeloSeleccionadoEdicion;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Activa el botón exportar si no hay resultados en la búsqueda. */
    private boolean emptySearch = true;

    /**
     * Iniciamos la pantalla cargando el combo de marcas.
     * @throws Exception error en inicio
     **/
    @PostConstruct
    public void init() throws Exception {

        LOGGER.debug("");

        try {
            marcaSeleccionada = null;
            modeloSeleccionado = null;
            // this.realizarBusqueda();

            marcaBean.setMarca(new Marca());

            listaModelos = new ArrayList<Modelo>(1);
            // Catálogo de Marcas
            listaMarcas = adminCatalogosFacade.findAllMarcas();

            filtros = new FiltroBusquedaMarcaModelo();
            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(adminCatalogosFacade,
                    Parametro.REGISTROS_POR_PAGINA_BACK));
            registroPorPagina = filtros.getResultadosPagina();

            marcasModel = new MarcaModeloLazyModel();
            marcasModel.setFiltros(filtros);
            marcasModel.setService(adminCatalogosFacade);
            this.emptySearch = adminCatalogosFacade.findAllMarcasCount(filtros) == 0;
        } catch (Exception ex) {
            LOGGER.error("error", ex);
        }
    }

    /**
     * Reseta los valores.
     */
    public void resetPantalla() {

        LOGGER.debug("");
        emptySearch = true;
        marcaSeleccionada = null;
        modeloSeleccionado = null;
        listaModelos = null;
        marcasModel = null;
        filtros.clear();
    }

    /**
     * Carga la marca.
     * @param idMarca is de la marca a cargar
     */
    public void cargarMarca(BigDecimal idMarca) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("solicitud {}", idMarca);
        }

        try {
            marcaBean.cargarMarca(idMarca);

        } catch (Exception e) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al seleccionar una marca del combo de marcas. */
    public void seleccionMarca() {

        LOGGER.debug("");

        try {
            if (marcaSeleccionada != null) {
                // Modelos
                listaModelos = adminCatalogosFacade.getModelosByMarca(marcaSeleccionada);
                modeloSeleccionado = null;
            } else {
                listaModelos = new ArrayList<Modelo>(1);
            }
        } catch (Exception ex) {
            LOGGER.error("error", ex);
        }
    }

    /**
     * Método invocado al pulsar el botón 'buscar' sobre el buscador de solicitudes.
     */
    public void realizarBusqueda() {

        if (marcasModel == null) {
            marcasModel = new MarcaModeloLazyModel();
            filtros = new FiltroBusquedaMarcaModelo();
            marcasModel.setFiltros(filtros);
            marcasModel.setService(adminCatalogosFacade);
        }

        // Resetamos los filtros
        filtros.clear();

        filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(adminCatalogosFacade,
                Parametro.REGISTROS_POR_PAGINA_BACK));

        if (null != modeloSeleccionado) {
            filtros.setIdModelo(modeloSeleccionado);
        } else if (null != marcaSeleccionada) {
            filtros.setIdMarca(marcaSeleccionada);
        }

        PaginatorUtil.resetPaginacion("FORM_MarcaModelo:TBL_Modelos",
                filtros.getResultadosPagina());

        marcasModel.setFiltros(filtros);
        this.emptySearch = adminCatalogosFacade.findAllMarcasCount(filtros) == 0;
    }

    /**
     * actualiza los campos.
     */
    public void actualizaCampos() {
        LOGGER.debug("");

        try {
            listaMarcas = adminCatalogosFacade.findAllMarcas();
            marcaBean.setMarca(new Marca());

        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
    }

    /**
     * Lista de marcas disponibles para seleccionar.
     * @return listaMarcas Catálogo de Marcas
     */
    public List<Marca> getListaMarcas() {
        return listaMarcas;
    }

    /**
     * Lista de marcas disponibles para seleccionar.
     * @param listaMarcas Catálogo de Marcas
     */
    public void setListaMarcas(List<Marca> listaMarcas) {
        this.listaMarcas = listaMarcas;
    }

    /**
     * Lista de modelos disponibles para seleccionar.
     * @return listaModelos Catálogo de Modelos
     */
    public List<Modelo> getListaModelos() {
        return listaModelos;
    }

    /**
     * Lista de modelos disponibles para seleccionar.
     * @param listaModelos Catálogo de Modelos
     */
    public void setListaModelo(List<Modelo> listaModelos) {
        this.listaModelos = listaModelos;
    }

    /**
     * Marca seleccionada.
     * @return BigDecimal
     */
    public BigDecimal getMarcaSeleccionada() {
        return marcaSeleccionada;
    }

    /**
     * Marca seleccionada.
     * @param marcaSeleccionada BigDecimal
     */
    public void setMarcaSeleccionada(BigDecimal marcaSeleccionada) {
        this.marcaSeleccionada = marcaSeleccionada;
    }

    /**
     * Modelo seleccionado.
     * @return BigDecimal
     */
    public BigDecimal getModeloSeleccionado() {
        return modeloSeleccionado;
    }

    /**
     * Modelo seleccionado.
     * @param modeloSeleccionado BigDecimal
     */
    public void setModeloSeleccionado(BigDecimal modeloSeleccionado) {
        this.modeloSeleccionado = modeloSeleccionado;
    }

    /**
     * Resultado para la paginación.
     * @return MarcaModeloLazyModel
     */
    public MarcaModeloLazyModel getMarcasModel() {
        return marcasModel;
    }

    /**
     * Resultado para la paginación.
     * @param marcasModel MarcaModeloLazyModel
     */
    public void setMarcasModel(MarcaModeloLazyModel marcasModel) {
        this.marcasModel = marcasModel;
    }

    /**
     * MarcaModeloSeleccionada.
     * @return Modelo
     */
    public Modelo getMarcaModeloSeleccionadoEdicion() {
        return marcaModeloSeleccionadoEdicion;
    }

    /**
     * MarcaModeloSeleccionada.
     * @param marcaModeloSeleccionadoEdicion Modelo
     */
    public void setMarcaModeloSeleccionadoEdicion(Modelo marcaModeloSeleccionadoEdicion) {
        this.marcaModeloSeleccionadoEdicion = marcaModeloSeleccionadoEdicion;
    }

    /**
     * Referencia al Bean de Solicitud de Asignaciones.
     * @return MarcaBean
     */
    public MarcaBean getMarcaBean() {
        return marcaBean;
    }

    /**
     * Referencia al Bean de Solicitud de Asignaciones.
     * @param marcaBean MarcaBean
     */
    public void setMarcaBean(MarcaBean marcaBean) {
        this.marcaBean = marcaBean;
    }

    /**
     * Método que realiza el export a excel de los resultados de la consulta.
     * @return excel
     * @throws Exception Exception
     */
    public StreamedContent getExportarDatos() {
        try {

            filtros.setUsarPaginacion(false);
            List<Marca> listaMarca = adminCatalogosFacade.findAllMarcasEager(filtros);
            InputStream stream = new ByteArrayInputStream(
                    adminCatalogosFacade.getExportConsultaCatalogoMarcas(listaMarca));
            String docName = "Datos Generales Marcas";

            docName = docName.concat(".xlsx");

            stream.close();

            LOGGER.debug("docname {}", docName);
            return new DefaultStreamedContent(stream,
                    "application/vnd.ms-excel", docName);
        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
        return null;
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
