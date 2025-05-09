package mx.ift.sns.web.backend.ac.cpsi;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.cpsi.EquipoSenalCpsi;
import mx.ift.sns.modelo.cpsi.EstudioEquipoCpsi;
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
@ManagedBean(name = "consultarEquiposSenalizacionCpsiBean")
@ViewScoped
public class ConsultarEquiposSenalizacionCpsiBean implements Serializable {
    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarEquiposSenalizacionCpsiBean.class);

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

    /** Código binario del CPSI. */
    private String codBinario;

    /** Código decimal total. */
    private String decimalTotal;

    /** Código formato decimal. */
    private String formatoDecimal;

    /** Filtro de búsqueda de equipos de señalización. */
    private FiltroBusquedaEquipoSenal filtros;

    /** Listado de equipos de señalización. */
    private List<EquipoSenalCpsi> listadoEquipos;

    /** Equipo de señalización a editar. */
    private EquipoSenalCpsi equipoEdicion;

    /** Datos del estudio de equipos de señalización. */
    private List<EstudioEquipoCpsi> listadoEstudio;

    /** Flag para activar el boton de estudio. */
    private boolean activarEstudio;

    /** Activa el botón exportar si no hay resultados en la búsqueda. */
    private boolean emptySearch = true;

    /** Bean asociado a la edicion de equipos de señalización. */
    @ManagedProperty("#{edicionEquipoSenalizacionCpsiBean}")
    private EdicionEquipoSenalizacionCpsiBean edicionEquipoSenalizacionCpsiBean;

    /** Bean de la carga de equipos de señalización. */
    @ManagedProperty("#{cargarEquiposCpsiBean}")
    private CargarEquiposCpsiBean cargarEquiposCpsiBean;

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
            listadoEquipos = catalogoService.findAllEquiposSenalCpsi(filtros);
            emptySearch = listadoEquipos.isEmpty();
            codBinario = "";
            decimalTotal = "";
            formatoDecimal = "";
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
                    && decimalTotal != null && !decimalTotal.isEmpty()
                    && formatoDecimal != null && !formatoDecimal.isEmpty()) {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("cpsi.equiposSenalizacion.consulta.codigo.error"), "");
            } else {
                crearFiltros();
                listadoEquipos = catalogoService.findAllEquiposSenalCpsi(filtros);
                emptySearch = listadoEquipos.isEmpty();
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
        // Filtro del cps decimal
        if (decimalTotal != null && !decimalTotal.isEmpty()) {
            filtros.setDecimalTotal(decimalTotal);
        }
        // Filtro del formato decimal
        if (formatoDecimal != null && !formatoDecimal.isEmpty()) {
            filtros.setFormatoDecimal(formatoDecimal);
        }
    }

    /** Método invocado al pulsar el botón 'limpiar' sobre el buscador de equipos. */
    public void limpiarBusqueda() {
        proveedorSeleccionado = null;
        codBinario = null;
        decimalTotal = null;
        listadoEquipos.clear();
        activarEstudio = false;
        formatoDecimal = null;
        emptySearch = true;
    }

    /**
     * Genera el excel de exportación de los equipos de señalización.
     * @return excel
     * @throws Exception e
     */
    public StreamedContent getExportar() throws Exception {
        crearFiltros();

        InputStream stream = new ByteArrayInputStream(catalogoService.exportarEquiposCpsi(filtros));
        String docName = "Equipos de Señalizacion Internacional";

        docName = docName.concat(".xlsx");
        stream.close();

        LOGGER.debug("docname {}", docName);

        return new DefaultStreamedContent(stream, "application/vnd.ms-excel", docName);
    }

    /** Función que edita el equipo de señalización seleccionado. */
    public void editarEquipoSenalCpsi() {
        try {
            if (equipoEdicion != null && edicionEquipoSenalizacionCpsiBean != null) {
                edicionEquipoSenalizacionCpsiBean.cargarEquipo(equipoEdicion);
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
                catalogoService.eliminarEquipoCpsi(equipoEdicion);
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
    public void estudioEquipoCPSI() {
        listadoEstudio = catalogoService.estudioEquipoCPSI(proveedorSeleccionado);
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
     * Código binario del CPSI.
     * @return String
     */
    public String getCodBinario() {
        return codBinario;
    }

    /**
     * Código binario del CPSI.
     * @param codBinario String
     */
    public void setCodBinario(String codBinario) {
        this.codBinario = codBinario;
    }

    /**
     * Código decimal total.
     * @return String
     */
    public String getDecimalTotal() {
        return decimalTotal;
    }

    /**
     * Código decimal total.
     * @param decimalTotal String
     */
    public void setDecimalTotal(String decimalTotal) {
        this.decimalTotal = decimalTotal;
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
     * @return List<EquipoSenalCpsi>
     */
    public List<EquipoSenalCpsi> getListadoEquipos() {
        return listadoEquipos;
    }

    /**
     * Listado de equipos de señalización.
     * @param listadoEquipos List<EquipoSenalCpsi>
     */
    public void setListadoEquipos(List<EquipoSenalCpsi> listadoEquipos) {
        this.listadoEquipos = listadoEquipos;
    }

    /**
     * Bean asociado a la edicion de equipos de señalización.
     * @return EdicionEquipoSenalizacionCpsiBean
     */
    public EdicionEquipoSenalizacionCpsiBean getEdicionEquipoSenalizacionCpsiBean() {
        return edicionEquipoSenalizacionCpsiBean;
    }

    /**
     * Bean asociado a la edicion de equipos de señalización.
     * @param edicionEquipoSenalizacionCpsiBean EdicionEquipoSenalizacionCpsiBean
     */
    public void setEdicionEquipoSenalizacionCpsiBean(EdicionEquipoSenalizacionCpsiBean
            edicionEquipoSenalizacionCpsiBean) {
        this.edicionEquipoSenalizacionCpsiBean = edicionEquipoSenalizacionCpsiBean;
    }

    /**
     * Equipo de señalización a editar.
     * @return EquipoSenalCpsi
     */
    public EquipoSenalCpsi getEquipoEdicion() {
        return equipoEdicion;
    }

    /**
     * Equipo de señalización a editar.
     * @param equipoEdicion EquipoSenalCpsi
     */
    public void setEquipoEdicion(EquipoSenalCpsi equipoEdicion) {
        this.equipoEdicion = equipoEdicion;
    }

    /**
     * Datos del estudio de equipos de señalización.
     * @return List<EstudioEquipoCpsi>
     */
    public List<EstudioEquipoCpsi> getListadoEstudio() {
        return listadoEstudio;
    }

    /**
     * Datos del estudio de equipos de señalización.
     * @param listadoEstudio List<EstudioEquipoCpsi>
     */
    public void setListadoEstudio(List<EstudioEquipoCpsi> listadoEstudio) {
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
     * Código formato decimal.
     * @return String
     */
    public String getFormatoDecimal() {
        return formatoDecimal;
    }

    /**
     * Código formato decimal.
     * @param formatoDecimal String
     */
    public void setFormatoDecimal(String formatoDecimal) {
        this.formatoDecimal = formatoDecimal;
    }

    /**
     * Bean de la carga de equipos de señalización.
     * @return CargarEquiposCpsiBean
     */
    public CargarEquiposCpsiBean getCargarEquiposCpsiBean() {
        return cargarEquiposCpsiBean;
    }

    /**
     * Bean de la carga de equipos de señalización.
     * @param cargarEquiposCpsiBean CargarEquiposCpsiBean
     */
    public void setCargarEquiposCpsiBean(CargarEquiposCpsiBean cargarEquiposCpsiBean) {
        this.cargarEquiposCpsiBean = cargarEquiposCpsiBean;
    }

    /**
     * Activa el botón exportar si no hay resultados en la búsqueda.
     * @return the emptySearch
     */
    public boolean isEmptySearch() {
        return emptySearch;
    }

}
