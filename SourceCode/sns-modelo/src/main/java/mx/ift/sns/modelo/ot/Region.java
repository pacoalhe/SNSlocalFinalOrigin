package mx.ift.sns.modelo.ot;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Representa una Región del Catálogo de Regiones de la Organización Territorial.
 */
@Entity
@Table(name = "CAT_REGION")
@ReadOnly
@SequenceGenerator(name = "SEQ_ID_REGION", sequenceName = "SEQ_ID_REGION", allocationSize = 1)
@NamedQuery(name = "Region.findAll", query = "SELECT r FROM Region r")
public class Region implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador de la región. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_REGION")
    @Column(name = "ID_REGION", unique = true, nullable = false)
    private BigDecimal idRegion;

    /** Descripción. */
    @Column(name = "DESCRIPCION", nullable = false, length = 32)
    private String descripcion;

    /** Constructor, por defecto vacío. */
    public Region() {
    }

    /**
     * @return the idRegion
     */
    public BigDecimal getIdRegion() {
        return idRegion;
    }

    /**
     * @param idRegion the idRegion to set
     */
    public void setIdRegion(BigDecimal idRegion) {
        this.idRegion = idRegion;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idRegion == null) ? 0 : idRegion.hashCode());
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
        Region other = (Region) obj;
        if (idRegion == null) {
            if (other.idRegion != null) {
                return false;
            }
        } else if (!idRegion.equals(other.idRegion)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Region [idRegion=" + idRegion + ", descripcion=" + descripcion + "]";
    }

}
