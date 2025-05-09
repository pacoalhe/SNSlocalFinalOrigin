package mx.ift.sns.modelo.cpsi;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Representa el estado del de la solicitud de codigos del UIT.
 */
@Entity
@Table(name = "CAT_ESTATUS_SOL_UIT")
@NamedQuery(name = "EstatusSolicitudCpsiUit.findAll", query = "SELECT e FROM EstatusSolicitudCpsiUit e")
@ReadOnly
public class EstatusSolicitudCpsiUit implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Estado Pendiente. */
    public static final String PENDIENTE = "P";

    /** Descripción Estado Pendiente. */
    public static final String TXT_PENDIENTE = "Pendiente";

    /** Estado Cedido. */
    public static final String ENTREGADO = "E";

    /** Descripción Estado Pendiente. */
    public static final String TXT_ENTREGADO = "Entregado";

    /** Estado Cancelado. */
    public static final String CANCELADO = "C";

    /** Descripción Estado Pendiente. */
    public static final String TXT_CANCELADO = "Cancelado";

    /** Identificador. */
    @Id
    @Column(name = "ID_ESTATUS_SOL_UIT", unique = true, nullable = false, length = 1)
    private String codigo;

    /** Descripción. */
    @Column(name = "DESCRIPCION", nullable = false, length = 100)
    private String descripcion;

    /** Constructor, por defecto vacío. */
    public EstatusSolicitudCpsiUit() {
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
        return new String(codigo + " - " + descripcion);
    }
}
