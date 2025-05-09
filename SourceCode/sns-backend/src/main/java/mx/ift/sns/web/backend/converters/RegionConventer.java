package mx.ift.sns.web.backend.converters;

import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import mx.ift.sns.modelo.ot.Region;
import mx.ift.sns.negocio.ot.IOrganizacionTerritorialService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase converter para Region.
 * @author X36155QU
 */
@ManagedBean(name = "regionConverter")
@RequestScoped
public class RegionConventer implements Converter {

    /**
     * Injection de IOrganizacionTerritorialService.
     */
    @EJB(mappedName = "OrganizacionTerritorialService")
    private IOrganizacionTerritorialService otService;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RegionConventer.class);

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value)
            throws ConverterException {
        LOGGER.debug("");
        Region region = new Region();
        if (StringUtils.isNotEmpty(value)) {
            region = otService.getRegionById(new BigDecimal(value));
        }
        return region;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value)
            throws ConverterException {
        String resultado = "";
        if (value != null) {
            resultado = ((Region) value).getIdRegion().toString();
        }
        return resultado;

    }
}
