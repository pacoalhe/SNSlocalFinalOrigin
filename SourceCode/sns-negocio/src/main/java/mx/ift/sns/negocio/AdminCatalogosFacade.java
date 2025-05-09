package mx.ift.sns.negocio;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import mx.ift.sns.dao.ng.IParametroDao;
import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.abn.EstadoAbn;
import mx.ift.sns.modelo.abn.PoblacionAbn;
import mx.ift.sns.modelo.central.Central;
import mx.ift.sns.modelo.central.ComboCentral;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.central.Marca;
import mx.ift.sns.modelo.central.Modelo;
import mx.ift.sns.modelo.central.VCatalogoCentral;
import mx.ift.sns.modelo.cpsi.CodigoCPSI;
import mx.ift.sns.modelo.cpsi.EquipoSenalCpsi;
import mx.ift.sns.modelo.cpsi.EstatusCPSI;
import mx.ift.sns.modelo.cpsi.EstudioEquipoCpsi;
import mx.ift.sns.modelo.cpsi.InfoCatCpsi;
import mx.ift.sns.modelo.cpsi.VEstudioCPSI;
import mx.ift.sns.modelo.cpsn.CodigoCPSN;
import mx.ift.sns.modelo.cpsn.EquipoSenalCPSN;
import mx.ift.sns.modelo.cpsn.EstatusCPSN;
import mx.ift.sns.modelo.cpsn.EstudioEquipoCPSN;
import mx.ift.sns.modelo.cpsn.TipoBloqueCPSN;
import mx.ift.sns.modelo.cpsn.VEstudioCPSN;
import mx.ift.sns.modelo.filtros.FiltroBusquedaABNs;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCentrales;
import mx.ift.sns.modelo.filtros.FiltroBusquedaClaveServicio;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCodigosCPSI;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCodigosCPSN;
import mx.ift.sns.modelo.filtros.FiltroBusquedaEquipoSenal;
import mx.ift.sns.modelo.filtros.FiltroBusquedaMarcaModelo;
import mx.ift.sns.modelo.filtros.FiltroBusquedaMunicipios;
import mx.ift.sns.modelo.filtros.FiltroBusquedaPlantillas;
import mx.ift.sns.modelo.filtros.FiltroBusquedaPoblaciones;
import mx.ift.sns.modelo.filtros.FiltroBusquedaRangos;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSeries;
import mx.ift.sns.modelo.ng.Serie;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.oficios.Plantilla;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.ExportMunicipio;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.MunicipioPK;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.ot.Region;
import mx.ift.sns.modelo.ot.VCatalogoPoblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.modelo.series.VCatalogoSerie;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.ac.ExportConsultaCatalogoEstado;
import mx.ift.sns.negocio.ac.ExportConsultaCatalogoMarcaModelo;
import mx.ift.sns.negocio.ac.IEstatusService;
import mx.ift.sns.negocio.centrales.ICentralesService;
import mx.ift.sns.negocio.conf.IParametrosService;
import mx.ift.sns.negocio.cpsi.DetalleImportacionEquiposCpsi;
import mx.ift.sns.negocio.cpsi.ICodigoCPSIService;
import mx.ift.sns.negocio.cpsi.IEquipoSenalizacionCpsiService;
import mx.ift.sns.negocio.cpsn.DetalleImportacionEquiposCpsn;
import mx.ift.sns.negocio.cpsn.ICodigoCPSNService;
import mx.ift.sns.negocio.cpsn.IEquipoSenalizacionCPSNService;
import mx.ift.sns.negocio.ng.ISeriesService;
import mx.ift.sns.negocio.ng.ISolicitudesService;
import mx.ift.sns.negocio.nng.IClaveServicioService;
import mx.ift.sns.negocio.oficios.IOficiosService;
import mx.ift.sns.negocio.ot.IOrganizacionTerritorialService;
import mx.ift.sns.negocio.psts.IProveedoresService;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;

/**
 * Adminstración de Catalogos.
 */
@Stateless(name = "AdminCatalogosFacade", mappedName = "AdminCatalogosFacade")
@Remote(IAdminCatalogosFacade.class)
public class AdminCatalogosFacade extends ConfiguracionFacade implements IAdminCatalogosFacade {

