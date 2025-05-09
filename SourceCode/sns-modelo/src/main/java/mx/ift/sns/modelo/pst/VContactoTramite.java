package mx.ift.sns.modelo.pst;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Vista sobre los Representantes Legales en los trámites.
 */
@Entity
@ReadOnly
@Table(name = "CONTACTO_EN_TRAMITE_VM")
@Cacheable(false)
public class VContactoTramite implements Serializable {
    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @Column(name = "ID")
    private String id;

    /** Representante Legal. */
    @Column(name = "ID_REPRESENTANTE_LEGAL")
    private BigDecimal idRepLegal;

    /** Representante Suplente. */
    @Column(name = "ID_REPRESENTANTE_SUPLENTE")
    private BigDecimal idRepSup;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the idRepLegal
     */
    public BigDecimal getIdRepLegal() {
        return idRepLegal;
    }

    /**
     * @param idRepLegal the idRepLegal to set
     */
    public void setIdRepLegal(BigDecimal idRepLegal) {
        this.idRepLegal = idRepLegal;
    }

    /**
     * @return the idRepSup
     */
    public BigDecimal getIdRepSup() {
        return idRepSup;
    }

    /**
     * @param idRepSup the idRepSup to set
     */
    public void setIdRepSup(BigDecimal idRepSup) {
        this.idRepSup = idRepSup;
    }
}
