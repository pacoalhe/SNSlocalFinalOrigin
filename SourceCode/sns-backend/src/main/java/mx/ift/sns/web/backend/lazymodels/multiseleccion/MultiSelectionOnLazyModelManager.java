package mx.ift.sns.web.backend.lazymodels.multiseleccion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestiona la selección múltiple realizada sobre tablas Lazy con paginación. Para su correcto funcionamiento <b>es
 * necesario asociar la variable 'seleccionTabla' con el atributo 'selection' de la tabla Lazy en JSF</b>
 * @author X53490DE
 * @param <T> Entidad que gestiona la tabla en las filas. <b> Es necesario que la entidad redefina el método 'equals' (y
 *        hashcode) para que sea capaz de comparar dos entidades correctamente</b>
 */
public class MultiSelectionOnLazyModelManager<T> implements ILazyModelRefreshable, Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiSelectionOnLazyModelManager.class);

    /** Selección de registros sobre la página actual de la tabla. */
    private List<T> seleccionTabla;

    /** Colección de registros seleccionados en todas las páginas. */
    private List<T> registrosSeleccionados;

    /** Organización de registros seleccionados por página. */
    private HashMap<Integer, ArrayList<T>> seleccionByPagina;

    /** Constructor, inicializa las variables. */
    public MultiSelectionOnLazyModelManager() {
        seleccionTabla = new ArrayList<>();
        registrosSeleccionados = new ArrayList<>();
        seleccionByPagina = new HashMap<>();
    }

    /** Resetea las colecciones de registros. */
    public void clear() {
        seleccionTabla.clear();
        registrosSeleccionados.clear();
        seleccionByPagina.clear();
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
        Integer index = new Integer(pPagina);
        if (pSeleccionado) {
            // Actualizamos la selección en paginación
            if (!seleccionByPagina.containsKey(index)) {
                seleccionByPagina.put(index, new ArrayList<T>());
            }
            seleccionByPagina.get(index).add(pRegistro);
            // Actualizamos la selección total
            registrosSeleccionados.add(pRegistro);
        } else {
            // Actualizamos la selección en paginación
            seleccionByPagina.get(index).remove(pRegistro);
            // Actualizamos la selección total
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
        Integer index = new Integer(pPagina);
        if (!seleccionByPagina.containsKey(index)) {
            seleccionByPagina.put(index, new ArrayList<T>());
        }

        // Limpiamos la selección actual de la página seleccionada.
        for (T registro : seleccionByPagina.get(index)) {
            registrosSeleccionados.remove(registro);
        }
        seleccionByPagina.get(index).clear();

        // Agregamos la página completa.
        if (pSeleccionado) {
            seleccionByPagina.get(index).addAll(seleccionTabla);
            registrosSeleccionados.addAll(seleccionTabla);
        }
    }

    @Override
    public void refreshLazyModelSelection(int pFirstItem, int pPageSize, Object[] pLazyDataSource) throws Exception {
        int page = (pFirstItem / pPageSize); // Devuelve un valor entero sin decimales
        Integer index = new Integer(page);
        if (seleccionByPagina.containsKey(index)) {
            seleccionTabla = seleccionByPagina.get(index);
        } else {
            seleccionTabla = new ArrayList<T>();
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Actualizando {} registros seleccionados en página {}.", seleccionTabla.size(), index);
        }
    }

    /**
     * Devuelve el último registro de la página actual o el último registro de las páginas anteriores. Si no existen
     * registros seleccionados devuelve null
     * @return Null si no existen registros o el último registro de la página actual o anteriores.
     */
    public T getLastRegisterSelected() {
        if (!seleccionTabla.isEmpty()) {
            return seleccionTabla.get(seleccionTabla.size() - 1);
        } else {
            // Si la página actual no tiene ningún registro seleccionado buscamos el registro
            // primer registro existente desde la primera página.
            T registro = null;
            if (!seleccionByPagina.isEmpty()) {
                for (Integer index : seleccionByPagina.keySet()) {
                    if (!seleccionByPagina.get(index).isEmpty()) {
                        registro = seleccionByPagina.get(index).get(seleccionByPagina.get(index).size() - 1);
                        break;
                    }
                }
            }
            return registro;
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
     * @param registrosSeleccionados the registrosSeleccionados to set
     */
    public void setRegistrosSeleccionados(List<T> registrosSeleccionados) {
        this.registrosSeleccionados = registrosSeleccionados;
    }

}
