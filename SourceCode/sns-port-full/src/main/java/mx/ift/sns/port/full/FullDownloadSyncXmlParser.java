package mx.ift.sns.port.full;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parser XML del fichero Numeros Ported.
 */
public class FullDownloadSyncXmlParser extends DefaultHandler {

    /** Logger de la clase. */
    private static final ConsoleLogger LOGGER = ConsoleLogger.getLogger(FullDownloadSyncXmlParser.class);

    /** Etiqueta XML. */
    private static final String ETIQUETA_PORT_DATA = "PortData";

    /** Etiqueta XML. */
    private static final String ETIQUETA_PORTID = "PortID";

    /** Etiqueta XML. */
    private static final String ETIQUETA_PORTTYPE = "PortType";

    /** Etiqueta XML. */
    private static final String ETIQUETA_ACTION = "Action";

    /** Etiqueta XML. */
    private static final String ETIQUETA_NUMBER = "Number";

    /** Etiqueta XML. */
    private static final String ETIQUETA_IS_MPP = "isMPP";

    /** Etiqueta XML. */
    private static final String ETIQUETA_RIDA = "RIDA";

    /** Etiqueta XML. */
    private static final String ETIQUETA_DIDA = "DIDA";

    /** Etiqueta XML. */
    private static final String ETIQUETA_DCR = "DCR";

    /** Etiqueta XML. */
    private static final String ETIQUETA_RCR = "RCR";

    /** Etiqueta XML. */
    private static final String ETIQUETA_ACTION_DATE = "ActionDate";

    /** Etiqueta XML. */
    private static final String ETIQUETA_TIMESTAMP = "Timestamp";

    /** Etiqueta XML. */
    private static final String ETIQUETA_NUMBER_OF_MESSAGES = "NumberOfMessages";

    /** Formato fecha. */
    private static final String FORMAT_DATE = "yyyyMMddhhmmss";

    /** Encontrado portid. */
    private boolean bPortId = false;

    /** Encontrado bPortType. */
    private boolean bPortType = false;

    /** Encontrado bAction. */
    private boolean bAction = false;

    /** Encontrado portid. */
    private boolean bNumber = false;

    /** Encontrado bIsMPP. */
    private boolean bIsMPP = false;

    /** Encontrado bRida. */
    private boolean bRida = false;

    /** Encontrado bDida. */
    private boolean bDida = false;

    /** Encontrado bDcr. */
    private boolean bDcr = false;

    /** Encontrado bActionDate. */
    private boolean bActionDate = false;

    /** Encontrado bRcr. */
    private boolean bRcr = false;

    /** Encontrado bTimeStamp. */
    private boolean bTimeStamp = false;

    /** Encontrado bNumberOfMessages. */
    private boolean bNumberOfMessages = false;

    /** numero tradado. */
    private PortData num = null;

    /** numero tradado. */
    private PortData portData = null;

    /** lista de rangos del numero. */
    private List<PortData> numeros = null;

    /** Numero total. */
    private long numTotal = 0;

    /** Timestamp del fichero. */
    private Date timeStamp;

    /** numero de mensajes . */
    private long numberOfMessages = 0;

    /** escritor. */
    private BufferedWriter writer = null;

    /** numero de tags number. */
    private int tagsNumbers = 0;

    /**
     * Constructor.
     */
    public FullDownloadSyncXmlParser() {

    }

