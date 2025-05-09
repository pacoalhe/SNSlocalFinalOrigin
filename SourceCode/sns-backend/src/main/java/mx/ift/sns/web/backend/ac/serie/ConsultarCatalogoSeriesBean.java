package mx.ift.sns.web.backend.ac.serie;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
import mx.ift.sns.modelo.filtros.FiltroBusquedaSeries;
import mx.ift.sns.modelo.ng.Serie;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.modelo.series.VCatalogoSerie;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.lazymodels.SeriesLazyModel;
import mx.ift.sns.web.backend.lazymodels.multiseleccion.MultiSelectionOnLazyModelManager;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Gestiona el buscador de series. */
@ManagedBean(name = "consultarCatalogoSeriesBean")
@ViewScoped
public class ConsultarCatalogoSeriesBean implements Serializable {
    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarCatalogoSeriesBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultarSeries";

    /** Servicio de Organizacion Territorial. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade adminCatFacadeService;

    /** Modelo de datos para Lazy Loading en las tablas. */
    private SeriesLazyModel seriesLazyModel;

    /** Código del Abn. */
    private String abn;

    /** Código de la serie. */
    private String sna;

    /** Nir. */
    private Nir nir;

    /** Estatus Serie. */
    private Estatus estatus;

    /** Listado de Estados de Serie. */
    private List<Estatus> listadoEstatus;

    /** Listado de Nirs. */
    private List<Nir> listadoNir;

    /** Filtros de búsqueda de Series. */
    private FiltroBusquedaSeries filtros;

    /** Lista de series seleccionadas para cambiar su estado. */
    // private List<VCatalogoSerie> serieSeleccionada;
    private VCatalogoSerie serieSeleccionada;

    /** Variable boolean que habilita o no el botón de cambiar estado. */
    private boolean habilitarBoton = false;

    /** Referencia al Bean de nueva central. */
    @ManagedProperty("#{expansionSeriesBean}")
    private ExpansionSeriesBean expansionSeriesBean;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Gestor de Selección Múltiple sobre la tabla Lazy. */
    private MultiSelectionOnLazyModelManager<VCatalogoSerie> multiSelectionManager;

    /** Activa el botón exportar si no hay resultados en la búsqueda. */
    private boolean emptySearch = true;

    /** Constructor por defecto. */
    public ConsultarCatalogoSeriesBean() {
    }

