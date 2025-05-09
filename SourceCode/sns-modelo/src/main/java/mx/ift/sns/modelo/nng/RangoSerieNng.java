package mx.ift.sns.modelo.nng;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.series.IRangoSerie;
import mx.ift.sns.modelo.solicitud.Solicitud;

/**
 * Representa un Rango contenido en una Serie de Numeración No Geográfica. Contiene información básica de la numeración.
 */
@Entity
@Table(name = "NNG_RANGO_SERIE")
@NamedQuery(name = "RangoSerieNng.findAll", query = "SELECT r FROM RangoSerieNng r")
public class RangoSerieNng implements Serializable, Cloneable, IRangoSerie {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Numeracion minima. */
    public static final String NUM_MIN = "0000";

    /** Numeracion máxima. */
    public static final String NUM_MAX = "9999";

    /** PK de la tabla. */
    @EmbeddedId
    private RangoSerieNngPK id;

    /** Identificador de ABC. */
    @Column(name = "ABC", precision = 3)
    private BigDecimal abc;

    /** Identificador de BCD. */
    @Column(name = "BCD", precision = 3)
    private BigDecimal bcd;

    /** Identificador de Cliente. */
    @Column(name = "CLIENTE", length = 128)
    private String cliente;

    /** Identificador de la Solicitud. Relación: Muchos rangos pueden pertenecer a la misma solicitud. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL_SOLICITUD", nullable = false)
    private Solicitud solicitud;

    /** Fecha de Asignación de la Numeración. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_ASIGNACION")
    private Date fechaAsignacion;

    /** Fecha de fin de reserva de la Numeración. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_FIN_RESERVA")
    private Date fechaFinReserva;

    /** Relación: Muchos rangos pueden tener el mismo estatus. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS_RANGO", nullable = false)
    private EstadoRango estatus;

    /** Numeracion solicitada. */
    @ManyToOne
    @JoinColumn(name = "ID_NNG_NUM_SOLI")
    private NumeracionSolicitadaNng numeracionSolicitada;

    /** Proveedor Concesionario de la Numeración. Muchos rangos pueden tener el mismo proveedor concesionario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_CONCESIONARIO")
    private Proveedor concesionario;

    /** Proveedor Arrendatario de la Numeración. Muchos rangos pueden tener el mismo proveedor arrendatario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_ARRENDATARIO")
    private Proveedor arrendatario;

    /** Proveedor Asignatario de la Numeración. Muchos rangos pueden tener el mismo proveedor asignatario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_ASIGNATARIO", nullable = false)
    private Proveedor asignatario;

    /** Final del Rango. */
    @Column(name = "NUM_FINAL", length = 4)
    private String numFinal;

    /** Inicio del Rango. */
    @Column(name = "NUM_INICIO", length = 4)
    private String numInicio;

    /** Observaciones sobre la numeración. */
    @Column(name = "OBSERVACIONES", length = 700)
    private String observaciones;

    /** Identificador de rango rentado. */
    @Column(name = "RENTADO", precision = 5)
    private BigDecimal rentado;

    /** Consecutivo del trámite de asignación que generó la numeración. */
    @Column(name = "ID_SOL_ASIG", nullable = true)
    private BigDecimal consecutivoAsignacion;

    /** Número de Oficio de Asignación del Rango. */
    @Column(name = "OFICIO_SOL_ASIG", nullable = true, length = 55)
    private String oficioAsignacion;

