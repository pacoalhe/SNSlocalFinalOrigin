package mx.ift.sns.negocio.port;

import java.math.BigDecimal;

public class NumeroCanceladoRequestDTO {

    private String portId;
    private String action;
    private String actionDate;
    private BigDecimal dcr;
    private BigDecimal dida;
    private Character isMpp;
    private String numberFrom;
    private String numberTo;
    private Character portType;
    private BigDecimal rcr;
    private BigDecimal rida;
    private BigDecimal assigneeCr;
    private BigDecimal assigneeIda;

    public String getPortId() {
        return portId;
    }

    public void setPortId(String portId) {
        this.portId = portId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionDate() {
        return actionDate;
    }

    public void setActionDate(String actionDate) {
        this.actionDate = actionDate;
    }

    public BigDecimal getDcr() {
        return dcr;
    }

    public void setDcr(BigDecimal dcr) {
        this.dcr = dcr;
    }

    public BigDecimal getDida() {
        return dida;
    }

    public void setDida(BigDecimal dida) {
        this.dida = dida;
    }

    public Character getIsMpp() {
        return isMpp;
    }

    public void setIsMpp(Character isMpp) {
        this.isMpp = isMpp;
    }

    public String getNumberFrom() {
        return numberFrom;
    }

    public void setNumberFrom(String numberFrom) {
        this.numberFrom = numberFrom;
    }

    public String getNumberTo() {
        return numberTo;
    }

    public void setNumberTo(String numberTo) {
        this.numberTo = numberTo;
    }

    public Character getPortType() {
        return portType;
    }

    public void setPortType(Character portType) {
        this.portType = portType;
    }

    public BigDecimal getRcr() {
        return rcr;
    }

    public void setRcr(BigDecimal rcr) {
        this.rcr = rcr;
    }

    public BigDecimal getRida() {
        return rida;
    }

    public void setRida(BigDecimal rida) {
        this.rida = rida;
    }

    public BigDecimal getAssigneeCr() {
        return assigneeCr;
    }

    public void setAssigneeCr(BigDecimal assigneeCr) {
        this.assigneeCr = assigneeCr;
    }

    public BigDecimal getAssigneeIda() {
        return assigneeIda;
    }

    public void setAssigneeIda(BigDecimal assigneeIda) {
        this.assigneeIda = assigneeIda;
    }

}