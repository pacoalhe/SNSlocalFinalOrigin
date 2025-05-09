package mx.ift.sns.modelo.central;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Clase para cargar los combos de centrales.
 * @author X36155QU
 */
public class ComboCentral implements Serializable {

    /**
     * Serial ID.
     */
    private static final long serialVersionUID = 8963859314086898024L;

    /** ID Central. */
    private BigDecimal id;

    /** Nombre central. */
    private String nombre;

    /** Latitud central. */
    private String latitud;

    /** Longitud central. */
    private String longitud;

    /** Proveedor central. */
    private String proveedor;

    /** Constructor vacio. */
    public ComboCentral() {

    }

    /**
     * Constructor.
     * @param id central
     * @param nombre central
     * @param latitud central
     * @param longitud central
     * @param proveedor central
     */
    public ComboCentral(BigDecimal id, String nombre, String latitud, String longitud, String proveedor) {
        this.id = id;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.proveedor = proveedor;
    }

    /**
     * ID Central.
     * @return the id
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * ID Central.
     * @param id the id to set
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Nombre central.
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Nombre central.
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Latitud central.
     * @return the latitud
     */
    public String getLatitud() {
        return latitud;
    }

    /**
     * Latitud central.
     * @param latitud the latitud to set
     */
    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    /**
     * Latitud central.
     * @return the longitud
     */
    public String getLongitud() {
        return longitud;
    }

    /**
     * Longitud central.
     * @param longitud the longitud to set
     */
    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    /**
     * Proveedor central.
     * @return the proveedor
     */
    public String getProveedor() {
        return proveedor;
    }

    /**
     * Proveedor central.
     * @param proveedor the proveedor to set
     */
    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

}
