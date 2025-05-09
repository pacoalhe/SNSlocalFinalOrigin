package mx.ift.sns.modelo.central;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Representa el tipo de central.
 */
@Entity
@Table(name = "CAT_TIPO_CENTRAL")
@NamedQuery(name = "TipoCentral.findAll", query = "SELECT t FROM TipoCentral t")
public class TipoCentral implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @Column(name = "ID_TIPO_CENTRAL", length = 2)
    private String cdg;

    /** Descripción. */
    @Column(name = "DESCRIPCION", nullable = false, length = 100)
    private String descripcion;

    /** Constructor, por defecto vacío. */
    public TipoCentral() {
    }

    /**
     * Identificador de Tipo de Central.
     * @return String
     */
    public String getCdg() {
        return this.cdg;
    }

    /**
     * Identificador de Tipo de Central.
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

    @Override
    public String toString() {
        return new String(cdg + " - " + descripcion);
    }
}
