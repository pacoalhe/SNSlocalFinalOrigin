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
 * Representa el detalle del reporte de la línea del arrendador de numeración no geográfica.
 */
@Entity
@Table(name = "NNG_REP_LIN_ARRENDADOR_DET")
@NamedQuery(name = "DetalleLineaArrendadorNng.findAll", query = "SELECT d FROM DetalleLineaArrendadorNng d")
public class DetalleLineaArrendadorNng implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** ID detalle. */
    @Id
    @SequenceGenerator(name = "SEQ_ID_NNG_RDLIN_ARR", sequenceName = "SEQ_ID_NNG_RDLIN_ARR", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_NNG_RDLIN_ARR")
    @Column(name = "ID_NNG_RDLIN_ARR")
    private BigDecimal id;

    /** ABC. */
    private BigDecimal abc;

    /** Referencia a la clave de Servicio asociada a la numeración. */
    @ManyToOne
    @JoinColumn(name = "ID_CLAVE_SERVICIO", nullable = false, insertable = false, updatable = false)
    private ClaveServicio claveServicio;

    /** Arrendatario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_ARRENDATARIO")
    private Proveedor arrendatario;

    /** Numeracion final. */
    @Column(name = "NUM_FINAL")
    private String numFinal;

    /** Numeracion inicial. */
    @Column(name = "NUM_INICIAL")
    private String numInicial;

    /** SNA. */
    private BigDecimal sna;

    /** Reporte linea arrendador. */
    @ManyToOne
    @JoinColumn(name = "ID_NNG_REP_REPORTE")
    private ReporteLineaArrendadorNng reporteLineaArrendador;

    /** Constructor. Por defecto vacío. */
    public DetalleLineaArrendadorNng() {
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
     * ABC.
     * @return the claveServicio
     */
    public ClaveServicio getClaveServicio() {
        return claveServicio;
    }

    /**
     * ABC.
     * @param claveServicio the claveServicio to set
     */
    public void setClaveServicio(ClaveServicio claveServicio) {
        this.claveServicio = claveServicio;
    }

    /**
     * Arrendatario.
     * @return the arrendatario
     */
    public Proveedor getArrendatario() {
        return arrendatario;
    }

    /**
     * Arrendatario.
     * @param arrendatario the arrendatario to set
     */
    public void setArrendatario(Proveedor arrendatario) {
        this.arrendatario = arrendatario;
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
     * Reporte linea arrendador.
     * @return the reporteLineaArrendador
     */
    public ReporteLineaArrendadorNng getReporteLineaArrendador() {
        return reporteLineaArrendador;
    }

    /**
     * Reporte linea arrendador.
     * @param reporteLineaArrendador the reporteLineaArrendador to set
     */
    public void setReporteLineaArrendador(ReporteLineaArrendadorNng reporteLineaArrendador) {
        this.reporteLineaArrendador = reporteLineaArrendador;
    }

}
