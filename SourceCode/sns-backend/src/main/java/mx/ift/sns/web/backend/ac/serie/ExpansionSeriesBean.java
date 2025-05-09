package mx.ift.sns.web.backend.ac.serie;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.util.UsuarioUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Gestiona el buscador de series. */
@ManagedBean(name = "expansionSeriesBean")
@ViewScoped
public class ExpansionSeriesBean implements Serializable {
    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExpansionSeriesBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ExpandirSerie";

    /** Servicio de Organizacion Territorial. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade adminCatFacadeService;

    /** Abn. */
    private String abn;

    /** Nir. */
    private Nir nir;

    /** codNir. */
    private String codNir;

    /** Listado de Nirs. */
    private List<Nir> listadoNir;

    /** Listado de Acciones. */
    private List<String> listaAccion;

    /** Acción seleccionada. */
    private String accionSeleccionada;

    /** Lista de códigos de Nir que no están. */
    private List<String> listaCodigosNirs;

    /** Nir seleccionado. */
    private List<String> listaNirsSeleccionados;

    /** Constante para el escenario 1. */
    private static final String CRECIMIENTO_NUMERACION_LOCAL = "CRECIMIENTO_NUMERACION_LOCAL";

    /** Constante para el escenario 2. */
    private static final String IMPLEMENTACION_MARCACION_10_DIGITOS = "IMPLEMENTACION_MARCACION_10_DIGITOS";

    /** Constante para sna inicial con nir de tres digitos. */
    private static final String SNAINITRESDIG = "000";

    /** Constante para sna final con nir de tres digitos. */
    private static final String SNAFINTRESDIG = "099";

    /** Constante para sna inicial con nir de dos digitos. */
    private static final String SNAINIDOSDIG = "0000";

    /** Constante para sna final con nir de dos digitos. */
    private static final String SNAFINDOSDIG = "0999";

    /** Constante para sna inicial con nir de un digito. */
    private static final String SNAINIUNDIG = "00000";

    /** Constante para sna final con nir de un digito. */
    private static final String SNAFINUNDIG = "00999";

    /** booleano que indica si está habilitado el botón de expandir. */
    private boolean btnExpandir = false;

    /** booleano que indica si está habilitado el botón de guardar. */
    private boolean btnGuardar = false;

    /** Valor del nuevo Nir. */
    private String nuevoNir;

    /** Valor del último dígito del Nir. */
    private String ultimoDigito;

    /** Valor nuevo del Sna Inicial. */
    private String nuevoSnaInicial;

    /** Valor nuevo del Sna Final. */
    private String nuevoSnaFinal;

    /** booleano que indica si el nir es de tres cifras o no. */
    private boolean nirTresCifras = false;

    /** Lista con el detalle de la expansión. */
    private List<DetalleExpansionSerie> listaDetalle;

    /** Muestra el panel cuando la accion sea EXPANDIR_NUM_SERIES_NIR. */
    private boolean muestraPanel = false;

    /** Constructor por defecto. */
    public ExpansionSeriesBean() {
    }

