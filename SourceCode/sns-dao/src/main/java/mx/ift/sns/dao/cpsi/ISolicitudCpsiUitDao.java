package mx.ift.sns.dao.cpsi;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.cpsi.SolicitudCpsiUit;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCPSI;

/**
 * Interfaz DAO para Solicitud de códigos CPSI a la UIT.
 * @author X48943LO
 */
public interface ISolicitudCpsiUitDao extends IBaseDAO<SolicitudCpsiUit> {

    /**
     * Recupera la lista de Solicitudes de códigos de CPSI completa, sin filtros.
     * @return lista de solicitudes de asignación
     */
    List<SolicitudCpsiUit> findAllSolicitudesCpsiUit();

    /**
     * Recupera la lista de solicitudes de códigos de CPSI en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return lista de solicitudes de asignación filtrada
     */
    List<SolicitudCpsiUit> findAllSolicitudesCpsiUit(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud);

    /**
     * Recupera el número de solicitudes de códigos CPSI que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de asignación
     */
    int findAllSolicitudesCpsiUitCount(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud);

    /**
     * Recupera una solicitud de códigos CPSI por su identificador.
     * @param pConsecutivo Identificador de la solicitud.
     * @return SolicitudAsignacionCpsi
     */
    SolicitudCpsiUit getSolicitudCpsiUitById(BigDecimal pConsecutivo);

    /**
     * Recupera una Solicitud de códigos CPSI a la UIT con todas sus dependencias cargadas.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudCpsiUit
     */
    SolicitudCpsiUit getSolicitudCpsiUitEagerLoad(SolicitudCpsiUit pSolicitud);

    /**
     * Método que cuenta cuantas solicitudes de códigos a la UIT coinciden con los parámetros.
     * @param filtro filtro
     * @return int
     */
    int findSolicitudCpsiUitByCodAndEstatusCount(FiltroBusquedaSolicitudesCPSI filtro);

}
