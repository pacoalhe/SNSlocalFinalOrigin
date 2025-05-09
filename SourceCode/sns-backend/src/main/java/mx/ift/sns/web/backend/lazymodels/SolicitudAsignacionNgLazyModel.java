package mx.ift.sns.web.backend.lazymodels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.ng.SolicitudAsignacion;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.ng.asignacion.DetalleConsultaAsignacionNg;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de Solicitudes de Asignación.
 */
public class SolicitudAsignacionNgLazyModel extends LazyDataModel<DetalleConsultaAsignacionNg> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudAsignacionNgLazyModel.class);

    /** Colección de objetos. */
    private List<DetalleConsultaAsignacionNg> dataSource;

    /** Identificador del Componente JSF de mensajes asociado a las búsquedas. */
    private String messagesId = null;

    /** Filtros de búsqueda. */
    private FiltroBusquedaSolicitudes filtros;

    /** Parseador de fechas. */
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    /** Servicio. */
    private INumeracionGeograficaService service;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = true;

    /** Constructor. */
    public SolicitudAsignacionNgLazyModel() {
        dataSource = new ArrayList<DetalleConsultaAsignacionNg>(1);
    }

    @Override
    public DetalleConsultaAsignacionNg getRowData(String rowKey) {
        for (DetalleConsultaAsignacionNg solicitud : dataSource) {
            if (solicitud.getConsecutivo().equals(rowKey)) {
                return solicitud;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(DetalleConsultaAsignacionNg sol) {
        return sol.getConsecutivo().toString();
    }

    @Override
    public List<DetalleConsultaAsignacionNg> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || service == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<DetalleConsultaAsignacionNg>(1);
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
            this.setRowCount(service.findAllSolicitudesAsignacionCount(filtros));

            if (resetPaginacion) {
                filtros.setNumeroPagina(0);
                resetPaginacion = false;
            } else {
                // Se establece la página a mostrar dependiendo de los elementos totales, el primer elemento
                // a mostrar y el número de elementos por página.
                filtros.setNumeroPagina(PaginatorUtil.getPagina(this.getRowCount(),
                        first, filtros.getResultadosPagina()));
            }

            dataSource.clear();

            // Búsqueda con paginación
            List<SolicitudAsignacion> listaSolicitudes = service.findAllSolicitudesAsignacion(filtros);
            for (SolicitudAsignacion sol : listaSolicitudes) {
                dataSource.add(this.getDetalleSolicitud(sol));
            }

            if (LOGGER.isDebugEnabled()) {
                StringBuffer trace = new StringBuffer("Paginando, ");
                trace.append("First(").append(first).append("), ");
                trace.append("PageSize(").append(pageSize).append("), ");
                trace.append("Filters: ").append(filtros.toString());
                LOGGER.debug(trace.toString());
            }

            // Indicaciones si no hay resultados
            if (dataSource.isEmpty() && (!StringUtils.isEmpty(messagesId))) {
                MensajesBean.addInfoMsg(messagesId, "No se han encontrado resultados para la búsqueda indicada");
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado: ", e);
            MensajesBean.addErrorMsg(messagesId, Errores.ERROR_0004);
            dataSource = new ArrayList<DetalleConsultaAsignacionNg>(1);
        }

        return dataSource;
    }

    /**
     * Obtiene el detalle.
     * @param sol solicitud
     * @return DetalleConsultaAsignacion
     */
    private DetalleConsultaAsignacionNg getDetalleSolicitud(SolicitudAsignacion sol) {

        DetalleConsultaAsignacionNg detSol = new DetalleConsultaAsignacionNg();
        detSol.setConsecutivo(sol.getId().toString());
        detSol.setEstatus(sol.getEstadoSolicitud().getDescripcion());
        detSol.setNumOficio(sol.getNumOficioSolicitante());

        if (sol.getProveedorSolicitante() != null) {
            detSol.setSolicitante(sol.getProveedorSolicitante().getNombre());
        }
        if (sol.getFechaSolicitud() != null) {
            detSol.setFechaSolicitud(sdf.format(sol.getFechaSolicitud()));
        }
        if (sol.getFechaAsignacion() != null) {
            detSol.setFechaAsignacion(sdf.format(sol.getFechaAsignacion()));
        }

        // // Fecha de Oficio del Proveedor Solicitante.
        // try {
        // if (!StringUtils.isEmpty(sol.getNumOficioSolicitante())) {
        // Oficio oficioSolicitante = service.getOficio(sol, TipoDestinatario.PST_SOLICITANTE);
        // detSol.setFechaOficio(sdf.format(oficioSolicitante.getFechaOficio()));
        // }
        // } catch (Exception e) {
        // detSol.setFechaOficio("");
        // }

        detSol.setSolicitud(sol);

        return detSol;
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

    /**
     * @return the resetPaginacion
     */
    public boolean isResetPaginacion() {
        return resetPaginacion;
    }

    /**
     * @param resetPaginacion the resetPaginacion to set
     */
    public void setResetPaginacion(boolean resetPaginacion) {
        this.resetPaginacion = resetPaginacion;
    }
}
