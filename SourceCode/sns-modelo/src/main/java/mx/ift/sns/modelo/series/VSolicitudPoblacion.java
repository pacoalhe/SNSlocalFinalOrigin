/**
 * .
 */
package mx.ift.sns.modelo.series;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;

/**
 * Vista sobre Poblaciones y Trámites.
 */
@Entity
@Table(name = "SOLICITUD_POBLACION_VM")
public class VSolicitudPoblacion implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @Column(name = "ID_SOLICITUD_POBLACION")
    private BigDecimal id;

    /** Solicitud. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL_SOLICITUD", nullable = false)
    private Solicitud solicitud;

    /** Población. */
    @Column(name = "ID_INEGI", insertable = false, updatable = false)
    private String inegi;

    /** Proveedor de Servicios de Telecomunicación.. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_SOLICITANTE", nullable = false)
    private Proveedor proveedor;

    /** Estatus de solicitud. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTADO_SOLICITUD", nullable = false)
    private EstadoSolicitud estatus;

    /** Fecha de solicitud. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_SOLICITUD")
    private Date fechaSolicitud;

    /** Fecha de asignación. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_ASIGNACION")
    private Date fechaAsignacion;

    /** Oficio. */
    @Column(name = "NUM_OFICIO", length = 55)
    private String numOficio;

    /** Tipo de solicitud. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_TIPO_SOLICITUD", nullable = false)
    private TipoSolicitud tipoSolicitud;

    /** Municipio. */
    @Column(name = "ID_MUNICIPIO")
    private String codMunicipio;

    /** Estado. */
    @Column(name = "ID_ESTADO")
    private String codEstado;

    /** Constructor vacio. */
    public VSolicitudPoblacion() {
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
     * Solicitud.
     * @return Solicitud
     */
    public Solicitud getSolicitud() {
        return solicitud;
    }

    /**
     * Solicitud.
     * @param solicitud Solicitud
     */
    public void setSolicitud(Solicitud solicitud) {
        this.solicitud = solicitud;
    }

    /**
     * Proveedor de Servicios de Telecomunicación.
     * @return Proveedor
     */
    public Proveedor getProveedor() {
        return proveedor;
    }

    /**
     * Proveedor de Servicios de Telecomunicación.
     * @param proveedor Proveedor
     */
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * Estatus de solicitud.
     * @return EstadoSolicitud
     */
    public EstadoSolicitud getEstatus() {
        return estatus;
    }

    /**
     * Estatus de solicitud.
     * @param estatus EstadoSolicitud
     */
    public void setEstatus(EstadoSolicitud estatus) {
        this.estatus = estatus;
    }

    /**
     * Fecha de solicitud.
     * @return Date
     */
    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    /**
     * Fecha de solicitud.
     * @param fechaSolicitud Date
     */
    public void setFechaSolicitud(Date fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    /**
     * Fecha de asignación.
     * @return Date
     */
    public Date getFechaAsignacion() {
        return fechaAsignacion;
    }

    /**
     * Fecha de asignación.
     * @param fechaAsignacion Date
     */
    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    /**
     * Oficio.
     * @return String
     */
    public String getNumOficio() {
        return numOficio;
    }

    /**
     * Oficio.
     * @param numOficio String
     */
    public void setNumOficio(String numOficio) {
        this.numOficio = numOficio;
    }

    /**
     * Tipo de solicitud.
     * @return TipoSolicitud
     */
    public TipoSolicitud getTipoSolicitud() {
        return tipoSolicitud;
    }

    /**
     * Tipo de solicitud.
     * @param tipoSolicitud TipoSolicitud
     */
    public void setTipoSolicitud(TipoSolicitud tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    /**
     * Población.
     * @return String
     */
    public String getInegi() {
        return inegi;
    }

    /**
     * Población.
     * @param inegi String
     */
    public void setInegi(String inegi) {
        this.inegi = inegi;
    }

    /**
     * Municipio.
     * @return String
     */
    public String getCodMunicipio() {
        return codMunicipio;
    }

    /**
     * Municipio.
     * @param codMunicipio String
     */
    public void setCodMunicipio(String codMunicipio) {
        this.codMunicipio = codMunicipio;
    }

    /**
     * Estado.
     * @return String
     */
    public String getCodEstado() {
        return codEstado;
    }

    /**
     * Estado.
     * @param codEstado String
     */
    public void setCodEstado(String codEstado) {
        this.codEstado = codEstado;
    }

    @Override
    public int hashCode() {
        return (id != null)
                ? (this.getClass().hashCode() + id.hashCode())
                : super.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof VSolicitudPoblacion) && (id != null)
                ? id.equals(((VSolicitudPoblacion) other).id)
                : (other == this);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("identificador={ ");
        b.append(id);
        b.append(" '");
        return b.toString();
    }
}
