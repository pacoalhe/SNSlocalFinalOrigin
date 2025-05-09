package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.cpsi.SolicitudAsignacionCpsi;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCPSI;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.cpsi.ICodigoCPSIFacade;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.cpsi.asignacion.DetalleConsultaAsignacionCpsi;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de Solicitudes de Asignación CPSI.
 */
public class SolicitudAsignacionCpsiConsultaLazyModel extends LazyDataModel<DetalleConsultaAsignacionCpsi> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudAsignacionCpsiConsultaLazyModel.class);

    /** Colección de objetos. */
    private List<DetalleConsultaAsignacionCpsi> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaSolicitudesCPSI filtros;

    /** Facade de Servicios. */
    private ICodigoCPSIFacade facade;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = true;

    /** Constructor. */
    public SolicitudAsignacionCpsiConsultaLazyModel() {
        dataSource = new ArrayList<DetalleConsultaAsignacionCpsi>(1);
    }

    @Override
    public DetalleConsultaAsignacionCpsi getRowData(String rowKey) {
        for (DetalleConsultaAsignacionCpsi detalle : dataSource) {
            if (detalle.getConsecutivo().toString().equals(rowKey)) {
                return detalle;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(DetalleConsultaAsignacionCpsi detalle) {
        return detalle.getConsecutivo().toString();
    }

    @Override
    public List<DetalleConsultaAsignacionCpsi> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || facade == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<DetalleConsultaAsignacionCpsi>(1);
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
            List<SolicitudAsignacionCpsi> listaSolicitudes = facade.findAllSolicitudesAsignacion(filtros);
            for (SolicitudAsignacionCpsi sol : listaSolicitudes) {
                dataSource.add(this.getDetalleSolicitud(sol));
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado: ", e);
            dataSource = new ArrayList<DetalleConsultaAsignacionCpsi>(1);
        }

        return dataSource;
    }

    /**
     * Genera las clases DetalleConsultaAsignacionCpsi con la información de las solicitudes de Asignación CPSI
     * encontradas.
     * @param pSolicitud Información de la Solicitud.
     * @return DetalleConsultaAsignacionCpsi
     */
    private DetalleConsultaAsignacionCpsi getDetalleSolicitud(SolicitudAsignacionCpsi pSolicitud) {
        DetalleConsultaAsignacionCpsi detAsig = new DetalleConsultaAsignacionCpsi();
        detAsig.setConsecutivo(pSolicitud.getId().toString());
        detAsig.setEstatus(pSolicitud.getEstadoSolicitud().getDescripcion());
        detAsig.setNumOficio(pSolicitud.getNumOficioSolicitante());
        detAsig.setCancelarDisponible(!pSolicitud.getEstadoSolicitud().getCodigo()
                .equals(EstadoSolicitud.SOLICITUD_CANCELADA));
        detAsig.setFechaSolicitud(FechasUtils.fechaToString(pSolicitud.getFechaSolicitud()));
        detAsig.setFechaAsignacion(FechasUtils.fechaToString(pSolicitud.getFechaAsignacion()));

        if (pSolicitud.getProveedorSolicitante() != null) {
            detAsig.setSolicitante(pSolicitud.getProveedorSolicitante().getNombre());
        }

        return detAsig;
    }

    /** Resetea los valores de búsqueda. */
    public void clear() {
        resetPaginacion = true;
    }

    // GETTERS & SETTERS

    /**
     * Filtros de búsqueda.
     * @param filtros FiltroBusquedaSolicitudesCPSI
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
