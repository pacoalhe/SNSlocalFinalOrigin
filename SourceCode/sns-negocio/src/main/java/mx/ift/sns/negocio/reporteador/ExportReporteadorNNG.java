package mx.ift.sns.negocio.reporteador;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroBusquedaRangos;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSeries;
import mx.ift.sns.modelo.nng.RangoSerieNng;
import mx.ift.sns.modelo.nng.SerieNng;
import mx.ift.sns.modelo.oficios.Oficio;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.negocio.IReporteadorFacade;
import mx.ift.sns.negocio.utils.excel.ExcelCabeceraInfo;
import mx.ift.sns.negocio.utils.excel.ExcelCeldaInfo;
import mx.ift.sns.negocio.utils.excel.IExportarExcel;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/** Genera un fichero Excel con la información del reporte. */
public class ExportReporteadorNNG implements IExportarExcel {

    /** Logger. */
    // private static final Logger LOGGER = LoggerFactory.getLogger(ExportReporteadorNNG.class);

    /** Cabecera del Excel . */
    private String[] textosCabecera = {"PST", "Clave", "Serie", "Cantidad Asignada",
            "Núm. Oficio", "Fecha Oficio"};

    /** Servicio de Adminstración de Catalogos. */
    private IReporteadorFacade reporteadorFacadeService;

    /** Filtro de campos. */
    private Proveedor pst;

    /** Libro Excel para el reporte. */
    private SXSSFWorkbook workBook = null;

    /**
     * Constructor.
     * @param filtroPst PST.
     * @param pReporteadorFacadeService fachada de servicios
     */
    public ExportReporteadorNNG(Proveedor filtroPst, IReporteadorFacade pReporteadorFacadeService) {
        pst = filtroPst;
        reporteadorFacadeService = pReporteadorFacadeService;
        workBook = new SXSSFWorkbook(5000);
        workBook.setCompressTempFiles(true);
    }

    @Override
    public SXSSFWorkbook getLibroExcel() {
        return workBook;
    }

    @Override
    public ExcelCabeceraInfo getCabecera() {
        ExcelCabeceraInfo ehi = new ExcelCabeceraInfo();
        ehi.setTitulos(textosCabecera);
        ehi.setInmovilizarCabecera(true);

        return ehi;
    }

