package mx.ift.sns.modelo.cpsi;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Contiene la información del Estudio de CPSI para un Proveedor.
 * @author X50880SA
 */
public class Linea1EstudioCPSI implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Cantidad de CPSI asignados al PST de la solicitud calculado desde el catalogo CPSI. */
    private BigDecimal totalAsignados;

    /** Cantidad de CPSI del catalogo de equipos de señalización pertenecientes al PST. */
    private BigDecimal totalActivos;

    /** (Total activos/Total Asignados)*100). */
    private BigDecimal utilizacion;

    /** Nombre del Proveedor. */
    private String nombreProveedor;

    /** Identificador del Proveedor. */
    private BigDecimal idProveedor;

    /** Indica que hay que actualizar el valor de Utilización con los nuevos valores. */
    private boolean utilizacionDirty = true;

    /** Constructor por defecto. */
    public Linea1EstudioCPSI() {

    }

    /**
     * Constructor específico.
     * @param pTotalAsignados Cantidad de CPSI asignados al PST de la solicitud calculado desde el catalogo CPSI.
     * @param pTotalActivos Cantidad de CPSI del catalogo de equipos de señalización pertenecientes al PST.
     * @param pNombreProveedor Nombre del Proveedor
     * @param pIdProveeedor Identificador del Proveedor.
     */
    public Linea1EstudioCPSI(BigDecimal pTotalAsignados, BigDecimal pTotalActivos, String pNombreProveedor,
            BigDecimal pIdProveeedor) {

        totalAsignados = pTotalAsignados;
        totalActivos = pTotalActivos;
        nombreProveedor = pNombreProveedor;
        idProveedor = pIdProveeedor;
        utilizacionDirty = true;
    }

    // GETTERS && SETTERS

    /**
     * Cantidad de CPSI asignados al PST de la solicitud calculado desde el catalogo CPSI.
     * @return BigDecimal
     */
    public BigDecimal getTotalAsignados() {
        return totalAsignados;
    }

    /**
     * Cantidad de CPSI asignados al PST de la solicitud calculado desde el catalogo CPSI.
     * @param totalAsignados BigDecimal
     */
    public void setTotalAsignados(BigDecimal totalAsignados) {
        this.totalAsignados = totalAsignados;
        utilizacionDirty = true;
    }

    /**
     * Cantidad de CPSI del catalogo de equipos de señalización pertenecientes al PST.
     * @return BigDecimal
     */
    public BigDecimal getTotalActivos() {
        return totalActivos;
    }

    /**
     * Cantidad de CPSI del catalogo de equipos de señalización pertenecientes al PST.
     * @param totalActivos BigDecimal
     */
    public void setTotalActivos(BigDecimal totalActivos) {
        this.totalActivos = totalActivos;
        utilizacionDirty = true;
    }

    /**
     * (Total activos/Total Asignados)*100).
     * @return Float
     */
    public BigDecimal getUtilizacion() {
        if (utilizacionDirty) {
            if (totalActivos != null && totalAsignados != null) {
                float average = (totalActivos.floatValue() / totalAsignados.floatValue()) * 100F;
                if (Float.isInfinite(average) || Float.isNaN(average)) {
                    average = 0F;
                }
                utilizacion = new BigDecimal(average);
            } else {
                utilizacion = new BigDecimal(0);
            }
            utilizacion = utilizacion.setScale(2, BigDecimal.ROUND_HALF_EVEN);
            utilizacionDirty = false;
        }
        return utilizacion;
    }

    /**
     * Nombre del Proveedor.
     * @return String
     */
    public String getNombreProveedor() {
        return nombreProveedor;
    }

    /**
     * Nombre del Proveedor.
     * @param nombreProveedor String
     */
    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    /**
     * Identificador del Proveedor.
     * @return BigDecimal
     */
    public BigDecimal getIdProveedor() {
        return idProveedor;
    }

    /**
     * Identificador del Proveedor.
     * @param idProveedor BigDecimal
     */
    public void setIdProveedor(BigDecimal idProveedor) {
        this.idProveedor = idProveedor;
    }
}
