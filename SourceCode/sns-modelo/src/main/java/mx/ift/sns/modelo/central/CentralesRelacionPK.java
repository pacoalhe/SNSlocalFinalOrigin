package mx.ift.sns.modelo.central;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Rrepresnta la clave primaria de la relación entre centrales para el trámite de asignación.
 */
@Embeddable
public class CentralesRelacionPK implements Serializable {
    /** default serial version id, required for serializable classes. */
    private static final long serialVersionUID = 1L;

    /** Id Central Origen. */
    @Column(name = "ID_CENTRAL_ORIGEN")
    private BigDecimal idCentralOrigen;

    /** Id Central Destino. */
    @Column(name = "ID_CENTRAL_DESTINO")
    private BigDecimal idCentralDestino;

    /** Constructor. */
    public CentralesRelacionPK() {
    }

    /**Id Central Origen.
     * @return the idCentralOrigen
     */
    public BigDecimal getIdCentralOrigen() {
        return idCentralOrigen;
    }

    /**Id Central Origen.
     * @param idCentralOrigen the idCentralOrigen to set
     */
    public void setIdCentralOrigen(BigDecimal idCentralOrigen) {
        this.idCentralOrigen = idCentralOrigen;
    }

    /**
     * Id Central Destino.
     * @return the idCentralDestino
     */
    public BigDecimal getIdCentralDestino() {
        return idCentralDestino;
    }

    /**
     * Id Central Destino.
     * @param idCentralDestino the idCentralDestino to set
     */
    public void setIdCentralDestino(BigDecimal idCentralDestino) {
        this.idCentralDestino = idCentralDestino;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idCentralDestino == null) ? 0 : idCentralDestino.hashCode());
        result = prime * result + ((idCentralOrigen == null) ? 0 : idCentralOrigen.hashCode());
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
        CentralesRelacionPK other = (CentralesRelacionPK) obj;
        if (idCentralDestino == null) {
            if (other.idCentralDestino != null) {
                return false;
            }
        } else if (!idCentralDestino.equals(other.idCentralDestino)) {
            return false;
        }
        if (idCentralOrigen == null) {
            if (other.idCentralOrigen != null) {
                return false;
            }
        } else if (!idCentralOrigen.equals(other.idCentralOrigen)) {
            return false;
        }
        return true;
    }

}
