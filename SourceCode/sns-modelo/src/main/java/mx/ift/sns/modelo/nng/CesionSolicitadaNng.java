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
import mx.ift.sns.modelo.pst.ProveedorConvenio;
import mx.ift.sns.modelo.solicitud.EstadoCesionSolicitada;

/**
 * Representa una Cesión Solicitada de Numeración No Geográfica. Contiene la información de la numeración seleccionada
 * para ceder y el estatus del proceso.
 */
@Entity
@Table(name = "NNG_CESION_SOLICITADA")
@SequenceGenerator(name = "SEQ_ID_NNG_CESION_SOL", sequenceName = "SEQ_ID_NNG_CESION_SOL", allocationSize = 1)
@NamedQuery(name = "CesionSolicitadaNng.findAll", query = "SELECT n FROM CesionSolicitadaNng n")
public class CesionSolicitadaNng implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_NNG_CESION_SOL")
    @Column(name = "ID_NNG_CESION_SOL", unique = true, nullable = false)
    private BigDecimal id;

    /** Identificador ABC del Proveedor. */
    @Column(name = "ABC", precision = 3)
    private BigDecimal abc;

    /** Identificador BCD del Proveedor. */
    @Column(name = "BCD", precision = 3)
    private BigDecimal bcd;

    /** Identificador del Consecutivo de la Solicitud de Asignación de la numeración cedida. */
    @Column(name = "CONSECUTIVO_ASIGNACION")
    private BigDecimal consecutivoAsignacion;

    /** Fecha de Asignación de la numeración cedida. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_ASIGNACION")
    private Date fechaAsignacion;

    /** Fecha de Cesión de la Numeración. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_CESION")
    private Date fechaCesion;

    /** 'S' Indica que el rango ha sido fraccionado para su cesión. */
    @Column(name = "FRACCIONAMIENTO_RANGO", length = 1)
    private String fraccionamientoRango;

    /** Identificador de la clave de Servicio. */
    @Column(name = "ID_CLAVE_SERVICIO", nullable = false)
    private BigDecimal idClaveServicio;

    /** Identificador del convenio del proveedor cesionario si existe. */
    @ManyToOne
    @JoinColumn(name = "ID_CONVENIO_CESIONARIO", nullable = true)
    private ProveedorConvenio convenioCesionario;

    /** Relación: Muchas Cesiones Solicitadas pueden tener el mismo estatus. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS_CESION", nullable = false)
    private EstadoCesionSolicitada estatus;

    /** Relación: Muchas cesiones Solicitadas pueden tener el mismo Proveedor Concesionario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_CONCESIONARIO", nullable = true)
    private Proveedor proveedorConcesionario;

    /** Relación: Muchas cesiones solicitadas pueden tener el mismo Proveedor. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_CESIONARIO", nullable = false)
    private Proveedor proveedorCesionario;

    /** Relación: Muchas cesiones solicitadas pueden tener el mismo Proveedor. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_CEDENTE", nullable = false)
    private Proveedor proveedorCedente;

    /** Relación: Muchas cesiones Solicitadas pueden tener el mismo Proveedor Arrendatario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_ARRENDATARIO", nullable = true)
    private Proveedor proveedorArrendatario;

    /** Final de la Numeración Cedida. */
    @Column(name = "NUM_FINAL", length = 4)
    private String numFinal;

    /** Inicio de la Numeración Cedida. */
    @Column(name = "NUM_INICIO", length = 4)
    private String numInicio;

    /** Número de Oficio de la Solicitud de Asignación de la Numeración. */
    @Column(name = "NUM_OFICIO_ASIG_RANGO", length = 55)
    private String numOficioAsigRango;

    /** Identificador de la Serie de la Numeración. */
    @Column(nullable = false, precision = 4)
    private BigDecimal sna;

    /** Indica si se ha de utilizar el ABC del Proveedor Cesionario para el enrutamiento de la numeración cedida. */
    @Column(name = "USAR_ABC_CESIONARIO", length = 1)
    private String usarAbcCesionario;

    /**
     * Indica si se ha de utilizar el convenio del Proveedor Cesionario para el enrutamiento de la numeración cedida.
     */
    @Column(name = "USAR_CONVENIO_CESIONARIO", length = 1)
    private String usarConvenioCesionario;

    /**
     * Relación: Muchas cesiones solicitadas pueden pertenecer a una misma solicitud de cesión de numeración no
     * geográfica.
     */
    @ManyToOne
    @JoinColumn(name = "ID_SOL_SOLICITUD", nullable = false)
    private SolicitudCesionNng solicitudCesion;

    /** Cliente de la numeración. */
    @Column(name = "CLIENTE", length = 128)
    private String cliente;

    /** Código IDA de Enrutamiento. */
    @Column(name = "IDA", nullable = true, length = 3)
    private BigDecimal ida;

    /** Constructor. Por defecto, vacío. */
    public CesionSolicitadaNng() {
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
     * Identificador ABC del Proveedor.
     * @return BigDecimal
     */
    public BigDecimal getAbc() {
        return abc;
    }

    /**
     * Identificador ABC del Proveedor.
     * @param abc BigDecimal
     */
    public void setAbc(BigDecimal abc) {
        this.abc = abc;
    }

    /**
     * Identificador BCD del Proveedor.
     * @return BigDecimal
     */
    public BigDecimal getBcd() {
        return bcd;
    }

    /**
     * Identificador BCD del Proveedor.
     * @param bcd BigDecimal
     */
    public void setBcd(BigDecimal bcd) {
        this.bcd = bcd;
    }

    // /**
    // * Código de la clave de Servicio.
    // * @return String
    // */
    // public String getCdgClaveServicio() {
    // return cdgClaveServicio;
    // }
    //
    // /**
    // * Código de la clave de Servicio.
    // * @param cdgClaveServicio String
    // */
    // public void setCdgClaveServicio(String cdgClaveServicio) {
    // this.cdgClaveServicio = cdgClaveServicio;
    // }

    /**
     * Identificador del Consecutivo de la Solicitud de Asignación de la numeración cedida.
     * @return BigDecimal
     */
    public BigDecimal getConsecutivoAsignacion() {
        return consecutivoAsignacion;
    }

    /**
     * Identificador del Consecutivo de la Solicitud de Asignación de la numeración cedida.
     * @param consecutivoAsignacion BigDecimal
     */
    public void setConsecutivoAsignacion(BigDecimal consecutivoAsignacion) {
        this.consecutivoAsignacion = consecutivoAsignacion;
    }

    /**
     * Fecha de Asignación de la numeración cedida.
     * @return Date
     */
    public Date getFechaAsignacion() {
        return fechaAsignacion;
    }

    /**
     * Fecha de Asignación de la numeración cedida.
     * @param fechaAsignacion Date
     */
    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    /**
     * Fecha de Cesión de la Numeración.
     * @return Date
     */
    public Date getFechaCesion() {
        return fechaCesion;
    }

    /**
     * Fecha de Cesión de la Numeración.
     * @param fechaCesion Date
     */
    public void setFechaCesion(Date fechaCesion) {
        this.fechaCesion = fechaCesion;
    }

    /**
     * 'S' Indica que el rango ha sido fraccionado para su cesión.
     * @return String
     */
    public String getFraccionamientoRango() {
        return fraccionamientoRango;
    }

    /**
     * 'S' Indica que el rango ha sido fraccionado para su cesión.
     * @param fraccionamientoRango String
     */
    public void setFraccionamientoRango(String fraccionamientoRango) {
        this.fraccionamientoRango = fraccionamientoRango;
    }

    /**
     * Identificador de la clave de Servicio.
     * @return BigDecimal
     */
    public BigDecimal getIdClaveServicio() {
        return idClaveServicio;
    }

    /**
     * Identificador de la clave de Servicio.
     * @param idClaveServicio BigDecimal
     */
    public void setIdClaveServicio(BigDecimal idClaveServicio) {
        this.idClaveServicio = idClaveServicio;
    }

    /**
     * Estatus de la Cesion Solicitada.
     * @return EstadoCesionSolicitada
     */
    public EstadoCesionSolicitada getEstatus() {
        return estatus;
    }

    /**
     * Estatus de la Cesion Solicitada.
     * @param estatus EstadoCesionSolicitada
     */
    public void setEstatus(EstadoCesionSolicitada estatus) {
        this.estatus = estatus;
    }

    /**
     * Proveedor Concesionario de la Numeración Cedida.
     * @return Proveedor
     */
    public Proveedor getProveedorConcesionario() {
        return proveedorConcesionario;
    }

    /**
     * Proveedor Concesionario de la Numeración Cedida.
     * @param proveedorConcesionario Proveedor
     */
    public void setProveedorConcesionario(Proveedor proveedorConcesionario) {
        this.proveedorConcesionario = proveedorConcesionario;
    }

    /**
     * Proveedor Cesionario de la Numeración Cedida.
     * @return Proveedor
     */
    public Proveedor getProveedorCesionario() {
        return proveedorCesionario;
    }

    /**
     * Proveedor Cesionario de la Numeración Cedida.
     * @param proveedorCesionario Proveedor
     */
    public void setProveedorCesionario(Proveedor proveedorCesionario) {
        this.proveedorCesionario = proveedorCesionario;
    }

    /**
     * Proveedor Cedente de la Numeración Cedida.
     * @return Proveedor
     */
    public Proveedor getProveedorCedente() {
        return proveedorCedente;
    }

    /**
     * Proveedor Cedente de la Numeración Cedida.
     * @param proveedorCedente Proveedor
     */
    public void setProveedorCedente(Proveedor proveedorCedente) {
        this.proveedorCedente = proveedorCedente;
    }

    /**
     * Proveedor Arrendatario de la Numeración Cedida.
     * @return Proveedor
     */
    public Proveedor getProveedorArrendatario() {
        return proveedorArrendatario;
    }

    /**
     * Proveedor Arrendatario de la Numeración Cedida.
     * @param proveedorArrendatario Proveedor
     */
    public void setProveedorArrendatario(Proveedor proveedorArrendatario) {
        this.proveedorArrendatario = proveedorArrendatario;
    }

    /**
     * Final de la Numeración Cedida.
     * @return String
     */
    public String getNumFinal() {
        return numFinal;
    }

    /**
     * Final de la Numeración Cedida.
     * @param numFinal String
     */
    public void setNumFinal(String numFinal) {
        this.numFinal = numFinal;
    }

    /**
     * Inicio de la Numeración Cedida.
     * @return String
     */
    public String getNumInicio() {
        return numInicio;
    }

    /**
     * Inicio de la Numeración Cedida.
     * @param numInicio String
     */
    public void setNumInicio(String numInicio) {
        this.numInicio = numInicio;
    }

    /**
     * Número de Oficio de la Solicitud de Asignación de la Numeración.
     * @return String
     */
    public String getNumOficioAsigRango() {
        return numOficioAsigRango;
    }

    /**
     * Número de Oficio de la Solicitud de Asignación de la Numeración.
     * @param numOficioAsigRango String
     */
    public void setNumOficioAsigRango(String numOficioAsigRango) {
        this.numOficioAsigRango = numOficioAsigRango;
    }

    /**
     * Identificador de la Serie de la Numeración.
     * @return BigDecimal
     */
    public BigDecimal getSna() {
        return sna;
    }

    /**
     * Identificador de la Serie de la Numeración.
     * @param sna BigDecimal
     */
    public void setSna(BigDecimal sna) {
        this.sna = sna;
    }

    /**
     * Indica si se ha de utilizar el ABC del Proveedor Cesionario para el enrutamiento de la numeración cedida.
     * @return String
     */
    public String getUsarAbcCesionario() {
        return usarAbcCesionario;
    }

    /**
     * Indica si se ha de utilizar el ABC del Proveedor Cesionario para el enrutamiento de la numeración cedida.
     * @param usarAbcCesionario String
     */
    public void setUsarAbcCesionario(String usarAbcCesionario) {
        this.usarAbcCesionario = usarAbcCesionario;
    }

    /**
     * Indica si se ha de utilizar el convenio del Proveedor Cesionario para el enrutamiento de la numeración cedida.
     * @return String
     */
    public String getUsarConvenioCesionario() {
        return usarConvenioCesionario;
    }

    /**
     * Indica si se ha de utilizar el convenio del Proveedor Cesionario para el enrutamiento de la numeración cedida.
     * @param usarConvenioCesionario String
     */
    public void setUsarConvenioCesionario(String usarConvenioCesionario) {
        this.usarConvenioCesionario = usarConvenioCesionario;
    }

    /**
     * Identificador de la Solicitud de Cesión de Numeración No Geográfica.
     * @return the solicitudCesion
     */
    public SolicitudCesionNng getSolicitudCesion() {
        return solicitudCesion;
    }

    /**
     * Identificador de la Solicitud de Cesión de Numeración No Geográfica.
     * @param solicitudCesion the solicitudCesion to set
     */
    public void setSolicitudCesion(SolicitudCesionNng solicitudCesion) {
        this.solicitudCesion = solicitudCesion;
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
     * Devuelve el final de rango como un valor int.
     * @return int
     * @throws Exception si no se puede parsear el número.
     */
    public int getNumFinalAsInt() throws Exception {
        return (Integer.valueOf(numFinal).intValue());
    }

    /**
     * Convenio del proveedor cesionario si existe.
     * @return ProveedorConvenio
     */
    public ProveedorConvenio getConvenioCesionario() {
        return convenioCesionario;
    }

    /**
     * Convenio del proveedor cesionario si existe.
     * @param convenioCesionario ProveedorConvenio
     */
    public void setConvenioCesionario(ProveedorConvenio convenioCesionario) {
        this.convenioCesionario = convenioCesionario;
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
        if (!(other instanceof CesionSolicitadaNng)) {
            return false;
        }
        CesionSolicitadaNng castOther = (CesionSolicitadaNng) other;
        return (this.id == castOther.id)
                && (this.idClaveServicio == castOther.idClaveServicio)
                && (this.sna == castOther.sna)
                && (this.numInicio == castOther.numInicio)
                && (this.proveedorCesionario == castOther.proveedorCesionario);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CesionSolicitadaNng={");
        builder.append("id=").append(this.id);
        builder.append(", idClaveServicio=").append(this.idClaveServicio);
        builder.append(", Sna=").append(this.sna);
        builder.append(", numInicio=").append(this.numInicio);
        builder.append(", numFinal=").append(this.numFinal);
        builder.append("}");
        return builder.toString();
    }

}
