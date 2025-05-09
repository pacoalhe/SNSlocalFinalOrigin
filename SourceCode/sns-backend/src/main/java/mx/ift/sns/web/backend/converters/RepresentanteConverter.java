package mx.ift.sns.web.backend.converters;

import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import mx.ift.sns.modelo.pst.Contacto;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;

/**
 * Clase converter para representanteLegal.
 * @author 67059279
 */
@ManagedBean(name = "representanteconverter")
@RequestScoped
public class RepresentanteConverter implements Converter {
    /**
     * Injection de INumeracionGeograficaService.
     */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    /**
     * Devuelve la representación contacto de JAVA a partir del valor de un componente JSF.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return contacto
     * @throws ConverterException converter
     */
    public Object getAsObject(FacesContext context, UIComponent component, String value)
            throws ConverterException {
        Contacto representante = new Contacto();
        if (!"".equals(value)) {
            try {
                representante = ngService.getRepresentanteLegalById(new BigDecimal(value));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return representante;
    }

    /**
     * Devuelve la representacion cadena de un contacto.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return contacto
     * @throws ConverterException converter
     */
    public String getAsString(FacesContext context, UIComponent component, Object value)
            throws ConverterException {
        String resultado = "";
        if (value != null) {
            try {
                resultado = String.valueOf(((Contacto) value).getId());
            } catch (Exception e) {
                resultado = "";
            }
        }
        return resultado;
    }
}
