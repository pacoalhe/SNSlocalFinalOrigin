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

import mx.ift.sns.modelo.filtros.FiltroReporteadorNG;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.reporteador.ElementoAgrupador;
import mx.ift.sns.negocio.IReporteadorFacade;
import mx.ift.sns.web.backend.common.MensajesBean;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase de soporte para el reporteador de NG Activas/Asignadas a fecha actual. */
@ManagedBean(name = "reporteadorNGFechaActualBean")
@ViewScoped
public class ReporteadorNGFechaActualBean implements Serializable {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReporteadorNGFechaActualBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_ReporteadorNgActual";

    /** Facade de servicios Administración de catálogo. */
    @EJB(mappedName = "ReporteadorFacade")
    private IReporteadorFacade reporteadorFacade;

    /** Filtros de búsqueda de numeraciones NG activas/asignadas. */
    private FiltroReporteadorNG filtros;

    /** Lista de elementos para la primera ordenación. */
    private List<ElementoAgrupador> listaPrimeraOrdenacion;

    /** Lista de elementos para la segunda ordenación. */
    private List<ElementoAgrupador> listaSegundaOrdenacion;

    /** Elemento de ordenación 1 seleccionado. */
    private ElementoAgrupador elemento1Seleccionado;

    /** Elemento de ordenación 2 seleccionado. */
    private ElementoAgrupador elemento2Seleccionado;

    /** Pst seleccionada. */
    private Proveedor pstSeleccionada;

    /** Estado seleccionado. */
    private Estado estadoSeleccionado;

    /** Municipio seleccionado. */
    private Municipio municipioSeleccionado;

    /** Poblacion seleccionado. */
    private Poblacion poblacionSeleccionada;

    /** ABN seleccionado. */
    private Integer abnSeleccionado;

    /** Lista de psts disponibles para seleccionar. */
    private List<Proveedor> listaPst;

    /** Lista de estados disponibles para seleccionar. */
    private List<Estado> listaEstado;

    /** Lista de municipios disponibles para seleccionar. */
    private List<Municipio> listaMunicipio;

    /** Lista de poblaciones disponibles para seleccionar. */
    private List<Poblacion> listaPoblacion;

    /**
     * Iniciamos la pantalla cargando los combos.
     * @throws Exception error en inicio
     **/
    @PostConstruct
    public void init() throws Exception {

        LOGGER.debug("");

        resetPantalla();

        try {

            listaPrimeraOrdenacion = addElementosOrdenacion(true, true, true, true, true);

            listaMunicipio = new ArrayList<Municipio>(1);
            listaPoblacion = new ArrayList<Poblacion>(1);
            // Catálogo de Proveedores
            listaPst = reporteadorFacade.findAllProveedoresActivos();
            listaEstado = reporteadorFacade.findAllEstados();

            // Filtros de búsqueda.
            filtros = new FiltroReporteadorNG();

        } catch (Exception ex) {
            LOGGER.error("error", ex);
        }
    }

