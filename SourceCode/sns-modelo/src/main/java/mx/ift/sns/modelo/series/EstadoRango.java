package mx.ift.sns.modelo.series;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Estado de un rango (Pendiente / Asignado /Reservado).
 */
@Entity
@Table(name = "CAT_ESTATUS_RANGO")
@ReadOnly
@NamedQuery(name = "EstadoRango.findAll", query = "SELECT e FROM EstadoRango e")
public class EstadoRango implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Rango en estado pendiente. */
    public static final String PENDIENTE = "P";

    /** Rango en estado asignado. */
    public static final String ASIGNADO = "A";

    /** Rango en estado reservado. */
    public static final String RESERVADO = "R";

    /** Rango en estado afectado. */
    public static final String AFECTADO = "F";

    /** Rango en estado de periodo de reserva. */
    public static final String PERIODO_RESERVA = "J";

    /** Rango migrado de TELMEX. */
    public static final String MIGRADO = "M";

    /** Identificador. */
    @Id
    @Column(name = "ID_ESTATUS_RANGO", unique = true, nullable = false, length = 1)
    private String codigo;

    /** Descripción. */
    @Column(name = "DESCRIPCION", nullable = false, length = 100)
    private String descripcion;

    /** Constructor, por defecto vacío. */
    public EstadoRango() {
    }

    /**
     * Identificador.
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Identificador.
     * @param codigo the codigo to set
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
        return (other instanceof EstadoRango) && (codigo != null)
                ? codigo.equals(((EstadoRango) other).codigo)
                : (other == this);
    }

    @Override
    public int hashCode() {
        return (codigo != null)
                ? (this.getClass().hashCode() + codigo.hashCode())
                : super.hashCode();
    }
}
