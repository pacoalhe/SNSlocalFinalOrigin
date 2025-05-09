package mx.ift.sns.modelo.ng;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
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

import mx.ift.sns.modelo.central.Central;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoModalidad;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.series.IRangoSerie;
import mx.ift.sns.modelo.solicitud.Solicitud;

/**
 * Representa un Rango contenido en una Serie de Numeración Geográfica. Contiene información básica de la numeración.
 */
@Entity
@Table(name = "RANGO_SERIE")
@NamedQuery(name = "RangoSerie.findAll", query = "SELECT r FROM RangoSerie r")
public class RangoSerie implements Serializable, Cloneable, IRangoSerie {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Numeracion minima. */
    public static final String NUM_MIN = "0000";

    /** Numeracion máxima. */
    public static final String NUM_MAX = "9999";

    /** PK de la tabla. */
    @EmbeddedId
    private RangoSeriePK id;

    /** Tipo de modalidad de red Móvil. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_MODALIDAD", nullable = false)
    private TipoModalidad tipoModalidad;

    /** Tipo Red. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_RED", nullable = false)
    private TipoRed tipoRed;

    /** Final del rango de numeración. */
    @Column(name = "N_FINAL", nullable = false, precision = 4)
    private String numFinal;

    /** Inicio del rango de numeración. */
    @Column(name = "N_INICIO", nullable = false, precision = 4)
    private String numInicio;

    /** Observaciones. */
    @Column(length = 700)
    private String observaciones;

    /** Identificador 'rentado' del rango. */
    @Column(precision = 5)
    private BigDecimal rentado;

    /** Central de origen asociada a la numeración. */
    @ManyToOne
    @JoinColumn(name = "ID_CENTRAL_ORIGEN", nullable = false)
    private Central centralOrigen;

    /** Central de destino asociada a la numeración. */
    @ManyToOne
    @JoinColumn(name = "ID_CENTRAL_DESTINO", nullable = false)
    private Central centralDestino;

    /** Poblacion asociada a la numeración. */
    @ManyToOne
    @JoinColumn(name = "ID_POBLACION", nullable = false)
    private Poblacion poblacion;

    /** Proveedor concesionario del rango. Es el dueño del rango. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_CONCESIONARIO", nullable = true)
    private Proveedor concesionario;

    /** Proovedor arrendatario del rango. Se lo pide al arrendatario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_ARRENDATARIO", nullable = false)
    private Proveedor arrendatario;

    /** Proovedor asignatario del rango. Peticionario de la solicitud asociada al Rango. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_ASIGNATARIO", nullable = false)
    private Proveedor asignatario;

    /** Relación: Muchos rangos pueden tener el mismo estatus. */
    @ManyToOne
    @JoinColumn(name = "ID_STATUS_RANGO", nullable = false)
    private EstadoRango estadoRango;

