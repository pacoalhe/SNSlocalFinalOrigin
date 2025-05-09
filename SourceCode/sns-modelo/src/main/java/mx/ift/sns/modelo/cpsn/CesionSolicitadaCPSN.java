package mx.ift.sns.modelo.cpsn;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import mx.ift.sns.modelo.solicitud.EstadoCesionSolicitada;

/**
 * Representa una Cesión Solicitada de CPSN. Contiene la información asociada a los códigos que se han seleccionado para
 * ceder y el estatus del proceso.
 */
@Entity
@Table(name = "CPSN_CESION_SOLICITADA")
@SequenceGenerator(name = "SEQ_ID_CPSN_CES_SOL", sequenceName = "SEQ_ID_CPSN_CES_SOL", allocationSize = 1)
@NamedQuery(name = "CesionSolicitadaCPSN.findAll", query = "SELECT s FROM CesionSolicitadaCPSN s")
public class CesionSolicitadaCPSN implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_CPSN_CES_SOL")
    @Column(name = "ID_CPSN_CESION_SOLICITADA", unique = true, nullable = false)
    private BigDecimal id;

    /** Solicitud de Cesión. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL_SOLICITUD", nullable = false)
    private SolicitudCesionCPSN solicitudCesionCPSN;

    /** Estatus de Cesión. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS_CESION", nullable = false)
    private EstadoCesionSolicitada estatus;

    /** Código de puntos de señalización nacional. */
    @Column(name = "ID_CPSN", nullable = false)
    private BigDecimal idCpsn;

    /** Tipo de Bloque de Código CPSN. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_BLOQUE")
    private TipoBloqueCPSN tipoBloqueCpsn;

    /** Fecha de Implementación. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_IMPLEMENTACION", nullable = false)
    private Date fechaImplementacion;

    /** Indicador de tiempo de implementación. */
    @Column(name = "PERIODO_IMPLEMENTACION")
    private String periodoImplementacion;

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

    /** Constructor. */
    public CesionSolicitadaCPSN() {
    }

    /**
     * Identificador.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Identificador.
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Solicitud de Cesión que ha generado la Cesión Solicitada.
     * @return SolicitudCesionCPSN
     */
    public SolicitudCesionCPSN getSolicitudCesionCPSN() {
        return solicitudCesionCPSN;
    }

    /**
     * Solicitud de Cesión que ha generado la Cesión Solicitada.
     * @param solicitudCesionCPSN SolicitudCesionCPSN
     */
    public void setSolicitudCesionCPSN(SolicitudCesionCPSN solicitudCesionCPSN) {
        this.solicitudCesionCPSN = solicitudCesionCPSN;
    }

    /**
     * Fecha de Implementación.
     * @return Date
     */
    public Date getFechaImplementacion() {
        return fechaImplementacion;
    }

    /**
     * Fecha de Implementación.
     * @param fechaImplementacion Date
     */
    public void setFechaImplementacion(Date fechaImplementacion) {
        this.fechaImplementacion = fechaImplementacion;
    }

    /**
     * Indicador de tiempo de implementación.
     * @return String
     */
    public String getPeriodoImplementacion() {
        return periodoImplementacion;
    }

    /**
     * Indicador de tiempo de implementación.
     * @param periodoImplementacion String
     */
    public void setPeriodoImplementacion(String periodoImplementacion) {
        this.periodoImplementacion = periodoImplementacion;
    }

    /**
     * Estatus de Cesión.
     * @return EstadoCesionSolicitada
     */
    public EstadoCesionSolicitada getEstatus() {
        return estatus;
    }

    /**
     * Estatus de Cesión.
     * @param estatus EstadoCesionSolicitada
     */
    public void setEstatus(EstadoCesionSolicitada estatus) {
        this.estatus = estatus;
    }

    /**
     * Código de puntos de señalización nacional.
     * @return BigDecimal
     */
    public BigDecimal getIdCpsn() {
        return idCpsn;
    }

    /**
     * Código de puntos de señalización nacional.
     * @param idCpsn BigDecimal
     */
    public void setIdCpsn(BigDecimal idCpsn) {
        this.idCpsn = idCpsn;
    }

    /**
     * Tipo de Bloque de Código CPSN.
     * @return TipoBloqueCPSN
     */
    public TipoBloqueCPSN getTipoBloqueCpsn() {
        return tipoBloqueCpsn;
    }

    /**
     * Tipo de Bloque de Código CPSN.
     * @param tipoBloqueCpsn TipoBloqueCPSN
     */
    public void setTipoBloqueCpsn(TipoBloqueCPSN tipoBloqueCpsn) {
        this.tipoBloqueCpsn = tipoBloqueCpsn;
    }

    /**
     * Binario del CPSN.
     * @return String
     */
    public String getBinario() {
        return binario;
    }

    /**
     * Binario del CPSN.
     * @param binario String
     */
    public void setBinario(String binario) {
        this.binario = binario;
    }

    /**
     * Decimal de Red del CPSN agrupado.
     * @return Integer
     */
    public Integer getDecimalRed() {
        return decimalRed;
    }

    /**
     * Decimal de Red del CPSN agrupado.
     * @param decimalRed Integer
     */
    public void setDecimalRed(Integer decimalRed) {
        this.decimalRed = decimalRed;
    }

    /**
     * Decimal de Total del CPSN individual.
     * @return Integer
     */
    public Integer getDecimalTotal() {
        return decimalTotal;
    }

    /**
     * Decimal de Total del CPSN individual.
     * @param decimalTotal Integer
     */
    public void setDecimalTotal(Integer decimalTotal) {
        this.decimalTotal = decimalTotal;
    }

    /**
     * Decimal de inicio del CPSN agrupado.
     * @return Integer
     */
    public Integer getDecimalDesde() {
        return decimalDesde;
    }

    /**
     * Decimal de inicio del CPSN agrupado.
     * @param decimalDesde Integer
     */
    public void setDecimalDesde(Integer decimalDesde) {
        this.decimalDesde = decimalDesde;
    }

    /**
     * Decimal de fin del Red del CPSN agrupado.
     * @return Integer
     */
    public Integer getDecimalHasta() {
        return decimalHasta;
    }

    /**
     * Decimal de fin del Red del CPSN agrupado.
     * @param decimalHasta Integer
     */
    public void setDecimalHasta(Integer decimalHasta) {
        this.decimalHasta = decimalHasta;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CesionSolicitadaCpsn = {");
        builder.append("id = ").append(this.id);
        builder.append(", Cpsn = ").append(idCpsn);
        if (tipoBloqueCpsn != null) {
            builder.append(", Tipo Bloque = ").append(tipoBloqueCpsn.getDescripcion());
        } else {
            builder.append(", Tipo Bloque = null");
        }
        builder.append("}");
        return builder.toString();
    }
}
