package mx.ift.sns.web.backend.ac.cpsn;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.cpsn.EquipoSenalCPSN;
import mx.ift.sns.modelo.cpsn.EstudioEquipoCPSN;
import mx.ift.sns.modelo.cpsn.TipoBloqueCPSN;
import mx.ift.sns.modelo.filtros.FiltroBusquedaEquipoSenal;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean de la consulta de equipos de señalización.
 */
@ManagedBean(name = "consultarEquiposSenalizacionBean")
@ViewScoped
public class ConsultarEquiposSenalizacionBean implements Serializable {
    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarEquiposSenalizacionBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultarEquiposSenalizacion";

    /** Servicio de Catalogo. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade catalogoService;

    /** Proveedor de Servicio. */
    private Proveedor proveedorSeleccionado;

    /** Listado de Proveedores resultado. */
    private List<Proveedor> listadoProveedores;

    /** Código binario del CPSN. */
    private String codBinario;

    /** Código decimal individual. */
    private String decimalIndividual;

    /** Filtro de búsqueda de equipos de señalización. */
    private FiltroBusquedaEquipoSenal filtros;

    /** Listado de equipos de señalización. */
    private List<EquipoSenalCPSN> listadoEquipos;

    /** Equipo de señalización a editar. */
    private EquipoSenalCPSN equipoEdicion;

    /** Datos del estudio de equipos de señalización. */
    private List<EstudioEquipoCPSN> listadoEstudio;

    /** Flag para activar el boton de estudio. */
    private boolean activarEstudio;

    /** Bean asociado a la edicion de equipos de señalización. */
    @ManagedProperty("#{edicionEquipoSenalizacionBean}")
    private EdicionEquipoSenalizacionBean edicionEquipoSenalizacionBean;

    /** Activa el botón exportar si no hay resultados en la búsqueda. */
    private boolean emptySearch = true;

    /** Bean de la carga de equipos de señalización. */
    @ManagedProperty("#{cargarEquiposCpsnBean}")
    private CargarEquiposCpsnBean cargarEquiposCpsnBean;

