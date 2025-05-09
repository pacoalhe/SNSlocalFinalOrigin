package mx.ift.sns.modelo.nng;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Representa el tipo de asignación de numeración no geográfica.
 */
@Entity
@Table(name = "CAT_TIPO_ASIGNACION")
@ReadOnly
@NamedQuery(name = "TipoAsignacion.findAll", query = "SELECT c FROM TipoAsignacion c")
public class TipoAsignacion implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Tipo de asignacion por Series. */
    public static final String SERIES = "S";

    /** Tipo de asignacion por Especifica. */
    public static final String ESPECIFICA = "E";

    /** Identificador. */
    @Id
    @Column(name = "ID_TIPO_ASIGNACION", insertable = false)
    private String cdg;

    /** Descripcion. */
    private String descripcion;

    /** Constructor vacio por defecto. */
    public TipoAsignacion() {
    }

    /**
     * Identificador.
     * @return the cdg
     */
    public String getCdg() {
        return cdg;
    }

    /**
     * Identificador.
     * @param cdg the cdg to set
     */
    public void setCdg(String cdg) {
        this.cdg = cdg;
    }

    /**
     * Descripcion.
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Descripcion.
     * @param descripcion the descripcion to set
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
        return (other instanceof TipoAsignacion) && (cdg != null)
                ? cdg.equals(((TipoAsignacion) other).cdg)
                : (other == this);
    }

    @Override
    public int hashCode() {
        return (cdg != null)
                ? (this.getClass().hashCode() + cdg.hashCode())
                : super.hashCode();
    }

}
