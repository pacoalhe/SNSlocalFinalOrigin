package mx.ift.sns.negocio.nng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.nng.CesionSolicitadaNng;
import mx.ift.sns.modelo.nng.LiberacionSolicitadaNng;
import mx.ift.sns.modelo.nng.NumeracionAsignadaNng;
import mx.ift.sns.modelo.nng.NumeracionSolicitadaNng;
import mx.ift.sns.modelo.nng.RedistribucionSolicitadaNng;
import mx.ift.sns.modelo.nng.SolicitudAsignacionNng;
import mx.ift.sns.modelo.nng.SolicitudCesionNng;
import mx.ift.sns.modelo.nng.SolicitudLiberacionNng;
import mx.ift.sns.modelo.nng.SolicitudLineasActivasNng;
import mx.ift.sns.modelo.nng.SolicitudRedistribucionNng;
import mx.ift.sns.modelo.nng.TipoAsignacion;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.PeticionCancelacion;

/**
 * Servicio de Solicitudes de Numeración No Geográfica.
 */
public interface ISolicitudesNngService {

    /**
     * Recupera el catálogo de estados de solicitud.
     * @return List
     */
    List<EstadoSolicitud> findAllEstadosSolicitud();

    /**
     * Méotodo que salva una solicitud de cesión de numeración no geográfica.
     * @param pSolicitudCesion solicitud a salvar
     * @return solicitud persistida
     */
    SolicitudCesionNng saveSolicitudCesion(SolicitudCesionNng pSolicitudCesion);

    /**
     * Recupera una Solicitud de Cesión NNG existente por su identificador.
     * @param pConsecutivo Identificador de la solicitud de cesión nng.
     * @return SolicitudCesionNng
     */
    SolicitudCesionNng getSolicitudCesionById(BigDecimal pConsecutivo);

    /**
     * Recupera una Solicitud de Cesión NNG con todas sus dependencias cargadas: Lista de Cesiones Solicitadas y
     * Oficios.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudCesionNng
     */
    SolicitudCesionNng getSolicitudCesionEagerLoad(SolicitudCesionNng pSolicitud);

    /**
     * Recupera todas las solicitudes de Cesión NNG de Base de Datos.
     * @return List
     */
    List<SolicitudCesionNng> findAllSolicitudesCesion();

