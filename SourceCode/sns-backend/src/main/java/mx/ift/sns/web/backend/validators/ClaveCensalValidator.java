package mx.ift.sns.web.backend.validators;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import mx.ift.sns.web.backend.common.Errores;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.validate.ClientValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que valida si una cadena es un sna correcto.
 */
@FacesValidator("ClaveCensalValidator")
public class ClaveCensalValidator implements Validator, ClientValidator {

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClaveCensalValidator.class);

    @Override
    public Map<String, Object> getMetadata() {
        return null;
    }

    @Override
    public String getValidatorId() {
        return "ClaveCensalValidator";
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("valor {}", value);
        }

        if (StringUtils.isEmpty((String) value)) {
            return;
        }

        int claveCensal = -1;
        try {
            claveCensal = Integer.parseInt((String) value);
        } catch (NumberFormatException e) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    Errores.getDescripcionError(Errores.ERROR_0009), ""));
        }

        if ((claveCensal < 0) || (claveCensal > 999999999) || (((String) value).length() != 9)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    Errores.getDescripcionError(Errores.ERROR_0009), ""));
        }
    }
}
