package mx.ift.sns.web.backend.converters;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;

import org.apache.commons.lang3.StringUtils;

/**
 * Clase converter para tipo de red.
 * @author 67059279
 */
@ManagedBean(name = "tipoSolicitudConverter")
@RequestScoped
public class TipoSolicitudConverter implements Converter {
    /**
     * Injection de INumeracionGeograficaService.
     */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    /**
     * Devuelve la representación tipo de red de JAVA a partir del valor de un componente JSF.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return tipo de red
     * @throws ConverterException converter
     */
    public Object getAsObject(FacesContext context, UIComponent component, String value)
            throws ConverterException {
        TipoSolicitud tipoSoli = new TipoSolicitud();
        if (StringUtils.isNotEmpty(value)) {
            try {
                tipoSoli = ngService.getTipoSolicitudById(Integer.valueOf(value));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tipoSoli;
    }

    /**
     * Devuelve la representacion cadena de un tipo de red.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return tipo de red
     * @throws ConverterException converter
     */
    public String getAsString(FacesContext context, UIComponent component, Object value)
            throws ConverterException {
        String resultado = "";
        if (value != null) {
            try {
                resultado = String.valueOf(((TipoSolicitud) value).getCdg());
            } catch (Exception e) {
                resultado = "";
            }
        }
        return resultado;
    }
}
