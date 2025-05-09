package mx.ift.sns.negocio.cpsi;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import mx.ift.sns.modelo.cpsi.Linea1EstudioCPSI;
import mx.ift.sns.modelo.cpsi.Linea2EstudioCPSI;
import mx.ift.sns.modelo.cpsi.VEstudioCPSI;
import mx.ift.sns.negocio.utils.excel.ExcelCabeceraInfo;
import mx.ift.sns.negocio.utils.excel.ExcelCeldaInfo;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xslf.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * Clase encargada de crear los ficheros de exportación excel para el catálogo de códigos cpsi.
 * @author X50880SA
 */
public class ExportConsultaEstudioCodigosCPSI {

    /** Texto de la hoja de la exportación de datos generales. */
    public static final String TXT_ESTUDIO_CPSI = "Estudio CPS Internacional";

    /** Constante con texto "linea1". */
    public static final String TXT_LINEA1_CPSI = "linea1";

    /** Constante con texto "linea2". */
    public static final String TXT_LINEA2_CPSI = "linea2";

    /** Cabecera de la linea 1 del Excel. */
    private String[] textosCabeceraLinea1 = {"", "Proveedor", "Total Asignados",
            "Total Utilizados", "%Utilización", ""};

    /** Cabecera de la linea 2 del Excel. */
    private String[] textosCabeceraLinea2 = {"Total", "Libres", "Reservados",
            "Asignados", "Planificados", "Cuarentena", "%Asignados"};

    /** Libro Excel para el reporte. */
    private SXSSFWorkbook workBook = null;
    /** Estilo de cabecera. */
    private CellStyle estiloCabecera;
    /** Tamaños máximos de columna. */
    private int[] columnMaxSizes = null;
    /** Variable que almacena el estilo a establecer en la columna. */
    private CellStyle estiloColumna;
    /** Fuente Arial 10. */
    private Font arial10;
    /** Informacion de la primera linea del informe. */
    private Linea1EstudioCPSI linea1;
    /** Informacion de la segunda linea del informe. */
    private Linea2EstudioCPSI linea2;
    /** Numero de columnas de la primera linea del informe. */
    private int numeroColumnas1;
    /** Numero de columnas de la segunda linea del informe. */
    private int numeroColumnas2;

    /**
     * Constructor.
     * @param estudio Datos del informe a exportar en el fichero Excel.
     */
    public ExportConsultaEstudioCodigosCPSI(VEstudioCPSI estudio) {

        linea1 = estudio.getLinea1EstudioCPSI();
        linea2 = estudio.getLinea2EstudioCPSI();

        workBook = new SXSSFWorkbook(5000);
        workBook.setCompressTempFiles(true);
    }

    /**
     * Libro Excel para el reporte.
     * @return SXSSFWorkbook
     */
    public SXSSFWorkbook getLibroExcel() {
        return workBook;
    }

    /**
     * Método que devuelve las cabeceras definidas en la variable pasada por parametro.
     * @param textosCabeceras array con los nombres de las cabeceras de cada linea
     * @return ExcelCabeceraInfo
     */
    public ExcelCabeceraInfo getCabecera(String[] textosCabeceras) {

        ExcelCabeceraInfo ehi = new ExcelCabeceraInfo();
        ehi.setTitulos(textosCabeceras);

        ehi.setCellFont(ExportarExcel.ARIAL_12_BOLD);
        ehi.setCellStyle(ExportarExcel.ESTILO_GENERAL_CENTRADO);
        ehi.setInmovilizarCabecera(true);

        return ehi;
    }

