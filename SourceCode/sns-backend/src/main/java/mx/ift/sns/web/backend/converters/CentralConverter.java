package mx.ift.sns.web.backend.converters;

import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import mx.ift.sns.modelo.central.Central;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;

/**
 * Clase converter para central.
 * @author 67059279
 */
@ManagedBean(name = "centralConverter")
@RequestScoped
public class CentralConverter implements Converter {
    /**
     * Injection de INumeracionGeograficaService.
     */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    /**
     * Devuelve la representación Central de JAVA a partir del valor de un componente JSF.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return central
     * @throws ConverterException converter
     */
    public Object getAsObject(FacesContext context, UIComponent component, String value)
            throws ConverterException {
        Central central = new Central();
        if (!"".equals(value)) {
            try {
                central = ngService.findCentralById(new BigDecimal(value));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return central;
    }

    /**
     * Devuelve la representacion cadena de un central.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return central
     * @throws ConverterException converter
     */
    public String getAsString(FacesContext context, UIComponent component, Object value)
            throws ConverterException {
        String resultado = "";
        if (value != null) {
            resultado = String.valueOf(((Central) value).getId());
        }
        return resultado;
    }
}
