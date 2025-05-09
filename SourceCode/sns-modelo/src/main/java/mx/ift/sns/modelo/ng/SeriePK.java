package mx.ift.sns.modelo.ng;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Representa el indentificador de una Serie de Numeración Geográfica.
 */
@Embeddable
public class SeriePK implements Serializable {

    /** default serial version id, required for serializable classes. */
    private static final long serialVersionUID = 1L;

    /** Identificador de numeración de serie. El id son 3 o 4 digitos y no puede empezar por 0. */
    @Column(name = "ID_SERIE", unique = true, nullable = false, precision = 4)
    private BigDecimal sna;

    /** Identificador de región de numeración. */
    @Column(name = "ID_NIR", insertable = false, updatable = false, unique = true, nullable = false)
    private BigDecimal idNir;

    /** Constructor. */
    public SeriePK() {
    }

    /**
     * Identificador de numeración de serie. El id son 3 o 4 digitos y no puede empezar por 0.
     * @return the sna
     */
    public BigDecimal getSna() {
        return sna;
    }

    /**
     * Identificador de numeración de serie. El id son 3 o 4 digitos y no puede empezar por 0.
     * @param sna the sna to set
     */
    public void setSna(BigDecimal sna) {
        this.sna = sna;
    }

    /**
     * Identificador de región de numeración.
     * @return the idNir
     */
    public BigDecimal getIdNir() {
        return idNir;
    }

    /**
     * Identificador de región de numeración.
     * @param idNir the idNir to set
     */
    public void setIdNir(BigDecimal idNir) {
        this.idNir = idNir;
    }

    @Override
    public boolean equals(Object other) {

        if (!(other instanceof SeriePK)) {
            return false;
        }
        SeriePK castOther = (SeriePK) other;
        return (this.sna.compareTo(castOther.getSna()) == 0)
                && (this.idNir.compareTo(castOther.getIdNir()) == 0);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + (this.sna.intValue() ^ (this.sna.intValue() >>> 32));
        hash = hash * prime + (this.idNir.intValue() ^ (this.idNir.intValue() >>> 32));

        return hash;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.idNir).append("-").append(this.sna);
        return sb.toString();
    }
}
