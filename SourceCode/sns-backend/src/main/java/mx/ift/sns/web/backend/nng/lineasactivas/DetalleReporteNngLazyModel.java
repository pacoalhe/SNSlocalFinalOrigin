package mx.ift.sns.web.backend.nng.lineasactivas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import mx.ift.sns.modelo.filtros.FiltroBusquedaLineasActivas;
import mx.ift.sns.modelo.lineas.DetalleReporteNng;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de Solicitudes de lienas activas.
 */
@Named
public class DetalleReporteNngLazyModel extends LazyDataModel<DetalleReporteNng> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DetalleReporteNngLazyModel.class);

    /** Colección de objetos. */
    private List<DetalleReporteNng> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaLineasActivas filtros;

    /** Fachada. */
    private INumeracionNoGeograficaFacade facade;

    /** Constructor. */
    public DetalleReporteNngLazyModel() {
        dataSource = new ArrayList<DetalleReporteNng>(1);
    }

    @Override
    public DetalleReporteNng getRowData(String rowKey) {
        for (DetalleReporteNng detalle : dataSource) {
            if (detalle.getId().equals(rowKey)) {
                return detalle;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(DetalleReporteNng det) {
        return det.getId();
    }

    @Override
    public List<DetalleReporteNng> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null) {
            LOGGER.error("Error en la carga, los filtros no se han definido.");
            return new ArrayList<DetalleReporteNng>(1);
        }
        if (facade == null) {
            // Si no esta inyectada la fachada no ejecutamos la busqueda
            return new ArrayList<DetalleReporteNng>(1);
        }

        try {
            // Información de paginación. El resto de filtros
            // viene dado por el bean que instancia el objeto.
            filtros.setUsarPaginacion(true);
            filtros.setNumeroPagina(first);
            // filtros.setResultadosPagina(pageSize);

            if (LOGGER.isDebugEnabled()) {
                StringBuffer trace = new StringBuffer("Paginando, ");
                trace.append("First(").append(first).append("), ");
                trace.append("PageSize(").append(pageSize).append("), ");
                trace.append("Filters: ").append(filtros.toString());
                LOGGER.debug(trace.toString());
            }

            dataSource.clear();

            // Búsqueda con paginación
            dataSource = facade.findAllDetalleReporte(filtros);

            this.setRowCount(facade.findAllDetalleReporteCount(filtros));

        } catch (Exception e) {
            LOGGER.error("load: ", e);
            dataSource = new ArrayList<DetalleReporteNng>(1);
        }

        return dataSource;
    }

    /**
     * Asocia los filtros que se usarán en las búsquedas.
     * @param filtros FiltroBusquedaSolicitudes
     */
    public void setFiltros(FiltroBusquedaLineasActivas filtros) {
        this.filtros = filtros;
    }

    /**
     * Fachada.
     * @return the facade
     */
    public INumeracionNoGeograficaFacade getFacade() {
        return facade;
    }

    /**
     * Fachada.
     * @param facade INumeracionNoGeograficaFacade
     */
    public void setFacade(INumeracionNoGeograficaFacade facade) {
        this.facade = facade;
    }
}
