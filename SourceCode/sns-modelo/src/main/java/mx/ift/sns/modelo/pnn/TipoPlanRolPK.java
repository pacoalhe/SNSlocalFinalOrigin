package mx.ift.sns.modelo.pnn;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the PNN_TIPO_PLAN_ROL database table.
 */
@Embeddable
public class TipoPlanRolPK implements Serializable {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * id del rol.
     */
    @Column(name = "ID_ROL", unique = true, nullable = false)
    private String idRol;

    /**
     * id del tipo plan.
     */
    @Column(name = "ID_TIPO_PLAN", unique = true, nullable = false)
    private String idTipoPlan;

    /**
     * Constructor por defecto.
     */
    public TipoPlanRolPK() {
    }

    /**
     * @return String
     */
    public String getIdRol() {
        return this.idRol;
    }

    /**
     * @param idRol String
     */
    public void setIdRol(String idRol) {
        this.idRol = idRol;
    }

    /**
     * @return String
     */
    public String getIdTipoPlan() {
        return this.idTipoPlan;
    }

    /**
     * @param idTipoPlan String
     */
    public void setIdTipoPlan(String idTipoPlan) {
        this.idTipoPlan = idTipoPlan;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TipoPlanRolPK)) {
            return false;
        }
        TipoPlanRolPK castOther = (TipoPlanRolPK) other;
        return this.idRol.equals(castOther.idRol)
                && this.idTipoPlan.equals(castOther.idTipoPlan);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.idRol.hashCode();
        hash = hash * prime + this.idTipoPlan.hashCode();

        return hash;
    }
}
