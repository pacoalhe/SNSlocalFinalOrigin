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
import mx.ift.sns.modelo.pst.ProveedorConvenio;
import mx.ift.sns.modelo.pst.TipoModalidad;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.modelo.solicitud.EstadoCesionSolicitada;

/**
 * Representa una Cesión Solicitada de Numeración Geográfica. Contiene la información de la numeración seleccionada para
 * ceder y el estatus del proceso.
 */
@Entity
@Table(name = "NG_CESION_SOLICITADA")
@SequenceGenerator(name = "SEQ_ID_CES_SOLI", sequenceName = "SEQ_ID_CES_SOLI", allocationSize = 1)
@NamedQuery(name = "CesionSolicitadaNg.findAll", query = "SELECT c FROM CesionSolicitadaNg c")
public class CesionSolicitadaNg implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_CES_SOLI")
    @Column(name = "ID_NG_CESION_SOLICITADA", unique = true, nullable = false)
    private BigDecimal id;

    /** Relación: Una solicitud de cesión puede tener muchas cesiones aplicadas. */
    @OneToMany(mappedBy = "cesionSolicitada")
    private List<Cesion> cesiones;

    /** Relación: Muchas cesiones solicitadas pueden ser de un mismo tipo de central. */
    @ManyToOne
    @JoinColumn(name = "ID_CENTRAL_DESTINO", nullable = false)
    private Central centralDestino;

    /** Relación: Muchas cesiones solicitadas pueden ser de un mismo tipo de central. */
    @ManyToOne
    @JoinColumn(name = "ID_CENTRAL_ORIGEN", nullable = false)
    private Central centralOrigen;

    /** Índice de final de la numeración a ceder. */
    @Column(name = "NUM_FINAL", length = 4)
    private String numFinal;

    /** Índice de inicio de la numeración a ceder. */
    @Column(name = "NUM_INICIO", length = 4)
    private String numInicio;

    /** Relación: Muchas cesiones solicitadas pueden pertenecer a una misma solicitud de cesión. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL", nullable = false)
    private SolicitudCesionNg solicitudCesion;

    /** Relación: Muchas cesiones solicitadas pueden ser de una misma población. */
    @ManyToOne
    @JoinColumn(name = "ID_POBLACION", nullable = false)
    private Poblacion poblacion;

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

    /** Relación: Muchas cesiones Solicitadas pueden tener el mismo Proveedor Concesionario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_CONCESIONARIO", nullable = true)
    private Proveedor proveedorConcesionario;

    /** Relación: Muchas cesiones solicitadas pueden tener el mismo tipo de modalidad. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_MODALIDAD", nullable = false)
    private TipoModalidad tipoModalidad;

    /** Relación: Muchas cesiones solicitadas pueden tener el mismo tipo de red. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_RED", nullable = false)
    private TipoRed tipoRed;

    /** Identificador de NIR. */
    @Column(name = "ID_NIR", nullable = false)
    private BigDecimal idNir;

    /** Identificador de la Serie. */
    @Column(name = "ID_SERIE", nullable = false, length = 5)
    private BigDecimal sna;

    /** Fecha de ejecución de la cesión solicitada. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_CESION", nullable = true)
    private Date fechaCesion;

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

    /** Relación: Muchas Cesiones Solicitadas pueden tener el mismo estado. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS_CESION", nullable = false)
    private EstadoCesionSolicitada estado;

    /** Indica si utilizar el IDO de un Cesionario Comercializador en una Cesión. */
    @Column(name = "USAR_IDO_CESIONARIO", nullable = true, length = 1)
    private String usarIdoCesionario;

    /** Indica si utilizar el Convenio de un Cesionario Comercializador en una Cesión. */
    @Column(name = "USAR_CONVENIO_CESIONARIO", nullable = true, length = 1)
    private String usarConvenioConcesionario;

    /** Convenio del Proveedor Cesionario Concesionario utilizado para enrutar la numeración. */
    @ManyToOne
    @JoinColumn(name = "ID_CONVENIO_CESIONARIO", nullable = true)
    private ProveedorConvenio convenioCesionario;

    /** Indica si la cesión genera fraccionamiento sobre la numeración. */
    @Column(name = "FRACCIONAMIENTO_RANGO", nullable = true, length = 1)
    private String fraccionamientoRango;

    /** Número de Oficio de Asignación del Rango seleccionado para Ceder. */
    @Column(name = "NUM_OFICIO_ASIG_RANGO", nullable = true, length = 55)
    private String numOficioRango;

    /** Referencia del identificador de la Serie con la que se creó la cesión solicitada. */
    @Column(name = "ID_SERIE_INICIAL", nullable = true, length = 5)
    private BigDecimal idSerieInicial;

    /** Constructor, por defecto vacío. */
    public CesionSolicitadaNg() {
    }

    /**
     * Identificador de la cesión solicitada.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return this.id;
    }

    /**
     * Identificador de la cesión solicitada.
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Central destino de la población asociada a la numeración a ceder.
     * @return Central
     */
    public Central getCentralDestino() {
        return centralDestino;
    }

    /**
     * Central destino de la población asociada a la numeración a ceder.
     * @param centralDestino Central
     */
    public void setCentralDestino(Central centralDestino) {
        this.centralDestino = centralDestino;
    }

    /**
     * Central origen de la población asociada a la numeración a ceder.
     * @return Central
     */
    public Central getCentralOrigen() {
        return centralOrigen;
    }

    /**
     * Central origen de la población asociada a la numeración a ceder.
     * @param centralOrigen Central
     */
    public void setCentralOrigen(Central centralOrigen) {
        this.centralOrigen = centralOrigen;
    }

    /**
     * Índice de inicio de la numeración a ceder.
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
     * Índice de inicio de la numeración a ceder.
     * @param numInicio String
     */
    public void setNumInicio(String numInicio) {
        this.numInicio = numInicio;
    }

    /**
     * Índice de final de la numeración a ceder.
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
     * Índice de final de la numeración a ceder.
     * @param numFinal String
     */
    public void setNumFinal(String numFinal) {
        this.numFinal = numFinal;
    }

    /**
     * Población asociada a la numeración a ceder.
     * @return Poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * Población asociada a la numeración a ceder.
     * @param poblacion Poblacion
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * Asociación de la Cesión Solicitada con la solicitud que la originó.
     * @return SolicitudCesionNg
     */
    public SolicitudCesionNg getSolicitudCesion() {
        return solicitudCesion;
    }

    /**
     * Asociación de la Cesión Solicitada con la solicitud que la originó.
     * @param solicitudCesion SolicitudCesionNg
     */
    public void setSolicitudCesion(SolicitudCesionNg solicitudCesion) {
        this.solicitudCesion = solicitudCesion;
    }

    /**
     * Proveedor cesionario de la numeración. Es el proveedor que recibe la numeración.
     * @return Proveedor
     */
    public Proveedor getProveedorCesionario() {
        return proveedorCesionario;
    }

    /**
     * Proveedor cesionario de la numeración. Es el proveedor que recibe la numeración.
     * @param proveedorCesionario Proveedor
     */
    public void setProveedorCesionario(Proveedor proveedorCesionario) {
        this.proveedorCesionario = proveedorCesionario;
    }

    /**
     * Proveedor cedente de la numeración. Es el asignatario que da la numeración.
     * @return Proveedor
     */
    public Proveedor getProveedorCedente() {
        return proveedorCedente;
    }

    /**
     * Proveedor cedente de la numeración. Es el asignatario que da la numeración.
     * @param proveedorCedente Proveedor
     */
    public void setProveedorCedente(Proveedor proveedorCedente) {
        this.proveedorCedente = proveedorCedente;
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
     * Asociación de la cesión con la cesión solicitada que la originó.
     * @return List
     */
    public List<Cesion> getCesiones() {
        return cesiones;
    }

    /**
     * Asociación de la cesión con la cesión solicitada que la originó.
     * @param cesiones List
     */
    public void setCesiones(List<Cesion> cesiones) {
        this.cesiones = cesiones;
    }

    /**
     * Asocia una cesión aplicada con la solicitud de cesión.
     * @param pCesion Cesion
     * @return Cesion
     */
    public Cesion addCesion(Cesion pCesion) {
        this.getCesiones().add(pCesion);
        pCesion.setCesionSolicitada(this);
        return pCesion;
    }

    /**
     * Asocia una cesión aplicada con la solicitud de cesión.
     * @param pCesion Cesion
     * @return Cesion
     */
    public Cesion removeLiberacion(Cesion pCesion) {
        this.getCesiones().remove(pCesion);
        pCesion.setCesionSolicitada(null);
        return pCesion;
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
     * @return BigDecimal
     */
    public BigDecimal getSna() {
        return this.sna;
    }

    /**
     * Identificador de la Serie.
     * @param sna BigDecimal
     */
    public void setSna(BigDecimal sna) {
        this.sna = sna;
    }

    /**
     * Fecha de ejecución de la cesión solicitada.
     * @return Date
     */
    public Date getFechaCesion() {
        return fechaCesion;
    }

    /**
     * Fecha de ejecución de la cesión solicitada.
     * @param fechaCesion Date
     */
    public void setFechaCesion(Date fechaCesion) {
        this.fechaCesion = fechaCesion;
    }

    /**
     * Estatus de la Cesión Solicitada.
     * @return EstadoCesionSolicitada
     */
    public EstadoCesionSolicitada getEstado() {
        return estado;
    }

    /**
     * Estatus de la Cesión Solicitada.
     * @param estado EstadoCesionSolicitada
     */
    public void setEstado(EstadoCesionSolicitada estado) {
        this.estado = estado;
    }

    /**
     * Indica si se ha seleccionado utilizar el IDO del Proveedor Cesionario Concesionario que va a enrutar la
     * numeración cedida.
     * @return 'Y' en caso afirmativo, 'N' o null en caso negativo.
     */
    public String getUsarIdoCesionario() {
        return usarIdoCesionario;
    }

    /**
     * Indica si se ha seleccionado utilizar el IDO del Proveedor Cesionario Concesionario que va a enrutar la
     * numeración cedida.
     * @param usarIdoCesionario 'Y' en caso afirmativo, 'N' o null en caso negativo.
     */
    public void setUsarIdoCesionario(String usarIdoCesionario) {
        this.usarIdoCesionario = usarIdoCesionario;
    }

    /**
     * Indica si se ha seleccionado utilizar el Convenio del Proveedor Cesionario para enrutar la numeración cedida.
     * @return Convenio del Proveedor Cesionario con un Proveedor Concesionario
     */
    public String getUsarConvenioConcesionario() {
        return usarConvenioConcesionario;
    }

    /**
     * Indica si se ha seleccionado utilizar el Convenio del Proveedor Cesionario para enrutar la numeración cedida.
     * @param usarConvenioConcesionario Convenio del Proveedor Cesionario con un Proveedor Concesionario
     */
    public void setUsarConvenioConcesionario(String usarConvenioConcesionario) {
        this.usarConvenioConcesionario = usarConvenioConcesionario;
    }

    /**
     * Convenio del Proveedor Cesionario con un Proveedor Concesionario.
     * @return ProveedorConvenio
     */
    public ProveedorConvenio getConvenioCesionario() {
        return convenioCesionario;
    }

    /**
     * Convenio del Proveedor Cesionario con un Proveedor Concesionario.
     * @param convenioCesionario ProveedorConvenio
     */
    public void setConvenioCesionario(ProveedorConvenio convenioCesionario) {
        this.convenioCesionario = convenioCesionario;
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
     * Indica si la cesión genera fraccionamiento sobre la numeración.
     * @return 'S' si existe fraccionamiento.
     */
    public String getFraccionamientoRango() {
        return fraccionamientoRango;
    }

    /**
     * Indica si la cesión genera fraccionamiento sobre la numeración.
     * @param fraccionamientoRango 'S' si existe fraccionamiento.
     */
    public void setFraccionamientoRango(String fraccionamientoRango) {
        this.fraccionamientoRango = fraccionamientoRango;
    }

    /**
     * Número de Oficio de Asignación del Rango seleccionado para Ceder.
     * @return String
     */
    public String getNumOficioRango() {
        return numOficioRango;
    }

    /**
     * Número de Oficio de Asignación del Rango seleccionado para Ceder.
     * @param numOficioRango String
     */
    public void setNumOficioRango(String numOficioRango) {
        this.numOficioRango = numOficioRango;
    }

    /**
     * Referencia del identificador de la Serie con la que se creó la cesión solicitada.
     * @return BigDecimal
     */
    public BigDecimal getIdSerieInicial() {
        return idSerieInicial;
    }

    /**
     * Referencia del identificador de la Serie con la que se creó la cesión solicitada.
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
        if (!(other instanceof CesionSolicitadaNg)) {
            return false;
        }
        CesionSolicitadaNg castOther = (CesionSolicitadaNg) other;
        return (this.id.intValue() == castOther.id.intValue())
                && (this.idNir.intValue() == castOther.idNir.intValue())
                && (this.sna.intValue() == castOther.sna.intValue())
                && (this.numInicio.equals(castOther.numInicio))
                && (this.proveedorCesionario.equals(proveedorCesionario));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CesionSolicitadaNg={");
        builder.append("id=").append(this.id);
        builder.append(", idNir=").append(this.idNir);
        builder.append(", Sna=").append(this.sna);
        builder.append(", numInicio=").append(this.numInicio);
        builder.append(", numFinal=").append(this.numFinal);
        builder.append("}");
        return builder.toString();
    }

}
