package mx.ift.sns.web.backend.ac.poblaciones;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.abn.PoblacionAbn;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.ot.IOrganizacionTerritorialService;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** ManagedBean. */
@ManagedBean(name = "editarPoblacionBean")
@ViewScoped
public class EditarPoblacionBean implements Serializable {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EditarPoblacionBean.class);

    /** Servicio de Administracion de catalogos. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade adminCatFacadeService;

    /** Servicio de oganización territorial. */
    @EJB(mappedName = "OrganizacionTerritorialService")
    private IOrganizacionTerritorialService otservice;

    /** ID mensaje. */
    private static final String MSG_ID = "MSG_Poblacion_Edit";

    /** Lista de estados. */
    private List<Estado> listaEstados;

    /** Lista de municipios. */
    private List<Municipio> listaMunicipios;

    /** Lista de nuevas poblaciones. */
    private List<Poblacion> listaPoblaciones;

    /** Lista de nuevas poblacionesAbn. */
    private List<PoblacionAbn> listaPoblacionesAbn;

    /** Estado. */
    private Estado estadoSeleccionado;

    /** Municipio. */
    private Municipio municipioSeleccionado;

    /** ABN. */
    private String abnSeleccionado;

    /** Poblacion a editar. */
    private Poblacion poblacion;

    /**
     * Iniciamos las listas de estado y region.
     */
    @PostConstruct
    public void init() {

        poblacion = new Poblacion();
        // Inicializa la lista de poblacionesAbn
        listaPoblacionesAbn = new ArrayList<PoblacionAbn>(1);

        try {
            listaEstados = adminCatFacadeService.findAllEstados();
        } catch (Exception e) {
            LOGGER.error("error", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }

    }

    /**
     * Método que realiza la carga de la población seleccionada para editar.
     * @param inegi código único de la población.
     */
    public void cargarPoblacion(String inegi) {
        try {
            Poblacion poblacionSelec = adminCatFacadeService.findPoblacionById(inegi);
            this.setPoblacion(poblacionSelec);
            this.setEstadoSeleccionado(poblacionSelec.getMunicipio().getEstado());
            this.setMunicipioSeleccionado(poblacionSelec.getMunicipio());
            this.setAbnSeleccionado(poblacionSelec.getAbn().getCodigoAbn().toString());
            // Cargamos los campos que no se muestran en el formulario y que deben mantenerse
            PoblacionAbn poblacionAbnAux = new PoblacionAbn();
            poblacionAbnAux.setAbn(poblacionSelec.getAbn());
            poblacionAbnAux.setInegi(poblacionSelec);
            listaPoblacionesAbn.clear();
            listaPoblacionesAbn.add(poblacionAbnAux);
            poblacion.setPoblacionAbn(poblacionSelec.getPoblacionAbn());
            poblacion.setPoblacionesAbn(listaPoblacionesAbn);

        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método que realiza la validación de los datos del formulario de alta de la población.
     * @param pPoblacion objeto de persistencia
     * @return boolean valido
     */
    private boolean validarDatosFormulario(Poblacion pPoblacion) {
        boolean valido = true;
        try {
            // Nombre población: si viene informado se carga en el objeto poblacion
            if (null == this.poblacion.getNombre() || "".equals(this.poblacion.getNombre().trim())) {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("catalogo.poblaciones.valida.nombre.poblacion.error"), "");
                valido = false;
            } else {
                poblacion.setNombre(this.poblacion.getNombre().toUpperCase());
            }
            // ABN: si viene informado se carga en el objeto poblacion
            // Validación Código de ABN disponible.
            if (!StringUtils.isEmpty(this.abnSeleccionado)) {
                if (StringUtils.isNumeric(this.abnSeleccionado)) {
                    if (StringUtils.length(this.abnSeleccionado) <= 3) {
                        // Validamos si el abn informado existe en el sistema
                        Abn abnAux = otservice.getAbnById(new BigDecimal(this.abnSeleccionado));
                        if (abnAux == null) {
                            MensajesBean.addErrorMsg(MSG_ID,
                                    MensajesBean.getTextoResource("catalogo.poblaciones.valida.abn.no.existe.error"),
                                    "");
                            valido = false;
                        } else {
                            poblacion.setAbn(abnAux);
                            PoblacionAbn poblacionAbnAux = new PoblacionAbn();
                            poblacionAbnAux.setAbn(abnAux);
                            poblacionAbnAux.setInegi(poblacion);
                            poblacion.setPoblacionAbn(poblacionAbnAux);
                        }
                    } else {
                        MensajesBean.addErrorMsg(MSG_ID,
                                MensajesBean.getTextoResource("catalogo.poblaciones.valida.abn.formato.error"), "");
                        valido = false;
                    }
                } else {
                    MensajesBean.addErrorMsg(MSG_ID,
                            MensajesBean.getTextoResource("catalogo.poblaciones.valida.abn.numerico.error"), "");
                    valido = false;
                }
            } else {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("catalogo.poblaciones.valida.abn.obligatorio.error"), "");
                valido = false;
            }
        } catch (Exception e) {
            MensajesBean.addErrorMsg(MSG_ID, "Error: " + e.getMessage(), "");
            valido = false;
        }
        return valido;
    }

    /**
     * Guarda las modificaciones de la población.
     */
    public void guardarPoblacionEditada() {

        try {
            // Se llama al método validarDatosFormulario que devolverá el resultado de la validación.
            if (this.validarDatosFormulario(poblacion)) {
                PoblacionAbn pobAux = new PoblacionAbn();
                pobAux.setInegi(poblacion);
                pobAux.setAbn(poblacion.getAbn());

                poblacion = adminCatFacadeService.savePoblacion(poblacion);
                poblacion.setPoblacionAbn(pobAux);
                adminCatFacadeService.savePoblacionAbn(poblacion.getPoblacionAbn());
                MensajesBean.addInfoMsg(MSG_ID, "Poblacion guardada: " + poblacion.getNombre(), "");
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.error("Se ha producido un error al guardar los datos de la población: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, "Error: " + e.getMessage(), "");
        }
    }

    /**
     * Reactiva una población dada de baja.
     */
    public void activarPoblacion() {
        try {
            // Se llama al método validarDatosFormulario que devolverá una poblacion rellena o un null.
            if (this.validarDatosFormulario(poblacion)) {
                PoblacionAbn pobAbn = poblacion.getPoblacionAbn();
                // Se pone el estado de la población a ACTIVO
                Estatus estatus = new Estatus();
                estatus.setCdg(Estatus.ACTIVO);
                poblacion.setEstatus(estatus);
                poblacion = adminCatFacadeService.savePoblacion(poblacion);
                adminCatFacadeService.savePoblacionAbn(pobAbn);
                MensajesBean.addInfoMsg(MSG_ID, "Poblacion reactivada: " + poblacion.getNombre(), "");
            } else {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("catalogo.poblaciones.reactivar.poblacion.error"), "");
            }
        } catch (Exception e) {
            LOGGER.error("Se ha producido un error al reactivar la población: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, "Error: " + e.getMessage(), "");
        }
    }

    /**
     * Restea los datos.
     */
    public void resetCampos() {

        poblacion = new Poblacion();

    }

    /**
     * Lista de estados.
     * @return List<Estado>
     */
    public List<Estado> getListaEstados() {
        return listaEstados;
    }

    /**
     * Lista de estados.
     * @param listaEstados List<Estado>
     */
    public void setListaEstados(List<Estado> listaEstados) {
        this.listaEstados = listaEstados;
    }

    /**
     * Poblacion a editar.
     * @return Poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * Poblacion a editar.
     * @param poblacion Poblacion
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * Lista de municipios.
     * @return List<Municipio>
     */
    public List<Municipio> getListaMunicipios() {
        return listaMunicipios;
    }

    /**
     * Lista de municipios.
     * @param listaMunicipios List<Municipio>
     */
    public void setListaMunicipios(List<Municipio> listaMunicipios) {
        this.listaMunicipios = listaMunicipios;
    }

    /**
     * Lista de nuevas poblaciones.
     * @return List<Poblacion>
     */
    public List<Poblacion> getListaPoblaciones() {
        return listaPoblaciones;
    }

    /**
     * Lista de nuevas poblaciones.
     * @param listaPoblaciones List<Poblacion>
     */
    public void setListaPoblaciones(List<Poblacion> listaPoblaciones) {
        this.listaPoblaciones = listaPoblaciones;
    }

    /**
     * Lista de nuevas poblacionesAbn.
     * @return List<PoblacionAbn>
     */
    public List<PoblacionAbn> getListaPoblacionesAbn() {
        return listaPoblacionesAbn;
    }

    /**
     * Lista de nuevas poblacionesAbn.
     * @param listaPoblacionesAbn List<PoblacionAbn>
     */
    public void setListaPoblacionesAbn(List<PoblacionAbn> listaPoblacionesAbn) {
        this.listaPoblacionesAbn = listaPoblacionesAbn;
    }

    /**
     * Estado.
     * @return Estado
     */
    public Estado getEstadoSeleccionado() {
        return estadoSeleccionado;
    }

    /**
     * Estado.
     * @param estadoSeleccionado Estado
     */
    public void setEstadoSeleccionado(Estado estadoSeleccionado) {
        this.estadoSeleccionado = estadoSeleccionado;
    }

    /**
     * Municipio.
     * @return Municipio
     */
    public Municipio getMunicipioSeleccionado() {
        return municipioSeleccionado;
    }

    /**
     * Municipio.
     * @param municipioSeleccionado Municipio
     */
    public void setMunicipioSeleccionado(Municipio municipioSeleccionado) {
        this.municipioSeleccionado = municipioSeleccionado;
    }

    /**
     * ABN.
     * @return String
     */
    public String getAbnSeleccionado() {
        return abnSeleccionado;
    }

    /**
     * ABN.
     * @param abnSeleccionado String
     */
    public void setAbnSeleccionado(String abnSeleccionado) {
        this.abnSeleccionado = abnSeleccionado;
    }

}
