package mx.ift.sns.modelo.abn;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import mx.ift.sns.modelo.ot.Poblacion;

/**
 * Relación entre una Población del Catálogo de Poblaciones y un Área Básica de Numeración.
 */
@Entity
@Table(name = "POBLACION_ABN")
@NamedQuery(name = "PoblacionAbn.findAll", query = "SELECT a FROM PoblacionAbn a")
public class PoblacionAbn implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Poblacion. */
    @Id
    @JoinColumn(name = "ID_INEGI", unique = true, nullable = false)
    private Poblacion inegi;

    /** Abn de la Población. */
    @ManyToOne
    @JoinColumn(name = "ID_ABN", nullable = false)
    private Abn abn;

    /**
     * Población del catálogo de poblaciones.
     * @return Poblacion
     */
    public Poblacion getInegi() {
        return inegi;
    }

    /**
     * Población del catálogo de poblaciones.
     * @param inegi Poblacion
     */
    public void setInegi(Poblacion inegi) {
        this.inegi = inegi;
    }

    /**
     * Abn del catálogo de ABNs.
     * @return Abn
     */
    public Abn getAbn() {
        return abn;
    }

    /**
     * Abn del catálogo de ABNs.
     * @param abn Abn
     */
    public void setAbn(Abn abn) {
        this.abn = abn;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("poblacion abn={ ");
        b.append(inegi);
        b.append(", ");
        b.append(abn);
        b.append("}");
        return b.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof PoblacionAbn) && (inegi != null)
                ? inegi.equals(((PoblacionAbn) obj).inegi)
                : (obj == this);
    }
}
