package mx.ift.sns.web.backend.nng.historico;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.filtros.FiltroBusquedaHistoricoSeries;
import mx.ift.sns.modelo.series.HistoricoSerieNng;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de Solicitudes de Cesión.
 */
public class HistoricoSeriesNngLazyModel extends LazyDataModel<HistoricoSerieNng> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoricoSeriesNngLazyModel.class);

    /** Colección de objetos. */
    private List<HistoricoSerieNng> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaHistoricoSeries filtros;

    /** Fachada. */
    private INumeracionNoGeograficaFacade facade;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = true;

    /**
     * Constructor.
     */
    public HistoricoSeriesNngLazyModel() {
        dataSource = new ArrayList<HistoricoSerieNng>(1);
    }

    @Override
    public HistoricoSerieNng getRowData(String rowKey) {
        for (HistoricoSerieNng solicitud : dataSource) {
            if (solicitud.getConsecutivo().toString().equals(rowKey)) {
                return solicitud;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(HistoricoSerieNng pSolicitud) {
        return pSolicitud.getConsecutivo().toString();
    }

    @Override
    public List<HistoricoSerieNng> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || facade == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<HistoricoSerieNng>(1);
        }

        try {

            if (LOGGER.isDebugEnabled()) {
                StringBuffer trace = new StringBuffer("Paginando, ");
                trace.append("First(").append(first).append("), ");
                trace.append("PageSize(").append(pageSize).append("), ");
                trace.append("Filters: ").append(filtros.toString());
                LOGGER.debug(trace.toString());
            }

            filtros.setUsarPaginacion(true);
            // filtros.setResultadosPagina(pageSize);
            if (resetPaginacion) {
                filtros.setNumeroPagina(0);
                resetPaginacion = false;
            } else {
                filtros.setNumeroPagina(first);
            }

            dataSource.clear();

            dataSource.addAll(facade.findAllHistoricoSeries(filtros));

            this.setRowCount(facade.findAllHistoricoSeriesCount(filtros));

        } catch (Exception e) {
            LOGGER.error("load: ", e);
            dataSource = new ArrayList<HistoricoSerieNng>(1);
        }

        return dataSource;
    }

    /**
     * Colección de objetos.
     * @return dataSource
     */
    public List<HistoricoSerieNng> getDataSource() {
        return dataSource;
    }

    /**
     * Colección de objetos.
     * @param dataSource dataSource to set
     */
    public void setDataSource(List<HistoricoSerieNng> dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Filtros de búsqueda.
     * @return filtros
     */
    public FiltroBusquedaHistoricoSeries getFiltros() {
        return filtros;
    }

    /**
     * Filtros de búsqueda.
     * @param filtros filtros to set
     */
    public void setFiltros(FiltroBusquedaHistoricoSeries filtros) {
        this.filtros = filtros;
    }

    /**
     * Fachada.
     * @return facade
     */
    public INumeracionNoGeograficaFacade getFacade() {
        return facade;
    }

    /**
     * Fachada.
     * @param facade facade to set
     */
    public void setFacade(INumeracionNoGeograficaFacade facade) {
        this.facade = facade;
    }

    /**
     * Indica si hay que resetear la paginación.
     * @return resetPaginacion
     */
    public boolean isResetPaginacion() {
        return resetPaginacion;
    }

    /**
     * Indica si hay que resetear la paginación.
     * @param resetPaginacion resetPaginacion to set
     */
    public void setResetPaginacion(boolean resetPaginacion) {
        this.resetPaginacion = resetPaginacion;
    }

}
