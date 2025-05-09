package mx.ift.sns.web.backend.converters;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Converter para la clase EstadoRango. */
@ManagedBean(name = "estatusRangoConverter")
@RequestScoped
public class EstatusRangoConverter implements Converter {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EstatusRangoConverter.class);

    /** Injection de INumeracionGeograficaService. */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    /**
     * Devuelve el objeto EstadoRango de JAVA a partir del valor de un componente JSF.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return estatus
     * @throws ConverterException converter
     */
    public Object getAsObject(FacesContext context, UIComponent component, String value)
            throws ConverterException {

        EstadoRango estatus = new EstadoRango();
        if (!"".equals(value)) {
            try {
                estatus = ngService.getEstadoRangoByCodigo(String.valueOf(value));
            } catch (Exception e) {
                LOGGER.error("error", e);
            }
        }
        return estatus;
    }

    /**
     * Devuelve el código de un objeto EstadoRango.
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
            resultado = String.valueOf(((EstadoRango) value).getCodigo());
        }
        return resultado;
    }
}