    /**
     * Parsea el xml.
     * @param filename fihcero entrada
     * @param filenameOut fichero salida
     * @param res resultado
     */
    public void parse(String filename, File filenameOut, ResultadoParser res) {

        try {

            // Obtain an instance of SAXParserFactory.
            SAXParserFactory spf = SAXParserFactory.newInstance();

            // Specify a validating parser.
            spf.setValidating(true); // Requires loading the DTD.

            // Obtain an instance of a SAX parser from the factory.
            SAXParser sp;

            sp = spf.newSAXParser();

            writer = new BufferedWriter(new FileWriter(filenameOut));

            // Parse the documnt.
            sp.parse(filename, this);

            res.setMensajes(new BigDecimal(numberOfMessages));
            res.setNumeros(new BigDecimal(numTotal));
            res.setTimestamp(timeStamp);
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

            num = new PortData();
            numeros = new ArrayList<PortData>();
            tagsNumbers = 0;

            portData = new PortData();

        } else if (qName.equalsIgnoreCase(ETIQUETA_PORTID)) {
            bPortId = true;
        } else if (qName.equalsIgnoreCase(ETIQUETA_ACTION)) {
            bAction = true;
        } else if (qName.equalsIgnoreCase(ETIQUETA_PORTTYPE)) {
            bPortType = true;
        } else if (qName.equalsIgnoreCase(ETIQUETA_NUMBER) && (tagsNumbers == 0)) {
            tagsNumbers++;
        } else if (qName.equalsIgnoreCase(ETIQUETA_NUMBER) && (tagsNumbers == 1)) {
            tagsNumbers++;
            num = new PortData();
            bNumber = true;
            numTotal++;
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

        if (ETIQUETA_NUMBER.equalsIgnoreCase(qName)) {
            tagsNumbers--;
        }

        if (ETIQUETA_PORT_DATA.equalsIgnoreCase(qName)) {

            if (numeros.size() > 0) {

                for (PortData r : numeros) {

                    portData.setNumber(r.getNumber());

                    try {
                        writer.write(portData.toLine());
                        writer.newLine();
                    } catch (IOException e) {
                        LOGGER.error("", e);
                    }

                }
            }
        } else if ((ETIQUETA_NUMBER.equalsIgnoreCase(qName)) && (tagsNumbers == 1)) {
            numeros.add(num);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        if (bTimeStamp) {
            try {
                DateFormat formatter = new SimpleDateFormat(FORMAT_DATE);
                timeStamp = (Date) formatter.parse(new String(ch, start, length));
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
            portData.setPortId(trim(new String(ch, start, length)));
            bPortId = false;
        }

        if (bPortType) {
            portData.setPortType(trim(new String(ch, start, length)));
            bPortType = false;
        }

        if (bNumber) {
            num.setNumber(new String(ch, start, length));
            bNumber = false;
        }

        if (bAction) {
            portData.setAction(new String(ch, start, length));
            bAction = false;
        }

        if (bIsMPP) {
            String mpp = new String(ch, start, length);
            if (isNotEmpty(mpp) && mpp.equalsIgnoreCase("Y")) {
                num.setIsMpp("Y");
            } else {
                num.setIsMpp("N");
            }
            bAction = false;
        }

        if (bNumber) {
            bAction = false;
        }

        if (bRida) {
            String cadena = new String(ch, start, length);
            portData.setRida(new BigDecimal(cadena));
            bRida = false;
        }

        if (bDida) {
            String cadena = new String(ch, start, length);
            portData.setDida(new BigDecimal(cadena));
            bDida = false;
        }
        if (bDcr) {
            String cadena = new String(ch, start, length);
            portData.setDcr(new BigDecimal(cadena));
            bDcr = false;
        }
        if (bRcr) {
            String cadena = new String(ch, start, length);
            portData.setRcr(new BigDecimal(cadena));
            bRcr = false;
        }

        if (bActionDate) {
            try {
                DateFormat formatter = new SimpleDateFormat(FORMAT_DATE);
                Date date;

                date = (Date) formatter.parse(new String(ch, start, length));

                Timestamp timeStamp = new Timestamp(date.getTime());
                portData.setActionDate(timeStamp);
            } catch (ParseException e) {

                LOGGER.error("", e);
            }

            bActionDate = false;
        }
    }

    /**
     * Borra espacios.
     * @param s cadena
     * @return nueva cadena
     */
    private String trim(final String s) {
        if (s == null) {
            return "";
        }

        return s.trim();
    }

    /**
     * Indica si la cadena no es vacia.
     * @param s cadena
     * @return true si no lo es
     */
    private boolean isNotEmpty(final String s) {
        boolean res = false;

        if ((s == null) || "".equals(s)) {
            res = true;
        }

        return res;
    }
}
