package mx.ift.sns.web.backend.converters;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.negocio.psts.IProveedoresService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Converter para clase TipoProveedor. */
@ManagedBean(name = "tipoProveedorConverter")
@RequestScoped
public class TipoProveedorConverter implements Converter {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TipoProveedorConverter.class);

    /** Injection de IProveedoresService. */
    @EJB(mappedName = "ProveedoresService")
    private IProveedoresService proveedoresService;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("value={}", value);
        }

        TipoProveedor tipoProveedor = new TipoProveedor();
        if (!"".equals(value)) {
            try {
                tipoProveedor = proveedoresService.getTipoProveedorByCdg(value);
            } catch (Exception e) {
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error("error", e);
                }
            }
        }
        return tipoProveedor;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String resultado = "";
        if (value != null) {
            resultado = String.valueOf(((TipoProveedor) value).getCdg());
        }
        return resultado;
    }

}