    /**
     * Método que inicializa los combos del buscador de proveedores.
     */
    @PostConstruct
    public void init() {
        try {
            abn = null;
            // Listado de nirs del abn
            // listadoNir = adminCatFacadeService.findAllNirs();
            listaAccion = new ArrayList<String>();
            //listaAccion.add(CRECIMIENTO_NUMERACION_LOCAL);
            //listaAccion.add(IMPLEMENTACION_MARCACION_10_DIGITOS);
            listaCodigosNirs = new ArrayList<String>();
            nir = null;
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error inesperado: " + e.getMessage());
            }
            MensajesBean.addInfoMsg(MSG_ID, MensajesBean.getTextoResource("errorGenerico"), "");
        }
    }

    /**
     * Función que carga los nirs asociados al abn seleccionado.
     * @throws Exception exception
     */
    public void cargarNirs() throws Exception {
        if (abn != null && !abn.isEmpty()) {
            listadoNir = adminCatFacadeService.findAllNirByAbn(new BigDecimal(abn));
        }
        listaCodigosNirs = null;
        listaDetalle = null;
        accionSeleccionada=null;
        btnExpandir = false;
        btnGuardar = false;
        
    }
    
    /**
     * Este metodo construye la lista
     * de acciones a mostrar en el combo Accion
     */
    public void cargarListaAcciones(){
    	
    	if(listaAccion.isEmpty()){
    		listaAccion.add(CRECIMIENTO_NUMERACION_LOCAL);
            listaAccion.add(IMPLEMENTACION_MARCACION_10_DIGITOS);
        }
   }

    /**
     * Método que comprueba los datos introducidos.
     */
    public void compruebaDatos() {
        Abn abnAux;
        try {
            if (abn != null && !abn.isEmpty()) {
                if (isNumeric(abn)) {
                    abnAux = adminCatFacadeService.getAbnById(new BigDecimal(abn));
                    if (abnAux != null) {
                        if (accionSeleccionada != null) {
                            if (nir == null) {
                                MensajesBean.addErrorMsg(MSG_ID,
                                        MensajesBean.getTextoResource("catalogo.series.expansion.nir.obligatorio"), "");
                                return;
                            } else {
                                this.compruebaExpansion();
                            }
                        } else {
                            MensajesBean.addErrorMsg(MSG_ID,
                                    MensajesBean.getTextoResource("catalogo.series.expansion.accion.obligatorio"), "");
                            return;
                        }
                    } else {
                        MensajesBean.addErrorMsg(MSG_ID,
                                MensajesBean.getTextoResource("catalogo.series.expansion.abn"), "");
                        return;
                    }
                } else {
                    MensajesBean.addErrorMsg(MSG_ID,
                            MensajesBean.getTextoResource("catalogo.centrales.abn"), "");
                    return;
                }
            } else {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("catalogo.series.expansion.abn.obligatorio"), "");
                return;
            }
        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, MensajesBean.getTextoResource("errorGenerico"), "");
        }
    }

    /**
     * Método que comprueba en qué escenadio de la expansión estamos.
     */
    private void compruebaExpansion() {
        codNir = Integer.toString(nir.getCodigo());
        // Chequeamos que no existan trámites que utilicen el nir seleccionado.
        if (adminCatFacadeService.isSolicitudPendieteByNir(this.nir.getId())
                || adminCatFacadeService.isRangosPentientesByNir(this.nir)) {
            btnExpandir = false;
            MensajesBean.addErrorMsg(MSG_ID,
                    "No es posible expandir la serie. Existen trámites pendientes para el nir: " + this.nir.getCodigo()
                            + " seleccionado.", "");
        } else {
            if (accionSeleccionada.equals(CRECIMIENTO_NUMERACION_LOCAL)) {
                this.validarExpansionSerieByNir();
            } else {
                this.validarExpansionSerieByMarcacion();
            }
        }
    }

    /**
     * Valida si es posible realizar la expansión (Escenario 1).
     */
    private void validarExpansionSerieByNir() {

        if (codNir.length() == 3) {
            nuevoSnaInicial = "000";
            nuevoSnaFinal = "999";
            String dosDigitos = codNir.substring(0, 2);
            Nir nir = adminCatFacadeService.getNirByCodigo(Integer.parseInt(dosDigitos));
            if (nir == null) {
                this.obtenerListaNirs(dosDigitos);
            } else {
                MensajesBean.addErrorMsg(MSG_ID, "Ya existe un Nir de 2 dígitos igual a "
                        + "los 2 dígitos del Nir seleccionado sin su último dígito", "");
                return;
            }
        } else if (codNir.length() == 2) {
            nuevoSnaInicial = "0000";
            nuevoSnaFinal = "9999";
            String unDigito = codNir.substring(0, 1);
            Nir nir = adminCatFacadeService.getNirByCodigo(Integer.parseInt(unDigito));
            if (nir == null) {
                this.obtenerListaNirs(unDigito);
            } else {
                MensajesBean.addErrorMsg(MSG_ID, "Ya existe un Nir de 1 dígito1 igual al "
                        + " 1 dígito del Nir seleccionado sin su último dígito", "");
                return;
            }
        }
        //Cuando el N
        else if (codNir.length() == 1){
        	
        	MensajesBean.addErrorMsg(MSG_ID, "El NIR es de un digito, por lo tanto no es posible ceder ningun digito", "");
            return;
        }

    }

    /**
     * Método que obtiene la lista de los Nirs a expandir.
     * @param digitos digitos
     */
    private void obtenerListaNirs(String digitos) {
        // No existe el Nir de 1 o 2 dígitos depende del partámetro
        listaCodigosNirs = new ArrayList<String>();
        List<Nir> listaNirsCoinciden = adminCatFacadeService.findNirsByDigitos(Integer
                .parseInt(digitos));
        // Se añade a la lista el propio Nir sobre el que se hace la consulta
        listaCodigosNirs.add(String.valueOf(this.nir.getCodigo()));

        if (listaNirsCoinciden.size() == 9) {
            MensajesBean.addInfoMsg(MSG_ID, "Solamente se puede expandir el Nir seleccionado", "");
        } else {
            boolean encontrado = false;
            String codigo;
            String codigoEncont = "";
            String ultimoDigito = "";
            for (int i = 1; i <= 9; i++) {
                for (Nir nirCoinciden : listaNirsCoinciden) {
                    codigo = Integer.toString(nirCoinciden.getCodigo());
                    if (codigo.length() == 3) {
                        ultimoDigito = codigo.substring(2);
                    } else if (codigo.length() == 2) {
                        ultimoDigito = codigo.substring(1);
                    }
                    if (ultimoDigito.equals(String.valueOf(i))) {
                        encontrado = false;
                        break;
                    } else {
                        encontrado = true;
                        codigoEncont = String.valueOf(i);
                    }
                }
                if (encontrado) {
                    codigoEncont = digitos + codigoEncont;
                    listaCodigosNirs.add(codigoEncont);
                }
            }
            LOGGER.debug("Lista de códigos que no estan: " + listaCodigosNirs);
        }
    }

    /**
     * Comprueba si habilita el botón de expandir o no.
     */
    public void habilitarBoton() {
        if (listaNirsSeleccionados != null && !listaNirsSeleccionados.isEmpty()) {
            btnExpandir = true;
        } else {
            btnExpandir = false;
        }
    }

    /**
     * Método que realiza los cálculos para mostrarlos en el resumen.
     */
    public void expandirSerie() {
        DetalleExpansionSerie detalleExp;
        ultimoDigito = "";
        listaDetalle = new ArrayList<DetalleExpansionSerie>();
        // Se añade a la lista el propio Nir sobre el que se hace la consulta
        listaNirsSeleccionados.add(String.valueOf(this.nir.getCodigo()));
        listaNirsSeleccionados = eliminarRepetidos(listaNirsSeleccionados);

        if (listaNirsSeleccionados != null && !listaNirsSeleccionados.isEmpty()) {
            for (String codigo : listaNirsSeleccionados) {
                detalleExp = new DetalleExpansionSerie();
                if (codigo.length() == 3) {
                    nuevoNir = codigo.substring(0, 2);
                    ultimoDigito = codigo.substring(2);
                    nuevoSnaInicial = "000";
                    nuevoSnaFinal = "999";
                    nirTresCifras = true;
                } else if (codigo.length() == 2) {
                    nuevoNir = codigo.substring(0, 1);
                    ultimoDigito = codigo.substring(1);
                    nuevoSnaInicial = "0000";
                    nuevoSnaFinal = "9999";
                    nirTresCifras = false;
                }
                nuevoSnaInicial = ultimoDigito + nuevoSnaInicial;
                nuevoSnaFinal = ultimoDigito + nuevoSnaFinal;
                detalleExp.setNir(nuevoNir);
                detalleExp.setSnaInicial(nuevoSnaInicial);
                detalleExp.setSnaFinal(nuevoSnaFinal);
                listaDetalle.add(detalleExp);
            }
            btnGuardar = true;
        }
    }

    /**
     * Valida si es posible realizar la expansión (Escenario 2).
     */
    private void validarExpansionSerieByMarcacion() {
        boolean existeSerie = adminCatFacadeService.existSerieWithNirAbn(abn, nir.getId().toString());
        if (existeSerie) {
            MensajesBean.addErrorMsg(MSG_ID, "No se puede Expandir la serie, ya que para ese Abn y ese Nir ya existen",
                    "");
            btnGuardar = false;
        } else {
            if (adminCatFacadeService.isSolicitudPendieteByNir(nir.getId())
                    || adminCatFacadeService.isRangosPentientesByNir(nir)) {
                MensajesBean.addErrorMsg(MSG_ID,
                        "No es posible expandir la serie. Existen trámites pendientes para el nir: "
                                + this.nir.getCodigo()
                                + " seleccionado.", "");

            } else {
                if (codNir.length() == 3) {
                    MensajesBean.addInfoMsg(MSG_ID, "La serie del " + SNAINITRESDIG + " al " + SNAFINTRESDIG
                            + " no existe, pulse el botón Guardar para crearlas", "");
                } else if (codNir.length() == 2) {
                    MensajesBean.addInfoMsg(MSG_ID, "La serie del " + SNAINIDOSDIG + " al " + SNAFINDOSDIG
                            + " no existe, pulse el botón Guardar para crearlas", "");
                } else {
                    MensajesBean.addInfoMsg(MSG_ID, "La serie del " + SNAINIUNDIG + " al " + SNAFINUNDIG
                            + " no existe, pulse el botón Guardar para crearlas", "");
                }
                btnGuardar = true;
            }
        }
    }

    /**
     * Método que realiza el guardado en BD de la expasión.
     */
    public void guardar() {
        int nuevoSna = 0;
        try {
            if (accionSeleccionada.equals(CRECIMIENTO_NUMERACION_LOCAL)) {
                if (listaDetalle != null && !listaDetalle.isEmpty()) {
                    if (nirTresCifras) {
                        for (DetalleExpansionSerie detalleSerie : listaDetalle) {
                            String primerDigito = detalleSerie.getSnaInicial().substring(0, 1);
                            ultimoDigito = String.valueOf(nir.getCodigo()).substring(2);
                            if (ultimoDigito.equals(primerDigito)) {
                                nuevoSna = Integer.parseInt(detalleSerie.getSnaInicial());
                                for (int i = 100; i <= 999; i++) {
                                    // llamada al procedimiento, la serie 100 pasa a ser la 1000, con lo cual la 999
                                    // sería la 1899
                                    adminCatFacadeService.actualizaSerie(new BigDecimal(i), nir.getId(),
                                            new BigDecimal(nuevoSna), UsuarioUtil.getUsuario()
                                                    .getId());
                                    nuevoSna = nuevoSna + 1;
                                }
                                // Este bucle es para crear las series que faltarían, es decir, desde la 1900 hasta la
                                // 1999
                                for (int i = nuevoSna; i <= Integer.parseInt(detalleSerie.getSnaFinal()); i++) {
                                    adminCatFacadeService.actualizaSerie(null, nir.getId(), new BigDecimal(i),
                                            UsuarioUtil
                                                    .getUsuario().getId());
                                }
                            } else {
                                for (int i = Integer.parseInt(detalleSerie.getSnaInicial()); i <= Integer
                                        .parseInt(detalleSerie
                                                .getSnaFinal()); i++) {
                                    // llamada al procedimiento para crear las demás series que se expanden
                                    adminCatFacadeService.actualizaSerie(null, nir.getId(), new BigDecimal(i),
                                            UsuarioUtil
                                                    .getUsuario().getId());
                                }
                            }
                        }
                        nir.setCodigo(Integer.parseInt(nuevoNir));
                        adminCatFacadeService.saveNir(nir);
                    } else {
                        for (DetalleExpansionSerie detalleSerie : listaDetalle) {
                            String primerDigito = detalleSerie.getSnaInicial().substring(0, 1);
                            ultimoDigito = String.valueOf(nir.getCodigo()).substring(1);
                            if (ultimoDigito.equals(primerDigito)) {
                                nuevoSna = Integer.parseInt(detalleSerie.getSnaInicial());
                                for (int i = 1000; i <= 9999; i++) {
                                    // llamada al procedimiento, la serie 1000 pasa a ser la 10000, con lo cual la 9999
                                    // sería la 18999
                                    adminCatFacadeService.actualizaSerie(new BigDecimal(i), nir.getId(),
                                            new BigDecimal(nuevoSna), UsuarioUtil.getUsuario()
                                                    .getId());
                                    nuevoSna = nuevoSna + 1;
                                }
                                // Este bucle es para crear las series que faltarían, es decir, desde la 19000 hasta la
                                // 19999
                                for (int i = nuevoSna; i <= Integer.parseInt(detalleSerie.getSnaFinal()); i++) {
                                    adminCatFacadeService.actualizaSerie(null, nir.getId(), new BigDecimal(i),
                                            UsuarioUtil
                                                    .getUsuario().getId());
                                }
                            } else {
                                for (int i = Integer.parseInt(detalleSerie.getSnaInicial()); i <= Integer
                                        .parseInt(detalleSerie
                                                .getSnaFinal()); i++) {
                                    // llamada al procedimiento para crear las demás series que se expanden
                                    adminCatFacadeService.actualizaSerie(null, nir.getId(), new BigDecimal(i),
                                            UsuarioUtil
                                                    .getUsuario().getId());
                                }
                            }
                        }
                        nir.setCodigo(Integer.parseInt(nuevoNir));
                        adminCatFacadeService.saveNir(nir);
                    }
                }
            } else {
                // if(adminCatFacadeService.isSolicitudPendieteWithSerie(idSerie,this.nir,
                // EstadoSolicitud.SOLICITUD_EN_TRAMITE))
                if (codNir.length() == 3) {
                    for (int i = Integer.parseInt(SNAINITRESDIG); i <= Integer.parseInt(SNAFINTRESDIG); i++) {
                        adminCatFacadeService.createSerie(nir, new BigDecimal(i));
                    }
                } else if (codNir.length() == 2) {
                    for (int i = Integer.parseInt(SNAINIDOSDIG); i <= Integer.parseInt(SNAFINDOSDIG); i++) {
                        adminCatFacadeService.createSerie(nir, new BigDecimal(i));
                    }
                } else {
                    for (int i = Integer.parseInt(SNAINIUNDIG); i <= Integer.parseInt(SNAFINUNDIG); i++) {
                        adminCatFacadeService.createSerie(nir, new BigDecimal(i));
                    }
                }
            }
            MensajesBean.addInfoMsg(MSG_ID, "Guardado realizado correctamente", "");
            btnGuardar = false;
            //Metodo que ejecuta nuevamente el cargue de NIRs actualizados
            //para el ABD actual
            this.cargarNirs();
        } catch (Exception e) {
            LOGGER.error("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, MensajesBean.getTextoResource("errorGenerico"), "");
        }
    }

    /**
     * Método que elimina los elementos repetidos de una lista.
     * @param lista lista
     * @return List<String>
     */
    private List<String> eliminarRepetidos(List<String> lista) {
        HashSet<String> hs = new HashSet<String>();
        hs.addAll(lista);
        lista.clear();
        lista.addAll(hs);
        return lista;
    }

    /**
     * Método que renderiza un Panel.
     */
    public void compruebaAccion() {
        if (accionSeleccionada != null && !accionSeleccionada.isEmpty()) {
            if (accionSeleccionada.equals(CRECIMIENTO_NUMERACION_LOCAL)) {
                muestraPanel = true;
            } else {
                muestraPanel = false;
            }
        }
    }

    /**
     * Método para saber si es numerica la cadena.
     * @param cadena cadena de entrada.
     * @return boolean true/false.
     */
    private static boolean isNumeric(String cadena) {
        try {
            Integer.parseInt(cadena);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * Reset.
     */
    public void resetTab() {
        abn = null;
        listadoNir = null;
        accionSeleccionada = null;
        listaNirsSeleccionados = null;
        btnExpandir = false;
        btnGuardar = false;
        listaCodigosNirs = null;
        nuevoNir = null;
        nuevoSnaInicial = null;
        nuevoSnaFinal = null;
        listaDetalle = null;
        muestraPanel = false;
    }

    // -------------------------- GETTERS Y SETTERS ---------------------------

    /**
     * Abn.
     * @return String
     */
    public String getAbn() {
        return abn;
    }

    /**
     * Abn.
     * @param abn String
     */
    public void setAbn(String abn) {
        this.abn = abn;
    }

    /**
     * Nir.
     * @return Nir
     */
    public Nir getNir() {
        return nir;
    }

    /**
     * Nir.
     * @param nir Nir
     */
    public void setNir(Nir nir) {
        this.nir = nir;
    }

    /**
     * Listado de Nirs.
     * @return List<Nir>
     */
    public List<Nir> getListadoNir() {
        return listadoNir;
    }

    /**
     * Listado de Nirs.
     * @param listadoNir List<Nir>
     */
    public void setListadoNir(List<Nir> listadoNir) {
        this.listadoNir = listadoNir;
    }

    /**
     * Listado de Acciones.
     * @return List<String>
     */
    public List<String> getListaAccion() {
        return listaAccion;
    }

    /**
     * Listado de Acciones.
     * @param listaAccion List<String>
     */
    public void setListaAccion(List<String> listaAccion) {
        this.listaAccion = listaAccion;
    }

    /**
     * Acción seleccionada.
     * @return String
     */
    public String getAccionSeleccionada() {
        return accionSeleccionada;
    }

    /**
     * Acción seleccionada.
     * @param accionSeleccionada String
     */
    public void setAccionSeleccionada(String accionSeleccionada) {
        this.accionSeleccionada = accionSeleccionada;
    }

    /**
     * codNir.
     * @return String
     */
    public String getCodNir() {
        return codNir;
    }

    /**
     * codNir.
     * @param codNir String
     */
    public void setCodNir(String codNir) {
        this.codNir = codNir;
    }

    /**
     * Lista de códigos de Nir que no están.
     * @return List<String>
     */
    public List<String> getListaCodigosNirs() {
        return listaCodigosNirs;
    }

    /**
     * Lista de códigos de Nir que no están.
     * @param listaCodigosNirs List<String>
     */
    public void setListaCodigosNirs(List<String> listaCodigosNirs) {
        this.listaCodigosNirs = listaCodigosNirs;
    }

    /**
     * booleano que indica si está habilitado el botón de expandir.
     * @return boolean
     */
    public boolean isBtnExpandir() {
        return btnExpandir;
    }

    /**
     * booleano que indica si está habilitado el botón de expandir.
     * @param btnExpandir boolean
     */
    public void setBtnExpandir(boolean btnExpandir) {
        this.btnExpandir = btnExpandir;
    }

    /**
     * booleano que indica si está habilitado el botón de guardar.
     * @return boolean
     */
    public boolean isBtnGuardar() {
        return btnGuardar;
    }

    /**
     * booleano que indica si está habilitado el botón de guardar.
     * @param btnGuardar boolean
     */
    public void setBtnGuardar(boolean btnGuardar) {
        this.btnGuardar = btnGuardar;
    }

    /**
     * Valor del nuevo Nir.
     * @return String
     */
    public String getNuevoNir() {
        return nuevoNir;
    }

    /**
     * Valor del nuevo Nir.
     * @param nuevoNir String
     */
    public void setNuevoNir(String nuevoNir) {
        this.nuevoNir = nuevoNir;
    }

    /**
     * Valor nuevo del Sna Inicial.
     * @return String
     */
    public String getNuevoSnaInicial() {
        return nuevoSnaInicial;
    }

    /**
     * Valor nuevo del Sna Inicial.
     * @param nuevoSnaInicial String
     */
    public void setNuevoSnaInicial(String nuevoSnaInicial) {
        this.nuevoSnaInicial = nuevoSnaInicial;
    }

    /**
     * Valor nuevo del Sna Final.
     * @return String
     */
    public String getNuevoSnaFinal() {
        return nuevoSnaFinal;
    }

    /**
     * Valor nuevo del Sna Final.
     * @param nuevoSnaFinal String
     */
    public void setNuevoSnaFinal(String nuevoSnaFinal) {
        this.nuevoSnaFinal = nuevoSnaFinal;
    }

    /**
     * Lista con el detalle de la expansión.
     * @return List<DetalleExpansionSerie>
     */
    public List<DetalleExpansionSerie> getListaDetalle() {
        return listaDetalle;
    }

    /**
     * Lista con el detalle de la expansión.
     * @param listaDetalle List<DetalleExpansionSerie>
     */
    public void setListaDetalle(List<DetalleExpansionSerie> listaDetalle) {
        this.listaDetalle = listaDetalle;
    }

    /**
     * Nir seleccionado.
     * @return List<String>
     */
    public List<String> getListaNirsSeleccionados() {
        return listaNirsSeleccionados;
    }

    /**
     * Nir seleccionado.
     * @param listaNirsSeleccionados List<String>
     */
    public void setListaNirsSeleccionados(List<String> listaNirsSeleccionados) {
        this.listaNirsSeleccionados = listaNirsSeleccionados;
    }

    /**
     * Muestra el panel cuando la accion sea EXPANDIR_NUM_SERIES_NIR.
     * @return boolean
     */
    public boolean isMuestraPanel() {
        return muestraPanel;
    }

    /**
     * Muestra el panel cuando la accion sea EXPANDIR_NUM_SERIES_NIR.
     * @param muestraPanel boolean
     */
    public void setMuestraPanel(boolean muestraPanel) {
        this.muestraPanel = muestraPanel;
    }

    /**
     * booleano que indica si el nir es de tres cifras o no.
     * @return the nirTresCifras
     */
    public boolean isNirTresCifras() {
        return nirTresCifras;
    }

    /**
     * booleano que indica si el nir es de tres cifras o no.
     * @param nirTresCifras the nirTresCifras to set
     */
    public void setNirTresCifras(boolean nirTresCifras) {
        this.nirTresCifras = nirTresCifras;
    }

}
