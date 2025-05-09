package mx.ift.sns.web.backend.converters;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import mx.ift.sns.web.backend.ng.lineasactivas.model.TipoConsultaLineas;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converter de tipo consulta lineas.
 */
@ManagedBean(name = "tipoConsultaLineasConverter")
@RequestScoped
public class TipoConsultaLineasConverter implements Converter {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TipoConsultaLineasConverter.class);

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        TipoConsultaLineas tipoReporte = new TipoConsultaLineas();
        if (StringUtils.isNotEmpty(value)) {
            if (value.equals(TipoConsultaLineas.HISTORICO)) {
                tipoReporte = TipoConsultaLineas.TIPO_HISTORICO;
            } else {
                tipoReporte = TipoConsultaLineas.TIPO_ULTIMO_REPORTE;
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("value='{}' res='{}'", value, tipoReporte.getCdg());
        }

        return tipoReporte;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {

        String resultado = "";
        if (value != null) {
            resultado = String.valueOf(((TipoConsultaLineas) value).getCdg());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("res='{}'", resultado);
        }

        return resultado;
    }
}
