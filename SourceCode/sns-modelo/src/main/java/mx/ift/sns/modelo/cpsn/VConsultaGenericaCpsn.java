package mx.ift.sns.modelo.cpsn;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import mx.ift.sns.modelo.solicitud.EstadoSolicitud;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Representa la vista de consulta genérica de CPSN.
 */
@Entity
@Cacheable(false)
@ReadOnly
@Table(name = "CONSULTA_GENERICA_CPSN_VM")
@NamedQuery(name = "VConsultaGenericaCpsn.findAll", query = "SELECT cgn FROM VConsultaGenericaCpsn cgn")
public class VConsultaGenericaCpsn implements Serializable {
    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @Column(name = "ID")
    private BigDecimal id;

    /** Consecutivo de la solicitud. */
    @Column(name = "CONSECUTIVO")
    private BigDecimal consecutivo;

    /** Identificador del tipo de solicitud. */
    @Column(name = "ID_TIPO_SOLICITUD")
    private Integer idTipoSolicitud;

    /** Desscripción del tipo de trámite. */
    @Column(name = "TIPO_TRAMITE")
    private String tipoTramite;

    /** Identificador del pst origen. */
    @Column(name = "ID_PST_ORIGEN")
    private BigDecimal idPstOrigen;

    /** Descripción del pst origen. */
    @Column(name = "PST_ORIGEN")
    private String pstOrigen;

    /** Identificador del pst destino. */
    @Column(name = "ID_PST_DESTINO")
    private BigDecimal idPstDestino;

    /** Descripción del pst destino. */
    @Column(name = "PST_DESTINO")
    private String pstDestino;

    /** Fecha de la solicitud. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_SOLICITUD")
    private Date fechaSolicitud;

    /** Fecha de implementación. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_IMPLEMENTACION")
    private Date fechaImplementacion;

    /** Número de oficio del solicitante. */
    @Column(name = "NUM_OFICIO_SOLICITANTE", length = 55)
    private String numOficioSolicitante;

    /** Estatus de la solicitud. */
    @JoinColumn(name = "ESTATUS_SOLICITUD")
    private EstadoSolicitud estatusSolicitud;

    /** Constructor. */
    public VConsultaGenericaCpsn() {
    }

    /**
     * Consecutivo de la solicitud.
     * @return the consecutivo
     */
    public BigDecimal getConsecutivo() {
        return consecutivo;
    }

    /**
     * Consecutivo de la solicitud.
     * @param consecutivo the consecutivo to set
     */
    public void setConsecutivo(BigDecimal consecutivo) {
        this.consecutivo = consecutivo;
    }

    /**
     * Identificador del tipo de solicitud.
     * @return the idTipoSolicitud
     */
    public Integer getIdTipoSolicitud() {
        return idTipoSolicitud;
    }

    /**
     * Identificador del tipo de solicitud.
     * @param idTipoSolicitud the idTipoSolicitud to set
     */
    public void setIdTipoSolicitud(Integer idTipoSolicitud) {
        this.idTipoSolicitud = idTipoSolicitud;
    }

    /**
     * Desscripción del tipo de trámite.
     * @return the tipoTramite
     */
    public String getTipoTramite() {
        return tipoTramite;
    }

    /**
     * Desscripción del tipo de trámite.
     * @param tipoTramite the tipoTramite to set
     */
    public void setTipoTramite(String tipoTramite) {
        this.tipoTramite = tipoTramite;
    }

    /**
     * @return the idPstOrigen
     */
    public BigDecimal getIdPstOrigen() {
        return idPstOrigen;
    }

    /**
     * Identificador del pst origen.
     * @param idPstOrigen the idPstOrigen to set
     */
    public void setIdPstOrigen(BigDecimal idPstOrigen) {
        this.idPstOrigen = idPstOrigen;
    }

    /**
     * Descripción del pst origen.
     * @return the pstOrigen
     */
    public String getPstOrigen() {
        return pstOrigen;
    }

    /**
     * Descripción del pst origen.
     * @param pstOrigen the pstOrigen to set
     */
    public void setPstOrigen(String pstOrigen) {
        this.pstOrigen = pstOrigen;
    }

    /**
     * @return the idPstDestino
     */
    public BigDecimal getIdPstDestino() {
        return idPstDestino;
    }

    /**
     * Identificador del pst destino.
     * @param idPstDestino the idPstDestino to set
     */
    public void setIdPstDestino(BigDecimal idPstDestino) {
        this.idPstDestino = idPstDestino;
    }

    /**
     * Identificador del pst destino.
     * @return the pstDestino
     */
    public String getPstDestino() {
        return pstDestino;
    }

    /**
     * Descripción del pst destino.
     * @param pstDestino the pstDestino to set
     */
    public void setPstDestino(String pstDestino) {
        this.pstDestino = pstDestino;
    }

    /**
     * Descripción del pst destino.
     * @return the fechaSolicitud
     */
    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    /**
     * Descripción del pst destino.
     * @param fechaSolicitud the fechaSolicitud to set
     */
    public void setFechaSolicitud(Date fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    /**
     * Fecha de implementación.
     * @return the fechaImplementacion
     */
    public Date getFechaImplementacion() {
        return fechaImplementacion;
    }

    /**
     * Fecha de implementación.
     * @param fechaImplementacion the fechaImplementacion to set
     */
    public void setFechaImplementacion(Date fechaImplementacion) {
        this.fechaImplementacion = fechaImplementacion;
    }

    /**
     * Número de oficio del solicitante.
     * @return the numOficioSolicitante
     */
    public String getNumOficioSolicitante() {
        return numOficioSolicitante;
    }

    /**
     * Número de oficio del solicitante.
     * @param numOficioSolicitante the numOficioSolicitante to set
     */
    public void setNumOficioSolicitante(String numOficioSolicitante) {
        this.numOficioSolicitante = numOficioSolicitante;
    }

    /**
     * Estatus de la solicitud.
     * @return the estatusSolicitud
     */
    public EstadoSolicitud getEstatusSolicitud() {
        return estatusSolicitud;
    }

    /**
     * Estatus de la solicitud.
     * @param estatusSolicitud the estatusSolicitud to set
     */
    public void setEstatusSolicitud(EstadoSolicitud estatusSolicitud) {
        this.estatusSolicitud = estatusSolicitud;
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

}
