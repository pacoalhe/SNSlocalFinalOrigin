package mx.ift.sns.web.backend.ng.lineasactivas;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.modelo.filtros.FiltroBusquedaLineasActivas;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoModalidad;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.web.backend.ApplicationException;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.ng.lineasactivas.model.SolicitudLineasActivasLazyModel;
import mx.ift.sns.web.backend.ng.lineasactivas.model.TipoConsultaLineas;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Consulta de los reportes de lineas activas.
 */
@ManagedBean(name = "consultarLineasActivasBean")
@ViewScoped
public class ConsultarLineasActivasNgBean implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 24973570852489092L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarLineasActivasNgBean.class);

    /** Id de mensajes de error. */
    private static final String MSG_ID = "MSG_Ubicacion";

    /** Servicio de Numeración Geográfica. */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    /** Referencia al Bean de Carga de Lineas Activas. */
    @ManagedProperty("#{cargaLineasActivasBean}")
    private CargaLineasActivasWizard cargaLineasActivasBean;

    /** concesionario seleccionado. */
    private Proveedor concesionario;

    /** Lista de concesionarios. */
    private List<Proveedor> listaConcesionarios;

    /** Fecha inicio consulta. */
    private Date fechaInicial;

    /** Fecha final consulta. */
    private Date fechaFinal;

    /** Indica si las fechas estan desactivadas. */
    private boolean fechasDisabled;

    /** Estado seleccionado. */
    private Estado estado;

    /** Lista combo Estados. */
    private List<Estado> listaEstados;

    /** Municipio seleccionado. */
    private Municipio municipio;

    /** Lista combo Municipios. */
    private List<Municipio> listaMunicipios;

    /** Poblacion seleccionada. */
    private Poblacion poblacion;

    /** Lista combo de poblaciones. */
    private List<Poblacion> listaPoblaciones;

    /** Clave censal. */
    private String claveCensal;

    /** ABN. */
    private String abn;

    /** Lista de tipos de consulta. */
    private List<TipoConsultaLineas> listaTiposConsulta;

    /** Tipo de consulta seleccionada. */
    private TipoConsultaLineas tipoConsulta;

    /** Modelo Lazy para reportes de lineas activas. */
    private SolicitudLineasActivasLazyModel model;

    /** Tipo de red seleccionada. */
    private TipoRed tipoRed;

    /** Lista de tipos de red. */
    private List<TipoRed> tiposRed;

    /** Tipo de modalidad seleccionada. */
    private TipoModalidad tipoModalidad;

    /** Lista de tipos de modadalidad. */
    private List<TipoModalidad> tiposModalidad;

    /** Modalidad disabled. */
    private boolean modalidadDisabled;

    /** Filtro de busqueda. */
    private FiltroBusquedaLineasActivas filtro;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Activa el botón exportar si no hay resultados en la búsqueda. */
    private boolean emptySearch = true;

    /**
     * Postconstructor.
     */
    @PostConstruct
    public void init() {

        try {
            LOGGER.debug("");

            // Cargamos el combo de concesionarios.
            listaConcesionarios = ngService.findAllConcesionarios();

            // Precargamos los datos de ubicacion.

            listaEstados = ngService.findEstados();
            listaMunicipios = new ArrayList<Municipio>();
            listaPoblaciones = new ArrayList<Poblacion>();

            // Estado estado = new Estado();
            estado = null;
            tipoConsulta = null;
            tipoRed = null;
            tiposModalidad = null;

            listaTiposConsulta = new ArrayList<TipoConsultaLineas>();

            listaTiposConsulta.add(TipoConsultaLineas.TIPO_HISTORICO);
            listaTiposConsulta.add(TipoConsultaLineas.TIPO_ULTIMO_REPORTE);

            tiposRed = ngService.findAllTiposRed();
            tiposModalidad = ngService.findAllTiposModalidad();

            fechasDisabled = true;
            modalidadDisabled = true;
            abn = null;

            filtro = new FiltroBusquedaLineasActivas();
            filtro.clear();

            filtro.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(ngService,
                    Parametro.REGISTROS_POR_PAGINA_BACK));
            registroPorPagina = filtro.getResultadosPagina();

            model = new SolicitudLineasActivasLazyModel();
            model.setFiltros(filtro);

        } catch (Exception e) {
            throw new ApplicationException();
        }
    }

    /**
     * Rellena la lista de municipios por Estado seleccionado.
     */
    public void habilitarFechas() {
        LOGGER.debug("");

        if ((tipoConsulta == null) || tipoConsulta.getCdg().equals(TipoConsultaLineas.HISTORICO)) {
            fechasDisabled = false;
        } else if (tipoConsulta.getCdg().equals(TipoConsultaLineas.ULTIMO_REPORTE)) {
            fechasDisabled = true;
            fechaInicial = null;
            fechaFinal = null;
        }

        LOGGER.debug("{}", fechasDisabled);
    }

    /**
     * Rellena la lista de municipios por Estado seleccionado.
     * @throws Exception excepcion
     */
    public void habilitarMunicipio() throws Exception {

        LOGGER.debug("");

        if (estado != null) {
            setListaMunicipios(ngService.findMunicipiosByEstado(estado.getCodEstado()));
            listaPoblaciones = new ArrayList<Poblacion>();
            poblacion = null;
            claveCensal = null;
            abn = null;
        }
    }

    /**
     * Rellena la lista de poblaciones por estado y municipio.
     * @throws Exception excepcion
     */
    public void habilitarPoblacion() throws Exception {

        LOGGER.debug("");

        if (municipio != null) {
            listaPoblaciones = ngService.findAllPoblaciones(estado.getCodEstado(),
                    municipio.getId().getCodMunicipio());
            claveCensal = null;
            abn = null;
        }
    }

    /**
     * Habilita el campo modalidad.
     * @throws Exception error
     */
    public void habilitarModalidad() throws Exception {

        modalidadDisabled = true;

        if ((tipoRed != null) && (tipoRed.getCdg().equals(TipoRed.MOVIL))) {
            modalidadDisabled = false;
        }

        LOGGER.debug("{}", modalidadDisabled);
    }

    /**
     * Genera la clave censal y el abn de la numeracion solicitada.
     */
    public void generaClave() {

        LOGGER.debug("");

        setClaveCensal(poblacion.getInegi());

        setAbn(poblacion.getAbn().getCodigoAbn().toString());

    }

    /**
     * Action al cambiar el ABN.
     */
    public void abnChange() {
        LOGGER.debug("");
    }

    /**
     * Se obtienen el estado, municipio y poblacion al cambiar la clave censal.
     */
    public void claveCensalChange() {

        LOGGER.debug("");

        try {

            if (!claveCensal.matches("-?\\d+(\\.\\d+)?")) {
                MensajesBean.addErrorMsg(MSG_ID, "La clave censal tiene que ser un valor numerico");
            } else {
                Poblacion poblacionAux = ngService.findPoblacionById(claveCensal);
                if (poblacionAux != null) {
                    estado = poblacionAux.getMunicipio().getEstado();
                    municipio = poblacionAux.getMunicipio();
                    poblacion = poblacionAux;

                    listaMunicipios = ngService.findMunicipiosByEstado(estado.getCodEstado());
                    listaPoblaciones = ngService.findAllPoblaciones(estado.getCodEstado(),
                            municipio.getId().getCodMunicipio());

                    if (poblacion.getAbn() != null) {
                        abn = poblacion.getAbn().getCodigoAbn().toString();
                    }

                } else {
                    MensajesBean.addErrorMsg(MSG_ID, "No existe ubicación para la clave censal introducida");
                }
            }

        } catch (NumberFormatException e) {
            MensajesBean.addErrorMsg(MSG_ID, "Se ha encontrado un problema al cargar la ubicación por la clave censal");
            LOGGER.error("error", e);
        } catch (Exception e) {
            MensajesBean.addErrorMsg(MSG_ID, "Se ha encontrado un problema al cargar la ubicación por la clave censal");
            LOGGER.error("error", e);
        }
    }

    /**
     * Realiza la busqueda de reportes de lineas activas.
     */
    public void realizarBusqueda() {

        LOGGER.debug("");

        if (tipoConsulta == null) {
            MensajesBean.addErrorMsg(MSG_ID, "Seleccione Datos a consultar.");
        } else if (tipoConsulta.getCdg().equals(TipoConsultaLineas.HISTORICO)
                && (fechaInicial != null) && (fechaFinal != null) && (fechaFinal.before(fechaInicial))) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0006);
        } else {
            filtro.clear();
            model.setService(ngService);
            if (StringUtils.isNotEmpty(abn)) {
                filtro.setAbn(abn);
            }

            if (StringUtils.isNotEmpty(claveCensal)) {
                filtro.setClaveCensal(claveCensal);
            }

            filtro.setEstado(estado);

            if (tipoConsulta.getCdg().equals(TipoConsultaLineas.HISTORICO)) {
                filtro.setHistorico(true);
                filtro.setFechaFinal(fechaFinal);
                filtro.setFechaInicial(fechaInicial);
            } else {
                filtro.setHistorico(false);
            }

            filtro.setMunicipio(municipio);
            filtro.setPoblacion(poblacion);
            filtro.setPst(concesionario);

            PaginatorUtil.resetPaginacion("FRM_buscarLineasActivas:TBL_LineasActivas",
                    filtro.getResultadosPagina());
            emptySearch = ngService.findAllDetalleReporteCount(filtro) == 0;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("filtro {}", filtro);
            }

        }
    }

    /**
     * Limpia los datos de la busqueda.
     */
    public void limpiarBusqueda() {

        LOGGER.debug("");

        concesionario = null;
        fechaInicial = null;
        fechaFinal = null;
        estado = null;
        municipio = null;
        poblacion = null;
        claveCensal = null;
        abn = null;
        emptySearch = true;

        listaMunicipios = null;
        listaPoblaciones = null;

        fechasDisabled = true;
        modalidadDisabled = true;

        tipoConsulta = null;

        filtro.clear();
        model.setService(null);
        model.setRowCount(0);
    }

    /**
     * reset.
     */
    public void reset() {

        LOGGER.debug("");

        cargaLineasActivasBean.resetTabs();
        realizarBusqueda();
    }

    /**
     * Obtiene un fichero excel con la consulta de lineas activas.
     * @return fichero
     * @throws Exception error
     */
    public StreamedContent getFicheroLineasActivas() throws Exception {

        InputStream stream = ngService.getExportConsultaLineaActiva(filtro);
        String docName = "lineas_activas.xlsx";
        stream.close();
        LOGGER.debug("docname {}", docName);
        return new DefaultStreamedContent(stream,
                "application/vnd.ms-excel", docName);

    }

    /**
     * concesionario seleccionado.
     * @return concesionario
     */
    public Proveedor getConcesionario() {
        return concesionario;
    }

    /**
     * concesionario seleccionado.
     * @param concesionario concesionario to set
     */
    public void setConcesionario(Proveedor concesionario) {
        this.concesionario = concesionario;
    }

    /**
     * Lista de concesionarios.
     * @return listaConcesionarios
     */
    public List<Proveedor> getListaConcesionarios() {
        return listaConcesionarios;
    }

    /**
     * Lista de concesionarios.
     * @param listaConcesionarios listaConcesionarios to set
     */
    public void setListaConcesionarios(List<Proveedor> listaConcesionarios) {
        this.listaConcesionarios = listaConcesionarios;
    }

    /**
     * Fecha inicio consulta.
     * @return fechaInicio
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * Fecha inicio consulta.
     * @param fechaInicio fechaInicio to set
     */
    public void setFechaInicial(Date fechaInicio) {
        this.fechaInicial = fechaInicio;
    }

    /**
     * Fecha final consulta.
     * @return fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * Fecha final consulta.
     * @param fechaFinal fechaFinal to set
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Lista combo Municipios.
     * @return listaMunicipios
     */
    public List<Municipio> getListaMunicipios() {
        return listaMunicipios;
    }

    /**
     * Lista combo Municipios.
     * @param listaMunicipios listaMunicipios to set
     */
    public void setListaMunicipios(List<Municipio> listaMunicipios) {
        this.listaMunicipios = listaMunicipios;
    }

    /**
     * Lista combo Estados.
     * @return listaEstados
     */
    public List<Estado> getListaEstados() {
        return listaEstados;
    }

    /**
     * Lista combo Estados.
     * @param listaEstados listaEstados to set
     */
    public void setListaEstados(List<Estado> listaEstados) {
        this.listaEstados = listaEstados;
    }

    /**
     * Municipio seleccionado.
     * @return municipio
     */
    public Municipio getMunicipio() {
        return municipio;
    }

    /**
     * Municipio seleccionado.
     * @param municipio municipio to set
     */
    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    /**
     * Poblacion seleccionada.
     * @return poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * Poblacion seleccionada.
     * @param poblacion poblacion to set
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * Lista combo de poblaciones.
     * @return listaPoblaciones
     */
    public List<Poblacion> getListaPoblaciones() {
        return listaPoblaciones;
    }

    /**
     * Lista combo de poblaciones.
     * @param listaPoblaciones listaPoblaciones to set
     */
    public void setListaPoblaciones(List<Poblacion> listaPoblaciones) {
        this.listaPoblaciones = listaPoblaciones;
    }

    /**
     * Clave censal.
     * @return claveCensal
     */
    public String getClaveCensal() {
        return claveCensal;
    }

    /**
     * Clave censal.
     * @param claveCensal claveCensal to set
     */
    public void setClaveCensal(String claveCensal) {
        this.claveCensal = claveCensal;
    }

    /**
     * ABN.
     * @return abn
     */
    public String getAbn() {
        return abn;
    }

    /**
     * ABN.
     * @param abn abn to set
     */
    public void setAbn(String abn) {
        this.abn = abn;
    }

    /**
     * Referencia al Bean de Carga de Lineas Activas.
     * @return cargaLineasActivasBean
     */
    public CargaLineasActivasWizard getCargaLineasActivasBean() {
        return cargaLineasActivasBean;
    }

    /**
     * Referencia al Bean de Carga de Lineas Activas.
     * @param cargaLineasActivasBean cargaLineasActivasBean to set
     */
    public void setCargaLineasActivasBean(CargaLineasActivasWizard cargaLineasActivasBean) {
        this.cargaLineasActivasBean = cargaLineasActivasBean;
    }

    /**
     * Estado seleccionado.
     * @return estado
     */
    public Estado getEstado() {
        return estado;
    }

    /**
     * Estado seleccionado.
     * @param estado estado to set
     */
    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    /**
     * Modelo Lazy para reportes de lineas activas.
     * @return model
     */
    public SolicitudLineasActivasLazyModel getModel() {
        return model;
    }

    /**
     * Modelo Lazy para reportes de lineas activas.
     * @param model model to set
     */
    public void setModel(SolicitudLineasActivasLazyModel model) {
        this.model = model;
    }

    /**
     * Lista de tipos de consulta.
     * @return listaTiposConsulta
     */
    public List<TipoConsultaLineas> getListaTiposConsulta() {
        return listaTiposConsulta;
    }

    /**
     * Lista de tipos de consulta.
     * @param listaTiposConsulta listaTiposConsulta to set
     */
    public void setListaTiposConsulta(List<TipoConsultaLineas> listaTiposConsulta) {
        this.listaTiposConsulta = listaTiposConsulta;
    }

    /**
     * Tipo de consulta seleccionada.
     * @return tipoConsulta
     */
    public TipoConsultaLineas getTipoConsulta() {
        return tipoConsulta;
    }

    /**
     * Tipo de consulta seleccionada.
     * @param tipoConsulta tipoConsulta to set
     */
    public void setTipoConsulta(TipoConsultaLineas tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    /**
     * Tipo de red seleccionada.
     * @return tipoRed
     */
    public TipoRed getTipoRed() {
        return tipoRed;
    }

    /**
     * Tipo de red seleccionada.
     * @param tipoRed tipoRed to set
     */
    public void setTipoRed(TipoRed tipoRed) {
        this.tipoRed = tipoRed;
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
     * @param tiposRed tiposRed to set
     */
    public void setTiposRed(List<TipoRed> tiposRed) {
        this.tiposRed = tiposRed;
    }

    /**
     * Tipo de modalidad seleccionada.
     * @return tipoModalidad
     */
    public TipoModalidad getTipoModalidad() {
        return tipoModalidad;
    }

    /**
     * Tipo de modalidad seleccionada.
     * @param tipoModalidad tipoModalidad to set
     */
    public void setTipoModalidad(TipoModalidad tipoModalidad) {
        this.tipoModalidad = tipoModalidad;
    }

    /**
     * Lista de tipos de modadalidad.
     * @return tiposModalidad
     */
    public List<TipoModalidad> getTiposModalidad() {
        return tiposModalidad;
    }

    /**
     * Lista de tipos de modadalidad.
     * @param tiposModalidad tiposModalidad to set
     */
    public void setTiposModalidad(List<TipoModalidad> tiposModalidad) {
        this.tiposModalidad = tiposModalidad;
    }

    /**
     * Indica si las fechas estan desactivadas.
     * @return fechasDisabled
     */
    public boolean isFechasDisabled() {
        return fechasDisabled;
    }

    /**
     * Indica si las fechas estan desactivadas.
     * @param fechasDisabled fechasDisabled to set
     */
    public void setFechasDisabled(boolean fechasDisabled) {
        this.fechasDisabled = fechasDisabled;
    }

    /**
     * Modalidad disabled.
     * @return modalidadDisabled
     */
    public boolean isModalidadDisabled() {
        return modalidadDisabled;
    }

    /**
     * Modalidad disabled.
     * @param modalidadDisabled modalidadDisabled to set
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
