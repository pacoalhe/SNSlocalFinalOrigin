package mx.ift.sns.modelo.cpsn;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Cacheable;
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
import javax.persistence.Transient;
import javax.persistence.Version;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.usu.Auditoria;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.annotations.CascadeOnDelete;
import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Representa un Equipo de Señalización de Códigos de Puntos de Señalización Nacional.
 */
@Entity
@Table(name = "CAT_EQUIPO_SENAL_CPSN")
@Cacheable(false)
@CascadeOnDelete
@SequenceGenerator(name = "SEQ_ID_EQUIPO_SENAL_CPSN", sequenceName = "SEQ_ID_EQUIPO_SENAL_CPSN", allocationSize = 1)
@NamedQuery(name = "EquipoSenalCPSN.findAll", query = "SELECT es FROM EquipoSenalCPSN es")
public class EquipoSenalCPSN extends Auditoria implements Serializable, Cloneable {

    /** Serialización . */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_EQUIPO_SENAL_CPSN")
    @Column(name = "ID_EQUIPO_SENAL_CPSN", unique = true, nullable = false)
    private BigDecimal id;

    /** Proveedor que tiene asignado el equipo de señalización. */
    @ManyToOne
    @JoinColumn(name = "ID_PST", nullable = false)
    private Proveedor proveedor;

    /** Nombre del equipo de señalizacion. */
    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    /** Latitud. */
    @Column(name = "LATITUD", nullable = false, length = 20)
    private String latitud;

    /** Longitud. */
    @Column(name = "LONGITUD", nullable = false, length = 20)
    private String longitud;

    /** CPS en Decimal. */
    @Column(name = "CPS", nullable = false)
    private Integer cps;

    /** CPS en Binario. */
    @Transient
    private String cpsBinario;

    /** Población. */
    @ManyToOne
    @JoinColumn(name = "ID_INEGI", nullable = false)
    private Poblacion poblacion;

    /** ABN. */
    @ManyToOne
    @JoinColumn(name = "ID_ABN", nullable = false)
    private Abn abn;

    /** Clave del equipo de señalizacion. */
    @Column(name = "CLAVE", nullable = false, length = 100)
    private String clave;

    /** Tipo del equipo de señalizacion. */
    @Column(name = "TIPO", nullable = false, length = 100)
    private String tipo;

    /** Nombre del archivo de carga. */
    @Column(name = "NOMBRE_FICHERO", nullable = true, length = 128)
    private String nombreFichero;

    /** Listado de warnings generados en la importación del excel de carga. */
    @OneToMany(mappedBy = "equipoSenalCPSN", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<EquipoSenalCPSNWarn> warnings;

    /** Mensaje de error de la validación del equipo de señalización. */
    @Transient
    private String errorValidacion;

    /** Identificador de procesamiento del registo desde excel. */
    @Transient
    private boolean procesarRegistro;

    /** Número de referencia para la importación desde excel. */
    @Transient
    private String numReferencia;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor por defecto. */
    public EquipoSenalCPSN() {
    }

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
     * Proveedor que tiene asignado el equipo de señalización.
     * @return Proveedor
     */
    public Proveedor getProveedor() {
        return proveedor;
    }

    /**
     * Proveedor que tiene asignado el equipo de señalización.
     * @param proveedor Proveedor
     */
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * Nombre del equipo de señalizacion.
     * @return String
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Nombre del equipo de señalizacion.
     * @param nombre String
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Latitud.
     * @return String
     */
    public String getLatitud() {
        return latitud;
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
        return longitud;
    }

    /**
     * Longitud.
     * @param longitud String
     */
    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    /**
     * CPS en Binario.
     * @return String
     */
    public String getCpsBinario() {
        if (this.cpsBinario != null && !this.cpsBinario.isEmpty()) {
            return this.cpsBinario;
        } else if (cps != null) {
            this.cpsBinario = StringUtils.leftPad(Integer.toBinaryString(cps), 14, '0');
            return this.cpsBinario;
        }

        return "";
    }

    /**
     * CPS en Binario.
     * @param cpsBinario String
     */
    public void setCpsBinario(String cpsBinario) {
        this.cpsBinario = cpsBinario;
    }

    /**
     * CPS en Decimal.
     * @return Integer
     */
    public Integer getCps() {
        return cps;
    }

    /**
     * CPS en Decimal.
     * @param cps Integer
     */
    public void setCps(Integer cps) {
        this.cps = cps;
    }

    /**
     * Población asociada al Equipo de señalización.
     * @return Poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * Población asociada al Equipo de señalización.
     * @param poblacion Poblacion
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * ABN asociada al Equipo de señalización.
     * @return Abn
     */
    public Abn getAbn() {
        return abn;
    }

    /**
     * ABN asociada al Equipo de señalización.
     * @param abn Abn
     */
    public void setAbn(Abn abn) {
        this.abn = abn;
    }

    /**
     * Clave del equipo de señalizacion.
     * @return String
     */
    public String getClave() {
        return clave;
    }

    /**
     * Clave del equipo de señalizacion.
     * @param clave String
     */
    public void setClave(String clave) {
        this.clave = clave;
    }

    /**
     * Tipo del equipo de señalizacion.
     * @return String
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Tipo del equipo de señalizacion.
     * @param tipo String
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Nombre del archivo de carga.
     * @return String
     */
    public String getNombreFichero() {
        return (nombreFichero != null) ? nombreFichero.substring(nombreFichero.lastIndexOf('\\') > nombreFichero
                .lastIndexOf('/') ? nombreFichero
                .lastIndexOf('\\') + 1 : nombreFichero.lastIndexOf('/') + 1) : "";
    }

    /**
     * Nombre del archivo de carga.
     * @param nombreFichero String
     */
    public void setNombreFichero(String nombreFichero) {
        this.nombreFichero = nombreFichero;
    }

    /**
     * Listado de warnings generados en la importación del excel de carga.
     * @return List<EquipoSenalCPSNWarn>
     */
    public List<EquipoSenalCPSNWarn> getWarnings() {
        return warnings;
    }

    /**
     * Listado de warnings generados en la importación del excel de carga.
     * @param warnings List<EquipoSenalCPSNWarn>
     */
    public void setWarnings(List<EquipoSenalCPSNWarn> warnings) {
        this.warnings = warnings;
    }

    /**
     * Mensaje de error de la validación del equipo de señalización.
     * @return String
     */
    public String getErrorValidacion() {
        return errorValidacion;
    }

    /**
     * Mensaje de error de la validación del equipo de señalización.
     * @param errorValidacion String
     */
    public void setErrorValidacion(String errorValidacion) {
        this.errorValidacion = errorValidacion;
    }

    /**
     * Método para generar copias del objeto.
     * @return EquipoSenalCPSN equipo
     * @throws CloneNotSupportedException ex
     */
    @Override
    public EquipoSenalCPSN clone() throws CloneNotSupportedException {
        return (EquipoSenalCPSN) super.clone();
    }

}
