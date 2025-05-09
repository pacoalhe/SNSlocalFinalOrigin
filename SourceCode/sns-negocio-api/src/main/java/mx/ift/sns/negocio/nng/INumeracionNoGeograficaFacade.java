package mx.ift.sns.negocio.nng;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroBusquedaHistoricoRangos;
import mx.ift.sns.modelo.filtros.FiltroBusquedaHistoricoSeries;
import mx.ift.sns.modelo.filtros.FiltroBusquedaLineasActivas;
import mx.ift.sns.modelo.filtros.FiltroBusquedaRangos;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSeries;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.lineas.DetalleLineaArrendadorNng;
import mx.ift.sns.modelo.lineas.DetalleLineaArrendatarioNng;
import mx.ift.sns.modelo.lineas.DetalleReporteNng;
import mx.ift.sns.modelo.lineas.ReporteLineaActivaDetNng;
import mx.ift.sns.modelo.lineas.ReporteLineaActivaNng;
import mx.ift.sns.modelo.lineas.ReporteLineaArrendadorNng;
import mx.ift.sns.modelo.lineas.ReporteLineaArrendatarioNng;
import mx.ift.sns.modelo.lineas.TipoReporte;
import mx.ift.sns.modelo.nng.CesionSolicitadaNng;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.nng.HistoricoRangoSerieNng;
import mx.ift.sns.modelo.nng.LiberacionSolicitadaNng;
import mx.ift.sns.modelo.nng.NumeracionAsignadaNng;
import mx.ift.sns.modelo.nng.NumeracionSolicitadaNng;
import mx.ift.sns.modelo.nng.RangoSerieNng;
import mx.ift.sns.modelo.nng.RedistribucionSolicitadaNng;
import mx.ift.sns.modelo.nng.SerieNng;
import mx.ift.sns.modelo.nng.SerieNngPK;
import mx.ift.sns.modelo.nng.SolicitudAsignacionNng;
import mx.ift.sns.modelo.nng.SolicitudCesionNng;
import mx.ift.sns.modelo.nng.SolicitudLiberacionNng;
import mx.ift.sns.modelo.nng.SolicitudLineasActivasNng;
import mx.ift.sns.modelo.nng.SolicitudRedistribucionNng;
import mx.ift.sns.modelo.nng.TipoAsignacion;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.pst.Contacto;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.ProveedorConvenio;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.series.HistoricoSerieNng;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.IConfiguracionFacade;
import mx.ift.sns.negocio.PeticionCancelacion;

/**
 * Interfaz del servicio de numeracion no geografica.
 */
public interface INumeracionNoGeograficaFacade extends IConfiguracionFacade {

    /**
     * Recupera la lista de proveedores completa sin filtros.
     * @return List<Proveedor>
     * @throws Exception en caso de error.
     */
    List<Proveedor> findAllProveedores() throws Exception;

    /**
     * Recupera la lista de proveedores activos completa sin filtros.
     * @return List<Proveedor>
     * @throws Exception en caso de error.
     */
    List<Proveedor> findAllProveedoresActivos() throws Exception;

    /**
     * Recupera el catálogo de estados de solicitud.
     * @return List
     */
    List<EstadoSolicitud> findAllEstadosSolicitud();

    /**
     * Persiste / Mergea un objeto SolicitudCesionNng.
     * @param pSolicitudCesion Objeto a almacenar.
     * @return SolicitudCesionNng persistido / mergeado.
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
     * Recupera la lista de Representantes legales del Proveedor indicado.
     * @param pTipoContacto Tipo de Representante Legal
     * @param pIdProveedor Identificador del Proveedor.
     * @return List<Contacto>
     */
    List<Contacto> getRepresentantesLegales(String pTipoContacto, BigDecimal pIdProveedor);

    /**
     * Recupera la lista de Proveedores que pueden ser cesionarios del proveedor cedente facilitado.
     * @param pCedente Proveedor Cedente (Concesionario, Comercializador o Ambos)
     * @return lista de Proveedores que pueden ser cesionarios del proveedor cedente
     */
    List<Proveedor> findAllCesionarios(Proveedor pCedente);

    /**
     * Recupera los rangos solicitados en función de los parámetros facilitados.
     * @param pFiltros Filtros de Búsqueda
     * @return List
     */
    List<RangoSerieNng> findAllRangos(FiltroBusquedaRangos pFiltros);

    /**
     * Recupera el número rangos solicitados en función de los parámetros facilitados.
     * @param pFiltros Filtros de Búsqueda
     * @return int Número de registros
     */
    int findAllRangosCount(FiltroBusquedaRangos pFiltros);