    /** Logger de la clase. */
    // private static final Logger LOGGER = LoggerFactory.getLogger(AdminCatalogosFacade.class);

    /** Servicio de centrales. */
    @EJB
    private ICentralesService centralService;

    /** Servicio de Organizacion Territorial. */
    @EJB
    private IOrganizacionTerritorialService otService;

    /** Servicio proveedor. */
    @EJB
    private IProveedoresService pService;

    /** Servicio series y rangos. */
    @EJB
    private ISeriesService serieService;

    /** Servicio Oficios. */
    @EJB
    private IOficiosService oficiosService;

    /** Servicio de Solicitudes. */
    @EJB
    private ISolicitudesService solicitudesService;

    /** Servicio parametros. */
    @EJB
    private IParametrosService paramService;

    /** Servicio de Estatus. */
    @EJB
    private IEstatusService estatusService;

    /** Service de Claves de Servicio. */
    @EJB
    private IClaveServicioService claveServicioService;

    /** Servicio de equipos de señalización. */
    @EJB
    private IEquipoSenalizacionCPSNService equipoSenalService;

    /** Servicio de codigos CPS Nacionales. */
    @EJB
    private ICodigoCPSNService codigoCPSNService;

    /** DAO Rangos Serie. */
    @Inject
    private IParametroDao parametroDao;

    /** Servicio de codigos CPS Internacionales. */
    @EJB
    private ICodigoCPSIService codigoCPSIService;

    /** Servicio de equipos de señalización de CPSI. */
    @EJB
    private IEquipoSenalizacionCpsiService equipoSenalCpsiService;

    @Override
    public Marca getMarcaByNombre(String nombre) throws Exception {
        return centralService.getMarcaByNombre(nombre);
    }

    @Override
    public Marca getMarcaById(BigDecimal idMarca) throws Exception {
        return centralService.getMarcaById(idMarca);
    }

    @Override
    public List<Marca> findAllMarcas() throws Exception {
        return centralService.findAllMarcas();
    }

    @Override
    public List<Marca> findAllMarcasEager(FiltroBusquedaMarcaModelo filtros) {
        return centralService.findAllMarcasEager(filtros);
    }

    @Override
    public int findAllMarcasCount(FiltroBusquedaMarcaModelo filtros) {
        return centralService.findAllMarcasCount(filtros);
    }

    @Override
    public List<Estatus> findAllEstatus() {
        return estatusService.findAllEstatus();
    }

    @Override
    public List<Municipio> findMunicipiosByEstado(String estado) throws Exception {
        return otService.findMunicipiosByEstado(estado);
    }

    @Override
    public List<Estado> findAllEstados() throws Exception {
        return otService.findEstados();
    }

    @Override
    public List<Estado> findEstados(int first, int pagesize) throws Exception {
        return otService.findEstados(first, pagesize);
    }

    @Override
    public Estado findEstadoById(String id) throws Exception {
        return otService.findEstadoById(id);
    }

    @Override
    public int findAllEstadosCount() {
        return otService.findAllEstadosCount();
    }

    @Override
    public Estado saveEstado(Estado estado) {
        return otService.saveEstado(estado);
    }

    @Override
    public List<Poblacion> findAllPoblaciones(FiltroBusquedaPoblaciones pFiltrosPoblacion) {
        return otService.findAllPoblaciones(pFiltrosPoblacion);
    }

    @Override
    public int findAllPoblacionesCount(FiltroBusquedaPoblaciones pFiltrosPoblacion) {
        return otService.findAllPoblacionesCount(pFiltrosPoblacion);
    }

    @Override
    public void guardaPoblaciones(List<Poblacion> listaPoblaciones) throws Exception {
        otService.guardaPoblaciones(listaPoblaciones);
    }

    @Override
    public Poblacion savePoblacion(Poblacion poblacion) {
        return otService.savePoblacion(poblacion);
    }

