package mx.ift.sns.web.backend.converters;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import mx.ift.sns.modelo.cpsi.EstatusCPSI;
import mx.ift.sns.negocio.IAdminCatalogosFacade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase converter para estatus que asociada a CAT_ESTATUS_CPSI.
 * @author X50880SA
 */
@ManagedBean(name = "estatusCPSIConverter")
@RequestScoped
public class EstatusCPSIConverter implements Converter {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EstatusCPSIConverter.class);

    /** Servicio de Catalogo. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade catalogoService;

    /**
     * Devuelve la representación EstatusCPSI de JAVA a partir del valor de un componente JSF.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return estatusCPSI
     * @throws ConverterException converter
     */
    public Object getAsObject(FacesContext context, UIComponent component, String value)
            throws ConverterException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("value={}", value);
        }

        EstatusCPSI estatus = new EstatusCPSI();
        if (!"".equals(value)) {
            try {
                estatus = catalogoService.getEstatusCPSIById(String.valueOf(value));
            } catch (Exception e) {
                LOGGER.error("error", e);
            }
        }
        return estatus;
    }

    /**
     * Devuelve la representacion cadena de un estatusCPSI.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return estatusCPSI
     * @throws ConverterException converter
     */
    public String getAsString(FacesContext context, UIComponent component, Object value)
            throws ConverterException {
        String resultado = "";
        if (value != null) {
            resultado = ((EstatusCPSI) value).getId();
        }
        return resultado;
    }
}
