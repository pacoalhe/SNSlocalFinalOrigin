package mx.ift.sns.web.backend.converters;

import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.negocio.psts.IProveedoresService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase converter para proveedor.
 * @author 67059279
 */
@ManagedBean(name = "proveedorConverter")
@RequestScoped
public class ProveedorConverter implements Converter {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProveedorConverter.class);

    /** Injection de IProveedoresService. */
    @EJB(mappedName = "ProveedoresService")
    private IProveedoresService proveedoresService;

    /**
     * Devuelve la representación Proveedor de JAVA a partir del valor de un componente JSF.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return provvedor
     * @throws ConverterException converter
     */
    public Object getAsObject(FacesContext context, UIComponent component, String value)
            throws ConverterException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("value={}", value);
        }

        Proveedor proveedor = new Proveedor();
        if (!"".equals(value)) {
            try {
                proveedor = proveedoresService.getProveedorById(new BigDecimal(value));
            } catch (Exception e) {
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error("error", e);
                }
            }
        }
        return proveedor;
    }

    /**
     * Devuelve la representacion cadena de un proveedor.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return proveedor
     * @throws ConverterException converter
     */
    public String getAsString(FacesContext context, UIComponent component, Object value)
            throws ConverterException {
        String resultado = "";
        if (value != null) {
            resultado = String.valueOf(((Proveedor) value).getId());
        }
        return resultado;
    }
}
