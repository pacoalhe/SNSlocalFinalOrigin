package mx.ift.sns.modelo.solicitud;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Estado de una solicitud.
 */
@Entity
@Table(name = "CAT_ESTATUS_SOLICITUD")
@ReadOnly
@NamedQuery(name = "EstadoSolicitud.findAll", query = "SELECT e FROM EstadoSolicitud e")
public class EstadoSolicitud implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Solicitud en tramite. */
    public static final String SOLICITUD_EN_TRAMITE = "E";

    /** Solicitud terminada. */
    public static final String SOLICITUD_TERMINADA = "T";

    /** Solicitud cancelada. */
    public static final String SOLICITUD_CANCELADA = "C";

    /** Identificador de estatus. */
    @Id
    @Column(name = "ID_ESTATUS_SOLICITUD", length = 1)
    private String codigo;

    /** Descripción de tipo. */
    @Column(name = "DESCRIPCION", length = 40)
    private String descripcion;

    /** Constructor, por defecto vacío. */
    public EstadoSolicitud() {
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
    public boolean equals(Object other) {
        return (other instanceof EstadoSolicitud) && (codigo != null)
                ? codigo.equals(((EstadoSolicitud) other).codigo)
                : (other == this);
    }

    @Override
    public int hashCode() {
        return (codigo != null)
                ? (this.getClass().hashCode() + codigo.hashCode())
                : super.hashCode();
    }

}
