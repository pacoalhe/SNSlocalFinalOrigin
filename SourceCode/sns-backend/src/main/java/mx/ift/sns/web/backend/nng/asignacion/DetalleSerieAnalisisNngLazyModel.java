package mx.ift.sns.web.backend.nng.asignacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.filtros.FiltroBusquedaSeries;
import mx.ift.sns.modelo.nng.SerieNng;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;
import mx.ift.sns.web.backend.lazymodels.multiseleccion.ILazyModelRefreshable;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy del detalle de Series para el analisis de Numeración No Geográfica.
 */
public class DetalleSerieAnalisisNngLazyModel extends LazyDataModel<DetalleSerieAnalisisNng> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DetalleSerieAnalisisNngLazyModel.class);

    /** Colección de objetos. */
    private List<DetalleSerieAnalisisNng> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaSeries filtros;

    /** Facade de servicios. */
    private INumeracionNoGeograficaFacade nngFacade;

    /** Gestor de Selección Múltiple sobre la tabla Lazy. */
    private ILazyModelRefreshable multiSelectionManager;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = false;

    /** Indica que se trata de una búsqueda automática usando los filtros de columna. */
    private boolean busquedaFiltros = false;

    /** Constructor. */
    public DetalleSerieAnalisisNngLazyModel() {
        dataSource = new ArrayList<DetalleSerieAnalisisNng>(1);
    }

    @Override
    public DetalleSerieAnalisisNng getRowData(String rowKey) {
        for (DetalleSerieAnalisisNng detalle : dataSource) {
            if (detalle.getSerie().getId().toString().equals(rowKey)) {
                return detalle;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(DetalleSerieAnalisisNng detalle) {
        return detalle.getSerie().getId().toString();
    }

    @Override
    public List<DetalleSerieAnalisisNng> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || nngFacade == null) {

            return new ArrayList<DetalleSerieAnalisisNng>(1);
        }

        try {

            // Si no se utilizan filtros de columna, se puede paginar.
            filtros.setUsarPaginacion(true);
            filtros.setResultadosPagina(pageSize);

            // Si la búsqueda anterior era por filtros hay que reiniciar los contadores. Si no,
            // dejamos el valor que tenga "resetPaginacion" por defecto.
            if (busquedaFiltros) {
                resetPaginacion = true;
                busquedaFiltros = false;
            }

            // Solo se resetea la paginación cuando se pulsa en buscar, no cuando se pagina.
            if (resetPaginacion) {
                filtros.setNumeroPagina(0);
                this.setRowCount(nngFacade.findAllSeriesCount(filtros));
                resetPaginacion = false;
            } else {
                filtros.setNumeroPagina(first);
            }

            // Búsqueda normal sin filtros de columna. Solo con filtros de interfaz.
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(filtros.toString());
            }
            dataSource.clear();
            List<SerieNng> listaSeries = nngFacade.findAllSeries(filtros);

            for (SerieNng serie : listaSeries) {
                DetalleSerieAnalisisNng detalle = new DetalleSerieAnalisisNng();
                detalle.setSerie(serie);
                int ocupacion = nngFacade.getTotalOcupacionSerie(serie);
                detalle.setOcupacion(ocupacion);
                detalle.setDisponible(10000 - ocupacion);
                dataSource.add(detalle);
            }

            // CallBack para el gestor de selección múltiple que gestiona éste LazyModel. Por cada invocación Ajax
            // al método 'load' del LazyModel se invoca al método 'refreshLazyModelSeleccion' implementado en el
            // gestor para que actualice la selección múltiple que se ha de mostrar en la tabla.
            if (multiSelectionManager != null) {
                multiSelectionManager.refreshLazyModelSelection(first, pageSize, null);
            }

            return dataSource;

        } catch (Exception e) {
            LOGGER.error("load: " + e.getMessage());
            dataSource = new ArrayList<DetalleSerieAnalisisNng>(1);
            return dataSource;
        }
    }

    /** Resetea los valores de búsqueda y paginación. */
    public void clear() {
        resetPaginacion = true;
        busquedaFiltros = false;
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
     * @return the filtros
     */
    public FiltroBusquedaSeries getFiltros() {
        return filtros;
    }

    /**
     * Asocia el Facade de Servicios que utilizarán las búsquedas.
     * @param nngFacade INumeracionNoGeograficaFacade
     */
    public void setFacadeServicios(INumeracionNoGeograficaFacade nngFacade) {
        this.nngFacade = nngFacade;
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
