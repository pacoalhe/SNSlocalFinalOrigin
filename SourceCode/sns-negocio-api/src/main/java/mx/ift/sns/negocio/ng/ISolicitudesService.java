package mx.ift.sns.negocio.ng;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import mx.ift.sns.modelo.abn.PoblacionAbn;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.ng.CesionSolicitadaNg;
import mx.ift.sns.modelo.ng.LiberacionSolicitadaNg;
import mx.ift.sns.modelo.ng.NumeracionSolicitada;
import mx.ift.sns.modelo.ng.RedistribucionSolicitadaNg;
import mx.ift.sns.modelo.ng.SolicitudAsignacion;
import mx.ift.sns.modelo.ng.SolicitudCesionNg;
import mx.ift.sns.modelo.ng.SolicitudConsolidacion;
import mx.ift.sns.modelo.ng.SolicitudLiberacionNg;
import mx.ift.sns.modelo.ng.SolicitudLineasActivas;
import mx.ift.sns.modelo.ng.SolicitudRedistribucionNg;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.PeticionCancelacion;

/**
 * Servicio de Solicitudes.
 */
public interface ISolicitudesService {

    /**
     * Méotodo que salva una solicitud de liberación.
     * @param pSolicitud solicitud a salvar
     * @return solicitud persistida
     * @throws Exception error al guardar
     */
    SolicitudLiberacionNg saveSolicitudLiberacion(SolicitudLiberacionNg pSolicitud) throws Exception;

    /**
     * Méotodo que salva una solicitud asignación.
     * @param solicitudAsignacion solicitud a crear
     * @return solicitud persistida
     * @throws Exception error al guardar
     */
    SolicitudAsignacion saveSolicitudAsignacion(SolicitudAsignacion solicitudAsignacion) throws Exception;

    /**
     * Méotodo que salva una solicitud cesión.
     * @param solicitudCesion solicitud a crear
     * @return solicitud persistida
     * @throws Exception error al guardar
     */
    SolicitudCesionNg saveSolicitudCesion(SolicitudCesionNg solicitudCesion) throws Exception;

    /**
     * Recupera una solicitud de cesión por su identificador.
     * @param pConsecutivo Identificador de la solicitud.
     * @return SolicitudCesionNg
     */
    SolicitudCesionNg getSolicitudCesionById(BigDecimal pConsecutivo);

    /**
     * Recupera una Solicitud de Cesión con todas sus dependencias cargadas: Lista de Cesiones Solicitadas, Aplicadas y
     * Oficios.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudCesionNg
     * @throws Exception en caso de error.
     */
    SolicitudCesionNg getSolicitudCesionEagerLoad(SolicitudCesionNg pSolicitud) throws Exception;

    /**
     * Si es posible, restablece la numeración afectada por el trámite deshaciendo los cambios.
     * @param pSolicitud Solicitud de Cesión a Modififcar.
     * @return PeticionCancelacion con la información del proceso de cancelación.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelSolicitudCesion(SolicitudCesionNg pSolicitud) throws Exception;

    /**
     * Cancela una cesión..
     * @param pCesSol Información de la cesión.
     * @param pUseCheck Indica si es necesario validar la cancelación previamente.
     * @return Objeto PeticionCancelacion con la información del proceso.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelCesion(CesionSolicitadaNg pCesSol, boolean pUseCheck) throws Exception;

    /**
     * Si es posible, restablece la numeración afectada por el trámite deshaciendo los cambios.
     * @param pSolicitud Solicitud de Liberación a Modififcar.
     * @return PeticionCancelacion con la información del proceso de cancelación.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelSolicitudLiberacion(SolicitudLiberacionNg pSolicitud) throws Exception;

    /**
     * Cancela una liberación.
     * @param pLibSol Información de la liberación.
     * @param pUseCheck Indica si es necesario validar la cancelación previamente.
     * @return Objeto PeticionCancelacion con la información del proceso.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelLiberacion(LiberacionSolicitadaNg pLibSol, boolean pUseCheck) throws Exception;

    /**
     * Recupera una Solicitud de Liberacion con todas sus dependencias cargadas: Lista de Liberaciones Solicitadas,
     * Aplicadas y Oficios.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudLiberacionNg
     * @throws Exception en caso de error.
     */
    SolicitudLiberacionNg getSolicitudLiberacionEagerLoad(SolicitudLiberacionNg pSolicitud) throws Exception;

    /**
     * Recupera una solicitud de liberación por su identificador.
     * @param pConsecutivo Identificador de la solicitud.
     * @return SolicitudLiberacionNg
     */
    SolicitudLiberacionNg getSolicitudLiberacionById(BigDecimal pConsecutivo);

    /**
     * Recupera una Solicitud de Asignacion con todas sus dependencias cargadas: Lista de Asignacion Solicitadas,
     * Aplicadas y Oficios.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudAsignacion
     * @throws Exception en caso de error.
     */
    SolicitudAsignacion getSolicitudAsignacionEagerLoad(SolicitudAsignacion pSolicitud) throws Exception;

