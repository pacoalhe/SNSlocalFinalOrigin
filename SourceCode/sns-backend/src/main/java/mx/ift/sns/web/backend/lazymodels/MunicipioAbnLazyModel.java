package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mx.ift.sns.modelo.filtros.FiltroBusquedaABNs;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.web.backend.lazymodels.multiseleccion.ILazyModelRefreshable;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de Municipios.
 */
public class MunicipioAbnLazyModel extends LazyDataModel<Municipio> {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EstadoLazyModel.class);

    /** Colección de objetos. */
    private List<Municipio> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaABNs filtros;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = true;

    /** Indica que se trata de una búsqueda automática usando los filtros de columna. */
    private boolean busquedaFiltros = false;

    /** Servicio. */
    private INumeracionGeograficaService service;

    /** Gestor de Selección Múltiple sobre la tabla Lazy. */
    private ILazyModelRefreshable multiSelectionManager;

    /** Constructor. */
    public MunicipioAbnLazyModel() {
        dataSource = new ArrayList<Municipio>(1);
    }

    @Override
    public Municipio getRowData(String rowKey) {
        for (Municipio municipio : dataSource) {
            if (municipio.getId().toString().equals(rowKey)) {
                return municipio;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(Municipio municipio) {
        return municipio.getId().toString();
    }

    @Override
    public List<Municipio> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || service == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<Municipio>(1);
        }

        try {
            // Diferentes tipos de búsqueda en función de los filtros de columna.
            boolean usarFiltroColumnas = (filters != null && !filters.isEmpty());
            if (usarFiltroColumnas) {
                // Si se usan los filtros de columna, no se puede paginar en la búsqueda
                // ya que los filtros se aplican sobre todos los resultados.
                filtros.setUsarPaginacion(false);

                // Al usar filtros de columna, tenemos que cargar todos los municipios que coincidan
                // con los filtros introducidos por el usuario en la búsqueda.
                if (!busquedaFiltros) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(filtros.toString());
                    }
                    // Si es la primera vez que se aplican filtros de columna cargarmos los municipios
                    // solicitados sin paginación.
                    dataSource.clear();
                    dataSource.addAll(service.findAllMunicipiosByAbnAndEstado(filtros));

                    // Indicamos que se ha realizado una búsqueda automática por filtros.
                    busquedaFiltros = true;
                }

                // Contenedor de resultados por filtro de columna sobre la búsqueda
                List<Municipio> dataSourceFiltered = new ArrayList<Municipio>();

                // Pasamos los filtros para cada Rango
                for (Municipio item : dataSource) {
                    boolean match = true;
                    for (Entry<String, Object> filter : filters.entrySet()) {
                        String filterField = filter.getKey();
                        String filterValue = String.valueOf(filter.getValue());
                        if (filterValue != null) {
                            String fieldValue = this.getFieldValue(item, filterField);
                            match = fieldValue.startsWith(filterValue);
                            if (!match) {
                                break;
                            }
                        }
                    }
                    if (match) {
                        dataSourceFiltered.add(item);
                    }
                }

                // Páginas a mostrar en el navegador de la tabla.
                List<Municipio> dataSourceShown;
                int dataSize = dataSourceFiltered.size();
                this.setRowCount(dataSize);
                if (dataSize > filtros.getResultadosPagina()) {
                    try {
                        dataSourceShown = dataSourceFiltered.subList(first, first + filtros.getResultadosPagina());
                    } catch (IndexOutOfBoundsException e) {
                        dataSourceShown = dataSourceFiltered.subList(first,
                                first + (dataSize % filtros.getResultadosPagina()));
                    }
                } else {
                    dataSourceShown = dataSourceFiltered;
                }

                // CallBack para el gestor de selección múltiple que gestiona éste LazyModel. Por cada invocación Ajax
                // al método 'load' del LazyModel se invoca al método 'refreshLazyModelSeleccion' implementado en el
                // gestor para que actualice la selección múltiple que se ha de mostrar en la tabla.
                if (multiSelectionManager != null) {
                    multiSelectionManager.refreshLazyModelSelection(first, filtros.getResultadosPagina(),
                            dataSourceShown.toArray());
                }

                return dataSourceShown;

            } else {
                // Información de paginación. El resto de filtros
                // viene dado por el bean que instancia el objeto.
                filtros.setUsarPaginacion(true);
                // filtros.setResultadosPagina(pageSize);

                // Si la búsqueda anterior era por filtros hay que reiniciar los contadores. Si no,
                // dejamos el valor que tenga "resetPaginacion" por defecto.
                if (busquedaFiltros) {
                    resetPaginacion = true;
                    busquedaFiltros = false;
                }

                // Se saca la consulta del número de elementos para que se realice independientemente de si
                // se está paginando, buscando o eliminando registros de la tabla, de forma que el número de
                // elementos sea siempre el correcto y no pase que se queden páginas inexistentes cuando se
                // eliminan registros.
                this.setRowCount(service.findAllMunicipiosByAbnAndEstadoCount(filtros));

                if (resetPaginacion) {
                    filtros.setNumeroPagina(0);
                    resetPaginacion = false;
                } else {
                    // Se establece la página a mostrar dependiendo de los elementos totales, el primer elemento
                    // a mostrar y el número de elementos por página.
                    filtros.setNumeroPagina(PaginatorUtil.getPagina(this.getRowCount(),
                            first, filtros.getResultadosPagina()));
                }

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(filtros.toString());
                }

                // Búsqueda con paginación
                dataSource.clear();
                dataSource.addAll(service.findAllMunicipiosByAbnAndEstado(filtros));

                // CallBack para el gestor de selección múltiple que gestiona éste LazyModel. Por cada invocación Ajax
                // al método 'load' del LazyModel se invoca al método 'refreshLazyModelSeleccion' implementado en el
                // gestor para que actualice la selección múltiple que se ha de mostrar en la tabla.
                if (multiSelectionManager != null) {
                    multiSelectionManager.refreshLazyModelSelection(first, filtros.getResultadosPagina(),
                            dataSource.toArray());
                }

                return dataSource;
            }
        } catch (Exception e) {
            LOGGER.error("load", e);
            dataSource = new ArrayList<Municipio>(1);
            return dataSource;
        }
    }

    /** Resetea los valores de búsqueda. */
    public void clear() {
        resetPaginacion = true;
        busquedaFiltros = false;
    }

    /**
     * Recupera el valor de un campo específico de un Municipio.
     * @param pMunicipio Información del Municipio
     * @param pCampo Valor del campo a recuperar.
     * @return String con el valor del campo a recuperar.
     */
    public String getFieldValue(Municipio pMunicipio, String pCampo) {
        String fieldValue = "";
        try {
            if (pCampo.equals("estado.codEstado")) {
                fieldValue = pMunicipio.getEstado().getCodEstado();
            } else if (pCampo.equals("estado.nombre")) {
                fieldValue = pMunicipio.getEstado().getNombre();
            } else if (pCampo.equals("id.codMunicipio")) {
                fieldValue = pMunicipio.getId().getCodMunicipio();
            } else if (pCampo.equals("nombre")) {
                fieldValue = pMunicipio.getNombre();
            }
        } catch (NullPointerException npe) {
            fieldValue = "";
        }

        return fieldValue;
    }

    // GETTERS & SETTERS

    /**
     * Colección de objetos.
     * @return dataSource
     */
    public List<Municipio> getDataSource() {
        return dataSource;
    }

    /**
     * Colección de objetos.
     * @param dataSource List<DetalleMunicipio>
     */
    public void setDataSource(List<Municipio> dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Filtros de búsqueda.
     * @return filtros
     */
    public FiltroBusquedaABNs getFiltros() {
        return filtros;
    }

    /**
     * Filtros de búsqueda.
     * @param filtros FiltroBusquedaABNs
     */
    public void setFiltros(FiltroBusquedaABNs filtros) {
        this.filtros = filtros;
    }

    /**
     * Servicio.
     * @return the service
     */
    public INumeracionGeograficaService getService() {
        return service;
    }

    /**
     * Servicio.
     * @param service the service to set
     */
    public void setService(INumeracionGeograficaService service) {
        this.service = service;
    }

    /**
     * ndica si hay que resetear la paginación.
     * @return resetPaginacion
     */
    public boolean isResetPaginacion() {
        return resetPaginacion;
    }

    /**
     * ndica si hay que resetear la paginación.
     * @param resetPaginacion boolean
     */
    public void setResetPaginacion(boolean resetPaginacion) {
        this.resetPaginacion = resetPaginacion;
    }

    /**
     * Gestor de Selección Múltiple sobre la tabla Lazy.
     * @param multiSelectionManager ILazyModelRefreshable
     */
    public void setMultiSelectionManager(ILazyModelRefreshable multiSelectionManager) {
        this.multiSelectionManager = multiSelectionManager;
    }
}
