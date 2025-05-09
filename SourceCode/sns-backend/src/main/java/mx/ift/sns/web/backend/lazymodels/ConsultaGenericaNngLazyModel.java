package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de Solicitudes de Asignación no Geografica.
 */
public class ConsultaGenericaNngLazyModel extends LazyDataModel<Solicitud> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultaGenericaNngLazyModel.class);

    /** Colección de objetos. */
    private List<Solicitud> dataSource;

    /** Identificador del Componente JSF de mensajes asociado a las búsquedas. */
    private String messagesId = null;

    /** Filtros de búsqueda. */
    private FiltroBusquedaSolicitudes filtros;

    /** Servicio. */
    private INumeracionNoGeograficaFacade facade;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = true;

    /** Constructor. */
    public ConsultaGenericaNngLazyModel() {
        dataSource = new ArrayList<Solicitud>(1);
    }

    @Override
    public Solicitud getRowData(String rowKey) {
        for (Solicitud detalle : dataSource) {
            if (detalle.getId().toString().equals(rowKey)) {
                return detalle;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(Solicitud detalle) {
        return detalle.getId().toString();
    }

    @Override
    public List<Solicitud> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || facade == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<Solicitud>(1);
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
            this.setRowCount(facade.findAllSolicitudesCount(filtros));

            if (resetPaginacion) {
                filtros.setNumeroPagina(0);
                resetPaginacion = false;
            } else {
                // Se establece la página a mostrar dependiendo de los elementos totales, el primer elemento
                // a mostrar y el número de elementos por página.
                filtros.setNumeroPagina(PaginatorUtil.getPagina(this.getRowCount(),
                        first, filtros.getResultadosPagina()));
            }

            dataSource.clear();
            dataSource.addAll(facade.findAllSolicitudes(filtros));

            if (LOGGER.isDebugEnabled()) {
                StringBuffer trace = new StringBuffer("Paginando, ");
                trace.append("First(").append(first).append("), ");
                trace.append("PageSize(").append(pageSize).append("), ");
                trace.append("Filters: ").append(filtros.toString());
                LOGGER.debug(trace.toString());
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado: ", e);
            MensajesBean.addErrorMsg(messagesId, Errores.ERROR_0004);
            dataSource = new ArrayList<Solicitud>(1);
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
     * @param facade INumeracionGeograficaService
     */
    public void setService(INumeracionNoGeograficaFacade facade) {
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
     * @param resetPaginacion boolean
     */
    public void setResetPaginacion(boolean resetPaginacion) {
        this.resetPaginacion = resetPaginacion;
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
     * @param messagesId messagesId to set
     */
    public void setMessagesId(String messagesId) {
        this.messagesId = messagesId;
    }

}
