package mx.ift.sns.modelo.series;

import mx.ift.sns.modelo.pst.Proveedor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Representa un movimiento sobre Series de Numeración No Geográfica. Contiene información sobre inserciones,
 * modificacicnes y eliminaciones de Series.
 */
@Entity
@Table(name = "HISTORICO_SERIE_NNG_VM")
@NamedQuery(name = "HistoricoSerieNng.findAll", query = "SELECT h FROM HistoricoSerieNng h")
public class HistoricoSerieNng implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Identificador interno. */
    @Id
    private String id;

    /** Código de Enrutamiento ABC. */
    private BigDecimal abc;

    /** Nombre del Proveedor Arrendatario. */
    private String arrendatario;

    /** Clave de servicio. */
    @Column(name = "CLAVE_SERVICIO")
    private BigDecimal claveServicio;

    /** Consecutivo del tramite. */
    private BigDecimal consecutivo;

    /** Fecha tramite. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_TRAMITE")
    private Date fechaTramite;

    /** Código de Enrutamiento IDA. */
    private BigDecimal ida;

    /** Nombre Proveedor asignatario. */
    @Column(name = "NOMBRE_PST")
    private String nombrePst;

    /** Número final. */
    @Column(name = "NUM_FINAL")
    private String numFinal;

    /** Número inicial. */
    @Column(name = "NUM_INICIAL")
    private String numInicial;

    /** Número oficio tramite. */
    @Column(name = "NUMERO_OFICIO", length = 55)
    private String numeroOficio;

    /** SNA de la serie. */
    private BigDecimal serie;

    /** Tipo de movimiento en la Serie. */
    @Column(name = "TIPO_MOVIMIENTO")
    private String tipoMovimiento;

    @ManyToOne(cascade={CascadeType.ALL})
    @JoinColumn(name = "NOMBRE_PST",referencedColumnName = "NOMBRE", insertable = false, updatable = false)
    private Proveedor pst;

    public Proveedor getPst() {
        return pst;
    }

    public void setPst(Proveedor pst) {
        this.pst = pst;
    }

    /** Contructor, vacio por defecto. */
    public HistoricoSerieNng() {
    }

    /**
     * Retorna la serie como un string con 0 delante.
     * @return serie
     */
    public String getSerieAsString() {
        return String.format("%03d", this.getSerie().intValue());
    }

    // GETTERS & SETTERS

    /**
     * Identificador interno.
     * @return String
     */
    public String getId() {
        return id;
    }

    /**
     * Identificador interno.
     * @param id String
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Código de Enrutamiento ABC.
     * @return BigDecimal
     */
    public BigDecimal getAbc() {
        return abc;
    }

    /**
     * Código de Enrutamiento ABC.
     * @param abc BigDecimal
     */
    public void setAbc(BigDecimal abc) {
        this.abc = abc;
    }

    /**
     * Nombre del Proveedor Arrendatario.
     * @return String
     */
    public String getArrendatario() {
        return arrendatario;
    }

    /**
     * Nombre del Proveedor Arrendatario.
     * @param arrendatario String
     */
    public void setArrendatario(String arrendatario) {
        this.arrendatario = arrendatario;
    }

    /**
     * Clave de servicio.
     * @return BigDecimal
     */
    public BigDecimal getClaveServicio() {
        return claveServicio;
    }

    /**
     * Clave de servicio.
     * @param claveServicio BigDecimal
     */
    public void setClaveServicio(BigDecimal claveServicio) {
        this.claveServicio = claveServicio;
    }

    /**
     * Consecutivo del tramite.
     * @return BigDecimal
     */
    public BigDecimal getConsecutivo() {
        return consecutivo;
    }

    /**
     * Consecutivo del tramite.
     * @param consecutivo BigDecimal
     */
    public void setConsecutivo(BigDecimal consecutivo) {
        this.consecutivo = consecutivo;
    }

    /**
     * Fecha tramite.
     * @return Date
     */
    public Date getFechaTramite() {
        return fechaTramite;
    }

    /**
     * Fecha tramite.
     * @param fechaTramite Date
     */
    public void setFechaTramite(Date fechaTramite) {
        this.fechaTramite = fechaTramite;
    }

    /**
     * Código de Enrutamiento IDA.
     * @return BigDecimal
     */
    public BigDecimal getIda() {
        return ida;
    }

    /**
     * Código de Enrutamiento IDA.
     * @param ida BigDecimal
     */
    public void setIda(BigDecimal ida) {
        this.ida = ida;
    }

    /**
     * Nombre Proveedor asignatario.
     * @return String
     */
    public String getNombrePst() {
        return nombrePst;
    }

    /**
     * Nombre Proveedor asignatario.
     * @param nombrePst String
     */
    public void setNombrePst(String nombrePst) {
        this.nombrePst = nombrePst;
    }

    /**
     * Numeror final.
     * @return String
     */
    public String getNumFinal() {
        return numFinal;
    }

    /**
     * Numeror final.
     * @param numFinal String
     */
    public void setNumFinal(String numFinal) {
        this.numFinal = numFinal;
    }

    /**
     * Número inicial.
     * @return String
     */
    public String getNumInicial() {
        return numInicial;
    }

    /**
     * Número inicial.
     * @param numInicial String
     */
    public void setNumInicial(String numInicial) {
        this.numInicial = numInicial;
    }

    /**
     * Número oficio tramite.
     * @return String
     */
    public String getNumeroOficio() {
        return numeroOficio;
    }

    /**
     * Número oficio tramite.
     * @param numeroOficio String
     */
    public void setNumeroOficio(String numeroOficio) {
        this.numeroOficio = numeroOficio;
    }

    /**
     * SNA de la serie.
     * @return BigDecimal
     */
    public BigDecimal getSerie() {
        return serie;
    }

    /**
     * SNA de la serie.
     * @param serie BigDecimal
     */
    public void setSerie(BigDecimal serie) {
        this.serie = serie;
    }

    /**
     * Tipo de movimiento en la Serie.
     * @return String
     */
    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    /**
     * Tipo de movimiento en la Serie.
     * @param tipoMovimiento String
     */
    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
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
    public String getIdaAsString() {
        return this.ida != null ? String.format("%03d", this.ida.intValue()) : "";
    }

}
