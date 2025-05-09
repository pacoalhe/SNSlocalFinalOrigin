package mx.ift.sns.web.backend.nng.historico;

import java.io.InputStream;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.modelo.filtros.FiltroBusquedaHistoricoSeries;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.Wizard;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean del Historico de Series.
 */
@ManagedBean(name = "historicoNngBean")
@ViewScoped
public class HistoricoSeriesNngBean extends Wizard {

    /** UID Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoricoSeriesNngBean.class);

    /** Id del mensaje de error. */
    private static final String MSG_ID = ":MSG_Historico";

    /** Fachada de Numeración Geográfica. */
    @EJB(mappedName = "NumeracionNoGeograficaFacade")
    private INumeracionNoGeograficaFacade nngFacade;

    /** Modelo Lazy para carga historico de series. */
    private HistoricoSeriesNngLazyModel model;

    /** SNA. */
    private String serie;

    /** numero inicial. */
    private String numIni;

    /** Numero final. */
    private String numFin;

    /** Rango inicial de búsqueda para fecha de Asignación de Solicitud. */
    private Date fechaDesdeAsignacion;

    /** Rango final de búsqueda para fecha de Asignación de Solicitud. */
    private Date fechaHastaAsignacion;

    /** Filtro de busquedas. */
    private FiltroBusquedaHistoricoSeries filtro;

    /** Clave de servicio seleccionada. */
    private String claveServicio;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Activa el botón exportar si no hay resultados en la búsqueda. */
    private boolean emptySearch = true;

    /**
     * Inicializacion del bean.
     * @throws Exception error en la creacion
     */
    @PostConstruct
    public void init() throws Exception {

        LOGGER.debug("");

        filtro = new FiltroBusquedaHistoricoSeries();
        filtro.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(nngFacade, Parametro.REGISTROS_POR_PAGINA_BACK));
        registroPorPagina = filtro.getResultadosPagina();

        model = new HistoricoSeriesNngLazyModel();

        model.setFiltros(filtro);
        model.setFacade(nngFacade);

