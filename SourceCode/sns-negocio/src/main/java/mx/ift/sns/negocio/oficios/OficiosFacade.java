package mx.ift.sns.negocio.oficios;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import mx.ift.sns.modelo.cpsi.SolicitudAsignacionCpsi;
import mx.ift.sns.modelo.cpsi.SolicitudCesionCpsi;
import mx.ift.sns.modelo.cpsi.SolicitudCpsiUit;
import mx.ift.sns.modelo.cpsi.SolicitudLiberacionCpsi;
import mx.ift.sns.modelo.cpsn.SolicitudAsignacionCpsn;
import mx.ift.sns.modelo.cpsn.SolicitudCesionCPSN;
import mx.ift.sns.modelo.cpsn.SolicitudLiberacionCpsn;
import mx.ift.sns.modelo.ng.SolicitudAsignacion;
import mx.ift.sns.modelo.ng.SolicitudCesionNg;
import mx.ift.sns.modelo.ng.SolicitudLiberacionNg;
import mx.ift.sns.modelo.ng.SolicitudRedistribucionNg;
import mx.ift.sns.modelo.nng.SolicitudAsignacionNng;
import mx.ift.sns.modelo.nng.SolicitudCesionNng;
import mx.ift.sns.modelo.nng.SolicitudLiberacionNng;
import mx.ift.sns.modelo.nng.SolicitudRedistribucionNng;
import mx.ift.sns.modelo.oficios.Oficio;
import mx.ift.sns.modelo.oficios.OficioBlob;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.negocio.conf.IParametrosService;
import mx.ift.sns.negocio.cpsi.ISolicitudesCpsiService;
import mx.ift.sns.negocio.cpsn.ISolicitudesCpsnService;
import mx.ift.sns.negocio.ng.ISolicitudesService;
import mx.ift.sns.negocio.nng.ISolicitudesNngService;

/**
 * Facade de métodos comunes para la generación de oficios.
 * @author X53490DE
 */
@Stateless(name = "OficiosFacade", mappedName = "OficiosFacade")
@Remote(IOficiosFacade.class)
public class OficiosFacade implements IOficiosFacade {

    /** Logger de la clase. */
    // private static final Logger LOGGER = LoggerFactory.getLogger(OficiosFacade.class);

    /** Servicio Oficios. */
    @EJB(name = "OficiosService")
    private IOficiosService oficiosService;

    /** Servicio de Solicitudes Numeración Geográfica. */
    @EJB(name = "SolicitudesService")
    private ISolicitudesService solicitudesNgService;

    /** Servicio de Solicitudes Numeración No Geográfica. */
    @EJB(name = "SolicitudesNngService")
    private ISolicitudesNngService solicitudesNngService;

    /** Servicio de Códigos CPSN. */
    @EJB(name = "SolicitudesCpsnService")
    private ISolicitudesCpsnService solicitudesCpsnService;

    /** Servicio de Códigos CPSI. */
    @EJB(name = "SolicitudesCpsiService")
    private ISolicitudesCpsiService solicitudesCpsiService;

    /** Servicio parametros. */
    @EJB(name = "ParametrosService")
    private IParametrosService paramService;

    /*************************************************************************************/

