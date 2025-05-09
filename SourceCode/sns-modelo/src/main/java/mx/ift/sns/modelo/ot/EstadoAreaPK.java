package mx.ift.sns.modelo.ot;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the PNN_TIPO_PLAN_ROL database table.
 */
@Embeddable
public class EstadoAreaPK implements Serializable {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * id del estado.
     */
    @Column(name = "ID_ESTADO", unique = true, nullable = false)
    private String idEstado;

    /**
     * id del nir.
     */
    @Column(name = "ID_NIR", unique = true, nullable = false)
    private BigDecimal idNir;

    /**
     * Constructor por defecto.
     */
    public EstadoAreaPK() {
    }

    /**
     * @return the idEstado
     */
    public String getIdEstado() {
        return idEstado;
    }

    /**
     * @param idEstado the idEstado to set
     */
    public void setIdEstado(String idEstado) {
        this.idEstado = idEstado;
    }

    /**
     * @return the idNir
     */
    public BigDecimal getIdNir() {
        return idNir;
    }

    /**
     * @param idNir the idNir to set
     */
    public void setIdNir(BigDecimal idNir) {
        this.idNir = idNir;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EstadoAreaPK)) {
            return false;
        }
        EstadoAreaPK castOther = (EstadoAreaPK) other;
        return this.idEstado.equals(castOther.idEstado)
                && this.idNir.equals(castOther.idNir);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.idEstado.hashCode();
        hash = hash * prime + this.idNir.hashCode();

        return hash;
    }
}
