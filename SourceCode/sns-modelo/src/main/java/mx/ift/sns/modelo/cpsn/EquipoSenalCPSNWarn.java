package mx.ift.sns.modelo.cpsn;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Cacheable;
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

/**
 * Warnings asociados a la carga de equipos de señalización.
 * @author X23016PE
 */
@Entity
@Table(name = "EQUIPO_SENAL_CPSN_WARN")
@Cacheable(false)
@SequenceGenerator(name = "SEQ_ID_EQUIPO_CPSN_WARN", sequenceName = "SEQ_ID_EQUIPO_CPSN_WARN", allocationSize = 1)
@NamedQuery(name = "EquipoSenalCPSNWarn.findAll", query = "SELECT esw FROM EquipoSenalCPSNWarn esw")
public class EquipoSenalCPSNWarn implements Serializable {

    /** Serialización . */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_EQUIPO_CPSN_WARN")
    @Column(name = "ID_EQUIPO_CPSN_WARN", unique = true, nullable = false)
    private BigDecimal id;

    /** Identificador del equipo de señalización asociado al warning. */
    @ManyToOne
    @JoinColumn(name = "ID_EQUIPO_SENAL_CPSN", nullable = false)
    private EquipoSenalCPSN equipoSenalCPSN;

    /** Warning generado al procesar el archivo de carga . */
    @Column(name = "WARNING", nullable = false)
    private String warning;

    /** Constructor por defecto. */
    public EquipoSenalCPSNWarn() {
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
     * Identificador del equipo de señalización asociado al warning.
     * @return the equipoSenalCPSN
     */
    public EquipoSenalCPSN getEquipoSenalCPSN() {
        return equipoSenalCPSN;
    }

    /**
     * Identificador del equipo de señalización asociado al warning.
     * @param equipoSenalCPSN the equipoSenalCPSN to set
     */
    public void setEquipoSenalCPSN(EquipoSenalCPSN equipoSenalCPSN) {
        this.equipoSenalCPSN = equipoSenalCPSN;
    }

    /**
     * Warning generado al procesar el archivo de carga .
     * @return the warning
     */
    public String getWarning() {
        return warning != null ? warning.trim() : warning;
    }

    /**
     * Warning generado al procesar el archivo de carga .
     * @param warning the warning to set
     */
    public void setWarning(String warning) {
        this.warning = warning;
    }

}