    /**
     * Recupera un tipo de Proveedor por su código.
     * @param pCdgTipo Código de Tipo de Proveedor.
     * @return TipoProveedor
     */
    TipoProveedor getTipoProveedorByCdg(String pCdgTipo);

    /**
     * Recupera la lista de convenios del Proveedor Comercializador con el tipo de red indicado o con ambos tipos de
     * red.
     * @param pComercializador Proveedor Comercializador
     * @param pTipoRedConvenio Tipo de Red del Convenio
     * @return Listado de Convenios que concuerdan con los parámetros
     */
    List<ProveedorConvenio> findAllConveniosByProveedor(Proveedor pComercializador, TipoRed pTipoRedConvenio);

    /**
     * Recupera un Rango en función de los parámetros dados.
     * @param pClaveServicio Identificador de la Clave de Servicio.
     * @param pSna Indentificador de la Serie.
     * @param pNumInicial Inicio del Rango.
     * @param pAsignatario Asignatario del rango.
     * @return RangoSerieNng
     */
    RangoSerieNng getRangoSerie(BigDecimal pClaveServicio, BigDecimal pSna, String pNumInicial, Proveedor pAsignatario);

    /**
     * Recupera el Rango original donde esta contenida la fracción indicada por los parámetros.
     * @param pClaveServicio Identificador de la Clave de Servicio.
     * @param pSna Indentificador de la Serie.
     * @param pNumInicial Inicio de Fracción
     * @param pNumFinal Fin de Fracción
     * @param pAsignatario Asignatario del rango.
     * @return RangoSerieNng
     */
    RangoSerieNng getRangoSerieByFraccion(BigDecimal pClaveServicio, BigDecimal pSna, String pNumInicial,
            String pNumFinal, Proveedor pAsignatario);

    /**
     * Devuelve un objeto EstadoRango por su codigo.
     * @param codigo codigo
     * @return EstadoRango
     */
    EstadoRango getEstadoRangoByCodigo(String codigo);

    /**
     * Busca todas las claves de servicio activas.
     * @return List<ClaveServicio>
     */
    List<ClaveServicio> findAllClaveServicioActivas();

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
     * @return String
     */
    List<String> findAllClientesBySolicitud(Solicitud sol);

    /**
     * Obtiene la clave servicio por su codigo.
     * @param codigo codigo
     * @return clave servicio
     */
    ClaveServicio getClaveServicioByCodigo(BigDecimal codigo);

    /**
     * Comprueba para un proveedor si existe algun convenio con unconcesionario que tenga ABC.
     * @param proveedor proveedor
     * @return boolean
     */
    boolean existeConcesionarioConvenioConBcd(Proveedor proveedor);

    /**
     * Guarda una solicitud de asignación no geografica.
     * @param solicitud SolicitudAsignacionNng
     * @return solicitud
     */
    SolicitudAsignacionNng saveSolicitudAsignacion(SolicitudAsignacionNng solicitud);

    /**
     * Obtiene todos los proveedores a los que se puede arrendar numeraciones no geograficas.
     * @return lista proveedores
     */
    List<Proveedor> findAllArrendatarios();

    /**
     * Recupera una Solicitud de Asignacion NNG con todas sus dependencias cargadas: Lista de Numeraciones Solicitadas,
     * Rangos Asignados y Oficios.
     * @param pSolicitud solicitud
     * @return solicitud
     */
    SolicitudAsignacionNng getSolicitudAsignacionEagerLoad(SolicitudAsignacionNng pSolicitud);

    /**
     * Obtiene la cantidad de series de numeración no geografiac segun filtros de busqueda.
     * @param filtros FiltroBusquedaSeries
     * @return total
     */
    Integer findAllSeriesCount(FiltroBusquedaSeries filtros);

    /**
     * Obtiene las series de numeración no geografica segun filtros de busqueda.
     * @param filtros FiltroBusquedaSeries
     * @return total
     */
    List<SerieNng> findAllSeries(FiltroBusquedaSeries filtros);

    /**
     * Persiste o Mergea un objeto pRangoSerieNng.
     * @param pRangoSerieNng objeto pRangoSerieNng
     * @return RangoSerieNng
     */
    RangoSerieNng saveRangoSerie(RangoSerieNng pRangoSerieNng);

    /**
     * Obtiene la ocupacion sobre una serie.
     * @param serie serie
     * @return ocupacion
     */
    Integer getTotalOcupacionSerie(SerieNng serie);

    /**
     * Obtiene de forma aleatoria una cantidad de series por su clave de servicio.
     * @param clave ClaveServicio
     * @param n numero de series a obtener
     * @return lista series
     */
    List<SerieNng> findRandomSeriesLibreByClaveServicio(ClaveServicio clave, int n);

