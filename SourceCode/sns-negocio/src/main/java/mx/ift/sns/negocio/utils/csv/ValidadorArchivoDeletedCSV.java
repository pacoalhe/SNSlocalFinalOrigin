package mx.ift.sns.negocio.utils.csv;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


import mx.ift.sns.modelo.port.NumeroCancelado;
import mx.ift.sns.modelo.port.NumeroPortado;
import mx.ift.sns.negocio.conf.IParametrosService;
import mx.ift.sns.negocio.ng.model.ResultadoValidacionArrendamiento;
import mx.ift.sns.negocio.ng.model.ResultadoValidacionCSV;
import mx.ift.sns.negocio.port.CanceladosDAO;
import mx.ift.sns.negocio.port.ProcesarRegistrosCancelados;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


/**
 * Validador de archivos csv.
 */

@Stateless
public class ValidadorArchivoDeletedCSV {

    //FJAH 26052025
    public ValidadorArchivoDeletedCSV() {
        // Constructor vacío requerido por EJB
    }

    final int BATCH_SIZE = 5000;

    /** DAO cancelados */
    @EJB
    private CanceladosDAO canceladosDAO;

    /** Servicio configuracion. */
    @EJB
    private IParametrosService paramService;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidadorArchivoDeletedCSV.class);

    /** Delimitador del fichero CSV. */
    private static final char DELIMITADOR = ',';

    /** Lector de ficheros en formato CSV. */
    private CSVReader cvsReader = null;

    /** Resultado de la validacion. */
    private ResultadoValidacionCSV res = new ResultadoValidacionCSV();

    /** Indica si se ha encontrado un error. */
    private boolean hayError = false;

    /** Indica si se ha de terminar la lectura de las filas. */
    private boolean terminar = false;

    /** Numero total de filas del archivo, sin la cabecera. */
    private int totalFilas = 0;

    //FJAH 24052025
    @EJB
    private ProcesarRegistrosCancelados procesadorCancelados;


    /**
     * Comprueba la cabecera del fichero.
     * @param headers cabeceras
     * @return true si es correcto, false eoc
     * @throws IOException error leyendo
     */
    public boolean checkHeader(String[] headers) throws IOException {

        return true;
    }

    /**
     * Valida una fila del archivo.
     * @param valores d
     */
    public void checkFila(String[] valores) {

    }

    /**
     * Valida el fichero de rangos de numeracion del pst arrendador.
     * @param fileName nombre del archivo
     * @return res resultado de la validacion
     * @throws Exception error
     */

    //FJAH 26052025 Refactorizacion por lotes
    public ResultadoValidacionCSV validar(String fileName) throws Exception {
        LOGGER.debug("fichero {}", fileName);

        ResultadoValidacionCSV res = new ResultadoValidacionCSV();
        int totalFilas = 0;
        int failCount = 0;
        String actionDateLote = null;
        String timestampOriginal = null;

        List<RegistroInvalido> registrosInvalidos = new ArrayList<>();
        List<NumeroCancelado> loteTotal = new ArrayList<>();

        if (StringUtils.isEmpty(fileName)) {
            LOGGER.debug("nombre fichero vacio");
            res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO);
            return res;
        }

        File f1 = new File(fileName);
        if (!f1.exists()) {
            LOGGER.debug("fichero no existe");
            res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO);
            return res;
        }

        try (FileReader freader = new FileReader(fileName);
             CSVReader cvsReader = new CSVReader(freader, '|', '"', 0)) {

            res.setError(ResultadoValidacionArrendamiento.VALIDACION_OK);
            List<String[]> allRows = cvsReader.readAll();
            LOGGER.info("----------------- Tamaño del fichero csvdeleted {}", allRows.size());

            if (!allRows.isEmpty()) {
                LOGGER.info("<-- Inicia el recorrido del csv {}", FechasUtils.getActualDate());

                // Paso 0: armar lote ORIGEN
                LOGGER.info("<--- Paso 0: armar lote ORIGEN ---->");
                for (String[] row : allRows) {
                    NumeroCancelado nc = mapRowANumeroCancelado(row, registrosInvalidos);
                    if (nc != null && nc.getNumberFrom() != null) {
                        loteTotal.add(nc);
                        totalFilas++;
                        if (actionDateLote == null && nc.getActionDate() != null) {
                            actionDateLote = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(nc.getActionDate());
                        }
                    } else {
                        failCount++;
                        // Reconstruir fila limpia con '|', compatible con Java 1.7
                        /*
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < row.length; i++) {
                            if (i > 0) sb.append("|");
                            sb.append(row[i] != null ? row[i] : "");
                        }
                        //String lineaCruda = sb.toString();
                        //registrosInvalidos.add(new RegistroInvalido("Error genérico en validación", lineaCruda));
                        //Ahora, solo usa invalidCancelado:
                        String filaCsv = sb.toString();
                        registrosInvalidos.add(new RegistroInvalido("Error mapeando fila CSV: validación estructural", filaCsv));
                        return null; // <- importante: no intentes seguir mapeando
                         */
                    }
                }

                // Paso Sub0: limpiar snapshots persistentes
                LOGGER.info("<--- Paso Sub0: limpiar snapshots SS ---->");
                canceladosDAO.limpiarSnapshotCancelado();

                // Paso 1: snapshot ORIGEN
                LOGGER.info("<--- Paso 1: guardar snapshot ORIGEN ---->");
                canceladosDAO.insertSnapshotOrigen(loteTotal);

                // Paso 2: limpiar previos (delete en cancelados)
                //SE OMITE PASO POR TENER EL MERGE EN CANCELADOS
                /*
                LOGGER.info("<--- Paso 2: borrar previos en PORT_NUM_CANCELADO ---->");
                if (actionDateLote != null) {
                    Date fechaProceso = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(actionDateLote);
                    canceladosDAO.deleteCanceladosPorFecha(fechaProceso);
                } else {
                    LOGGER.warn("No se pudo determinar actionDate del lote, no se ejecuta borrado en PORT_NUM_CANCELADO");
                }
                 */

                // Paso 3: insertar en cancelados Fallback
                LOGGER.info("<--- Paso 3: insertar en PORT_NUM_CANCELADO ---->");
                //int insertados = canceladosDAO.insertCanceladosBatch(loteTotal);
                //TODO Caso de prueba 1 bloquear llamado
                int insertados = canceladosDAO.upsertHibridoCancelados(loteTotal, loteTotal.size());

                // Paso 4: eliminar de portados Fallback
                LOGGER.info("<--- Paso 4: eliminar en PORT_NUM_PORTADO ---->");
                //TODO Caso de prueba 1 bloquear llamado
                int eliminados = canceladosDAO.deleteHibridoPortados(loteTotal, loteTotal.size());

                // Paso 5: actualizar snapshot -> ESTADO_FINAL_CANC
                canceladosDAO.updateSnapshotEstadoFinalCanc();

                // Paso 6: actualizar snapshot -> ESTADO_FINAL_PORT
                canceladosDAO.updateSnapshotEstadoFinalPort();

                // Paso 7: totales
                ResultadoValidacionCSV totales = canceladosDAO.getTotalesSnapshot();
                //res.setTotalOrigenCanc(totales.getTotalOrigenCanc());
                res.setTotalInsertadosCanc(totales.getTotalInsertadosCanc());
                res.setTotalEliminadosCanc(totales.getTotalEliminadosCanc());
                res.setTotalFallidosInsercionCanc(totales.getTotalFallidosInsercionCanc());
                res.setTotalFallidosEliminacionCanc(totales.getTotalFallidosEliminacionCanc());
                res.setTotalProcesadosCanc(totales.getTotalProcesadosCanc());
                // errores de estructura = inválidos al parsear CSV
                res.setTotalErrorEstructuraCanc(registrosInvalidos.size());

                LOGGER.info("Totales cancelados (técnico) -> Origen={}, Insertados={}, Eliminados={}, Procesados={}, FallidosInsercion={}, FallidosEliminacion={}, ErroresEstructura={}, FallidosTotales={}",
                        //res.getTotalOrigenCanc(),
                        res.getTotalInsertadosCanc(),
                        res.getTotalEliminadosCanc(),
                        res.getTotalProcesadosCanc(),
                        res.getTotalFallidosInsercionCanc(),
                        res.getTotalFallidosEliminacionCanc(),
                        res.getTotalErrorEstructuraCanc(),
                        res.getTotalFallidosCanc());

                // Paso 8: generar log de inválidos
                if (!registrosInvalidos.isEmpty()) {
                    generarLogInvalidos(registrosInvalidos);
                }

                // Paso 9: generar XML de fallidos (cancelados)
                LOGGER.info("<--- Paso 9: generar XML de fallidos (cancelados) ---->");
                try {
                    canceladosDAO.generarXmlFallidosCanceladosBatch();
                    LOGGER.info("XML de no persistidos cancelados generado correctamente (streaming desde BD).");
                } catch (Exception e) {
                    LOGGER.error("Error generando XML de no persistidos en cancelados.", e);
                }

            } else {
                res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO_VACIO);
            }

        } catch (Exception e) {
            res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO);
            LOGGER.error("Error validando archivo cancelados {}", fileName, e);
        }

        res.setActionDateLote(actionDateLote);
        return res;
    }

    private boolean isNumeric(String str) {
        if (str == null || str.trim().isEmpty()) return false;
        try {
            new BigDecimal(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static final SimpleDateFormat ACTIONDATE_FORMAT_COMPACT = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final SimpleDateFormat ACTIONDATE_FORMAT_EXTENDED = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static {
        ACTIONDATE_FORMAT_COMPACT.setLenient(false);
        ACTIONDATE_FORMAT_EXTENDED.setLenient(false);
    }

    private NumeroCancelado mapRowANumeroCancelado(String[] valores, List<RegistroInvalido> registrosInvalidos) {
        try {
            if (valores == null || valores.length < 13) {
                return invalidCancelado("Fila inválida (menos de 13 columnas)", valores, registrosInvalidos);
            }

            // ==== Validación de longitudes (VARCHAR2/CHAR) ====
            if (valores[0] != null && valores[0].length() > 22) {
                return invalidCancelado("PortID demasiado largo (" + valores[0].length() + " > 22)", valores, registrosInvalidos);
            }
            if (valores[1] != null && valores[1].length() > 1) {
                return invalidCancelado("PortType demasiado largo (" + valores[1].length() + " > 1)", valores, registrosInvalidos);
            }
            if (valores[2] != null && valores[2].length() > 20) {
                return invalidCancelado("Action demasiado largo (" + valores[2].length() + " > 20)", valores, registrosInvalidos);
            }
            if (valores[3] != null && valores[3].length() > 10) {
                return invalidCancelado("NumberFrom demasiado largo (" + valores[3].length() + " > 10)", valores, registrosInvalidos);
            }
            if (valores[4] != null && valores[4].length() > 20) {
                return invalidCancelado("NumberTo demasiado largo (" + valores[4].length() + " > 20)", valores, registrosInvalidos);
            }
            if (valores[5] != null && valores[5].length() > 1) {
                return invalidCancelado("isMPP demasiado largo (" + valores[5].length() + " > 1)", valores, registrosInvalidos);
            }

            NumeroCancelado num = new NumeroCancelado();
            num.setPortId(StringUtils.trimToEmpty(valores[0]));
            num.setPortType(StringUtils.trimToEmpty(valores[1]));
            num.setAction(StringUtils.trimToEmpty(valores[2]));
            num.setNumberFrom(StringUtils.trimToEmpty(valores[3]));
            num.setNumberTo(StringUtils.trimToEmpty(valores[4]));
            num.setIsMpp(StringUtils.defaultIfBlank(StringUtils.trim(valores[5]), "N"));

            // ==== Validación de numéricos obligatorios (RIDA, RCR, ASSIGNEEIDA, ASSIGNEECR) ====
            if (!isNumeric(valores[6]) || !isNumeric(valores[7]) ||
                    !isNumeric(valores[11]) || !isNumeric(valores[12])) {
                return invalidCancelado("Valores no numéricos en RIDA/RCR/AssigneeIDA/AssigneeCR", valores, registrosInvalidos);
            }

            // ==== Validar rango numérico (máx 3 dígitos) ====
            if (valores[6] != null && valores[6].length() > 3) {
                return invalidCancelado("RIDA fuera de rango (" + valores[6] + " > 3 dígitos)", valores, registrosInvalidos);
            }
            if (valores[7] != null && valores[7].length() > 3) {
                return invalidCancelado("RCR fuera de rango (" + valores[7] + " > 3 dígitos)", valores, registrosInvalidos);
            }
            if (valores[11] != null && valores[11].length() > 3) {
                return invalidCancelado("AssigneeIDA fuera de rango (" + valores[11] + " > 3 dígitos)", valores, registrosInvalidos);
            }
            if (valores[12] != null && valores[12].length() > 3) {
                return invalidCancelado("AssigneeCR fuera de rango (" + valores[12] + " > 3 dígitos)", valores, registrosInvalidos);
            }

            // ==== Asignar obligatorios ====
            num.setRida(new BigDecimal(valores[6]));
            num.setRcr(new BigDecimal(valores[7]));
            num.setAssigneeIda(new BigDecimal(valores[11]));
            num.setAssigneeCr(new BigDecimal(valores[12]));

            // ==== Opcionales (DIDA, DCR) ====
            if (StringUtils.isNotBlank(valores[8]) && isNumeric(valores[8]) && valores[8].length() <= 3) {
                num.setDida(new BigDecimal(valores[8]));
            }
            if (StringUtils.isNotBlank(valores[9]) && isNumeric(valores[9]) && valores[9].length() <= 3) {
                num.setDcr(new BigDecimal(valores[9]));
            }

            // ==== Fecha (col 10) ====
            String rawDate = StringUtils.trimToNull(valores[10]);
            if (rawDate != null) {
                Date parsed = null;
                try {
                    parsed = ACTIONDATE_FORMAT_COMPACT.parse(rawDate);
                } catch (ParseException e1) {
                    try {
                        parsed = ACTIONDATE_FORMAT_EXTENDED.parse(rawDate);
                    } catch (ParseException e2) {
                        try {
                            String sanitized = rawDate.replace("T", " ").split("\\.")[0];
                            parsed = ACTIONDATE_FORMAT_EXTENDED.parse(sanitized);
                        } catch (ParseException ignored) {
                            return invalidCancelado("ActionDate inválido: " + rawDate, valores, registrosInvalidos);
                        }
                    }
                }
                num.setActionDate(new java.sql.Timestamp(parsed.getTime()));
            } else {
                return invalidCancelado("ActionDate vacío", valores, registrosInvalidos);
            }

            return num;

        } catch (Exception ex) {
            return invalidCancelado("Error inesperado", valores, registrosInvalidos);
        }
    }

    // ==== Auxiliar: representar un registro inválido con motivo + fila cruda ====
    private static class RegistroInvalido {
        String motivo;
        String filaCsv;

        RegistroInvalido(String motivo, String filaCsv) {
            this.motivo = motivo;
            this.filaCsv = filaCsv;
        }
    }


    /**
     * Auxiliar para marcar fila inválida de Cancelados (mensaje + fila CSV limpia)
     */
    private NumeroCancelado invalidCancelado(String motivo, String[] valores, List<RegistroInvalido> registrosInvalidos) {
        String msg = "Error mapeando fila CSV: " + motivo;
        LOGGER.error("{} Contenido={}", msg, Arrays.toString(valores));

        if (registrosInvalidos != null) {
            // reconstruir la fila CSV limpia
            StringBuilder sb = new StringBuilder();
            if (valores != null && valores.length > 0) {
                for (int i = 0; i < valores.length; i++) {
                    if (i > 0) sb.append("|");
                    sb.append(valores[i] != null ? valores[i].trim() : "");
                }
            }
            registrosInvalidos.add(new RegistroInvalido(msg, sb.toString()));
        }
        return null;
    }

    /**
     * PASO 8: Genera un archivo .log con los registros inválidos de contenido CSV (Cancelados).
     * Con filtro para descartar duplicados tipo [col0, col1, ...]
     */
    private void generarLogInvalidos(List<RegistroInvalido> registrosInvalidos) {
        if (registrosInvalidos == null || registrosInvalidos.isEmpty()) {
            LOGGER.info("No se generó archivo de inválidos CSV (Cancelados): no se detectaron errores.");
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(
                new File(paramService.getParamByName("port_XMLLOG_path.portados"),
                        "port_num_cancelados_CSVinvalidos_" +
                                new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".log")))) {

            bw.write("==== Registros inválidos detectados en cancelados ====\n\n");

            for (RegistroInvalido reg : registrosInvalidos) {
                // mensaje de error
                bw.write(reg.motivo);
                bw.newLine();

                if (StringUtils.isNotBlank(reg.filaCsv)) {
                    String[] cols = reg.filaCsv.split("\\|", -1);

                    bw.write("<PortData>\n");
                    bw.write("   <PortID>"       + safe(cols,0)  + "</PortID>\n");
                    bw.write("   <PortType>"     + safe(cols,1)  + "</PortType>\n");
                    bw.write("   <Action>"       + safe(cols,2)  + "</Action>\n");

                    bw.write("   <NumberRanges>\n");
                    bw.write("      <NumberRange>\n");
                    bw.write("         <NumberFrom>" + safe(cols,3) + "</NumberFrom>\n");
                    bw.write("         <NumberTo>"   + safe(cols,4) + "</NumberTo>\n");
                    bw.write("         <isMPP>"      + safe(cols,5) + "</isMPP>\n");
                    bw.write("      </NumberRange>\n");
                    bw.write("   </NumberRanges>\n");

                    bw.write("   <RIDA>"        + safe(cols,6)  + "</RIDA>\n");
                    bw.write("   <RCR>"         + safe(cols,7)  + "</RCR>\n");
                    bw.write("   <DIDA>"        + safe(cols,8)  + "</DIDA>\n");
                    bw.write("   <DCR>"         + safe(cols,9)  + "</DCR>\n");
                    bw.write("   <ActionDate>"  + safe(cols,10) + "</ActionDate>\n");
                    bw.write("   <AssigneeIDA>" + safe(cols,11) + "</AssigneeIDA>\n");
                    bw.write("   <AssigneeCR>"  + safe(cols,12) + "</AssigneeCR>\n");
                    bw.write("</PortData>\n\n");
                }
            }

            LOGGER.info("Archivo de inválidos CSV (Cancelados) generado correctamente.");

        } catch (Exception e) {
            LOGGER.error("Error al generar log de inválidos CSV (Cancelados)", e);
        }
    }


    // Utilidad segura
    private String safe(String[] cols, int idx) {
        return (cols != null && idx < cols.length && cols[idx] != null) ? cols[idx].trim() : "";
    }

    public void generarXmlNoPersistidos(List<NumeroCancelado> noPersistidos, String timestampOriginal) throws Exception {
        LOGGER.info("Iniciando generación de XML con no persistidos de cancelados...");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        // Nodo raíz NPCData
        Element root = doc.createElement("NPCData");
        doc.appendChild(root);

        // Encabezado
        appendChild(doc, root, "MessageName", "Canceling Data");
        appendChild(doc, root, "Timestamp", timestampOriginal);

        // Número de mensajes = total fallidos
        appendChild(doc, root, "NumberOfMessages", String.valueOf(noPersistidos.size()));

        // Lista de PortData
        Element portDataList = doc.createElement("PortDataList");
        root.appendChild(portDataList);

        for (NumeroCancelado n : noPersistidos) {
            Element portData = doc.createElement("PortData");

            appendChild(doc, portData, "PortID", n.getPortId());
            appendChild(doc, portData, "PortType", n.getPortType());
            appendChild(doc, portData, "Action", n.getAction());

            // Subnodo NumberRanges -> NumberRange
            Element numberRanges = doc.createElement("NumberRanges");
            Element numberRange = doc.createElement("NumberRange");
            appendChild(doc, numberRange, "NumberFrom", n.getNumberFrom());
            appendChild(doc, numberRange, "NumberTo", n.getNumberTo());
            appendChild(doc, numberRange, "isMPP", n.getIsMpp());
            numberRanges.appendChild(numberRange);
            portData.appendChild(numberRanges);

            appendChild(doc, portData, "RIDA", n.getRida() != null ? n.getRida().toString() : "");
            appendChild(doc, portData, "RCR", n.getRcr() != null ? n.getRcr().toString() : "");
            appendChild(doc, portData, "DIDA", n.getDida() != null ? n.getDida().toString() : "");
            appendChild(doc, portData, "DCR", n.getDcr() != null ? n.getDcr().toString() : "");
            appendChild(doc, portData, "ActionDate", n.getActionDate() != null ? n.getActionDate().toString() : "");

            portDataList.appendChild(portData);
        }

        // Path desde parámetros
        String basePath = paramService.getParamByName("port_XMLLOG_path.portados");
        if (StringUtils.isEmpty(basePath)) {
            LOGGER.warn("Parametro port_XMLLOG_path.portados no definido, usando directorio actual");
            basePath = ".";
        }
        File dir = new File(basePath);
        if (!dir.exists()) {
            LOGGER.warn("Directorio {} no existe, verificar configuración de parámetros", basePath);
        }

        // Nombre con timestamp actual para diferenciar ejecuciones
        String fechaStr = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File outFile = new File(dir, "port_num_cancelados_fallidos_" + fechaStr + ".xml");

        // Escribir XML
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(doc), new StreamResult(outFile));

        LOGGER.info("Archivo XML de no persistidos de cancelados generado: {}", outFile.getAbsolutePath());
    }

    /**
     * Utilidad para agregar hijos de texto a un elemento.
     */
    private void appendChild(Document doc, Element parent, String name, String value) {
        Element e = doc.createElement(name);
        e.appendChild(doc.createTextNode(value != null ? value : ""));
        parent.appendChild(e);
    }

    /**
     * @return the hayError
     */
    public boolean isHayError() {
        return hayError;
    }

    /**
     * @param hayError the hayError to set
     */
    public void setHayError(boolean hayError) {
        this.hayError = hayError;
    }

    /**
     * @return the res
     */
    public ResultadoValidacionCSV getRes() {
        return res;
    }

    /**
     * @param res the res to set
     */
    public void setRes(ResultadoValidacionCSV res) {
        this.res = res;
    }

    /**
     * @return the totalFilas
     */
    public int getTotalFilas() {
        return totalFilas;
    }

    /**
     * @param totalFilas the totalFilas to set
     */
    public void setTotalFilas(int totalFilas) {
        this.totalFilas = totalFilas;
    }

    /**
     * @return the terminar
     */
    public boolean isTerminar() {
        return terminar;
    }

    /**
     * @param terminar the terminar to set
     */
    public void setTerminar(boolean terminar) {
        this.terminar = terminar;
    }
}

