package mx.ift.sns.modelo.central;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Respresenta el estatus de central.
 */
@Entity
@Table(name = "CAT_ESTATUS")
@ReadOnly
@NamedQuery(name = "Estatus.findAll", query = "SELECT e FROM Estatus e")
public class Estatus implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Estatus Activo. */
    public static final String ACTIVO = "1";

    /** Estatus Inactivo. */
    public static final String INACTIVO = "2";

    /** Identificador interno. */
    @Id
    @Column(name = "ID_ESTATUS", length = 1)
    private String cdg;

    /** Descripción del tipo. */
    @Column(name = "DESCRIPCION", length = 100)
    private String descripcion;

    /** Constructor, por defecto vacío. */
    public Estatus() {
    }

    /**
     * Código de tipo.
     * @return String
     */
    public String getCdg() {
        return this.cdg;
    }

    /**
     * Código de tipo.
     * @param cdg String
     */
    public void setCdg(String cdg) {
        this.cdg = cdg;
    }

    /**
     * Descripción de tipo.
     * @return String
     */
    public String getDescripcion() {
        return this.descripcion;
    }

    /**
     * Descripción de tipo.
     * @param descripcion String
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cdg == null) ? 0 : cdg.hashCode());
        result = prime * result + ((descripcion == null) ? 0 : descripcion.hashCode());
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
        Estatus other = (Estatus) obj;
        if (cdg == null) {
            if (other.cdg != null) {
                return false;
            }
        } else if (!cdg.equals(other.cdg)) {
            return false;
        }
        if (descripcion == null) {
            if (other.descripcion != null) {
                return false;
            }
        } else if (!descripcion.equals(other.descripcion)) {
            return false;
        }
        return true;
    }
}
