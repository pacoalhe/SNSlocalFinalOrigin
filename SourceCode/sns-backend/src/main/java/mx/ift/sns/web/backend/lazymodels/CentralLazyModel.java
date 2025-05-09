package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.central.VCatalogoCentral;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCentrales;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de la central.
 */
public class CentralLazyModel extends LazyDataModel<VCatalogoCentral> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CentralLazyModel.class);

    /** Colección de objetos. */
    private List<VCatalogoCentral> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaCentrales filtros;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = true;

    /** Servicio. */
    private IAdminCatalogosFacade service;

    /** Identificador del Componente JSF de mensajes asociado a las búsquedas. */
    private String messagesId = null;

    /** Constructor. */
    public CentralLazyModel() {
        dataSource = new ArrayList<VCatalogoCentral>(1);
    }

    @Override
    public VCatalogoCentral getRowData(String rowKey) {
        for (VCatalogoCentral vCentral : dataSource) {
            if (vCentral.getId().toString().equals(rowKey)) {
                return vCentral;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(VCatalogoCentral pcentral) {
        return pcentral.getId().toString();
    }

    @Override
    public List<VCatalogoCentral> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || service == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<VCatalogoCentral>(1);
        }

        try {
            // Información de paginación. El resto de filtros
            // viene dado por el bean que instancia el objeto.
            filtros.setUsarPaginacion(true);
            // filtros.setResultadosPagina(pageSize);

            // Se saca la consulta del número de elementos para que se realice independientemente de si
            // se está paginando, buscando o eliminando registros de la tabla, de forma que el número de
            // elementos sea siempre el correcto y no pase que se queden páginas inexistentes cuando se
            // eliminan registros.
            this.setRowCount(service.findAllCentralesCount(filtros));

            if (resetPaginacion) {
                filtros.setNumeroPagina(0);
                resetPaginacion = false;
            } else {
                // Se establece la página a mostrar dependiendo de los elementos totales, el primer elemento
                // a mostrar y el número de elementos por página.
                filtros.setNumeroPagina(PaginatorUtil.getPagina(this.getRowCount(),
                        first, filtros.getResultadosPagina()));
            }

            if (LOGGER.isDebugEnabled()) {
                StringBuffer trace = new StringBuffer("Paginando, ");
                trace.append("First(").append(first).append("), ");
                trace.append("PageSize(").append(pageSize).append("), ");
                trace.append("Filters: ").append(filtros.toString());
                LOGGER.debug(trace.toString());
            }

            // Búsqueda con paginación
            dataSource.clear();
            // Búsqueda con paginación
            dataSource.addAll(service.findAllCentrales(filtros));
            // Indicaciones si no hay resultados
            if (dataSource.isEmpty() && (!StringUtils.isEmpty(messagesId))) {
                MensajesBean.addInfoMsg(messagesId, "No se han encontrado resultados para la búsqueda indicada");
            }
        } catch (Exception e) {
            LOGGER.error("load: " + e.getMessage());
            dataSource = new ArrayList<VCatalogoCentral>(1);
        }

        return dataSource;
    }

    /** Resetea los valores de búsqueda. */
    public void clear() {
        resetPaginacion = true;
    }

    /**
     * Colección de objetos.
     * @return List<VCatalogoCentral>
     */
    public List<VCatalogoCentral> getDataSource() {
        return dataSource;
    }

    /**
     * Colección de objetos.
     * @param dataSource List<VCatalogoCentral>
     */
    public void setDataSource(List<VCatalogoCentral> dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Servicio.
     * @return IAdminCatalogosFacade
     */
    public IAdminCatalogosFacade getService() {
        return service;
    }

    /**
     * Servicio.
     * @param service IAdminCatalogosFacade
     */
    public void setService(IAdminCatalogosFacade service) {
        this.service = service;
    }

    /**
     * Identificador del Componente JSF de mensajes asociado a las búsquedas.
     * @return messagesId
     */
    public String getMessagesId() {
        return messagesId;
    }

    /**
     * Identificador del Componente JSF de mensajes asociado a las búsquedas.
     * @param messagesId String
     */
    public void setMessagesId(String messagesId) {
        this.messagesId = messagesId;
    }

    /**
     * Filtros de búsqueda.
     * @return filtros
     */
    public FiltroBusquedaCentrales getFiltros() {
        return filtros;
    }

    /**
     * Filtros de búsqueda.
     * @param filtros FiltroBusquedaCentrales
     */
    public void setFiltros(FiltroBusquedaCentrales filtros) {
        this.filtros = filtros;
    }

}
