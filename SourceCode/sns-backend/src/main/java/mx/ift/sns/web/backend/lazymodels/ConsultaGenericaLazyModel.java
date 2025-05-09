package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.filtros.FiltroBusquedaLineasActivas;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de Solicitudes de Liberación.
 */
public class ConsultaGenericaLazyModel extends LazyDataModel<Solicitud> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultaGenericaLazyModel.class);

    /** Colección de objetos. */
    private List<Solicitud> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaSolicitudes filtros;

    /** Filtros de búsqueda lineas activas. */
    private FiltroBusquedaLineasActivas filtroLineas;

    /** Identificador del Componente JSF de mensajes asociado a las búsquedas. */
    private String messagesId = null;

    /** Servicio. */
    private INumeracionGeograficaService service;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = true;

    /** Constructor. */
    public ConsultaGenericaLazyModel() {
        dataSource = new ArrayList<Solicitud>(1);
    }

    @Override
    public Solicitud getRowData(String rowKey) {
        for (Solicitud solicitud : dataSource) {
            if (solicitud.getId().toString().equals(rowKey)) {
                return solicitud;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(Solicitud pSolicitud) {
        return pSolicitud.getId().toString();
    }

    @Override
    public List<Solicitud> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || service == null) {
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
            this.setRowCount(service.findAllSolicitudesCount(filtros));

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
            dataSource.addAll(service.findAllSolicitudes(filtros));

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
     * @param service INumeracionGeograficaService
     */
    public void setService(INumeracionGeograficaService service) {
        this.service = service;
    }

    /**
     * @return the messagesId
     */
    public String getMessagesId() {
        return messagesId;
    }

    /**
     * @param messagesId the messagesId to set
     */
    public void setMessagesId(String messagesId) {
        this.messagesId = messagesId;
    }

    /**
     * @return the filtros
     */
    public FiltroBusquedaSolicitudes getFiltros() {
        return filtros;
    }

    /**
     * @return the filtroLineas
     */
    public FiltroBusquedaLineasActivas getFiltroLineas() {
        return filtroLineas;
    }

    /**
     * @param filtroLineas the filtroLineas to set
     */
    public void setFiltroLineas(FiltroBusquedaLineasActivas filtroLineas) {
        this.filtroLineas = filtroLineas;
    }

}
