package mx.ift.sns.web.backend.converters;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import mx.ift.sns.modelo.lineas.TipoReporte;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;

import org.apache.commons.lang3.StringUtils;

/**
 * Converter de tipo reporte.
 * @author X36155QU
 */
@ManagedBean(name = "tipoReporteConverter")
@RequestScoped
public class TipoReporteConverter implements Converter {

    /**
     * Injection de INumeracionGeograficaService.
     */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        TipoReporte tipoReporte = new TipoReporte();
        if (StringUtils.isNotEmpty(value)) {
            try {
                tipoReporte = ngService.getTipoReporteByCdg(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tipoReporte;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String resultado = "";
        if (value != null) {
            try {
                resultado = String.valueOf(((TipoReporte) value).getCodigo());
            } catch (Exception e) {
                resultado = "";
            }
        }
        return resultado;
    }

}
