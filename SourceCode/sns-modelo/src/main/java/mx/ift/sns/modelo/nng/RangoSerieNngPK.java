package mx.ift.sns.modelo.nng;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;

/**
 * Representa el indentificador de un Rango de Numeración No Geográfica.
 */
@Embeddable
public class RangoSerieNngPK implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Primary Key. */
    @SequenceGenerator(name = "SEQ_ID_NNG_RANGO_SERIE", sequenceName = "SEQ_ID_NNG_RANGO_SERIE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_NNG_RANGO_SERIE")
    @Column(name = "ID_NNG_RANGO_SERIE", unique = true, nullable = false)
    private BigDecimal id;

    /** Identificador de la Clave de Servicio. */
    @Column(name = "ID_CLAVE_SERVICIO", unique = true, nullable = false)
    private BigDecimal idClaveServicio;

    /** Identificador de Serie. */
    @Column(name = "SNA", unique = true, nullable = false, precision = 3)
    private BigDecimal sna;

    /** Constructor. Por defecto vacío. */
    public RangoSerieNngPK() {

    }

    // GETTERS & SETTERS

    /**
     * Primary Key.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Primary Key.
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Identificador de la Clave de Servicio.
     * @return BigDecimal
     */
    public BigDecimal getIdClaveServicio() {
        return idClaveServicio;
    }

    /**
     * Identificador de la Clave de Servicio.
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

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RangoSerieNngPK)) {
            return false;
        }
        RangoSerieNngPK castOther = (RangoSerieNngPK) other;
        return (this.id.intValue() == castOther.id.intValue())
                && (this.idClaveServicio.intValue() == castOther.idClaveServicio.intValue())
                && (this.sna.intValue() == castOther.sna.intValue());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + ((int) (this.id.intValue() ^ (this.id.intValue() >>> 32)));
        hash = hash * prime + ((int) (this.idClaveServicio.intValue() ^ (this.idClaveServicio.intValue() >>> 32)));
        hash = hash * prime + ((int) (this.sna.intValue() ^ (this.sna.intValue() >>> 32)));

        return hash;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.id).append("-").append(this.idClaveServicio).append("-").append(this.sna);
        return sb.toString();
    }
}
