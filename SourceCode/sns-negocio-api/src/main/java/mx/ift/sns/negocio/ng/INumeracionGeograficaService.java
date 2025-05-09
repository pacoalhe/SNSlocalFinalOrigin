package mx.ift.sns.negocio.ng;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.abn.AbnCentral;
import mx.ift.sns.modelo.abn.EstadoAbn;
import mx.ift.sns.modelo.abn.PoblacionAbn;
import mx.ift.sns.modelo.central.Central;
import mx.ift.sns.modelo.central.CentralesRelacion;
import mx.ift.sns.modelo.central.ComboCentral;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.filtros.FiltroBusquedaABNs;
import mx.ift.sns.modelo.filtros.FiltroBusquedaHistoricoSeries;
import mx.ift.sns.modelo.filtros.FiltroBusquedaLineasActivas;
import mx.ift.sns.modelo.filtros.FiltroBusquedaPoblaciones;
import mx.ift.sns.modelo.filtros.FiltroBusquedaProveedores;
import mx.ift.sns.modelo.filtros.FiltroBusquedaRangos;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSeries;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.lineas.DetLineaActivaDet;
import mx.ift.sns.modelo.lineas.DetLineaArrendada;
import mx.ift.sns.modelo.lineas.DetLineaArrendatario;
import mx.ift.sns.modelo.lineas.DetalleLineaActiva;
import mx.ift.sns.modelo.lineas.DetalleReporte;
import mx.ift.sns.modelo.lineas.LineaActivaDet;
import mx.ift.sns.modelo.lineas.LineaArrendada;
import mx.ift.sns.modelo.lineas.LineaArrendatario;
import mx.ift.sns.modelo.lineas.Reporte;
import mx.ift.sns.modelo.lineas.ReporteLineasActivas;
import mx.ift.sns.modelo.lineas.TipoReporte;
import mx.ift.sns.modelo.ng.AbnConsolidar;
import mx.ift.sns.modelo.ng.CesionSolicitadaNg;
import mx.ift.sns.modelo.ng.LiberacionSolicitadaNg;
import mx.ift.sns.modelo.ng.NirConsolidar;
import mx.ift.sns.modelo.ng.NumeracionSolicitada;
import mx.ift.sns.modelo.ng.PoblacionConsolidar;
import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.ng.RedistribucionSolicitadaNg;
import mx.ift.sns.modelo.ng.Serie;
import mx.ift.sns.modelo.ng.SeriePK;
import mx.ift.sns.modelo.ng.SolicitudAsignacion;
import mx.ift.sns.modelo.ng.SolicitudCesionNg;
import mx.ift.sns.modelo.ng.SolicitudConsolidacion;
import mx.ift.sns.modelo.ng.SolicitudLiberacionNg;
import mx.ift.sns.modelo.ng.SolicitudLineasActivas;
import mx.ift.sns.modelo.ng.SolicitudRedistribucionNg;
import mx.ift.sns.modelo.oficios.Oficio;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.MunicipioPK;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Contacto;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.ProveedorConvenio;
import mx.ift.sns.modelo.pst.TipoContacto;
import mx.ift.sns.modelo.pst.TipoModalidad;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.modelo.pst.TipoServicio;
import mx.ift.sns.modelo.reporteabd.SerieArrendada;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.series.HistoricoSerie;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.IConfiguracionFacade;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.negocio.ng.model.ResultadoValidacionArrendamiento;
import mx.ift.sns.negocio.ng.model.RetornoProcesaFicheroAsignacion;
import mx.ift.sns.negocio.ng.model.RetornoProcesaFicheroReportes;
import mx.ift.sns.negocio.oficios.IOficiosService;
import mx.ift.sns.negocio.ot.IOrganizacionTerritorialService;

/**
 * Interfaz del servicio de numeracion geografica.
 */
public interface INumeracionGeograficaService extends IConfiguracionFacade {

    /**
     * Recupera la lista de Proveedores activos completa, sin filtros.
     * @return List
     * @throws Exception en caso de error.
     */
    List<Proveedor> findAllProveedoresActivos() throws Exception;

    /**
     * Recupera la lista de Proveedores completa, sin filtros.
     * @return List
     * @throws Exception en caso de error.
     */
    List<Proveedor> findAllProveedores() throws Exception;

    /**
     * Recupera la lista de Proveedores que pueden ser cesionarios del proveedor cedente facilitado.
     * @param pCedente Proveedor Cedente (Concesionario, Comercializador o Ambos)
     * @return lista de Proveedores que pueden ser cesionarios del proveedor cedente
     */
    List<Proveedor> findAllCesionarios(Proveedor pCedente);

    /**
     * Recupera la lista de Proveedores Concesionarios con los que el Proveedor Comercializador tiene convenio.
     * @param pComercializador Proveedor Comercializador
     * @param pTipoRed Tipo de Red del Convenio
     * @return Lista de Concesionarios con los que el Proveedor Comercializador tiene convenio.
     */
    List<Proveedor> findAllConcesionariosFromConvenios(Proveedor pComercializador, TipoRed pTipoRed);

    /**
     * Recupera todos los proveedores de un mismo tipo de servicio.
     * @param pTipoProveedor TipoProveedor
     * @param pTipoRed TipoRed
     * @param pIdSolicitante Identificador del Proveedor que solicita la Lista. Puede se nulo. Si existe, se elimina el
     *        proveedor de los resultados.
     * @return List
     */
    List<Proveedor> findAllProveedoresByServicio(TipoProveedor pTipoProveedor,
            TipoRed pTipoRed, BigDecimal pIdSolicitante);

    /**
     * Se encarga de obtener los proveedores por tipo de servicio que cumplan.
     * <ul>
     * <li>Si tipo de proveedor no es Comercializador se buscan todos los tipos de proveedores por el tipo de red</li>
     * <li>Si es Comercializador se buscan los proveedores de tipo comercializador y comercializador/concesionario</li>
     * </ul>
     * @param pTipoProveedor TipoProveedor
     * @param pTipoRed TipoRed
     * @param idPst BigDecimal
     * @return List<Proveedor>
     */
    List<Proveedor> findAllProveedoresByServicioArrendar(TipoProveedor pTipoProveedor,
            TipoRed pTipoRed, BigDecimal idPst);

    /**
     * Recupera un Proveedor por su nobre.
     * @param pNombre String
     * @return Proveedor
     * @throws Exception en caso de error.
     */
    Proveedor getProveedorByNombre(String pNombre) throws Exception;

    /**
     * Busca las centrales por nombre.
     * @param name name
     * @return List<Central>
     * @throws Exception exception
     */
    List<Central> findAllCentralesByName(String name) throws Exception;

    /**
     * Recupera la lista de centrales que pertenecen al Proveedor.
     * @param pProveedor Proveedor
     * @param name name
     * @return List<Central>
     */
    List<Central> findAllCentralesByProveedor(Proveedor pProveedor, String name);

    /**
     * Recupera un Proveedor por su identificador.
     * @param pIdProveedor BigDecimal
     * @return Proveedor
     * @throws Exception en caso de error.
     */
    Proveedor getProveedorById(BigDecimal pIdProveedor) throws Exception;

    /**
     * Actualiza un proveedor.
     * @param pProveedor Proveedor
     * @throws Exception en caso de error.
     */
    void updateProveedor(Proveedor pProveedor) throws Exception;

