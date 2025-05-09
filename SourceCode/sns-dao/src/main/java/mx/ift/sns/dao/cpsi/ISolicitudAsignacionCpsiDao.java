package mx.ift.sns.dao.cpsi;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.cpsi.SolicitudAsignacionCpsi;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCPSI;

/**
 * Interfaz DAO para Solicitud de Asignación de CPSI.
 * @author X53490DE
 */
public interface ISolicitudAsignacionCpsiDao extends IBaseDAO<SolicitudAsignacionCpsi> {

    /**
     * Recupera la lista de Solicitudes de asignación completa, sin filtros.
     * @return lista de solicitudes de asignación
     */
    List<SolicitudAsignacionCpsi> findAllSolicitudesAsignacion();

    /**
     * Recupera la lista de solicitudes de asignación en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return lista de solicitudes de asignación filtrada
     */
    List<SolicitudAsignacionCpsi> findAllSolicitudesAsignacion(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud);

    /**
     * Recupera el número de solicitudes de asignación que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de asignación
     */
    int findAllSolicitudesAsignacionCount(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud);

    /**
     * Recupera una solicitud de asignación por su identificador.
     * @param pConsecutivo Identificador de la solicitud.
     * @return SolicitudAsignacionCpsi
     */
    SolicitudAsignacionCpsi getSolicitudAsignacionById(BigDecimal pConsecutivo);

    /**
     * Recupera una Solicitud de Asignacion con todas sus dependencias cargadas.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudAsignacionCpsi
     */
    SolicitudAsignacionCpsi getSolicitudAsignacionEagerLoad(SolicitudAsignacionCpsi pSolicitud);

}
