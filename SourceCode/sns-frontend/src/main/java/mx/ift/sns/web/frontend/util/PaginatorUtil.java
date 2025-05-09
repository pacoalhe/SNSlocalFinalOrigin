package mx.ift.sns.web.frontend.util;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.negocio.IConfiguracionFacade;

import org.primefaces.component.datatable.DataTable;

/**
 * Clase encargada de tratar la paginación de los dataTable de JSF.
 * @author X23016PE
 */
public final class PaginatorUtil {

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
     * @return numRegistrosPorPagina a mostrar
     */
    public static int getRegistrosPorPagina(IConfiguracionFacade configuracionFacade) {
        int registroPorPagina = 15;
        String parametro = configuracionFacade.getParamByName(Parametro.REGISTROS_POR_PAGINA_FRONT);

        if (parametro != null) {
            try {
                registroPorPagina = Integer.parseInt(parametro);
            } catch (NumberFormatException nfe) {
                registroPorPagina = 10;
            }
        }

        return registroPorPagina;
    }

    /**
     * Método que obtiene el número de registros por página.
     * @param numRegistrosPorPagina int
     * @return listado en Strig con formato "número, número" para el rowsPerPageTemplate de los dataTable.
     */
    public static String getGruposRegistrosPorPagina(int numRegistrosPorPagina) {
        List<Integer> listaMultiplosCinco = new ArrayList<Integer>();
        for (int i = 5; i <= 25; i += 5) {
            listaMultiplosCinco.add(i);
        }
        if (numRegistrosPorPagina % 5 != 0 && numRegistrosPorPagina < 25) {
            if (numRegistrosPorPagina < 5) {
                listaMultiplosCinco.add(0, numRegistrosPorPagina);
            } else if (numRegistrosPorPagina > 5 && numRegistrosPorPagina < 10) {
                listaMultiplosCinco.add(1, numRegistrosPorPagina);
            } else if (numRegistrosPorPagina > 10 && numRegistrosPorPagina < 15) {
                listaMultiplosCinco.add(2, numRegistrosPorPagina);
            } else if (numRegistrosPorPagina > 15 && numRegistrosPorPagina < 20) {
                listaMultiplosCinco.add(3, numRegistrosPorPagina);
            } else if (numRegistrosPorPagina > 20 && numRegistrosPorPagina < 25) {
                listaMultiplosCinco.add(4, numRegistrosPorPagina);
            }
        } else if (numRegistrosPorPagina > 25) {
            listaMultiplosCinco.add(numRegistrosPorPagina);
        }
        String stringAux = "";
        for (String letra : listaMultiplosCinco.toString().split("\\[|\\]")) {
            stringAux = stringAux + letra;
        }
        return stringAux;
    }
}
