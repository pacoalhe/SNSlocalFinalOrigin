package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

import mx.ift.sns.modelo.filtros.FiltroBusquedaClaveServicio;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.web.backend.common.MensajesBean;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Implementación LazyModel para consulta Lazy de Claves de Servicio. */
public class ClaveServicioLazyModel extends LazyDataModel<ClaveServicio> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClaveServicioLazyModel.class);

    /** Colección de objetos. */
    private List<ClaveServicio> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaClaveServicio filtros;

    /** Servicio. */
    //@EJB
    private IAdminCatalogosFacade service;

    /** Identificador del Componente JSF de mensajes asociado a las búsquedas. */
    private String messagesId = null;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = false;

    /** Constructor. */
    public ClaveServicioLazyModel() {
        dataSource = new ArrayList<ClaveServicio>(1);
    }

    @Override
    public ClaveServicio getRowData(String rowKey) {
        for (ClaveServicio claveServicio : dataSource) {
            if (claveServicio.getCodigo().toString().equals(rowKey)) {
                return claveServicio;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(ClaveServicio claveServicio) {
        return claveServicio.getCodigo().toString();
    }

    @Override
    public List<ClaveServicio> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || service == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<ClaveServicio>(1);
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

            if (LOGGER.isDebugEnabled()) {
                StringBuffer trace = new StringBuffer("Paginando, ");
                trace.append("First(").append(first).append("), ");
                trace.append("PageSize(").append(pageSize).append("), ");
                trace.append("Filters: ").append(filtros.toString());
                LOGGER.debug(trace.toString());
            }

            dataSource.clear();

            // Búsqueda con paginación
            dataSource.addAll(service.findAllClaveServicio(filtros));
            // Numero de resultados totales en la query
            setRowCount(service.findAllClaveServicioCount(filtros));

            // Indicaciones si no hay resultados
            if (dataSource.isEmpty() && (!StringUtils.isEmpty(messagesId))) {
                MensajesBean.addInfoMsg(messagesId, "No se han encontrado resultados para la búsqueda indicada");
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado en la carga: " + e.getMessage());
            MensajesBean.addInfoMsg(messagesId, "Error inesperado: ");
            dataSource = new ArrayList<ClaveServicio>(1);
        }

        return dataSource;
    }

    /** Resetea los valores de búsqueda. */
    public void clear() {
        resetPaginacion = true;
    }

    /**
     * Colección de objetos.
     * @return dataSource
     */
    public List<ClaveServicio> getDataSource() {
        return dataSource;
    }

    /**
     * Colección de objetos.
     * @param dataSource dataSource to set
     */
    public void setDataSource(List<ClaveServicio> dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @return filtros
     */
    public FiltroBusquedaClaveServicio getFiltros() {
        return filtros;
    }

    /**
     * Filtros de búsqueda.
     * @param filtros FiltroBusquedaClaveServicio
     */
    public void setFiltros(FiltroBusquedaClaveServicio filtros) {
        this.filtros = filtros;
    }

    /**
     * Servicio.
     * @return service
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
     * Indica si hay que resetear la paginación.
     * @return resetPaginacion
     */
    public boolean isResetPaginacion() {
        return resetPaginacion;
    }

    /**
     * Indica si hay que resetear la paginación.
     * @param resetPaginacion boolean
     */
    public void setResetPaginacion(boolean resetPaginacion) {
        this.resetPaginacion = resetPaginacion;
    }

}
