package mx.ift.sns.web.backend.ac.cpsn;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.cpsn.CodigoCPSN;
import mx.ift.sns.modelo.cpsn.EstatusCPSN;
import mx.ift.sns.modelo.cpsn.TipoBloqueCPSN;
import mx.ift.sns.modelo.cpsn.VEstudioCPSN;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCodigosCPSN;
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
 * Bean de la consulta de codigos nacionales.
 */
@ManagedBean(name = "consultarCodigosCPSNBean")
@ViewScoped
public class ConsultarCodigosCPSNBean implements Serializable {
    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarCodigosCPSNBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultarCodigosCPSN";

    /** Servicio de Catalogo. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade catalogoService;

    /** Proveedor de Servicio. */
    private Proveedor proveedorSeleccionado;

    /** Listado de Proveedores resultado. */
    private List<Proveedor> listadoProveedores;

    /** Tipo bloque. */
    private TipoBloqueCPSN tipoBloqueCPSNSeleccionado;

    /** Listado de Tipos de bloque. */
    private List<TipoBloqueCPSN> listadoTiposBloqueCPSN;

    /** Estatus. */
    private EstatusCPSN estatusCPSNSeleccionado;

    /** Listado de Estatus. */
    private List<EstatusCPSN> listadoEstatusCPSN;

    /** Filtro de búsqueda de codigos CPS Nacionales. */
    private FiltroBusquedaCodigosCPSN filtros;

    /** Listado de códigos nacionales. */
    private List<CodigoCPSN> listadoCodigosCPSN;

    /** Codigo CPS Nacional a editar. */
    private CodigoCPSN codigoCPSNEdicion;

    /** Listado de Códigos CPSN seleccionados. */
    private List<CodigoCPSN> codigosCPSNSeleccionados;

    /** Número de registros a generar en la desagrupación. */
    private int numCodigosAGenerar;

    /** Flag que indica si es una desagrupación. */
    private boolean desagrupacion;

    /** Flag que indica si es una agrupación. */
    private boolean agrupacion;

    /** Flag para activar o desactivar los botones de agrupacion. */
    private boolean activarBotonAgr;

    /** Flag para activar o desactivar los botones de desagrupacion. */
    private boolean activarBotonDesagr;

    /** Tipo de bloque resultado de la agrupación. */
    private String tipoBloqueAgrupacion;

    /** Activa el botón exportar si no hay resultados en la búsqueda. */
    private boolean emptySearch = true;

    /** Datos del estudio de asignación de códigos CPS Nacionales. */
    private List<VEstudioCPSN> listadoEstudio;

