package mx.ift.sns.negocio.oficios;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import mx.ift.sns.modelo.pst.TipoModalidad;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.conf.IParametrosService;
import mx.ift.sns.negocio.cpsi.ICodigoCPSIService;
import mx.ift.sns.negocio.nng.ISeriesNngService;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Proporciona la lógica base para la generación de oficios.
 * @author X53490DE
 */
public abstract class GeneradorOficio implements ITFGeneradorOficio {

    /** Servicio de Parámetros. */
    private IParametrosService paramService;

    /** Servicio de Series de Numeración No Geográfica. */
    private ISeriesNngService seriesNngService;

    /** Servicio de Códigos CPSI. */
    private ICodigoCPSIService codigosCpsiService;

    /** Variables de sustitución en el documento. */
    public static final String VACIO = "";

    /** Variables de sustitución en el documento. */
    public static final String NUM_OFICIO = "varNumOficio";

    /** Variables de sustitución en el documento. */
    public static final String FECHA_OFICIO = "varFechaOficio";

    /** Variables de sustitución en el documento. */
    public static final String CONCESIONARIO = "varConcesionario";

    /** Variables de sustitución en el documento. */
    public static final String CONCESIONARIO_CORTO = "varConcesionarioCorto";

    /** Variables de sustitución en el documento. */
    public static final String CALLE = "varCalle";

    /** Variables de sustitución en el documento. */
    public static final String COLONIA = "varColonia";

    /** Variables de sustitución en el documento. */
    public static final String CP = "varCP";

    /** Variables de sustitución en el documento. */
    public static final String CP_CESIONARIO = "varCPCes";

    /** Variables de sustitución en el documento. */
    public static final String SOLICITUD = "varSolicitud";

    /** Variables de sustitución en el documento. */
    public static final String REFERENCIA_SOLICITUD = "varReferencia";

    /** Variables de sustitución en el documento. */
    public static final String FECHA_SOLICITUD = "varFecha";

    /** Variables de sustitución en el documento. */
    public static final String FECHA_ACTUAL = "varFechaHoy";

    /** Variables de sustitución en el documento. */
    public static final String ENTIDAD = "varEntidad";

    /** Variables de sustitución en el documento. */
    public static final String ATENCION = "varAtencion";

    /** Variables de sustitución en el documento. */
    public static final String REPRESENTANTE_LEGAL = "varRepLegal";

    /** Variables de sustitución en el documento. */
    public static final String REPRESENTANTE_LEGAL_SUPLENTE = "varRepLegalSupl";

    /** Variables de sustitución en el documento. */
    public static final String REPRESENTANTE_LEGAL_SUPLENTE_CESIONARIO = "varRepLegalSuplCes";

    /** Variables de sustitución en el documento. */
    public static final String ASIGNADOS = "varAsignados";

    /** Variables de sustitución en el documento. */
    public static final String LEYENDA = "varLeyenda";

    /** Variables de sustitución en el documento. */
    public static final String FECHA_INICIO_UTILIZACION = "varFechaInicioUtilizacion";

    /** Variables de sustitución en el documento. */
    public static final String FECHA_IMPLEMENTACION = "varFechaImpl";

    /** Variables de sustitución en el documento. */
    public static final String MES_ANIO = "varMesYAño";

    /** Variables de sustitución en el documento. */
    public static final String MES = "varMes";

    /** Variables de sustitución en el documento. */
    public static final String CESIONARIO = "varCesionario";

    /** Variables de sustitución en el documento. */
    public static final String CALLE_CESIONARIO = "varCalleCes";

    /** Variables de sustitución en el documento. */
    public static final String COLONIA_CESIONARIO = "varColoniaCes";

    /** Variables de sustitución en el documento. */
    public static final String ENTIDAD_CESIONARIO = "varEntidadCes";

    /** Variables de sustitución en el documento. */
    public static final String REPRESENTANTE_LEGAL_CESIONARIO = "varRepLegalCes";

    /** Variables de sustitución en el documento. */
    public static final String CESIONARIO_CORTO = "varCesionarioCorto";

    /** Variables de sustitución en el documento. */
    public static final String CEDENTE = "varCedente";

    /** Variables de sustitución en el documento. */
    public static final String CEDENTE_CORTO = "varCedenteCorto";

    /** Variables de sustitución en el documento. */
    public static final String ESTADO = "varEstado";

    /** Variables de sustitución en el documento. */
    public static final String ESTADO_CESIONARIO = "varEstadoCes";

    /** Variables de sustitución en el documento. */
    public static final String NUM_EXT = "varNExt";

