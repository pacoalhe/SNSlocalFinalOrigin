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

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoModalidad;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.modelo.series.Nir;

/**
 * Reprsenta el detalle del reporte de una línea de arrendatario.
 */
@Entity
@Table(name = "REP_DET_LINEA_ARRENDATARIO")
@NamedQuery(name = "DetLineaArrendatario.findAll", query = "SELECT d FROM DetLineaArrendatario d")
public class DetLineaArrendatario implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** ID. */
    @Id
    @SequenceGenerator(name = "REPO_DET_REP_LIN_ARRENDATARIO_ID_GENERATOR", sequenceName = "SEQ_ID_DET_REP_LIN_ARREND",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REPO_DET_REP_LIN_ARRENDATARIO_ID_GENERATOR")
    @Column(name = "ID_REP_REPORTE", unique = true, nullable = false)
    private long id;

    /** POblacion. */
    @ManyToOne
    @JoinColumn(name = "ID_INEGI", nullable = false)
    private Poblacion poblacion;

    /** Tipo modalidad. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_MODALIDAD", nullable = false)
    private TipoModalidad tipoModalidad;

    /** Tipo red. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_RED", nullable = false)
    private TipoRed tipoRed;

    /** ABN. */
    @ManyToOne
    @JoinColumn(name = "ID_ABN", nullable = false)
    private Abn abn;

    /** NIR. */
    @ManyToOne
    @JoinColumn(name = "ID_NIR", nullable = false)
    private Nir nir;

    /** Arrendador. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_ARRENDADOR", nullable = false)
    private Proveedor arrendador;

    /** Numeracion final. */
    @Column(name = "NUM_FINAL")
    private String numFinal;

    /** Numeracion inicial. */
    @Column(name = "NUM_INICIAL")
    private String numInicial;

    /** SNA. */
    @Column(name = "ID_SERIE")
    private BigDecimal sna;

    /** Total de lineas activas. */
    @Column(name = "TOTAL_LINEAS_ACTIVAS")
    private BigDecimal totalLineasActivas;

    /** Reporte de linea arrendatario. */
    @ManyToOne
    @JoinColumn(name = "ID_REPOR")
    private LineaArrendatario lineaArrendatario;

    /** Constructor, vacio por defecto. */
    public DetLineaArrendatario() {
    }

    /**
     * ID.
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * ID.
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * POblacion.
     * @return the poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * POblacion.
     * @param poblacion the poblacion to set
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * Tipo modalidad.
     * @return the tipoModalidad
     */
    public TipoModalidad getTipoModalidad() {
        return tipoModalidad;
    }

    /**
     * Tipo modalidad.
     * @param tipoModalidad the tipoModalidad to set
     */
    public void setTipoModalidad(TipoModalidad tipoModalidad) {
        this.tipoModalidad = tipoModalidad;
    }

    /**
     * Tipo red.
     * @return the tipoRed
     */
    public TipoRed getTipoRed() {
        return tipoRed;
    }

    /**
     * Tipo red.
     * @param tipoRed the tipoRed to set
     */
    public void setTipoRed(TipoRed tipoRed) {
        this.tipoRed = tipoRed;
    }

    /**
     * ABN.
     * @return the abn
     */
    public Abn getAbn() {
        return abn;
    }

    /**
     * ABN.
     * @param abn the abn to set
     */
    public void setAbn(Abn abn) {
        this.abn = abn;
    }

    /**
     * NIR.
     * @return the nir
     */
    public Nir getNir() {
        return nir;
    }

    /**
     * NIR.
     * @param nir the nir to set
     */
    public void setNir(Nir nir) {
        this.nir = nir;
    }

    /**
     * Arrendador.
     * @return the arrendador
     */
    public Proveedor getArrendador() {
        return arrendador;
    }

    /**
     * Arrendador.
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
     * Devuelve el final de rango como un valor int.
     * @return int
     * @throws Exception si no se puede parsear el número.
     */
    public int getNumFinalAsInt() throws Exception {
        return (Integer.valueOf(numFinal).intValue());
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
     * Devuelve el inicio de rango como un valor int.
     * @return int
     * @throws Exception si no se puede parsear el número.
     */
    public int getNumInicialAsInt() throws Exception {
        return (Integer.valueOf(numInicial).intValue());
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
     * Total de lineas activas.
     * @return the totalLineasActivas
     */
    public BigDecimal getTotalLineasActivas() {
        return totalLineasActivas;
    }

    /**
     * Total de lineas activas.
     * @param totalLineasActivas the totalLineasActivas to set
     */
    public void setTotalLineasActivas(BigDecimal totalLineasActivas) {
        this.totalLineasActivas = totalLineasActivas;
    }

    /**
     * Reporte de linea arrendatario.
     * @return the lineaArrendatario
     */
    public LineaArrendatario getLineaArrendatario() {
        return lineaArrendatario;
    }

    /**
     * Reporte de linea arrendatario.
     * @param lineaArrendatario the lineaArrendatario to set
     */
    public void setLineaArrendatario(LineaArrendatario lineaArrendatario) {
        this.lineaArrendatario = lineaArrendatario;
    }
}
