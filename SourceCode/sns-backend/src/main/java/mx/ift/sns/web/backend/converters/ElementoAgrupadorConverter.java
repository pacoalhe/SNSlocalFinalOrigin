package mx.ift.sns.web.backend.converters;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import mx.ift.sns.modelo.reporteador.ElementoAgrupador;

/**
 * Clase converter para elemento agrupador.
 */
@ManagedBean(name = "elementoAgrupadorConverter")
@RequestScoped
public class ElementoAgrupadorConverter implements Converter {

    /**
     * Devuelve la representación ElementoAgrupador de JAVA a partir del valor de un componente JSF.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return central
     * @throws ConverterException converter
     */
    public Object getAsObject(FacesContext context, UIComponent component, String value)
            throws ConverterException {
        ElementoAgrupador elementoAgrupador = new ElementoAgrupador();
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        if (!"".equals(value)) {
            try {
                if (value.equalsIgnoreCase(ElementoAgrupador.COD_PST)) {
                    elementoAgrupador.setDescripcion(ElementoAgrupador.DESC_PST);
                } else if (value.equalsIgnoreCase(ElementoAgrupador.COD_ESTADO)) {
                    elementoAgrupador.setDescripcion(ElementoAgrupador.DESC_ESTADO);
                } else if (value.equalsIgnoreCase(ElementoAgrupador.COD_MUNICIPIO)) {
                    elementoAgrupador.setDescripcion(ElementoAgrupador.DESC_MUNICIPIO);
                } else if (value.equalsIgnoreCase(ElementoAgrupador.COD_POBLACION)) {
                    elementoAgrupador.setDescripcion(ElementoAgrupador.DESC_POBLACION);
                } else if (value.equalsIgnoreCase(ElementoAgrupador.COD_ABN)) {
                    elementoAgrupador.setDescripcion(ElementoAgrupador.DESC_ABN);
                }
                elementoAgrupador.setCodigo(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return elementoAgrupador;
    }

    /**
     * Devuelve la representacion cadena de un elemento agrupador.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return elemento agrupador
     * @throws ConverterException converter
     */
    public String getAsString(FacesContext context, UIComponent component, Object value)
            throws ConverterException {
        String resultado = "";
        if (value != null) {
            try {
                resultado = String.valueOf(((ElementoAgrupador) value).getCodigo());
            } catch (Exception e) {
                resultado = "";
            }
        }
        return resultado;
    }
}
