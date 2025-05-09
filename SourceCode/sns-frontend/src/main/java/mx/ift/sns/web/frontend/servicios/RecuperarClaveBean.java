package mx.ift.sns.web.frontend.servicios;

import java.io.InputStream;
import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.negocio.IConsultaPublicaFacade;
import mx.ift.sns.negocio.usu.IUsuariosService;
import mx.ift.sns.web.frontend.common.MensajesFrontBean;

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador de recuperación de contraseña.
 * @author X51461MO
 */
@ManagedBean(name = "recuperarClaveBean")
@ViewScoped
public class RecuperarClaveBean implements Serializable {

    /** Servicio de Busqueda Publica. */
    @EJB(mappedName = "ConsultaPublicaFacade")
    private IConsultaPublicaFacade ngPublicService;

    /** Servicio de usuarios. */
    @EJB(mappedName = "UsuariosService")
    private IUsuariosService usuariosService;

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RecuperarClaveBean.class);

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultaFront";
    /**
     * idUsuario introducido.
     */
    private String idUsuario;
    /**
     * Email introducido.
     */
    private String email;
    /**
     * Clave nueva.
     */
    private String nuevaClave;
    /**
     * Clave nueva para chequear valided.
     */
    private String nuevaClaveRep;
    /**
     * Flag de activación de botón submit.
     */
    private boolean camposActivados = true;
    /**
     * Flag de activación de inputs.
     */
    private boolean botonVolverActivado = false;

    // ////////////////////////////MÉTODOS/////////////////////////////////
    /**
     * Envía el email de recuperación de contraseña y valida los campos de usuario e email introducidos.
     */
    public void chequeaUsuarioEmail() {

        try {
            InputStream stream = null;
            // ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            // if (classLoader == null) {
            // classLoader = this.getClass().getClassLoader();
            // }
            stream = this.getClass().getResourceAsStream("/ift_logo.png");
            int validacion = 0;
            validacion = usuariosService.recuperaContrasena(this.idUsuario, this.email, stream);
            if (validacion == 1) {
                MensajesFrontBean.addErrorMsg(MSG_ID, "El campo usuario contiene caracteres no válidos");
            } else if (validacion == 2) {
                MensajesFrontBean.addErrorMsg(MSG_ID, "El formato del email introducido no es válido");
            } else if (validacion == 3) {
                MensajesFrontBean.addErrorMsg(MSG_ID, "Datos no válidos");
            } else if (validacion == 0) {
                RequestContext context = RequestContext.getCurrentInstance();
                context.update("FORM_recover:P_containerrecover");
                this.setCamposActivados(false);
                MensajesFrontBean.addInfoMsg(MSG_ID,
                        "Su clave ha sido enviada con éxito. Por favor, consulte su email");
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado al chequear usuario e email" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado del sistema");
        }

    }

    // /////////////////////////////GETTERS Y SETTERS/////////////////////////////

    /**
     * Flag de activación de botón submit.
     * @return the camposActivados
     */
    public boolean isCamposActivados() {
        return camposActivados;
    }

    /**
     * Flag de activación de botón submit.
     * @param camposActivados the camposActivados to set
     */
    public void setCamposActivados(boolean camposActivados) {
        this.camposActivados = camposActivados;
    }

    /**
     * Servicio de usuarios.
     * @return the usuariosService
     */
    public IUsuariosService getUsuariosService() {
        return usuariosService;
    }

    /**
     * Servicio de usuarios.
     * @param usuariosService the usuariosService to set
     */
    public void setUsuariosService(IUsuariosService usuariosService) {
        this.usuariosService = usuariosService;
    }

    /**
     * Flag de activación de inputs.
     * @return the botonVolverActivado
     */
    public boolean isBotonVolverActivado() {
        return botonVolverActivado;
    }

    /**
     * Flag de activación de inputs.
     * @param botonVolverActivado the botonVolverActivado to set
     */
    public void setBotonVolverActivado(boolean botonVolverActivado) {
        this.botonVolverActivado = botonVolverActivado;
    }

    /**
     * Clave nueva.
     * @return the nuevaClave
     */
    public String getNuevaClave() {
        return nuevaClave;
    }

    /**
     * Clave nueva.
     * @param nuevaClave the nuevaClave to set
     */
    public void setNuevaClave(String nuevaClave) {
        this.nuevaClave = nuevaClave;
    }

    /**
     * Clave nueva para chequear valided.
     * @return the nuevaClaveRep
     */
    public String getNuevaClaveRep() {
        return nuevaClaveRep;
    }

    /**
     * Clave nueva para chequear valided.
     * @param nuevaClaveRep the nuevaClaveRep to set
     */
    public void setNuevaClaveRep(String nuevaClaveRep) {
        this.nuevaClaveRep = nuevaClaveRep;
    }

    /**
     * idUsuario introducido.
     * @return the idUsuario
     */
    public String getIdUsuario() {
        return idUsuario;
    }

    /**
     * idUsuario introducido.
     * @param idUsuario the idUsuario to set
     */
    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * Email service.
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Email service.
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
