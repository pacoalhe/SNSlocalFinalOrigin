package mx.ift.sns.web.backend.ac.estado;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.lazymodels.EstadoLazyModel;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** ManagedBean. */
@ManagedBean(name = "consultarEstadosBean")
@ViewScoped
public class ConsultarEstadosBean implements Serializable {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarEstadosBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Id de errores. */
    private static final String MSG_ID = "MSG_ConsultarEstados";

    /** Id de errores en la edición. */
    private static final String MSG_ID_EDICION = "MSG_Estado_Edit";

    /** Estado seleccionado en la tabla de búsqueda. */
    private Estado estadoSeleccionadoEdicion;

    /** Catálogo de Estados. */
    private List<Estado> listaEstados;

    /** Estado seleccionado en el filtro. */
    private Estado estadoSeleccionado;

    /** Resultado para la paginación. */
    private EstadoLazyModel estadosModel;

    /** Listado de Municipios de estadoSeleccinado. */
    private List<Municipio> listaMunicipios;

    /** Activa el botón exportar si no hay resultados en la búsqueda. */
    private boolean emptySearch = true;

    /** Constructor, por defecto vacío. */
    public ConsultarEstadosBean() {
    }

    /** Servicio de Organizacion Territorial. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade adminCatFacadeService;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /**
     * Iniciamos la listaAsignacion y la lista proveedores y cargamos los combos.
     * @throws Exception error en inicio
     **/
    @PostConstruct
    public void init() throws Exception {

        // Listado de estados
        listaEstados = adminCatFacadeService.findAllEstados();
        estadoSeleccionado = new Estado();

        // Carga Lazy y Filtros de búsqueda
        estadosModel = new EstadoLazyModel();
        estadosModel.setService(adminCatFacadeService);
        estadosModel.setEstadoSeleccionado(estadoSeleccionado);
        estadosModel.setRegistroPorPagina(PaginatorUtil.getRegistrosPorPagina(adminCatFacadeService,
                Parametro.REGISTROS_POR_PAGINA_BACK));
        registroPorPagina = estadosModel.getRegistroPorPagina();
        emptySearch = adminCatFacadeService.findAllEstadosCount() == 0;
    }

    /**
     * Método invocado al pulsar el botón 'buscar' sobre el buscador de solicitudes.
     */
    public void realizarBusqueda() {
        try {
            if (estadosModel == null) {
                estadosModel = new EstadoLazyModel();
                estadosModel.setService(adminCatFacadeService);
            }
            estadosModel.setEstadoSeleccionado(this.estadoSeleccionado);
            estadosModel.setRegistroPorPagina(PaginatorUtil.getRegistrosPorPagina(adminCatFacadeService,
                    Parametro.REGISTROS_POR_PAGINA_BACK));
            PaginatorUtil.resetPaginacion("FRM_ConsultarEstados:TBL_Estados",
                    estadosModel.getRegistroPorPagina());
            emptySearch = adminCatFacadeService.findAllEstadosCount() == 0;
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error inesperado: " + e.getMessage());
            }
            MensajesBean.addInfoMsg(MSG_ID,
                    MensajesBean.getTextoResource("errorGenerico"));
        }
    }

    /**
     * Método invocado al pulsar el botón 'limpiar' sobre el buscador de solicitudes.
     */
    public void limpiarBusqueda() {

        LOGGER.debug("");
        this.emptySearch = true;
        this.estadoSeleccionado = null;
        this.estadosModel = null;
    }

    /**
     * Busqueda de Municipios de un Estado.
     */
    public void buscarMunicipios() {
        try {
            if (estadoSeleccionadoEdicion != null) {
                listaMunicipios = adminCatFacadeService.
                        findMunicipiosByEstado(estadoSeleccionadoEdicion.getCodEstado());
            }
        } catch (Exception e) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }

    }

    /**
     * Guarda los datos del Estado Editado.
     * @return boolean
     */
    public boolean guardarCambios() {
        try {
            // Guardamos los cambios
            estadoSeleccionadoEdicion = adminCatFacadeService.saveEstado(estadoSeleccionadoEdicion);
            MensajesBean.addInfoMsg(MSG_ID_EDICION, "Cambios guardados.", "");
            return true;
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID_EDICION, Errores.ERROR_0015);
            return false;
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error inesperado: " + e.getMessage());
            }
            MensajesBean.addErrorMsg(MSG_ID_EDICION, Errores.ERROR_0004);
            return false;
        }
    }

    /**
     * @return fichero con el listado de Estados y municipios
     * @throws Exception exception
     */
    public StreamedContent getReportEstados() throws Exception {

        LOGGER.debug("getReportEstados");
        List<Estado> dataSourceListado = new ArrayList<Estado>(1);

        if (null != estadoSeleccionado) {
            dataSourceListado.add(estadoSeleccionado);
        } else {
            dataSourceListado.addAll(listaEstados);
        }
        InputStream stream = new ByteArrayInputStream(adminCatFacadeService.generarListadoEstados(dataSourceListado));
        String docName = "LISTADO_ESTADOS";

        docName = docName.concat(".xlsx");

        stream.close();

        LOGGER.debug("docname {}", docName);
        return new DefaultStreamedContent(stream,
                "application/vnd.ms-excel", docName);
    }

    /**
     * Estado seleccionado en la tabla de búsqueda.
     * @return Estado
     */
    public Estado getEstadoSeleccionadoEdicion() {
        return estadoSeleccionadoEdicion;
    }

    /**
     * Estado seleccionado en la tabla de búsqueda.
     * @param estadoSeleccionado Estado
     */
    public void setEstadoSeleccionadoEdicion(Estado estadoSeleccionado) {
        this.estadoSeleccionadoEdicion = estadoSeleccionado;
    }

    /**
     * Estado seleccionado en el filtro.
     * @return Estado
     */
    public Estado getEstadoSeleccionado() {
        return estadoSeleccionado;
    }

    /**
     * Estado seleccionado en el filtro.
     * @param estadoSeleccionado Estado
     */
    public void setEstadoSeleccionado(Estado estadoSeleccionado) {
        this.estadoSeleccionado = estadoSeleccionado;
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
     * @return EstadoLazyModel
     */
    public EstadoLazyModel getEstadosModel() {
        return estadosModel;
    }

    /**
     * Resultado para la paginación.
     * @param estadosModel EstadoLazyModel
     */
    public void setEstadosModel(EstadoLazyModel estadosModel) {
        this.estadosModel = estadosModel;
    }

    /**
     * Listado de Municipios de estadoSeleccinado.
     * @return List<Municipio>
     */
    public List<Municipio> getListaMunicipios() {
        return listaMunicipios;
    }

    /**
     * Listado de Municipios de estadoSeleccinado.
     * @param listaMunicipios List<Municipio>
     */
    public void setListaMunicipios(List<Municipio> listaMunicipios) {
        this.listaMunicipios = listaMunicipios;
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