        this.emptySearch = nngFacade.findAllHistoricoSeriesCount(filtro) == 0;
    }

    /**
     * Busca por los campos del formulario.
     */
    public void buscar() {

        if (model == null) {
            filtro = new FiltroBusquedaHistoricoSeries();
            model = new HistoricoSeriesNngLazyModel();
            model.setFiltros(filtro);
            model.setFacade(nngFacade);
        }

        creaFiltro();

        if (!filtro.isEmpty()) {
            if ((fechaDesdeAsignacion != null)
                    && (fechaHastaAsignacion != null)
                    && fechaHastaAsignacion.before(fechaDesdeAsignacion)) {
                MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0006);
            }
        }
        emptySearch = nngFacade.findAllHistoricoSeriesCount(filtro) == 0;
        PaginatorUtil.resetPaginacion("FRM_Historico:TBL_HistoricoSeries",
                filtro.getResultadosPagina());

        this.emptySearch = nngFacade.findAllHistoricoSeriesCount(filtro) == 0;
    }

    /**
     * Método que setea los filtros de búsqueda.
     */
    private void creaFiltro() {
        filtro.clear();

        filtro.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(nngFacade, Parametro.REGISTROS_POR_PAGINA_BACK));

        if (claveServicio != null) {
            filtro.setClaveServicio(claveServicio);
        }

        if (StringUtils.isNotEmpty(serie)) {
            filtro.setIdSna(Integer.valueOf(serie).toString());
        }

        if (StringUtils.isNotEmpty(numIni)) {
            filtro.setNumIni(numIni);
        }

        if (StringUtils.isNotEmpty(numFin)) {
            filtro.setNumFin(numFin);
        }

        if (fechaDesdeAsignacion != null) {
            filtro.setFechaInicio(fechaDesdeAsignacion);
        }

        if (fechaHastaAsignacion != null) {
            filtro.setFechaFin(fechaHastaAsignacion);
        } else {
            // Si la fin no tiene valor se busca por la fecha exacta puesta en inicio
            if (fechaDesdeAsignacion != null) {
                filtro.setFechaFin(fechaDesdeAsignacion);
            }
        }
        model.setFiltros(filtro);
    }

    /**
     * Limpia los campos del formulario de busqueda.
     */
    public void limpiar() {
        LOGGER.debug("");

        serie = "";
        numIni = "";
        numFin = "";
        claveServicio = "";
        emptySearch = true;

        fechaDesdeAsignacion = null;
        fechaHastaAsignacion = null;
        model = null;
        filtro.clear();

        this.emptySearch = true;
    }

    /**
     * Retorna el fichero excel del historico de series.
     * @return fichero municipios
     * @throws Exception error
     */
    public StreamedContent getFicheroHistoricoSeries() throws Exception {

        LOGGER.debug("");

        InputStream stream = nngFacade.getExportHistoricoSeries(filtro);
        String docName = "historico.xlsx";
        stream.close();
        LOGGER.debug("docname {}", docName);
        return new DefaultStreamedContent(stream,
                "application/vnd.ms-excel", docName);

    }

    /**
     * Modelo Lazy para carga historico de series.
     * @return model
     */
    public HistoricoSeriesNngLazyModel getModel() {
        return model;
    }

    /**
     * Modelo Lazy para carga historico de series.
     * @param model model to set
     */
    public void setModel(HistoricoSeriesNngLazyModel model) {
        this.model = model;
    }

    /**
     * SNA.
     * @return serie
     */
    public String getSerie() {
        return serie;
    }

    /**
     * SNA.
     * @param serie serie to set
     */
    public void setSerie(String serie) {
        this.serie = serie;
    }

    /**
     * numero inicial.
     * @return numIni
     */
    public String getNumIni() {
        return numIni;
    }

    /**
     * numero inicial.
     * @param numIni numIni to set
     */
    public void setNumIni(String numIni) {
        this.numIni = numIni;
    }

    /**
     * Numero final.
     * @return numFin
     */
    public String getNumFin() {
        return numFin;
    }

    /**
     * Numero final.
     * @param numFin numFin to set
     */
    public void setNumFin(String numFin) {
        this.numFin = numFin;
    }

    /**
     * Rango inicial de búsqueda para fecha de Asignación de Solicitud.
     * @return fechaDesdeAsignacion
     */
    public Date getFechaDesdeAsignacion() {
        return fechaDesdeAsignacion;
    }

    /**
     * Rango inicial de búsqueda para fecha de Asignación de Solicitud.
     * @param fechaDesdeAsignacion fechaDesdeAsignacion to set
     */
    public void setFechaDesdeAsignacion(Date fechaDesdeAsignacion) {
        this.fechaDesdeAsignacion = fechaDesdeAsignacion;
    }

    /**
     * Rango final de búsqueda para fecha de Asignación de Solicitud.
     * @return fechaHastaAsignacion
     */
    public Date getFechaHastaAsignacion() {
        return fechaHastaAsignacion;
    }

    /**
     * Rango final de búsqueda para fecha de Asignación de Solicitud.
     * @param fechaHastaAsignacion fechaHastaAsignacion to set
     */
    public void setFechaHastaAsignacion(Date fechaHastaAsignacion) {
        this.fechaHastaAsignacion = fechaHastaAsignacion;
    }

    /**
     * Filtro de busquedas.
     * @return filtro
     */
    public FiltroBusquedaHistoricoSeries getFiltro() {
        return filtro;
    }

    /**
     * Filtro de busquedas.
     * @param filtro filtro to set
     */
    public void setFiltro(FiltroBusquedaHistoricoSeries filtro) {
        this.filtro = filtro;
    }

    /**
     * Clave de servicio seleccionada.
     * @return claveServicio
     */
    public String getClaveServicio() {
        return claveServicio;
    }

    /**
     * Clave de servicio seleccionada.
     * @param claveServicio claveServicio to set
     */
    public void setClaveServicio(String claveServicio) {
        this.claveServicio = claveServicio;
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
