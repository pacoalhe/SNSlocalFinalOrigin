package mx.ift.sns.modelo.pst;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Representa el Estatus de un Convenio entre dos Proveedores de Servicios.
 */
@Entity
@Table(name = "CAT_ESTATUS_CONVENIO")
@NamedQuery(name = "EstadoEstadoConvenio.findAll", query = "SELECT e FROM EstadoConvenio e")
@ReadOnly
public class EstadoConvenio implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Estatus Vigente. */
    public static final String VIGENTE = "V";

    /** Estatus No Vigente. */
    public static final String NO_VIGENTE = "N";

    /** Identificador interno. */
    @Id
    @Column(name = "ID_ESTATUS_CONVENIO", length = 1)
    private String cdg;

    /** Descripción del tipo. */
    @Column(name = "DESCRIPCION", length = 100)
    private String descripcion;

    /** Constructor, por defecto vacío. */
    public EstadoConvenio() {
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

}
