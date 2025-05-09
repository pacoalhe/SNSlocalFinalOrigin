package mx.ift.sns.modelo.ng;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import mx.ift.sns.modelo.series.Nir;

/**
 * Representa la consolidación de un nir de numeración geográfica.
 */
@Entity
@Table(name = "NG_NIR_CONSOLIDAR")
@SequenceGenerator(name = "SEQ_ID_NIR_CONSO", sequenceName = "SEQ_ID_NIR_CONSO", allocationSize = 1)
@NamedQuery(name = "NirConsolidar.findAll", query = "SELECT n FROM NirConsolidar n")
public class NirConsolidar implements Serializable {

    /** Serializacion . */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_NIR_CONSO")
    @Column(name = "ID_NIR_CONSOLIDAR", unique = true, nullable = false)
    private BigDecimal id;

    /** Relación: Un abnConsolidar puede tener muchas Solicitud de Consolidacion. */
    @ManyToOne
    @JoinColumn(name = "ID_ABN_CONSOLIDAR", nullable = false)
    private AbnConsolidar abnConsolidar;

    /** Relación: Una abnConsolidar puede tener muchas poblaciones. */
    @ManyToOne
    @JoinColumn(name = "ID_NIR", nullable = false)
    private Nir nir;

    /** Constructor, por defecto vacío. */
    public NirConsolidar() {
    }

    /**
     * Identificador.
     * @return the id
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Identificador.
     * @param id the id to set
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Relación: Un abnConsolidar puede tener muchas Solicitud de Consolidacion.
     * @return the abnConsolidar
     */
    public AbnConsolidar getAbnConsolidar() {
        return abnConsolidar;
    }

    /**
     * Relación: Un abnConsolidar puede tener muchas Solicitud de Consolidacion.
     * @param abnConsolidar the abnConsolidar to set
     */
    public void setAbnConsolidar(AbnConsolidar abnConsolidar) {
        this.abnConsolidar = abnConsolidar;
    }

    /**
     * Relación: Una abnConsolidar puede tener muchas poblaciones.
     * @return the nir
     */
    public Nir getNir() {
        return nir;
    }

    /**
     * Relación: Una abnConsolidar puede tener muchas poblaciones.
     * @param nir the nir to set
     */
    public void setNir(Nir nir) {
        this.nir = nir;
    }

}
