package mx.ift.sns.port.full;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Tabla de Numeros Portados.
 */
public class PortData implements Serializable {

    /** Serial id. */
    private static final long serialVersionUID = 1L;

    /** Formato de fecha. */
    private static final String FORMAT_DATE = "yyyyMMddhhmmss";

    /** Campo PORTID. */

    private String portId;

    /** Campo ACTION. */

    private String action;

    /** Campo ACTIONDATE. */

    private Timestamp actionDate;

    /** Campo DCR. */

    private BigDecimal dcr;

    /** Campo DIDA. */

    private BigDecimal dida;

    /** Campo ISMPP. */

    private String isMpp;

    /** Campo NUMBER. */
    private String number;

    /** Campo PORTTYPE. */

    private String portType;

    /** Campo RCR. */

    private BigDecimal rcr;

    /** Campo RIDA. */

    private BigDecimal rida;

    /**
     * @return the portId
     */
    public String getPortId() {
        return portId;
    }

    /**
     * @param portId the portId to set
     */
    public void setPortId(String portId) {
        this.portId = portId;
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @return action date
     */
    public Timestamp getActionDate() {
        return this.actionDate;
    }

    /**
     * @param actiondate actiondate
     */
    public void setActionDate(Timestamp actiondate) {
        this.actionDate = actiondate;
    }

    /**
     * @return the dcr
     */
    public BigDecimal getDcr() {
        return dcr;
    }

    /**
     * @param dcr the dcr to set
     */
    public void setDcr(BigDecimal dcr) {
        this.dcr = dcr;
    }

    /**
     * @return the dida
     */
    public BigDecimal getDida() {
        return dida;
    }

    /**
     * @param dida the dida to set
     */
    public void setDida(BigDecimal dida) {
        this.dida = dida;
    }

    /**
     * @return the isMpp
     */
    public String getIsMpp() {
        return isMpp;
    }

    /**
     * @param isMpp the isMpp to set
     */
    public void setIsMpp(String isMpp) {
        this.isMpp = isMpp;
    }

    /**
     * @return the number
     */
    public String getNumber() {
        return number;
    }

    /**
     * @param number the number to set
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * @return the portType
     */
    public String getPortType() {
        return portType;
    }

    /**
     * @param portType the portType to set
     */
    public void setPortType(String portType) {
        this.portType = portType;
    }

    /**
     * @return the rcr
     */
    public BigDecimal getRcr() {
        return rcr;
    }

    /**
     * @param rcr the rcr to set
     */
    public void setRcr(BigDecimal rcr) {
        this.rcr = rcr;
    }

    /**
     * @return the rida
     */
    public BigDecimal getRida() {
        return rida;
    }

    /**
     * @param rida the rida to set
     */
    public void setRida(BigDecimal rida) {
        this.rida = rida;
    }

    /**
     * @param actionDate actiondate
     */
    public void setActionDate(String actionDate) {
        try {
            DateFormat formatter = new SimpleDateFormat(FORMAT_DATE);
            Date date = (Date) formatter.parse(actionDate);
            Timestamp timeStamp = new Timestamp(date.getTime());
            setActionDate(timeStamp);
        } catch (ParseException e1) {
            actionDate = null;
        }
    }

    /**
     * @return string
     */
    public String toLine() {
        /*
         * INSERT INTO PORT_NUM_PORTADO (PORTID,PORTTYPE,ACTION,NUMBERFROM,NUMBERTO,ISMPP,RIDA,RCR,DIDA,DCR,ACTIONDATE)
         * VALUES ('118201510131623058778','6','Port','8119790925','8119790925','N',118,118,188,188,{ts '2015-10-14
         * 02:00:00.0'});
         */
        StringBuilder b = new StringBuilder();

        b.append("INSERT INTO PORT_NUM_PORTADO");
        b.append("(PORTID,PORTTYPE,ACTION,NUMBERFROM,NUMBERTO,ISMPP,RIDA,RCR,DIDA,DCR,ACTIONDATE) VALUES (");

        b.append("'");
        b.append(portId);
        b.append("',");

        b.append("'");
        b.append(portType);
        b.append("',");

        b.append("'");
        b.append(action);
        b.append("',");

        b.append("'");
        b.append(number);
        b.append("',");

        b.append("'");
        b.append(number);
        b.append("',");

        if ((isMpp == null) || (isMpp.equals("Y"))) {
            b.append("'Y'");
        } else {
            b.append("'N'");
        }
        b.append(",");

        b.append(rida);
        b.append(",");

        b.append(rcr);
        b.append(",");

        b.append(dida);
        b.append(",");

        b.append(dcr);
        b.append(",");

        DateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmss");
        String date = formatter.format(actionDate);
        b.append("to_timestamp('");
        b.append(date);
        b.append("','YYYYMMDDHH24MISS'");
        b.append("))");

        return b.toString();
    }

    @Override
    public String toString() {

        StringBuilder b = new StringBuilder();

        b.append("portid ");
        b.append(portId);

        b.append(" porttype ");
        b.append(portType);

        b.append(" action ");
        b.append(action);

        b.append(" actiondate ");
        if (actionDate != null) {
            DateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmss");
            String date = formatter.format(actionDate);
            b.append(date);
        }

        b.append(" dcr ");
        b.append(dcr);

        b.append(" dida ");
        b.append(dida);

        b.append(" ismpp ");
        b.append(isMpp);

        b.append(" number ");
        b.append(number);

        b.append(" porttype ");
        b.append(portType);

        b.append(" rcr ");
        b.append(rcr);

        b.append(" rida ");
        b.append(rida);

        return b.toString();
    }
}
