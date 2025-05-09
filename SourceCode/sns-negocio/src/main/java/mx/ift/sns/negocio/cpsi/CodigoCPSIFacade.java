package mx.ift.sns.negocio.cpsi;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import mx.ift.sns.modelo.cpsi.CesionSolicitadaCpsi;
import mx.ift.sns.modelo.cpsi.CodigoCPSI;
import mx.ift.sns.modelo.cpsi.CpsiAsignado;
import mx.ift.sns.modelo.cpsi.CpsiUitEntregado;
import mx.ift.sns.modelo.cpsi.LiberacionSolicitadaCpsi;
import mx.ift.sns.modelo.cpsi.Linea1EstudioCPSI;
import mx.ift.sns.modelo.cpsi.SolicitudAsignacionCpsi;
import mx.ift.sns.modelo.cpsi.SolicitudCesionCpsi;
import mx.ift.sns.modelo.cpsi.SolicitudCpsiUit;
import mx.ift.sns.modelo.cpsi.SolicitudLiberacionCpsi;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCodigosCPSI;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCPSI;
import mx.ift.sns.modelo.pst.Contacto;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.ConfiguracionFacade;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.negocio.psts.IProveedoresService;

/**
 * Implementación del Facade de servicios para Códigos de Señalización Internacional.
 */
@Stateless(name = "CodigoCPSIFacade", mappedName = "CodigoCPSIFacade")
@Remote(ICodigoCPSIFacade.class)
public class CodigoCPSIFacade extends ConfiguracionFacade implements ICodigoCPSIFacade {

    /** Logger de la clase . */
    // private static final Logger LOGGER = LoggerFactory.getLogger(CodigoCPSIFacade.class);

    /** Servicio de Solicitudes CPSI. */
    @EJB
    private ISolicitudesCpsiService solicitudesCpsiService;

    /** Servicio de Proveedores. */
    @EJB
    private IProveedoresService proveedoresService;

    /** Servicio de Códigos CPSI. */
    @EJB
    private ICodigoCPSIService codigoCpsiService;

