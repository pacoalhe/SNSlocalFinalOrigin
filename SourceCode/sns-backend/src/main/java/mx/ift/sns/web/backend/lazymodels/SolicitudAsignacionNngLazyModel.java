package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.nng.SolicitudAsignacionNng;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.nng.asignacion.DetalleConsultaAsignacionNng;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de Solicitudes de Asignación no Geografica.
 */
public class SolicitudAsignacionNngLazyModel extends LazyDataModel<DetalleConsultaAsignacionNng> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudAsignacionNngLazyModel.class);

    /** Colección de objetos. */
    private List<DetalleConsultaAsignacionNng> dataSource;

    /** Identificador del Componente JSF de mensajes asociado a las búsquedas. */
    private String messagesId = null;

    /** Filtros de búsqueda. */
    private FiltroBusquedaSolicitudes filtros;

    /** Servicio. */
    private INumeracionNoGeograficaFacade facade;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = true;

    /** Constructor. */
    public SolicitudAsignacionNngLazyModel() {
        dataSource = new ArrayList<DetalleConsultaAsignacionNng>(1);
    }

    @Override
    public DetalleConsultaAsignacionNng getRowData(String rowKey) {
        for (DetalleConsultaAsignacionNng detalle : dataSource) {
            if (detalle.getSolicitud().getId().toString().equals(rowKey)) {
                return detalle;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(DetalleConsultaAsignacionNng detalle) {
        return detalle.getSolicitud().getId().toString();
    }

    @Override
    public List<DetalleConsultaAsignacionNng> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || facade == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<DetalleConsultaAsignacionNng>(1);
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
            this.setRowCount(facade.findAllSolicitudesAsignacionCount(filtros));

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

            // Búsqueda con paginación
            List<SolicitudAsignacionNng> listaSolicitudes = facade.findAllSolicitudesAsignacion(filtros);
            for (SolicitudAsignacionNng sol : listaSolicitudes) {
                dataSource.add(this.getDetalleSolicitud(sol));
            }

            if (LOGGER.isDebugEnabled()) {
                StringBuffer trace = new StringBuffer("Paginando, ");
                trace.append("First(").append(first).append("), ");
                trace.append("PageSize(").append(pageSize).append("), ");
                trace.append("Filters: ").append(filtros.toString());
                LOGGER.debug(trace.toString());
            }

            // Indicaciones si no hay resultados
            if (dataSource.isEmpty() && (!StringUtils.isEmpty(messagesId))) {
                MensajesBean.addInfoMsg(messagesId, "No se han encontrado resultados para la búsqueda indicada");
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado: ", e);
            MensajesBean.addErrorMsg(messagesId, Errores.ERROR_0004);
            dataSource = new ArrayList<DetalleConsultaAsignacionNng>(1);
        }

        return dataSource;
    }

    /**
     * Obtiene el detalle.
     * @param sol solicitud
     * @return DetalleConsultaAsignacion
     */
    private DetalleConsultaAsignacionNng getDetalleSolicitud(SolicitudAsignacionNng sol) {

        DetalleConsultaAsignacionNng detSol = new DetalleConsultaAsignacionNng();

        detSol.setSolicitud(sol);

        List<String> listaClientes = facade.findAllClientesBySolicitud(sol);

        if (listaClientes.size() > 1) {
            detSol.setCliente("Múltiples");
        } else if (!listaClientes.isEmpty()) {
            detSol.setCliente(listaClientes.get(0));
        }
        return detSol;
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
     * @return the resetPaginacion
     */
    public boolean isResetPaginacion() {
        return resetPaginacion;
    }

    /**
     * @param resetPaginacion the resetPaginacion to set
     */
    public void setResetPaginacion(boolean resetPaginacion) {
        this.resetPaginacion = resetPaginacion;
    }

    /**
     * @return the facade
     */
    public INumeracionNoGeograficaFacade getFacade() {
        return facade;
    }

    /**
     * @param facade the facade to set
     */
    public void setFacade(INumeracionNoGeograficaFacade facade) {
        this.facade = facade;
    }
}
