package mx.ift.sns.negocio.cpsn;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.cpsn.CesionSolicitadaCPSN;
import mx.ift.sns.modelo.cpsn.CodigoCPSN;
import mx.ift.sns.modelo.cpsn.EstudioEquipoCPSN;
import mx.ift.sns.modelo.cpsn.LiberacionSolicitadaCpsn;
import mx.ift.sns.modelo.cpsn.NumeracionAsignadaCpsn;
import mx.ift.sns.modelo.cpsn.SolicitudAsignacionCpsn;
import mx.ift.sns.modelo.cpsn.SolicitudCesionCPSN;
import mx.ift.sns.modelo.cpsn.SolicitudLiberacionCpsn;
import mx.ift.sns.modelo.cpsn.TipoBloqueCPSN;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCodigosCPSN;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCpsn;
import mx.ift.sns.modelo.pst.Contacto;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.IConfiguracionFacade;
import mx.ift.sns.negocio.PeticionCancelacion;

/**
 * Facade de servicios para Códigos de Señalización Nacional.
 */
public interface ICodigoCPSNFacade extends IConfiguracionFacade {

    /**
     * Recupera la lista de proveedores completa sin filtros.
     * @return List<Proveedor>
     * @throws Exception en caso de error.
     */
    List<Proveedor> findAllProveedores() throws Exception;

    /**
     * Recupera la lista de proveedores activos sin filtros.
     * @return List<Proveedor>
     * @throws Exception en caso de error.
     */
    List<Proveedor> findAllProveedoresActivos() throws Exception;

    /**
     * Recupera un Código CPSN en función de su tipo de bloque, identificador y el Proveedor asignado.
     * @param pIdTipoBloque Identificador del tipo de bloque.
     * @param pIdCodigo Identificador del Código CPSN.
     * @param pProveedor Proveedor asignatario del CPSN. Puede ser nulo si se buscan códigos libres.
     * @return CodigoCPSN
     */
    CodigoCPSN getCodigoCpsn(String pIdTipoBloque, BigDecimal pIdCodigo, Proveedor pProveedor);

    /**
     * Recupera el catálogo de estados de solicitud.
     * @return List
     */
    List<EstadoSolicitud> findAllEstadosSolicitud();

    /**
     * Recupera el número de solicitudes de asignación que cumplen los filtros dados.
     * @param filtros filtros de busqueda
     * @return int número de solicitudes de asignación
     * @throws Exception en caso de error.
     */
    int findAllSolicitudesAsignacionCount(FiltroBusquedaSolicitudesCpsn filtros) throws Exception;

    /**
     * Devueve la lista de solicitudes de asignación en función de los filtros facilitados.
     * @param pFiltrosSolicitud Filtros del buscador
     * @return Lista de solicitudes de asignación
     * @throws Exception en caso de que haya un error en la búsqueda
     */
    List<SolicitudAsignacionCpsn> findAllSolicitudesAsignacion(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud)
            throws Exception;

    /**
     * Recupera la lista de Representantes legales del Proveedor indicado.
     * @param pTipoContacto Tipo de Representante Legal
     * @param pIdProveedor Identificador del Proveedor.
     * @return List<Contacto>
     */
    List<Contacto> getRepresentantesLegales(String pTipoContacto, BigDecimal pIdProveedor);

    /**
     * Guarda una solicitud de asignación no geografica.
     * @param solicitud SolicitudAsignacionCpsn
     * @return SolicitudAsignacionCpsn
     * @throws Exception en caso de que haya un error en la búsqueda
     */
    SolicitudAsignacionCpsn saveSolicitudAsignacion(SolicitudAsignacionCpsn solicitud) throws Exception;

    /**
     * Recupera una Solicitud de Asignacion con todas sus dependencias cargadas: Lista de Asignacion Solicitadas,
     * Aplicadas y Oficios.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudAsignacionCpsn
     * @throws Exception en caso de error.
     */
    SolicitudAsignacionCpsn getSolicitudAsignacionEagerLoad(SolicitudAsignacionCpsn pSolicitud) throws Exception;

