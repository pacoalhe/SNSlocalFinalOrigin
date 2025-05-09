package mx.ift.sns.modelo.abn;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Representa el Estatus de un Área Básica de Numeración.
 */
@Entity
@Table(name = "CAT_ESTATUS_ABN")
@ReadOnly
@NamedQuery(name = "EstadoAbn.findAll", query = "SELECT e FROM EstadoAbn e")
public class EstadoAbn implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Estatus Activo. */
    public static final String ACTIVO = "1";

    /** Estatus Inactivo. */
    public static final String INACTIVO = "2";

    /** Estatus En Trámite de Consolidación. */
    public static final String TRAMITE_CONSOLIDACION = "3";

    /** Identificador de Estatus. */
    @Id
    @Column(name = "ID_ESTATUS_ABN", unique = true, nullable = false, length = 1)
    private String codigo;

    /** Descripción. */
    @Column(name = "DESCRIPCION", nullable = false, length = 100)
    private String descripcion;

    /** Constructor, por defecto vacío. */
    public EstadoAbn() {
    }

    /**
     * Identificador de Estatus.
     * @return String
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Identificador de Estatus.
     * @param codigo String
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
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
        StringBuilder builder = new StringBuilder();
        builder.append("{cdg=");
        builder.append(codigo);
        builder.append(" descripcion=");
        builder.append(descripcion);
        builder.append("}");
        return builder.toString();
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof EstadoAbn) && (codigo != null)
                ? codigo.equals(((EstadoAbn) other).codigo)
                : (other == this);
    }

    @Override
    public int hashCode() {
        return (codigo != null)
                ? (this.getClass().hashCode() + codigo.hashCode())
                : super.hashCode();
    }

}
