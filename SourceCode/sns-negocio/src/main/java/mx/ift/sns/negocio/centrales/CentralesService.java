package mx.ift.sns.negocio.centrales;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import mx.ift.sns.dao.ng.ICentralDao;
import mx.ift.sns.dao.ng.ICentralesRelacionDao;
import mx.ift.sns.dao.ng.IEstatusDao;
import mx.ift.sns.dao.ng.IMarcaDao;
import mx.ift.sns.dao.ng.IModeloDao;
import mx.ift.sns.dao.ng.IParametroDao;
import mx.ift.sns.modelo.central.Central;
import mx.ift.sns.modelo.central.CentralesRelacion;
import mx.ift.sns.modelo.central.ComboCentral;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.central.Marca;
import mx.ift.sns.modelo.central.Modelo;
import mx.ift.sns.modelo.central.ReporteCentralVm;
import mx.ift.sns.modelo.central.VCatalogoCentral;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCentrales;
import mx.ift.sns.modelo.filtros.FiltroBusquedaMarcaModelo;
import mx.ift.sns.modelo.filtros.FiltroReporteadorCentrales;
import mx.ift.sns.negocio.ac.ExportConsultaCatalogoCentral;
import mx.ift.sns.negocio.ac.ExportConsultaCatalogoMarcaModelo;
import mx.ift.sns.negocio.usu.IUsuariosService;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio de centrales.
 */
