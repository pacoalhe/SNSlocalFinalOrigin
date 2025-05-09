package mx.ift.sns.web.backend.bitacora;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.bitacora.Bitacora;
import mx.ift.sns.modelo.filtros.FiltroBusquedaBitacoraLog;
import mx.ift.sns.negocio.IBitacoraService;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para bitacora..
 */
public class BitacoraLazyModel extends LazyDataModel<Bitacora> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(BitacoraLazyModel.class);

    /** Colección de objetos. */
    private List<Bitacora> dataSource;

    /** Servicio. */
    private IBitacoraService service;

    /** Filtro de busqueda. */
    private FiltroBusquedaBitacoraLog filtro;

    /**
     * Constructor.
     */
    public BitacoraLazyModel() {
        dataSource = new ArrayList<Bitacora>(1);
    }

    @Override
    public Bitacora getRowData(String rowKey) {
        return dataSource.get(0);
    }

    @Override
    public Object getRowKey(Bitacora bitacora) {
        return bitacora.getFecha().getTime();
    }

    @Override
    public int getRowCount() {
        return service.findBitacoraCount(filtro);
    }

    @Override
    public List<Bitacora> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("first {} pagesize {} sortField {} sortOrder {}", first, pageSize, sortField, sortOrder);
            }

            filtro.setFirst(first);
            filtro.setPageSize(pageSize);

            // Búsqueda con paginación
            dataSource = service.findBitacora(filtro);

        } catch (Exception e) {
            LOGGER.error("load: ", e);
        }

        return dataSource;
    }

    /**
     * @return the service
     */
    public IBitacoraService getService() {
        return service;
    }

    /**
     * @param service the service to set
     */
    public void setService(IBitacoraService service) {
        this.service = service;
    }

    /**
     * @return the filtro
     */
    public FiltroBusquedaBitacoraLog getFiltro() {
        return filtro;
    }

    /**
     * @param filtro the filtro to set
     */
    public void setFiltro(FiltroBusquedaBitacoraLog filtro) {
        this.filtro = filtro;
    }
}
