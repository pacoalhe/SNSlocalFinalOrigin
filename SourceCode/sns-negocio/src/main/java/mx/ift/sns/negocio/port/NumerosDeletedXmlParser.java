package mx.ift.sns.negocio.port;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import mx.ift.sns.modelo.port.NumeroCancelado;

import mx.ift.sns.negocio.port.modelo.ResultadoParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Parser XML del fichero Numeros Deleted.
 */
public class NumerosDeletedXmlParser extends DefaultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NumerosDeletedXmlParser.class);
    private BufferedWriter writer;
    private NumeroCancelado currentNumero;
    private StringBuilder currentValue = new StringBuilder();

    private int totalDeclarados = 0;
    private int totalGenerados = 0;
    private int invalidosXml = 0;

    // === Constructor vacío (legacy, usado en ParseTest) ===
    public NumerosDeletedXmlParser() {
        // writer se inicializará en parse(File outFile,...)
    }

    // === Constructor nuevo (refactor, usado en flujo actual) ===
    public NumerosDeletedXmlParser(Writer writer) {
        this.writer = new BufferedWriter(writer);
    }

    // Setter opcional
    public void setWriter(Writer writer) {
        this.writer = new BufferedWriter(writer);
    }

    // === Método parse (nuevo, recibe File directamente) ===
    public void parse(String xmlPath, File outFile, ResultadoParser res) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        if (this.writer == null && outFile != null) {
            this.writer = new BufferedWriter(new FileWriter(outFile));
        }

        saxParser.parse(xmlPath, this);

        if (this.writer != null) {
            this.writer.flush();
            this.writer.close();
        }

        LOGGER.info("XML Cancelados parseado -> Declarados={}, Generados={}, Invalidos={}",
                totalDeclarados, totalGenerados, invalidosXml);
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        currentValue.setLength(0);
        if ("PortData".equalsIgnoreCase(qName)) {
            currentNumero = new NumeroCancelado();
            totalDeclarados++;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        currentValue.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        String value = currentValue.toString().trim();

        if (currentNumero != null) {

            if ("PortID".equalsIgnoreCase(qName)) {
                currentNumero.setPortId(value);

            } else if ("FolioID".equalsIgnoreCase(qName)) {
                // En Cancelados NO existe este campo en la tabla.
                // Se ignora para no romper el CSV.
                LOGGER.debug("FolioID ignorado en Cancelados: {}", value);

            } else if ("PortType".equalsIgnoreCase(qName)) {
                currentNumero.setPortType(value);

            } else if ("Action".equalsIgnoreCase(qName)) {
                currentNumero.setAction(value);

            } else if ("NumberFrom".equalsIgnoreCase(qName)) {
                currentNumero.setNumberFrom(value);

            } else if ("NumberTo".equalsIgnoreCase(qName)) {
                currentNumero.setNumberTo(value);

            } else if ("isMPP".equalsIgnoreCase(qName)) {
                currentNumero.setIsMpp(value);

            } else if ("RIDA".equalsIgnoreCase(qName)) {
                currentNumero.setRida(toBigDecimal(value));

            } else if ("RCR".equalsIgnoreCase(qName)) {
                currentNumero.setRcr(toBigDecimal(value));

            } else if ("DIDA".equalsIgnoreCase(qName)) {
                currentNumero.setDida(toBigDecimal(value));

            } else if ("DCR".equalsIgnoreCase(qName)) {
                currentNumero.setDcr(toBigDecimal(value));

            } else if ("AssigneeIDA".equalsIgnoreCase(qName)) {
                currentNumero.setAssigneeIda(toBigDecimal(value));

            } else if ("AssigneeCR".equalsIgnoreCase(qName)) {
                currentNumero.setAssigneeCr(toBigDecimal(value));

            } else if ("ActionDate".equalsIgnoreCase(qName)) {
                try {
                    if (value != null && !value.isEmpty()) {
                        String formatted = value.replaceFirst(
                                "(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})",
                                "$1-$2-$3 $4:$5:$6"
                        );
                        Timestamp ts = Timestamp.valueOf(formatted);
                        currentNumero.setActionDate((Timestamp) ts); //fuerza a Timestamp
                    }
                } catch (Exception e) {
                    LOGGER.warn("ActionDate inválido: {}", value);
                    currentNumero.setActionDate((Timestamp) null); //fuerza a Timestamp
                }

            } else if ("PortData".equalsIgnoreCase(qName)) {
                if (isRegistroValido(currentNumero)) {
                    try {
                        if (writer != null) {
                            writer.write(toCsvRow(currentNumero));
                            writer.newLine();
                        }
                        totalGenerados++;
                    } catch (IOException e) {
                        throw new SAXException("Error escribiendo fila CSV", e);
                    }
                } else {
                    invalidosXml++;
                    LOGGER.warn("Registro Cancelado inválido descartado: {}", currentNumero);
                }
                currentNumero = null;

            } else if ("NumberRanges".equalsIgnoreCase(qName) || "NumberRange".equalsIgnoreCase(qName)) {
                // ignoramos contenedores vacíos
            } else {
                // ignorar silenciosamente otras etiquetas ya validadas
            }
        }
    }


    // Conversión segura
    private BigDecimal toBigDecimal(String value) {
        try {
            if (value != null && !value.isEmpty()) {
                return new BigDecimal(value);
            }
        } catch (NumberFormatException e) {
            LOGGER.warn("Valor no numérico para campo BigDecimal en Cancelados: '{}'", value);
        }
        return null;
    }

    private boolean isRegistroValido(NumeroCancelado nc) {
        return nc != null &&
                nc.getPortId() != null && !nc.getPortId().isEmpty() &&
                nc.getNumberFrom() != null && !nc.getNumberFrom().isEmpty();
    }

    private String toCsvRow(NumeroCancelado n) {
        StringBuilder sb = new StringBuilder();
        sb.append(safe(n.getPortId())).append("|");          // 1 PORTID
        sb.append(safe(n.getPortType())).append("|");        // 2 PORTTYPE
        sb.append(safe(n.getAction())).append("|");          // 3 ACTION
        sb.append(safe(n.getNumberFrom())).append("|");      // 4 NUMBERFROM
        sb.append(safe(n.getNumberTo())).append("|");        // 5 NUMBERTO
        sb.append(safe(n.getIsMpp())).append("|");           // 6 ISMPP
        sb.append(safeNumber(n.getRida())).append("|");      // 7 RIDA
        sb.append(safeNumber(n.getRcr())).append("|");       // 8 RCR
        sb.append(safeNumber(n.getDida())).append("|");      // 9 DIDA
        sb.append(safeNumber(n.getDcr())).append("|");       // 10 DCR
        sb.append(formatDate(n.getActionDate())).append("|");// 11 ACTIONDATE
        sb.append(safeNumber(n.getAssigneeIda())).append("|");// 12 ASSIGNEEIDA
        sb.append(safeNumber(n.getAssigneeCr()));             // 13 ASSIGNEECR

        return sb.toString();
    }

    private String safe(Object value) {
        return (value == null) ? "" : value.toString().trim();
    }

    private String safeNumber(Object value) {
        try {
            return (value == null) ? "" : new BigDecimal(value.toString()).toPlainString();
        } catch (Exception e) {
            LOGGER.warn("Valor no numérico encontrado en Cancelados: {}", value);
            return "";
        }
    }

    private String formatDate(java.util.Date date) {
        if (date == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    private String safe(String v) {
        return v != null ? v : "";
    }

    private String safeBD(BigDecimal v) {
        return v != null ? v.toString() : "";
    }

    private String safeDate(Timestamp ts) {
        if (ts != null) {
            return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ts);
        }
        return "";
    }

    private List<String> registrosInvalidosGlobal;
    public void setRegistrosInvalidosGlobal(List<String> registrosInvalidosGlobal) {
        this.registrosInvalidosGlobal = registrosInvalidosGlobal;
    }

    // === Getters para validación externa ===
    public int getTotalDeclarados() { return totalDeclarados; }
    public int getTotalGenerados() { return totalGenerados; }
    public int getInvalidosXml() { return invalidosXml; }

}