    /** Método invocado al seleccionar un valor del combo de primera ordenación. */
    public void seleccionPrimeraOrdenacion() {

        try {
            if (elemento1Seleccionado != null) {
                if (elemento1Seleccionado.getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_PST)) {
                    listaSegundaOrdenacion = addElementosOrdenacion(false, true, true, true, true);
                } else if (elemento1Seleccionado.getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ESTADO)) {
                    listaSegundaOrdenacion = addElementosOrdenacion(true, false, true, true, true);
                } else if (elemento1Seleccionado.getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_MUNICIPIO)) {
                    listaSegundaOrdenacion = addElementosOrdenacion(true, false, false, true, true);
                } else if (elemento1Seleccionado.getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_POBLACION)) {
                    listaSegundaOrdenacion = addElementosOrdenacion(true, false, false, false, false);
                } else if (elemento1Seleccionado.getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ABN)) {
                    listaSegundaOrdenacion = addElementosOrdenacion(true, false, false, true, false);
                }
            } else {
                elemento2Seleccionado = null;
                listaSegundaOrdenacion = null;
            }
        } catch (Exception ex) {
            LOGGER.error("error", ex);
        }
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

        try {
            if (municipioSeleccionado != null) {
                // Poblaciones del municipio seleccionado
                listaPoblacion = reporteadorFacade.findAllPoblaciones(
                        estadoSeleccionado.getCodEstado(), municipioSeleccionado.getId().getCodMunicipio());
                poblacionSeleccionada = null;
            } else {
                listaPoblacion = null;
            }
        } catch (Exception ex) {
            LOGGER.error("error", ex);
        }
    }

    /**
     * Método que setea los filtros de búsqueda.
     */
    private void creaFiltros() {
        // Resetamos los filtros
        filtros.clear();
        // Agrupaciones
        if (elemento1Seleccionado != null) {
            filtros.setPrimeraAgrupacion(elemento1Seleccionado);
        }
        if (elemento2Seleccionado != null) {
            filtros.setSegundaAgrupacion(elemento2Seleccionado);
        }
        // Filtro de proveedor (PST)
        if (pstSeleccionada != null) {
            filtros.setPstSeleccionada(pstSeleccionada);
        }
        // Filtro del estado
        if (estadoSeleccionado != null) {
            filtros.setEstadoSeleccionado(estadoSeleccionado);
        }
        // Filtro del municipio
        if (municipioSeleccionado != null) {
            filtros.setMunicipioSeleccionado(municipioSeleccionado);
        }
        // Filtro del poblacion
        if (poblacionSeleccionada != null) {
            filtros.setPoblacionSeleccionada(poblacionSeleccionada);
        }
        // Filtro del abn
        if (abnSeleccionado != null) {
            filtros.setAbnSeleccionado(abnSeleccionado);
        }
    }

    /**
     * Exporta los datos de la Consulta en un fichero Excel.
     * @return StreamedContent fichero con el listado de poblaciones
     * @throws Exception excepcion en caso de error
     */
    public StreamedContent getExportReporteadorNGFechaActual() throws Exception {

        LOGGER.debug("getExportReporteadorNGFechaActual");
        if (validateFiltroPoblacionMunicipio()) {
            creaFiltros();
            /** Cargamos el filtro propio de reporteador para series */
            InputStream stream = new ByteArrayInputStream(
                    reporteadorFacade.getReporteNGFechaActual(filtros));
            stream.close();

            StringBuilder docName = new StringBuilder();
            docName.append("Reporteador_NGFechaActual").append(".xlsx");

            LOGGER.debug("docname {}", docName);

            StreamedContent fichero = new DefaultStreamedContent(
                    stream, "application/vnd.ms-excel", docName.toString());

            return fichero;
        } else {
            return null;
        }
    }

    /**
     * Devuelve una lista de elementos de ordenación.
     * @return boolean true para error y false para lo contrario
     */
    private boolean validateFiltroPoblacionMunicipio() {
        boolean cumple = true;
        if (elemento1Seleccionado != null) {
            if ((elemento1Seleccionado.getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_MUNICIPIO)
                    && (estadoSeleccionado == null))
                    || (elemento1Seleccionado.getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_POBLACION)
                    && (municipioSeleccionado == null
                    && abnSeleccionado == null))) {
                cumple = false;
            }
        }
        if (elemento2Seleccionado != null) {
            if ((elemento2Seleccionado.getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_MUNICIPIO)
                    && (estadoSeleccionado == null))
                    || (elemento2Seleccionado.getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_POBLACION)
                    && (municipioSeleccionado == null
                    && abnSeleccionado == null))) {
                cumple = false;
            }
        }
        if (!cumple) {
            MensajesBean.addErrorMsg(MSG_ID,
                    MensajesBean.getTextoResource("reporteador.filtro.validate.poblacion.municipio"), "");
        }
        return cumple;
    }

    /**
     * Devuelve una lista de elementos de ordenación.
     * @param pst proveedor
     * @param estado estado
     * @param municipio municipio
     * @param poblacion poblacion
     * @param abn abn
     * @return List lista de elementos
     */
    private List<ElementoAgrupador> addElementosOrdenacion(boolean pst, boolean estado, boolean municipio,
            boolean poblacion, boolean abn) {

        List<ElementoAgrupador> lista = new ArrayList<ElementoAgrupador>();
        if (pst) {
            ElementoAgrupador elemento = new ElementoAgrupador();
            elemento.setCodigo(ElementoAgrupador.COD_PST);
            elemento.setDescripcion(ElementoAgrupador.DESC_PST);
            lista.add(elemento);
        }
        if (estado) {
            ElementoAgrupador elemento = new ElementoAgrupador();
            elemento.setCodigo(ElementoAgrupador.COD_ESTADO);
            elemento.setDescripcion(ElementoAgrupador.DESC_ESTADO);
            lista.add(elemento);
        }
        if (municipio) {
            ElementoAgrupador elemento = new ElementoAgrupador();
            elemento.setCodigo(ElementoAgrupador.COD_MUNICIPIO);
            elemento.setDescripcion(ElementoAgrupador.DESC_MUNICIPIO);
            lista.add(elemento);
        }
        if (poblacion) {
            ElementoAgrupador elemento = new ElementoAgrupador();
            elemento.setCodigo(ElementoAgrupador.COD_POBLACION);
            elemento.setDescripcion(ElementoAgrupador.DESC_POBLACION);
            lista.add(elemento);
        }
        if (abn) {
            ElementoAgrupador elemento = new ElementoAgrupador();
            elemento.setCodigo(ElementoAgrupador.COD_ABN);
            elemento.setDescripcion(ElementoAgrupador.DESC_ABN);
            lista.add(elemento);
        }
        return lista;
    }

    /**
     * Resetea los valores.
     */
    public void resetPantalla() {

        LOGGER.debug("");

        elemento1Seleccionado = null;
        elemento2Seleccionado = null;
        pstSeleccionada = null;
        estadoSeleccionado = null;
        municipioSeleccionado = null;
        poblacionSeleccionada = null;
        abnSeleccionado = null;
        listaSegundaOrdenacion = null;
        listaMunicipio = new ArrayList<Municipio>(1);
        listaPoblacion = new ArrayList<Poblacion>(1);
    }

    /**
     * Facade de servicios Administración de catálogo.
     * @return IReporteadorFacade
     */
    public IReporteadorFacade getReporteadorFacade() {
        return reporteadorFacade;
    }

    /**
     * Facade de servicios Administración de catálogo.
     * @param reporteadorFacade IReporteadorFacade
     */
    public void setReporteadorFacade(IReporteadorFacade reporteadorFacade) {
        this.reporteadorFacade = reporteadorFacade;
    }

    /**
     * Filtros de búsqueda de numeraciones NG activas/asignadas.
     * @return FiltroReporteadorNG
     */
    public FiltroReporteadorNG getFiltros() {
        return filtros;
    }

    /**
     * Filtros de búsqueda de numeraciones NG activas/asignadas.
     * @param filtros FiltroReporteadorNG
     */
    public void setFiltros(FiltroReporteadorNG filtros) {
        this.filtros = filtros;
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
    public Integer getAbnSeleccionado() {
        return abnSeleccionado;
    }

    /**
     * ABN seleccionado.
     * @param abnSeleccionado Integer
     */
    public void setAbnSeleccionado(Integer abnSeleccionado) {
        this.abnSeleccionado = abnSeleccionado;
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
     * Lista de elementos para la primera ordenación.
     * @return List<ElementoAgrupador>
     */
    public List<ElementoAgrupador> getListaPrimeraOrdenacion() {
        return listaPrimeraOrdenacion;
    }

    /**
     * Lista de elementos para la primera ordenación.
     * @param listaPrimeraOrdenacion List<ElementoAgrupador>
     */
    public void setListaPrimeraOrdenacion(List<ElementoAgrupador> listaPrimeraOrdenacion) {
        this.listaPrimeraOrdenacion = listaPrimeraOrdenacion;
    }

    /**
     * Lista de elementos para la segunda ordenación.
     * @return List<ElementoAgrupador>
     */
    public List<ElementoAgrupador> getListaSegundaOrdenacion() {
        return listaSegundaOrdenacion;
    }

    /**
     * Lista de elementos para la segunda ordenación.
     * @param listaSegundaOrdenacion List<ElementoAgrupador>
     */
    public void setListaSegundaOrdenacion(List<ElementoAgrupador> listaSegundaOrdenacion) {
        this.listaSegundaOrdenacion = listaSegundaOrdenacion;
    }

    /**
     * Elemento de ordenación 1 seleccionado.
     * @return ElementoAgrupador
     */
    public ElementoAgrupador getElemento1Seleccionado() {
        return elemento1Seleccionado;
    }

    /**
     * Elemento de ordenación 1 seleccionado.
     * @param elemento1Seleccionado ElementoAgrupador
     */
    public void setElemento1Seleccionado(ElementoAgrupador elemento1Seleccionado) {
        this.elemento1Seleccionado = elemento1Seleccionado;
    }

    /**
     * Elemento de ordenación 2 seleccionado.
     * @return ElementoAgrupador
     */
    public ElementoAgrupador getElemento2Seleccionado() {
        return elemento2Seleccionado;
    }

    /**
     * Elemento de ordenación 2 seleccionado.
     * @param elemento2Seleccionado ElementoAgrupador
     */
    public void setElemento2Seleccionado(ElementoAgrupador elemento2Seleccionado) {
        this.elemento2Seleccionado = elemento2Seleccionado;
    }

}
