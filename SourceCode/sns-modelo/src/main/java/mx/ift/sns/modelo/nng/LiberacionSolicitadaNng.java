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
import mx.ift.sns.modelo.solicitud.EstadoLiberacionSolicitada;

/**
 * Representa una Liberación Solicitada de Numeración No Geográfica. Contiene la información de la numeración
 * seleccionada para liberar y el estatus del proceso.
 */
@Entity
@Table(name = "NNG_LIBERACION_SOLICITADA")
@SequenceGenerator(name = "SEQ_ID_NNG_LIBERACION_SOL", sequenceName = "SEQ_ID_NNG_LIBERACION_SOL", allocationSize = 1)
@NamedQuery(name = "LiberacionSolicitadaNng.findAll", query = "SELECT l FROM LiberacionSolicitadaNng l")
public class LiberacionSolicitadaNng implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_NNG_LIBERACION_SOL")
    @Column(name = "ID_NNG_LIBERACION_SOL", unique = true, nullable = false)
    private BigDecimal id;

    /** Código ABC. */
    @Column(name = "ABC", precision = 3)
    private BigDecimal abc;

    /** Código BCD. */
    @Column(name = "BCD", precision = 3)
    private BigDecimal bcd;

    /** Consecutivo del trámite de asignación que generó la numeración. */
    @Column(name = "CONSECUTIVO_ASIGNACION", nullable = true)
    private BigDecimal consecutivoAsignacion;

    /** Fecha de asignación de la numeración. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_ASIGNACION", nullable = true)
    private Date fechaAsignacion;

    /** Fecha de liberación de la numeración. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_LIBERACION")
    private Date fechaLiberacion;

    /** Indica si la liberación genera fraccionamiento sobre la numeración. */
    @Column(name = "FRACCIONAMIENTO_RANGO", nullable = true, length = 1)
    private String fraccionamientoRango;

    /** Identificador de la clave de servicio. */
    @Column(name = "ID_CLAVE_SERVICIO", nullable = false)
    private BigDecimal idClaveServicio;

    /** Relación: Muchas Liberaciones Solicitadas pueden tener el mismo estado. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS_LIBERACION", nullable = false)
    private EstadoLiberacionSolicitada estatus;

    /** Relación: Muchas Liberaciones Solicitadas pueden tener el mismo Proveedor Cesionario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST", nullable = false)
    private Proveedor proveedorCesionario;

    /** Relación: Muchas Liberaciones Solicitadas pueden tener el mismo Proveedor Arrendatario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_ARRENDATARIO", nullable = true)
    private Proveedor proveedorArrendatario;

    /** Relación: Muchas Liberaciones Solicitadas pueden tener el mismo Proveedor Concesionario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_CONCESIONARIO", nullable = true)
    private Proveedor proveedorConcesionario;

    /** Relación: Muchas Liberaciones Solicitadas pueden ser de una misma solicitud. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL_SOLICITUD", nullable = false)
    private SolicitudLiberacionNng solicitudLiberacion;

    /** Índice de final de la numeración a liberar. */
    @Column(name = "NUM_FINAL", length = 4)
    private String numFinal;

    /** Índice de inicio de la numeración a liberar. */
    @Column(name = "NUM_INICIO", length = 4)
    private String numInicio;

    /** Número de Oficio de Asignación del Rango seleccionado para Liberar. */
    @Column(name = "NUM_OFICIO_ASIG_RANGO", nullable = true, length = 55)
    private String numOficioAsigRango;

    /** Fecha de finalización del periodo de reserva. */
    @Column(name = "FECHA_FIN_RESERVA", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaFinReserva;

    /** Identificador de la Serie. */
    @Column(name = "SNA", nullable = false)
    private BigDecimal sna;

    /** Cliente de la numeración. */
    @Column(name = "CLIENTE", length = 128)
    private String cliente;

    /** Tiempo de periodo de reserva indicado. */
    @Column(name = "PERIODO_RESERVA", length = 10)
    private String periodoReserva;

    /** Código IDA de Enrutamiento. */
    @Column(name = "IDA", nullable = true, length = 3)
    private BigDecimal ida;

    /** Constructor, por defecto vacío. */
    public LiberacionSolicitadaNng() {
    }

    // GETTERS & SETTERS

    /**
     * Identificador de la liberación solicitada.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return this.id;
    }

    /**
     * Identificador de la liberación solicitada.
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Índice de final de la numeración a liberar.
     * @return String
     */
    public String getNumFinal() {
        return this.numFinal;
    }

    /**
     * Devuelve el final de rango como un valor int.
     * @return int
     * @throws Exception si no se puede parsear el número.
     */
    public int getNumFinalAsInt() throws Exception {
        return (Integer.valueOf(numFinal).intValue());
    }

    /**
     * Índice de final de la numeración a liberar.
     * @param numFinal String
     */
    public void setNumFinal(String numFinal) {
        this.numFinal = numFinal;
    }

    /**
     * Índice de inicio de la numeración a liberar.
     * @return String
     */
    public String getNumInicio() {
        return this.numInicio;
    }

    /**
     * Devuelve el inicio de rango como un valor int.
     * @return int
     * @throws Exception si no se puede parsear el número.
     */
    public int getNumInicioAsInt() throws Exception {
        return (Integer.valueOf(numInicio).intValue());
    }

    /**
     * Índice de inicio de la numeración a liberar.
     * @param numInicio int
     */
    public void setNumInicio(String numInicio) {
        this.numInicio = numInicio;
    }

    /**
     * Solicitud de liberación asociada a ésta petición de liberación.
     * @return SolicitudLiberacionNg
     */
    public SolicitudLiberacionNng getSolicitudLiberacion() {
        return solicitudLiberacion;
    }

    /**
     * Solicitud de liberación asociada a ésta petición de liberación.
     * @param solicitudLiberacion SolicitudLiberacionNng
     */
    public void setSolicitudLiberacion(SolicitudLiberacionNng solicitudLiberacion) {
        this.solicitudLiberacion = solicitudLiberacion;
    }

    /**
     * Proveedor cesionario de la numeración.
     * @return Proveedor
     */
    public Proveedor getProveedorCesionario() {
        return proveedorCesionario;
    }

    /**
     * Proveedor cesionario de la numeración.
     * @param proveedorCesionario Proveedor
     */
    public void setProveedorCesionario(Proveedor proveedorCesionario) {
        this.proveedorCesionario = proveedorCesionario;
    }

    /**
     * Fecha de aplicación de la liberación.
     * @return Date
     */
    public Date getFechaLiberacion() {
        return this.fechaLiberacion;
    }

    /**
     * Fecha de aplicación de la liberación.
     * @param fechaLiberacion Date
     */
    public void setFechaLiberacion(Date fechaLiberacion) {
        this.fechaLiberacion = fechaLiberacion;
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
     * Identificador de la Serie.
     * @return String
     */
    public BigDecimal getSna() {
        return this.sna;
    }

    /**
     * Identificador de la Serie.
     * @param sna String
     */
    public void setSna(BigDecimal sna) {
        this.sna = sna;
    }

    /**
     * Estatus de LiberaciónSolicitada.
     * @return the estatus
     */
    public EstadoLiberacionSolicitada getEstatus() {
        return estatus;
    }

    /**
     * Estatus de LiberaciónSolicitada.
     * @param estatus the estatus to set
     */
    public void setEstatus(EstadoLiberacionSolicitada estatus) {
        this.estatus = estatus;
    }

    /**
     * Fecha de asignación de la numeración en cesión.
     * @return Date
     */
    public Date getFechaAsignacion() {
        return fechaAsignacion;
    }

    /**
     * Fecha de asignación de la numeración en cesión.
     * @param fechaAsignacion Date
     */
    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
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
     * Almacena la información del Proveedor Arrendatario si existe arrendamiento en la numeración que se va a liberar.
     * @return Proveedor arrendatario del arrendamiento de la numeración a liberar.
     */
    public Proveedor getProveedorArrendatario() {
        return proveedorArrendatario;
    }

    /**
     * Almacena la información del Proveedor Arrendatario si existe arrendamiento en la numeración que se va a liberar.
     * @param proveedorArrendatario Proveedor arrendatario del arrendamiento de la numeración a liberar.
     */
    public void setProveedorArrendatario(Proveedor proveedorArrendatario) {
        this.proveedorArrendatario = proveedorArrendatario;
    }

    /**
     * Almacena la información del Proveedor Concesionario si existe arrendamiento en la numeración que se va a liberar.
     * @return Proveedor concesionario del arrendamiento de la numeración a liberar.
     */
    public Proveedor getProveedorConcesionario() {
        return proveedorConcesionario;
    }

    /**
     * Almacena la información del Proveedor Concesionario si existe arrendamiento en la numeración que se va a liberar.
     * @param proveedorConcesionario Proveedor concesionario del arrendamiento de la numeración a liberar.
     */
    public void setProveedorConcesionario(Proveedor proveedorConcesionario) {
        this.proveedorConcesionario = proveedorConcesionario;
    }

    /**
     * Indica si la liberación genera fraccionamiento sobre la numeración.
     * @return 'S' si existe fraccionamiento.
     */
    public String getFraccionamientoRango() {
        return fraccionamientoRango;
    }

    /**
     * Indica si la liberación genera fraccionamiento sobre la numeración.
     * @param fraccionamientoRango 'S' si existe fraccionamiento.
     */
    public void setFraccionamientoRango(String fraccionamientoRango) {
        this.fraccionamientoRango = fraccionamientoRango;
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
     * Número de Oficio de Asignación del Rango seleccionado para Liberar.
     * @return String
     */
    public String getNumOficioAsigRango() {
        return numOficioAsigRango;
    }

    /**
     * Número de Oficio de Asignación del Rango seleccionado para Liberar.
     * @param numOficioAsigRango String
     */
    public void setNumOficioAsigRango(String numOficioAsigRango) {
        this.numOficioAsigRango = numOficioAsigRango;
    }

    /**
     * Fecha de finalización del periodo de reserva.
     * @return Date
     */
    public Date getFechaFinReserva() {
        return fechaFinReserva;
    }

    /**
     * Fecha de finalización del periodo de reserva.
     * @param fechaFinReserva Date
     */
    public void setFechaFinReserva(Date fechaFinReserva) {
        this.fechaFinReserva = fechaFinReserva;
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
     * Tiempo de periodo de reserva indicado.
     * @return String
     */
    public String getPeriodoReserva() {
        return periodoReserva;
    }

    /**
     * Tiempo de periodo de reserva indicado.
     * @param periodoReserva String
     */
    public void setPeriodoReserva(String periodoReserva) {
        this.periodoReserva = periodoReserva;
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
        if (!(other instanceof LiberacionSolicitadaNng)) {
            return false;
        }
        LiberacionSolicitadaNng castOther = (LiberacionSolicitadaNng) other;
        return (this.id == castOther.id)
                && (this.idClaveServicio.intValue() == castOther.idClaveServicio.intValue())
                && (this.sna.intValue() == castOther.sna.intValue())
                && (this.numInicio == castOther.numInicio)
                && (this.proveedorCesionario == castOther.proveedorCesionario);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LiberacionSolicitada NNG = {");
        builder.append("id=").append(this.id);
        builder.append(", Clave=").append(this.idClaveServicio);
        builder.append(", Sna=").append(this.sna);
        builder.append(", numInicio=").append(this.numInicio);
        builder.append(", numFinal=").append(this.numFinal);
        builder.append("}");
        return builder.toString();
    }

}
