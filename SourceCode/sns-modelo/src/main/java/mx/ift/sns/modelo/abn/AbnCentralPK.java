package mx.ift.sns.modelo.abn;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Clave primaria de una entidad AbnCentral.
 */
@Embeddable
public class AbnCentralPK implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador de AbnCentral. */
    @Column(name = "ID_ABN_CENTRAL")
    private long idCentral;

    /** Identificador de ABN. */
    @Column(name = "ID_ABN", precision = 3)
    private long idAbn;

    /**
     * Constructor.
     */
    public AbnCentralPK() {
    }

    /**
     * Identificador de AbnCentral.
     * @return long
     */
    public long getIdCentral() {
        return this.idCentral;
    }

    /**
     * Identificador de AbnCentral.
     * @param idCentral long
     */
    public void setIdCentral(long idCentral) {
        this.idCentral = idCentral;
    }

    /**
     * Identificador de ABN.
     * @return long
     */
    public long getIdAbn() {
        return this.idAbn;
    }

    /**
     * Identificador de ABN.
     * @param idAbn long
     */
    public void setIdAbn(long idAbn) {
        this.idAbn = idAbn;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (idAbn ^ (idAbn >>> 32));
        result = prime * result + (int) (idCentral ^ (idCentral >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbnCentralPK other = (AbnCentralPK) obj;
        if (idAbn != other.idAbn) {
            return false;
        }
        if (idCentral != other.idCentral) {
            return false;
        }
        return true;
    }

}