    /** Variables de sustitución en el documento. */
    public static final String NUM_EXT_CESIONARIO = "varNExtCes";

    /** Variables de sustitución en el documento. */
    public static final String NUM_INT = "varNInt";

    /** Variables de sustitución en el documento. */
    public static final String NUM_INT_CESIONARIO = "varNIntCes";

    /** Variables de sustitución en el documento. */
    public static final String TEXTO_ADICIONAL = "varTextoAdicional";

    /** Variables de sustitución en el documento. */
    public static final String EXCEL_ASIGNACION = "varExcelAsignacion";

    /** Variables de sustitución en el documento. */
    public static final String EXCEL_NO_ASIGNADOS = "varExcelNoAsignados";

    /** Variables de sustitución en el documento. */
    public static final String EXCEL_LIBERACIONES = "varExcelLiberaciones";

    /** Variables de sustitución en el documento. */
    public static final String EXCEL_CESIONES_CEDENTE = "varExcelCedente";

    /** Variables de sustitución en el documento. */
    public static final String EXCEL_CESIONES_CESIONARIO = "varExcelCesionario";

    /** Variables de sustitución en el documento. */
    public static final String HORA = "varHora";

    /** Variables de sustitución en el documento. */
    public static final String EXCEL_REDIST_ANTES = "varExcelRedistAntes";

    /** Variables de sustitución en el documento. */
    public static final String EXCEL_REDIST_DESPUES = "varExcelRedistDespues";

    /** Variables de sustitución en el documento. */
    public static final String TITULO_FIRMANTE = "varTituloFirmante";

    /** Variables de sustitución en el documento. */
    public static final String FIRMANTE_OFICIO = "varFirmanteOficio";

    /** Texto variable de asignacion nng 1. */
    public static final String TEXTO_ASIGNACION_NNG_1 = "varTextoAsignacion1";

    /** Texto variable de asignacion nng 2. */
    public static final String TEXTO_ASIGNACION_NNG_2 = "varTextoAsignacion2";

    /** Texto variable de asignacion nng 3. */
    public static final String TEXTO_ASIGNACION_NNG_3 = "varTextoAsignacion3";

    /** Texto variable de asignacion nng 4. */
    public static final String TEXTO_ASIGNACION_NNG_4 = "varTextoAsignacion4";

    /** Texto variable de asignacion nng 5. */
    public static final String TEXTO_ASIGNACION_NNG_5 = "varTextoAsignacion5";

    /** Texto variable de asignacion otros pst nng 5. */
    public static final String TEXTO_ASIGNACION_OTROS_NNG_1 = "varTextoAsignacionOtros1";

    /** Variables de sustitución en el documento. */
    public static final String EXCEL_ASIGANCION_CPSN = "varExcelBloquesCPSN";

    /** Variables de sustitución en el documento. */
    public static final String EXCEL_ASIGANCION_CPSI = "varExcelCodigosCPSI";

    /** Variables de sustitución en el documento. */
    public static final String CPSI_CATALOGO = "varCpsiAsignados";

    /** Variables de sustitución en el documento. */
    public static final String CPSI_PORCENTAJE = "varCpsiPorcentaje";

    /** Contenedor de variables a sustituir en el cuerpo de la plantilla. */
    protected HashMap<String, String> variables = new HashMap<>(1);

    /** Contenedor de variables a sustituir en el header de la plantilla. */
    protected HashMap<String, String> header = new HashMap<>(1);

    /** Contenedor de tablas a sustituir en el cuerpo de la plantilla. */
    protected HashMap<String, XWPFTable> tablas = new HashMap<>(1);

