package mx.ift.sns.web.backend.converters;

import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import mx.ift.sns.modelo.pst.ProveedorConvenio;
import mx.ift.sns.negocio.psts.IProveedoresService;

/**
 * Clase Converter para Entidad ProveedorConvenio.
 */
@ManagedBean(name = "convenioConverter")
@RequestScoped
public class ConvenioConverter implements Converter {

    /** Injection de IProveedoresService. */
    @EJB(mappedName = "ProveedoresService")
    private IProveedoresService proveedoresService;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
        ProveedorConvenio convenio = new ProveedorConvenio();
        if (!"".equals(value)) {
            try {
                convenio = proveedoresService.getConvenioById(new BigDecimal(value));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return convenio;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
        String resultado = "";
        if (value != null) {
            resultado = String.valueOf(((ProveedorConvenio) value).getId());
        }
        return resultado;
    }

}