//public class NumerosDeletedXmlParser extends NumerosPortadosXmlParser {

    /** Logger de la clase. */
/*    private static final Logger LOGGER = LoggerFactory.getLogger(NumerosDeletedXmlParser.class);


 */
    /** Etiqueta XML. */
/*    protected static final String ETIQUETA_ASSIGNEE_IDA = "AssigneeIDA";


 */
    /** Etiqueta XML. */
/*    protected static final String ETIQUETA_ASSIGNEE_CR = "AssigneeCR";


 */
    /** Cabecera CSV. */
/*    private static final String HEADER_DELETED = HEADER_PORTED + ",assigneeida,assigneecr";


 */
    /** etiqueta assigneecr. */
/*    private boolean bAsigneeCR = false;


 */
    /** etiqueta assigneeida. */
/*
    private boolean bAsigneeIDA = false;
 */

    /**
     * Constructor.
     */
/*    public NumerosDeletedXmlParser() {
        header = HEADER_DELETED;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        super.startElement(uri, localName, qName, attributes);

        if (qName.equalsIgnoreCase(ETIQUETA_ASSIGNEE_CR)) {
            bAsigneeCR = true;
        } else if (qName.equalsIgnoreCase(ETIQUETA_ASSIGNEE_IDA)) {
            bAsigneeIDA = true;
        } else if (qName.equalsIgnoreCase(ETIQUETA_PORT_DATA)) {
            num = new NumeroCancelado();
            rangos = new ArrayList<Rango>();

            numLeidosTotal++;
        }
    }

    @Override
    public void endElement(String uri, String localName,
                           String qName) throws SAXException {

        if (ETIQUETA_PORT_DATA.equalsIgnoreCase(qName)) {
            if (rangos.size() > 0) {

                for (Rango r : rangos) {

                    BigDecimal from = new BigDecimal(r.getNumberFrom());
                    BigDecimal to = new BigDecimal(r.getNumberTo());

                    // LOGGER.debug("{} {}", from, to);
                    long diff = to.subtract(from).longValue();

                    for (long i = 0; i <= diff; i++) {

                        numTotal++;

                        BigDecimal n = from.add(new BigDecimal(i));

                        // LOGGER.debug("i= {} n={}", i, n);
                        num.setNumberFrom("" + n);
                        num.setNumberTo("" + n);
                        if (r.isMpp()) {
                            num.setIsMpp("Y");
                        } else {
                            num.setIsMpp("N");
                        }

                        // LOGGER.debug("num {}", num);

                        try {
                            writer.write(num.toLine());
                            writer.newLine();
                        } catch (IOException e) {
                            LOGGER.error("", e);
                        }
                    }
                }
            }

        } else if (ETIQUETA_NUMBER_RANGE.equalsIgnoreCase(qName)) {
            rangos.add(rango);
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        super.characters(ch, start, length);

        if (bAsigneeIDA) {
            ((NumeroCancelado) num).setAssigneeIda(new BigDecimal(new String(ch, start, length)));
            bAsigneeIDA = false;
        }
        if (bAsigneeCR) {
            ((NumeroCancelado) num).setAssigneeCr(new BigDecimal(new String(ch, start, length)));
            bAsigneeCR = false;
        }
    }


 */