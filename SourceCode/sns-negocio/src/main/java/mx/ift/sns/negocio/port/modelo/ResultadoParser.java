package mx.ift.sns.negocio.port.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Resultado del parseo de los ficheros de portabilidad.
 */
public class ResultadoParser implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Total de numeros. */
    private BigDecimal numerosTotal = BigDecimal.ZERO;

    /** Numeros ok. */
    private BigDecimal numerosOk = BigDecimal.ZERO;

    /** Numeros con error. */
    private BigDecimal numerosError = BigDecimal.ZERO;

    /** Timestamp del fichero. */
    private Date timestamp = null;

    /**
     * @return the numerosTotal
     */
    public BigDecimal getNumerosTotal() {
        return numerosTotal;
    }

    /**
     * @param numerosTotal the numerosTotal to set
     */
    public void setNumerosTotal(BigDecimal numerosTotal) {
        this.numerosTotal = numerosTotal;
    }

    /**
     * @return the numerosOk
     */
    public BigDecimal getNumerosOk() {
        return numerosOk;
    }

    /**
     * @param numerosOk the numerosOk to set
     */
    public void setNumerosOk(BigDecimal numerosOk) {
        this.numerosOk = numerosOk;
    }

    /**
     * @return the numerosError
     */
    public BigDecimal getNumerosError() {
        return numerosError;
    }

    /**
     * @param numerosError the numerosError to set
     */
    public void setNumerosError(BigDecimal numerosError) {
        this.numerosError = numerosError;
    }

    /**
     * @return the timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();

        b.append("numerosTotal=");
        b.append(numerosTotal);

        b.append(" numerosOk=");
        b.append(numerosOk);

        b.append(" numerosError=");
        b.append(numerosError);

        return b.toString();
    }
}
