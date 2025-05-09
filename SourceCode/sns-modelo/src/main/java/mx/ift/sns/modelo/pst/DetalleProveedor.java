package mx.ift.sns.modelo.pst;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Representa el objeto de mapeo del detalle del proveedor.
 */
public class DetalleProveedor implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Nombre del PST. */
    private String nombre;

    /** IDO. */
    private BigDecimal ido;

    /** IDA. */
    private BigDecimal ida;

    /** ABC. */
    private BigDecimal abc;

    /** BCD. */
    private BigDecimal bcd;

    /**
     * @param nombre del pst
     * @param ido del pst
     * @param ida del pst
     * @param abc del pst
     * @param bcd del pst
     */
    public DetalleProveedor(String nombre, BigDecimal ido, BigDecimal ida, BigDecimal abc, BigDecimal bcd) {
        super();
        this.nombre = nombre;
        this.ido = ido;
        this.ida = ida;
        this.abc = abc;
        this.bcd = bcd;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the ido
     */
    public BigDecimal getIdo() {
        return ido;
    }

    /**
     * @param ido the ido to set
     */
    public void setIdo(BigDecimal ido) {
        this.ido = ido;
    }

    /**
     * @return the ida
     */
    public BigDecimal getIda() {
        return ida;
    }

    /**
     * @param ida the ida to set
     */
    public void setIda(BigDecimal ida) {
        this.ida = ida;
    }

    /**
     * @return the abc
     */
    public BigDecimal getAbc() {
        return abc;
    }

    /**
     * @param abc the abc to set
     */
    public void setAbc(BigDecimal abc) {
        this.abc = abc;
    }

    /**
     * @return the bcd
     */
    public BigDecimal getBcd() {
        return bcd;
    }

    /**
     * @param bcd the bcd to set
     */
    public void setBcd(BigDecimal bcd) {
        this.bcd = bcd;
    }

    /**
     * Obtiene el ido con 0 delante.
     * @return ido
     */
    public String getIdoAsString() {
        return this.ido != null ? String.format("%03d", this.ido.intValue()) : "";
    }

    /**
     * Obtiene el ida con 0 delante.
     * @return ida
     */
    public String getIdaAsString() {
        return this.ida != null ? String.format("%03d", this.ida.intValue()) : "";
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
    public String getBcdAsString() {
        return this.bcd != null ? String.format("%03d", this.bcd.intValue()) : "";
    }
}
