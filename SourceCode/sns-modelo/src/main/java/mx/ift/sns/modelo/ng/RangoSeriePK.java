package mx.ift.sns.modelo.ng;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;

/**
 * Representa el indentificador de un Rango de Numeración Geográfica.
 */
@Embeddable
public class RangoSeriePK implements Serializable {

    /** default serial version id, required for serializable classes. */
    private static final long serialVersionUID = 1L;

    /** Primary Key. */
    @SequenceGenerator(name = "SEQ_ID_RANGO_SERIE", sequenceName = "SEQ_ID_RANGO_SERIE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_RANGO_SERIE")
    @Column(name = "ID_RANGO_SERIE", unique = true, nullable = false)
    private BigDecimal id;

    /** Identificador de región numérica. */
    @Column(name = "ID_NIR", unique = true, nullable = false)
    private BigDecimal idNir;

    /** Identificador de serie. */
    @Column(name = "ID_SERIE", unique = true, nullable = false, precision = 5)
    private BigDecimal sna;

    /** Constructor. */
    public RangoSeriePK() {
    }

    /**
     * Identificador PK.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Identificador PK.
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Código identificador de Región.
     * @return BigDecimal
     */
    public BigDecimal getIdNir() {
        return idNir;
    }

    /**
     * Código identificador de Región.
     * @param idNir BigDecimal
     */
    public void setIdNir(BigDecimal idNir) {
        this.idNir = idNir;
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
        if (!(other instanceof RangoSeriePK)) {
            return false;
        }
        RangoSeriePK castOther = (RangoSeriePK) other;
        return (this.id == castOther.id)
                && (this.idNir == castOther.idNir)
                && (this.sna == castOther.sna);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + (this.id.intValue() ^ (this.id.intValue() >>> 32));
        hash = hash * prime + (this.idNir.intValue() ^ (this.idNir.intValue() >>> 32));
        hash = hash * prime + (this.sna.intValue() ^ (this.sna.intValue() >>> 32));

        return hash;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.id).append("-").append(this.idNir).append("-").append(this.sna);
        return sb.toString();
    }
}
