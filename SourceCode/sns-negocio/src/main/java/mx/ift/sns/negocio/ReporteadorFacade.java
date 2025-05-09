package mx.ift.sns.negocio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import mx.ift.sns.dao.ng.IDetalleReporteDao;
import mx.ift.sns.dao.ng.IParametroDao;
import mx.ift.sns.modelo.central.ReporteCentralVm;
import mx.ift.sns.modelo.filtros.FiltroBusquedaRangos;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSeries;
import mx.ift.sns.modelo.filtros.FiltroReporteadorCentrales;
import mx.ift.sns.modelo.filtros.FiltroReporteadorNG;
import mx.ift.sns.modelo.lineas.DetalleReporte;
import mx.ift.sns.modelo.nng.RangoSerieNng;
import mx.ift.sns.modelo.nng.SerieNng;
import mx.ift.sns.modelo.oficios.Oficio;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.reporteador.NGReporteador;
import mx.ift.sns.negocio.centrales.ICentralesService;
import mx.ift.sns.negocio.ng.IReportesService;
import mx.ift.sns.negocio.ng.ISeriesService;
import mx.ift.sns.negocio.nng.ISeriesNngService;
import mx.ift.sns.negocio.oficios.IOficiosService;
import mx.ift.sns.negocio.ot.IOrganizacionTerritorialService;
import mx.ift.sns.negocio.psts.IProveedoresService;
import mx.ift.sns.negocio.reporteador.ExportNGPorFechas;
import mx.ift.sns.negocio.reporteador.ExportReporteadorCentrales;
import mx.ift.sns.negocio.reporteador.ExportReporteadorNGFechaActual;
import mx.ift.sns.negocio.reporteador.ExportReporteadorNGTramites;
import mx.ift.sns.negocio.reporteador.ExportReporteadorNNG;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;

/**
 * Adminstración de Catalogos.
 */
@Stateless(name = "ReporteadorFacade", mappedName = "ReporteadorFacade")
@Remote(IReporteadorFacade.class)
public class ReporteadorFacade implements IReporteadorFacade {

    /** Servicio de Organizacion Territorial. */
    @EJB
    private IOrganizacionTerritorialService otService;

    /** Servicio series. */
    @EJB
    private ISeriesService seriesService;

    /** Servicio series Nng. */
    @EJB
    private ISeriesNngService seriesnng;

    /** Servicio proveedor. */
    @EJB
    private IProveedoresService pService;

    /** Servicio reportes. */
    @EJB(mappedName = "ReportesService", beanInterface = mx.ift.sns.negocio.ng.IReportesService.class)//ejbLink="../payment.jar#PaymentService")
    private IReportesService reportService;

    /** Servicio oficios. */
    @EJB
    private IOficiosService oficiosService;

    /** Servicio centrales. */
    @EJB
    private ICentralesService centralesService;

    /** DAO de parametros. */
    @Inject
    private IParametroDao parametroDao;

    @Inject
    private IDetalleReporteDao detalleReporteDao;

    @Override
    public List<Municipio> findMunicipiosByEstado(String estado) throws Exception {
        return otService.findMunicipiosByEstado(estado);
    }

    @Override
    public List<Estado> findAllEstados() throws Exception {
        return otService.findEstados();
    }

    @Override
    public List<Poblacion> findAllPoblaciones(String estado, String municipio) {
        return otService.findAllPoblaciones(estado, municipio);
    }

    @Override
    public List<Poblacion> findAllPoblaciones(String estado, String municipio, boolean pUseCache) {
        return otService.findAllPoblaciones(estado, municipio, pUseCache);
    }

