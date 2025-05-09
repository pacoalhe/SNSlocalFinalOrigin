package mx.ift.sns.modelo.pst;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Representa un Tipo de Proveedor de Servicios de Telecomunicación.
 */
@Entity
@Table(name = "CAT_TIPO_PST")
@ReadOnly
@NamedQuery(name = "TipoProveedor.findAll", query = "SELECT t FROM TipoProveedor t")
public class TipoProveedor implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Proveedor Concesionario. */
    public static final String CONCESIONARIO = "C";

    /** Representante Comercializadora. */
    public static final String COMERCIALIZADORA = "Z";

    /** Representante Concesionario y Comercializadora. */
    public static final String AMBOS = "A";

    /** Identificador interno. */
    @Id
    @Column(name = "ID_TIPO_PST", length = 1, insertable = false)
    private String cdg;

    /** Descripción del tipo. */
    @Column(name = "DESCRIPCION", length = 100)
    private String descripcion;

    /** Constructor, por defecto vacío. */
    public TipoProveedor() {
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
        return (other instanceof TipoProveedor) && (cdg != null)
                ? cdg.equals(((TipoProveedor) other).cdg)
                : (other == this);
    }

    @Override
    public int hashCode() {
        return (cdg != null)
                ? (this.getClass().hashCode() + cdg.hashCode())
                : super.hashCode();
    }
}
