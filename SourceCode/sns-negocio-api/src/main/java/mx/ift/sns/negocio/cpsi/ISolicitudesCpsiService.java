package mx.ift.sns.negocio.cpsi;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.cpsi.CesionSolicitadaCpsi;
import mx.ift.sns.modelo.cpsi.CpsiAsignado;
import mx.ift.sns.modelo.cpsi.CpsiUitEntregado;
import mx.ift.sns.modelo.cpsi.LiberacionSolicitadaCpsi;
import mx.ift.sns.modelo.cpsi.SolicitudAsignacionCpsi;
import mx.ift.sns.modelo.cpsi.SolicitudCesionCpsi;
import mx.ift.sns.modelo.cpsi.SolicitudCpsiUit;
import mx.ift.sns.modelo.cpsi.SolicitudLiberacionCpsi;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCPSI;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.PeticionCancelacion;

/**
 * Servicio de Códigos de Señalización Internacional.
 */
public interface ISolicitudesCpsiService {

    /**
     * Recupera el catálogo de estados de solicitud.
     * @return List
     */
    List<EstadoSolicitud> findAllEstadosSolicitud();

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
     * Méotodo que salva una solicitud asignación.
     * @param pSolicitud solicitud a salvar
     * @return SolicitudLiberacionCpsi
     */
    SolicitudLiberacionCpsi saveSolicitudLiberacion(SolicitudLiberacionCpsi pSolicitud);

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

    /**
     * Ejecuta las liberaciones solicitadas de una Solicitud de Liberación.
     * @param pSolicitud Información de la liberación
     * @return Nueva instancia de la Solicitud de Liberación con los cambios
     * @throws Exception en caso de error.
     */
    SolicitudLiberacionCpsi applyLiberacionesSolicitadas(SolicitudLiberacionCpsi pSolicitud) throws Exception;

    /**
     * Ejecuta las liberaciones solicitadas de todas las solicitudes de liberación pendientes.
     * @throws Exception en caso de error.
     */
    void applyLiberacionesPendientes() throws Exception;

    /**
     * Si es posible, restablece la numeración afectada por el trámite deshaciendo los cambios.
     * @param pSolicitud Solicitud de Liberación a Modififcar.
     * @return PeticionCancelacion con la información del proceso de cancelación.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelSolicitudLiberacion(SolicitudLiberacionCpsi pSolicitud) throws Exception;

    /**
     * Cancela una liberación de códigos CPSI programada.
     * @param pLibSol Información de la liberación.
     * @param pUseCheck Indica si es necesario validar la cancelación previamente.
     * @return Objeto PeticionCancelacion con la información del proceso.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelLiberacion(LiberacionSolicitadaCpsi pLibSol, boolean pUseCheck) throws Exception;

    /**
     * Genera las notificaciones de las liberaciones programadas pendientes de ejecutar.
     * @return Lista de noficitaciones.
     */
    List<String> getNotificacionesLiberacionesPendientes();

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
     * Méotodo que salva una solicitud de cesión CSPI.
     * @param pSolicitud solicitud a salvar
     * @return solicitud persistida
     */
    SolicitudCesionCpsi saveSolicitudCesion(SolicitudCesionCpsi pSolicitud);

    /**
     * Recupera una Solicitud de Cesión con todas sus dependencias cargadas: Lista de Cesiones Solicitadas y Oficios.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudCesionCpsi
     */
    SolicitudCesionCpsi getSolicitudCesionEagerLoad(SolicitudCesionCpsi pSolicitud);

    /**
     * Ejecuta las cesiones solicitadas de una Solicitud de Cesión.
     * @param pSolicitud Información de la cesión
     * @return Nueva instancia de la Solicitud de Cesión con los cambios
     * @throws Exception en caso de error.
     */
    SolicitudCesionCpsi applyCesionesSolicitadas(SolicitudCesionCpsi pSolicitud) throws Exception;

    /**
     * Ejecuta las cesiones solicitadas de todas las solicitudes de cesión pendientes.
     * @throws Exception en caso de error.
     */
    void applyCesionesPendientes() throws Exception;

    /**
     * Si es posible, restablece la numeración afectada por el trámite deshaciendo los cambios.
     * @param pSolicitud Solicitud de Cesión a Modififcar.
     * @return PeticionCancelacion con la información del proceso de cancelación.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelSolicitudCesion(SolicitudCesionCpsi pSolicitud) throws Exception;

    /**
     * Cancela una cesión de códigos CPSI programada.
     * @param pCesSol Información de la cesión.
     * @param pUseCheck Indica si es necesario validar la cancelación previamente.
     * @return Objeto PeticionCancelacion con la información del proceso.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelCesion(CesionSolicitadaCpsi pCesSol, boolean pUseCheck) throws Exception;

    /**
     * Genera las notificaciones de las cesiones programadas pendientes de ejecutar.
     * @return Lista de noficitaciones.
     */
    List<String> getNotificacionesCesionesPendientes();

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
     * Méotodo que salva una solicitud asignación.
     * @param pSolicitud solicitud a salvar
     * @return SolicitudAsignacionCpsi
     */
    SolicitudAsignacionCpsi saveSolicitudAsignacion(SolicitudAsignacionCpsi pSolicitud);

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

