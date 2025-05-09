package mx.ift.sns.modelo.port;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/** Clase con los campos comunes de los numeros portados y cancelados. */
@MappedSuperclass
public class Numero implements Serializable {

    /** Serial id. */
    private static final long serialVersionUID = 1L;

    /** Formato de fecha. */
    private static final String FORMAT_DATE = "yyyyMMddhhmmss";

    /** Campo PORTID. */
    @Column(name = "PORTID")
    private String portId;

    /** Campo ACTION. */
    @Column(name = "\"ACTION\"")
    private String action;

    /** Campo ACTIONDATE. */
    @Column(name = "ACTIONDATE")
    private Timestamp actionDate;

    /** Campo DCR. */
    @Column(name = "DCR")
    private BigDecimal dcr;

    /** Campo DIDA. */
    @Column(name = "DIDA")
    private BigDecimal dida;

    /** Campo ISMPP. */
    @Column(name = "ISMPP")
    private String isMpp;

    /** Campo NUMBERFROM. */
    @Id
    @Column(name = "NUMBERFROM")
    private String numberFrom;

    /** Campo NUMBERTO. */
    @Column(name = "NUMBERTO")
    private String numberTo;

    /** Campo PORTTYPE. */
    @Column(name = "PORTTYPE")
    private String portType;

    /** Campo RCR. */
    @Column(name = "RCR")
    private BigDecimal rcr;

    /** Campo RIDA. */
    @Column(name = "RIDA")
    private BigDecimal rida;

    /** Constructor. */
    public Numero() {

    }

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
     * @return the actionDate
     */
    public Timestamp getActionDate() {
        return actionDate;
    }

    /**
     * @param actionDate the actionDate to set
     */
    public void setActionDate(Timestamp actionDate) {
        this.actionDate = actionDate;
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
     * @return the numberFrom
     */
    public String getNumberFrom() {
        return numberFrom;
    }

    /**
     * @param numberFrom the numberFrom to set
     */
    public void setNumberFrom(String numberFrom) {
        this.numberFrom = numberFrom;
    }

    /**
     * @return the numberTo
     */
    public String getNumberTo() {
        return numberTo;
    }

    /**
     * @param numberTo the numberTo to set
     */
    public void setNumberTo(String numberTo) {
        this.numberTo = numberTo;
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
            Date date = formatter.parse(actionDate);
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

        StringBuilder b = new StringBuilder();

        b.append(getPortId());
        b.append(",");

        b.append(getPortType());
        b.append(",");

        b.append(getAction());
        b.append(",");

        b.append(getNumberFrom());
        b.append(",");

        b.append(getNumberTo());
        b.append(",");

        b.append(getIsMpp());
        b.append(",");

        b.append(getRida());
        b.append(",");

        b.append(getRcr());
        b.append(",");

        b.append(getDida());
        b.append(",");

        b.append(getDcr());
        b.append(",");

        DateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmss");
        String date = formatter.format(getActionDate());
        b.append(date);

        return b.toString();
    }

}