    @Override
    public ArrayList<ExcelCeldaInfo[]> getContenido() {

        ArrayList<ExcelCeldaInfo[]> contenido = new ArrayList<ExcelCeldaInfo[]>();
        ExcelCeldaInfo[] contenidoFila = null;
        ExcelCeldaInfo celda = null;

        FiltroBusquedaRangos pFiltros = new FiltroBusquedaRangos();
        if (this.pst != null) {
            pFiltros.setAsignatario(pst);
        }
        EstadoRango estatusRango = new EstadoRango();
        estatusRango.setCodigo(EstadoRango.ASIGNADO);
        pFiltros.setEstatusRango(estatusRango);
        pFiltros.setUsarPaginacion(false);
        pFiltros.setOrdernarPor("id.idClaveServicio", FiltroBusquedaRangos.ORDEN_ASC);
        pFiltros.setOrdernarPor("id.sna", FiltroBusquedaRangos.ORDEN_ASC);

        List<RangoSerieNng> rangos = reporteadorFacadeService.findAllRangosNNG(pFiltros);
        FiltroBusquedaSeries pFiltrosSeries = new FiltroBusquedaSeries();
        pFiltrosSeries.setUsarPaginacion(false);
        pFiltrosSeries.setSerieLibre(true);
        pFiltrosSeries.setClaveOrder(FiltroBusquedaRangos.ORDEN_ASC);
        pFiltrosSeries.setSnaOrder(FiltroBusquedaRangos.ORDEN_ASC);

        int asignadas = 0;
        String numOficio = "";
        Date fechaOficio = null;
        int contHS = 0;
        boolean isSolicitudUnica = true;

        if (rangos.size() > 0) {
            Iterator<RangoSerieNng> it = rangos.iterator();
            RangoSerieNng rangoSerieNngAnterior = (RangoSerieNng) it.next();
            while (it.hasNext()) {
                RangoSerieNng rangoSerieNng = (RangoSerieNng) it.next();
                try {
                    asignadas = asignadas + rangoSerieNngAnterior.getNumFinalAsInt()
                            - rangoSerieNngAnterior.getNumInicioAsInt()
                            + 1;
                } catch (Exception e) {
                }

                if (rangoSerieNngAnterior.getId().getIdClaveServicio()
                        .equals(rangoSerieNng.getId().getIdClaveServicio())
                        && rangoSerieNngAnterior.getId().getSna().equals(rangoSerieNng.getId().getSna())) {
                    if (!rangoSerieNngAnterior.getSolicitud().getId().equals(rangoSerieNng.getSolicitud().getId())) {
                        isSolicitudUnica = false;
                    }
                } else {
                    contHS = 0;
                    contenidoFila = this.nuevaFila(contenido);
                    // Solo pintamos la PST si se filtra por ella
                    if (this.pst != null) {
                        celda = new ExcelCeldaInfo((String) rangoSerieNngAnterior.getAsignatario().getNombre(), null,
                                null, false);
                    } else {
                        celda = new ExcelCeldaInfo("", null, null, false);
                    }
                    contenidoFila[contHS++] = celda;
                    celda = new ExcelCeldaInfo("" + rangoSerieNngAnterior.getId().getIdClaveServicio(), null, null,
                            false);
                    contenidoFila[contHS++] = celda;
                    celda = new ExcelCeldaInfo("" + rangoSerieNngAnterior.getId().getSna(), null, null, false);
                    contenidoFila[contHS++] = celda;
                    celda = new ExcelCeldaInfo("" + asignadas, null, null, false);
                    contenidoFila[contHS++] = celda;

                    numOficio = "";
                    fechaOficio = null;
                    // Solo se saca el Oficio si la serie está asignada completamente en una sola solicitud
                    if (asignadas >= 10000 && isSolicitudUnica) {
                        List<Oficio> oficios = reporteadorFacadeService.getOficiosBySolicitud(rangoSerieNngAnterior
                                .getSolicitud().getId());
                        for (Iterator<Oficio> iterator = oficios.iterator(); iterator.hasNext();) {
                            Oficio oficio = iterator.next();
                            if (oficio.getTipoDestinatario().getCdg().equals(TipoDestinatario.PST_SOLICITANTE)) {
                                numOficio = oficio.getNumOficio();
                                fechaOficio = oficio.getFechaOficio();
                            }
                        }
                    }
                    celda = new ExcelCeldaInfo(numOficio, null, null, false);
                    contenidoFila[contHS++] = celda;
                    celda = new ExcelCeldaInfo(FechasUtils.fechaToString(fechaOficio), null, null, false);
                    contenidoFila[contHS++] = celda;
                    asignadas = 0;
                    isSolicitudUnica = true;
                }
                rangoSerieNngAnterior = rangoSerieNng;
            }
            try {
                asignadas = asignadas + rangoSerieNngAnterior.getNumFinalAsInt()
                        - rangoSerieNngAnterior.getNumInicioAsInt() + 1;
            } catch (Exception e) {
            }

            contHS = 0;
            contenidoFila = this.nuevaFila(contenido);

            if (this.pst != null) {
                celda = new ExcelCeldaInfo((String) rangoSerieNngAnterior.getAsignatario().getNombre(), null,
                        null, false);
            } else {
                celda = new ExcelCeldaInfo("", null, null, false);
            }
            contenidoFila[contHS++] = celda;
            celda = new ExcelCeldaInfo("" + rangoSerieNngAnterior.getId().getIdClaveServicio(), null, null, false);
            contenidoFila[contHS++] = celda;
            celda = new ExcelCeldaInfo("" + rangoSerieNngAnterior.getId().getSna(), null, null, false);
            contenidoFila[contHS++] = celda;
            celda = new ExcelCeldaInfo("" + asignadas, null, null, false);
            asignadas = 0;
            contenidoFila[contHS++] = celda;

            numOficio = "";
            fechaOficio = null;
            if (asignadas >= 10000 && isSolicitudUnica) {
                List<Oficio> oficios = reporteadorFacadeService.getOficiosBySolicitud(rangoSerieNngAnterior
                        .getSolicitud().getId());
                for (Iterator<Oficio> iterator = oficios.iterator(); iterator.hasNext();) {
                    Oficio oficio = iterator.next();
                    if (oficio.getTipoDestinatario().getCdg().equals(TipoDestinatario.PST_SOLICITANTE)) {
                        numOficio = oficio.getNumOficio();
                        fechaOficio = oficio.getFechaOficio();
                    }
                }
            }
            celda = new ExcelCeldaInfo(numOficio, null, null, false);
            contenidoFila[contHS++] = celda;

            celda = new ExcelCeldaInfo(FechasUtils.fechaToString(fechaOficio), null, null, false);
            contenidoFila[contHS++] = celda;
        }

        // Solo listamos series libres si no se ha escogido ningun PST
        if (this.pst == null) {
            List<SerieNng> serieslibres = reporteadorFacadeService.findAllSeriesNNG(pFiltrosSeries);
            for (SerieNng serieNng : serieslibres) {
                contHS = 0;
                contenidoFila = this.nuevaFila(contenido);
                celda = new ExcelCeldaInfo("", null, null, false);
                contenidoFila[contHS++] = celda;
                celda = new ExcelCeldaInfo("" + serieNng.getId().getIdClaveServicio(), null, null, false);
                contenidoFila[contHS++] = celda;
                celda = new ExcelCeldaInfo("" + serieNng.getId().getSna(), null, null, false);
                contenidoFila[contHS++] = celda;
            }
        }
        return contenido;
    }

    /**
     * Agrega una nueva fila al contenedor de filas.
     * @param pContenedor Contenedor de filas
     * @return nueva fila para ser editada
     */
    private ExcelCeldaInfo[] nuevaFila(ArrayList<ExcelCeldaInfo[]> pContenedor) {
        ExcelCeldaInfo[] contenidoFila = new ExcelCeldaInfo[textosCabecera.length];
        pContenedor.add(contenidoFila);
        return contenidoFila;
    }
}
