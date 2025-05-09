package mx.ift.sns.web.backend.converters;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import mx.ift.sns.modelo.nng.TipoAsignacion;
import mx.ift.sns.negocio.nng.ISolicitudesNngService;

/**
 * Clase converter para los tipos de asignación.
 * @author X36155QU
 */
@ManagedBean(name = "tipoAsignacionConverter")
@RequestScoped
public class TipoAsignacionConverter implements Converter {

    /**
     * Injection de ISolicitudesNngService.
     */
    @EJB(mappedName = "SolicitudesNngService")
    private ISolicitudesNngService solicitudesNngService;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
        TipoAsignacion tipoAsignacion = new TipoAsignacion();
        if (!"".equals(value)) {
            try {
                tipoAsignacion = solicitudesNngService.getTipoAsignacionById(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tipoAsignacion;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
        String resultado = "";
        if (value != null) {
            resultado = String.valueOf(((TipoAsignacion) value).getCdg());
        }
        return resultado;
    }
}
