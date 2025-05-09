package mx.ift.sns.negocio.port;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import mx.ift.sns.modelo.port.NumeroCancelado;
import mx.ift.sns.negocio.port.modelo.Rango;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Parser XML del fichero Numeros Deleted.
 */
public class NumerosDeletedXmlParser extends NumerosPortadosXmlParser {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NumerosDeletedXmlParser.class);

    /** Etiqueta XML. */
    protected static final String ETIQUETA_ASSIGNEE_IDA = "AssigneeIDA";

    /** Etiqueta XML. */
    protected static final String ETIQUETA_ASSIGNEE_CR = "AssigneeCR";

    /** Cabecera CSV. */
    private static final String HEADER_DELETED = HEADER_PORTED + ",assigneeida,assigneecr";

    /** etiqueta assigneecr. */
    private boolean bAsigneeCR = false;

    /** etiqueta assigneeida. */
    private boolean bAsigneeIDA = false;

    /**
     * Constructor.
     */
    public NumerosDeletedXmlParser() {
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
}
