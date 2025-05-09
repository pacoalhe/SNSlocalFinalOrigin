package mx.ift.sns.negocio.cpsn;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

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
import mx.ift.sns.negocio.ConfiguracionFacade;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.negocio.oficios.IOficiosService;
import mx.ift.sns.negocio.psts.IProveedoresService;

/**
 * Implementación del Facade de servicios para Códigos de Señalización Nacional.
 */
@Stateless(name = "CodigoCPSNFacade", mappedName = "CodigoCPSNFacade")
@Remote(ICodigoCPSNFacade.class)
public class CodigoCPSNFacade extends ConfiguracionFacade implements ICodigoCPSNFacade {

    /** Logger de la clase. */
    // private static final Logger LOGGER = LoggerFactory.getLogger(CodigoCPSNFacade.class);

    /** Servicio de Proveedores. */
    @EJB
    private IProveedoresService proveedoresService;

    /** Service de Oficios. */
    @EJB
    private IOficiosService oficiosService;

    /** Service de Oficios. */
    @EJB
    private ISolicitudesCpsnService solicitudesCpsnService;

    /** Service de Códigos CPSN. */
    @EJB
    private ICodigoCPSNService codigoCpsnService;

    /** Servicio de equipos de señalización. */
    @EJB
    private IEquipoSenalizacionCPSNService equipoSenalService;