    @Override
    public String getParamByName(String name) {
        return paramService.getParamByName(name);
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
    public Oficio crearOficio(ParametrosOficio pParametros) throws Exception {
        return oficiosService.crearOficio(pParametros);
    }

    @Override
    public Oficio getOficio(Solicitud pSolicitud, String pCdgTipoDestinatario) {
        return oficiosService.getOficio(pSolicitud, pCdgTipoDestinatario);
    }

    @Override
    public Oficio saveOficio(Oficio pOficio) {
        return oficiosService.saveOficio(pOficio);
    }

    @Override
    public OficioBlob getOficioBlob(BigDecimal pOficioBlobId) {
        return oficiosService.getOficioBlob(pOficioBlobId);
    }

    @Override
    public OficioBlob saveOficioBlob(OficioBlob pOficioBlob) {
        return oficiosService.saveOficioBlob(pOficioBlob);
    }

    @Override
    public Oficio getOficioByNumOficio(String numOficio, TipoDestinatario pTipoDestinatario) {
        return oficiosService.getOficioByNumOficio(numOficio, pTipoDestinatario);
    }

    @Override
    public Oficio actualizarOficio(ParametrosOficio pParametros) throws Exception {
        return oficiosService.actualizarOficio(pParametros);
    }

    @Override
    public boolean existeNumeroOficio(String numeroOficio) {
        return oficiosService.existeNumeroOficio(numeroOficio);
    }

    @Override
    public SolicitudAsignacion saveSolicitudAsignacion(SolicitudAsignacion solicitudAsignacion) throws Exception {
        return solicitudesNgService.saveSolicitudAsignacion(solicitudAsignacion);
    }

    @Override
    public SolicitudCesionNg saveSolicitudCesion(SolicitudCesionNg solicitudCesion) throws Exception {
        return solicitudesNgService.saveSolicitudCesion(solicitudCesion);
    }

    @Override
    public SolicitudLiberacionNg saveSolicitudLiberacion(SolicitudLiberacionNg pSolicitud) throws Exception {
        return solicitudesNgService.saveSolicitudLiberacion(pSolicitud);
    }

    @Override
    public SolicitudRedistribucionNg saveSolicitudRedistribucion(SolicitudRedistribucionNg pSolicitud) {
        return solicitudesNgService.saveSolicitudRedistribucion(pSolicitud);
    }

    @Override
    public SolicitudCesionNng saveSolicitudCesion(SolicitudCesionNng pSolicitudCesion) {
        return solicitudesNngService.saveSolicitudCesion(pSolicitudCesion);
    }

    @Override
    public SolicitudAsignacionNng saveSolicitudAsignacion(SolicitudAsignacionNng pSolicitud) {
        return solicitudesNngService.saveSolicitudAsignacion(pSolicitud);
    }

    @Override
    public SolicitudLiberacionNng saveSolicitudLiberacion(SolicitudLiberacionNng pSolicitud) {
        return solicitudesNngService.saveSolicitudLiberacion(pSolicitud);
    }

    @Override
    public SolicitudRedistribucionNng saveSolicitudRedistribucion(SolicitudRedistribucionNng pSolicitud) {
        return solicitudesNngService.saveSolicitudRedistribucion(pSolicitud);
    }

    @Override
    public SolicitudLiberacionCpsn saveSolicitudLiberacion(SolicitudLiberacionCpsn pSolicitud) {
        return solicitudesCpsnService.saveSolicitudLiberacion(pSolicitud);
    }

    @Override
    public SolicitudAsignacionCpsn saveSolicitudAsignacion(SolicitudAsignacionCpsn solicitudAsignacion)
            throws Exception {
        return solicitudesCpsnService.saveSolicitudAsignacion(solicitudAsignacion);
    }

    @Override
    public SolicitudCesionCPSN saveSolicitudCesion(SolicitudCesionCPSN solicitudCesion) throws Exception {
        return solicitudesCpsnService.saveSolicitudCesion(solicitudCesion);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudLiberacionCpsi saveSolicitudLiberacion(SolicitudLiberacionCpsi pSolicitud) {
        return solicitudesCpsiService.saveSolicitudLiberacion(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudAsignacionCpsi saveSolicitudAsignacion(SolicitudAsignacionCpsi pSolicitud) {
        return solicitudesCpsiService.saveSolicitudAsignacion(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudCesionCpsi saveSolicitudCesion(SolicitudCesionCpsi pSolicitud) {
        return solicitudesCpsiService.saveSolicitudCesion(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudCpsiUit saveSolicitudCpsiUit(SolicitudCpsiUit pSolicitud) {
        return solicitudesCpsiService.saveSolicitudCpsiUit(pSolicitud);
    }

}
