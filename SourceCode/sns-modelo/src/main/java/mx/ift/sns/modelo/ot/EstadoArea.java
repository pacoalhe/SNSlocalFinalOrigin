package mx.ift.sns.modelo.ot;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the CAT_ESTADO_AREA database table.
 */
@Entity
@Table(name = "CAT_ESTADO_AREA")
@NamedQuery(name = "EstadoArea.findAll", query = "SELECT e FROM EstadoArea e")
public class EstadoArea implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del EstadoArea. */
    @EmbeddedId
    private EstadoAreaPK id;

    /** Abreviatura. */
    @Column(name = "COORDENADAS", length = 2048)
    private String coordenadas;

    /** Constructor, por defecto vacío. */
    public EstadoArea() {
    }

    /**
     * @return the id
     */
    public EstadoAreaPK getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(EstadoAreaPK id) {
        this.id = id;
    }

    /**
     * @return the coordenadas
     */
    public String getCoordenadas() {
        return coordenadas;
    }

    /**
     * @param coordenadas the coordenadas to set
     */
    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

}