    /**
     * Método que inicializa los combos del buscador de proveedores.
     */
    @PostConstruct
    public void init() {
        try {
            abn = null;

            // Listado de nirs del abn
            listadoNir = adminCatFacadeService.findAllNirs();
            listadoEstatus = adminCatFacadeService.findAllEstatus();

            nir = null;
            sna = null;
            estatus = new Estatus();

            // Carga Lazy y Filtros de búsqueda.
            filtros = new FiltroBusquedaSeries();
            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(adminCatFacadeService,
                    Parametro.REGISTROS_POR_PAGINA_BACK));
            registroPorPagina = filtros.getResultadosPagina();
            multiSelectionManager = new MultiSelectionOnLazyModelManager<VCatalogoSerie>();
            seriesLazyModel = new SeriesLazyModel();
            seriesLazyModel.setFiltros(filtros);
            seriesLazyModel.setService(adminCatFacadeService);
            seriesLazyModel.setMessagesId(MSG_ID);
            seriesLazyModel.setMultiSelectionManager(multiSelectionManager);
            this.emptySearch = adminCatFacadeService.findAllCatalogoSeriesCount(filtros) == 0;
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error inesperado: " + e.getMessage());
            }
            MensajesBean.addInfoMsg(MSG_ID, MensajesBean.getTextoResource("errorGenerico"));
        }
    }

    /** Método invocado al pulsar el botón 'buscar'. */
    public void realizarBusqueda() {
        try {
            if (seriesLazyModel == null) {
                seriesLazyModel = new SeriesLazyModel();
                seriesLazyModel.setFiltros(filtros);
                seriesLazyModel.setService(adminCatFacadeService);
                seriesLazyModel.setMessagesId(MSG_ID);
                seriesLazyModel.setMultiSelectionManager(multiSelectionManager);
            }

            seriesLazyModel.clear();
            creaFiltros();
            // Limpiamos la selección previa
            multiSelectionManager.clear();

            PaginatorUtil.resetPaginacion("FRM_ConsultarSeries:TBL_Series",
                    filtros.getResultadosPagina());
            this.emptySearch = adminCatFacadeService.findAllCatalogoSeriesCount(filtros) == 0;
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error inesperado: " + e.getMessage());
            }
            MensajesBean.addInfoMsg(MSG_ID, MensajesBean.getTextoResource("errorGenerico"));
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

        // Filtro del abn
        if (abn != null && !abn.isEmpty()) {
            if (isNumeric(abn)) {
                filtros.setIdAbn(new BigDecimal(abn));
            } else {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("catalogo.centrales.abn"), "");
                return;
            }
        }
        // Filtro del nir
        if (nir != null) {
            filtros.setIdNir(new BigDecimal(nir.getCodigo()));
        }

        // Filtro del sna
        if (sna != null && !sna.isEmpty()) {
            filtros.setIdSna(new BigDecimal(sna));
        }

        // Filtro del estado
        if (estatus != null) {
            filtros.setEstatus(estatus.getDescripcion());
        }

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
     * Función que carga los nirs asociados al abn seleccionado.
     * @throws Exception exception
     */
    public void cargarNirs() throws Exception {
        if (!StringUtils.isEmpty(abn)) {
            listadoNir = adminCatFacadeService.findAllNirByAbn(new BigDecimal(abn));
        } else {
            listadoNir = adminCatFacadeService.findAllNirs();
        }
    }

    /**
     * Método que realiza que limpia el formulario.
     */
    public void limpiarBusqueda() {
        this.emptySearch = true;
        this.abn = null;
        this.estatus = null;
        this.nir = null;
        this.sna = null;
        this.habilitarBoton = false;
        this.serieSeleccionada = null;
        this.seriesLazyModel = null;
        multiSelectionManager.clear();
        try {
            this.listadoNir = adminCatFacadeService.findAllNirs();
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error inesperado: " + e.getMessage());
            }
            MensajesBean.addInfoMsg(MSG_ID, MensajesBean.getTextoResource("errorGenerico"));
        }
    }
    
    /**
     * Limpia el registros seleccionado en la tabla
     * al cerrar la ventana emergente de Expansion de Series.
     */
	public void limpiarSeleccionSerieTabla() {

		this.serieSeleccionada = null;
			multiSelectionManager.clear();

	}

    /***********************************/

    /**
     * Método invocado al seleccionar todas las filas de la tabla mediante el checkbox de cabecera.
     * @param event ToggleSelectEvent
     */
    public void deSeleccionSerie(UnselectEvent event) {
        try {
            // Actualizamos la selección múltiple en paginación
            serieSeleccionada = (VCatalogoSerie) event.getObject();
            int pagina = ((DataTable) event.getSource()).getPage();
            multiSelectionManager.updateSelection(serieSeleccionada, pagina, false);

            this.compruebaSeleccion();
            // Devuelve el último registro seleccionado o null
            serieSeleccionada = multiSelectionManager.getLastRegisterSelected();
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**************************************/

    /**
     * Método invocado al seleccionar todas las filas de la tabla mediante el checkbox de cabecera.
     * @param event ToggleSelectEvent
     */
    public void seleccionPagina(ToggleSelectEvent event) {
        try {
            // Actualizamos la selección múltiple en paginación
            int pagina = ((DataTable) event.getSource()).getPage();
            multiSelectionManager.toggleSelecction(pagina, event.isSelected());
            this.compruebaSeleccion();
            if (event.isSelected()) {
                serieSeleccionada = multiSelectionManager.getLastRegisterSelected();
            } else {
                serieSeleccionada = null;
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al seleccionar una fila de la tabla de Series.
     * @param event SelectEvent
     */
    public void seleccionSerie(SelectEvent event) {
        try {
            // Actualizamos la selección múltiple en paginación
            serieSeleccionada = (VCatalogoSerie) event.getObject();
            int pagina = ((DataTable) event.getSource()).getPage();
            multiSelectionManager.updateSelection(serieSeleccionada, pagina, true);
            this.compruebaSeleccion();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método que comprueba la lista y habilita el botón o no.
     */
    public void compruebaSeleccion() {
        if (multiSelectionManager != null && null != multiSelectionManager.getRegistrosSeleccionados()
                && !multiSelectionManager.getRegistrosSeleccionados().isEmpty()) {
            habilitarBoton = true;
        } else {
            habilitarBoton = false;
        }
    }

    /**
     * Método que cambia el estado de las series seleccionadas.
     */
    public void cambiarEstado() {
        boolean iguales = false;
        Serie serie = null;
        Nir nir = null;
        List<VCatalogoSerie> seriesSeleccionadas = multiSelectionManager.getRegistrosSeleccionados();
        try {
            nir = adminCatFacadeService.getNirByCodigo(Integer.parseInt(seriesSeleccionadas.get(0).getCdgNir()));
            serie = adminCatFacadeService.getSerie(new BigDecimal(seriesSeleccionadas.get(0).getIdSna()), nir.getId());
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error inesperado: " + e.getMessage());
            }
            MensajesBean.addInfoMsg(MSG_ID, MensajesBean.getTextoResource("errorGenerico"));
        }
        Estatus estado = serie.getEstatus();
        if (seriesSeleccionadas.size() > 1) {
            // La lista tiene más de un valor
            for (int i = 1; i < seriesSeleccionadas.size(); i++) {
                Serie serieAux = null;
                Nir nirAux = null;
                try {
                    nirAux = adminCatFacadeService
                            .getNirByCodigo(Integer.parseInt(seriesSeleccionadas.get(i).getCdgNir()));
                    serieAux = adminCatFacadeService.getSerie(new BigDecimal(seriesSeleccionadas.get(i).getIdSna()),
                            nirAux.getId());
                } catch (Exception e) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Error inesperado: " + e.getMessage());
                    }
                    MensajesBean.addInfoMsg(MSG_ID, MensajesBean.getTextoResource("errorGenerico"));
                }
                if (!estado.equals(serieAux.getEstatus())) {
                    iguales = false;
                    break;
                } else {
                    iguales = true;
                }
            }
        } else {
            // La lista sólo tiene un valor
            iguales = true;
        }
        if (iguales) {
            if (estado.getCdg().equals(Estatus.ACTIVO)) {
                for (VCatalogoSerie vSerie : seriesSeleccionadas) {
                    Serie serieAct = null;
                    Nir nirAct = null;
                    try {
                        nirAct = adminCatFacadeService.getNirByCodigo(Integer.parseInt(vSerie.getCdgNir()));
                        serieAct = adminCatFacadeService.getSerie(new BigDecimal(vSerie.getIdSna()), nirAct.getId());
                    } catch (Exception e) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Error inesperado: " + e.getMessage());
                        }
                        MensajesBean.addInfoMsg(MSG_ID, MensajesBean.getTextoResource("errorGenerico"));
                    }
                    Estatus estadoSerie = new Estatus();
                    estadoSerie.setCdg(Estatus.INACTIVO);
                    serieAct.setEstatus(estadoSerie);
                    adminCatFacadeService.saveSerie(serieAct);
                }
            } else {
                for (VCatalogoSerie vSerie : seriesSeleccionadas) {
                    Serie serieInac = null;
                    Nir nirInac = null;
                    try {
                        nirInac = adminCatFacadeService.getNirByCodigo(Integer.parseInt(vSerie.getCdgNir()));
                        serieInac = adminCatFacadeService.getSerie(new BigDecimal(vSerie.getIdSna()), nirInac.getId());
                    } catch (Exception e) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Error inesperado: " + e.getMessage());
                        }
                        MensajesBean.addInfoMsg(MSG_ID, MensajesBean.getTextoResource("errorGenerico"));
                    }
                    Estatus estadoSerie = new Estatus();
                    estadoSerie.setCdg(Estatus.ACTIVO);
                    serieInac.setEstatus(estadoSerie);
                    adminCatFacadeService.saveSerie(serieInac);
                }
            }
            multiSelectionManager.clear();
        } else {
            MensajesBean.addErrorMsg(MSG_ID, MensajesBean.getTextoResource("catalogo.series.error"));
        }
    }

    /**
     * Método que realiza el export a excel de los resultados de la consulta.
     * @return excel
     * @throws Exception Exception
     */
    public StreamedContent getExportarConsultaSeries() throws Exception {
        creaFiltros();
        InputStream stream = new
                ByteArrayInputStream(adminCatFacadeService.getExportConsultaCatalogoSeries(filtros));
        String docName = "Catálogo_Series";

        docName = docName.concat(".xlsx");

        stream.close();

        LOGGER.debug("docname {}", docName);
        return new DefaultStreamedContent(stream,
                "application/vnd.ms-excel", docName);
    }

    /**
     * Código del Abn.
     * @return String
     */
    public String getAbn() {
        return abn;
    }

    /**
     * Código del Abn.
     * @param abn String
     */
    public void setAbn(String abn) {
        this.abn = abn;
    }

    /**
     * Nir.
     * @return Nir
     */
    public Nir getNir() {
        return nir;
    }

    /**
     * Nir.
     * @param nir Nir
     */
    public void setNir(Nir nir) {
        this.nir = nir;
    }

    /**
     * Estatus Serie.
     * @return Estatus
     */
    public Estatus getEstatus() {
        return estatus;
    }

    /**
     * Estatus Serie.
     * @param estatus Estatus
     */
    public void setEstatus(Estatus estatus) {
        this.estatus = estatus;
    }

    /**
     * Listado de Estados de Serie.
     * @return List<Estatus>
     */
    public List<Estatus> getListadoEstatus() {
        return listadoEstatus;
    }

    /**
     * Listado de Estados de Serie.
     * @param listadoEstatus List<Estatus>
     */
    public void setListadoEstatus(List<Estatus> listadoEstatus) {
        this.listadoEstatus = listadoEstatus;
    }

    /**
     * Listado de Nirs.
     * @return List<Nir>
     */
    public List<Nir> getListadoNir() {
        return listadoNir;
    }

    /**
     * Listado de Nirs.
     * @param listadoNir List<Nir>
     */
    public void setListadoNir(List<Nir> listadoNir) {
        this.listadoNir = listadoNir;
    }

    /**
     * Filtros de búsqueda de Series.
     * @return FiltroBusquedaSeries
     */
    public FiltroBusquedaSeries getFiltros() {
        return filtros;
    }

    /**
     * Filtros de búsqueda de Series.
     * @param filtros FiltroBusquedaSeries
     */
    public void setFiltros(FiltroBusquedaSeries filtros) {
        this.filtros = filtros;
    }

    /**
     * Modelo de datos para Lazy Loading en las tablas.
     * @return SeriesLazyModel
     */
    public SeriesLazyModel getSeriesLazyModel() {
        return seriesLazyModel;
    }

    /**
     * Modelo de datos para Lazy Loading en las tablas.
     * @param seriesLazyModel SeriesLazyModel
     */
    public void setSeriesLazyModel(SeriesLazyModel seriesLazyModel) {
        this.seriesLazyModel = seriesLazyModel;
    }

    /**
     * Lista de series seleccionadas para cambiar su estado.
     * @return VCatalogoSerie>
     */
    public VCatalogoSerie getSerieSeleccionada() {
        return serieSeleccionada;
    }

    /**
     * series seleccionadas para cambiar su estado.
     * @param serieSeleccionada VCatalogoSerie
     */
    public void setSerieSeleccionada(VCatalogoSerie serieSeleccionada) {
        this.serieSeleccionada = serieSeleccionada;
    }

    /**
     * Variable boolean que habilita o no el botón de cambiar estado.
     * @return boolean
     */
    public boolean isHabilitarBoton() {
        return habilitarBoton;
    }

    /**
     * Variable boolean que habilita o no el botón de cambiar estado.
     * @param habilitarBoton boolean
     */
    public void setHabilitarBoton(boolean habilitarBoton) {
        this.habilitarBoton = habilitarBoton;
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
     * Referencia al Bean de nueva central.
     * @return ExpansionSeriesBean
     */
    public ExpansionSeriesBean getExpansionSeriesBean() {
        return expansionSeriesBean;
    }

    /**
     * Referencia al Bean de nueva central.
     * @param expansionSeriesBean ExpansionSeriesBean
     */
    public void setExpansionSeriesBean(ExpansionSeriesBean expansionSeriesBean) {
        this.expansionSeriesBean = expansionSeriesBean;
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
     * Gestor de Selección Múltiple sobre la tabla Lazy.
     * @return MultiSelectionOnLazyModelManager
     */
    public MultiSelectionOnLazyModelManager<VCatalogoSerie> getMultiSelectionManager() {
        return multiSelectionManager;
    }

    /**
     * Código de la serie.
     * @return the sna
     */
    public String getSna() {
        return sna;
    }

    /**
     * Código de la serie.
     * @param sna the sna to set
     */
    public void setSna(String sna) {
        this.sna = sna;
    }

    /**
     * Activa el botón exportar si no hay resultados en la búsqueda.
     * @return the emptySearch
     */
    public boolean isEmptySearch() {
        return emptySearch;
    }

}
