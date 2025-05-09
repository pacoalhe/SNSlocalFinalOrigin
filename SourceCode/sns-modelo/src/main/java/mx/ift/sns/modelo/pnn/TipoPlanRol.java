package mx.ift.sns.modelo.pnn;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the PNN_TIPO_PLAN_ROL database table.
 */
@Entity
@Table(name = "PNN_TIPO_PLAN_ROL")
@NamedQuery(name = "TipoPlanRol.findAll", query = "SELECT t FROM TipoPlanRol t")
public class TipoPlanRol implements Serializable {
    /** Serial UID. */
    private static final long serialVersionUID = 1L;
    /** Id. */
    @EmbeddedId
    private TipoPlanRolPK id;

    /**
     * Constructor por defecto.
     */
    public TipoPlanRol() {
    }

    /**
     *  @return id
     */
    public TipoPlanRolPK getId() {
        return this.id;
    }

    /**
     *  @param id TipoPLanRolPk
     */
    public void setId(TipoPlanRolPK id) {
        this.id = id;
    }

}