    @Override
    public PoblacionAbn savePoblacionAbn(PoblacionAbn poblacionAbn) {
        return otService.savePoblacionAbn(poblacionAbn);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public byte[] getExportConsultaCatalogoPoblaciones(FiltroBusquedaPoblaciones pFiltros) throws Exception {
        return otService.getExportConsultaCatalogoPoblaciones(pFiltros);
    }

    @Override
    public Marca saveMarca(Marca marca) {
        return centralService.saveMarca(marca);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public byte[] generarListadoEstados(List<Estado> listaEstados) throws Exception {
        ExportConsultaCatalogoEstado excca = new ExportConsultaCatalogoEstado(listaEstados, this);
        ExportarExcel export = new ExportarExcel(parametroDao);
        return export.generarReporteExcel("Catálogo de Estados", excca);
    }

    @Override
    public List<Modelo> getModelosByMarca(BigDecimal idMarca) {
        return centralService.getModelosByMarca(idMarca);
    }

    @Override
    public List<Modelo> findAllModelos(FiltroBusquedaMarcaModelo filtros) {
        return centralService.findAll(filtros);
    }

    @Override
    public int findAllModelosCount(FiltroBusquedaMarcaModelo filtros) {
        return centralService.findAllCount(filtros);
    }

    @Override
    public byte[] getExportConsultaCatalogoMarcas(List<Marca> pListaMarca) throws Exception {
        ExportConsultaCatalogoMarcaModelo excca = new ExportConsultaCatalogoMarcaModelo(pListaMarca);
        ExportarExcel export = new ExportarExcel(parametroDao);
        return export.generarReporteExcel("Catálogo de Marcas", excca);
    }

    @Override
    public List<Poblacion> findAllPoblaciones(String estado, String municipio) {
        return otService.findAllPoblaciones(estado, municipio);
    }

    @Override
    public List<Poblacion> findAllPoblaciones(String estado, String municipio, boolean pUseCache) {
        return otService.findAllPoblaciones(estado, municipio, pUseCache);
    }

    @Override
    public List<Proveedor> findAllProveedoresActivos() throws Exception {
        return pService.findAllProveedoresActivos();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Proveedor> findAllProveedores() throws Exception {
        return pService.findAllProveedores();
    }

    @Override
    public List<ComboCentral> getComboCentrales() {
        return centralService.getComboCentrales();
    }

    @Override
    public Central findCentralById(BigDecimal idCentral) throws Exception {
        return centralService.findCentralById(idCentral);
    }

    @Override
    public List<Central> getCentralesActivasByModelo(Modelo modelo) {
        return centralService.getCentralesActivasByModelo(modelo);
    }

    @Override
    public List<VCatalogoCentral> findAllCentrales(FiltroBusquedaCentrales pFiltrosCentral) {
        return centralService.findAllCentrales(pFiltrosCentral);
    }

    @Override
    public int findAllCentralesCount(FiltroBusquedaCentrales pFiltrosCentral) {
        return centralService.findAllCentralesCount(pFiltrosCentral);
    }

    @Override
    public void bajaCentral(Central central) throws Exception {
        centralService.bajaCentral(central);
    }

    @Override
    public List<Region> findAllRegiones() {

        return otService.findAllRegiones();
    }

    @Override
    public List<Municipio> findAllMunicipios(FiltroBusquedaMunicipios filtros) {

        return otService.findAllMunicipios(filtros);
    }

    @Override
    public Central saveCentral(Central central) throws Exception {
        return centralService.saveCentral(central);
    }

    @Override
    public Poblacion findPoblacionById(String inegi) throws Exception, NoResultException {
        return otService.findPoblacionById(inegi);
    }

    @Override
    public Municipio findMunicipioById(MunicipioPK id) throws Exception {
        return otService.findMunicipioById(id);
    }

    @Override
    public Integer getTotalNumRangosAsignadosByMunicipio(Municipio municipio) {

        return serieService.getTotalNumRangosAsignadosByMunicipio(municipio);
    }

    @Override
    public Integer findAllMunicipiosCount(FiltroBusquedaMunicipios filtros) {

        return otService.findAllMunicipiosCount(filtros);
    }

    @Override
    public boolean getMunicipioByEstado(String codEstado, String codMunicipio) {

        return otService.getMunicipioByEstado(codEstado, codMunicipio);
    }

    @Override
    public void guardaMunicipios(List<Municipio> listaMunicipios) throws Exception {
        otService.guardaMunicipios(listaMunicipios);
    }

    @Override
    public Municipio saveMunicipio(Municipio municipio) {
        return otService.saveMunicipio(municipio);
    }

    @Override
    public byte[] getExportConsultaCatalogoCentrales(FiltroBusquedaCentrales pfiltro) throws Exception {
        return centralService.getExportConsultaCatalogoCentrales(pfiltro);
    }

    @Override
    public Proveedor bajaProveedor(Proveedor proveedor) {
        return pService.bajaProveedor(proveedor);
    }

    @Override
    public Proveedor activarProveedor(Proveedor proveedor) {
        return pService.activarProveedor(proveedor);
    }

    @Override
    public List<Central> findAllCentralesByName(String name) {
        return centralService.findAllCentralesByName(name);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public byte[] getExportConsultaCatalogoABNs(FiltroBusquedaABNs pFiltros) throws Exception {
        return otService.getExportConsultaCatalogoABNs(pFiltros);
    }

    @Override
    public void removeMunicipio(Municipio municipio) {
        otService.removeMunicipio(municipio);

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public InputStream generarListadoMunicipios(List<ExportMunicipio> listaDatos) throws Exception {
        ByteArrayInputStream bais = null;
        bais = new ByteArrayInputStream(otService.generarListadoMunicipios(listaDatos));
        return bais;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<ExportMunicipio> findAllExportMunicipio(FiltroBusquedaMunicipios filtros) {

        return otService.findAllExportMunicipio(filtros);
    }

    @Override
    public Integer findAllExportMunicipioCount(FiltroBusquedaMunicipios filtros) {

        return otService.findAllExportMunicipioCount(filtros);
    }

    @Override
    public List<ClaveServicio> findAllClaveServicio() {
        return claveServicioService.findAllClaveServicio();
    }

    @Override
    public ClaveServicio getClaveServicioByCodigo(BigDecimal pCodigo) {
        return serieService.getClaveServicioByCodigo(pCodigo);
    }

    @Override
    public int findAllRangosCount(FiltroBusquedaRangos pFiltros) throws Exception {
        return serieService.findAllRangosCount(pFiltros);
    }

    @Override
    public Nir getNirByCodigo(int cdgNir) {
        return otService.getNirByCodigo(cdgNir);
    }

    @Override
    public List<Nir> findAllNirByParteCodigo(int pCodigoNir) {
        return otService.findAllNirByParteCodigo(pCodigoNir);
    }

    @Override
    public List<Nir> findAllNirByAbn(BigDecimal numAbn) throws Exception {
        return otService.findAllNirByAbn(numAbn);
    }

    @Override
    public void desactivarNir(Nir pNir) throws Exception {
        otService.desactivarNir(pNir);
    }

    @Override
    public Nir createNir(Abn pAbn, int pCdgNir, List<String> pListaSnas) throws Exception {
        return otService.createNir(pAbn, pCdgNir, pListaSnas);
    }

    @Override
    public Nir saveNir(Nir nir) {
        return otService.saveNir(nir);
    }

    @Override
    public Serie createSerie(Nir pNir, BigDecimal pSna) {
        return serieService.createSerie(pNir, pSna);
    }

    @Override
    public Serie getSerie(BigDecimal pIdSna, BigDecimal pIdNir) throws Exception {
        return serieService.getSerie(pIdSna, pIdNir);
    }

    @Override
    public Abn saveAbn(Abn abn) throws Exception {
        return otService.saveAbn(abn);
    }

    @Override
    public Abn getAbnById(BigDecimal pCodigo) throws Exception {
        return otService.getAbnById(pCodigo);
    }

    @Override
    public Abn changeAbnCode(Abn pAbn, BigDecimal pNewCode) throws Exception {
        return otService.changeAbnCode(pAbn, pNewCode);
    }

    @Override
    public void updatePoblacionesAbn(BigDecimal pCodAbn, List<PoblacionAbn> pPoblacionesAbn) {
        otService.updatePoblacionesAbn(pCodAbn, pPoblacionesAbn);
    }

    @Override
    public Serie saveSerie(Serie pSerie) {
        return serieService.saveSerie(pSerie);
    }

    @Override
    public List<Poblacion> findAllPoblacionesByAbn(Abn pAbn) {
        return otService.findAllPoblacionesByAbn(pAbn);
    }

    @Override
    public List<Poblacion> findAllPoblacionesByAbn(Abn pAbn, boolean pUseCache) {
        return otService.findAllPoblacionesByAbn(pAbn, pUseCache);
    }

    @Override
    public List<Municipio> findAllMunicipiosByAbn(Abn pAbn) {
        return otService.findAllMunicipiosByAbn(pAbn);
    }

    @Override
    public List<Municipio> findAllMunicipiosByAbn(Abn pAbn, boolean pUseCache) {
        return otService.findAllMunicipiosByAbn(pAbn, pUseCache);
    }

    @Override
    public List<Plantilla> findAllPlantillas() {
        return oficiosService.findAllPlantillas();
    }

    @Override
    public List<Plantilla> findAllPlantillas(FiltroBusquedaPlantillas pFiltros) {
        return oficiosService.findAllPlantillas(pFiltros);
    }

    @Override
    public int findAllPlantillasCount(FiltroBusquedaPlantillas pFiltros) {
        return oficiosService.findAllPlantillasCount(pFiltros);
    }

    @Override
    public List<TipoSolicitud> findAllTiposSolicitud() {
        return solicitudesService.findAllTiposSolicitud();
    }

    @Override
    public TipoSolicitud getTipoSolicitudById(Integer idTipoSolicitud) {
        return solicitudesService.getTipoSolicitudById(idTipoSolicitud);
    }

    @Override
    public Plantilla savePlantilla(Plantilla pPlantilla) {
        return oficiosService.savePlantilla(pPlantilla);
    }

    @Override
    public Plantilla getPlantilla(TipoSolicitud pTipoSolicitud, TipoDestinatario pTipoDestinatario) {
        return oficiosService.getPlantilla(pTipoSolicitud, pTipoDestinatario);
    }

    @Override
    public byte[] getExportConsultaCatalogoPlantillas(List<Plantilla> pListaPlantillas) throws Exception {
        return oficiosService.getExportConsultaCatalogoPlantillas(pListaPlantillas);
    }

    @Override
    public List<TipoDestinatario> findAllTiposDestinatario() {
        return oficiosService.findAllTiposDestinatario();
    }

    @Override
    public TipoDestinatario getTipoDestinatarioByCdg(String pCdgDestinatario) {
        return oficiosService.getTipoDestinatarioByCdg(pCdgDestinatario);
    }

    @Override
    public List<ClaveServicio> findAllClaveServicio(FiltroBusquedaClaveServicio pFiltros) throws Exception {
        return claveServicioService.findAllClaveServicio(pFiltros);
    }

    @Override
    public int findAllClaveServicioCount(FiltroBusquedaClaveServicio pFiltros) throws Exception {
        return claveServicioService.findAllClaveServicioCount(pFiltros);
    }

    @Override
    public ClaveServicio validaClaveServicio(ClaveServicio claveServicio, boolean modoEdicion) {
        return claveServicioService.validaClaveServicio(claveServicio, modoEdicion);
    }

    @Override
    public ClaveServicio guardarClaveServicio(ClaveServicio claveServicio, boolean modoEdicion) {
        return claveServicioService.guardarClaveServicio(claveServicio, modoEdicion);
    }

    @Override
    public int findAllCatalogoSeriesCount(FiltroBusquedaSeries pFiltros) throws Exception {
        return serieService.findAllCatalogoSeriesCount(pFiltros);
    }

    @Override
    public List<VCatalogoSerie> findAllCatalogoSeries(FiltroBusquedaSeries pFiltros) throws Exception {
        return serieService.findAllCatalogoSeries(pFiltros);
    }

    @Override
    public List<Nir> findAllNirs() throws Exception {
        return otService.findAllNirs();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public byte[] getExportConsultaCatalogoSeries(FiltroBusquedaSeries pfiltro) throws Exception {
        return serieService.getExportConsultaCatalogoSeries(pfiltro);
    }

    @Override
    public List<EquipoSenalCPSN> findAllEquiposSenal(FiltroBusquedaEquipoSenal pFiltros) throws Exception {
        return equipoSenalService.findAllEquiposSenal(pFiltros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public byte[] exportarEquiposCPSN(FiltroBusquedaEquipoSenal pFiltros) throws Exception {
        return equipoSenalService.exportarEquiposCPSN(pFiltros);
    }

    @Override
    public List<Nir> findNirsByDigitos(int pCdgNir) {
        return otService.findNirsByDigitos(pCdgNir);
    }

    @Override
    public boolean existSerieWithNirAbn(String abn, String nir) {
        return serieService.existSerieWithNirAbn(abn, nir);
    }

    @Override
    public TipoBloqueCPSN getTipoBloqueCPSNById(String id) throws Exception {
        return codigoCPSNService.getTipoBloqueCPSNById(id);
    }

    @Override
    public EstatusCPSN getEstatusCPSNById(String id) throws Exception {
        return codigoCPSNService.getEstatusCPSNById(id);
    }

    @Override
    public List<TipoBloqueCPSN> findAllTiposBloqueCPSN() {
        return codigoCPSNService.findAllTiposBloqueCPSN();
    }

    @Override
    public void eliminarEquipo(EquipoSenalCPSN equipo) {
        equipoSenalService.eliminarEquipo(equipo);
    }

    @Override
    public List<EstatusCPSN> findAllEstatusCPSN() {
        return codigoCPSNService.findAllEstatusCPSN();
    }

    @Override
    public List<CodigoCPSN> findCodigosCPSN(FiltroBusquedaCodigosCPSN pFiltro) {
        return codigoCPSNService.findCodigosCPSN(pFiltro);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public byte[] exportarCodigosCPSN(FiltroBusquedaCodigosCPSN pFiltros) throws Exception {
        return codigoCPSNService.exportarCodigosCPSN(pFiltros);
    }

    @Override
    public String liberarCodigosCPSN(List<CodigoCPSN> codigosCPSNSeleccionados) {
        return codigoCPSNService.liberarCodigosCPSN(codigosCPSNSeleccionados);
    }

    @Override
    public String reservarCodigosCPSN(List<CodigoCPSN> codigosCPSNSeleccionados) {
        return codigoCPSNService.reservarCodigosCPSN(codigosCPSNSeleccionados);
    }

    @Override
    public String validarDesagrupacionCodigosCPSN(List<CodigoCPSN> codigosCPSNSeleccionados) {
        return codigoCPSNService.validarDesagrupacionCodigosCPSN(codigosCPSNSeleccionados);
    }

    @Override
    public String desagruparCodigoCPSN(List<CodigoCPSN> codigosCPSNSeleccionados) {
        return codigoCPSNService.desagruparCodigoCPSN(codigosCPSNSeleccionados);
    }

    @Override
    public String agruparCodigoCPSN(List<CodigoCPSN> codigosCPSNSeleccionados) {
        return codigoCPSNService.agruparCodigoCPSN(codigosCPSNSeleccionados);
    }

    @Override
    public List<VEstudioCPSN> estudioCPSN() {
        return codigoCPSNService.estudioCPSN();
    }

    @Override
    public List<EstudioEquipoCPSN> estudioEquipoCPSN(Proveedor pst) {
        return equipoSenalService.estudioEquipoCPSN(pst);
    }

    @Override
    public EquipoSenalCPSN getEquipoSenalCPSNEagerLoad(EquipoSenalCPSN equipo) {
        return equipoSenalService.getEquipoSenalCPSNEagerLoad(equipo);
    }

    @Override
    public List<PoblacionAbn> findAllPoblacionesAbn(FiltroBusquedaABNs pFiltros) {
        return otService.findAllPoblacionesAbn(pFiltros);
    }

    @Override
    public int findAllPoblacionesAbnCount(FiltroBusquedaABNs pFiltros) {
        return otService.findAllPoblacionesAbnCount(pFiltros);
    }

    @Override
    public List<EstadoAbn> findAllEstadosAbn() {
        return otService.findAllEstadosAbn();
    }

    @Override
    public List<Abn> findAllAbns() throws Exception {
        return otService.findAllAbns();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Abn> findAllAbns(FiltroBusquedaABNs pFiltros) {
        return otService.findAllAbns(pFiltros);
    }

    @Override
    public int findAllAbnsCount(FiltroBusquedaABNs pFiltros) {
        return otService.findAllAbnsCount(pFiltros);
    }

    @Override
    public EstatusCPSI getEstatusCPSIById(String id) throws Exception {
        return codigoCPSIService.getEstatusCPSIById(id);
    }

    @Override
    public List<EstatusCPSI> findAllEstatusCPSI() {
        return codigoCPSIService.findAllEstatusCPSI();
    }

    @Override
    public List<CodigoCPSI> findCodigosCPSI(FiltroBusquedaCodigosCPSI pFiltro) {
        return codigoCPSIService.findAllCodigosCPSI(pFiltro);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public byte[] exportarCodigosCPSI(FiltroBusquedaCodigosCPSI pFiltros) throws Exception {
        return codigoCPSIService.exportarCodigosCPSI(pFiltros);
    }

    @Override
    public byte[] exportarEstudioCPSI(VEstudioCPSI estudio) throws Exception {
        return codigoCPSIService.exportarEstudioCPSI(estudio);
    }

    @Override
    public String liberarCodigosCPSI(List<InfoCatCpsi> codigosCPSISeleccionados) {
        return codigoCPSIService.liberarCodigosCPSI(codigosCPSISeleccionados);
    }

    @Override
    public String reservarCodigosCPSI(List<InfoCatCpsi> codigosCPSISeleccionados) {
        return codigoCPSIService.reservarCodigosCPSI(codigosCPSISeleccionados);
    }

    @Override
    public VEstudioCPSI estudioCPSI(Proveedor proveedor) {
        return codigoCPSIService.estudioCPSI(proveedor);
    }

    @Override
    public DetalleImportacionEquiposCpsn procesarArchivoEquiposCpsn(File archivo, Proveedor pst)
            throws Exception {
        return equipoSenalService.procesarArchivoEquipos(archivo, pst);
    }

    @Override
    public DetalleImportacionEquiposCpsi procesarArchivoEquiposCpsi(File archivo, Proveedor pst)
            throws Exception {
        return equipoSenalCpsiService.procesarArchivoEquipos(archivo, pst);
    }

    @Override
    public List<EstudioEquipoCpsi> estudioEquipoCPSI(Proveedor proveedor) {
        return equipoSenalCpsiService.estudioEquipo(proveedor);
    }

    @Override
    public List<EquipoSenalCpsi> findAllEquiposSenalCpsi(FiltroBusquedaEquipoSenal pFiltros) throws Exception {
        return equipoSenalCpsiService.findAllEquiposSenal(pFiltros);
    }

    @Override
    public EquipoSenalCpsi getEquipoSenalCpsiEagerLoad(EquipoSenalCpsi equipo) {
        return equipoSenalCpsiService.getEquipoSenalCpsiEagerLoad(equipo);
    }

    @Override
    public void eliminarEquipoCpsi(EquipoSenalCpsi equipo) {
        equipoSenalCpsiService.eliminarEquipo(equipo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public byte[] exportarEquiposCpsi(FiltroBusquedaEquipoSenal pFiltros) throws Exception {
        return equipoSenalCpsiService.exportarEquipos(pFiltros);
    }

    @Override
    public List<InfoCatCpsi> findAllInfoCatCPSI(FiltroBusquedaCodigosCPSI filtros) {
        return codigoCPSIService.findAllInfoCatCPSI(filtros);

    }

    @Override
    public boolean isNumeracionAsignada(Poblacion poblacion) {
        boolean isNumeracion = false;
        VCatalogoPoblacion vPoblacion = otService.findPoblacion(poblacion.getInegi());
        if (null != vPoblacion) {
            if ("SI".equalsIgnoreCase(vPoblacion.getNumeracionAsignada())) {
                isNumeracion = true;
            }

        }
        return isNumeracion;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String actualizaSerie(BigDecimal serieOriginal, BigDecimal nirOriginal, BigDecimal serieNueva,
            BigDecimal usuario) {
        return serieService.actualizaSerie(serieOriginal, nirOriginal, serieNueva, usuario);
    }

    @Override
    public boolean isSolicitudPendieteByNir(BigDecimal idNir) {
        return solicitudesService.isSolicitudPendieteByNir(idNir);
    }

    @Override
    public boolean isRangosPentientesByNir(Nir nir) {
        return serieService.isRangosPentientesByNir(nir);
    }
}
