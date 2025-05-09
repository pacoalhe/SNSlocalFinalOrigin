package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.ng.LiberacionSolicitadaNg;
import mx.ift.sns.modelo.ng.SolicitudLiberacionNg;
import mx.ift.sns.modelo.oficios.Oficio;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.ng.liberacion.DetalleConsultaLiberacionNg;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de objetos DetalleConsultaLiberacionNg de Solicitudes de Liberación.
 */
public class SolicitudLiberacionNgConsultaLazyModel extends LazyDataModel<DetalleConsultaLiberacionNg> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudLiberacionNgConsultaLazyModel.class);

    /** Colección de objetos. */
    private List<DetalleConsultaLiberacionNg> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaSolicitudes filtros;

    /** Servicio. */
    private INumeracionGeograficaService service;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = true;

    /** Constructor. */
    public SolicitudLiberacionNgConsultaLazyModel() {
        dataSource = new ArrayList<DetalleConsultaLiberacionNg>(1);
    }

    @Override
    public DetalleConsultaLiberacionNg getRowData(String rowKey) {
        for (DetalleConsultaLiberacionNg detSol : dataSource) {
            if (detSol.getConsecutivo().equals(rowKey)) {
                return detSol;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(DetalleConsultaLiberacionNg pDetSol) {
        return pDetSol.getConsecutivo();
    }

    @Override
    public List<DetalleConsultaLiberacionNg> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || service == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<DetalleConsultaLiberacionNg>(1);
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
            this.setRowCount(service.findAllSolicitudesLiberacionCount(filtros));

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
            List<SolicitudLiberacionNg> listaSolicitudes = service.findAllSolicitudesLiberacion(filtros);
            for (SolicitudLiberacionNg solLib : listaSolicitudes) {
                dataSource.add(this.getDetalleSolicitud(solLib));
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            dataSource = new ArrayList<DetalleConsultaLiberacionNg>(1);
        }

        return dataSource;
    }

    /**
     * Crea un objeto DetalleConsultaLiberacionNg con la información de la Solicitud de Liberación dada.
     * @param pSolicitud Información de la Solicitud de Liberacion.
     * @return Objeto DetalleConsultaLiberacionNg con la información relevante para la tabla.
     */
    private DetalleConsultaLiberacionNg getDetalleSolicitud(SolicitudLiberacionNg pSolicitud) {
        DetalleConsultaLiberacionNg detLib = new DetalleConsultaLiberacionNg();
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
            for (LiberacionSolicitadaNg libSol : pSolicitud.getLiberacionesSolicitadas()) {
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

        // Fecha de Oficio del Proveedor Solicitante.
        try {
            if (!StringUtils.isEmpty(pSolicitud.getNumOficioSolicitante())) {
                Oficio oficioSolicitante = service.getOficio(pSolicitud, TipoDestinatario.PST_SOLICITANTE);
                detLib.setFechaOficio(FechasUtils.fechaToString(oficioSolicitante.getFechaOficio()));
            }
        } catch (Exception e) {
            detLib.setFechaOficio("");
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
     * Asocia el servicio que se usarán en las búsquedas.
     * @param service INumeracionGeograficaService
     */
    public void setService(INumeracionGeograficaService service) {
        this.service = service;
    }

}
