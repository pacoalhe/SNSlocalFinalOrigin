package mx.ift.sns.web.backend.converters;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import mx.ift.sns.modelo.pst.TipoServicio;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Converter para clase TipoServicio. */
@ManagedBean(name = "tipoServicioConverter")
@RequestScoped
public class TipoServicioConverter implements Converter {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TipoServicioConverter.class);

    /** Injection de INumeracionGeograficaService. */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    /**
     * Devuelve la representación tipo de servicio de JAVA a partir del valor de un componente JSF.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return tipo de servicio
     * @throws ConverterException converter
     */
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("value={}", value);
        }

        TipoServicio tipoServicio = new TipoServicio();
        if (!StringUtils.isBlank(value)) {
            try {
                tipoServicio = ngService.getTipoServicioById(value);
            } catch (Exception e) {
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error("error", e);
                }
            }
        }
        return tipoServicio;
    }

    /**
     * Devuelve la representacion cadena de un tipo de servicio.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return tipo de servicio
     * @throws ConverterException converter
     */
    public String getAsString(FacesContext context, UIComponent component, Object value)
            throws ConverterException {
        String resultado = "";
        if (value != null && !"".equals(value)) {
            resultado = String.valueOf(((TipoServicio) value).getCdg());
        }
        return resultado;
    }
}
