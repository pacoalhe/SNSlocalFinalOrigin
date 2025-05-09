package mx.ift.sns.modelo.cpsn;

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

import mx.ift.sns.modelo.cps.EstatusAsignacionCps;

/**
 * Representa un bloque asignado.
 */
@Entity
@Table(name = "CPSN_BLOQUE_ASIGNADO")
@SequenceGenerator(name = "SEQ_ID_BLOQUE_ASIG_CPSN", sequenceName = "SEQ_ID_BLOQUE_ASIG_CPSN", allocationSize = 1)
@NamedQuery(name = "NumeracionAsignadaCpsn.findAll", query = "SELECT n FROM NumeracionAsignadaCpsn n")
public class NumeracionAsignadaCpsn implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_BLOQUE_ASIG_CPSN")
    @Column(name = "ID_CODIGO_ASIGNADO", unique = true, nullable = false)
    private BigDecimal id;

    /** Solicitud. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL_SOLICITUD", nullable = false)
    private SolicitudAsignacionCpsn solicitudAsignacion;

    /** Código de puntos de señalización nacional. */
    @Column(name = "ID_CPSN", nullable = false)
    private BigDecimal idCpsn;

    /** Tipo de Bloque de Código CPSN. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_BLOQUE")
    private TipoBloqueCPSN tipoBloqueCpsn;

    /** Binario del CPSN. */
    @Column(name = "BINARIO", length = 14)
    private String binario;

    /** Decimal de Red del CPSN agrupado. */
    @Column(name = "DECIMAL_RED", length = 5)
    private Integer decimalRed;

    /** Decimal de Total del CPSN individual. */
    @Column(name = "DECIMAL_TOTAL", length = 5)
    private Integer decimalTotal;

    /** Decimal de inicio del CPSN agrupado. */
    @Column(name = "DECIMAL_DESDE", length = 5)
    private Integer decimalDesde;

    /** Decimal de fin del Red del CPSN agrupado. */
    @Column(name = "DECIMAL_HASTA", length = 5)
    private Integer decimalHasta;

    /** Estatus de Cesión. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS_ASIGNACION", nullable = false)
    private EstatusAsignacionCps estatus;

    /** Constructor vacio por defecto. */
    public NumeracionAsignadaCpsn() {
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
     * Solicitud.
     * @return the solicitudAsignacion
     */
    public SolicitudAsignacionCpsn getSolicitudAsignacion() {
        return solicitudAsignacion;
    }

    /**
     * Solicitud.
     * @param solicitudAsignacion the solicitudAsignacion to set
     */
    public void setSolicitudAsignacion(SolicitudAsignacionCpsn solicitudAsignacion) {
        this.solicitudAsignacion = solicitudAsignacion;
    }

    /**
     * Código de puntos de señalización nacional.
     * @return the idCpsn
     */
    public BigDecimal getIdCpsn() {
        return idCpsn;
    }

    /**
     * Código de puntos de señalización nacional.
     * @param idCpsn the idCpsn to set
     */
    public void setIdCpsn(BigDecimal idCpsn) {
        this.idCpsn = idCpsn;
    }

    /**
     * Tipo de Bloque de Código CPSN.
     * @return the tipoBloqueCpsn
     */
    public TipoBloqueCPSN getTipoBloqueCpsn() {
        return tipoBloqueCpsn;
    }

    /**
     * Tipo de Bloque de Código CPSN.
     * @param tipoBloqueCpsn the tipoBloqueCpsn to set
     */
    public void setTipoBloqueCpsn(TipoBloqueCPSN tipoBloqueCpsn) {
        this.tipoBloqueCpsn = tipoBloqueCpsn;
    }

    /**
     * Binario del CPSN.
     * @return the binario
     */
    public String getBinario() {
        return binario;
    }

    /**
     * Binario del CPSN.
     * @param binario the binario to set
     */
    public void setBinario(String binario) {
        this.binario = binario;
    }

    /**
     * Decimal de Red del CPSN agrupado.
     * @return the decimalRed
     */
    public Integer getDecimalRed() {
        return decimalRed;
    }

    /**
     * Decimal de Red del CPSN agrupado.
     * @param decimalRed the decimalRed to set
     */
    public void setDecimalRed(Integer decimalRed) {
        this.decimalRed = decimalRed;
    }

    /**
     * Decimal de Total del CPSN individual.
     * @return the decimalTotal
     */
    public Integer getDecimalTotal() {
        return decimalTotal;
    }

    /**
     * Decimal de Total del CPSN individual.
     * @param decimalTotal the decimalTotal to set
     */
    public void setDecimalTotal(Integer decimalTotal) {
        this.decimalTotal = decimalTotal;
    }

    /**
     * Decimal de inicio del CPSN agrupado.
     * @return the decimalDesde
     */
    public Integer getDecimalDesde() {
        return decimalDesde;
    }

    /**
     * Decimal de inicio del CPSN agrupado.
     * @param decimalDesde the decimalDesde to set
     */
    public void setDecimalDesde(Integer decimalDesde) {
        this.decimalDesde = decimalDesde;
    }

    /**
     * Decimal de fin del Red del CPSN agrupado.
     * @return the decimalHasta
     */
    public Integer getDecimalHasta() {
        return decimalHasta;
    }

    /**
     * Decimal de fin del Red del CPSN agrupado.
     * @param decimalHasta the decimalHasta to set
     */
    public void setDecimalHasta(Integer decimalHasta) {
        this.decimalHasta = decimalHasta;
    }

    /**
     * Estatus de Cesión.
     * @return the estatus
     */
    public EstatusAsignacionCps getEstatus() {
        return estatus;
    }

    /**
     * Estatus de Cesión.
     * @param estatus the estatus to set
     */
    public void setEstatus(EstatusAsignacionCps estatus) {
        this.estatus = estatus;
    }

}
