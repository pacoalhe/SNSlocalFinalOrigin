package mx.ift.sns.negocio.nng;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;

import mx.ift.sns.dao.ng.IParametroDao;
import mx.ift.sns.dao.ng.ITipoReporteDao;
import mx.ift.sns.dao.nng.IDetalleLineaActivaDetNngDao;
import mx.ift.sns.dao.nng.IDetalleLineaActivaNngDao;
import mx.ift.sns.dao.nng.IDetalleLineaArrendadorNngDao;
import mx.ift.sns.dao.nng.IDetalleLineaArrendatarioNngDao;
import mx.ift.sns.dao.nng.IDetalleReporteNngDao;
import mx.ift.sns.dao.nng.IReporteLineaActivaDetNngDao;
import mx.ift.sns.dao.nng.IReporteLineaActivaNngDao;
import mx.ift.sns.dao.nng.IReporteLineaArrendadorNngDao;
import mx.ift.sns.dao.nng.IReporteLineaArrendatarioNngDao;
import mx.ift.sns.modelo.filtros.FiltroBusquedaLineasActivas;
import mx.ift.sns.modelo.lineas.DetalleLineaArrendadorNng;
import mx.ift.sns.modelo.lineas.DetalleLineaArrendatarioNng;
import mx.ift.sns.modelo.lineas.DetalleReporteNng;
import mx.ift.sns.modelo.lineas.ReporteLineaActivaDetNng;
import mx.ift.sns.modelo.lineas.ReporteLineaActivaNng;
import mx.ift.sns.modelo.lineas.ReporteLineaArrendadorNng;
import mx.ift.sns.modelo.lineas.ReporteLineaArrendatarioNng;
import mx.ift.sns.modelo.lineas.TipoReporte;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;

/**
 * Servicio de reportes NNG.
 * @author X36155QU
 */
@Stateless(name = "ReportesNngService", mappedName = "ReportesNngService")
@Remote(IReportesNngService.class)
public class ReportesNngService implements IReportesNngService {

    /** DAO de parametros. */
    @Inject
    private IParametroDao parametroDao;

    /** Dao Tipo Reporte. */
    @Inject
    private ITipoReporteDao tipoReporteDao;

    /** Dao Detalle Reporte. */
    @Inject
    private IDetalleReporteNngDao detalleReporteDao;

    /** Dao Linea Arrendatario. */
    @Inject
    private IReporteLineaArrendatarioNngDao lineaArrendatarioDao;

    /** Dao Detalle Linea Arrendatario. */
    @Inject
    private IDetalleLineaArrendatarioNngDao detLineaArrendatarioDao;

    /** Dao Linea Arrendatario. */
    @Inject
    private IReporteLineaArrendadorNngDao lineaArrendadorDao;

    /** Dao Detalle Linea Arrendador. */
    @Inject
    private IDetalleLineaArrendadorNngDao detLineaArrendadorDao;

    /** Dao Linea Activa Detallada. */
    @Inject
    private IReporteLineaActivaDetNngDao lineaActivaDetDao;

    /** Dao Detalle Linea Activa Detallada. */
    @Inject
    private IDetalleLineaActivaDetNngDao detLineaActDetDao;

    /** Dao Linea Activa. */
    @Inject
    private IReporteLineaActivaNngDao lineaActivaDao;

    /** Dao Detalle Linea Activa. */
    @Inject
    private IDetalleLineaActivaNngDao detLineaActDao;

    @Override
    public List<TipoReporte> findAllTiposReporte() {

        return tipoReporteDao.findAllTiposReporte();
    }

    @Override
    public ReporteLineaArrendatarioNng saveLineaArrendatario(ReporteLineaArrendatarioNng reporte) {

        return lineaArrendatarioDao.saveLineaArrendatario(reporte);
    }

    @Override
    public void saveDetLineaArrendatario(List<String> listaDatos, BigDecimal id) {
        detLineaArrendatarioDao.saveDetLineaArrendatario(listaDatos, id);

    }

    @Override
    public ReporteLineaArrendadorNng saveLineaArrendada(ReporteLineaArrendadorNng reporte) {

        return lineaArrendadorDao.saveLineaArrendada(reporte);
    }

    @Override
    public void saveDetLineaArrendada(List<String> listaDatos, BigDecimal id) {
        detLineaArrendadorDao.saveDetLineaArrendada(listaDatos, id);

    }