    /**
     * Método que inicializa la consulta de codigos CPS Nacionalesn.
     */
    @PostConstruct
    public void init() {
        try {
            // Listado de proveedores de servicio
            listadoProveedores = catalogoService.findAllProveedores();
            proveedorSeleccionado = new Proveedor();

            listadoTiposBloqueCPSN = catalogoService.findAllTiposBloqueCPSN();
            tipoBloqueCPSNSeleccionado = new TipoBloqueCPSN();

            listadoEstatusCPSN = catalogoService.findAllEstatusCPSN();
            estatusCPSNSeleccionado = new EstatusCPSN();

            // Carga inicial de todos los equipos
            filtros = new FiltroBusquedaCodigosCPSN();
            // listadoCodigosCPSN = catalogoService.findCodigosCPSN(filtros);

            codigosCPSNSeleccionados = new ArrayList<CodigoCPSN>();

            numCodigosAGenerar = 0;
            desagrupacion = false;
            agrupacion = false;
            activarBotonAgr = false;
            activarBotonDesagr = false;
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error inesperado: " + e.getMessage());
            }
            MensajesBean.addInfoMsg(MSG_ID, MensajesBean.getTextoResource("errorGenerico"), "");
        }
    }

    /** Método invocado al pulsar el botón 'buscar'. */
    public void realizarBusqueda() {
        try {
            crearFiltros();
            listadoCodigosCPSN = catalogoService.findCodigosCPSN(filtros);
            emptySearch = listadoCodigosCPSN.isEmpty();
            codigosCPSNSeleccionados = new ArrayList<CodigoCPSN>();
            numCodigosAGenerar = 0;
            desagrupacion = false;
            agrupacion = false;
            activarBotonAgr = false;
            activarBotonDesagr = false;
        } catch (Exception ex) {
            MensajesBean.addInfoMsg(MSG_ID, MensajesBean.getTextoResource("errorGenerico"), "");
        }
    }

    /** Creación de los filtros de búsqueda. */
    private void crearFiltros() {
        // Resetamos los filtros
        filtros.clear();

        // Filtro del proveedor
        if (proveedorSeleccionado != null && proveedorSeleccionado.getId() != null) {
            filtros.setProveedor(proveedorSeleccionado);
        }
        // Filtro de tipo de bloque
        if (tipoBloqueCPSNSeleccionado != null && tipoBloqueCPSNSeleccionado.getId() != null) {
            filtros.setTipoBloqueCPSN(tipoBloqueCPSNSeleccionado);
        }
        // Filtro del estatus
        if (estatusCPSNSeleccionado != null && estatusCPSNSeleccionado.getId() != null) {
            filtros.setEstatusCPSN(estatusCPSNSeleccionado);
        }
    }

    /** Método invocado al pulsar el botón 'limpiar' sobre el buscador de codigos CPS Nacionales. */
    public void limpiarBusqueda() {
        proveedorSeleccionado = null;
        tipoBloqueCPSNSeleccionado = null;
        estatusCPSNSeleccionado = null;
        codigosCPSNSeleccionados = new ArrayList<CodigoCPSN>();
        listadoCodigosCPSN.clear();
        numCodigosAGenerar = 0;
        desagrupacion = false;
        agrupacion = false;
        activarBotonAgr = false;
        activarBotonDesagr = false;
        emptySearch = true;
    }

    /**
     * Genera el excel de exportación de los codigos CPS Nacionales.
     * @return excel
     * @throws Exception e
     */
    public StreamedContent getExportar() throws Exception {
        crearFiltros();
        InputStream stream = new ByteArrayInputStream(catalogoService.exportarCodigosCPSN(filtros));
        String docName = "Códigos CPS Nacionales";

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
            if (codigosCPSNSeleccionados == null || codigosCPSNSeleccionados.isEmpty()) {
                MensajesBean.addWarningMsg(MSG_ID, MensajesBean.getTextoResource("cpsn.codigos.CPSN.seleccion.error"),
                        "");
            } else {
                String error = catalogoService.liberarCodigosCPSN(codigosCPSNSeleccionados);
                if (error != null) {
                    MensajesBean.addErrorMsg(MSG_ID, MensajesBean.getTextoResource(error), "");
                } else {
                    MensajesBean.addInfoMsg(MSG_ID, MensajesBean.getTextoResource("cpsn.codigos.CPSN.liberar.ok"), "");
                    codigosCPSNSeleccionados.clear();
                    realizarBusqueda();
                }
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.debug("Error inesperado", e.getMessage());
            MensajesBean.addInfoMsg(MSG_ID, MensajesBean.getTextoResource("errorGenerico"), "");
        }
    }

    /**
     * Método que reserva los codigos seleccionados en caso de ser correcta la selección de los mismos.
     */
    public void reservar() {
        try {
            if (codigosCPSNSeleccionados == null || codigosCPSNSeleccionados.isEmpty()) {
                MensajesBean.addWarningMsg(MSG_ID, MensajesBean.getTextoResource("cpsn.codigos.CPSN.seleccion.error"),
                        "");
            } else {
                String error = catalogoService.reservarCodigosCPSN(codigosCPSNSeleccionados);
                if (error != null) {
                    MensajesBean.addErrorMsg(MSG_ID, MensajesBean.getTextoResource(error), "");
                } else {
                    MensajesBean.addInfoMsg(MSG_ID, MensajesBean.getTextoResource("cpsn.codigos.CPSN.reservar.ok"), "");
                    codigosCPSNSeleccionados.clear();
                    realizarBusqueda();
                }
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.debug("Error inesperado", e.getMessage());
            MensajesBean.addInfoMsg(MSG_ID, MensajesBean.getTextoResource("errorGenerico"), "");
        }
    }

    /**
     * Método que comprueba si el registro seleccionado se puede desagrupar.
     */
    public void procesarDesagrupacion() {
        desagrupacion = true;
        numCodigosAGenerar = codigosCPSNSeleccionados.get(0).getNumRegistrosAGenerar();
        tipoBloqueAgrupacion = null;
    }

    /**
     * Método que activa el dialogo para agrupar.
     */
    public void procesarAgrupacion() {
        agrupacion = true;
        tipoBloqueAgrupacion = codigosCPSNSeleccionados.get(0).getTipoBloqueAgrupacion();
        numCodigosAGenerar = 0;
    }

    /** Método que se encarga de desagrupar el código seleccionado. */
    public void desagrupar() {
        String error = catalogoService.desagruparCodigoCPSN(codigosCPSNSeleccionados);
        if (error != null && !error.isEmpty()) {
            MensajesBean.addErrorMsg(MSG_ID, MensajesBean.getTextoResource(error), "");
        } else {
            numCodigosAGenerar = 0;
            desagrupacion = false;
            MensajesBean.addInfoMsg(MSG_ID, MensajesBean.getTextoResource("cpsn.codigos.CPSN.seleccion.desagrupar.ok"),
                    "");
            codigosCPSNSeleccionados.clear();
            realizarBusqueda();
        }
    }

    /** Método que se encarga de agrupar el código seleccionado. */
    public void agrupar() {
        String error = catalogoService.agruparCodigoCPSN(codigosCPSNSeleccionados);
        if (error != null && !error.isEmpty()) {
            MensajesBean.addErrorMsg(MSG_ID, MensajesBean.getTextoResource(error), "");
        } else {
            tipoBloqueAgrupacion = null;
            agrupacion = false;
            MensajesBean.addInfoMsg(MSG_ID, MensajesBean.getTextoResource("cpsn.codigos.CPSN.seleccion.agrupar.ok"),
                    "");
            codigosCPSNSeleccionados.clear();
            realizarBusqueda();
        }
    }

    /** Función encargada de actualizar el mensaje a mostrar para la agrupaciçon / desagrupación. */
    public void seleccionarCodigo() {
        if (codigosCPSNSeleccionados.size() != 1) {
            activarBotonAgr = false;
            activarBotonDesagr = false;
        } else {
            if (TipoBloqueCPSN.INDIVIDUAL.equals(codigosCPSNSeleccionados.get(0).getTipoBloqueCPSN().getId())) {
                activarBotonAgr = true;
                activarBotonDesagr = false;
            } else if (TipoBloqueCPSN.BLOQUE_2048.equals(codigosCPSNSeleccionados.get(0).getTipoBloqueCPSN().getId())) {
                activarBotonAgr = false;
                activarBotonDesagr = true;
            } else {
                activarBotonAgr = true;
                activarBotonDesagr = true;
            }
        }
    }

    /** Función encargada de actualizar el mensaje a mostrar para la agrupaciçon / desagrupación. */
    public void deSeleccionarCodigo() {
        if (codigosCPSNSeleccionados.size() == 1) {
            if (TipoBloqueCPSN.INDIVIDUAL.equals(codigosCPSNSeleccionados.get(0).getTipoBloqueCPSN().getId())) {
                activarBotonAgr = true;
                activarBotonDesagr = false;
            } else if (TipoBloqueCPSN.BLOQUE_2048.equals(codigosCPSNSeleccionados.get(0).getTipoBloqueCPSN().getId())) {
                activarBotonAgr = false;
                activarBotonDesagr = true;
            } else {
                activarBotonAgr = true;
                activarBotonDesagr = true;
            }
        } else {
            activarBotonAgr = false;
            activarBotonDesagr = false;
        }

    }

    /**
     * Método encargado de obtenerlos datos del estudio de utilización de códigos CPS Nacionales.
     */
    public void estudioCPSN() {
        listadoEstudio = catalogoService.estudioCPSN();
    }

    /**
     * Método encargado de obtenerlos datos del historico de utilización de códigos CPS Nacionales.
     */
    public void historicoCPSN() {
        listadoEstudio = catalogoService.estudioCPSN();
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
     * Tipo bloque.
     * @return TipoBloqueCPSN
     */
    public TipoBloqueCPSN getTipoBloqueCPSNSeleccionado() {
        return tipoBloqueCPSNSeleccionado;
    }

    /**
     * Tipo bloque.
     * @param tipoBloqueCPSNSeleccionado TipoBloqueCPSN
     */
    public void setTipoBloqueCPSNSeleccionado(TipoBloqueCPSN tipoBloqueCPSNSeleccionado) {
        this.tipoBloqueCPSNSeleccionado = tipoBloqueCPSNSeleccionado;
    }

    /**
     * Listado de Tipos de bloque.
     * @return List<TipoBloqueCPSN>
     */
    public List<TipoBloqueCPSN> getListadoTiposBloqueCPSN() {
        return listadoTiposBloqueCPSN;
    }

    /**
     * Listado de Tipos de bloque.
     * @param listadoTiposBloqueCPSN List<TipoBloqueCPSN>
     */
    public void setListadoTiposBloqueCPSN(List<TipoBloqueCPSN> listadoTiposBloqueCPSN) {
        this.listadoTiposBloqueCPSN = listadoTiposBloqueCPSN;
    }

    /**
     * Estatus.
     * @return EstatusCPSN
     */
    public EstatusCPSN getEstatusCPSNSeleccionado() {
        return estatusCPSNSeleccionado;
    }

    /**
     * Estatus.
     * @param estatusCPSNSeleccionado EstatusCPSN
     */
    public void setEstatusCPSNSeleccionado(EstatusCPSN estatusCPSNSeleccionado) {
        this.estatusCPSNSeleccionado = estatusCPSNSeleccionado;
    }

    /**
     * Listado de Estatus.
     * @return List<EstatusCPSN>
     */
    public List<EstatusCPSN> getListadoEstatusCPSN() {
        return listadoEstatusCPSN;
    }

    /**
     * Listado de Estatus.
     * @param listadoEstatusCPSN List<EstatusCPSN>
     */
    public void setListadoEstatusCPSN(List<EstatusCPSN> listadoEstatusCPSN) {
        this.listadoEstatusCPSN = listadoEstatusCPSN;
    }

    /**
     * Listado de códigos nacionales.
     * @return List<CodigoCPSN>
     */
    public List<CodigoCPSN> getListadoCodigosCPSN() {
        return listadoCodigosCPSN;
    }

    /**
     * Listado de códigos nacionales.
     * @param listadoCodigosCPSN List<CodigoCPSN>
     */
    public void setListadoCodigosCPSN(List<CodigoCPSN> listadoCodigosCPSN) {
        this.listadoCodigosCPSN = listadoCodigosCPSN;
    }

    /**
     * Codigo CPS Nacional a editar.
     * @return CodigoCPSN
     */
    public CodigoCPSN getCodigoCPSNEdicion() {
        return codigoCPSNEdicion;
    }

    /**
     * Codigo CPS Nacional a editar.
     * @param codigoCPSNEdicion CodigoCPSN
     */
    public void setCodigoCPSNEdicion(CodigoCPSN codigoCPSNEdicion) {
        this.codigoCPSNEdicion = codigoCPSNEdicion;
    }

    /**
     * Listado de Códigos CPSN seleccionados.
     * @return List<CodigoCPSN>
     */
    public List<CodigoCPSN> getCodigosCPSNSeleccionados() {
        return codigosCPSNSeleccionados;
    }

    /**
     * Listado de Códigos CPSN seleccionados.
     * @param codigosCPSNSeleccionados List<CodigoCPSN>
     */
    public void setCodigosCPSNSeleccionados(List<CodigoCPSN> codigosCPSNSeleccionados) {
        this.codigosCPSNSeleccionados = codigosCPSNSeleccionados;
    }

    /**
     * Flag que indica si es una desagrupación.
     * @return boolean
     */
    public boolean isDesagrupacion() {
        return desagrupacion;
    }

    /**
     * Flag que indica si es una desagrupación.
     * @param desagrupacion boolean
     */
    public void setDesagrupacion(boolean desagrupacion) {
        this.desagrupacion = desagrupacion;
    }

    /**
     * Flag que indica si es una agrupación.
     * @return boolean
     */
    public boolean isAgrupacion() {
        return agrupacion;
    }

    /**
     * Flag que indica si es una agrupación.
     * @param agrupacion boolean
     */
    public void setAgrupacion(boolean agrupacion) {
        this.agrupacion = agrupacion;
    }

    /**
     * Número de registros a generar en la desagrupación.
     * @return int
     */
    public int getNumCodigosAGenerar() {
        return numCodigosAGenerar;
    }

    /**
     * Número de registros a generar en la desagrupación.
     * @param numCodigosAGenerar int
     */
    public void setNumCodigosAGenerar(int numCodigosAGenerar) {
        this.numCodigosAGenerar = numCodigosAGenerar;
    }

    /**
     * Flag para activar o desactivar los botones de agrupacion.
     * @return boolean
     */
    public boolean isActivarBotonAgr() {
        return activarBotonAgr;
    }

    /**
     * Flag para activar o desactivar los botones de agrupacion.
     * @param activarBotonAgr boolean
     */
    public void setActivarBotonAgr(boolean activarBotonAgr) {
        this.activarBotonAgr = activarBotonAgr;
    }

    /**
     * Flag para activar o desactivar los botones de desagrupacion.
     * @return boolean
     */
    public boolean isActivarBotonDesagr() {
        return activarBotonDesagr;
    }

    /**
     * Flag para activar o desactivar los botones de desagrupacion.
     * @param activarBotonDesagr boolean
     */
    public void setActivarBotonDesagr(boolean activarBotonDesagr) {
        this.activarBotonDesagr = activarBotonDesagr;
    }

    /**
     * Tipo de bloque resultado de la agrupación.
     * @return String
     */
    public String getTipoBloqueAgrupacion() {
        return tipoBloqueAgrupacion;
    }

    /**
     * Tipo de bloque resultado de la agrupación.
     * @param tipoBloqueAgrupacion String
     */
    public void setTipoBloqueAgrupacion(String tipoBloqueAgrupacion) {
        this.tipoBloqueAgrupacion = tipoBloqueAgrupacion;
    }

    /**
     * Datos del estudio de asignación de códigos CPS Nacionales.
     * @return List<VEstudioCPSN>
     */
    public List<VEstudioCPSN> getListadoEstudio() {
        return listadoEstudio;
    }

    /**
     * Datos del estudio de asignación de códigos CPS Nacionales.
     * @param listadoEstudio List<VEstudioCPSN>
     */
    public void setListadoEstudio(List<VEstudioCPSN> listadoEstudio) {
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
