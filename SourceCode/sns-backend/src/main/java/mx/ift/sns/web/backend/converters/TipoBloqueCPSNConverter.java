package mx.ift.sns.web.backend.converters;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import mx.ift.sns.modelo.cpsn.TipoBloqueCPSN;
import mx.ift.sns.negocio.IAdminCatalogosFacade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase converter para tipo bloque CPSN de asociada a CPSN_TIPO_BLOQUE.
 */
@ManagedBean(name = "tipoBloqueCPSNConverter")
@RequestScoped
public class TipoBloqueCPSNConverter implements Converter {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TipoBloqueCPSNConverter.class);

    /** Servicio de Catalogo. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade catalogoService;

    /**
     * Devuelve la representación TipoBloqueCPSN de JAVA a partir del valor de un componente JSF.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return tipoBloqueCPSN
     * @throws ConverterException converter
     */
    public Object getAsObject(FacesContext context, UIComponent component, String value)
            throws ConverterException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("value={}", value);
        }

        TipoBloqueCPSN tipoBloqueCPSN = new TipoBloqueCPSN();
        if (!"".equals(value)) {
            try {
                tipoBloqueCPSN = catalogoService.getTipoBloqueCPSNById(String.valueOf(value));
            } catch (Exception e) {
                LOGGER.error("error", e);
            }
        }
        return tipoBloqueCPSN;
    }

    /**
     * Devuelve la representacion cadena de un tipoBloqueCPSN.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return tipoBloqueCPSN
     * @throws ConverterException converter
     */
    public String getAsString(FacesContext context, UIComponent component, Object value)
            throws ConverterException {
        String resultado = "";
        if (value != null) {
            resultado = ((TipoBloqueCPSN) value).getId();
        }
        return resultado;
    }
}
