package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.filtros.FiltroBusquedaPlantillas;
import mx.ift.sns.modelo.oficios.Plantilla;
import mx.ift.sns.negocio.IAdminCatalogosFacade;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de Plantillas.
 */
public class PlantillasLazyModel extends LazyDataModel<Plantilla> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PlantillasLazyModel.class);

    /** Colección de objetos. */
    private List<Plantilla> dataSource;

    /** Facade de Servicios.. */
    private IAdminCatalogosFacade facade;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = true;

    /** Filtros de búsqueda. */
    private FiltroBusquedaPlantillas filtros;

    /** Constructor. */
    public PlantillasLazyModel() {
        dataSource = new ArrayList<Plantilla>(1);
    }

    @Override
    public Object getRowKey(Plantilla pPlantiila) {
        return pPlantiila.getId().toString();
    }

    @Override
    public List<Plantilla> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        try {
            // Información de paginación. El resto de filtros
            // viene dado por el bean que instancia el objeto.
            filtros.setUsarPaginacion(true);
            // filtros.setResultadosPagina(pageSize);

            if (resetPaginacion) {
                filtros.setNumeroPagina(0);
                resetPaginacion = false;
                this.setRowCount(facade.findAllPlantillasCount(filtros));
            } else {
                filtros.setNumeroPagina(first);
            }

            dataSource.clear();
            dataSource.addAll(facade.findAllPlantillas(filtros));

            if (LOGGER.isDebugEnabled()) {
                StringBuffer trace = new StringBuffer("Paginando, ");
                trace.append("First(").append(first).append("), ");
                trace.append("PageSize(").append(pageSize).append("), ");
                trace.append("Filters: ").append(filtros.toString());
                LOGGER.debug(trace.toString());
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado: ", e);
            dataSource = new ArrayList<Plantilla>(1);
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
     * @param filtros FiltroBusquedaPlantillas
     */
    public void setFiltros(FiltroBusquedaPlantillas filtros) {
        this.filtros = filtros;
    }

    /**
     * Facade de Servicios.
     * @param facade IAdminCatalogosFacade
     */
    public void setFacade(IAdminCatalogosFacade facade) {
        this.facade = facade;
    }

}
