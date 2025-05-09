package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.filtros.FiltroBusquedaPoblaciones;
import mx.ift.sns.modelo.ot.DetallePoblacion;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Implementación LazyModel para consulta Lazy de Poblaciones. */
public class PoblacionesLazyModel extends LazyDataModel<DetallePoblacion> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PoblacionesLazyModel.class);

    /** Colección de objetos. */
    private List<DetallePoblacion> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaPoblaciones filtros;

    /** Servicio. */
    private IAdminCatalogosFacade service;

    /** Identificador del Componente JSF de mensajes asociado a las búsquedas. */
    private String messagesId = null;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = true;

    /** Constructor. */
    public PoblacionesLazyModel() {
        dataSource = new ArrayList<DetallePoblacion>(1);
    }

    @Override
    public DetallePoblacion getRowData(String rowKey) {
        for (DetallePoblacion detalle : dataSource) {
            if (detalle.getPoblacion().getCdgPoblacion().toString().equals(rowKey)) {
                return detalle;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(DetallePoblacion detalle) {
        return detalle.getPoblacion().getCdgPoblacion().toString();
    }

    @Override
    public List<DetallePoblacion> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || service == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<DetallePoblacion>(1);
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
            this.setRowCount(service.findAllPoblacionesCount(filtros));

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

            List<Poblacion> lista = new ArrayList<Poblacion>();
            // Búsqueda con paginación
            lista.addAll(service.findAllPoblaciones(filtros));

            for (Poblacion poblacion : lista) {
                DetallePoblacion data = new DetallePoblacion();
                data.setPoblacion(poblacion);
                // Comprobamos si hay numeraciones asignadas para la población
                if (service.isNumeracionAsignada(poblacion)) {
                    data.setNumeracionAsignada("SI");
                } else {
                    data.setNumeracionAsignada("NO");
                }
                dataSource.add(data);

            }

            // Indicaciones si no hay resultados
            if (dataSource.isEmpty() && (!StringUtils.isEmpty(messagesId))) {
                MensajesBean.addInfoMsg(messagesId,
                        "No se han encontrado resultados para la búsqueda indicada");
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado en la carga: " + e.getMessage());
            MensajesBean.addInfoMsg(messagesId, "Error inesperado: ");
            dataSource = new ArrayList<DetallePoblacion>(1);
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
     * @param filtros FiltroBusquedaPoblaciones
     */
    public void setFiltros(FiltroBusquedaPoblaciones filtros) {
        this.filtros = filtros;
    }

    /**
     * Asocia el servicio que se usarán en las búsquedas.
     * @param service IAdminCatalogosFacade
     */
    public void setService(IAdminCatalogosFacade service) {
        this.service = service;
    }

    /**
     * Asocia un componente JSF de mensajes para mostrar avisos.
     * @param messagesId String
     */
    public void setMessagesId(String messagesId) {
        this.messagesId = messagesId;
    }
}
