package mx.ift.sns.negocio.ng;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import mx.ift.sns.dao.ng.IDetalleReporteDao;
import mx.ift.sns.dao.ng.INumeracionSolicitadaDAO;
import mx.ift.sns.dao.nng.IDetalleLineaActivaDetNngDao;
import mx.ift.sns.dao.nng.IDetalleLineaActivaNngDao;
import mx.ift.sns.dao.nng.IDetalleLineaArrendatarioNngDao;
import mx.ift.sns.dao.nng.IDetalleReporteNngDao;
import mx.ift.sns.dao.nng.INumeracionAsignadaNngDao;
import mx.ift.sns.dao.nng.INumeracionSolicitadaNngDao;
import mx.ift.sns.dao.nng.IRangoSerieNngDao;
import mx.ift.sns.dao.nng.IReporteNngDao;
import mx.ift.sns.modelo.lineas.DetalleReporte;
import mx.ift.sns.modelo.lineas.ReporteNng;
import mx.ift.sns.modelo.lineas.TipoReporte;
import mx.ift.sns.modelo.ng.SolicitudAsignacion;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.nng.NumeracionSolicitadaNng;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.utils.number.NumerosUtils;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Genera el analisis historico de asignaciones de un proveedor.
 * @author X36155QU
 */
@Stateless(mappedName = "GenerarAnalisisNumeracionService")
@Remote(IGenerarAnalisisNumeracion.class)
public class GenerarAnalisisNumeracionService implements IGenerarAnalisisNumeracion {

    /** Literales Cabeceras. */
    private static final String[] CABECERAS = {"NUMERACION ASIGNADA", "NUMERACIÓN SOLICITADA", "NUMERACIÓN ACTIVA",
            "PORCENTAJE DE UTILIZACIÓN."};

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(GenerarAnalisisNumeracionService.class);

    /** DAO para Numeraciones Solicitadas NG. */
    @Inject
    private INumeracionSolicitadaDAO numSolDao;

    /** DAO para Numeraciones Solicitadas NNG. */
    @Inject
    private INumeracionSolicitadaNngDao numSolNngDao;

    /** DAO para Numeraciones Asignadas NNG. */
    @Inject
    private INumeracionAsignadaNngDao numAsigDao;

    /** DAO para Rango Series NNG. */
    @Inject
    private IRangoSerieNngDao rangoSerieNngDao;

    /** DAO para Detalle de Lineas Activa. */
    @Inject
    private IDetalleReporteDao detalleReporteDao;

    /** DAO para Detalle de Lineas Activa. */
    @Inject
    private IDetalleReporteNngDao detalleReporteNngDao;

    /** DAO para Detalle de Lineas Activa. */
    @Inject
    private IDetalleLineaActivaNngDao detalleLineaActNngDao;

    /** DAO para Detalle de Lineas Activa Detallada. */
    @Inject
    private IDetalleLineaActivaDetNngDao detalleLineaActDetNngDao;

    /** DAO para Detalle de Lineas Activa Arrendatario. */
    @Inject
    private IDetalleLineaArrendatarioNngDao detalleLineaArrendatarioNngDao;

    /** DAO para Reportes NNG. */
    @Inject
    private IReporteNngDao reporteNngDao;

    /** Estilo cabecera PST. */
    private XSSFCellStyle styleCabPST;

    /** Estilo CABECERAS. */
    private XSSFCellStyle styleCab;

    /** Estilo subCABECERAS. */
    private XSSFCellStyle styleSubCab;

    /** Estilo celda general. */
    private XSSFCellStyle styleCell;

    /** Estilo entre bloques. */
    private XSSFCellStyle styleCellX;

    /** Estilo columna. */
    private XSSFCellStyle styleCellCol;

    /** Estilo totales. */
    private XSSFCellStyle styleCellTotales;

    /** Estilo para fechas. */
    private XSSFCellStyle styleCellFecha;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public byte[] generarAnalisisNumeracion(SolicitudAsignacion solicitud) throws Exception {
        LOGGER.debug("INICIO");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            XSSFWorkbook wb = new XSSFWorkbook();

            // Creamos las hojas del archivo.
            XSSFSheet hojaPoblacion = wb.createSheet("POBLACION");
            XSSFSheet hojaAbn = wb.createSheet("ABN");

            cargarAnchosColumnas(hojaPoblacion);
            cargarAnchosColumnas(hojaAbn);

            cargarEstilos(wb);

            cargarExcelPorPoblacion(solicitud, hojaPoblacion, solicitud.getProveedorSolicitante());

            // Cargamos la hoja por ABN.
            crearCeldaPst(hojaAbn, solicitud.getProveedorSolicitante());

            cargarExcelPorAbn(solicitud, hojaAbn, solicitud.getProveedorSolicitante());

            wb.getCreationHelper().createFormulaEvaluator().evaluateAll();

            wb.write(bos);

            wb.close();
            bos.close();

        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        return bos.toByteArray();

    }

