package mx.ift.sns.modelo.nng;

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

import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.solicitud.EstadoRedistribucionSolicitada;

/**
 * Representa un movimiento sobre Redistribuciones de Rangos de Numeración No Geográfica. Contiene información sobre
 * inserciones, modificaciones y eliminaciones de redistribuciones de rangos..
 */
@Entity
@Table(name = "NNG_HIST_REDISTRIBUCION")
@SequenceGenerator(name = "SEQ_ID_NNG_HIST_REDIST", sequenceName = "SEQ_ID_NNG_HIST_REDIST", allocationSize = 1)
@NamedQuery(name = "HistoricoRedistribucionNng.findAll", query = "SELECT h FROM HistoricoRedistribucionNng h")
public class HistoricoRedistribucionNng implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Acción de Inserción en el histórico. */
    public static final String ACCION_INSERCION = "A";

    /** Acción de Modificación en el histórico. */
    public static final String ACCION_MODIFICACION = "M";

    /** Acción de Eliminado en el histórico. */
    public static final String ACCION_BORRADO = "B";

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_NNG_HIST_REDIST")
    @Column(name = "ID_NNG_HIST_REDIST", unique = true, nullable = false)
    private BigDecimal id;

    /** Código ABC. */
    @Column(name = "ABC", precision = 3)
    private BigDecimal abc;

    /** Código BCD. */
    @Column(name = "BCD", precision = 3)
    private BigDecimal bcd;

    /** Tipo de movimiento. */
    @Column(name = "ACCION", nullable = false, length = 1)
    private String accion;

    /** Fecha del movimiento. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_ACCION")
    private Date fechaAccion;

    /** Cliente inicial de la numeración redistribuida. */
    @Column(name = "CLIENTE", length = 128)
    private String cliente;

    /** Consecutivo de la Solicitud de Asginación original de la numeración. */
    @Column(name = "CONSECUTIVO_ASIGNACION")
    private BigDecimal consecutivoAsignacion;

    /** Indicador de rango fraccionado. */
    @Column(name = "FRACCIONAMIENTO_RANGO", length = 1)
    private String fraccionamientoRango;

    /** Identificador de la clave de servicio de la numeración redistribuida. */
    @Column(name = "ID_CLAVE_SERVICIO")
    private BigDecimal idClaveServicio;

    /** Estatus de la Redistribución solicitada después del movimiento. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS_REDIST", nullable = false)
    private EstadoRedistribucionSolicitada estatus;

    /** Redistribucion Solicitada que generó el movimiento. */
    @ManyToOne
    @JoinColumn(name = "ID_NNG_REDIST_SOL", nullable = false)
    private RedistribucionSolicitadaNng redistribucionSolicitada;

    /** Id del Proveedor solicitante de la redistribución. */
    @Column(name = "ID_PST")
    private BigDecimal idProveedorSolicitante;

    /** Proveedor Arrendatario de la numeración original. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_ARRENDATARIO")
    private Proveedor proveedorArrendatario;

    /** Proveedor Concesionario de la numeración original si existía convenio. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_CONVENIO")
    private Proveedor proveedorConcesionario;

    /** Identificador de la Solicitud de Redistribución de la Redistribución Solicitada. */
    @Column(name = "ID_SOL_SOLICITUD")
    private BigDecimal idSolicitudRedistribucion;

    /** Final de Rango de la numeración original. */
    @Column(name = "NUM_FINAL", length = 4)
    private String numFinal;

    /** Inicio de Rango de la numeración original. */
    @Column(name = "NUM_INICIO", length = 4)
    private String numInicio;

    /** Id de la Serie de la numeración original. */
    @Column(name = "SNA", precision = 4)
    private BigDecimal idSerie;

    /** Constructor, por defecto vacío. */
    public HistoricoRedistribucionNng() {
    }

    // GETTERS & SETTERS

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
     * Código ABC.
     * @return BigDecimal
     */
    public BigDecimal getAbc() {
        return abc;
    }

    /**
     * Código ABC.
     * @param abc BigDecimal
     */
    public void setAbc(BigDecimal abc) {
        this.abc = abc;
    }

    /**
     * Código BCD.
     * @return BigDecimal
     */
    public BigDecimal getBcd() {
        return bcd;
    }

    /**
     * Código BCD.
     * @param bcd BigDecimal
     */
    public void setBcd(BigDecimal bcd) {
        this.bcd = bcd;
    }

    /**
     * Tipo de movimiento.
     * @return String
     */
    public String getAccion() {
        return accion;
    }

    /**
     * Tipo de movimiento.
     * @param accion String
     */
    public void setAccion(String accion) {
        this.accion = accion;
    }

    /**
     * Fecha del movimiento.
     * @return Date
     */
    public Date getFechaAccion() {
        return fechaAccion;
    }

    /**
     * Fecha del movimiento.
     * @param fechaAccion Date
     */
    public void setFechaAccion(Date fechaAccion) {
        this.fechaAccion = fechaAccion;
    }

    /**
     * Cliente inicial de la numeración redistribuida.
     * @return String
     */
    public String getCliente() {
        return cliente;
    }

    /**
     * Cliente inicial de la numeración redistribuida.
     * @param cliente String
     */
    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    /**
     * Consecutivo de la Solicitud de Asginación original de la numeración.
     * @return BigDecimal
     */
    public BigDecimal getConsecutivoAsignacion() {
        return consecutivoAsignacion;
    }

    /**
     * Consecutivo de la Solicitud de Asginación original de la numeración.
     * @param consecutivoAsignacion BigDecimal
     */
    public void setConsecutivoAsignacion(BigDecimal consecutivoAsignacion) {
        this.consecutivoAsignacion = consecutivoAsignacion;
    }

    /**
     * Indicador de rango fraccionado.
     * @return String
     */
    public String getFraccionamientoRango() {
        return fraccionamientoRango;
    }

    /**
     * Indicador de rango fraccionado.
     * @param fraccionamientoRango String
     */
    public void setFraccionamientoRango(String fraccionamientoRango) {
        this.fraccionamientoRango = fraccionamientoRango;
    }

    /**
     * Identificador de la clave de servicio de la numeración redistribuida.
     * @return BigDecimal
     */
    public BigDecimal getIdClaveServicio() {
        return idClaveServicio;
    }

    /**
     * Identificador de la clave de servicio de la numeración redistribuida.
     * @param idClaveServicio BigDecimal
     */
    public void setIdClaveServicio(BigDecimal idClaveServicio) {
        this.idClaveServicio = idClaveServicio;
    }

    /**
     * Estatus de la Redistribución solicitada después del movimiento.
     * @return EstadoRedistribucionSolicitada
     */
    public EstadoRedistribucionSolicitada getEstatus() {
        return estatus;
    }

    /**
     * Estatus de la Redistribución solicitada después del movimiento.
     * @param estatus EstadoRedistribucionSolicitada
     */
    public void setEstatus(EstadoRedistribucionSolicitada estatus) {
        this.estatus = estatus;
    }

    /**
     * Redistribucion Solicitada que generó el movimiento.
     * @return RedistribucionSolicitadaNng
     */
    public RedistribucionSolicitadaNng getRedistribucionSolicitada() {
        return redistribucionSolicitada;
    }

    /**
     * Redistribucion Solicitada que generó el movimiento.
     * @param redistribucionSolicitada RedistribucionSolicitadaNng
     */
    public void setRedistribucionSolicitada(RedistribucionSolicitadaNng redistribucionSolicitada) {
        this.redistribucionSolicitada = redistribucionSolicitada;
    }

    /**
     * Id del Proveedor solicitante de la redistribución.
     * @return BigDecimal
     */
    public BigDecimal getIdProveedorSolicitante() {
        return idProveedorSolicitante;
    }

    /**
     * Id del Proveedor solicitante de la redistribución.
     * @param idProveedorSolicitante BigDecimal
     */
    public void setIdProveedorSolicitante(BigDecimal idProveedorSolicitante) {
        this.idProveedorSolicitante = idProveedorSolicitante;
    }

    /**
     * Proveedor Arrendatario de la numeración original.
     * @return Proveedor
     */
    public Proveedor getProveedorArrendatario() {
        return proveedorArrendatario;
    }

    /**
     * Proveedor Arrendatario de la numeración original.
     * @param proveedorArrendatario Proveedor
     */
    public void setProveedorArrendatario(Proveedor proveedorArrendatario) {
        this.proveedorArrendatario = proveedorArrendatario;
    }

    /**
     * Proveedor Concesionario de la numeración original si existía convenio.
     * @return Proveedor
     */
    public Proveedor getProveedorConcesionario() {
        return proveedorConcesionario;
    }

    /**
     * Proveedor Concesionario de la numeración original si existía convenio.
     * @param proveedorConcesionario Proveedor
     */
    public void setProveedorConcesionario(Proveedor proveedorConcesionario) {
        this.proveedorConcesionario = proveedorConcesionario;
    }

    /**
     * Identificador de la Solicitud de Redistribución de la Redistribución Solicitada.
     * @return BigDecimal
     */
    public BigDecimal getIdSolicitudRedistribucion() {
        return idSolicitudRedistribucion;
    }

    /**
     * Identificador de la Solicitud de Redistribución de la Redistribución Solicitada.
     * @param idSolicitudRedistribucion BigDecimal
     */
    public void setIdSolicitudRedistribucion(BigDecimal idSolicitudRedistribucion) {
        this.idSolicitudRedistribucion = idSolicitudRedistribucion;
    }

    /**
     * Final de Rango de la numeración original.
     * @return String
     */
    public String getNumFinal() {
        return numFinal;
    }

    /**
     * Final de Rango de la numeración original.
     * @param numFinal String
     */
    public void setNumFinal(String numFinal) {
        this.numFinal = numFinal;
    }

    /**
     * Inicio de Rango de la numeración original.
     * @return String numInicio
     */
    public String getNumInicio() {
        return numInicio;
    }

    /**
     * Inicio de Rango de la numeración original.
     * @param numInicio String numInicio
     */
    public void setNumInicio(String numInicio) {
        this.numInicio = numInicio;
    }

    /**
     * Id de la Serie de la numeración original.
     * @return BigDecimal
     */
    public BigDecimal getIdSerie() {
        return idSerie;
    }

    /**
     * Id de la Serie de la numeración original.
     * @param idSerie BigDecimal
     */
    public void setIdSerie(BigDecimal idSerie) {
        this.idSerie = idSerie;
    }

}
