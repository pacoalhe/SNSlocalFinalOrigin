package mx.ift.sns.modelo.nng;

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

import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.solicitud.EstadoRedistribucionSolicitada;

/**
 * Representa una Redistribución Solicitada de Numeración No Geográfica. Contiene la información de la numeración
 * seleccionada para redistribuir y el estatus del proceso.
 */
@Entity
@Table(name = "NNG_REDISTRIBUCION_SOL")
@SequenceGenerator(name = "SEQ_ID_NNG_REDIST_SOL", sequenceName = "SEQ_ID_NNG_REDIST_SOL", allocationSize = 1)
@NamedQuery(name = "RedistribucionSolicitadaNng.findAll", query = "SELECT r FROM RedistribucionSolicitadaNng r")
public class RedistribucionSolicitadaNng implements Serializable, Cloneable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_NNG_REDIST_SOL")
    @Column(name = "ID_NNG_REDIST_SOL", unique = true, nullable = false)
    private BigDecimal id;

    /** Código ABC. */
    @Column(name = "ABC", precision = 3)
    private BigDecimal abc;

    /** Código BCD. */
    @Column(name = "BCD", precision = 3)
    private BigDecimal bcd;

    /** Consecutivo del trámite de asignación que generó la numeración. */
    @Column(name = "CONSECUTIVO_ASIGNACION", nullable = true)
    private BigDecimal consecutivoAsignacion;;

    /** Indica si la liberación genera fraccionamiento sobre la numeración. */
    @Column(name = "FRACCIONAMIENTO_RANGO", nullable = true, length = 1)
    private String fraccionamientoRango;

    /** Identificador de la clave de servicio. */
    @Column(name = "ID_CLAVE_SERVICIO", nullable = false)
    private BigDecimal idClaveServicio;

    /** Relación: Muchas Redistribuciones Solicitadas pueden tener el mismo estado. */
    @JoinColumn(name = "ID_ESTATUS_REDIST", nullable = false)
    private EstadoRedistribucionSolicitada estatus;

    /** Relación: Muchas Redistribuciones Solicitadas pueden tener el mismo Proveedor Solicitante. */
    @ManyToOne
    @JoinColumn(name = "ID_PST", nullable = false)
    private Proveedor proveedorSolicitante;

    /** Relación: Muchas Redistribuciones Solicitadas pueden tener el mismo Proveedor Arrendatario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_ARRENDATARIO", nullable = true)
    private Proveedor proveedorArrendatario;

    /** Relación: Muchas Redistribuciones Solicitadas pueden tener el mismo Proveedor Concesionario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_CONVENIO", nullable = true)
    private Proveedor proveedorConcesionario;

    /** Índice de final de la numeración a liberar. */
    @Column(name = "NUM_FINAL", length = 4)
    private String numFinal;

    /** Índice de inicio de la numeración a liberar. */
    @Column(name = "NUM_INICIO", length = 4)
    private String numInicio;

    /** Identificador de la Serie. */
    @Column(nullable = false, precision = 4)
    private BigDecimal sna;

    /** Relación: Muchas Redistribuciones Solicitadas pueden ser de una misma solicitud. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL_SOLICITUD", nullable = false)
    private SolicitudRedistribucionNng solicitudRedistribucion;

    /** Cliente de la numeración. */
    @Column(name = "CLIENTE", length = 128)
    private String cliente;

    /** Código IDA de Enrutamiento. */
    @Column(name = "IDA", nullable = true, length = 3)
    private BigDecimal ida;

    /** Constructor, por defecto vacío. */
    public RedistribucionSolicitadaNng() {
    }

    /**
     * Solicitud de redistribución asociada a la redistribución solicitada.
     * @return SolicitudRedistribucionNng
     */
    public SolicitudRedistribucionNng getSolicitudRedistribucion() {
        return solicitudRedistribucion;
    }

    /**
     * Solicitud de redistribución asociada a la redistribución solicitada.
     * @param solicitudRedistribucion SolicitudRedistribucionNng
     */
    public void setSolicitudRedistribucion(SolicitudRedistribucionNng solicitudRedistribucion) {
        this.solicitudRedistribucion = solicitudRedistribucion;
    }

    /**
     * Identificador de la redistribución solicitada.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return this.id;
    }

    /**
     * Identificador de la redistribución solicitada.
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Índice de final de la numeración a fraccionar.
     * @return String
     */
    public String getNumFinal() {
        return this.numFinal;
    }

    /**
     * Índice de final de la numeración a fraccionar.
     * @param numFinal String
     */
    public void setNumFinal(String numFinal) {
        this.numFinal = numFinal;
    }

    /**
     * Índice de inicio de la numeración a fraccionar.
     * @return String
     */
    public String getNumInicio() {
        return this.numInicio;
    }

    /**
     * Índice de inicio de la numeración a fraccionar.
     * @param numInicio String
     */
    public void setNumInicio(String numInicio) {
        this.numInicio = numInicio;
    }

    /**
     * Proveedor Concesionario.
     * @return Proveedor
     */
    public Proveedor getProveedorConcesionario() {
        return proveedorConcesionario;
    }

    /**
     * Proveedor Concesionario.
     * @param proveedorConcesionario Proveedor
     */
    public void setProveedorConcesionario(Proveedor proveedorConcesionario) {
        this.proveedorConcesionario = proveedorConcesionario;
    }

    /**
     * Proveedor Arrendatario.
     * @return Proveedor
     */
    public Proveedor getProveedorArrendatario() {
        return proveedorArrendatario;
    }

    /**
     * Proveedor Arrendatario.
     * @param proveedorArrendatario Proveedor
     */
    public void setProveedorArrendatario(Proveedor proveedorArrendatario) {
        this.proveedorArrendatario = proveedorArrendatario;
    }

    /**
     * Identificador de la Serie.
     * @return BigDecimal
     */
    public BigDecimal getSna() {
        return sna;
    }

    /**
     * Identificador de la Serie.
     * @param sna BigDecimal
     */
    public void setSna(BigDecimal sna) {
        this.sna = sna;
    }

    /**
     * Devuelve el inicio de rango como un valor int.
     * @return int Inicio de Rango
     * @throws Exception si no se puede parsear el número.
     */
    public int getNumInicioAsInt() throws Exception {
        return (Integer.valueOf(numInicio).intValue());
    }

    /**
     * Devuelve el final de rango como un valor int.
     * @return int Final de Rango
     * @throws Exception si no se puede parsear el número.
     */
    public int getNumFinalAsInt() throws Exception {
        return (Integer.valueOf(numFinal).intValue());
    }

    /**
     * Proveedor solicitante del trámite de redistribución.
     * @return Proveedor
     */
    public Proveedor getProveedorSolicitante() {
        return proveedorSolicitante;
    }

    /**
     * Proveedor solicitante del trámite de redistribución.
     * @param proveedorSolicitante Proveedor
     */
    public void setProveedorSolicitante(Proveedor proveedorSolicitante) {
        this.proveedorSolicitante = proveedorSolicitante;
    }

    /**
     * Indica si la redistribución genera fraccionamiento sobre la numeración.
     * @return 'S' si existe fraccionamiento.
     */
    public String getFraccionamientoRango() {
        return fraccionamientoRango;
    }

    /**
     * Indica si la redistribución genera fraccionamiento sobre la numeración.
     * @param fraccionamientoRango 'S' si existe fraccionamiento.
     */
    public void setFraccionamientoRango(String fraccionamientoRango) {
        this.fraccionamientoRango = fraccionamientoRango;
    }

    /**
     * Consecutivo del trámite de asignación que generó la numeración.
     * @return BigDecimal
     */
    public BigDecimal getConsecutivoAsignacion() {
        return consecutivoAsignacion;
    }

    /**
     * Consecutivo del trámite de asignación que generó la numeración.
     * @param consecutivoAsignacion BigDecimal
     */
    public void setConsecutivoAsignacion(BigDecimal consecutivoAsignacion) {
        this.consecutivoAsignacion = consecutivoAsignacion;
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
     * Identificador de la clave de servicio.
     * @return BigDecimal
     */
    public BigDecimal getIdClaveServicio() {
        return idClaveServicio;
    }

    /**
     * Identificador de la clave de servicio.
     * @param idClaveServicio BigDecimal
     */
    public void setIdClaveServicio(BigDecimal idClaveServicio) {
        this.idClaveServicio = idClaveServicio;
    }

    /**
     * Estatus de la Redistribución Solicitada.
     * @return EstadoRedistribucionSolicitada
     */
    public EstadoRedistribucionSolicitada getEstatus() {
        return estatus;
    }

    /**
     * Estatus de la Redistribución Solicitada.
     * @param estatus EstadoRedistribucionSolicitada
     */
    public void setEstatus(EstadoRedistribucionSolicitada estatus) {
        this.estatus = estatus;
    }

    /**
     * Cliente de la numeración.
     * @return String
     */
    public String getCliente() {
        return cliente;
    }

    /**
     * Cliente de la numeración.
     * @param cliente String
     */
    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    /**
     * Código IDA de Enrutamiento.
     * @return BigDecimal
     */
    public BigDecimal getIda() {
        return ida;
    }

    /**
     * Código IDA de Enrutamiento.
     * @param ida the ida to set
     */
    public void setIda(BigDecimal ida) {
        this.ida = ida;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RedistribucionSolicitadaNng)) {
            return false;
        }
        RedistribucionSolicitadaNng castOther = (RedistribucionSolicitadaNng) other;
        return (this.id == castOther.id)
                && (this.idClaveServicio.intValue() == castOther.idClaveServicio.intValue())
                && (this.sna.intValue() == castOther.sna.intValue())
                && (this.numInicio == castOther.numInicio)
                && (this.proveedorSolicitante == castOther.proveedorSolicitante);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RedistribucionSolicitadaNg NNG = {");
        builder.append("id=").append(this.id);
        builder.append(", idClaveServ=").append(this.idClaveServicio);
        builder.append(", Sna=").append(this.sna);
        builder.append(", numInicio=").append(this.numInicio);
        builder.append(", numFinal=").append(this.numFinal);
        builder.append("}");
        return builder.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        // Clonamos todas las variables exception el Id
        RedistribucionSolicitadaNng redistCloned = (RedistribucionSolicitadaNng) super.clone();
        redistCloned.setId(null);
        return redistCloned;
    }

}
