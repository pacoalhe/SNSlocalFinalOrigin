package mx.ift.sns.negocio;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import mx.ift.sns.dao.ng.IParametroDao;
import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.nng.RangoSerieNng;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.EstadoArea;
import mx.ift.sns.modelo.ot.EstadoNumeracion;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.MunicipioPK;
import mx.ift.sns.modelo.ot.NirNumeracion;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.ot.PoblacionNumeracion;
import mx.ift.sns.modelo.pnn.Plan;
import mx.ift.sns.modelo.port.NumeroPortado;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.modelo.usu.Usuario;
import mx.ift.sns.negocio.ng.ISeriesService;
import mx.ift.sns.negocio.nng.IClaveServicioService;
import mx.ift.sns.negocio.nng.ISeriesNngService;
import mx.ift.sns.negocio.num.INumeracionService;
import mx.ift.sns.negocio.num.model.Numero;
import mx.ift.sns.negocio.ot.IOrganizacionTerritorialService;
import mx.ift.sns.negocio.pnn.IPlanNumeracionService;
import mx.ift.sns.negocio.port.IPortabilidadService;
import mx.ift.sns.negocio.psts.IProveedoresService;
import mx.ift.sns.negocio.usu.IUsuariosService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fachada de la consulta pública.
 */
@Stateless(name = "ConsultaPublicaFacade", mappedName = "ConsultaPublicaFacade")
@Remote(IConsultaPublicaFacade.class)
public class ConsultaPublicaFacade implements IConsultaPublicaFacade {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultaPublicaFacade.class);

    /** Servicio organizacion territorial. */
    @EJB
    private IOrganizacionTerritorialService otService;

    /** Servicio de proveedores. */
    @EJB
    private IProveedoresService prServive;

    /** Servicio series. */
    @EJB
    private ISeriesService seriesService;

    /** Servicio numeracion. */
    @EJB
    private INumeracionService numeracionService;

    /** Servicio de Usuarios. */
    @EJB
    private IUsuariosService usuariosService;

    /** Servicio de Planes de numeración. */
    @EJB
    private IPlanNumeracionService planNumeracionService;

    /** Servicio de claves de numeración no geográfica. */
    @EJB
    private IClaveServicioService clavesServicioService;

    /** Servicio de rango serie numeración no geográfica. */
    @EJB
    private ISeriesNngService seriesNngService;

    /** Servicio de portabilidad. */
    @EJB
    private IPortabilidadService portabilidadService;
    
    /** DAO de parametros. */
    @Inject
    private IParametroDao parametroDao;

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
    public List<Poblacion> findAllPoblacionesLikeNombre(String cadena) {

        LOGGER.debug("");

        return otService.findAllPoblacionesLikeNombre(cadena);
    }

    @Override
    public List<String> findAllPoblacionesNameLikeNombre(String cadena) {

        return otService.findAllPoblacionesNameLikeNombre(cadena);
    }

    @Override
    public List<Proveedor> findAllConcesionariosByAbn(Abn abn) throws Exception {
        return prServive.findAllConcesionariosByAbn(abn);

    }

    @Override
    public List<Proveedor> findAllConcesionariosByPoblacion(Poblacion poblacion) throws Exception {
        return prServive.findAllConcesionariosByPoblacion(poblacion);
    }

    @Override
    public BigDecimal getTotalNumRangosAsignadosByPoblacion(String tipoRed, String tipoModalidad, Proveedor proveedor,
            Poblacion poblacion) throws Exception {
        return seriesService.getTotalNumRangosAsignadosByPoblacion(tipoRed, tipoModalidad, proveedor, poblacion);
    }

    @Override
    public BigDecimal getTotalNumRangosAsignadosByAbn(
            String tipoRed, String tipoModalidad, Abn abn, Proveedor proveedor) {
        return seriesService.getTotalNumRangosAsignadosByAbn(tipoRed, tipoModalidad, abn, proveedor);
    }

    @Override
    public Integer getTotalNumeracionAsignadaByEstado(Estado estado) {
        return seriesService.getTotalNumRangosAsignadosByEstado(estado);
    }

    @Override
    public Poblacion getPoblacionWithMaxNumAsignadaByAbn(Abn abn) throws Exception {

        return seriesService.getPoblacionWithMaxNumAsignadaByAbn(abn);
    }

    @Override
    public Abn getAbnByCodigoNir(String nir) throws Exception {

        return otService.getAbnByCodigoNir(nir);
    }

    @Override
    public Numero parseNumeracion(String numeracion) {
        return numeracionService.parseNumeracion(numeracion);
    }

    @Override
    public RangoSerie getRangoPertenece(Numero numero) {
        return seriesService.getRangoPertenece(numero);
    }

    @Override
    public List<Nir> findNirsNumeroLocal(Numero numeroLocal) {
        return seriesService.findNirsNumeroLocal(numeroLocal);
    }

    @Override
    public List<PoblacionNumeracion> getPoblacionesNumeracionByProveedor(Proveedor proveedorServ) {
        return seriesService.getPoblacionesNumeracionByProveedor(proveedorServ);
    }

    @Override
    public List<EstadoNumeracion> getEstadosNumeracionByProveedor(Proveedor proveedorServ) {
        return seriesService.getEstadosNumeracionByProveedor(proveedorServ);
    }

    @Override
    public List<NirNumeracion> getNirsNumeracionByProveedor(Proveedor proveedorServ) {
        return seriesService.getNirsNumeracionByProveedor(proveedorServ);
    }

    @Override
    public BigDecimal getNumeracionPoblacionByProveedor(Proveedor proveedorServ, Poblacion poblacion) {
        return seriesService.getNumeracionPoblacionByProveedor(proveedorServ, poblacion);
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<Poblacion> findAllPoblacionesByAbn(Abn pAbn) {
        return otService.findAllPoblacionesByAbn(pAbn);
    }

    @Override
    public List<Municipio> findAllMunicipiosByAbn(Abn pAbn) {
        return otService.findAllMunicipiosByAbn(pAbn);
    }

    @Override
    public boolean existsNir(String nir) {
        return otService.existsNir(nir);
    }

    @Override
    public String countAllPoblacionesByEstado(String estado) {
        return otService.countAllPoblacionesByEstado(estado);
    }

    @Override
    public List<Proveedor> findAllConcesionariosByEstado(Estado estado) {
        return prServive.findAllConcesionariosByEstado(estado);
    }

    @Override
    public List<Abn> findAbnInEstado(String estado) {
        return otService.findAbnInEstado(estado);
    }

    @Override
    public List<Nir> findAllNirByAbn(BigDecimal numAbn) {
        return otService.findAllNirByAbn(numAbn);
    }

    @Override
    public List<Abn> findAbnInMunicipio(String municipio, String estado) {
        return otService.findAbnInMunicipio(municipio, estado);
    }

    @Override
    public List<Nir> finAllNirInMunicipio(String municipio, String estado) {
        return otService.finAllNirInMunicipio(municipio, estado);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<Proveedor> findAllConcesionariosByMunicipio(Municipio municipio) {
        return prServive.findAllConcesionariosByMunicipio(municipio);
    }

    @Override
    public Integer getTotalNumRangosAsignadosByMunicipio(Municipio municipio) {
        return seriesService.getTotalNumRangosAsignadosByMunicipio(municipio);
    }

    @Override
    public Usuario findUsuario(String uid) {
        return usuariosService.findUsuario(uid);
    }

    @Override
    public List<String> findAllTipoPlanByRol(String idRol) {
        return planNumeracionService.findAllTipoPlanByRol(idRol);
    }

    @Override
    public List<Plan> findAllPlanByRol(String idRol) {
        return planNumeracionService.findAllPlanByRol(idRol);
    }

    @Override
    public Plan getPlanByTipo(String idTipo) {
        return planNumeracionService.getPlanByTipo(idTipo);
    }

    @Override
    public List<ClaveServicio> findAllClaveServicio() {
        return clavesServicioService.findAllClaveServicio();
    }

    @Override
    public EstadoRango getEstadoRangoByCodigo(String codigo) {
        return seriesService.getEstadoRangoByCodigo(codigo);
    }

    @Override
    public Proveedor getProveedorById(BigDecimal pIdProveedor) throws Exception {
        return prServive.getProveedorById(pIdProveedor);
    }

    @Override
    public RangoSerieNng getRangoSerie(BigDecimal pClaveServicio, BigDecimal pSna, String pNumInicial,
            Proveedor pAsignatario) {
        return seriesNngService.getRangoSerie(pClaveServicio, pSna, pNumInicial, pAsignatario);
    }

    @Override
    public RangoSerieNng getRangoSerieByFraccion(BigDecimal pClaveServicio, BigDecimal pSna, String pNumInicial,
            String pNumFinal, Proveedor pAsignatario) {
        return seriesNngService.getRangoSerieByFraccion(pClaveServicio, pSna, pNumInicial, pNumFinal, pAsignatario);
    }

    @Override
    public RangoSerieNng getRangoSeriePertenece(BigDecimal pClaveServicio, BigDecimal pSna, String pNumeracion) {
        return seriesNngService.getRangoSeriePertenece(pClaveServicio, pSna, pNumeracion);
    }

    @Override
    public List<ClaveServicio> findAllClaveServicioActivas() {
        return clavesServicioService.findAllClaveServicioActivas();
    }

    @Override
    public boolean existeEmailUsuario(String idUsuario, String email) {
        return usuariosService.existeEmailUsuario(idUsuario, email);
    }

    @Override
    public Plan getPlanByTipoAndClaveServicio(String idTipo, BigDecimal claveServicio) {
        return planNumeracionService.getPlanByTipoAndClaveServicio(idTipo, claveServicio);
    }

    @Override
    public Integer getTotalNumeracionAginadaProveedor(BigDecimal idPst) {
        return seriesService.getTotalNumeracionAginadaProveedor(idPst);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<Poblacion> findAllPoblacionesEstadoNumeracion(Estado estado) {
        return otService.findAllPoblacionesEstadoNumeracion(estado);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<Nir> findAllNirByPoblacion(Poblacion poblacion) {
        return otService.findAllNirByPoblacion(poblacion);
    }

    @Override
    public Nir getNirByCodigo(int cdgNir) {
        return seriesService.getNirByCodigo(cdgNir);
    }

    @Override
    public List<PoblacionNumeracion>
            findALLPoblacionesNumeracionByProveedorEstado(Proveedor proveedorServ, Estado estado) {
        return seriesService.findALLPoblacionesNumeracionByProveedorEstado(proveedorServ, estado);
    }

    @Override
    public List<PoblacionNumeracion>
            findALLPoblacionesNumeracionByProveedorNir(Proveedor proveedorServ, Nir nir) {
        return seriesService.findALLPoblacionesNumeracionByProveedorNir(proveedorServ, nir);
    }

    @Override
    public List<ClaveServicio> findAllClaveServicioActivasWeb() {
        return clavesServicioService.findAllClaveServicioActivasWeb();
    }

    @Override
    public Abn getAbnByPoblacion(Poblacion poblacion) {
        return otService.getAbnByPoblacion(poblacion);
    }

    @Override
    public List<Proveedor> findAllPrestadoresServicioBy(Nir nir, Abn abn, Poblacion poblacion, Municipio municipio,
            Estado estado) {
        return seriesService.findAllPrestadoresServicioBy(nir, abn, poblacion, municipio, estado);
    }

    @Override
    public List<EstadoArea> findAllAreaEstadoByEstado(Estado estado) {
        return otService.findAllAreaEstadoByEstado(estado);
    }

    @Override
    public Nir getNirById(BigDecimal id) throws Exception {
        return seriesService.getNirById(id);
    }

    @Override
    public List<Poblacion> findALLPoblacionesNumeracionAsignadaByNir(Nir nir) {
        return seriesService.findALLPoblacionesNumeracionAsignadaByNir(nir);
    }

    @Override
    public int findNumeracionesAsignadasNir(Nir nir) {
        return seriesService.findNumeracionesAsignadasNir(nir);
    }

    @Override
    public List<Poblacion> findALLPoblacionesNumeracionAsignadaByMunicipio(Municipio municipio) {
        return seriesService.findALLPoblacionesNumeracionAsignadaByMunicipio(municipio);
    }

    @Override
    public List<Municipio> findAllMunicipiosByNir(Nir nir, boolean pUseCache) {
        return otService.findAllMunicipiosByNir(nir, pUseCache);
    }

    @Override
    public List<Nir> findAllNirByEstado(Estado estado) {
        return otService.findAllNirByEstado(estado);
    }

    @Override
    public NumeroPortado findNumeroPortado(String numero) {
        return portabilidadService.findNumeroPortado(numero);
    }

    @Override
    public List<Proveedor> getProveedorByIDA(BigDecimal idaProveedor) {
        return prServive.getProveedorByIDA(idaProveedor);
    }

    @Override
    public List<Proveedor> getProveedorByIDO(BigDecimal idoProveedor) {
        return prServive.getProveedorByIDO(idoProveedor);
    }

    @Override
    public List<Proveedor> getProveedorByABC(BigDecimal abcProveedor) {
        return prServive.getProveedorByABC(abcProveedor);
    }

	@Override
	public String getActionPorted(String nameParameter) {
		return parametroDao.getParamByName(nameParameter);
		
	}

	@Override
    public Municipio findMunicipioByNombreAndEstado(String nombreMun, String nombreEst) throws Exception{
        return otService.findMunicipioByNombreAndEstado(nombreMun,nombreEst);
    }

    @Override
    public List<Poblacion> findPoblacionByNombreAndMunicipioAndEstado(String nombrePob, Municipio mun) {
        LOGGER.debug("");
        return otService.findPoblacionByNombreAndMunicipioAndEstado(nombrePob, mun);
    }

}
