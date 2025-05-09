package mx.ift.sns.web.backend.validators;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import mx.ift.sns.modelo.nng.SerieNng;
import mx.ift.sns.web.backend.common.Errores;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.validate.ClientValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que valida si una cadena es un sna correcto.
 */
@FacesValidator("SerieNngValidator")
public class SerieNngValidator implements Validator, ClientValidator {

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(SerieNngValidator.class);

    @Override
    public Map<String, Object> getMetadata() {
        return null;
    }

    @Override
    public String getValidatorId() {
        return "SerieNngValidator";
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("valor {}", value);
        }

        if (StringUtils.isEmpty((String) value)) {
            return;
        }

        int sna = -1;
        try {
            sna = Integer.parseInt((String) value);
        } catch (NumberFormatException e) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    Errores.getDescripcionError(Errores.ERROR_0013), ""));
        }

        if ((sna < SerieNng.MIN_SERIE_NNG) || (sna > SerieNng.MAX_SERIE_NNG)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    Errores.getDescripcionError(Errores.ERROR_0013), ""));
        }
    }
}
