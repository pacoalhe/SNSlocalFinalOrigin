package mx.ift.sns.modelo.cpsi;

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
 * Representa una Liberación Solicitada de CPSI. Contiene la información asociada a los códigos que se han seleccionado
 * para liberar y el estatus del proceso.
 */
@Entity
@Table(name = "CPSI_LIBERACION_SOLICITADA")
@SequenceGenerator(name = "SEQ_ID_CPSI_LIB_SOL", sequenceName = "SEQ_ID_CPSI_LIB_SOL", allocationSize = 1)
@NamedQuery(name = "LiberacionSolicitadaCpsi.findAll", query = "SELECT l FROM LiberacionSolicitadaCpsi l")
public class LiberacionSolicitadaCpsi implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Id. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_CPSI_LIB_SOL")
    @Column(name = "ID_CPSI_LIBERACION_SOL", unique = true, nullable = false)
    private BigDecimal id;

    /** Fecha de Implementación de la liberación. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_IMPLEMENTACION", nullable = false)
    private Date fechaImplementacion;

    /** Identificador del código de puntos de señalización internacional. */
    @Column(name = "ID_CPSI", nullable = false)
    private BigDecimal idCpsi;

    /** Representación del código CPSI en binario (con 14 bits). */
    @Column(name = "BINARIO", length = 14)
    private String binario;

    /** Valor decimal de los 14 bits de cualquier código CPSI. */
    @Column(name = "DECIMAL_TOTAL")
    private Integer decimal;

    /** Fecha de fin de reserva de un CPSI después de una liberación. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_FIN_CUARENTENA")
    private Date fechaFinCuarentena;

    /** Representación decimal de las 3 partes que forman un código CPSI, separadas con un guion. */
    @Column(name = "FORMATO_DECIMAL", length = 7)
    private String formatoDecimal;

    /** Estatus de Liberación Solicitada de CPSI. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS_LIBERACION", nullable = false)
    private EstadoLiberacionSolicitada estatus;

    /** Indicador de tiempo de cuarentena. */
    @Column(name = "PERIODO_CUARENTENA")
    private String periodoCuarentena;

    /** Indicador de tiempo de implementación. */
    @Column(name = "PERIODO_IMPLEMENTACION")
    private String periodoImplementacion;

    /** Solicitud de Liberación CPSI asociada a la Liberación Solicitada. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL_SOLICITUD", nullable = false)
    private SolicitudLiberacionCpsi solicitudLiberacion;

    /** Constructor, por defecto vacío. */
    public LiberacionSolicitadaCpsi() {
    }

    // GETTERS & SETTERS

    /**
     * Identificador de código CPSI.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Identificador de código CPSI.
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
     * Identificador del código de puntos de señalización internacional.
     * @return BigDecimal
     */
    public BigDecimal getIdCpsi() {
        return idCpsi;
    }

    /**
     * Identificador del código de puntos de señalización internacional.
     * @param idCpsi BigDecimal
     */
    public void setIdCpsi(BigDecimal idCpsi) {
        this.idCpsi = idCpsi;
    }

    /**
     * Representación del código CPSI en binario (con 14 bits).
     * @return String
     */
    public String getBinario() {
        return binario;
    }

    /**
     * Representación del código CPSI en binario (con 14 bits).
     * @param binario String
     */
    public void setBinario(String binario) {
        this.binario = binario;
    }

    /**
     * Valor decimal de los 14 bits de cualquier código CPSI.
     * @return Integer
     */
    public Integer getDecimal() {
        return decimal;
    }

    /**
     * Valor decimal de los 14 bits de cualquier código CPSI.
     * @param decimal Integer
     */
    public void setDecimal(Integer decimal) {
        this.decimal = decimal;
    }

    /**
     * Fecha de fin de reserva de un CPSI después de una liberación.
     * @return Date
     */
    public Date getFechaFinCuarentena() {
        return fechaFinCuarentena;
    }

    /**
     * Fecha de Implementación de la liberación.
     * @param fechaFinCuarentena Date
     */
    public void setFechaFinCuarentena(Date fechaFinCuarentena) {
        this.fechaFinCuarentena = fechaFinCuarentena;
    }

    /**
     * Representación decimal de las 3 partes que forman un código CPSI, separadas con un guion.
     * @return String
     */
    public String getFormatoDecimal() {
        return formatoDecimal;
    }

    /**
     * Representación decimal de las 3 partes que forman un código CPSI, separadas con un guion.
     * @param formatoDecimal String
     */
    public void setFormatoDecimal(String formatoDecimal) {
        this.formatoDecimal = formatoDecimal;
    }

    /**
     * Estatus de Liberación Solicitada de CPSI.
     * @return EstadoLiberacionSolicitada
     */
    public EstadoLiberacionSolicitada getEstatus() {
        return estatus;
    }

    /**
     * Estatus de Liberación Solicitada de CPSI.
     * @param estatus EstadoLiberacionSolicitada
     */
    public void setEstatus(EstadoLiberacionSolicitada estatus) {
        this.estatus = estatus;
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
     * Solicitud de Liberación CPSI asociada a la Liberación Solicitada.
     * @return SolicitudLiberacionCpsi
     */
    public SolicitudLiberacionCpsi getSolicitudLiberacion() {
        return solicitudLiberacion;
    }

    /**
     * Solicitud de Liberación CPSI asociada a la Liberación Solicitada.
     * @param solicitudLiberacion SolicitudLiberacionCpsi
     */
    public void setSolicitudLiberacion(SolicitudLiberacionCpsi solicitudLiberacion) {
        this.solicitudLiberacion = solicitudLiberacion;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LiberacionSolicitadaCpsi = {");
        builder.append("id = ").append(this.id);
        builder.append(", Cpsi = ").append(idCpsi);
        builder.append("}");
        return builder.toString();
    }
}
