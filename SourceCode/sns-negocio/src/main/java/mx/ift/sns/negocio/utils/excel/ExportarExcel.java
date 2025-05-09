package mx.ift.sns.negocio.utils.excel;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.dao.ng.IParametroDao;
import mx.ift.sns.modelo.abn.VCatalogoAbn;
import mx.ift.sns.modelo.central.ReporteCentralVm;
import mx.ift.sns.modelo.lineas.DetalleReporte;
import mx.ift.sns.modelo.lineas.DetalleReporteNng;
import mx.ift.sns.modelo.ot.ExportMunicipio;
import mx.ift.sns.modelo.ot.VCatalogoPoblacion;
import mx.ift.sns.modelo.series.HistoricoSerie;
import mx.ift.sns.modelo.series.HistoricoSerieNng;
import mx.ift.sns.modelo.series.VCatalogoSerie;
import mx.ift.sns.negocio.ac.ExportConsultaCatalogoAbn;
import mx.ift.sns.negocio.ng.ExportHistoricoSerieNg;
import mx.ift.sns.negocio.nng.ExportHistoricoSerieNng;
import mx.ift.sns.negocio.reporteador.ExportNGPorFechas;
import mx.ift.sns.negocio.reporteador.ExportReporteadorNGFechaActual;
import mx.ift.sns.negocio.reporteador.ExportReporteadorNGTramites;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.utils.number.NumerosUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xslf.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Clase para la exportación a excel de los catálogos.
 * @author X23016PE
 */
public class ExportarExcel {

    /** Logger de la clase. */
    // private static final Logger LOGGER = LoggerFactory.getLogger(ExportarExcel.class);

    /** Texto de la hoja de proveedores. */
    public static final String TXT_SHEET_PROVEEDORES = "Proveedores";

    /** Parametro del BBDD. */
    public static final String TAM_MAX_EXPORTACION = "tamMaxRegistrosExportacion";

    /** String de formato de fecha. */
    // public static final String FORMATO_FECHA = "dd/MM/yyyy";

    /** Estilo de cabecera. */
    private CellStyle estiloCabecera;
    /** Variable que almacena el estilo a establecer en la columna. */
    private CellStyle estiloColumna;
    /** Estilo general de una columna. */
    private CellStyle estiloGeneralC;
    /** Estilo general de una columna. */
    private CellStyle estiloGeneralL;
    /** Estilo general con wrap. */
    private CellStyle estiloGeneralWrap;
    /** Estilo general con wrap left. */
    private CellStyle estiloGeneralWrapLeft;
    /** Estilo Light Green. */
    private CellStyle estiloLG;
    /** Estilo Municipio. */
    private CellStyle estiloMunicipio;
    /** Estilo Abn. */
    private CellStyle estiloAbn;
    /** Estilo Aqua. */
    private CellStyle estiloAqua;
    /** Estilo Palid Blue. */
    private CellStyle estiloPBlue;

    /** Strings de tipos de estilos. */
    public static final String ESTILO_GENERAL_CENTRADO = "GENERALC";
    /** Strings de tipos de estilos. */
    public static final String ESTILO_GENERAL_LEFT = "GENERALL";
    /** Strings de tipos de estilos. */
    public static final String ESTILO_GENERAL_WRAP = "GENERAL_WRAP";
    /** Strings de tipos de estilos. */
    public static final String ESTILO_GENERAL_WRAP_LEFT = "GENERAL_WRAPLEFT";
    /** Strings de tipos de estilos. */
    public static final String ESTILO_LG = "LG";
    /** Strings de tipos de estilos. */
    public static final String ESTILO_MUNICIPIO = "MUNICIPIO";
    /** Strings de tipos de estilos. */
    public static final String ESTILO_ABN = "ABN";
    /** Strings de tipos de estilos. */
    public static final String ESTILO_AQUA = "AQUA";
    /** Strings de tipos de estilos. */
    public static final String ESTILO_PBLUE = "PBLUE";

    /** String para la fuente Arial 10. */
    public static final String ARIAL_10 = "ARIAL10";
    /** String para la fuente Arial 11 Bold. */
    public static final String ARIAL_11_BOLD = "ARIAL11BOLD";
    /** String para la fuente Arial 12 Bold. */
    public static final String ARIAL_12_BOLD = "ARIAL12BOLD";

    /** Fuente Arial 10. */
    private Font arial10;
    /** Fuente Arial 11 Bold. */
    private Font arial11Bold;
    /** Fuente Arial 12 Bold. */
    private Font arial12Bold;

    /** Tamaños máximos de columna. */
    private int[] columnMaxSizes = null;

    /** Tamaño máximo de exportación genérica con array previos. */
    private long tamMaxExportacion;

