package mx.ift.sns.web.backend.nng.lineasactivas;

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
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;
import mx.ift.sns.web.backend.ApplicationException;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.ng.lineasactivas.model.TipoConsultaLineas;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Consulta de los reportes de lineas activas.
 */
@ManagedBean(name = "consultarLineasActivasNngBean")
@ViewScoped
public class ConsultarLineasActivasNngBean implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 24973570852489092L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarLineasActivasNngBean.class);

    /** Id de mensajes de error. */
    private static final String MSG_ID = "MSG_Ubicacion";

    /** Fachada de Numeración no Geográfica. */
    @EJB(mappedName = "NumeracionNoGeograficaFacade")
    private INumeracionNoGeograficaFacade nngFacade;

    /** Referencia al Bean de Carga de Lineas Activas. */
    @ManagedProperty("#{cargaLineasActivasNngBean}")
    private CargaLineasActivasNngBean cargaLineasActivasBean;

    /** proveedor seleccionado. */
    private Proveedor proveedor;

    /** Lista de proveedors. */
    private List<Proveedor> listaProveedores;

    /** Fecha inicio consulta. */
    private Date fechaInicial;

    /** Fecha final consulta. */
    private Date fechaFinal;

    /** Indica si las fechas estan desactivadas. */
    private boolean fechasDisabled;

    /** Lista de tipos de consulta. */
    private List<TipoConsultaLineas> listaTiposConsulta;

    /** Tipo de consulta seleccionada. */
    private TipoConsultaLineas tipoConsulta;

    /** Modelo Lazy para consulta de lineas activas. */
    private DetalleReporteNngLazyModel model;

    /** Filtro de busqueda. */
    private FiltroBusquedaLineasActivas filtro;

    /** Clave de servicio seleccionada. */
    private ClaveServicio claveServicio;

    /** Lista de claves de servicio. */
    private List<ClaveServicio> listaClavesServicio;

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

            // Cargamos el combo de proveedors.
            listaProveedores = nngFacade.findAllProveedoresActivos();

            // Precargamos los datos.
            listaClavesServicio = nngFacade.findAllClaveServicioActivas();

            tipoConsulta = null;

            listaTiposConsulta = new ArrayList<TipoConsultaLineas>();

            listaTiposConsulta.add(TipoConsultaLineas.TIPO_HISTORICO);
            listaTiposConsulta.add(TipoConsultaLineas.TIPO_ULTIMO_REPORTE);

            fechasDisabled = true;

            filtro = new FiltroBusquedaLineasActivas();
            filtro.clear();

            filtro.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(nngFacade,
                    Parametro.REGISTROS_POR_PAGINA_BACK));
            registroPorPagina = filtro.getResultadosPagina();

            model = new DetalleReporteNngLazyModel();
            model.setFiltros(filtro);
            // model.setFacade(nngFacade);

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
     * Realiza la busqueda de reportes de lineas activas.
     */
    public void realizarBusqueda() {

        if (tipoConsulta == null) {
            MensajesBean.addErrorMsg(MSG_ID, "Seleccione Datos a consultar.");

        } else if (tipoConsulta.getCdg().equals(TipoConsultaLineas.HISTORICO)
                && (fechaInicial != null) && (fechaFinal != null) && (fechaFinal.before(fechaInicial))) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0006);
        } else {
            filtro.clear();

            filtro.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(nngFacade,
                    Parametro.REGISTROS_POR_PAGINA_BACK));

            filtro.setClaveServicio(claveServicio);

            if (tipoConsulta.getCdg().equals(TipoConsultaLineas.HISTORICO)) {
                filtro.setHistorico(true);
                filtro.setFechaFinal(fechaFinal);
                filtro.setFechaInicial(fechaInicial);
            } else {
                filtro.setHistorico(false);
            }

            filtro.setPst(proveedor);

            PaginatorUtil.resetPaginacion("FRM_buscarLineasActivas:TBL_LineasActivas",
                    filtro.getResultadosPagina());

            model.setFacade(nngFacade);
            emptySearch = nngFacade.findAllDetalleReporteCount(filtro) == 0;
        }
    }

    /**
     * Limpia los datos de la busqueda.
     */
    public void limpiarBusqueda() {

        LOGGER.debug("");

        proveedor = null;
        fechaInicial = null;
        fechaFinal = null;
        claveServicio = null;
        emptySearch = true;
        fechasDisabled = true;

        tipoConsulta = null;

        filtro.clear();

        model.setFacade(null);

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

        InputStream stream = nngFacade.getExportConsultaLineaActiva(filtro);
        String docName = "lineas_activas.xlsx";
        stream.close();
        LOGGER.debug("docname {}", docName);
        return new DefaultStreamedContent(stream,
                "application/vnd.ms-excel", docName);

    }

    /**
     * Lista de proveedors.
     * @return listaProveedores
     */
    public List<Proveedor> getListaProveedores() {
        return listaProveedores;
    }

    /**
     * Lista de proveedors.
     * @param listaProveedores listaProveedores to set
     */
    public void setListaProveedores(List<Proveedor> listaProveedores) {
        this.listaProveedores = listaProveedores;
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
     * Modelo Lazy para consulta de lineas activas.
     * @return model
     */
    public DetalleReporteNngLazyModel getModel() {
        return model;
    }

    /**
     * Modelo Lazy para consulta de lineas activas.
     * @param model model to set
     */
    public void setModel(DetalleReporteNngLazyModel model) {
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
     * Fachada de Numeración no Geográfica.
     * @return nngFacade
     */
    public INumeracionNoGeograficaFacade getNngFacade() {
        return nngFacade;
    }

    /**
     * Fachada de Numeración no Geográfica.
     * @param nngFacade nngFacade to set
     */
    public void setNngFacade(INumeracionNoGeograficaFacade nngFacade) {
        this.nngFacade = nngFacade;
    }

    /**
     * Referencia al Bean de Carga de Lineas Activas.
     * @return cargaLineasActivasBean
     */
    public CargaLineasActivasNngBean getCargaLineasActivasBean() {
        return cargaLineasActivasBean;
    }

    /**
     * Referencia al Bean de Carga de Lineas Activas.
     * @param cargaLineasActivasBean cargaLineasActivasBean to set
     */
    public void setCargaLineasActivasBean(CargaLineasActivasNngBean cargaLineasActivasBean) {
        this.cargaLineasActivasBean = cargaLineasActivasBean;
    }

    /**
     * proveedor.
     * @return proveedor
     */
    public Proveedor getProveedor() {
        return proveedor;
    }

    /**
     * proveedor.
     * @param proveedor proveedor to set
     */
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * Filtro de busqueda.
     * @return filtro
     */
    public FiltroBusquedaLineasActivas getFiltro() {
        return filtro;
    }

    /**
     * Filtro de busqueda.
     * @param filtro filtro to set
     */
    public void setFiltro(FiltroBusquedaLineasActivas filtro) {
        this.filtro = filtro;
    }

    /**
     * Clave de servicio seleccionada.
     * @return claveServicio
     */
    public ClaveServicio getClaveServicio() {
        return claveServicio;
    }

    /**
     * Clave de servicio seleccionada.
     * @param claveServicio claveServicio to set
     */
    public void setClaveServicio(ClaveServicio claveServicio) {
        this.claveServicio = claveServicio;
    }

    /**
     * Lista de claves de servicio.
     * @return listaClavesServicio
     */
    public List<ClaveServicio> getListaClavesServicio() {
        return listaClavesServicio;
    }

    /**
     * Lista de claves de servicio.
     * @param listaClavesServicio listaClavesServicio to set
     */
    public void setListaClavesServicio(List<ClaveServicio> listaClavesServicio) {
        this.listaClavesServicio = listaClavesServicio;
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
