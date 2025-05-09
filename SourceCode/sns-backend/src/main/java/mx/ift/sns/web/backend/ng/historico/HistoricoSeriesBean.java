package mx.ift.sns.web.backend.ng.historico;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.modelo.filtros.FiltroBusquedaHistoricoSeries;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.TipoModalidad;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
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
@ManagedBean(name = "historicoBean")
@ViewScoped
public class HistoricoSeriesBean extends Wizard {

    /** UID Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoricoSeriesBean.class);

    /** Id del mensaje de error. */
    private static final String MSG_ID = ":MSG_Historico";

    /** Servicio de Numeración Geográfica. */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService servicio;

    /** Lista de tipos de red. */
    private List<TipoRed> tiposRed;

    /** Tipo de red seleccionado. */
    private TipoRed tipoRed;

    /** Lista de modalidades. */
    private List<TipoModalidad> tiposModalidad;

    /** Modalidad seleccionada. */
    private TipoModalidad tipoModalidad;

    /** Modalidad desactivada. */
    private boolean modalidadDisabled;

    /** Lista para los estados. **/
    private List<Estado> estados;

    /** Estado. */
    private Estado estado;

    /** Lista de municipios. */
    private List<Municipio> municipios;

    /** Combo de municipios desactivado. */
    private boolean municipiosDisabled;

    /** Municipio. */
    private Municipio municipio;

    /** Lista de poblaciones. */
    private List<Poblacion> poblaciones;

    /** Combo de poblaciones desactivado. */
    private boolean poblacionesDisabled;

    /** Población. */
    private Poblacion poblacion;

    /** Modelo Lazy para carga historico de series. */
    private HistoricoSeriesLazyModel model;

    /** abn. */
    private String abn;

    /** Nir. */
    private String nir;

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

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Filtro de busquedas. */
    private FiltroBusquedaHistoricoSeries filtro;

    /** Activa el botón exportar si no hay resultados en la búsqueda. */
    private boolean emptySearch = true;

    /**
     * Inicializacion del bean.
     * @throws Exception error en la creacion
     */
    @PostConstruct
    public void init() throws Exception {
        LOGGER.debug("");

        tiposRed = servicio.findAllTiposRed();
        tiposModalidad = servicio.findAllTiposModalidad();
        modalidadDisabled = true;
        estados = servicio.findAllEstados();

        poblacionesDisabled = true;
        municipiosDisabled = true;

        filtro = new FiltroBusquedaHistoricoSeries();
        filtro.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(servicio, Parametro.REGISTROS_POR_PAGINA_BACK));
        registroPorPagina = filtro.getResultadosPagina();

