package mx.ift.sns.modelo.ng;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import mx.ift.sns.modelo.central.Central;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoModalidad;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.modelo.solicitud.EstadoLiberacionSolicitada;

/**
 * Representa una Liberación Solicitada de Numeración Geográfica. Contiene la información de la numeración seleccionada
 * para liberar y el estatus del proceso.
 */
@Entity
@Table(name = "NG_LIBERACION_SOLICITADA")
@SequenceGenerator(name = "SEQ_ID_LIBER_SOLI", sequenceName = "SEQ_ID_LIBER_SOLI", allocationSize = 1)
@NamedQuery(name = "LiberacionSolicitadaNg.findAll", query = "SELECT l FROM LiberacionSolicitadaNg l")
public class LiberacionSolicitadaNg implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_LIBER_SOLI")
    @Column(name = "ID_NG_LIBERACION_SOLICITADA", unique = true, nullable = false)
    private BigDecimal id;

    /** Relación: Muchas Liberaciones Solicitadas pueden ser de un mismo tipo de central. */
    @ManyToOne
    @JoinColumn(name = "ID_CENTRAL_DESTINO", nullable = false)
    private Central centralDestino;

    /** Relación: Muchas Liberaciones Solicitadas pueden ser de un mismo tipo de central. */
    @ManyToOne
    @JoinColumn(name = "ID_CENTRAL_ORIGEN", nullable = false)
    private Central centralOrigen;

    /** Relación: Muchas Liberaciones Solicitadas pueden ser de una misma solicitud. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL", nullable = false)
    private SolicitudLiberacionNg solicitudLiberacion;

    /** Relación: Muchas Liberaciones Solicitadas pueden ser de una misma población. */
    @ManyToOne
    @JoinColumn(name = "ID_POBLACION", nullable = false)
    private Poblacion poblacion;

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

    /** Relación: Muchas Liberaciones Solicitadas pueden tener el mismo tipo de modalidad de red. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_MODALIDAD", nullable = false)
    private TipoModalidad tipoModalidad;

    /** Relación: Muchas Liberaciones Solicitadas pueden tener el mismo tipo de red. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_RED", nullable = false)
    private TipoRed tipoRed;

    /** Relación: Una solicitud de liberación puede tener muchas liberaciones aplicadas. */
    @OneToMany(mappedBy = "liberacionSolicitada")
    private List<Liberacion> liberaciones;

    /** Índice de final de la numeración a liberar. */
    @Column(name = "NUM_FINAL", length = 4)
    private String numFinal;

    /** Índice de inicio de la numeración a liberar. */
    @Column(name = "NUM_INICIO", length = 4)
    private String numInicio;

    /** Fecha de aplicación de la liberación. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_LIBERACION")
    private Date fechaLiberacion;

    /** Identificador de la Serie. */
    @Column(name = "ID_SERIE", nullable = false)
    private BigDecimal sna;

    /** Identificador de NIR. */
    @Column(name = "ID_NIR", nullable = false)
    private BigDecimal idNir;

    /** Código de NIR. */
    @Column(name = "CDG_NIR", nullable = true, length = 3)
    private int cdgNir;

    /** Identificador de ABN. */
    @Column(name = "ID_ABN", nullable = true, length = 3)
    private BigDecimal idAbn;

    /** IDO Plan de Numeración. */
    @Column(name = "IDO_PNN", nullable = true, length = 3)
    private BigDecimal idoPnn;

    /** IDA Plan de Numeración. */
    @Column(name = "IDA_PNN", nullable = true, length = 3)
    private BigDecimal idaPnn;

    /** Fecha de asignación de la numeración en cesión. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_ASIGNACION", nullable = true)
    private Date fechaAsignacion;

    /** Consecutivo del trámite de asignación que generó la numeración. */
    @Column(name = "CONSECUTIVO_ASIGNACION", nullable = true)
    private BigDecimal consecutivoAsignacion;

    /** Relación: Muchas Liberaciones Solicitadas pueden tener el mismo estado. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS_LIBERACION", nullable = false)
    private EstadoLiberacionSolicitada estado;

    /** Indica si la liberación genera fraccionamiento sobre la numeración. */
    @Column(name = "FRACCIONAMIENTO_RANGO", nullable = true, length = 1)
    private String fraccionamientoRango;

    /** Número de Oficio de Asignación del Rango seleccionado para Liberar. */
    @Column(name = "NUM_OFICIO_ASIG_RANGO", nullable = true, length = 55)
    private String numOficioRango;

    /** Referencia del identificador de la Serie con la que se creó la liberación solicitada. */
    @Column(name = "ID_SERIE_INICIAL", nullable = true, length = 5)
    private BigDecimal idSerieInicial;

    /** Constructor, por defecto vacío. */
    public LiberacionSolicitadaNg() {
    }

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
     * Central destino de la población asociada a la numeración a liberar.
     * @return Central
     */
    public Central getCentralDestino() {
        return centralDestino;
    }

    /**
     * Central destino de la población asociada a la numeración a liberar.
     * @param centralDestino Central
     */
    public void setCentralDestino(Central centralDestino) {
        this.centralDestino = centralDestino;
    }

    /**
     * Central origen de la población asociada a la numeración a liberar.
     * @return Central
     */
    public Central getCentralOrigen() {
        return centralOrigen;
    }

    /**
     * Central origen de la población asociada a la numeración a liberar.
     * @param centralOrigen Central
     */
    public void setCentralOrigen(Central centralOrigen) {
        this.centralOrigen = centralOrigen;
    }

    /**
     * Solicitud de liberación asociada a ésta petición de liberación.
     * @return SolicitudLiberacionNg
     */
    public SolicitudLiberacionNg getSolicitudLiberacion() {
        return solicitudLiberacion;
    }

    /**
     * Solicitud de liberación asociada a ésta petición de liberación.
     * @param solicitudLiberacion SolicitudLiberacionNg
     */
    public void setSolicitudLiberacion(SolicitudLiberacionNg solicitudLiberacion) {
        this.solicitudLiberacion = solicitudLiberacion;
    }

    /**
     * Población asociada a la numeración a liberar.
     * @return Poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * Población asociada a la numeración a liberar.
     * @param poblacion Poblacion
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
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
     * Tipo de la modalidad de red.
     * @return TipoModalidad
     */
    public TipoModalidad getTipoModalidad() {
        return tipoModalidad;
    }

    /**
     * Tipo de la modalidad de red.
     * @param tipoModalidad TipoModalidad
     */
    public void setTipoModalidad(TipoModalidad tipoModalidad) {
        this.tipoModalidad = tipoModalidad;
    }

    /**
     * Tipo de red.
     * @return TipoRed
     */
    public TipoRed getTipoRed() {
        return tipoRed;
    }

    /**
     * Tipo de red.
     * @param tipoRed TipoRed
     */
    public void setTipoRed(TipoRed tipoRed) {
        this.tipoRed = tipoRed;
    }

    /**
     * Asociación de la liberación con la liberación solicitada.
     * @return List
     */
    public List<Liberacion> getLiberaciones() {
        return liberaciones;
    }

    /**
     * Asociación de la liberación con la liberación solicitada.
     * @param liberaciones List
     */
    public void setLiberaciones(List<Liberacion> liberaciones) {
        this.liberaciones = liberaciones;
    }

    /**
     * Asocia una liberación aplicada con la solicitud de liberación.
     * @param pLiberacion Liberacion
     * @return Liberacion
     */
    public Liberacion addLiberacion(Liberacion pLiberacion) {
        this.getLiberaciones().add(pLiberacion);
        pLiberacion.setLiberacionSolicitada(this);
        return pLiberacion;
    }

    /**
     * Asocia una liberación aplicada con la solicitud de liberación.
     * @param pLiberacion Liberacion
     * @return Liberacion
     */
    public Liberacion removeLiberacion(Liberacion pLiberacion) {
        this.getLiberaciones().remove(pLiberacion);
        pLiberacion.setLiberacionSolicitada(null);
        return pLiberacion;
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
     * Identificador de NIR.
     * @return BigDecimal
     */
    public BigDecimal getIdNir() {
        return this.idNir;
    }

    /**
     * Identificador de NIR.
     * @param idNir BigDecimal
     */
    public void setIdNir(BigDecimal idNir) {
        this.idNir = idNir;
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
     * Estado de LiberaciónSolicitada.
     * @return EstadoLiberacionSolicitada
     */
    public EstadoLiberacionSolicitada getEstado() {
        return estado;
    }

    /**
     * Estado de LiberaciónSolicitada.
     * @param estado EstadoLiberacionSolicitada
     */
    public void setEstado(EstadoLiberacionSolicitada estado) {
        this.estado = estado;
    }

    /**
     * Código de NIR.
     * @return int
     */
    public int getCdgNir() {
        return cdgNir;
    }

    /**
     * Código de NIR.
     * @param cdgNir int
     */
    public void setCdgNir(int cdgNir) {
        this.cdgNir = cdgNir;
    }

    /**
     * Identificador de ABN.
     * @return BigDecimal
     */
    public BigDecimal getIdAbn() {
        return idAbn;
    }

    /**
     * Identificador de ABN.
     * @param idAbn BigDecimal
     */
    public void setIdAbn(BigDecimal idAbn) {
        this.idAbn = idAbn;
    }

    /**
     * IDO de Plan de Numeración.
     * @return BigDecimal
     */
    public BigDecimal getIdoPnn() {
        return idoPnn;
    }

    /**
     * IDO de Plan de Numeración.
     * @param idoPnn BigDecimal
     */
    public void setIdoPnn(BigDecimal idoPnn) {
        this.idoPnn = idoPnn;
    }

    /**
     * IDA de Plan de Numeración.
     * @return BigDecimal
     */
    public BigDecimal getIdaPnn() {
        return idaPnn;
    }

    /**
     * IDA de Plan de Numeración.
     * @param idaPnn BigDecimal
     */
    public void setIdaPnn(BigDecimal idaPnn) {
        this.idaPnn = idaPnn;
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
     * Número de Oficio de Asignación del Rango seleccionado para Liberar.
     * @return String
     */
    public String getNumOficioRango() {
        return numOficioRango;
    }

    /**
     * Número de Oficio de Asignación del Rango seleccionado para Liberar.
     * @param numOficioRango String
     */
    public void setNumOficioRango(String numOficioRango) {
        this.numOficioRango = numOficioRango;
    }

    /**
     * Referencia del identificador de la Serie con la que se creó la liberación solicitada.
     * @return BigDecimal
     */
    public BigDecimal getIdSerieInicial() {
        return idSerieInicial;
    }

    /**
     * Referencia del identificador de la Serie con la que se creó la liberación solicitada.
     * @param idSerieInicial BigDecimal
     */
    public void setIdSerieInicial(BigDecimal idSerieInicial) {
        this.idSerieInicial = idSerieInicial;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof LiberacionSolicitadaNg)) {
            return false;
        }
        LiberacionSolicitadaNg castOther = (LiberacionSolicitadaNg) other;
        return (this.id == castOther.id)
                && (this.idNir.intValue() == castOther.idNir.intValue())
                && (this.sna.intValue() == castOther.sna.intValue())
                && (this.numInicio == castOther.numInicio)
                && (this.proveedorCesionario == castOther.proveedorCesionario);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LiberacionSolicitada NG = {");
        builder.append("id=").append(this.id);
        builder.append(", idNir=").append(this.idNir);
        builder.append(", Sna=").append(this.sna);
        builder.append(", numInicio=").append(this.numInicio);
        builder.append(", numFinal=").append(this.numFinal);
        builder.append("}");
        return builder.toString();
    }
}
