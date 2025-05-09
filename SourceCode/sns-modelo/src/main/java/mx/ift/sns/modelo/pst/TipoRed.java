package mx.ift.sns.modelo.pst;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Representa un Tipo de Red.
 */
@Entity
@Table(name = "CAT_TIPO_RED")
@ReadOnly
@NamedQuery(name = "TipoRed.findAll", query = "SELECT t FROM TipoRed t")
public class TipoRed implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Tipo de red fija. */
    public static final String FIJA = "F";

    /** Tipo de red mólv. */
    public static final String MOVIL = "M";

    /** Tipo de red fija y móvil. */
    public static final String AMBAS = "A";

    /** Tipo de red fija y móvil. */
    public static final String DESCONOCIDA = "0";

    /** Tipo de red fija. */
    public static final String FIJA_DESC = "Fijo";

    /** Tipo de red mólv. */
    public static final String MOVIL_DESC = "Móvil";

    /** Tipo de red fija y móvil. */
    public static final String AMBAS_DESC = "Ambas";

    /** Identificador interno. */
    @Id
    @Column(name = "ID_TIPO_RED", length = 1, insertable = false)
    private String cdg;

    /** Descripción del tipo. */
    @Column(name = "DESCRIPCION", length = 25)
    private String descripcion;

    /** Constructor, por defecto vacío. */
    public TipoRed() {
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
        return (other instanceof TipoRed) && (cdg != null)
                ? cdg.equals(((TipoRed) other).cdg)
                : (other == this);
    }

    @Override
    public int hashCode() {
        return (cdg != null)
                ? (this.getClass().hashCode() + cdg.hashCode())
                : super.hashCode();
    }
}
