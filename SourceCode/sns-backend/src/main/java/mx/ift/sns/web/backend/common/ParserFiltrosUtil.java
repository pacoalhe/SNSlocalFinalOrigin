package mx.ift.sns.web.backend.common;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase para parsear el filtro de un Lazy Model a un filtro de busquedas.
 * @author X36155QU
 */
public final class ParserFiltrosUtil {
    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(ParserFiltrosUtil.class);

    /**
     * Constructor.
     */
    private ParserFiltrosUtil() {

    }

    /**
     * Metodo para parsear el filtro de un Lazy Model a un filtro de busquedas.
     * @param obj filtro de busqueda
     * @param filters filtro Lazy Model
     * @return filtro de busqueda
     */
    public static Object parseDatosFiltro(Object obj, Map<String, Object> filters) {
        try {
            for (String key : filters.keySet()) {
                Object value = filters.get(key);
                Field f = obj.getClass().getDeclaredField(key);
                if (f.getName().equalsIgnoreCase(key)) {
                    f.setAccessible(true);
                    if (key.startsWith("is")) {
                        if (value.toString().equalsIgnoreCase("Yes")) {
                            f.set(obj, Boolean.TRUE);
                        } else if (value.toString().equalsIgnoreCase("No")) {
                            f.set(obj, Boolean.FALSE);
                        }
                    } else {
                        if (f.getType().getName().equals("java.math.BigDecimal")) {
                            f.set(obj, new BigDecimal(value.toString()));
                        } else if (f.getType().getName().equals("java.lang.Integer")) {
                            f.set(obj, new Integer(value.toString()));
                        } else if (f.getType().getName().equals("java.lang.Long")) {
                            f.set(obj, new Integer(value.toString()));
                        } else {
                            f.set(obj, value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("error", e);
        }
        return obj;
    }
}
