package mx.ift.sns.web.backend.ac.municipio;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.modelo.filtros.FiltroBusquedaMunicipios;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.ExportMunicipio;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Region;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.lazymodels.MunicipioLazyModel;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Managed Bean de la consulta de municipios. */
@ManagedBean(name = "consultarMunicipiosBean")
@ViewScoped
public class ConsultarMunicipiosBean implements Serializable {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarMunicipiosBean.class);

    /** Servicio de Administracion de catalogos. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade adminCatFacadeService;

    /** Referencia al Bean de nuevo municipio. */
    @ManagedProperty("#{nuevoMunicipioBean}")
    private NuevoMunicipioBean nuevoMunicipioBean;

    /** Referencia al Bean de edición de municipio. */
    @ManagedProperty("#{editarMunicipioBean}")
    private EditarMunicipioBean editarMunicipioBean;

    /** ID mensaje. */
    private static final String MSG_ID = "MSG_consultaMunicipios";

    /** Lista de municipios. */
    private MunicipioLazyModel listaMunicipios;

    /** Lista de estados. */
    private List<Estado> listaEstados;

    /** Estado seleccionado. */
    private Estado estado;

    /** Clave municipio. */
    private String claveMunicipio;

    /** Nombre Municipio. */
    private String nombreMunicipio;

    /** Lista regiones celular. */
    private List<Region> listaRegionesCelular;

    /** Region celular seleccionada. */
    private Region regionCelular;

    /** Lista regiones PCS. */
    private List<Region> listaRegionesPcs;

    /** Region PCS seleccionada. */
    private Region regionPcs;

    /** Filtros de busqueda. */
    private FiltroBusquedaMunicipios filtros;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Activa el botón exportar si no hay resultados en la búsqueda. */
    private boolean emptySearch = true;
    /**
     * Municipio seleccionado al eliminar.
     */
    private DetalleMunicipio municipioSeleccionado;

    /** Constructor, por defecto vacío. */
    public ConsultarMunicipiosBean() {
    }

    /**
     * Iniciamos la lista de Estados y Regiones.
     **/
    @PostConstruct
    public void init() {

        LOGGER.debug("");

        try {
            listaEstados = adminCatFacadeService.findAllEstados();
            listaRegionesCelular = adminCatFacadeService.findAllRegiones();
            listaRegionesPcs = adminCatFacadeService.findAllRegiones();

            // Cargamos el lazy de municipios
            setFiltros(new FiltroBusquedaMunicipios());
            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(adminCatFacadeService,
                    Parametro.REGISTROS_POR_PAGINA_BACK));
            registroPorPagina = filtros.getResultadosPagina();

            listaMunicipios = new MunicipioLazyModel();
            listaMunicipios.setService(adminCatFacadeService);
            listaMunicipios.setFiltros(filtros);
            this.emptySearch = adminCatFacadeService.findAllMunicipiosCount(filtros) == 0;
        } catch (Exception e) {
            LOGGER.error("error", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Realiza la busqueda de municipios. */
    public void realizarBusqueda() {

        if (isNumeric(claveMunicipio)) {

            listaMunicipios = new MunicipioLazyModel();
            listaMunicipios.setService(adminCatFacadeService);

            filtros.clear();

            filtros.setResultadosPagina(PaginatorUtil.getRegistrosPorPagina(adminCatFacadeService,
                    Parametro.REGISTROS_POR_PAGINA_BACK));

            // Cargamos los filtros
            filtros.setCodMunicipio(claveMunicipio);
            filtros.setEstado(estado);
            filtros.setNombre(nombreMunicipio);
            filtros.setRegionCelular(regionCelular);
            filtros.setRegionPcs(regionPcs);

            PaginatorUtil.resetPaginacion("FRM_consultaMunicipios:TBL_Municipios",
                    filtros.getResultadosPagina());

            listaMunicipios.setResetPaginacion(true);
            listaMunicipios.setFiltros(filtros);
            this.emptySearch = adminCatFacadeService.findAllMunicipiosCount(filtros) == 0;
        } else {
            MensajesBean.addErrorMsg(MSG_ID, "El campo Clave Municipio no es númerico", "");
        }
    }

    /** Limpia los datos de busqueda. */
    public void limpiarBusqueda() {
        estado = null;
        claveMunicipio = null;
        nombreMunicipio = null;
        emptySearch = true;
        regionPcs = null;
        regionCelular = null;

        filtros.clear();
        listaMunicipios = null;
    }

    /**
     * Pasa a estado inactivo un municipio.
     */
    public void cancelar() {
        try {

            if (municipioSeleccionado != null) {
                if (!municipioSeleccionado.getNumeracionAsignada().equals("SI")) {
                    Municipio municipio = municipioSeleccionado.getMunicipio();
                    Estatus status = new Estatus();
                    status.setCdg(Estatus.INACTIVO);
                    municipio.setEstatus(status);
                    municipio = adminCatFacadeService.saveMunicipio(municipio);
                    municipioSeleccionado.setMunicipio(municipio);
                    MensajesBean.addInfoMsg(MSG_ID,
                            "Municipio dado de baja correctamente", "");
                } else {
                    MensajesBean.addErrorMsg(MSG_ID,
                            "No se puede dar de baja un municipio con numeraciones asignadas", "");
                }
            } else {
                MensajesBean.addErrorMsg(MSG_ID,
                        "No existe detalle de municipio", "");
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Pasa a estado activo un muncipio.
     */
    public void activar() {
        try {
            if (municipioSeleccionado != null) {
                Municipio municipio = municipioSeleccionado.getMunicipio();
                Estatus status = new Estatus();
                status.setCdg(Estatus.ACTIVO);
                municipio.setEstatus(status);
                municipio = adminCatFacadeService.saveMunicipio(municipio);
                municipioSeleccionado.setMunicipio(municipio);
                MensajesBean.addInfoMsg(MSG_ID,
                        "Municipio activado correctamente", "");
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Setea el municipio seleccionado al bean de editar.
     * @param detalle municipio
     */
    public void editar(DetalleMunicipio detalle) {
        editarMunicipioBean.setMunicipio(detalle.getMunicipio());

        editarMunicipioBean.getIdMuncipioAnt().setCodEstado(detalle.getMunicipio().getId().getCodEstado());
        editarMunicipioBean.getIdMuncipioAnt().setCodMunicipio(detalle.getMunicipio().getId().getCodMunicipio());
    }

    /**
     * Resetea modales.
     */
    public void reset() {
        nuevoMunicipioBean.resetTab();
        editarMunicipioBean.resetTab();
        realizarBusqueda();
    }

    /**
     * Retorna el fichero excel de municipios.
     * @return fichero municipios
     * @throws Exception error
     */
    public StreamedContent getFicheroMunicipios() throws Exception {

        LOGGER.debug("");

        List<ExportMunicipio> listaDatos = adminCatFacadeService.findAllExportMunicipio(listaMunicipios
                .getFiltros());
        InputStream stream = adminCatFacadeService.generarListadoMunicipios(listaDatos);
        String docName = "listado_municipios.xlsx";
        stream.close();
        LOGGER.debug("docname {}", docName);
        return new DefaultStreamedContent(stream,
                "application/vnd.ms-excel", docName);

    }

    /**
     * Método para saber si es numerica la cadena.
     * @param cadena cadena de entrada.
     * @return boolean true/false.
     */
    private static boolean isNumeric(String cadena) {
        return cadena.matches("\\d*");

    }

    /**
     * Obtiene la Lista de estados.
     * @return List<Estado>
     */
    public List<Estado> getListaEstados() {
        return listaEstados;
    }

    /**
     * Carga la Lista de estados.
     * @param listaEstados List<Estado>
     */
    public void setListaEstados(List<Estado> listaEstados) {
        this.listaEstados = listaEstados;
    }

    /**
     * Obtiene Estado seleccionado.
     * @return Estado
     */
    public Estado getEstado() {
        return estado;
    }

    /**
     * Carga Estado seleccionado.
     * @param estado Estado
     */
    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    /**
     * Obtiene Clave municipio.
     * @return String
     */
    public String getClaveMunicipio() {
        return claveMunicipio;
    }

    /**
     * Carga Clave municipio.
     * @param claveMunicipio String
     */
    public void setClaveMunicipio(String claveMunicipio) {
        this.claveMunicipio = claveMunicipio;
    }

    /**
     * Obtiene Nombre Municipio.
     * @return String
     */
    public String getNombreMunicipio() {
        return nombreMunicipio;
    }

    /**
     * Carga Nombre Municipio.
     * @param nombreMunicipio String
     */
    public void setNombreMunicipio(String nombreMunicipio) {
        this.nombreMunicipio = nombreMunicipio;
    }

    /**
     * Obtiene Lista regiones celular.
     * @return List<Region>
     */
    public List<Region> getListaRegionesCelular() {
        return listaRegionesCelular;
    }

    /**
     * Carga Lista regiones celular.
     * @param listaRegionesCelular List<Region>
     */
    public void setListaRegionesCelular(List<Region> listaRegionesCelular) {
        this.listaRegionesCelular = listaRegionesCelular;
    }

    /**
     * Obtiene Region celular seleccionada.
     * @return Region
     */
    public Region getRegionCelular() {
        return regionCelular;
    }

    /**
     * Carga Region celular seleccionada.
     * @param regionCelular Region
     */
    public void setRegionCelular(Region regionCelular) {
        this.regionCelular = regionCelular;
    }

    /**
     * Obtiene Lista regiones PCS.
     * @return List<Region>
     */
    public List<Region> getListaRegionesPcs() {
        return listaRegionesPcs;
    }

    /**
     * Carga Lista regiones PCS.
     * @param listaRegionesPcs List<Region>
     */
    public void setListaRegionesPcs(List<Region> listaRegionesPcs) {
        this.listaRegionesPcs = listaRegionesPcs;
    }

    /**
     * Obtiene Region PCS seleccionada.
     * @return Region
     */
    public Region getRegionPcs() {
        return regionPcs;
    }

    /**
     * Carga Region PCS seleccionada.
     * @param regionPcs Region
     */
    public void setRegionPcs(Region regionPcs) {
        this.regionPcs = regionPcs;
    }

    /**
     * Obtiene la Lista de municipios.
     * @return MunicipioLazyModel
     */
    public MunicipioLazyModel getListaMunicipios() {
        return listaMunicipios;
    }

    /**
     * Carga la Lista de municipios.
     * @param listaMunicipios MunicipioLazyModel
     */
    public void setListaMunicipios(MunicipioLazyModel listaMunicipios) {
        this.listaMunicipios = listaMunicipios;
    }

    /**
     * Obtiene Filtros de busqueda.
     * @return FiltroBusquedaMunicipios
     */
    public FiltroBusquedaMunicipios getFiltros() {
        return filtros;
    }

    /**
     * Carga Filtros de busqueda.
     * @param filtros FiltroBusquedaMunicipios
     */
    public void setFiltros(FiltroBusquedaMunicipios filtros) {
        this.filtros = filtros;
    }

    /**
     * Obtiene la Referencia al Bean de nuevo municipio.
     * @return NuevoMunicipioBean
     */
    public NuevoMunicipioBean getNuevoMunicipioBean() {
        return nuevoMunicipioBean;
    }

    /**
     * Carga la Referencia al Bean de nuevo municipio.
     * @param nuevoMunicipioBean NuevoMunicipioBean
     */
    public void setNuevoMunicipioBean(NuevoMunicipioBean nuevoMunicipioBean) {
        this.nuevoMunicipioBean = nuevoMunicipioBean;
    }

    /**
     * Obtiene Referencia al Bean de edición de municipio.
     * @return EditarMunicipioBean
     */
    public EditarMunicipioBean getEditarMunicipioBean() {
        return editarMunicipioBean;
    }

    /**
     * Carga la Referencia al Bean de edición de municipio.
     * @param editarMunicipioBean EditarMunicipioBean
     */
    public void setEditarMunicipioBean(EditarMunicipioBean editarMunicipioBean) {
        this.editarMunicipioBean = editarMunicipioBean;
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

    /**
     * Municipio seleccionado al eliminar.
     * @return the municipioSeleccionado
     */
    public DetalleMunicipio getMunicipioSeleccionado() {
        return municipioSeleccionado;
    }

    /**
     * Municipio seleccionado al eliminar.
     * @param municipioSeleccionado the municipioSeleccionado to set
     */
    public void setMunicipioSeleccionado(DetalleMunicipio municipioSeleccionado) {
        this.municipioSeleccionado = municipioSeleccionado;
    }
}
