package mx.ift.sns.negocio.port;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import mx.ift.sns.modelo.port.NumeroPortado;

import mx.ift.sns.negocio.port.modelo.ResultadoParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parser XML del fichero Numeros Ported.
 */
public class NumerosPortadosXmlParser extends DefaultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NumerosPortadosXmlParser.class);

    private BufferedWriter writer;
    private NumeroPortado currentNumero;
    private StringBuilder currentValue = new StringBuilder();

    private int totalDeclarados = 0;
    private int totalGenerados = 0;
    private int invalidosXml = 0;

    // === Constructor vacío (legacy) ===
    public NumerosPortadosXmlParser() {
        // writer se inicializará en parse(File outFile,...)
    }

    // === Constructor nuevo (refactor) ===
    public NumerosPortadosXmlParser(Writer writer) {
        this.writer = new BufferedWriter(writer);
    }

    // Setter opcional
    public void setWriter(Writer writer) {
        this.writer = new BufferedWriter(writer);
    }

    // === Método parse (soporta legacy y refactor) ===
    public void parse(String xmlPath, File outFile, ResultadoParser res) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        // Si se usó constructor vacío, inicializamos aquí el writer
        if (this.writer == null && outFile != null) {
            this.writer = new BufferedWriter(new FileWriter(outFile));
        }

        saxParser.parse(xmlPath, this);

        if (this.writer != null) {
            this.writer.flush();
            this.writer.close();
        }

        LOGGER.info("XML Portados parseado -> Declarados={}, Generados={}, Invalidos={}",
                totalDeclarados, totalGenerados, invalidosXml);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        currentValue.setLength(0);
        if ("PortData".equalsIgnoreCase(qName)) {
            currentNumero = new NumeroPortado();
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
                    LOGGER.warn("Registro Portado inválido descartado: {}", currentNumero);
                }
                currentNumero = null;

            } else {
                //String msg = "Etiqueta XML inesperada encontrada: <" + qName + "> con valor='" + value + "'";
                //LOGGER.warn(msg);
                //if (registrosInvalidosGlobal != null) registrosInvalidosGlobal.add(msg);
            }
        }
    }

    /*@Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        String value = currentValue.toString().trim();
        String tag = qName.toLowerCase();

        if (currentNumero != null) {
            switch (tag) {
                case "PortID": currentNumero.setPortId(value); break;
                case "PortType": currentNumero.setPortType(value); break;
                case "Action": currentNumero.setAction(value); break;
                case "NumberFrom": currentNumero.setNumberFrom(value); break;
                case "NumberTo": currentNumero.setNumberTo(value); break;
                case "isMPP": currentNumero.setIsMpp(value); break;

                case "RIDA": currentNumero.setRida(toBigDecimal(value)); break;
                case "RCR":  currentNumero.setRcr(toBigDecimal(value)); break;
                case "DIDA": currentNumero.setDida(toBigDecimal(value)); break;
                case "DCR":  currentNumero.setDcr(toBigDecimal(value)); break;
                case "numberranges":
                case "numberrange":
                    break;


                case "actiondate":
                    try {
                        if (value != null && !value.isEmpty()) {
                            String formatted = value.replaceFirst(
                                    "(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})",
                                    "$1-$2-$3 $4:$5:$6"
                            );
                            Timestamp ts = Timestamp.valueOf(formatted);
                            currentNumero.setActionDate((Timestamp) ts); // 🔑 fuerza a Timestamp
                        }
                    } catch (Exception e) {
                        LOGGER.warn("ActionDate inválido: {}", value);
                        currentNumero.setActionDate((Timestamp) null); // 🔑 fuerza a Timestamp
                    }
                    break;

                case "PortData":
                    if (isRegistroValido(currentNumero)) {
                        try {
                            if (writer != null) {
                                writer.write(toCsvRow((NumeroPortado) currentNumero));
                                writer.newLine();
                            }
                            totalGenerados++;
                        } catch (IOException e) {
                            throw new SAXException("Error escribiendo fila CSV", e);
                        }
                    } else {
                        invalidosXml++;
                        LOGGER.warn("Registro Portado inválido descartado: {}", currentNumero);
                    }
                    currentNumero = null;
                    break;
                default:
                    String msg = "Etiqueta XML inesperada encontrada: <" + qName + "> con valor='" + value + "'";
                    LOGGER.warn(msg);
                    if (registrosInvalidosGlobal != null) registrosInvalidosGlobal.add(msg);
                    break;
            }
        }
    }

     */

    // Conversión segura
    private BigDecimal toBigDecimal(String value) {
        try {
            if (value != null && !value.isEmpty()) {
                return new BigDecimal(value);
            }
        } catch (NumberFormatException e) {
            LOGGER.warn("Valor no numérico para campo BigDecimal en Portados: '{}'", value);
        }
        return null;
    }

    private boolean isRegistroValido(NumeroPortado np) {
        return np != null &&
                np.getPortId() != null && !np.getPortId().isEmpty() &&
                np.getNumberFrom() != null && !np.getNumberFrom().isEmpty();
    }

    private String toCsvRow(NumeroPortado np) {
        StringBuilder sb = new StringBuilder();
        sb.append(safe(np.getPortId())).append("|")
                .append(safe(np.getPortType())).append("|")
                .append(safe(np.getAction())).append("|")
                .append(safe(np.getNumberFrom())).append("|")
                .append(safe(np.getNumberTo())).append("|")
                .append(safe(np.getIsMpp())).append("|")
                .append(safeBD(np.getRida())).append("|")
                .append(safeBD(np.getRcr())).append("|")
                .append(safeBD(np.getDida())).append("|")
                .append(safeBD(np.getDcr())).append("|")
                .append(safeDate(np.getActionDate()));
        //return sb.toString();
        String linea = sb.toString();

        //Validación rápida: contar cuántos separadores trae
        int pipes = linea.length() - linea.replace("|", "").length();
        if (pipes < 10) {
            LOGGER.warn("Línea CSV generada con {} columnas (esperado 11): {}",
            pipes+1, linea);
        } else if (totalGenerados < 5) {
            // Solo para debug: loguear las primeras 5 filas válidas
            //LOGGER.info("Fila CSV generada [{}]: {}", totalGenerados+1, linea);
        }

        return linea;
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




    /** Logger de la clase. */
    /*
private static final Logger LOGGER = LoggerFactory.getLogger(NumerosPortadosXmlParser.class);


     */
    /** Etiqueta XML. */
/*    protected static final String ETIQUETA_PORT_DATA = "PortData";

 */

    /** Etiqueta XML. */
/*    protected static final String ETIQUETA_PORTID = "PortID";

 */

    /** Etiqueta XML. */
/*    protected static final String ETIQUETA_PORTTYPE = "PortType";

 */

    /** Etiqueta XML. */
/*    protected static final String ETIQUETA_ACTION = "Action";

 */

    /** Etiqueta XML. */
/*    protected static final String ETIQUETA_NUMBER_RANGE = "NumberRange";

 */

    /** Etiqueta XML. */
/*    protected static final String ETIQUETA_NUMBER_FROM = "NumberFrom";

 */

    /** Etiqueta XML. */
/*    protected static final String ETIQUETA_NUMBER_TO = "NumberTo";

 */

    /** Etiqueta XML. */
/*    protected static final String ETIQUETA_IS_MPP = "isMPP";

 */

    /** Etiqueta XML. */
/*    protected static final String ETIQUETA_RIDA = "RIDA";

 */

    /** Etiqueta XML. */
/*    protected static final String ETIQUETA_DIDA = "DIDA";

 */

    /** Etiqueta XML. */
/*    protected static final String ETIQUETA_DCR = "DCR";

 */

    /** Etiqueta XML. */
/*    protected static final String ETIQUETA_RCR = "RCR";

 */

    /** Etiqueta XML. */
/*    protected static final String ETIQUETA_ACTION_DATE = "ActionDate";

 */


    /** Etiqueta XML. */
/*    protected static final String ETIQUETA_TIMESTAMP = "Timestamp";

 */

    /** Etiqueta XML. */
/*    protected static final String ETIQUETA_NUMBER_OF_MESSAGES = "NumberOfMessages";


 */
    /** Cabecera CSV Ported. */
/*    protected static final String HEADER_PORTED = "portId,portType,action,numberFrom,numberTo,isMpp"
              + ",rida,rcr,dida,dcr,actionDate";

 */

    /** Formato Fecha. */
/*    protected static final String FORMAT_DATE = "yyyyMMddhhmmss";


 */
    /** bPortId. */
/*    protected boolean bPortId = false;

    protected boolean bPortType = false;

    protected boolean bAction = false;

    protected boolean bNumberRange = false;

    protected boolean bNumberFrom = false;

    protected boolean bNumberTo = false;

    protected boolean bIsMPP = false;

    protected boolean bRida = false;

    protected boolean bDida = false;

    protected boolean bDcr = false;

    protected boolean bActionDate = false;

    protected boolean bRcr = false;

    protected boolean bTimeStamp = false;

    protected boolean bNumberOfMessages = false;


 */
    /** numero tradado. */
/*    protected Numero num = null;


 */
    /** lista de rangos del numero. */
/*    protected List<Rango> rangos = null;


 */
    /** Rango del numero. */
/*    protected Rango rango = null;


 */
    /** Numeros leidos. */
/*    protected long numLeidosTotal = 0;


 */
    /** Numero total. */
/*    protected long numTotal = 0;
    protected Date timeStamp;

    protected long numberOfMessages = 0;

    protected BufferedWriter writer = null;

    protected String header;

    /**
     * Constructor.
     */
/*    public NumerosPortadosXmlParser() {
        header = HEADER_PORTED;
    }

    /**
     * @param filename Nombre del fichero de entrada.
     * @param filenameOut Nombre del fichero de salida.
     * @param res Resultado del Parseo.
     */
 /*   public void parse(String filename, File filenameOut, ResultadoParser res) {

        try {

            // Obtain an instance of SAXParserFactory.
            SAXParserFactory spf = SAXParserFactory.newInstance();

            // Specify a validating parser.
            spf.setValidating(true); // Requires loading the DTD.

            // Obtain an instance of a SAX parser from the factory.
            SAXParser sp;

            sp = spf.newSAXParser();

            writer = new BufferedWriter(new FileWriter(filenameOut));

            writer.write(header);
            writer.newLine();

            // Parse the documnt.
            sp.parse(filename, this);

            res.setNumerosTotal(new BigDecimal(numTotal));
            res.setNumerosOk(new BigDecimal(numTotal));
            res.setTimestamp(timeStamp);

            LOGGER.debug(" numberofmessages {} leidos {} total {}", numberOfMessages, numLeidosTotal, numTotal);

        } catch (ParserConfigurationException | SAXException e) {
            LOGGER.error("exc", e);
        } catch (IOException e) {
            LOGGER.error("exc", e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                ;
            }
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        if (qName.equalsIgnoreCase(ETIQUETA_TIMESTAMP)) {
            bTimeStamp = true;
        } else if (qName.equalsIgnoreCase(ETIQUETA_NUMBER_OF_MESSAGES)) {
            bNumberOfMessages = true;
        } else if (qName.equalsIgnoreCase(ETIQUETA_PORT_DATA)) {

            num = new NumeroPortado();
            rangos = new ArrayList<Rango>();

            numLeidosTotal++;
        } else if (qName.equalsIgnoreCase(ETIQUETA_PORTID)) {
            bPortId = true;
        } else if (qName.equalsIgnoreCase(ETIQUETA_ACTION)) {
            bAction = true;
        } else if (qName.equalsIgnoreCase(ETIQUETA_PORTTYPE)) {
            bPortType = true;
        } else if (qName.equalsIgnoreCase(ETIQUETA_NUMBER_RANGE)) {
            rango = new Rango();
            bNumberRange = true;
        } else if (qName.equalsIgnoreCase(ETIQUETA_NUMBER_FROM)) {
            bNumberFrom = true;
        } else if (qName.equalsIgnoreCase(ETIQUETA_NUMBER_TO)) {
            bNumberTo = true;
        } else if (qName.equalsIgnoreCase(ETIQUETA_IS_MPP)) {
            bIsMPP = true;
        } else if (qName.equalsIgnoreCase(ETIQUETA_RIDA)) {
            bRida = true;
        } else if (qName.equalsIgnoreCase(ETIQUETA_DIDA)) {
            bDida = true;
        } else if (qName.equalsIgnoreCase(ETIQUETA_DCR)) {
            bDcr = true;
        } else if (qName.equalsIgnoreCase(ETIQUETA_RCR)) {
            bRcr = true;
        } else if (qName.equalsIgnoreCase(ETIQUETA_ACTION_DATE)) {
            bActionDate = true;
        }
    }

    @Override
    public void endElement(String uri, String localName,
                           String qName) throws SAXException {

        if (ETIQUETA_PORT_DATA.equalsIgnoreCase(qName)) {
            if (rangos.size() > 0) {

                for (Rango r : rangos) {

                    Long from = new Long(r.getNumberFrom());
                    Long to = new Long(r.getNumberTo());

                    for (long i = from; i <= to; i++) {

                        numTotal++;

                        String n = Long.toString(i);

                        num.setNumberFrom(n);
                        num.setNumberTo(n);
                        if (r.isMpp()) {
                            num.setIsMpp("Y");
                        } else {
                            num.setIsMpp("N");
                        }

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

        if (bTimeStamp) {
            try {
                DateFormat formatter = new SimpleDateFormat(FORMAT_DATE);
                timeStamp = formatter.parse(new String(ch, start, length));
            } catch (ParseException e) {
                LOGGER.error("", e);
            }

            bTimeStamp = false;
        }

        if (bNumberOfMessages) {
            numberOfMessages = Integer.parseInt(new String(ch, start, length));
            bNumberOfMessages = false;
        }
        if (bPortId) {
            num.setPortId(StringUtils.trim(new String(ch, start, length)));
            bPortId = false;
        }

        if (bPortType) {
            num.setPortType(StringUtils.trim(new String(ch, start, length)));
            bPortType = false;
        }

        if (bNumberFrom) {
            rango.setNumberFrom(new String(ch, start, length));
            bNumberFrom = false;
        }

        if (bNumberTo) {
            rango.setNumberTo(StringUtils.trim(new String(ch, start, length)));
            bNumberTo = false;
        }

        if (bAction) {
            num.setAction(new String(ch, start, length));
            bAction = false;
        }

        if (bIsMPP) {
            String mpp = new String(ch, start, length);
            if (StringUtils.isNotEmpty(mpp) && mpp.equalsIgnoreCase("Y")) {
                rango.setMpp(true);
            } else {
                rango.setMpp(false);
            }
            bAction = false;
        }

        if (bNumberRange) {
            bAction = false;
        }

        if (bRida) {
            String cadena = new String(ch, start, length);
            num.setRida(new BigDecimal(cadena));
            bRida = false;
        }

        if (bDida) {
            String cadena = new String(ch, start, length);
            cadena = cadena.replaceAll("\n", "").trim();
            num.setDida(!StringUtils.isEmpty(cadena) ? new BigDecimal(cadena) : null);
            bDida = false;
        }
        if (bDcr) {
            String cadena = new String(ch, start, length);
            cadena = cadena.replaceAll("\n", "").trim();
            num.setDcr(!StringUtils.isEmpty(cadena) ? new BigDecimal(cadena) : null);
            bDcr = false;
        }
        if (bRcr) {
            String cadena = new String(ch, start, length);
            num.setRcr(new BigDecimal(cadena));
            bRcr = false;
        }

        if (bActionDate) {
            try {
                DateFormat formatter = new SimpleDateFormat(FORMAT_DATE);
                Date date;

                date = formatter.parse(new String(ch, start, length));

                Timestamp timeStamp = new Timestamp(date.getTime());
                num.setActionDate(timeStamp);
            } catch (ParseException e) {

                LOGGER.error("", e);
            }

            bActionDate = false;
        }
    }
 */
