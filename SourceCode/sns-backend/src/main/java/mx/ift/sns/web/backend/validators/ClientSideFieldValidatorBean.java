package mx.ift.sns.web.backend.validators;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import mx.ift.sns.web.backend.common.MensajesBean;

import org.apache.commons.lang3.StringUtils;

/**
 * Backing Bean para validaciones de campos Client-Side.
 * <p>
 * Ejemplo uso: validator="#{clientSideFieldValidatorBean.checkEnteroPositivo}" en los p:inputText
 * @author X53490DE
 */
@ManagedBean(name = "clientSideFieldValidatorBean")
@SessionScoped
public class ClientSideFieldValidatorBean implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /**
     * Construye un objeto FacesMessage para mostrar información en los componentes p:message.
     * @param pSummary Texto para sumario.
     * @param pDetail Texto para detalle.
     * @return FacesMessage
     */
    private FacesMessage getErrorMessage(String pSummary, String pDetail) {
        FacesMessage message = new FacesMessage();
        message.setSeverity(FacesMessage.SEVERITY_ERROR);
        message.setSummary(pSummary);
        message.setDetail(pDetail);
        return message;
    }

    /**
     * Si existe, devuelve la etiqueta asociada al componente. Si no, devuelve el Identificador.
     * @param pComponent Componente JSF.
     * @return String con el identificador del componente.
     */
    private String getComponentIdentifier(UIComponent pComponent) {
        // if (pComponent.getAttributes().containsKey("label")) { // No funciona
        String label = String.valueOf(pComponent.getAttributes().get("label"));
        if (StringUtils.isEmpty(label)) {
            return pComponent.getClientId();
        } else {
            return label;
        }
    }

    /**
     * Construye un objeto FacesMessage con el identificador del componente y el texto de Bundle indicado.
     * @param pComponent Componente JSF.
     * @param pResourceText Identificador del texto de Messages Bundel.
     * @return Objeto FacesMessage
     */
    private FacesMessage getSummaryMessage(UIComponent pComponent, String pResourceText) {
        StringBuilder sbMensaje = new StringBuilder(this.getComponentIdentifier(pComponent));
        sbMensaje.append(": ").append(MensajesBean.getTextoResource(pResourceText));
        FacesMessage mensaje = getErrorMessage(sbMensaje.toString(), "");
        return mensaje;
    }

    /**
     * Validador para numéros enteros positivos. <b>Para evitar las validaciones de JSF el campo debería estar asociado
     * a una variable String.</b> La validación es correcta siempre y cuando: <li>El valor del componente sea nulo. <li>
     * El valor del componente sea un valor entero positivo. <li>El valor del componente sea cero.</li>
     * <p>
     * En el resto de casos la validación se considerará incorrecta y se mostrará un mensaje específico en el componente
     * p:messages del formulario.
     * @author X53490DE
     * @param context FacesContext de JSF.
     * @param component UIComponent de JSF.
     * @param value Valor del componente.
     * @throws ValidatorException En caso de que la validación haya sido fallida.
     */
    public void checkEnteroPositivo(FacesContext context, UIComponent component, Object value)
            throws ValidatorException {

        // Si el valor no puede ser nulo se debe controlar con el atríbuto 'Required' en el propio tag.
        if (value == null) {
            return;
        }

        String strValue = String.valueOf(value);
        if (StringUtils.isEmpty(strValue)) {
            return;
        }

        Integer intValue = null;
        try {
            intValue = Integer.parseInt(strValue);
        } catch (Exception ex) {
            throw new ValidatorException(this.getSummaryMessage(component, "validacion.error.numerico"));
        }

        if (intValue < 0) {
            throw new ValidatorException(this.getSummaryMessage(component, "validacion.error.numerico.positivo"));
        }
    }

    /**
     * Validador para numéros enteros positivos mayores que cero. <b>Para evitar las validaciones de JSF el campo
     * debería estar asociado a una variable String.</b> La validación es correcta siempre y cuando: <li>El valor del
     * componente sea nulo. <li>El valor del componente sea un valor entero positivo.</li>
     * <p>
     * En el resto de casos la validación se considerará incorrecta y se mostrará un mensaje específico en el componente
     * p:messages del formulario.
     * @author X53490DE
     * @param context FacesContext de JSF.
     * @param component UIComponent de JSF.
     * @param value Valor del componente.
     * @throws ValidatorException En caso de que la validación haya sido fallida.
     */
    public void checkEnteroPositivoMayorCero(FacesContext context, UIComponent component, Object value)
            throws ValidatorException {

        // Si el valor no puede ser nulo se debe controlar con el atríbuto 'Required' en el propio tag.
        if (value == null) {
            return;
        }

        String strValue = String.valueOf(value);
        if (StringUtils.isEmpty(strValue)) {
            return;
        }

        Integer intValue = null;
        try {
            intValue = Integer.parseInt(strValue);
        } catch (Exception ex) {
            throw new ValidatorException(this.getSummaryMessage(component, "validacion.error.numerico"));
        }

        if (intValue <= 0) {
            throw new ValidatorException(this.getSummaryMessage(component,
                    "validacion.error.numerico.positivo.mayorcero"));
        }
    }

    /**
     * Validador para el formato decimal de los códigos cps internacionales. <b>Para evitar las validaciones de JSF el
     * campo debería estar asociado a una variable String.</b> La validación es correcta siempre y cuando: <li>El valor
     * del componente sea nulo. <li>El valor del componente tenga el formato x-xxx-x donde x en un dígito.</li>
     * <p>
     * En el resto de casos la validación se considerará incorrecta y se mostrará un mensaje específico en el componente
     * p:messages del formulario.
     * @author X53490PR
     * @param context FacesContext de JSF.
     * @param component UIComponent de JSF.
     * @param value Valor del componente.
     * @throws ValidatorException En caso de que la validación haya sido fallida.
     */
    public void checkFormatoDecimal(FacesContext context, UIComponent component, Object value)
            throws ValidatorException {

        /** Expresion regular que valida el formato decimal. */
        String regexFormatoDecimal = "^[0-9]-[0-9]([0-9]?[0-9]?)-[0-9]$";

        // Si el valor no puede ser nulo se debe controlar con el atríbuto 'Required' en el propio tag.
        if (value == null) {
            return;
        }

        String strValue = String.valueOf(value);
        if (StringUtils.isEmpty(strValue)) {
            return;
        }

        String formatoDecimal = null;
        formatoDecimal = value.toString();
        if (!formatoDecimal.matches(regexFormatoDecimal)) {
            throw new ValidatorException(this.getSummaryMessage(component, "validacion.error.formatoDecimal"));
        }
    }
}
