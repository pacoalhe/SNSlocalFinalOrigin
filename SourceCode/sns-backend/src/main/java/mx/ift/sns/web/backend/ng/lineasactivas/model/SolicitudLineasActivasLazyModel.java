package mx.ift.sns.web.backend.ng.lineasactivas.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.filtros.FiltroBusquedaLineasActivas;
import mx.ift.sns.modelo.lineas.DetalleReporte;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de Solicitudes de lienas activas.
 */
public class SolicitudLineasActivasLazyModel extends LazyDataModel<ResumenConsultaLineasActivas> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudLineasActivasLazyModel.class);

    /** Colección de objetos. */
    private List<ResumenConsultaLineasActivas> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaLineasActivas filtros;

    /** Servicio. */
    private INumeracionGeograficaService service;

    /** Constructor. */
    public SolicitudLineasActivasLazyModel() {
        dataSource = new ArrayList<ResumenConsultaLineasActivas>(1);
    }

    @Override
    public ResumenConsultaLineasActivas getRowData(String rowKey) {
        for (ResumenConsultaLineasActivas solicitud : dataSource) {
            if (solicitud.getDetalleReporte().getId().equals(rowKey)) {
                return solicitud;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(ResumenConsultaLineasActivas det) {
        return det.getDetalleReporte().getId();
    }

    @Override
    public List<ResumenConsultaLineasActivas> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null) {
            LOGGER.error("Error en la carga, los filtros no se han definido.");
            return new ArrayList<ResumenConsultaLineasActivas>(1);
        }

        if (service == null) {
            // Si la fachada de NG no esta inyectada devolvemos la lista vacia
            return new ArrayList<ResumenConsultaLineasActivas>(1);
        }

        try {
            // Información de paginación. El resto de filtros
            // viene dado por el bean que instancia el objeto.
            filtros.setUsarPaginacion(true);
            filtros.setNumeroPagina(first);
            // filtros.setResultadosPagina(pageSize);

            if (LOGGER.isDebugEnabled()) {
                StringBuffer trace = new StringBuffer("Paginando, ");
                trace.append("First(").append(first).append("), ");
                trace.append("PageSize(").append(pageSize).append("), ");
                trace.append("Filters: ").append(filtros.toString());
                LOGGER.debug(trace.toString());
            }

            dataSource.clear();

            // Búsqueda con paginación
            List<DetalleReporte> listaDetalle = service.findAllDetalleReporte(filtros);
            for (DetalleReporte detalle : listaDetalle) {
                ResumenConsultaLineasActivas dato = new ResumenConsultaLineasActivas();
                dato.setDetalleReporte(detalle);

                dataSource.add(dato);
            }

            this.setRowCount(service.findAllDetalleReporteCount(filtros));

        } catch (Exception e) {
            LOGGER.error("load: ", e);
            dataSource = new ArrayList<ResumenConsultaLineasActivas>(1);
        }

        return dataSource;
    }

    /**
     * Asocia los filtros que se usarán en las búsquedas.
     * @param filtros FiltroBusquedaSolicitudes
     */
    public void setFiltros(FiltroBusquedaLineasActivas filtros) {
        this.filtros = filtros;
    }

    /**
     * Asocia el servicio que se usarán en las búsquedas.
     * @param service INumeracionGeograficaService
     */
    public void setService(INumeracionGeograficaService service) {
        this.service = service;
    }
}
