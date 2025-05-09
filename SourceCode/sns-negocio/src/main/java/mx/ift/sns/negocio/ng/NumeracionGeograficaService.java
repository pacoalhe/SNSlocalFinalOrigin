package mx.ift.sns.negocio.ng;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import mx.ift.sns.dao.ng.IAbnCentralDao;
import mx.ift.sns.dao.ng.ICentralDao;
import mx.ift.sns.dao.ng.INumeracionSolicitadaDAO;
import mx.ift.sns.dao.ng.ISolicitudAsignacionDao;
import mx.ift.sns.dao.ng.ISolicitudConsolidacionDao;
import mx.ift.sns.dao.ng.ITipoReporteDao;
import mx.ift.sns.dao.ot.IEstadoDao;
import mx.ift.sns.dao.pst.IContactoDao;
import mx.ift.sns.dao.pst.IProveedorDao;
import mx.ift.sns.dao.pst.ITipoContactoDao;
import mx.ift.sns.dao.pst.ITipoServicioDao;
import mx.ift.sns.dao.solicitud.IEstadoSolicitudDao;
import mx.ift.sns.dao.solicitud.ITipoSolicitudDao;
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
import mx.ift.sns.negocio.ConfiguracionFacade;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.negocio.ac.IEstatusService;
import mx.ift.sns.negocio.centrales.ICentralesService;
import mx.ift.sns.negocio.ng.model.ResultadoValidacionArrendamiento;
import mx.ift.sns.negocio.ng.model.RetornoProcesaFicheroAsignacion;
import mx.ift.sns.negocio.ng.model.RetornoProcesaFicheroReportes;
import mx.ift.sns.negocio.oficios.IOficiosService;
import mx.ift.sns.negocio.ot.IOrganizacionTerritorialService;
import mx.ift.sns.negocio.psts.IProveedoresService;
import mx.ift.sns.negocio.usu.IUsuariosService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Facade de servicios de Numeración Geográfica.
 */