    /**
     * Genera una instancia derivada de ITFGeneradorOficio en función del tipo de solicitud.
     * @param pParametros Información del oficio.
     * @return ITFGeneradorOficio
     */
    public static ITFGeneradorOficio getGeneradorOficio(ParametrosOficio pParametros) {

        ITFGeneradorOficio generadorOficio;
        Integer tipoSolicitud = pParametros.getSolicitud().getTipoSolicitud().getCdg();
        if (tipoSolicitud.equals(TipoSolicitud.ASIGNACION)) {
            generadorOficio = new OficioSolicitudAsignacionNg(pParametros);
        } else if (tipoSolicitud.equals(TipoSolicitud.LIBERACION)) {
            generadorOficio = new OficioSolicitudLiberacionNg(pParametros);
        } else if (tipoSolicitud.equals(TipoSolicitud.CESION_DERECHOS)) {
            generadorOficio = new OficioSolicitudCesionNg(pParametros);
        } else if (tipoSolicitud.equals(TipoSolicitud.REDISTRIBUCION)) {
            generadorOficio = new OficioSolicitudRedistribucionNg(pParametros);
        } else if (tipoSolicitud.equals(TipoSolicitud.ASIGNACION_NNG)) {
            generadorOficio = new OficioSolicitudAsignacionNng(pParametros);
        } else if (tipoSolicitud.equals(TipoSolicitud.LIBERACION_NNG)) {
            generadorOficio = new OficioSolicitudLiberacionNng(pParametros);
        } else if (tipoSolicitud.equals(TipoSolicitud.CESION_DERECHOS_NNG)) {
            generadorOficio = new OficioSolicitudCesionNng(pParametros);
        } else if (tipoSolicitud.equals(TipoSolicitud.REDISTRIBUCION_NNG)) {
            generadorOficio = new OficioSolicitudRedistribucionNng(pParametros);
        } else if (tipoSolicitud.equals(TipoSolicitud.ASIGNACION_CPSN)) {
            generadorOficio = new OficioSolicitudAsignacionCpsn(pParametros);
        } else if (tipoSolicitud.equals(TipoSolicitud.CESION_CPSN)) {
            generadorOficio = new OficioSolicitudCesionCpsn(pParametros);
        } else if (tipoSolicitud.equals(TipoSolicitud.LIBERACION_CPSN)) {
            generadorOficio = new OficioSolicitudLiberacionCpsn(pParametros);
        } else if (tipoSolicitud.equals(TipoSolicitud.ASIGNACION_CPSI)) {
            generadorOficio = new OficioSolicitudAsignacionCpsi(pParametros);
        } else if (tipoSolicitud.equals(TipoSolicitud.CESION_CPSI)) {
            generadorOficio = new OficioSolicitudCesionCpsi(pParametros);
        } else if (tipoSolicitud.equals(TipoSolicitud.LIBERACION_CPSI)) {
            generadorOficio = new OficioSolicitudLiberacionCpsi(pParametros);
        } else if (tipoSolicitud.equals(TipoSolicitud.SOLICITUD_CPSI_UIT)) {
            generadorOficio = new OficioSolicitudCpsiUIT(pParametros);
        } else {
            // No debería darse el caso.
            generadorOficio = null;
        }

        return generadorOficio;
    }
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneradorOficio.class);


    /**
     * Carga las variables a sustituir en todos los oficios.
     * @param pSolicitud Información de la Solicitud genérica.
     * @param pNumOficio Número de oficio indicado por el usuario.
     * @param pFechaOficio Fecha de oficio indicada por el usuario.
     */
    protected void cargaVariablesComunes(Solicitud pSolicitud, String pNumOficio, Date pFechaOficio) {
        
        if (LOGGER.isDebugEnabled()) {
            StringBuffer sbTrace = new StringBuffer("Revision de variables en el oficio.");
            sbTrace.append(" Solicitud: ").append(pSolicitud.getId());
            LOGGER.debug(sbTrace.toString());
        }
    	
    	header.put(NUM_OFICIO, pNumOficio);
        variables.put(NUM_OFICIO, pNumOficio);
        variables.put(FECHA_OFICIO, FechasUtils.fechaToString(pFechaOficio, "dd 'de' MMMM 'de' yyyy"));
        variables.put(SOLICITUD, String.valueOf(pSolicitud.getId()));
        variables.put(CONCESIONARIO, this.getStringNullProof(pSolicitud.getProveedorSolicitante().getNombre()));
        variables.put(CALLE, this.getStringNullProof(pSolicitud.getProveedorSolicitante().getCalle()));
        variables.put(NUM_EXT, this.getStringNullProof(pSolicitud.getProveedorSolicitante().getNumExt()));
        variables.put(NUM_INT, this.getStringNullProof(pSolicitud.getProveedorSolicitante().getNumInt()));
        variables.put(CP, this.getStringNullProof(pSolicitud.getProveedorSolicitante().getCp()));
        variables.put(COLONIA, this.getStringNullProof(pSolicitud.getProveedorSolicitante().getColonia()));
        variables.put(ENTIDAD, this.getStringNullProof(pSolicitud.getProveedorSolicitante().getCiudad()));
        variables.put(ESTADO, this.getStringNullProof(pSolicitud.getProveedorSolicitante().getEstado().getNombre()));
        variables.put(FECHA_ACTUAL, FechasUtils.fechaToString(new Date(), "dd 'de' MMMM 'de' yyyy"));
        variables.put(FECHA_SOLICITUD,
                FechasUtils.fechaToString(pSolicitud.getFechaSolicitud(), "dd 'de' MMMM 'de' yyyy"));
        // Variables cargadas de la tabla de Parámetros
        try {
            variables.put(TITULO_FIRMANTE, paramService.getParamByName(TITULO_FIRMANTE).toUpperCase());
        } catch (Exception ex) {
            variables.put(TITULO_FIRMANTE, VACIO);
        }

        try {
            variables.put(FIRMANTE_OFICIO, paramService.getParamByName(FIRMANTE_OFICIO).toUpperCase());
        } catch (Exception ex) {
            variables.put(FIRMANTE_OFICIO, VACIO);
        }
    }

    /**
     * Carga las variables a sustituir en los documentos de cédula de notificación.
     * @param pFechaOficio Fecha de oficio indicada por el usuario.
     */
    protected void cargaVariablesCedula(Date pFechaOficio) {
        variables.put(MES, FechasUtils.fechaToString(pFechaOficio, "MMMM"));
        variables.put(MES_ANIO, FechasUtils.fechaToString(pFechaOficio, "MMMM yyyy"));
        // variables.put(FECHA_OFICIO, FechasUtils.fechaToString(pFechaOficio, "dd 'de' MMMM 'de' yyyy"));
    }

    /**
     * Carga las variables a sustituir en los documentos de acta circunstanciada.
     * @param pSolicitud Información de la Solicitud genérica.
     * @param pFechaOficio Date
     */
    protected void cargaVariablesActa(Solicitud pSolicitud, Date pFechaOficio) {
        // variables.put(CONCESIONARIO, pSolicitud.getProveedorSolicitante().getNombre()); // Ya Cargado en comunes
        variables.put(REPRESENTANTE_LEGAL, pSolicitud.getRepresentanteLegal().getNombre());
        // variables.put(FECHA_OFICIO, FechasUtils.fechaToString(pFechaOficio, "dd 'de' MMMM 'de' yyyy"));
    }

    /**
     * Genera un fichero serializado con la plantilla actualizada usando los valores recibidos del sistema.
     * @param pPlantillaSerializada Bytes de la Plantilla almacenada en BBDD
     * @param pVarContent Lista de Variables para actualizar en la plantilla
     * @param pTables Lista de Tablas para actualizar en la plantilla
     * @param pVarHeader Lista de Variables para actualizar en el header
     * @return Bytes del Oficio
     * @throws Exception En caso de Error
     */
    protected byte[] actualizarVariablesOficio(byte[] pPlantillaSerializada,
            HashMap<String, String> pVarContent,
            HashMap<String, XWPFTable> pTables,
            HashMap<String, String> pVarHeader) throws Exception {

        // Es necesario que el formato de Word sea .docx (2007), para ello
        // las plantillas se almacenan en BBDD con el formato indicado.

        ByteArrayInputStream bais = null;
        ByteArrayOutputStream baos = null;
        HashMap<XWPFParagraph, Integer> marcadores = new HashMap<XWPFParagraph, Integer>(1);
        int tablas = 0;

        try {
            // Contenido del documento word
            bais = new ByteArrayInputStream(pPlantillaSerializada);
            XWPFDocument doc = new XWPFDocument(bais);

            // Recorrido por las variables del Header
            for (XWPFHeader headerVar : doc.getHeaderList()) {
                for (XWPFParagraph paragraph : headerVar.getParagraphs()) {
                    List<XWPFRun> runs = paragraph.getRuns(); // Recorrido por las palabras
                    if (runs != null) {
                        for (XWPFRun run : runs) {
                            String text = run.getText(0);
                            if (text != null && text.startsWith("var")) {
                                // Sustituimos las variables
                            	System.out.println("Variable : " + pVarHeader.get(text.trim()));
                                run.setText(pVarHeader.get(text.trim()), 0);
                            }
                        }
                    }
                }
            }

            // Recorrido por los paragraphs
            for (XWPFParagraph paragraph : doc.getParagraphs()) {

                // Recorrido por las palabras
                List<XWPFRun> runs = paragraph.getRuns();
                if (runs != null) {
                    for (XWPFRun run : runs) {
                        String text = run.getText(0);
                        if (text != null && text.startsWith("var")) {
                            // Comprobamos si es una tabla excel
                            if (text.contains("Excel")) {
                                XmlCursor cursor = paragraph.getCTP().newCursor();
                                paragraph.getBody().insertNewTbl(cursor);
                                doc.setTable(tablas, pTables.get(text.trim()));
                                marcadores.put(paragraph, new Integer(runs.indexOf(run)));
                                tablas++;
                            } else {
                                // Sustituimos las variables
                                run.setText(pVarContent.get(text.trim()), 0);
                            }
                        }
                    }
                }
            }

            // Eliminamos los marcadores de tablas Excel
            for (XWPFParagraph paragraph : marcadores.keySet()) {
                paragraph.removeRun(marcadores.get(paragraph).intValue());
            }

            // Generamos el Oficio actualizado
            baos = new ByteArrayOutputStream();
            doc.write(baos);

        } finally {
            if (bais != null) {
                bais.close();
            }
            if (baos != null) {
                baos.close();
            }
        }

        return baos.toByteArray();
    }

    /**
     * Fusiona columnas de una fila horizontalmente.
     * @param pTabla Instancia de la tabla.
     * @param pFila Fila seleccionada cuyas celdas se van a fusionar.
     * @param pDesdeColumna Columna de inicio de fusión.
     * @param pHastaColumna Columna final de Fusión.
     */
    protected static void fusionarCeldas(XWPFTable pTabla, int pFila, int pDesdeColumna, int pHastaColumna) {
        for (int cellIndex = pDesdeColumna; cellIndex < pHastaColumna; cellIndex++) {
            XWPFTableCell cell = pTabla.getRow(pFila).getCell(cellIndex);
            if (cellIndex == pDesdeColumna) {
                // The first merged cell is set with RESTART merge value
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
            } else {
                // Cells which join (merge) the first one, are set with CONTINUE
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
            }
        }
    }

    /**
     * Genera una celda con el texto y estilo indicado.
     * @param pCell Celda a formatear.
     * @param pTexto Texto de la celda.
     * @param pFontSize Tamaño de fuente.
     * @param pBold Negrita.
     */
    protected static void generaCelda(XWPFTableCell pCell, String pTexto, int pFontSize, boolean pBold) {
        XWPFParagraph cellParagraph = pCell.getParagraphs().get(0);
        XWPFRun cellRun = cellParagraph.createRun();
        cellRun.setFontFamily("Arial");
        cellRun.setFontSize(pFontSize);
        cellRun.setBold(pBold);
        cellRun.setText(pTexto);
    }

    /**
     * Devuelve el String del tipo de Red para las tablas de numeración.
     * @param pTipoRed Información del tipo de Red
     * @param pTipoModalidad Información del tipo de Modalidad
     * @return String
     */
    protected static String getTipoRed(TipoRed pTipoRed, TipoModalidad pTipoModalidad) {
        if (pTipoRed.getCdg().equals(TipoRed.FIJA)) {
            return pTipoRed.getDescripcion().toUpperCase();

        } else if (pTipoRed.getCdg().equals(TipoRed.MOVIL)) {
            StringBuffer sbTipoRed = new StringBuffer();
            sbTipoRed.append(pTipoRed.getDescripcion());
            if (pTipoModalidad != null) {
                sbTipoRed.append("-").append(pTipoModalidad.getDescripcion());
            }
            return sbTipoRed.toString().toUpperCase();

        } else {
            return pTipoRed.getDescripcion().toUpperCase();
        }
    }

    /**
     * Recupera el valor del String o un "" si es nulo.
     * @param pValue String
     * @return String pasado como parámetro o "" si es nulo.
     */
    private String getStringNullProof(String pValue) {
        if (pValue != null) {
            return pValue;
        }
        return VACIO;
    }

    @Override
    public void setParamService(IParametrosService paramService) {
        this.paramService = paramService;
    }

    /**
     * Devuelve la instancia del servicio de parámetros.
     * @return IParametrosService
     */
    protected IParametrosService getParamService() {
        return paramService;
    }

    @Override
    public void setSeriesNngService(ISeriesNngService seriesNngService) {
        this.seriesNngService = seriesNngService;
    }

    /**
     * Devuelve la instancia del servicio de series de numeración no geográfica.
     * @return ISeriesNngService
     */
    protected ISeriesNngService getSeriesNngService() {
        return seriesNngService;
    }

    @Override
    public void setCodigosCpsiService(ICodigoCPSIService codigosCpsiService) {
        this.codigosCpsiService = codigosCpsiService;
    }

    /**
     * Recupera el servicio de Códigos CPSI para oficios de Solicitudes UIT.
     * @return the codigosCpsiService Instancia del servicio de códigos CPSI.
     */
    protected ICodigoCPSIService getCodigosCpsiService() {
        return codigosCpsiService;
    }
}