    /**
     * Obtiene una serie por una clave de servicio que tenga disponible por lo menos una cantidad dada.
     * @param clave servicio
     * @param cantidad asignar
     * @return serie
     */
    SerieNng getRandomSerieOcupadaByClaveServicio(ClaveServicio clave, BigDecimal cantidad);

    /**
     * Obtiene un rango por una clave de servicio que tenga un rango disponible con por lo menos una cantidad dada.
     * @param clave servicio
     * @param cantidad asignar
     * @return serie
     */
    RangoSerieNng getRandomRangoByClaveServicio(ClaveServicio clave, int cantidad);

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
     * Ejecuta las cesiones solicitadas de una Solicitud de Cesión.
     * @param pSolicitud Información de la Cesión
     * @return Nueva instancia de la Solicitud de Cesión con los cambios
     * @throws Exception error
     */
    SolicitudCesionNng applyCesionesSolicitadas(SolicitudCesionNng pSolicitud) throws Exception;

    /**
     * Obtiene las CesionesSolicitadas en Trámite con implementación próxima.
     * @return List<String> Notificaciones.
     */
    List<String> getNotificacionesLoginCesion();

    /**
     * Obtiene las LiberacionesSolicitadas en Trámite con implementación próxima.
     * @return List<String> Notificaciones.
     */
    List<String> getNotificacionesLoginLiberacion();

    /**
     * Recupera el catálogo de tipos de destinatario.
     * @return List
     */
    List<TipoDestinatario> findAllTiposDestinatario();

    /**
     * Eliminamos un rango de numeración no geografica.
     * @param rango RangoSerieNng
     */
    void removeRango(RangoSerieNng rango);

    /**
     * Obtiene el total de numeraciones asignadas por una clave de servicio.
     * @param clave servicio
     * @return total
     */
    Integer getTotalNumeracionAsignadaByClaveServicio(ClaveServicio clave);

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
     * Méotodo que salva una solicitud asignación.
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
     * Ejecuta las liberaciones solicitadas de una Solicitud de Liberación.
     * @param pSolicitud Información de la liberación
     * @return Nueva instancia de la Solicitud de Liberación con los cambios
     * @throws Exception en caso de error.
     */
    SolicitudLiberacionNng applyLiberacionesSolicitadas(SolicitudLiberacionNng pSolicitud) throws Exception;

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
     * Méotodo que salva una solicitud redistribución.
     * @param pSolicitud solicitudRedistribucion a crear
     * @return SolicitudRedistribucionNng
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
     * Obtiene todos los concesionarios que un proveedor tiene convenio para numeración no geográfica(el concesionario
     * tiene ABC).
     * @param proveedor convenio
     * @return lista concesionario
     */
    List<Proveedor> findAllConcesionarioConvenio(Proveedor proveedor);

    /**
     * Valida una numeracion. Si existen rango para esta los retorna.
     * @param clave servicio
     * @param sna serie
     * @param numeroInicial inicio
     * @param numeroFinal final
     * @return lista rango
     */
    List<RangoSerieNng> validateRango(BigDecimal clave, BigDecimal sna,
            String numeroInicial, String numeroFinal);

    /**
     * Obtiene una serie por su PK (clave servicio y sna).
     * @param id serie
     * @return serie
     */
    SerieNng getSerieById(SerieNngPK id);

    /**
     * Comprueba si una numeracion solicitada tiene rango asociados.
     * @param numeracionSolicitada NumeracionSolicitadaNng
     * @return true/false
     */
    boolean isNumeracionSolicitadaWithRangos(NumeracionSolicitadaNng numeracionSolicitada);

    /**
     * Recupera la lista de Proveedores que pueden ser arrendatarios del proveedor indicado por parámetros. Los
     * arrendatarios pueden ser:<br>
     * - Todos los Concesionarios o Comercializadoras que tengan ABC definio<br>
     * - Comercializadores sin ABC, pero con Convenio con algún Concesionario que sí tenga ABC
     * @param proveedor Proveedor solicitante de la lista de arrendatarios
     * @return lista de Proveedores que pueden ser arrendatarios del proveedor indicado
     */
    List<Proveedor> findAllArrendatariosByAbc(Proveedor proveedor);

