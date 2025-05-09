package mx.ift.sns.web.backend.converters;

import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;

/**
 * Clase converter para Clave de Servicio.
 * @author X36155QU
 */
@ManagedBean(name = "claveServicioConverter")
@RequestScoped
public class ClaveServicioConverter implements Converter {
    /**
     * Injection de INumeracionNoGeograficaFacade.
     */
    @EJB(mappedName = "NumeracionNoGeograficaFacade")
    private INumeracionNoGeograficaFacade nngFacade;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value)
            throws ConverterException {
        ClaveServicio clave = new ClaveServicio();
        if (!"".equals(value)) {
            try {
                clave = nngFacade.getClaveServicioByCodigo(new BigDecimal(value));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return clave;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value)
            throws ConverterException {
        String resultado = "";
        if (value != null) {
            resultado = String.valueOf(((ClaveServicio) value).getCodigo().toString());
        }
        return resultado;
    }
}
