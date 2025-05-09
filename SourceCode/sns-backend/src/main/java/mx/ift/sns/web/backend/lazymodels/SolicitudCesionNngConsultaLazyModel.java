package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.nng.CesionSolicitadaNng;
import mx.ift.sns.modelo.nng.SolicitudCesionNng;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.nng.cesion.DetalleConsultaCesionNng;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de objetos DetalleConsultaCesionNng de Solicitudes de Cesión de
 * Numeración No Geográfica.
 */
public class SolicitudCesionNngConsultaLazyModel extends LazyDataModel<DetalleConsultaCesionNng> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudCesionNngConsultaLazyModel.class);

    /** Colección de objetos. */
    private List<DetalleConsultaCesionNng> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaSolicitudes filtros;

    /** Servicio de Numeración No Geográfica. */
    private INumeracionNoGeograficaFacade nngFacade;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = true;

    /** Constructor. */
    public SolicitudCesionNngConsultaLazyModel() {
        dataSource = new ArrayList<DetalleConsultaCesionNng>(1);
    }

    @Override
    public DetalleConsultaCesionNng getRowData(String rowKey) {
        for (DetalleConsultaCesionNng detSol : dataSource) {
            if (detSol.getConsecutivo().equals(rowKey)) {
                return detSol;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(DetalleConsultaCesionNng pDetSol) {
        return pDetSol.getConsecutivo();
    }

    @Override
    public List<DetalleConsultaCesionNng> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || nngFacade == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<DetalleConsultaCesionNng>(1);
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
            this.setRowCount(nngFacade.findAllSolicitudesCesionCount(filtros));

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
            List<SolicitudCesionNng> listaSolicitudes = nngFacade.findAllSolicitudesCesion(filtros);
            for (SolicitudCesionNng solCes : listaSolicitudes) {
                dataSource.add(this.getDetalleCesion(solCes));
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado: ", e);
            dataSource = new ArrayList<DetalleConsultaCesionNng>(1);
        }

        return dataSource;
    }

    /**
     * Crea un objeto DetalleConsultaCesionNng con la información de la Solicitud de Cesión NNG dada.
     * @param pSolicitud Información de la Solicitud de Cesión NNG.
     * @return Objeto DetalleConsultaCesionNng con la información relevante para la tabla.
     */
    private DetalleConsultaCesionNng getDetalleCesion(SolicitudCesionNng pSolicitud) {
        DetalleConsultaCesionNng detCes = new DetalleConsultaCesionNng();
        detCes.setConsecutivo(pSolicitud.getId().toString());
        detCes.setEstatus(pSolicitud.getEstadoSolicitud().getDescripcion());
        detCes.setNumOficio(pSolicitud.getNumOficioSolicitante());
        detCes.setCancelarDisponible(!pSolicitud.getEstadoSolicitud().getCodigo()
                .equals(EstadoSolicitud.SOLICITUD_CANCELADA));
        detCes.setFechaSolicitud(FechasUtils.fechaToString(pSolicitud.getFechaSolicitud()));
        detCes.setFechaAsignacion(FechasUtils.fechaToString(pSolicitud.getFechaAsignacion()));

        if (pSolicitud.getProveedorSolicitante() != null) {
            detCes.setCedente(pSolicitud.getProveedorSolicitante().getNombre());
        }
        if (pSolicitud.getProveedorCesionario() != null) {
            detCes.setCesionario(pSolicitud.getProveedorCesionario().getNombre());
        }

        // Revisamos las fechas de Cesión. Si son iguales serán la fecha que se indique. Si no,
        // se mostrará "Multiples"
        if (pSolicitud.getCesionesSolicitadas() != null) {
            boolean multiples = false;
            Date fecha = null;
            for (CesionSolicitadaNng cesSol : pSolicitud.getCesionesSolicitadas()) {
                if (fecha == null) {
                    fecha = cesSol.getFechaCesion();
                } else {
                    if (fecha.compareTo(cesSol.getFechaCesion()) != 0) {
                        multiples = true;
                        break;
                    }
                }
            }
            if (multiples) {
                detCes.setFechaImplementacion("Múltiples");
            } else {
                detCes.setFechaImplementacion(FechasUtils.fechaToString(fecha));
            }
        }

        return detCes;
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
