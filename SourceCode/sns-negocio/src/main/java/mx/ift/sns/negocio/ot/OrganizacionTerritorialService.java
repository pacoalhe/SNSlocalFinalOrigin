package mx.ift.sns.negocio.ot;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import mx.ift.sns.dao.ng.IAbnConsolidarDao;
import mx.ift.sns.dao.ng.IAbnDao;
import mx.ift.sns.dao.ng.IEstadoAbnDao;
import mx.ift.sns.dao.ng.INirConsolidarDao;
import mx.ift.sns.dao.ng.INirDao;
import mx.ift.sns.dao.ng.IParametroDao;
import mx.ift.sns.dao.ng.IPoblacionAbnDao;
import mx.ift.sns.dao.ng.IPoblacionConsolidarDao;
import mx.ift.sns.dao.ot.IEstadoDao;
import mx.ift.sns.dao.ot.IExportMunicipioDao;
import mx.ift.sns.dao.ot.IMunicipioDao;
import mx.ift.sns.dao.ot.IPoblacionDao;
import mx.ift.sns.dao.ot.IRegionDao;
import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.abn.EstadoAbn;
import mx.ift.sns.modelo.abn.PoblacionAbn;
import mx.ift.sns.modelo.abn.VCatalogoAbn;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.filtros.FiltroBusquedaABNs;
import mx.ift.sns.modelo.filtros.FiltroBusquedaMunicipios;
import mx.ift.sns.modelo.filtros.FiltroBusquedaPoblaciones;
import mx.ift.sns.modelo.ng.AbnConsolidar;
import mx.ift.sns.modelo.ng.NirConsolidar;
import mx.ift.sns.modelo.ng.PoblacionConsolidar;
import mx.ift.sns.modelo.ng.Serie;
import mx.ift.sns.modelo.ng.SeriePK;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.EstadoArea;
import mx.ift.sns.modelo.ot.ExportMunicipio;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.MunicipioPK;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.ot.Region;
import mx.ift.sns.modelo.ot.VCatalogoPoblacion;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.modelo.usu.Usuario;
import mx.ift.sns.negocio.ac.ExportConsultaCatalogoAbn;
import mx.ift.sns.negocio.ac.ExportConsultaCatalogoMunicipio;
import mx.ift.sns.negocio.ac.ExportConsultaCatalogoPoblacion;
import mx.ift.sns.negocio.ng.ISeriesService;
import mx.ift.sns.negocio.usu.IUsuariosService;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio de organizcion territorial.
 */