    /**
     * Recupera los representantes legales de un Proveedor seleccionado.
     * @param pTipoContacto Tipo de Contacto
     * @param pIdProveedor Identificador de Proveedor
     * @return List
     */
    List<Contacto> getRepresentantesLegales(String pTipoContacto, BigDecimal pIdProveedor);

    /**
     * Recupera un representante legal por su identificador.
     * @param pIdContacto Identificador de contacto
     * @return Contacto
     * @throws Exception excepcion general
     */
    Contacto getRepresentanteLegal(BigDecimal pIdContacto) throws Exception;

    /**
     * Recupera un convenio por su identificador.
     * @param pId Identificador de Convenio
     * @return ProveedorConvenio
     */
    ProveedorConvenio getConvenioById(BigDecimal pId);

    /**
     * Devuelve la lista de convenios por proveedor.
     * @param pCodPstComercializador BigDecimal
     * @return List
     * @throws Exception en caso de error.
     */
    List<ProveedorConvenio> findAllConveniosByProveedor(BigDecimal pCodPstComercializador) throws Exception;

    /**
     * Recupera la lista de convenios del Proveedor Comercializador con el tipo de red indicado o con ambos tipos de
     * red.
     * @param pComercializador Proveedor Comercializador
     * @param pTipoRedConvenio Tipo de Red del Convenio
     * @return Listado de Convenios que concuerdan con los parámetros
     */
    List<ProveedorConvenio> findAllConveniosByProveedor(Proveedor pComercializador, TipoRed pTipoRedConvenio);

    /**
     * Devuelve la lista de concecisionarios con que un comercializador tiene convenio.
     * @param tipoRed tipoRed
     * @param comercializador Proveedor
     * @return List<Proveedor>
     * @throws Exception en caso de error.
     */
    List<Proveedor> findAllConcesionariosByComercializador(TipoRed tipoRed, Proveedor comercializador) throws Exception;

    /**
     * Recupera el catálogo de tipos de contacto.
     * @return List
     * @throws Exception en caso de error.
     */
    List<TipoContacto> findAllTiposContacto() throws Exception;

    /**
     * Método que devuelve los tipos modalidad.
     * @return List
     */
    List<TipoModalidad> findAllTiposModalidad();

    /**
     * Método que devuelve el tipo modalidad.
     * @param idtipoModalidad String
     * @return TipoModalidad
     */
    TipoModalidad getTipoModalidadById(String idtipoModalidad);

    /**
     * Recupera el catálogo de tipos de proveedor.
     * @return List
     * @throws Exception en caso de error.
     */
    List<TipoProveedor> findAllTiposProveedor() throws Exception;

    /**
     * Recupera un tipo de Proveedor por su código.
     * @param pCdgTipo Código de Tipo de Proveedor.
     * @return TipoProveedor
     */
    TipoProveedor getTipoProveedorByCdg(String pCdgTipo);

    /**
     * Método que devuelve los tipos de red.
     * @return List
     */
    List<TipoRed> findAllTiposRed();

    /**
     * Método que devuelve el tipo red.
     * @param idTipoRed String
     * @return TipoRed
     */
    TipoRed getTipoRedById(String idTipoRed);

    /**
     * Recupera el catálogo de tipos de servicio.
     * @return List
     * @throws Exception en caso de error.
     */
    List<TipoServicio> findAllTiposServicio() throws Exception;

    /**
     * Recupera el catálogo de tipos de solicitud NG.
     * @return List
     * @throws Exception en caso de error.
     */
    List<TipoSolicitud> findAllTiposSolicitud() throws Exception;

    /**
     * Recupera el catálogo de estados de solicitud.
     * @return List
     * @throws Exception en caso de error.
     */
    List<EstadoSolicitud> findAllEstadosSolicitud() throws Exception;

    /**
     * Recupera un estado de solicitud por su identificador.
     * @param pIdSolicitud String
     * @return EstadoSolicitud
     * @throws Exception en caso de error.
     */
    EstadoSolicitud getEstadoSolicitudById(String pIdSolicitud) throws Exception;

    /**
     * Persite un objeto rangoSerie.
     * @param rangoSerie RangoSerie
     * @return RangoSerie
     * @throws Exception excepcion
     */
    RangoSerie saveRangoSerie(RangoSerie rangoSerie) throws Exception;

    /**
     * Recupera un ABN por código.
     * @param pCodigo BigDecimal
     * @return Abn
     * @throws Exception en caso de error
     */
    Abn getAbnById(BigDecimal pCodigo) throws Exception;

    /**
     * Recupera el catálogo de centrales de áreas de numeración.
     * @return List
     * @throws Exception en caso de error
     */
    List<AbnCentral> findAllCentralesAbn() throws Exception;

    /**
     * Recupera una central por código.
     * @param pCentralId BigDecimal
     * @return AbnCentral
     * @throws Exception en caso de error
     */
    AbnCentral getCentralAbnById(BigDecimal pCentralId) throws Exception;

    /**
     * Recupera el catálogo de estados de ABN.
     * @return List
     * @throws Exception en caso de error
     */
    List<EstadoAbn> findAllEstadosAbn() throws Exception;

    /**
     * Recupera un EstadoABN por su código.
     * @param pCodigo Código de EstadoABN
     * @return EstadoAbn
     */
    EstadoAbn getEstadoAbnByCodigo(String pCodigo);

    /**
     * Devuelve las Numeraciones Solicitadas de una solicitud.
     * @param codSol codigo solicitud
     * @return NumsSolicitada
     * @throws Exception escepción
     */
    List<NumeracionSolicitada> getNumeracionesSolicitadas(BigDecimal codSol) throws Exception;

    /**
     * @param fichero fichero
     * @throws Exception excepcion
     * @return RetornoProcesaFichero
     */
    RetornoProcesaFicheroAsignacion validarFicheroAsignacionAutomatica(File fichero) throws Exception;

    /**
     * Devueve la lista de solicitudes de asignación en función de los filtros facilitados.
     * @param pFiltrosSolicitud Filtros del buscador
     * @return Lista de solicitudes de asignación
     * @throws Exception en caso de que haya un error en la búsqueda
     */
    List<SolicitudAsignacion> findAllSolicitudesAsignacion(FiltroBusquedaSolicitudes pFiltrosSolicitud)
            throws Exception;

    /**
     * Recupera una serie por SNA y NIR.
     * @param pIdSna BigDecimal
     * @param pIdNir BigDecimal
     * @return Serie
     * @throws Exception en caso de que haya un error en la búsqueda
     */
    Serie getSerie(BigDecimal pIdSna, BigDecimal pIdNir) throws Exception;

    /**
     * Recupera las series ocupadas por un PST en un ABN.
     * @param pst pst
     * @param abn abn
     * @throws Exception excepción
     * @return listaOcupacion
     **/
    List<Serie> findSeriesPst(Proveedor pst, Abn abn) throws Exception;

    /**
     * Recupera las series ocupadas por otros PSTs en un ABN.
     * @param pst pst
     * @param abn abn
     * @throws Exception excepción genérica
     * @return listaOcupacion
     */
    List<Serie> findSeriesOtrosPsts(Proveedor pst, Abn abn) throws Exception;

    /**
     * Recupera las series libres en un ABN.
     * @param abn abn
     * @return listaSerie
     * @throws Exception excepcion
     */
    List<Serie> findSeriesLibres(Abn abn) throws Exception;

