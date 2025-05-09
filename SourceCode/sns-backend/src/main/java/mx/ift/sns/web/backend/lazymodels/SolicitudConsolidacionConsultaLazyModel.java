package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.ng.AbnConsolidar;
import mx.ift.sns.modelo.ng.SolicitudConsolidacion;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.ng.consolidacion.DetalleConsultaConsolidacion;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de Solicitudes de Liberación.
 */
public class SolicitudConsolidacionConsultaLazyModel extends LazyDataModel<DetalleConsultaConsolidacion> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudConsolidacionConsultaLazyModel.class);

    /** Colección de objetos. */
    private List<DetalleConsultaConsolidacion> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaSolicitudes filtros;

    /** Servicio. */
    private INumeracionGeograficaService service;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = true;

    /** Constructor. */
    public SolicitudConsolidacionConsultaLazyModel() {
        dataSource = new ArrayList<DetalleConsultaConsolidacion>(1);
    }

    @Override
    public DetalleConsultaConsolidacion getRowData(String rowKey) {
        for (DetalleConsultaConsolidacion detSol : dataSource) {
            if (detSol.getConsecutivo().toString().equals(rowKey)) {
                return detSol;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(DetalleConsultaConsolidacion pDetSol) {
        return pDetSol.getConsecutivo().toString();
    }

    @Override
    public List<DetalleConsultaConsolidacion> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || service == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<DetalleConsultaConsolidacion>(1);
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
            this.setRowCount(service.findAllSolicitudesConsolidacionCount(filtros));

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

            // Búsqueda con paginación
            dataSource.clear();
            List<SolicitudConsolidacion> listaSolicitudes = service.findAllSolicitudesConsolidacion(filtros);
            for (SolicitudConsolidacion solConso : listaSolicitudes) {
                dataSource.add(this.getDetalleConsolidacion(solConso));
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            dataSource = new ArrayList<DetalleConsultaConsolidacion>(1);
        }

        return dataSource;
    }

    /**
     * Crea un objeto DetalleConsultaConsolidacion con la información de la Solicitud de Consolidación dada.
     * @param pSolicitud Información de la Solicitud de Consolidación.
     * @return Objeto DetalleConsultaConsolidacion con la información relevante para la tabla.
     */
    private DetalleConsultaConsolidacion getDetalleConsolidacion(SolicitudConsolidacion pSolicitud) {
        DetalleConsultaConsolidacion detConso = new DetalleConsultaConsolidacion();
        detConso.setConsecutivo(pSolicitud.getId().toString());
        detConso.setEstatus(pSolicitud.getEstadoSolicitud().getDescripcion());
        detConso.setCancelarDisponible(!pSolicitud.getEstadoSolicitud().getCodigo()
                .equals(EstadoSolicitud.SOLICITUD_CANCELADA));
        if (pSolicitud.getAbnEntrega() != null) {
            detConso.setAbnEntrega(pSolicitud.getAbnEntrega().getCodigoAbn().toString());
        }
        if (pSolicitud.getAbnRecibe() != null) {
            detConso.setAbnRecibe(pSolicitud.getAbnRecibe().getCodigoAbn().toString());
        }

        // Revisamos las fechas de Consolidación. Si son iguales serán la fecha que se indique. Si no,
        // se mostrará "Multiples"
        if (pSolicitud.getAbnsConsolidados() != null) {
            boolean multiples = false;
            Date fecha = null;
            for (AbnConsolidar abnConso : pSolicitud.getAbnsConsolidados()) {
                if (fecha == null) {
                    fecha = abnConso.getFechaConsolidacion();
                } else {
                    if (fecha.compareTo(abnConso.getFechaConsolidacion()) != 0) {
                        multiples = true;
                        break;
                    }
                }
            }
            if (multiples) {
                detConso.setFechaConsolidacion("Múltiples");
            } else {
                detConso.setFechaConsolidacion(FechasUtils.fechaToString(fecha));
            }
        }

        return detConso;
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
