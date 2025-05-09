package mx.ift.sns.web.backend.ac.oficios;

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

import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.modelo.filtros.FiltroBusquedaPlantillas;
import mx.ift.sns.modelo.oficios.Plantilla;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.lazymodels.PlantillasLazyModel;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Bean para consulta de catálogo de Plantillas de Oficio. */
@ManagedBean(name = "catalogoPlantillasBean")
@ViewScoped
public class ConsultarCatalogoPlantillasBean implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarCatalogoPlantillasBean.class);

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_CatalogoPlantillas";

    /** Facade de Catálogos. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade adminCatFacadeService;

    /** Bean de Edición de Plantillas. */
    @ManagedProperty("#{edicionPlantillaBean}")
    private EdicionPlantillaBean edicionPlantillaBean;

    /** Lista de Tipos de Destinatario Disponibles. */
    private List<TipoDestinatario> tiposDestinatario;

    /** Tipo de Destinatario seleccionado para la búsqueda. */
    private TipoDestinatario destinatarioSeleccionado;

    /** Lista de Tipos de Solicitud Disponibles. */
    private List<TipoSolicitud> tiposSolicitud;

    /** Tipo de Solicitud seleccionada para la búsqueda. */
    private TipoSolicitud tramiteSeleccionado;

    /** Plantilla Seleccionada para Edición. */
    private Plantilla plantillaSeleccionada;

    /** Modelo Lazy para carga de plantillas. */
    private PlantillasLazyModel plantillasModel;

    /** Filtros de búsqueda de Plantillas. */
    private FiltroBusquedaPlantillas filtros;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Activa el botón exportar si no hay resultados en la búsqueda. */
    private boolean emptySearch = true;

    /** Constructor vacío. */
    public ConsultarCatalogoPlantillasBean() {
    }

    /** Inicialización de Variables. JSR260, Método invocado al cargar terminar de instanciar el bean. */
    @PostConstruct
    public void init() {
        try {
            // No todos los tipos de solicitud tienen oficios.
            // tiposSolicitud = adminCatFacadeService.findAllTiposSolicitud();

            // Los agregamos a mano
            tiposSolicitud = new ArrayList<TipoSolicitud>(14);

            // Trámites de Numeración Geográfica.
            tiposSolicitud.add(adminCatFacadeService.getTipoSolicitudById(TipoSolicitud.ASIGNACION));
            tiposSolicitud.add(adminCatFacadeService.getTipoSolicitudById(TipoSolicitud.CESION_DERECHOS));
            tiposSolicitud.add(adminCatFacadeService.getTipoSolicitudById(TipoSolicitud.LIBERACION));
            tiposSolicitud.add(adminCatFacadeService.getTipoSolicitudById(TipoSolicitud.REDISTRIBUCION));

            // Trámites de Numeración No Geográfica.
            tiposSolicitud.add(adminCatFacadeService.getTipoSolicitudById(TipoSolicitud.ASIGNACION_NNG));
            tiposSolicitud.add(adminCatFacadeService.getTipoSolicitudById(TipoSolicitud.CESION_DERECHOS_NNG));
            tiposSolicitud.add(adminCatFacadeService.getTipoSolicitudById(TipoSolicitud.LIBERACION_NNG));
            tiposSolicitud.add(adminCatFacadeService.getTipoSolicitudById(TipoSolicitud.REDISTRIBUCION_NNG));

            // Trámites de Códigos de Puntos Nacionales.
            tiposSolicitud.add(adminCatFacadeService.getTipoSolicitudById(TipoSolicitud.ASIGNACION_CPSN));
            tiposSolicitud.add(adminCatFacadeService.getTipoSolicitudById(TipoSolicitud.CESION_CPSN));
            tiposSolicitud.add(adminCatFacadeService.getTipoSolicitudById(TipoSolicitud.LIBERACION_CPSN));

            // Trámites de Códigos de Puntos Internacionales.
            tiposSolicitud.add(adminCatFacadeService.getTipoSolicitudById(TipoSolicitud.ASIGNACION_CPSI));
            tiposSolicitud.add(adminCatFacadeService.getTipoSolicitudById(TipoSolicitud.CESION_CPSI));
            tiposSolicitud.add(adminCatFacadeService.getTipoSolicitudById(TipoSolicitud.LIBERACION_CPSI));
            tiposSolicitud.add(adminCatFacadeService.getTipoSolicitudById(TipoSolicitud.SOLICITUD_CPSI_UIT));

            // Tipos de Destinatario
            tiposDestinatario = adminCatFacadeService.findAllTiposDestinatario();

            // Filtros
            filtros = new FiltroBusquedaPlantillas();

            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(adminCatFacadeService,
                    Parametro.REGISTROS_POR_PAGINA_BACK));
            registroPorPagina = filtros.getResultadosPagina();

            plantillasModel = new PlantillasLazyModel();
            plantillasModel.setFiltros(filtros);
            plantillasModel.setFacade(adminCatFacadeService);
            emptySearch = (adminCatFacadeService.findAllPlantillasCount(filtros) == 0);

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado pulsar sobre el botón "Búsqueda". */
    public void realizarBusqueda() {
        try {
            if (plantillasModel == null) {
                plantillasModel = new PlantillasLazyModel();
                plantillasModel.setFiltros(filtros);
                plantillasModel.setFacade(adminCatFacadeService);
            }
            // Resetamos los filtros
            filtros.clear();
            plantillasModel.clear();

            // Asignamos la búsqueda
            filtros.setTipoDestinatario(destinatarioSeleccionado);
            filtros.setTipoSolicitud(tramiteSeleccionado);

            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(adminCatFacadeService,
                    Parametro.REGISTROS_POR_PAGINA_BACK));
            PaginatorUtil.resetPaginacion("FRM_CatalogoPlantillas:TBL_ConsultaPlantillas",
                    filtros.getResultadosPagina());
            emptySearch = (adminCatFacadeService.findAllPlantillasCount(filtros) == 0);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado pulsar sobre el botón "Limpiar". */
    public void limpiarBusqueda() {
        // Resetamos los filtros
        filtros.clear();
        // Limpiamos los campos
        this.destinatarioSeleccionado = null;
        this.tramiteSeleccionado = null;
        this.plantillasModel = null;
        this.emptySearch = true;
    }

    /** Método invocado pulsar sobre el botón 'Editar'. */
    public void editarPlantilla() {
        edicionPlantillaBean.cargarPlantilla(plantillaSeleccionada);
    }

    /**
     * Exporta los datos de la Consulta en un fichero Excel.
     * @return StreamedContent del fichero.
     * @throws Exception en Caso de Error.
     */
    public StreamedContent getExportarConsultaPlantillas() {
        try {
            // Listado de Plantillas
            filtros.setUsarPaginacion(false);
            List<Plantilla> listaPlantillas = adminCatFacadeService.findAllPlantillas(filtros);

            // Documento
            byte[] excelSerializado = adminCatFacadeService.getExportConsultaCatalogoPlantillas(listaPlantillas);
            InputStream stream = new ByteArrayInputStream(excelSerializado);
            stream.close();

            StringBuilder docName = new StringBuilder();
            docName.append("Catálogo_Plantillas").append(".xlsx");

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Documento generado: {}", docName.toString());
            }

            StreamedContent fichero = new DefaultStreamedContent(
                    stream, "application/vnd.ms-excel", docName.toString());

            return fichero;
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
        return null;
    }

    // GETTERS & SETTERS

    /**
     * Lista de Tipos de Destinatario Disponibles.
     * @return List
     */
    public List<TipoDestinatario> getTiposDestinatario() {
        return tiposDestinatario;
    }

    /**
     * Tipo de Destinatario seleccionado para la búsqueda.
     * @return TipoDestinatario
     */
    public TipoDestinatario getDestinatarioSeleccionado() {
        return destinatarioSeleccionado;
    }

    /**
     * Tipo de Destinatario seleccionado para la búsqueda.
     * @param destinatarioSeleccionado TipoDestinatario
     */
    public void setDestinatarioSeleccionado(TipoDestinatario destinatarioSeleccionado) {
        this.destinatarioSeleccionado = destinatarioSeleccionado;
    }

    /**
     * Tipo de Solicitud seleccionada para la búsqueda.
     * @return TipoSolicitud
     */
    public TipoSolicitud getTramiteSeleccionado() {
        return tramiteSeleccionado;
    }

    /**
     * Tipo de Solicitud seleccionada para la búsqueda.
     * @param tramiteSeleccionado TipoSolicitud
     */
    public void setTramiteSeleccionado(TipoSolicitud tramiteSeleccionado) {
        this.tramiteSeleccionado = tramiteSeleccionado;
    }

    /**
     * Lista de Tipos de Solicitud Disponibles.
     * @return List
     */
    public List<TipoSolicitud> getTiposSolicitud() {
        return tiposSolicitud;
    }

    /**
     * Plantilla Seleccionada para Edición.
     * @return Plantilla
     */
    public Plantilla getPlantillaSeleccionada() {
        return plantillaSeleccionada;
    }

    /**
     * Plantilla Seleccionada para Edición.
     * @param plantillaSeleccionada Plantilla
     */
    public void setPlantillaSeleccionada(Plantilla plantillaSeleccionada) {
        this.plantillaSeleccionada = plantillaSeleccionada;
    }

    /**
     * Bean de Edición de Plantillas.
     * @return EdicionPlantillaBean
     */
    public EdicionPlantillaBean getEdicionPlantillaBean() {
        return edicionPlantillaBean;
    }

    /**
     * Bean de Edición de Plantillas.
     * @param edicionPlantillaBean EdicionPlantillaBean
     */
    public void setEdicionPlantillaBean(EdicionPlantillaBean edicionPlantillaBean) {
        this.edicionPlantillaBean = edicionPlantillaBean;
    }

    /**
     * Modelo Lazy para carga de plantillas.
     * @return PlantillasLazyModel
     */
    public PlantillasLazyModel getPlantillasModel() {
        return plantillasModel;
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
