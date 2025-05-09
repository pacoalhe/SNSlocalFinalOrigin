package mx.ift.sns.modelo.cpsi;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.modelo.usu.Auditoria;

import org.apache.commons.lang3.StringUtils;

/**
 * Representa un Código de Puntos de Señalización Internacional.
 */
@Entity
@Cacheable(false)
@Table(name = "CAT_CPSI")
@NamedQuery(name = "CodigoCPSI.findAll", query = "SELECT cpsi FROM CodigoCPSI cpsi")
public class CodigoCPSI extends Auditoria implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Número de Bits del Identificador de Región. */
    public static final byte ID_REGION_BITS = 3;

    /** Número de Bits del Identificador de Red. */
    public static final byte ID_RED_BITS = 8;

    /** Número de Bits del Identificador del Código CPSI. */
    public static final byte ID_CPSI_BITS = 3;

    /** Posibles valores del CPSI (14 Bits 16.384 valores). */
    public static final byte CPSI_MAX_BITS = ID_REGION_BITS + ID_RED_BITS + ID_CPSI_BITS;

    /** Identificador. */
    @Id
    @Column(name = "ID_CPSI", length = 5)
    private BigDecimal id;

    /** PST. */
    @ManyToOne
    @JoinColumn(name = "ID_PST")
    private Proveedor proveedor;

    /** Fecha de cuarentena. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_CUARENTENA")
    private Date fechaFinCuarentena;

    /** Estatus CPSI. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS_CPSI", nullable = false)
    private EstatusCPSI estatus;

    /** Relación: Muchos códigos pueden haber sido asignados / editados por la misma Solicitud. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL_SOLICITUD", nullable = false)
    private Solicitud solicitud;

    /** Representación decimal de las 3 partes que forman un código CPSI, separadas con un guion. */
    @Transient
    private String formatoDecimal;

    /** Representación del código CPSI en binario (con 14 bits). */
    @Transient
    private String binario;

    /** Valor decimal de los 14 bits de cualquier código CPSI. */
    @Transient
    private Integer decimalTotal;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor, por defecto vacío. */
    public CodigoCPSI() {
    }

    // GETTERS & SETTERS

    /**
     * Identificador de CPSI.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Identificador de CPSI.
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Proveedor asociado al CPSI.
     * @return Proveedor
     */
    public Proveedor getProveedor() {
        return proveedor;
    }

    /**
     * Proveedor asociado al CPSI.
     * @param proveedor Proveedor
     */
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * Fecha de fin de cuarentena si existe reserva sobre el CPSI.
     * @return Date
     */
    public Date getFechaFinCuarentena() {
        return fechaFinCuarentena;
    }

    /**
     * Fecha de fin de cuarentena si existe reserva sobre el CPSI.
     * @param fechaFinCuarentena Date
     */
    public void setFechaFinCuarentena(Date fechaFinCuarentena) {
        this.fechaFinCuarentena = fechaFinCuarentena;
    }

    /**
     * Estatus del CPSI.
     * @return EstatusCPSI
     */
    public EstatusCPSI getEstatus() {
        return estatus;
    }

    /**
     * Estatus del CPSI.
     * @param estatus EstatusCPSI
     */
    public void setEstatus(EstatusCPSI estatus) {
        this.estatus = estatus;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CPSI = {");
        builder.append("Id: ").append(this.id);
        if (proveedor != null) {
            builder.append(", Pst: ").append(proveedor.getNombre());
        } else {
            builder.append(", Pst: null");
        }
        builder.append("}");
        return builder.toString();
    }

    /**
     * Representación del código CPSI en binario (con 14 bits).
     * @return String codigo binario
     */
    public String getBinario() {
        if (binario == null) {
            // Rellenamos con ceros a la izquierda hasta completar los 14 bits
            binario = StringUtils.leftPad(Integer.toBinaryString(this.id.intValue()), CPSI_MAX_BITS, '0');
        }
        return binario;
    }

    /**
     * Valor decimal de los 14 bits de cualquier código CPSI.
     * @return Integer
     */
    public Integer getDecimalTotal() {
        if (decimalTotal == null) {
            decimalTotal = this.id.intValue();
        }
        return decimalTotal;
    }

    /**
     * Última solicitud que modificó el CPSI.
     * @return Solicitud
     */
    public Solicitud getSolicitud() {
        return solicitud;
    }

    /**
     * Última solicitud que modificó el CPSI.
     * @param solicitud Solicitud
     */
    public void setSolicitud(Solicitud solicitud) {
        this.solicitud = solicitud;
    }

    /**
     * Representación decimal de las 3 partes que forman un código CPSI, separadas con un guion.
     * @return String
     */
    public String getFormatoDecimal() {
        if (formatoDecimal == null) {
            String binario = this.getBinario();

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
}