/*
public ResultadoValidacionCSV validar(String fileName) throws Exception {
        LOGGER.debug("fichero {}", fileName);

        ResultadoValidacionCSV res = new ResultadoValidacionCSV();
        int totalFilas = 0;
        int successCount = 0;
        int failCount = 0;
        //final int BATCH_SIZE = 2500;
        List<NumeroCancelado> lote = new ArrayList<>(BATCH_SIZE);

        if (StringUtils.isEmpty(fileName)) {
            LOGGER.debug("nombre fichero vacio");
            res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO);
            return res;
        }

        File f1 = new File(fileName);
        if (!f1.exists()) {
            LOGGER.debug("fichero no existe");
            res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO);
            return res;
        }

        try (FileReader freader = new FileReader(fileName);
             CSVReader cvsReader = new CSVReader(freader, DELIMITADOR, '"', 1)) {

            res.setError(ResultadoValidacionArrendamiento.VALIDACION_OK);

            List<String[]> allRows = cvsReader.readAll();
            LOGGER.info("-----------------Tamaño del fichero csvdeleted {}", allRows.size());

            if (!allRows.isEmpty()) {
                LOGGER.info("<--Inicia el recorrido del csv {}", FechasUtils.getActualDate());

                //Eliminar registros del día procesado
                for (String[] row : allRows) {
                    LOGGER.info("Operación borrado");
                    if (row != null && row.length >= 13) {
                        try {
                            //LOGGER.info("Cumple condición row != null");
                            NumeroCancelado primera = mapRowANumeroCancelado(row);
                            if (primera != null && primera.getActionDate() != null) {
                                // Generar fecha como string tipo 'yyyy-MM-dd'
                                String fechaStr = new SimpleDateFormat("yyyy-MM-dd").format(primera.getActionDate());
                                //LOGGER.info("Ocupa metodo canceladosDAO con uso de fechaStr {}", fechaStr);
                                int borradosPrevios = canceladosDAO.deleteCanceladosPorFechaStr(fechaStr);
                                //LOGGER.info("Se eliminaron {} registros previos de cancelados para la fecha {}", borradosPrevios, fechaStr);
                            }
                        } catch (Exception e) {
                            LOGGER.error("Error al eliminar registros del día procesado: {}", Arrays.toString(row), e);
                        }
                        break; // solo necesitamos una fila válida
                    }
                }

                for (String[] row : allRows) {
                    totalFilas++;
                    // Validación básica
                    if (row == null || row.length < 13) {
                        LOGGER.error("Fila incompleta (esperado 13 columnas, recibido: {}). Fila omitida: {}", (row == null ? 0 : row.length), Arrays.toString(row));
                        failCount++;
                        continue;
                    }
                    // Mapeo según tu método checkFila/mapRowANumeroCancelado
                    NumeroCancelado num = mapRowANumeroCancelado(row);
                    if (num != null) {
                        lote.add(num);
                    }
                    // Cuando el lote llega a 500, procesa
                    if (lote.size() == BATCH_SIZE) {
                        //int procesados = canceladosDAO.procesarCanceladosBatch(lote);
                        //int insertados = canceladosDAO.upsertCanceladosBatch(lote);
                        //LOGGER.info("Manda llamar a canceladosDAO.procesarCanceladosConLimpiezaPorFecha(lote) con lote");
                        int insertados = canceladosDAO.procesarCanceladosConLimpiezaPorFecha(lote);
                        LOGGER.info("Batch de {} procesados (insert/deleted)", insertados);
                        //int borrados = canceladosDAO.deleteDePortadosPorNumero(lote);
                        //LOGGER.info("Proceso terminado. Cancelados insertados/actualizados: {}", insertados);
                        successCount += insertados;
                        lote.clear();
                    }
                }
                // Procesa el último lote pendiente
                if (!lote.isEmpty()) {
                    //int procesados = canceladosDAO.procesarCanceladosBatch(lote);
                    //LOGGER.info("Manda llamar a canceladosDAO.procesarCanceladosConLimpiezaPorFecha(lote) con lote");
                    int insertados = canceladosDAO.procesarCanceladosConLimpiezaPorFecha(lote);
                    LOGGER.info("Batch de {} procesados (insert/deleted)", insertados);
                    //int insertados = canceladosDAO.upsertCanceladosBatch(lote);
                    //int borrados = canceladosDAO.deleteDePortadosPorNumero(lote);
                    //LOGGER.info("Proceso terminado. Cancelados insertados/actualizados: {}", insertados);
                    successCount += insertados;
                    lote.clear();
                }
            } else {
                LOGGER.debug("fichero vacio");
                res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO_VACIO);
            }
        } catch (Exception e) {
            res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO);
            LOGGER.error("Error validando archivo {}", fileName, e);
        }

        LOGGER.info("RESUMEN FINAL CANCELADOS: Exitosos = {}, Fallidos = {}, Total filas = {}", successCount, failCount, totalFilas);

        return res;
    }
 */


