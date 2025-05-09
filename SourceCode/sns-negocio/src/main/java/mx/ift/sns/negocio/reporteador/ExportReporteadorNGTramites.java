package mx.ift.sns.negocio.reporteador;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroReporteadorNG;
import mx.ift.sns.modelo.reporteador.ElementoAgrupador;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xslf.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * Genera un fichero Excel con la información del reporte sobre los Tramites y numeraciones para una PST y rango de
 * fechas.
 */
public class ExportReporteadorNGTramites {

    /** Logger. */
    // private static final Logger LOGGER = LoggerFactory.getLogger(ExportReporteadorNGTramites.class);

    /** Cabecera del Excel de Tramites de NG. */
    private String[] textosCabecera = {"Período", "Tipo Trámite", "Cantidad Trámites", "Volumen Números",
            "Volumen Registros"};

    /** Lista . */
    private List<Object[]> lista;

    /** Filtro de campos. */
    private FiltroReporteadorNG filtroReporteadorNG;

    /** Libro Excel para el reporte. */
    private SXSSFWorkbook workBook = null;

    /**
     * Constructor.
     * @param pLista Lista de Totales.
     * @param pFiltro valores por los que se filtra
     */
    public ExportReporteadorNGTramites(List<Object[]> pLista, FiltroReporteadorNG pFiltro) {
        lista = pLista;
        filtroReporteadorNG = pFiltro;
        workBook = new SXSSFWorkbook(5000);
        workBook.setCompressTempFiles(true);
    }

    /**
     * Recupera el objeto SXSSFWorkbook con el libro Excel en uso.
     * @return SXSSFWorkbook
     */
    public SXSSFWorkbook getLibroExcel() {
        return workBook;
    }

    /**
     * Devuelve la hoja con los datos.
     * @param hoja Hoja a rellenar
     * @return hoja con datos
     */
    public Sheet generarContenidoTramites(Sheet hoja) {

        int contFila = 0;
        int cont = 0;

        CellStyle estiloGrey = crearEstilo(HSSFColor.GREY_25_PERCENT.index, true, this.workBook);
        CellStyle estiloCentrado = crearEstilo(HSSFColor.BLACK.index, false, this.workBook);

        Cell celda = null;

        Row linea = hoja.createRow(contFila++);
        celda = linea.createCell(cont);

        Calendar calendarFin = Calendar.getInstance();
        calendarFin.setTime(filtroReporteadorNG.getFechaFin());
        calendarFin.add(Calendar.MONTH, -1);

        String textoCabeceraFiltros = "Tramites correspondientes al PST:  "
                + filtroReporteadorNG.getPstSeleccionada().getNombre()
                + "entre las fehas "
                + FechasUtils.fechaToString(filtroReporteadorNG.getFechaInicio(), "MMMM-yyyy")
                + " y "
                + FechasUtils.fechaToString(calendarFin.getTime(), "MMMM-yyyy");
        if (null != filtroReporteadorNG.getPrimeraAgrupacion()
                && !"".equalsIgnoreCase(filtroReporteadorNG.getPrimeraAgrupacion().getCodigo())) {
            textoCabeceraFiltros = textoCabeceraFiltros + " agrupada por"
                    + (filtroReporteadorNG.getPrimeraAgrupacion().getCodigo().
                            equalsIgnoreCase(ElementoAgrupador.COD_MES) ? " meses"
                            : " años");
        }
        celda.setCellValue(textoCabeceraFiltros);

        cont = 0;
        linea = hoja.createRow(contFila++);
        for (int i = 0; i < textosCabecera.length; i++) {
            celda = linea.createCell(cont++);
            celda.setCellValue(textosCabecera[i]);
            celda.setCellStyle(estiloGrey);
            hoja.setColumnWidth(i, (textosCabecera[i].length() + 5) * 256);

        }

        String formato = "yyyy";
        String periodoAnterior = "-1";

        if (null != filtroReporteadorNG.getPrimeraAgrupacion()
                && ElementoAgrupador.COD_MES.equalsIgnoreCase(filtroReporteadorNG.getPrimeraAgrupacion().getCodigo())) {
            formato = "MMM-yy";
        }

        for (Object[] elemento : lista) {
            cont = 0;
            linea = hoja.createRow(contFila++);
            celda = linea.createCell(cont++);

            String periodo = "";
            if (null != filtroReporteadorNG.getPrimeraAgrupacion()
                    && !"".equalsIgnoreCase(filtroReporteadorNG.getPrimeraAgrupacion().getCodigo())) {
                periodo = FechasUtils.fechaToString((Date) elemento[0], formato);
                if (!periodoAnterior.equalsIgnoreCase(periodo)) {
                    celda.setCellValue(periodo);
                } else {
                    celda.setCellValue("");
                }
            } else {
                celda.setCellValue("");
            }

            periodoAnterior = periodo;

            celda = linea.createCell(cont++);
            celda.setCellValue((String) elemento[1]);

            celda = linea.createCell(cont++);
            celda.setCellValue("" + elemento[2]);
            celda.setCellStyle(estiloCentrado);

            celda = linea.createCell(cont++);
            celda.setCellValue("" + elemento[4]);
            celda.setCellStyle(estiloCentrado);

            celda = linea.createCell(cont++);
            celda.setCellValue("" + elemento[3]);
            celda.setCellStyle(estiloCentrado);

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

}
