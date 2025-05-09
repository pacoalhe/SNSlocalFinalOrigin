package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.cpsn.SolicitudAsignacionCpsn;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCpsn;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.cpsn.ICodigoCPSNFacade;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.cpsn.asignacion.DetalleConsultaAsignacionCpsn;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de Solicitudes de Asignación CPSN.
 */
public class SolicitudAsignacionCpsnConsultaLazyModel extends LazyDataModel<DetalleConsultaAsignacionCpsn> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudAsignacionCpsnConsultaLazyModel.class);

    /** Colección de objetos. */
    private List<DetalleConsultaAsignacionCpsn> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaSolicitudesCpsn filtros;

    /** Servicio. */
    private ICodigoCPSNFacade facade;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = true;

    /** Constructor. */
    public SolicitudAsignacionCpsnConsultaLazyModel() {
        dataSource = new ArrayList<DetalleConsultaAsignacionCpsn>(1);
    }

    @Override
    public DetalleConsultaAsignacionCpsn getRowData(String rowKey) {
        for (DetalleConsultaAsignacionCpsn detalle : dataSource) {
            if (detalle.getConsecutivo().toString().equals(rowKey)) {
                return detalle;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(DetalleConsultaAsignacionCpsn detalle) {
        return detalle.getConsecutivo().toString();
    }

    @Override
    public List<DetalleConsultaAsignacionCpsn> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || facade == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<DetalleConsultaAsignacionCpsn>(1);
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

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(filtros.toString());
            }

            dataSource.clear();
            List<SolicitudAsignacionCpsn> listaSolicitudes = facade.findAllSolicitudesAsignacion(filtros);
            for (SolicitudAsignacionCpsn sol : listaSolicitudes) {
                dataSource.add(this.getDetalleSolicitud(sol));
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            dataSource = new ArrayList<DetalleConsultaAsignacionCpsn>(1);
        }

        return dataSource;
    }

    /**
     * Obtiene el detalle.
     * @param pSolicitud solicitud
     * @return DetalleConsultaAsignacion
     */
    private DetalleConsultaAsignacionCpsn getDetalleSolicitud(SolicitudAsignacionCpsn pSolicitud) {

        DetalleConsultaAsignacionCpsn detSol = new DetalleConsultaAsignacionCpsn();
        detSol.setConsecutivo(pSolicitud.getId().toString());
        detSol.setEstatus(pSolicitud.getEstadoSolicitud().getDescripcion());
        detSol.setNumOficio(pSolicitud.getNumOficioSolicitante());
        detSol.setCancelarDisponible(!pSolicitud.getEstadoSolicitud().getCodigo()
                .equals(EstadoSolicitud.SOLICITUD_CANCELADA));
        detSol.setFechaSolicitud(FechasUtils.fechaToString(pSolicitud.getFechaSolicitud()));
        detSol.setFechaAsignacion(FechasUtils.fechaToString(pSolicitud.getFechaAsignacion()));

        if (pSolicitud.getProveedorSolicitante() != null) {
            detSol.setProveedor(pSolicitud.getProveedorSolicitante().getNombre());
        }

        return detSol;
    }

    /** Resetea los valores de búsqueda. */
    public void clear() {
        resetPaginacion = true;
    }

    // GETTERS & SETTERS

    /**
     * @param filtros the filtros to set
     */
    public void setFiltros(FiltroBusquedaSolicitudesCpsn filtros) {
        this.filtros = filtros;
    }

    /**
     * Asocia el servicio que se usarán en las búsquedas.
     * @param facade INumeracionGeograficaService
     */
    public void setFacade(ICodigoCPSNFacade facade) {
        this.facade = facade;
    }

}
