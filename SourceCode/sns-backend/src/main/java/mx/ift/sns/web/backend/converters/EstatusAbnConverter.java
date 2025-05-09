package mx.ift.sns.web.backend.converters;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import mx.ift.sns.modelo.abn.EstadoAbn;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Converter para EstadoAbn. */
@ManagedBean(name = "estatusAbnConverter")
@RequestScoped
public class EstatusAbnConverter implements Converter {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EstatusAbnConverter.class);

    /** Servicio de Numeración Geográfica. */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        EstadoAbn estatus = new EstadoAbn();
        if (!"".equals(value)) {
            try {
                estatus = ngService.getEstadoAbnByCodigo(String.valueOf(value));
            } catch (Exception e) {
                LOGGER.error("error", e);
            }
        }
        return estatus;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String resultado = "";
        if (value != null) {
            resultado = String.valueOf(((EstadoAbn) value).getCodigo());
        }
        return resultado;
    }
}
