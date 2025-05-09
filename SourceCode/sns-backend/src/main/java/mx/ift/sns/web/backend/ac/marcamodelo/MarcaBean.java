package mx.ift.sns.web.backend.ac.marcamodelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.central.Central;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.central.Marca;
import mx.ift.sns.modelo.central.Modelo;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * bean para recoger valores de la Edicion de Marcas y Modelos.
 * @author IECISA
 * @version 1.0
 */
@ManagedBean(name = "marcaBean")
@ViewScoped
public class MarcaBean implements Serializable {

    /** Serial uid. */
    private static final long serialVersionUID = -6977772487921944690L;

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MarcaBean.class);

    /** Id del mensajes de error. */
    private static final String MSG_ID = "MSG_Marca";

    /** Servicio administracion de catalogos. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade service;

    /** Marca editada. **/
    private Marca marca;

    /** Model Nuevo. **/
    private Modelo modeloNuevo = new Modelo();

    /** Marca seleccionado. **/
    private Modelo modeloSeleccionado = new Modelo();

    /** Lista de estados disponibles para seleccionar. */
    private List<Estatus> listaEstados;

    /** Estado seleccionado. **/
    private Estatus estadoSeleccionado = new Estatus();

    /**
     * Iniciamos la pantalla cargando el combo de marcas.
     * @throws Exception error en inicio
     **/
    @PostConstruct
    public void init() throws Exception {

        LOGGER.debug("");

        try {
            marca = new Marca();
            modeloNuevo = new Modelo();
            modeloNuevo.setMarca(marca);
            listaEstados = service.findAllEstatus();

        } catch (Exception ex) {
            LOGGER.error("error", ex);
        }
    }

    /**
     * Carga una marca para su edición.
     * @param idMarca identificador de la marca
     */
    public void cargarMarca(BigDecimal idMarca) {
        this.modeloNuevo = new Modelo();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("marca {}", idMarca);
        }

        try {
            Marca marcaLoaded = service.getMarcaById(idMarca);
            this.setMarca(marcaLoaded);
            listaEstados = service.findAllEstatus();
            this.setListaEstados(listaEstados);
            this.setEstadoSeleccionado(marcaLoaded.getEstatus());

        } catch (Exception e) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Anyade un nuevo modelo al listado.
     */
    public void agregarModelo() {
        if (validaModelo(modeloNuevo)) {
            if (!modeloRepetido()) {
                this.marca.addModelos(modeloNuevo);
                this.modeloNuevo = new Modelo();
            } else {
                MensajesBean.addErrorMsg(MSG_ID, "El tipo de modelo ya existe. Por favor, seleccione otros datos", "");
            }
        } else {
            MensajesBean.addErrorMsg(MSG_ID, "Debe incluir los datos del modelo.", "");
        }
    }

    /**
     * Elimina un modelo del listado.
     */
    public void eliminarModelo() {
        this.marca.removeModelos(modeloSeleccionado);
    }

    /**
     * Persiste los cambios introducidos.
     */
    public void guardarCambios() {
        try {
            if (null != this.marca.getNombre() && !"".equals(marca.getNombre().trim())) {
                if (!existeOtraMarca(this.marca)) {
                    if (this.marca.getId() == null || isEstatusValid(marca)) {
                        marca = service.saveMarca(marca);
                        MensajesBean.addInfoMsg(MSG_ID, "Marca guardada", "");
                        modeloNuevo = new Modelo();
                    } else {
                        MensajesBean.addErrorMsg(MSG_ID,
                                "Modificacion de Estado no permitida por estar modelo asociado a centrales", "");
                    }
                } else {
                    MensajesBean.addErrorMsg(MSG_ID, "Ya existe una marca con el mismo nombre", "");
                }
            } else {
                MensajesBean.addErrorMsg(MSG_ID, "La marca no puede estar vacía", "");
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.error("error", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Validacion de otra marca con el mismo codigo.
     * @param marca a validar
     * @return existencia de otra marca igual
     * @throws Exception excepcion lanzada
     */
    private boolean existeOtraMarca(Marca marca) throws Exception {
        boolean existe = false;
        Marca marcaDB = service.getMarcaByNombre(marca.getNombre());
        if (null != marcaDB && (null == marca.getId() || marcaDB.getId().compareTo(marca.getId()) != 0)) {
            existe = true;
        }
        return existe;
    }

    /**
     * Validacion de marca y modelos inactivos con centrales asociadas.
     * @param marca a validar
     * @return validez de estados de marcas y modelos
     * @throws Exception excepcion lanzada
     */
    private boolean isEstatusValid(Marca marca) throws Exception {
        boolean marcaValida = true;
        List<Modelo> modelos = marca.getModelos();

        for (Modelo modelo : modelos) {
            if (marca.getEstatus().getCdg().equals(Estatus.INACTIVO)
                    || modelo.getEstatus().getCdg().equals(Estatus.INACTIVO)) {
                List<Central> centrales = service.getCentralesActivasByModelo(modelo);
                if (centrales != null && centrales.size() > 0) {
                    marcaValida = false;
                    break;
                }
            }
        }
        return marcaValida;
    }

    /**
     * Validacion de modelo ya existente.
     * @return existencia del modelo
     */
    private boolean modeloRepetido() {
        LOGGER.debug("");

        String rowKey = (String) modeloNuevo.getTipoModelo();
        if (null != marca.getModelos()) {
            for (int i = 0; i < marca.getModelos().size(); i++) {
                Modelo modelo = marca.getModelos().get(i);
                if (rowKey.equalsIgnoreCase(modelo.getTipoModelo())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Valida parametros obligatorios.
     * @param modelo a validar
     * @return modelo válido
     */
    private boolean validaModelo(Modelo modelo) {
        LOGGER.debug("");
        boolean valido = true;
        if (null == modelo || null == modelo.getTipoModelo() || "".equalsIgnoreCase(modelo.getTipoModelo().trim())
                || null == modelo.getDescripcion() || "".equalsIgnoreCase(modelo.getDescripcion().trim())) {
            valido = false;
        }
        return valido;
    }

    /**
     * Servicio administracion de catalogos.
     * @return IAdminCatalogosFacade
     */
    public IAdminCatalogosFacade getService() {
        return service;
    }

    /**
     * Servicio administracion de catalogos.
     * @param service IAdminCatalogosFacade
     */
    public void setService(IAdminCatalogosFacade service) {
        this.service = service;
    }

    /**
     * Marca editada.
     * @return Marca
     */
    public Marca getMarca() {
        return marca;
    }

    /**
     * Marca editada.
     * @param marca Marca
     */
    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    /**
     * Model Nuevo.
     * @return Modelo
     */
    public Modelo getModeloNuevo() {
        return modeloNuevo;
    }

    /**
     * Model Nuevo.
     * @param modeloNuevo Modelo
     */
    public void setModeloNuevo(Modelo modeloNuevo) {
        this.modeloNuevo = modeloNuevo;
    }

    /**
     * Marca seleccionado.
     * @return Modelo
     */
    public Modelo getModeloSeleccionado() {
        return modeloSeleccionado;
    }

    /**
     * Marca seleccionado.
     * @param modeloSeleccionado Modelo
     */
    public void setModeloSeleccionado(Modelo modeloSeleccionado) {
        this.modeloSeleccionado = modeloSeleccionado;
    }

    /**
     * Lista de modelos disponibles para seleccionar.
     * @return listaEstados Catálogo de Modelos
     */
    public List<Estatus> getListaEstados() {
        return listaEstados;
    }

    /**
     * Lista de estados disponibles para seleccionar.
     * @param listaEstados Catálogo de Modelos
     */
    public void setListaEstados(List<Estatus> listaEstados) {
        this.listaEstados = listaEstados;
    }

    /**
     * Estado seleccionado.
     * @return Estatus
     */
    public Estatus getEstadoSeleccionado() {
        return estadoSeleccionado;
    }

    /**
     * Estado seleccionado.
     * @param estadoSeleccionado Estatus
     */
    public void setEstadoSeleccionado(Estatus estadoSeleccionado) {
        this.estadoSeleccionado = estadoSeleccionado;
    }
}