/*
public ResultadoValidacionCSV validar(String fileName) throws Exception {

        LOGGER.debug("fichero {}", fileName);

        totalFilas = 0;
        int successCount = 0;
        int failCount = 0;
        int POOL_SIZE = 8; // O ajústalo según tu infra

        if (StringUtils.isEmpty(fileName)) {
            LOGGER.debug("nombre fichero vacio");
            res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO);

            return res;
        }

        File f1 = new File(fileName);
        if (!f1.exists()) {
            LOGGER.debug("fichero no existe");
            res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO);
            return res;
        }

        ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);
        List<Future<Boolean>> futures = new ArrayList<>();

        try (FileReader freader = new FileReader(fileName)){

            res.setError(ResultadoValidacionArrendamiento.VALIDACION_OK);
            //FileReader freader = null;
            //freader = new FileReader(fileName);
            cvsReader = new CSVReader(freader, DELIMITADOR,'"',1);
            List<String[]> allRows = cvsReader.readAll();
            LOGGER.info("-----------------Tamaño del fichero csvdeleted "+ allRows.size() );

            //int cpus = Runtime.getRuntime().availableProcessors();

            if (!allRows.isEmpty()) {
                LOGGER.info("<--Inicia el recorrido del csv {}", FechasUtils.getActualDate());

                for (final String[] row : allRows) {
                    futures.add(executor.submit(new Callable<Boolean>() {
                        @Override
                        public Boolean call() {
                            try {
                                // Aquí llamas tu método asíncrono y esperas el resultado
                                return procesadorCancelados.procesarAsync(row).get();
                            } catch (Exception e) {
                                LOGGER.error("Error procesando registro", e);
                                return false;
                            }
                        }
                    }));
                }

                /*
                List<Future<Boolean>> futures = new ArrayList<>();
                for (String[] row : allRows) {
                    futures.add(procesadorCancelados.procesarAsync(row));
                }
                 */