    @Override
    public ReporteLineaActivaDetNng saveLineaActivaDet(ReporteLineaActivaDetNng reporte) {

        return lineaActivaDetDao.saveLineaActivaDet(reporte);
    }

    @Override
    public void saveDetLineaActivaDet(List<String> listaDatos, BigDecimal id) {
        detLineaActDetDao.saveDetLineaActivaDet(listaDatos, id);

    }

    @Override
    public ReporteLineaActivaNng saveLineaActiva(ReporteLineaActivaNng lineaActiva) {

        return lineaActivaDao.saveLineaActiva(lineaActiva);
    }

    @Override
    public void saveDetLineaActiva(List<String> listaDatos, BigDecimal id) {
        detLineaActDao.saveDetalleLineaActiva(listaDatos, id);

    }

    @Override
    public List<DetalleReporteNng> findAllDetalleReporte(FiltroBusquedaLineasActivas filtro) {

        return detalleReporteDao.findAllDetalleReporte(filtro);
    }

    @Override
    public int findAllDetalleReporteCount(FiltroBusquedaLineasActivas filtro) {

        return detalleReporteDao.findAllDetalleReporteCount(filtro);
    }

    @Override
    public byte[] getExportConsultaLineaActiva(FiltroBusquedaLineasActivas filtro) throws Exception {
        filtro.setUsarPaginacion(false);
        List<DetalleReporteNng> listaDetalles = detalleReporteDao.findAllDetalleReporte(filtro);

        ExportConsultaLineasActivasNng excla = new ExportConsultaLineasActivasNng(listaDetalles);
        ExportarExcel export = new ExportarExcel(parametroDao);
        if (export.getTamMaxExportacion() >= listaDetalles.size()) {
            return export.generarReporteExcel("Lineas Activas", excla);
        } else {
            return export.generarReporteExcelLineasActivasNng("Lineas Activas", excla, listaDetalles);
        }
    }

    @Override
    public List<DetalleLineaArrendadorNng> findAllDetLineaArrendadaByReporte(BigDecimal idReporte, int first,
            int pageSize) {

        return detLineaArrendadorDao.findAllDetLineaArrendadaByReporte(idReporte, first, pageSize);
    }

    @Override
    public List<DetalleLineaArrendatarioNng> findAllDetLineaArrendatarioByReporte(BigDecimal idReporte, int first,
            int pageSize) {

        return detLineaArrendatarioDao.findAllDetLineaArrendatarioByReporte(idReporte, first, pageSize);
    }

    @Override
    public Integer getNumeracionActivaSerie(ClaveServicio claveServicio, Proveedor proveedor, BigDecimal idReporte) {

        return detLineaActDao.getNumeracionActivaSerie(claveServicio.getCodigo().toString(), proveedor, idReporte);
    }

    @Override
    public Integer getNumeracionActivaRango(ClaveServicio claveServicio, Proveedor proveedor, BigDecimal idReporte) {

        return detLineaActDao.getNumeracionActivaRango(claveServicio.getCodigo().toString(), proveedor, idReporte);
    }

    @Override
    public Integer getNumeracionActivaEspecifica(ClaveServicio claveServicio, Proveedor proveedor,
            BigDecimal idReporte) {

        return detLineaActDao.getNumeracionActivaEspecifica(claveServicio.getCodigo().toString(), proveedor, idReporte);
    }

    @Override
    public Integer getNumeracionActivaDetSerie(ClaveServicio claveServicio, Proveedor proveedor, BigDecimal idReporte) {

        return detLineaActDetDao.getNumeracionActivaDetSerie(claveServicio.getCodigo().toString(),
                proveedor, idReporte);
    }

    @Override
    public Integer getNumeracionActivaDetRango(ClaveServicio claveServicio, Proveedor proveedor, BigDecimal idReporte) {

        return detLineaActDetDao.getNumeracionActivaDetRango(claveServicio.getCodigo().toString(),
                proveedor, idReporte);
    }

    @Override
    public Integer getNumeracionActivaDetEspecifica(ClaveServicio claveServicio, Proveedor proveedor,
            BigDecimal idReporte) {

        return detLineaActDetDao.getNumeracionActivaDetEspecifica(claveServicio.getCodigo().toString(), proveedor,
                idReporte);
    }
}
