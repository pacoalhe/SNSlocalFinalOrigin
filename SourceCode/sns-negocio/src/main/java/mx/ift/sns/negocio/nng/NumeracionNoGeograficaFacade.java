package mx.ift.sns.negocio.nng;

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

import mx.ift.sns.modelo.filtros.FiltroBusquedaHistoricoRangos;
import mx.ift.sns.modelo.filtros.FiltroBusquedaHistoricoSeries;
import mx.ift.sns.modelo.filtros.FiltroBusquedaLineasActivas;
import mx.ift.sns.modelo.filtros.FiltroBusquedaRangos;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSeries;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.lineas.DetalleLineaArrendadorNng;
import mx.ift.sns.modelo.lineas.DetalleLineaArrendatarioNng;
import mx.ift.sns.modelo.lineas.DetalleReporteNng;
import mx.ift.sns.modelo.lineas.ReporteLineaActivaDetNng;
import mx.ift.sns.modelo.lineas.ReporteLineaActivaNng;
import mx.ift.sns.modelo.lineas.ReporteLineaArrendadorNng;
import mx.ift.sns.modelo.lineas.ReporteLineaArrendatarioNng;
import mx.ift.sns.modelo.lineas.TipoReporte;
import mx.ift.sns.modelo.nng.CesionSolicitadaNng;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.nng.HistoricoRangoSerieNng;
import mx.ift.sns.modelo.nng.LiberacionSolicitadaNng;
import mx.ift.sns.modelo.nng.NumeracionAsignadaNng;
import mx.ift.sns.modelo.nng.NumeracionSolicitadaNng;
import mx.ift.sns.modelo.nng.RangoSerieNng;
import mx.ift.sns.modelo.nng.RedistribucionSolicitadaNng;
import mx.ift.sns.modelo.nng.SerieNng;
import mx.ift.sns.modelo.nng.SerieNngPK;
import mx.ift.sns.modelo.nng.SolicitudAsignacionNng;
import mx.ift.sns.modelo.nng.SolicitudCesionNng;
import mx.ift.sns.modelo.nng.SolicitudLiberacionNng;
import mx.ift.sns.modelo.nng.SolicitudLineasActivasNng;
import mx.ift.sns.modelo.nng.SolicitudRedistribucionNng;
import mx.ift.sns.modelo.nng.TipoAsignacion;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.pst.Contacto;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.ProveedorConvenio;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.series.HistoricoSerieNng;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.ConfiguracionFacade;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.negocio.ng.IGenerarAnalisisNumeracion;
import mx.ift.sns.negocio.ng.IValidacionFicheroReportes;
import mx.ift.sns.negocio.oficios.IOficiosService;
import mx.ift.sns.negocio.psts.IProveedoresService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio de numeracion no geografica.
 */