    /** Relacion: Muchos Rangos pueden pertenecer a la misma serie. */
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ID_CLAVE_SERVICIO", referencedColumnName = "ID_CLAVE_SERVICIO",
                    nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "SNA", referencedColumnName = "SNA",
                    nullable = false, insertable = false, updatable = false)
    })
    private SerieNng serie;

    /** Referencia a la clave de Servicio asociada a la numeración. */
    @ManyToOne
    @JoinColumn(name = "ID_CLAVE_SERVICIO", nullable = false, insertable = false, updatable = false)
    private ClaveServicio claveServicio;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor. Por defecto vacío. */
    public RangoSerieNng() {
    }

    // GETTERS & SETTERS

    /**
     * PK de la tabla.
     * @return RangoSerieNngPK
     */
    public RangoSerieNngPK getId() {
        return id;
    }

    /**
     * PK de la tabla.
     * @param id RangoSerieNngPK
     */
    public void setId(RangoSerieNngPK id) {
        this.id = id;
    }

    /**
     * Identificador de ABC.
     * @return BigDecimal
     */
    public BigDecimal getAbc() {
        return abc;
    }

    /**
     * Identificador de ABC.
     * @param abc BigDecimal
     */
    public void setAbc(BigDecimal abc) {
        this.abc = abc;
    }

    /**
     * Identificador de BCD.
     * @return BigDecimal
     */
    public BigDecimal getBcd() {
        return bcd;
    }

    /**
     * Identificador de BCD.
     * @param bcd BigDecimal
     */
    public void setBcd(BigDecimal bcd) {
        this.bcd = bcd;
    }

    /**
     * Identificador de Cliente.
     * @return String
     */
    public String getCliente() {
        return cliente;
    }

    /**
     * Identificador de Cliente.
     * @param cliente String
     */
    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    /**
     * Fecha de Asignación de la Numeración.
     * @return Date
     */
    public Date getFechaAsignacion() {
        return fechaAsignacion;
    }

    /**
     * Fecha de Asignación de la Numeración.
     * @param fechaAsignacion Date
     */
    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    /**
     * Fecha de fin de reserva de la Numeración.
     * @return Date
     */
    public Date getFechaFinReserva() {
        return fechaFinReserva;
    }

    /**
     * Fecha de fin de reserva de la Numeración.
     * @param fechaFinReserva Date
     */
    public void setFechaFinReserva(Date fechaFinReserva) {
        this.fechaFinReserva = fechaFinReserva;
    }

    /**
     * Estatus del Rango.
     * @return EstadoRango
     */
    public EstadoRango getEstatus() {
        return estatus;
    }

    /**
     * Estatus del Rango.
     * @param estatus EstadoRango
     */
    public void setEstatus(EstadoRango estatus) {
        this.estatus = estatus;
    }

    /**
     * Proveedor Concesionario de la Numeración.
     * @return Proveedor
     */
    public Proveedor getConcesionario() {
        return concesionario;
    }

    /**
     * Proveedor Concesionario de la Numeración.
     * @param concesionario Proveedor
     */
    public void setConcesionario(Proveedor concesionario) {
        this.concesionario = concesionario;
    }

    /**
     * Proveedor Arrendatario de la Numeración.
     * @return Proveedor
     */
    public Proveedor getArrendatario() {
        return arrendatario;
    }

    /**
     * Proveedor Arrendatario de la Numeración.
     * @param arrendatario Proveedor
     */
    public void setArrendatario(Proveedor arrendatario) {
        this.arrendatario = arrendatario;
    }

    /**
     * Proveedor Asignatario de la Numeración.
     * @return Proveedor
     */
    public Proveedor getAsignatario() {
        return asignatario;
    }

    /**
     * Proveedor Asignatario de la Numeración.
     * @param asignatario Proveedor
     */
    public void setAsignatario(Proveedor asignatario) {
        this.asignatario = asignatario;
    }

    /**
     * Final del Rango.
     * @return String
     */
    public String getNumFinal() {
        return numFinal;
    }

    /**
     * Final del Rango.
     * @param numFinal String
     */
    public void setNumFinal(String numFinal) {
        this.numFinal = numFinal;
    }

    /**
     * Inicio del Rango.
     * @return String
     */
    public String getNumInicio() {
        return numInicio;
    }

    /**
     * Inicio del Rango.
     * @param numInicio String
     */
    public void setNumInicio(String numInicio) {
        this.numInicio = numInicio;
    }

    /**
     * Observaciones sobre la numeración.
     * @return String
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * Observaciones sobre la numeración.
     * @param observaciones String
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
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
     * Número de Oficio de Asignación del Rango.
     * @return String
     */
    public String getOficioAsignacion() {
        return oficioAsignacion;
    }

    /**
     * Número de Oficio de Asignación del Rango.
     * @param oficioAsignacion String
     */
    public void setOficioAsignacion(String oficioAsignacion) {
        this.oficioAsignacion = oficioAsignacion;
    }

    /**
     * Identificador de rango rentado.
     * @return BigDecimal
     */
    public BigDecimal getRentado() {
        return rentado;
    }

    /**
     * Identificador de rango rentado.
     * @param rentado BigDecimal
     */
    public void setRentado(BigDecimal rentado) {
        this.rentado = rentado;
    }

    /**
     * Serie asociada a la numeración.
     * @return SerieNng
     */
    public SerieNng getSerie() {
        return serie;
    }

    /**
     * Serie asociada a la numeración.
     * @param serie SerieNng
     */
    public void setSerie(SerieNng serie) {
        this.serie = serie;
    }

    /**
     * Referencia a la clave de Servicio asociada a la numeración.
     * @return ClaveServicio
     */
    public ClaveServicio getClaveServicio() {
        return claveServicio;
    }

    /**
     * Referencia a la clave de Servicio asociada a la numeración.
     * @param claveServicio ClaveServicio
     */
    public void setClaveServicio(ClaveServicio claveServicio) {
        this.claveServicio = claveServicio;
    }

    /**
     * Identificador de la Solicitud.
     * @return Solicitud
     */
    public Solicitud getSolicitud() {
        return solicitud;
    }

    /**
     * Identificador de la Solicitud.
     * @param solicitud Solicitud
     */
    public void setSolicitud(Solicitud solicitud) {
        this.solicitud = solicitud;
    }

    /**
     * Numeracion solicitada.
     * @return the numeracionSolicitada
     */
    public NumeracionSolicitadaNng getNumeracionSolicitada() {
        return numeracionSolicitada;
    }

    /**
     * Numeracion solicitada.
     * @param numeracionSolicitada the numeracionSolicitada to set
     */
    public void setNumeracionSolicitada(NumeracionSolicitadaNng numeracionSolicitada) {
        this.numeracionSolicitada = numeracionSolicitada;

    }

    @Override
    public String getIdentificadorRango() {
        if (id == null) {
            return null;
        } else {
            return this.id.toString();
        }
    }

    @Override
    public int getNumInicioAsInt() throws Exception {
        return (Integer.valueOf(numInicio).intValue());
    }

    @Override
    public int getNumFinalAsInt() throws Exception {
        return (Integer.valueOf(numFinal).intValue());
    }

    @Override
    public BigDecimal getSna() {
        return this.id.getSna();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RangoNng = {ClaveServicio=");
        builder.append(id.getIdClaveServicio());
        builder.append(", Sna=");
        builder.append(id.getSna());
        builder.append(", numInicio=");
        builder.append(numInicio);
        builder.append(", numFinal=");
        builder.append(numFinal);
        builder.append(", asignatario=");
        builder.append(asignatario.getId());
        builder.append(", concesionario=");
        if (concesionario != null) {
            builder.append(concesionario.getId());
        }
        builder.append(", arrendatario=");
        if (arrendatario != null) {
            builder.append(arrendatario.getId());
        }
        builder.append("}");
        return builder.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        // Clonamos todas las variables excepto el ID para la PK
        RangoSerieNngPK rangoClonedPk = new RangoSerieNngPK();
        rangoClonedPk.setIdClaveServicio(this.id.getIdClaveServicio());
        rangoClonedPk.setSna(this.id.getSna());

        // Clonamos todas las variables excepto la Solicitud
        RangoSerieNng rangoCloned = new RangoSerieNng();
        rangoCloned.setId(rangoClonedPk);
        rangoCloned.setConcesionario(this.concesionario);
        rangoCloned.setArrendatario(this.arrendatario);
        rangoCloned.setAsignatario(this.getAsignatario());
        rangoCloned.setEstatus(this.estatus);
        rangoCloned.setAbc(this.abc);
        rangoCloned.setBcd(this.bcd);
        rangoCloned.setFechaAsignacion(this.fechaAsignacion);
        rangoCloned.setFechaFinReserva(this.fechaFinReserva);
        rangoCloned.setNumFinal(this.numFinal);
        rangoCloned.setNumInicio(this.numInicio);
        rangoCloned.setObservaciones(this.observaciones);
        rangoCloned.setRentado(this.rentado);
        rangoCloned.setSerie(this.serie);
        rangoCloned.setClaveServicio(this.claveServicio);
        rangoCloned.setCliente(this.cliente);
        rangoCloned.setConsecutivoAsignacion(this.consecutivoAsignacion);
        rangoCloned.setOficioAsignacion(this.oficioAsignacion);
        rangoCloned.setNumeracionSolicitada(this.numeracionSolicitada);

        return rangoCloned;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RangoSerieNng)) {
            return false;
        }

        // Solamente se pueden comparar Rangos con PK. Si no existe PK, es necesario hacer una comparación
        // de los atributos del Rango (Nir, Abn, Etc.) en un método independiente.
        // Ver métodos de RangosSeriesUtils.

        RangoSerieNng other = (RangoSerieNng) obj;
        if (this.id == null || other.id == null) {
            return false;
        }

        return (this.id.getId().intValue() == other.id.getId().intValue());
    }

    @Override
    public int hashCode() {
        return (id != null)
                ? (this.getClass().hashCode() + id.hashCode())
                : super.hashCode();
    }

    /**
     * Obtiene el abc con 0 delante.
     * @return abc
     */
    public String getAbcAsString() {
        return this.abc != null ? String.format("%03d", this.abc.intValue()) : "";
    }

    /**
     * Obtiene el bcd con 0 delante.
     * @return bcd
     */
    public String getBcdAsString() {
        return this.bcd != null ? String.format("%03d", this.bcd.intValue()) : "";
    }
}
