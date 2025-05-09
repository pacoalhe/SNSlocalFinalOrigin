package mx.ift.sns.negocio.ng;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import mx.ift.sns.dao.ng.IDetLineaActivaDetDao;
import mx.ift.sns.dao.ng.IDetLineaArrendadaDao;
import mx.ift.sns.dao.ng.IDetLineaArrendatarioDao;
import mx.ift.sns.dao.ng.IDetalleLineaActivaDao;
import mx.ift.sns.dao.ng.IDetalleReporteDao;
import mx.ift.sns.dao.ng.ILineaActivaDao;
import mx.ift.sns.dao.ng.ILineaActivaDetDao;
import mx.ift.sns.dao.ng.ILineaArrendadaDao;
import mx.ift.sns.dao.ng.ILineaArrendatarioDao;
import mx.ift.sns.dao.ng.IParametroDao;
import mx.ift.sns.dao.ng.ITipoReporteDao;
import mx.ift.sns.modelo.filtros.FiltroBusquedaLineasActivas;
import mx.ift.sns.modelo.filtros.FiltroReporteadorNG;
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
import mx.ift.sns.modelo.ng.SolicitudLineasActivas;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.negocio.num.INumeracionService;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;

/**
 * Servicio de Reportes.
 * @author X36155QU
 */
@Stateless(name = "ReportesService", mappedName = "ReportesService")
@Remote(IReportesService.class)
public class ReportesService implements IReportesService {

    /** Dao Tipo Reporte. */
    @Inject
    private ITipoReporteDao tipoReporteDao;

    /** Servicio de series. */
    @EJB(mappedName = "SeriesService")
    private ISeriesService seriesService;

    /** Servicio de numeracion. */
    @EJB
    private INumeracionService numeracionService;

    /** DAO de parametros. */
    @Inject
    private IParametroDao parametroDao;

    /** Linea Activa Dao. */
    @Inject
    private ILineaActivaDao lineaActivaDao;

    /** Linea Activa Dao. */
    @Inject
    private ILineaActivaDetDao lineaActivaDetDao;

    /** Linea Arrendatario Dao. */
    @Inject
    private ILineaArrendatarioDao lineaArrendatarioDao;

    /** Linea Arrendada Dao. */
    @Inject
    private ILineaArrendadaDao lineaArrendadaDao;

    /** DAO linea activa. */
    @Inject
    private ILineaActivaDao lineaActivaDAO;

    /** DAO Detalle Linea Activa. */
    @Inject
    private IDetalleLineaActivaDao detalleLineaActivaDao;

    /** DAO Detalle Linea Activa Detallada. */
    @Inject
    private IDetLineaActivaDetDao detalleLineaActivaDetDao;

    /** DAO Detalle Linea Activa Arrendatario. */
    @Inject
    private IDetLineaArrendatarioDao detalleLineaArrendatarioDao;

    /** DAO Detalle Linea Arrendada. */
    @Inject
    private IDetLineaArrendadaDao detalleLineaArrendadaDao;

