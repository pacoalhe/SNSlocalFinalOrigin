package mx.ift.sns.web.backend.reporteador;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.filtros.FiltroReporteadorCentrales;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.negocio.IReporteadorFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase de soporte para el reporteador de Centrales. */
@ManagedBean(name = "reporteadorCentralesBean")
@ViewScoped
public class ReporteadorCentralesBean implements Serializable {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReporteadorCentralesBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_BuscadorCentrales";

    /** Facade de servicios Administración de catálogo. */
    @EJB(mappedName = "ReporteadorFacade")
    private IReporteadorFacade reporteadorFacade;

    /** Pst seleccionada. */
    private Proveedor pstSeleccionada;

    /** Estado seleccionado. */
    private Estado estadoSeleccionado;

    /** Municipio seleccionado. */
    private Municipio municipioSeleccionado;

    /** Poblacion seleccionado. */
    private Poblacion poblacionSeleccionada;

    /** ABN seleccionado. */
    private Integer abnSeleccionada;

    /** Lista de psts disponibles para seleccionar. */
    private List<Proveedor> listaPst;

    /** Lista de estados disponibles para seleccionar. */
    private List<Estado> listaEstado;

    /** Lista de municipios disponibles para seleccionar. */
    private List<Municipio> listaMunicipio;

    /** Lista de poblaciones disponibles para seleccionar. */
    private List<Poblacion> listaPoblacion;

    /** Filtros de búsqueda de centrales. */
    private FiltroReporteadorCentrales filtros;

    /**
     * Iniciamos la pantalla cargando los combos.
     * @throws Exception error en inicio
     **/
    @PostConstruct
    public void init() throws Exception {

        LOGGER.debug("");

        try {
            pstSeleccionada = null;
            estadoSeleccionado = null;
            municipioSeleccionado = null;
            poblacionSeleccionada = null;

            listaMunicipio = new ArrayList<Municipio>(1);
            listaPoblacion = new ArrayList<Poblacion>(1);
            // Catálogo de Proveedores
            listaPst = reporteadorFacade.findAllProveedoresActivos();
            listaEstado = reporteadorFacade.findAllEstados();
            // Filtros de búsqueda.
            filtros = new FiltroReporteadorCentrales();

        } catch (Exception ex) {
            LOGGER.error("error", ex);
        }
    }

    /**
     * Reseta los valores.
     */
    public void resetPantalla() {

        LOGGER.debug("");

        pstSeleccionada = null;
        estadoSeleccionado = null;
        municipioSeleccionado = null;
        poblacionSeleccionada = null;
        listaMunicipio = null;
        listaPoblacion = null;
        abnSeleccionada = null;
    }

    /** Método invocado al seleccionar un estado del combo de estados. */
    public void seleccionEstado() {

        LOGGER.debug("");

        try {
            if (estadoSeleccionado != null) {
                // Municipios
                listaMunicipio = reporteadorFacade.findMunicipiosByEstado(estadoSeleccionado.getCodEstado());
                municipioSeleccionado = null;
                poblacionSeleccionada = null;
                listaPoblacion = new ArrayList<Poblacion>(1);
            } else {
                // Inicialización de los desplegables de municipios y poblaciones
                listaMunicipio = new ArrayList<Municipio>(1);
                listaPoblacion = new ArrayList<Poblacion>(1);
            }
        } catch (Exception ex) {
            LOGGER.error("error", ex);
        }
    }

    /** Método invocado al seleccionar un municipio del combo de municipios. */
    public void seleccionMunicipio() {

        LOGGER.debug("");

        try {
            if (municipioSeleccionado != null && estadoSeleccionado != null) {
                // Poblaciones
                listaPoblacion = reporteadorFacade.findAllPoblaciones(estadoSeleccionado.getCodEstado(),
                        municipioSeleccionado.getId().getCodMunicipio());
                poblacionSeleccionada = null;
            } else {
                listaPoblacion = null;
            }
        } catch (Exception ex) {
            LOGGER.error("error", ex);
        }
    }

