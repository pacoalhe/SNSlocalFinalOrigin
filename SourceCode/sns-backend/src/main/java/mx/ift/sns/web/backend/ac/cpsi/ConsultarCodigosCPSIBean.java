package mx.ift.sns.web.backend.ac.cpsi;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.cpsi.EstatusCPSI;
import mx.ift.sns.modelo.cpsi.InfoCatCpsi;
import mx.ift.sns.modelo.cpsi.VEstudioCPSI;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCodigosCPSI;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean de la consulta de codigos Internacionales.
 * @author X50880SA
 */

@ManagedBean(name = "consultarCodigosCPSIBean")
@ViewScoped
public class ConsultarCodigosCPSIBean implements Serializable {
    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarCodigosCPSIBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultarCodigosCPSI";

    /** Servicio de Catalogo. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade catalogoService;

    /** Proveedor de Servicio. */
    private Proveedor proveedorSeleccionado;

    /** Listado de Proveedores resultado. */
    private List<Proveedor> listadoProveedores;

    /** Estatus. */
    private EstatusCPSI estatusCPSISeleccionado;

    /** Listado de Estatus. */
    private List<EstatusCPSI> listadoEstatusCPSI;

    /** Filtro de búsqueda de codigos CPS Internacionales. */
    private FiltroBusquedaCodigosCPSI filtros;

    /** Listado de códigos Internacionales. */
    private List<InfoCatCpsi> listadoCodigosCPSI;

    /** Listado de Códigos CPSI seleccionados. */
    private List<InfoCatCpsi> codigosCPSISeleccionados;

    /** Datos del estudio de asignación de códigos CPS internacionales. */
    private VEstudioCPSI listadoEstudio;

    /** Activa el botón exportar si no hay resultados en la búsqueda. */
    private boolean emptySearch = true;