    /**
     * Recupera la lista de Proveedores con los que el solicitante tiene convenios y que, además, tengan ABC.
     * @param pComercializador Proveedor Solicitante.
     * @return lista de Proveedores con los que el solicitante tiene convenios y ABC definido.
     */
    List<Proveedor> findAllConcesionariosFromConveniosByAbc(Proveedor pComercializador);

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
     * Recupera la lista de objetos HistoricoRangoSerieNng con la información de los movimientos sobre los Rangos Serie
     * NNG.
     * @param pFiltros Información de los filtros.
     * @return List
     */
    List<HistoricoRangoSerieNng> findAllHistoricActionsFromRangos(FiltroBusquedaHistoricoRangos pFiltros);

    /**
     * Recupera el número de movimientos históricos en función de los filtros indicados.
     * @param pFiltros Información de los filtros.
     * @return número de movimientos históricos
     */
    int findAllHistoricActionsFromRangosCount(FiltroBusquedaHistoricoRangos pFiltros);

    /**
     * Comprueba si una solictud tiene rangos.
     * @param solicitud solictud
     * @return true/false
     */
    boolean isSolicitudWithRangos(Solicitud solicitud);

    /**
     * Obtiene todo los tipo de reporte.
     * @return TipoReporte
     */
    List<TipoReporte> findAllTiposReporte();

    /**
     * Valida un reporte de lineas activa no geografica.
     * @param fichero fichero
     * @return reportes
     * @throws Exception error
     */
    RetornoProcesaFicheroReportesNng validarFicheroLineasActivas(File fichero) throws Exception;

    /**
     * Salva una solicitud de lineas activas.
     * @param solicitud lineas activas
     * @return solicitud
     */
    SolicitudLineasActivasNng saveSolicitudLineasActivas(SolicitudLineasActivasNng solicitud);

    /**
     * Valida un reporte de lineas activa no geografica.
     * @param fichero fichero
     * @return reportes
     * @throws Exception error
     */
    RetornoProcesaFicheroReportesNng validarFicheroLineasActivasDet(File fichero) throws Exception;

    /**
     * Valida un reporte de lineas activa de arrendatario.
     * @param fichero fichero
     * @return reportes
     * @throws Exception error
     */
    RetornoProcesaFicheroReportesNng validarFicheroLineasArrendatario(File fichero) throws Exception;

    /**
     * Guarda un reporte de linea activas de arrendatario.
     * @param reporte reporte
     * @return reporte
     */
    ReporteLineaArrendatarioNng saveLineaArrendatario(ReporteLineaArrendatarioNng reporte);

    /**
     * Guarda los detalles del reporte de lineas del arrendatario.
     * @param listaDatos detalles
     * @param id reporte
     */
    void saveDetLineaArrendatario(List<String> listaDatos, BigDecimal id);

    /**
     * Valida un reporte de lineas arrendador.
     * @param fichero fichero
     * @return reportes
     * @throws Exception error
     */
    RetornoProcesaFicheroReportesNng validarFicheroLineasArrendada(File fichero) throws Exception;

    /**
     * Guarda un reporte de linea activas de arrendador.
     * @param reporte reporte
     * @return reporte
     */
    ReporteLineaArrendadorNng saveLineaArrendada(ReporteLineaArrendadorNng reporte);

    /**
     * Guarda los detalles del reporte de lineas del arrendatario.
     * @param listaDatos detalles
     * @param id reporte
     */
    void saveDetLineaArrendada(List<String> listaDatos, BigDecimal id);

    /**
     * Guarda un reporte de linea activas detallada.
     * @param reporte reporte
     * @return reporte
     */
    ReporteLineaActivaDetNng saveLineaActivaDet(ReporteLineaActivaDetNng reporte);

    /**
     * Guarda los detalles del reporte de lineas activas detallado.
     * @param listaDatos detalles
     * @param id reporte
     */
    void saveDetLineaActivaDet(List<String> listaDatos, BigDecimal id);

    /**
     * Guarda un reporte de linea activas.
     * @param lineaActiva reporte
     * @return reporte
     */
    ReporteLineaActivaNng saveLineaActiva(ReporteLineaActivaNng lineaActiva);

    /**
     * Guarda los detalles del reporte de lineas activas.
     * @param listaDatos detalles
     * @param id reporte
     */
    void saveDetalleLineaActiva(List<String> listaDatos, BigDecimal id);

    /**
     * Obtiene los detalles de los reportes de lineas activas.
     * @param filtro busqueda
     * @return lista detalles
     */
    List<DetalleReporteNng> findAllDetalleReporte(FiltroBusquedaLineasActivas filtro);

    /**
     * Obtiene el total de detalles de los reportes de lineas activas.
     * @param filtro busqueda
     * @return total
     */
    int findAllDetalleReporteCount(FiltroBusquedaLineasActivas filtro);