    /**
     * Caraga la hoja por ABN.
     * @param solicitud asignacion
     * @param hojaAbn hojaAbn
     * @param proveedor proveedor
     */
    private void cargarExcelPorAbn(SolicitudAsignacion solicitud, XSSFSheet hojaAbn, Proveedor proveedor) {
        LOGGER.debug("");

        List<Object[]> listaPorAbn = numSolDao.findAllNumSolicitadasBySolicitudGroupByAbn(solicitud);

        BigDecimal abn = null;

        int fila = 1;

        for (int i = 0; i < listaPorAbn.size(); i++) {

            BigDecimal abnAux = (BigDecimal) listaPorAbn.get(i)[0];
            // Pintamos las cabeceras

            if (abn == null || !abn.equals(abnAux)) {
                abn = abnAux;
                fila = pintarCabeceras(fila, hojaAbn, "ABN", abn);
                fila = crearSubCabeceras(hojaAbn, fila);
            }
            // Pintamos los datos por fecha
            if (abn.equals(abnAux)) {

                Calendar fechaAsignacion = Calendar.getInstance();
                fechaAsignacion.setTime((Date) listaPorAbn.get(i)[1]);

                fila++;
                XSSFRow rowData = hojaAbn.createRow(fila);

                XSSFCell celdaData = rowData.createCell(1);
                celdaData.setCellStyle(styleCellFecha);
                celdaData.setCellValue(fechaAsignacion);

                celdaData = rowData.createCell(2);
                celdaData.setCellStyle(styleCell);
                celdaData.setCellValue(((BigDecimal) listaPorAbn.get(i)[3]).longValue());

                celdaData = rowData.createCell(3);
                celdaData.setCellStyle(styleCell);
                celdaData.setCellValue(((BigDecimal) listaPorAbn.get(i)[4]).longValue()
                        + ((BigDecimal) listaPorAbn.get(i)[5]).longValue());
                celdaData = rowData.createCell(4);
                celdaData.setCellStyle(styleCell);
                celdaData.setCellValue(((BigDecimal) listaPorAbn.get(i)[4]).longValue());
                celdaData = rowData.createCell(5);
                celdaData.setCellStyle(styleCell);
                celdaData.setCellValue(((BigDecimal) listaPorAbn.get(i)[5]).longValue());
                celdaData = rowData.createCell(6);
                celdaData.setCellStyle(styleCell);
                celdaData.setCellValue(((BigDecimal) listaPorAbn.get(i)[2]).longValue());

                for (int j = 1; j < CABECERAS.length; j++) {
                    int iniCab = 1 + (6 * j);
                    for (int k = 0; k < 6; k++) {
                        if (k == 0) {
                            XSSFCell celdaX = rowData.createCell(iniCab);
                            celdaX.setCellStyle(styleCellX);
                        } else {
                            XSSFCell celda = rowData.createCell(iniCab + k);
                            celda.setCellStyle(styleCell);
                        }
                    }
                }
            }

            BigDecimal nextAbn = null;
            if (i < listaPorAbn.size() - 1) {
                nextAbn = (BigDecimal) listaPorAbn.get(i + 1)[0];
            }

            if (!abn.equals(nextAbn)) {
                // Pintamos los datos por año

                fila++;
                XSSFRow rowSubtotales = hojaAbn.createRow(fila);

                XSSFCell celdaSubtotales = rowSubtotales.createCell(1);
                celdaSubtotales.setCellStyle(styleCellTotales);
                celdaSubtotales.setCellValue("SUBTOTALES");

                for (int j = 0; j < CABECERAS.length; j++) {
                    int iniCab = 1 + (6 * j);
                    for (int k = 0; k < 6; k++) {
                        if (k == 0 && j > 0) {
                            XSSFCell celdaX = rowSubtotales.createCell(iniCab);
                            celdaX.setCellStyle(styleCellX);
                        } else if (k > 0) {
                            XSSFCell celda = rowSubtotales.createCell(iniCab + k);
                            celda.setCellStyle(styleCell);
                        }
                    }
                }
                // Pintamos los totales por año
                List<Object[]> totalesAnios = numSolDao.findAllNumeracionAsignadaByAbnGroupByAnio(abn, proveedor);
                fila = cargarFilasPorAnios(totalesAnios, fila, hojaAbn);

                // Pintamos totales asignadas
                fila++;
                XSSFRow rowTotal = hojaAbn.createRow(fila);
                int filaTotales = fila;
                int col = 1;

                XSSFCell celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCellTotales);
                celdaTotal.setCellValue("TOTALES");

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                celdaTotal
                        .setCellFormula(getTotalColumna("C", filaTotales - totalesAnios.size() + 1, filaTotales));

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                celdaTotal
                        .setCellFormula(getTotalColumna("D", filaTotales - totalesAnios.size() + 1, filaTotales));

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                celdaTotal
                        .setCellFormula(getTotalColumna("E", filaTotales - totalesAnios.size() + 1, filaTotales));

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                celdaTotal
                        .setCellFormula(getTotalColumna("F", filaTotales - totalesAnios.size() + 1, filaTotales));

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                celdaTotal
                        .setCellFormula(getTotalColumna("G", filaTotales - totalesAnios.size() + 1, filaTotales));

                XSSFCell celdaX = rowTotal.createCell(col++);
                celdaX.setCellStyle(styleCellX);

                // Pintamos totales solictadas
                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Long totalFijoSol = numSolDao.getTotalNumSolicitadasByAbn("F", "", abn, proveedor)
                        .longValue();
                celdaTotal.setCellValue(totalFijoSol);

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Long totalMovilSol = numSolDao.getTotalNumSolicitadasByAbn("M", "", abn, proveedor)
                        .longValue();
                celdaTotal.setCellValue(totalMovilSol);

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Long totalCppSol = numSolDao.getTotalNumSolicitadasByAbn("M", "CPP", abn, proveedor)
                        .longValue();
                celdaTotal.setCellValue(totalCppSol);

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Long totalMppSol = numSolDao.getTotalNumSolicitadasByAbn("M", "MPP", abn, proveedor)
                        .longValue();
                celdaTotal.setCellValue(totalMppSol);

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Long totalSol = numSolDao.getTotalNumSolicitadasByAbn("", "", abn, proveedor)
                        .longValue();
                celdaTotal.setCellValue(totalSol);

                celdaX = rowTotal.createCell(col++);
                celdaX.setCellStyle(styleCellX);

                // Totales lineas activas
                Object[] detalle = detalleReporteDao.getTotalDetalleReporteByAbn(proveedor, abn);

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Long totalFijoAct = ((BigDecimal) detalle[1]).longValue();
                celdaTotal.setCellValue(totalFijoAct);

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Long totalMovilAct = ((BigDecimal) detalle[2]).longValue() + ((BigDecimal) detalle[3]).longValue();
                celdaTotal.setCellValue(totalMovilAct);

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Long totalCppAct = ((BigDecimal) detalle[2]).longValue();
                celdaTotal.setCellValue(totalCppAct);

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Long totalMppAct = ((BigDecimal) detalle[3]).longValue();
                celdaTotal.setCellValue(totalMppAct);

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Long totalAct = ((BigDecimal) detalle[0]).longValue();
                celdaTotal.setCellValue(totalAct);

                celdaX = rowTotal.createCell(col++);
                celdaX.setCellStyle(styleCellX);

                // Pintamos porcentaje de uso

                // Object[] totalDetalles = detalleReporteDao.getTotalDetalleReporteByAbn(null, abn);
                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Long totalFijoActAbn = ((BigDecimal) detalle[1]).longValue();
                Long totalFijoAsigAbn = ((BigDecimal) detalle[5]).longValue();
                celdaTotal.setCellValue(NumerosUtils.calcularPorcentajeAsString(totalFijoActAbn, totalFijoAsigAbn));

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Long totalMovilActAbn = ((BigDecimal) detalle[2]).longValue()
                        + ((BigDecimal) detalle[3]).longValue();
                Long totalMovilAsigAbn = ((BigDecimal) detalle[6]).longValue()
                        + ((BigDecimal) detalle[7]).longValue();
                celdaTotal.setCellValue(NumerosUtils.calcularPorcentajeAsString(totalMovilActAbn, totalMovilAsigAbn));

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Long totalCppActAbn = ((BigDecimal) detalle[2]).longValue();
                Long totalCppAsigAbn = ((BigDecimal) detalle[6]).longValue();
                celdaTotal.setCellValue(NumerosUtils.calcularPorcentajeAsString(totalCppActAbn, totalCppAsigAbn));

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Long totalMppActAbn = ((BigDecimal) detalle[3]).longValue();
                Long totalMppAsigAbn = ((BigDecimal) detalle[7]).longValue();
                celdaTotal.setCellValue(NumerosUtils.calcularPorcentajeAsString(totalMppActAbn, totalMppAsigAbn));

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Long totalActAbn = ((BigDecimal) detalle[0]).longValue();
                Long totalAsigAbn = ((BigDecimal) detalle[4]).longValue();
                celdaTotal.setCellValue(NumerosUtils.calcularPorcentajeAsString(totalActAbn, totalAsigAbn));

                fila++;
                rowTotal = hojaAbn.createRow(fila);
            }

        }
        LOGGER.debug("FIN");
    }

    /**
     * Pintas las cabeceras generales (ABN,inegi,Clave).
     * @param fila fila
     * @param hoja Sheet
     * @param initStr inicio de cabecera
     * @param dato dato
     * @return fila
     */
    private int pintarCabeceras(int fila, XSSFSheet hoja, String initStr, Object dato) {
        fila++;
        XSSFRow cabAbn = hoja.createRow(fila);
        XSSFCell celdaAbn = cabAbn.createCell(1);
        CellRangeAddress regionCab = new CellRangeAddress(fila, fila, 1, 24);
        pintarStyleRegion(regionCab, styleCab, Color.ORANGE, hoja);
        hoja.addMergedRegion(regionCab);
        celdaAbn.setCellValue(initStr + dato);

        return fila;
    }

    /**
     * Crear las subcabeceras.
     * @param hoja hoja
     * @param fila fila
     * @return fila
     */
    private int crearSubCabeceras(XSSFSheet hoja, int fila) {
        fila++;

        XSSFRow subCab = hoja.createRow(fila);
        XSSFCell celda = subCab.createCell(1);
        celda.setCellStyle(styleCell);

        for (int j = 0; j < CABECERAS.length; j++) {

            int iniCab = 2 + (6 * j);

            XSSFCell celdaSubCab = subCab.createCell(iniCab);
            CellRangeAddress regionSubCab = new CellRangeAddress(fila, fila, iniCab, iniCab + 4);
            pintarStyleRegion(regionSubCab, styleSubCab, null, hoja);
            hoja.addMergedRegion(regionSubCab);

            celdaSubCab.setCellValue(CABECERAS[j]);
            if (j < CABECERAS.length - 1) {
                XSSFCell celdaX = subCab.createCell(iniCab + 5);
                celdaX.setCellStyle(styleCellX);
            }

        }

        fila++;
        XSSFRow colRow = hoja.createRow(fila);

        for (int j = 0; j < CABECERAS.length; j++) {
            int iniCab = 1 + (6 * j);
            if (j == 0) {
                XSSFCell celdaColFechas = colRow.createCell(iniCab);
                celdaColFechas.setCellStyle(styleCellCol);
                celdaColFechas.setCellValue("FECHA DE ASIGNACION");
            } else {
                XSSFCell celdaX = colRow.createCell(iniCab);
                celdaX.setCellStyle(styleCellX);
            }

            if (j < CABECERAS.length - 1) {
                XSSFCell celdaColFijo = colRow.createCell(iniCab + 1);
                celdaColFijo.setCellStyle(styleCellCol);
                celdaColFijo.setCellValue("FIJO");
                XSSFCell celdaColMovil = colRow.createCell(iniCab + 2);
                celdaColMovil.setCellStyle(styleCellCol);
                celdaColMovil.setCellValue("MOVIL");
                XSSFCell celdaColCpp = colRow.createCell(iniCab + 3);
                celdaColCpp.setCellStyle(styleCellCol);
                celdaColCpp.setCellValue("CPP");
                XSSFCell celdaColMpp = colRow.createCell(iniCab + 4);
                celdaColMpp.setCellStyle(styleCellCol);
                celdaColMpp.setCellValue("MPP");
                XSSFCell celdaColTotal = colRow.createCell(iniCab + 5);
                celdaColTotal.setCellStyle(styleCellCol);
                celdaColTotal.setCellValue("Tot-Gral (Fijo-Móvil)");
            } else {
                XSSFCell celdaColFijo = colRow.createCell(iniCab + 1);
                celdaColFijo.setCellStyle(styleCellCol);
                celdaColFijo.setCellValue("% FIJO");
                XSSFCell celdaColMovil = colRow.createCell(iniCab + 2);
                celdaColMovil.setCellStyle(styleCellCol);
                celdaColMovil.setCellValue("% MOVIL");
                XSSFCell celdaColCpp = colRow.createCell(iniCab + 3);
                celdaColCpp.setCellStyle(styleCellCol);
                celdaColCpp.setCellValue("% CPP");
                XSSFCell celdaColMpp = colRow.createCell(iniCab + 4);
                celdaColMpp.setCellStyle(styleCellCol);
                celdaColMpp.setCellValue("% MPP");
                XSSFCell celdaColTotal = colRow.createCell(iniCab + 5);
                celdaColTotal.setCellStyle(styleCellCol);
                celdaColTotal.setCellValue("% General");
            }

        }
        return fila;
    }

    /**
     * Carga la hoja por poblacion.
     * @param solicitud asignacion
     * @param hojaPoblacion hojaPoblacion
     * @param proveedor proveedor
     */
    private void cargarExcelPorPoblacion(SolicitudAsignacion solicitud, XSSFSheet hojaPoblacion, Proveedor proveedor) {
        LOGGER.debug("");
        // Cargamos la hoja por poblacion.
        crearCeldaPst(hojaPoblacion, solicitud.getProveedorSolicitante());

        // Creamos una lista aux con el historico de las numeraciones solicitadas de un proveedor por poblacion
        List<Object[]> listaPorPoblacion = numSolDao.findAllNumSolicitadasBySolicitudGroupByPoblacion(solicitud);

        String poblacion = null;

        int fila = 1;

        for (int i = 0; i < listaPorPoblacion.size(); i++) {

            String poblacionAux = (String) listaPorPoblacion.get(i)[0];

            if (poblacion == null || !poblacion.equals(poblacionAux)) {

                poblacion = poblacionAux;

                fila = pintarCabeceras(fila, hojaPoblacion, "", listaPorPoblacion.get(i)[2]);
                fila = crearSubCabeceras(hojaPoblacion, fila);
            }

            if (poblacion.equals(poblacionAux)) {

                Calendar fechaAsignacion = Calendar.getInstance();
                fechaAsignacion.setTime((Date) listaPorPoblacion.get(i)[1]);

                fila++;
                XSSFRow rowData = hojaPoblacion.createRow(fila);

                XSSFCell celdaData = rowData.createCell(1);
                celdaData.setCellStyle(styleCellFecha);
                celdaData.setCellValue(fechaAsignacion);

                celdaData = rowData.createCell(2);
                celdaData.setCellStyle(styleCell);

                celdaData.setCellValue(((BigDecimal) listaPorPoblacion.get(i)[4]).longValue());

                celdaData = rowData.createCell(3);
                celdaData.setCellStyle(styleCell);
                celdaData.setCellValue(((BigDecimal) listaPorPoblacion.get(i)[5]).longValue()
                        + ((BigDecimal) listaPorPoblacion.get(i)[6]).longValue());
                celdaData = rowData.createCell(4);
                celdaData.setCellStyle(styleCell);
                celdaData.setCellValue(((BigDecimal) listaPorPoblacion.get(i)[5]).longValue());
                celdaData = rowData.createCell(5);
                celdaData.setCellStyle(styleCell);
                celdaData.setCellValue(((BigDecimal) listaPorPoblacion.get(i)[6]).longValue());
                celdaData = rowData.createCell(6);
                celdaData.setCellStyle(styleCell);
                celdaData.setCellValue(((BigDecimal) listaPorPoblacion.get(i)[3]).longValue());

                for (int j = 1; j < CABECERAS.length; j++) {
                    int iniCab = 1 + (6 * j);
                    for (int k = 0; k < 6; k++) {
                        if (k == 0) {
                            XSSFCell celdaX = rowData.createCell(iniCab);
                            celdaX.setCellStyle(styleCellX);
                        } else {
                            XSSFCell celda = rowData.createCell(iniCab + k);
                            celda.setCellStyle(styleCell);
                        }
                    }
                }
            }

            String nextPoblacion = null;
            if (i < listaPorPoblacion.size() - 1) {
                nextPoblacion = (String) listaPorPoblacion.get(i + 1)[0];
            }

            if (!poblacion.equals(nextPoblacion)) {

                fila++;
                XSSFRow rowSubtotales = hojaPoblacion.createRow(fila);

                XSSFCell celdaSubtotales = rowSubtotales.createCell(1);
                celdaSubtotales.setCellStyle(styleCellTotales);
                celdaSubtotales.setCellValue("SUBTOTALES");

                for (int j = 0; j < CABECERAS.length; j++) {
                    int iniCab = 1 + (6 * j);
                    for (int k = 0; k < 6; k++) {
                        if (k == 0 && j > 0) {
                            XSSFCell celdaX = rowSubtotales.createCell(iniCab);
                            celdaX.setCellStyle(styleCellX);
                        } else if (k > 0) {
                            XSSFCell celda = rowSubtotales.createCell(iniCab + k);
                            celda.setCellStyle(styleCell);
                        }
                    }
                }

                // Pintamos los totales por año.
                List<Object[]> totalesAnios = numSolDao.findAllNumeracionAsignadaByPoblacionGroupByAnio(poblacion,
                        proveedor);
                fila = cargarFilasPorAnios(totalesAnios, fila, hojaPoblacion);

                // Calculamos el resto de totales

                // Total Asignadas
                fila++;
                XSSFRow rowTotal = hojaPoblacion.createRow(fila);
                int filaTotales = fila;
                int col = 1;

                XSSFCell celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCellTotales);
                celdaTotal.setCellValue("TOTALES");

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                celdaTotal
                        .setCellFormula(getTotalColumna("C", filaTotales - totalesAnios.size() + 1, filaTotales));

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                celdaTotal
                        .setCellFormula(getTotalColumna("D", filaTotales - totalesAnios.size() + 1, filaTotales));

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                celdaTotal
                        .setCellFormula(getTotalColumna("E", filaTotales - totalesAnios.size() + 1, filaTotales));

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                celdaTotal
                        .setCellFormula(getTotalColumna("F", filaTotales - totalesAnios.size() + 1, filaTotales));

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                celdaTotal
                        .setCellFormula(getTotalColumna("G", filaTotales - totalesAnios.size() + 1, filaTotales));

                XSSFCell celdaX = rowTotal.createCell(col++);
                celdaX.setCellStyle(styleCellX);

                // Totales solicitadas
                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Long totalFijoSol = numSolDao.getTotalNumSolicitadasByPoblacion("F", "", poblacion, proveedor)
                        .longValue();
                celdaTotal.setCellValue(totalFijoSol);

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Long totalMovilSol = numSolDao.getTotalNumSolicitadasByPoblacion("M", "", poblacion, proveedor)
                        .longValue();
                celdaTotal.setCellValue(totalMovilSol);

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Long totalCppSol = numSolDao.getTotalNumSolicitadasByPoblacion("M", "CPP", poblacion, proveedor)
                        .longValue();
                celdaTotal.setCellValue(totalCppSol);

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Long totalMppSol = numSolDao.getTotalNumSolicitadasByPoblacion("M", "MPP", poblacion, proveedor)
                        .longValue();
                celdaTotal.setCellValue(totalMppSol);

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Long totalSol = numSolDao.getTotalNumSolicitadasByPoblacion("", "", poblacion, proveedor)
                        .longValue();
                celdaTotal.setCellValue(totalSol);

                celdaX = rowTotal.createCell(col++);
                celdaX.setCellStyle(styleCellX);

                // Total Activas y porcetaje de uso
                DetalleReporte detalle = detalleReporteDao.getLastDetalleReporteByPoblacion(proveedor, poblacion);

                Long totalFijoAsigPob = detalle.getTotalAsignadasFijas().longValue();
                Long totalMovilAsigPob = detalle.getTotalAsignadasCpp().longValue()
                        + detalle.getTotalAsignadasMpp().longValue();
                Long totalCppAsigPob = detalle.getTotalAsignadasCpp().longValue();
                Long totalMppAsigPob = detalle.getTotalAsignadasMpp().longValue();
                Long totalAsigPob = detalle.getTotalAsignadas().longValue();

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Long totalFijoAct = detalle.getTotalLineasActivas().longValue();
                celdaTotal.setCellValue(totalFijoAct);

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Long totalMovilAct = detalle.getTotalLineasActivasCpp().longValue()
                        + detalle.getTotalLineasActivasMpp().longValue();
                celdaTotal.setCellValue(totalMovilAct);

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Long totalCppAct = detalle.getTotalLineasActivasCpp().longValue();
                celdaTotal.setCellValue(totalCppAct);

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Long totalMppAct = detalle.getTotalLineasActivasMpp().longValue();
                celdaTotal.setCellValue(totalMppAct);

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Long totalAct = detalle.getTotalLineasActivas().longValue();

                celdaTotal.setCellValue(totalAct);

                celdaX = rowTotal.createCell(col++);
                celdaX.setCellStyle(styleCellX);

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);

                celdaTotal.setCellValue(NumerosUtils.calcularPorcentaje(totalFijoAct, totalFijoAsigPob));

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);

                celdaTotal.setCellValue(NumerosUtils.calcularPorcentaje(totalMovilAct, totalMovilAsigPob));

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);

                celdaTotal.setCellValue(NumerosUtils.calcularPorcentaje(totalCppAct, totalCppAsigPob));

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);

                celdaTotal.setCellValue(NumerosUtils.calcularPorcentaje(totalMppAct, totalMppAsigPob));

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);

                celdaTotal.setCellValue(NumerosUtils.calcularPorcentaje(totalAct, totalAsigPob));

                fila++;
                rowTotal = hojaPoblacion.createRow(fila);
            }

        }

    }

    /**
     * Carga totales por año.
     * @param totalesAnios totales
     * @param fila fila
     * @param hoja sheet
     * @return fila final
     */
    private int cargarFilasPorAnios(List<Object[]> totalesAnios, int fila, XSSFSheet hoja) {

        for (int j = 0; j < totalesAnios.size(); j++) {
            fila++;
            XSSFRow rowData = hoja.createRow(fila);

            XSSFCell celdaData = rowData.createCell(1);
            celdaData.setCellStyle(styleCellTotales);
            celdaData.setCellValue(((BigDecimal) totalesAnios.get(j)[0]).longValue());

            celdaData = rowData.createCell(2);
            celdaData.setCellStyle(styleCell);
            celdaData.setCellValue(((BigDecimal) totalesAnios.get(j)[2]).longValue());

            celdaData = rowData.createCell(3);
            celdaData.setCellStyle(styleCell);
            celdaData.setCellValue(((BigDecimal) totalesAnios.get(j)[3]).longValue()
                    + ((BigDecimal) totalesAnios.get(j)[4]).longValue());
            celdaData = rowData.createCell(4);
            celdaData.setCellStyle(styleCell);
            celdaData.setCellValue(((BigDecimal) totalesAnios.get(j)[3]).longValue());
            celdaData = rowData.createCell(5);
            celdaData.setCellStyle(styleCell);
            celdaData.setCellValue(((BigDecimal) totalesAnios.get(j)[4]).longValue());
            celdaData = rowData.createCell(6);
            celdaData.setCellStyle(styleCell);
            celdaData.setCellValue(((BigDecimal) totalesAnios.get(j)[1]).longValue());

            for (int l = 1; l < CABECERAS.length; l++) {
                int iniCab = 1 + (6 * l);
                for (int k = 0; k < 6; k++) {
                    if (k == 0) {
                        XSSFCell celdaX = rowData.createCell(iniCab);
                        celdaX.setCellStyle(styleCellX);
                    } else {
                        XSSFCell celda = rowData.createCell(iniCab + k);
                        celda.setCellStyle(styleCell);
                    }
                }
            }
        }

        return fila;

    }

    /**
     * Obtiene el total de una columna entre una fila y otra.
     * @param col columna
     * @param filaIni fila
     * @param filaFin fila
     * @return formula
     */
    private String getTotalColumna(String col, int filaIni, int filaFin) {
        StringBuffer formula = new StringBuffer("SUM(");
        formula.append(col).append(filaIni).append(":").append(col).append(filaFin).append(")");
        return formula.toString();
    }

    /**
     * Crea la celda del proveedor.
     * @param hoja hoja
     * @param proveedor proveedor
     */
    private void crearCeldaPst(XSSFSheet hoja, Proveedor proveedor) {
        XSSFRow cabPST = hoja.createRow(1);
        XSSFCell celdaPST = cabPST.createCell(1);

        celdaPST.setCellStyle(styleCabPST);

        CellRangeAddress regionCabPst = new CellRangeAddress(1, 1, 1, 24);

        pintarStyleRegion(regionCabPst, styleCabPST, Color.YELLOW, hoja);

        celdaPST.setCellValue(proveedor.getNombre());

        hoja.addMergedRegion(regionCabPst);

    }

    /**
     * Carga los estilos del excel.
     * @param wb Workbook
     */
    private void cargarEstilos(XSSFWorkbook wb) {

        // Estilos de las celdas.
        styleCabPST = wb.createCellStyle();
        styleCab = wb.createCellStyle();
        styleSubCab = wb.createCellStyle();
        styleCell = cargarStyle(null, wb);
        styleCellX = cargarStyle(new Color(191, 191, 191), wb);
        styleCellCol = cargarStyle(new Color(196, 215, 155), wb);
        styleCellTotales = cargarStyle(null, wb);

        styleCellCol.setWrapText(true);

        XSSFFont negrita = wb.createFont();
        negrita.setBold(true);

        styleCellTotales.setFont(negrita);

        styleCellFecha = cargarStyle(null, wb);

        DataFormat df = wb.createDataFormat();

        styleCellFecha.setDataFormat(df.getFormat("dd/MM/yyyy"));
        styleCellFecha.setAlignment(CellStyle.ALIGN_CENTER);
    }

    /**
     * Metodo privado que precarga los anchos de las columnas.
     * @param hoja Sheet
     */
    private void cargarAnchosColumnas(XSSFSheet hoja) {

        hoja.setColumnWidth(0, 180);
        hoja.setColumnWidth(1, 3680);

        hoja.setColumnWidth(6, 2960);
        hoja.setColumnWidth(7, 360);

        hoja.setColumnWidth(12, 2960);
        hoja.setColumnWidth(13, 360);

        hoja.setColumnWidth(18, 2960);
        hoja.setColumnWidth(19, 360);

        hoja.setColumnWidth(24, 2960);

    }

    /**
     * Pinta los estilos de una celda.
     * @param color Color
     * @param wb Workbook
     * @return cellStyle
     */
    private XSSFCellStyle cargarStyle(Color color, XSSFWorkbook wb) {

        XSSFCellStyle styleCell = wb.createCellStyle();

        styleCell.setBorderBottom(CellStyle.BORDER_THIN);
        styleCell.setBorderLeft(CellStyle.BORDER_THIN);
        styleCell.setBorderRight(CellStyle.BORDER_THIN);
        styleCell.setBorderTop(CellStyle.BORDER_THIN);

        if (color != null) {
            styleCell.setFillForegroundColor(new XSSFColor(color));
            styleCell.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        }

        return styleCell;
    }

    /**
     * Carga el estilo de una region.
     * @param region region
     * @param styleCell estilo
     * @param color color
     * @param hoja hoja
     */
    private void pintarStyleRegion(CellRangeAddress region, XSSFCellStyle styleCell, Color color,
            XSSFSheet hoja) {

        styleCell.setBorderBottom(CellStyle.BORDER_MEDIUM);
        styleCell.setBorderLeft(CellStyle.BORDER_MEDIUM);
        styleCell.setBorderRight(CellStyle.BORDER_MEDIUM);
        styleCell.setBorderTop(CellStyle.BORDER_MEDIUM);
        styleCell.setAlignment(CellStyle.ALIGN_CENTER);

        if (color != null) {
            styleCell.setFillForegroundColor(new XSSFColor(color));
            styleCell.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        }
        XSSFRow row = hoja.getRow(region.getFirstRow());
        for (int i = region.getFirstColumn(); i <= region.getLastColumn(); ++i) {
            XSSFCell cell = row.createCell(i);
            cell.setCellStyle(styleCell);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public byte[] generarAnalisisNumeracionNng(Proveedor proveedor,
            List<NumeracionSolicitadaNng> numeracionesSolicitadas) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            XSSFWorkbook wb = new XSSFWorkbook();

            // Creamos las hojas del archivo.
            XSSFSheet hojaAnalisis = wb.createSheet("Analisis");

            cargarEstilos(wb);

            // Cargamos la hoja por clave de servicio.
            crearCeldaPstNng(hojaAnalisis, proveedor);

            // Creamos una lista auxiliar de las claves de servicio que estan en las numeraciones solicitadas en la
            // solicitud actual
            List<ClaveServicio> listaClaveAux = new ArrayList<ClaveServicio>();
            for (NumeracionSolicitadaNng numeracionSolicitada : numeracionesSolicitadas) {
                if (!listaClaveAux.contains(numeracionSolicitada.getClaveServicio())) {
                    listaClaveAux.add(numeracionSolicitada.getClaveServicio());
                }
            }
            // Creamos una lista aux con el historico de las numeraciones solicitadas de un proveedor por poblacion
            List<Object[]> listaPorClave = numAsigDao.findAllNumeracionAsignadaByPstGroupByClave(proveedor,
                    listaClaveAux);

            cargarExcelPorClave(listaPorClave, hojaAnalisis, proveedor);

            cargarAnchosColumnasNng(hojaAnalisis);

            wb.getCreationHelper().createFormulaEvaluator().evaluateAll();

            wb.write(bos);

            wb.close();
            bos.close();

        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        return bos.toByteArray();

    }

    /**
     * Carga el excel de analisis de asignación por clave de servicio.
     * @param listaPorClave claves
     * @param hoja sheet
     * @param proveedor pst
     */
    private void cargarExcelPorClave(List<Object[]> listaPorClave, XSSFSheet hoja, Proveedor proveedor) {
        String clave = null;

        int fila = 1;

        for (int i = 0; i < listaPorClave.size(); i++) {

            String claveAux = ((BigDecimal) listaPorClave.get(i)[0]).toString();

            if (clave == null || !clave.equals(claveAux)) {

                clave = claveAux;
                fila = pintarCabeceras(fila, hoja, "", clave);

                fila = crearSubCabecerasNng(hoja, fila);

            }

            if (clave.equals(claveAux)) {

                Calendar fechaAsignacion = Calendar.getInstance();
                fechaAsignacion.setTime((Date) listaPorClave.get(i)[1]);

                fila++;
                XSSFRow rowData = hoja.createRow(fila);
                crearCeldaXNng(rowData);

                XSSFCell celdaData = rowData.createCell(1);
                celdaData.setCellStyle(styleCellFecha);
                celdaData.setCellValue(fechaAsignacion);

                celdaData = rowData.createCell(2);
                celdaData.setCellStyle(styleCell);

                celdaData.setCellValue(((BigDecimal) listaPorClave.get(i)[2]).longValue());

                celdaData = rowData.createCell(3);
                celdaData.setCellStyle(styleCell);
                celdaData.setCellValue(((BigDecimal) listaPorClave.get(i)[3]).longValue());
                celdaData = rowData.createCell(4);
                celdaData.setCellStyle(styleCell);
                celdaData.setCellValue(((BigDecimal) listaPorClave.get(i)[4]).longValue());

            }

            String nextClave = null;
            if (i < listaPorClave.size() - 1) {
                nextClave = ((BigDecimal) listaPorClave.get(i + 1)[0]).toString();
            }

            if (!clave.equals(nextClave)) {

                fila++;
                XSSFRow rowSubtotales = hoja.createRow(fila);
                crearCeldaXNng(rowSubtotales);
                XSSFCell celdaSubtotales = rowSubtotales.createCell(1);
                celdaSubtotales.setCellStyle(styleCellTotales);
                celdaSubtotales.setCellValue("SUBTOTALES");

                // Pintamos los totales por año.
                List<Object[]> totalesAnios = numAsigDao.findAllNumeracionAsignadaByClaveGroupByAnio(clave, proveedor);
                fila = cargarFilasPorAniosNng(totalesAnios, fila, hoja);

                // Calculamos el resto de totales

                // Total Asignadas
                fila++;
                XSSFRow rowTotal = hoja.createRow(fila);
                int filaTotales = fila;
                int col = 1;

                XSSFCell celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCellTotales);
                celdaTotal.setCellValue("TOTALES");

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                celdaTotal
                        .setCellFormula(getTotalColumna("C", filaTotales - totalesAnios.size() + 1, filaTotales));

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                celdaTotal
                        .setCellFormula(getTotalColumna("D", filaTotales - totalesAnios.size() + 1, filaTotales));

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                celdaTotal
                        .setCellFormula(getTotalColumna("E", filaTotales - totalesAnios.size() + 1, filaTotales));

                XSSFCell celdaX = rowTotal.createCell(col++);
                celdaX.setCellStyle(styleCellX);

                // Totales solicitadas
                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                Integer totalSolicitadas = numSolNngDao.getTotalNumeracionesSolicitadasByClave(clave, proveedor);
                celdaTotal.setCellValue(totalSolicitadas);

                celdaX = rowTotal.createCell(col++);
                celdaX.setCellStyle(styleCellX);

                // Total Activas y porcetaje de uso

                Integer totalSerieAsig = rangoSerieNngDao.getNumeracionAsignadaSerie(clave, proveedor);
                Integer totalRangoAsig = rangoSerieNngDao.getNumeracionAsignadaRango(clave, proveedor);
                Integer totalEspecificoAsig = rangoSerieNngDao.getNumeracionAsignadaEspecifica(clave, proveedor);

                Integer totalSerieActiva = 0;
                Integer totalRangoActiva = 0;
                Integer totalEspecificoActiva = 0;

                BigDecimal idReporte = detalleReporteNngDao.getLastConsecutivoReporte(clave, proveedor);

                if (idReporte != null) {
                    ReporteNng reporte = reporteNngDao.getReporteById(idReporte);
                    switch (reporte.getTipoReporte().getCodigo()) {
                    case TipoReporte.LINEAS_ACTIVAS:
                        totalSerieActiva = detalleLineaActNngDao.getNumeracionActivaSerie(clave, proveedor, idReporte);
                        totalRangoActiva = detalleLineaActNngDao.getNumeracionActivaRango(clave, proveedor, idReporte);
                        totalEspecificoActiva = detalleLineaActNngDao.getNumeracionActivaEspecifica(clave, proveedor,
                                idReporte);
                        break;
                    case TipoReporte.LINEAS_ACTIVAS_DET:
                        totalSerieActiva = detalleLineaActDetNngDao
                                .getNumeracionActivaDetSerie(clave, proveedor, idReporte);
                        totalRangoActiva = detalleLineaActDetNngDao
                                .getNumeracionActivaDetRango(clave, proveedor, idReporte);
                        totalEspecificoActiva = detalleLineaActDetNngDao.getNumeracionActivaDetEspecifica(clave,
                                proveedor, idReporte);
                        break;

                    case TipoReporte.LINEAS_ACTIVAS_ARRENDATARIO:
                        totalSerieActiva = detalleLineaArrendatarioNngDao
                                .getNumeracionActivaArrendatarioSerie(clave, proveedor, idReporte);
                        totalRangoActiva = detalleLineaArrendatarioNngDao
                                .getNumeracionActivaArrendatarioRango(clave, proveedor, idReporte);
                        totalEspecificoActiva = detalleLineaArrendatarioNngDao
                                .getNumeracionActivaArrendatarioEspecifica(clave,
                                        proveedor, idReporte);
                        break;
                    }
                }

                // Totales activas
                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                celdaTotal.setCellValue(totalSerieActiva);

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                celdaTotal.setCellValue(totalRangoActiva);

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                celdaTotal.setCellValue(totalEspecificoActiva);

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                celdaTotal.setCellValue(totalSerieActiva + totalRangoActiva + totalEspecificoActiva);

                celdaX = rowTotal.createCell(col++);
                celdaX.setCellStyle(styleCellX);

                // Porcentajes
                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                celdaTotal.setCellValue(NumerosUtils.calcularPorcentajeAsString(totalSerieActiva, totalSerieAsig));

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                celdaTotal.setCellValue(NumerosUtils.calcularPorcentajeAsString(totalRangoActiva, totalRangoAsig));

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                celdaTotal.setCellValue(NumerosUtils.calcularPorcentajeAsString(totalSerieActiva + totalRangoActiva,
                        totalSerieAsig + totalRangoAsig));

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                celdaTotal.setCellValue(NumerosUtils
                        .calcularPorcentajeAsString(totalEspecificoActiva, totalEspecificoAsig));

                celdaTotal = rowTotal.createCell(col++);
                celdaTotal.setCellStyle(styleCell);
                celdaTotal.setCellValue(NumerosUtils.calcularPorcentajeAsString(totalSerieActiva + totalRangoActiva
                        + totalEspecificoActiva, totalSerieAsig + totalRangoAsig + totalEspecificoAsig));

                fila++;
                rowTotal = hoja.createRow(fila);
            }

        }

    }

    /**
     * Carga totales por año.
     * @param totalesAnios totales
     * @param fila fila
     * @param hoja sheet
     * @return fila final
     */
    private int cargarFilasPorAniosNng(List<Object[]> totalesAnios, int fila, XSSFSheet hoja) {

        for (int j = 0; j < totalesAnios.size(); j++) {
            fila++;
            XSSFRow rowData = hoja.createRow(fila);
            crearCeldaXNng(rowData);

            XSSFCell celdaData = rowData.createCell(1);
            celdaData.setCellStyle(styleCellTotales);
            celdaData.setCellValue(((BigDecimal) totalesAnios.get(j)[0]).longValue());

            celdaData = rowData.createCell(2);
            celdaData.setCellStyle(styleCell);
            celdaData.setCellValue(((BigDecimal) totalesAnios.get(j)[1]).longValue());

            celdaData = rowData.createCell(3);
            celdaData.setCellStyle(styleCell);
            celdaData.setCellValue(((BigDecimal) totalesAnios.get(j)[2]).longValue());
            celdaData = rowData.createCell(4);
            celdaData.setCellStyle(styleCell);
            celdaData.setCellValue(((BigDecimal) totalesAnios.get(j)[3]).longValue());

        }

        return fila;

    }

    /**
     * Crea las celdas intermedias entre bloques.
     * @param rowData fila
     */
    private void crearCeldaXNng(XSSFRow rowData) {
        for (int k = 2; k <= 17; k++) {
            XSSFCell celda = rowData.createCell(k);
            celda.setCellStyle(styleCell);
        }
        int iniCab = 5;
        for (int j = 1; j < CABECERAS.length; j++) {
            XSSFCell celdaX = rowData.createCell(iniCab);
            celdaX.setCellStyle(styleCellX);
            if (j == 1) {
                iniCab += 2;
            } else if (j == 2) {
                iniCab += 5;
            }

        }

    }

    /**
     * Crear las subcabeceras para NNG.
     * @param hoja hoja
     * @param fila fila
     * @return fila
     */
    private int crearSubCabecerasNng(XSSFSheet hoja, int fila) {
        fila++;

        XSSFRow subCab = hoja.createRow(fila);
        XSSFCell celda = subCab.createCell(1);
        celda.setCellStyle(styleCell);

        int iniCab = 2;
        for (int j = 0; j < CABECERAS.length; j++) {

            XSSFCell celdaSubCab = subCab.createCell(iniCab);
            CellRangeAddress regionSubCab;
            if (j == 0) {
                regionSubCab = new CellRangeAddress(fila, fila, iniCab, iniCab + 2);
                iniCab += 3;
            } else if (j == 1) {
                regionSubCab = new CellRangeAddress(fila, fila, iniCab, iniCab);
                iniCab += 1;
            } else if (j == 2) {
                regionSubCab = new CellRangeAddress(fila, fila, iniCab, iniCab + 3);
                iniCab += 4;
            } else {
                regionSubCab = new CellRangeAddress(fila, fila, iniCab, iniCab + 4);
            }
            pintarStyleRegion(regionSubCab, styleSubCab, null, hoja);
            hoja.addMergedRegion(regionSubCab);

            celdaSubCab.setCellValue(CABECERAS[j]);

            if (j < CABECERAS.length - 1) {
                XSSFCell celdaX = subCab.createCell(iniCab++);
                celdaX.setCellStyle(styleCellX);
            }

        }

        fila++;
        XSSFRow colRow = hoja.createRow(fila);
        iniCab = 1;
        for (int j = 0; j < CABECERAS.length; j++) {

            if (j == 0) {
                XSSFCell celdaColFechas = colRow.createCell(iniCab++);
                celdaColFechas.setCellStyle(styleCellCol);
                celdaColFechas.setCellValue("FECHA DE ASIGNACION");
            } else {
                XSSFCell celdaX = colRow.createCell(iniCab++);
                celdaX.setCellStyle(styleCellX);
            }
            XSSFCell celdaCol;
            if (j == 0) {
                celdaCol = colRow.createCell(iniCab++);
                celdaCol.setCellStyle(styleCellCol);
                celdaCol.setCellValue("Por Serie");
                celdaCol = colRow.createCell(iniCab++);
                celdaCol.setCellStyle(styleCellCol);
                celdaCol.setCellValue("Por Rango");
                celdaCol = colRow.createCell(iniCab++);
                celdaCol.setCellStyle(styleCellCol);
                celdaCol.setCellValue("Por Especifica");
            } else if (j == 1) {
                celdaCol = colRow.createCell(iniCab++);
                celdaCol.setCellStyle(styleCellCol);
                celdaCol.setCellValue("Cantidad");
            } else if (j == 2) {
                celdaCol = colRow.createCell(iniCab++);
                celdaCol.setCellStyle(styleCellCol);
                celdaCol.setCellValue("Por Serie");
                celdaCol = colRow.createCell(iniCab++);
                celdaCol.setCellStyle(styleCellCol);
                celdaCol.setCellValue("Por Rango");
                celdaCol = colRow.createCell(iniCab++);
                celdaCol.setCellStyle(styleCellCol);
                celdaCol.setCellValue("Por Especifica");
                celdaCol = colRow.createCell(iniCab++);
                celdaCol.setCellStyle(styleCellCol);
                celdaCol.setCellValue("Cantidad");
            } else {
                celdaCol = colRow.createCell(iniCab++);
                celdaCol.setCellStyle(styleCellCol);
                celdaCol.setCellValue("% Por serie");
                celdaCol = colRow.createCell(iniCab++);
                celdaCol.setCellStyle(styleCellCol);
                celdaCol.setCellValue("% Por rango");
                celdaCol = colRow.createCell(iniCab++);
                celdaCol.setCellStyle(styleCellCol);
                celdaCol.setCellValue("% Serie y rango");
                celdaCol = colRow.createCell(iniCab++);
                celdaCol.setCellStyle(styleCellCol);
                celdaCol.setCellValue("%Especifico");
                celdaCol = colRow.createCell(iniCab++);
                celdaCol.setCellStyle(styleCellCol);
                celdaCol.setCellValue("% General");
            }

        }
        return fila;
    }

    /**
     * Crear la cabecera del proveedor por NNG.
     * @param hoja hoja
     * @param proveedor pst
     */
    private void crearCeldaPstNng(XSSFSheet hoja, Proveedor proveedor) {
        XSSFRow cabPST = hoja.createRow(1);
        XSSFCell celdaPST = cabPST.createCell(1);

        celdaPST.setCellStyle(styleCabPST);

        CellRangeAddress regionCabPst = new CellRangeAddress(1, 1, 1, 17);

        pintarStyleRegion(regionCabPst, styleCabPST, Color.YELLOW, hoja);

        celdaPST.setCellValue(proveedor.getNombre());

        hoja.addMergedRegion(regionCabPst);

    }

    /**
     * Carga los achos de columnas para NNG.
     * @param hoja XSSFSheet
     */
    private void cargarAnchosColumnasNng(XSSFSheet hoja) {

        hoja.setColumnWidth(0, 180);

        hoja.setColumnWidth(1, 3680);
        hoja.setColumnWidth(2, 2670);
        hoja.setColumnWidth(3, 2950);
        hoja.setColumnWidth(4, 4000);

        hoja.setColumnWidth(5, 360);

        hoja.setColumnWidth(6, 7680);

        hoja.setColumnWidth(7, 360);

        hoja.setColumnWidth(8, 2670);
        hoja.setColumnWidth(9, 2950);
        hoja.setColumnWidth(10, 4000);
        hoja.setColumnWidth(11, 3040);

        hoja.setColumnWidth(12, 360);

        hoja.setColumnWidth(13, 3270);
        hoja.setColumnWidth(14, 3450);
        hoja.setColumnWidth(15, 4420);
        hoja.setColumnWidth(16, 3450);
        hoja.setColumnWidth(17, 2990);

    }

}
