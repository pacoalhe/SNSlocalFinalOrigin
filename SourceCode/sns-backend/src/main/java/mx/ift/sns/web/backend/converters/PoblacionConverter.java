package mx.ift.sns.web.backend.converters;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.negocio.ot.IOrganizacionTerritorialService;

import org.apache.commons.lang3.StringUtils;

/**
 * Clase converter para poblacion.
 * @author 67059279
 */
@ManagedBean(name = "poblacionConverter")
@RequestScoped
public class PoblacionConverter implements Converter {
    /**
     * Injection de IOrganizacionTerritorialService.
     */
    @EJB(mappedName = "OrganizacionTerritorialService")
    private IOrganizacionTerritorialService otService;

    /**
     * Devuelve la representación poblaicon de JAVA a partir del valor de un componente JSF.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return poblacion
     * @throws ConverterException converter
     */
    public Object getAsObject(FacesContext context, UIComponent component, String value)
            throws ConverterException {
        Poblacion poblacion = new Poblacion();
        if (StringUtils.isNotEmpty(value)) {
            try {
                String inegi = value;
                poblacion = otService.findPoblacionById(inegi);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return poblacion;
    }

    /**
     * Devuelve la representacion cadena de una poblacion.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return poblacion
     * @throws ConverterException converter
     */
    public String getAsString(FacesContext context, UIComponent component, Object value)
            throws ConverterException {
        String resultado = "";
        if (value != null) {
            try {
                resultado = ((Poblacion) value).getInegi();

            } catch (Exception e) {
                resultado = "";
            }
        }
        return resultado;
    }
}
