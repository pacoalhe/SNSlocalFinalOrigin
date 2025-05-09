package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.nng.LiberacionSolicitadaNng;
import mx.ift.sns.modelo.nng.SolicitudLiberacionNng;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.nng.liberacion.DetalleConsultaLiberacionNng;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de objetos DetalleConsultaLiberacionNng de Solicitudes de Liberación.
 */
public class SolicitudLiberacionNngConsultaLazyModel extends LazyDataModel<DetalleConsultaLiberacionNng> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudLiberacionNngConsultaLazyModel.class);

    /** Colección de objetos. */
    private List<DetalleConsultaLiberacionNng> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaSolicitudes filtros;

    /** Servicio de Numeración No Geográfica. */
    private INumeracionNoGeograficaFacade nngFacade;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = true;

    /** Constructor. */
    public SolicitudLiberacionNngConsultaLazyModel() {
        dataSource = new ArrayList<DetalleConsultaLiberacionNng>(1);
    }

    @Override
    public DetalleConsultaLiberacionNng getRowData(String rowKey) {
        for (DetalleConsultaLiberacionNng detSol : dataSource) {
            if (detSol.getConsecutivo().equals(rowKey)) {
                return detSol;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(DetalleConsultaLiberacionNng pDetSol) {
        return pDetSol.getConsecutivo();
    }

    @Override
    public List<DetalleConsultaLiberacionNng> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || nngFacade == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<DetalleConsultaLiberacionNng>(1);
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
            this.setRowCount(nngFacade.findAllSolicitudesLiberacionCount(filtros));

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
            List<SolicitudLiberacionNng> listaSolicitudes = nngFacade.findAllSolicitudesLiberacion(filtros);
            for (SolicitudLiberacionNng solLib : listaSolicitudes) {
                dataSource.add(this.getDetalleSolicitud(solLib));
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            dataSource = new ArrayList<DetalleConsultaLiberacionNng>(1);
        }

        return dataSource;
    }

    /**
     * Crea un objeto DetalleConsultaLiberacionNng con la información de la Solicitud de Liberación dada.
     * @param pSolicitud Información de la Solicitud de Liberacion.
     * @return Objeto DetalleConsultaLiberacionNng con la información relevante para la tabla.
     */
    private DetalleConsultaLiberacionNng getDetalleSolicitud(SolicitudLiberacionNng pSolicitud) {
        DetalleConsultaLiberacionNng detLib = new DetalleConsultaLiberacionNng();
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
            for (LiberacionSolicitadaNng libSol : pSolicitud.getLiberacionesSolicitadas()) {
                if (fecha == null) {
                    fecha = libSol.getFechaLiberacion();
                } else {
                    if (fecha.compareTo(libSol.getFechaLiberacion()) != 0) {
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
     * Asocia los filtros que se usarán en las búsquedas.
     * @param filtros FiltroBusquedaSolicitudes
     */
    public void setFiltros(FiltroBusquedaSolicitudes filtros) {
        this.filtros = filtros;
    }

    /**
     * Asocia el Facade de Servicios que utilizarán las búsquedas.
     * @param nngFacade the nngFacade to set
     */
    public void setNngFacade(INumeracionNoGeograficaFacade nngFacade) {
        this.nngFacade = nngFacade;
    }

}
