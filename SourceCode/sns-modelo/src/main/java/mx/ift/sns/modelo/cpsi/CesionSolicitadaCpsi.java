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

import mx.ift.sns.modelo.solicitud.EstadoCesionSolicitada;

/**
 * Representa una Cesión Solicitada de CPSI. Contiene la información asociada a los códigos que se han seleccionado para
 * ceder y el estatus del proceso.
 */
@Entity
@Table(name = "CPSI_CESION_SOLICITADA")
@SequenceGenerator(name = "SEQ_ID_CPSI_CES_SOL", sequenceName = "SEQ_ID_CPSI_CES_SOL", allocationSize = 1)
@NamedQuery(name = "CesionSolicitadaCpsi.findAll", query = "SELECT c FROM CesionSolicitadaCpsi c")
public class CesionSolicitadaCpsi implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_CPSI_CES_SOL")
    @Column(name = "ID_CPSI_CESION_SOLICITADA")
    private BigDecimal id;

    /** Solicitud de Cesión. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL_SOLICITUD")
    private SolicitudCesionCpsi solicitudCesion;

    /** Representación del código CPSI en binario (con 14 bits). */
    @Column(name = "BINARIO", length = 14)
    private String binario;

    /** Fecha de Implementación de la cesión. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_IMPLEMENTACION", nullable = false)
    private Date fechaImplementacion;

    /** Representación decimal de las 3 partes que forman un código CPSI, separadas con un guion. */
    @Column(name = "FORMATO_DECIMAL", length = 7)
    private String formatoDecimal;

    /** Identificador del código de puntos de señalización internacional. */
    @Column(name = "ID_CPSI", nullable = false)
    private BigDecimal idCpsi;

    /** Estatus de Cesión Solicitada de CPSI. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS_CESION")
    private EstadoCesionSolicitada estatus;

    /** Indicador de tiempo de implementación. */
    @Column(name = "PERIODO_IMPLEMENTACION")
    private String periodoImplementacion;

    /** Constructor, por defecto vacío. */
    public CesionSolicitadaCpsi() {
    }

    // GETTERS & SETTERS

    /**
     * Identificador de CPSI.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Identificador de CPSI.
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Solicitud de Cesión asociada al CPSI.
     * @return SolicitudCesionCpsi
     */
    public SolicitudCesionCpsi getSolicitudCesion() {
        return solicitudCesion;
    }

    /**
     * Solicitud de Cesión asociada al CPSI.
     * @param solicitudCesion SolicitudCesionCpsi
     */
    public void setSolicitudCesion(SolicitudCesionCpsi solicitudCesion) {
        this.solicitudCesion = solicitudCesion;
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
     * Fecha de Implementación de la cesión.
     * @return Date
     */
    public Date getFechaImplementacion() {
        return fechaImplementacion;
    }

    /**
     * Fecha de Implementación de la cesión.
     * @param fechaImplementacion Date
     */
    public void setFechaImplementacion(Date fechaImplementacion) {
        this.fechaImplementacion = fechaImplementacion;
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
     * Estatus de Cesión Solicitada de CPSI.
     * @return EstadoCesionSolicitada
     */
    public EstadoCesionSolicitada getEstatus() {
        return estatus;
    }

    /**
     * Estatus de Cesión Solicitada de CPSI.
     * @param estatus EstadoCesionSolicitada
     */
    public void setEstatus(EstadoCesionSolicitada estatus) {
        this.estatus = estatus;
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CesionSolicitadaCpsi = {");
        builder.append("id = ").append(this.id);
        builder.append(", Cpsi = ").append(idCpsi);
        builder.append("}");
        return builder.toString();
    }
}
