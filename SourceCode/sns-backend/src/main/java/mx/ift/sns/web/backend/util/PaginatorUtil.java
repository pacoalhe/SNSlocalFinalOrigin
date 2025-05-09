package mx.ift.sns.web.backend.util;

import javax.faces.context.FacesContext;

import mx.ift.sns.negocio.IConfiguracionFacade;

import org.primefaces.component.datatable.DataTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase encargada de tratar la paginación de los dataTable de JSF.
 * @author X23016PE
 */
public final class PaginatorUtil {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PaginatorUtil.class);

    /** Constructor vacío. */
    private PaginatorUtil() {
    }

    /**
     * Método que resetea la paginación de el dataTable pasado por parámetro.
     * @param componente a resetear
     * @param numRegistrosPorPagina a mostrar
     */
    public static void resetPaginacion(String componente, int numRegistrosPorPagina) {
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot()
                .findComponent(componente);
        dataTable.reset();
        dataTable.setRows(numRegistrosPorPagina);
    }

    /**
     * Método que obtiene el número de registros por página.
     * @param configuracionFacade Facade de métodos relacionados con la configuración
     * @param parametro a buscar
     * @return numRegistrosPorPagina a mostrar
     */
    public static int getRegistrosPorPagina(IConfiguracionFacade configuracionFacade, String parametro) {
        int registroPorPagina = 10;

        try {
            String param = configuracionFacade.getParamByName(parametro);

            if (param != null) {
                registroPorPagina = Integer.parseInt(param);
            } else {
                registroPorPagina = 10;
            }
        } catch (NumberFormatException nfe) {
            LOGGER.error("No se ha podido realizar el casting del parámetro: " + parametro);
            registroPorPagina = 10;
        }

        return registroPorPagina;
    }

    /**
     * Método encargado de obtener el número de la página a mostrar dependiendo del número de elementos totales y el
     * elemento inicial a mostrar.
     * @param totalElementos de la tabla
     * @param primerElemento a mostrar
     * @param elementosPorPagina de la tabla
     * @return primer elemento a mostrar en la tabla de resultados
     */
    public static int getPagina(int totalElementos, int primerElemento, int elementosPorPagina) {
        if (totalElementos > primerElemento) {
            return primerElemento;
        } else {
            if (primerElemento - elementosPorPagina >= 0) {
                return primerElemento - elementosPorPagina;
            } else {
                return 0;
            }
        }
    }
}
