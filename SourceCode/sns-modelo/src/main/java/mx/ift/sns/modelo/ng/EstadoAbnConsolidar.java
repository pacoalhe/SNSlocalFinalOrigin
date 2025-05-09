package mx.ift.sns.modelo.ng;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Representa el estado de la consolicación de un abn de numeración no geográfica.
 */
@Entity
@Table(name = "NG_ESTADO_ABN_CONSOLIDAR")
@ReadOnly
@NamedQuery(name = "EstadoAbnConsolidar.findAll", query = "SELECT e FROM EstadoAbnConsolidar e")
public class EstadoAbnConsolidar implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Estado en Trámite. */
    public static final String EN_TRAMITE = "T";

    /** Estado Consolidado. */
    public static final String CONSOLIDADO = "C";

    /** Estado Cancelado. */
    public static final String CANCELADO = "X";

    /** Identificador. */
    @Id
    @Column(name = "ID_NG_ESTADO_ABN_CONSOLIDAR", unique = true, nullable = false, length = 1)
    private String codigo;

    /** Descripción. */
    @Column(name = "DESCRIPCION", nullable = false, length = 100)
    private String descripcion;

    /** Constructor, por defecto vacío. */
    public EstadoAbnConsolidar() {
    }

    /**
     * codigo.
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * codigo.
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
