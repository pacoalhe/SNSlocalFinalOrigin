package mx.ift.sns.modelo.cpsi;

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
 * Representa la vista de consulta de CPSI.
 */
@Entity
@Cacheable(false)
@ReadOnly
@Table(name = "CONSULTA_GENERICA_CPSI_VM")
@NamedQuery(name = "VConsultaGenericaCpsI.findAll", query = "SELECT cgi FROM VConsultaGenericaCpsi cgi")
public class VConsultaGenericaCpsi implements Serializable {

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

    /** Identificador del Proveedor de Servicios de origen. */
    @Column(name = "ID_PST_ORIGEN")
    private BigDecimal idPstOrigen;

    /** Descripción del Proveedor de Servicios de origen. */
    @Column(name = "PST_ORIGEN")
    private String pstOrigen;

    /** Identificador del Proveedor de Servicios de destino. */
    @Column(name = "ID_PST_DESTINO")
    private BigDecimal idPstDestino;

    /** Descripción del Proveedor de Servicios de destino. */
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
    public VConsultaGenericaCpsi() {
    }

    /**
     * Consecutivo de la solicitud.
     * @return BigDecimal
     */
    public BigDecimal getConsecutivo() {
        return consecutivo;
    }

    /**
     * Consecutivo de la solicitud.
     * @param consecutivo BigDecimal
     */
    public void setConsecutivo(BigDecimal consecutivo) {
        this.consecutivo = consecutivo;
    }

    /**
     * Identificador del tipo de solicitud.
     * @return Integer
     */
    public Integer getIdTipoSolicitud() {
        return idTipoSolicitud;
    }

    /**
     * Identificador del tipo de solicitud.
     * @param idTipoSolicitud Integer
     */
    public void setIdTipoSolicitud(Integer idTipoSolicitud) {
        this.idTipoSolicitud = idTipoSolicitud;
    }

    /**
     * Desscripción del tipo de trámite.
     * @return String
     */
    public String getTipoTramite() {
        return tipoTramite;
    }

    /**
     * Desscripción del tipo de trámite.
     * @param tipoTramite String
     */
    public void setTipoTramite(String tipoTramite) {
        this.tipoTramite = tipoTramite;
    }

    /**
     * Identificador del Proveedor de Servicios de origen.
     * @return BigDecimal
     */
    public BigDecimal getIdPstOrigen() {
        return idPstOrigen;
    }

    /**
     * Identificador del Proveedor de Servicios de origen.
     * @param idPstOrigen BigDecimal
     */
    public void setIdPstOrigen(BigDecimal idPstOrigen) {
        this.idPstOrigen = idPstOrigen;
    }

    /**
     * Descripción del Proveedor de Servicios de origen.
     * @return String
     */
    public String getPstOrigen() {
        return pstOrigen;
    }

    /**
     * Descripción del Proveedor de Servicios de origen.
     * @param pstOrigen String
     */
    public void setPstOrigen(String pstOrigen) {
        this.pstOrigen = pstOrigen;
    }

    /**
     * Identificador del Proveedor de Servicios de destino.
     * @return BigDecimal
     */
    public BigDecimal getIdPstDestino() {
        return idPstDestino;
    }

    /**
     * Identificador del Proveedor de Servicios de destino.
     * @param idPstDestino BigDecimal
     */
    public void setIdPstDestino(BigDecimal idPstDestino) {
        this.idPstDestino = idPstDestino;
    }

    /**
     * Descripción del Proveedor de Servicios de destino.
     * @return String
     */
    public String getPstDestino() {
        return pstDestino;
    }

    /**
     * Descripción del Proveedor de Servicios de destino.
     * @param pstDestino String
     */
    public void setPstDestino(String pstDestino) {
        this.pstDestino = pstDestino;
    }

    /**
     * Fecha de la solicitud.
     * @return Date
     */
    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    /**
     * Fecha de la solicitud.
     * @param fechaSolicitud Date
     */
    public void setFechaSolicitud(Date fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    /**
     * Fecha de implementación.
     * @return Date
     */
    public Date getFechaImplementacion() {
        return fechaImplementacion;
    }

    /**
     * Fecha de implementación.
     * @param fechaImplementacion Date
     */
    public void setFechaImplementacion(Date fechaImplementacion) {
        this.fechaImplementacion = fechaImplementacion;
    }

    /**
     * Número de oficio del solicitante.
     * @return String
     */
    public String getNumOficioSolicitante() {
        return numOficioSolicitante;
    }

    /**
     * Número de oficio del solicitante.
     * @param numOficioSolicitante String
     */
    public void setNumOficioSolicitante(String numOficioSolicitante) {
        this.numOficioSolicitante = numOficioSolicitante;
    }

    /**
     * Estatus de la solicitud.
     * @return EstadoSolicitud
     */
    public EstadoSolicitud getEstatusSolicitud() {
        return estatusSolicitud;
    }

    /**
     * Estatus de la solicitud.
     * @param estatusSolicitud EstadoSolicitud
     */
    public void setEstatusSolicitud(EstadoSolicitud estatusSolicitud) {
        this.estatusSolicitud = estatusSolicitud;
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

}