    /**
     * Recupera el catálogo de tipos de solicitud.
     * @return List
     */
    List<TipoSolicitud> findAllTiposSolicitud();

    /**
     * Método que busca las solicitudes de códigos cpsi según los filtros.
     * @param filtros a cumplir
     * @return listado de solicitudes
     */
    List<DetalleConsultaGenerica> findAllSolicitudes(FiltroBusquedaSolicitudesCPSI filtros);

    /**
     * Método que cuenta las solicitudes de códigos cpsi según los filtros.
     * @param filtros a cumplir
     * @return número de solicitudes
     */
    Integer findAllSolicitudesCount(FiltroBusquedaSolicitudesCPSI filtros);

    /**
     * Dada una solicitud de asignación CPSI asigna todos los CPSI solicitados al proveedor solicitante.
     * @param pSolicitud Información de la solicitud de asignación.
     * @return SolicitudAsignacionCpsi actualizada.
     * @throws Exception en caso de error.
     */
    SolicitudAsignacionCpsi applyAsignacionesSolicitadas(SolicitudAsignacionCpsi pSolicitud) throws Exception;

    /**
     * Si es posible, restablece los códigos afectada por el trámite deshaciendo los cambios.
     * @param pSolicitud Solicitud de Asignación a Modififcar.
     * @return PeticionCancelacion con la información del proceso de cancelación.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelSolicitudAsignacion(SolicitudAsignacionCpsi pSolicitud) throws Exception;

    /**
     * Cancela una asignación de CPSI pendiente.
     * @param pCpsiAsig Información del CPSI.
     * @param pUseCheck Indica si es necesario validar la cancelación previamente.
     * @return Objeto PeticionCancelacion con la información del proceso.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelAsignacion(CpsiAsignado pCpsiAsig, boolean pUseCheck) throws Exception;

    /**
     * Méotodo que salva una solicitud de códigos a la UIT.
     * @param pSolicitud solicitud a salvar
     * @return SolicitudCpsiUit
     */
    SolicitudCpsiUit saveSolicitudCpsiUit(SolicitudCpsiUit pSolicitud);

    /**
     * Recupera el número de solicitudes de códigos de CPSI a la UIT que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de asignación
     */
    int findAllSolicitudesCpsiUitCount(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud);

    /**
     * Recupera la lista de solicitudes de códigos CPSI a la UIT en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return lista de solicitudes de asignación filtrada
     */
    List<SolicitudCpsiUit> findAllSolicitudesCpsiUit(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud);

    /**
     * Recupera una Solicitud de códigos CPSI con todas sus dependencias cargadas.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudCpsiUit
     */
    SolicitudCpsiUit getSolicitudCpsiUitEagerLoad(SolicitudCpsiUit pSolicitud);

    /**
     * Recupera una solicitud de codigos CPSI por su identificador.
     * @param pConsecutivo Identificador de la solicitud.
     * @return SolicitudCpsiUit
     */
    SolicitudCpsiUit getSolicitudCpsiUitById(BigDecimal pConsecutivo);

    /**
     * Método que cuenta cuantas solicitudes de códigos a la UIT coinciden con los parámetros.
     * @param filtro filtro
     * @return int
     */
    int findSolicitudCpsiUitByCodAndEstatusCount(FiltroBusquedaSolicitudesCPSI filtro);

    /**
     * Cancela una solicitud de códigos CPSI a la UIT pendiente.
     * @param pCpsiUitEnt Información del CPSI.
     * @param pUseCheck Indica si es necesario validar la cancelación previamente.
     * @return Objeto PeticionCancelacion con la información del proceso.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelSolicitud(CpsiUitEntregado pCpsiUitEnt, boolean pUseCheck) throws Exception;

    /**
     * Ejecuta las solicitudes de códigos de Cpsi a la UIT.
     * @param pSolicitud Información de la solicitud
     * @return Nueva instancia de la Solicitud
     * @throws Exception en caso de error.
     */
    SolicitudCpsiUit applySolicitudCpsiUit(SolicitudCpsiUit pSolicitud) throws Exception;

    /**
     * Si es posible, restablece los códigos afectada por el trámite deshaciendo los cambios.
     * @param pSolicitud Solicitud de códigos a Modififcar.
     * @return PeticionCancelacion con la información del proceso de cancelación.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelSolicitud(SolicitudCpsiUit pSolicitud) throws Exception;
}