    /**
     * Recupera una solicitud de Asignación por su identificador.
     * @param pConsecutivo Identificador de la solicitud.
     * @return SolicitudAsignacion
     */
    SolicitudAsignacionCpsn getSolicitudAsignacionById(BigDecimal pConsecutivo);

    /**
     * Recupera la lista de solicitudes de liberación en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return lista de solicitudes de liberación filtrada
     */
    List<SolicitudLiberacionCpsn> findAllSolicitudesLiberacion(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud);

    /**
     * Recupera el número de solicitudes de liberación que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de liberación
     */
    int findAllSolicitudesLiberacionCount(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud);

    /**
     * Méotodo que salva una solicitud asignación.
     * @param pSolicitud solicitud a salvar
     * @return SolicitudLiberacionCpsn
     */
    SolicitudLiberacionCpsn saveSolicitudLiberacion(SolicitudLiberacionCpsn pSolicitud);

    /**
     * Recupera una solicitud de liberación por su identificador.
     * @param pConsecutivo Identificador de la solicitud.
     * @return SolicitudLiberacionCpsn
     */
    SolicitudLiberacionCpsn getSolicitudLiberacionById(BigDecimal pConsecutivo);

    /**
     * Recupera una Solicitud de Liberacion con todas sus dependencias cargadas.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudLiberacionCpsn
     */
    SolicitudLiberacionCpsn getSolicitudLiberacionEagerLoad(SolicitudLiberacionCpsn pSolicitud);

    /**
     * Ejecuta las liberaciones solicitadas de una Solicitud de Liberación.
     * @param pSolicitud Información de la liberación
     * @return Nueva instancia de la Solicitud de Liberación con los cambios
     * @throws Exception en caso de error.
     */
    SolicitudLiberacionCpsn applyLiberacionesSolicitadas(SolicitudLiberacionCpsn pSolicitud) throws Exception;

    /**
     * Si es posible, restablece la numeración afectada por el trámite deshaciendo los cambios.
     * @param pSolicitud Solicitud de Liberación a Modififcar.
     * @return PeticionCancelacion con la información del proceso de cancelación.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelSolicitudLiberacion(SolicitudLiberacionCpsn pSolicitud) throws Exception;

    /**
     * Cancela una liberación de códigos CPSN programada.
     * @param pLibSol Información de la liberación.
     * @param pUseCheck Indica si es necesario validar la cancelación previamente.
     * @return Objeto PeticionCancelacion con la información del proceso.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelLiberacion(LiberacionSolicitadaCpsn pLibSol, boolean pUseCheck) throws Exception;

    /**
     * Obtiene las LiberacionesSolicitadas en Trámite que esten antes de ejecución en 1 o 2 días.
     * @return List<String>
     */
    List<String> getNotificacionesLoginLiberacion();

    /**
     * Recupera el número de solicitudes de cesión cpsn que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de cesión
     */
    int findAllSolicitudesCesionCount(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud);

    /**
     * Recupera la lista de solicitudes de cesión cpsn en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return lista de solicitudes de cesión filtrada
     */
    List<SolicitudCesionCPSN> findAllSolicitudesCesion(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud);

    /**
     * Recupera una solicitud de cesión cpsn por su identificador.
     * @param pConsecutivo Identificador de la solicitud.
     * @return SolicitudCesionCPSN
     */
    SolicitudCesionCPSN getSolicitudCesionById(BigDecimal pConsecutivo);

    /**
     * Méotodo que salva una solicitud cesión cpsn.
     * @param solicitudCesion solicitud a crear
     * @return solicitudcesión salvada
     * @throws Exception error al crear solicitud
     */
    SolicitudCesionCPSN saveSolicitudCesion(SolicitudCesionCPSN solicitudCesion) throws Exception;

    /**
     * Recupera una Solicitud de Cesión CPSN con todas sus dependencias cargadas: Lista de Cesiones Solicitadas,
     * Aplicadas y Oficios.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudCesionCPSN
     * @throws Exception en caso de error.
     */
    SolicitudCesionCPSN getSolicitudCesionEagerLoad(SolicitudCesionCPSN pSolicitud) throws Exception;

