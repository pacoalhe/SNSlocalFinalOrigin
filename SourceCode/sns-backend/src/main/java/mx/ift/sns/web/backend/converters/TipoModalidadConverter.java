package mx.ift.sns.web.backend.converters;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import mx.ift.sns.modelo.pst.TipoModalidad;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;

import org.apache.commons.lang3.StringUtils;

/**
 * Clase converter para tipo de modalidad.
 * @author 67059279
 */
@ManagedBean(name = "tipoModalidadConverter")
@RequestScoped
public class TipoModalidadConverter implements Converter {
    /**
     * Injection de INumeracionGeograficaService.
     */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    /**
     * Devuelve la representación tipo de modalidad de JAVA a partir del valor de un componente JSF.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return tipo de modalidad
     * @throws ConverterException converter
     */
    public Object getAsObject(FacesContext context, UIComponent component, String value)
            throws ConverterException {
        TipoModalidad tipoModalidad = new TipoModalidad();
        if (StringUtils.isNotEmpty(value)) {
            try {
                tipoModalidad = ngService.getTipoModalidadById(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tipoModalidad;
    }

    /**
     * Devuelve la representacion cadena de un tipo de modalidad.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return tipo de modalidad
     * @throws ConverterException converter
     */
    public String getAsString(FacesContext context, UIComponent component, Object value)
            throws ConverterException {
        String resultado = "";
        if (value != null) {
            try {
                resultado = String.valueOf(((TipoModalidad) value).getCdg());
            } catch (Exception e) {
                resultado = "";
            }
        }
        return resultado;
    }
}
