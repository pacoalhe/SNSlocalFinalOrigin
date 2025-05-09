package mx.ift.sns.web.backend.ng.historico;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.filtros.FiltroBusquedaHistoricoSeries;
import mx.ift.sns.modelo.series.HistoricoSerie;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de Solicitudes de Cesión.
 */
public class HistoricoSeriesLazyModel extends LazyDataModel<HistoricoSerie> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoricoSeriesLazyModel.class);

    /** Colección de objetos. */
    private List<HistoricoSerie> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaHistoricoSeries filtros;

    /** Servicio. */
    private INumeracionGeograficaService service;

    /**
     * Constructor.
     */
    public HistoricoSeriesLazyModel() {
        dataSource = new ArrayList<HistoricoSerie>(1);
    }

    @Override
    public HistoricoSerie getRowData(String rowKey) {
        for (HistoricoSerie solicitud : dataSource) {
            if (solicitud.getConsecutivo().toString().equals(rowKey)) {
                return solicitud;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(HistoricoSerie pSolicitud) {
        return pSolicitud.getConsecutivo().toString();
    }

    @Override
    public List<HistoricoSerie> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || service == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<HistoricoSerie>(1);
        }
        

        try {

            if (LOGGER.isDebugEnabled()) {
                StringBuffer trace = new StringBuffer("Paginando, ");
                trace.append("First(").append(first).append("), ");
                trace.append("PageSize(").append(pageSize).append("), ");
                trace.append("Filters: ").append(filtros.toString());
                LOGGER.debug(trace.toString());
            }

            dataSource.clear();

            dataSource.addAll(service.findHistoricoSeries(filtros, first, filtros.getResultadosPagina()));
            
            for (HistoricoSerie historicoSerie : dataSource) 
            {
				historicoSerie.setIdserieAsString(historicoSerie.getSnaAsString());
				
			}

        } catch (Exception e) {
            LOGGER.error("load: ", e);
            dataSource = new ArrayList<HistoricoSerie>(1);
        }

        return dataSource;
    }

    /**
     * Asocia los filtros que se usarán en las búsquedas.
     * @param filtros FiltroBusquedaSolicitudes
     */
    public void setFiltros(FiltroBusquedaHistoricoSeries filtros) {
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
