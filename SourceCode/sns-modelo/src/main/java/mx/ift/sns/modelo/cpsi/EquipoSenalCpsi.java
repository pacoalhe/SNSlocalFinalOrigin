package mx.ift.sns.modelo.cpsi;

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
import javax.persistence.Transient;
import javax.persistence.Version;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.usu.Auditoria;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Representa un Equipo de Señalización de Códigos de Puntos de Señalización Internacional.
 */
@Entity
@Table(name = "CAT_EQUIPO_SENAL_CPSI")
@SequenceGenerator(name = "SEQ_ID_EQUIPO_SENAL_CPSI", sequenceName = "SEQ_ID_EQUIPO_SENAL_CPSI", allocationSize = 1)
@NamedQuery(name = "EquipoSenalCpsi.findAll", query = "SELECT e FROM EquipoSenalCpsi e")
public class EquipoSenalCpsi extends Auditoria implements Serializable, Cloneable {

    /** Número de Bits del Identificador de Región. */
    public static final byte ID_REGION_BITS = 3;

    /** Número de Bits del Identificador de Red. */
    public static final byte ID_RED_BITS = 8;

    /** Número de Bits del Identificador del Código CPSI. */
    public static final byte ID_CPSI_BITS = 3;

    /** Posibles valores del CPSI (14 Bits 16.384 valores). */
    public static final byte CPSI_MAX_BITS = ID_REGION_BITS + ID_RED_BITS + ID_CPSI_BITS;

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_EQUIPO_SENAL_CPSI")
    @Column(name = "ID_EQUIPO_SENAL_CPSI", unique = true, nullable = false)
    private BigDecimal id;

    /** Clave del equipo de señalizacion. */
    @Column(name = "CLAVE", nullable = false, length = 100)
    private String clave;

    /** CPS en Decimal. */
    @Column(name = "CPS", nullable = false)
    private Integer cps;

    /** ABN. */
    @ManyToOne
    @JoinColumn(name = "ID_ABN", nullable = false)
    private Abn abn;

    /** Población. */
    @ManyToOne
    @JoinColumn(name = "ID_INEGI", nullable = false)
    private Poblacion poblacion;

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

    /** Nombre del archivo de carga. */
    @Column(name = "NOMBRE_FICHERO", nullable = true, length = 128)
    private String nombreFichero;

    /** Listado de warnings generados en la importación del excel de carga. */
    @OneToMany(mappedBy = "equipoSenalCpsi", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<EquipoSenalCpsiWarn> warnings;

    /** Tipo del equipo de señalizacion. */
    @Column(name = "TIPO", nullable = false, length = 100)
    private String tipo;

    /** CPS en Binario. */
    @Transient
    private String cpsBinario;

    /** Mensaje de error de la validación del equipo de señalización. */
    @Transient
    private String errorValidacion;

    /** Representación decimal de las 3 partes que forman un código CPSI, separadas con un guion. */
    @Transient
    private String formatoDecimal;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor por defecto. */
    public EquipoSenalCpsi() {
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
     * ABN asociado al Equipo de señalización.
     * @return Abn
     */
    public Abn getAbn() {
        return abn;
    }

    /**
     * ABN asociado al Equipo de señalización.
     * @param abn Abn
     */
    public void setAbn(Abn abn) {
        this.abn = abn;
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
     * Proveedor asociado al Equipo de señalización.
     * @return Proveedor
     */
    public Proveedor getProveedor() {
        return proveedor;
    }

    /**
     * Proveedor asociado al Equipo de señalización.
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
     * Logitud.
     * @return String
     */
    public String getLongitud() {
        return longitud;
    }

    /**
     * Logitud.
     * @param longitud String
     */
    public void setLongitud(String longitud) {
        this.longitud = longitud;
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
     * CPS en Binario.
     * @return String
     */
    public String getCpsBinario() {

        if (this.cpsBinario != null && !this.cpsBinario.isEmpty()) {
            return StringUtils.leftPad(this.cpsBinario, 14, '0');
        } else if (cps != null) {
            this.cpsBinario = StringUtils.leftPad(Integer.toBinaryString(cps), 14, '0');
            return this.cpsBinario;
        }

        return "";
    }

    /**
     * Representación decimal de las 3 partes que forman un código CPSI, separadas con un guion.
     * @return String
     */
    public String getFormatoDecimal() {
        if (formatoDecimal == null) {
            String binario = this.getCpsBinario();

            // Id de Región los 3 primers bits (0-3)
            String idRegion = binario.substring(0, ID_REGION_BITS);

            // Id de Red los 8 siguientes bits (3-11)
            String idRed = binario.substring(ID_REGION_BITS, (ID_REGION_BITS + ID_RED_BITS));

            // Ide de codigo CPSI los 3 siguientes bis (11-14)
            String idPunto = binario.substring((ID_REGION_BITS + ID_RED_BITS),
                    (ID_REGION_BITS + ID_RED_BITS + ID_CPSI_BITS));

            // Formato decimal
            StringBuilder sbDecimal = new StringBuilder();
            sbDecimal.append(Integer.parseInt(idRegion, 2)).append("-");
            sbDecimal.append(Integer.parseInt(idRed, 2)).append("-");
            sbDecimal.append(Integer.parseInt(idPunto, 2));

            formatoDecimal = sbDecimal.toString();
        }
        return formatoDecimal;
    }

    /**
     * Representación decimal de las 3 partes que forman un código CPSI, separadas con un guion.
     * @param formatoDecimal String
     */
    public void setFormatoDecimal(String formatoDecimal) {
        this.formatoDecimal = formatoDecimal;
    }

    /**
     * CPS en Binario.
     * @param cpsBinario String
     */
    public void setCpsBinario(String cpsBinario) {
        this.cpsBinario = cpsBinario;
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
     * @return EquipoSenalCpsi equipo
     * @throws CloneNotSupportedException ex
     */
    @Override
    public EquipoSenalCpsi clone() throws CloneNotSupportedException {
        return (EquipoSenalCpsi) super.clone();
    }

    /**
     * Listado de warnings generados en la importación del excel de carga.
     * @return List<EquipoSenalCpsiWarn>
     */
    public List<EquipoSenalCpsiWarn> getWarnings() {
        return warnings;
    }

    /**
     * Listado de warnings generados en la importación del excel de carga.
     * @param warnings List<EquipoSenalCpsiWarn>
     */
    public void setWarnings(List<EquipoSenalCpsiWarn> warnings) {
        this.warnings = warnings;
    }

}
