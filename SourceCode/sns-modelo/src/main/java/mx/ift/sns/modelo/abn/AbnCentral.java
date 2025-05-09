package mx.ift.sns.modelo.abn;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import mx.ift.sns.modelo.central.Central;

/**
 * Relación entre una Central y un ABN.
 */
@Entity
@Table(name = "ABN_CENTRAL")
@NamedQuery(name = "AbnCentral.findAll", query = "SELECT a FROM AbnCentral a")
public class AbnCentral implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @EmbeddedId
    private AbnCentralPK id;

    /** Relacion: Muchos AbnCentral pueden pertenecer al mismo ABN. */
    @ManyToOne
    @JoinColumn(name = "ID_ABN", insertable = false, updatable = false, unique = true, nullable = false)
    private Abn abn;

    /** Relacion: Muchas Centrales pueden pertenecer al mismo ABN. */
    @ManyToOne
    @JoinColumn(name = "ID_ABN_CENTRAL", insertable = false, updatable = false, unique = true, nullable = false)
    private Central central;

    /**
     * Constructor.
     */
    public AbnCentral() {
    }

    /**
     * Identificador.
     * @return PK AbnCentral
     */
    public AbnCentralPK getId() {
        return this.id;
    }

    /**
     * Identificador.
     * @param id AbnCentralPK
     */
    public void setId(AbnCentralPK id) {
        this.id = id;
    }

    /**
     * ABN asociado.
     * @return Abn
     */
    public Abn getAbn() {
        return this.abn;
    }

    /**
     * ABN asociado.
     * @param abn Abn
     */
    public void setAbn(Abn abn) {
        this.abn = abn;
    }

    /**
     * Central asociada.
     * @return Central
     */
    public Central getCentral() {
        return this.central;
    }

    /**
     * Central asociada.
     * @param central Central
     */
    public void setCentral(Central central) {
        this.central = central;
    }

}
