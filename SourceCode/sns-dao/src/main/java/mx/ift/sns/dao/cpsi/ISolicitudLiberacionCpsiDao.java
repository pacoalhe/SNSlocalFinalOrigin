package mx.ift.sns.dao.cpsi;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.cpsi.SolicitudLiberacionCpsi;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCPSI;

/**
 * Interfaz DAO para Solicitud de Liberación de CPSI.
 * @author X53490DE
 */
public interface ISolicitudLiberacionCpsiDao extends IBaseDAO<SolicitudLiberacionCpsi> {

    /**
     * Recupera la lista de Solicitudes de liberación completa, sin filtros.
     * @return lista de solicitudes de liberación
     */
    List<SolicitudLiberacionCpsi> findAllSolicitudesLiberacion();

    /**
     * Recupera la lista de solicitudes de liberación en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return lista de solicitudes de liberación filtrada
     */
    List<SolicitudLiberacionCpsi> findAllSolicitudesLiberacion(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud);

    /**
     * Recupera el número de solicitudes de liberación que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de liberación
     */
    int findAllSolicitudesLiberacionCount(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud);

    /**
     * Recupera una solicitud de liberación por su identificador.
     * @param pConsecutivo Identificador de la solicitud.
     * @return SolicitudLiberacionCpsi
     */
    SolicitudLiberacionCpsi getSolicitudLiberacionById(BigDecimal pConsecutivo);

    /**
     * Recupera una Solicitud de Liberacion con todas sus dependencias cargadas.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudLiberacionCpsi
     */
    SolicitudLiberacionCpsi getSolicitudLiberacionEagerLoad(SolicitudLiberacionCpsi pSolicitud);

}
