package mx.ift.sns.web.backend.converters;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.MunicipioPK;
import mx.ift.sns.negocio.ot.IOrganizacionTerritorialService;

import org.apache.commons.lang3.StringUtils;

/**
 * Clase converter para municipio.
 * @author 67059279
 */
@ManagedBean(name = "municipioConverter")
@RequestScoped
public class MunicipioConverter implements Converter {
    /**
     * Injection de IOrganizacionTerritorialService.
     */
    @EJB(mappedName = "OrganizacionTerritorialService")
    private IOrganizacionTerritorialService otService;

    /**
     * Devuelve la representación municipio de JAVA a partir del valor de un componente JSF.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return municipio
     * @throws ConverterException converter
     */
    public Object getAsObject(FacesContext context, UIComponent component, String value)
            throws ConverterException {
        Municipio municipio = new Municipio();
        if (StringUtils.isNotEmpty(value)) {
            try {
                String[] values = value.split("-");
                String idEstado = values[0];
                String idMunicipio = values[1];
                MunicipioPK id = new MunicipioPK();
                id.setCodEstado(idEstado);
                id.setCodMunicipio(idMunicipio);
                municipio = otService.findMunicipioById(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return municipio;
    }

    /**
     * Devuelve la representacion cadena de un municipio.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return municipio
     * @throws ConverterException converter
     */
    public String getAsString(FacesContext context, UIComponent component, Object value)
            throws ConverterException {
        String resultado = "";
        if (value != null) {
            try {
                resultado = String.valueOf(((Municipio) value).getId().toString());
            } catch (Exception e) {
                resultado = "";
            }
        }
        return resultado;
    }
}