    /**
     * Pinta el contenido de los objetos linea1 y linea2 en el excel.
     * @param numeroLinea numero de linea a pintar
     * @return ArrayList<ExcelCeldaInfo[]>
     */
    public ArrayList<ExcelCeldaInfo[]> getContenido(String numeroLinea) {
        ArrayList<ExcelCeldaInfo[]> contenido = new ArrayList<ExcelCeldaInfo[]>();
        ExcelCeldaInfo[] contenidoFila = null;
        ExcelCeldaInfo celda = null;

        int contCelda = 0;

        if (numeroLinea.compareTo(TXT_LINEA1_CPSI) == 0) {

            contenidoFila = this.nuevaFila(contenido, numeroColumnas1);

            celda = new ExcelCeldaInfo("", null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(linea1.getNombreProveedor(), null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(linea1.getTotalAsignados().toString(), null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(linea1.getTotalActivos().toString(), null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(linea1.getUtilizacion().toString() + "%", null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo("", null, null, false);
            contenidoFila[contCelda++] = celda;

        } else {

            contCelda = 0;
            contenidoFila = this.nuevaFila(contenido, numeroColumnas2);

            celda = new ExcelCeldaInfo(linea2.getTotal().toString(), null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(linea2.getLibres().toString(), null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(linea2.getReservados().toString(), null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(linea2.getAsignados().toString(), null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(linea2.getPlanificados().toString(), null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(linea2.getCuarentena().toString(), null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(linea2.getPorcentaje().toString(), null, null, false);
            contenidoFila[contCelda++] = celda;

        }
        return contenido;
    }

    /**
     * Agrega una nueva fila al contenedor de filas.
     * @param pConteneder Contenedor de filas
     * @param numeroColumnas numero de columnas de esta fila
     * @return nueva fila para ser editada
     */
    private ExcelCeldaInfo[] nuevaFila(ArrayList<ExcelCeldaInfo[]> pConteneder, int numeroColumnas) {
        ExcelCeldaInfo[] contenidoFila = null;

        contenidoFila = new ExcelCeldaInfo[numeroColumnas];

        pConteneder.add(contenidoFila);
        return contenidoFila;
    }

    /**************************************************************************************************/

    /**
     * Genera un fichero Excel con el contenido de la linea1 y linea2 del informe.
     * @return byte[]
     * @throws Exception lanza la excepción
     */
    public byte[] generarReporteExcel() throws Exception {

        numeroColumnas1 = 0;
        numeroColumnas2 = 0;

        // Generamos el Documento Excel.
        SXSSFWorkbook workbook = getLibroExcel();

        // Hoja Excel.
        Sheet hoja = workbook.createSheet(TXT_ESTUDIO_CPSI);

        estiloCabecera = getEstiloCabecera(workbook);

        if (linea1 != null) {
            // CabeceraLinea1
            ExcelCabeceraInfo cabeceraLinea1 = getCabecera(textosCabeceraLinea1);
            generarCabecera(hoja, cabeceraLinea1, 0);

            numeroColumnas1 = cabeceraLinea1.getTitulos().length;
            columnMaxSizes = new int[numeroColumnas1];
            for (int i = 0; i < numeroColumnas1; i++) {
                columnMaxSizes[i] = cabeceraLinea1.getTitulos()[i].length();
            }

            // ContenidoLinea1
            ArrayList<ExcelCeldaInfo[]> contenidoLinea1 = getContenido(TXT_LINEA1_CPSI);

            generarContenido(hoja, contenidoLinea1);

            for (int i = 0; i < numeroColumnas1; i++) {
                int tam = (columnMaxSizes[i] + 5) * 256;
                hoja.setColumnWidth(i, (tam > (255 * 256) ? 255 * 256 : tam));
            }

        }

        // CabeceraLinea2
        ExcelCabeceraInfo cabeceraLinea2 = getCabecera(textosCabeceraLinea2);
        if (linea1 == null) {
            generarCabecera(hoja, cabeceraLinea2, 0);
        } else {
            generarCabecera(hoja, cabeceraLinea2, 3);
        }

        numeroColumnas2 = cabeceraLinea2.getTitulos().length;
        columnMaxSizes = new int[numeroColumnas2];
        for (int i = 0; i < numeroColumnas2; i++) {
            columnMaxSizes[i] = cabeceraLinea2.getTitulos()[i].length();
        }

        // ContenidoLinea2
        ArrayList<ExcelCeldaInfo[]> contenidoLinea2 = getContenido(TXT_LINEA2_CPSI);

        generarContenido(hoja, contenidoLinea2);

        for (int i = 0; i < numeroColumnas2; i++) {
            int tam = (columnMaxSizes[i] + 5) * 256;
            hoja.setColumnWidth(i, (tam > (255 * 256) ? 255 * 256 : tam));
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        workbook.dispose();

        return baos.toByteArray();
    }

    /**
     * Establece el estilo de las columnas de la cabecera.
     * @param workbook Workbook
     * @return CellStyle cellStyle
     */
    private static CellStyle getEstiloCabecera(Workbook workbook) {
        CellStyle formatoCabecera = workbook.createCellStyle();
        formatoCabecera.setAlignment(CellStyle.ALIGN_CENTER);

        Font formatoLetra = workbook.createFont();
        formatoLetra.setFontHeightInPoints((short) 12);
        formatoLetra.setFontName("Arial");
        formatoLetra.setBoldweight(Font.BOLDWEIGHT_BOLD);
        formatoLetra.setItalic(false);

        formatoCabecera.setFont(formatoLetra);

        return formatoCabecera;
    }

    /**
     * Genera la cebecera del Excel en la hoja indicada.
     * @param pHojaExcel Hoja del libro Excel.
     * @param pInfoCabecera Información de la cabecera.
     * @param rowNumber fila en la que va a pintar la cabecera.
     * @throws Exception en caso de error.
     */
    private void generarCabecera(Sheet pHojaExcel, ExcelCabeceraInfo pInfoCabecera, int rowNumber) throws Exception {
        if (pInfoCabecera.isVisible()) {
            Row cabecera = pHojaExcel.createRow(rowNumber);
            String[] textosCabecera = pInfoCabecera.getTitulos();
            for (int i = 0; i < textosCabecera.length; i++) {
                cabecera.createCell(i).setCellValue(textosCabecera[i]);
                if (pInfoCabecera.getCellStyle() != null) {
                    // Estilo Cabecera específica.
                    cabecera.getCell(i).setCellStyle(
                            getEstilo(pInfoCabecera.getCellFont(),
                                    pHojaExcel.getWorkbook()));
                } else {
                    // Estilo Cabecera genérico.
                    cabecera.getCell(i).setCellStyle(estiloCabecera);
                }
            }

            if (pInfoCabecera.isInmovilizarCabecera()) {
                pHojaExcel.createFreezePane(0, 1);
            }
        }
    }

    /**
     * Genera el contenido de la hoja Excel.
     * @param pHojaExcel Hoja excel donde se mostrará el contenido.
     * @param pContenido Contenido a mostrar.
     * @throws Exception en caso de error.
     */
    private void generarContenido(Sheet pHojaExcel, ArrayList<ExcelCeldaInfo[]> pContenido) throws Exception {
        int contFila = pHojaExcel.getLastRowNum() + 1;
        CellStyle estiloFila = null;

        for (ExcelCeldaInfo[] contenidoFila : pContenido) {
            Row linea = pHojaExcel.createRow(contFila);
            Cell celda = null;

            for (int col = 0; col < contenidoFila.length; col++) {
                if (contenidoFila[col] != null) {
                    estiloColumna = getEstilo(contenidoFila[col].getCellFont(), pHojaExcel.getWorkbook());

                    if (contenidoFila[col].isAutoColorearFila()) {
                        estiloFila = estiloColumna;
                    }

                    celda = linea.createCell(col);
                    celda.setCellValue(contenidoFila[col].getTexto());
                    celda.setCellStyle((estiloFila != null) ? estiloFila : estiloColumna);
                } else {
                    estiloColumna = getEstilo(null, pHojaExcel.getWorkbook());
                    celda = linea.createCell(col);
                    celda.setCellValue("");
                    celda.setCellStyle((estiloFila != null) ? estiloFila : estiloColumna);
                }

                updateColumnMaxSize(celda.getStringCellValue(), col);
            }

            contFila++;
            estiloFila = null;

        }
    }

    /**
     * Método que obtiene el estilo solicitado.
     * @param fuente String
     * @param workbook excel
     * @return estilo CellStyle
     */
    private CellStyle getEstilo(String fuente, Workbook workbook) {
        CellStyle cellStyle = null;
        Font font = null;

        cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment((short) VerticalAlignment.MIDDLE.ordinal());

        font = getArial10(workbook);

        cellStyle.setFont(font);

        return cellStyle;
    }

    /**
     * Método que obtiene la fuente solicitada.
     * @param workbook excel
     * @return fuente Font
     */
    private Font getArial10(Workbook workbook) {
        if (arial10 == null) {
            arial10 = workbook.createFont();
            arial10.setFontHeightInPoints((short) 10);
            arial10.setFontName("Arial");
        }

        return arial10;
    }

    /**
     * Actualiza los tamñanos máximos de columna.
     * @param pText Texto de la celda.
     * @param pIndex Indice de columna.
     */
    private void updateColumnMaxSize(String pText, int pIndex) {
        if (pText != null) {
            if (pText.length() > columnMaxSizes[pIndex]) {
                columnMaxSizes[pIndex] = pText.length();
            }
        }
    }
}
