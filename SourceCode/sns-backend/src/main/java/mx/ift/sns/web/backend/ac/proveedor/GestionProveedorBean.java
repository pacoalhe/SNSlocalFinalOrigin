package mx.ift.sns.web.backend.ac.proveedor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.pst.Contacto;
import mx.ift.sns.modelo.pst.EstadoConvenio;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.ProveedorConvenio;
import mx.ift.sns.modelo.pst.TipoContacto;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.modelo.pst.TipoServicio;
import mx.ift.sns.modelo.usu.Usuario;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.util.ErroresUtil;

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Gestiona la edición y creación de proveedores de telecomunicaciones. */
@ManagedBean(name = "gestionProveedorBean")
@ViewScoped
public class GestionProveedorBean implements Serializable {
    /** Serializar. **/
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(GestionProveedorBean.class);

    /** Estado de ALTA. */
    public static final int ALTA = 1;

    /** Estado de EDICION. */
    public static final int EDICION = 2;

    /** Estado de BAJA. */
    public static final int BAJA = 0;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_Proveedores";

    /** Servicio de Numeración Geográfica. */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    /** Proveedor. */
    private Proveedor proveedor;

    /** Listado de Tipos de Servicio. */
    private List<TipoServicio> listadoTiposServicio;

    /** Listado de Tipos de PST. */
    private List<TipoProveedor> listadoTipoPst;

    /** Listado de los Tipos de Red. */
    private List<TipoRed> listadoTiposRed;

    /** Listado de Estados. */
    private List<Estado> listadoEstados;

    /** Contacto. */
    private Contacto contacto;

    /** Convenio. */
    private ProveedorConvenio proveedorConvenio;

    /** Listado de Proveedores. */
    private List<Proveedor> listaProveedores;

    /** Listado de Proveedores selecionables para la creación de un convenio. */
    private List<Proveedor> listaProveedoresConvenio;

    /** Flag para renderizar la creación de convenios. */
    private boolean mostrarConvenios;

    /** Flag para renderizar la creación de contactos. */
    private boolean mostrarContactos;

    /** Listado de Tipos de Contacto. */
    private List<TipoContacto> listaTiposContacto;

    /** Flag que indica si es una edición o creación. */
    private boolean edicion;

    /** Listado de estatus del proveedor. */
    private List<Estatus> listadoEstatus;

    /** Flag que habilita los campos asociados Listado de estatus del proveedor. */
    private boolean habilitarComercializadora;

    /** Flag que habilita los campos asociados al tipo de proveedor. */
    private boolean habilitarConcComerc;

    /** Entero que almacena si el IDO existe en otro proveedor. */
    private int existeIdo;

    /** Entero que almacena si el IDA existe en otro proveedor. */
    private int existeIda;

    /** Entero que almacena si el ABC existe en otro proveedor. */
    private int existeAbc;

    /** Entero que almacena si el BCD existe en otro proveedor. */
    private int existeBcd;

    /** Booleano que indica si el guardado requiere o no de confirmación por parte del analista. */
    private boolean requiereConfirmacion;

    /** Modo de inserción de datos de los convenios. */
    private int modoConvenio;

    /** Modo de inserción de datos de los contactos. */
    private int modoContacto;

    /** Convenio temporal guardado para los casos de ediciones sin guardar. */
    private ProveedorConvenio convenioTemporal;

    /** Contacto temporal para los casos de ediciones de contactos sin guardar. */
    private Contacto contactoTemporal;

    /** Flag que indica si es una edición sin usuario. */
    private boolean edicionUsuario;

    /** Tipo Proveedor inicial. */
    private String tipoProveedorInicial;

    /** Tipo Red inicial. */
    private String tipoRedInicial;

    /** Tipo Red Original inicial. */
    private String tipoRedOriginalInicial;

    /** Listado de los Tipos de Red del Convenio. */
    private List<TipoRed> listadoTiposRedConvenio;

    /** Listado de los Tipos de Red Original. */
    private List<TipoRed> listadoTiposRedOriginal;

    /** Flag para renderizar el listado de tipos de red del convenio. */
    private boolean mostrarTipoRedConvenio;

    /**
     * Password del proveedor guardada en BBDD en caso de editar el proveedor, utilizada para validar si se ha
     * modificado la misma en la edición.
     */
    private String passBBDD;

    /** Flag que indica si hay que encriptar o no la password. */
    private boolean encriptarPass;

    /** Constructor vacio. */
    public GestionProveedorBean() {
    }

