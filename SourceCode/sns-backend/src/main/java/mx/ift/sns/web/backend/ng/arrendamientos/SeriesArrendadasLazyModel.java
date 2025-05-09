package mx.ift.sns.web.backend.ng.arrendamientos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.reporteabd.SerieArrendadaPadre;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para series arrendadas.
 */
public class SeriesArrendadasLazyModel extends LazyDataModel<SerieArrendadaPadre> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SeriesArrendadasLazyModel.class);

    /** Colección de objetos. */
    private List<SerieArrendadaPadre> dataSource;

    /** Servicio. */
    private INumeracionGeograficaService service;

    /**
     * Constructor.
     */
    public SeriesArrendadasLazyModel() {
        dataSource = new ArrayList<SerieArrendadaPadre>(1);
    }

    @Override
    public SerieArrendadaPadre getRowData(String rowKey) {
        for (SerieArrendadaPadre serie : dataSource) {
            if (serie.getId().toString().equals(rowKey)) {
                return serie;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(SerieArrendadaPadre serie) {
        return serie.getId();
    }

    @Override
    public List<SerieArrendadaPadre> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        try {
            // Número de elementos
            this.setRowCount(service.getSeriesService().findSeriesArrendadasPadreCount());

            dataSource.clear();

            // Búsqueda con paginación
            dataSource = service.getSeriesService().findSeriesArrendadasPadre(first, pageSize);
        } catch (Exception e) {
            LOGGER.error("load: ", e);
            // dataSource = new ArrayList<SolicitudCesionNg>(1);
        }

        return dataSource;
    }

    /**
     * Servicio.
     * @return service
     */
    public INumeracionGeograficaService getService() {
        return service;
    }

    /**
     * Servicio.
     * @param service INumeracionGeograficaService
     */
    public void setService(INumeracionGeograficaService service) {
        this.service = service;
    }
}
