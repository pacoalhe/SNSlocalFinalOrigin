package mx.ift.sns.web.backend.converters;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.negocio.ot.IOrganizacionTerritorialService;

import org.apache.commons.lang3.StringUtils;

/**
 * Clase converter para estado.
 * @author 67059279
 */
@ManagedBean(name = "estadoConverter")
@RequestScoped
public class EstadoConverter implements Converter {
    /**
     * Injection de IOrganizacionTerritorialService.
     */
    @EJB(mappedName = "OrganizacionTerritorialService")
    private IOrganizacionTerritorialService otService;

    /**
     * Devuelve la representación estado de JAVA a partir del valor de un componente JSF.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return estado
     * @throws ConverterException converter
     */
    public Object getAsObject(FacesContext context, UIComponent component, String value)
            throws ConverterException {
        Estado estado = new Estado();
        if (StringUtils.isNotEmpty(value)) {
            try {
                String idEstado = value;
                estado = otService.findEstadoById(idEstado);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return estado;
    }

    /**
     * Devuelve la representacion cadena de un estado.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return estado
     * @throws ConverterException converter
     */
    public String getAsString(FacesContext context, UIComponent component, Object value)
            throws ConverterException {
        String resultado = "";
        if (value != null) {
            try {
                resultado = String.valueOf(((Estado) value).getCodEstado());
            } catch (Exception e) {
                resultado = "";
            }
        }
        return resultado;
    }
}
