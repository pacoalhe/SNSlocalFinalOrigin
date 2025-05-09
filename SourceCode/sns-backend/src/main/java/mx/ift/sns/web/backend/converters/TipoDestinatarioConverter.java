package mx.ift.sns.web.backend.converters;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase Converter para Tipos de Destinatario.
 */
@ManagedBean(name = "tipoDestinatarioConverter")
@RequestScoped
public class TipoDestinatarioConverter implements Converter {

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(TipoDestinatarioConverter.class);

    /** Injection de INumeracionGeograficaService. */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        TipoDestinatario tipoDestinatario = new TipoDestinatario();
        if (StringUtils.isNotEmpty(value)) {
            try {
                tipoDestinatario = ngService.getTipoDestinatarioByCdg(value);
            } catch (Exception e) {
                LOGGER.error("error", e);
            }
        }
        return tipoDestinatario;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String resultado = "";
        if (value != null) {
            try {
                resultado = String.valueOf(((TipoDestinatario) value).getCdg());
            } catch (Exception e) {
                resultado = "";
                LOGGER.error("error", e);
            }
        }
        return resultado;
    }
}
