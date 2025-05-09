package mx.ift.sns.web.backend.ac.central;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.central.Central;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.central.Marca;
import mx.ift.sns.modelo.central.Modelo;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.utils.LocalizacionUtil;
import mx.ift.sns.web.backend.ac.estado.ConsultarEstadosBean;
import mx.ift.sns.web.backend.ac.marcamodelo.MarcaBean;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** ManagedBean. */
@ManagedBean(name = "nuevaCentralBean")
@ViewScoped
public class NuevaCentralBean implements Serializable {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultarEstadosBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Id de errores. */
    private static final String MSG_ID = "MSG_DatosCentral";

    /** Estado. */
    private Estado estadoMun;

    /** Municipio. */
    private Municipio municipio;

    /** Lista Estado. */
    private List<Estado> listaEstados;

    /** Lista Municipio. */
    private List<Municipio> listaMunicipio;

    /** Lista Población. */
    private List<Poblacion> listaPoblacion;

    /** Lista de proveedores disponibles para seleccionar. */
    private List<Proveedor> listaProveedores;

    /** Objeto central con el que se va a trabajar. */
    private Central central;

    /** Lista de marcas disponibles para seleccionar. */
    private List<Marca> listaMarcas;

    /** Lista de modelos disponibles para seleccionar. */
    private List<Modelo> listaModelos;

    /** Constante con el valor máximo de la longitud. */
    private static final int MAX_LONGITUD = 117;

    /** Constante con el valor mínimo de la longitud. */
    private static final int MIN_LONGITUD = 86;

    /** Constante con el valor máximo de la latitud. */
    private static final int MAX_LATITUD = 32;

    /** Constante con el valor mínimo de la latitud. */
    private static final int MIN_LATITUD = 14;

    /** Constante con el valor máximo de los minutos y segundos. */
    private static final double MAX_MINUTOS = 60.0;

    /** Constante con el valor mínimo de los minutos y segundos. */
    private static final double MIN_MINUTOS = 0.0;

    /** Constante con el valor mínimo de los minutos y segundos. */
    private static final String CDG_INEGI = "000000000";

    /** Constante con el valor de las coordenadas a 0. */
    private static final String COORDENADA_0 = "0° 0' 0\"";

    /** Booleano que nos indica si se edita la central. */
    private boolean isEditar = false;

    /** Servicio de Organizacion Territorial. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade adminCatFacadeService;

    /** Referencia al Bean de Solicitud de Asignaciones. */
    @ManagedProperty("#{marcaBean}")
    private MarcaBean marcaBean;

