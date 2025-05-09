package mx.ift.sns.negocio.port;

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

import mx.ift.sns.modelo.port.Numero;
import mx.ift.sns.modelo.port.NumeroPortado;
import mx.ift.sns.negocio.port.modelo.Rango;
import mx.ift.sns.negocio.port.modelo.ResultadoParser;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parser XML del fichero Numeros Ported.
 */
public class NumerosPortadosXmlParser extends DefaultHandler {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NumerosPortadosXmlParser.class);

    /** Etiqueta XML. */
    protected static final String ETIQUETA_PORT_DATA = "PortData";

    /** Etiqueta XML. */
    protected static final String ETIQUETA_PORTID = "PortID";

    /** Etiqueta XML. */
    protected static final String ETIQUETA_PORTTYPE = "PortType";

    /** Etiqueta XML. */
    protected static final String ETIQUETA_ACTION = "Action";

    /** Etiqueta XML. */
    protected static final String ETIQUETA_NUMBER_RANGE = "NumberRange";

    /** Etiqueta XML. */
    protected static final String ETIQUETA_NUMBER_FROM = "NumberFrom";

    /** Etiqueta XML. */
    protected static final String ETIQUETA_NUMBER_TO = "NumberTo";

    /** Etiqueta XML. */
    protected static final String ETIQUETA_IS_MPP = "isMPP";

    /** Etiqueta XML. */
    protected static final String ETIQUETA_RIDA = "RIDA";

    /** Etiqueta XML. */
    protected static final String ETIQUETA_DIDA = "DIDA";

    /** Etiqueta XML. */
    protected static final String ETIQUETA_DCR = "DCR";

    /** Etiqueta XML. */
    protected static final String ETIQUETA_RCR = "RCR";

    /** Etiqueta XML. */
    protected static final String ETIQUETA_ACTION_DATE = "ActionDate";

    /** Etiqueta XML. */
    protected static final String ETIQUETA_TIMESTAMP = "Timestamp";

    /** Etiqueta XML. */
    protected static final String ETIQUETA_NUMBER_OF_MESSAGES = "NumberOfMessages";

    /** Cabecera CSV Ported. */
    protected static final String HEADER_PORTED = "portId,portType,action,numberFrom,numberTo,isMpp"
            + ",rida,rcr,dida,dcr,actionDate";

    /** Formato Fecha. */
    protected static final String FORMAT_DATE = "yyyyMMddhhmmss";

    /** bPortId. */
    protected boolean bPortId = false;

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

    /** numero tradado. */
    protected Numero num = null;

    /** lista de rangos del numero. */
    protected List<Rango> rangos = null;

    /** Rango del numero. */
    protected Rango rango = null;

    /** Numeros leidos. */
    protected long numLeidosTotal = 0;

    /** Numero total. */
    protected long numTotal = 0;

    protected Date timeStamp;

    protected long numberOfMessages = 0;

    protected BufferedWriter writer = null;

    protected String header;

    /**
     * Constructor.
     */
    public NumerosPortadosXmlParser() {
        header = HEADER_PORTED;
    }

    /**
     * @param filename Nombre del fichero de entrada.
     * @param filenameOut Nombre del fichero de salida.
     * @param res Resultado del Parseo.
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
}
