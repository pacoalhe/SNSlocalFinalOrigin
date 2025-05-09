package mx.ift.sns.negocio.oficios;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import mx.ift.sns.dao.ng.IParametroDao;
import mx.ift.sns.dao.oficios.IOficioBlobDao;
import mx.ift.sns.dao.oficios.IOficioDao;
import mx.ift.sns.dao.oficios.IPlantillaDao;
import mx.ift.sns.dao.oficios.ITipoDestinatarioDao;
import mx.ift.sns.modelo.filtros.FiltroBusquedaPlantillas;
import mx.ift.sns.modelo.oficios.Oficio;
import mx.ift.sns.modelo.oficios.OficioBlob;
import mx.ift.sns.modelo.oficios.Plantilla;
import mx.ift.sns.modelo.oficios.PlantillaPK;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.ac.ExportConsultaCatalogoPlantillas;
import mx.ift.sns.negocio.conf.IParametrosService;
import mx.ift.sns.negocio.cpsi.ICodigoCPSIService;
import mx.ift.sns.negocio.nng.ISeriesNngService;
import mx.ift.sns.negocio.usu.IUsuariosService;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;

/** Servicio para Generación de Oficios. */
@Stateless(mappedName = "OficiosService")
@Remote(IOficiosService.class)
public class OficiosService implements IOficiosService {

    /** Logger de la clase. */
    // private static final Logger LOGGER = LoggerFactory.getLogger(OficiosService.class);

    /** Servicio parametros. */
    @EJB
    private IParametrosService paramService;

    /** Servicio de usuarios. */
    @EJB
    private IUsuariosService usuariosService;

    /** Servicio de Códigos CPSI. */
    @EJB
    private ICodigoCPSIService codigosCpsiService;

    /** Servicio de Series de Numeración No Geográfica. */
    @EJB
    private ISeriesNngService seriesNngService;

    /** DAO para Generación de Oficios. */
    @Inject
    private IOficioDao oficioDAO;

    /** DAO de Plantillas de Oficios. */
    @Inject
    private IPlantillaDao plantillaDAO;

    /** DAO para Generación de BLOBS. */
    @Inject
    private IOficioBlobDao oficioBlobDAO;

    /** DAO para los Tipos de Destinatario. */
    @Inject
    private ITipoDestinatarioDao tipoDestinatarioDAO;

    /** DAO Rangos Serie. */
    @Inject
    private IParametroDao parametroDao;

    @Override
    public Oficio crearOficio(ParametrosOficio pParametros) throws Exception {

        // Nuevo objeto oficio
        Oficio oficio = new Oficio();
        oficio.setNumOficio(pParametros.getNumOficio());
        oficio.setSolicitud(pParametros.getSolicitud());
        oficio.setTipoDestinatario(pParametros.getTipoDestinatario());
        oficio.setFechaOficio(pParametros.getFechaOficio());

        // Plantilla
        Plantilla plantilla = plantillaDAO.getPlantilla(
                pParametros.getSolicitud().getTipoSolicitud(),
                pParametros.getTipoDestinatario());

        // Oficio
        ITFGeneradorOficio generadorOficio = GeneradorOficio.getGeneradorOficio(pParametros);

        // Asociamos el Servicio de Parámetros necesario para algunos textos.
        generadorOficio.setParamService(paramService);

        // Asociamos el Servicio de Series de Numeración No Gegráfica para los oficios que requieren usar el histórico
        // de Series NNG.
        generadorOficio.setSeriesNngService(seriesNngService);

        // Asociamos el Servicio de Códigos CPSI para los oficios de Solicitudes UIT.
        generadorOficio.setCodigosCpsiService(codigosCpsiService);

        // Creamos el objeto OficioBlob con el documento serializado.
        OficioBlob oficioBlob = new OficioBlob();
        oficioBlob.setDocumento(generadorOficio.getDocumentoOficio(plantilla.getPlantilla()));
        oficioBlob = this.saveOficioBlob(oficioBlob);

        // El oficio se guarda al guardar la solicitud.
        oficio.setIdOficioBlob(oficioBlob.getId());
        return oficio;
    }

    @Override
    public Oficio actualizarOficio(ParametrosOficio pParametros) throws Exception {

        // Plantilla
        Plantilla plantilla = plantillaDAO.getPlantilla(
                pParametros.getOficio().getSolicitud().getTipoSolicitud(),
                pParametros.getOficio().getTipoDestinatario());

        // Oficio
        ITFGeneradorOficio generadorOficio = GeneradorOficio.getGeneradorOficio(pParametros);

        // Asociamos el Servicio de Parámetros necesario para algunos textos.
        generadorOficio.setParamService(paramService);

        // Asociamos el Servicio de Series de Numeración No Gegráfica para los oficios que requieren usar el histórico
        // de Series NNG.
        generadorOficio.setSeriesNngService(seriesNngService);

        // Asociamos el Servicio de Códigos CPSI para los oficios de Solicitudes UIT.
        generadorOficio.setCodigosCpsiService(codigosCpsiService);

        OficioBlob oficioBlob = oficioBlobDAO.getOficioBlob(pParametros.getOficio().getIdOficioBlob());
        oficioBlob.setDocumento(generadorOficio.getDocumentoOficio(plantilla.getPlantilla()));
        oficioBlob = this.saveOficioBlob(oficioBlob);

        // Al guardar la solicitud se guarda la información del oficio.
        return pParametros.getOficio();
    }

