package mx.ift.sns.modelo.cps;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Representa el Estatus de una Asignación de Códigos de Puntos de Señalización.
 */
@Entity
@Table(name = "CAT_ESTATUS_ASIGNACION")
@NamedQuery(name = "EstatusAsignacionCps.findAll", query = "SELECT e FROM EstatusAsignacionCps e")
@ReadOnly
public class EstatusAsignacionCps implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Estado Pendiente. */
    public static final String PENDIENTE = "P";

    /** Descripción Estado Pendiente. */
    public static final String TXT_PENDIENTE = "Pendiente";

    /** Estado Cedido. */
    public static final String ASIGNADO = "A";

    /** Descripción Estado Pendiente. */
    public static final String TXT_ASIGNADO = "Asignado";

    /** Estado Cancelado. */
    public static final String CANCELADO = "X";

    /** Descripción Estado Pendiente. */
    public static final String TXT_CANCELADO = "Cancelado";

    /** Identificador. */
    @Id
    @Column(name = "ID_ESTATUS_ASIGNACION", unique = true, nullable = false, length = 1)
    private String codigo;

    /** Descripción. */
    @Column(name = "DESCRIPCION", nullable = false, length = 100)
    private String descripcion;

    /** Constructor, por defecto vacío. */
    public EstatusAsignacionCps() {
    }

    /**
     * Identificador.
     * @return String
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Identificador.
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