/*
// Espera a que todos terminen (importante)
                for (Future<Boolean> f : futures) {
        try {
        Boolean ok = f.get(); // Espera a que termine
        if (Boolean.TRUE.equals(ok)) {
        successCount++;
        } else {
        failCount++;
        }
        //f.get(); // espera a que termine, puedes manejar el resultado
        } catch (Exception e) {
        LOGGER.error("Error esperando procesamiento asíncrono", e);
        LOGGER.info("Procesados exitosamente: {} / Fallidos: {}", successCount, failCount);
        }
        }
        LOGGER.info("<---Finished all threads---->");
        } else {
        LOGGER.debug("fichero vacio");
        res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO_VACIO);
        }

            /*
            if (allRows.size() > 0) {
            	LOGGER.info("<--Inicia el recorido del csv {}",FechasUtils.getActualDate());
            	//ExecutorService executorServiceCancelados=Executors.newFixedThreadPool(cpus);
				for (String[] row : allRows) {
                    //FJAH 24052025
					//Runnable worker=new ProcesarRegistrosCancelados(row);
					//executorServiceCancelados.execute(worker);
					//checkFila(row);
                    procesadorCancelados.procesarAsync(row);

				}
                //FJAH 24052025
				//executorServiceCancelados.shutdown();
				//while (!executorServiceCancelados.isTerminated()) {}
				LOGGER.info("<---Finished all threads---->");
				LOGGER.info("<--finaliza el recorido del csv {}",FechasUtils.getActualDate());
                }
            else
            {
                // fichero vacio. //
                LOGGER.debug("fichero vacio");
                res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO_VACIO);
            }

             */

/*
        } catch (Exception e) {
        // fichero incorrecto. //
        res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO);

        LOGGER.error("Error validando archivo {}", fileName, e);
        } finally {
        if (cvsReader != null) {
        cvsReader.close();
        }
        }

        LOGGER.debug("fin validacion {} filas={} res={}", fileName, totalFilas, res.getError());
        LOGGER.info("RESUMEN FINAL CANCELADOS: Exitosos = {}, Fallidos = {}", successCount, failCount);

        return res;
        }

 */


