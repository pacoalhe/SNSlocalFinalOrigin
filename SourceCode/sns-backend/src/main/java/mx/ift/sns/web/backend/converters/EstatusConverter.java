package mx.ift.sns.web.backend.converters;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase converter para estatus de asociada a CAT_ESTATUS.
 */
@ManagedBean(name = "estatusConverter")
@RequestScoped
public class EstatusConverter implements Converter {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EstatusConverter.class);

    /** Servicio de numeracion geografica. */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    /**
     * Devuelve la representación Estatus de JAVA a partir del valor de un componente JSF.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return estatus
     * @throws ConverterException converter
     */
    public Object getAsObject(FacesContext context, UIComponent component, String value)
            throws ConverterException {

        Estatus estatus = new Estatus();
        if (!"".equals(value)) {
            try {
                estatus = ngService.getEstatusById(String.valueOf(value));
            } catch (Exception e) {
                LOGGER.error("error", e);
            }
        }
        return estatus;
    }

    /**
     * Devuelve la representacion cadena de un estatus.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return estatus
     * @throws ConverterException converter
     */
    public String getAsString(FacesContext context, UIComponent component, Object value)
            throws ConverterException {
        String resultado = "";
        if (value != null) {
            resultado = String.valueOf(((Estatus) value).getCdg());
        }
        return resultado;
    }
}
