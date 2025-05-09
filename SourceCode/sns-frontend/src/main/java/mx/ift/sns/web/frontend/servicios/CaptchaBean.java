package mx.ift.sns.web.frontend.servicios;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.web.frontend.common.MensajesFrontBean;

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador del captcha.
 * @author X51461MO
 */
@ManagedBean(name = "captchaBean")
@ViewScoped
public class CaptchaBean implements Serializable {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CaptchaBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultaFront";

    /**
     * Valor del input del captcha.
     */
    private String inputCaptcha;

    // ///////////////////////////MÉTODOS///////////////////////

    /**
     * Método que realiza el submit del formulario 'JSecurityCheck' si el captcha ha sido validado con éxito. Sí no se
     * valida con éxito no se ejecuta éste método.
     */
    public void submit() {
        try {
            if (ProteccionCsrf.checkCsrfToken("Form_captcha:csrfTokenCaptcha")) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Captcha ok y protección CSRF ok");
                }
                RequestContext requestContext = RequestContext.getCurrentInstance();
                requestContext.execute("document.getElementById('FORM_login').submit()");
            } else {
                LOGGER.warn("Excepción de seguridad CSRF detectada");
            }
        } catch (Exception e) {
            LOGGER.error("Error de servicio captcha" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado del servicio");
        }
    }

    // ////////////////////////////////////GETTERS Y SETTERS///////////////////////
    /**
     * Valor del input del captcha.
     * @return the inputCaptcha
     */
    public String getInputCaptcha() {
        return inputCaptcha;
    }

    /**
     * Valor del input del captcha.
     * @param inputCaptcha the inputCaptcha to set
     */
    public void setInputCaptcha(String inputCaptcha) {
        this.inputCaptcha = inputCaptcha;
    }
}
