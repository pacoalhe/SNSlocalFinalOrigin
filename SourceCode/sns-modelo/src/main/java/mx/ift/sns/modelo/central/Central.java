package mx.ift.sns.modelo.central;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.Version;

import mx.ift.sns.modelo.abn.AbnCentral;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.usu.Auditoria;

/**
 * Representa una central.
 */
@Entity
@Table(name = "CAT_CENTRAL")
@SequenceGenerator(name = "SEQ_ID_CENTRAL", sequenceName = "SEQ_ID_CENTRAL", allocationSize = 1)
@NamedQuery(name = "Central.findAll", query = "SELECT c FROM Central c")
public class Central extends Auditoria implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_CENTRAL")
    @Column(name = "ID_CENTRAL")
    private BigDecimal id;

    /** Calle. */
    @Column(name = "CALLE", length = 100)
    private String calle;

    /** Capacidad final. */
    @Column(name = "CAPACIDAD_FINAL", precision = 10)
    private BigDecimal capacidadFinal;

    /** Capacidad inicial. */
    @Column(name = "CAPACIDAD_INICIAL", precision = 10)
    private BigDecimal capacidadInicial;

    /** Colonia. */
    @Column(name = "COLONIA", length = 200)
    private String colonia;

    /** Codigo Postal. */
    @Column(name = "CP", length = 10)
    private String cp;

    /** Jerarquía. */
    @Column(name = "JERARQUIA", length = 100)
    private String jerarquia;

    /** Latitud. */
    @Column(name = "LATITUD", nullable = false, length = 20)
    private String latitud;

    /** Longitud. */
    @Column(name = "LONGITUD", nullable = false, length = 20)
    private String longitud;

    /** Nombre. */
    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    /** Número. */
    @Column(name = "NUMERO", length = 30)
    private String numero;

    /** Relación: Una Central puede estar registrada en varios ABN. */
    @OneToMany(mappedBy = "central", cascade = CascadeType.ALL)
    private List<AbnCentral> abnCentrales;

    /** Relación: Una Central puede tener relación con varias centrales destino. */
    @OneToMany(mappedBy = "centralOrigen")
    private List<CentralesRelacion> centralRelacion;

    /** Identificador de modelo. */
    @ManyToOne
    @JoinColumn(name = "ID_MODELO", nullable = false, insertable = true, updatable = true)
    private Modelo modelo;

    /** Relación: Muchas centrales pueden ser del mismo tipo. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_CENTRAL")
    private TipoCentral tipoCentral;

    /** Relación: Muchas centrales pueden ser del mismo tipo de transmisión. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_MEDIO_TRANSMISION")
    private TipoMedioTransmision tipoMedioTransmision;

    /** Relación: Muchas centrales pueden pertenecer a una misma población. */
    @ManyToOne
    @JoinColumn(name = "ID_POBLACION", nullable = false)
    private Poblacion poblacion;

    /** Relación: Muchas centrales pueden estar asociadas al mismo proveedor. */
    @ManyToOne
    @JoinColumn(name = "ID_PST", nullable = false)
    private Proveedor proveedor;

    /** Relación: Muchos centrales pueden tener el mismo estatus. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS", nullable = false)
    private Estatus estatus;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor, por defecto vacío. */
    public Central() {
    }

    /**
     * Identifciador de Central.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return this.id;
    }

    /**
     * Identifciador de Central.
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Calle.
     * @return String
     */
    public String getCalle() {
        return this.calle;
    }

    /**
     * Calle.
     * @param calle String
     */
    public void setCalle(String calle) {
        this.calle = calle;
    }

    /**
     * Capacidad final.
     * @return BigDecimal
     */
    public BigDecimal getCapacidadFinal() {
        return this.capacidadFinal;
    }

    /**
     * Capacidad final.
     * @param capacidadFinal BigDecimal
     */
    public void setCapacidadFinal(BigDecimal capacidadFinal) {
        this.capacidadFinal = capacidadFinal;
    }

    /**
     * Capacidad inicial.
     * @return BigDecimal
     */
    public BigDecimal getCapacidadInicial() {
        return this.capacidadInicial;
    }

    /**
     * Capacidad inicial.
     * @param capacidadInicial BigDecimal
     */
    public void setCapacidadInicial(BigDecimal capacidadInicial) {
        this.capacidadInicial = capacidadInicial;
    }

    /**
     * Colonia.
     * @return String
     */
    public String getColonia() {
        return this.colonia;
    }

    /**
     * Colonia.
     * @param colonia String
     */
    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    /**
     * Código Postal.
     * @return String
     */
    public String getCp() {
        return this.cp;
    }

    /**
     * Código Postal.
     * @param cp String
     */
    public void setCp(String cp) {
        this.cp = cp;
    }

    /**
     * Jerarquía.
     * @return String
     */
    public String getJerarquia() {
        return this.jerarquia;
    }

    /**
     * Jerarquía.
     * @param jerarquia String
     */
    public void setJerarquia(String jerarquia) {
        this.jerarquia = jerarquia;
    }

    /**
     * Latitud.
     * @return String
     */
    public String getLatitud() {
        return this.latitud;
    }

    /**
     * Latitud.
     * @param latitud String
     */
    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    /**
     * Longitud.
     * @return String
     */
    public String getLongitud() {
        return this.longitud;
    }

    /**
     * Longitud.
     * @param longitud String
     */
    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    /**
     * Nombre.
     * @return String
     */
    public String getNombre() {
        return this.nombre;
    }

    /**
     * Nombre.
     * @param nombre String
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Número.
     * @return the numero
     */
    public String getNumero() {
        return numero;
    }

    /**
     * Número.
     * @param numero the numero to set
     */
    public void setNumero(String numero) {
        this.numero = numero;
    }

    /**
     * Lista de ABN asociados a la central.
     * @return List
     */
    public List<AbnCentral> getAbnCentrales() {
        return abnCentrales;
    }

    /**
     * Lista de ABN asociados a la central.
     * @param abnCentrales Lis
     */
    public void setAbnCentrales(List<AbnCentral> abnCentrales) {
        this.abnCentrales = abnCentrales;
    }

    /**
     * Asocia una relación AbnCentral con ésta central.
     * @param pAbnCentral AbnCentral
     * @return AbnCentral
     */
    public AbnCentral addAbnCentrales(AbnCentral pAbnCentral) {
        this.getAbnCentrales().add(pAbnCentral);
        pAbnCentral.setCentral(this);
        return pAbnCentral;
    }

    /**
     * Asocia una relación AbnCentral con ésta central.
     * @param pAbnCentral AbnCentral
     * @return AbnCentral
     */
    public AbnCentral removeAbnCentrales(AbnCentral pAbnCentral) {
        this.getAbnCentrales().remove(pAbnCentral);
        pAbnCentral.setCentral(null);
        return pAbnCentral;
    }

    /**
     * Tipo de central.
     * @return TipoCentral
     */
    public TipoCentral getTipoCentral() {
        return tipoCentral;
    }

    /**
     * Tipo de central.
     * @param tipoCentral TipoCentral
     */
    public void setTipoCentral(TipoCentral tipoCentral) {
        this.tipoCentral = tipoCentral;
    }

    /**
     * Tipo de medio de transmisión.
     * @return TipoMedioTransmision
     */
    public TipoMedioTransmision getTipoMedioTransmision() {
        return tipoMedioTransmision;
    }

    /**
     * Tipo de medio de transmisión.
     * @param tipoMedioTransmision TipoMedioTransmision
     */
    public void setTipoMedioTransmision(TipoMedioTransmision tipoMedioTransmision) {
        this.tipoMedioTransmision = tipoMedioTransmision;
    }

    /**
     * Población asociada a la central.
     * @return Poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * Población asociada a la central.
     * @param poblacion Poblacion
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * Proveedor asociado a la central.
     * @return Proveedor
     */
    public Proveedor getProveedor() {
        return proveedor;
    }

    /**
     * Proveedor asociado a la central.
     * @param proveedor Proveedor
     */
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * Identificador de modelo.
     * @return the modelo
     */
    public Modelo getModelo() {
        return modelo;
    }

    /**
     * Identificador de modelo.
     * @param modelo the modelo to set
     */
    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
    }

    /**
     * Hashcode de Central.
     */
    @Override
    public int hashCode() {
        return (id != null)
                ? (this.getClass().hashCode() + id.hashCode())
                : super.hashCode();
    }

    /**
     * Comparador objeto Central.
     */
    @Override
    public boolean equals(Object other) {
        return (other instanceof Central) && (id != null)
                ? id.equals(((Central) other).id)
                : (other == this);
    }

    /**
     * Relación: Muchos centrales pueden tener el mismo estatus.
     * @return the estatus
     */
    public Estatus getEstatus() {
        return estatus;
    }

    /**
     * Relación: Muchos centrales pueden tener el mismo estatus.
     * @param estatus the estatus to set
     */
    public void setEstatus(Estatus estatus) {
        this.estatus = estatus;
    }

    /**
     * Relación: Una Central puede tener relación con varias centrales destino.
     * @return the centralRelacion
     */
    public List<CentralesRelacion> getCentralRelacion() {
        return centralRelacion;
    }

    /**
     * Relación: Una Central puede tener relación con varias centrales destino.
     * @param centralRelacion the centralRelacion to set
     */
    public void setCentralRelacion(List<CentralesRelacion> centralRelacion) {
        this.centralRelacion = centralRelacion;
    }

    /**
     * Retorna etiqueta combo central origen.
     * @return label
     */
    public String getLabelCentralOrigen() {
        StringBuffer sb = new StringBuffer();

        return sb.append(this.nombre).append(" ").append(this.latitud).append(" ").append(this.longitud).toString();

    }

    /**
     * Retorna etiqueta combo central destino.
     * @return label
     */
    public String getLabelCentralDestino() {

        StringBuffer sb = new StringBuffer();

        return sb.append(this.nombre).append(" ").append(this.latitud).append(" ").append(this.longitud).append(" ")
                .append(this.proveedor.getNombre()).toString();

    }

}
