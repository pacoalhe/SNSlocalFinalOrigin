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

import mx.ift.sns.modelo.solicitud.EstadoLiberacionSolicitada;

/**
 * Representa una Liberación Solicitada de CPSN. Contiene la información asociada a los códigos que se han seleccionado
 * para liberar y el estatus del proceso.
 */
@Entity
@Table(name = "CPSN_LIBERACION_SOLICITADA")
@SequenceGenerator(name = "SEQ_ID_CPSN_LIB_SOL", sequenceName = "SEQ_ID_CPSN_LIB_SOL", allocationSize = 1)
@NamedQuery(name = "LiberacionSolicitadaCpsn.findAll", query = "SELECT l FROM LiberacionSolicitadaCpsn l")
public class LiberacionSolicitadaCpsn implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Id. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_CPSN_LIB_SOL")
    @Column(name = "ID_CPSN_LIBERACION_SOL", unique = true, nullable = false)
    private BigDecimal id;

    /** Fecha de Implementación de la liberación. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_IMPLEMENTACION", nullable = false)
    private Date fechaImplementacion;

    /** Código de puntos de señalización nacional. */
    @Column(name = "ID_CPSN", nullable = false)
    private BigDecimal idCpsn;

    /** Tipo de Bloque de Código CPSN. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_BLOQUE")
    private TipoBloqueCPSN tipoBloqueCpsn;

    /** Estatus de Liberación Solicitada de CPSN. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS_LIBERACION", nullable = false)
    private EstadoLiberacionSolicitada estatus;

    /** Indicador de tiempo de cuarentena. */
    @Column(name = "PERIODO_CUARENTENA")
    private String periodoCuarentena;

    /** Indicador de tiempo de implementación. */
    @Column(name = "PERIODO_IMPLEMENTACION")
    private String periodoImplementacion;

    /** Solicitud de Liberación CPSN asociada a la Liberación Solicitada. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL_SOLICITUD", nullable = false)
    private SolicitudLiberacionCpsn solicitudLiberacion;

    /** Fecha de fin de reserva de un CPSN después de una liberación. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_FIN_CUARENTENA")
    private Date fechaFinCuarentena;

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

    /** Constructor, por defecto vacío. */
    public LiberacionSolicitadaCpsn() {
    }

    /**
     * Identificador de la Liberación Solicitada de CPSN.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Identificador de la Liberación Solicitada de CPSN.
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Fecha de Implementación de la liberación.
     * @return Date
     */
    public Date getFechaImplementacion() {
        return fechaImplementacion;
    }

    /**
     * Fecha de Implementación de la liberación.
     * @param fechaImplementacion Date
     */
    public void setFechaImplementacion(Date fechaImplementacion) {
        this.fechaImplementacion = fechaImplementacion;
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
     * Estatus de Liberación Solicitada de CPSN.
     * @return EstadoLiberacionSolicitada
     */
    public EstadoLiberacionSolicitada getEstatus() {
        return estatus;
    }

    /**
     * Estatus de Liberación Solicitada de CPSN.
     * @param estatus EstadoLiberacionSolicitada
     */
    public void setEstatus(EstadoLiberacionSolicitada estatus) {
        this.estatus = estatus;
    }

    /**
     * Solicitud de Liberación CPSN asociada a la Liberación Solicitada.
     * @return SolicitudLiberacionCpsn
     */
    public SolicitudLiberacionCpsn getSolicitudLiberacion() {
        return solicitudLiberacion;
    }

    /**
     * Solicitud de Liberación CPSN asociada a la Liberación Solicitada.
     * @param solicitudLiberacion SolicitudLiberacionCpsn
     */
    public void setSolicitudLiberacion(SolicitudLiberacionCpsn solicitudLiberacion) {
        this.solicitudLiberacion = solicitudLiberacion;
    }

    /**
     * Indicador de tiempo de cuarentena.
     * @return String
     */
    public String getPeriodoCuarentena() {
        return periodoCuarentena;
    }

    /**
     * Indicador de tiempo de cuarentena.
     * @param periodoCuarentena String
     */
    public void setPeriodoCuarentena(String periodoCuarentena) {
        this.periodoCuarentena = periodoCuarentena;
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
     * Fecha de fin de reserva de un CPSN después de una liberación.
     * @return the fechaFinCuarentena
     */
    public Date getFechaFinCuarentena() {
        return fechaFinCuarentena;
    }

    /**
     * Fecha de fin de reserva de un CPSN después de una liberación.
     * @param fechaFinCuarentena the fechaFinCuarentena to set
     */
    public void setFechaFinCuarentena(Date fechaFinCuarentena) {
        this.fechaFinCuarentena = fechaFinCuarentena;
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LiberacionSolicitadaCpsn = {");
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
