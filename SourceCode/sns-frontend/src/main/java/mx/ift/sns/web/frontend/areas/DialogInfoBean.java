package mx.ift.sns.web.frontend.areas;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.negocio.IConfiguracionFacade;
import mx.ift.sns.negocio.IConsultaPublicaFacade;
import mx.ift.sns.web.frontend.util.PaginatorUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Muestra el listado de provedores y renderiza las tablas correspondiente del dialog.
 * @author X51461MO
 */
@ManagedBean(name = "dialogInfoBean")
@ViewScoped
public class DialogInfoBean implements Serializable {

    /** Servicio de Busqueda Publica. */
    @EJB(mappedName = "ConsultaPublicaFacade")
    private IConsultaPublicaFacade ngPublicService;

    /** Servicio de configuración Publica. */
    @EJB(mappedName = "ConfiguracionFacade")
    private IConfiguracionFacade ngConfiguracionService;

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DialogInfoBean.class);

    /** Flag comprobacion input poblacion activado. */
    private boolean populationInputActivated = true;

    /** Flag comprobacion boton formulario activado. */
    private boolean formButtonActivated = false;

    /** Flag para renderizado de tabla poblacion. */
    private boolean populationsTableActivated = false;

    /**
     * Lista de concesionarios que prestan servicio en un estado.
     */
    private List<Proveedor> concesionariosEstado;

    /**
     * Flag para activar la tabla de info municipio.
     */
    private boolean tableMunicipioActivated;

    /**
     * Flag de activación de la tabla e proveedores/estado.
     */
    private boolean tablaProvEstadoActivated = false;
    /**
     * Flag de activación de la tabla de proveedores/ abn.
     */
    private boolean tablaProvAbnActivated = false;

    /**
     * String con los distintos número de registros por página. Por defecto: 5,10,15,20,15
     */
    private String numeroRegistros = "5, 10, 15, 20, 25";
    /**
     * Listado con las poblaciones con numeración asignada de un municipio y con mismo NIR.
     */
    private List<Poblacion> poblacionesMunicipio;
    /**
     * Listado con poblaciones con numeración asignada de un NIR.
     */
    private List<Poblacion> poblacionesNir;
    /**
     * Listado con los municipios con numeración asginada de un NIR.
     */
    private List<Municipio> municipiosNir;

    // ////////////////////////////MÉTODOS////////////////////////////////////
    /**
     * Setea la lista de proveedores estado y activa la tabla proveedor/estado del dialog.
     * @param proveedoresEstado List<Proveedor>
     */
    public void setAndActivatedProveedorEstado(List<Proveedor> proveedoresEstado) {
        try {
            int numeroRegistro = PaginatorUtil.getRegistrosPorPagina(ngConfiguracionService);
            PaginatorUtil.resetPaginacion(":TBL_proveedoresEstado", numeroRegistro);
            this.setNumeroRegistros(PaginatorUtil.getGruposRegistrosPorPagina(numeroRegistro));

            this.setConcesionariosEstado(proveedoresEstado);
            this.setTablaProvEstadoActivated(true);
            this.setTablaProvAbnActivated(false);
            this.setTableMunicipioActivated(false);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al carga el listado de proveedores de un estado " + e.getMessage());
        }
    }

    /**
     * Activa la tabla Proveedores/Abn del dialog.
     */
    public void setAndActivatedProveedorNir() {
        try {
            int numeroRegistro = PaginatorUtil.getRegistrosPorPagina(ngConfiguracionService);
            PaginatorUtil.resetPaginacion(":TBL_proveedoresNir", numeroRegistro);
            this.setNumeroRegistros(PaginatorUtil.getGruposRegistrosPorPagina(numeroRegistro));
            this.setTablaProvEstadoActivated(false);
            this.setTableMunicipioActivated(false);
            this.setTablaProvAbnActivated(true);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al carga el listado de proveedores de un ABN" + e.getMessage());
        }

    }

    /**
     * Activa la tabla Proveedor/Municipio del dialog.
     */
    public void setAndActivatedProveedorMunicipio() {
        try {
            int numeroRegistro = PaginatorUtil.getRegistrosPorPagina(ngConfiguracionService);
            PaginatorUtil.resetPaginacion(":TBL_proveedoreMunicipio", numeroRegistro);
            this.setNumeroRegistros(PaginatorUtil.getGruposRegistrosPorPagina(numeroRegistro));
            this.setTableMunicipioActivated(true);
            this.setTablaProvEstadoActivated(false);
            this.setTablaProvAbnActivated(false);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al carga el listado de proveedores de un municipio " + e.getMessage());
        }

    }

    /**
     * Setea las poblaciones con numeración asignada de un municipio y hace reset de la paginación del dataTable cada
     * vez que se cargan los datos.
     * @param municipioSeleccionado Municipio
     */
    public void setPoblacionesMunicipio(Municipio municipioSeleccionado) {

        try {
            int numeroRegistro = PaginatorUtil.getRegistrosPorPagina(ngConfiguracionService);
            PaginatorUtil.resetPaginacion(":TBL_pobM", numeroRegistro);
            this.setNumeroRegistros(PaginatorUtil.getGruposRegistrosPorPagina(numeroRegistro));
            this.setPoblacionesMunicipio(ngPublicService
                    .findALLPoblacionesNumeracionAsignadaByMunicipio(municipioSeleccionado));
        } catch (Exception e) {
            LOGGER.error("Error inesperado al carga el listado de poblaciones de un municipio " + e.getMessage());
        }
    }

    /**
     * Setea las poblaciones con numeración asignada de un NIR y hace reset de la paginación del dataTable cada vez que
     * se cargan los datos.
     * @param nirSeleccionado Nir
     */
    public void setPoblacionesNumeracionNir(Nir nirSeleccionado) {
        try {
            int numeroRegistro = PaginatorUtil.getRegistrosPorPagina(ngConfiguracionService);
            PaginatorUtil.resetPaginacion(":TBL_pobNir", numeroRegistro);
            this.setNumeroRegistros(PaginatorUtil.getGruposRegistrosPorPagina(numeroRegistro));
            this.setPoblacionesNir(ngPublicService.findALLPoblacionesNumeracionAsignadaByNir(nirSeleccionado));
        } catch (Exception e) {
            LOGGER.error("Error inesperado al cargar las poblaciones con numeración asignada de un NIR "
                    + e.getMessage());
        }
    }

    /**
     * Setea los municipios con numeración asignada de un NIR y hace reset de la paginación del dataTable cada vez que
     * se cargan los datos.
     * @param nirSeleccionado Nir
     */
    public void setMunicipiosNumeracionNir(Nir nirSeleccionado) {
        try {
            int numeroRegistro = PaginatorUtil.getRegistrosPorPagina(ngConfiguracionService);
            PaginatorUtil.resetPaginacion(":TBL_municipiosNir", numeroRegistro);
            this.setNumeroRegistros(PaginatorUtil.getGruposRegistrosPorPagina(numeroRegistro));
            this.setMunicipiosNir(ngPublicService.findAllMunicipiosByNir(nirSeleccionado, true));
        } catch (Exception e) {
            LOGGER.error("Error inesperado al carga el listado de municipios de un Nir " + e.getMessage());
        }
    }

    // ////////////////////////////////GETTERS AND SETTERS////////////////////////////

    /**
     * Flag de activación de la tabla e proveedores/estado.
     * @return the tablaProvEstadoActivated
     */
    public boolean isTablaProvEstadoActivated() {
        return tablaProvEstadoActivated;
    }

    /**
     * Flag de activación de la tabla e proveedores/estado.
     * @param tablaProvEstadoActivated the tablaProvEstadoActivated to set
     */
    public void setTablaProvEstadoActivated(boolean tablaProvEstadoActivated) {
        this.tablaProvEstadoActivated = tablaProvEstadoActivated;
    }

    /**
     * Flag de activación de la tabla de proveedores/ abn.
     * @return the tablaProvAbnActivated
     */
    public boolean isTablaProvAbnActivated() {
        return tablaProvAbnActivated;
    }

    /**
     * Flag de activación de la tabla de proveedores/ abn.
     * @param tablaProvAbnActivated the tablaProvAbnActivated to set
     */
    public void setTablaProvAbnActivated(boolean tablaProvAbnActivated) {
        this.tablaProvAbnActivated = tablaProvAbnActivated;
    }

    /**
     * Flag para activar la tabla de info municipio.
     * @return the tableMunicipioActivate
     */
    public boolean isTableMunicipioActivated() {
        return tableMunicipioActivated;
    }

    /**
     * Flag para activar la tabla de info municipio.
     * @param tableMunicipioActivated the tableMunicipioActivate to set
     */
    public void setTableMunicipioActivated(boolean tableMunicipioActivated) {
        this.tableMunicipioActivated = tableMunicipioActivated;
    }

    /**
     * Flag comprobacion input poblacion activado.
     * @return the populationInputActivated
     */
    public boolean isPopulationInputActivated() {
        return populationInputActivated;
    }

    /**
     * Flag comprobacion input poblacion activado.
     * @param populationInputActivated the populationInputActivated to set
     */
    public void setPopulationInputActivated(boolean populationInputActivated) {
        this.populationInputActivated = populationInputActivated;
    }

    /**
     * Flag comprobacion boton formulario activado.
     * @return the formButtonActivated
     */
    public boolean isFormButtonActivated() {
        return formButtonActivated;
    }

    /**
     * Flag comprobacion boton formulario activado.
     * @param formButtonActivated the formButtonActivated to set
     */
    public void setFormButtonActivated(boolean formButtonActivated) {
        this.formButtonActivated = formButtonActivated;
    }

    /**
     * Flag para renderizado de tabla poblacion.
     * @return the populationsTableActivated
     */
    public boolean isPopulationsTableActivated() {
        return populationsTableActivated;
    }

    /**
     * Flag para renderizado de tabla poblacion.
     * @param populationsTableActivated the populationsTableActivated to set
     */
    public void setPopulationsTableActivated(boolean populationsTableActivated) {
        this.populationsTableActivated = populationsTableActivated;
    }

    /**
     * Lista de concesionarios que prestan servicio en un estado.
     * @return the concesionariosEstado
     */
    public List<Proveedor> getConcesionariosEstado() {
        return concesionariosEstado;
    }

    /**
     * Lista de concesionarios que prestan servicio en un estado.
     * @param concesionariosEstado the concesionariosEstado to set
     */
    public void setConcesionariosEstado(List<Proveedor> concesionariosEstado) {
        this.concesionariosEstado = concesionariosEstado;
    }

    /**
     * String con los distintos número de registros por página. Por defecto: 5,10,15,20,15
     * @return the numeroRegistros
     */
    public String getNumeroRegistros() {
        return numeroRegistros;
    }

    /**
     * String con los distintos número de registros por página. Por defecto: 5,10,15,20,15
     * @param numeroRegistros the numeroRegistros to set
     */
    public void setNumeroRegistros(String numeroRegistros) {
        this.numeroRegistros = numeroRegistros;
    }

    /**
     * Listado con las poblaciones de un municipio y con mismo NIR.
     * @return the poblacionesMunicipio
     */
    public List<Poblacion> getPoblacionesMunicipio() {
        return poblacionesMunicipio;
    }

    /**
     * Listado con las poblaciones de un municipio y con mismo NIR.
     * @param poblacionesMunicipio the poblacionesMunicipio to set
     */
    public void setPoblacionesMunicipio(List<Poblacion> poblacionesMunicipio) {
        this.poblacionesMunicipio = poblacionesMunicipio;
    }

    /**
     * Listado con poblaciones con numeración asignada de un NIR.
     * @return the poblacionesNir
     */
    public List<Poblacion> getPoblacionesNir() {
        return poblacionesNir;
    }

    /**
     * Listado con poblaciones con numeración asignada de un NIR.
     * @param poblacionesNir the poblacionesNir to set
     */
    public void setPoblacionesNir(List<Poblacion> poblacionesNir) {
        this.poblacionesNir = poblacionesNir;
    }

    /**
     * Listado con los municipios con numeración asginada de un NIR.
     * @return the municipiosNir
     */
    public List<Municipio> getMunicipiosNir() {
        return municipiosNir;
    }

    /**
     * Listado con los municipios con numeración asginada de un NIR.
     * @param municipiosNir the municipiosNir to set
     */
    public void setMunicipiosNir(List<Municipio> municipiosNir) {
        this.municipiosNir = municipiosNir;
    }

}