@Stateless(name = "CentralesService", mappedName = "CentralesService")
@Remote(ICentralesService.class)
public class CentralesService implements ICentralesService {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CentralesService.class);

    /**
     * Dao marca.
     */
    @Inject
    private IMarcaDao marcaDao;

    /**
     * Dao modelo.
     */
    @Inject
    private IModeloDao modeloDao;

    /**
     * Dao estatus.
     */
    @Inject
    private IEstatusDao estatusDao;

    /**
     * Dao CentralesRelacion.
     */
    @Inject
    private ICentralesRelacionDao centralesRelacionDao;

    /**
     * Dao Central.
     */
    @Inject
    private ICentralDao centralDao;

    /** DAO Rangos Serie. */
    @Inject
    private IParametroDao parametroDao;

    /** Servicio de usuarios. */
    @EJB(mappedName = "UsuariosService")
    private IUsuariosService usuariosService;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Marca> findAllMarcas() throws Exception {
        return marcaDao.findAllMarcas();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Marca getMarcaByNombre(String nombre) throws Exception {
        return marcaDao.getMarcaByNombre(nombre);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Marca getMarcaById(BigDecimal idMarca) throws Exception {
        return marcaDao.getMarcaById(idMarca);
    }

    @Override
    public List<Modelo> getModelosByMarca(BigDecimal idMarca) {
        return modeloDao.getModelosByMarca(idMarca);
    }

    @Override
    public List<Modelo> findAll(FiltroBusquedaMarcaModelo filtros) {
        return modeloDao.findAllModelos(filtros);
    }

    @Override
    public int findAllCount(FiltroBusquedaMarcaModelo filtros) {
        return modeloDao.findAllModelosCount(filtros);
    }

    @Override
    public List<Central> findAllCentralesByName(String name) {

        return centralDao.findAllCentralesByName(name);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Marca> findAllMarcasEager(FiltroBusquedaMarcaModelo filtros) {
        return marcaDao.findAllMarcasEager(filtros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int findAllMarcasCount(FiltroBusquedaMarcaModelo filtros) {
        return marcaDao.findAllMarcasCount(filtros);
    }

    @Override
    public List<Estatus> findAllEstatus() {
        return estatusDao.findAllEstatus();
    }

    @Override
    public CentralesRelacion saveCentralesRelacion(CentralesRelacion centralesRelacion) {

        return centralesRelacionDao.saveCentralesRelacion(centralesRelacion);
    }

    @Override
    public Marca saveMarca(Marca marca) {

        // Se hace la Auditoría del registro
        marca.updateAuditableValues(usuariosService.getCurrentUser());

        /*************************************/
        if (null != marca.getModelos() && marca.getModelos().size() > 0) {
            // También hay que auditar el modelo de la marca
            for (int i = 0; i < marca.getModelos().size(); i++) {
                Modelo modeloModificadoEnPantalla = null;
                Modelo modeloEnBaseDatos = null;
                modeloModificadoEnPantalla = marca.getModelos().get(i);
                if (modeloModificadoEnPantalla.getId() != null) {
                    modeloEnBaseDatos = modeloDao.getModeloById(modeloModificadoEnPantalla.getId());
                }
                // Cuando se ha hecho un cambio en un modelo o cuando el modelo es nuevo (modeloEnBaseDatos==null)
                // Entonces se hace la auditoría
                // En caso contrarío no hace falta porque no habrá habido modificación de ningun modelo de esa marca
                if (!modeloModificadoEnPantalla.equals(modeloEnBaseDatos)) {
                    modeloModificadoEnPantalla.updateAuditableValues(usuariosService.getCurrentUser());
                    marca.getModelos().set(i, modeloModificadoEnPantalla);
                }

            }
        }

        /************************************/
        return marcaDao.saveOrUpdate(marca);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<ComboCentral> getComboCentrales() {
        return centralDao.getComboCentrales();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Central findCentralById(BigDecimal idCentral) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("central por id");
        }
        return centralDao.getCentralById(idCentral);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Central> getCentralesActivasByModelo(Modelo modelo) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("centrales por idModelo");
        }
        return centralDao.getCentralesActivasByModelo(modelo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<VCatalogoCentral> findAllCentrales(FiltroBusquedaCentrales pFiltrosCentral) {
        return centralDao.findAllCentrales(pFiltrosCentral);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int findAllCentralesCount(FiltroBusquedaCentrales pFiltrosCentral) {
        return centralDao.findAllCentralesCount(pFiltrosCentral);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Central saveCentral(Central central) throws Exception {
        // Se hace la Auditoría del registro
        central.updateAuditableValues(usuariosService.getCurrentUser());
        return centralDao.saveOrUpdate(central);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void bajaCentral(Central central) throws Exception {
        Estatus estado = new Estatus();
        estado.setCdg(Estatus.INACTIVO);
        central.setEstatus(estado);
        // Se hace la Auditoría del registro
        central.updateAuditableValues(usuariosService.getCurrentUser());
        centralDao.saveOrUpdate(central);
    }

    @Override
    public byte[] getExportConsultaCatalogoCentrales(FiltroBusquedaCentrales pfiltro) throws Exception {
        pfiltro.setUsarPaginacion(false);
        List<VCatalogoCentral> listado = findAllCentrales(pfiltro);

        ExportConsultaCatalogoCentral excca = new ExportConsultaCatalogoCentral(listado);
        ExportarExcel export = new ExportarExcel(parametroDao);

        return export.generarReporteExcel("Catálogo de Centrales", excca);
    }

    @Override
    public byte[] getExportConsultaMarcas(List<Marca> pListaMarca) throws Exception {
        ExportConsultaCatalogoMarcaModelo excca = new ExportConsultaCatalogoMarcaModelo(pListaMarca);
        ExportarExcel export = new ExportarExcel(parametroDao);

        return export.generarReporteExcel("Catálogo de Marcas", excca);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<ReporteCentralVm> findAllCentralesPorLocalidad(FiltroReporteadorCentrales filtro) {
        return centralDao.findAllCentralesPorLocalidad(filtro);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int findAllCentralesPorLocalidadCount(FiltroReporteadorCentrales pFiltrosCentral) {
        return centralDao.findAllCentralesPorLocalidadCount(pFiltrosCentral);
    }

    @Override
    public Central comprobarCentral(Central central) {

        return centralDao.comprobarCentral(central);
    }

	@Override
	public Central saveCentralFromAsignacion(Central central) throws Exception {
		 // Se hace la Auditoría del registro
        central.updateAuditableValues(usuariosService.getCurrentUser());
        //Persistimos la marca
        Marca marca=central.getModelo().getMarca();
        marca=marcaDao.saveOrUpdate(marca);
        central.getModelo().setMarca(marca);
        //Persistimos el modelo
        Modelo modelo=central.getModelo();
        modelo=modeloDao.saveOrUpdate(modelo);
        central.setModelo(modelo);        
        return centralDao.saveOrUpdate(central);
	}

}
