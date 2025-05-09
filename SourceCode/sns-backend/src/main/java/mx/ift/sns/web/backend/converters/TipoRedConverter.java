package mx.ift.sns.web.backend.converters;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;

import org.apache.commons.lang3.StringUtils;

/**
 * Clase converter para tipo de red.
 * @author 67059279
 */
@ManagedBean(name = "tipoRedConverter")
@RequestScoped
public class TipoRedConverter implements Converter {
    /**
     * Injection de INumeracionGeograficaService.
     */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    /**
     * Devuelve la representación tipo de red de JAVA a partir del valor de un componente JSF.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return tipo de red
     * @throws ConverterException converter
     */
    public Object getAsObject(FacesContext context, UIComponent component, String value)
            throws ConverterException {
        TipoRed tipoRed = new TipoRed();
        if (StringUtils.isNotEmpty(value)) {
            try {
                tipoRed = ngService.getTipoRedById(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tipoRed;
    }

    /**
     * Devuelve la representacion cadena de un tipo de red.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return tipo de red
     * @throws ConverterException converter
     */
    public String getAsString(FacesContext context, UIComponent component, Object value)
            throws ConverterException {
        String resultado = "";
        if (value != null) {
            try {
                resultado = String.valueOf(((TipoRed) value).getCdg());
            } catch (Exception e) {
                resultado = "";
            }
        }
        return resultado;
    }
}
