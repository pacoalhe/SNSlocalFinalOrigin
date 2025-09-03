package mx.ift.sns.negocio.utils.csv;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;


import mx.ift.sns.modelo.port.NumeroPortado;
import mx.ift.sns.negocio.conf.IParametrosService;
import mx.ift.sns.negocio.ng.model.ResultadoValidacionArrendamiento;
import mx.ift.sns.negocio.ng.model.ResultadoValidacionCSV;
import mx.ift.sns.negocio.port.PortadosDAO;
import mx.ift.sns.negocio.port.ProcesarRegistrosCancelados;
import mx.ift.sns.negocio.port.ProcesarRegistrosPortados;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Validador de archivos csv.
 */

@Stateless
public class ValidadorArchivoPortadosCSV {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidadorArchivoPortadosCSV.class);

    //FJAH 26052025
    public ValidadorArchivoPortadosCSV() {
        // Constructor vacío requerido por EJB
    }

    final int BATCH_SIZE = 5000;

    //FJAH 26052025
    @EJB
    private PortadosDAO portadosDAO;

    /** Servicio configuracion. */
    @EJB
    private IParametrosService paramService;

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
    private ProcesarRegistrosPortados procesadorPortados;

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

    //FJAH 26052025 Refactorización del metodo con snapshots ORIGEN/ANTES/DESPUES
    // FJAH 26052025 Refactorización del método con snapshots unificados (ORIGEN/ESTADO/ESTADO_FINAL)
    // FJAH 26052025 Refactorización con snapshots unificados (ORIGEN/ESTADO/ESTADO_FINAL)
    public ResultadoValidacionCSV validar(String fileName) throws Exception {
        LOGGER.info("fichero {}", fileName);

        ResultadoValidacionCSV res = new ResultadoValidacionCSV();
        int totalFilas = 0;
        int failCount = 0;

        String actionDateLote = null;
        String timestampOriginal = null;

        List<String> registrosInvalidos = new ArrayList<String>();
        List<NumeroPortado> loteTotal = new ArrayList<NumeroPortado>();

        // Intentar obtener timestamp SOLO si es XML
        if (fileName != null && fileName.toLowerCase().endsWith(".xml")) {
            try {
                timestampOriginal = obtenerTimestampDeXml(fileName);
            } catch (Exception e) {
                LOGGER.warn("No se pudo obtener timestamp del XML original: {}", e.getMessage());
            }
        }
        if (timestampOriginal == null) {
            timestampOriginal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        }

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

        CSVReader cvsReader = null;
        try (FileReader freader = new FileReader(fileName)) {
            res.setError(ResultadoValidacionArrendamiento.VALIDACION_OK);
            //cvsReader = new CSVReader(freader, DELIMITADOR, '"', 1);
            cvsReader = new CSVReader(freader, '|', '"', 0);

            List<String[]> allRows = cvsReader.readAll();
            LOGGER.debug("--------------------------- tamaño del fichero csvportados {}", allRows.size());

            if (!allRows.isEmpty()) {
                LOGGER.info("<-- Inicia el recorrido del csv {}", FechasUtils.getActualDate());

                // Paso 0: armar lote ORIGEN
                for (String[] row : allRows) {
                    NumeroPortado np = mapeaRowANumeroPortado(row, registrosInvalidos);
                    if (np != null && np.getNumberFrom() != null) {
                        loteTotal.add(np);
                        totalFilas++;
                        if (actionDateLote == null && np.getActionDate() != null) {
                            actionDateLote = String.valueOf(np.getActionDate());
                        }
                    } else {
                        failCount++;
                        registrosInvalidos.add(Arrays.toString(row));
                    }
                }

                // Paso 1: snapshot ORIGEN
                LOGGER.info("<--- Paso 1: guardar snapshot ORIGEN ---->");
                portadosDAO.insertSnapshotOrigen(loteTotal);

                // Paso 2: clasificar registros (INSERTAR / ACTUALIZAR)
                LOGGER.info("<--- Paso 2: clasificar registros ---->");
                portadosDAO.clasificarSnapshot();

                // Paso 3: ejecutar MERGE con fallback por lotes
                LOGGER.info("<--- Paso 3: ejecutar MERGE ---->");
                //int procesados = portadosDAO.upsertBatchNoStopMergeBatch(loteTotal);
                //int procesados = portadosDAO.upsertMergeBatchUnionAll(loteTotal);
                //TODO Caso de prueba 1 bloquear llamado
                int procesados = portadosDAO.upsertHibrido(loteTotal, loteTotal.size());

                // Paso 4: actualizar ESTADO_FINAL
                LOGGER.info("<--- Paso 4: actualizar ESTADO_FINAL ---->");
                portadosDAO.actualizarEstadoFinalInsertar();
                portadosDAO.actualizarEstadoFinalActualizar();

                // Paso 5: obtener totales
                LOGGER.info("<--- Paso 5: obtener totales ---->");
                ResultadoValidacionCSV totales = portadosDAO.getTotalesSnapshotPortados();
                //res.setTotalOrigen(totales.getTotalOrigen());
                res.setTotalProcesados(totales.getTotalProcesados());
                res.setTotalInsertados(totales.getTotalInsertados());
                res.setTotalActualizados(totales.getTotalActualizados());
                res.setTotalNoPersistidos(totales.getTotalNoPersistidos());
                res.setTotalPendientesInsertar(totales.getTotalPendientesInsertar());
                res.setTotalPendientesActualizar(totales.getTotalPendientesActualizar());
                res.setTotalErrorEstructura(registrosInvalidos.size());


                LOGGER.info("Totales -> Origen={}, Procesados={}, Insertados={}, Actualizados={}, Fallidos={}, PendientesInsertar={}, PendientesActualizar={}, ErrorEstructura={}",
                        res.getTotalOrigen(),
                        res.getTotalProcesados(),
                        res.getTotalInsertados(),
                        res.getTotalActualizados(),
                        res.getTotalNoPersistidos(),
                        res.getTotalPendientesInsertar(),
                        res.getTotalPendientesActualizar(),
                        res.getTotalErrorEstructura());

                // Paso 6: generar log de inválidos
                LOGGER.info("<--- Paso 6: generar log de inválidos ---->");
                if (!registrosInvalidos.isEmpty()) {
                    generarLogInvalidos(registrosInvalidos);
                }

                // Paso 7: generar XML de fallidos
                LOGGER.info("<--- Paso 7: generar XML de fallidos ---->");
                try {
                    portadosDAO.generarXmlFallidosBatch();
                    LOGGER.info("XML de no persistidos generado correctamente (streaming desde BD).");
                } catch (Exception e) {
                    LOGGER.error("Error generando XML de no persistidos (fallidos).", e);
                }


            } else {
                res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO_VACIO);
            }
        } finally {
            if (cvsReader != null) {
                cvsReader.close();
            }
        }

        res.setActionDateLote(actionDateLote);
        return res;
    }


    /*
    public ResultadoValidacionCSV validar(String fileName) throws Exception {
        LOGGER.debug("fichero {}", fileName);

        ResultadoValidacionCSV res = new ResultadoValidacionCSV();
        int successCount = 0;
        int failCount = 0;
        int totalFilas = 0;

        String actionDateLote = null;
        String timestampOriginal = null;

        List<String> registrosInvalidos = new ArrayList<String>(); // para log
        List<NumeroPortado> loteTotal = new ArrayList<NumeroPortado>(); // todos los válidos
        List<String> numbersOrigen = new ArrayList<String>(); // solo los numberFrom

        // Intentar obtener timestamp SOLO si es XML
        if (fileName != null && fileName.toLowerCase().endsWith(".xml")) {
            try {
                timestampOriginal = obtenerTimestampDeXml(fileName);
            } catch (Exception e) {
                LOGGER.warn("No se pudo obtener timestamp del XML original: {}", e.getMessage());
            }
        }

        // Fallback: si sigue nulo, usar actionDateLote o la fecha actual
        if (timestampOriginal == null) {
            if (actionDateLote != null) {
                timestampOriginal = actionDateLote;  // usar el mismo actionDate del lote
            } else {
                timestampOriginal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            }
        }

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

        CSVReader cvsReader = null;
        try (FileReader freader = new FileReader(fileName)) {
            res.setError(ResultadoValidacionArrendamiento.VALIDACION_OK);
            cvsReader = new CSVReader(freader, DELIMITADOR, '"', 1);

            List<String[]> allRows = cvsReader.readAll();
            LOGGER.debug("---------------------------tamaño del fichero csvportados {}", allRows.size());

            if (!allRows.isEmpty()) {
                LOGGER.info("<--Inicia el recorrido del csv {}", FechasUtils.getActualDate());

                // Paso 0: armar lote ORIGEN (todos los del archivo)
                LOGGER.info("<--Paso -1 armar lote ORIGEN (todos los del archivo");
                for (String[] row : allRows) {
                    NumeroPortado np = mapeaRowANumeroPortado(row);
                    if (np != null && np.getNumberFrom() != null) {
                        loteTotal.add(np);
                        numbersOrigen.add(np.getNumberFrom());
                    }
                }

                // Paso 0: snapshot ORIGEN
                LOGGER.info("<---Paso 0: guardar snapshot ORIGEN---->");
                portadosDAO.insertSnapshotOrigen(loteTotal);

                // Paso 1: snapshot ANTES
                LOGGER.info("<---Paso 1: obtener snapshot ANTES---->");
                portadosDAO.insertSnapshotAntes(numbersOrigen);

                // Paso 1.1: procesar archivo (MERGE)
                LOGGER.info("<---Paso 1.1: Recorrido del archivo y MERGE---->");
                List<NumeroPortado> batch = new ArrayList<NumeroPortado>();

                for (NumeroPortado n : loteTotal) {
                    totalFilas++;
                    try {
                        if (actionDateLote == null && n.getActionDate() != null) {
                            actionDateLote = String.valueOf(n.getActionDate());
                        }

                        batch.add(n);

                        if (batch.size() == BATCH_SIZE) {
                            successCount += portadosDAO.upsertBatchNoStopMergeBatch(batch);
                            LOGGER.info("Batch enviado: {} filas | Procesados OK: {}", batch.size(), successCount);
                            batch.clear();
                        }
                    } catch (Exception ex) {
                        failCount++;
                        registrosInvalidos.add(n.toString());
                        LOGGER.warn("Registro inválido en fila {}: {}", totalFilas, n, ex);
                    }
                }

                if (!batch.isEmpty()) {
                    successCount += portadosDAO.upsertBatchNoStopMergeBatch(batch);
                    LOGGER.info("Batch final de {} procesados (insert/update) | Total OK: {}", batch.size(), successCount);
                }

                LOGGER.info("<---Batch processing terminado---->");

                // Paso 2: snapshot DESPUES
                LOGGER.info("<---Paso 2: obtener snapshot DESPUES---->");
                portadosDAO.insertSnapshotDespues(numbersOrigen);

                // Paso 3: comparar ORIGEN vs ANTES vs DESPUES
                LOGGER.info("<---Paso 3: comparar snapshots---->");
                int insertados = 0;
                int actualizados = 0;
                int noPersistidos = 0;
                List<NumeroPortado> noPersistidosGlobal = new ArrayList<>();

                try {
                    insertados = portadosDAO.getInsertados();
                    actualizados = portadosDAO.getActualizados();
                    noPersistidosGlobal = portadosDAO.getFallidos();
                    noPersistidos = (noPersistidosGlobal != null) ? noPersistidosGlobal.size() : 0;

                    LOGGER.info("Comparación snapshots -> Insertados={}, Actualizados={}, NoPersistidos={}",
                            insertados, actualizados, noPersistidos);
                } catch (Exception e) {
                    LOGGER.error("Error consultando snapshots en DAO", e);
                }

                // Paso 4: guardar totales
                LOGGER.info("<---Paso 4: guardar totales---->");
                res.setTotalOrigen(totalFilas);
                res.setTotalProcesados(successCount);
                res.setTotalErrorEstructura(failCount);
                res.setTotalInsertados(insertados);
                res.setTotalActualizados(actualizados);
                res.setTotalNoPersistidos(noPersistidos);

                LOGGER.info("Totales -> Origen={}, Procesados={}, Insertados={}, Actualizados={}, NoPersistidos={}, ErrorEstructura={}, FallidosGlobal={}",
                        res.getTotalOrigen(),
                        res.getTotalProcesados(),
                        res.getTotalInsertados(),
                        res.getTotalActualizados(),
                        res.getTotalNoPersistidos(),
                        res.getTotalErrorEstructura(),
                        res.getTotalFallidosGlobal());

                // Paso 5: generar log de inválidos si aplica
                LOGGER.info("<---Paso 5: generar log de inválidos si aplica---->");
                if (!registrosInvalidos.isEmpty()) {
                    generarLogInvalidos(registrosInvalidos);
                }

                // Paso 6: certificación BD vs archivo origen (XML de no persistidos)
                LOGGER.info("<---Paso 6: certificación BD vs archivo origen---->");
                try {
                    if (noPersistidosGlobal != null && !noPersistidosGlobal.isEmpty()) {
                        generarXmlNoPersistidos(noPersistidosGlobal, timestampOriginal);
                        LOGGER.info("XML de no persistidos generado. Total={}", noPersistidosGlobal.size());
                    } else {
                        LOGGER.info("No hay no persistidos para generar XML.");
                    }
                } catch (Exception certEx) {
                    LOGGER.error("Error al generar XML de no persistidos", certEx);
                }

            } else {
                res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO_VACIO);
            }
        } finally {
            if (cvsReader != null) {
                cvsReader.close();
            }
        }

        res.setActionDateLote(actionDateLote);
        LOGGER.info("actionDateLote que se setea en resultado: {}", actionDateLote);
        return res;
    }

     */

    /**
     * Obtiene el valor del <Timestamp> del XML original.
     */
    private String obtenerTimestampDeXml(String fileName) {
        try {
            File xmlFile = new File(fileName.replace(".csv", ".xml"));
            if (!xmlFile.exists()) {
                LOGGER.warn("Archivo XML original no encontrado para extraer Timestamp: {}", xmlFile.getAbsolutePath());
                return null;
            }

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("Timestamp");
            if (list.getLength() > 0) {
                return list.item(0).getTextContent().trim();
            }
        } catch (Exception e) {
            LOGGER.error("Error obteniendo Timestamp del XML original", e);
        }
        return null;
    }

    // Mapear fila CSV a NumeroPortado
    // helper para validar números
    private boolean isNumeric(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        try {
            new BigDecimal(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private NumeroPortado mapeaRowANumeroPortado(String[] valores, List<String> registrosInvalidos) {
        // Validación previa
        if (valores == null || valores.length < 11) {
            LOGGER.error("Fila inválida en CSV: esperado 11 columnas, recibidas {}. Contenido={}",
                    (valores != null ? valores.length : -1),
                    Arrays.toString(valores));

            if (registrosInvalidos != null) {
                registrosInvalidos.add("Fila inválida en CSV: " + Arrays.toString(valores));
            }
            return null; // fila no utilizable
        }

        NumeroPortado num = new NumeroPortado();

        try {
            num.setPortId(StringUtils.trim(valores[0]));
            num.setPortType(StringUtils.trim(valores[1]));
            num.setAction(StringUtils.trim(valores[2]));
            num.setNumberFrom(StringUtils.trim(valores[3]));
            num.setNumberTo(StringUtils.trim(valores[4]));
            num.setIsMpp(StringUtils.trim(valores[5]));

            // ==== Validar RIDA, RCR, DIDA, DCR ====
            if (!isNumeric(valores[6]) ||
                    !isNumeric(valores[7]) ||
                    (!"null".equalsIgnoreCase(valores[8]) && !isNumeric(valores[8])) ||
                    (!"null".equalsIgnoreCase(valores[9]) && !isNumeric(valores[9]))) {

                StringBuilder sb = new StringBuilder();
                sb.append("Error mapeando fila CSV (valores no numéricos):\n");
                sb.append("<PortData>\n");
                sb.append("  <PortID>").append(valores[0]).append("</PortID>\n");
                sb.append("  <PortType>").append(valores[1]).append("</PortType>\n");
                sb.append("  <Action>").append(valores[2]).append("</Action>\n");
                sb.append("  <NumberFrom>").append(valores[3]).append("</NumberFrom>\n");
                sb.append("  <NumberTo>").append(valores[4]).append("</NumberTo>\n");
                sb.append("  <isMPP>").append(valores[5]).append("</isMPP>\n");
                sb.append("  <RIDA>").append(valores[6]).append("</RIDA>\n");
                sb.append("  <RCR>").append(valores[7]).append("</RCR>\n");
                sb.append("  <DIDA>").append(valores[8]).append("</DIDA>\n");
                sb.append("  <DCR>").append(valores[9]).append("</DCR>\n");
                sb.append("  <ActionDate>").append(valores[10]).append("</ActionDate>\n");
                sb.append("</PortData>");

                LOGGER.error(sb.toString());

                if (registrosInvalidos != null) {
                    registrosInvalidos.add(sb.toString());
                }

                return null; // descartamos fila inválida
            }

            // ==== Si son válidos, convertir ====
            num.setRida(new BigDecimal(valores[6]));
            num.setRcr(new BigDecimal(valores[7]));
            num.setDida(!"null".equalsIgnoreCase(valores[8]) ? new BigDecimal(valores[8]) : null);
            num.setDcr(!"null".equalsIgnoreCase(valores[9]) ? new BigDecimal(valores[9]) : null);
            num.setActionDate(StringUtils.trim(valores[10]));

        } catch (Exception e) {
            LOGGER.error("Error inesperado mapeando fila CSV: {}", Arrays.toString(valores), e);
            if (registrosInvalidos != null) {
                registrosInvalidos.add("Error inesperado mapeando fila CSV: " + Arrays.toString(valores));
            }
            return null;
        }

        return num;
    }

    /**
     * Paso 6: Genera un archivo .log con los registros inválidos de contenido CSV (Portados).
     * idéntico a la estructura del insumo original de PORTADOS.
     * Solo se genera cuando hay errores.
     * Si no hay errores, no se crea ningún archivo.
     */
    private void generarLogInvalidos(List<String> registrosInvalidos) {
        if (registrosInvalidos == null || registrosInvalidos.isEmpty()) {
            LOGGER.info("No se generó archivo de inválidos CSV (Portados): no se detectaron errores.");
            return;
        }

        try {
            String basePath = paramService.getParamByName("port_XMLLOG_path.portados");
            if (StringUtils.isEmpty(basePath)) {
                LOGGER.warn("Parametro port_XMLLOG_path.portados no definido, usando directorio actual");
                basePath = ".";
            }

            File dir = new File(basePath);
            if (!dir.exists()) {
                LOGGER.warn("Directorio {} no existe, verificar configuración de parámetros", basePath);
            }

            // Nombre con solo la fecha
            String fechaStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
            File logFile = new File(dir, "port_num_portados_CSVinvalidos_" + fechaStr + ".log");

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile))) {
                for (String linea : registrosInvalidos) {
                    bw.write(linea);
                    bw.newLine();
                }
            }

            LOGGER.info("Archivo de inválidos CSV generado: {}", logFile.getAbsolutePath());
        } catch (Exception e) {
            LOGGER.error("Error al generar archivo de inválidos CSV", e);
        }
    }

    /**
     * Paso 7: genera un XML con los no persistidos,
     * idéntico a la estructura del insumo original de PORTADOS.
     * Solo se genera cuando hay registros fallidos.
     */
    private void generarXmlNoPersistidos(List<NumeroPortado> noPersistidos, String timestampOriginal) throws Exception {
        if (noPersistidos == null || noPersistidos.isEmpty()) {
            LOGGER.info("No se generó XML de no persistidos (Portados): no hay registros fallidos.");
            return;
        }

        LOGGER.info("Iniciando generación de XML con no persistidos (Portados)...");

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

        // Nombre con solo la fecha
        String fechaStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        File outFile = new File(dir, "port_num_portados_fallidos_" + fechaStr + ".xml");

        // Formato correcto para ActionDate
        SimpleDateFormat sdfActionDate = new SimpleDateFormat("yyyyMMddHHmmss");

        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        try (OutputStream out = new FileOutputStream(outFile)) {
            XMLStreamWriter writer = factory.createXMLStreamWriter(out, "UTF-8");

            // Encabezado XML
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeStartElement("NPCData");

            // Encabezado
            writer.writeStartElement("MessageName");
            writer.writeCharacters("Porting Data");
            writer.writeEndElement();

            writer.writeStartElement("Timestamp");
            writer.writeCharacters(timestampOriginal != null ? timestampOriginal : "");
            writer.writeEndElement();

            writer.writeStartElement("NumberOfMessages");
            writer.writeCharacters(String.valueOf(noPersistidos.size()));
            writer.writeEndElement();

            // Lista de PortData
            writer.writeStartElement("PortDataList");

            for (NumeroPortado n : noPersistidos) {
                writer.writeStartElement("PortData");

                writer.writeStartElement("PortID");
                writer.writeCharacters(n.getPortId() != null ? n.getPortId() : "");
                writer.writeEndElement();

                writer.writeStartElement("PortType");
                writer.writeCharacters(n.getPortType() != null ? n.getPortType() : "");
                writer.writeEndElement();

                writer.writeStartElement("Action");
                writer.writeCharacters(n.getAction() != null ? n.getAction() : "");
                writer.writeEndElement();

                // NumberRanges -> NumberRange
                writer.writeStartElement("NumberRanges");
                writer.writeStartElement("NumberRange");

                writer.writeStartElement("NumberFrom");
                writer.writeCharacters(n.getNumberFrom() != null ? n.getNumberFrom() : "");
                writer.writeEndElement();

                writer.writeStartElement("NumberTo");
                writer.writeCharacters(n.getNumberTo() != null ? n.getNumberTo() : "");
                writer.writeEndElement();

                writer.writeStartElement("isMPP");
                writer.writeCharacters(n.getIsMpp() != null ? n.getIsMpp() : "");
                writer.writeEndElement();

                writer.writeEndElement(); // </NumberRange>
                writer.writeEndElement(); // </NumberRanges>

                writer.writeStartElement("RIDA");
                writer.writeCharacters(n.getRida() != null ? n.getRida().toString() : "");
                writer.writeEndElement();

                writer.writeStartElement("RCR");
                writer.writeCharacters(n.getRcr() != null ? n.getRcr().toString() : "");
                writer.writeEndElement();

                writer.writeStartElement("DIDA");
                writer.writeCharacters(n.getDida() != null ? n.getDida().toString() : "");
                writer.writeEndElement();

                writer.writeStartElement("DCR");
                writer.writeCharacters(n.getDcr() != null ? n.getDcr().toString() : "");
                writer.writeEndElement();

                writer.writeStartElement("ActionDate");
                if (n.getActionDate() != null) {
                    writer.writeCharacters(sdfActionDate.format(n.getActionDate()));
                } else {
                    writer.writeCharacters("");
                }
                writer.writeEndElement();

                writer.writeEndElement(); // </PortData>
            }

            writer.writeEndElement(); // </PortDataList>
            writer.writeEndElement(); // </NPCData>
            writer.writeEndDocument();
            writer.flush();
        }

        LOGGER.info("Archivo XML de no persistidos (Portados) generado: {} (Total={})", outFile.getAbsolutePath(), noPersistidos.size());
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

        try (FileReader freader = new FileReader(fileName)) {

            res.setError(ResultadoValidacionArrendamiento.VALIDACION_OK);
            //FileReader freader = null;
            //freader = new FileReader(fileName);
            cvsReader = new CSVReader(freader, DELIMITADOR,'"',1);

            List<String[]> allRows = cvsReader.readAll();
            LOGGER.debug("---------------------------tamaño del fichero csvportados "+allRows.size());
            //int cpus = Runtime.getRuntime().availableProcessors();

            if (!allRows.isEmpty()) {
                LOGGER.info("<--Inicia el recorido del csv {}", FechasUtils.getActualDate());

                /*
                List<Future<Boolean>> futures = new ArrayList<>();
                for (String[] row : allRows) {
                    futures.add(procesadorPortados.procesarAsync(row));
                }

                 */
/*
                for (final String[] row : allRows) {
                    futures.add(executor.submit(new Callable<Boolean>() {
                        @Override
                        public Boolean call() {
                            try {
                                // Aquí llamas tu método asíncrono y esperas el resultado
                                return procesadorPortados.procesarAsync(row).get();
                            } catch (Exception e) {
                                LOGGER.error("Error procesando registro", e);
                                return false;
                            }
                        }
                    }));
                }

                // Espera a que todos terminen (importante)
                for (Future<Boolean> f : futures) {
                    try {
                        Boolean ok = f.get();
                        if (Boolean.TRUE.equals(ok)) {
                            successCount++;
                        } else {
                            failCount++;
                        }
                        //f.get(); // espera a que termine, puedes manejar el resultado
                    } catch (Exception e) {
                        failCount++;
                        LOGGER.error("Error esperando procesamiento asíncrono", e);
                    }
                }
                LOGGER.info("<---Finished all threads---->");
                LOGGER.info("Procesados exitosamente: {} / Fallidos: {}", successCount, failCount);
            } else {
                LOGGER.debug("fichero vacio");
                res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO_VACIO);
            }

            /*
            if (allRows.size() > 0) {

                //ExecutorService executorServicePortados=Executors.newFixedThreadPool(cpus);
                for (String[] row : allRows) {
                    //FJAH 24052025
                    //Runnable worker=new ProcesarRegistrosPortados(row);
                    //executorServicePortados.execute(worker);
                    //checkFila(row);
                    //executorServicePortados.execute(worker);
                    procesadorPortados.procesarAsync(row);

                }
                //FJAH 24052025
                //executorServicePortados.shutdown();
                //executorServicePortados.awaitTermination(30, TimeUnit.MINUTES);
                //while (!executorServicePortados.isTerminated()) {}
                LOGGER.info("<---Finished all threads---->");

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
 */
    /*

        } finally {
            executor.shutdown();
            if (cvsReader != null) {
                cvsReader.close();
            }
        }

        LOGGER.debug("fin validacion {} filas={} res={}", fileName, totalFilas, res.getError());

        // Resumen al resultado si lo requieres
        LOGGER.info("RESUMEN FINAL PORTADOS: Exitosos = {}, Fallidos = {}", successCount, failCount);

        return res;
    }

     */