    @Override
    public List<Proveedor> findAllProveedoresActivos() throws Exception {
        return pService.findAllProveedoresActivos();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<NGReporteador> findHistoricoSeries(FiltroReporteadorNG filtro) throws Exception {
        return seriesService.findHistoricoSeries(filtro);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<NGReporteador> findHistoricoSeriesAsignadas(FiltroReporteadorNG filtro) {
        return seriesService.findHistoricoSeriesAsignadas(filtro);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public byte[] getReporteNGFechaActual(FiltroReporteadorNG pFiltro) throws Exception {
        List<NGReporteador> listado = this.findHistoricoSeries(pFiltro);
        ExportReporteadorNGFechaActual exccp = new ExportReporteadorNGFechaActual(listado, pFiltro);
        ExportarExcel export = new ExportarExcel(parametroDao);
        return export.generarReporteExcelNGActual("Reporte NG Fecha Actual", exccp);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public byte[] getReporteNGPorFechas(FiltroReporteadorNG pFiltro) throws Exception {
        ExportNGPorFechas exccp = new ExportNGPorFechas(pFiltro, this);
        ExportarExcel export = new ExportarExcel(parametroDao);
        return export.generarReporteExcelNGPorFechas("Reporte NG Fechas ", exccp);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DetalleReporte findTotalesActivosDetalleReporte(FiltroReporteadorNG filtro) {
        return detalleReporteDao.findTotalesActivosDetalleReporte(filtro);
    }

    @Override
    public List<Object[]> findTotalTramites(FiltroReporteadorNG filtro) {
        return seriesService.findTotalesTramites(filtro);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public byte[] getReporteNGTramites(FiltroReporteadorNG pFiltro) throws Exception {
        List<Object[]> listado = this.findTotalTramites(pFiltro);
        ExportReporteadorNGTramites exccp = new ExportReporteadorNGTramites(listado, pFiltro);
        ExportarExcel export = new ExportarExcel(parametroDao);
        return export.generarReporteExcelNGTramites("Reporte NG Trámites", exccp);
    }

    @Override
    public List<RangoSerieNng> findAllRangosNNG(FiltroBusquedaRangos pFiltros) {
        return seriesnng.findAllRangos(pFiltros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public byte[] getReporteNNG(Proveedor pst) throws Exception {
        ExportReporteadorNNG exccp = new ExportReporteadorNNG(pst, this);
        ExportarExcel export = new ExportarExcel(parametroDao);
        return export.generarReporteExcel("Reporte NNG", exccp);
    }

    @Override
    public List<Oficio> getOficiosBySolicitud(BigDecimal id) {
        return oficiosService.getOficiosBySolicitud(id);

    }

    @Override
    public List<SerieNng> findAllSeriesNNG(FiltroBusquedaSeries filtros) {
        return seriesnng.findAllSeries(filtros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public byte[] getExportCentrales(FiltroReporteadorCentrales filtro) throws Exception {
        List<ReporteCentralVm> listacentralestotal = new ArrayList<ReporteCentralVm>();
        int total = centralesService.findAllCentralesPorLocalidadCount(filtro);

        if (total > filtro.getResultadosPagina()) {
            filtro.setUsarPaginacion(true);
            int numConsultas = total / filtro.getResultadosPagina() + 1;
            int numPagina = 0;
            for (int i = 0; i < numConsultas; i++) {
                filtro.setNumeroPagina(numPagina);
                List<ReporteCentralVm> listacentrales = centralesService.findAllCentralesPorLocalidad(filtro);
                listacentralestotal.addAll(listacentrales);
                numPagina = numPagina + filtro.getResultadosPagina();

            }

        } else {
            filtro.setUsarPaginacion(false);
            listacentralestotal = centralesService.findAllCentralesPorLocalidad(filtro);
        }

        ExportReporteadorCentrales exc = new ExportReporteadorCentrales(listacentralestotal);
        ExportarExcel export = new ExportarExcel(parametroDao);
        if (export.getTamMaxExportacion() >= total) {
            return export.generarReporteExcel("Centrales", exc);
        } else {
            return export.generarReporteExcelCentrales("Centrales", exc, listacentralestotal);
        }
    }
}
