package mx.ift.sns.web.backend.ac.abn;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import mx.ift.sns.modelo.filtros.FiltroBusquedaABNs;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.lazymodels.AbnLazyModel;
import mx.ift.sns.web.backend.lazymodels.PoblacionAbnLazyModel;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Backing Bean para consulta de catálogo de Abns. */
@ManagedBean(name = "consultarCatalogoAbnBean")
@ViewScoped
public class ConsultarCatalogoAbnBean implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarCatalogoAbnBean.class);

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_CatalogoABN";

    /**
     * Tipo de búsqueda por ABN: Todos los estados, municipios y poblaciones asociados a la ABN.
     */
    private static final String BUSQUEDA_ABN = "Cod_ABN";

    /**
     * Tipo de búsqueda por Población ABN: Todas las ABN's que tengan relación con el estado, municipios y poblaciones
     * seleccionada.
     */
    private static final String BUSQUEDA_POB_ABN = "Pob_ABN";

    /** Facade de Catálogos. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade adminCatFacadeService;

    /** Bean de Edición de ABNs. */
    @ManagedProperty("#{edicionAbnBean}")
    private EdicionAbnBean edicionAbnBean;

    /** Código de ABN. */
    private String codigoAbn;

    /** Estado Seleccionado. */
    private Estado estadoSeleccionado;

    /** Catálogo de Estados. */
    private List<Estado> listaEstados;

    /** Municipio seleccionado. */
    private Municipio municipioSeleccionado;

    /** Catálogo de Municipios. */
    private List<Municipio> listaMunicipios;

    /** Población seleccionada. */
    private Poblacion poblacionSeleccionada;

    /** Catálogo de Poblaciones. */
    private List<Poblacion> listaPoblaciones;

    /** Presuscripción seleccionada. */
    private boolean presuscripcionSeleccionada;

    /** Estatus ABN seleccionado. */
    private EstadoAbn estatusAbnSeleccionado;

    /** Lista de Estatus de ABN. */
    private List<EstadoAbn> listaEstatus;

    /** ABN seleccionado para edición. */
    private Abn abnSeleccionado;

    /** Lista de Poblaciones ABN Lazy. */
    private PoblacionAbnLazyModel poblacionAbnModel;

    /** Lista de Abns Lazy. */
    private AbnLazyModel catAbnModel;

    /** Filtros de búsqueda sobre ABN. */
    private FiltroBusquedaABNs filtros;

    /** Lista de tipos de búsqueda de ABN's. */
    private List<String> listaTiposBusqueda;

    /** Tipo de Búsqueda seleccionada. */
    private String tipoBusqueda;

    /** Indica si el tipo de búsqueda es por Código de Abn. */
    private boolean tipoBusquedaCodAbn = false;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Activa el botón exportar si no hay resultados en la búsqueda. */
    private boolean emptySearch = true;

    /** Constructor vacío. */
    public ConsultarCatalogoAbnBean() {
    }

    /** Inicialización de Variables. JSR260, Método invocado al cargar terminar de instanciar el bean. */
    @PostConstruct
    public void init() {
        try {
            // Combos de Selección
            listaEstados = adminCatFacadeService.findAllEstados();
            listaEstatus = adminCatFacadeService.findAllEstadosAbn();
            listaMunicipios = new ArrayList<Municipio>(1);
            listaPoblaciones = new ArrayList<Poblacion>(1);

            // Búsqueda Inicial por ABN's activos.
            EstadoAbn statusActivo = new EstadoAbn();
            statusActivo.setCodigo(EstadoAbn.ACTIVO);

            // // Búsqueda
            filtros = new FiltroBusquedaABNs();

            // filtros.setEstatusAbn(statusActivo);
            // poblacionAbnModel = new PoblacionAbnLazyModel();
            // poblacionAbnModel.setMessagesId(MSG_ID);
            // poblacionAbnModel.setService(adminCatFacadeService);
            // poblacionAbnModel.setFiltros(filtros);
            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(adminCatFacadeService,
                    Parametro.REGISTROS_POR_PAGINA_BACK));
            registroPorPagina = filtros.getResultadosPagina();

            // Tipo de Búsqueda por defecto
            tipoBusqueda = BUSQUEDA_POB_ABN;
            listaTiposBusqueda = new ArrayList<String>(2);
            listaTiposBusqueda.add(BUSQUEDA_POB_ABN);
            listaTiposBusqueda.add(BUSQUEDA_ABN);

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al seleccionar un Estado del combo. Actualiza la lista de municipios. */
    public void seleccionEstado() {
        try {
            if (estadoSeleccionado != null) {
                listaMunicipios = adminCatFacadeService.findMunicipiosByEstado(estadoSeleccionado.getCodEstado());
            } else {
                listaMunicipios.clear();
            }
            listaPoblaciones.clear();
            poblacionSeleccionada = null;
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al seleccionar un Municipio del combo. Actualiza la lista de poblaciones. */
    public void seleccionMunicipio() {
        try {
            if (municipioSeleccionado != null) {
                listaPoblaciones = adminCatFacadeService.findAllPoblaciones(
                        estadoSeleccionado.getCodEstado(),
                        municipioSeleccionado.getId().getCodMunicipio());
            } else {
                listaPoblaciones.clear();
            }
            poblacionSeleccionada = null;
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado pulsar sobre el botón "Buscar". Utiliza los filtros definidos en el interfaz para realizar una
     * búsqueda específica.
     */
    public void realizarBusqueda() {
        try {
            if (tipoBusquedaCodAbn) {
                // Búsqueda por Cod ABN: todos los estados, municipios y poblaciones asociados a la ABN
                if (poblacionAbnModel == null) {
                    filtros = new FiltroBusquedaABNs();
                    poblacionAbnModel = new PoblacionAbnLazyModel();
                    poblacionAbnModel.setService(adminCatFacadeService);
                    poblacionAbnModel.setFiltros(filtros);
                }
                filtros.clear();
                poblacionAbnModel.clear();
            } else {
                // Búsqueda por OT: Todas las ABN’s que tengan relación con el estado, municipios y poblaciones
                // seleccionada
                if (catAbnModel == null) {
                    filtros = new FiltroBusquedaABNs();
                    catAbnModel = new AbnLazyModel();
                    catAbnModel.setService(adminCatFacadeService);
                    catAbnModel.setFiltros(filtros);
                }
                filtros.clear();
                catAbnModel.clear();
            }

            filtros.setEstado(estadoSeleccionado);
            filtros.setMunicipio(municipioSeleccionado);
            filtros.setPoblacion(poblacionSeleccionada);
            filtros.setEstatusAbn(estatusAbnSeleccionado);
            filtros.setPoblacionAncla(poblacionSeleccionada);
            filtros.setPresuscripcion(presuscripcionSeleccionada);

            if (!StringUtils.isEmpty(codigoAbn)) {
                filtros.setCodigoAbn(new BigDecimal(codigoAbn));
            }

            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(adminCatFacadeService,
                    Parametro.REGISTROS_POR_PAGINA_BACK));

            PaginatorUtil.resetPaginacion("FRM_CatalogoABN:TBL_ConsultaABNs",
                    filtros.getResultadosPagina());
            if (tipoBusquedaCodAbn) {
                emptySearch = (adminCatFacadeService.findAllPoblacionesAbnCount(filtros) == 0);
            } else {
                emptySearch = (adminCatFacadeService.findAllAbnsCount(filtros) == 0);
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al pulsar sobre el botón "Limpiar". Deja todos los campos de búsqueda del interfaz con sus
     * valores por defecto.
     */
    public void limpiarBusqueda() {
        this.filtros.clear();
        this.codigoAbn = null;
        this.abnSeleccionado = null;
        this.poblacionAbnModel = null;
        this.catAbnModel = null;
        this.estadoSeleccionado = null;
        this.estatusAbnSeleccionado = null;
        this.listaMunicipios.clear();
        this.listaPoblaciones.clear();
        this.municipioSeleccionado = null;
        this.poblacionSeleccionada = null;
        this.presuscripcionSeleccionada = false;
        this.tipoBusqueda = BUSQUEDA_POB_ABN;
        this.tipoBusquedaCodAbn = false;
        this.emptySearch = true;
    }

    /**
     * Método invocado pulsar sobre el botón 'Editar'. Abre la modal de edición de un ABN con la información del ABN
     * seleccionado cargada por defecto.
     */
    public void editarAbn() {
        edicionAbnBean.cargarAbn(abnSeleccionado);
    }

    /**
     * Método invocado al cambiar sobre el combo de tipo de búsqueda. Resetea los valores de búsqueda e inicializa los
     * campos con los valores por defecto según el tipo de búsqueda seleccionado.
     */
    public void seleccionTipoBusqueda() {
        tipoBusquedaCodAbn = tipoBusqueda.equals(BUSQUEDA_ABN);
        this.codigoAbn = null;
        this.abnSeleccionado = null;
        this.estadoSeleccionado = null;
        this.estatusAbnSeleccionado = null;
        this.listaMunicipios.clear();
        this.listaPoblaciones.clear();
        this.municipioSeleccionado = null;
        this.poblacionSeleccionada = null;
        this.presuscripcionSeleccionada = false;
        this.catAbnModel = null;
        this.poblacionAbnModel = null;
        this.emptySearch = true;
    }

    /**
     * Exporta los datos de la Consulta en un fichero Excel.
     * @return StreamedContent Objeto StreamedContent con el fichero Excel serializado.
     */
    public StreamedContent getExportarConsultaAbns() {
        try {
            // Lista de Abns
            // Se permite la exportación de ABN's sin filtros de búsqueda, aunque tarde muchísimo.
            if (filtros == null) {
                filtros = new FiltroBusquedaABNs();
            }

            // Documento
            LOGGER.debug("Listado INI: {}", new Date());
            InputStream stream =
                    new ByteArrayInputStream(adminCatFacadeService.getExportConsultaCatalogoABNs(filtros));
            LOGGER.debug("Listado FIN: {}", new Date());
            stream.close();

            StringBuilder docName = new StringBuilder();
            docName.append("Catálogo_ABN").append(".xlsx");

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Documento generado: {}", docName.toString());
            }

            StreamedContent fichero = new DefaultStreamedContent(
                    stream, "application/vnd.ms-excel", docName.toString());

            return fichero;
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
        return null;
    }

    // GETTERS & SETTERS

    /**
     * Código de ABN seleccionado para la búsqueda.
     * @return String
     */
    public String getCodigoAbn() {
        return codigoAbn;
    }

    /**
     * Código de ABN seleccionado para la búsqueda.
     * @param codigoAbn String
     */
    public void setCodigoAbn(String codigoAbn) {
        this.codigoAbn = codigoAbn;
    }

    /**
     * Estado (OT) Seleccionado para la búsqueda.
     * @return Estado
     */
    public Estado getEstadoSeleccionado() {
        return estadoSeleccionado;
    }

    /**
     * Estado (OT) Seleccionado para la búsqueda.
     * @param estadoSeleccionado Estado
     */
    public void setEstadoSeleccionado(Estado estadoSeleccionado) {
        this.estadoSeleccionado = estadoSeleccionado;
    }

    /**
     * Municipio (OT) Seleccionado para la búsqueda.
     * @return Municipio
     */
    public Municipio getMunicipioSeleccionado() {
        return municipioSeleccionado;
    }

    /**
     * Municipio (OT) Seleccionado para la búsqueda.
     * @param municipioSeleccionado Municipio
     */
    public void setMunicipioSeleccionado(Municipio municipioSeleccionado) {
        this.municipioSeleccionado = municipioSeleccionado;
    }

    /**
     * Población (OT) Seleccionado para la búsqueda.
     * @return Poblacion
     */
    public Poblacion getPoblacionSeleccionada() {
        return poblacionSeleccionada;
    }

    /**
     * Población (OT) Seleccionado para la búsqueda.
     * @param poblacionSeleccionada Poblacion
     */
    public void setPoblacionSeleccionada(Poblacion poblacionSeleccionada) {
        this.poblacionSeleccionada = poblacionSeleccionada;
    }

    /**
     * Presuscripción seleccionada para la búsqueda.
     * @return boolean
     */
    public boolean isPresuscripcionSeleccionada() {
        return presuscripcionSeleccionada;
    }

    /**
     * Presuscripción seleccionada para la búsqueda.
     * @param presuscripcionSeleccionada boolean
     */
    public void setPresuscripcionSeleccionada(boolean presuscripcionSeleccionada) {
        this.presuscripcionSeleccionada = presuscripcionSeleccionada;
    }

    /**
     * Estatus de ABN seleccionado para la búsqueda.
     * @return EstadoAbn
     */
    public EstadoAbn getEstatusAbnSeleccionado() {
        return estatusAbnSeleccionado;
    }

    /**
     * Estatus de ABN seleccionado para la búsqueda.
     * @param estatusAbnSeleccionado EstadoAbn
     */
    public void setEstatusAbnSeleccionado(EstadoAbn estatusAbnSeleccionado) {
        this.estatusAbnSeleccionado = estatusAbnSeleccionado;
    }

    /**
     * ABN Seleccionado para edición.
     * @return Abn
     */
    public Abn getAbnSeleccionado() {
        return abnSeleccionado;
    }

    /**
     * ABN Seleccionado para edición.
     * @param abnSeleccionado Abn
     */
    public void setAbnSeleccionado(Abn abnSeleccionado) {
        this.abnSeleccionado = abnSeleccionado;
    }

    /**
     * Lista de Estados (OT).
     * @return List
     */
    public List<Estado> getListaEstados() {
        return listaEstados;
    }

    /**
     * Lista de Municipio (OT).
     * @return List
     */
    public List<Municipio> getListaMunicipios() {
        return listaMunicipios;
    }

    /**
     * Lista de Poblaciones (OT).
     * @return List
     */
    public List<Poblacion> getListaPoblaciones() {
        return listaPoblaciones;
    }

    /**
     * Lista de Estatus de ABN.
     * @return List
     */
    public List<EstadoAbn> getListaEstatus() {
        return listaEstatus;
    }

    /**
     * Bean de Edición de ABNs.
     * @return EdicionAbnBean
     */
    public EdicionAbnBean getEdicionAbnBean() {
        return edicionAbnBean;
    }

    /**
     * Bean de Edición de ABNs.
     * @param edicionAbnBean EdicionAbnBean
     */
    public void setEdicionAbnBean(EdicionAbnBean edicionAbnBean) {
        this.edicionAbnBean = edicionAbnBean;
    }

    /**
     * Tipo de Búsqueda seleccionada.
     * @return String
     */
    public String getTipoBusqueda() {
        return tipoBusqueda;
    }

    /**
     * Tipo de Búsqueda seleccionada.
     * @param tipoBusqueda String
     */
    public void setTipoBusqueda(String tipoBusqueda) {
        this.tipoBusqueda = tipoBusqueda;
    }

    /**
     * Lista de tipos de búsqueda de ABN's.
     * @return the listaTiposBusqueda
     */
    public List<String> getListaTiposBusqueda() {
        return listaTiposBusqueda;
    }

    /**
     * Indica si el tipo de búsqueda es por Código de Abn.
     * @return boolean
     */
    public boolean isTipoBusquedaCodAbn() {
        return tipoBusquedaCodAbn;
    }

    /**
     * Lista de Poblaciones ABN Lazy.
     * @return the poblacionAbnModel
     */
    public PoblacionAbnLazyModel getPoblacionAbnModel() {
        return poblacionAbnModel;
    }

    /**
     * Lista de Abns Lazy.
     * @return AbnLazyModel
     */
    public AbnLazyModel getCatAbnModel() {
        return catAbnModel;
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
     * @return emptySearch
     */
    public boolean isEmptySearch() {
        return emptySearch;
    }
}