    /**
     * Método que realiza el export a excel de los resultados de la consulta.
     * @return excel
     * @throws Exception Exception
     */
    public StreamedContent getExportarDatos() {
        StreamedContent fichero = null;
        try {
            LOGGER.debug("");

            if( estadoSeleccionado == null && municipioSeleccionado == null
                    && poblacionSeleccionada == null && abnSeleccionada == null && pstSeleccionada == null ){
                LOGGER.debug("Error inesperado: No se selecciono ni un filtro");
                MensajesBean.addErrorMsg(MSG_ID, "Debe seleccionar un Filtro","");
                return null;
            }

            filtros.setResultadosPagina(50000);
            filtros.setEstadoSeleccionado(this.estadoSeleccionado);
            filtros.setMunicipioSeleccionado(this.municipioSeleccionado);
            filtros.setPoblacionSeleccionada(this.poblacionSeleccionada);
            filtros.setAbnSeleccionado(this.abnSeleccionada);
            filtros.setPstSeleccionada(this.pstSeleccionada);
            InputStream stream = new ByteArrayInputStream(
                    reporteadorFacade.getExportCentrales(filtros));
            String docName = "Centrales por Localidad";

            docName = docName.concat(".xlsx");

            LOGGER.debug("docname {}", docName);
            return new DefaultStreamedContent(stream,
                    "application/vnd.ms-excel", docName);

        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
        return fichero;
    }

    /**
     * Pst seleccionada.
     * @return Proveedor
     */
    public Proveedor getPstSeleccionada() {
        return pstSeleccionada;
    }

    /**
     * Pst seleccionada.
     * @param pstSeleccionada Proveedor
     */
    public void setPstSeleccionada(Proveedor pstSeleccionada) {
        this.pstSeleccionada = pstSeleccionada;
    }

    /**
     * Estado seleccionado.
     * @return Estado
     */
    public Estado getEstadoSeleccionado() {
        return estadoSeleccionado;
    }

    /**
     * Estado seleccionado.
     * @param estadoSeleccionado Estado
     */
    public void setEstadoSeleccionado(Estado estadoSeleccionado) {
        this.estadoSeleccionado = estadoSeleccionado;
    }

    /**
     * Municipio seleccionado.
     * @return Municipio
     */
    public Municipio getMunicipioSeleccionado() {
        return municipioSeleccionado;
    }

    /**
     * Municipio seleccionado.
     * @param municipioSeleccionado Municipio
     */
    public void setMunicipioSeleccionado(Municipio municipioSeleccionado) {
        this.municipioSeleccionado = municipioSeleccionado;
    }

    /**
     * Poblacion seleccionado.
     * @return Poblacion
     */
    public Poblacion getPoblacionSeleccionada() {
        return poblacionSeleccionada;
    }

    /**
     * Poblacion seleccionado.
     * @param poblacionSeleccionada Poblacion
     */
    public void setPoblacionSeleccionada(Poblacion poblacionSeleccionada) {
        this.poblacionSeleccionada = poblacionSeleccionada;
    }

    /**
     * ABN seleccionado.
     * @return Integer
     */
    public Integer getAbnSeleccionada() {
        return abnSeleccionada;
    }

    /**
     * ABN seleccionado.
     * @param abnSeleccionada Integer
     */
    public void setAbnSeleccionada(Integer abnSeleccionada) {
        this.abnSeleccionada = abnSeleccionada;
    }

    /**
     * Lista de psts disponibles para seleccionar.
     * @return List<Proveedor>
     */
    public List<Proveedor> getListaPst() {
        return listaPst;
    }

    /**
     * Lista de psts disponibles para seleccionar.
     * @param listaPst List<Proveedor>
     */
    public void setListaPst(List<Proveedor> listaPst) {
        this.listaPst = listaPst;
    }

    /**
     * Lista de estados disponibles para seleccionar.
     * @return List<Estado>
     */
    public List<Estado> getListaEstado() {
        return listaEstado;
    }

    /**
     * Lista de estados disponibles para seleccionar.
     * @param listaEstado List<Estado>
     */
    public void setListaEstado(List<Estado> listaEstado) {
        this.listaEstado = listaEstado;
    }

    /**
     * Lista de municipios disponibles para seleccionar.
     * @return List<Municipio>
     */
    public List<Municipio> getListaMunicipio() {
        return listaMunicipio;
    }

    /**
     * Lista de municipios disponibles para seleccionar.
     * @param listaMunicipio List<Municipio>
     */
    public void setListaMunicipio(List<Municipio> listaMunicipio) {
        this.listaMunicipio = listaMunicipio;
    }

    /**
     * Lista de poblaciones disponibles para seleccionar.
     * @return List<Poblacion>
     */
    public List<Poblacion> getListaPoblacion() {
        return listaPoblacion;
    }

    /**
     * Lista de poblaciones disponibles para seleccionar.
     * @param listaPoblacion List<Poblacion>
     */
    public void setListaPoblacion(List<Poblacion> listaPoblacion) {
        this.listaPoblacion = listaPoblacion;
    }

    /**
     * Filtros de búsqueda de centrales.
     * @return FiltroReporteadorCentrales
     */
    public FiltroReporteadorCentrales getFiltros() {
        return filtros;
    }

    /**
     * Filtros de búsqueda de centrales.
     * @param filtros FiltroReporteadorCentrales
     */
    public void setFiltros(FiltroReporteadorCentrales filtros) {
        this.filtros = filtros;
    }

}
