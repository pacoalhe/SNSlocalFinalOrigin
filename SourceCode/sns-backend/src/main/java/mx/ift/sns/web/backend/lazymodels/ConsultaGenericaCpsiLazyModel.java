package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCPSI;
import mx.ift.sns.negocio.cpsi.DetalleConsultaGenerica;
import mx.ift.sns.negocio.cpsi.ICodigoCPSIFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de Solicitudes Cpsi.
 */
public class ConsultaGenericaCpsiLazyModel extends LazyDataModel<DetalleConsultaGenerica> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultaGenericaCpsiLazyModel.class);

    /** Colección de objetos. */
    private List<DetalleConsultaGenerica> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaSolicitudesCPSI filtros;

    /** Identificador del Componente JSF de mensajes asociado a las búsquedas. */
    private String messagesId = null;

    /** Servicio. */
    private ICodigoCPSIFacade service;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = true;

    /** Constructor. */
    public ConsultaGenericaCpsiLazyModel() {
        dataSource = new ArrayList<DetalleConsultaGenerica>(1);
    }

    @Override
    public DetalleConsultaGenerica getRowData(String rowKey) {
        for (DetalleConsultaGenerica solicitud : dataSource) {
            if (solicitud.getConsecutivo().toString().equals(rowKey)) {
                return solicitud;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(DetalleConsultaGenerica pSolicitud) {
        return pSolicitud.getConsecutivo().toString();
    }

    @Override
    public List<DetalleConsultaGenerica> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || service == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<DetalleConsultaGenerica>(1);
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

            if (dataSource != null) {
                dataSource.clear();
            }

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
            dataSource = new ArrayList<DetalleConsultaGenerica>(1);
        }

        return dataSource;
    }

    /** Resetea los valores de búsqueda. */
    public void clear() {
        resetPaginacion = true;
    }

    // GETTERS & SETTERS
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
    public FiltroBusquedaSolicitudesCPSI getFiltros() {
        return filtros;
    }

    /**
     * Filtros de búsqueda.
     * @param filtros FiltroBusquedaSolicitudesCPSI
     */
    public void setFiltros(FiltroBusquedaSolicitudesCPSI filtros) {
        this.filtros = filtros;
    }

    /**
     * Servicio.
     * @return service
     */
    public ICodigoCPSIFacade getService() {
        return service;
    }

    /**
     * Servicio.
     * @param service ICodigoCPSIFacade
     */
    public void setService(ICodigoCPSIFacade service) {
        this.service = service;
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