    /*************************************************************************************************/

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<Proveedor> findAllProveedores() throws Exception {
        return proveedoresService.findAllProveedores();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<Proveedor> findAllProveedoresActivos() throws Exception {
        return proveedoresService.findAllProveedoresActivos();
    }

    @Override
    public CodigoCPSN getCodigoCpsn(String pIdTipoBloque, BigDecimal pIdCodigo, Proveedor pProveedor) {
        return codigoCpsnService.getCodigoCpsn(pIdTipoBloque, pIdCodigo, pProveedor);
    }

    @Override
    public List<EstadoSolicitud> findAllEstadosSolicitud() {
        return solicitudesCpsnService.findAllEstadosSolicitud();
    }

    @Override
    public int findAllSolicitudesAsignacionCount(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud) throws Exception {
        return solicitudesCpsnService.findAllSolicitudesAsignacionCount(pFiltrosSolicitud);
    }

    @Override
    public List<SolicitudAsignacionCpsn> findAllSolicitudesAsignacion(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud)
            throws Exception {
        return solicitudesCpsnService.findAllSolicitudesAsignacion(pFiltrosSolicitud);
    }

    @Override
    public List<Contacto> getRepresentantesLegales(String pTipoContacto, BigDecimal pIdProveedor) {
        return proveedoresService.getRepresentantesLegales(pTipoContacto, pIdProveedor);
    }

    @Override
    public SolicitudAsignacionCpsn saveSolicitudAsignacion(SolicitudAsignacionCpsn solicitudAsignacion)
            throws Exception {
        return solicitudesCpsnService.saveSolicitudAsignacion(solicitudAsignacion);
    }

    @Override
    public SolicitudAsignacionCpsn getSolicitudAsignacionEagerLoad(SolicitudAsignacionCpsn pSolicitud)
            throws Exception {
        return solicitudesCpsnService.getSolicitudAsignacionEagerLoad(pSolicitud);
    }

    @Override
    public SolicitudAsignacionCpsn getSolicitudAsignacionById(BigDecimal pConsecutivo) {
        return solicitudesCpsnService.getSolicitudAsignacionById(pConsecutivo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<SolicitudLiberacionCpsn> findAllSolicitudesLiberacion(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud) {
        return solicitudesCpsnService.findAllSolicitudesLiberacion(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public int findAllSolicitudesLiberacionCount(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud) {
        return solicitudesCpsnService.findAllSolicitudesLiberacionCount(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudLiberacionCpsn saveSolicitudLiberacion(SolicitudLiberacionCpsn pSolicitud) {
        return solicitudesCpsnService.saveSolicitudLiberacion(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public SolicitudLiberacionCpsn getSolicitudLiberacionById(BigDecimal pConsecutivo) {
        return solicitudesCpsnService.getSolicitudLiberacionById(pConsecutivo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudLiberacionCpsn getSolicitudLiberacionEagerLoad(SolicitudLiberacionCpsn pSolicitud) {
        return solicitudesCpsnService.getSolicitudLiberacionEagerLoad(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudLiberacionCpsn applyLiberacionesSolicitadas(SolicitudLiberacionCpsn pSolicitud) throws Exception {
        return solicitudesCpsnService.applyLiberacionesSolicitadas(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelSolicitudLiberacion(SolicitudLiberacionCpsn pSolicitud) throws Exception {
        return solicitudesCpsnService.cancelSolicitudLiberacion(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelLiberacion(LiberacionSolicitadaCpsn pLibSol, boolean pUseCheck) throws Exception {
        return solicitudesCpsnService.cancelLiberacion(pLibSol, pUseCheck);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<String> getNotificacionesLoginLiberacion() {
        return solicitudesCpsnService.getNotificacionesLiberacionesPendientes();
    }

    @Override
    public int findAllSolicitudesCesionCount(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud) {
        return solicitudesCpsnService.findAllSolicitudesCesionCount(pFiltrosSolicitud);
    }

    @Override
    public List<SolicitudCesionCPSN> findAllSolicitudesCesion(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud) {
        return solicitudesCpsnService.findAllSolicitudesCesion(pFiltrosSolicitud);
    }

    @Override
    public SolicitudCesionCPSN getSolicitudCesionById(BigDecimal pConsecutivo) {
        return solicitudesCpsnService.getSolicitudCesionById(pConsecutivo);
    }

    @Override
    public SolicitudCesionCPSN saveSolicitudCesion(SolicitudCesionCPSN pSolicitud) throws Exception {
        return solicitudesCpsnService.saveSolicitudCesion(pSolicitud);
    }

    @Override
    public SolicitudCesionCPSN getSolicitudCesionEagerLoad(SolicitudCesionCPSN pSolicitud) throws Exception {
        return solicitudesCpsnService.getSolicitudCesionEagerLoad(pSolicitud);
    }

    @Override
    public List<TipoBloqueCPSN> findAllTiposBloqueCPSN() {
        return codigoCpsnService.findAllTiposBloqueCPSN();
    }

    @Override
    public List<CodigoCPSN> findCodigosCPSN(FiltroBusquedaCodigosCPSN pFiltro) {
        return codigoCpsnService.findCodigosCPSN(pFiltro);
    }

    @Override
    public List<EstudioEquipoCPSN> estudioEquipoCPSN(Proveedor pst) {
        return equipoSenalService.estudioEquipoCPSN(pst);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudCesionCPSN applyCesionesSolicitadas(SolicitudCesionCPSN pSolicitud) throws Exception {
        return solicitudesCpsnService.applyCesionesSolicitadas(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelSolicitudCesion(SolicitudCesionCPSN pSolicitud) throws Exception {
        return solicitudesCpsnService.cancelSolicitudCesion(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelCesion(CesionSolicitadaCPSN pCesSol, boolean pUseCheck) throws Exception {
        return solicitudesCpsnService.cancelCesion(pCesSol, pUseCheck);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<String> getNotificacionesLoginCesion() {
        return solicitudesCpsnService.getNotificacionesCesionesPendientes();
    }

    @Override
    public List<DetalleConsultaGenerica> findAllSolicitudes(FiltroBusquedaSolicitudesCpsn filtros) {
        return solicitudesCpsnService.findAllSolicitudes(filtros);
    }

    @Override
    public Integer findAllSolicitudesCount(FiltroBusquedaSolicitudesCpsn filtros) {
        return solicitudesCpsnService.findAllSolicitudesCount(filtros);
    }

    @Override
    public List<TipoSolicitud> findAllTiposSolicitud() {
        return solicitudesCpsnService.findAllTiposSolicitud();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudAsignacionCpsn applyAsignacionesSolicitadas(SolicitudAsignacionCpsn pSolicitud) throws Exception {
        return solicitudesCpsnService.applyAsignacionesSolicitadas(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelAsignacion(NumeracionAsignadaCpsn pCpsnAsig, boolean pUseCheck) throws Exception {
        return solicitudesCpsnService.cancelAsignacion(pCpsnAsig, pUseCheck);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelSolicitudAsignacion(SolicitudAsignacionCpsn pSolicitud) throws Exception {
        return solicitudesCpsnService.cancelSolicitudAsignacion(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<CodigoCPSN> findAllCodigosCPSN(Proveedor pProveedor, TipoBloqueCPSN tipoBloque) {
        return codigoCpsnService.findAllCodigosCPSN(pProveedor, tipoBloque);
    }
}
