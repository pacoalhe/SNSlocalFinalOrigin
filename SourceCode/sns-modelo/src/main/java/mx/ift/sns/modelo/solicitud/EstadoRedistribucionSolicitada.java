package mx.ift.sns.modelo.solicitud;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * The persistent class for the NG_ESTADOS_REDIST_SOL database table.
 */
@Entity
@Table(name = "CAT_ESTATUS_REDISTRIBUCION")
@ReadOnly
@NamedQuery(name = "EstadoRedistribucionSolicitada.findAll", query = "SELECT e FROM EstadoRedistribucionSolicitada e")
public class EstadoRedistribucionSolicitada implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Estado Pendiente. */
    public static final String PENDIENTE = "P";

    /** Estado Redistribuido. */
    public static final String REDISTRIBUIDO = "R";

    /** Estado Cancelado. */
    public static final String CANCELADO = "X";

    /** Identificador de estatus. */
    @Id
    @Column(name = "ID_ESTATUS_REDIST", unique = true, nullable = false, length = 1)
    private String codigo;

    /** Descripción. */
    @Column(name = "DESCRIPCION", nullable = false, length = 100)
    private String descripcion;

    /** Constructor, por defecto vacío. */
    public EstadoRedistribucionSolicitada() {
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