    /**
     * Método que inicializa la consulta de equipos de señalización.
     */
    @PostConstruct
    public void init() {
        try {
            // Listado de proveedores de servicio
            listadoProveedores = catalogoService.findAllProveedores();
            proveedorSeleccionado = new Proveedor();

            // Carga inicial de todos los equipos
            filtros = new FiltroBusquedaEquipoSenal();
            listadoEquipos = catalogoService.findAllEquiposSenal(filtros);
            this.emptySearch = listadoEquipos.isEmpty();
            codBinario = "";
            decimalIndividual = "";
            activarEstudio = false;

        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error inesperado: " + e.getMessage());
            }
            MensajesBean.addErrorMsg("MSG_ConsultarEquiposSenalizacion",
                    MensajesBean.getTextoResource("errorGenerico"));
        }
    }

    /** Método invocado al pulsar el botón 'buscar'. */
    public void realizarBusqueda() {
        try {
            if (codBinario != null && !codBinario.isEmpty()
                    && decimalIndividual != null && !decimalIndividual.isEmpty()) {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("cpsn.equiposSenalizacion.consulta.codigo.error"), "");
            } else if (codBinario != null && !codBinario.isEmpty()
                    && codBinario.length() != TipoBloqueCPSN.TAM_BLOQUE_2048
                    && codBinario.length() != TipoBloqueCPSN.TAM_BLOQUE_128
                    && codBinario.length() != TipoBloqueCPSN.TAM_BLOQUE_8
                    && codBinario.length() != TipoBloqueCPSN.TAM_INDIVIDUAL) {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("cpsn.equiposSenalizacion.consulta.codigo.bloque.error"), "");
            } else {
                crearFiltros();
                listadoEquipos = catalogoService.findAllEquiposSenal(filtros);
                this.emptySearch = listadoEquipos.isEmpty();
            }
        } catch (Exception ex) {
            MensajesBean.addErrorMsg(MSG_ID, MensajesBean.getTextoResource("errorGenerico"));
        }
    }

    /** Creación de los filtros de búsqueda. */
    private void crearFiltros() {
        // Resetamos los filtros
        filtros.clear();

        // Filtro del proveedor
        if (proveedorSeleccionado != null) {
            filtros.setProveedor(proveedorSeleccionado);
        }
        // Filtro de cps binario
        if (codBinario != null && !codBinario.isEmpty()) {
            filtros.setCodBinario(codBinario);
        }
        // Filtro del cps decimal individual
        if (decimalIndividual != null && !decimalIndividual.isEmpty()) {
            filtros.setDecimalIndividual(decimalIndividual);
        }
    }

    /** Método invocado al pulsar el botón 'limpiar' sobre el buscador de equipos. */
    public void limpiarBusqueda() {
        emptySearch = true;
        proveedorSeleccionado = null;
        codBinario = null;
        decimalIndividual = null;
        listadoEquipos.clear();
        activarEstudio = false;
    }

    /**
     * Genera el excel de exportación de los equipos de señalización.
     * @return excel
     * @throws Exception e
     */
    public StreamedContent getExportar() throws Exception {
        crearFiltros();

        InputStream stream = new ByteArrayInputStream(catalogoService.exportarEquiposCPSN(filtros));
        String docName = "Equipos de Señalizacion Nacional";

        docName = docName.concat(".xlsx");
        stream.close();

        LOGGER.debug("docname {}", docName);

        return new DefaultStreamedContent(stream, "application/vnd.ms-excel", docName);
    }

    /** Función que edita el equipo de señalización seleccionado. */
    public void editarEquipoSenalCPSN() {
        try {
            if (equipoEdicion != null && edicionEquipoSenalizacionBean != null) {
                edicionEquipoSenalizacionBean.cargarEquipo(equipoEdicion);
            }
        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método encargado de comprobar si se puede mostrar el botón de estudio o no. */
    public void proveedorChange() {
        if (proveedorSeleccionado != null && proveedorSeleccionado.getId() != null) {
            activarEstudio = true;
        } else {
            activarEstudio = false;
        }
    }

    /** Función que elimina el equipo de señalización. */
    public void eliminarEquipo() {
        try {
            if (equipoEdicion != null) {
                catalogoService.eliminarEquipo(equipoEdicion);
                MensajesBean.addInfoMsg(MSG_ID,
                        MensajesBean.getTextoResource("cpsn.equiposSenalizacion.CPSN.eliminarEquipo.ok"), "");
            }
        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Función encargada public void eliminarEquipo() { } /** Método encargado de obtenerlos datos del estudio de
     * equipos.
     */
    public void estudioEquipoCPSN() {
        listadoEstudio = catalogoService.estudioEquipoCPSN(proveedorSeleccionado);
    }

    /**
     * Proveedor de Servicio.
     * @return Proveedor
     */
    public Proveedor getProveedorSeleccionado() {
        return proveedorSeleccionado;
    }

    /**
     * Proveedor de Servicio.
     * @param proveedorSeleccionado Proveedor
     */
    public void setProveedorSeleccionado(Proveedor proveedorSeleccionado) {
        this.proveedorSeleccionado = proveedorSeleccionado;
    }

    /**
     * Listado de Proveedores resultado.
     * @return List<Proveedor>
     */
    public List<Proveedor> getListadoProveedores() {
        return listadoProveedores;
    }

    /**
     * Listado de Proveedores resultado.
     * @param listadoProveedores List<Proveedor>
     */
    public void setListadoProveedores(List<Proveedor> listadoProveedores) {
        this.listadoProveedores = listadoProveedores;
    }

    /**
     * Código binario del CPSN.
     * @return String
     */
    public String getCodBinario() {
        return codBinario;
    }

    /**
     * Código binario del CPSN.
     * @param codBinario String
     */
    public void setCodBinario(String codBinario) {
        this.codBinario = codBinario;
    }

    /**
     * Código decimal individual.
     * @return String
     */
    public String getDecimalIndividual() {
        return decimalIndividual;
    }

    /**
     * Código decimal individual.
     * @param decimalIndividual String
     */
    public void setDecimalIndividual(String decimalIndividual) {
        this.decimalIndividual = decimalIndividual;
    }

    /**
     * Filtro de búsqueda de equipos de señalización.
     * @return FiltroBusquedaEquipoSenal
     */
    public FiltroBusquedaEquipoSenal getFiltros() {
        return filtros;
    }

    /**
     * Filtro de búsqueda de equipos de señalización.
     * @param filtros FiltroBusquedaEquipoSenal
     */
    public void setFiltros(FiltroBusquedaEquipoSenal filtros) {
        this.filtros = filtros;
    }

    /**
     * Listado de equipos de señalización.
     * @return List<EquipoSenalCPSN>
     */
    public List<EquipoSenalCPSN> getListadoEquipos() {
        return listadoEquipos;
    }

    /**
     * Listado de equipos de señalización.
     * @param listadoEquipos List<EquipoSenalCPSN>
     */
    public void setListadoEquipos(List<EquipoSenalCPSN> listadoEquipos) {
        this.listadoEquipos = listadoEquipos;
    }

    /**
     * Bean asociado a la edicion de equipos de señalización.
     * @return EdicionEquipoSenalizacionBean
     */
    public EdicionEquipoSenalizacionBean getEdicionEquipoSenalizacionBean() {
        return edicionEquipoSenalizacionBean;
    }

    /**
     * Bean asociado a la edicion de equipos de señalización.
     * @param edicionEquipoSenalizacionBean EdicionEquipoSenalizacionBean
     */
    public void setEdicionEquipoSenalizacionBean(EdicionEquipoSenalizacionBean edicionEquipoSenalizacionBean) {
        this.edicionEquipoSenalizacionBean = edicionEquipoSenalizacionBean;
    }

    /**
     * Equipo de señalización a editar.
     * @return EquipoSenalCPSN
     */
    public EquipoSenalCPSN getEquipoEdicion() {
        return equipoEdicion;
    }

    /**
     * Equipo de señalización a editar.
     * @param equipoEdicion EquipoSenalCPSN
     */
    public void setEquipoEdicion(EquipoSenalCPSN equipoEdicion) {
        this.equipoEdicion = equipoEdicion;
    }

    /**
     * Datos del estudio de equipos de señalización.
     * @return List<EstudioEquipoCPSN>
     */
    public List<EstudioEquipoCPSN> getListadoEstudio() {
        return listadoEstudio;
    }

    /**
     * Datos del estudio de equipos de señalización.
     * @param listadoEstudio List<EstudioEquipoCPSN>
     */
    public void setListadoEstudio(List<EstudioEquipoCPSN> listadoEstudio) {
        this.listadoEstudio = listadoEstudio;
    }

    /**
     * Flag para activar el boton de estudio.
     * @return boolean
     */
    public boolean isActivarEstudio() {
        return activarEstudio;
    }

    /**
     * Flag para activar el boton de estudio.
     * @param activarEstudio boolean
     */
    public void setActivarEstudio(boolean activarEstudio) {
        this.activarEstudio = activarEstudio;
    }

    /**
     * Bean de la carga de equipos de señalización.
     * @return CargarEquiposCpsnBean
     */
    public CargarEquiposCpsnBean getCargarEquiposCpsnBean() {
        return cargarEquiposCpsnBean;
    }

    /**
     * Bean de la carga de equipos de señalización.
     * @param cargarEquiposCpsnBean CargarEquiposCpsnBean
     */
    public void setCargarEquiposCpsnBean(CargarEquiposCpsnBean cargarEquiposCpsnBean) {
        this.cargarEquiposCpsnBean = cargarEquiposCpsnBean;
    }

    /**
     * @return the emptySearch
     */
    public boolean isEmptySearch() {
        return emptySearch;
    }

}
