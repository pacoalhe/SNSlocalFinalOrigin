package mx.ift.sns.web.backend.lazymodels;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.filtros.FiltroBusquedaSeries;
import mx.ift.sns.modelo.ng.Serie;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.web.backend.common.ParserFiltrosUtil;
import mx.ift.sns.web.backend.lazymodels.multiseleccion.ILazyModelRefreshable;
import mx.ift.sns.web.backend.ng.asignacion.manual.analisis.NumeracionDisponible;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de Rangos Serie.
 */
public class NumDisponibleLazyModel extends LazyDataModel<NumeracionDisponible> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NumDisponibleLazyModel.class);

    /** Colección de objetos. */
    private List<NumeracionDisponible> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaSeries filtros;

    /** Servicio. */
    private INumeracionGeograficaService service;

    /** Gestor de Selección Múltiple sobre la tabla Lazy. */
    private ILazyModelRefreshable multiSelectionManager;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = false;

    /** Indica si hay que ejecutar o no el lazy. */
    private boolean ejecutar = true;

    /** Constructor. */
    public NumDisponibleLazyModel() {
        dataSource = new ArrayList<NumeracionDisponible>(1);
    }

    @Override
    public NumeracionDisponible getRowData(String rowKey) {
        for (NumeracionDisponible serie : dataSource) {
            if (serie.getSerie().getId().toString().equals(rowKey)) {
                return serie;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(NumeracionDisponible serie) {
        return serie.getSerie().getId().toString();
    }

    @Override
    public List<NumeracionDisponible> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (ejecutar) {
            if (filtros == null || service == null) {
                LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
                return new ArrayList<NumeracionDisponible>(1);
            }
            try {

                filtros.setRangosDisponibles(true);

                // El abn siempre es fijo por que lo mantenemos en una variable auxiliar.
                BigDecimal abnAux = filtros.getIdAbn();

                // Cargamos los filtros
                // Primero limpiamos los especificos de Faces.
                filtros.setCdgNir(null);
                filtros.setDisponible(null);
                filtros.setOcupacion(null);
                filtros.setIdSna(null);
                filtros.setIdAbn(null);

                filtros = (FiltroBusquedaSeries) ParserFiltrosUtil.parseDatosFiltro(filtros, filters);

                // Información de paginación. El resto de filtros
                // viene dado por el bean que instancia el objeto.
                filtros.setUsarPaginacion(true);
                filtros.setResultadosPagina(pageSize);
                filtros.setNirOrder("ASC");
                filtros.setSnaOrder("ASC");
                if (resetPaginacion) {
                    filtros.setNumeroPagina(0);
                    resetPaginacion = false;
                } else {
                    filtros.setNumeroPagina(first);
                }

                if (LOGGER.isDebugEnabled()) {
                    StringBuffer trace = new StringBuffer("Paginando, ");
                    trace.append("First(").append(first).append("), ");
                    trace.append("PageSize(").append(pageSize).append("), ");
                    trace.append("Filters: ").append(filtros.toString());
                    LOGGER.debug(trace.toString());
                }

                if (sortOrder.name().equals("ASCENDING")) {
                    filtros.setNirOrder("ASC");
                } else if (sortOrder.name().equals("DESCENDING")) {
                    filtros.setNirOrder("DESC");
                }

                // Si el filtro de ABN esta vacio restauramos el original
                if (filtros.getIdAbn() == null) {
                    filtros.setIdAbn(abnAux);
                }

                // Si el filtro de ABN sigue siendo igual al original hacemos la busqueda si no vaciamos la tabla
                if (filtros.getIdAbn().compareTo(abnAux) == 0) {
                    // Búsqueda con paginación
                    dataSource.clear();
                    dataSource.addAll(parseNumsDisponible(service.findAllSeries(filtros)));
                    // Numero de resultados totales en la query
                    this.setRowCount(service.findAllSeriesRangosCount(filtros));
                } else {
                    // Recuperamos el ABN original
                    filtros.setIdAbn(abnAux);
                    dataSource = new ArrayList<NumeracionDisponible>(1);
                    this.setRowCount(0);
                }

                // CallBack para el gestor de selección múltiple que gestiona éste LazyModel. Por cada invocación Ajax
                // al método 'load' del LazyModel se invoca al método 'refreshLazyModelSeleccion' implementado en el
                // gestor para que actualice la selección múltiple que se ha de mostrar en la tabla.
                if (multiSelectionManager != null) {
                    multiSelectionManager.refreshLazyModelSelection(first, pageSize, null);
                }

            } catch (Exception e) {
                LOGGER.error("load: " + e.getMessage());
                dataSource = new ArrayList<NumeracionDisponible>(1);
            }
        } else {
            ejecutar = true;
        }
        return dataSource;
    }

    /**
     * @param series seris
     * @return lista
     * @throws Exception error
     */
    private List<NumeracionDisponible> parseNumsDisponible(List<Serie> series) throws Exception {

        List<NumeracionDisponible> numsDisponibles = new ArrayList<NumeracionDisponible>();
        if (filtros.isSerieLibre()) {
            for (int k = 0; k < series.size(); k++) {
                NumeracionDisponible numeracionDisponible = new NumeracionDisponible();

                numeracionDisponible.setSerie(series.get(k));
                numeracionDisponible.setOcupacion(0);
                numeracionDisponible.setDisponible(10000);

                numsDisponibles.add(numeracionDisponible);
            }
        } else {
            for (int k = 0; k < series.size(); k++) {
                NumeracionDisponible numeracionDisponible = new NumeracionDisponible();

                numeracionDisponible.setSerie(series.get(k));
                numeracionDisponible.setOcupacion(service.getTotalNumOcupadaSerie(
                        numeracionDisponible.getSerie().getId()).intValue());
                numeracionDisponible.setDisponible(10000 - numeracionDisponible.getOcupacion());

                numsDisponibles.add(numeracionDisponible);
            }
        }
        return numsDisponibles;
    }

    /** Resetea los valores de búsqueda. */
    public void clear() {
        resetPaginacion = true;
    }

    // GETTERS & SETTERS

    /**
     * Asocia los filtros que se usarán en las búsquedas.
     * @param filtros FiltroBusquedaSeries
     */
    public void setFiltros(FiltroBusquedaSeries filtros) {
        this.filtros = filtros;
    }

    /**
     * Asocia el servicio que se usarán en las búsquedas.
     * @param service INumeracionGeograficaService
     */
    public void setService(INumeracionGeograficaService service) {
        this.service = service;
    }

    /**
     * Indica si hay que ejecutar o no el lazy.
     * @return ejecutar
     */
    public boolean isEjecutar() {
        return ejecutar;
    }

    /**
     * Indica si hay que ejecutar o no el lazy.
     * @param ejecutar boolean
     */
    public void setEjecutar(boolean ejecutar) {
        this.ejecutar = ejecutar;
    }

    /**
     * Gestor de Selección Múltiple sobre la tabla Lazy.
     * @return multiSelectionManager
     */
    public ILazyModelRefreshable getMultiSelectionManager() {
        return multiSelectionManager;
    }

    /**
     * Gestor de Selección Múltiple sobre la tabla Lazy.
     * @param multiSelectionManager multiSelectionManager to set
     */
    public void setMultiSelectionManager(ILazyModelRefreshable multiSelectionManager) {
        this.multiSelectionManager = multiSelectionManager;
    }
}
