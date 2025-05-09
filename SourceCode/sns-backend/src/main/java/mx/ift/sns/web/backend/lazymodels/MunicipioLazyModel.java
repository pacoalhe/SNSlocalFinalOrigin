package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.filtros.FiltroBusquedaMunicipios;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.web.backend.ac.municipio.DetalleMunicipio;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de Municipios.
 */
public class MunicipioLazyModel extends LazyDataModel<DetalleMunicipio> {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EstadoLazyModel.class);

    /** Colección de objetos. */
    private List<DetalleMunicipio> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaMunicipios filtros;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = false;

    /** Servicio. */
    private IAdminCatalogosFacade service;

    /** Constructor. */
    public MunicipioLazyModel() {
        dataSource = new ArrayList<DetalleMunicipio>(1);
    }

    @Override
    public DetalleMunicipio getRowData(String rowKey) {
        for (DetalleMunicipio detalle : dataSource) {

            if (detalle.getMunicipio().getId().toString().equals(rowKey)) {
                return detalle;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(DetalleMunicipio detalle) {
        return detalle.getMunicipio().getId().toString();
    }

    @Override
    public List<DetalleMunicipio> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || service == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<DetalleMunicipio>(1);
        }

        // Información de paginación. El resto de filtros
        // viene dado por el bean que instancia el objeto.
        filtros.setUsarPaginacion(true);
        // filtros.setResultadosPagina(pageSize);

        // Se saca la consulta del número de elementos para que se realice independientemente de si
        // se está paginando, buscando o eliminando registros de la tabla, de forma que el número de
        // elementos sea siempre el correcto y no pase que se queden páginas inexistentes cuando se
        // eliminan registros.
        this.setRowCount(service.findAllMunicipiosCount(filtros));

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

        List<Municipio> lista = new ArrayList<Municipio>();
        lista.addAll(service.findAllMunicipios(filtros));

        for (Municipio municipio : lista) {
            DetalleMunicipio data = new DetalleMunicipio();
            data.setMunicipio(municipio);
            // Comprobamos si hay numeraciones asignadas para el municipio
            Integer totalAsignados = new Integer(0);
            totalAsignados = service.getTotalNumRangosAsignadosByMunicipio(municipio);

            if (totalAsignados > 0) {
                data.setNumeracionAsignada("SI");
            } else {
                data.setNumeracionAsignada("NO");
            }

            dataSource.add(data);

        }

        return dataSource;
    }

    /**
     * Colección de objetos.
     * @return dataSource
     */
    public List<DetalleMunicipio> getDataSource() {
        return dataSource;
    }

    /**
     * Colección de objetos.
     * @param dataSource List<DetalleMunicipio>
     */
    public void setDataSource(List<DetalleMunicipio> dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Filtros de búsqueda.
     * @return filtros
     */
    public FiltroBusquedaMunicipios getFiltros() {
        return filtros;
    }

    /**
     * Filtros de búsqueda.
     * @param filtros FiltroBusquedaMunicipios
     */
    public void setFiltros(FiltroBusquedaMunicipios filtros) {
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
     * ndica si hay que resetear la paginación.
     * @return resetPaginacion
     */
    public boolean isResetPaginacion() {
        return resetPaginacion;
    }

    /**
     * ndica si hay que resetear la paginación.
     * @param resetPaginacion boolean
     */
    public void setResetPaginacion(boolean resetPaginacion) {
        this.resetPaginacion = resetPaginacion;
    }

}