    /**
     * Recupera las series libres en función de los filtros dados.
     * @param pFiltros Filtros de búsqueda
     * @return List
     * @throws Exception en caso de error
     */
    List<Serie> findAllSeries(FiltroBusquedaSeries pFiltros) throws Exception;

    /**
     * Recupera el número de resultados de búsqueda de series/rangos en función de los filtros.
     * @param pFiltros Filtros de búsqueda
     * @return int número de series para series libres o de rangos para series no libres
     * @throws Exception Exception en caso de error
     */
    int findAllSeriesRangosCount(FiltroBusquedaSeries pFiltros) throws Exception;

    /**
     * Recupera los rangos solicitados en función de los parámetros facilitados.
     * @param pFiltros Filtros de Búsqueda
     * @return List
     * @throws Exception en caso de error.
     */
    List<RangoSerie> findAllRangos(FiltroBusquedaRangos pFiltros) throws Exception;

    /**
     * Recupera el número rangos solicitados en función de los parámetros facilitados.
     * @param pFiltros Filtros de Búsqueda
     * @return int Número de registros
     * @throws Exception en caso de error.
     */
    int findAllRangosCount(FiltroBusquedaRangos pFiltros) throws Exception;

    /**
     * Ejecuta las liberacione solicitadas de una Solicitud de Liberación.
     * @param pSolicitud Información de la liberación
     * @return Nueva instancia de la Solicitud de Liberación con los cambios
     * @throws Exception en caso de error.
     */
    SolicitudLiberacionNg applyLiberacionesSolicitadas(SolicitudLiberacionNg pSolicitud) throws Exception;

    /**
     * Ejecuta las cesiones solicitadas de una Solicitud de Cesión.
     * @param pSolicitud Información de la Cesión
     * @return Nueva instancia de la Solicitud de Cesión con los cambios
     * @throws Exception en caso de error.
     */
    SolicitudCesionNg applyCesionesSolicitadas(SolicitudCesionNg pSolicitud) throws Exception;

    /**
     * Comprueba si existe un rango con los parámetros indicados.
     * @param pIdNir Identificador de Región.
     * @param pSna Identificador de Serie.
     * @param pNumInicial Inicio de Rango.
     * @param pAsignatario Asignatario del Rango.
     * @return True si el rango existe.
     */
    boolean existeRangoSerie(BigDecimal pIdNir, BigDecimal pSna,
            String pNumInicial, Proveedor pAsignatario);

    /**
     * Devuelve un rango asignado a una serie en función del Nir, Sna y Numero Inicial.
     * @param pIdNir Identificador de Nir
     * @param pSna Identificador de Serie
     * @param pNumInicial Número Inicial
     * @param pAsignatario Proveedor Asignatario del Rango
     * @return RangoSerie
     * @throws Exception en caso de error.
     */
    RangoSerie getRangoSerie(BigDecimal pIdNir, BigDecimal pSna,
            String pNumInicial, Proveedor pAsignatario) throws Exception;

    /**
     * Devuelve el rango que contiene la numeracion indicada por parámetros.
     * @param pIdNir Identificador de Región
     * @param pSna Identificador de Serie
     * @param pNumInicial Inicio de Fracción
     * @param pNumFinal Fin de Fracción
     * @param pAsignatario Proveedor Asignatario del Rango
     * @return Rango del proveedor que contiene la numeración
     * @throws Exception en caso de error
     */
    RangoSerie getRangoSerieByFraccion(BigDecimal pIdNir, BigDecimal pSna,
            String pNumInicial, String pNumFinal, Proveedor pAsignatario) throws Exception;

    /**
     * Recupera el total de numeraciones ocupadas en una serie.
     * @param id SeriePK
     * @return total
     * @throws Exception exception
     **/
    BigDecimal getTotalNumOcupadaSerie(SeriePK id) throws Exception;

    /**
     * Recupera las numeraciones asigandas por ABN, NIR y SNA.
     * @param abn abn de la serie
     * @param nir nir de la serie
     * @param sna identificador de la serie
     * @return lista de ocupaciones
     */
    List<RangoSerie> findNumeracionesAsiganadasSerie(Abn abn, Nir nir, BigDecimal sna);

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
     * Méotodo que salva una solicitud asignación.
     * @param pSolicitud solicitud a salvar
     * @return solicitud persistida
     * @throws Exception error al guardar
     */
    SolicitudLiberacionNg saveSolicitudLiberacion(SolicitudLiberacionNg pSolicitud) throws Exception;

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
     * Recupera una solicitud de liberación por su identificador.
     * @param pConsecutivo Identificador de la solicitud.
     * @return SolicitudLiberacionNg
     */
    SolicitudLiberacionNg getSolicitudLiberacionById(BigDecimal pConsecutivo);

    /**
     * Recupera una Solicitud de Liberacion con todas sus dependencias cargadas: Lista de Liberaciones Solicitadas,
     * Aplicadas y Oficios.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudLiberacionNg
     * @throws Exception en caso de error.
     */
    SolicitudLiberacionNg getSolicitudLiberacionEagerLoad(SolicitudLiberacionNg pSolicitud) throws Exception;

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
     * Méotodo que salva una solicitud asignación.
     * @param solicitudAsignacion solicitud a crear
     * @return solicitudasignación salvada
     * @throws Exception error al crear solicitud
     */
    SolicitudAsignacion saveSolicitudAsignacion(SolicitudAsignacion solicitudAsignacion) throws Exception;

    /**
     * Méotodo que salva una solicitud cesión.
     * @param solicitudCesion solicitud a crear
     * @return solicitudcesión salvada
     * @throws Exception error al crear solicitud
     */
    SolicitudCesionNg saveSolicitudCesion(SolicitudCesionNg solicitudCesion) throws Exception;

    /**
     * Si es posible, restablece la numeración afectada por el trámite deshaciendo los cambios.
     * @param pSolicitud Solicitud de Cesión a Modififcar.
     * @return PeticionCancelacion con la información del proceso de cancelación.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelSolicitudCesion(SolicitudCesionNg pSolicitud) throws Exception;

    /**
     * Cancela una cesión.
     * @param pCesSol Información de la cesión.
     * @param pUseCheck Indica si es necesario validar la cancelación previamente.
     * @return Objeto PeticionCancelacion con la información del proceso.
     * @throws Exception en caso de error.
     */
    PeticionCancelacion cancelCesion(CesionSolicitadaNg pCesSol, boolean pUseCheck) throws Exception;

    /**
     * Devuelve los rangos de una serie por solicitud.
     * @param codSol codigo solicitud
     * @return Ocupacion
     * @throws Exception escepción
     */
    List<RangoSerie> findNumeracionesSeleccionadas(BigDecimal codSol) throws Exception;

    /**
     * Devuelve una central por su id.
     * @param idCentral id central
     * @return central por id
     * @throws Exception exception
     */
    Central findCentralById(BigDecimal idCentral) throws Exception;

    /**
     * Devuelve las rangos sin asignar de una serie por solicitud.
     * @param codSol codigo solicitud
     * @return Ocupacion
     * @throws Exception escepción
     */
    List<RangoSerie> findNumeracionesSeleccionadasSinAsignar(BigDecimal codSol)
            throws Exception;