    /**
     * Recupera una solicitud de Asignación por su identificador.
     * @param pConsecutivo Identificador de la solicitud.
     * @return SolicitudAsignacion
     */
    SolicitudAsignacion getSolicitudAsignacionById(BigDecimal pConsecutivo);

    /**
     * Méotodo que salva una solicitud de consolidación.
     * @param pSolicitud solicitud a salvar
     * @return solicitud persistida
     * @throws Exception error al guardar
     */
    SolicitudConsolidacion saveSolicitudConsolidacion(SolicitudConsolidacion pSolicitud) throws Exception;

    /**
     * Ejecuta las liberaciones solicitadas de una Solicitud de Liberación.
     * @param pSolicitud Información de la liberación
     * @return Nueva instancia de la Solicitud de Liberación con los cambios
     * @throws Exception en caso de error.
     */
    SolicitudLiberacionNg applyLiberacionesSolicitadas(SolicitudLiberacionNg pSolicitud) throws Exception;

    /**
     * Ejecuta las liberaciones solicitadas de todas las solicitudes de liberación pendientes.
     * @throws Exception en caso de error.
     */
    void applyLiberacionesPendientes() throws Exception;

    // /**
    // * Ejecuta la Fusión de Proveedor Cesionario con el Proveedor Cedente en una solicitud de Cesión.
    // * @param pSolicitud Solicitud de Cesión.
    // * @param pInmediata Indica si se ha de ejecutar el trámite de cesión de forma inmediata (true) o programada
    // (false)
    // * @return SolicitudCesionNg actualizada.
    // * @throws Exception en caso de error.
    // */
    // SolicitudCesionNg fusionarProveedores(SolicitudCesionNg pSolicitud, boolean pInmediata) throws Exception;

    /**
     * Ejecuta las redistribuciones solicitadas de una Solicitud de Redistribución.
     * @param pSolicitud Información de la liberación
     * @return Nueva instancia de la Solicitud de Liberación con los cambios
     * @throws Exception en caso de error.
     */
    SolicitudRedistribucionNg applyRedistribucionesSolicitadas(SolicitudRedistribucionNg pSolicitud) throws Exception;

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
     * Genera las notificaciones de las liberaciones programadas pendientes de ejecutar.
     * @return Lista de noficitaciones.
     */
    List<String> getNotificacionesLiberacionesPendientes();

    /**
     * Ejecuta las cesiones solicitadas de una Solicitud de Cesión.
     * @param pSolicitud Información de la Cesión
     * @return Nueva instancia de la Solicitud de Cesión con los cambios
     * @throws Exception error
     */
    SolicitudCesionNg applyCesionesSolicitadas(SolicitudCesionNg pSolicitud) throws Exception;

    /**
     * Recupera la lista de Solicitudes de liberación completa, sin filtros.
     * @return lista de solicitudes de liberación
     */
    List<SolicitudLiberacionNg> findAllSolicitudesLiberacion();

