package mx.ift.sns.modelo.pst;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Representa el tipo de Modalidad, cpp o mpp, de un tipo de red asociada a un Proveedor de Servicios.
 */
@Entity
@Table(name = "CAT_TIPO_MODALIDAD")
@ReadOnly
@NamedQuery(name = "TipoModalidad.findAll", query = "SELECT t FROM TipoModalidad t")
public class TipoModalidad implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Tipo de modalidad CPP. */
    public static final String CPP = "CPP";

    /** Tipo de modalidad MPP. */
    public static final String MPP = "MPP";

    /** Identificador interno. */
    @Id
    @Column(name = "ID_TIPO_MODALIDAD", length = 3, insertable = false)
    private String cdg;

    /** Descripción del tipo. */
    @Column(name = "DESCRIPCION", length = 25)
    private String descripcion;

    /** Constructor, por defecto vacío. */
    public TipoModalidad() {
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
    public String toString() {
        return new String(cdg + " - " + descripcion);
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof TipoModalidad) && (cdg != null)
                ? cdg.equals(((TipoModalidad) other).cdg)
                : (other == this);
    }

    @Override
    public int hashCode() {
        return (cdg != null)
                ? (this.getClass().hashCode() + cdg.hashCode())
                : super.hashCode();
    }

}