    /**
     * Recupera el total de numeraciones asignadas por solicitud.
     * @param codSol codigo de solicitud
     * @return total
     * @throws Exception exception
     **/
    BigDecimal getTotalNumAsignadaSolicitud(BigDecimal codSol) throws Exception;

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
     * Recupera el total de numeraciones ocupadas en un ABN.
     * @param abn abn
     * @return total
     * @throws Exception exception
     */
    BigDecimal getTotalNumOcupadaAbn(Abn abn) throws Exception;

    /**
     * Devuelve un objeto EstadoRango por su codigo.
     * @param codigo codigo
     * @return EstadoRango
     * @throws Exception en caso de error.
     */
    EstadoRango getEstadoRangoByCodigo(String codigo) throws Exception;

    /**
     * Recupera el catálogo estados de rangos.
     * @return List
     */
    List<EstadoRango> findAllEstadosRango();

    /**
     * Lista de estado.
     * @return lista de estados
     * @throws Exception exepcion
     */
    List<Estado> findEstados() throws Exception;

    /**
     * Devuelve un estado por su id.
     * @param id estado
     * @return estado
     * @throws Exception excepcion
     */
    Estado findEstadoById(String id) throws Exception;

    /**
     * Lista de municipios por estado.
     * @param estado estado
     * @return lista de municipios
     * @throws Exception exepcion
     */
    List<Municipio> findMunicipiosByEstado(String estado) throws Exception;

    /**
     * Devuelve un municipio por su id.
     * @param id estado y municipio
     * @return municipio
     * @throws Exception excepcion
     */
    Municipio findMunicipioById(MunicipioPK id) throws Exception;

    /**
     * Devuelve lista de poblacion de un estado y municipio concreto.
     * @param estado Código de Estado
     * @param municipio Código de Municipio
     * @return Lista de poblaciones por estado y municipio
     */
    List<Poblacion> findAllPoblaciones(String estado, String municipio);

    /**
     * Devuelve una poblacion por su id.
     * @param inegi inegi
     * @return poblacion
     * @throws Exception excepcion
     */
    Poblacion findPoblacionById(String inegi) throws Exception;

    /**
     * Recupera la lista de Solicitudes de asignación completa, sin filtros.
     * @return lista de solicitudes de asignación
     * @throws Exception error al devolver la lista de solicitudes
     */
    List<SolicitudAsignacion> findAllSolicitudesAsignacion() throws Exception;

    /**
     * Busca las posibles centrales origen de un concesionario y un arredatario por nombre.
     * @param name name
     * @param concesionario concesionario
     * @param arrendatario arrendatario
     * @return List<Central>
     * @throws Exception exception
     */
    List<Central> findAllCentralesOrigenByName(String name, Proveedor concesionario, Proveedor arrendatario)
            throws Exception;

    /**
     * Devuelve un objeto Contacto por su id.
     * @param pIdContacto pIdContacto
     * @return Contacto
     * @throws Exception exception
     */
    Contacto getRepresentanteLegalById(BigDecimal pIdContacto) throws Exception;

    /**
     * Recupera la poblacion ancla de un ABN.
     * @param codigo abn
     * @return poblacion ancla
     * @throws Exception en caso de error
     */
    Poblacion getPoblacionAnclaByCodigoAbn(BigDecimal codigo) throws Exception;

    /**
     * Méotodo que salva una solicitud redistribución.
     * @param pSolicitud pSolicitud a crear
     * @return solicitudRedistribución salvada
     */
    SolicitudRedistribucionNg saveSolicitudRedistribucion(SolicitudRedistribucionNg pSolicitud);

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
     * Método que salva una central.
     * @param central central
     * @return central central
     * @throws Exception exception
     */
    Central saveCentral(Central central) throws Exception;

    /**
     * Método que salva una central desde 
     * el tramite de Asignacion Automatica NG
     * @param central central
     * @return central central
     * @throws Exception exception
     */
    Central saveCentralFromAsignacion(Central central) throws Exception;
    
    /**
     * Devueve la lista de consolidaciones de abn´s, sin filtros.
     * @return lista de SolicitudConsolidacion
     * @throws Exception error al devolver la lista de solicitudes
     */
    List<SolicitudConsolidacion> findAllSolicitudesConsolidacion() throws Exception;

    /**
     * Devueve la lista de consolidaciones de abn´s, con filtros.
     * @param pFiltrosSolicitud pFiltrosSolicitud
     * @return lista de SolicitudConsolidacion
     * @throws Exception Exception
     */
    List<SolicitudConsolidacion> findAllSolicitudesConsolidacion(FiltroBusquedaSolicitudes pFiltrosSolicitud)
            throws Exception;

