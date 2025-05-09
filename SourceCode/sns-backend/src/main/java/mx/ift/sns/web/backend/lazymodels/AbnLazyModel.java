package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.filtros.FiltroBusquedaABNs;
import mx.ift.sns.negocio.IAdminCatalogosFacade;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de objetos ABN.
 */
public class AbnLazyModel extends LazyDataModel<Abn> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbnLazyModel.class);

    /** Colección de objetos. */
    private List<Abn> dataSource = new ArrayList<Abn>();

    /** Filtros de Búsqueda. */
    private FiltroBusquedaABNs filtros;

    /** Facade de Servicios. */
    private IAdminCatalogosFacade service;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = true;

    /** Activa el botón exportar si no hay resultados en la búsqueda. */
    private boolean emptySearch = true;

    @Override
    public Abn getRowData(String rowKey) {
        for (Abn abn : dataSource) {
            if (abn.getCodigoAbn().toString().equals(rowKey)) {
                return abn;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(Abn abn) {
        return abn.getCodigoAbn().toString();
    }

    @Override
    public List<Abn> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

        if (filtros == null || service == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<Abn>(1);
        }

        try {
            // Información de paginación. El resto de filtros
            // viene dado por el bean que instancia el objeto.
            filtros.setUsarPaginacion(true);
            filtros.setResultadosPagina(pageSize);
            if (resetPaginacion) {
                filtros.setNumeroPagina(0);
                resetPaginacion = false;
                this.setRowCount(service.findAllAbnsCount(filtros));
            } else {
                filtros.setNumeroPagina(first);
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(filtros.toString());
            }

            dataSource.clear();
            dataSource.addAll(service.findAllAbns(filtros));
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            dataSource = new ArrayList<Abn>(1);
        }

        return dataSource;
    }

    /** Resetea los valores de búsqueda. */
    public void clear() {
        resetPaginacion = true;
    }

    // GETTERS & SETTERS

    /**
     * Asocia los filtros que se usarán en las búsquedas.
     * @param filtros FiltroBusquedaABNs
     */
    public void setFiltros(FiltroBusquedaABNs filtros) {
        this.filtros = filtros;
    }

    /**
     * Asocia el servicio que se usarán en las búsquedas.
     * @param service IAdminCatalogosFacade
     */
    public void setService(IAdminCatalogosFacade service) {
        this.service = service;
    }

    /**
     * Activa el botón exportar si no hay resultados en la búsqueda.
     * @return the emptySearch
     */
    public boolean isEmptySearch() {
        return emptySearch;
    }

}
