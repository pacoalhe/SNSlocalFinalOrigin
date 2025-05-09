package mx.ift.sns.modelo.ng;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import mx.ift.sns.modelo.central.Central;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoModalidad;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.modelo.solicitud.EstadoRedistribucionSolicitada;

/**
 * Representa una Redistribución Solicitada de Numeración Geográfica. Contiene la información de la numeración
 * seleccionada para redistribuir y el estatus del proceso.
 */
@Entity
@Table(name = "NG_REDISTRIBUCION_SOL")
@SequenceGenerator(name = "SEQ_ID_REDIST_SOLI", sequenceName = "SEQ_ID_REDIST_SOLI", allocationSize = 1)
@NamedQuery(name = "RedistribucionSolicitadaNg.findAll", query = "SELECT r FROM RedistribucionSolicitadaNg r")
public class RedistribucionSolicitadaNg implements Serializable, Cloneable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_REDIST_SOLI")
    @Column(name = "ID_NG_REDISTRIBUCION_SOL", unique = true, nullable = false)
    private BigDecimal id;

    /** Relación: Muchas Redistribuciones Solicitadas pueden ser de un mismo tipo de central. */
    @ManyToOne
    @JoinColumn(name = "ID_CENTRAL_ORIGEN", nullable = false)
    private Central centralOrigen;

    /** Relación: Muchas Redistribuciones Solicitadas pueden ser de un mismo tipo de central. */
    @ManyToOne
    @JoinColumn(name = "ID_CENTRAL_DESTINO", nullable = false)
    private Central centralDestino;

    /** Relación: Muchas Redistribuciones Solicitadas pueden ser de una misma solicitud. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL", nullable = false)
    private SolicitudRedistribucionNg solicitudRedistribucion;

    /** Relación: Muchas Redistribuciones Solicitadas pueden ser de una misma población. */
    @ManyToOne
    @JoinColumn(name = "ID_POBLACION", nullable = false)
    private Poblacion poblacion;

    /** Relación: Muchas Redistribuciones Solicitadas pueden tener el mismo Proveedor Concesionario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_CONVENIO", nullable = true)
    private Proveedor proveedorConcesionario;

    /** Relación: Muchas Redistribuciones Solicitadas pueden tener el mismo Proveedor Solicitante. */
    @ManyToOne
    @JoinColumn(name = "ID_PST", nullable = false)
    private Proveedor proveedorSolicitante;

    /** Relación: Muchas Redistribuciones Solicitadas pueden tener el mismo Proveedor Arrendatario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_ARRENDATARIO", nullable = true)
    private Proveedor proveedorArrendatario;

    /** Relación: Muchas Redistribuciones Solicitadas pueden tener el mismo tipo de modalidad de red. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_MODALIDAD", nullable = true)
    private TipoModalidad tipoModalidad;

    /** Relación: Muchas Redistribuciones Solicitadas pueden tener el mismo tipo de red. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_RED", nullable = false)
    private TipoRed tipoRed;

    /** Relación: Una redistribución solicitada puede tener muchas numeraciones redistribuidas. */
    @OneToMany(mappedBy = "redistribucionSolicitada", fetch = FetchType.EAGER)
    private List<NumeracionRedistribuida> numeracionesRedistribuidas;

    /** Índice de final de la numeración a liberar. */
    @Column(name = "NUM_FINAL", length = 4)
    private String numFinal;

    /** Índice de inicio de la numeración a liberar. */
    @Column(name = "NUM_INICIO", length = 4)
    private String numInicio;

    /** Identificador de NIR. */
    @Column(name = "ID_NIR", nullable = false)
    private BigDecimal idNir;

    /** Identificador de la Serie. */
    @Column(name = "ID_SERIE", nullable = false)
    private BigDecimal sna;

    /** Relación: Muchas Redistribuciones Solicitadas pueden tener el mismo estado. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS_REDIST", nullable = false)
    private EstadoRedistribucionSolicitada estado;

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

    /** Consecutivo del trámite de asignación que generó la numeración. */
    @Column(name = "CONSECUTIVO_ASIGNACION", nullable = true)
    private BigDecimal consecutivoAsignacion;

    /** Indica si la cesión genera fraccionamiento sobre la numeración. */
    @Column(name = "FRACCIONAMIENTO_RANGO", nullable = true, length = 1)
    private String fraccionamientoRango;

    /** Referencia del identificador de la Serie con la que se creó la redistribución solicitada. */
    @Column(name = "ID_SERIE_INICIAL", nullable = true, length = 5)
    private BigDecimal idSerieInicial;

    /** Constructor, por defecto vacío. */
    public RedistribucionSolicitadaNg() {
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
     * Central destino de la población asociada a la numeración a fraccionar.
     * @return Central
     */
    public Central getCentralDestino() {
        return centralDestino;
    }

    /**
     * Central destino de la población asociada a la numeración a fraccionar.
     * @param centralDestino Central
     */
    public void setCentralDestino(Central centralDestino) {
        this.centralDestino = centralDestino;
    }

    /**
     * Central origen de la población asociada a la numeración a fraccionar.
     * @return Central
     */
    public Central getCentralOrigen() {
        return centralOrigen;
    }

    /**
     * Central origen de la población asociada a la numeración a fraccionar.
     * @param centralOrigen Central
     */
    public void setCentralOrigen(Central centralOrigen) {
        this.centralOrigen = centralOrigen;
    }

    /**
     * Población asociada a la numeración a redistribuir.
     * @return Poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * Población asociada a la numeración a redistribuir.
     * @param poblacion Poblacion
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
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

    /** @return solicitudRedistribucion */
    public SolicitudRedistribucionNg getSolicitudRedistribucion() {
        return solicitudRedistribucion;
    }

    /** @param solicitudRedistribucion */
    public void setSolicitudRedistribucion(SolicitudRedistribucionNg solicitudRedistribucion) {
        this.solicitudRedistribucion = solicitudRedistribucion;
    }

    /**
     * Add numeracionesRedistribuidas.
     * @param pnumeracionesRedistribuidas NumeracionRedistribuida
     * @return NumeracionRedistribuida
     */
    public NumeracionRedistribuida addNumeracionRedistribuida(NumeracionRedistribuida pnumeracionesRedistribuidas) {
        getNumeracionesRedistribuidas().add(pnumeracionesRedistribuidas);
        pnumeracionesRedistribuidas.setRedistribucionSolicitada(this);
        return pnumeracionesRedistribuidas;
    }

    /**
     * Remove numeracionRedistribuida.
     * @param pnumeracionesRedistribuidas NumeracionRedistribuida
     * @return NumeracionRedistribuida
     */
    public NumeracionRedistribuida removeNumeracionRedistribuida(NumeracionRedistribuida pnumeracionesRedistribuidas) {
        getNumeracionesRedistribuidas().remove(pnumeracionesRedistribuidas);
        pnumeracionesRedistribuidas.setRedistribucionSolicitada(null);
        return pnumeracionesRedistribuidas;
    }

    /**
     * Identificador del IdNir.
     * @return BigDecimal
     */
    public BigDecimal getIdNir() {
        return idNir;
    }

    /**
     * Identificador del NIR.
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
     * identificador estado.
     * @return estado
     */
    public EstadoRedistribucionSolicitada getEstado() {
        return estado;
    }

    /**
     * EstadoRedistribucionSolicitada.
     * @param estado EstadoRedistribucionSolicitada
     */
    public void setEstado(EstadoRedistribucionSolicitada estado) {
        this.estado = estado;
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
     * Lista de Numeraciones redistribuidas por la Solicitud de redistribución.
     * @return List
     */
    public List<NumeracionRedistribuida> getNumeracionesRedistribuidas() {
        return numeracionesRedistribuidas;
    }

    /**
     * Lista de Numeraciones redistribuidas por la Solicitud de redistribución.
     * @param numeracionesRedistribuidas List
     */
    public void setNumeracionesRedistribuidas(List<NumeracionRedistribuida> numeracionesRedistribuidas) {
        this.numeracionesRedistribuidas = numeracionesRedistribuidas;
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
     * Referencia del identificador de la Serie con la que se creó la redistribución solicitada.
     * @return BigDecimal
     */
    public BigDecimal getIdSerieInicial() {
        return idSerieInicial;
    }

    /**
     * Referencia del identificador de la Serie con la que se creó la redistribución solicitada.
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
        if (!(other instanceof RedistribucionSolicitadaNg)) {
            return false;
        }
        RedistribucionSolicitadaNg castOther = (RedistribucionSolicitadaNg) other;
        return (this.id == castOther.id)
                && (this.idNir.intValue() == castOther.idNir.intValue())
                && (this.sna.intValue() == castOther.sna.intValue())
                && (this.numInicio == castOther.numInicio)
                && (this.proveedorSolicitante == castOther.proveedorSolicitante);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RedistribucionSolicitadaNg NG = {");
        builder.append("id=").append(this.id);
        builder.append(", idNir=").append(this.idNir);
        builder.append(", Sna=").append(this.sna);
        builder.append(", numInicio=").append(this.numInicio);
        builder.append(", numFinal=").append(this.numFinal);
        builder.append("}");
        return builder.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        // Clonamos todas las variables exception el Id
        RedistribucionSolicitadaNg redistCloned = (RedistribucionSolicitadaNg) super.clone();
        redistCloned.setId(null);
        return redistCloned;
    }
}
