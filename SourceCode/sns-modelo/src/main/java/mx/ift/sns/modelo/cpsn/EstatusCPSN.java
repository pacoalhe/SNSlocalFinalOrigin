package mx.ift.sns.modelo.cpsn;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Representa el Estatus de un Código de Puntos de Señalización Nacional.
 */
@Entity
@Table(name = "CAT_ESTATUS_CPSN")
@ReadOnly
@NamedQuery(name = "EstatusCPSN.findAll", query = "SELECT e FROM EstatusCPSN e")
public class EstatusCPSN implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Estatus Asignado. */
    public static final String ASIGNADO = "A";

    /** Estatus Libre. */
    public static final String LIBRE = "L";

    /** Estatus Reservado. */
    public static final String RESERVADO = "R";

    /** Estatus Cuarentena. */
    public static final String CUARENTENA = "Q";

    /** Estatus Planificado. */
    public static final String PLANIFICADO = "P";

    /** Identificador. */
    @Id
    @Column(name = "ID_ESTATUS_CPSN", length = 1)
    private String id;

    /** Descripción del tipo. */
    @Column(name = "DESCRIPCION", length = 100)
    private String descripcion;

    /** Constructor, por defecto vacío. */
    public EstatusCPSN() {
    }

    /**
     * Constructor específico.
     * @param id Identificador de Estado.
     */
    public EstatusCPSN(String id) {
        this.id = id;
    }

    /**
     * Identificador.
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Identificador.
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * descripcion.
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * descripcion.
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((descripcion == null) ? 0 : descripcion.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
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
        EstatusCPSN other = (EstatusCPSN) obj;
        if (descripcion == null) {
            if (other.descripcion != null) {
                return false;
            }
        } else if (!descripcion.equals(other.descripcion)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }
}
