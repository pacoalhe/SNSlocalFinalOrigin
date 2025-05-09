package mx.ift.sns.web.backend.lazymodels.multiseleccion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestiona la selección múltiple realizada sobre tablas Lazy con paginación y filtros de Columna. Para su correcto
 * funcionamiento <b>es necesario asociar la variable 'seleccionTabla' con el atributo 'selection' de la tabla Lazy en
 * JSF</b>
 * @author X53490DE
 * @param <T> Entidad que gestiona la tabla en las filas. <b> Es necesario que la entidad redefina el método 'equals' (y
 *        hashcode) para que sea capaz de comparar dos entidades correctamente</b>
 */
public class MultiSelectionOnLazyModelWithFiltersManager<T> implements ILazyModelRefreshable, Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiSelectionOnLazyModelWithFiltersManager.class);

    /** Selección de registros sobre la página actual de la tabla. */
    private List<T> seleccionTabla;

    /** Copia de la selección de registros sobre la página actual de la tabla. */
    private List<T> seleccionTablaBackup;

    /** Colección de registros seleccionados en todas las páginas. */
    private List<T> registrosSeleccionados;

    /** Constructor, inicializa las variables. */
    public MultiSelectionOnLazyModelWithFiltersManager() {
        seleccionTabla = new ArrayList<>();
        seleccionTablaBackup = new ArrayList<>();
        registrosSeleccionados = new ArrayList<>();
    }

    /** Resetea las colecciones de registros. */
    public void clear() {
        seleccionTabla.clear();
        seleccionTablaBackup.clear();
        registrosSeleccionados.clear();
    }

    /**
     * Indica si existen registros seleccionados.
     * @return True si no hay registros seleccionados en la tabla.
     */
    public boolean isEmpty() {
        return registrosSeleccionados.isEmpty();
    }

    /**
     * Indica el número total de registros seleccionados.
     * @return Número total de registros seleccionados.
     */
    public int size() {
        return registrosSeleccionados.size();
    }

    /**
     * Actualiza las variables de selección múltiple de registros para mantener la paginación en la tabla Lazy Model.
     * @param pRegistro Rango Seleccionado
     * @param pPagina Página en la tabla
     * @param pSeleccionado True si el registro ha sido seleccionado, False si ha sido deseleccionado.
     * @throws Exception en caso de error
     */
    public void updateSelection(T pRegistro, int pPagina, boolean pSeleccionado) throws Exception {
        if (pSeleccionado) {
            registrosSeleccionados.add(pRegistro);
        } else {
            registrosSeleccionados.remove(pRegistro);
        }
    }

    /**
     * Actualiza las variables de selección múltiple cuando se seleccionan todos los registros para mantener la
     * paginación en la tabla Lazy Model.
     * @param pPagina Página en la tabla
     * @param pSeleccionado True si los registros ha sido seleccionados, False si han sido deseleccionados
     * @throws Exception en caso de error
     */
    public void toggleSelecction(int pPagina, boolean pSeleccionado) {
        if (pSeleccionado) {
            // Al seleccionar, todos los registros seleccionados se actualizan en la lista 'seleccionTabla'
            for (T item : seleccionTabla) {
                if (!registrosSeleccionados.contains(item)) {
                    registrosSeleccionados.add(item);
                }
            }
            // Hacemos una copia de los registros seleccionados para usar con los eventos 'toggle'
            seleccionTablaBackup.clear();
            seleccionTablaBackup.addAll(seleccionTabla);
        } else {
            // Al deseleccionar, la lista 'seleccionTabla' queda vacía. Necesitamos otra lista de respaldo
            // 'seleccionTablaBackup' que nos indique qué registros había seleccionados antes de la deselección.
            for (T item : seleccionTablaBackup) {
                registrosSeleccionados.remove(item);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void refreshLazyModelSelection(int pFirstItem, int pPageSize, Object[] pLazyDataSource) throws Exception {
        // Reconstruimos la selección de la tabla en función de la lista de registros mostrados en la página.
        seleccionTabla.clear();
        seleccionTablaBackup.clear();
        if (pLazyDataSource != null && (!registrosSeleccionados.isEmpty())) {
            for (Object item : pLazyDataSource) {
                T castItem = (T) item;
                if (registrosSeleccionados.contains(castItem)) {
                    seleccionTabla.add(castItem);
                    seleccionTablaBackup.add(castItem);
                }
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Actualizando {} registros seleccionados en página actual.", seleccionTabla.size());
        }
    }

    // GETTERS & SETTERS

    /**
     * Devuelve los registros seleccionados sobre la tabla.
     * @return List
     */
    public List<T> getRegistrosSeleccionados() {
        return registrosSeleccionados;
    }

    /**
     * Colección de registros seleccionados en la tabla. <b>Es necesario asociar la variable 'seleccionTabla' con el
     * atributo 'selection' de la tabla Lazy en JSF</b>
     * @return List
     */
    public List<T> getSeleccionTabla() {
        return seleccionTabla;
    }

    /**
     * Colección de registros seleccionados en la tabla. <b>Es necesario asociar la variable 'seleccionTabla' con el
     * atributo 'selection' de la tabla Lazy en JSF</b>
     * @param seleccionTabla List
     */
    public void setSeleccionTabla(List<T> seleccionTabla) {
        this.seleccionTabla = seleccionTabla;
    }

    /**
     * Colección de registros seleccionados en todas las páginas.
     * @param registrosSeleccionados the registrosSeleccionados to set
     */
    public void setRegistrosSeleccionados(List<T> registrosSeleccionados) {
        this.registrosSeleccionados = registrosSeleccionados;
    }

    /**
     * Copia de la selección de registros sobre la página actual de la tabla.
     * @return the seleccionTablaBackup
     */
    public List<T> getSeleccionTablaBackup() {
        return seleccionTablaBackup;
    }

}