    /**
     * Constructor privado.
     * @param dao del parametros
     */
    public ExportarExcel(IParametroDao dao) {
        String valor = dao.getParamByName(TAM_MAX_EXPORTACION);
        tamMaxExportacion = Long.parseLong(valor);
    };

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
     * Genera un fichero Excel con el contenido indicado por los parámetros.
     * @param pNombreHoja Nombre de la hoja del Excel.
     * @param pContenido Información de Cabecera y Celdas.
     * @return Documento Serializado.
     * @throws Exception En caso de error.
     */
    public byte[] generarReporteExcel(String pNombreHoja, IExportarExcel pContenido) throws Exception {

        // Generamos el Documento Excel.
        SXSSFWorkbook workbook = pContenido.getLibroExcel();

        // Hoja Excel.
        Sheet hoja = workbook.createSheet(pNombreHoja);

        estiloCabecera = getEstiloCabecera(workbook);

        // Cabecera
        ExcelCabeceraInfo cabecera = pContenido.getCabecera();
        generarCabecera(hoja, cabecera);

        columnMaxSizes = new int[cabecera.getTitulos().length];
        for (int i = 0; i < cabecera.getTitulos().length; i++) {
            columnMaxSizes[i] = cabecera.getTitulos()[i].length();
        }

        // Contenido
        ArrayList<ExcelCeldaInfo[]> contenido = pContenido.getContenido();

        generarContenido(hoja, contenido);

        for (int i = 0; i < cabecera.getTitulos().length; i++) {
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
     * Genera un fichero Excel de Asignadas/Activas a fecha actual y con el contenido filtrado según los parámetros
     * indicados.
     * @param pNombreHoja Nombre de la hoja del Excel.
     * @param pContenido Información de Cabecera y Celdas.
     * @return Documento Serializado.
     * @throws Exception En caso de error.
     */
    public byte[] generarReporteExcelNGActual(String pNombreHoja, ExportReporteadorNGFechaActual pContenido)
            throws Exception {

        // Generamos el Documento Excel.
        SXSSFWorkbook workbook = pContenido.getLibroExcel();

        // Hoja Excel.
        Sheet hoja = workbook.createSheet(pNombreHoja);
        hoja = pContenido.generarContenidoAsignadosActivosActual(hoja);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        workbook.dispose();

        return baos.toByteArray();
    }

    /**
     * Genera un fichero Excel con el contenido indicado por los parámetros.
     * @param pNombreHoja Nombre de la hoja del Excel.
     * @param pContenido Información de Cabecera y Celdas.
     * @return Documento Serializado.
     * @throws Exception En caso de error.
     */
    public byte[] generarReporteExcelNGPorFechas(String pNombreHoja, ExportNGPorFechas pContenido) throws Exception {

        // Generamos el Documento Excel.
        SXSSFWorkbook workbook = pContenido.getLibroExcel();

        // Hoja Excel.
        Sheet hoja = workbook.createSheet(pNombreHoja);
        hoja = pContenido.generarContenidoAsignadosActivosFechas(hoja);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        workbook.dispose();

        return baos.toByteArray();
    }

    /**
     * Genera un fichero Excel con el contenido indicado por los parámetros.
     * @param pNombreHoja Nombre de la hoja del Excel.
     * @param pContenido Información de Cabecera y Celdas.
     * @return Documento Serializado.
     * @throws Exception En caso de error.
     */
    public byte[] generarReporteExcelNGTramites(String pNombreHoja, ExportReporteadorNGTramites pContenido)
            throws Exception {

        // Generamos el Documento Excel.
        SXSSFWorkbook workbook = pContenido.getLibroExcel();

        // Hoja Excel.
        Sheet hoja = workbook.createSheet(pNombreHoja);
        hoja = pContenido.generarContenidoTramites(hoja);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        workbook.dispose();

        return baos.toByteArray();
    }

    /**
     * Genera un fichero Excel con el contenido indicado por los parámetros.
     * @param pNombreHoja Nombre de la hoja del Excel.
     * @param pContenido Información de Cabecera y Celdas.
     * @param listado lista de series a exportar
     * @return Documento Serializado.
     * @throws Exception En caso de error.
     */
    public byte[] generarReporteExcelSeries(String pNombreHoja, IExportarExcel pContenido, List<VCatalogoSerie> listado)
            throws Exception {

        // Generamos el Documento Excel.
        SXSSFWorkbook workbook = pContenido.getLibroExcel();

        // Hoja Excel.
        Sheet hoja = workbook.createSheet(pNombreHoja);
        estiloCabecera = getEstiloCabecera(workbook);

        // Cabecera
        ExcelCabeceraInfo cabecera = pContenido.getCabecera();
        generarCabecera(hoja, cabecera);

        // Contenido
        columnMaxSizes = new int[cabecera.getTitulos().length];
        for (int i = 0; i < cabecera.getTitulos().length; i++) {
            columnMaxSizes[i] = cabecera.getTitulos()[i].length();
        }

        generarContenidoSerie(hoja, listado);

        for (int i = 0; i < cabecera.getTitulos().length; i++) {
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
     * Genera un fichero Excel con el contenido indicado por los parámetros.
     * @param pNombreHoja Nombre de la hoja del Excel.
     * @param pContenido Información de Cabecera y Celdas.
     * @param listado lista de series a exportar
     * @return Documento Serializado.
     * @throws Exception En caso de error.
     */
    public byte[] generarReporteExcelLineasActivasNg(String pNombreHoja, IExportarExcel pContenido,
            List<DetalleReporte> listado)
            throws Exception {

        // Generamos el Documento Excel.
        SXSSFWorkbook workbook = pContenido.getLibroExcel();

        // Hoja Excel.
        Sheet hoja = workbook.createSheet(pNombreHoja);
        estiloCabecera = getEstiloCabecera(workbook);

        // Cabecera
        ExcelCabeceraInfo cabecera = pContenido.getCabecera();
        generarCabecera(hoja, cabecera);

        // Contenido
        columnMaxSizes = new int[cabecera.getTitulos().length];
        for (int i = 0; i < cabecera.getTitulos().length; i++) {
            columnMaxSizes[i] = cabecera.getTitulos()[i].length();
        }

        generarContenidoLineaActivaNg(hoja, listado);

        for (int i = 0; i < cabecera.getTitulos().length; i++) {
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
     * Genera un fichero Excel con el contenido indicado por los parámetros.
     * @param pNombreHoja Nombre de la hoja del Excel.
     * @param pContenido Información de Cabecera y Celdas.
     * @param listado lista de series a exportar
     * @return Documento Serializado.
     * @throws Exception En caso de error.
     */
    public byte[] generarReporteExcelLineasActivasNng(String pNombreHoja, IExportarExcel pContenido,
            List<DetalleReporteNng> listado)
            throws Exception {

        // Generamos el Documento Excel.
        SXSSFWorkbook workbook = pContenido.getLibroExcel();

        // Hoja Excel.
        Sheet hoja = workbook.createSheet(pNombreHoja);
        estiloCabecera = getEstiloCabecera(workbook);

        // Cabecera
        ExcelCabeceraInfo cabecera = pContenido.getCabecera();
        generarCabecera(hoja, cabecera);

        // Contenido
        columnMaxSizes = new int[cabecera.getTitulos().length];
        for (int i = 0; i < cabecera.getTitulos().length; i++) {
            columnMaxSizes[i] = cabecera.getTitulos()[i].length();
        }

        generarContenidoLineaActivaNng(hoja, listado);

        for (int i = 0; i < cabecera.getTitulos().length; i++) {
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
     * Genera un fichero Excel con el contenido indicado por los parámetros.
     * @param pNombreHoja Nombre de la hoja del Excel.
     * @param pContenido Información de Cabecera y Celdas.
     * @param listado lista de centrales a exportar
     * @return Documento Serializado.
     * @throws Exception En caso de error.
     */
    public byte[] generarReporteExcelCentrales(String pNombreHoja, IExportarExcel pContenido,
            List<ReporteCentralVm> listado)
            throws Exception {

        // Generamos el Documento Excel.
        SXSSFWorkbook workbook = pContenido.getLibroExcel();

        // Hoja Excel.
        Sheet hoja = workbook.createSheet(pNombreHoja);
        estiloCabecera = getEstiloCabecera(workbook);

        // Cabecera
        ExcelCabeceraInfo cabecera = pContenido.getCabecera();
        generarCabecera(hoja, cabecera);

        // Contenido
        columnMaxSizes = new int[cabecera.getTitulos().length];
        for (int i = 0; i < cabecera.getTitulos().length; i++) {
            columnMaxSizes[i] = cabecera.getTitulos()[i].length();
        }

        generarContenidoCentrales(hoja, listado);

        for (int i = 0; i < cabecera.getTitulos().length; i++) {
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
     * Genera el contenido de la hoja excel de lineas activas.
     * @param hoja Sheet
     * @param listado DetalleReporte
     */
    private void generarContenidoLineaActivaNg(Sheet hoja, List<DetalleReporte> listado) {

        int contFila = hoja.getLastRowNum() + 1;

        CellStyle estiloIzq = getEstilo(ESTILO_GENERAL_LEFT, null, hoja.getWorkbook());
        CellStyle estiloCen = getEstilo(ESTILO_GENERAL_CENTRADO, null, hoja.getWorkbook());

        for (DetalleReporte detalle : listado) {
            // Nueva Fila
            Row linea = hoja.createRow(contFila++);
            Cell celda = null;
            int contCelda = 0;

            celda = linea.createCell(contCelda);
            celda.setCellValue(detalle.getConsecutivo().toString());
            celda.setCellStyle(estiloCen);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(detalle.getProveedor().getNombre());
            celda.setCellStyle(estiloIzq);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(detalle.getPoblacion().getMunicipio().getEstado().getNombre());
            celda.setCellStyle(estiloIzq);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(detalle.getPoblacion().getMunicipio().getNombre());
            celda.setCellStyle(estiloIzq);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(detalle.getPoblacion().getNombre());
            celda.setCellStyle(estiloIzq);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(detalle.getPoblacion().getAbn().getCodigoAbn().toString());
            celda.setCellStyle(estiloCen);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(FechasUtils.fechaToString(detalle.getFechaReporte()));
            celda.setCellStyle(estiloCen);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(detalle.getTotalAsignadas().toString());
            celda.setCellStyle(estiloCen);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(detalle.getTotalLineasActivas().toString());
            celda.setCellStyle(estiloCen);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(NumerosUtils.calcularPorcentajeAsString(detalle.getTotalLineasActivas(),
                    detalle.getTotalAsignadas()));
            celda.setCellStyle(estiloCen);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(detalle.getTotalAsignadasFijas().toString());
            celda.setCellStyle(estiloCen);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(detalle.getTotalLineasActivasFijas().toString());
            celda.setCellStyle(estiloCen);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(NumerosUtils.calcularPorcentajeAsString(detalle.getTotalLineasActivasFijas(),
                    detalle.getTotalAsignadasFijas()));
            celda.setCellStyle(estiloCen);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(detalle.getTotalAsignadasCpp().add(detalle.getTotalAsignadasMpp()).toString());
            celda.setCellStyle(estiloCen);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(detalle.getTotalLineasActivasCpp().add(detalle.getTotalLineasActivasMpp())
                    .toString());
            celda.setCellStyle(estiloCen);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(NumerosUtils.calcularPorcentajeAsString(
                    detalle.getTotalLineasActivasCpp().add(detalle.getTotalLineasActivasMpp()),
                    detalle.getTotalAsignadasCpp().add(detalle.getTotalAsignadasMpp())));
            celda.setCellStyle(estiloCen);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(detalle.getTotalAsignadasCpp().toString());
            celda.setCellStyle(estiloCen);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(detalle.getTotalLineasActivasCpp().toString());
            celda.setCellStyle(estiloCen);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(NumerosUtils.calcularPorcentajeAsString(detalle.getTotalLineasActivasCpp(),
                    detalle.getTotalAsignadasCpp()));
            celda.setCellStyle(estiloCen);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(detalle.getTotalAsignadasMpp().toString());
            celda.setCellStyle(estiloCen);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(detalle.getTotalLineasActivasMpp().toString());
            celda.setCellStyle(estiloCen);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(NumerosUtils.calcularPorcentajeAsString(detalle.getTotalLineasActivasMpp(),
                    detalle.getTotalAsignadasMpp()));
            celda.setCellStyle(estiloCen);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

        }

    }

    /**
     * Genera el contenido de la hoja excel de lineas activas.
     * @param hoja Sheet
     * @param listado DetalleReporte
     */
    private void generarContenidoLineaActivaNng(Sheet hoja, List<DetalleReporteNng> listado) {

        int contFila = hoja.getLastRowNum() + 1;

        CellStyle estiloIzq = getEstilo(ESTILO_GENERAL_LEFT, null, hoja.getWorkbook());
        CellStyle estiloCen = getEstilo(ESTILO_GENERAL_CENTRADO, null, hoja.getWorkbook());

        for (DetalleReporteNng detalle : listado) {
            // Nueva Fila
            Row linea = hoja.createRow(contFila++);
            Cell celda = null;
            int contCelda = 0;

            celda = linea.createCell(contCelda);
            celda.setCellValue(detalle.getConsecutivo().toString());
            celda.setCellStyle(estiloCen);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(detalle.getProveedor().getNombre());
            celda.setCellStyle(estiloIzq);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(detalle.getClaveServicio().getCodigo().toString());
            celda.setCellStyle(estiloCen);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(FechasUtils.fechaToString(detalle.getFechaReporte()));
            celda.setCellStyle(estiloCen);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(detalle.getTotalAsignadas() != null ? detalle.getTotalAsignadas().toString() : "");
            celda.setCellStyle(estiloCen);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(detalle.getTotalLineasActivas() != null
                    ? detalle.getTotalLineasActivas().toString() : "");
            celda.setCellStyle(estiloCen);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(detalle.getPorcentajeUso());
            celda.setCellStyle(estiloCen);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

        }

    }

    /**
     * Genera un fichero Excel con el contenido indicado por los parámetros.
     * @param pNombreHoja Nombre de la hoja del Excel.
     * @param pContenido Información de Cabecera y Celdas.
     * @param listado lista de series a exportar
     * @return Documento Serializado.
     * @throws Exception En caso de error.
     */
    public byte[] generarReporteExcelPoblaciones(String pNombreHoja, IExportarExcel pContenido,
            List<VCatalogoPoblacion> listado)
            throws Exception {

        // Generamos el Documento Excel.
        SXSSFWorkbook workbook = pContenido.getLibroExcel();

        // Hoja Excel.
        Sheet hoja = workbook.createSheet(pNombreHoja);
        estiloCabecera = getEstiloCabecera(workbook);

        // Cabecera
        ExcelCabeceraInfo cabecera = pContenido.getCabecera();
        generarCabecera(hoja, cabecera);

        // Contenido
        columnMaxSizes = new int[cabecera.getTitulos().length];
        for (int i = 0; i < cabecera.getTitulos().length; i++) {
            columnMaxSizes[i] = cabecera.getTitulos()[i].length();
        }

        generarContenidoPoblaciones(hoja, listado);

        for (int i = 0; i < cabecera.getTitulos().length; i++) {
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
     * Genera la cebecera del Excel en la hoja indicada.
     * @param pHojaExcel Hoja del libro Excel.
     * @param pInfoCabecera Información de la cabecera.
     * @throws Exception en caso de error.
     */
    private void generarCabecera(Sheet pHojaExcel, ExcelCabeceraInfo pInfoCabecera) throws Exception {
        if (pInfoCabecera.isVisible()) {
            Row cabecera = pHojaExcel.createRow(0);
            String[] textosCabecera = pInfoCabecera.getTitulos();
            for (int i = 0; i < textosCabecera.length; i++) {
                cabecera.createCell(i).setCellValue(textosCabecera[i]);
                if (pInfoCabecera.getCellStyle() != null) {
                    // Estilo Cabecera específica.
                    cabecera.getCell(i).setCellStyle(
                            getEstilo(pInfoCabecera.getCellStyle(), pInfoCabecera.getCellFont(),
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
                    estiloColumna = getEstilo(contenidoFila[col].getCellStyle(),
                            contenidoFila[col].getCellFont(), pHojaExcel.getWorkbook());

                    if (contenidoFila[col].isAutoColorearFila()) {
                        estiloFila = estiloColumna;
                    }

                    celda = linea.createCell(col);
                    celda.setCellValue(contenidoFila[col].getTexto());
                    celda.setCellStyle((estiloFila != null) ? estiloFila : estiloColumna);
                } else {
                    estiloColumna = getEstilo(null,
                            null, pHojaExcel.getWorkbook());
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
     * Función específica para la exportación de series.
     * @param pHojaExcel hoja excel
     * @param listado listado de series
     * @throws Exception ex
     */
    private void generarContenidoSerie(Sheet pHojaExcel, List<VCatalogoSerie> listado) throws Exception {
        int contFila = pHojaExcel.getLastRowNum() + 1;
        CellStyle estilo = getEstilo(ESTILO_GENERAL_WRAP, null, pHojaExcel.getWorkbook());
        int contCelda = 0;

        for (VCatalogoSerie vSerie : listado) {

            Row linea = pHojaExcel.createRow(contFila);
            Cell celda = null;

            celda = linea.createCell(contCelda);
            celda.setCellValue(vSerie.getIdAbn());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(vSerie.getCdgNir());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(StringUtils.leftPad(vSerie.getIdSna(), (6 - vSerie.getCdgNir().length()), '0'));
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue("0000");
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue("9999");
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(vSerie.getNombreEstatus());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            contFila++;
            contCelda = 0;
        }
    }

    /**
     * Función específica para la exportación de Abns.
     * @param pHojaExcel hoja excel
     * @param listado listado de Abns
     * @param pExportConsultaCatalogAbn ExportConsultaCatalogoAbn
     * @throws Exception ex
     */
    private void generarContenidoAbns(Sheet pHojaExcel, List<VCatalogoAbn> listado,
            ExportConsultaCatalogoAbn pExportConsultaCatalogAbn) throws Exception {

        int contCelda = 0;
        int lastAbn = -1;

        int headerAbnSize = pExportConsultaCatalogAbn.getHeaderAbn().length;
        int headerOtSize = pExportConsultaCatalogAbn.getHeaderOt().length;

        CellStyle estiloAbn = getEstilo(ESTILO_ABN, ARIAL_11_BOLD, pHojaExcel.getWorkbook());
        CellStyle eslitoPoblacion = getEstilo(ESTILO_MUNICIPIO, ARIAL_11_BOLD, pHojaExcel.getWorkbook());
        CellStyle eslitoGeneralMid = getEstilo(ESTILO_GENERAL_CENTRADO, ARIAL_10, pHojaExcel.getWorkbook());
        CellStyle eslitoGeneralLeft = getEstilo(ESTILO_GENERAL_LEFT, ARIAL_10, pHojaExcel.getWorkbook());

        int contFila = pHojaExcel.getLastRowNum();
        Row linea = null;
        Cell celda = null;

        for (VCatalogoAbn registro : listado) {
            // Nuevo ABN a mostrar
            if ((lastAbn < 0) || (lastAbn != registro.getIdAbn())) {
                // Cabecera ABN
                linea = pHojaExcel.createRow(++contFila);
                for (int i = 0; i < headerAbnSize; i++) {
                    celda = linea.createCell(i);
                    celda.setCellValue(pExportConsultaCatalogAbn.getHeaderAbn()[i]);
                    celda.setCellStyle(estiloAbn);
                    updateColumnMaxSize(celda.getStringCellValue(), i);
                }

                // Nueva Fila
                contCelda = 0;
                linea = pHojaExcel.createRow(++contFila);

                // Codigo ABN
                celda = linea.createCell(contCelda++);
                celda.setCellValue(String.valueOf(registro.getIdAbn()));
                celda.setCellStyle(eslitoGeneralMid);

                // Presuscripción
                celda = linea.createCell(contCelda++);
                celda.setCellValue(registro.getPresuscripcion());
                celda.setCellStyle(eslitoGeneralMid);

                // Población Ancla
                celda = linea.createCell(contCelda);
                celda.setCellValue(registro.getPoblacionAncla());
                celda.setCellStyle(eslitoGeneralLeft);
                updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

                // Fecha Consolidación
                celda = linea.createCell(contCelda++);
                celda.setCellValue(FechasUtils.fechaToString(registro.getFechaConsolidacion()));
                celda.setCellStyle(eslitoGeneralMid);

                // Lista de Nirs
                celda = linea.createCell(contCelda);
                celda.setCellValue(registro.getListaNirsABN());
                celda.setCellStyle(eslitoGeneralLeft);
                updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

                // Cabecera Poblaciones
                linea = pHojaExcel.createRow(++contFila);
                for (int i = 0; i < headerOtSize; i++) {
                    celda = linea.createCell(i);
                    celda.setCellValue(pExportConsultaCatalogAbn.getHeaderOt()[i]);
                    celda.setCellStyle(eslitoPoblacion);
                    updateColumnMaxSize(celda.getStringCellValue(), i);
                }

                // Actualizamos el id de ABN para las cabeceras.
                lastAbn = registro.getIdAbn();
            }

            // Nueva Fila
            contCelda = 0;
            linea = pHojaExcel.createRow(++contFila);

            // Código de Estado
            celda = linea.createCell(contCelda++);
            celda.setCellValue(registro.getIdEstado());
            celda.setCellStyle(eslitoGeneralMid);

            // Nombre de Estado
            celda = linea.createCell(contCelda);
            celda.setCellValue(registro.getNombreEstado());
            celda.setCellStyle(eslitoGeneralLeft);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            // Código de Municipio
            celda = linea.createCell(contCelda++);
            celda.setCellValue(registro.getIdMunicipio());
            celda.setCellStyle(eslitoGeneralMid);

            // Nombre de Municipio
            celda = linea.createCell(contCelda);
            celda.setCellValue(registro.getNombreMunicipio());
            celda.setCellStyle(eslitoGeneralLeft);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            // Código de Población
            celda = linea.createCell(contCelda++);
            celda.setCellValue(registro.getIdPoblacion());
            celda.setCellStyle(eslitoGeneralMid);

            // Nombre de Población
            celda = linea.createCell(contCelda);
            celda.setCellValue(registro.getNombrePoblacion());
            celda.setCellStyle(eslitoGeneralLeft);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);
        }
    }

    /**
     * Función específica para la exportación de poblaciones.
     * @param pHojaExcel hoja excel
     * @param listado listado de poblaciones
     * @throws Exception ex
     */
    private void generarContenidoPoblaciones(Sheet pHojaExcel, List<VCatalogoPoblacion> listado) throws Exception {
        int contFila = pHojaExcel.getLastRowNum() + 1;
        CellStyle estilo = getEstilo(ESTILO_GENERAL_WRAP, null, pHojaExcel.getWorkbook());
        CellStyle estiloEstado = getEstilo(ESTILO_LG, ARIAL_11_BOLD, pHojaExcel.getWorkbook());
        CellStyle estiloMunicipio = getEstilo(ESTILO_PBLUE, ARIAL_11_BOLD, pHojaExcel.getWorkbook());
        boolean cambioEstado = true;
        boolean cambioMunicipio = true;

        int contCelda = 0;
        String estado = null;
        String municipio = null;

        for (VCatalogoPoblacion poblacion : listado) {
            Cell celda = null;

            if (!poblacion.getIdEstado().equals(estado)) {
                cambioEstado = true;
            } else {
                cambioEstado = false;
            }

            if (!poblacion.getIdMunicipio().equals(municipio)) {
                cambioMunicipio = true;
            } else {
                cambioMunicipio = false;
            }

            if (cambioEstado) {
                // Nombre de Estado
                Row linea = pHojaExcel.createRow(contFila++);
                celda = linea.createCell(contCelda);
                celda.setCellValue(poblacion.getNombreEstado());
                celda.setCellStyle(estiloEstado);
                updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

                estado = poblacion.getIdEstado();

                for (int i = contCelda; i < columnMaxSizes.length; i++) {
                    celda = linea.createCell(i);
                    celda.setCellValue("");
                    celda.setCellStyle(estiloEstado);
                }
            } else {
                contCelda++;
            }

            if (cambioMunicipio) {
                // Nombre de Municipio
                Row linea = pHojaExcel.createRow(contFila++);
                celda = linea.createCell(contCelda);
                celda.setCellValue(poblacion.getNombreMunicipio());
                celda.setCellStyle(estiloMunicipio);
                updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

                municipio = poblacion.getIdMunicipio();

                for (int i = contCelda; i < columnMaxSizes.length; i++) {
                    celda = linea.createCell(i);
                    celda.setCellValue("");
                    celda.setCellStyle(estiloMunicipio);
                }
            } else {
                contCelda++;
            }

            Row linea = pHojaExcel.createRow(contFila++);
            celda = linea.createCell(contCelda);
            celda.setCellValue(poblacion.getNombrePoblacion());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(poblacion.getId());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(poblacion.getNumeracionAsignada());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(poblacion.getPresuscripcion());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(poblacion.getIdAbn().toString());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            String nirs = (poblacion.getNirs() != null) ? poblacion.getNirs().substring(1,
                    poblacion.getNirs().length() - 1) : "";
            celda.setCellValue(nirs.replaceAll(",", "\n"));
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(poblacion.getRegionAntigua());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(poblacion.getRegionCelular());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(poblacion.getRegionPcs());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            contCelda = 0;
        }
    }

    /**
     * Función específica para la exportación de series.
     * @param pHojaExcel hoja excel
     * @param listado listado de series
     * @throws Exception ex
     */
    private void generarContenidoCentrales(Sheet pHojaExcel, List<ReporteCentralVm> listado) throws Exception {
        int contFila = pHojaExcel.getLastRowNum() + 1;
        CellStyle estilo = getEstilo(ESTILO_GENERAL_WRAP, null, pHojaExcel.getWorkbook());
        int contCelda = 0;

        for (ReporteCentralVm vCentral : listado) {

            Row linea = pHojaExcel.createRow(contFila);
            Cell celda = null;

            celda = linea.createCell(contCelda);
            celda.setCellValue(vCentral.getPstNombre());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(vCentral.getNombreCentral());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue("" + vCentral.getIdAbn());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(vCentral.getEstadoNombre());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(vCentral.getMunicipioNombre());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(vCentral.getPoblacionNombre());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            contFila++;
            contCelda = 0;
        }
    }

    /**
     * Genera un fichero Excel con el contenido indicado por los parámetros.
     * @param pNombreHoja Nombre de la hoja del Excel.
     * @param pContenido Información de Cabecera y Celdas.
     * @param listado lista de ABNs a exportar
     * @return Documento Serializado.
     * @throws Exception En caso de error.
     */
    public byte[] generarReporteExcelAbns(String pNombreHoja, IExportarExcel pContenido, List<VCatalogoAbn> listado)
            throws Exception {

        // Generamos el Documento Excel.
        SXSSFWorkbook workbook = pContenido.getLibroExcel();

        // Hoja Excel.
        Sheet hoja = workbook.createSheet(pNombreHoja);
        estiloCabecera = getEstiloCabecera(workbook);

        // Cabecera
        ExcelCabeceraInfo cabecera = pContenido.getCabecera();
        generarCabecera(hoja, cabecera);

        // Contenido
        columnMaxSizes = new int[cabecera.getTitulos().length];
        for (int i = 0; i < cabecera.getTitulos().length; i++) {
            columnMaxSizes[i] = cabecera.getTitulos()[i].length();
        }

        generarContenidoAbns(hoja, listado, (ExportConsultaCatalogoAbn) pContenido);

        for (int i = 0; i < cabecera.getTitulos().length; i++) {
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
     * Crea un tipo de fuente según los parámetros indicados.
     * @param pWorkbook Libro Excel creado para el reporte.
     * @param pNombre Nombre del tipo de Fuente.
     * @param pTamanio Tamñano de Fuente
     * @param pBold Negrita
     * @param pItalic Cursiva.
     * @return Font
     */
    public Font crearFuente(XSSFWorkbook pWorkbook, String pNombre,
            int pTamanio, boolean pBold, boolean pItalic) {
        Font formatoLetra = pWorkbook.createFont();
        formatoLetra.setFontHeightInPoints((short) pTamanio);
        formatoLetra.setFontName(pNombre);
        if (pBold) {
            formatoLetra.setBoldweight(Font.BOLDWEIGHT_BOLD);
        }
        formatoLetra.setItalic(pItalic);
        return formatoLetra;
    }

    /**
     * Crea un estilo de celda según los parámetros indicados.
     * @param pWorkbook Libro Excel creado para el reporte.
     * @param pFuente Tipo de Fuente.
     * @param pAlineamiento Alineamiento de celda
     * @see CellStyle.ALIGN_CENTER
     * @return CellStyle
     */
    public CellStyle crearEstiloCelda(XSSFWorkbook pWorkbook, Font pFuente, short pAlineamiento) {
        CellStyle estiloCelda = pWorkbook.createCellStyle();
        estiloCelda.setAlignment(pAlineamiento);
        estiloCelda.setFont(pFuente);
        return estiloCelda;
    }

    /**
     * Crea un estilo de celda según los parámetros indicados.
     * @param pWorkbook Libro Excel creado para el reporte.
     * @param pFuente Tipo de Fuente.
     * @param pAlineamiento Alineamiento de celda
     * @param pForeGroundColor Índice HSSFColor del Color de Fondo.
     * @see CellStyle.ALIGN_CENTER
     * @return CellStyle
     */
    public CellStyle crearEstiloCelda(XSSFWorkbook pWorkbook, Font pFuente, short pAlineamiento,
            short pForeGroundColor) {

        CellStyle estiloCelda = pWorkbook.createCellStyle();
        estiloCelda.setAlignment(pAlineamiento);
        estiloCelda.setFont(pFuente);
        estiloCelda.setFillForegroundColor(pForeGroundColor);
        estiloCelda.setFillPattern(CellStyle.SOLID_FOREGROUND);

        return estiloCelda;
    }

    /**
     * Crea un estilo de celda según los parámetros indicados.
     * @param pWorkbook Libro Excel creado para el reporte.
     * @param pFuente Tipo de Fuente.
     * @param pAlineamiento Alineamiento de celda
     * @param pBackGroundColor Índice HSSFColor del Color de Fondo.
     * @param pForeGroundColor Índice HSSFColor del Color de Letra.
     * @see CellStyle.ALIGN_CENTER
     * @return CellStyle
     */
    public CellStyle crearEstiloCelda(XSSFWorkbook pWorkbook, Font pFuente, short pAlineamiento,
            short pBackGroundColor, short pForeGroundColor) {

        CellStyle estiloCelda = pWorkbook.createCellStyle();
        estiloCelda.setAlignment(pAlineamiento);
        estiloCelda.setFont(pFuente);
        estiloCelda.setFillBackgroundColor(pBackGroundColor);
        estiloCelda.setFillForegroundColor(pForeGroundColor);
        estiloCelda.setFillPattern(CellStyle.SOLID_FOREGROUND);

        return estiloCelda;
    }

    /**
     * Obtiene una instancia HSSFColor con el color indicado por su componente RGB.
     * @param pWorkbook Libro Excel creado para el reporte.
     * @param pRojo Componente Rojo.
     * @param pVerder Componente Verde.
     * @param pAzul Componente Azul
     * @return HSSFColor con el color indicado
     */
    public HSSFColor crearColor(HSSFWorkbook pWorkbook, int pRojo, int pVerder, int pAzul) {
        HSSFColor hssfColor = pWorkbook.getCustomPalette().findColor((byte) pRojo, (byte) pVerder, (byte) pAzul);
        if (hssfColor == null) {
            hssfColor = pWorkbook.getCustomPalette().findSimilarColor((byte) pRojo, (byte) pVerder, (byte) pAzul);
        }
        return hssfColor;
    }

    /**
     * Estilo General warp.
     * @param workbook Workbook
     * @param estilo String
     * @return CellStyle
     */
    private CellStyle getEstiloGeneralWrap(Workbook workbook, String estilo) {
        if ((ESTILO_GENERAL_WRAP.equals(estilo) || estilo == null) && estiloGeneralWrap == null) {
            if (estiloGeneralWrap == null) {
                estiloGeneralWrap = workbook.createCellStyle();
                estiloGeneralWrap.setAlignment(CellStyle.ALIGN_CENTER);

                estiloGeneralWrap.setVerticalAlignment((short) VerticalAlignment.MIDDLE.ordinal());
                estiloGeneralWrap.setWrapText(true);
            }
            return estiloGeneralWrap;
        } else if (ESTILO_GENERAL_WRAP_LEFT.equals(estilo) && estiloGeneralWrapLeft == null) {
            estiloGeneralWrapLeft = workbook.createCellStyle();
            estiloGeneralWrapLeft.setAlignment(CellStyle.ALIGN_LEFT);

            estiloGeneralWrapLeft.setVerticalAlignment((short) VerticalAlignment.MIDDLE.ordinal());
            estiloGeneralWrapLeft.setWrapText(true);
            return estiloGeneralWrapLeft;
        }
        return (ESTILO_GENERAL_WRAP_LEFT.equals(estilo)) ? estiloGeneralWrapLeft : estiloGeneralWrap;
    }

    /**
     * Estilo General.
     * @param workbook workbook
     * @param estilo alineamiento
     * @return CellStyle celda
     */
    private CellStyle getEstiloGeneral(Workbook workbook, String estilo) {
        if ((ESTILO_GENERAL_CENTRADO.equals(estilo) || estilo == null) && estiloGeneralC == null) {
            estiloGeneralC = workbook.createCellStyle();
            estiloGeneralC.setAlignment(CellStyle.ALIGN_CENTER);
            estiloGeneralC.setVerticalAlignment((short) VerticalAlignment.MIDDLE.ordinal());
            return estiloGeneralC;
        } else if (ESTILO_GENERAL_LEFT.equals(estilo) && estiloGeneralL == null) {
            estiloGeneralL = workbook.createCellStyle();
            estiloGeneralL.setAlignment(CellStyle.ALIGN_LEFT);
            estiloGeneralL.setVerticalAlignment((short) VerticalAlignment.MIDDLE.ordinal());
            return estiloGeneralL;
        }

        return (ESTILO_GENERAL_LEFT.equals(estilo)) ? estiloGeneralL : estiloGeneralC;
    }

    /**
     * Crea el estilo de celda para líneas de Estado.
     * @return CellStyle
     * @param workbook excel
     */
    private CellStyle getEstiloLG(Workbook workbook) {
        if (estiloLG == null) {
            estiloLG = workbook.createCellStyle();
            estiloLG.setAlignment(CellStyle.ALIGN_CENTER);

            estiloLG.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
            estiloLG.setFillPattern(CellStyle.SOLID_FOREGROUND);
        }

        return estiloLG;
    }

    /**
     * Crea el estilo de celda para líneas de Municipio.
     * @return CellStyle
     * @param workbook excel
     */
    private CellStyle getEstiloMunicipio(Workbook workbook) {
        if (estiloMunicipio == null) {
            estiloMunicipio = workbook.createCellStyle();
            estiloMunicipio.setAlignment(CellStyle.ALIGN_CENTER);

            estiloMunicipio.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
            estiloMunicipio.setFillPattern(CellStyle.SOLID_FOREGROUND);
        }

        return estiloMunicipio;
    }

    /**
     * Crea el estilo de celda para líneas con ABN.
     * @return CellStyle
     * @param workbook excel
     */
    private CellStyle getEstiloAbn(Workbook workbook) {
        if (estiloAbn == null) {
            estiloAbn = workbook.createCellStyle();
            estiloAbn.setAlignment(CellStyle.ALIGN_CENTER);

            estiloAbn.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
            estiloAbn.setFillPattern(CellStyle.SOLID_FOREGROUND);
        }

        return estiloAbn;
    }

    /**
     * Crea el estilo aqua 11.
     * @return CellStyle
     * @param workbook excel
     */
    private CellStyle getEstiloAqua(Workbook workbook) {
        if (estiloAqua == null) {
            estiloAqua = workbook.createCellStyle();
            estiloAqua.setAlignment(CellStyle.ALIGN_CENTER);

            estiloAqua.setFillForegroundColor(HSSFColor.AQUA.index);
            estiloAqua.setFillPattern(CellStyle.SOLID_FOREGROUND);
        }

        return estiloAqua;
    }

    /**
     * Crea el estilo pale blue.
     * @return CellStyle
     * @param workbook excel
     */
    private CellStyle getEstiloPBlue(Workbook workbook) {
        if (estiloPBlue == null) {
            estiloPBlue = workbook.createCellStyle();
            estiloPBlue.setAlignment(CellStyle.ALIGN_CENTER);

            estiloPBlue.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
            estiloPBlue.setFillPattern(CellStyle.SOLID_FOREGROUND);
        }

        return estiloPBlue;
    }

    /**
     * Método que obtiene el estilo solicitado.
     * @param estilo String
     * @param fuente String
     * @param workbook excel
     * @return estilo CellStyle
     */
    private CellStyle getEstilo(String estilo, String fuente, Workbook workbook) {
        CellStyle cellStyle = null;
        Font font = null;

        cellStyle = getStyle(estilo, workbook);
        font = getFont(fuente, workbook);

        cellStyle.setFont(font);

        return cellStyle;
    }

    /**
     * Función que obtiene el estilo solicitado para la celda.
     * @param fuente String
     * @param workbook libro excel
     * @return fuente Font
     */
    private Font getFont(String fuente, Workbook workbook) {
        Font font = null;

        if (null == fuente || fuente.isEmpty() || ARIAL_10.equals(fuente)) {
            font = getArial10(workbook);
        } else if (ARIAL_11_BOLD.equals(fuente)) {
            font = getArial11Bold(workbook);
        } else if (ARIAL_12_BOLD.equals(fuente)) {
            font = getArial12Bold(workbook);
        }

        return font;
    }

    /**
     * Función que obtiene el estilo solicitado para la celda.
     * @param estilo String
     * @param workbook libro excel
     * @return cellstyle
     */
    private CellStyle getStyle(String estilo, Workbook workbook) {
        CellStyle style = null;

        if (null == estilo || estilo.isEmpty() || ESTILO_GENERAL_CENTRADO.equals(estilo)
                || ESTILO_GENERAL_LEFT.equals(estilo)) {
            style = getEstiloGeneral(workbook, estilo);
        } else if (ESTILO_GENERAL_WRAP.equals(estilo)) {
            style = getEstiloGeneralWrap(workbook, estilo);
        } else if (ESTILO_GENERAL_WRAP_LEFT.equals(estilo)) {
            style = getEstiloGeneralWrap(workbook, estilo);
        } else if (ESTILO_ABN.equals(estilo)) {
            style = getEstiloAbn(workbook);
        } else if (ESTILO_LG.equals(estilo)) {
            style = getEstiloLG(workbook);
        } else if (ESTILO_MUNICIPIO.equals(estilo)) {
            style = getEstiloMunicipio(workbook);
        } else if (ESTILO_AQUA.equals(estilo)) {
            style = getEstiloAqua(workbook);
        } else if (ESTILO_PBLUE.equals(estilo)) {
            style = getEstiloPBlue(workbook);
        }

        return style;
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
     * Método que obtiene la fuente solicitada.
     * @param workbook excel
     * @return fuente Font
     */
    private Font getArial11Bold(Workbook workbook) {
        if (arial11Bold == null) {
            arial11Bold = workbook.createFont();
            arial11Bold.setFontHeightInPoints((short) 11);
            arial11Bold.setFontName("Arial");
            arial11Bold.setBoldweight(Font.BOLDWEIGHT_BOLD);
        }

        return arial11Bold;
    }

    /**
     * Método que obtiene la fuente solicitada.
     * @param workbook excel
     * @return fuente Font
     */
    private Font getArial12Bold(Workbook workbook) {
        if (arial12Bold == null) {
            arial12Bold = workbook.createFont();
            arial12Bold.setFontHeightInPoints((short) 12);
            arial12Bold.setFontName("Arial");
            arial12Bold.setBoldweight(Font.BOLDWEIGHT_BOLD);
        }

        return arial12Bold;
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

    /**
     * @return the tamMaxExportacion
     */
    public long getTamMaxExportacion() {
        return tamMaxExportacion;
    }

    /**
     * @param tamMaxExportacion the tamMaxExportacion to set
     */
    public void setTamMaxExportacion(long tamMaxExportacion) {
        this.tamMaxExportacion = tamMaxExportacion;
    }

    /**
     * Genera un fichero Excel con el contenido indicado por los parámetros.
     * @param pNombreHoja Nombre de la hoja del Excel.
     * @param pContenido Información de Cabecera y Celdas.
     * @param listado lista de series a exportar
     * @return Documento Serializado.
     * @throws Exception En caso de error.
     */
    public byte[] generarReporteExcelMunicipios(String pNombreHoja, IExportarExcel pContenido,
            List<ExportMunicipio> listado) throws Exception {
        // Generamos el Documento Excel.
        SXSSFWorkbook workbook = pContenido.getLibroExcel();

        // Hoja Excel.
        Sheet hoja = workbook.createSheet(pNombreHoja);
        estiloCabecera = getEstiloCabecera(workbook);

        // Cabecera
        ExcelCabeceraInfo cabecera = pContenido.getCabecera();
        generarCabecera(hoja, cabecera);

        // Contenido
        columnMaxSizes = new int[cabecera.getTitulos().length];
        for (int i = 0; i < cabecera.getTitulos().length; i++) {
            columnMaxSizes[i] = cabecera.getTitulos()[i].length();
        }

        generarContenidoMunicipios(hoja, listado);

        for (int i = 0; i < cabecera.getTitulos().length; i++) {
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
     * Carga un listado de municipios en un excel.
     * @param pHojaExcel hoja
     * @param listado datos
     */
    private void generarContenidoMunicipios(Sheet pHojaExcel, List<ExportMunicipio> listado) {

        int contFila = pHojaExcel.getLastRowNum() + 1;
        CellStyle estilo = getEstilo(ESTILO_GENERAL_WRAP, null, pHojaExcel.getWorkbook());
        CellStyle estiloMunicipio = getEstilo(ESTILO_AQUA, ARIAL_11_BOLD, pHojaExcel.getWorkbook());
        CellStyle estiloIzq = getEstilo(ESTILO_GENERAL_LEFT, null, pHojaExcel.getWorkbook());

        String idMunicipio = "";

        for (ExportMunicipio dato : listado) {
            Cell celda = null;
            Row linea = pHojaExcel.createRow(contFila++);

            // Cargamos los datos del municipio
            if (!idMunicipio.equals(dato.getIdEstado() + dato.getIdMunicipio())) {
                int contCelda = 0;
                idMunicipio = dato.getIdEstado() + dato.getIdMunicipio();

                celda = linea.createCell(contCelda);
                celda.setCellValue(dato.getNombreEstado());
                celda.setCellStyle(estiloMunicipio);
                updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

                celda = linea.createCell(contCelda);
                celda.setCellValue(dato.getNombreMunicipio());
                celda.setCellStyle(estiloMunicipio);
                updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

                celda = linea.createCell(contCelda);
                celda.setCellValue(dato.getNumeracionAsignada());
                celda.setCellStyle(estiloMunicipio);
                updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

                celda = linea.createCell(contCelda);
                celda.setCellValue((dato.getRegionCelular() != null) ? dato.getRegionCelular().toString() : "");
                celda.setCellStyle(estiloMunicipio);
                updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

                celda = linea.createCell(contCelda);
                celda.setCellValue((dato.getRegionPcs() != null) ? dato.getRegionPcs().toString() : "");
                celda.setCellStyle(estiloMunicipio);
                updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

                linea = pHojaExcel.createRow(contFila++);

            }

            if (dato.getClaveCensal() != null) {
                // Cargamos los datos de cada poblacion
                int contCeldaPob = 5;

                celda = linea.createCell(contCeldaPob);
                celda.setCellValue(dato.getClaveCensal());
                celda.setCellStyle(estilo);
                updateColumnMaxSize(celda.getStringCellValue(), contCeldaPob++);

                celda = linea.createCell(contCeldaPob);
                celda.setCellValue(dato.getNombrePoblacion());
                celda.setCellStyle(estiloIzq);
                updateColumnMaxSize(celda.getStringCellValue(), contCeldaPob++);

                celda = linea.createCell(contCeldaPob);
                celda.setCellValue((dato.getIdAbn() != null) ? dato.getIdAbn().toString() : "");
                celda.setCellStyle(estilo);
                updateColumnMaxSize(celda.getStringCellValue(), contCeldaPob++);

                celda = linea.createCell(contCeldaPob);
                celda.setCellValue(dato.getPresuscripcion());
                celda.setCellStyle(estilo);
                updateColumnMaxSize(celda.getStringCellValue(), contCeldaPob++);

            }

        }

    }

    /**
     * Genera un fichero Excel del historico de series NNG.
     * @param pNombreHoja Nombre de la hoja del Excel.
     * @param pContenido Información de Cabecera y Celdas.
     * @return Documento Serializado.
     * @throws Exception En caso de error.
     */
    public byte[] generarReporteExcelHistoricoSeriesNng(String pNombreHoja, ExportHistoricoSerieNng pContenido,
            List<HistoricoSerieNng> listado) throws Exception {

        // Generamos el Documento Excel.
        SXSSFWorkbook workbook = pContenido.getLibroExcel();

        // Hoja Excel.
        Sheet hoja = workbook.createSheet(pNombreHoja);
        estiloCabecera = getEstiloCabecera(workbook);

        // Cabecera
        ExcelCabeceraInfo cabecera = pContenido.getCabecera();
        generarCabecera(hoja, cabecera);

        // Contenido
        columnMaxSizes = new int[cabecera.getTitulos().length];
        for (int i = 0; i < cabecera.getTitulos().length; i++) {
            columnMaxSizes[i] = cabecera.getTitulos()[i].length();
        }

        generarContenidoHistoricoSeriesNng(hoja, listado);

        for (int i = 0; i < cabecera.getTitulos().length; i++) {
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
     * Carga un historico de series NNG en un excel.
     * @param pHojaExcel hoja
     * @param listado datos
     */
    private void generarContenidoHistoricoSeriesNng(Sheet pHojaExcel, List<HistoricoSerieNng> listado) {

        int contFila = pHojaExcel.getLastRowNum() + 1;
        CellStyle estilo = getEstilo(ESTILO_GENERAL_WRAP, null, pHojaExcel.getWorkbook());
        CellStyle estiloIzq = getEstilo(ESTILO_GENERAL_LEFT, null, pHojaExcel.getWorkbook());

        for (HistoricoSerieNng historico : listado) {

            Row linea = pHojaExcel.createRow(contFila);
            Cell celda = null;
            int contCelda = 0;

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getConsecutivo().toString());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getNumeroOficio());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getTipoMovimiento());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(FechasUtils.fechaToString(historico.getFechaTramite()));
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getNombrePst());
            celda.setCellStyle(estiloIzq);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getPst().getBcd().toString());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getIda().toString());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getClaveServicio().toString());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getSerieAsString());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getNumInicial());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getNumFinal());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);
        }

    }

    /**
     * Carga un historico de series NG en un excel.
     * @param pHojaExcel hoja
     * @param listado datos
     * @throws Exception error
     */
    private void generarContenidoHistoricoSeriesNg(Sheet pHojaExcel, List<HistoricoSerie> listado) throws Exception {

        int contFila = pHojaExcel.getLastRowNum() + 1;
        CellStyle estilo = getEstilo(ESTILO_GENERAL_WRAP, null, pHojaExcel.getWorkbook());
        CellStyle estiloIzq = getEstilo(ESTILO_GENERAL_LEFT, null, pHojaExcel.getWorkbook());

        for (HistoricoSerie historico : listado) {

            Row linea = pHojaExcel.createRow(contFila);
            Cell celda = null;
            int contCelda = 0;

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getConsecutivo().toString());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getTiposolicitud());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getNumOficio());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getNombrePst());
            celda.setCellStyle(estiloIzq);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getIdoAsString());
            celda.setCellStyle(estiloIzq);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getIdaAsString());
            celda.setCellStyle(estiloIzq);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(FechasUtils.fechaToString(historico.getFechaAsignacion()));
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getIdAbn() != null ? historico.getIdAbn().toString() : "");
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getIda().toString());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getCdgNir().toString());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getSnaAsString());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getInicioRango());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getFinRango());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getIdTipoRed());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getIdTipoModalidad());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getEstado());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getMunicipio());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getPoblacion());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);

            celda = linea.createCell(contCelda);
            celda.setCellValue(historico.getInegi());
            celda.setCellStyle(estilo);
            updateColumnMaxSize(celda.getStringCellValue(), contCelda++);
        }

    }

    /**
     * Genera un fichero Excel del historico de series NNG.
     * @param pNombreHoja Nombre de la hoja del Excel.
     * @param pContenido Información de Cabecera y Celdas.
     * @param listado datos
     * @return Documento Serializado.
     * @throws Exception En caso de error.
     */
    public byte[] generarReporteExcelHistoricoSeriesNg(String pNombreHoja, ExportHistoricoSerieNg pContenido,
            List<HistoricoSerie> listado) throws Exception {

        // Generamos el Documento Excel.
        SXSSFWorkbook workbook = pContenido.getLibroExcel();

        // Hoja Excel.
        Sheet hoja = workbook.createSheet(pNombreHoja);
        estiloCabecera = getEstiloCabecera(workbook);

        // Cabecera
        ExcelCabeceraInfo cabecera = pContenido.getCabecera();
        generarCabecera(hoja, cabecera);

        // Contenido
        columnMaxSizes = new int[cabecera.getTitulos().length];
        for (int i = 0; i < cabecera.getTitulos().length; i++) {
            columnMaxSizes[i] = cabecera.getTitulos()[i].length();
        }

        generarContenidoHistoricoSeriesNg(hoja, listado);

        for (int i = 0; i < cabecera.getTitulos().length; i++) {
            int tam = (columnMaxSizes[i] + 5) * 256;
            hoja.setColumnWidth(i, (tam > (255 * 256) ? 255 * 256 : tam));
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        workbook.dispose();

        return baos.toByteArray();

    }
}