    @Override
    public Oficio getOficio(BigDecimal pCodigoOficio) {
        return oficioDAO.getOficio(pCodigoOficio);
    }

    @Override
    public Oficio getOficio(Solicitud pSolicitud, String pCdgDestinatario) {
        TipoDestinatario dest = new TipoDestinatario();
        dest.setCdg(pCdgDestinatario);
        return oficioDAO.getOficio(pSolicitud, dest);
    }

    @Override
    public void guardarPlantilla(TipoSolicitud pTipoSolicitud,
            TipoDestinatario pTipoDestinatario, byte[] pPlantillaSerializada, String pDescripcion) throws Exception {

        // Identificador de Plantilla
        PlantillaPK plantillaPK = new PlantillaPK();
        plantillaPK.setCdgTipoDestinatario(pTipoDestinatario.getCdg());
        plantillaPK.setCdgTipoSolicitud(pTipoSolicitud.getCdg());

        Plantilla plantilla = new Plantilla();
        plantilla.setId(plantillaPK);
        plantilla.setPlantilla(pPlantillaSerializada);
        plantilla.setTipoDestinatario(pTipoDestinatario);
        plantilla.setTipoSolicitud(pTipoSolicitud);
        plantilla.setDescripcion(pDescripcion);

        plantilla = this.savePlantilla(plantilla);
    }

    @Override
    public byte[] descargarPlantilla(TipoSolicitud pTipoSolicitud,
            TipoDestinatario pTipoDestinatario) throws Exception {
        return plantillaDAO.getPlantilla(pTipoSolicitud, pTipoDestinatario).getPlantilla();
    }

    @Override
    public void removePlantilla(TipoSolicitud pTipoSolicitud, TipoDestinatario pTipoDestinatario) throws Exception {

        // Identificador de Plantilla
        PlantillaPK plantillaPK = new PlantillaPK();
        plantillaPK.setCdgTipoDestinatario(pTipoDestinatario.getCdg());
        plantillaPK.setCdgTipoSolicitud(pTipoSolicitud.getCdg());

        Plantilla plantilla = new Plantilla();
        plantilla.setId(plantillaPK);
        plantilla.setTipoDestinatario(pTipoDestinatario);
        plantilla.setTipoSolicitud(pTipoSolicitud);

        this.removePlantilla(plantilla);
    }

    @Override
    public List<TipoDestinatario> findAllTiposDestinatario() {
        return tipoDestinatarioDAO.findAllTiposDestinatario();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TipoDestinatario getTipoDestinatarioByCdg(String pCdgDestinatario) {
        return tipoDestinatarioDAO.getTipoDestinatarioByCdg(pCdgDestinatario);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int getOficiosCount() {
        return oficioDAO.getOficiosCount();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Oficio getOficioByNumOficio(String numOficio, TipoDestinatario pTipoDestinatario) {
        return oficioDAO.getOficioByNumOficio(numOficio, pTipoDestinatario);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Oficio saveOficio(Oficio pOficio) {
        return oficioDAO.saveOrUpdate(pOficio);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public OficioBlob getOficioBlob(BigDecimal pOficioBlobId) {
        return oficioBlobDAO.getOficioBlob(pOficioBlobId);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public OficioBlob saveOficioBlob(OficioBlob pOficioBlob) {
        return oficioBlobDAO.saveOrUpdate(pOficioBlob);
    }

    @Override
    public List<Plantilla> findAllPlantillas() {
        return plantillaDAO.findAllPlantillas();
    }

    @Override
    public List<Plantilla> findAllPlantillas(FiltroBusquedaPlantillas pFiltros) {
        return plantillaDAO.findAllPlantillas(pFiltros);
    }

    @Override
    public int findAllPlantillasCount(FiltroBusquedaPlantillas pFiltros) {
        return plantillaDAO.findAllPlantillasCount(pFiltros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Plantilla savePlantilla(Plantilla pPlantilla) {
        // Se hace la Auditoría del registro
        pPlantilla.updateAuditableValues(usuariosService.getCurrentUser());
        return plantillaDAO.saveOrUpdate(pPlantilla);
    }

    @Override
    public Plantilla getPlantilla(TipoSolicitud pTipoSolicitud, TipoDestinatario pTipoDestinatario) {
        return plantillaDAO.getPlantilla(pTipoSolicitud, pTipoDestinatario);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removePlantilla(Plantilla pPlantilla) {
        plantillaDAO.delete(pPlantilla);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public byte[] getExportConsultaCatalogoPlantillas(List<Plantilla> pListaPlantillas) throws Exception {
        ExportConsultaCatalogoPlantillas eccp = new ExportConsultaCatalogoPlantillas(pListaPlantillas);
        ExportarExcel export = new ExportarExcel(parametroDao);
        return export.generarReporteExcel("Catálogo de Plantillas de Oficios", eccp);
    }

    @Override
    public boolean existeNumeroOficio(String numeroOficio) {
        return oficioDAO.existeNumeroOficio(numeroOficio);
    }

    @Override
    public List<Oficio> getOficiosBySolicitud(BigDecimal idSolicitud) {
        return oficioDAO.getOficiosBySolicitud(idSolicitud);
    }

}