    /********************************************************************************************/

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Proveedor> findAllProveedores() throws Exception {
        return proveedoresService.findAllProveedores();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Proveedor> findAllProveedoresActivos() throws Exception {
        return proveedoresService.findAllProveedoresActivos();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Contacto> getRepresentantesLegales(String pTipoContacto, BigDecimal pIdProveedor) {
        return proveedoresService.getRepresentantesLegales(pTipoContacto, pIdProveedor);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<EstadoSolicitud> findAllEstadosSolicitud() {
        return solicitudesCpsiService.findAllEstadosSolicitud();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<CodigoCPSI> findAllCodigosCPSI(FiltroBusquedaCodigosCPSI pFiltro) {
        return codigoCpsiService.findAllCodigosCPSI(pFiltro);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<CodigoCPSI> findAllCodigosCPSIForAnalisis(Proveedor pProveedor) {
        return codigoCpsiService.findAllCodigosCPSIForAnalisis(pProveedor);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public CodigoCPSI getCodigoCpsi(BigDecimal pIdCodigo, Proveedor pProveedor) {
        return codigoCpsiService.getCodigoCpsi(pIdCodigo, pProveedor);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<SolicitudLiberacionCpsi> findAllSolicitudesLiberacion(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud) {
        return solicitudesCpsiService.findAllSolicitudesLiberacion(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public int findAllSolicitudesLiberacionCount(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud) {
        return solicitudesCpsiService.findAllSolicitudesLiberacionCount(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudLiberacionCpsi saveSolicitudLiberacion(SolicitudLiberacionCpsi pSolicitud) {
        return solicitudesCpsiService.saveSolicitudLiberacion(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public SolicitudLiberacionCpsi getSolicitudLiberacionById(BigDecimal pConsecutivo) {
        return solicitudesCpsiService.getSolicitudLiberacionById(pConsecutivo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudLiberacionCpsi getSolicitudLiberacionEagerLoad(SolicitudLiberacionCpsi pSolicitud) {
        return solicitudesCpsiService.getSolicitudLiberacionEagerLoad(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudLiberacionCpsi applyLiberacionesSolicitadas(SolicitudLiberacionCpsi pSolicitud) throws Exception {
        return solicitudesCpsiService.applyLiberacionesSolicitadas(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelSolicitudLiberacion(SolicitudLiberacionCpsi pSolicitud) throws Exception {
        return solicitudesCpsiService.cancelSolicitudLiberacion(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelLiberacion(LiberacionSolicitadaCpsi pLibSol, boolean pUseCheck) throws Exception {
        return solicitudesCpsiService.cancelLiberacion(pLibSol, pUseCheck);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<String> getNotificacionesLoginLiberacion() {
        return solicitudesCpsiService.getNotificacionesLiberacionesPendientes();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<SolicitudCesionCpsi> findAllSolicitudesCesion(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud) {
        return solicitudesCpsiService.findAllSolicitudesCesion(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public int findAllSolicitudesCesionCount(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud) {
        return solicitudesCpsiService.findAllSolicitudesCesionCount(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public SolicitudCesionCpsi getSolicitudCesionById(BigDecimal pConsecutivo) {
        return solicitudesCpsiService.getSolicitudCesionById(pConsecutivo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudCesionCpsi saveSolicitudCesion(SolicitudCesionCpsi pSolicitud) {
        return solicitudesCpsiService.saveSolicitudCesion(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudCesionCpsi getSolicitudCesionEagerLoad(SolicitudCesionCpsi pSolicitud) {
        return solicitudesCpsiService.getSolicitudCesionEagerLoad(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudCesionCpsi applyCesionesSolicitadas(SolicitudCesionCpsi pSolicitud) throws Exception {
        return solicitudesCpsiService.applyCesionesSolicitadas(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelSolicitudCesion(SolicitudCesionCpsi pSolicitud) throws Exception {
        return solicitudesCpsiService.cancelSolicitudCesion(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelCesion(CesionSolicitadaCpsi pCesSol, boolean pUseCheck) throws Exception {
        return solicitudesCpsiService.cancelCesion(pCesSol, pUseCheck);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<String> getNotificacionesLoginCesion() {
        return solicitudesCpsiService.getNotificacionesCesionesPendientes();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<SolicitudAsignacionCpsi> findAllSolicitudesAsignacion(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud) {
        return solicitudesCpsiService.findAllSolicitudesAsignacion(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public int findAllSolicitudesAsignacionCount(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud) {
        return solicitudesCpsiService.findAllSolicitudesAsignacionCount(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudAsignacionCpsi saveSolicitudAsignacion(SolicitudAsignacionCpsi pSolicitud) {
        return solicitudesCpsiService.saveSolicitudAsignacion(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public SolicitudAsignacionCpsi getSolicitudAsignacionById(BigDecimal pConsecutivo) {
        return solicitudesCpsiService.getSolicitudAsignacionById(pConsecutivo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudAsignacionCpsi getSolicitudAsignacionEagerLoad(SolicitudAsignacionCpsi pSolicitud) {
        return solicitudesCpsiService.getSolicitudAsignacionEagerLoad(pSolicitud);
    }

    @Override
    public List<DetalleConsultaGenerica> findAllSolicitudes(FiltroBusquedaSolicitudesCPSI filtros) {
        return solicitudesCpsiService.findAllSolicitudes(filtros);
    }

    @Override
    public Integer findAllSolicitudesCount(FiltroBusquedaSolicitudesCPSI filtros) {
        return solicitudesCpsiService.findAllSolicitudesCount(filtros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Linea1EstudioCPSI getEstudioCpsiProveedor(Proveedor pProveedor) {
        return codigoCpsiService.getEstudioCpsiProveedor(pProveedor);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudAsignacionCpsi applyAsignacionesSolicitadas(SolicitudAsignacionCpsi pSolicitud) throws Exception {
        return solicitudesCpsiService.applyAsignacionesSolicitadas(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelSolicitudAsignacion(SolicitudAsignacionCpsi pSolicitud) throws Exception {
        return solicitudesCpsiService.cancelSolicitudAsignacion(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelAsignacion(CpsiAsignado pCpsiAsig, boolean pUseCheck) throws Exception {
        return solicitudesCpsiService.cancelAsignacion(pCpsiAsig, pUseCheck);
    }

    @Override
    public List<TipoSolicitud> findAllTiposSolicitud() {
        return solicitudesCpsiService.findAllTiposSolicitud();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudCpsiUit saveSolicitudCpsiUit(SolicitudCpsiUit pSolicitud) {
        return solicitudesCpsiService.saveSolicitudCpsiUit(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public int findAllSolicitudesCpsiUitCount(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud) {
        return solicitudesCpsiService.findAllSolicitudesCpsiUitCount(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<SolicitudCpsiUit> findAllSolicitudesCpsiUit(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud) {
        return solicitudesCpsiService.findAllSolicitudesCpsiUit(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudCpsiUit getSolicitudCpsiUitEagerLoad(SolicitudCpsiUit pSolicitud) {
        return solicitudesCpsiService.getSolicitudCpsiUitEagerLoad(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public SolicitudCpsiUit getSolicitudCpsiUitById(BigDecimal pConsecutivo) {
        return solicitudesCpsiService.getSolicitudCpsiUitById(pConsecutivo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public int findSolicitudCpsiUitByCodAndEstatusCount(FiltroBusquedaSolicitudesCPSI filtro) {
        return solicitudesCpsiService.findSolicitudCpsiUitByCodAndEstatusCount(filtro);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelSolicitud(CpsiUitEntregado pCpsiUitEnt, boolean pUseCheck) throws Exception {
        return solicitudesCpsiService.cancelSolicitud(pCpsiUitEnt, pUseCheck);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudCpsiUit applySolicitudCpsiUit(SolicitudCpsiUit pSolicitud) throws Exception {
        return solicitudesCpsiService.applySolicitudCpsiUit(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelSolicitud(SolicitudCpsiUit pSolicitud) throws Exception {
        return solicitudesCpsiService.cancelSolicitud(pSolicitud);
    }
}