    /**
     * Consulta de todos los tipos de bloque CPSN.
     * @return listado de los bloques.
     */
    List<TipoBloqueCPSN> findAllTiposBloqueCPSN();

    /**
     * Consulta de los códigos CPSN que cumplen el filtro.
     * @param pFiltro filtro de la búsqueda
     * @return listado de codigos.
     */
    List<CodigoCPSN> findCodigosCPSN(FiltroBusquedaCodigosCPSN pFiltro);

    /**
     * Método encargado de obtener los datos del estudio de equipos CPS Nacionales.
     * @param pst proveedor
     * @return List<EstudioEquipoCPSN> estudio
     */
    List<EstudioEquipoCPSN> estudioEquipoCPSN(Proveedor pst);

    /**
     * Ejecuta las cesiones solicitadas de una Solicitud de Cesión.
     * @param pSolicitud Información de la cesión
     * @return Nueva instancia de la Solicitud de Cesión con los cambios
     * @throws Exception en caso de error.
     */
    SolicitudCesionCPSN applyCesionesSolicitadas(SolicitudCesionCPSN pSolicitud) throws Exception;

    /**
     * Si es posible, restablece la numeración afectada por el trámite deshaciendo los cambios.
     * @param pSolicitud Solicitud de Cesión a Modififcar.
     * @return PeticionCancelacion con la información del proceso de cancelación.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelSolicitudCesion(SolicitudCesionCPSN pSolicitud) throws Exception;

    /**
     * Cancela una cesión de códigos CPSN programada.
     * @param pCesSol Información de la cesión.
     * @param pUseCheck Indica si es necesario validar la cancelación previamente.
     * @return Objeto PeticionCancelacion con la información del proceso.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelCesion(CesionSolicitadaCPSN pCesSol, boolean pUseCheck) throws Exception;

    /**
     * Obtiene las CesionesSolicitadas en Trámite que esten antes de ejecución en 1 o 2 días.
     * @return List<String>
     */
    List<String> getNotificacionesLoginCesion();

    /**
     * Método que busca las solicitudes de códigos cpsn según los filtros.
     * @param filtros a cumplir
     * @return listado de solicitudes
     */
    List<DetalleConsultaGenerica> findAllSolicitudes(FiltroBusquedaSolicitudesCpsn filtros);

    /**
     * Método que cuenta las solicitudes de códigos cpsn según los filtros.
     * @param filtros a cumplir
     * @return número de solicitudes
     */
    Integer findAllSolicitudesCount(FiltroBusquedaSolicitudesCpsn filtros);

    /**
     * Recupera el catálogo de tipos de solicitud.
     * @return List
     */
    List<TipoSolicitud> findAllTiposSolicitud();

    /**
     * Dada una solicitud de asignación CPSN asigna todos los CPSN solicitados al proveedor solicitante.
     * @param pSolicitud Información de la solicitud de asignación.
     * @return SolicitudAsignacionCpsn actualizada.
     * @throws Exception en caso de error.
     */
    SolicitudAsignacionCpsn applyAsignacionesSolicitadas(SolicitudAsignacionCpsn pSolicitud) throws Exception;

    /**
     * Cancela una asignación de CPSN pendiente.
     * @param pCpsnAsig Información del CPSN.
     * @param pUseCheck Indica si es necesario validar la cancelación previamente.
     * @return Objeto PeticionCancelacion con la información del proceso.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelAsignacion(NumeracionAsignadaCpsn pCpsnAsig, boolean pUseCheck) throws Exception;

    /**
     * Si es posible, restablece los códigos afectada por el trámite deshaciendo los cambios.
     * @param pSolicitud Solicitud de Asignación a Modififcar.
     * @return PeticionCancelacion con la información del proceso de cancelación.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelSolicitudAsignacion(SolicitudAsignacionCpsn pSolicitud) throws Exception;

    /**
     * Recupera el listado de Códigos CPSN asignados al Proveedor (Estatus 'A') y todos los que estan libres.
     * @param pProveedor Información del Proveedor.
     * @param tipoBloque tipo de bloque
     * @return List
     */
    List<CodigoCPSN> findAllCodigosCPSN(Proveedor pProveedor, TipoBloqueCPSN tipoBloque);
}
