package mx.ift.sns.web.backend.converters;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import mx.ift.sns.modelo.oficios.TipoRol;

/**
 * Clase Converter para Tipos de Rol.
 */
@ManagedBean(name = "tipoRolConverter")
@RequestScoped
public class TipoRolConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return new TipoRol(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value instanceof TipoRol) {
            return ((TipoRol) value).getCdg();
        } else {
            return "";
        }
    }
}
