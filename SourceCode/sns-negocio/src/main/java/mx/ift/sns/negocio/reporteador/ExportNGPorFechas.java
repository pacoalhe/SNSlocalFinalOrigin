package mx.ift.sns.negocio.reporteador;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroReporteadorNG;
import mx.ift.sns.modelo.lineas.DetalleReporte;
import mx.ift.sns.modelo.reporteador.ElementoAgrupador;
import mx.ift.sns.modelo.reporteador.NGReporteador;
import mx.ift.sns.negocio.IReporteadorFacade;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xslf.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/** Genera un fichero Excel con la información de la consulta sobre el Catálogo de Marcas y Modelos. */
public class ExportNGPorFechas {

    /** Filtro de búsqueda. */
    private FiltroReporteadorNG filtro;

    /** Servicio del Reporteador. */
    private IReporteadorFacade reporteadorFacadeService;

    /** Cabecera del Excel de Catálogos de Marcas y Modelos. */
    private String[] textosCabecera = {"", "Total", "Fijo", "Movil", "CPP", "MPP", "Total", "Fijo", "Movil",
            "CPP", "MPP"};

    /** Libro Excel para el reporte. */
    private SXSSFWorkbook workBook = null;

    /**
     * Constructor.
     * @param pFiltro filtro
     * @param pReporteadorFacadeService pReporteadorFacadeService
     */
    public ExportNGPorFechas(FiltroReporteadorNG pFiltro, IReporteadorFacade pReporteadorFacadeService) {
        this.filtro = pFiltro;
        reporteadorFacadeService = pReporteadorFacadeService;
        workBook = new SXSSFWorkbook(5000);
        workBook.setCompressTempFiles(true);
    }

    /**
     * Recupera el objeto SXSSFWorkbook de la instancia.
     * @return SXSSFWorkbook Libro Excel.
     */
    public SXSSFWorkbook getLibroExcel() {
        return workBook;
    }

    /**
     * @param hoja Sheet
     * @return Sheet
     */
    public Sheet generarContenidoAsignadosActivosFechas(Sheet hoja) {
        int contFila = 0;

        hoja.addMergedRegion(new CellRangeAddress(2, 3, 0, 0));
        hoja.addMergedRegion(new CellRangeAddress(2, 2, 1, 5));
        hoja.addMergedRegion(new CellRangeAddress(2, 2, 6, 10));

        CellStyle estiloBlue = crearEstilo(HSSFColor.PALE_BLUE.index, true, this.workBook);
        CellStyle estiloGrey = crearEstilo(HSSFColor.GREY_25_PERCENT.index, true, this.workBook);

        Cell celda = null;
        ArrayList<Date[]> filtroFechas = new ArrayList<Date[]>();
        Date[] fechas = new Date[2];
        int cont = 0;

        Date pFechaInicio = filtro.getFechaInicio();
        Calendar calFechaFin = Calendar.getInstance();
        calFechaFin.setTime(filtro.getFechaFin());

        Calendar calendarFin = Calendar.getInstance();
        calendarFin.setTime(filtro.getFechaFin());
        calendarFin.add(Calendar.MONTH, -1);

        Row linea = hoja.createRow(contFila++);
        celda = linea.createCell(cont);
        celda.setCellValue("Comparativa de Numeraciones Geográficas Asignada/Activas entre "
                + FechasUtils.fechaToString(pFechaInicio, "MMMM-yyyy") + " y "
                + FechasUtils.fechaToString(calendarFin.getTime(), "MMMM-yyyy") + " y agrupada por"
                + (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_MES) ? " meses"
                        : " años"));

        linea = hoja.createRow(contFila++);
        celda = linea.createCell(cont);
        celda.setCellValue(getTextFiltros(filtro));