@Stateless(name = "OrganizacionTerritorialService", mappedName = "OrganizacionTerritorialService")
@Remote(IOrganizacionTerritorialService.class)
public class OrganizacionTerritorialService implements IOrganizacionTerritorialService {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizacionTerritorialService.class);

    /** Dao estado. */
    @Inject
    private IEstadoDao estadoDao;

    /** Dao municipio. */
    @Inject
    private IMunicipioDao municipioDao;

    /** Dao poblacion. */
    @Inject
    private IPoblacionDao poblacionDao;

    /** Dao abn. */
    @Inject
    private IAbnDao abnDao;

    /** Dao NIR. */
    @Inject
    private INirDao nirDao;

    /** Dao EstadoAbn. */
    @Inject
    private IEstadoAbnDao estadoAbnDao;

    /** DAO Region. */
    @Inject
    private IRegionDao regionDao;

    /** DAO ExportMunicipio. */
    @Inject
    private IExportMunicipioDao exportMunicipioDao;

    /** DAO PoblacionAbn. */
    @Inject
    private IPoblacionAbnDao poblacionAbnDao;

    /** Servicio de Series. */
    @EJB
    private ISeriesService seriesService;

    /** DAO de parametros. */
    @Inject
    private IParametroDao parametroDao;

    /** DAO de abnConsolidar. */
    @Inject
    private IAbnConsolidarDao abnConsolidarDao;

    /** Dao NIrConsolidar. */
    @Inject
    private INirConsolidarDao nirConsolidarDao;

    /** Servicio de usuarios. */
    @EJB(mappedName = "UsuariosService")
    private IUsuariosService usuariosService;

    /** Dao PoblacionConsolidar. */
    @Inject
    private IPoblacionConsolidarDao poblacionConsolidarDao;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Municipio> findMunicipiosByEstado(String estado) throws Exception {
        return municipioDao.findMunicipio(estado);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Municipio findMunicipioById(MunicipioPK id) throws Exception {
        return municipioDao.getMunicipioById(id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Estado> findEstados() throws Exception {
        return estadoDao.findEstados();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Estado> findEstados(int first, int pagesize) throws Exception {
        return estadoDao.findAllEstados(first, pagesize);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Estado findEstadoById(String id) throws Exception, NoResultException {
        return estadoDao.getEstadoByCodigo(id);
    }

    @Override
    public List<Poblacion> findAllPoblaciones(String estado, String municipio) {
        return poblacionDao.findAllPoblaciones(estado, municipio);
    }

    @Override
    public List<Poblacion> findAllPoblaciones(String estado, String municipio, boolean pUseCache) {
        return poblacionDao.findAllPoblaciones(estado, municipio, pUseCache);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Poblacion findPoblacionById(String inegi) throws Exception, NoResultException {
        return poblacionDao.getPoblacionByInegi(inegi);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<Poblacion> findAllPoblacionesByAbn(Abn pAbn) {
        return poblacionAbnDao.findAllPoblacionesByAbn(pAbn);
    }

    @Override
    public List<Poblacion> findAllPoblacionesByAbn(Abn pAbn, boolean pUseCache) {
        return poblacionAbnDao.findAllPoblacionesByAbn(pAbn, pUseCache);
    }

    @Override
    public List<Municipio> findAllMunicipiosByAbn(Abn pAbn) {
        return poblacionAbnDao.findAllMunicipiosByAbn(pAbn);
    }

    @Override
    public List<Municipio> findAllMunicipiosByAbn(Abn pAbn, boolean pUseCache) {
        return poblacionAbnDao.findAllMunicipiosByAbn(pAbn, pUseCache);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Poblacion> findAllPoblacionByAbnUbicacion(String estado, String municipio, BigDecimal abn)
            throws Exception {
        return poblacionDao.findAllPoblacionByAbnUbicacion(estado, municipio, abn);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Poblacion getPoblacionByAbnInegi(BigDecimal codigoAbn, String inegi) {
        return poblacionDao.getPoblacionByAbnInegi(codigoAbn, inegi);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardaPoblaciones(List<Poblacion> listaPoblaciones) throws Exception {
        Usuario usuario = usuariosService.getCurrentUser();
        for (Poblacion poblacion : listaPoblaciones) {
            poblacion.updateAuditableValues(usuario);
            poblacionDao.saveOrUpdate(poblacion);
        }
    }

    @Override
    public Poblacion savePoblacion(Poblacion poblacion) {
        // Se hace la Auditoría del registro
        poblacion.updateAuditableValues(usuariosService.getCurrentUser());
        return poblacionDao.saveOrUpdate(poblacion);
    }

    @Override
    public Estado saveEstado(Estado estado) {
        // Se hace la Auditoría del registro
        estado.updateAuditableValues(usuariosService.getCurrentUser());
        return estadoDao.saveOrUpdate(estado);
    }

    @Override
    public int findAllEstadosCount() {
        return estadoDao.findAllEstadosCount();
    }

    @Override
    public int findAllPoblacionesCount(BigDecimal codigo) {
        return poblacionDao.findAllPoblacionesCount(codigo);
    }

    @Override
    public int findAllMunicipiosCount(BigDecimal codigo) {
        return poblacionDao.findAllMunicipiosCount(codigo);
    }

    @Override
    public PoblacionAbn savePoblacionAbn(PoblacionAbn poblacionAbn) {
        return poblacionAbnDao.saveOrUpdate(poblacionAbn);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PoblacionAbn getPoblacionAbnByAbnInegi(BigDecimal codigoAbn, String inegi) {
        return poblacionDao.getPoblacionAbnByAbnInegi(codigoAbn, inegi);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Poblacion> findAllPoblacionesLikeNombre(String cadena) {
        return poblacionDao.findAllPoblacionesLikeNombre(cadena);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<String> findAllPoblacionesNameLikeNombre(String cadena) {
        return poblacionDao.findAllPoblacionesNameLikeNombre(cadena);
    }

    @Override
    public boolean existePoblacionEnAbn(Poblacion poblacion, Abn abn) {
        return poblacionDao.existePoblacionEnAbn(poblacion, abn);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updatePoblacionesAbn(BigDecimal pCodAbn, List<PoblacionAbn> pPoblacionesAbn) {
        poblacionAbnDao.updatePoblacionesAbn(pCodAbn, pPoblacionesAbn);
    }

    @Override
    public Abn getAbnByCodigoNir(String codigoNir) {
        return abnDao.getAbnByCodigoNir(codigoNir);
    }

    @Override
    public  List<Abn> getAbnByZona(int zona) {
        return abnDao.getAbnByZona(zona);
    }

    @Override
    public Region getRegionById(BigDecimal id) {
        return regionDao.getRegionById(id);
    }

    @Override
    public List<Region> findAllRegiones() {
        return regionDao.findAllRegiones();
    }

    @Override
    public List<Municipio> findAllMunicipios(FiltroBusquedaMunicipios filtros) {
        return municipioDao.findAllMunicipios(filtros);
    }

    @Override
    public List<Poblacion> findAllPoblaciones(FiltroBusquedaPoblaciones filtros) {
        return poblacionDao.findAllPoblaciones(filtros);
    }

    @Override
    public int findAllPoblacionesCount(FiltroBusquedaPoblaciones filtros) {
        return poblacionDao.findAllPoblacionesCount(filtros);
    }

    @Override
    public Integer findAllMunicipiosCount(FiltroBusquedaMunicipios filtros) {
        return municipioDao.findAllMunicipiosCount(filtros);

    }

    @Override
    public boolean getMunicipioByEstado(String codEstado, String codMunicipio) {
        return municipioDao.getMunicipioByEstado(codEstado, codMunicipio);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardaMunicipios(List<Municipio> listaMunicipios) throws Exception {
        Usuario usuario = usuariosService.getCurrentUser();
        for (Municipio municipio : listaMunicipios) {
            // Se hace la Auditoría del registro
            municipio.updateAuditableValues(usuario);
            municipioDao.saveOrUpdate(municipio);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Municipio saveMunicipio(Municipio municipio) {
        // Se hace la Auditoría del registro
        municipio.updateAuditableValues(usuariosService.getCurrentUser());
        return municipioDao.saveOrUpdate(municipio);
    }

    @Override
    public List<EstadoAbn> findAllEstadosAbn() {
        return estadoAbnDao.findAllEstadosAbn();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public byte[] getExportConsultaCatalogoABNs(FiltroBusquedaABNs pFiltros) throws Exception {

        // Listado de ABNs devuelto por la Vista de BBDD
        pFiltros.setUsarPaginacion(false);
        List<VCatalogoAbn> listaAbn = this.findAllAbnsForCatalog(pFiltros);

        // Generación del Reporte
        ExportConsultaCatalogoAbn excca = new ExportConsultaCatalogoAbn(listaAbn, this);
        ExportarExcel export = new ExportarExcel(parametroDao);
        // if (export.getTamMaxExportacion() >= listaAbn.size()) {
        // return export.generarReporteExcel("Catálogo de ABN's", excca);
        // } else {
        return export.generarReporteExcelAbns("Catálogo de ABN's", excca, listaAbn);
        // }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public EstadoAbn getEstadoAbnByCodigo(String pCodigo) {
        return estadoAbnDao.getEstadoAbnByCodigo(pCodigo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Abn> findAllAbns() throws Exception {
        return abnDao.findAllAbns();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Abn> findAllAbns(FiltroBusquedaABNs pFiltros) {
        return abnDao.findAllAbns(pFiltros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public int findAllAbnsCount(FiltroBusquedaABNs pFiltros) {
        return abnDao.findAllAbnsCount(pFiltros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Abn getAbnById(BigDecimal pCodigo) throws Exception {
        return abnDao.getAbnById(pCodigo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Poblacion getPoblacionAnclaByCodigoAbn(BigDecimal codigo) throws Exception {
        return abnDao.getPoblacionAnclaByCodigoAbn(codigo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Abn saveAbn(Abn abn) throws Exception {
        // Se hace la Auditoría del registro
        abn.updateAuditableValues(usuariosService.getCurrentUser());
        return abnDao.saveOrUpdate(abn);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Abn changeAbnCode(Abn pAbn, BigDecimal pNewCode) throws Exception {

        // Al ser PK, no se puede cambiar directamente el código de ABN. Primero
        // hemos de crear el nuevo ABN, asociar todos las tablas que tengan referencia
        // con el nuevo ABN y borrar el registro del ABN antiguo.

        // Desde el Front-End se validará que no exista el ABN nuevo.
        Abn newAbn = new Abn();
        newAbn.setCodigoAbn(pNewCode);
        newAbn.setNombre(pAbn.getNombre());
        newAbn.setEstadoAbn(pAbn.getEstadoAbn());
        newAbn.setFechaConsolidacion(pAbn.getFechaConsolidacion());
        newAbn.setPresuscripcion(pAbn.getPresuscripcion());
        newAbn.setPoblacionAncla(pAbn.getPoblacionAncla());

        // Persistimos para que el Abn exista en el modelo
        newAbn = abnDao.saveOrUpdate(newAbn);

        try {
            // Asociamos todas las relaciones del viejo código de ABN al nuevo código de ABN
            newAbn = abnDao.changeAbnCode(pAbn, newAbn);
        } catch (Exception ex) {
            // En caso de excepción eliminamos el Nuevo ABN creado y no se continúa con la operación.
            abnDao.delete(newAbn);
            throw new Exception("Error al actualizar las claves ajenas asociadas al ABN "
                    + pAbn.getCodigoAbn().toString());
        }

        // Eliminamos el viejo código de ABN
        abnDao.delete(pAbn);

        return newAbn;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Nir getNir(BigDecimal id) {
        return nirDao.getNir(id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Nir getNirByCodigo(int cdgNir) {
        return nirDao.getNirByCodigo(cdgNir);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Nir> findAllNirByAbn(BigDecimal numAbn) {
        return nirDao.findAllNirByAbn(numAbn);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Nir> findAllNirByParteCodigo(int pCodigoNir) {
        return nirDao.findAllNirByParteCodigo(pCodigoNir);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Nir saveNir(Nir nir) {

        // Se hace la Auditoría del registro
        nir.updateAuditableValues(usuariosService.getCurrentUser());

        return nirDao.saveOrUpdate(nir);
    }

    @Override
    public void removeMunicipio(Municipio municipio) {
        municipioDao.delete(municipio);
    }

    @Override
    public List<Poblacion> findAllPoblacionesByEstadoAbn(String estado, BigDecimal codAbn) {
        return poblacionDao.findAllPoblacionesByEstadoAbn(estado, codAbn);
    }

    @Override
    public String countAllPoblacionesByEstado(String estado) {
        return poblacionDao.countAllPoblacionesByEstado(estado);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public byte[] generarListadoMunicipios(List<ExportMunicipio> listaDatos) throws Exception {
        ExportConsultaCatalogoMunicipio exccm = new ExportConsultaCatalogoMunicipio(listaDatos);
        ExportarExcel export = new ExportarExcel(parametroDao);
        if (export.getTamMaxExportacion() >= listaDatos.size()) {
            return export.generarReporteExcel("Catálogo de Municipios", exccm);
        } else {
            return export.generarReporteExcelMunicipios("Catálogo de Municipios", exccm, listaDatos);
        }

    }

    @Override
    public List<ExportMunicipio> findAllExportMunicipio(FiltroBusquedaMunicipios filtros) {
        return exportMunicipioDao.findAllExportMunicipio(filtros);
    }

    @Override
    public Integer findAllExportMunicipioCount(FiltroBusquedaMunicipios filtros) {
        return exportMunicipioDao.findAllExportMunicipioCount(filtros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int findAllNirByAbnCount(BigDecimal abn) {
        return nirDao.findAllNirByAbnCount(abn);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void desactivarNir(Nir pNir) throws Exception {

        // Los Nirs no se pueden desasociar de un ABN ya que el ABN es obligatorio en el modelo de datos
        // para un NIR, y tampoco se pueden eliminar ya que hay tablas con dependencias al NIR. Lo que se
        // hace es poner el estatus del nir a inactivo junto a sus series.

        // Series del NIR
        seriesService.desactivarSeriesByNir(pNir.getId());

        // Estatus Inactivo
        Estatus estatusDesactivado = new Estatus();
        estatusDesactivado.setCdg(Estatus.INACTIVO);
        pNir.setEstatus(estatusDesactivado);
        this.saveNir(pNir);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Nir con código {} actualizado a estado 'Inactivo'.", pNir.getCodigo());
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Nir createNir(Abn pAbn, int pCdgNir, List<String> pListaSnas) throws Exception {
        // Estatus Activo
        Estatus estatusActivo = new Estatus();
        estatusActivo.setCdg(Estatus.ACTIVO);

        // Nuevo Nir
        Nir nuevoNir = new Nir();
        nuevoNir.setAbn(pAbn);
        nuevoNir.setCodigo(pCdgNir);
        nuevoNir.setEstatus(estatusActivo);

        // Persistimos el Nir para obtener ID y poder asociar las series.
        nuevoNir = this.saveNir(nuevoNir);

        // Si las series no existen se crean previamente
        List<Serie> seriesNir = new ArrayList<Serie>(pListaSnas.size());

        for (String nuevoSna : pListaSnas) {
            Serie serieNueva = new Serie();
            serieNueva.setEstatus(estatusActivo);
            serieNueva.setNir(nuevoNir);

            SeriePK idSerie = new SeriePK();
            idSerie.setIdNir(nuevoNir.getId());
            idSerie.setSna(new BigDecimal(nuevoSna));
            serieNueva.setId(idSerie);

            seriesNir.add(seriesService.saveSerie(serieNueva));
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("NIR {} creado para el ABN {}. Id Nir: {}", pCdgNir, pAbn.getCodigoAbn(), nuevoNir.getId());
        }
        return nuevoNir;
    }

    @Override
    public List<Nir> findAllNirs() throws Exception {
        return nirDao.findAllNirs();
    }

    @Override
    public Poblacion getPoblacionByInegi(String inegi) {

        return poblacionDao.getPoblacionByInegi(inegi);
    }

    @Override
    public boolean existsNir(String nir) {
        return nirDao.existsNir(nir);
    }

    @Override
    public boolean existsZona(String zona) {
        return nirDao.existsZona(zona);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Nir> findNirsByDigitos(int pCdgNir) {
        return nirDao.findNirsByDigitos(pCdgNir);
    }

    @Override
    public List<Abn> findAbnInEstado(String estado) {
        return abnDao.findAbnInEstado(estado);
    }

    @Override
    public List<Abn> findAbnInMunicipio(String municipio, String estado) {
        return abnDao.findAbnInMunicipio(municipio, estado);
    }

    @Override
    public List<Nir> finAllNirInMunicipio(String municipio, String estado) {
        return nirDao.finAllNirInMunicipio(municipio, estado);
    }

    @Override
    public List<PoblacionAbn> findAllPoblacionesAbn(FiltroBusquedaABNs pFiltros) {
        return poblacionAbnDao.findAllPoblacionesAbn(pFiltros);
    }

    @Override
    public int findAllPoblacionesAbnCount(FiltroBusquedaABNs pFiltros) {
        return poblacionAbnDao.findAllPoblacionesAbnCount(pFiltros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<VCatalogoAbn> findAllAbnsForCatalog(FiltroBusquedaABNs pFiltros) {
        return abnDao.findAllAbnsForCatalog(pFiltros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public byte[] getExportConsultaCatalogoPoblaciones(FiltroBusquedaPoblaciones pFiltros) throws Exception {
        List<VCatalogoPoblacion> listado = poblacionDao.findAllCatalogoPoblacion(pFiltros);
        ExportConsultaCatalogoPoblacion exccp = new ExportConsultaCatalogoPoblacion(listado);
        ExportarExcel export = new ExportarExcel(parametroDao);
        if (export.getTamMaxExportacion() >= listado.size()) {
            return export.generarReporteExcel("Catálogo de Poblaciones", exccp);
        } else {
            return export.generarReporteExcelPoblaciones("Catálogo de Poblaciones", exccp, listado);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<PoblacionConsolidar> findPoblacionConsolidarById(BigDecimal pId) {
        return poblacionConsolidarDao.findPoblacionConsolidarById(pId);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<NirConsolidar> findNirConsolidarById(BigDecimal pId) {
        return nirConsolidarDao.findNirConsolidarById(pId);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public AbnConsolidar getAbnConsolidarByIdSol(BigDecimal pIdSol) {
        return abnConsolidarDao.getAbnConsolidarByIdSol(pIdSol);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<Poblacion> findAllPoblacionesEstadoNumeracion(Estado estado) {
        return poblacionDao.findAllPoblacionesEstadoNumeracion(estado);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<Nir> findAllNirByPoblacion(Poblacion poblacion) {
        return nirDao.findAllNirByPoblacion(poblacion);
    }

    @Override
    public Abn getAbnByPoblacion(Poblacion poblacion) {
        return abnDao.getAbnByPoblacion(poblacion);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public String getFechaConsolidacionByRangoAbn(Abn rangoAbn) {
        return abnConsolidarDao.getFechaConsolidacionByRangoAbn(rangoAbn);
    }

    @Override
    public List<EstadoArea> findAllAreaEstadoByEstado(Estado estado) {
        return estadoDao.findAllAreaEstadoByEstado(estado);
    }

    @Override
    public Date getFechaConsolidacionPoblacion(Poblacion poblacion) {

        return abnConsolidarDao.getFechaConsolidacionPoblacion(poblacion);
    }

    @Override
    public List<Municipio> findAllMunicipiosByNir(Nir nir, boolean pUseCache) {
        return poblacionAbnDao.findAllMunicipiosByNir(nir, pUseCache);
    }

    @Override
    public List<Nir> findAllNirByEstado(Estado estado) {
        return nirDao.findAllNirByEstado(estado);
    }

    @Override
    public VCatalogoPoblacion findPoblacion(String inegi) {
        return poblacionDao.findPoblacion(inegi);
    }

    @Override
    public int findAllMunicipiosByAbnAndEstadoCount(FiltroBusquedaABNs filtros) {
        return poblacionAbnDao.findAllMunicipiosByAbnAndEstadoCount(filtros);
    }

    @Override
    public List<Municipio> findAllMunicipiosByAbnAndEstado(FiltroBusquedaABNs filtros) {
        return poblacionAbnDao.findAllMunicipiosByAbnAndEstado(filtros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<PoblacionAbn> findAllPoblacionAbnByAbn(Abn pAbn) {
        return poblacionAbnDao.findAllPoblacionAbnByAbn(pAbn, true);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Municipio findMunicipioByNombreAndEstado(String nombreMun, String nombreEst) throws Exception {
        return municipioDao.findMunicipioByNombreAndEstado(nombreMun,nombreEst);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Poblacion> findPoblacionByNombreAndMunicipioAndEstado(String nombrePob, Municipio mun){
        return poblacionDao.findPoblacionByNombreAndMunicipioAndEstado(nombrePob, mun.getId().getCodMunicipio());
    }

    /**
     * FJAH 27.05.2025
     * @param idZona
     * @return
     * @throws Exception
     */
    @Override
    public List<Municipio> findMunicipiosByZona(Integer idZona) throws Exception {
        List<Object[]> resultados = municipioDao.findMunicipiosByZona(idZona);
        List<Municipio> municipios = new ArrayList<>();

        for (Object[] row : resultados) {
            Municipio m = new Municipio();

            MunicipioPK pk = new MunicipioPK();
            pk.setCodMunicipio(String.valueOf(row[0])); // ID_MUNICIPIO
            pk.setCodEstado(String.valueOf(row[2]));    // ID_ESTADO
            m.setId(pk);

            m.setNombre((String) row[1]);               // NOMBRE del municipio
            //m.setClaveInegi5((String) row[5]);          // CLAVE_INEGI_5 (SUBSTR del ID_INEGI)

            Estado estado = new Estado();
            //estado.setNombre((String) row[4]);          // NOMBRE del estado
            estado.setNombre((String) row[3]);          // NOMBRE del estado
            m.setEstado(estado);

            municipios.add(m);
        }

        return municipios;
    }

    @Override
    public Long countMunicipiosByZona(Integer idZona) throws Exception {
        return municipioDao.countMunicipiosByZona(idZona);
    }

}
