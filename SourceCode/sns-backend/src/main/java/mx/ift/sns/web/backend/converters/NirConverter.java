package mx.ift.sns.web.backend.converters;

import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase converter para nir.
 */
@ManagedBean(name = "nirConverter")
@RequestScoped
public class NirConverter implements Converter {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NirConverter.class);

    /** Injection de INumeracionGeograficaService. */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    /**
     * Devuelve la representación Nir de JAVA a partir del valor de un componente JSF.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return provvedor
     * @throws ConverterException converter
     */
    public Object getAsObject(FacesContext context, UIComponent component, String value)
            throws ConverterException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("value={}", value);
        }

        Nir nir = new Nir();
        if (!"".equals(value)) {
            try {
                nir = ngService.getNirById(new BigDecimal(value));
            } catch (Exception e) {
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error("error", e);
                }
            }
        }
        return nir;
    }

    /**
     * Devuelve la representacion cadena de un nir.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return nir
     * @throws ConverterException converter
     */
    public String getAsString(FacesContext context, UIComponent component, Object value)
            throws ConverterException {
        String resultado = "";
        if (value != null) {
            resultado = String.valueOf(((Nir) value).getId().toString());
        }
        return resultado;
    }
}