        while (pFechaInicio.before(filtro.getFechaFin())) {
            fechas = new Date[2];
            Date pFechaFin;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(pFechaInicio);
            if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_MES)) {
                calendar.add(Calendar.MONTH, 1); // Mes siguiente
                pFechaFin = calendar.getTime();
            } else {
                // Ponemos al uno de Enero y sumamos un año
                // Salvo que sea el ultimo año, en cuyo caso ponemos la fecha escogida
                if (calendar.get(Calendar.YEAR) == calFechaFin.get(Calendar.YEAR)) {
                    pFechaFin = filtro.getFechaFin();
                } else {
                    calendar.set(calendar.get(Calendar.YEAR), 0, 1);
                    calendar.add(Calendar.YEAR, 1);
                    pFechaFin = calendar.getTime();
                }
            }
            fechas[0] = pFechaInicio;
            fechas[1] = pFechaFin;
            filtroFechas.add(fechas);
            pFechaInicio = pFechaFin;
        }

        cont = 0;
        linea = hoja.createRow(contFila++);
        celda = linea.createCell(cont++);
        celda.setCellValue("Periodo");
        celda.setCellStyle(estiloGrey);

        celda = linea.createCell(cont++);
        celda.setCellValue("Asignados");
        celda.setCellStyle(estiloBlue);
        for (int i = 0; i < 4; i++) {
            celda = linea.createCell(cont++);
            celda.setCellValue("");
            celda.setCellStyle(estiloBlue);
        }

        celda = linea.createCell(cont++);
        celda.setCellValue("Activos");
        celda.setCellStyle(estiloBlue);
        for (int i = 0; i < 4; i++) {
            celda = linea.createCell(cont++);
            celda.setCellValue("");
            celda.setCellStyle(estiloBlue);
        }

        cont = 0;
        linea = hoja.createRow(contFila++);
        for (int i = 0; i < textosCabecera.length; i++) {
            celda = linea.createCell(cont++);
            celda.setCellValue(textosCabecera[i]);
            celda.setCellStyle(estiloGrey);

        }

        DetalleReporte activos;
        List<NGReporteador> listaAsignados;
        NGReporteador asignados = new NGReporteador();
        for (Iterator<Date[]> iterator = filtroFechas.iterator(); iterator.hasNext();) {
            Date[] dates = (Date[]) iterator.next();
            Date fechaIni = dates[0];
            Date fechaFin = dates[1];
            filtro.setFechaFin(fechaFin);
            activos = reporteadorFacadeService.findTotalesActivosDetalleReporte(filtro);
            listaAsignados = reporteadorFacadeService.findHistoricoSeriesAsignadas(filtro);
            if (null != listaAsignados && listaAsignados.size() > 0) {
                asignados = listaAsignados.get(0);
            }
            // Nueva Fila
            cont = 0;
            linea = hoja.createRow(contFila++);
            // Fecha
            String periodo = "";
            if (filtro.getPrimeraAgrupacion().getCodigo().equals(ElementoAgrupador.COD_MES)) {
                periodo = FechasUtils.fechaToString(fechaIni, "MMM-yy");
            } else {
                periodo = FechasUtils.fechaToString(fechaIni, "yyyy");
            }
            celda = linea.createCell(cont++);
            celda.setCellValue(periodo.toString());

            // Total Asignados
            int totalAsignadas = (null != asignados.getTotalAsignadas()) ? asignados
                    .getTotalAsignadas().intValue() : 0;
            celda = linea.createCell(cont++);
            celda.setCellValue("" + totalAsignadas);

            // Total Fijos Asignados
            int totalFijasAsignadas = (null != asignados.getAsignadasFijas())
                    ? asignados.getAsignadasFijas().intValue() : 0;
            celda = linea.createCell(cont++);
            celda.setCellValue("" + totalFijasAsignadas);

            // Total Movil Asignados
            int totalMovilesAsignadasMpp = (null != asignados.getAsignadasMovilesMPP())
                    ? asignados.getAsignadasMovilesMPP().intValue() : 0;
            int totalMovilesAsignadasCpp = (null != asignados.getAsignadasMovilesCPP())
                    ? asignados.getAsignadasMovilesCPP().intValue() : 0;
            celda = linea.createCell(cont++);
            celda.setCellValue("" + (totalMovilesAsignadasCpp + totalMovilesAsignadasMpp));

            // Total Movil Asignados CPP
            celda = linea.createCell(cont++);
            celda.setCellValue("" + totalMovilesAsignadasCpp);

            // Total Movil Asignados MPP
            celda = linea.createCell(cont++);
            celda.setCellValue("" + totalMovilesAsignadasMpp);

            // Total Activos
            int totalActivas = (null != activos.getTotalLineasActivas()) ? activos
                    .getTotalLineasActivas().intValue() : 0;
            celda = linea.createCell(cont++);
            celda.setCellValue("" + totalActivas);

            // Total Fijos Activos
            int totalFijasActivas = (null != activos.getTotalLineasActivasFijas())
                    ? activos.getTotalLineasActivasFijas().intValue() : 0;
            celda = linea.createCell(cont++);
            celda.setCellValue("" + totalFijasActivas);

            // Total Movil Activos
            int totalMovilsActivasMpp = (null != activos.getTotalLineasActivasMpp())
                    ? activos.getTotalLineasActivasMpp().intValue() : 0;
            int totalMovilsActivasCpp = (null != activos.getTotalLineasActivasCpp())
                    ? activos.getTotalLineasActivasCpp().intValue() : 0;
            celda = linea.createCell(cont++);
            celda.setCellValue("" + (totalMovilsActivasCpp + totalMovilsActivasMpp));

            // Total Movil Activos CPP
            celda = linea.createCell(cont++);
            celda.setCellValue("" + totalMovilsActivasCpp);

            // Total Movil Activos MPP
            celda = linea.createCell(cont++);
            celda.setCellValue("" + totalMovilsActivasMpp);
        }
        return hoja;
    }

    /**
     * Genera un stilo del excel.
     * @param color numero de Color
     * @param marcarBordes bordes en negrita
     * @param workbook hoja
     * @return CellStyle estilo
     */
    private CellStyle crearEstilo(short color, boolean marcarBordes, SXSSFWorkbook workbook) {

        CellStyle estilo = workBook.createCellStyle();
        estilo.setAlignment(CellStyle.ALIGN_CENTER);
        estilo.setVerticalAlignment((short) VerticalAlignment.MIDDLE.ordinal());
        estilo.setFillForegroundColor(color);
        if (marcarBordes) {
            estilo.setFillPattern(CellStyle.SOLID_FOREGROUND);
            estilo.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
            estilo.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
            estilo.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
            estilo.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
        }
        return estilo;
    }

    /**
     * Evalúa la existencia de algun filtro.
     * @param filtro filtro
     * @return String texto
     */
    private String getTextFiltros(FiltroReporteadorNG filtro) {
        String textoCelda = "";
        if (null != filtro.getPstSeleccionada()) {
            textoCelda = textoCelda + ("   PST: " + filtro.getPstSeleccionada().getNombre());
        }
        if (null != filtro.getEstadoSeleccionado()) {
            textoCelda = textoCelda + ("   Estado: " + filtro.getEstadoSeleccionado().getNombre());
        }
        if (null != filtro.getMunicipioSeleccionado()) {
            textoCelda = textoCelda + ("   Municipio: " + filtro.getMunicipioSeleccionado().getNombre());
        }
        if (null != filtro.getPoblacionSeleccionada()) {
            textoCelda = textoCelda + ("   Poblacion: " + filtro.getPoblacionSeleccionada().getNombre());
        }
        if (null != filtro.getAbnSeleccionado()) {
            textoCelda = textoCelda + ("   ABN: " + filtro.getAbnSeleccionado());
        }

        if (textoCelda.length() > 0) {
            textoCelda = "Para" + textoCelda;
        }
        return textoCelda;

    }

}
