package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.cpsn.LiberacionSolicitadaCpsn;
import mx.ift.sns.modelo.cpsn.SolicitudLiberacionCpsn;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCpsn;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.cpsn.ICodigoCPSNFacade;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.cpsn.liberacion.DetalleConsultaLiberacionCpsn;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de Solicitudes de Liberación CPSN.
 */
public class SolicitudLiberacionCpsnConsultaLazyModel extends LazyDataModel<DetalleConsultaLiberacionCpsn> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudLiberacionCpsnConsultaLazyModel.class);

    /** Colección de objetos. */
    private List<DetalleConsultaLiberacionCpsn> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaSolicitudesCpsn filtros;

    /** Facade de Servicios. */
    private ICodigoCPSNFacade facade;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = true;

    /** Constructor. */
    public SolicitudLiberacionCpsnConsultaLazyModel() {
        dataSource = new ArrayList<DetalleConsultaLiberacionCpsn>(1);
    }

    @Override
    public DetalleConsultaLiberacionCpsn getRowData(String rowKey) {
        for (DetalleConsultaLiberacionCpsn detalle : dataSource) {
            if (detalle.getConsecutivo().toString().equals(rowKey)) {
                return detalle;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(DetalleConsultaLiberacionCpsn detalle) {
        return detalle.getConsecutivo().toString();
    }

    @Override
    public List<DetalleConsultaLiberacionCpsn> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || facade == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<DetalleConsultaLiberacionCpsn>(1);
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
            this.setRowCount(facade.findAllSolicitudesLiberacionCount(filtros));

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
            List<SolicitudLiberacionCpsn> listaSolicitudes = facade.findAllSolicitudesLiberacion(filtros);
            for (SolicitudLiberacionCpsn sol : listaSolicitudes) {
                dataSource.add(this.getDetalleSolicitud(sol));
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            dataSource = new ArrayList<DetalleConsultaLiberacionCpsn>(1);
        }

        return dataSource;
    }

    /**
     * Genera las clases DetalleConsultaLiberacionCpsn con la información de las solicitudes de Liberación CPSN
     * encontradas.
     * @param pSolicitud Información de la Solicitud.
     * @return DetalleConsultaLiberacionCpsn
     */
    private DetalleConsultaLiberacionCpsn getDetalleSolicitud(SolicitudLiberacionCpsn pSolicitud) {
        DetalleConsultaLiberacionCpsn detLib = new DetalleConsultaLiberacionCpsn();
        detLib.setConsecutivo(pSolicitud.getId().toString());
        detLib.setEstatus(pSolicitud.getEstadoSolicitud().getDescripcion());
        detLib.setNumOficio(pSolicitud.getNumOficioSolicitante());
        detLib.setCancelarDisponible(!pSolicitud.getEstadoSolicitud().getCodigo()
                .equals(EstadoSolicitud.SOLICITUD_CANCELADA));
        detLib.setFechaSolicitud(FechasUtils.fechaToString(pSolicitud.getFechaSolicitud()));
        detLib.setFechaLiberacion(FechasUtils.fechaToString(pSolicitud.getFechaAsignacion()));

        if (pSolicitud.getProveedorSolicitante() != null) {
            detLib.setSolicitante(pSolicitud.getProveedorSolicitante().getNombre());
        }

        if (pSolicitud.getLiberacionesSolicitadas() != null) {
            boolean multiples = false;
            Date fecha = null;
            for (LiberacionSolicitadaCpsn libSol : pSolicitud.getLiberacionesSolicitadas()) {
                if (fecha == null) {
                    fecha = libSol.getFechaImplementacion();
                } else {
                    if (fecha.compareTo(libSol.getFechaImplementacion()) != 0) {
                        multiples = true;
                        break;
                    }
                }
            }
            if (multiples) {
                detLib.setFechaLiberacion("Múltiples");
            } else {
                detLib.setFechaLiberacion(FechasUtils.fechaToString(fecha));
            }
        }

        return detLib;
    }

    /** Resetea los valores de búsqueda. */
    public void clear() {
        resetPaginacion = true;
    }

    // GETTERS & SETTERS

    /**
     * Filtros de búsqueda.
     * @param filtros FiltroBusquedaSolicitudesCpsn
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