    /**
     * Inicializa el Bean.
     * @throws Exception en caso de error.
     */
    @PostConstruct
    public void init() throws Exception {
        proveedor = new Proveedor();
        proveedor.setUsuario(new Usuario());
        contacto = new Contacto();
        proveedorConvenio = new ProveedorConvenio();
        convenioTemporal = null;
        contactoTemporal = null;
        mostrarConvenios = false;
        mostrarContactos = false;
        edicion = false;
        //edicionUsuario = true;
        habilitarComercializadora = false;
        habilitarComercializadora = false;
        existeIdo = 0;
        existeIda = 0;
        existeAbc = 0;
        existeBcd = 0;
        requiereConfirmacion = false;
        modoConvenio = ALTA;
        modoContacto = ALTA;
        passBBDD = null;
        encriptarPass = true;

        // LIstado de tipos de servicio
        listadoTiposServicio = ngService.findAllTiposServicio();

        // LIstado de Tipos de PST
        listadoTipoPst = ngService.findAllTiposProveedor();

        // Listado de Tipos de Red
        listadoTiposRed = ngService.findAllTiposRedValidos();
        listadoEstados = ngService.findAllEstados();
        listadoTiposRedConvenio = ngService.findAllTiposRed();
        listadoTiposRedOriginal = ngService.findAllTiposRedValidos();

        listadoEstatus = ngService.findAllEstatus();
    }

