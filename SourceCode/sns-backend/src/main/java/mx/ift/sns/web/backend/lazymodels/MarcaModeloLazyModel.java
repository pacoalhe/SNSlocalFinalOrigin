package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.central.Marca;
import mx.ift.sns.modelo.filtros.FiltroBusquedaMarcaModelo;
import mx.ift.sns.negocio.IAdminCatalogosFacade;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de Marccas y modelos.
 */
public class MarcaModeloLazyModel extends LazyDataModel<Marca> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MarcaModeloLazyModel.class);

    /** Colección de objetos. */
    private List<Marca> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaMarcaModelo filtros;

    /** Facade de Servicios de Cataálogos. */
    private IAdminCatalogosFacade service;

    /** Constructor. */
    public MarcaModeloLazyModel() {
        dataSource = new ArrayList<Marca>(1);
    }

    @Override
    public Marca getRowData(String rowKey) {
        for (Marca marca : dataSource) {
            if (marca.getId().toString().equals(rowKey)) {
                return marca;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(Marca marca) {
        return marca.getId();
    }

    @Override
    public List<Marca> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, Object> filters) {
        LOGGER.error("Lazy loading is not implemented.");
        return null;
    }

    @Override
    /**
     * Recupera las busquedas paginas
     */
    public List<Marca> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || service == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<Marca>(1);
        }

        try {
            // Información de paginación. El resto de filtros
            // viene dado por el bean que instancia el objeto.
            filtros.setUsarPaginacion(true);
            // filtros.setResultadosPagina(pageSize);
            filtros.setNumeroPagina(first);

            if (LOGGER.isDebugEnabled()) {
                StringBuffer trace = new StringBuffer("Paginando, ");
                trace.append("First(").append(first).append("), ");
                trace.append("PageSize(").append(pageSize).append("), ");
                trace.append("Filters: ").append(filtros.toString());
                LOGGER.debug(trace.toString());
            }

            // Búsqueda con paginación
            dataSource.clear();
            // dataSource.addAll(service.findAllModelos(filtros));
            dataSource.addAll(service.findAllMarcasEager(filtros));

            // Numero de resultados totales en la query
            this.setRowCount(service.findAllMarcasCount(filtros));

        } catch (Exception e) {
            LOGGER.error("load: " + e.getMessage());
            dataSource = new ArrayList<Marca>(1);
        }

        return dataSource;
    }

    // GETTERS & SETTERS
    /**
     * Asocia los filtros que se usarán en las búsquedas.
     * @param filtros FiltroBusqueda
     */
    public void setFiltros(FiltroBusquedaMarcaModelo filtros) {
        this.filtros = filtros;
    }

    /**
     * Asocia el servicio que se usarán en las búsquedas.
     * @param service IAdminCatalogosFacade
     */
    public void setService(IAdminCatalogosFacade service) {
        this.service = service;
    }
}
