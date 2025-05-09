package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.ng.SolicitudConsolidacion;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de Solicitudes de Liberación.
 */
public class SolicitudConsolidacionLazyModel extends LazyDataModel<SolicitudConsolidacion> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudConsolidacionLazyModel.class);

    /** Colección de objetos. */
    private List<SolicitudConsolidacion> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaSolicitudes filtros;

    /** Servicio. */
    private INumeracionGeograficaService service;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = true;

    /** Constructor. */
    public SolicitudConsolidacionLazyModel() {
        dataSource = new ArrayList<SolicitudConsolidacion>(1);
    }

    @Override
    public SolicitudConsolidacion getRowData(String rowKey) {
        for (SolicitudConsolidacion solicitud : dataSource) {
            if (solicitud.getId().toString().equals(rowKey)) {
                return solicitud;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(SolicitudConsolidacion pSolicitud) {
        return pSolicitud.getId().toString();
    }

    @Override
    public List<SolicitudConsolidacion> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || service == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<SolicitudConsolidacion>(1);
        }

        try {
            // Información de paginación. El resto de filtros
            // viene dado por el bean que instancia el objeto.
            filtros.setUsarPaginacion(true);
            filtros.setResultadosPagina(pageSize);
            if (resetPaginacion) {
                filtros.setNumeroPagina(0);
                resetPaginacion = false;
                this.setRowCount(service.findAllSolicitudesConsolidacionCount(filtros));
            } else {
                filtros.setNumeroPagina(first);
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(filtros.toString());
            }

            // Búsqueda con paginación
            dataSource.clear();
            dataSource.addAll(service.findAllSolicitudesConsolidacion(filtros));

        } catch (Exception e) {
            LOGGER.error("load: " + e.getMessage());
            dataSource = new ArrayList<SolicitudConsolidacion>(1);
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
     * @param filtros FiltroBusquedaSolicitudes
     */
    public void setFiltros(FiltroBusquedaSolicitudes filtros) {
        this.filtros = filtros;
    }

    /**
     * Asocia el servicio que se usarán en las búsquedas.
     * @param service INumeracionGeograficaService
     */
    public void setService(INumeracionGeograficaService service) {
        this.service = service;
    }
}
