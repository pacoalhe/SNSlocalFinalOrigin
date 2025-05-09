package mx.ift.sns.web.backend.converters;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import mx.ift.sns.modelo.cpsn.EstatusCPSN;
import mx.ift.sns.negocio.IAdminCatalogosFacade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase converter para estatus de asociada a CAT_ESTATUS_CPSN.
 */
@ManagedBean(name = "estatusCPSNConverter")
@RequestScoped
public class EstatusCPSNConverter implements Converter {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EstatusCPSNConverter.class);

    /** Servicio de Catalogo. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade catalogoService;

    /**
     * Devuelve la representación EstatusCPSN de JAVA a partir del valor de un componente JSF.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return estatusCPSN
     * @throws ConverterException converter
     */
    public Object getAsObject(FacesContext context, UIComponent component, String value)
            throws ConverterException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("value={}", value);
        }

        EstatusCPSN estatus = new EstatusCPSN();
        if (!"".equals(value)) {
            try {
                estatus = catalogoService.getEstatusCPSNById(String.valueOf(value));
            } catch (Exception e) {
                LOGGER.error("error", e);
            }
        }
        return estatus;
    }

    /**
     * Devuelve la representacion cadena de un estatusCPSN.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return estatusCPSN
     * @throws ConverterException converter
     */
    public String getAsString(FacesContext context, UIComponent component, Object value)
            throws ConverterException {
        String resultado = "";
        if (value != null) {
            resultado = ((EstatusCPSN) value).getId();
        }
        return resultado;
    }
}