    /**
     * Método invovado al pulsar sobre 'Modificar Proveedor' o 'Modificar Representante' en los fieldset de edición de
     * proveedores o representantes. Carga la información del proveedor en el formulario de edición.
     * @param pPstEdicion Proveedor cargado en el componente de información/edición de proveedores.
     */
    public void cargarProveedor(Proveedor pPstEdicion) {
        try {
            // Limpiamos el formulario.
            this.limpiarDatos();

            // Cargamos el Nuevo Proveedor.
            this.proveedor = pPstEdicion;
            if (proveedor.getUsuario() == null || proveedor.getUsuario().getId() == null) {
                proveedor.setUsuario(new Usuario());
                
            } 
            
            edicion = true;
            modoConvenio = (ALTA);

            tipoProveedorInicial = proveedor.getTipoProveedor().getCdg();
            tipoRedInicial = proveedor.getTipoRed().getCdg();
            tipoRedOriginalInicial = proveedor.getTipoRedOriginal().getCdg();

            this.existeIdo();
            this.existeIda();
            this.existeAbc();
            this.existeBcd();
            this.compruebaConfirmacion();
            this.tipoPstChange();
            this.tiposRedValidos();

            passBBDD = (proveedor.getUsuario().getContrasenna());

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Resetea las variables de edición de proveedor.
     * @throws Exception excepcion
     */
    public void limpiarDatos() throws Exception {
        proveedor = new Proveedor();
        proveedor.setUsuario(new Usuario());
        contacto = new Contacto();
        proveedorConvenio = new ProveedorConvenio();
        convenioTemporal = null;
        contactoTemporal = null;
        mostrarConvenios = false;
        mostrarContactos = false;
        habilitarComercializadora = false;
        habilitarComercializadora = false;
        existeIdo = 0;
        existeIda = 0;
        existeAbc = 0;
        existeBcd = 0;
        edicion = false;
        //edicionUsuario = true;
        requiereConfirmacion = false;
        modoConvenio = ALTA;
        modoContacto = ALTA;
        tipoProveedorInicial = null;
        tipoRedInicial = null;
        tipoRedOriginalInicial = null;
        passBBDD = null;
        encriptarPass = true;

        // LIstado de Tipos de PST
        listadoTipoPst = ngService.findAllTiposProveedor();

        // Listado de Tipos de Red
        listadoTiposRed = ngService.findAllTiposRedValidos();
    }

    /** Se encarga de añadir el convenio al pst. */
    public void crearConvenio() {
        limpiarConvenio();
        mostrarConvenios = true;
        mostrarContactos = false;
        modoConvenio = ALTA;

        EstadoConvenio estatus = new EstadoConvenio();
        estatus.setCdg(EstadoConvenio.VIGENTE);
        proveedorConvenio.setEstatus(estatus);
        convenioTemporal = new ProveedorConvenio();
        convenioTemporal.setEstatus(estatus);

        mostrarTipoRedConvenio = (TipoRed.AMBAS.equals(proveedor.getTipoRed().getCdg())) ? true : false;

        cargaListaPstConvenios();
    }

    /** Carga el listao de PST asignables para los convenios. */
    private void cargaListaPstConvenios() {
        try {
            if (!mostrarTipoRedConvenio || (mostrarTipoRedConvenio && convenioTemporal.getTipoRed() == null)) {
                listaProveedoresConvenio = ngService.findAllProveedoresByTRed(proveedor.getTipoRed());
            } else {
                listaProveedoresConvenio = ngService.findAllProveedoresByTRed(convenioTemporal.getTipoRed());
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método encargado de actualizar el listado de proveedores seleccionables para la creación del convenio.
     */
    public void tipoRedConvenioChange() {
        try {
            if (convenioTemporal.getTipoRed() == null) {
                if (listaProveedoresConvenio == null) {
                    listaProveedoresConvenio = null;
                } else {
                    listaProveedoresConvenio.clear();
                }
            } else {
                listaProveedoresConvenio = ngService.findAllProveedoresByTRed(convenioTemporal.getTipoRed());
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Carga el listao de tipos de contactos. */
    private void cargaListaTiposContactos() {
        if (listaTiposContacto == null || listaTiposContacto.isEmpty()) {
            try {
                listaTiposContacto = ngService.findAllTiposContacto();
            } catch (Exception e) {
                LOGGER.error("Error inesperado", e);
                MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            }
        }
    }

    /** Se encarga de añadir un convenio. */
    public void agregarConvenio() {
        if (validarConvenio(false)) {
            if (proveedor.getConveniosComercializador() == null) {
                proveedor.setConveniosComercializador(new ArrayList<ProveedorConvenio>());
            }

            proveedorConvenio.copy(convenioTemporal);

            proveedorConvenio.setProveedorConvenio(proveedor);
            if (proveedorConvenio.getTipoRed() == null) {
                proveedorConvenio.setTipoRed(proveedorConvenio.getProveedorConcesionario().getTipoRed());
            }

            proveedor.addConvenioComercializador(proveedorConvenio);

            limpiarConvenio();
        }
    }

    /**
     * Edita el convenio.
     */
    public void editarConvenio() {
        try {
            if (proveedorConvenio.getId() == null
                    || !ngService.existeNumeracionAsignadaAlPstByConvenio(proveedorConvenio)) {
                mostrarConvenios = true;
                mostrarContactos = false;
                modoConvenio = EDICION;
                convenioTemporal = proveedorConvenio.clone();
                cargaListaPstConvenios();
            } else {
                List<String> errores = new ArrayList<String>();
                errores.add("catalogo.proveedores.validacion.error.convenio.editar");
                ErroresUtil.generarErrores(errores, MSG_ID);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Actualiza el convenio del listado.
     */
    public void actualizarConvenio() {
        try {
            if (validarConvenio(false)) {
                proveedorConvenio.copy(convenioTemporal);

                // Si el convenio a actualizar ya se guardó en BBDD tendrá id
                if (proveedorConvenio.getId() != null) {
                    for (ProveedorConvenio conv : proveedor.getConveniosComercializador()) {
                        if (proveedorConvenio.getId().equals(conv.getId())) {
                            conv = convenioTemporal;
                            limpiarConvenio();
                            MensajesBean.addInfoMsg(MSG_ID,
                                    MensajesBean.getTextoResource("catalogo.proveedores.mensaje.convenio.actualizar"),
                                    "");
                            return;
                        }
                    }
                } else {
                    // Si no tiene id es un convenio nuevo sin guardar y por tanto hay que buscar el
                    // en la lista de convenios el que se ha editado para actualizarlo
                    for (ProveedorConvenio conv : proveedor.getConveniosComercializador()) {
                        if (mismoConvenio(conv, proveedorConvenio)) {
                            conv = convenioTemporal;
                            limpiarConvenio();
                            MensajesBean.addInfoMsg(MSG_ID,
                                    MensajesBean.getTextoResource("catalogo.proveedores.mensaje.convenio.actualizar"),
                                    "");
                            return;
                        }
                    }
                }

                limpiarConvenio();
            } else {
                convenioTemporal = proveedorConvenio.clone();
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Da de baja el convenio. */
    public void bajaConvenio() {
        List<String> errores = new ArrayList<String>();

        try {
            if (validarConvenio(true)) {
                if (proveedorConvenio.getId() != null) {
                    if (ngService.existeNumeracionAsignadaAlPstByConvenio(proveedorConvenio)) {
                        errores.add("catalogo.proveedores.validacion.error.convenio.baja");
                        ErroresUtil.generarErrores(errores, MSG_ID);
                        return;
                    }
                    for (ProveedorConvenio conv : proveedor.getConveniosComercializador()) {
                        if (proveedorConvenio.getId().equals(conv.getId())) {
                            EstadoConvenio estadoConvenio = new EstadoConvenio();
                            estadoConvenio.setCdg(EstadoConvenio.NO_VIGENTE);
                            proveedorConvenio.setEstatus(estadoConvenio);
                            conv = proveedorConvenio;
                            MensajesBean.addInfoMsg(MSG_ID, MensajesBean
                                    .getTextoResource("catalogo.proveedores.mensaje.convenio.baja"), "");
                            limpiarConvenio();
                            return;
                        }
                    }
                } else {
                    proveedor.getConveniosComercializador().remove(convenioTemporal);
                    MensajesBean.addInfoMsg(MSG_ID,
                            MensajesBean.getTextoResource("catalogo.proveedores.mensaje.convenio.eliminar"), "");
                    limpiarConvenio();
                }
                limpiarConvenio();
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Se encarga de iniciar la baja del convenio. */
    public void iniciarBajaConvenio() {
        List<String> errores = new ArrayList<String>();

        try {
            convenioTemporal = proveedorConvenio;
            if (proveedorConvenio.getId() == null) {
                proveedor.getConveniosComercializador().remove(convenioTemporal);
            } else {
                if (ngService.existeNumeracionAsignadaAlPstByConvenio(proveedorConvenio)) {
                    errores.add("catalogo.proveedores.validacion.error.convenio.baja");
                    ErroresUtil.generarErrores(errores, MSG_ID);
                    return;
                }
                modoConvenio = BAJA;
                mostrarConvenios = true;
                mostrarContactos = false;
            }

            cargaListaPstConvenios();
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Función que valida los campos obligatorios del convenio.
     * @param baja boolean
     * @return boolean boolean
     */
    private boolean validarConvenio(boolean baja) {
        boolean hayError = false;
        List<String> errores = new ArrayList<String>();

        try {
            if (convenioTemporal.getProveedorConcesionario() == null) {
                errores.add("catalogo.proveedores.validacion.error.pstConvenio");
                hayError = true;
            }
            if (convenioTemporal.getContrato() == null || convenioTemporal.getContrato().isEmpty()) {
                errores.add("catalogo.proveedores.validacion.error.contratoConvenio");
                hayError = true;
            }
            if (convenioTemporal.getFechaInicio() == null) {
                errores.add("catalogo.proveedores.validacion.error.fecInicioConvenio");
                hayError = true;
            }
            if (convenioTemporal.getTipoRed() == null && TipoRed.AMBAS.equals(proveedor.getTipoRed().getCdg())) {
                errores.add("catalogo.proveedores.validacion.error.convenio.tipoRed");
                hayError = true;
            }

            if (TipoProveedor.CONCESIONARIO.equals(proveedor.getTipoProveedor().getCdg())) {
                errores.add("catalogo.proveedores.validacion.error.convenio.concesionario");
                hayError = true;
            }

            if (baja) {
                if (convenioTemporal.getReferenciaBaja() == null
                        || "".equals(convenioTemporal.getReferenciaBaja().trim())) {
                    errores.add("catalogo.proveedores.validacion.error.baja.referencia");
                    hayError = true;
                }
                if (convenioTemporal.getFechaFin() == null) {
                    errores.add("catalogo.proveedores.validacion.error.baja.fecBaja");
                    hayError = true;
                } else {
                    if (convenioTemporal.getFechaInicio().after(convenioTemporal.getFechaFin())) {
                        errores.add("catalogo.proveedores.validacion.error.baja.fecIncompatibles");
                        hayError = true;
                    }
                }
            }

            ErroresUtil.generarErrores(errores, MSG_ID);

            return !hayError;
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            hayError = true;
        }

        return !hayError;
    }

    /** Función que limpia los datos del convenio. */
    private void limpiarConvenio() {
        mostrarConvenios = false;
        modoConvenio = ALTA;
        proveedorConvenio = new ProveedorConvenio();
        convenioTemporal = null;
        if (listaProveedores != null) {
            listaProveedores.clear();
        }
    }

    /** Se encarga de añadir el contacto al pst. */
    public void crearContacto() {
        limpiarContacto();
        mostrarContactos = true;
        mostrarConvenios = false;
        modoContacto = ALTA;

        cargaListaTiposContactos();
    }

    /** Se encarga de añadir el contacto al pst. */
    public void agregarContacto() {
        try {
            List<String> errores = ngService.validarContacto(contacto);
            if (errores.isEmpty()) {
                if (TipoContacto.REPRESENTANTE_LEGAL.equals(contacto.getTipoContacto().getCdg())) {
                    if (!ngService.faltaRepresentanteLegal(proveedor)) {
                        errores.add("catalogo.proveedores.validacion.contactos.representante");
                        ErroresUtil.generarErrores(errores, MSG_ID);
                        return;
                    }
                }

                if (proveedor.getContactos() == null) {
                    proveedor.setContactos(new ArrayList<Contacto>());
                }

                proveedor.addContacto(contacto);
                limpiarContacto();
            } else {
                ErroresUtil.generarErrores(errores, MSG_ID);
                return;
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Edita el contacto. */
    public void editarContacto() {
        mostrarContactos = true;
        mostrarConvenios = false;
        modoContacto = EDICION;
        contactoTemporal = contacto;
        cargaListaTiposContactos();
    }

    /** Actualiza el contacto del listado. */
    public void actualizarContacto() {
        try {
            List<String> errores = ngService.validarContacto(contacto);
            if (errores.isEmpty()) {
                // Si el contacto a actualizar ya se guardó en BBDD tendrá id
                if (contacto.getId() != null) {
                    for (Contacto cont : proveedor.getContactos()) {
                        if (contacto.getId().equals(cont.getId())) {
                            cont = contacto;
                            limpiarContacto();
                            MensajesBean.addInfoMsg(MSG_ID,
                                    MensajesBean.getTextoResource("catalogo.proveedores.mensaje.contacto.actualizar"),
                                    "");
                            return;
                        }
                    }
                } else {
                    // Si no tiene id es un contacto nuevo sin guardar y por tanto hay que buscar el
                    // en la lista de contactos el que se ha editado para actualizarlo
                    for (Contacto cont : proveedor.getContactos()) {
                        if (mismoContacto(cont, contactoTemporal)) {
                            cont = contacto;
                            limpiarContacto();
                            MensajesBean.addInfoMsg(MSG_ID,
                                    MensajesBean.getTextoResource("catalogo.proveedores.mensaje.contacto.actualizar"),
                                    "");
                            return;
                        }
                    }
                }
            } else {
                contacto = contactoTemporal;
                ErroresUtil.generarErrores(errores, MSG_ID);
                return;
            }

            limpiarContacto();
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Se encarga de iniciar la baja del contacto. */
    public void iniciarBajaContacto() {
        contactoTemporal = contacto;
        modoContacto = BAJA;
        mostrarContactos = true;
        mostrarConvenios = false;

        cargaListaTiposContactos();
    }

    /** Da de baja el contacto. */
    public void bajaContacto() {
        List<String> errores = new ArrayList<String>();

        try {
            if (ngService.contactoNoUsado(contactoTemporal)) {
                proveedor.getContactos().remove(contactoTemporal);
                MensajesBean.addInfoMsg(MSG_ID,
                        MensajesBean.getTextoResource("catalogo.proveedores.mensaje.contacto.eliminar"),
                        "");

                limpiarContacto();
            } else {
                errores.add("catalogo.proveedores.mensaje.error.contacto.eliminar");
                ErroresUtil.generarErrores(errores, MSG_ID);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Función que limpia los datos del contacto. */
    private void limpiarContacto() {
        mostrarContactos = false;
        modoContacto = ALTA;
        contacto = new Contacto();
        contactoTemporal = null;
        if (listaTiposContacto != null) {
            listaTiposContacto.clear();
        }
    }

    /**
     * Guarda los cambios en el PST.
     * @throws Exception exception
     */
    public void guardarCambios() throws Exception {
        try {
        	/*Se comenta la sección debido a que el BCD ahora será un campo editable*/ 
        	/*if(existeIdo == 0 && (proveedor.getBcd() == null || proveedor.getBcd().intValue() == 0)){
                existeBcd = existeIdo;
                proveedor.setBcd(proveedor.getIdo());
            }*/

            if (validaProveedor()) {
                encriptarPass = (!edicion || !proveedor.getUsuario().getContrasenna().equals(passBBDD));
                proveedor = ngService.guardarProveedor(proveedor, encriptarPass);
                limpiarConvenio();
                limpiarContacto();
                MensajesBean.addInfoMsg(MSG_ID, MensajesBean.getTextoResource("catalogo.proveedores.edicion.mensaje"),
                        "");
                
                if (!edicion) {
                    RequestContext context = RequestContext.getCurrentInstance();
                    context.update(":mensajeAltaPassword");
                    context.execute("PF('mensajeAltaPassword').show();");
                }

                //edicion = true;
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Funcion que valida que todos los datos necesarios han sido introducidos para la creación o actualización del PST.
     * @return boolean validado
     */
    private boolean validaProveedor() {
        try {
            proveedor = ngService.validaProveedor(proveedor, tipoProveedorInicial, tipoRedInicial, edicionUsuario);
            if (!proveedor.getErroresValidacion().isEmpty()) {
                ErroresUtil.generarErrores(proveedor.getErroresValidacion(), MSG_ID);
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
        return true;
    }

    /** Gestiona los cambios de tipo de PST. */
    public void tipoPstChange() {
        if (TipoProveedor.COMERCIALIZADORA.equals(proveedor.getTipoProveedor().getCdg())) {
            habilitarComercializadora = true;
            habilitarConcComerc = false;
            if (proveedor.getId() == null) {
                proveedor.setIdo(null);
                proveedor.setAbc(null);
                proveedor.setBcd(null);
            }
        } else {
            habilitarComercializadora = false;
            habilitarConcComerc = true;

            if (TipoProveedor.CONCESIONARIO.equals(proveedor.getTipoProveedor().getCdg())) {
                if (proveedor.getId() == null) {
                    proveedor.setIda(null);
                }
            }
        }
    }

    /** Gestiona los cambios de tipo de red. */
    public void tipoRedChange() {
        mostrarConvenios = false;
    }

    /** Comprueba si existe otro proveedor con el mismo IDO. */
    public void existeIdo() {
        existeIdo = ngService.existeIdo(proveedor);
        compruebaConfirmacion();
    }

    /** Comprueba si existe otro proveedor con el mismo IDA. */
    public void existeIda() {
        existeIda = ngService.existeIda(proveedor);
        compruebaConfirmacion();
    }

    /** Comprueba si existe otro proveedor con el mismo ABC. */
    public void existeAbc() {
        existeAbc = ngService.existeAbc(proveedor);
        compruebaConfirmacion();
    }

    /** Comprueba si existe otro proveedor con el mismo BCD. */
    public void existeBcd() {
        existeBcd = ngService.existeBcd(proveedor);
        compruebaConfirmacion();
    }

    /** Comprueba si alguno de los campos requiere de confirmación por parte del analista. */
    public void compruebaConfirmacion() {
        if (existeIdo != 0 || existeIda != 0 || existeAbc != 0 || existeBcd != 0) {
            requiereConfirmacion = true;
        } else {
            requiereConfirmacion = false;
        }
    }

    /**
     * Método encargado de comparar dos convenios por sus campos para el caso de que no tenga id.
     * @param conv1 ProveedorConvenio
     * @param conv2 ProveedorConvenio
     * @return boolean
     */
    private boolean mismoConvenio(ProveedorConvenio conv1, ProveedorConvenio conv2) {
        if (conv1.getProveedorConcesionario().getId().equals(conv2.getProveedorConcesionario().getId())
                && conv1.getContrato().equals(conv2.getContrato())
                && conv1.getFechaInicio().equals(conv2.getFechaInicio())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Método encargado de comparar dos contactos por sus campos para el caso de que no tenga id.
     * @param cont1 Contacto
     * @param cont2 Contacto
     * @return boolean
     */
    private boolean mismoContacto(Contacto cont1, Contacto cont2) {
        if (cont1.getTipoContacto().getCdg().equals(cont2.getTipoContacto().getCdg())
                && cont1.getNombre().equals(cont2.getNombre())
                && cont1.getEmail().equals(cont2.getEmail())) {
            return true;
        } else {
            return false;
        }
    }

    /** Método que carga el listado de tipos de red permitidos para la modificación del pst. */
    public void tiposRedValidos() {
        try {
            listadoTiposRed = ngService.findAllTiposRedValidos(proveedor.getTipoRed());
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    // GETTERS & SETTERS

    /**
     * Proveedor.
     * @return Proveedor
     */
    public Proveedor getProveedor() {
        return proveedor;
    }

    /**
     * Proveedor.
     * @param proveedor Proveedor
     */
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * Listado de Tipos de Servicio.
     * @return List<TipoServicio>
     */
    public List<TipoServicio> getListadoTiposServicio() {
        return listadoTiposServicio;
    }

    /**
     * Listado de Tipos de PST.
     * @return List<TipoProveedor>
     */
    public List<TipoProveedor> getListadoTipoPst() {
        return listadoTipoPst;
    }

    /**
     * Listado de los Tipos de Red.
     * @return List<TipoRed>
     */
    public List<TipoRed> getListadoTiposRed() {
        return listadoTiposRed;
    }

    /**
     * Listado de Estados.
     * @return List<Estado>
     */
    public List<Estado> getListadoEstados() {
        return listadoEstados;
    }

    /**
     * Listado de Estados.
     * @param listadoEstados List<Estado>
     */
    public void setListadoEstados(List<Estado> listadoEstados) {
        this.listadoEstados = listadoEstados;
    }

    /**
     * Contacto.
     * @return Contacto
     */
    public Contacto getContacto() {
        return contacto;
    }

    /**
     * Contacto.
     * @param contacto Contacto
     */
    public void setContacto(Contacto contacto) {
        this.contacto = contacto;
    }

    /**
     * Convenio.
     * @return ProveedorConvenio
     */
    public ProveedorConvenio getProveedorConvenio() {
        return proveedorConvenio;
    }

    /**
     * Convenio.
     * @param proveedorConvenio ProveedorConvenio
     */
    public void setProveedorConvenio(ProveedorConvenio proveedorConvenio) {
        this.proveedorConvenio = proveedorConvenio;
    }

    /**
     * Listado de Proveedores.
     * @return List<Proveedor>
     */
    public List<Proveedor> getListaProveedores() {
        return listaProveedores;
    }

    /**
     * Flag para renderizar la creación de convenios.
     * @return boolean
     */
    public boolean isMostrarConvenios() {
        return mostrarConvenios;
    }

    /**
     * Flag para renderizar la creación de convenios.
     * @param mostrarConvenios boolean
     */
    public void setMostrarConvenios(boolean mostrarConvenios) {
        this.mostrarConvenios = mostrarConvenios;
    }

    /**
     * Flag para renderizar la creación de contactos.
     * @return boolean
     */
    public boolean isMostrarContactos() {
        return mostrarContactos;
    }

    /**
     * Flag para renderizar la creación de contactos.
     * @param mostrarContactos boolean
     */
    public void setMostrarContactos(boolean mostrarContactos) {
        this.mostrarContactos = mostrarContactos;
    }

    /**
     * Listado de Tipos de Contacto.
     * @return List<TipoContacto>
     */
    public List<TipoContacto> getListaTiposContacto() {
        return listaTiposContacto;
    }

    /**
     * Flag que indica si es una edición o creación.
     * @return boolean
     */
    public boolean isEdicion() {
        return edicion;
    }

    /**
     * Flag que indica si es una edición o creación.
     * @param edicion boolean
     */
    public void setEdicion(boolean edicion) {
        this.edicion = edicion;
    }

    /**
     * Listado de estatus del proveedor.
     * @return List<Estatus>
     */
    public List<Estatus> getListadoEstatus() {
        return listadoEstatus;
    }

    /**
     * Flag que habilita los campos asociados Listado de estatus del proveedor.
     * @return boolean
     */
    public boolean isHabilitarComercializadora() {
        return habilitarComercializadora;
    }

    /**
     * Flag que habilita los campos asociados Listado de estatus del proveedor.
     * @param habilitarComercializadora boolean
     */
    public void setHabilitarComercializadora(boolean habilitarComercializadora) {
        this.habilitarComercializadora = habilitarComercializadora;
    }

    /**
     * Flag que habilita los campos asociados al tipo de proveedor.
     * @return boolean
     */
    public boolean isHabilitarConcComerc() {
        return habilitarConcComerc;
    }

    /**
     * Flag que habilita los campos asociados al tipo de proveedor.
     * @param habilitarConcComerc boolean
     */
    public void setHabilitarConcComerc(boolean habilitarConcComerc) {
        this.habilitarConcComerc = habilitarConcComerc;
    }

    /**
     * Entero que almacena si el IDO existe en otro proveedor.
     * @return int
     */
    public int getExisteIdo() {
        return existeIdo;
    }

    /**
     * Entero que almacena si el IDO existe en otro proveedor.
     * @param existeIdo int
     */
    public void setExisteIdo(int existeIdo) {
        this.existeIdo = existeIdo;
    }

    /**
     * Entero que almacena si el IDA existe en otro proveedor.
     * @return int
     */
    public int getExisteIda() {
        return existeIda;
    }

    /**
     * Entero que almacena si el IDA existe en otro proveedor.
     * @param existeIda int
     */
    public void setExisteIda(int existeIda) {
        this.existeIda = existeIda;
    }

    /**
     * Entero que almacena si el ABC existe en otro proveedor.
     * @return int
     */
    public int getExisteAbc() {
        return existeAbc;
    }

    /**
     * Entero que almacena si el ABC existe en otro proveedor.
     * @param existeAbc int
     */
    public void setExisteAbc(int existeAbc) {
        this.existeAbc = existeAbc;
    }

    /**
     * Entero que almacena si el BCD existe en otro proveedor.
     * @return int
     */
    public int getExisteBcd() {
        return existeBcd;
    }

    /**
     * Entero que almacena si el BCD existe en otro proveedor.
     * @param existeBcd int
     */
    public void setExisteBcd(int existeBcd) {
        this.existeBcd = existeBcd;
    }

    /**
     * Booleano que indica si el guardado requiere o no de confirmación por parte del analista.
     * @return boolean
     */
    public boolean isRequiereConfirmacion() {
        return requiereConfirmacion;
    }

    /**
     * Booleano que indica si el guardado requiere o no de confirmación por parte del analista.
     * @param requiereConfirmacion boolean
     */
    public void setRequiereConfirmacion(boolean requiereConfirmacion) {
        this.requiereConfirmacion = requiereConfirmacion;
    }

    /**
     * Modo de inserción de datos de los convenios.
     * @return int
     */
    public int getModoConvenio() {
        return modoConvenio;
    }

    /**
     * Modo de inserción de datos de los convenios.
     * @param modoConvenio int
     */
    public void setModoConvenio(int modoConvenio) {
        this.modoConvenio = modoConvenio;
    }

    /**
     * Modo de inserción de datos de los contactos.
     * @return int
     */
    public int getModoContacto() {
        return modoContacto;
    }

    /**
     * Modo de inserción de datos de los contactos.
     * @param modoContacto int
     */
    public void setModoContacto(int modoContacto) {
        this.modoContacto = modoContacto;
    }

    /**
     * Convenio temporal guardado para los casos de ediciones sin guardar.
     * @return ProveedorConvenio
     */
    public ProveedorConvenio getConvenioTemporal() {
        return convenioTemporal;
    }

    /**
     * Convenio temporal guardado para los casos de ediciones sin guardar.
     * @param convenioTemporal ProveedorConvenio
     */
    public void setConvenioTemporal(ProveedorConvenio convenioTemporal) {
        this.convenioTemporal = convenioTemporal;
    }

    /**
     * Contacto temporal para los casos de ediciones de contactos sin guardar.
     * @return Contacto
     */
    public Contacto getContactoTemporal() {
        return contactoTemporal;
    }

    /**
     * Contacto temporal para los casos de ediciones de contactos sin guardar.
     * @param contactoTemporal Contacto
     */
    public void setContactoTemporal(Contacto contactoTemporal) {
        this.contactoTemporal = contactoTemporal;
    }

    /**
     * Flag que indica si es una edición sin usuario.
     * @return boolean
     */
    public boolean isEdicionUsuario() {
        return edicionUsuario;
    }

    /**
     * Flag que indica si es una edición sin usuario.
     * @param edicionUsuario boolean
     */
    public void setEdicionUsuario(boolean edicionUsuario) {
        this.edicionUsuario = edicionUsuario;
    }

    /**
     * Tipo Proveedor inicial.
     * @return String
     */
    public String getTipoProveedorInicial() {
        return tipoProveedorInicial;
    }

    /**
     * Tipo Proveedor inicial.
     * @param tipoProveedorInicial String
     */
    public void setTipoProveedorInicial(String tipoProveedorInicial) {
        this.tipoProveedorInicial = tipoProveedorInicial;
    }

    /**
     * Tipo Red inicial.
     * @return String
     */
    public String getTipoRedInicial() {
        return tipoRedInicial;
    }

    /**
     * Tipo Red inicial.
     * @param tipoRedInicial String
     */
    public void setTipoRedInicial(String tipoRedInicial) {
        this.tipoRedInicial = tipoRedInicial;
    }

    /**
     * Tipo Red Original inicial.
     * @return String
     */
    public String getTipoRedOriginalInicial() {
        return tipoRedOriginalInicial;
    }

    /**
     * Tipo Red Original inicial.
     * @param tipoRedOriginalInicial String
     */
    public void setTipoRedOriginalInicial(String tipoRedOriginalInicial) {
        this.tipoRedOriginalInicial = tipoRedOriginalInicial;
    }

    /**
     * Listado de los Tipos de Red del Convenio.
     * @return List<TipoRed>
     */
    public List<TipoRed> getListadoTiposRedConvenio() {
        return listadoTiposRedConvenio;
    }

    /**
     * Flag para renderizar el listado de tipos de red del convenio.
     * @return boolean
     */
    public boolean isMostrarTipoRedConvenio() {
        return mostrarTipoRedConvenio;
    }

    /**
     * Flag para renderizar el listado de tipos de red del convenio.
     * @param mostrarTipoRedConvenio boolean
     */
    public void setMostrarTipoRedConvenio(boolean mostrarTipoRedConvenio) {
        this.mostrarTipoRedConvenio = mostrarTipoRedConvenio;
    }

    /**
     * Listado de los Tipos de Red Original.
     * @return List<TipoRed>
     */
    public List<TipoRed> getListadoTiposRedOriginal() {
        return listadoTiposRedOriginal;
    }

    /**
     * @return the passBBDD
     */
    public String getPassBBDD() {
        return passBBDD;
    }

    /**
     * @param passBBDD the passBBDD to set
     */
    public void setPassBBDD(String passBBDD) {
        this.passBBDD = passBBDD;
    }

    /**
     * Listado de proveedores asignables para la creación de un convenio.
     * @return the listaProveedoresConvenio
     */
    public List<Proveedor> getListaProveedoresConvenio() {
        return listaProveedoresConvenio;
    }

}
