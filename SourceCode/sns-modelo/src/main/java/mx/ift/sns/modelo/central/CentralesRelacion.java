package mx.ift.sns.modelo.central;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Representa la relación entre centrales para el trámite de asignación.
 */
@Entity
@Table(name = "CENTRALES_RELACION")
@NamedQuery(name = "CentralesRelacion.findAll", query = "SELECT c FROM CentralesRelacion c")
public class CentralesRelacion implements Serializable {
    /** Serial ID. */
    private static final long serialVersionUID = 1L;

    /** ID. */
    @EmbeddedId
    private CentralesRelacionPK id;

    /** Central de origen. */
    @ManyToOne
    @JoinColumn(name = "ID_CENTRAL_ORIGEN", insertable = false, updatable = false, unique = true, nullable = false)
    private Central centralOrigen;

    /** Central de destino. */
    @ManyToOne
    @JoinColumn(name = "ID_CENTRAL_DESTINO", insertable = false, updatable = false, unique = true, nullable = false)
    private Central centralDestino;

    /** Constructor. */
    public CentralesRelacion() {
    }

    /**
     * ID.
     * @return the id
     */
    public CentralesRelacionPK getId() {
        return id;
    }

    /**
     * ID.
     * @param id the id to set
     */
    public void setId(CentralesRelacionPK id) {
        this.id = id;
    }

    /**
     * Central de origen.
     * @return the centralOrigen
     */
    public Central getCentralOrigen() {
        return centralOrigen;
    }

    /**
     * Central de origen.
     * @param centralOrigen the centralOrigen to set
     */
    public void setCentralOrigen(Central centralOrigen) {
        this.centralOrigen = centralOrigen;
    }

    /**
     * Central de destino.
     * @return the centralDestino
     */
    public Central getCentralDestino() {
        return centralDestino;
    }

    /**
     * Central de destino.
     * @param centralDestino the centralDestino to set
     */
    public void setCentralDestino(Central centralDestino) {
        this.centralDestino = centralDestino;
    }

}
