package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.cpsi.CesionSolicitadaCpsi;
import mx.ift.sns.modelo.cpsi.SolicitudCesionCpsi;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCPSI;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.cpsi.ICodigoCPSIFacade;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.cpsi.cesion.DetalleConsultaCesionCpsi;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de Solicitudes de Cesión CPSI.
 */
public class SolicitudCesionCpsiConsultaLazyModel extends LazyDataModel<DetalleConsultaCesionCpsi> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudCesionCpsiConsultaLazyModel.class);

    /** Colección de objetos. */
    private List<DetalleConsultaCesionCpsi> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaSolicitudesCPSI filtros;

    /** Facade de Servicios. */
    private ICodigoCPSIFacade facade;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = true;

    /** Constructor. */
    public SolicitudCesionCpsiConsultaLazyModel() {
        dataSource = new ArrayList<DetalleConsultaCesionCpsi>(1);
    }

    @Override
    public DetalleConsultaCesionCpsi getRowData(String rowKey) {
        for (DetalleConsultaCesionCpsi detalle : dataSource) {
            if (detalle.getConsecutivo().toString().equals(rowKey)) {
                return detalle;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(DetalleConsultaCesionCpsi detalle) {
        return detalle.getConsecutivo().toString();
    }

    @Override
    public List<DetalleConsultaCesionCpsi> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || facade == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<DetalleConsultaCesionCpsi>(1);
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
            this.setRowCount(facade.findAllSolicitudesCesionCount(filtros));

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
            List<SolicitudCesionCpsi> listaSolicitudes = facade.findAllSolicitudesCesion(filtros);
            for (SolicitudCesionCpsi sol : listaSolicitudes) {
                dataSource.add(this.getDetalleSolicitud(sol));
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado: ", e);
            dataSource = new ArrayList<DetalleConsultaCesionCpsi>(1);
        }

        return dataSource;
    }

    /**
     * Genera las clases DetalleConsultaCesionCpsi con la información de las solicitudes de Liberación CPSN encontradas.
     * @param pSolicitud Información de la Solicitud.
     * @return DetalleConsultaLiberacionCpsn
     */
    private DetalleConsultaCesionCpsi getDetalleSolicitud(SolicitudCesionCpsi pSolicitud) {
        DetalleConsultaCesionCpsi detCes = new DetalleConsultaCesionCpsi();
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
            for (CesionSolicitadaCpsi cesSol : pSolicitud.getCesionesSolicitadas()) {
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
     * @param filtros the filtros to set
     */
    public void setFiltros(FiltroBusquedaSolicitudesCPSI filtros) {
        this.filtros = filtros;
    }

    /**
     * Asocia el servicio que se usarán en las búsquedas.
     * @param facade INumeracionGeograficaService
     */
    public void setFacade(ICodigoCPSIFacade facade) {
        this.facade = facade;
    }

}