    /**
     * Recupera la lista de solicitudes de Cesión NNG en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return lista de solicitudes de cesión filtrada
     */
    List<SolicitudCesionNng> findAllSolicitudesCesion(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Recupera el número de solicitudes de Cesión NNG que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de cesión
     */
    int findAllSolicitudesCesionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Obtiene un tipo de asignacion por su codigo.
     * @param cdg codigo
     * @return TipoAsignacion
     */
    TipoAsignacion getTipoAsignacionById(String cdg);

    /**
     * Obtiene los tipos de asignacion.
     * @return List<TipoAsignacion>
     */
    List<TipoAsignacion> findAllTipoAsignacion();

    /**
     * Obtiene las solicitudes de asignacion no geagraficas.
     * @param filtros busqueda
     * @return lista solicitudes
     * @throws Exception error
     */
    List<SolicitudAsignacionNng> findAllSolicitudesAsignacion(FiltroBusquedaSolicitudes filtros) throws Exception;

    /**
     * Obiente el numero de solicitudes de asignacion no geagraficas.
     * @param filtros busqueda
     * @return total
     * @throws Exception error
     */
    Integer findAllSolicitudesAsignacionCount(FiltroBusquedaSolicitudes filtros) throws Exception;

    /**
     * Obtiene todo a los que que se ha solicitado asignación de numeración por solicitud de asignación.
     * @param sol solicitud
     * @return List
     */
    List<String> findAllClientesBySolicitud(Solicitud sol);

    /**
     * Méotodo que salva una solicitud de asignación de numeración no geográfica.
     * @param solicitud solicitud a salvar
     * @return solicitud persistida
     */
    SolicitudAsignacionNng saveSolicitudAsignacion(SolicitudAsignacionNng solicitud);

    /**
     * Recupera una Solicitud de Asignacion NNG con todas sus dependencias cargadas: Lista de Numeraciones Solicitadas,
     * Rangos Asignados y Oficios.
     * @param pSolicitud solicitud
     * @return solicitud
     */
    SolicitudAsignacionNng getSolicitudAsignacionEagerLoad(SolicitudAsignacionNng pSolicitud);

    /**
     * Si es posible, restablece la numeración afectada por el trámite deshaciendo los cambios.
     * @param pSolicitud Solicitud de Cesión a Modififcar.
     * @return PeticionCancelacion con la información del proceso de cancelación.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelSolicitudCesion(SolicitudCesionNng pSolicitud) throws Exception;

    /**
     * Cancela una cesión.
     * @param pCesSol Información de la cesión.
     * @param pUseCheck Indica si es necesario validar la cancelación previamente.
     * @return Objeto PeticionCancelacion con la información del proceso.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelCesion(CesionSolicitadaNng pCesSol, boolean pUseCheck) throws Exception;

    /**
     * Ejecuta las cesiones solicitadas de todas las solicitudes de cesión pendientes.
     * @throws Exception en caso de error.
     */
    void applyCesionesPendientes() throws Exception;

    /**
     * Genera las notificaciones de las cesiones programadas pendientes de ejecutar.
     * @return Lista de noficitaciones.
     */
    List<String> getNotificacionesCesionesPendientes();

    /**
     * Ejecuta las cesiones solicitadas de una Solicitud de Cesión.
     * @param pSolicitud Información de la Cesión
     * @return Nueva instancia de la Solicitud de Cesión con los cambios
     * @throws Exception error
     */
    SolicitudCesionNng applyCesionesSolicitadas(SolicitudCesionNng pSolicitud) throws Exception;

    /**
     * Devueve la lista de solicitudes NNG, con filtros.
     * @param pFiltrosSolicitud pFiltrosSolicitud
     * @return lista de Solicitud
     * @throws Exception Exception
     */
    List<Solicitud> findAllSolicitudes(FiltroBusquedaSolicitudes pFiltrosSolicitud)
            throws Exception;

    /**
     * Recupera el número de solicitudes NNG que cumplen los filtros dados.
     * @param pFiltrosSolicitud pFiltros filtros de busqueda
     * @return número de solicitudes
     * @throws Exception en caso de error.
     */
    int findAllSolicitudesCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) throws Exception;

    /**
     * Recupera el catálogo de tipos de solicitud NNG.
     * @return List
     * @throws Exception en caso de error.
     */
    List<TipoSolicitud> findAllTiposSolicitud() throws Exception;