    /**
     * Iniciamos la lista de estados y la lista proveedores y cargamos los combos.
     * @throws Exception error en inicio
     **/
    @PostConstruct
    public void init() throws Exception {
        try {
            // Listado de estados
            listaEstados = adminCatFacadeService.findAllEstados();

            listaMunicipio = new ArrayList<Municipio>();

            listaPoblacion = new ArrayList<Poblacion>();

            // Catálogo de Proveedores
            listaProveedores = adminCatFacadeService.findAllProveedoresActivos();

            // Catálogo de Marcas
            listaMarcas = adminCatFacadeService.findAllMarcas();

            central = new Central();
            Marca marca = new Marca();
            Modelo modelo = new Modelo();
            modelo.setMarca(marca);

            central.setModelo(modelo);

        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método que realiza el guardado de los datos del formulario.
     **/
    public void guardarCentral() {
        try {
            if (this.validarDatosFormulario(central)) {
                central = adminCatFacadeService.saveCentral(central);
                MensajesBean.addInfoMsg(MSG_ID, "Central guardada: " + central.getId(), "");
            } 
            //else {
             //   MensajesBean.addErrorMsg(MSG_ID, "Error al guardar la Central", "");
            //}
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método que realiza la validación de los datos del formulario de alta de la central.
     * @return boolean
     * @param pcentral central
     */
    private boolean validarDatosFormulario(Central pcentral) {
        boolean valido = false;
        try {
            if (StringUtils.isEmpty(pcentral.getNombre().trim())) {
                //MensajesBean.addErrorMsg(MSG_ID, "El nombre introducido no es correcto.", "");
                //return false;
            	throw new Exception("El nombre introducido no es correcto.");
            }

            // Campos obligatorios del formulario
            central.setNombre(pcentral.getNombre());
            central.setProveedor(pcentral.getProveedor());

            // Se pone el estado de la central a ACTIVO
            Estatus estatus = new Estatus();
            estatus.setCdg(Estatus.ACTIVO);
            central.setEstatus(estatus);

            // Código Postal: SI no es numérico salta la excepción, Si es nulo se le pone un "0"(VL2)
            if (pcentral.getCp() != null && !pcentral.getCp().isEmpty()) {
                if (!isNumeric(pcentral.getCp())) {
                    throw new Exception("El campo Código Postal, debe ser numérico.");
                }
            } else {
                central.setCp("0");
            }
            central.setCp(pcentral.getCp());

            // Estado, Municipio y Población
            if (pcentral.getPoblacion() == null) {
                // no se introduce ningún estado, municipio, población. Se coge la la población "DESCONOCIDA" con
                // inegi="000000000"
                central.setPoblacion(adminCatFacadeService.findPoblacionById(CDG_INEGI));
            } else {
                central.setPoblacion(pcentral.getPoblacion());
            }

            // Calle: (VL2)
            if (pcentral.getCalle() == null || pcentral.getCalle().isEmpty()) {
                central.setCalle("0");
            }
            central.setCalle(pcentral.getCalle());

            // Número exterior: (VL2)
            if (pcentral.getNumero() == null) {
                central.setNumero("0");
            }
            central.setNumero(pcentral.getNumero());

            // Colonia: (VL2)
            if (pcentral.getColonia() == null || pcentral.getColonia().isEmpty()) {
                central.setColonia("0");
            }
            central.setColonia(pcentral.getColonia());

            // Latitud: (VL2) y propias de la localización
            if (pcentral.getLatitud() == null || pcentral.getLatitud().isEmpty() || pcentral.getLatitud().equals("0")) {
                central.setLatitud(COORDENADA_0);
            } else {
                pcentral.setLatitud(LocalizacionUtil.reemplazarGrado(pcentral.getLatitud()));
                if (this.validarLatitud(pcentral.getLatitud())) {
                    central.setLatitud(pcentral.getLatitud());
                } else {
                    throw new Exception("La latitud no es correcta.");
                }
            }

            // Longitud: (VL2) y propias de la localización
            if (pcentral.getLongitud() == null
                    || pcentral.getLongitud().isEmpty()
                    || pcentral.getLongitud().equals("0")) {
                central.setLongitud(COORDENADA_0);
            } else {
                pcentral.setLongitud(LocalizacionUtil.reemplazarGrado(pcentral.getLongitud()));
                if (this.validarLongitud(pcentral.getLongitud())) {
                    central.setLongitud(pcentral.getLongitud());
                } else {
                    throw new Exception("La longitud no es correcta.");
                }
            }

            // Jerarquía de momento no tiene validaciones
            central.setJerarquia(pcentral.getJerarquia());

            // Marca y Modelo: (VL2) si no se selecciona una, por defecto se pone la Marca '0' (DESCONOCIDA)
            List<Modelo> listaModelo;
            Modelo modelo = new Modelo();
            if (pcentral.getModelo().getId() == null) {
                listaModelo = adminCatFacadeService.getModelosByMarca(new BigDecimal(0));
                modelo = listaModelo.get(0);
                central.setModelo(modelo);
            } else {
                central.setModelo(pcentral.getModelo());
            }

            // Capacidad inicial: SI no es numérico salta la excepción, Si es nulo se le pone un "0"(VL2)
            if (pcentral.getCapacidadInicial() != null) {
                if (!isNumeric(pcentral.getCapacidadInicial().toString())) {
                    throw new Exception("El campo Capacidad Inicial, debe ser numérico.");
                } else {
                    if (pcentral.getCapacidadInicial().intValue() < 0) {
                        throw new Exception("El campo Capacidad Inicial, debe ser positivo.");
                    }
                }
            } else {
                central.setCapacidadInicial(new BigDecimal("0"));
            }
            central.setCapacidadInicial(pcentral.getCapacidadInicial());

            // Capacidad final: SI no es numérico salta la excepción, Si es nulo se le pone un "0"(VL2)
            if (pcentral.getCapacidadFinal() != null) {
                if (!isNumeric(pcentral.getCapacidadFinal().toString())) {
                    throw new Exception("El campo Capacidad Final, debe ser numérico.");
                } else {
                    if (pcentral.getCapacidadFinal().intValue() < 0) {
                        throw new Exception("El campo Capacidad Final, debe ser positivo.");
                    }
                }
            } else {
                central.setCapacidadFinal(new BigDecimal("0"));
            }
            central.setCapacidadFinal(pcentral.getCapacidadFinal());

            valido = true;
        } catch (Exception e) {
            MensajesBean.addErrorMsg(MSG_ID, "Error: " + e.getMessage(), "");
            valido = false;
        }
        return valido;
    }

    /**
     * Método que realiza la carga de la central seleccionada para editar.
     * @param pId id de la central
     */
    public void cargarCentral(BigDecimal pId) {
        try {
            Central centralSelec = adminCatFacadeService.findCentralById(pId);
            this.setCentral(centralSelec);
            this.setEstadoMun(centralSelec.getPoblacion().getMunicipio().getEstado());
            this.setMunicipio(centralSelec.getPoblacion().getMunicipio());
            this.setEditar(true);
            this.habilitarMunicipio();
            this.habilitarPoblacion();
            Marca marca = adminCatFacadeService.getMarcaById(central.getModelo().getMarca().getId());
            List<Marca> listaMarc = new ArrayList<Marca>();
            listaMarc.add(marca);
            this.setListaMarcas(listaMarc);
            this.habilitarSeleccionModelo();
        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método que realiza el refresco de la central, después de ir al catálogo de Marcas y Modelos.
     */
    public void refrescaCentral() {
        try {
            if (central.getModelo().getMarca().getId() != null) {
                Marca marca = adminCatFacadeService.getMarcaById(central.getModelo().getMarca().getId());
                central.getModelo().setMarca(marca);
                this.setCentral(central);
            }
            listaMarcas = adminCatFacadeService.findAllMarcas();
            this.habilitarSeleccionModelo();
        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método que comprueba que no deseleccionen un modelo.
     */
    public void compruebaModelo() {
        if (central.getModelo().getId() == null) {
            MensajesBean.addErrorMsg(MSG_ID, "No puede haber una central sin un Modelo asignado.", "");
        }
    }

    /**
     * Método que realiza la carga de la Marca y el Modelo del Catálogo.
     */
    public void editarMarcaModelo() {
        marcaBean.cargarMarca(central.getModelo().getMarca().getId());
    }

    /**
     * Método que realiza la llamada al init de la Marca.
     */
    public void crearMarcaModelo() {
        try {
            marcaBean.init();
        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método que valida las coordenadas.
     * @param latitud latitud
     * @return correcto
     * @throws Exception exception
     */
    private boolean validarLatitud(String latitud) throws Exception {
        boolean correcto = false;
        String[] part;
        String grados;
        String simGrados;
        String simMinut;
        String simSeg;

        part = latitud.trim().split(" ");
        try {
            // Se comprueba si es la latitud 0° 0' 0". Si no lo es continúa y si lo es la damos como correcta
            if (!latitud.equals(COORDENADA_0)) {
                simGrados = part[0].substring(part[0].length() - 1);
                simMinut = part[1].substring(part[1].length() - 1);
                simSeg = part[2].substring(part[2].length() - 1);
                grados = part[0].substring(0, part[0].length() - 1);
                if (simGrados.equals("°") && simMinut.equals("'") && simSeg.equals("\"")) {
                    if (isNumeric(grados)) {
                        if ((MIN_LATITUD <= Integer.parseInt(grados)) && (Integer.parseInt(grados) <= MAX_LATITUD)) {
                            String minutos = part[1].substring(0, part[1].length() - 1);
                            if (isNumeric(minutos)) {
                                if ((MIN_MINUTOS <= Double.parseDouble(minutos))
                                        && (Double.parseDouble(minutos) < MAX_MINUTOS)) {
                                    String segundos = part[2].substring(0, part[2].length() - 1);
                                    if (isNumeric(segundos)) {
                                        if ((MIN_MINUTOS <= Double.parseDouble(segundos))
                                                && (Double.parseDouble(segundos) < MAX_MINUTOS)) {
                                            correcto = true;
                                        } else {
                                            correcto = false;
                                        }
                                    }
                                } else {
                                    correcto = false;
                                }
                            }
                        } else {
                            correcto = false;
                        }
                    }
                } else {
                    throw new Exception();
                }
            } else {
                correcto = true;
            }
        } catch (Exception e) {
            throw new Exception("Formato de la latitud es incorrecto. Ejemplo 20° 30' 30\"");
        }
        return correcto;
    }

    /**
     * Método que valida las coordenadas.
     * @param longitud longitud
     * @return correcto
     * @throws Exception exception
     */
    private boolean validarLongitud(String longitud) throws Exception {
        boolean correcto = false;
        String[] part;
        String grados;
        String simGrados;
        String simMinut;
        String simSeg;

        longitud = LocalizacionUtil.reemplazarGrado(longitud);
        part = longitud.trim().split(" ");
        try {
            if (!longitud.equals(COORDENADA_0)) {
                simGrados = part[0].substring(part[0].length() - 1);
                simMinut = part[1].substring(part[1].length() - 1);
                simSeg = part[2].substring(part[2].length() - 1);
                grados = part[0].substring(0, part[0].length() - 1);
                if (simGrados.equals("°") && simMinut.equals("'") && simSeg.equals("\"")) {
                    if (isNumeric(grados)) {
                        if ((MIN_LONGITUD <= Integer.parseInt(grados)) && (Integer.parseInt(grados) <= MAX_LONGITUD)) {
                            String minutos = part[1].substring(0, part[1].length() - 1);
                            if (isNumeric(minutos)) {
                                if ((MIN_MINUTOS <= Double.parseDouble(minutos))
                                        && (Double.parseDouble(minutos) < MAX_MINUTOS)) {
                                    String segundos = part[2].substring(0, part[2].length() - 1);
                                    if (isNumeric(segundos)) {
                                        if ((MIN_MINUTOS <= Double.parseDouble(segundos))
                                                && (Double.parseDouble(segundos) < MAX_MINUTOS)) {
                                            correcto = true;
                                        } else {
                                            correcto = false;
                                        }
                                    }
                                } else {
                                    correcto = false;
                                }
                            }
                        } else {
                            correcto = false;
                        }
                    }
                } else {
                    throw new Exception();
                }
            } else {
                correcto = true;
            }
        } catch (Exception e) {
            throw new Exception("Formato de la longitud es incorrecto. Ejemplo 90° 30' 30\"");
        }

        return correcto;
    }

    /**
     * Método para saber si es numerica la cadena.
     * @param cadena cadena de entrada.
     * @return boolean true/false.
     */
    private static boolean isNumeric(String cadena) {
        try {
            cadena.matches("\\d*");
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /** Método invocado al seleccionar una marca del combo de marcas. */
    public void habilitarSeleccionModelo() {
        try {
            if (central.getModelo().getMarca().getId() != null) {
                // Modelos
                listaModelos = adminCatFacadeService.getModelosByMarca(central.getModelo().getMarca().getId());
                // modeloSeleccionado = null;
                this.setEditar(true);
            } else {
                listaModelos = null;
            }
        } catch (Exception ex) {
            LOGGER.error("error", ex);
        }
    }

    /**
     * Rellena la lista de municipios por Estado seleccionado.
     * @throws Exception excepcion
     */
    public void habilitarMunicipio() throws Exception {
        listaPoblacion = null;
        if (estadoMun != null) {
            listaMunicipio = adminCatFacadeService.findMunicipiosByEstado(estadoMun.getCodEstado());
        } else {
            listaMunicipio = null;
            listaPoblacion = null;
        }
    }

    /**
     * Rellena la lista de poblaciones por estado y municipio.
     * @throws Exception excepcion
     */
    public void habilitarPoblacion() throws Exception {
        if (municipio != null) {
            listaPoblacion = adminCatFacadeService.findAllPoblaciones(estadoMun.getCodEstado(),
                    municipio.getId().getCodMunicipio());
        } else {
            listaPoblacion = null;
        }
    }

    /**
     * Reset.
     */
    public void resetTab() {
        this.estadoMun = null;
        this.municipio = null;
        this.central = new Central();
        Marca marca = new Marca();
        Modelo modelo = new Modelo();
        modelo.setMarca(marca);
        this.central.setModelo(modelo);
        this.isEditar = false;
        this.listaModelos = null;
        this.listaMunicipio = null;
        this.listaPoblacion = null;
    }

    /**
     * Estado.
     * @return Estado
     */
    public Estado getEstadoMun() {
        return estadoMun;
    }

    /**
     * Estado.
     * @param estadoMun Estado
     */
    public void setEstadoMun(Estado estadoMun) {
        this.estadoMun = estadoMun;
    }

    /**
     * Municipio.
     * @return Municipio
     */
    public Municipio getMunicipio() {
        return municipio;
    }

    /**
     * Municipio.
     * @param municipio Municipio
     */
    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    /**
     * Lista Estado.
     * @return List<Estado>
     */
    public List<Estado> getListaEstados() {
        return listaEstados;
    }

    /**
     * Lista Estado.
     * @param listaEstados List<Estado>
     */
    public void setListaEstados(List<Estado> listaEstados) {
        this.listaEstados = listaEstados;
    }

    /**
     * Lista Municipio.
     * @return List<Municipio>
     */
    public List<Municipio> getListaMunicipio() {
        return listaMunicipio;
    }

    /**
     * Lista Municipio.
     * @param listaMunicipio List<Municipio>
     */
    public void setListaMunicipio(List<Municipio> listaMunicipio) {
        this.listaMunicipio = listaMunicipio;
    }

    /**
     * Lista Población.
     * @return List<Poblacion>
     */
    public List<Poblacion> getListaPoblacion() {
        return listaPoblacion;
    }

    /**
     * Lista Población.
     * @param listaPoblacion List<Poblacion>
     */
    public void setListaPoblacion(List<Poblacion> listaPoblacion) {
        this.listaPoblacion = listaPoblacion;
    }

    /**
     * Lista de proveedores disponibles para seleccionar.
     * @return List<Proveedor>
     */
    public List<Proveedor> getListaProveedores() {
        return listaProveedores;
    }

    /**
     * Lista de proveedores disponibles para seleccionar.
     * @param listaProveedores List<Proveedor>
     */
    public void setListaProveedores(List<Proveedor> listaProveedores) {
        this.listaProveedores = listaProveedores;
    }

    /**
     * Servicio de Organizacion Territorial.
     * @return IAdminCatalogosFacade
     */
    public IAdminCatalogosFacade getAdminCatFacadeService() {
        return adminCatFacadeService;
    }

    /**
     * Servicio de Organizacion Territorial.
     * @param adminCatFacadeService IAdminCatalogosFacade
     */
    public void setAdminCatFacadeService(IAdminCatalogosFacade adminCatFacadeService) {
        this.adminCatFacadeService = adminCatFacadeService;
    }

    /**
     * Lista de marcas disponibles para seleccionar.
     * @return List<Marca>
     */
    public List<Marca> getListaMarcas() {
        return listaMarcas;
    }

    /**
     * Lista de marcas disponibles para seleccionar.
     * @param listaMarcas List<Marca>
     */
    public void setListaMarcas(List<Marca> listaMarcas) {
        this.listaMarcas = listaMarcas;
    }

    /**
     * Lista de modelos disponibles para seleccionar.
     * @return listaModelos
     */
    public List<Modelo> getListaModelos() {
        return listaModelos;
    }

    /**
     * Lista de modelos disponibles para seleccionar.
     * @param listaModelos listaModelos
     */
    public void setListaModelos(List<Modelo> listaModelos) {
        this.listaModelos = listaModelos;
    }

    /**
     * Booleano que nos indica si se edita la central.
     * @return boolean
     */
    public boolean isEditar() {
        return isEditar;
    }

    /**
     * Booleano que nos indica si se edita la central.
     * @param isEditar boolean
     */
    public void setEditar(boolean isEditar) {
        this.isEditar = isEditar;
    }

    /**
     * Objeto central con el que se va a trabajar.
     * @return Central
     */
    public Central getCentral() {
        return central;
    }

    /**
     * Objeto central con el que se va a trabajar.
     * @param central Central
     */
    public void setCentral(Central central) {
        this.central = central;
    }

    /**
     * Referencia al Bean de Solicitud de Asignaciones.
     * @return MarcaBean
     */
    public MarcaBean getMarcaBean() {
        return marcaBean;
    }

    /**
     * Referencia al Bean de Solicitud de Asignaciones.
     * @param marcaBean MarcaBean
     */
    public void setMarcaBean(MarcaBean marcaBean) {
        this.marcaBean = marcaBean;
    }

}
