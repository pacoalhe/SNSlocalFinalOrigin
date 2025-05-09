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
@FacesValidator("claveServicioValidator")
public class ClaveServicioValidator implements Validator, ClientValidator {

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClaveServicioValidator.class);

    @Override
    public Map<String, Object> getMetadata() {
        return null;
    }

    @Override
    public String getValidatorId() {
        return "claveServicioValidator";
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("valor {}", value);
        }

        if (StringUtils.isEmpty((String) value)) {
            return;
        }

        int claveServicio = -1;
        try {
            claveServicio = Integer.parseInt((String) value);
        } catch (NumberFormatException e) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    Errores.getDescripcionError(Errores.ERROR_0014), ""));
        }

        if ((claveServicio < 100) || (claveServicio > 999) || (((String) value).length() != 3)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    Errores.getDescripcionError(Errores.ERROR_0014), ""));
        }
    }
}
