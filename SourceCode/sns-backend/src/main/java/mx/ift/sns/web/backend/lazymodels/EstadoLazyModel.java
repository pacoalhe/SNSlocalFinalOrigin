package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.negocio.IAdminCatalogosFacade;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de Estados.
 */
public class EstadoLazyModel extends LazyDataModel<Estado> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EstadoLazyModel.class);

    /** Colección de objetos. */
    private List<Estado> dataSource;

    /** Filtros de búsqueda. */
    private Estado estadoSeleccionado;

    /** Servicio. */
    private IAdminCatalogosFacade service;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Constructor. */
    public EstadoLazyModel() {
        dataSource = new ArrayList<Estado>(1);
    }

    @Override
    public Estado getRowData(String rowKey) {
        for (Estado rango : dataSource) {
            if (rango.getCodEstado().equals(rowKey)) {
                return rango;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(Estado pRango) {
        return pRango.getCodEstado();
    }

    @Override
    public List<Estado> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        try {

            if (LOGGER.isDebugEnabled()) {
                StringBuffer trace = new StringBuffer("Paginando, ");
                trace.append("First(").append(first).append("), ");
                trace.append("PageSize(").append(registroPorPagina).append("), ");
                LOGGER.debug(trace.toString());
            }

            // Búsqueda con paginación
            dataSource.clear();
            if (null != estadoSeleccionado && null != estadoSeleccionado.getCodEstado()) {
                dataSource.add(estadoSeleccionado);
                this.setRowCount(1);
            } else {
                dataSource.addAll(service.findEstados(first, registroPorPagina));
                // Numero de resultados totales en la query
                this.setRowCount(service.findAllEstadosCount());
            }

        } catch (Exception e) {
            LOGGER.error("load: " + e.getMessage());
            dataSource = new ArrayList<Estado>(1);
        }

        return dataSource;
    }

    /**
     * Colección de objetos.
     * @return dataSource
     */
    public List<Estado> getDataSource() {
        return dataSource;
    }

    /**
     * Colección de objetos.
     * @param dataSource List<Estado>
     */
    public void setDataSource(List<Estado> dataSource) {
        this.dataSource = dataSource;
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
     * Filtros de búsqueda.
     * @return estadoSeleccionado
     */
    public Estado getEstadoSeleccionado() {
        return estadoSeleccionado;
    }

    /**
     * Filtros de búsqueda.
     * @param estadoSeleccionado Estado
     */
    public void setEstadoSeleccionado(Estado estadoSeleccionado) {
        this.estadoSeleccionado = estadoSeleccionado;
    }

    /**
     * Obtiene el valor del número de elementos por página de la tabla de resultados.
     * @return the registroPorPagina
     */
    public int getRegistroPorPagina() {
        return registroPorPagina;
    }

    /**
     * Establece el valor del número de elementos por página de la tabla de resultados.
     * @param registroPorPagina the registroPorPagina to set
     */
    public void setRegistroPorPagina(int registroPorPagina) {
        this.registroPorPagina = registroPorPagina;
    }
}
