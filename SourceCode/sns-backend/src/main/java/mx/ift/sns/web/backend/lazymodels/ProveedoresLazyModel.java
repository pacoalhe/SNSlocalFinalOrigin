package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.filtros.FiltroBusquedaProveedores;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Implementación LazyModel para consulta Lazy de Proveedores. */
public class ProveedoresLazyModel extends LazyDataModel<Proveedor> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProveedoresLazyModel.class);

    /** Colección de objetos. */
    private List<Proveedor> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaProveedores filtros;

    /** Servicio. */
    private INumeracionGeograficaService service;

    /** Identificador del Componente JSF de mensajes asociado a las búsquedas. */
    private String messagesId = null;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = false;

    /** Constructor. */
    public ProveedoresLazyModel() {
        dataSource = new ArrayList<Proveedor>(1);
    }

    @Override
    public Proveedor getRowData(String rowKey) {
        for (Proveedor proveedor : dataSource) {
            if (proveedor.getId().toString().equals(rowKey)) {
                return proveedor;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(Proveedor proveedor) {
        return proveedor.getId().toString();
    }

    @Override
    public List<Proveedor> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || service == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<Proveedor>(1);
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
            this.setRowCount(service.findAllProveedoresCount(filtros));

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

            dataSource.clear();
            if (filtros.isPreCarga() || !filtros.getFiltrosProveedor().isEmpty()) {
                // Búsqueda con paginación
                dataSource.addAll(service.findAllProveedores(filtros));
            }

            // Indicaciones si no hay resultados
            if (dataSource.isEmpty() && (!StringUtils.isEmpty(messagesId))) {
                MensajesBean.addInfoMsg(messagesId, "No se han encontrado resultados para la búsqueda indicada");
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado en la carga: " + e.getMessage());
            MensajesBean.addInfoMsg(messagesId, "Error inesperado: ");
            dataSource = new ArrayList<Proveedor>(1);
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
     * @param filtros FiltroBusquedaProveedores
     */
    public void setFiltros(FiltroBusquedaProveedores filtros) {
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
     * Asocia un componente JSF de mensajes para mostrar avisos.
     * @param messagesId String
     */
    public void setMessagesId(String messagesId) {
        this.messagesId = messagesId;
    }
}
