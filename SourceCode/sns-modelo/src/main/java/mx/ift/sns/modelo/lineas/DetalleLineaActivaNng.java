package mx.ift.sns.modelo.lineas;

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

import mx.ift.sns.modelo.nng.ClaveServicio;

/**
 * Representa el detalle del reporte de la línea activa de numeración no geográfica.
 */
@Entity
@Table(name = "NNG_REP_DET_LINEA_ACTIVA")
@NamedQuery(name = "DetalleLineaActivaNng.findAll", query = "SELECT d FROM DetalleLineaActivaNng d")
public class DetalleLineaActivaNng implements Serializable {

    /**
     * Serial ID.
     */
    private static final long serialVersionUID = 1L;

    /** ID detalle. */
    @Id
    @SequenceGenerator(name = "SEQ_ID_NNG_REP_DET_LIN_ACT", sequenceName = "SEQ_ID_NNG_REP_DET_LIN_ACT",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_NNG_REP_DET_LIN_ACT")
    @Column(name = "ID_NNG_REP_DET_LIN_ACT")
    private BigDecimal id;

    /** Referencia a la clave de Servicio asociada a la numeración. */
    @ManyToOne
    @JoinColumn(name = "ID_CLAVE_SERVICIO", nullable = false, insertable = false, updatable = false)
    private ClaveServicio claveServicio;

    /** Numeracion activa. */
    @Column(name = "NUM_ACTIVA")
    private BigDecimal numActiva;

    /** Numeracion final. */
    @Column(name = "NUM_FINAL")
    private String numFinal;

    /** Numeracion ininicial. */
    @Column(name = "NUM_INICIO")
    private String numInicio;

    /** SNA. */
    private BigDecimal sna;

    /** Reporte de linea activa. */
    @ManyToOne
    @JoinColumn(name = "ID_NNG_REP_REPORTE")
    private ReporteLineaActivaNng reporteLineaActiva;

    /** Constructor. Por defecto vacío. */
    public DetalleLineaActivaNng() {

    }

    /**
     * ID detalle.
     * @return the id
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * ID detalle.
     * @param id the id to set
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Referencia a la clave de Servicio asociada a la numeración.
     * @return the claveServicio
     */
    public ClaveServicio getClaveServicio() {
        return claveServicio;
    }

    /**
     * Referencia a la clave de Servicio asociada a la numeración.
     * @param claveServicio the claveServicio to set
     */
    public void setClaveServicio(ClaveServicio claveServicio) {
        this.claveServicio = claveServicio;
    }

    /**
     * Numeracion activa.
     * @return the numActiva
     */
    public BigDecimal getNumActiva() {
        return numActiva;
    }

    /**
     * Numeracion activa.
     * @param numActiva the numActiva to set
     */
    public void setNumActiva(BigDecimal numActiva) {
        this.numActiva = numActiva;
    }

    /**
     * Numeracion final.
     * @return the numFinal
     */
    public String getNumFinal() {
        return numFinal;
    }

    /**
     * Numeracion final.
     * @param numFinal String
     */
    public void setNumFinal(String numFinal) {
        this.numFinal = numFinal;
    }

    /**
     * Numeracion ininicial.
     * @return the numInicio
     */
    public String getNumInicio() {
        return numInicio;
    }

    /**
     * Numeracion ininicial.
     * @param numInicio the numInicio to set
     */
    public void setNumInicio(String numInicio) {
        this.numInicio = numInicio;
    }

    /**
     * SNA.
     * @return the sna
     */
    public BigDecimal getSna() {
        return sna;
    }

    /**
     * SNA.
     * @param sna the sna to set
     */
    public void setSna(BigDecimal sna) {
        this.sna = sna;
    }

    /**
     * Reporte de linea activa.
     * @return the reporteLineaActiva
     */
    public ReporteLineaActivaNng getReporteLineaActiva() {
        return reporteLineaActiva;
    }

    /**
     * Reporte de linea activa.
     * @param reporteLineaActiva the reporteLineaActiva to set
     */
    public void setReporteLineaActiva(ReporteLineaActivaNng reporteLineaActiva) {
        this.reporteLineaActiva = reporteLineaActiva;
    }

}
