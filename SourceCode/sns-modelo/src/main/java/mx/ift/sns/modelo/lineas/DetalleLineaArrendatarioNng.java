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
import mx.ift.sns.modelo.pst.Proveedor;

/**
 * Representa el detalle del reporte de línea de arrendatario de numeración no geográfica.
 */
@Entity
@Table(name = "NNG_REP_DET_LIN_ARRTARIO")
@NamedQuery(name = "DetalleLineaArrendatarioNng.findAll", query = "SELECT d FROM DetalleLineaArrendatarioNng d")
public class DetalleLineaArrendatarioNng implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** ID detalle. */
    @Id
    @SequenceGenerator(name = "SEQ_ID_NNG_RDLIN_ARRTARIO", sequenceName = "SEQ_ID_NNG_RDLIN_ARRTARIO",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_NNG_RDLIN_ARRTARIO")
    @Column(name = "ID_NNG_RDLIN_ARRTARIO")
    private BigDecimal id;

    /** ABC. */
    private BigDecimal abc;

    /** Referencia a la clave de Servicio asociada a la numeración. */
    @ManyToOne
    @JoinColumn(name = "ID_CLAVE_SERVICIO", nullable = false, insertable = false, updatable = false)
    private ClaveServicio claveServicio;

    /** Arrendatario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_ARRENDADOR")
    private Proveedor arrendador;

    /** Numeracion final. */
    @Column(name = "NUM_FINAL")
    private String numFinal;

    /** Numeracion inicial. */
    @Column(name = "NUM_INICIAL")
    private String numInicial;

    /** Numeracion activa. */
    @Column(name = "NUM_ACTIVA")
    private BigDecimal numActiva;

    /** SNA. */
    private BigDecimal sna;

    /** Reporte linea arrendatario. */
    @ManyToOne
    @JoinColumn(name = "ID_NNG_REP_REPORTE")
    private ReporteLineaArrendatarioNng reporteLineaArrendatario;

    /** Constructor. Por defecto vacío. */
    public DetalleLineaArrendatarioNng() {
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
     * ABC.
     * @return the abc
     */
    public BigDecimal getAbc() {
        return abc;
    }

    /**
     * ABC.
     * @param abc the abc to set
     */
    public void setAbc(BigDecimal abc) {
        this.abc = abc;
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
     * Arrendatario.
     * @return the arrendador
     */
    public Proveedor getArrendador() {
        return arrendador;
    }

    /**
     * Arrendatario.
     * @param arrendador the arrendador to set
     */
    public void setArrendador(Proveedor arrendador) {
        this.arrendador = arrendador;
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
     * @param numFinal the numFinal to set
     */
    public void setNumFinal(String numFinal) {
        this.numFinal = numFinal;
    }

    /**
     * Numeracion inicial.
     * @return the numInicial
     */
    public String getNumInicial() {
        return numInicial;
    }

    /**
     * Numeracion inicial.
     * @param numInicial the numInicial to set
     */
    public void setNumInicial(String numInicial) {
        this.numInicial = numInicial;
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
     * Reporte linea arrendatario.
     * @return the reporteLineaArrendatario
     */
    public ReporteLineaArrendatarioNng getReporteLineaArrendatario() {
        return reporteLineaArrendatario;
    }

    /**
     * Reporte linea arrendatario.
     * @param reporteLineaArrendatario the reporteLineaArrendatario to set
     */
    public void setReporteLineaArrendatario(ReporteLineaArrendatarioNng reporteLineaArrendatario) {
        this.reporteLineaArrendatario = reporteLineaArrendatario;
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

}
