package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.ng.SolicitudRedistribucionNg;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.ng.redistribucion.DetalleConsultaRedistNg;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de Solicitudes de Redistribucion.
 */
public class SolicitudRedistNgConsultaLazyModel extends LazyDataModel<DetalleConsultaRedistNg> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudRedistNgConsultaLazyModel.class);

    /** Colección de objetos. */
    private List<DetalleConsultaRedistNg> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaSolicitudes filtros;

    /** Servicio. */
    private INumeracionGeograficaService service;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = true;

    /** Constructor. */
    public SolicitudRedistNgConsultaLazyModel() {
        dataSource = new ArrayList<DetalleConsultaRedistNg>(1);
    }

    @Override
    public DetalleConsultaRedistNg getRowData(String rowKey) {
        for (DetalleConsultaRedistNg detRedist : dataSource) {
            if (detRedist.getConsecutivo().equals(rowKey)) {
                return detRedist;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(DetalleConsultaRedistNg detRedist) {
        return detRedist.getConsecutivo();
    }

    @Override
    public List<DetalleConsultaRedistNg> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || service == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<DetalleConsultaRedistNg>(1);
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
            this.setRowCount(service.findAllSolicitudesRedistribucionCount(filtros));

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
                LOGGER.debug(filtros.toString());
            }

            dataSource.clear();
            List<SolicitudRedistribucionNg> listaSolicitudes = service.findAllSolicitudesRedistribucion(filtros);
            for (SolicitudRedistribucionNg solRed : listaSolicitudes) {
                dataSource.add(this.getDetalleRedistribucion(solRed));
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            dataSource = new ArrayList<DetalleConsultaRedistNg>(1);
        }

        return dataSource;
    }

    /**
     * Crea un objeto DetalleConsultaRedistNg con la información de la Solicitud de Redistribución dada.
     * @param pSolicitud Información de la Solicitud de Redistribución.
     * @return Objeto DetalleConsultaRedistNg con la información relevante para la tabla.
     */
    private DetalleConsultaRedistNg getDetalleRedistribucion(SolicitudRedistribucionNg pSolicitud) {
        DetalleConsultaRedistNg detRedist = new DetalleConsultaRedistNg();
        detRedist.setConsecutivo(pSolicitud.getId().toString());
        detRedist.setEstatus(pSolicitud.getEstadoSolicitud().getDescripcion());
        detRedist.setNumOficio(pSolicitud.getNumOficioSolicitante());
        detRedist.setCancelarDisponible(!pSolicitud.getEstadoSolicitud().getCodigo()
                .equals(EstadoSolicitud.SOLICITUD_CANCELADA));
        detRedist.setFechaSolicitud(FechasUtils.fechaToString(pSolicitud.getFechaSolicitud()));
        detRedist.setFechaRedist(FechasUtils.fechaToString(pSolicitud.getFechaAsignacion()));

        if (pSolicitud.getProveedorSolicitante() != null) {
            detRedist.setSolicitante(pSolicitud.getProveedorSolicitante().getNombre());
        }

        return detRedist;
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

}
