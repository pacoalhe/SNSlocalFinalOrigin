package mx.ift.sns.web.backend.converters;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import mx.ift.sns.modelo.pst.TipoContacto;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Converter para clase TipoContacto. */
@ManagedBean(name = "tipoContactoConverter")
@RequestScoped
public class TipoContactoConverter implements Converter {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TipoContactoConverter.class);

    /** Injection de INumeracionGeograficaService. */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("value={}", value);
        }

        TipoContacto tipoContacto = new TipoContacto();
        if (!"".equals(value)) {
            try {
                tipoContacto = ngService.getTipoContactoByCdg(value);
            } catch (Exception e) {
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error("error", e);
                }
            }
        }
        return tipoContacto;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String resultado = "";
        if (value != null) {
            resultado = String.valueOf(((TipoContacto) value).getCdg());
        }
        return resultado;
    }

}