    /** DAO Detalle Reporte. */
    @Inject
    private IDetalleReporteDao detalleReporteDao;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ReporteLineasActivas saveLineaActiva(ReporteLineasActivas lineaActiva) {

        return lineaActivaDao.saveOrUpdate(lineaActiva);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public LineaActivaDet saveLineaActivaDet(LineaActivaDet lineaActivaDet) {

        return lineaActivaDetDao.saveOrUpdate(lineaActivaDet);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public LineaArrendatario saveLineaArrendatario(LineaArrendatario lineaArrendatario) {

        return lineaArrendatarioDao.saveLineaArrendatario(lineaArrendatario);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public LineaArrendada saveLineaArrendada(LineaArrendada lineaArrendada) {

        return lineaArrendadaDao.saveLineaArrendada(lineaArrendada);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<DetalleLineaActiva> findAllSolicitudesLineasActivas(FiltroBusquedaLineasActivas pFiltrosSolicitud) {
        return lineaActivaDAO.findAllSolicitudesLineasActivas(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int findAllSolicitudesLineasActivasCount(FiltroBusquedaLineasActivas pFiltrosSolicitud) {
        return lineaActivaDAO.findAllSolicitudesLineasActivasCount(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<SolicitudLineasActivas> findAllSolicitudesLineasActivasConsulta(
            FiltroBusquedaLineasActivas pFiltrosSolicitud) {
        return lineaActivaDAO.findAllSolicitudesLineasActivasConsulta(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int findAllSolicitudesLineasActivasConsultaCount(FiltroBusquedaLineasActivas pFiltrosSolicitud) {
        return lineaActivaDAO.findAllSolicitudesLineasActivasConsultaCount(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ReporteLineasActivas getLastReporteLineasActiva(Proveedor proveedor) {

        return lineaActivaDAO.getLastReporteLineasActiva(proveedor);
    }

    @Override
    public void saveDetalleLineaActiva(List<String> listaDatos, BigDecimal idReporte) {
        detalleLineaActivaDao.saveDetalleLineaActiva(listaDatos, idReporte);

    }

    @Override
    public List<String> findAllAvisoAsignacionDetalleLineaActiva(Reporte reporte) {

        return detalleLineaActivaDao.findAllAvisoAsignacionDetalleLineaActiva(reporte);
    }

    @Override
    public List<DetLineaActivaDet> findAllDetLineaActivaDetByReporte(BigDecimal idReporte, int first, int pageSize) {

        return detalleLineaActivaDetDao.findAllDetLineaActivaDetByReporte(idReporte, first, pageSize);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void saveDetLineaActivaDet(List<String> listaDatos, BigDecimal idReporte) {
        detalleLineaActivaDetDao.saveDetLineaActivaDet(listaDatos, idReporte);

    }

    @Override
    public List<String> findAllAvisoAsignacionDetalleLineaActivaDet(Reporte reporte) {

        return detalleLineaActivaDetDao.findAllAvisoAsignacionDetalleLineaActivaDet(reporte);
    }

    @Override
    public void saveDetLineaArrendatario(List<String> listaDatos, BigDecimal idReporte) {
        detalleLineaArrendatarioDao.saveDetLineaArrendatario(listaDatos, idReporte);

    }

    @Override
    public List<DetLineaArrendatario> findAllDetLineaArrendatarioByReporte(BigDecimal idReporte, int first,
            int pageSize) {

        return detalleLineaArrendatarioDao.findAllDetLineaArrendatarioByReporte(idReporte, first, pageSize);
    }

    @Override
    public void saveDetLineaArrendada(List<String> listaDatos, BigDecimal idReporte) {
        detalleLineaArrendadaDao.saveDetLineaArrendada(listaDatos, idReporte);

    }

    @Override
    public List<DetLineaArrendada> findAllDetLineaArrendadaByReporte(BigDecimal idReporte, int first, int pageSize) {

        return detalleLineaArrendadaDao.findAllDetLineaArrendadaByReporte(idReporte, first, pageSize);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<DetalleReporte> findAllDetalleReporte(FiltroBusquedaLineasActivas filtros) {

        return detalleReporteDao.findAllDetalleReporte(filtros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Integer findAllDetalleReporteCount(FiltroBusquedaLineasActivas filtros) {

        return detalleReporteDao.findAllDetalleReporteCount(filtros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public byte[] getExportConsultaLineaActiva(FiltroBusquedaLineasActivas filtro) throws Exception {

        filtro.setUsarPaginacion(false);
        List<DetalleReporte> listaDetalles = detalleReporteDao.findAllDetalleReporte(filtro);

        ExportConsultaLineasActivasNg excla = new ExportConsultaLineasActivasNg(listaDetalles);
        ExportarExcel export = new ExportarExcel(parametroDao);
        if (export.getTamMaxExportacion() >= listaDetalles.size()) {
            return export.generarReporteExcel("Lineas Activas", excla);
        } else {
            return export.generarReporteExcelLineasActivasNg("Lineas Activas", excla, listaDetalles);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DetalleReporte findTotalesActivosDetalleReporte(FiltroReporteadorNG filtro) {
        return detalleReporteDao.findTotalesActivosDetalleReporte(filtro);
    }

    @Override
    public List<TipoReporte> findAllTiposReporte() {

        return tipoReporteDao.findAllTiposReporte();
    }
}