    /**
     * Si es posible, restablece la numeración afectada por el trámite deshaciendo los cambios.
     * @param pSolicitud Solicitud de Liberación a Modififcar.
     * @return PeticionCancelacion con la información del proceso de cancelación.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelSolicitudLiberacion(SolicitudLiberacionNng pSolicitud) throws Exception;

    /**
     * Cancela una liberación.
     * @param pLibSol Información de la liberación.
     * @param pUseCheck Indica si es necesario validar la cancelación previamente.
     * @return Objeto PeticionCancelacion con la información del proceso.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelLiberacion(LiberacionSolicitadaNng pLibSol, boolean pUseCheck) throws Exception;

    /**
     * Recupera la lista de solicitudes de liberación en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return lista de solicitudes de liberación filtrada
     */
    List<SolicitudLiberacionNng> findAllSolicitudesLiberacion(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Recupera el número de solicitudes de liberación que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de liberación
     */
    int findAllSolicitudesLiberacionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Méotodo que salva una solicitud de liberación de numeración no geográfica.
     * @param pSolicitud solicitud a salvar
     * @return solicitud persistida
     */
    SolicitudLiberacionNng saveSolicitudLiberacion(SolicitudLiberacionNng pSolicitud);

    /**
     * Recupera una solicitud de liberación por su identificador.
     * @param pConsecutivo Identificador de la solicitud.
     * @return SolicitudLiberacionNng
     */
    SolicitudLiberacionNng getSolicitudLiberacionById(BigDecimal pConsecutivo);

    /**
     * Recupera una Solicitud de Liberacion con todas sus dependencias cargadas: Lista de Liberaciones Solicitadas y
     * Oficios.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudLiberacionNg
     */
    SolicitudLiberacionNng getSolicitudLiberacionEagerLoad(SolicitudLiberacionNng pSolicitud);

    /**
     * Ejecuta las liberaciones solicitadas de una Solicitud de Liberación.
     * @param pSolicitud Información de la liberación
     * @return Nueva instancia de la Solicitud de Liberación con los cambios
     * @throws Exception en caso de error.
     */
    SolicitudLiberacionNng applyLiberacionesSolicitadas(SolicitudLiberacionNng pSolicitud) throws Exception;

    /**
     * Ejecuta las liberaciones solicitadas de todas las solicitudes de liberación pendientes.
     * @throws Exception en caso de error.
     */
    void applyLiberacionesPendientes() throws Exception;

    /**
     * Genera las notificaciones de las liberaciones programadas pendientes de ejecutar.
     * @return Lista de noficitaciones.
     */
    List<String> getNotificacionesLiberacionesPendientes();

    /**
     * Recupera la lista de solicitudes de redistribución en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de búsqueda
     * @return lista de solicitudes de redistribución filtrada
     */
    List<SolicitudRedistribucionNng> findAllSolicitudesRedistribucion(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Recupera el número de solicitudes de redistribucion que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de redistribucion
     */
    int findAllSolicitudesRedistribucionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Méotodo que salva una solicitud de redistribución de numeración no geográfica.
     * @param pSolicitud solicitud a salvar
     * @return solicitud persistida
     */
    SolicitudRedistribucionNng saveSolicitudRedistribucion(SolicitudRedistribucionNng pSolicitud);

    /**
     * Recupera una solicitud de redistribución por su identificador.
     * @param pConsecutivo Identificador de la solicitud.
     * @return SolicitudRedistribucionNng
     */
    SolicitudRedistribucionNng getSolicitudRedistribucionById(BigDecimal pConsecutivo);

    /**
     * Recupera una Solicitud de redistribución con todas sus dependencias cargadas: Lista de Redistribuciones
     * Solicitadas, Aplicadas y Oficios.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudRedistribucionNng
     */
    SolicitudRedistribucionNng getSolicitudRedistribucionEagerLoad(SolicitudRedistribucionNng pSolicitud);

    /**
     * Comprueba si una numeracion solicitada tiene rango asociados.
     * @param numeracionSolicitada NumeracionSolicitadaNng
     * @return true/false
     */
    boolean isNumeracionSolicitadaWithRangos(NumeracionSolicitadaNng numeracionSolicitada);

    /**
     * Ejecuta las redistribuciones solicitadas de una Solicitud de redistribución.
     * @param pSolicitud Información de la Cesión
     * @return Nueva instancia de la Solicitud de redistribución con los cambios
     * @throws Exception error
     */
    SolicitudRedistribucionNng applyRedistribucionesSolicitadas(SolicitudRedistribucionNng pSolicitud) throws Exception;

    /**
     * Si es posible, restablece la numeración afectada por el trámite deshaciendo los cambios.
     * @param pRedSol RedistribucionSolicitadaNng a Modififcar.
     * @param pUseCheck Indica si es necesario o no validar la cancelación de la redistribución
     * @return PeticionCancelacion con la información del proceso de cancelación.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelRedistribucion(RedistribucionSolicitadaNng pRedSol, boolean pUseCheck) throws Exception;

    /**
     * Si es posible, restablece la numeración afectada por el trámite deshaciendo los cambios.
     * @param pSolicitud Solicitud de Redistribución a Modififcar.
     * @return PeticionCancelacion con la información del proceso de cancelación.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelSolicitudRedistribucion(SolicitudRedistribucionNng pSolicitud) throws Exception;

    /**
     * Comprueba si una solictud tiene rangos.
     * @param solicitud solictud
     * @return true/false
     */
    boolean isSolicitudWithRangos(Solicitud solicitud);

    /**
     * Méotodo que salva una solicitud de líneas activas de numeración no geográfica.
     * @param solicitud solicitud a salvar
     * @return solicitud persistida
     */
    SolicitudLineasActivasNng saveSolicitudLineasActivas(SolicitudLineasActivasNng solicitud);

    /**
     * Obtiene las numeraciones asignadas por la solicitud de asignacion.
     * @param solicitud asignacion
     * @return neraciones asignadas
     */
    List<NumeracionAsignadaNng> findAllNumeracionAsignadaBySolicitud(SolicitudAsignacionNng solicitud);

}