    /**
     * Recupera el número de solicitudes de consolidacion que cumplen los filtros dados.
     * @param pFiltrosSolicitud pFiltros filtros de busqueda
     * @return número de solicitudes de consolidacion
     * @throws Exception en caso de error.
     */
    int findAllSolicitudesConsolidacionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) throws Exception;

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
     * Devuelve todos los estados.
     * @return List<Estado>
     * @throws Exception Exception
     */
    List<Estado> findAllEstados() throws Exception;

    /**
     * Generar el plan de ABD Presuscripcion si es posible.
     * @param ficheroArrendador fichero PST arrendador
     * @param ficheroArrendatario fichero PST arrendatario
     * @return res resultado de la validacion
     */
    ResultadoValidacionArrendamiento validar(String ficheroArrendador, String ficheroArrendatario);

    /**
     * Genera tablas reporte abd.
     */
    void generarTablas();

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
     * Recupera un nir por su código.
     * @param cdgNir codigo
     * @return el nir
     */
    Nir getNirByCodigo(int cdgNir);

    /**
     * Recupera un nir.
     * @param id id del nir
     * @return el nir
     */
    Nir getNir(BigDecimal id);

    /**
     * Guarda un AbnCentral.
     * @param abnCentral abnCentral
     * @throws Exception Exception
     */
    void saveAbnCentral(AbnCentral abnCentral) throws Exception;

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
     * Genera el analis de la numeracion geográfica de una solicitud de asignacion NG.
     * @param solicitud asignacion
     * @return InputStream xls file
     * @throws Exception error
     */
    InputStream generarAnalisisNumeracion(SolicitudAsignacion solicitud) throws Exception;

    /**
     * Devuelve los nirs.
     * @param numAbn abn
     * @return List<Nir>
     * @throws Exception Exception
     */
    List<Nir> findAllNirByAbn(BigDecimal numAbn) throws Exception;

    /**
     * Méotodo que salva una solicitud de consolidacion.
     * @param pSolicitud solicitud a salvar
     * @return solicitud persistida
     * @throws Exception error al guardar
     */
    SolicitudConsolidacion saveSolicitudConsolidacion(SolicitudConsolidacion pSolicitud) throws Exception;

    /**
     * Recupera una Solicitud de Consolidacion con todas sus dependencias cargadas.
     * @param pSolicitud Solicitud a cargar
     * @return SolicitudLiberacionNg
     * @throws Exception en caso de error.
     */
    SolicitudConsolidacion getSolicitudConsolidacionEagerLoad(SolicitudConsolidacion pSolicitud) throws Exception;

    /**
     * Recupera la lista de poblaciones de un abn.
     * @param pAbn Información del Abn.
     * @return List<Poblacion> poblaciones.
     */
    List<Poblacion> findAllPoblacionesByAbn(Abn pAbn);

    /**
     * Ejecuta las liberacione solicitadas de una Solicitud de Liberación.
     * @param pSolicitud Información de la liberación
     * @return Nueva instancia de la Solicitud de Liberación con los cambios
     * @throws Exception en caso de error.
     */

    SolicitudRedistribucionNg applyRedistribucionesSolicitadas(SolicitudRedistribucionNg pSolicitud) throws Exception;

    /**
     * Método que devuelve las poblaciones a partir del Abn, del estado y del municipio.
     * @param estado estado
     * @param municipio municipio
     * @param abn abn
     * @return List<Poblacion>
     * @throws Exception exception
     */
    List<Poblacion> findAllPoblacionByAbnUbicacion(String estado, String municipio, BigDecimal abn)
            throws Exception;

    /**
     * Actualiza un abn.
     * @param abn abn
     * @return Abn
     * @throws Exception exception
     */
    Abn saveAbn(Abn abn) throws Exception;

    /**
     * Recupera la lista de series arrendadas.
     * @param first Información de paginación.
     * @param pageSize first Información de paginación.
     * @return List
     */
    List<SerieArrendada> findSeriesArrendadas(int first, int pageSize);

    /**
     * Recupera el número de series arrendadas.
     * @return int
     */
    int getSeriesArrendadasCount();

    /**
     * Recupera una lista de todos los proveedor de tipo concesionario.
     * @return lista proveedores
     */
    List<Proveedor> findAllConcesionarios();

    /**
     * Recupera los tipos de reporte.
     * @return lista TipoReporte
     * @throws Exception exception
     */
    List<TipoReporte> findAllTiposReporte() throws Exception;

    /**
     * Obtiene el tipo de reporte por su codigo.
     * @param value codigo
     * @return tipo reporte
     */
    TipoReporte getTipoReporteByCdg(String value);

    /**
     * Instancia del Servicio de series de numeración geográfica.
     * @return Instancia del Servicio
     */
    ISeriesService getSeriesService();

    /**
     * Instancia del Servicio de organización territorial.
     * @return Instancia del Servicio
     */
    IOrganizacionTerritorialService getOtService();

    /**
     * Instancia del Servicio generación de oficios.
     * @return Instancia del Servicio
     */
    IOficiosService getOficiosService();

    /**
     * @return Instancia del Servicio
     */
    IValidadorArchivosPNNABD getValidadorABD();

    /**
     * Valida el reporte de lineas activas.
     * @param fichero fichero
     * @return RetornoProcesaFicheroReportes
     * @throws Exception exception
     */
    RetornoProcesaFicheroReportes validarFicheroLineasActivas(File fichero) throws Exception;

    /**
     * Recupera el número de solicitudes de asignación que cumplen los filtros dados.
     * @param filtros filtros de busqueda
     * @return int número de solicitudes de asignación
     * @throws Exception en caso de error.
     */
    int findAllSolicitudesAsignacionCount(FiltroBusquedaSolicitudes filtros) throws Exception;

    /**
     * Obtiene el total de numeraciones en los rangos asignados a un PST.
     * @param tipoRed String
     * @param tipoModalidad String
     * @param proveedor Proveedor
     * @return total
     */
    BigDecimal getTotalRangosAsignadosByPst(String tipoRed, String tipoModalidad, Proveedor proveedor);

    /**
     * Obtiene el total de numeraciones en los rangos asignados a un PST por poblacion.
     * @param tipoRed tipoRed
     * @param tipoModalidad tipoModalidad
     * @param proveedor proveedor
     * @param poblacion poblacion
     * @return BigDecimal
     */
    BigDecimal getTotalNumRangosAsignadosByPoblacion(String tipoRed, String tipoModalidad, Proveedor proveedor,
            Poblacion poblacion);

    /**
     * Salva una Linea Activa.
     * @param lineaActiva lineaActiva
     * @return LineaActiva
     */
    ReporteLineasActivas saveLineaActiva(ReporteLineasActivas lineaActiva);

    /**
     * Métdodo para guardar el Nir.
     * @param nir nir
     * @return Nir
     */
    Nir saveNir(Nir nir);

    /**
     * Método que devuelve todos los Abn Centrales a partir del abn.
     * @param codigo codigo
     * @return List<AbnCentral>
     */
    List<AbnCentral> findAllAbnCentralesByAbn(BigDecimal codigo);

    /**
     * Método que devuelve la población a partir del abn y el inegi.
     * @param codigoAbn codigoAbn
     * @param inegi inegi
     * @return Poblacion
     */
    Poblacion getPoblacionByAbnInegi(BigDecimal codigoAbn, String inegi);

    /**
     * Método para guardar la población.
     * @param poblacion poblacion
     * @return Poblacion
     */
    Poblacion savePoblacion(Poblacion poblacion);

    /**
     * Método que devuelve cuantos hay.
     * @param codigo codigo
     * @return int
     */
    int findAllPoblacionesCount(BigDecimal codigo);

    /**
     * Recupera el listado de Poblaciones que cumplen con el filtro de búsqueda.
     * @param pFiltros FiltroBusquedaPoblaciones
     * @return List<Poblacion>
     */
    List<Poblacion> findAllPoblaciones(FiltroBusquedaPoblaciones pFiltros);

    /**
     * Busca las poblaciones por filtro.
     * @param filtros filtros
     * @return int
     */
    int findAllPoblacionesCount(FiltroBusquedaPoblaciones filtros);

    /**
     * Método que devuelve cuantos hay.
     * @param codigo codigo
     * @return int
     */
    int findAllMunicipiosCount(BigDecimal codigo);

    /**
     * Valida el reporte de lineas activas detallado.
     * @param fichero File
     * @return RetornoProcesaFicheroReportes
     * @throws Exception Exception
     */
    RetornoProcesaFicheroReportes validarFicheroLineasActivasDet(File fichero) throws Exception;

    /**
     * Guarda una linea activa detallada.
     * @param lineaActivaDet lineaActivaDet
     * @return LineaActivaDet
     */
    LineaActivaDet saveLineaActivaDet(LineaActivaDet lineaActivaDet);

    /**
     * Devueve la lista de solicitudes, con filtros.
     * @param pFiltrosSolicitud pFiltrosSolicitud
     * @return lista de Solicitud
     * @throws Exception Exception
     */
    List<Solicitud> findAllSolicitudes(FiltroBusquedaSolicitudes pFiltrosSolicitud)
            throws Exception;

    /**
     * Recupera el número de solicitudes que cumplen los filtros dados.
     * @param pFiltrosSolicitud pFiltros filtros de busqueda
     * @return número de solicitudes
     * @throws Exception en caso de error.
     */
    int findAllSolicitudesCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) throws Exception;

    /**
     * Método que recupera el tipo solicitud a oartir del id.
     * @param idTipoSolicitud idTipoSolicitud
     * @return TipoSolicitud
     * @throws Exception exception
     */
    TipoSolicitud getTipoSolicitudById(Integer idTipoSolicitud) throws Exception;

    /**
     * Valida el reporte de lineas activas arrendatario.
     * @param fichero file
     * @return RetornoProcesaFicheroReportes
     * @throws Exception Exception
     */
    RetornoProcesaFicheroReportes validarFicheroLineasArrendatario(File fichero) throws Exception;

    /**
     * Guarda una linea arrendatario.
     * @param lineaArrendatario lineaArrendatario
     * @return LineaArrendatario
     */
    LineaArrendatario saveLineaArrendatario(LineaArrendatario lineaArrendatario);

    /**
     * Valida el reporte de lineas arrendadas.
     * @param fichero file
     * @return RetornoProcesaFicheroReportes
     * @throws Exception Exception
     */
    RetornoProcesaFicheroReportes validarFicheroLineasArrendada(File fichero) throws Exception;

    /**
     * Guarda una linea arrendada.
     * @param lineaArrendada lineaArrendada
     * @return LineaArrendada
     */
    LineaArrendada saveLineaArrendada(LineaArrendada lineaArrendada);

    /**
     * Método para obtener el histórico de las series.
     * @param filtro filtro
     * @param first first
     * @param pagesize pagesize
     * @return List<RangoSerie>
     */
    List<HistoricoSerie> findHistoricoSeries(FiltroBusquedaHistoricoSeries filtro, int first, int pagesize);

    /**
     * Método que devuelve el número del histórico de series.
     * @param filtro filtro
     * @return int
     */
    int findHistoricoSeriesCount(FiltroBusquedaHistoricoSeries filtro);

    /**
     * Ejecuta las consolidaciones solicitadas de una Solicitud de consolidacion.
     * @param pSolicitud Información de la Consolidacón
     * @return Nueva instancia de la Solicitud de consolidacion con los cambios
     * @throws Exception en caso de error.
     */
    SolicitudConsolidacion applyConsolidacionesSolicitadas(SolicitudConsolidacion pSolicitud) throws Exception;

    /**
     * Recupera la lista de solicitudes de cesión en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return lista de solicitudes de cesión filtrada
     */
    List<DetalleLineaActiva> findAllSolicitudesLineasActivas(FiltroBusquedaLineasActivas pFiltrosSolicitud);

    /**
     * Recupera el número de solicitudes de cesión que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de cesión
     */
    int findAllSolicitudesLineasActivasCount(FiltroBusquedaLineasActivas pFiltrosSolicitud);

    /**
     * Recupera las centrales de uno o dos proveedores.
     * @param pst1 Proveedor
     * @param pst2 Proveedor
     * @return lista Central
     */
    List<Central> findAllCentralesByProveedores(Proveedor pst1, Proveedor pst2);

    /**
     * Método que realiza el guardado en BD de los AbnConsolidar.
     * @param listaPoblacion lista de poblaciones a consolidar
     * @param fechaConsolidacion fecha de consolidación
     * @param solicitud solicitud de consolidación
     * @param listaNir lista de nirs
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
     * Obtiene el combo del catalogo de centrales.
     * @return combo centrales
     */
    List<ComboCentral> getComboCentrales();

    /**
     * Persiste una SolicitudLineasActivas.
     * @param solicitudLineasActivas solicitudLineasActivas
     * @return SolicitudLineasActivas
     */
    SolicitudLineasActivas saveSolicitudLineasActivas(SolicitudLineasActivas solicitudLineasActivas);

    /**
     * Recupera la lista de solicitudes de lineas activas en función de los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return List<SolicitudLineasActivas>
     */
    List<SolicitudLineasActivas> findAllSolicitudesLineasActivasConsulta(FiltroBusquedaLineasActivas pFiltrosSolicitud);

    /**
     * Persite la relacion entre centrales de origen y destino.
     * @param centralesRelacion relacion
     * @return CentralesRelacion relacion
     */
    CentralesRelacion saveCentralesRelacion(CentralesRelacion centralesRelacion);

    /**
     * Recupera el número de solicitudes de cesión que cumplen los filtros dados.
     * @param pFiltrosSolicitud filtros de busqueda
     * @return número de solicitudes de cesión
     */
    int findAllSolicitudesLineasActivasConsultaCount(FiltroBusquedaLineasActivas pFiltrosSolicitud);

    /**
     * Obtiene el ultimo reporte de lineas activas de un proveedor.
     * @param proveedorSolicitante pst
     * @return reporte lineas activas
     */
    ReporteLineasActivas getLastReporteLineasActiva(Proveedor proveedorSolicitante);

    /**
     * Obtiene los rangos creado a partir de una numeracion solicitada.
     * @param numeracionSolicitada numeracionSolicitada
     * @return lista rangos
     */
    List<RangoSerie> findAllRangosByNumSol(NumeracionSolicitada numeracionSolicitada);

    /**
     * Obtiene las LiberacionesSolicitadas en Trámite que esten antes de ejecución en 1 o 2 días.
     * @return List<String>
     */
    List<String> getNotificacionesLoginLiberacion();

    /**
     * Obtiene las CesionesSolicitadas en Trámite que esten antes de ejecución en 1 o 2 días.
     * @return List<String>
     */
    List<String> getNotificacionesLoginCesion();

    /**
     * Comprueba si el abn está asociado a la poblacion.
     * @param poblacion Poblacion
     * @param abn Abn
     * @return boolean
     */
    boolean existePoblacionEnAbn(Poblacion poblacion, Abn abn);

    /**
     * Obtiene el total de numeraciones en los rangos asignados a un PST por abn.
     * @param tipoRed tipoRed
     * @param tipoModalidad tipoModalidad
     * @param abn abn
     * @param proveedor proveedor
     * @return BigDecimal
     */

    BigDecimal getTotalNumRangosAsignadosByAbn(String tipoRed, String tipoModalidad, Abn abn, Proveedor proveedor);

    /**
     * @param abn Abn.
     * @return Poblacion con mayor numeracion asignada de un abn.
     * @throws Exception Exception
     */

    Poblacion getPoblacionWithMaxNumAsignadaByAbn(Abn abn) throws Exception;

    /**
     * Obtiene el estatus del catálogo por el id.
     * @param pId String
     * @return Estatus
     * @throws Exception ex
     */
    Estatus getEstatusById(String pId) throws Exception;

    /**
     * Médotodo que obtiene el tipo de servicio a través de su id.
     * @param id String
     * @return TipoServicio
     * @throws Exception ex
     */
    TipoServicio getTipoServicioById(String id) throws Exception;

    /**
     * Recupera el listado de Proveedores que cumplen con el filtro de búsqueda.
     * @param pFiltros FiltroBusquedaProveedores
     * @return List<Proveedor>
     * @throws Exception ex
     */
    List<Proveedor> findAllProveedores(FiltroBusquedaProveedores pFiltros) throws Exception;

    /**
     * Obtiene el número de proveedores que cumplen los filtros de búsqueda.
     * @param pFiltros FiltroBusquedaProveedores
     * @return int
     * @throws Exception ex
     */
    int findAllProveedoresCount(FiltroBusquedaProveedores pFiltros) throws Exception;

    /**
     * Obtiene el tipo de contacto por el cdg.
     * @param pCdgTipo String
     * @return TipoContacto
     */
    TipoContacto getTipoContactoByCdg(String pCdgTipo);

    /**
     * Recupera el catálogo de tipos de destinatario.
     * @return List
     * @throws Exception en caso de error.
     */
    List<TipoDestinatario> findAllTiposDestinatario() throws Exception;

    /**
     * Recupera un Tipo Destinatario por su código.
     * @param pCdgDestinatario Código de Tipo de Destinatario
     * @return TipoDestinatario
     * @throws Exception en caso de error.
     */
    TipoDestinatario getTipoDestinatarioByCdg(String pCdgDestinatario) throws Exception;

    /**
     * Recupera un oficio en función de su Solicitud y Tipo de Destinatario.
     * @param pSolicitud Solicitud a la que pertenece
     * @param pCdgTipoDestinatario Tipo de Destinatario del oficio
     * @return Oficio
     */
    Oficio getOficio(Solicitud pSolicitud, String pCdgTipoDestinatario);

    /**
     * Guarda o actualiza el proveedor en la bbdd.
     * @param proveedor Proveedor
     * @param encriptarPass boolean
     * @return Proveedor
     */
    Proveedor guardarProveedor(Proveedor proveedor, boolean encriptarPass);

    /**
     * Exporta los datos generales de los proveedores.
     * @param filtro FiltroBusquedaProveedores
     * @return byte[]
     * @throws Exception ex
     */
    byte[] exportarDatosGenerales(FiltroBusquedaProveedores filtro) throws Exception;

    /**
     * Exporta los datos de los contactos de los proveedores.
     * @param filtro FiltroBusquedaProveedores
     * @return byte[]
     * @throws Exception ex
     */
    byte[] exportarDatosContactos(FiltroBusquedaProveedores filtro) throws Exception;

    /**
     * Exporta los datos de convenios de los proveedores.
     * @param filtro FiltroBusquedaProveedores
     * @return byte[]
     * @throws Exception ex
     */
    byte[] exportarDatosConvenios(FiltroBusquedaProveedores filtro) throws Exception;

    /**
     * Comprueba si existe otro proveedor con el mismo IDO.
     * @param proveedor Proveedor
     * @return <ul>
     *         <li>0 - No Existe</li>
     *         <li>1 - Existe Activo</li>
     *         <li>2- Existe Inactivo</li>
     *         </ul>
     */
    int existeIdo(Proveedor proveedor);

    /**
     * Comprueba si existe otro proveedor con el mismo IDA.
     * @param proveedor Proveedor
     * @return <ul>
     *         <li>0 - No Existe</li>
     *         <li>1 - Existe Activo</li>
     *         <li>2- Existe Inactivo</li>
     *         </ul>
     */
    int existeIda(Proveedor proveedor);

    /**
     * Comprueba si existe otro proveedor con el mismo ABC.
     * @param proveedor Proveedor
     * @return <ul>
     *         <li>0 - No Existe</li>
     *         <li>1 - Existe Activo</li>
     *         <li>2- Existe Inactivo</li>
     *         </ul>
     */
    int existeAbc(Proveedor proveedor);

    /**
     * Comprueba si existe otro proveedor con el mismo BCD.
     * @param proveedor Proveedor
     * @return <ul>
     *         <li>0 - No Existe</li>
     *         <li>1 - Existe Activo</li>
     *         <li>2- Existe Inactivo</li>
     *         </ul>
     */
    int existeBcd(Proveedor proveedor);

    /**
     * Obtiene todos los estatus posibles del PST.
     * @return List<Estatus> listado
     */
    List<Estatus> findAllEstatus();

    /**
     * Comprueba si existe el usuario por el id.
     * @param idUsuario a buscar
     * @return boolean existe
     */
    boolean existeUsuario(String idUsuario);

    /**
     * Valida si los datos del proveedor son correctos.
     * @param proveedor Proveedor
     * @param tipoPstInicial String
     * @param tipoRedInicial String
     * @param validaUsuario boolean
     * @return Proveedor proveedor
     */
    Proveedor validaProveedor(Proveedor proveedor, String tipoPstInicial, String tipoRedInicial, boolean validaUsuario);

    /**
     * Valida si existe un representante legal para el proveedor.
     * @param proveedor Proveedor
     * @return boolean boolean
     */
    boolean faltaRepresentanteLegal(Proveedor proveedor);

    /**
     * Lista de poblaciones por estado.
     * @param estado estado
     * @param codAbn codAbn
     * @return lista de poblaciones
     */
    List<Poblacion> findAllPoblacionesByEstadoAbn(String estado, BigDecimal codAbn);

    /**
     * Recupera un Nir por su identificador.
     * @param pIdNir BigDecimal
     * @return Nir
     * @throws Exception en caso de error.
     */
    Nir getNirById(BigDecimal pIdNir) throws Exception;

    /**
     * Recupera la lista de Proveedores que soportarn el tipo de red del proveedor.
     * @param tipoRed TipoRed
     * @return List
     * @throws Exception en caso de error.
     */
    List<Proveedor> findAllProveedoresByTRed(TipoRed tipoRed) throws Exception;

    /**
     * Método que devuelve los tipos de red válidos (desconocido no se muestra).
     * @return List
     */
    List<TipoRed> findAllTiposRedValidos();

    /**
     * Método que devuelve los tipos de red válidos que soportaan el tipo de red especificado.
     * @param tipoRed TipoRed
     * @return List
     */
    List<TipoRed> findAllTiposRedValidos(TipoRed tipoRed);

    /**
     * Función que comprueba si existe una numeración asignada al Pst.
     * @param proveedor Proveedor
     * @return boolean existe
     */
    boolean existeNumeracionAsignadaAlPst(Proveedor proveedor);

    /**
     * Función que comprueba si existe una numeración asignada al Pst a través de un convenio.
     * @param proveedorConvenio ProveedorConvenio
     * @return boolean existe
     */
    boolean existeNumeracionAsignadaAlPstByConvenio(ProveedorConvenio proveedorConvenio);

    /**
     * Devuelve el número de nirs.
     * @param abn abn
     * @return int
     * @throws Exception Exception
     */
    int findAllNirByAbnCount(BigDecimal abn);

    /**
     * Método que obtiene el número de nirs a partir de las poblaciones.
     * @param poblacion poblacion
     * @return int
     */
    int getNirsByPoblacion(Poblacion poblacion);

    /**
     * Método que obtiene los Nirs de una poblacion.
     * @param poblacion poblacion
     * @return List<Nir>
     */
    List<Nir> findAllNirsByPoblacion(Poblacion poblacion);

    /**
     * Función que obtiene el listado de Nirs Activos.
     * @return List<Nir>
     * @throws Exception exception
     */
    List<Nir> findAllNirs() throws Exception;

    /**
     * Obtiene todos los registros de un reporte de lineas activas cuyo total asignacion no coincide en el SNS.
     * @param reporte reporte
     * @return registros
     */
    List<String> findAllAvisoAsignacionDetalleLineaActiva(Reporte reporte);

    /**
     * Obtiene la poblacion por un inegi.
     * @param inegi inegi
     * @return poblacion
     */
    Poblacion getPoblacionByInegi(String inegi);

    /**
     * Guardado masivo de los registro del reporte de lineas activas.
     * @param listaDatos registros
     * @param idReporte reporte
     */
    void saveDetalleLineaActiva(List<String> listaDatos, BigDecimal idReporte);

    /**
     * Obtiene los registro de un reporte de lineas activas detallado.
     * @param idReporte reporte
     * @param first pagina
     * @param pageSize registros
     * @return lista detalles
     */
    List<DetLineaActivaDet> findAllDetLineaActivaDetByReporte(BigDecimal idReporte, int first, int pageSize);

    /**
     * Carga masiva de registros de un reporte de lineas activas detallada.
     * @param listaDatos registros
     * @param idReporte reporte
     */
    void saveDetLineaActivaDet(List<String> listaDatos, BigDecimal idReporte);

    /**
     * Comprueba el datos de total asignaciones de los registros de un reporte de lineas activas detallada.
     * @param reporte reporte
     * @return avisos
     */
    List<String> findAllAvisoAsignacionDetalleLineaActivaDet(Reporte reporte);

    /**
     * Carga masiva de registros de un reporte de lineas activas arrendatario.
     * @param listaDatos registros
     * @param idReporte reporte
     */
    void saveDetLineaArrendatario(List<String> listaDatos, BigDecimal idReporte);

    /**
     * Obtiene los registro de un reporte de lineas arrendatario.
     * @param idReporte reporte
     * @param first pagina
     * @param pageSize registros
     * @return lista detalles
     */
    List<DetLineaArrendatario> findAllDetLineaArrendatarioByReporte(BigDecimal idReporte, int first, int pageSize);

    /**
     * Carga masiva de registros de un reporte de lineas arredadas.
     * @param listaDatos registros
     * @param idReporte reporte
     */
    void saveDetLineaArrendada(List<String> listaDatos, BigDecimal idReporte);

    /**
     * Obtiene los registro de un reporte de lineas arrendadas.
     * @param idReporte reporte
     * @param first pagina
     * @param pageSize registros
     * @return lista detalles
     */
    List<DetLineaArrendada> findAllDetLineaArrendadaByReporte(BigDecimal idReporte, int first, int pageSize);

    /**
     * Consulta generica a la vista de detalle de reporte.
     * @param filtros FiltroBusquedaLineasActivas
     * @return lista detalles
     */
    List<DetalleReporte> findAllDetalleReporte(FiltroBusquedaLineasActivas filtros);

    /**
     * Obtiene el total de una consulta generica a la vista de detalle de reporte.
     * @param filtros FiltroBusquedaLineasActivas
     * @return total
     */
    Integer findAllDetalleReporteCount(FiltroBusquedaLineasActivas filtros);

    /**
     * FUnción que valida que los datos del contacto sean correctos.
     * @param contacto Contacto
     * @return List<String> errores de validación del contacto
     */
    List<String> validarContacto(Contacto contacto);

    /**
     * Función que comprueba si el contacto está siendo referenciado en algún trámite.
     * @param contacto Contacto
     * @return boolean usado
     */
    boolean contactoNoUsado(Contacto contacto);

    /**
     * Genera un fichero excel con los filtros de una cosulta de lineas activas.
     * @param filtro busqueda
     * @return fichero
     * @throws Exception error
     */
    InputStream getExportConsultaLineaActiva(FiltroBusquedaLineasActivas filtro) throws Exception;

    /**
     * Busca las series ocupadas segun filtro.
     * @param filtros FiltroBusquedaSeries
     * @return series
     */
    List<Object[]> findAllSeriesOcupadas(FiltroBusquedaSeries filtros);

    /**
     * Método que devuelve todas las poblaciones a consolidar para un abnConsolidar.
     * @param pId id
     * @return List<PoblacionConsolidar>
     */
    List<PoblacionConsolidar> findPoblacionConsolidarById(BigDecimal pId);

    /**
     * Método que devuelve todas los Nirs a consolidar para un abnConsolidar.
     * @param pId id
     * @return List<NirConsolidar>
     */
    List<NirConsolidar> findNirConsolidarById(BigDecimal pId);

    /**
     * Método que devuelve el abnConsolidar dependiendo del Id de la Solicitud.
     * @param pIdSol id de la solicitud
     * @return AbnConsolidar
     */
    AbnConsolidar getAbnConsolidarByIdSol(BigDecimal pIdSol);

    /**
     * Método que devuelve la fecha de consolidacion de un Abn.
     * @param rangoAbn el Abn del rango
     * @return un String con la fecha de consolidacion.
     */
    String getFechaConsolidacionByRangoAbn(Abn rangoAbn);

    /**
     * Elimina rango de numeracion.
     * @param rango serie
     * @throws Exception error
     */
    void removeRangoSerie(RangoSerie rango) throws Exception;

    /**
     * Genera un fichero excel con los filtros de una cosulta de historico de series.
     * @param filtro busqueda
     * @return fichero
     * @throws Exception error
     */
    InputStream getExportHistoricoSeries(FiltroBusquedaHistoricoSeries filtro) throws Exception;

    /**
     * Recupera el número de Municipios según los filtros dados.
     * @param pFiltros filtros
     * @return int
     */
    int findAllMunicipiosByAbnAndEstadoCount(FiltroBusquedaABNs pFiltros);

    /**
     * Recupera los municipios vinculados al abn y al estado.
     * @param pFiltros filtros
     * @return List<Municipio>
     */
    List<Municipio> findAllMunicipiosByAbnAndEstado(FiltroBusquedaABNs pFiltros);

    /**
     * Recupera el número de PoblacionesAbn según los filtros dados.
     * @param pFiltros Condiciones de búsqueda.
     * @return List<PoblacionAbn> Lista de PoblacionesAbn.
     */
    int findAllPoblacionesAbnCount(FiltroBusquedaABNs pFiltros);

    /**
     * Recupera la lista de PoblacionesAbn según los filtros dados.
     * @param pFiltros Condiciones de búsqueda.
     * @return List<PoblacionAbn> Lista de PoblacionesAbn.
     */
    List<PoblacionAbn> findAllPoblacionesAbn(FiltroBusquedaABNs pFiltros);

    /**
     * Recupera la lista de poblaciones Abn de un abn.
     * @param pAbn Información del Abn.
     * @return List<Poblacion> poblaciones.
     */
    List<PoblacionAbn> findAllPoblacionAbnByAbn(Abn pAbn);

    /**
     * Comprueba si un rango esta libre.
     * @param nir nir
     * @param sna sna
     * @param inicioRango inicioRango
     * @param finRango finRango
     * @return true/false
     */
    boolean isRangoLibre(BigDecimal nir, BigDecimal sna, String inicioRango, String finRango);

    /**
     * Comprueba si una numeracion solicitada tiene numeraciones asignadas asociadas.
     * @param numeracionSolicitada numeracionSolicitada
     * @return true/false
     */
    boolean existNumeracionAsignadaBySolicita(NumeracionSolicitada numeracionSolicitada);

    /**
     * Método encargado de procesar la generación de planes ABD de los archivos de arrendador y arrendatario.
     * @param arrendadorUrl ruta del fichero de arrendador
     * @param arrendatarioUrl ruta fichero de arrendatario
     * @param arrendadorNombre nombre del fichero de arrendador
     * @param arrendatarioNombre nombre del fichero de arrendatario
     * @return ResultadoValidacionArrendamienton resultado
     */
    ResultadoValidacionArrendamiento procesarFicherosAbd(String arrendadorUrl, String arrendatarioUrl,
            String arrendadorNombre, String arrendatarioNombre);

    /**
     * Comprueba que una central extiste por su nombre, proveedor y coordenadas. Devuelve la central existente si no
     * retorna la central que se comprueba.
     * @param central central
     * @return central
     */
    Central comprobarCentral(Central central);

    /** Genera tablas reporte ABD. */
    void generarRegistrosAbd();
}
