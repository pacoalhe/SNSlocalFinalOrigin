package mx.ift.sns.web.frontend.common;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase utilizada para mostrar mensajes.
 * @author 67059279
 */
public final class MensajesFrontBean {

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(MensajesFrontBean.class);

    /**
     * Constructor privado.
     */
    private MensajesFrontBean() {
    }

    /**
     * Método que inserta en un p:message determinado una descripcion y un detalle del tipo INFO.
     * @param contenedorMsg donde lo muestra si es un null al global
     * @param desc descripcion
     * @param detail detalle
     */
    public static void addInfoMsg(final String contenedorMsg, final String desc, final String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, desc, detail);
        FacesContext.getCurrentInstance().addMessage(contenedorMsg, message);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("'{}' '{}' '{}'", contenedorMsg, desc, detail);
        }
    }

    /**
     * Método que inserta en un p:message determinado una descripcion y un detalle del tipo INFO.
     * @param contenedorMsg donde lo muestra si es un null al global
     * @param desc descripcion
     */
    public static void addInfoMsg(final String contenedorMsg, final String desc) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, desc, "");
        FacesContext.getCurrentInstance().addMessage(contenedorMsg, message);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("id '{}' desc '{}'", contenedorMsg, desc);
        }
    }

    /**
     * Método que inserta en un p:message determinado una descripcion y un detalle del tipo WARN.
     * @param contenedorMsg donde lo muestra si es un null al global
     * @param desc descripcion
     * @param detail detalle
     */
    public static void addWarningMsg(final String contenedorMsg, final String desc, final String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, desc, detail);
        FacesContext.getCurrentInstance().addMessage(contenedorMsg, message);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("'{}' '{}' '{}'", contenedorMsg, desc, detail);
        }
    }

    /**
     * Método que inserta en un p:message determinado una descripcion y un detalle del tipo ERROR.
     * @param contenedorMsg donde lo muestra si es un null al global
     * @param desc descripcion
     * @param detail detalle
     */
    public static void addErrorMsg(final String contenedorMsg, final String desc, final String detail) {

        if (StringUtils.isEmpty(contenedorMsg)) {
            LOGGER.warn("El contenedor de mensajes no existe.");
        }

        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, desc, detail);
        FacesContext.getCurrentInstance().addMessage(contenedorMsg, message);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("'{}' '{}' '{}'", contenedorMsg, desc, detail);
        }
    }

    /**
     * Método que inserta en un p:message determinado una descripcion y un detalle del tipo ERROR.
     * @param contenedorMsg donde lo muestra si es un null al global
     * @param desc descripcion
     */
    public static void addErrorMsg(final String contenedorMsg, final String desc) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, desc, "");
        FacesContext.getCurrentInstance().addMessage(contenedorMsg, message);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("'{}' '{}'", contenedorMsg, desc);
        }
    }

    /**
     * Método que inserta en un p:message determinado una descripcion y un detalle del tipo ERROR.
     * @param contenedorMsg donde lo muestra si es un null al global
     * @param error error
     */
    public static void addErrorMsg(final String contenedorMsg, final ErroresFront error) {
        String descripcion = ErroresFront.getDescripcionError(error);

        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, descripcion, "");
        FacesContext.getCurrentInstance().addMessage(contenedorMsg, message);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("'{}' 'ERROR_{} {}'", contenedorMsg, error.getCode(), descripcion);
        }
    }

    /**
     * Método que devuelve un texto de applicationMessages.
     * @param cadena cadena a buscar en applicationMessages
     * @return String
     */
    public static String getTextoResource(String cadena) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String baseName = facesContext.getApplication().getMessageBundle();
        ResourceBundle bundle = ResourceBundle.getBundle(baseName, new Locale("es"));

        String res = bundle.getString(cadena);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("cadena='{}' res='{}'", cadena, res);
        }

        return res;
    }
}
