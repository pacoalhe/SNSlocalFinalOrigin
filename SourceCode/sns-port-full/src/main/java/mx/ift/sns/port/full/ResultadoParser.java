package mx.ift.sns.port.full;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Resultado del parseo de los ficheros de full sync.
 */
public class ResultadoParser implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Total de numeros. */
    private BigDecimal numeros = BigDecimal.ZERO;

    /** Mensajes del xml. */
    private BigDecimal mensajes = BigDecimal.ZERO;

    /** Timestamp del fichero. */
    private Date timestamp = null;

    /**
     * @return the numeros
     */
    public BigDecimal getNumeros() {
        return numeros;
    }

    /**
     * @param numeros the numeros to set
     */
    public void setNumeros(BigDecimal numeros) {
        this.numeros = numeros;
    }

    /**
     * @return the mensajes
     */
    public BigDecimal getMensajes() {
        return mensajes;
    }

    /**
     * @param mensajes the mensajes to set
     */
    public void setMensajes(BigDecimal mensajes) {
        this.mensajes = mensajes;
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

}