    /**
     * Método que inicializa la consulta de codigos CPS internacionales.
     */
    @PostConstruct
    public void init() {
        try {
            // Listado de proveedores y estatus
            listadoProveedores = catalogoService.findAllProveedores();
            listadoEstatusCPSI = catalogoService.findAllEstatusCPSI();

            // Carga inicial de todos los equipos
            filtros = new FiltroBusquedaCodigosCPSI();
            // listadoCodigosCPSI = catalogoService.findCodigosCPSI(filtros);

            codigosCPSISeleccionados = new ArrayList<InfoCatCpsi>();
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar el botón 'buscar'. */
    public void realizarBusqueda() {
        try {
            crearFiltros();
            listadoCodigosCPSI = catalogoService.findAllInfoCatCPSI(filtros);
            emptySearch = listadoCodigosCPSI.isEmpty();
            codigosCPSISeleccionados.clear();
        } catch (Exception ex) {
            MensajesBean.addInfoMsg(MSG_ID, MensajesBean.getTextoResource("errorGenerico"), "");
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

        // Filtro del estatus
        if (estatusCPSISeleccionado != null) {
            filtros.setEstatusCPSI(estatusCPSISeleccionado);
        }
    }

    /** Método invocado al pulsar el botón 'limpiar' sobre el buscador de codigos CPS internacionales. */
    public void limpiarBusqueda() {
        proveedorSeleccionado = null;
        estatusCPSISeleccionado = null;
        codigosCPSISeleccionados.clear();
        listadoCodigosCPSI.clear();
        emptySearch = true;
    }

    /**
     * Genera el excel de exportación de los codigos CPS internacionales.
     * @return excel
     * @throws Exception e
     */
    public StreamedContent getExportar() throws Exception {
        crearFiltros();
        InputStream stream = new ByteArrayInputStream(catalogoService.exportarCodigosCPSI(filtros));
        String docName = "Códigos CPS Internacionales";

        docName = docName.concat(".xlsx");
        stream.close();

        LOGGER.debug("docname {}", docName);

        return new DefaultStreamedContent(stream, "application/vnd.ms-excel", docName);
    }

    /**
     * Genera el excel de exportación de los datos del estudio mostrados por pantalla.
     * @return StreamedContent
     * @throws Exception Lanza una excepcion
     */
    public StreamedContent getExportarEstudio() throws Exception {

        InputStream stream = new ByteArrayInputStream(catalogoService.exportarEstudioCPSI(listadoEstudio));
        String docName = "Estudio CPS Internacionales";

        docName = docName.concat(".xlsx");
        stream.close();

        LOGGER.debug("docname {}", docName);

        return new DefaultStreamedContent(stream, "application/vnd.ms-excel", docName);
    }

    /**
     * Método que libera los codigos seleccionados en caso de ser correcta la selección de los mismos.
     */
    public void liberar() {

        try {
            if (codigosCPSISeleccionados.isEmpty() || codigosCPSISeleccionados.isEmpty()) {
                MensajesBean.addWarningMsg(MSG_ID, MensajesBean.getTextoResource("cpsi.codigos.CPSI.seleccion.error"),
                        "");
            } else {
                String error = catalogoService.liberarCodigosCPSI(codigosCPSISeleccionados);
                if (error != null) {
                    MensajesBean.addErrorMsg(MSG_ID, MensajesBean.getTextoResource(error), "");
                } else {
                    MensajesBean.addInfoMsg(MSG_ID, MensajesBean.getTextoResource("cpsi.codigos.CPSI.liberar.ok"), "");
                    codigosCPSISeleccionados.clear();
                    realizarBusqueda();
                }
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.debug("Error inesperado", e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, MensajesBean.getTextoResource("errorGenerico"), "");
        }
    }

    /**
     * Método que reserva los codigos seleccionados en caso de ser correcta la selección de los mismos.
     */
    public void reservar() {
        try {
            if (codigosCPSISeleccionados.isEmpty() || codigosCPSISeleccionados.isEmpty()) {
                MensajesBean.addWarningMsg(MSG_ID, MensajesBean.getTextoResource("cpsi.codigos.CPSI.seleccion.error"),
                        "");
            } else {
                String error = catalogoService.reservarCodigosCPSI(codigosCPSISeleccionados);
                if (error != null) {
                    MensajesBean.addErrorMsg(MSG_ID, MensajesBean.getTextoResource(error), "");
                } else {
                    MensajesBean.addInfoMsg(MSG_ID, MensajesBean.getTextoResource("cpsi.codigos.CPSI.reservar.ok"), "");
                    codigosCPSISeleccionados.clear();
                    realizarBusqueda();
                }
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.debug("Error inesperado", e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, MensajesBean.getTextoResource("errorGenerico"), "");
        }
    }

    /**
     * Método encargado de obtenerlos datos del estudio de utilización de códigos CPS Internacionales.
     */
    public void estudioCPSI() {
        listadoEstudio = catalogoService.estudioCPSI(proveedorSeleccionado);
    }

    /**
     * Método encargado de obtenerlos datos del historico de utilización de códigos CPS Internacionales.
     * @param proveedor Pst
     */
    public void historicoCPSI(Proveedor proveedor) {
        listadoEstudio = catalogoService.estudioCPSI(proveedorSeleccionado);
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
     * Estatus.
     * @return EstatusCPSI
     */
    public EstatusCPSI getEstatusCPSISeleccionado() {
        return estatusCPSISeleccionado;
    }

    /**
     * Estatus.
     * @param estatusCPSISeleccionado EstatusCPSI
     */
    public void setEstatusCPSISeleccionado(EstatusCPSI estatusCPSISeleccionado) {
        this.estatusCPSISeleccionado = estatusCPSISeleccionado;
    }

    /**
     * Listado de Estatus.
     * @return List<EstatusCPSI>
     */
    public List<EstatusCPSI> getListadoEstatusCPSI() {
        return listadoEstatusCPSI;
    }

    /**
     * Listado de Estatus.
     * @param listadoEstatusCPSI List<EstatusCPSI>
     */
    public void setListadoEstatusCPSI(List<EstatusCPSI> listadoEstatusCPSI) {
        this.listadoEstatusCPSI = listadoEstatusCPSI;
    }

    /**
     * Listado de códigos Internacionales.
     * @return List<InfoCatCpsi>
     */
    public List<InfoCatCpsi> getListadoCodigosCPSI() {
        return listadoCodigosCPSI;
    }

    /**
     * Listado de códigos Internacionales.
     * @param listadoCodigosCPSI List<InfoCatCpsi>
     */
    public void setListadoCodigosCPSI(List<InfoCatCpsi> listadoCodigosCPSI) {
        this.listadoCodigosCPSI = listadoCodigosCPSI;
    }

    /**
     * Listado de Códigos CPSI seleccionados.
     * @return List<InfoCatCpsi>
     */
    public List<InfoCatCpsi> getCodigosCPSISeleccionados() {
        return codigosCPSISeleccionados;
    }

    /**
     * Listado de Códigos CPSI seleccionados.
     * @param codigosCPSISeleccionados List<InfoCatCpsi>
     */
    public void setCodigosCPSISeleccionados(List<InfoCatCpsi> codigosCPSISeleccionados) {
        this.codigosCPSISeleccionados = codigosCPSISeleccionados;
    }

    /**
     * Datos del estudio de asignación de códigos CPS internacionales.
     * @return VEstudioCPSI
     */
    public VEstudioCPSI getListadoEstudio() {
        return listadoEstudio;
    }

    /**
     * Datos del estudio de asignación de códigos CPS internacionales.
     * @param listadoEstudio VEstudioCPSI
     */
    public void setListadoEstudio(VEstudioCPSI listadoEstudio) {
        this.listadoEstudio = listadoEstudio;
    }

    /**
     * Activa el botón exportar si no hay resultados en la búsqueda.
     * @return the emptySearch
     */
    public boolean isEmptySearch() {
        return emptySearch;
    }

}
