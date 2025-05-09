package mx.ift.sns.web.backend.ac.cpsn;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.cpsn.EquipoSenalCPSN;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.usu.Usuario;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.negocio.cpsn.IEquipoSenalizacionCPSNService;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.utils.LocalizacionUtil;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.util.UsuarioUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Gestiona la edición y creación de equipos de señalización. */
@ManagedBean(name = "edicionEquipoSenalizacionBean")
@ViewScoped
public class EdicionEquipoSenalizacionBean implements Serializable {
    /** Serializar. **/
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EdicionEquipoSenalizacionBean.class);

    /** Máximo número de cps decimal. */
    private static final int MAX_CPS = 16383;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_EdicionEquipoSenalizacion";

    /** Servicio de Numeración Geográfica. */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    /** Servicio de Catalogo. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade catalogoService;

    /** Servicio de Equipos de señalización Nacional. */
    @EJB(mappedName = "EquipoSenalizacionCPSNService")
    private IEquipoSenalizacionCPSNService equipoCPSNService;

    /** Equipo de señalización. */
    private EquipoSenalCPSN equipoSenal;

    /** Equipo de señalización temporal para la edición. */
    private EquipoSenalCPSN equipoSenalTemp;

    /** Indica si estamos editando un equipo de señalización o creando uno nuevo. */
    private boolean modoEdicion = false;

    /** Organitazión Territorial - Lista de estados. */
    private List<Estado> listaEstados;

    /** Estado. */
    private Estado estado;

    /** Organitazión Territorial - Lista de municipios. */
    private List<Municipio> listaMunicipios;

    /** Municipio. */
    private Municipio municipio;

    /** Organitazión Territorial - Lista de poblaciones. */
    private List<Poblacion> listaPoblaciones;

    /** Poblacion. */
    private Poblacion poblacion;

    /** Clave censal. */
    private String claveCensal;

    /** CPS en binario. */
    private String cpsBinario;

    /** CPS en decimal. */
    private String cpsDecimal;

    /** ABN. */
    private Abn abn;

    /** Inicialización de Variables. JSR260, Método invocado al cargar terminar de instanciar el bean. */
    @PostConstruct
    public void init() {
        try {
            equipoSenal = new EquipoSenalCPSN();
            equipoSenal.setPoblacion(new Poblacion());
            equipoSenal.getPoblacion().setMunicipio(new Municipio());
            equipoSenal.setAbn(new Abn());
            modoEdicion = false;
            estado = null;
            municipio = null;
            poblacion = null;
            abn = new Abn();
            claveCensal = null;
            cpsBinario = null;
            cpsDecimal = null;

            // Inicializaciones las listas con valores fijos
            listaEstados = ngService.findEstados();

            // Listas instanciadas vacías para evitar errores JSF
            listaMunicipios = new ArrayList<Municipio>();
            listaPoblaciones = new ArrayList<Poblacion>();

            equipoSenalTemp = null;

        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al cerrar la modal de Edición de Equipos de Señalización. */
    public void resetValues() {
        equipoSenal = new EquipoSenalCPSN();
        listaMunicipios = new ArrayList<Municipio>();
        listaPoblaciones = new ArrayList<Poblacion>();
        modoEdicion = false;
        estado = null;
        municipio = null;
        poblacion = null;
        abn = new Abn();
        claveCensal = null;
        equipoSenalTemp = null;
        cpsBinario = null;
        cpsDecimal = null;
    }

    /** Método invocado al modificar la clave censal. */
    public void cambioClaveCensal() {
        try {
            if (!StringUtils.isBlank(claveCensal)) {
                if (!claveCensal.matches("-?\\d+(\\.\\d+)?")) {
                    MensajesBean.addErrorMsg(MSG_ID, "La clave censal tiene que ser un valor numerico", "");

                } else {
                    listaMunicipios.clear();
                    listaPoblaciones.clear();

                    Poblacion poblacionAux = ngService.findPoblacionById(claveCensal);
                    if (poblacionAux != null) {
                        equipoSenal.setPoblacion(poblacionAux);
                        equipoSenal.setAbn(poblacionAux.getAbn());
                        poblacion = poblacionAux;
                        municipio = poblacion.getMunicipio();
                        estado = municipio.getEstado();
                        abn = poblacion.getAbn();

                        listaMunicipios.add(poblacionAux.getMunicipio());
                        listaPoblaciones.add(poblacionAux);

                    } else {
                        this.limpiarDatosUbicacion();
                        MensajesBean.addErrorMsg(MSG_ID, "No existe ubicación para la clave censal introducida", "");
                    }
                }
            } else {
                this.limpiarDatosUbicacion();
            }
        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al modificar el campo ABN de Ubicación. */
    public void cambioAbnUbicacion() {
        try {
            if (abn.getCodigoAbn() != null) {
                if (!abn.getCodigoAbn().toString().matches("-?\\d+(\\.\\d+)?")) {
                    MensajesBean.addErrorMsg(MSG_ID, "El ABN tiene que ser un valor numerico", "");

                } else {
                    listaMunicipios.clear();
                    listaPoblaciones.clear();

                    Poblacion poblacionAux = ngService.getPoblacionAnclaByCodigoAbn(abn.getCodigoAbn());
                    if (poblacionAux != null) {
                        equipoSenal.setPoblacion(poblacionAux);
                        equipoSenal.setAbn(poblacionAux.getAbn());
                        poblacion = poblacionAux;
                        municipio = poblacion.getMunicipio();
                        estado = municipio.getEstado();
                        abn = poblacion.getAbn();
                        claveCensal = poblacion.getInegi();

                        listaMunicipios.add(poblacionAux.getMunicipio());
                        listaPoblaciones.add(poblacionAux);
                    } else {
                        this.limpiarDatosUbicacion();
                        MensajesBean.addErrorMsg(MSG_ID, "No existe ubicación para el ABN introducido", "");
                    }
                }
            } else {
                this.limpiarDatosUbicacion();
            }
        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al modificar la selección de Municipio. */
    public void habilitarMunicipio() {
        try {
            if (estado != null) {
                listaMunicipios = ngService.findMunicipiosByEstado(estado.getCodEstado());
            } else {
                listaMunicipios.clear();
            }
            listaPoblaciones.clear();
            equipoSenal.setPoblacion(null);
            equipoSenal.setAbn(null);
            claveCensal = null;
            poblacion = null;
            municipio = null;
            abn = new Abn();
        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al modificar la selección de Población. */
    public void habilitarPoblacion() {
        try {
            if (municipio != null) {
                listaPoblaciones = ngService.findAllPoblaciones(estado.getCodEstado(), municipio.getId()
                        .getCodMunicipio());
            } else {
                listaPoblaciones.clear();
            }
        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Genera la clave censal y el abn de la numeracion solicitada. */
    public void generaClave() {
        if (poblacion != null) {
            equipoSenal.setAbn(poblacion.getAbn());
            abn = poblacion.getAbn();
            claveCensal = poblacion.getInegi();
            equipoSenal.setPoblacion(poblacion);
        } else {
            equipoSenal.setPoblacion(null);
            equipoSenal.setAbn(new Abn());
            claveCensal = null;
            poblacion = null;
            abn = new Abn();
        }
    }

    /** Limpia los campos relacionados con la ubicación. */
    private void limpiarDatosUbicacion() {
        estado = null;
        municipio = null;
        poblacion = null;
        abn = new Abn();
        claveCensal = null;
    }

    /** Función que valida el formato de la latitud del equipo de señalización. */
    public void validarLatitud() {
        equipoSenal.setLatitud(LocalizacionUtil.reemplazarGrado(equipoSenal.getLatitud()));
        if (!LocalizacionUtil.validarLatitud(equipoSenal.getLatitud())) {
            equipoSenal.setLatitud(null);
            MensajesBean.addErrorMsg(MSG_ID, "Formato de latitud no válido", "");
        }
    }

    /** Función que valida el formato de la longitud del equipo de señalización. */
    public void validarLongitud() {
        equipoSenal.setLongitud(LocalizacionUtil.reemplazarGrado(equipoSenal.getLongitud()));
        if (!LocalizacionUtil.validarLongitud(equipoSenal.getLongitud())) {
            equipoSenal.setLongitud(null);
            MensajesBean.addErrorMsg(MSG_ID, "Formato de longitud no válido", "");
        }
    }

    /**
     * Función encargada de guardar el equipo de señalización si todos los datos introducidos son correctos.
     * @throws CloneNotSupportedException ex
     */
    public void guardar() throws CloneNotSupportedException {
        try {
            if (validaEquipoSenalCPSN()) {
                Usuario usuario = (Usuario) UsuarioUtil.getUsuario();
                if (!modoEdicion) {
                    equipoSenal.setUsuarioCreacion(usuario);
                } else {
                    equipoSenal.setUsuarioModificacion(usuario);
                }

                equipoSenal = equipoCPSNService.guardar(equipoSenal, equipoSenalTemp, modoEdicion);

                MensajesBean
                        .addInfoMsg(MSG_ID, MensajesBean.getTextoResource("cpsn.equiposSenalizacion.guardado"), "");
                modoEdicion = true;
                equipoSenalTemp = null;
                equipoSenalTemp = equipoSenal.clone();
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        }
    }

    /** Función que calcula el cps decimal a partir del binario. */
    public void cpsBinarioChange() {
        if (cpsBinario != null && !cpsBinario.isEmpty()) {
            cpsDecimal = String.valueOf(Integer.parseInt(cpsBinario, 2));
            equipoSenal.setCps(Integer.parseInt(cpsBinario, 2));
        } else {
            cpsDecimal = null;
            equipoSenal.setCps(null);
        }
    }

    /** Función que calcula el cps binario a partir del decimal. */
    public void cpsDecimalChange() {
        if (cpsDecimal != null && !cpsDecimal.isEmpty() && Integer.valueOf(cpsDecimal) <= MAX_CPS) {
            cpsBinario = Integer.toBinaryString(Integer.valueOf(cpsDecimal));
            equipoSenal.setCpsBinario(cpsBinario);
            equipoSenal.setCps(Integer.valueOf(cpsDecimal));
        } else if (cpsDecimal == null || cpsDecimal.isEmpty()) {
            MensajesBean.addErrorMsg(MSG_ID, "El valor del CPS es obligatorio.", "");
            cpsBinario = null;
            equipoSenal.setCpsBinario(cpsBinario);
            equipoSenal.setCps(null);
        } else if (Integer.valueOf(cpsDecimal) > MAX_CPS) {
            MensajesBean.addErrorMsg(MSG_ID, "El valor del CPS no puede ser superior a " + MAX_CPS, "");
            cpsBinario = null;
            equipoSenal.setCpsBinario(cpsBinario);
            equipoSenal.setCps(null);
        }
    }

    /**
     * Funcion que valida que todos los datos del equipo de señalización son correctos.
     * @return boolean validado
     */
    private boolean validaEquipoSenalCPSN() {
        equipoSenal.setErrorValidacion(null);
        equipoSenal = equipoCPSNService.validaEquipoSenalCPSN(equipoSenal, modoEdicion);
        if (equipoSenal.getErrorValidacion() != null) {
            MensajesBean.addErrorMsg(MSG_ID, MensajesBean.getTextoResource(equipoSenal.getErrorValidacion()), "");
            return false;
        }
        return true;
    }

    /**
     * Carga el equipo de señalización para edición.
     * @param equipoSenalCPSN Equipo a editar.
     * @throws CloneNotSupportedException ex
     */
    public void cargarEquipo(EquipoSenalCPSN equipoSenalCPSN) throws CloneNotSupportedException {
        equipoSenalCPSN = catalogoService.getEquipoSenalCPSNEagerLoad(equipoSenalCPSN);
        estado = equipoSenalCPSN.getPoblacion().getMunicipio().getEstado();
        habilitarMunicipio();
        municipio = equipoSenalCPSN.getPoblacion().getMunicipio();
        habilitarPoblacion();
        poblacion = equipoSenalCPSN.getPoblacion();
        abn = equipoSenalCPSN.getAbn();
        equipoSenal = equipoSenalCPSN;
        claveCensal = poblacion.getInegi();
        cpsBinario = equipoSenal.getCpsBinario();
        cpsDecimal = equipoSenal.getCps().toString();
        cpsDecimalChange();
        equipoSenalTemp = equipoSenalCPSN.clone();
        modoEdicion = true;
    }

    /**
     * Equipo de señalización.
     * @return EquipoSenalCPSN
     */
    public EquipoSenalCPSN getEquipoSenal() {
        return equipoSenal;
    }

    /**
     * Equipo de señalización.
     * @param equipoSenal EquipoSenalCPSN
     */
    public void setEquipoSenal(EquipoSenalCPSN equipoSenal) {
        this.equipoSenal = equipoSenal;
    }

    /**
     * Indica si estamos editando un equipo de señalización o creando uno nuevo.
     * @return boolean
     */
    public boolean isModoEdicion() {
        return modoEdicion;
    }

    /**
     * Indica si estamos editando un equipo de señalización o creando uno nuevo.
     * @param modoEdicion boolean
     */
    public void setModoEdicion(boolean modoEdicion) {
        this.modoEdicion = modoEdicion;
    }

    /**
     * Organitazión Territorial - Lista de estados.
     * @return List<Estado>
     */
    public List<Estado> getListaEstados() {
        return listaEstados;
    }

    /**
     * Organitazión Territorial - Lista de estados.
     * @param listaEstados List<Estado>
     */
    public void setListaEstados(List<Estado> listaEstados) {
        this.listaEstados = listaEstados;
    }

    /**
     * Organitazión Territorial - Lista de municipios.
     * @return List<Municipio>
     */
    public List<Municipio> getListaMunicipios() {
        return listaMunicipios;
    }

    /**
     * Organitazión Territorial - Lista de municipios.
     * @param listaMunicipios List<Municipio>
     */
    public void setListaMunicipios(List<Municipio> listaMunicipios) {
        this.listaMunicipios = listaMunicipios;
    }

    /**
     * Organitazión Territorial - Lista de poblaciones.
     * @return List<Poblacion>
     */
    public List<Poblacion> getListaPoblaciones() {
        return listaPoblaciones;
    }

    /**
     * Organitazión Territorial - Lista de poblaciones.
     * @param listaPoblaciones List<Poblacion>
     */
    public void setListaPoblaciones(List<Poblacion> listaPoblaciones) {
        this.listaPoblaciones = listaPoblaciones;
    }

    /**
     * Estado.
     * @return Estado
     */
    public Estado getEstado() {
        return estado;
    }

    /**
     * Estado.
     * @param estado Estado
     */
    public void setEstado(Estado estado) {
        this.estado = estado;
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
     * Poblacion.
     * @return Poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * Poblacion.
     * @param poblacion Poblacion
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * Clave censal.
     * @return String
     */
    public String getClaveCensal() {
        return claveCensal;
    }

    /**
     * Clave censal.
     * @param claveCensal String
     */
    public void setClaveCensal(String claveCensal) {
        this.claveCensal = claveCensal;
    }

    /**
     * ABN.
     * @return Abn
     */
    public Abn getAbn() {
        return abn;
    }

    /**
     * ABN.
     * @param abn Abn
     */
    public void setAbn(Abn abn) {
        this.abn = abn;
    }

    /**
     * Equipo de señalización temporal para la edición.
     * @return EquipoSenalCPSN
     */
    public EquipoSenalCPSN getEquipoSenalTemp() {
        return equipoSenalTemp;
    }

    /**
     * Equipo de señalización temporal para la edición.
     * @param equipoSenalTemp EquipoSenalCPSN
     */
    public void setEquipoSenalTemp(EquipoSenalCPSN equipoSenalTemp) {
        this.equipoSenalTemp = equipoSenalTemp;
    }

    /**
     * CPS en binario.
     * @return String
     */
    public String getCpsBinario() {
        return cpsBinario;
    }

    /**
     * CPS en binario.
     * @param cpsBinario String
     */
    public void setCpsBinario(String cpsBinario) {
        this.cpsBinario = cpsBinario;
    }

    /**
     * CPS en decimal.
     * @return String
     */
    public String getCpsDecimal() {
        return cpsDecimal;
    }

    /**
     * CPS en decimal.
     * @param cpsDecimal String
     */
    public void setCpsDecimal(String cpsDecimal) {
        this.cpsDecimal = cpsDecimal;
    }

}
