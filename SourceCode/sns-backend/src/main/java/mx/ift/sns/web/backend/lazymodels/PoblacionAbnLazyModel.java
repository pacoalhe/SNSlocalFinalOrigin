package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.abn.PoblacionAbn;
import mx.ift.sns.modelo.filtros.FiltroBusquedaABNs;
import mx.ift.sns.negocio.IAdminCatalogosFacade;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Implementación LazyModel para consulta Lazy de Poblaciones Abn. */
public class PoblacionAbnLazyModel extends LazyDataModel<PoblacionAbn> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PoblacionAbnLazyModel.class);

    /** Colección de objetos. */
    private List<PoblacionAbn> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaABNs filtros;

    /** Facade de Servicios. */
    private IAdminCatalogosFacade service;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = true;

    /** Constructor. */
    public PoblacionAbnLazyModel() {
        dataSource = new ArrayList<PoblacionAbn>(1);
    }

    @Override
    public PoblacionAbn getRowData(String rowKey) {
        for (PoblacionAbn pobAbn : dataSource) {
            if (pobAbn.getInegi().getInegi().equals(rowKey)) {
                return pobAbn;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(PoblacionAbn pPoblacionAbn) {
        return pPoblacionAbn.getInegi().getInegi();
    }

    @Override
    public List<PoblacionAbn> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || service == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<PoblacionAbn>(1);
        }

        try {
            // Información de paginación. El resto de filtros
            // viene dado por el bean que instancia el objeto.
            filtros.setUsarPaginacion(true);
            // filtros.setResultadosPagina(pageSize);
            if (resetPaginacion) {
                filtros.setNumeroPagina(0);
                resetPaginacion = false;
                this.setRowCount(service.findAllPoblacionesAbnCount(filtros));
            } else {
                filtros.setNumeroPagina(first);
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(filtros.toString());
            }

            dataSource.clear();
            dataSource.addAll(service.findAllPoblacionesAbn(filtros));

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            dataSource = new ArrayList<PoblacionAbn>(1);
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

}
