package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.cpsi.SolicitudCpsiUit;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCPSI;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.cpsi.ICodigoCPSIFacade;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.cpsi.solicitud.DetalleConsultaSolicitudCpsiUit;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de Solicitudes de Asignación CPSI.
 */
public class SolicitudCpsiUitConsultaLazyModel extends LazyDataModel<DetalleConsultaSolicitudCpsiUit> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudCpsiUitConsultaLazyModel.class);

    /** Colección de objetos. */
    private List<DetalleConsultaSolicitudCpsiUit> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaSolicitudesCPSI filtros;

    /** Facade de Servicios. */
    private ICodigoCPSIFacade facade;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = true;

    /** Constructor. */
    public SolicitudCpsiUitConsultaLazyModel() {
        dataSource = new ArrayList<DetalleConsultaSolicitudCpsiUit>(1);
    }

    @Override
    public DetalleConsultaSolicitudCpsiUit getRowData(String rowKey) {
        for (DetalleConsultaSolicitudCpsiUit detalle : dataSource) {
            if (detalle.getConsecutivo().toString().equals(rowKey)) {
                return detalle;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(DetalleConsultaSolicitudCpsiUit detalle) {
        return detalle.getConsecutivo().toString();
    }

    @Override
    public List<DetalleConsultaSolicitudCpsiUit> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || facade == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<DetalleConsultaSolicitudCpsiUit>(1);
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
            this.setRowCount(facade.findAllSolicitudesCpsiUitCount(filtros));

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
            List<SolicitudCpsiUit> listaSolicitudes = facade.findAllSolicitudesCpsiUit(filtros);
            for (SolicitudCpsiUit sol : listaSolicitudes) {
                dataSource.add(this.getDetalleSolicitud(sol));
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            dataSource = new ArrayList<DetalleConsultaSolicitudCpsiUit>(1);
        }

        return dataSource;
    }

    /**
     * Genera las clases DetalleConsultaSolicitudCpsiUit con la información de las solicitud de CPSI a la UIT.
     * @param pSolicitud Información de la Solicitud.
     * @return DetalleConsultaSolicitudCpsiUit
     */
    private DetalleConsultaSolicitudCpsiUit getDetalleSolicitud(SolicitudCpsiUit pSolicitud) {
        DetalleConsultaSolicitudCpsiUit detAsig = new DetalleConsultaSolicitudCpsiUit();
        detAsig.setConsecutivo(pSolicitud.getId().toString());
        detAsig.setEstatus(pSolicitud.getEstadoSolicitud().getDescripcion());
        detAsig.setNumOficio(pSolicitud.getNumOficioSolicitante());
        detAsig.setCantidadSolicitada(pSolicitud.getNumCpsiSolicitados());
        detAsig.setCancelarDisponible(!pSolicitud.getEstadoSolicitud().getCodigo()
                .equals(EstadoSolicitud.SOLICITUD_CANCELADA));

        if (pSolicitud.getCpsiUitEntregado() != null) {
            detAsig.setCantidadEntregada(pSolicitud.getCpsiUitEntregado().size());
        }

        if (pSolicitud.getFechaSolicitud() != null) {
            detAsig.setFechaSolicitud(FechasUtils.fechaToString(pSolicitud.getFechaSolicitud()));
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
     * @param filtros filtros to set
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
