package mx.ift.sns.modelo.nng;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Representa el indentificador de una Serie de Numeración No Geográfica.
 */
@Embeddable
public class SerieNngPK implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador de Clave de Servicio. */
    @Column(name = "ID_CLAVE_SERVICIO", insertable = false, updatable = false, unique = true, nullable = false)
    private BigDecimal idClaveServicio;

    /** Identificador de Serie. */
    @Column(name = "SNA", unique = true, nullable = false, precision = 3)
    private BigDecimal sna;

    /** Constructor. Por defecto vacío. */
    public SerieNngPK() {
    }

    // GETTERS & SETTERS

    /**
     * Identificador de Clave de Servicio.
     * @return BigDecimal
     */
    public BigDecimal getIdClaveServicio() {
        return idClaveServicio;
    }

    /**
     * Identificador de Clave de Servicio.
     * @param idClaveServicio BigDecimal
     */
    public void setIdClaveServicio(BigDecimal idClaveServicio) {
        this.idClaveServicio = idClaveServicio;
    }

    /**
     * Identificador de Serie.
     * @return BigDecimal
     */
    public BigDecimal getSna() {
        return sna;
    }

    /**
     * Identificador de Serie.
     * @param sna BigDecimal
     */
    public void setSna(BigDecimal sna) {
        this.sna = sna;
    }

    /**
     * Método que añade al SNA ceros.
     * @return String
     * @throws Exception Exception
     */
    public String getSnaAsString() throws Exception {

        return String.format("%03d", this.getSna().intValue());

    }

    @Override
    public boolean equals(Object other) {

        if (!(other instanceof SerieNngPK)) {
            return false;
        }
        SerieNngPK castOther = (SerieNngPK) other;
        return (this.sna.compareTo(castOther.getSna()) == 0)
                && (this.idClaveServicio.compareTo(castOther.getIdClaveServicio()) == 0);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + (this.idClaveServicio.intValue() ^ (this.idClaveServicio.intValue() >>> 32));
        hash = hash * prime + (this.sna.intValue() ^ (this.sna.intValue() >>> 32));

        return hash;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        return sb.append(this.idClaveServicio.toString()).append("-").append(this.sna.toString()).toString();
    }
}
