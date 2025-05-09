package mx.ift.sns.web.backend.validators;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.validate.ClientValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que valida si una cadena es un sna correcto.
 */
@FacesValidator("NumeroInternoValidator")
public class NumeroInternoValidator implements Validator, ClientValidator {

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(NumeroInternoValidator.class);

    @Override
    public Map<String, Object> getMetadata() {
        return null;
    }

    @Override
    public String getValidatorId() {
        return "SerieValidator";
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("valor {}", value);
        }

        String label = (String) component.getAttributes().get("label");

        if (StringUtils.isEmpty((String) value)) {
            return;
        }

        String s = (String) value;

        if (s.length() > 4) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, label + " no valido", ""));
        } else {
            int num = -1;
            try {
                num = Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, label + " no valido", ""));
            }

            if ((num < 0) || (num > 9999)) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, label + " no valido", ""));
            }
        }
    }
}
