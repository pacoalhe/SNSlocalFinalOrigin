package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.cpsn.CesionSolicitadaCPSN;
import mx.ift.sns.modelo.cpsn.SolicitudCesionCPSN;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCpsn;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.cpsn.ICodigoCPSNFacade;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.cpsn.cesion.DetalleConsultaCesionCPSN;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de objetos DetalleConsultaCesionCPSN de Solicitudes de Cesión.
 */
public class SolicitudCesionCpsnConsultaLazyModel extends LazyDataModel<DetalleConsultaCesionCPSN> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudCesionCpsnConsultaLazyModel.class);

    /** Colección de objetos. */
    private List<DetalleConsultaCesionCPSN> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaSolicitudesCpsn filtros;

    /** Servicio. */
    private ICodigoCPSNFacade service;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = true;

    /** Constructor. */
    public SolicitudCesionCpsnConsultaLazyModel() {
        dataSource = new ArrayList<DetalleConsultaCesionCPSN>(1);
    }

    @Override
    public DetalleConsultaCesionCPSN getRowData(String rowKey) {
        for (DetalleConsultaCesionCPSN detSol : dataSource) {
            if (detSol.getConsecutivo().equals(rowKey)) {
                return detSol;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(DetalleConsultaCesionCPSN pDetSol) {
        return pDetSol.getConsecutivo();
    }

    @Override
    public List<DetalleConsultaCesionCPSN> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || service == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<DetalleConsultaCesionCPSN>(1);
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
            this.setRowCount(service.findAllSolicitudesCesionCount(filtros));

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
            List<SolicitudCesionCPSN> listaSolicitudes = service.findAllSolicitudesCesion(filtros);
            for (SolicitudCesionCPSN solCes : listaSolicitudes) {
                dataSource.add(this.getDetalleCesionCPSN(solCes));
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            dataSource = new ArrayList<DetalleConsultaCesionCPSN>(1);
        }

        return dataSource;
    }

    /**
     * Crea un objeto DetalleConsultaCesionCPSN con la información de la Solicitud de Cesión dada.
     * @param pSolicitud Información de la Solicitud de Cesión.
     * @return Objeto DetalleConsultaCesionCPSN con la información relevante para la tabla.
     */
    private DetalleConsultaCesionCPSN getDetalleCesionCPSN(SolicitudCesionCPSN pSolicitud) {
        DetalleConsultaCesionCPSN detCes = new DetalleConsultaCesionCPSN();
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
        if (pSolicitud.getCesionesSolicitadasCPSN() != null) {
            boolean multiples = false;
            Date fecha = null;
            for (CesionSolicitadaCPSN cesSol : pSolicitud.getCesionesSolicitadasCPSN()) {
                if (fecha == null) {
                    fecha = cesSol.getFechaImplementacion();
                } else {
                    if (fecha.compareTo(cesSol.getFechaImplementacion()) != 0) {
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
     * @param filtros FiltroBusquedaSolicitudesCPSN
     */
    public void setFiltros(FiltroBusquedaSolicitudesCpsn filtros) {
        this.filtros = filtros;
    }

    /**
     * Asocia el servicio que se usarán en las búsquedas.
     * @param service ICodigoCPSNFacade
     */
    public void setService(ICodigoCPSNFacade service) {
        this.service = service;
    }
}