    /**
     * Genera un fichero excel con los filtros de una cosulta de lineas activas.
     * @param filtro busqueda
     * @return fichero
     * @throws Exception error
     */
    InputStream getExportConsultaLineaActiva(FiltroBusquedaLineasActivas filtro) throws Exception;

    /**
     * Obtiene los detalles de un reporte de lineas arrendadas.
     * @param idReporte reporte
     * @param first pagina
     * @param pageSize filas
     * @return detalles
     */
    List<DetalleLineaArrendadorNng> findAllDetLineaArrendadaByReporte(BigDecimal idReporte, int first, int pageSize);

    /**
     * Obtiene los detalles de un reporte de lineas del arrendatario.
     * @param idReporte reporte
     * @param first pagina
     * @param pageSize filas
     * @return detalles
     */
    List<DetalleLineaArrendatarioNng> findAllDetLineaArrendatarioByReporte(BigDecimal idReporte, int first,
            int pageSize);

    /**
     * Obtiene el total de la numeracion asignada por serie por clave de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @return total
     */
    Integer getNumeracionAsignadaSerie(ClaveServicio claveServicio, Proveedor proveedor);

    /**
     * Obtiene el total de la numeracion asignada por rango por clave de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @return total
     */
    Integer getNumeracionAsignadaRango(ClaveServicio claveServicio, Proveedor proveedor);

    /**
     * Obtiene el total de la numeracion asignada por especifica por clave de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @return total
     */
    Integer getNumeracionAsignadaEspecifica(ClaveServicio claveServicio, Proveedor proveedor);

    /**
     * Obtiene el total de la numeracion asignada por serie por clave de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @param idReporte reporte
     * @return total
     */
    Integer getNumeracionActivaSerie(ClaveServicio claveServicio, Proveedor proveedor, BigDecimal idReporte);

    /**
     * Obtiene el total de la numeracion asignada por rango por clave de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @param idReporte reporte
     * @return total
     */
    Integer getNumeracionActivaRango(ClaveServicio claveServicio, Proveedor proveedor, BigDecimal idReporte);

    /**
     * Obtiene el total de la numeracion asignada por rango por especifica de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @param idReporte reporte
     * @return total
     */
    Integer getNumeracionActivaEspecifica(ClaveServicio claveServicio, Proveedor proveedor, BigDecimal idReporte);

    /**
     * Obtiene el total de la numeracion asignada por rango por serie de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @param idReporte reporte
     * @return total
     */
    Integer getNumeracionActivaDetSerie(ClaveServicio claveServicio, Proveedor proveedor, BigDecimal idReporte);

    /**
     * Obtiene el total de la numeracion asignada por rango por rango de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @param idReporte reporte
     * @return total
     */
    Integer getNumeracionActivaDetRango(ClaveServicio claveServicio, Proveedor proveedor, BigDecimal idReporte);

    /**
     * Obtiene el total de la numeracion asignada por rango por rango de servicio y proveedor.
     * @param claveServicio clave
     * @param proveedor pst
     * @param idReporte reporte
     * @return total
     */
    Integer getNumeracionActivaDetEspecifica(ClaveServicio claveServicio, Proveedor proveedor, BigDecimal idReporte);

    /**
     * Genera el analis de la numeracion de un proveedor.
     * @param proveedor Información del Proveedor.
     * @param numeracionesSolicitadas Información de las numeraciones solicitadas.
     * @return InputStream fichero
     * @throws Exception en caso de error.
     */
    InputStream generarAnalisisNumeracion(Proveedor proveedor, List<NumeracionSolicitadaNng> numeracionesSolicitadas)
            throws Exception;

    /**
     * Obtiene el historico de las series NNG.
     * @param pFiltros busqueda
     * @return historico
     */
    List<HistoricoSerieNng> findAllHistoricoSeries(FiltroBusquedaHistoricoSeries pFiltros);

    /**
     * Obtiene total de historicos de las series NNG.
     * @param pFiltros busqueda
     * @return total
     */
    int findAllHistoricoSeriesCount(FiltroBusquedaHistoricoSeries pFiltros);

    /**
     * Genera un fichero excel con los filtros de una cosulta de historico de series.
     * @param filtro busqueda
     * @return fichero
     * @throws Exception error
     */
    InputStream getExportHistoricoSeries(FiltroBusquedaHistoricoSeries filtro) throws Exception;

    /**
     * Obtiene las numeraciones asignadas por la solicitud de asignacion.
     * @param solicitud asignacion
     * @return neraciones asignadas
     */
    List<NumeracionAsignadaNng> findAllNumeracionAsignadaBySolicitud(SolicitudAsignacionNng solicitud);

}