@Stateless(name = "NumeracionGeograficaService", mappedName = "NumeracionGeograficaService")
@Remote(INumeracionGeograficaService.class)
public class NumeracionGeograficaService extends ConfiguracionFacade implements INumeracionGeograficaService {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NumeracionGeograficaService.class);

    /** DAO de Proveedores. */
    @Inject
    private IProveedorDao proveedorDAO;

    /** DAO de Numeraciones Solicitadas. */
    @Inject
    private INumeracionSolicitadaDAO numsSolicitadaDao;

    /** DAO Solicitudes de Asignacion. */
    @Inject
    private ISolicitudAsignacionDao solicitudAsigDao;

    /** Validaciones automáticas de ficheros de asignación. */
    @EJB
    private IValidacionFicheroAsignacionAutomatica validacionFicheroAsignacionAutomatica;

    /** DAO de Estados Solicitud. */
    @Inject
    private IEstadoSolicitudDao estadoSolicitudDao;

    /** DAO de Tipos de Solicitud. */
    @Inject
    private ITipoSolicitudDao tipoSolicitudDao;

    /** DAO de contactos. */
    @Inject
    private IContactoDao contactoDao;

    /** Dao central. */
    @Inject
    private ICentralDao centralDao;

    /** Dao TipoContacto. */
    @Inject
    private ITipoContactoDao tipoContactoDao;

    /** Dao TipoServicio. */
    @Inject
    private ITipoServicioDao tipoServicioDao;

    /** Dao AbnCentral. */
    @Inject
    private IAbnCentralDao abnCentralDao;

    /** Dao Estado. */
    @Inject
    private IEstadoDao estadoDao;

    /** Dao Solicitud Consolidacion. */
    @Inject
    private ISolicitudConsolidacionDao solicitudConsolidacionDao;

    /** Servicio de Reportes. */
    @EJB
    private IReportesService reportesService;

    /** Servicio Generar Analis. */
    @EJB
    private IGenerarAnalisisNumeracion generarAnalisisService;

    /** Servicio series. */
    @EJB
    private ISeriesService seriesService;

    /** Servicio organizacion territorial. */
    @EJB
    private IOrganizacionTerritorialService otService;

    /** Servicio de Generación de Oficios. */
    @EJB
    private IOficiosService oficiosService;

    /** Servicio de Validacion de archivo. */
    @EJB
    private IValidadorArchivosPNNABD validadorABD;

    /** Servicio de Validacion de archivo. */
    @EJB
    private IValidacionFicheroReportes validadorReportes;

    /** Servicio de Proveedores. */
    @EJB
    private IProveedoresService proveedoresService;

    /** Servicio de Solicitudes. */
    @EJB
    private ISolicitudesService solicitudesService;

    /** Servicio de Centrales. */
    @EJB
    private ICentralesService centralesService;

    /** Servicio de usuarios. */
    @EJB(mappedName = "UsuariosService")
    private IUsuariosService usuariosService;

    /** Dao Abn. */
    @Inject
    private ITipoReporteDao tipoReporteDao;

    /** Service del Usuario. */
    @EJB
    private IUsuariosService usuarioService;

    /** Service de Estatus. */
    @EJB
    private IEstatusService estatusService;

    /** Service de Plan de Numeración. */
    @EJB
    private IPlanNumeracionABDService planAbdService;

    // ****************************************************************************//

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<Proveedor> findAllProveedoresActivos() throws Exception {
        return proveedoresService.findAllProveedoresActivos();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<Proveedor> findAllProveedores() throws Exception {
        return proveedoresService.findAllProveedores();
    }

    @Override
    public List<Proveedor> findAllCesionarios(Proveedor pCedente) {
        return proveedoresService.findAllCesionarios(pCedente);
    }

    @Override
    public List<Proveedor> findAllConcesionariosFromConvenios(Proveedor pComercializador, TipoRed pTipoRed) {
        return proveedoresService.findAllConcesionariosFromConvenios(pComercializador, pTipoRed);
    }

    @Override
    public List<Proveedor> findAllProveedoresByServicio(TipoProveedor pTipoProveedor, TipoRed pTipoRed,
            BigDecimal pIdSolicitante) {
        return proveedoresService.findAllProveedoresByServicio(pTipoProveedor, pTipoRed, pIdSolicitante);
    }

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
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Proveedor> findAllProveedoresByServicioArrendar(TipoProveedor pTipoProveedor,
            TipoRed pTipoRed, BigDecimal idPst) {

        return proveedorDAO.findAllProveedoresByServicioArrendar(pTipoProveedor, pTipoRed, idPst);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Proveedor getProveedorByNombre(String pNombre) throws Exception {
        return proveedorDAO.getProveedorByNombre(pNombre);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Proveedor getProveedorById(BigDecimal pIdProveedor) throws Exception {
        return proveedorDAO.getProveedorById(pIdProveedor);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateProveedor(Proveedor pProveedor) throws Exception {
        proveedoresService.updateProveedor(pProveedor);
    }

    // ****************************************************************************//

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Contacto> getRepresentantesLegales(String pTipoContacto, BigDecimal pIdProveedor) {
        return contactoDao.getRepresentantesLegales(pTipoContacto, pIdProveedor);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Contacto getRepresentanteLegal(BigDecimal pIdContacto) throws Exception {
        return contactoDao.getRepresentanteLegalById(pIdContacto);
    }

    // ****************************************************************************//

    @Override
    public ProveedorConvenio getConvenioById(BigDecimal pId) {
        return proveedoresService.getConvenioById(pId);
    }

    @Override
    public List<ProveedorConvenio> findAllConveniosByProveedor(BigDecimal pCodPstComercializador) throws Exception {
        return proveedoresService.findAllConveniosByProveedor(pCodPstComercializador);
    }

    @Override
    public List<ProveedorConvenio> findAllConveniosByProveedor(Proveedor pComercializador, TipoRed pTipoRedConvenio) {
        return proveedoresService.findAllConveniosByProveedor(pComercializador, pTipoRedConvenio);
    }

    @Override
    public List<Proveedor> findAllConcesionariosByComercializador(TipoRed tipoRed, Proveedor comercializador)
            throws Exception {
        return proveedoresService.findAllConcesionariosByComercializador(tipoRed, comercializador);
    }

    // ****************************************************************************//

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<TipoContacto> findAllTiposContacto() throws Exception {
        return tipoContactoDao.findAllTiposContacto();
    }

    // ****************************************************************************//

    @Override
    public List<TipoModalidad> findAllTiposModalidad() {
        return proveedoresService.findAllTiposModalidad();
    }

    @Override
    public TipoModalidad getTipoModalidadById(String idtipoModalidad) {
        return proveedoresService.getTipoModalidadById(idtipoModalidad);
    }

    // ****************************************************************************//

    @Override
    public List<TipoProveedor> findAllTiposProveedor() throws Exception {
        return proveedoresService.findAllTiposProveedor();
    }

    @Override
    public TipoProveedor getTipoProveedorByCdg(String pCdgTipo) {
        return proveedoresService.getTipoProveedorByCdg(pCdgTipo);
    }

    // ****************************************************************************//

    @Override
    public List<TipoRed> findAllTiposRed() {
        return proveedoresService.findAllTiposRed();
    }

    @Override
    public TipoRed getTipoRedById(String id) {
        return proveedoresService.getTipoRedById(id);
    }

    @Override
    public List<TipoRed> findAllTiposRedValidos() {
        return proveedoresService.findAllTiposRedValidos();
    }

    @Override
    public List<TipoRed> findAllTiposRedValidos(TipoRed tipoRed) {
        return proveedoresService.findAllTiposRedValidos(tipoRed);
    }

    // ****************************************************************************//

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<TipoServicio> findAllTiposServicio() throws Exception {
        return tipoServicioDao.findAllTiposServicio();
    }

    // ****************************************************************************//

    @Override
    public List<TipoSolicitud> findAllTiposSolicitud() throws Exception {
        return solicitudesService.findAllTiposSolicitud();
    }

    // ****************************************************************************//

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<EstadoSolicitud> findAllEstadosSolicitud() throws Exception {
        return estadoSolicitudDao.findAllEstadosSolicitud();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public EstadoSolicitud getEstadoSolicitudById(String pIdSolicitud) throws Exception {
        return estadoSolicitudDao.getEstadoSolicitudById(pIdSolicitud);
    }

    @Override
    public Abn getAbnById(BigDecimal pCodigo) throws Exception {
        return otService.getAbnById(pCodigo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<AbnCentral> findAllCentralesAbn() throws Exception {
        return abnCentralDao.findAllCentralesAbn();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AbnCentral getCentralAbnById(BigDecimal pCentralId) throws Exception {
        return abnCentralDao.getCentralAbnById(pCentralId);
    }

    @Override
    public List<EstadoAbn> findAllEstadosAbn() throws Exception {
        return otService.findAllEstadosAbn();
    }

    @Override
    public EstadoAbn getEstadoAbnByCodigo(String pCodigo) {
        return otService.getEstadoAbnByCodigo(pCodigo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<NumeracionSolicitada> getNumeracionesSolicitadas(BigDecimal codSol) throws Exception {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("lista de numeraciones solicitas");
        }

        return numsSolicitadaDao.getNumSolicitada(codSol);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RetornoProcesaFicheroAsignacion validarFicheroAsignacionAutomatica(File fichero) throws Exception {
        RetornoProcesaFicheroAsignacion procesaFich = validacionFicheroAsignacionAutomatica.validar(fichero);
        return procesaFich;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<SolicitudAsignacion> findAllSolicitudesAsignacion(FiltroBusquedaSolicitudes pFiltrosSolicitud)
            throws Exception {
        return solicitudAsigDao.findAllSolicitudesAsignacion(pFiltrosSolicitud);
    }

    // ****************************************************************************//
    @Override
    public List<Serie> findSeriesPst(Proveedor pst, Abn abn) {
        return seriesService.findSeriesPst(pst, abn);
    }

    @Override
    public List<Serie> findSeriesOtrosPsts(Proveedor pst, Abn abn) throws Exception {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("lista de series asisgnadas a otro PST");
        }

        return seriesService.findSeriesOtrosPsts(pst, abn);
    }

    @Override
    public List<Serie> findSeriesLibres(Abn abn) throws Exception {

        List<Serie> list = null;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("lista de series libres");
        }

        list = seriesService.findSeriesLibres(abn);

        return list;
    }

    @Override
    public Serie getSerie(BigDecimal pIdSna, BigDecimal pIdNir) throws Exception {
        return seriesService.getSerie(pIdSna, pIdNir);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<Serie> findAllSeries(FiltroBusquedaSeries pFiltros) throws Exception {
        return seriesService.findAllSeries(pFiltros);
    }

    @Override
    public List<RangoSerie> findAllRangos(FiltroBusquedaRangos pFiltros) throws Exception {
        return seriesService.findAllRangos(pFiltros);
    }

    @Override
    public int findAllRangosCount(FiltroBusquedaRangos pFiltros) throws Exception {
        return seriesService.findAllRangosCount(pFiltros);
    }

    @Override
    public Poblacion getPoblacionWithMaxNumAsignadaByAbn(Abn abn) throws Exception {
        return seriesService.getPoblacionWithMaxNumAsignadaByAbn(abn);
    }

    @Override
    public SolicitudLiberacionNg applyLiberacionesSolicitadas(SolicitudLiberacionNg pSolicitud) throws Exception {
        return solicitudesService.applyLiberacionesSolicitadas(pSolicitud);
    }

    @Override
    public SolicitudRedistribucionNg applyRedistribucionesSolicitadas(SolicitudRedistribucionNg pSolicitud)
            throws Exception {
        return solicitudesService.applyRedistribucionesSolicitadas(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public int findAllSeriesRangosCount(FiltroBusquedaSeries pFiltros) throws Exception {
        return seriesService.findAllSeriesRangosCount(pFiltros);
    }

    @Override
    public boolean existeRangoSerie(BigDecimal pIdNir, BigDecimal pSna,
            String pNumInicial, Proveedor pAsignatario) {
        return seriesService.existeRangoSerie(pIdNir, pSna, pNumInicial, pAsignatario);
    }

    @Override
    public RangoSerie getRangoSerie(BigDecimal pIdNir, BigDecimal pSna,
            String pNumInicial, Proveedor pAsignatario) throws Exception {
        return seriesService.getRangoSerie(pIdNir, pSna, pNumInicial, pAsignatario);
    }

    @Override
    public RangoSerie getRangoSerieByFraccion(BigDecimal pIdNir, BigDecimal pSna,
            String pNumInicial, String pNumFinal, Proveedor pAsignatario) throws Exception {
        return seriesService.getRangoSerieByFraccion(pIdNir, pSna, pNumInicial, pNumFinal, pAsignatario);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public BigDecimal getTotalNumOcupadaSerie(SeriePK id) throws Exception {
        return seriesService.getTotalNumOcupadaSerie(id);
    }

    @Override
    public List<RangoSerie> findNumeracionesAsiganadasSerie(Abn abn, Nir nir, BigDecimal sna) {
        return seriesService.findNumeracionesAsiganadasSerie(abn, nir, sna);
    }

    @Override
    public SolicitudAsignacion saveSolicitudAsignacion(SolicitudAsignacion solicitudAsignacion) throws Exception {
        return solicitudesService.saveSolicitudAsignacion(solicitudAsignacion);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudCesionNg saveSolicitudCesion(SolicitudCesionNg pSolicitud) throws Exception {
        return solicitudesService.saveSolicitudCesion(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelSolicitudCesion(SolicitudCesionNg pSolicitud) throws Exception {
        return solicitudesService.cancelSolicitudCesion(pSolicitud);
    }

    @Override
    public PeticionCancelacion cancelCesion(CesionSolicitadaNg pCesSol, boolean pUseCheck) throws Exception {
        return solicitudesService.cancelCesion(pCesSol, pUseCheck);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public SolicitudCesionNg applyCesionesSolicitadas(SolicitudCesionNg pSolicitud) throws Exception {
        return solicitudesService.applyCesionesSolicitadas(pSolicitud);
    }

    @Override
    public List<SolicitudCesionNg> findAllSolicitudesCesion() {
        return solicitudesService.findAllSolicitudesCesion();
    }

    @Override
    public List<SolicitudCesionNg> findAllSolicitudesCesion(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudesService.findAllSolicitudesCesion(pFiltrosSolicitud);
    }

    @Override
    public int findAllSolicitudesCesionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudesService.findAllSolicitudesCesionCount(pFiltrosSolicitud);
    }

    @Override
    public List<SolicitudLiberacionNg> findAllSolicitudesLiberacion() {
        return solicitudesService.findAllSolicitudesLiberacion();
    }

    @Override
    public List<SolicitudLiberacionNg> findAllSolicitudesLiberacion(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudesService.findAllSolicitudesLiberacion(pFiltrosSolicitud);
    }

    @Override
    public int findAllSolicitudesLiberacionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudesService.findAllSolicitudesLiberacionCount(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudLiberacionNg saveSolicitudLiberacion(SolicitudLiberacionNg pSolicitud) throws Exception {
        return solicitudesService.saveSolicitudLiberacion(pSolicitud);
    }

    @Override
    public PeticionCancelacion cancelSolicitudLiberacion(SolicitudLiberacionNg pSolicitud) throws Exception {
        return solicitudesService.cancelSolicitudLiberacion(pSolicitud);
    }

    @Override
    public PeticionCancelacion cancelLiberacion(LiberacionSolicitadaNg pLibSol, boolean pUseCheck) throws Exception {
        return solicitudesService.cancelLiberacion(pLibSol, pUseCheck);
    }

    @Override
    public SolicitudLiberacionNg getSolicitudLiberacionById(BigDecimal pConsecutivo) {
        return solicitudesService.getSolicitudLiberacionById(pConsecutivo);
    }

    @Override
    public SolicitudLiberacionNg getSolicitudLiberacionEagerLoad(SolicitudLiberacionNg pSolicitud) throws Exception {
        return solicitudesService.getSolicitudLiberacionEagerLoad(pSolicitud);
    }

    @Override
    public SolicitudCesionNg getSolicitudCesionById(BigDecimal pConsecutivo) {
        return solicitudesService.getSolicitudCesionById(pConsecutivo);
    }

    @Override
    public SolicitudCesionNg getSolicitudCesionEagerLoad(SolicitudCesionNg pSolicitud) throws Exception {
        return solicitudesService.getSolicitudCesionEagerLoad(pSolicitud);
    }

    @Override
    public List<RangoSerie> findNumeracionesSeleccionadasSinAsignar(BigDecimal codSol)
            throws Exception {
        List<RangoSerie> list = null;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("lista de Numeraciones Seleccionadas");
        }

        list = seriesService.findNumeracionesSeleccionadasSinAsignar(codSol);

        return list;
    }

    // ****************************************************************************//

    @Override
    public Central findCentralById(BigDecimal idCentral) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("central por id");
        }
        return centralesService.findCentralById(idCentral);
    }

    // ****************************************************************************//

    @Override
    public List<RangoSerie> findNumeracionesSeleccionadas(BigDecimal codSol) throws Exception {
        return seriesService.findNumeracionesSeleccionadas(codSol);
    }

    @Override
    public BigDecimal getTotalNumAsignadaSolicitud(BigDecimal codSol) throws Exception {
        return seriesService.getTotalNumAsignadaSolicitud(codSol);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public BigDecimal getTotalNumOcupadaAbn(Abn abn) throws Exception {
        return seriesService.getTotalNumOcupadaAbn(abn);
    }

    @Override
    public EstadoRango getEstadoRangoByCodigo(String codigo) throws Exception {
        return seriesService.getEstadoRangoByCodigo(codigo);
    }

    @Override
    public List<EstadoRango> findAllEstadosRango() {
        return seriesService.findAllEstadosRango();
    }

    @Override
    public RangoSerie saveRangoSerie(RangoSerie rangoSerie) throws Exception {
        return seriesService.saveRangoSerie(rangoSerie);
    }

    @Override
    public Estado findEstadoById(String id) throws Exception {
        return otService.findEstadoById(id);
    }

    @Override
    public List<Municipio> findMunicipiosByEstado(String estado) throws Exception {
        return otService.findMunicipiosByEstado(estado);
    }

    @Override
    public Municipio findMunicipioById(MunicipioPK id) throws Exception {
        return otService.findMunicipioById(id);
    }

    @Override
    public List<Poblacion> findAllPoblaciones(String estado, String municipio) {
        return otService.findAllPoblaciones(estado, municipio);
    }

    @Override
    public Poblacion findPoblacionById(String inegi) throws Exception {
        return otService.findPoblacionById(inegi);
    }

    @Override
    public List<Estado> findEstados() throws Exception {
        return otService.findEstados();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<SolicitudAsignacion> findAllSolicitudesAsignacion() throws Exception {
        return solicitudAsigDao.findAllSolicitudesAsignacion();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Central> findAllCentralesOrigenByName(String name, Proveedor concesionario, Proveedor arrendatario)
            throws Exception {
        return centralDao.findAllCentralesOrigenByName(name, concesionario, arrendatario);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Contacto getRepresentanteLegalById(BigDecimal pIdContacto) throws Exception {
        return contactoDao.getRepresentanteLegalById(pIdContacto);
    }

    @Override
    public Poblacion getPoblacionAnclaByCodigoAbn(BigDecimal codigo) throws Exception {
        return otService.getPoblacionAnclaByCodigoAbn(codigo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Central> findAllCentralesByName(String name) throws Exception {
        return centralDao.findAllCentralesByName(name);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Central> findAllCentralesByProveedor(Proveedor pProveedor, String name) {
        return centralDao.findAllCentralesByProveedor(pProveedor, name);
    }

    @Override
    public Central saveCentral(Central central) throws Exception {
        return centralesService.saveCentral(central);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<SolicitudConsolidacion> findAllSolicitudesConsolidacion() throws Exception {
        return solicitudConsolidacionDao.findAllSolicitudesConsolidacion();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<SolicitudConsolidacion> findAllSolicitudesConsolidacion(FiltroBusquedaSolicitudes pFiltrosSolicitud)
            throws Exception {
        return solicitudConsolidacionDao.findAllSolicitudesConsolidacion(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int findAllSolicitudesConsolidacionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) throws Exception {
        return solicitudConsolidacionDao.findAllSolicitudesConsolidacionCount(pFiltrosSolicitud);
    }

    @Override
    public List<Estado> findAllEstados() throws Exception {
        return estadoDao.findEstados();
    }

    @Override
    public ResultadoValidacionArrendamiento validar(String ficheroArrendador, String ficheroArrendatario) {
        return validadorABD.validar(ficheroArrendador, ficheroArrendatario);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveAbnCentral(AbnCentral abnCentral) throws Exception {
        abnCentralDao.saveAbnCentral(abnCentral);
    }

    @Override
    public SolicitudAsignacion getSolicitudAsignacionEagerLoad(SolicitudAsignacion pSolicitud) throws Exception {

        return solicitudesService.getSolicitudAsignacionEagerLoad(pSolicitud);
    }

    @Override
    public SolicitudAsignacion getSolicitudAsignacionById(BigDecimal pConsecutivo) {
        return solicitudesService.getSolicitudAsignacionById(pConsecutivo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public InputStream generarAnalisisNumeracion(SolicitudAsignacion solicitud) throws Exception {
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(generarAnalisisService.generarAnalisisNumeracion(solicitud));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return bais;
    }

    @Override
    public Nir getNirByCodigo(int cdgNir) {
        return otService.getNirByCodigo(cdgNir);
    }

    @Override
    public Nir getNir(BigDecimal id) {
        return otService.getNir(id);
    }

    @Override
    public List<Nir> findAllNirByAbn(BigDecimal numAbn) throws Exception {
        return otService.findAllNirByAbn(numAbn);
    }

    @Override
    public Nir saveNir(Nir nir) {
        return otService.saveNir(nir);
    }

    @Override
    public SolicitudConsolidacion saveSolicitudConsolidacion(SolicitudConsolidacion pSolicitud) throws Exception {
        return solicitudesService.saveSolicitudConsolidacion(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudConsolidacion getSolicitudConsolidacionEagerLoad(SolicitudConsolidacion pSolicitud)
            throws Exception {
        return solicitudConsolidacionDao.getSolicitudConsolidacionEagerLoad(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<Poblacion> findAllPoblacionesByAbn(Abn pAbn) {
        return otService.findAllPoblacionesByAbn(pAbn);
    }

    @Override
    public List<Poblacion> findAllPoblacionByAbnUbicacion(String estado, String municipio, BigDecimal abn)
            throws Exception {
        return otService.findAllPoblacionByAbnUbicacion(estado, municipio, abn);
    }

    @Override
    public Abn saveAbn(Abn abn) throws Exception {
        return otService.saveAbn(abn);
    }

    @Override
    public List<SerieArrendada> findSeriesArrendadas(int first, int pageSize) {
        return seriesService.findSeriesArrendadas(first, pageSize);
    }

    @Override
    public int getSeriesArrendadasCount() {
        return seriesService.getSeriesArrendadasCount();
    }

    @Override
    public List<Proveedor> findAllConcesionarios() {

        return proveedorDAO.findAllConcesionarios();
    }

    @Override
    public List<TipoReporte> findAllTiposReporte() throws Exception {

        return tipoReporteDao.findAllTiposReporte();
    }

    @Override
    public TipoReporte getTipoReporteByCdg(String value) {

        return tipoReporteDao.getTipoReporteByCdg(value);
    }

    /**
     * @return the seriesService
     */
    @Override
    public ISeriesService getSeriesService() {
        return seriesService;
    }

    /**
     * @return the otService
     */
    @Override
    public IOrganizacionTerritorialService getOtService() {
        return otService;
    }

    /**
     * @return the goService
     */
    @Override
    public IOficiosService getOficiosService() {
        return oficiosService;
    }

    /**
     * @return the validadorABD
     */
    @Override
    public IValidadorArchivosPNNABD getValidadorABD() {
        return validadorABD;
    }

    /**
     * @return the proveedoresService
     */
    public IProveedoresService getProveedoresService() {
        return proveedoresService;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public RetornoProcesaFicheroReportes validarFicheroLineasActivas(File fichero) throws Exception {

        return validadorReportes.validarLineasActivas(fichero);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int findAllSolicitudesAsignacionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) throws Exception {
        return solicitudAsigDao.findAllSolicitudesAsignacionCount(pFiltrosSolicitud);
    }

    @Override
    public BigDecimal getTotalRangosAsignadosByPst(String tipoRed, String tipoModalidad, Proveedor proveedor) {
        return seriesService.getTotalRangosAsignadosByPst(tipoRed, tipoModalidad, proveedor);
    }

    @Override
    public BigDecimal getTotalNumRangosAsignadosByPoblacion(String tipoRed, String tipoModalidad, Proveedor proveedor,
            Poblacion poblacion) {
        return seriesService.getTotalNumRangosAsignadosByPoblacion(tipoRed, tipoModalidad, proveedor, poblacion);
    }

    @Override
    public BigDecimal getTotalNumRangosAsignadosByAbn(
            String tipoRed, String tipoModalidad, Abn abn, Proveedor proveedor) {
        return seriesService.getTotalNumRangosAsignadosByAbn(tipoRed, tipoModalidad, abn, proveedor);
    }

    @Override
    public ReporteLineasActivas saveLineaActiva(ReporteLineasActivas lineaActiva) {
        return reportesService.saveLineaActiva(lineaActiva);
    }

    /**
     * @return the reportesService
     */
    public IReportesService getReportesService() {
        return reportesService;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<AbnCentral> findAllAbnCentralesByAbn(BigDecimal codigo) {
        return abnCentralDao.findAllAbnCentralesByAbn(codigo);
    }

    @Override
    public Poblacion getPoblacionByAbnInegi(BigDecimal codigoAbn, String inegi) {
        return otService.getPoblacionByAbnInegi(codigoAbn, inegi);
    }

    @Override
    public Poblacion savePoblacion(Poblacion poblacion) {
        return otService.savePoblacion(poblacion);
    }

    @Override
    public int findAllPoblacionesCount(BigDecimal codigo) {
        return otService.findAllPoblacionesCount(codigo);
    }

    @Override
    public List<Poblacion> findAllPoblaciones(FiltroBusquedaPoblaciones pFiltros) {
        return otService.findAllPoblaciones(pFiltros);
    }

    @Override
    public int findAllPoblacionesCount(FiltroBusquedaPoblaciones filtros) {
        return otService.findAllPoblacionesCount(filtros);
    }

    @Override
    public int findAllMunicipiosCount(BigDecimal codigo) {
        return otService.findAllMunicipiosCount(codigo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public RetornoProcesaFicheroReportes validarFicheroLineasActivasDet(File fichero) throws Exception {
        return validadorReportes.validarFicheroLineasActivasDet(fichero);
    }

    @Override
    public LineaActivaDet saveLineaActivaDet(LineaActivaDet lineaActivaDet) {
        return reportesService.saveLineaActivaDet(lineaActivaDet);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<Solicitud> findAllSolicitudes(FiltroBusquedaSolicitudes pFiltrosSolicitud)
            throws Exception {
        return solicitudesService.findAllSolicitudes(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public int findAllSolicitudesCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) throws Exception {
        return solicitudesService.findAllSolicitudesCount(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TipoSolicitud getTipoSolicitudById(Integer id) throws Exception {
        return tipoSolicitudDao.getTipoSolicitudById(id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public RetornoProcesaFicheroReportes validarFicheroLineasArrendatario(File fichero) throws Exception {

        return validadorReportes.validarFicheroLineasArrendatario(fichero);
    }

    @Override
    public LineaArrendatario saveLineaArrendatario(LineaArrendatario lineaArrendatario) {

        return reportesService.saveLineaArrendatario(lineaArrendatario);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public RetornoProcesaFicheroReportes validarFicheroLineasArrendada(File fichero) throws Exception {

        return validadorReportes.validarFicheroLineasArrendada(fichero);

    }

    @Override
    public LineaArrendada saveLineaArrendada(LineaArrendada lineaArrendada) {
        return reportesService.saveLineaArrendada(lineaArrendada);
    }

    @Override
    public List<HistoricoSerie> findHistoricoSeries(FiltroBusquedaHistoricoSeries filtro, int first, int pagesize) {
        return seriesService.findHistoricoSeries(filtro, first, pagesize);
    }

    @Override
    public int findHistoricoSeriesCount(FiltroBusquedaHistoricoSeries filtro) {
        return seriesService.findHistoricoSeriesCount(filtro);
    }

    @Override
    public SolicitudConsolidacion applyConsolidacionesSolicitadas(SolicitudConsolidacion pSolicitud) throws Exception {
        return solicitudesService.applyConsolidacionesSolicitadas(pSolicitud);
    }

    @Override
    public List<DetalleLineaActiva> findAllSolicitudesLineasActivas(FiltroBusquedaLineasActivas pFiltrosSolicitud) {
        return reportesService.findAllSolicitudesLineasActivas(pFiltrosSolicitud);
    }

    @Override
    public int findAllSolicitudesLineasActivasCount(FiltroBusquedaLineasActivas pFiltrosSolicitud) {
        return reportesService.findAllSolicitudesLineasActivasCount(pFiltrosSolicitud);
    }

    @Override
    public List<Central> findAllCentralesByProveedores(Proveedor pst1, Proveedor pst2) {
        return centralDao.findAllCentralesByProveedores(pst1, pst2);
    }

    @Override
    public SolicitudConsolidacion applyAbnConsolidar(List<PoblacionAbn> listaPoblacion, List<Nir> listaNir,
            Date fechaConsolidacion, SolicitudConsolidacion solicitud) throws Exception {
        return solicitudesService.applyAbnConsolidar(listaPoblacion, listaNir, fechaConsolidacion, solicitud);
    }

    @Override
    public SolicitudConsolidacion getSolicitudConsolidacionById(BigDecimal pConsecutivo) {
        return solicitudesService.getSolicitudConsolidacionById(pConsecutivo);
    }

    @Override
    public List<ComboCentral> getComboCentrales() {
        return centralesService.getComboCentrales();
    }

    @Override
    public SolicitudLineasActivas saveSolicitudLineasActivas(SolicitudLineasActivas solicitudLineasActivas) {
        return solicitudesService.saveSolicitudLineasActivas(solicitudLineasActivas);
    }

    @Override
    public List<SolicitudLineasActivas> findAllSolicitudesLineasActivasConsulta(
            FiltroBusquedaLineasActivas pFiltrosSolicitud) {
        return reportesService.findAllSolicitudesLineasActivasConsulta(pFiltrosSolicitud);
    }

    @Override
    public CentralesRelacion saveCentralesRelacion(CentralesRelacion centralesRelacion) {
        return centralesService.saveCentralesRelacion(centralesRelacion);
    }

    @Override
    public int findAllSolicitudesLineasActivasConsultaCount(FiltroBusquedaLineasActivas pFiltrosSolicitud) {
        return reportesService.findAllSolicitudesLineasActivasConsultaCount(pFiltrosSolicitud);
    }

    @Override
    public ReporteLineasActivas getLastReporteLineasActiva(Proveedor proveedor) {

        return reportesService.getLastReporteLineasActiva(proveedor);
    }

    @Override
    public List<RangoSerie> findAllRangosByNumSol(NumeracionSolicitada numeracionSolicitada) {

        return seriesService.findAllRangosByNumSol(numeracionSolicitada);
    }

    @Override
    public List<String> getNotificacionesLoginLiberacion() {
        return solicitudesService.getNotificacionesLiberacionesPendientes();
    }

    @Override
    public List<String> getNotificacionesLoginCesion() {
        return solicitudesService.getNotificacionesCesionesPendientes();
    }

    @Override
    public List<SolicitudRedistribucionNg> findAllSolicitudesRedistribucion() {
        return solicitudesService.findAllSolicitudesRedistribucion();
    }

    @Override
    public List<SolicitudRedistribucionNg> findAllSolicitudesRedistribucion(
            FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudesService.findAllSolicitudesRedistribucion(pFiltrosSolicitud);
    }

    @Override
    public int findAllSolicitudesRedistribucionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudesService.findAllSolicitudesRedistribucionCount(pFiltrosSolicitud);
    }

    @Override
    public SolicitudRedistribucionNg saveSolicitudRedistribucion(SolicitudRedistribucionNg pSolicitud) {
        return solicitudesService.saveSolicitudRedistribucion(pSolicitud);
    }

    @Override
    public PeticionCancelacion cancelRedistribucion(RedistribucionSolicitadaNg pRedSol, boolean pUseCheck)
            throws Exception {
        return solicitudesService.cancelRedistribucion(pRedSol, pUseCheck);
    }

    @Override
    public PeticionCancelacion cancelSolicitudRedistribucion(SolicitudRedistribucionNg pSolicitud) throws Exception {
        return solicitudesService.cancelSolicitudRedistribucion(pSolicitud);
    }

    @Override
    public SolicitudRedistribucionNg getSolicitudRedistribucionById(BigDecimal pConsecutivo) {
        return solicitudesService.getSolicitudRedistribucionById(pConsecutivo);
    }

    @Override
    public SolicitudRedistribucionNg getSolicitudRedistribucionEagerLoad(SolicitudRedistribucionNg pSolicitud) {
        return solicitudesService.getSolicitudRedistribucionEagerLoad(pSolicitud);
    }

    @Override
    public void generarTablas() {
        validadorABD.generarTablas();
    }

    /**
     * Comprueba si el abn está asociado a la poblacion.
     * @param poblacion Poblacion
     * @param abn Abn
     * @return boolean
     */
    @Override
    public boolean existePoblacionEnAbn(Poblacion poblacion, Abn abn) {
        return otService.existePoblacionEnAbn(poblacion, abn);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TipoServicio getTipoServicioById(String id) throws Exception {
        return tipoServicioDao.getTipoServicioById(id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Estatus getEstatusById(String pId) throws Exception {
        return estatusService.getEstatusById(pId);
    }

    @Override
    public List<Proveedor> findAllProveedores(FiltroBusquedaProveedores pFiltros) throws Exception {
        return proveedoresService.findAllProveedores(pFiltros);
    }

    @Override
    public int findAllProveedoresCount(FiltroBusquedaProveedores pFiltros) throws Exception {
        return proveedoresService.findAllProveedoresCount(pFiltros);
    }

    @Override
    public TipoContacto getTipoContactoByCdg(String pCdgTipo) {
        return tipoContactoDao.getTipoContactoByCdg(pCdgTipo);
    }

    @Override
    public List<TipoDestinatario> findAllTiposDestinatario() throws Exception {
        return oficiosService.findAllTiposDestinatario();
    }

    @Override
    public TipoDestinatario getTipoDestinatarioByCdg(String pCdgDestinatario) throws Exception {
        return oficiosService.getTipoDestinatarioByCdg(pCdgDestinatario);
    }

    @Override
    public Oficio getOficio(Solicitud pSolicitud, String pCdgTipoDestinatario) {
        return oficiosService.getOficio(pSolicitud, pCdgTipoDestinatario);
    }

    @Override
    public Proveedor guardarProveedor(Proveedor proveedor, boolean encriptarPass) {
        return proveedoresService.guardarProveedor(proveedor, encriptarPass);
    }

    @Override
    public byte[] exportarDatosGenerales(FiltroBusquedaProveedores filtro) throws Exception {
        return proveedoresService.exportarDatosGenerales(filtro);
    }

    @Override
    public byte[] exportarDatosContactos(FiltroBusquedaProveedores filtro) throws Exception {
        return proveedoresService.exportarDatosContactos(filtro);
    }

    @Override
    public byte[] exportarDatosConvenios(FiltroBusquedaProveedores filtro) throws Exception {
        return proveedoresService.exportarDatosConvenios(filtro);
    }

    @Override
    public int existeIdo(Proveedor proveedor) {
        return proveedoresService.existeIdo(proveedor);
    }

    @Override
    public int existeIda(Proveedor proveedor) {
        return proveedoresService.existeIda(proveedor);
    }

    @Override
    public int existeAbc(Proveedor proveedor) {
        return proveedoresService.existeAbc(proveedor);
    }

    @Override
    public int existeBcd(Proveedor proveedor) {
        return proveedoresService.existeBcd(proveedor);
    }

    @Override
    public List<Estatus> findAllEstatus() {
        return estatusService.findAllEstatus();
    }

    @Override
    public boolean existeUsuario(String idUsuario) {
        return usuarioService.existeUsuario(idUsuario);
    }

    @Override
    public Proveedor validaProveedor(Proveedor proveedor, String tipoPstInicial, String tipoRedInicial,
            boolean validaUsuario) {
        return proveedoresService.validaProveedor(proveedor, tipoPstInicial, tipoRedInicial, validaUsuario);
    }

    @Override
    public boolean faltaRepresentanteLegal(Proveedor proveedor) {
        return proveedoresService.faltaRepresentanteLegal(proveedor);
    }

    @Override
    public List<Poblacion> findAllPoblacionesByEstadoAbn(String estado, BigDecimal codAbn) {
        return otService.findAllPoblacionesByEstadoAbn(estado, codAbn);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Nir getNirById(BigDecimal pdIdNir) throws Exception {
        return seriesService.getNirById(pdIdNir);
    }

    @Override
    public List<Proveedor> findAllProveedoresByTRed(TipoRed tipoRed) throws Exception {
        return proveedoresService.findAllProveedoresByTRed(tipoRed);
    }

    @Override
    public boolean existeNumeracionAsignadaAlPst(Proveedor proveedor) {
        return seriesService.existeNumeracionAsignadaAlPst(proveedor);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int findAllNirByAbnCount(BigDecimal abn) {
        return otService.findAllNirByAbnCount(abn);
    }

    @Override
    public int getNirsByPoblacion(Poblacion poblacion) {
        return seriesService.getNirsByPoblacion(poblacion);
    }

    @Override
    public List<Nir> findAllNirsByPoblacion(Poblacion poblacion) {
        return seriesService.findAllNirsByPoblacion(poblacion);
    }

    @Override
    public boolean existeNumeracionAsignadaAlPstByConvenio(ProveedorConvenio proveedorConvenio) {
        return seriesService.existeNumeracionAsignadaAlPstByConvenio(proveedorConvenio);
    }

    @Override
    public List<Nir> findAllNirs() throws Exception {
        return otService.findAllNirs();
    }

    @Override
    public Poblacion getPoblacionByInegi(String inegi) {

        return otService.getPoblacionByInegi(inegi);
    }

    @Override
    public void saveDetalleLineaActiva(List<String> listaDatos, BigDecimal idReporte) {
        reportesService.saveDetalleLineaActiva(listaDatos, idReporte);

    }

    @Override
    public List<String> findAllAvisoAsignacionDetalleLineaActiva(Reporte reporte) {

        return reportesService.findAllAvisoAsignacionDetalleLineaActiva(reporte);
    }

    @Override
    public List<DetLineaActivaDet> findAllDetLineaActivaDetByReporte(BigDecimal idReporte, int first, int pageSize) {

        return reportesService.findAllDetLineaActivaDetByReporte(idReporte, first, pageSize);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void saveDetLineaActivaDet(List<String> listaDatos, BigDecimal idReporte) {
        reportesService.saveDetLineaActivaDet(listaDatos, idReporte);
    }

    @Override
    public List<String> findAllAvisoAsignacionDetalleLineaActivaDet(Reporte reporte) {

        return reportesService.findAllAvisoAsignacionDetalleLineaActivaDet(reporte);
    }

    @Override
    public void saveDetLineaArrendatario(List<String> listaDatos, BigDecimal idReporte) {
        reportesService.saveDetLineaArrendatario(listaDatos, idReporte);
    }

    @Override
    public List<DetLineaArrendatario> findAllDetLineaArrendatarioByReporte(
            BigDecimal idReporte, int first, int pageSize) {
        return reportesService.findAllDetLineaArrendatarioByReporte(idReporte, first, pageSize);
    }

    @Override
    public void saveDetLineaArrendada(List<String> listaDatos, BigDecimal idReporte) {
        reportesService.saveDetLineaArrendada(listaDatos, idReporte);
    }

    @Override
    public List<DetLineaArrendada> findAllDetLineaArrendadaByReporte(BigDecimal idReporte, int first, int pageSize) {
        return reportesService.findAllDetLineaArrendadaByReporte(idReporte, first, pageSize);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<DetalleReporte> findAllDetalleReporte(FiltroBusquedaLineasActivas filtros) {
        return reportesService.findAllDetalleReporte(filtros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Integer findAllDetalleReporteCount(FiltroBusquedaLineasActivas filtros) {
        return reportesService.findAllDetalleReporteCount(filtros);
    }

    @Override
    public List<String> validarContacto(Contacto contacto) {
        return proveedoresService.validarContacto(contacto);
    }

    @Override
    public boolean contactoNoUsado(Contacto contacto) {
        return proveedoresService.contactoNoUsado(contacto);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public InputStream getExportConsultaLineaActiva(FiltroBusquedaLineasActivas filtro) throws Exception {
        ByteArrayInputStream bais = null;
        bais = new ByteArrayInputStream(reportesService.getExportConsultaLineaActiva(filtro));
        return bais;
    }

    @Override
    public List<Object[]> findAllSeriesOcupadas(FiltroBusquedaSeries filtros) {

        return seriesService.findAllSeriesOcupadas(filtros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<PoblacionConsolidar> findPoblacionConsolidarById(BigDecimal pId) {
        return otService.findPoblacionConsolidarById(pId);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<NirConsolidar> findNirConsolidarById(BigDecimal pId) {
        return otService.findNirConsolidarById(pId);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public AbnConsolidar getAbnConsolidarByIdSol(BigDecimal pIdSol) {
        return otService.getAbnConsolidarByIdSol(pIdSol);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public String getFechaConsolidacionByRangoAbn(Abn rangoAbn) {
        return otService.getFechaConsolidacionByRangoAbn(rangoAbn);
    }

    @Override
    public void removeRangoSerie(RangoSerie rango) throws Exception {
        seriesService.removeRangoSerie(rango);

    }

    @Override
    public InputStream getExportHistoricoSeries(FiltroBusquedaHistoricoSeries filtro) throws Exception {
        ByteArrayInputStream bais = null;
        bais = new ByteArrayInputStream(seriesService.getExportHistoricoSeries(filtro));
        return bais;
    }

    @Override
    public int findAllMunicipiosByAbnAndEstadoCount(FiltroBusquedaABNs filtros) {
        return otService.findAllMunicipiosByAbnAndEstadoCount(filtros);
    }

    @Override
    public List<Municipio> findAllMunicipiosByAbnAndEstado(FiltroBusquedaABNs filtros) {
        return otService.findAllMunicipiosByAbnAndEstado(filtros);
    }

    @Override
    public int findAllPoblacionesAbnCount(FiltroBusquedaABNs pFiltros) {
        return otService.findAllPoblacionesAbnCount(pFiltros);
    }

    @Override
    public List<PoblacionAbn> findAllPoblacionesAbn(FiltroBusquedaABNs pFiltros) {
        return otService.findAllPoblacionesAbn(pFiltros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<PoblacionAbn> findAllPoblacionAbnByAbn(Abn pAbn) {
        return otService.findAllPoblacionAbnByAbn(pAbn);
    }

    @Override
    public boolean isRangoLibre(BigDecimal nir, BigDecimal sna, String inicioRango, String finRango) {

        return seriesService.isRangoLibre(nir, sna, inicioRango, finRango);
    }

    @Override
    public boolean existNumeracionAsignadaBySolicita(NumeracionSolicitada numeracionSolicitada) {

        return solicitudesService.existNumeracionAsignadaBySolicita(numeracionSolicitada);
    }

    @Override
    public ResultadoValidacionArrendamiento procesarFicherosAbd(String arrendadorUrl, String arrendatarioUrl,
            String arrendadorNombre, String arrendatarioNombre) {
        return planAbdService.procesarFicherosAbd(arrendadorUrl, arrendatarioUrl, arrendadorNombre, arrendatarioNombre);
    }

    @Override
    public void generarRegistrosAbd() {
        planAbdService.generarRegistrosAbd();
    }

    @Override
    public Central comprobarCentral(Central central) {

        return centralesService.comprobarCentral(central);
    }

	@Override
	public Central saveCentralFromAsignacion(Central central) throws Exception {
		return centralesService.saveCentralFromAsignacion(central);
	}
}
