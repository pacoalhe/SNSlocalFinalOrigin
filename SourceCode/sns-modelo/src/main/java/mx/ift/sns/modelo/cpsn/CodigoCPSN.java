package mx.ift.sns.modelo.cpsn;

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
import mx.ift.sns.modelo.usu.Auditoria;

import org.apache.commons.lang3.StringUtils;

/**
 * Representa un Código de Puntos de Señalización Nacional.
 */
@Entity
@Cacheable(false)
@Table(name = "CAT_CPSN")
@NamedQuery(name = "CodigoCPSN.findAll", query = "SELECT cpsn FROM CodigoCPSN cpsn")
public class CodigoCPSN extends Auditoria implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Constante de la acción DESAGRUPAR. */
    public static final String DESAGRUPAR = "D";

    /** Constante de la acción AGRUPAR. */
    public static final String AGRUPAR = "A";

    /** Identificador. */
    @Id
    @Column(name = "ID_CPSN", length = 5)
    private BigDecimal id;

    /** Tipo de Bloque. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_BLOQUE", nullable = false)
    private TipoBloqueCPSN tipoBloqueCPSN;

    /** PST. */
    @ManyToOne
    @JoinColumn(name = "ID_PST")
    private Proveedor proveedor;

    /** Fecha de cuarentena. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_CUARENTENA")
    private Date fechaCuarentena;

    /** ESTATUS CPSN. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS_CPSN", nullable = false)
    private EstatusCPSN estatusCPSN;

    /** CPS en binario. */
    @Transient
    private String binario;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor, por defecto vacío. */
    public CodigoCPSN() {
    }

    /**
     * Identificador.
     * @return the id
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Identificador.
     * @param id the id to set
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * PST.
     * @return the proveedor
     */
    public Proveedor getProveedor() {
        return proveedor;
    }

    /**
     * PST.
     * @param proveedor the proveedor to set
     */
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * Fecha de cuarentena.
     * @return the fechaCuarentena
     */
    public Date getFechaCuarentena() {
        return fechaCuarentena;
    }

    /**
     * Fecha de cuarentena.
     * @param fechaCuarentena the fechaCuarentena to set
     */
    public void setFechaCuarentena(Date fechaCuarentena) {
        this.fechaCuarentena = fechaCuarentena;
    }

    /**
     * ESTATUS CPSN.
     * @return the estatusCPSN
     */
    public EstatusCPSN getEstatusCPSN() {
        return estatusCPSN;
    }

    /**
     * ESTATUS CPSN.
     * @param estatusCPSN the estatusCPSN to set
     */
    public void setEstatusCPSN(EstatusCPSN estatusCPSN) {
        this.estatusCPSN = estatusCPSN;
    }

    /**
     * Devuelve el código en binario.
     * @return String codigo binario
     */
    public String getBinario() {
        if (tipoBloqueCPSN.getId().equals(TipoBloqueCPSN.BLOQUE_2048)) {
            binario = StringUtils.leftPad(Integer.toBinaryString(Integer.parseInt(id.toString())),
                    TipoBloqueCPSN.TAM_INDIVIDUAL,
                    '0').substring(0, TipoBloqueCPSN.TAM_BLOQUE_2048);
        } else if (tipoBloqueCPSN.getId().equals(TipoBloqueCPSN.BLOQUE_128)) {
            binario = StringUtils.leftPad(Integer.toBinaryString(Integer.parseInt(id.toString())),
                    TipoBloqueCPSN.TAM_INDIVIDUAL,
                    '0').substring(0, TipoBloqueCPSN.TAM_BLOQUE_128);
        } else if (tipoBloqueCPSN.getId().equals(TipoBloqueCPSN.BLOQUE_8)) {
            binario = StringUtils
                    .leftPad(Integer.toBinaryString(Integer.parseInt(id.toString())),
                            TipoBloqueCPSN.TAM_INDIVIDUAL, '0')
                    .substring(0, TipoBloqueCPSN.TAM_BLOQUE_8);
        } else if (tipoBloqueCPSN.getId().equals(TipoBloqueCPSN.INDIVIDUAL)) {
            binario = StringUtils.leftPad(Integer.toBinaryString(Integer.parseInt(id.toString())),
                    TipoBloqueCPSN.TAM_INDIVIDUAL,
                    '0');
        }

        return binario;
    }

    /**
     * Devuelve el código en binario de la agrupación.
     * @return String codigo binario
     */
    public String getBinarioAgrupado() {
        String binario = "";
        if (tipoBloqueCPSN.getId().equals(TipoBloqueCPSN.BLOQUE_128)) {
            binario = StringUtils.leftPad(Integer.toBinaryString(Integer.parseInt(id.toString())),
                    TipoBloqueCPSN.TAM_INDIVIDUAL,
                    '0').substring(0, TipoBloqueCPSN.TAM_BLOQUE_2048);
        } else if (tipoBloqueCPSN.getId().equals(TipoBloqueCPSN.BLOQUE_8)) {
            binario = StringUtils
                    .leftPad(Integer.toBinaryString(Integer.parseInt(id.toString())),
                            TipoBloqueCPSN.TAM_INDIVIDUAL, '0')
                    .substring(0, TipoBloqueCPSN.TAM_BLOQUE_128);
        } else if (tipoBloqueCPSN.getId().equals(TipoBloqueCPSN.INDIVIDUAL)) {
            binario = StringUtils.leftPad(Integer.toBinaryString(Integer.parseInt(id.toString())),
                    TipoBloqueCPSN.TAM_INDIVIDUAL,
                    '0').substring(0, TipoBloqueCPSN.TAM_BLOQUE_8);
        }

        return binario;
    }

    /**
     * Devuelve el valor en decimal del cpsn.
     * @return cpsn
     */
    public Integer getDecimalRed() {
        return (tipoBloqueCPSN.getId().equals(TipoBloqueCPSN.INDIVIDUAL)) ? null : Integer.valueOf(binario, 2);
    }

    /**
     * Devuelve el valor máximo del cpsn solo en caso de ser Individual.
     * @return cpsn
     */
    public Integer getDecimalTotal() {
        return (tipoBloqueCPSN.getId().equals(TipoBloqueCPSN.INDIVIDUAL)) ? Integer.valueOf(binario, 2) : null;
    }

    /**
     * Devuelve el valor mínimo del bloque.
     * @return cps minimo
     */
    public Integer getDecimalDesde() {
        return (tipoBloqueCPSN.getId().equals(TipoBloqueCPSN.INDIVIDUAL))
                ? null
                : Integer.valueOf(StringUtils.rightPad(binario, TipoBloqueCPSN.TAM_INDIVIDUAL, '0'), 2);
    }

    /**
     * Devuelve el valor máximo del bloque.
     * @return cps máximo
     */
    public Integer getDecimalHasta() {
        return (tipoBloqueCPSN.getId().equals(TipoBloqueCPSN.INDIVIDUAL))
                ? null
                : Integer.valueOf(StringUtils.rightPad(binario, TipoBloqueCPSN.TAM_INDIVIDUAL, '1'), 2);
    }

    /**
     * @return the tipoBloqueCPSN
     */
    public TipoBloqueCPSN getTipoBloqueCPSN() {
        return tipoBloqueCPSN;
    }

    /**
     * @param tipoBloqueCPSN the tipoBloqueCPSN to set
     */
    public void setTipoBloqueCPSN(TipoBloqueCPSN tipoBloqueCPSN) {
        this.tipoBloqueCPSN = tipoBloqueCPSN;
    }

    /**
     * Método que devuelve el número de códigos a generar si se desagrupa el código.
     * @return integer numero de codigos a generar
     */
    public Integer getNumRegistrosAGenerar() {
        if (TipoBloqueCPSN.BLOQUE_2048.equals(tipoBloqueCPSN.getId())) {
            return TipoBloqueCPSN.NUM_REG_2048;
        } else if (TipoBloqueCPSN.BLOQUE_128.equals(tipoBloqueCPSN.getId())) {
            return TipoBloqueCPSN.NUM_REG_128;
        } else if (TipoBloqueCPSN.BLOQUE_8.equals(tipoBloqueCPSN.getId())) {
            return TipoBloqueCPSN.NUM_REG_8;
        }

        return 0;
    }

    /**
     * @param binario the binario to set
     */
    public void setBinario(String binario) {
        this.binario = binario;
    }

    /**
     * Método encargado de obtener el tipo de bloque que se generará de la agrupación.
     * @return string tipo de bloque
     */
    public String getTipoBloqueAgrupacion() {
        String tipo = null;

        if (TipoBloqueCPSN.BLOQUE_128.equals(tipoBloqueCPSN.getId())) {
            tipo = TipoBloqueCPSN.TIPO_BLOQUE_2048;
        } else if (TipoBloqueCPSN.BLOQUE_8.equals(tipoBloqueCPSN.getId())) {
            tipo = TipoBloqueCPSN.TIPO_BLOQUE_128;
        } else if (TipoBloqueCPSN.INDIVIDUAL.equals(tipoBloqueCPSN.getId())) {
            tipo = TipoBloqueCPSN.TIPO_BLOQUE_8;
        }

        return tipo;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CPSN = {");
        builder.append("Id: ").append(this.id);
        if (this.tipoBloqueCPSN != null) {
            builder.append(", Tipo bloque: ").append(this.tipoBloqueCPSN.getDescripcion());
            builder.append(", Binario: ").append(this.getBinario());
            if (this.tipoBloqueCPSN.getId().equals(TipoBloqueCPSN.INDIVIDUAL)) {
                builder.append(", Dec. Total: ").append(this.getDecimalTotal());
            } else {
                builder.append(", Dec. Red: ").append(this.getDecimalRed());
                builder.append(", Dec. Desde: ").append(this.getDecimalDesde());
                builder.append(", Dec. Hasta: ").append(this.getDecimalHasta());
            }
        } else {
            builder.append(", Tipo bloque: null");
        }
        builder.append("}");
        return builder.toString();
    }
}
