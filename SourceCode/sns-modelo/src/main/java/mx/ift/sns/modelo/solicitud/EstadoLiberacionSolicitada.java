package mx.ift.sns.modelo.solicitud;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * The persistent class for the CAT_ESTATUS_LIBERACION_SOL database table.
 */
@Entity
@Table(name = "CAT_ESTATUS_LIBERACION")
@NamedQuery(name = "EstadoLiberacionSolicitada.findAll", query = "SELECT e FROM EstadoLiberacionSolicitada e")
@ReadOnly
public class EstadoLiberacionSolicitada implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Estado Reservado. */
    // public static final String RESERVADO = "R";

    /** Estado Liberado. */
    public static final String LIBERADO = "L";

    /** Estado Cancelado. */
    public static final String CANCELADO = "X";

    /** Estado Pendiente. */
    public static final String PENDIENTE = "P";

    /** Descripción Estado Pendiente. */
    public static final String TXT_PENDIENTE = "Pendiente";

    /** Estado de periodo de reserva. */
    public static final String PERIODO_RESERVA = "J";

    /** Identificador de estatus. */
    @Id
    @Column(name = "ID_ESTATUS_LIBERACION", unique = true, nullable = false, length = 1)
    private String codigo;

    /** Descripción. */
    @Column(name = "DESCRIPCION", nullable = false, length = 100)
    private String descripcion;

    /** Constructor, por defecto vacío. */
    public EstadoLiberacionSolicitada() {
    }

    /**
     * Identificador de estatus.
     * @return String
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Identificador de estatus.
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
        return new String(codigo + " - " + descripcion);
    }
}
