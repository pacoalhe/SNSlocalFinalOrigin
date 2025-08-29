package mx.ift.sns.modelo.port;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.*;

/**
 * Tabla de Numeros Cancelados.
 */
@Entity
@Table(name = "PORT_NUM_CANCELADO")
@NamedQuery(name = "findNumeroCancelado", query = "SELECT n FROM NumeroCancelado n where n.numberFrom = :num")
public class NumeroCancelado extends Numero implements Serializable {

    /** Serial id. */
    private static final long serialVersionUID = 1L;

    /** ASSIGNEECR. */
    @Column(name = "ASSIGNEECR")
    private BigDecimal assigneeCr;

    /** ASSIGNEEIDA. */
    @Column(name = "ASSIGNEEIDA")
    private BigDecimal assigneeIda;

    /** Constructor. */
    public NumeroCancelado() {
        super();

    }

    /**
     * @return the assigneeCr
     */
    public BigDecimal getAssigneeCr() {
        return assigneeCr;
    }

    /**
     * @param assigneeCr the assigneeCr to set
     */
    public void setAssigneeCr(BigDecimal assigneeCr) {
        this.assigneeCr = assigneeCr;
    }

    /**
     * @return the assigneeIda
     */
    public BigDecimal getAssigneeIda() {
        return assigneeIda;
    }

    /**
     * @param assigneeIda the assigneeIda to set
     */
    public void setAssigneeIda(BigDecimal assigneeIda) {
        this.assigneeIda = assigneeIda;
    }

    /**
     * convierte a string.
     * @return string
     */
    @Override
    public String toLine() {

        String s = super.toLine();

        StringBuilder b = new StringBuilder();

        b.append(s);
        b.append(",");

        b.append(assigneeIda);
        b.append(",");

        b.append(assigneeCr);

        return b.toString();
    }

    @Override
    public String toString() {

        String s = super.toString();

        StringBuilder b = new StringBuilder();

        b.append(s);

        b.append(" asigneeida ");
        b.append(assigneeIda);

        b.append(" asigneecr ");
        b.append(assigneeCr);

        return b.toString();
    }

    /** FOLIOID (solo en XML, no en BD). */
    @Transient
    private String folioId;

    public String getFolioId() {
        return folioId;
    }

    public void setFolioId(String folioId) {
        this.folioId = folioId;
    }

}