        model = new HistoricoSeriesLazyModel();
        model.setService(servicio);
        model.setFiltros(filtro);
        model.setRowCount(servicio.findHistoricoSeriesCount(filtro));
        emptySearch = servicio.findHistoricoSeriesCount(filtro) == 0;
    }

    /**
     * Busca por los campos del formulario.
     */
    public void buscar() {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("abn '{}' nir  '{}' sna '{}' numIni '{}' numFinal '{}' estado '{}' muni '{}' pob '{}'"
                    + " tipored '{}' modalidad '{}' fecha ini '{}' fecha fin '{}'", abn, nir, serie, numIni, numFin,
                    estado, municipio, poblacion,
                    tipoRed, tipoModalidad, fechaDesdeAsignacion, fechaHastaAsignacion);
        }

        creaFiltro();

        PaginatorUtil.resetPaginacion("FRM_Historico:TBL_HistoricoSeries",
                filtro.getResultadosPagina());

        if ((fechaDesdeAsignacion != null) && (fechaHastaAsignacion != null)
                && fechaHastaAsignacion.before(fechaDesdeAsignacion)) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0006);
        } else {
            model.setRowCount(servicio.findHistoricoSeriesCount(filtro));
        }
        emptySearch = servicio.findHistoricoSeriesCount(filtro) == 0;
    }

    /**
     * Método que setea los filtros de búsqueda.
     */
    private void creaFiltro() {
        filtro.clear();

        filtro.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(servicio, Parametro.REGISTROS_POR_PAGINA_BACK));

        if (StringUtils.isNotEmpty(abn)) {
            filtro.setIdAbn(abn);
        }

        if (StringUtils.isNotEmpty(nir)) {
            filtro.setCdgNir(nir);
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

        if (estado != null) {
            filtro.setEstado(estado);
        }

        if (municipio != null) {
            filtro.setMunicipio(municipio);
        }

        if (poblacion != null) {
            filtro.setPoblacion(poblacion);
        }

        if (tipoRed != null) {
            filtro.setTipoRed(tipoRed);
        }

        if (tipoModalidad != null) {
            filtro.setTipoModalidad(tipoModalidad);
        }

        if (fechaDesdeAsignacion != null) {
            filtro.setFechaInicio(fechaDesdeAsignacion);
        }

        if (fechaHastaAsignacion != null) {
            filtro.setFechaFin(fechaHastaAsignacion);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("filtro empty {}", filtro.isEmpty());
            LOGGER.debug("filtro {}", filtro);
        }

    }

    /**
     * Limpia los campos del formulario de busqueda.
     */
    public void limpiar() {
        LOGGER.debug("");

        abn = "";
        nir = "";
        serie = "";
        numIni = "";
        numFin = "";

        estado = null;
        municipio = null;
        municipiosDisabled = true;
        poblacion = null;
        poblacionesDisabled = true;
        tipoModalidad = null;
        modalidadDisabled = true;
        tipoRed = null;
        emptySearch = true;

        fechaDesdeAsignacion = null;
        fechaHastaAsignacion = null;

        filtro.clear();

        model.setRowCount(0);
    }

    /**
     * Rellena la lista de municipios por Estado seleccionado.
     * @throws Exception excepcion
     */
    public void habilitarMunicipio() throws Exception {

        LOGGER.debug("");

        if (estado != null) {
            municipios = servicio.findMunicipiosByEstado(estado.getCodEstado());
            municipio = null;
            municipiosDisabled = false;

            poblaciones = null;
            poblacion = null;
            poblacionesDisabled = true;
        }
    }

    /**
     * Rellena la lista de poblaciones por estado y municipio.
     * @throws Exception excepcion
     */
    public void habilitarPoblacion() throws Exception {

        LOGGER.debug("");

        if (municipio != null) {
            poblaciones = servicio.findAllPoblaciones(
                    estado.getCodEstado(), municipio.getId().getCodMunicipio());
            poblacionesDisabled = false;
        }
    }

    /**
     * Si tipo red es movil habilitamos modalidad. Si no, desahabilitamos modalidad y borramos lo que hubiera.
     */
    public void habilitarModalidad() {
        LOGGER.debug("");

        if ((tipoRed != null) && (tipoRed.getCdg().equals(TipoRed.MOVIL))) {
            modalidadDisabled = false;
        } else {
            modalidadDisabled = true;
            tipoModalidad = null;
        }
    }

    /**
     * Retorna el fichero excel del historico de series.
     * @return fichero municipios
     * @throws Exception error
     */
    public StreamedContent getFicheroHistoricoSeries() throws Exception {

        LOGGER.debug("");

        InputStream stream = servicio.getExportHistoricoSeries(filtro);
        String docName = "historico.xlsx";
        stream.close();
        LOGGER.debug("docname {}", docName);
        return new DefaultStreamedContent(stream,
                "application/vnd.ms-excel", docName);

    }

    /**
     * Lista de tipos de red.
     * @return tiposRed
     */
    public List<TipoRed> getTiposRed() {
        return tiposRed;
    }

    /**
     * Lista de tipos de red.
     * @param tiposRed List<TipoRed>
     */
    public void setTiposRed(List<TipoRed> tiposRed) {
        this.tiposRed = tiposRed;
    }

    /**
     * Lista de modalidades.
     * @return tiposModalidad
     */
    public List<TipoModalidad> getTiposModalidad() {
        return tiposModalidad;
    }

    /**
     * Lista de modalidades.
     * @param tiposModalidad List<TipoModalidad>
     */
    public void setTiposModalidad(List<TipoModalidad> tiposModalidad) {
        this.tiposModalidad = tiposModalidad;
    }

    /**
     * Tipo de red seleccionado.
     * @return tipoRed
     */
    public TipoRed getTipoRed() {
        return tipoRed;
    }

    /**
     * Tipo de red seleccionado.
     * @param tipoRed TipoRed
     */
    public void setTipoRed(TipoRed tipoRed) {
        this.tipoRed = tipoRed;
    }

    /**
     * Modalidad seleccionada.
     * @return tipoModalidad
     */
    public TipoModalidad getTipoModalidad() {
        return tipoModalidad;
    }

    /**
     * Modalidad seleccionada.
     * @param tipoModalidad TipoModalidad
     */
    public void setTipoModalidad(TipoModalidad tipoModalidad) {
        this.tipoModalidad = tipoModalidad;
    }

    /**
     * Lista para los estados.
     * @return estados
     */
    public List<Estado> getEstados() {
        return estados;
    }

    /**
     * Lista para los estados.
     * @param estados List<Estado>
     */
    public void setEstados(List<Estado> estados) {
        this.estados = estados;
    }

    /**
     * Estado.
     * @return estado
     */
    public Estado getEstado() {
        return estado;
    }

    /**
     * Estado.
     * @param estado Estado
     */
    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    /**
     * Municipio.
     * @return municipio
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
     * poblacion.
     * @return poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * poblacion.
     * @param poblacion poblacion
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * Servicio de Numeración Geográfica.
     * @return servicio
     */
    public INumeracionGeograficaService getServicio() {
        return servicio;
    }

    /**
     * Servicio de Numeración Geográfica.
     * @param servicio INumeracionGeograficaService
     */
    public void setServicio(INumeracionGeograficaService servicio) {
        this.servicio = servicio;
    }

    /**
     * Lista de municipios.
     * @return municipios
     */
    public List<Municipio> getMunicipios() {
        return municipios;
    }

    /**
     * Lista de municipios.
     * @param municipios List<Municipio>
     */
    public void setMunicipios(List<Municipio> municipios) {
        this.municipios = municipios;
    }

    /**
     * Lista de poblaciones.
     * @return poblaciones
     */
    public List<Poblacion> getPoblaciones() {
        return poblaciones;
    }

    /**
     * Lista de poblaciones.
     * @param poblaciones List<Poblacion>
     */
    public void setPoblaciones(List<Poblacion> poblaciones) {
        this.poblaciones = poblaciones;
    }

    /**
     * Combo de municipios desactivado.
     * @return municipiosDisabled
     */
    public boolean isMunicipiosDisabled() {
        return municipiosDisabled;
    }

    /**
     * Combo de municipios desactivado.
     * @param municipiosDisabled boolean
     */
    public void setMunicipiosDisabled(boolean municipiosDisabled) {
        this.municipiosDisabled = municipiosDisabled;
    }

    /**
     * Combo de poblaciones desactivado.
     * @return poblacionesDisabled
     */
    public boolean isPoblacionesDisabled() {
        return poblacionesDisabled;
    }

    /**
     * Combo de poblaciones desactivado.
     * @param poblacionesDisabled boolean
     */
    public void setPoblacionesDisabled(boolean poblacionesDisabled) {
        this.poblacionesDisabled = poblacionesDisabled;
    }

    /**
     * Modelo Lazy para carga historico de series.
     * @return model
     */
    public HistoricoSeriesLazyModel getModel() {
        return model;
    }

    /**
     * Modelo Lazy para carga historico de series.
     * @param model HistoricoSeriesLazyModel
     */
    public void setModel(HistoricoSeriesLazyModel model) {
        this.model = model;
    }

    /**
     * abn.
     * @return abn
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
     * Nir.
     * @return nir
     */
    public String getNir() {
        return nir;
    }

    /**
     * Nir.
     * @param nir String
     */
    public void setNir(String nir) {
        this.nir = nir;
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
     * @param serie String
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
     * @param numIni String
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
     * @param numFin String
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
     * @param fechaDesdeAsignacion Date
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
     * @param fechaHastaAsignacion Date
     */
    public void setFechaHastaAsignacion(Date fechaHastaAsignacion) {
        this.fechaHastaAsignacion = fechaHastaAsignacion;
    }

    /**
     * Modalidad desactivada.
     * @return modalidadDisabled
     */
    public boolean isModalidadDisabled() {
        return modalidadDisabled;
    }

    /**
     * Modalidad desactivada.
     * @param modalidadDisabled boolean
     */
    public void setModalidadDisabled(boolean modalidadDisabled) {
        this.modalidadDisabled = modalidadDisabled;
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
