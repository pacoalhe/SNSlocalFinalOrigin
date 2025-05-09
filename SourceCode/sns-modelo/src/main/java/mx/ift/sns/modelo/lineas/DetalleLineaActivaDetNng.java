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
 * Representa el detalle del reporte de líneas activas de numeración no geográfica.
 */
@Entity
@Table(name = "NNG_REP_DET_LIN_ACT_DET")
@NamedQuery(name = "DetalleLineaActivaDetNng.findAll", query = "SELECT d FROM DetalleLineaActivaDetNng d")
public class DetalleLineaActivaDetNng implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** ID detalle. */
    @Id
    @SequenceGenerator(name = "SEQ_ID_NNG_RDET_LACT_DET", sequenceName = "SEQ_ID_NNG_RDET_LACT_DET", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_NNG_RDET_LACT_DET")
    @Column(name = "ID_NNG_RDET_LACT_DET")
    private BigDecimal id;

    /** Referencia a la clave de Servicio asociada a la numeración. */
    @ManyToOne
    @JoinColumn(name = "ID_CLAVE_SERVICIO", nullable = false, insertable = false, updatable = false)
    private ClaveServicio claveServicio;

    /** Numeracion final. */
    @Column(name = "NUM_FINAL")
    private String numFinal;

    /** Numeracion inicial. */
    @Column(name = "NUM_INICIAL")
    private String numInicial;

    /** Numeracion en servicio. */
    @Column(name = "NUM_SERVICIO")
    private BigDecimal numServicio;

    /** Numeracion en cuerentena. */
    @Column(name = "NUM_CUARENTENA")
    private BigDecimal numCuarentena;

    /** Numeracion portada. */
    @Column(name = "NUM_PORTADA")
    private BigDecimal numPortada;

    /** SNA. */
    private BigDecimal sna;

    /** Reporte de lineas activas detallado. */
    @ManyToOne
    @JoinColumn(name = "ID_NNG_REP_REPORTE")
    private ReporteLineaActivaDetNng reporteLineasActivasDet;

    /** Constructor. Por defecto vacío. */
    public DetalleLineaActivaDetNng() {
    }

    /**
     * Numeracion en servicio.
     * @return the numServicio
     */
    public BigDecimal getNumServicio() {
        return numServicio;
    }

    /**
     * Numeracion en servicio.
     * @param numServicio the numServicio to set
     */
    public void setNumServicio(BigDecimal numServicio) {
        this.numServicio = numServicio;
    }

    /**
     * Numeracion en cuerentena.
     * @return the numCuarentena
     */
    public BigDecimal getNumCuarentena() {
        return numCuarentena;
    }

    /**
     * Numeracion en cuerentena.
     * @param numCuarentena the numCuarentena to set
     */
    public void setNumCuarentena(BigDecimal numCuarentena) {
        this.numCuarentena = numCuarentena;
    }

    /**
     * Numeracion portada.
     * @return the numPortada
     */
    public BigDecimal getNumPortada() {
        return numPortada;
    }

    /**
     * Numeracion portada.
     * @param numPortada the numPortada to set
     */
    public void setNumPortada(BigDecimal numPortada) {
        this.numPortada = numPortada;
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
     * Reporte de lineas activas detallado.
     * @return the reporteLineasActivasDet
     */
    public ReporteLineaActivaDetNng getReporteLineasActivasDet() {
        return reporteLineasActivasDet;
    }

    /**
     * Reporte de lineas activas detallado.
     * @param reporteLineasActivasDet the reporteLineasActivasDet to set
     */
    public void setReporteLineasActivasDet(ReporteLineaActivaDetNng reporteLineasActivasDet) {
        this.reporteLineasActivasDet = reporteLineasActivasDet;
    }

}
