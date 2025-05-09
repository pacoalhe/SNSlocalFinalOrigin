package mx.ift.sns.web.backend.ac.claveservicio;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Bean asociado a la creación y edición de claves de servicio. */
@ManagedBean(name = "edicionClaveServicioBean")
@ViewScoped
public class EdicionClaveServicioBean implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EdicionClaveServicioBean.class);

    /** Facade de administración de catálogos. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade adminCatFacadeService;

    /** Identificador del componente de mensajes JSF para la visualización del os mensajes. */
    private static final String MSG_ID = "MSG_EdicionClaveServicio";

    /** Clave de Servicio a editar o crear. */
    private ClaveServicio claveServicio;

    /** Listado de estatus de la clave de servicio. */
    private List<Estatus> listadoEstatus;

    /** Flag que indica si estamos editando una clave de servicio o creando una nueva. */
    private boolean modoEdicion = false;

    /** JSR260, Método invocado al cargar el bean para terminar su instanciación. */
    @PostConstruct
    public void init() {
        try {
            claveServicio = new ClaveServicio();
            listadoEstatus = adminCatFacadeService.findAllEstatus();
            modoEdicion = false;

        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método encargado de cargar la clave de servicio recibida para su edición.
     * @param pClaveServicio Clave de servicio a editar.
     */
    public void cargarClaveServicio(ClaveServicio pClaveServicio) {
        claveServicio = pClaveServicio;
        modoEdicion = true;
    }

    /**
     * Método encargado de limpiar los valores de la edición o creación de la clave de servicio al cerrar la pantalla de
     * edición..
     */
    public void resetValues() {
        claveServicio = new ClaveServicio();
        modoEdicion = false;
    }

    /**
     * Método encargado de guardar la clave de servicio.
     */
    public void guardar() {
        try {
            if (validaClaveServicio()) {
                claveServicio = adminCatFacadeService.guardarClaveServicio(claveServicio, modoEdicion);

                MensajesBean.addInfoMsg(MSG_ID,
                        MensajesBean.getTextoResource("catalogo.clave.servicio.edicion.mensaje"), "");
                modoEdicion = true;
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        }
    }

    /**
     * Funcion que valida que todos los datos necesarios han sido introducidos para la creación o actualización de la
     * clave de servicio.
     * <ul>
     * <li>Código</li>
     * <li>Descripción</li>
     * <li>Estatus</li>
     * </ul>
     * @return boolean validado
     */
    private boolean validaClaveServicio() {
        claveServicio = adminCatFacadeService.validaClaveServicio(claveServicio, modoEdicion);
        if (claveServicio.getErrorValidacion() != null) {
            MensajesBean.addErrorMsg(MSG_ID, MensajesBean.getTextoResource(claveServicio.getErrorValidacion()), "");
            return false;
        }
        return true;
    }

    /**
     * Método que devuelve la clave de servicio que se está editando o creando.
     * @return ClaveServicio
     */
    public ClaveServicio getClaveServicio() {
        return claveServicio;
    }

    /**
     * Método que establece la clave de servicio a editar o crear.
     * @param claveServicio ClaveServicio
     */
    public void setClaveServicio(ClaveServicio claveServicio) {
        this.claveServicio = claveServicio;
    }

    /**
     * Método que devuelve el listado de estatus que puede tener la clave de servicio.
     * @return List<Estatus>
     */
    public List<Estatus> getListadoEstatus() {
        return listadoEstatus;
    }

    /**
     * Método que establece el listado de estatus que puede tener la clave de servicio.
     * @param listadoEstatus List<Estatus>
     */
    public void setListadoEstatus(List<Estatus> listadoEstatus) {
        this.listadoEstatus = listadoEstatus;
    }

    /**
     * Método que devuelve si la clave de servicio se está editando o creando.
     * <ul>
     * <li>True = Edición</li>
     * <li>False = Creación</li>
     * </ul>
     * @return the modoEdicion
     */
    public boolean isModoEdicion() {
        return modoEdicion;
    }

    /**
     * Método que establece el modo en el que se está modificando la clave de servicio.
     * <ul>
     * <li>True = Edición</li>
     * <li>False = Creación</li>
     * </ul>
     * @param modoEdicion the modoEdicion to set
     */
    public void setModoEdicion(boolean modoEdicion) {
        this.modoEdicion = modoEdicion;
    }

}
