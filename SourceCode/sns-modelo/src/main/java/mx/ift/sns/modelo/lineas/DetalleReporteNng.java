package mx.ift.sns.modelo.lineas;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.pst.Proveedor;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Representa la vista del detalle del reporte de numeración no geográfica.
 */
@Entity
@Table(name = "DETALLE_REPORTE_NNG_VM")
@ReadOnly
@Cacheable(false)
public class DetalleReporteNng implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** ID interno. */
    @Id
    @Column(name = "ID_DETALLE_REPORTE")
    private String id;

    /** Consecutivo del reporte. */
    private BigDecimal consecutivo;

    /** Fecha de reporte. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_REPORTE")
    private Date fechaReporte;

    /** Poblacion. */
    @ManyToOne
    @JoinColumn(name = "CLAVE_SERVICIO")
    private ClaveServicio claveServicio;

    /** Proveedor que reporta. */
    @ManyToOne
    @JoinColumn(name = "PST")
    private Proveedor proveedor;

    /** Total lineas activas. */
    @Column(name = "TOTAL_LINEAS_ACTIVAS")
    private BigDecimal totalLineasActivas;

    /** Total Asignadas. */
    @Column(name = "TOTAL_ASIGNADAS")
    private BigDecimal totalAsignadas;

    /** Constructor vacio por defecto. */
    public DetalleReporteNng() {
    }

    /**
     * Obtiene el porcentaje de uso.
     * @return porcentaje
     */
    public String getPorcentajeUso() {
        if (this.totalLineasActivas != null) {
            return (this.totalAsignadas.intValue() == 0 || this.totalLineasActivas.intValue() == 0)
                    ? "0 %" : String.format("%.2f", this.totalLineasActivas.intValue() * 100.0
                            / this.totalAsignadas.intValue())
                            + " %";
        } else {
            return null;
        }
    }

    /**
     * ID interno.
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * ID interno.
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Consecutivo del reporte.
     * @return the consecutivo
     */
    public BigDecimal getConsecutivo() {
        return consecutivo;
    }

    /**Consecutivo del reporte.
     * @param consecutivo consecutivo the consecutivo to set
     */
    public void setConsecutivo(BigDecimal consecutivo) {
        this.consecutivo = consecutivo;
    }

    /**
     * Fecha de reporte.
     * @return the fechaReporte
     */
    public Date getFechaReporte() {
        return fechaReporte;
    }

    /**
     * Fecha de reporte.
     * @param fechaReporte the fechaReporte to set
     */
    public void setFechaReporte(Date fechaReporte) {
        this.fechaReporte = fechaReporte;
    }

    /**
     * Proveedor que reporta.
     * @return the proveedor
     */
    public Proveedor getProveedor() {
        return proveedor;
    }

    /**
     * Proveedor que reporta.
     * @param proveedor the proveedor to set
     */
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * Total lineas activas.
     * @return the totalLineasActivas
     */
    public BigDecimal getTotalLineasActivas() {
        return totalLineasActivas;
    }

    /**
     * Total lineas activas.
     * @param totalLineasActivas the totalLineasActivas to set
     */
    public void setTotalLineasActivas(BigDecimal totalLineasActivas) {
        this.totalLineasActivas = totalLineasActivas;
    }

    /**
     * totalAsignadas.
     * @return the totalAsignadas
     */
    public BigDecimal getTotalAsignadas() {
        return totalAsignadas;
    }

    /**
     * totalAsignadas.
     * @param totalAsignadas the totalAsignadas to set
     */
    public void setTotalAsignadas(BigDecimal totalAsignadas) {
        this.totalAsignadas = totalAsignadas;
    }

    /**
     * Poblacion.
     * @return the claveServicio
     */
    public ClaveServicio getClaveServicio() {
        return claveServicio;
    }

    /**
     * Poblacion.
     * @param claveServicio the claveServicio to set
     */
    public void setClaveServicio(ClaveServicio claveServicio) {
        this.claveServicio = claveServicio;
    }

}