    /**
     * Recupera la lista de solicitudes de liberación en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return lista de solicitudes de liberación filtrada
     */
    List<SolicitudLiberacionNg> findAllSolicitudesLiberacion(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Recupera el número de solicitudes de liberación que cumplen los filtros dados.
     * @param pFiltrosSolicitud pFiltros filtros de busqueda
     * @return número de solicitudes de liberación
     */
    int findAllSolicitudesLiberacionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Recupera la lista de Solicitudes de cesión completa, sin filtros.
     * @return lista de solicitudes de cesión
     */
    List<SolicitudCesionNg> findAllSolicitudesCesion();

    /**
     * Recupera la lista de solicitudes de cesión en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return lista de solicitudes de cesión filtrada
     */
    List<SolicitudCesionNg> findAllSolicitudesCesion(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Recupera el número de solicitudes de cesión que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de cesión
     */
    int findAllSolicitudesCesionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Ejecuta las consolidaciones solicitadas de una Solicitud de Consolidacón.
     * @param pSolicitud Información de la consolidación
     * @return Nueva instancia de la Solicitud de Consolidación con los cambios
     * @throws Exception en caso de error.
     */
    SolicitudConsolidacion applyConsolidacionesSolicitadas(SolicitudConsolidacion pSolicitud) throws Exception;

    /**
     * Ejecuta las consolidaciones solicitadas de todas las solicitudes de consolidación pendientes.
     * @throws Exception en caso de error.
     */
    void applyConsolidacionesPendientes() throws Exception;

    /**
     * Recupera la lista de Solicitudes de consolidación completa, sin filtros.
     * @return lista de solicitudes de consolidación
     */
    List<SolicitudConsolidacion> findAllSolicitudesConsolidacion();

    /**
     * Recupera la lista de solicitudes de lineas activas en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return lista de solicitudes de lineas activas filtrada
     */
    List<SolicitudConsolidacion> findAllSolicitudesConsolidacion(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Recupera el número de solicitudes de consolidación que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de consolidación
     */
    int findAllSolicitudesConsolidacionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Recupera el tipo de solicitud al que pertenece la solicitud con el consecutivo indicado.
     * @param pConsecutivo Identificador de la solicitud.
     * @return TipoSolicitud
     */
    TipoSolicitud getTipoSolicitudById(BigDecimal pConsecutivo);

    /**
     * Recupera la lista de Solicitudes completa, sin filtros.
     * @return lista de solicitudes
     */
    List<SolicitudRedistribucionNg> findAllSolicitudesRedistribucion();

    /**
     * Recupera la lista de solicitudes de redistribución en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de búsqueda
     * @return lista de solicitudes de redistribución filtrada
     */
    List<SolicitudRedistribucionNg> findAllSolicitudesRedistribucion(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Recupera el número de solicitudes de redistribucion que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de redistribucion
     */
    int findAllSolicitudesRedistribucionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud);

    /**
     * Méotodo que salva una solicitud de liberación.
     * @param pSolicitud solicitud a salvar
     * @return solicitud persistida
     */
    SolicitudRedistribucionNg saveSolicitudRedistribucion(SolicitudRedistribucionNg pSolicitud);

    /**
     * Recupera una solicitud de redistribución por su identificador.
     * @param pConsecutivo Identificador de la solicitud.
     * @return SolicitudCesionNg
     */
    SolicitudRedistribucionNg getSolicitudRedistribucionById(BigDecimal pConsecutivo);

    /**
     * Recupera una Solicitud de Liberacion con todas sus dependencias cargadas: Lista de Redistribuciones Solicitadas,
     * Aplicadas y Oficios.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudRedistribucionNg
     */
    SolicitudRedistribucionNg getSolicitudRedistribucionEagerLoad(SolicitudRedistribucionNg pSolicitud);

    /**
     * Si es posible, restablece la numeración afectada por el trámite deshaciendo los cambios.
     * @param pRedSol RedistribucionSolicitadaNg a Modififcar.
     * @param pUseCheck Indica si es necesario o no validar la cancelación de la redistribución
     * @return PeticionCancelacion con la información del proceso de cancelación.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelRedistribucion(RedistribucionSolicitadaNg pRedSol, boolean pUseCheck) throws Exception;

    /**
     * Si es posible, restablece la numeración afectada por el trámite deshaciendo los cambios.
     * @param pSolicitud Solicitud de Redistribución a Modififcar.
     * @return PeticionCancelacion con la información del proceso de cancelación.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelSolicitudRedistribucion(SolicitudRedistribucionNg pSolicitud) throws Exception;

    /**
     * Método que realiza el guardado en BD de los AbnConsolidar.
     * @param listaPoblacion lista de poblaciones a consolidar
     * @param fechaConsolidacion fecha de consolidación
     * @param solicitud solicitud de consolidación
     * @param listaNir lista de nir
     * @return SolicitudConsolidacion
     * @throws Exception Exception
     */
    SolicitudConsolidacion applyAbnConsolidar(List<PoblacionAbn> listaPoblacion, List<Nir> listaNir,
            Date fechaConsolidacion, SolicitudConsolidacion solicitud) throws Exception;

    /**
     * Recupera una solicitud de consolidación por su identificador.
     * @param pConsecutivo Identificador de la solicitud.
     * @return SolicitudConsolidacion
     */
    SolicitudConsolidacion getSolicitudConsolidacionById(BigDecimal pConsecutivo);

    /**
     * Méotodo que salva una solicitud de líneas activas.
     * @param solicitudLineasActivas solicitud a salvar
     * @return solicitud persistida
     */
    SolicitudLineasActivas saveSolicitudLineasActivas(SolicitudLineasActivas solicitudLineasActivas);

    /**
     * Recupera el catálogo de tipos de solicitud.
     * @return List
     */
    List<TipoSolicitud> findAllTiposSolicitud();

    /**
     * Método que devuelve el tipo solicitud.
     * @param idTipoSolicitud idTipoSolicitud
     * @return TipoSolicitud
     */
    TipoSolicitud getTipoSolicitudById(Integer idTipoSolicitud);

    /**
     * Devueve la lista de solicitudes, con filtros.
     * @param pFiltrosSolicitud pFiltrosSolicitud
     * @return lista de Solicitud
     * @throws Exception Exception
     */
    List<Solicitud> findAllSolicitudes(FiltroBusquedaSolicitudes pFiltrosSolicitud) throws Exception;

    /**
     * Recupera el número de solicitudes que cumplen los filtros dados.
     * @param pFiltrosSolicitud pFiltros filtros de busqueda
     * @return número de solicitudes
     * @throws Exception en caso de error.
     */
    int findAllSolicitudesCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) throws Exception;

    /**
     * Comprueba si una numeracion solicitada tiene numeraciones asignadas asociadas.
     * @param numeracionSolicitada numeracionSolicitada
     * @return true/false
     */
    boolean existNumeracionAsignadaBySolicita(NumeracionSolicitada numeracionSolicitada);

    /**
     * Compueba si existen solicitudes en trámite para el nir dado.
     * @param idNir Nir
     * @return boolean
     */
    boolean isSolicitudPendieteByNir(BigDecimal idNir);

    SolicitudCesionNg procesarCesionesSolicitadas(List<CesionSolicitadaNg> cesiones, SolicitudCesionNg pSolicitud)
            throws Exception;

}
