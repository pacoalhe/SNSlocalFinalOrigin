package mx.ift.sns.web.backend.converters;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import mx.ift.sns.negocio.port.TipoFicheroPort;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converter de tipo consulta lineas.
 */
@ManagedBean(name = "tipoFicheroPortConverter")
@RequestScoped
public class TipoFicheroPortConverter implements Converter {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TipoFicheroPortConverter.class);

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        TipoFicheroPort tipoFichero = null;
        if (StringUtils.isNotEmpty(value)) {
            if (value.equals(TipoFicheroPort.DIARIO_PORTED)) {
                tipoFichero = TipoFicheroPort.TIPO_DIARIO_PORTED;
            } else {
                tipoFichero = TipoFicheroPort.TIPO_DIARIO_DELETED;
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("value='{}' res='{}'", value, tipoFichero.getCdg());
        }

        return tipoFichero;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {

        String resultado = "";
        if (value != null) {
            resultado = String.valueOf(((TipoFicheroPort) value).getCdg());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("value='{}' res='{}'", value, resultado);
        }

        return resultado;
    }
}
