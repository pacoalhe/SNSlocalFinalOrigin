package mx.ift.sns.dao.cpsi;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.cpsi.SolicitudCesionCpsi;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCPSI;

/**
 * Interfaz DAO para Solicitud de Liberación de CPSI.
 * @author X53490DE
 */
public interface ISolicitudCesionCpsiDao extends IBaseDAO<SolicitudCesionCpsi> {

    /**
     * Recupera la lista de Solicitudes de cesión de CSPI completa, sin filtros.
     * @return lista de solicitudes de cesión
     */
    List<SolicitudCesionCpsi> findAllSolicitudesCesion();

    /**
     * Recupera la lista de solicitudes de cesión CSPI en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return lista de solicitudes de cesión filtrada
     */
    List<SolicitudCesionCpsi> findAllSolicitudesCesion(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud);

    /**
     * Recupera el número de solicitudes de cesión que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de cesión
     */
    int findAllSolicitudesCesionCount(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud);

    /**
     * Recupera una solicitud de cesión CSPI por su identificador.
     * @param pConsecutivo Identificador de la solicitud.
     * @return SolicitudCesionCpsi
     */
    SolicitudCesionCpsi getSolicitudCesionById(BigDecimal pConsecutivo);

    /**
     * Recupera una Solicitud de Cesión con todas sus dependencias cargadas: Lista de Cesiones Solicitadas y Oficios.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudCesionCpsi
     */
    SolicitudCesionCpsi getSolicitudCesionEagerLoad(SolicitudCesionCpsi pSolicitud);
}
