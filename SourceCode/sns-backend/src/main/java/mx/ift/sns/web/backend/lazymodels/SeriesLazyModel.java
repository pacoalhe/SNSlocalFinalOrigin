package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.filtros.FiltroBusquedaSeries;
import mx.ift.sns.modelo.series.VCatalogoSerie;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.lazymodels.multiseleccion.ILazyModelRefreshable;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Implementación LazyModel para consulta Lazy de Series. */
public class SeriesLazyModel extends LazyDataModel<VCatalogoSerie> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SeriesLazyModel.class);

    /** Colección de objetos. */
    private List<VCatalogoSerie> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaSeries filtros;

    /** Servicio. */
    private IAdminCatalogosFacade service;

    /** Identificador del Componente JSF de mensajes asociado a las búsquedas. */
    private String messagesId = null;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = true;

    /** Gestor de Selección Múltiple sobre la tabla Lazy. */
    private ILazyModelRefreshable multiSelectionManager;

    /**
     * @return the multiSelectionManager
     */
    public ILazyModelRefreshable getMultiSelectionManager() {
        return multiSelectionManager;
    }

    /**
     * @param multiSelectionManager the multiSelectionManager to set
     */
    public void setMultiSelectionManager(ILazyModelRefreshable multiSelectionManager) {
        this.multiSelectionManager = multiSelectionManager;
    }

    /** Constructor. */
    public SeriesLazyModel() {
        dataSource = new ArrayList<VCatalogoSerie>(1);
    }

    @Override
    public VCatalogoSerie getRowData(String rowKey) {
        for (VCatalogoSerie vSerie : dataSource) {
            if (vSerie.getId().toString().equals(rowKey)) {
                return vSerie;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(VCatalogoSerie vSerie) {
        return vSerie.getId().toString();
    }

    @Override
    public List<VCatalogoSerie> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || service == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<VCatalogoSerie>(1);
        }

        try {
            // Información de paginación. El resto de filtros
            // viene dado por el bean que instancia el objeto.
            filtros.setUsarPaginacion(true);
            // filtros.setResultadosPagina(pageSize);
            if (resetPaginacion) {
                filtros.setNumeroPagina(0);
                resetPaginacion = false;
            } else {
                filtros.setNumeroPagina(first);
            }

            this.setRowCount(service.findAllCatalogoSeriesCount(filtros));

            if (LOGGER.isDebugEnabled()) {
                StringBuffer trace = new StringBuffer("Paginando, ");
                trace.append("First(").append(first).append("), ");
                trace.append("PageSize(").append(pageSize).append("), ");
                trace.append("Filters: ").append(filtros.toString());
                LOGGER.debug(trace.toString());
            }

            dataSource.clear();

            dataSource.addAll(service.findAllCatalogoSeries(filtros));
            for (VCatalogoSerie vCatalogoSerie : dataSource) {
				vCatalogoSerie.setIdSna(vCatalogoSerie.getSnaAsString());
			}

            if (multiSelectionManager != null) {
                multiSelectionManager.refreshLazyModelSelection(first, pageSize, null);
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado en la carga: " + e.getMessage());
            MensajesBean.addInfoMsg(messagesId, "Error inesperado: ");
        }

        return dataSource;
    }

    /** Resetea los valores de búsqueda. */
    public void clear() {
        resetPaginacion = true;
    }

    // GETTERS & SETTERS

    /**
     * Asocia el servicio que se usarán en las búsquedas.
     * @param service INumeracionGeograficaService
     */
    public void setService(IAdminCatalogosFacade service) {
        this.service = service;
    }

    /**
     * Filtros de búsqueda.
     * @return filtros
     */
    public FiltroBusquedaSeries getFiltros() {
        return filtros;
    }

    /**
     * Filtros de búsqueda.
     * @param filtros filtros to set
     */
    public void setFiltros(FiltroBusquedaSeries filtros) {
        this.filtros = filtros;
    }

    /**
     * Asocia un componente JSF de mensajes para mostrar avisos.
     * @param messagesId String
     */
    public void setMessagesId(String messagesId) {
        this.messagesId = messagesId;
    }

    /**
     * Colección de objetos.
     * @return dataSource
     */
    public List<VCatalogoSerie> getDataSource() {
        return dataSource;
    }

    /**
     * Colección de objetos.
     * @param dataSource List<VCatalogoSerie>
     */
    public void setDataSource(List<VCatalogoSerie> dataSource) {
        this.dataSource = dataSource;
    }

}