    /** Relación: Muchos rangos pueden pertenecer al mismo NIR y Serie. */
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ID_NIR", referencedColumnName = "ID_NIR",
                    nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "ID_SERIE", referencedColumnName = "ID_SERIE",
                    nullable = false, insertable = false, updatable = false)
    })
    private Serie serie;

    /** Relación: Muchos rangos pueden haber sido asignados / editados por la misma Solicitud. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL", nullable = false)
    private Solicitud solicitud;

    /** Relación: Muchos rangos pueden haber sido asignados por la misma Numeración Solicitada. */
    @ManyToOne
    @JoinColumn(name = "ID_NUM_SOLICITADA", nullable = true)
    private NumeracionSolicitada numSolicitada;

    /** Código IDA. */
    @Column(name = "IDA_PNN", precision = 3)
    private BigDecimal idaPnn;

    /** Código IDO. */
    @Column(name = "IDO_PNN", precision = 3)
    private BigDecimal idoPnn;

    /** Fecha de asignación de la numeración. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_SOL_ASIG", nullable = true)
    private Date fechaAsignacion;

    /** Consecutivo del trámite de asignación que asignó la numeración. */
    @Column(name = "ID_SOL_ASIG", nullable = true)
    private BigDecimal consecutivoAsignacion;

    /** Número de Oficio de Asignación del Rango. */
    @Column(name = "OFICIO_SOL_ASIG", nullable = true, length = 55)
    private String oficioAsignacion;

    /** Version JPA. */
    @Version
    private long version;

    /**
     * Identificador de la numeración.
     * @return RangoSeriePK
     */
    public RangoSeriePK getId() {
        return id;
    }

    /**
     * Identificador de la numeración.
     * @param id RangoSeriePK
     */
    public void setId(RangoSeriePK id) {
        this.id = id;
    }

    /**
     * Observaciones.
     * @return String
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * Observaciones.
     * @param observaciones String
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * Identificador 'rentado' del rango.
     * @return BigDecimal
     */
    public BigDecimal getRentado() {
        return rentado;
    }

    /**
     * Identificador 'rentado' del rango.
     * @param rentado BigDecimal
     */
    public void setRentado(BigDecimal rentado) {
        this.rentado = rentado;
    }

    /**
     * Numeración solicitada asociada a la asignación del rango.
     * @return NumeracionSolicitada
     */
    public NumeracionSolicitada getNumSolicitada() {
        return numSolicitada;
    }

    /**
     * Numeración solicitada asociada a la asignación del rango.
     * @param numSolicitada NumeracionSolicitada
     */
    public void setNumSolicitada(NumeracionSolicitada numSolicitada) {
        this.numSolicitada = numSolicitada;
    }

    /**
     * Tipo de modalidad de red Móvil.
     * @return TipoModalidad
     */
    public TipoModalidad getTipoModalidad() {
        return tipoModalidad;
    }

    /**
     * Tipo de modalidad de red Móvil.
     * @param tipoModalidad TipoModalidad
     */
    public void setTipoModalidad(TipoModalidad tipoModalidad) {
        this.tipoModalidad = tipoModalidad;
    }

    /**
     * Tipo Red.
     * @return TipoRed
     */
    public TipoRed getTipoRed() {
        return tipoRed;
    }

    /**
     * Tipo Red.
     * @param tipoRed TipoRed
     */
    public void setTipoRed(TipoRed tipoRed) {
        this.tipoRed = tipoRed;
    }

    /**
     * Final del rango de numeración.
     * @return String
     */
    public String getNumFinal() {
        return numFinal;
    }

    /**
     * Final del rango de numeración.
     * @param numFinal String
     */
    public void setNumFinal(String numFinal) {
        this.numFinal = numFinal;
    }

    /**
     * Inicio del rango de numeración.
     * @return String
     */
    public String getNumInicio() {
        return numInicio;
    }

    /**
     * Inicio del rango de numeración.
     * @param numInicio String
     */
    public void setNumInicio(String numInicio) {
        this.numInicio = numInicio;
    }

    /**
     * Central de origen asociada a la numeración.
     * @return Central
     */
    public Central getCentralOrigen() {
        return centralOrigen;
    }

    /**
     * Central de origen asociada a la numeración.
     * @param centralOrigen Central
     */
    public void setCentralOrigen(Central centralOrigen) {
        this.centralOrigen = centralOrigen;
    }

    /**
     * Central de destino asociada a la numeración.
     * @return Central
     */
    public Central getCentralDestino() {
        return centralDestino;
    }

    /**
     * Central de destino asociada a la numeración.
     * @param centralDestino Central
     */
    public void setCentralDestino(Central centralDestino) {
        this.centralDestino = centralDestino;
    }

    /**
     * Identificador del proveedor que solicita la numeración al arrendatario.
     * @return Proveedor arrendatario
     */
    public Proveedor getArrendatario() {
        return arrendatario;
    }

    /**
     * Identificador del proveedor que solicita la numeración al arrendatario.
     * @param arrendatario Proveedor arrendatario
     */
    public void setArrendatario(Proveedor arrendatario) {
        this.arrendatario = arrendatario;
    }

    /**
     * Proveedor peticionario de la solicitud asociada al Rango.
     * @return Proveedor asignatario
     */
    public Proveedor getAsignatario() {
        return asignatario;
    }

    /**
     * Proveedor peticionario de la solicitud asociada al Rango.
     * @param asignatario Proveedor asignatario
     */
    public void setAsignatario(Proveedor asignatario) {
        this.asignatario = asignatario;
    }

    /**
     * Estatus del rango.
     * @return EstadoRango
     */
    public EstadoRango getEstadoRango() {
        return estadoRango;
    }

    /**
     * Estatus del rango.
     * @param estadoRango EstadoRango
     */
    public void setEstadoRango(EstadoRango estadoRango) {
        this.estadoRango = estadoRango;
    }

    /**
     * Serie a la que pertenece la numeración.
     * @return Serie
     */
    public Serie getSerie() {
        return serie;
    }

    /**
     * Serie a la que pertenece la numeración.
     * @param serie Serie
     */
    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    /**
     * Solicitud que asignó o modificó el rango por última vez.
     * @return Solicitud
     */
    public Solicitud getSolicitud() {
        return solicitud;
    }

    /**
     * Solicitud que asignó o modificó el rango por última vez.
     * @param solicitud Solicitud
     */
    public void setSolicitud(Solicitud solicitud) {
        this.solicitud = solicitud;
    }

    /**
     * Poblacion asociada a la numeración.
     * @return Poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * Poblacion asociada a la numeración.
     * @param poblacion Poblacion
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * Código IDA del Plan de Numeración.
     * @return BigDecimal
     */
    public BigDecimal getIdaPnn() {
        return idaPnn;
    }

    /**
     * Código IDA del Plan de Numeración.
     * @param idaPnn BigDecimal
     */
    public void setIdaPnn(BigDecimal idaPnn) {
        this.idaPnn = idaPnn;
    }

    /**
     * Código IDO del Plan de Numeración.
     * @return BigDecimal
     */
    public BigDecimal getIdoPnn() {
        return idoPnn;
    }

    /**
     * Código IDO del Plan de Numeración.
     * @param idoPnn BigDecimal
     */
    public void setIdoPnn(BigDecimal idoPnn) {
        this.idoPnn = idoPnn;
    }

    /**
     * Proveedor dueño del rango de numeración.
     * @return Proveedor concesionario.
     */
    public Proveedor getConcesionario() {
        return concesionario;
    }

    /**
     * Proveedor dueño del rango de numeración.
     * @param concesionario Proveedor concesionario.
     */
    public void setConcesionario(Proveedor concesionario) {
        this.concesionario = concesionario;
    }

    /**
     * Fecha de asignación de la numeración.
     * @return Date
     */
    public Date getFechaAsignacion() {
        return fechaAsignacion;
    }

    /**
     * Fecha de asignación de la numeración.
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("rango={idnir=");
        builder.append(id.getIdNir());

        builder.append("  sna=");
        builder.append(id.getSna());

        builder.append("  numInicio=");
        builder.append(numInicio);

        builder.append("  numFinal=");
        builder.append(numFinal);

        builder.append("  asignatario=");
        builder.append(asignatario.getId());

        builder.append("  concesionario=");
        if (concesionario != null) {
            builder.append(concesionario.getId());
        }

        builder.append("  arrendatario=");
        if (arrendatario != null) {
            builder.append(arrendatario.getId());
        }

        builder.append("}");

        return builder.toString();
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
    public Object clone() throws CloneNotSupportedException {
        // Clonamos todas las variables excepto el ID para la PK
        RangoSeriePK rangoClonedPk = new RangoSeriePK();
        rangoClonedPk.setIdNir(this.getId().getIdNir());
        rangoClonedPk.setSna(this.id.getSna());

        // Clonamos todas las variables excepto la Solicitud
        RangoSerie rangoCloned = new RangoSerie();
        rangoCloned.setId(rangoClonedPk);
        rangoCloned.setConcesionario(this.concesionario);
        rangoCloned.setArrendatario(this.arrendatario);
        rangoCloned.setAsignatario(this.asignatario);
        rangoCloned.setCentralDestino(this.centralDestino);
        rangoCloned.setCentralOrigen(this.centralOrigen);
        rangoCloned.setEstadoRango(this.estadoRango);
        rangoCloned.setIdaPnn(this.idaPnn);
        rangoCloned.setIdoPnn(this.idoPnn);
        rangoCloned.setNumFinal(this.numFinal);
        rangoCloned.setNumInicio(this.numInicio);
        rangoCloned.setObservaciones(this.observaciones);
        rangoCloned.setPoblacion(this.poblacion);
        rangoCloned.setRentado(this.rentado);
        rangoCloned.setSerie(this.serie);
        rangoCloned.setTipoModalidad(this.tipoModalidad);
        rangoCloned.setTipoRed(this.tipoRed);
        rangoCloned.setConsecutivoAsignacion(this.consecutivoAsignacion);
        rangoCloned.setOficioAsignacion(this.oficioAsignacion);
        rangoCloned.setFechaAsignacion(this.fechaAsignacion);
        rangoCloned.setNumSolicitada(this.numSolicitada);
        return rangoCloned;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RangoSerie)) {
            return false;
        }

        // Solamente se pueden comparar Rangos con PK. Si no existe PK, es necesario hacer una comparación
        // de los atributos del Rango (Nir, Abn, Etc.) en un método independiente.
        // Ver métodos de RangosSeriesUtils.

        RangoSerie other = (RangoSerie) obj;
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
     * Indica si el Rango tiene el tipo de Red indicado por parámetro.
     * @param pCdgTipoRed Código del Tipo de Red.
     * @return 'True' Si el tipo de red del Rango coincide con el indicado.
     */
    public boolean isTipoRed(String pCdgTipoRed) {
        if (tipoRed != null) {
            return (this.tipoRed.getCdg().equals(pCdgTipoRed));
        } else {
            return false;
        }
    }

    /**
     * Obtiene el ida con 0 delante.
     * @return ida
     */
    public String getIdaPnnAsString() {
        return this.idaPnn != null ? String.format("%03d", this.idaPnn.intValue()) : "";
    }

    /**
     * Obtiene el ido con 0 delante.
     * @return ido
     */
    public String getIdoPnnAsString() {
        return this.idoPnn != null ? String.format("%03d", this.idoPnn.intValue()) : "";
    }

	public static Comparator<RangoSerie> numInicioComparator = new Comparator<RangoSerie>() {

		public int compare(RangoSerie rango1, RangoSerie rango2) {

			int numInicioRango1 = Integer.parseInt(rango1.numInicio);
			int numInicioRango2 = Integer.parseInt(rango2.numInicio);

			// ascending order
			return numInicioRango1-numInicioRango2;

			// descending order
			// return fruitName2.compareTo(fruitName1);
		}
	};
}