@Stateless(name = "NumeracionNoGeograficaFacade", mappedName = "NumeracionNoGeograficaFacade")
@Remote(INumeracionNoGeograficaFacade.class)
public class NumeracionNoGeograficaFacade extends ConfiguracionFacade implements INumeracionNoGeograficaFacade {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NumeracionNoGeograficaFacade.class);

    /** Servicio de Solicitudes NNG. */
    @EJB
    private ISolicitudesNngService solicitudesNngService;

    /** Servicio Generar Analis. */
    @EJB
    private IGenerarAnalisisNumeracion generarAnalisisService;

    /** Servicio de Proveedores. */
    @EJB
    private IProveedoresService proveedoresService;

    /** Servicio de Series NNG. */
    @EJB
    private ISeriesNngService seriesNngService;

    /** Service de Claves de Servicio. */
    @EJB
    private IClaveServicioService claveServicioService;

    /** Service de Oficios. */
    @EJB
    private IOficiosService oficiosService;

    /** Service de Reportes. */
    @EJB
    private IReportesNngService reportesService;

    /** Servicio de Validacion de archivo. */
    @EJB
    private IValidacionFicheroReportes validadorReportes;

    /*************************************************************************************************/

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<Proveedor> findAllProveedores() throws Exception {
        return proveedoresService.findAllProveedoresActivos();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<Proveedor> findAllProveedoresActivos() throws Exception {
        return proveedoresService.findAllProveedoresActivos();
    }

    @Override
    public List<EstadoSolicitud> findAllEstadosSolicitud() {
        return solicitudesNngService.findAllEstadosSolicitud();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudCesionNng saveSolicitudCesion(SolicitudCesionNng pSolicitudCesion) {
        return solicitudesNngService.saveSolicitudCesion(pSolicitudCesion);
    }

    @Override
    public SolicitudCesionNng getSolicitudCesionById(BigDecimal pConsecutivo) {
        return solicitudesNngService.getSolicitudCesionById(pConsecutivo);
    }

    @Override
    public SolicitudCesionNng getSolicitudCesionEagerLoad(SolicitudCesionNng pSolicitud) {
        return solicitudesNngService.getSolicitudCesionEagerLoad(pSolicitud);
    }

    @Override
    public List<SolicitudCesionNng> findAllSolicitudesCesion() {
        return solicitudesNngService.findAllSolicitudesCesion();
    }

    @Override
    public List<SolicitudCesionNng> findAllSolicitudesCesion(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudesNngService.findAllSolicitudesCesion(pFiltrosSolicitud);
    }

    @Override
    public int findAllSolicitudesCesionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudesNngService.findAllSolicitudesCesionCount(pFiltrosSolicitud);
    }

    @Override
    public List<Contacto> getRepresentantesLegales(String pTipoContacto, BigDecimal pIdProveedor) {
        return proveedoresService.getRepresentantesLegales(pTipoContacto, pIdProveedor);
    }

    @Override
    public List<Proveedor> findAllCesionarios(Proveedor pCedente) {
        return proveedoresService.findAllCesionarios(pCedente);
    }

    @Override
    public List<RangoSerieNng> findAllRangos(FiltroBusquedaRangos pFiltros) {
        return seriesNngService.findAllRangos(pFiltros);
    }

    @Override
    public int findAllRangosCount(FiltroBusquedaRangos pFiltros) {
        return seriesNngService.findAllRangosCount(pFiltros);
    }

    @Override
    public TipoProveedor getTipoProveedorByCdg(String pCdgTipo) {
        return proveedoresService.getTipoProveedorByCdg(pCdgTipo);
    }

    @Override
    public List<ProveedorConvenio> findAllConveniosByProveedor(Proveedor pComercializador, TipoRed pTipoRedConvenio) {
        return proveedoresService.findAllConveniosByProveedor(pComercializador, pTipoRedConvenio);
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
    public EstadoRango getEstadoRangoByCodigo(String codigo) {
        return seriesNngService.getEstadoRangoByCodigo(codigo);
    }

    @Override
    public List<ClaveServicio> findAllClaveServicioActivas() {
        return claveServicioService.findAllClaveServicioActivas();
    }

    @Override
    public List<TipoAsignacion> findAllTipoAsignacion() {
        return solicitudesNngService.findAllTipoAsignacion();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<SolicitudAsignacionNng> findAllSolicitudesAsignacion(FiltroBusquedaSolicitudes filtros)
            throws Exception {
        return solicitudesNngService.findAllSolicitudesAsignacion(filtros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Integer findAllSolicitudesAsignacionCount(FiltroBusquedaSolicitudes filtros) throws Exception {
        return solicitudesNngService.findAllSolicitudesAsignacionCount(filtros);
    }

    @Override
    public List<String> findAllClientesBySolicitud(Solicitud sol) {
        return solicitudesNngService.findAllClientesBySolicitud(sol);
    }

    @Override
    public ClaveServicio getClaveServicioByCodigo(BigDecimal codigo) {
        return claveServicioService.getClaveServicioByCodigo(codigo);
    }

    @Override
    public boolean existeConcesionarioConvenioConBcd(Proveedor proveedor) {
        return proveedoresService.existeConcesionarioConvenioConBcd(proveedor);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudAsignacionNng saveSolicitudAsignacion(SolicitudAsignacionNng solicitud) {
        return solicitudesNngService.saveSolicitudAsignacion(solicitud);
    }

    @Override
    public List<Proveedor> findAllArrendatarios() {
        return proveedoresService.findAllArrendatariosNng();
    }

    @Override
    public SolicitudAsignacionNng getSolicitudAsignacionEagerLoad(SolicitudAsignacionNng pSolicitud) {
        return solicitudesNngService.getSolicitudAsignacionEagerLoad(pSolicitud);
    }

    @Override
    public Integer findAllSeriesCount(FiltroBusquedaSeries filtros) {
        return seriesNngService.findAllSeriesCount(filtros);
    }

    @Override
    public List<SerieNng> findAllSeries(FiltroBusquedaSeries filtros) {
        return seriesNngService.findAllSeries(filtros);
    }

    @Override
    public Integer getTotalOcupacionSerie(SerieNng serie) {
        return seriesNngService.getTotalOcupacionSerie(serie);
    }

    @Override
    public List<SerieNng> findRandomSeriesLibreByClaveServicio(ClaveServicio clave, int n) {
        return seriesNngService.findRandomSeriesLibreByClaveServicio(clave, n);
    }

    @Override
    public SerieNng getRandomSerieOcupadaByClaveServicio(ClaveServicio clave, BigDecimal cantidad) {
        return seriesNngService.getRandomSerieOcupadaByClaveServicio(clave, cantidad);
    }

    @Override
    public RangoSerieNng getRandomRangoByClaveServicio(ClaveServicio clave, int cantidad) {
        return seriesNngService.getRandomRangoByClaveServicio(clave, cantidad);
    }

    @Override
    public RangoSerieNng saveRangoSerie(RangoSerieNng pRangoSerieNng) {
        return seriesNngService.saveRangoSerie(pRangoSerieNng);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelSolicitudCesion(SolicitudCesionNng pSolicitud) throws Exception {
        return solicitudesNngService.cancelSolicitudCesion(pSolicitud);
    }

    @Override
    public PeticionCancelacion cancelCesion(CesionSolicitadaNng pCesSol, boolean pUseCheck) throws Exception {
        return solicitudesNngService.cancelCesion(pCesSol, pUseCheck);
    }

    @Override
    public SolicitudCesionNng applyCesionesSolicitadas(SolicitudCesionNng pSolicitud) throws Exception {
        return solicitudesNngService.applyCesionesSolicitadas(pSolicitud);
    }

    @Override
    public List<String> getNotificacionesLoginCesion() {
        return solicitudesNngService.getNotificacionesCesionesPendientes();
    }

    @Override
    public List<String> getNotificacionesLoginLiberacion() {
        return solicitudesNngService.getNotificacionesLiberacionesPendientes();
    }

    @Override
    public List<TipoDestinatario> findAllTiposDestinatario() {
        return oficiosService.findAllTiposDestinatario();
    }

    @Override
    public void removeRango(RangoSerieNng rango) {
        seriesNngService.removeRangoSerie(rango);
    }

    @Override
    public Integer getTotalNumeracionAsignadaByClaveServicio(ClaveServicio clave) {
        return seriesNngService.getTotalNumeracionAsignadaByClaveServicio(clave);
    }

    @Override
    public List<Solicitud> findAllSolicitudes(FiltroBusquedaSolicitudes pFiltrosSolicitud)
            throws Exception {
        return solicitudesNngService.findAllSolicitudes(pFiltrosSolicitud);
    }

    @Override
    public int findAllSolicitudesCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) throws Exception {
        return solicitudesNngService.findAllSolicitudesCount(pFiltrosSolicitud);
    }

    @Override
    public List<Proveedor> findAllConcesionarioConvenio(Proveedor proveedor) {
        return proveedoresService.findAllConcesonarioConvenioNng(proveedor);
    }

    @Override
    public List<TipoSolicitud> findAllTiposSolicitud() throws Exception {
        return solicitudesNngService.findAllTiposSolicitud();
    }

    @Override
    public List<SolicitudLiberacionNng> findAllSolicitudesLiberacion(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudesNngService.findAllSolicitudesLiberacion(pFiltrosSolicitud);
    }

    @Override
    public int findAllSolicitudesLiberacionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudesNngService.findAllSolicitudesLiberacionCount(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudLiberacionNng saveSolicitudLiberacion(SolicitudLiberacionNng pSolicitud) {
        return solicitudesNngService.saveSolicitudLiberacion(pSolicitud);
    }

    @Override
    public SolicitudLiberacionNng getSolicitudLiberacionById(BigDecimal pConsecutivo) {
        return solicitudesNngService.getSolicitudLiberacionById(pConsecutivo);
    }

    @Override
    public SolicitudLiberacionNng getSolicitudLiberacionEagerLoad(SolicitudLiberacionNng pSolicitud) {
        return solicitudesNngService.getSolicitudLiberacionEagerLoad(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelSolicitudLiberacion(SolicitudLiberacionNng pSolicitud) throws Exception {
        return solicitudesNngService.cancelSolicitudLiberacion(pSolicitud);
    }

    @Override
    public PeticionCancelacion cancelLiberacion(LiberacionSolicitadaNng pLibSol, boolean pUseCheck) throws Exception {
        return solicitudesNngService.cancelLiberacion(pLibSol, pUseCheck);
    }

    @Override
    public SolicitudLiberacionNng applyLiberacionesSolicitadas(SolicitudLiberacionNng pSolicitud) throws Exception {
        return solicitudesNngService.applyLiberacionesSolicitadas(pSolicitud);
    }

    @Override
    public List<SolicitudRedistribucionNng> findAllSolicitudesRedistribucion(
            FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudesNngService.findAllSolicitudesRedistribucion(pFiltrosSolicitud);
    }

    @Override
    public int findAllSolicitudesRedistribucionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudesNngService.findAllSolicitudesRedistribucionCount(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudRedistribucionNng saveSolicitudRedistribucion(SolicitudRedistribucionNng pSolicitud) {
        return solicitudesNngService.saveSolicitudRedistribucion(pSolicitud);
    }

    @Override
    public SolicitudRedistribucionNng getSolicitudRedistribucionById(BigDecimal pConsecutivo) {
        return solicitudesNngService.getSolicitudRedistribucionById(pConsecutivo);
    }

    @Override
    public SolicitudRedistribucionNng getSolicitudRedistribucionEagerLoad(SolicitudRedistribucionNng pSolicitud) {
        return solicitudesNngService.getSolicitudRedistribucionEagerLoad(pSolicitud);
    }

    @Override
    public List<RangoSerieNng> validateRango(BigDecimal clave, BigDecimal sna,
            String numeroInicial, String numeroFinal) {
        return seriesNngService.validateRango(clave, sna, numeroInicial, numeroFinal);
    }

    @Override
    public SerieNng getSerieById(SerieNngPK id) {
        return seriesNngService.getSerieById(id);
    }

    @Override
    public boolean isNumeracionSolicitadaWithRangos(NumeracionSolicitadaNng numeracionSolicitada) {

        return solicitudesNngService.isNumeracionSolicitadaWithRangos(numeracionSolicitada);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<Proveedor> findAllArrendatariosByAbc(Proveedor proveedor) {
        return proveedoresService.findAllArrendatariosByAbc(proveedor);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<Proveedor> findAllConcesionariosFromConveniosByAbc(Proveedor pComercializador) {
        return proveedoresService.findAllConcesionariosFromConveniosByAbc(pComercializador);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudRedistribucionNng applyRedistribucionesSolicitadas(SolicitudRedistribucionNng pSolicitud)
            throws Exception {
        return solicitudesNngService.applyRedistribucionesSolicitadas(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelRedistribucion(RedistribucionSolicitadaNng pRedSol, boolean pUseCheck)
            throws Exception {
        return solicitudesNngService.cancelRedistribucion(pRedSol, pUseCheck);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelSolicitudRedistribucion(SolicitudRedistribucionNng pSolicitud) throws Exception {
        return solicitudesNngService.cancelSolicitudRedistribucion(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<HistoricoRangoSerieNng> findAllHistoricActionsFromRangos(FiltroBusquedaHistoricoRangos pFiltros) {
        return seriesNngService.findAllHistoricActionsFromRangos(pFiltros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public int findAllHistoricActionsFromRangosCount(FiltroBusquedaHistoricoRangos pFiltros) {
        return seriesNngService.findAllHistoricActionsFromRangosCount(pFiltros);
    }

    @Override
    public boolean isSolicitudWithRangos(Solicitud solicitud) {
        return solicitudesNngService.isSolicitudWithRangos(solicitud);
    }

    @Override
    public List<TipoReporte> findAllTiposReporte() {

        return reportesService.findAllTiposReporte();
    }

    @Override
    public RetornoProcesaFicheroReportesNng validarFicheroLineasActivas(File fichero) throws Exception {

        return validadorReportes.validarLineasActivasNng(fichero);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudLineasActivasNng saveSolicitudLineasActivas(SolicitudLineasActivasNng solicitud) {
        return solicitudesNngService.saveSolicitudLineasActivas(solicitud);
    }

    @Override
    public RetornoProcesaFicheroReportesNng validarFicheroLineasActivasDet(File fichero) throws Exception {

        return validadorReportes.validarLineasActivasDetNng(fichero);

    }

    @Override
    public RetornoProcesaFicheroReportesNng validarFicheroLineasArrendatario(File fichero) throws Exception {
        return validadorReportes.validarFicheroLineasArrendatarioNng(fichero);
    }

    @Override
    public ReporteLineaArrendatarioNng saveLineaArrendatario(ReporteLineaArrendatarioNng reporte) {

        return reportesService.saveLineaArrendatario(reporte);
    }

    @Override
    public void saveDetLineaArrendatario(List<String> listaDatos, BigDecimal id) {
        reportesService.saveDetLineaArrendatario(listaDatos, id);

    }

    @Override
    public RetornoProcesaFicheroReportesNng validarFicheroLineasArrendada(File fichero) throws Exception {

        return validadorReportes.validarFicheroLineasArrendadaNng(fichero);
    }

    @Override
    public ReporteLineaArrendadorNng saveLineaArrendada(ReporteLineaArrendadorNng reporte) {

        return reportesService.saveLineaArrendada(reporte);
    }

    @Override
    public void saveDetLineaArrendada(List<String> listaDatos, BigDecimal id) {
        reportesService.saveDetLineaArrendada(listaDatos, id);

    }

    @Override
    public ReporteLineaActivaDetNng saveLineaActivaDet(ReporteLineaActivaDetNng reporte) {

        return reportesService.saveLineaActivaDet(reporte);
    }

    @Override
    public void saveDetLineaActivaDet(List<String> listaDatos, BigDecimal id) {
        reportesService.saveDetLineaActivaDet(listaDatos, id);
    }

    @Override
    public ReporteLineaActivaNng saveLineaActiva(ReporteLineaActivaNng lineaActiva) {

        return reportesService.saveLineaActiva(lineaActiva);
    }

    @Override
    public void saveDetalleLineaActiva(List<String> listaDatos, BigDecimal id) {
        reportesService.saveDetLineaActiva(listaDatos, id);
    }

    @Override
    public List<DetalleReporteNng> findAllDetalleReporte(FiltroBusquedaLineasActivas filtro) {

        return reportesService.findAllDetalleReporte(filtro);
    }

    @Override
    public int findAllDetalleReporteCount(FiltroBusquedaLineasActivas filtros) {

        return reportesService.findAllDetalleReporteCount(filtros);
    }

    @Override
    public InputStream getExportConsultaLineaActiva(FiltroBusquedaLineasActivas filtro) throws Exception {
        ByteArrayInputStream bais = null;
        bais = new ByteArrayInputStream(reportesService.getExportConsultaLineaActiva(filtro));
        return bais;
    }

    @Override
    public List<DetalleLineaArrendadorNng> findAllDetLineaArrendadaByReporte(BigDecimal idReporte, int first,
            int pageSize) {

        return reportesService.findAllDetLineaArrendadaByReporte(idReporte, first, pageSize);
    }

    @Override
    public List<DetalleLineaArrendatarioNng> findAllDetLineaArrendatarioByReporte(BigDecimal idReporte, int first,
            int pageSize) {

        return reportesService.findAllDetLineaArrendatarioByReporte(idReporte, first, pageSize);

    }

    @Override
    public Integer getNumeracionAsignadaSerie(ClaveServicio claveServicio, Proveedor proveedor) {

        return seriesNngService.getNumeracionAsignadaSerie(claveServicio, proveedor);
    }

    @Override
    public Integer getNumeracionAsignadaRango(ClaveServicio claveServicio, Proveedor proveedor) {

        return seriesNngService.getNumeracionAsignadaRango(claveServicio, proveedor);
    }

    @Override
    public Integer getNumeracionAsignadaEspecifica(ClaveServicio claveServicio, Proveedor proveedor) {

        return seriesNngService.getNumeracionAsignadaEspecifica(claveServicio, proveedor);
    }

    @Override
    public Integer getNumeracionActivaSerie(ClaveServicio claveServicio, Proveedor proveedor, BigDecimal idReporte) {

        return reportesService.getNumeracionActivaSerie(claveServicio, proveedor, idReporte);
    }

    @Override
    public Integer getNumeracionActivaRango(ClaveServicio claveServicio, Proveedor proveedor, BigDecimal idReporte) {

        return reportesService.getNumeracionActivaRango(claveServicio, proveedor, idReporte);
    }

    @Override
    public Integer getNumeracionActivaEspecifica(ClaveServicio claveServicio, Proveedor proveedor,
            BigDecimal idReporte) {

        return reportesService.getNumeracionActivaEspecifica(claveServicio, proveedor, idReporte);
    }

    @Override
    public Integer getNumeracionActivaDetSerie(ClaveServicio claveServicio, Proveedor proveedor, BigDecimal idReporte) {

        return reportesService.getNumeracionActivaDetSerie(claveServicio, proveedor, idReporte);
    }

    @Override
    public Integer getNumeracionActivaDetRango(ClaveServicio claveServicio, Proveedor proveedor, BigDecimal idReporte) {

        return reportesService.getNumeracionActivaDetRango(claveServicio, proveedor, idReporte);
    }

    @Override
    public Integer getNumeracionActivaDetEspecifica(ClaveServicio claveServicio, Proveedor proveedor,
            BigDecimal idReporte) {

        return reportesService.getNumeracionActivaDetEspecifica(claveServicio, proveedor, idReporte);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public InputStream generarAnalisisNumeracion(
            Proveedor proveedor, List<NumeracionSolicitadaNng> numeracionesSolicitadas) throws Exception {
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(generarAnalisisService.generarAnalisisNumeracionNng(proveedor,
                    numeracionesSolicitadas));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return bais;
    }

    @Override
    public List<HistoricoSerieNng> findAllHistoricoSeries(FiltroBusquedaHistoricoSeries pFiltros) {

        return seriesNngService.findAllHistoricoSeries(pFiltros);
    }

    @Override
    public int findAllHistoricoSeriesCount(FiltroBusquedaHistoricoSeries pFiltros) {

        return seriesNngService.findAllHistoricoSeriesCount(pFiltros);
    }

    @Override
    public InputStream getExportHistoricoSeries(FiltroBusquedaHistoricoSeries filtro) throws Exception {
        ByteArrayInputStream bais = null;
        bais = new ByteArrayInputStream(seriesNngService.getExportHistoricoSeries(filtro));
        return bais;
    }

    @Override
    public List<NumeracionAsignadaNng> findAllNumeracionAsignadaBySolicitud(SolicitudAsignacionNng solicitud) {

        return solicitudesNngService.findAllNumeracionAsignadaBySolicitud(solicitud);
    }
}
