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

import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Representa la vista del detalle de reporte.
 */
@Entity
@Table(name = "DETALLE_REPORTE_VM")
@ReadOnly
@Cacheable(false)
public class DetalleReporte implements Serializable {

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
    @JoinColumn(name = "POBLACION")
    private Poblacion poblacion;

    /** Proveedor que reporta. */
    @ManyToOne
    @JoinColumn(name = "PST")
    private Proveedor proveedor;

    /** ABN. */
    private BigDecimal abn;

    /** Total lineas activas. */
    @Column(name = "TOTAL_LINEAS_ACTIVAS")
    private BigDecimal totalLineasActivas;

    /** Total lineas activas CPP. */
    @Column(name = "TOTAL_LINEAS_ACTIVAS_CPP")
    private BigDecimal totalLineasActivasCpp;

    /** Total lineas activas fijo. */
    @Column(name = "TOTAL_LINEAS_ACTIVAS_FIJAS")
    private BigDecimal totalLineasActivasFijas;

    /** Total lineas activas MPP. */
    @Column(name = "TOTAL_LINEAS_ACTIVAS_MPP")
    private BigDecimal totalLineasActivasMpp;

    /** Total Asignadas. */
    @Column(name = "TOTAL_ASIGNADAS")
    private BigDecimal totalAsignadas;

    /** Total Asignadas CPP. */
    @Column(name = "TOTAL_ASIGNADAS_CPP")
    private BigDecimal totalAsignadasCpp;

    /** Total Asignadas fijo. */
    @Column(name = "TOTAL_ASIGNADAS_FIJA")
    private BigDecimal totalAsignadasFijas;

    /** Total Asignadas MPP. */
    @Column(name = "TOTAL_ASIGNADAS_MPP")
    private BigDecimal totalAsignadasMpp;

    /** Constructor vacio por defecto. */
    public DetalleReporte() {
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the consecutivo
     */
    public BigDecimal getConsecutivo() {
        return consecutivo;
    }

    /**
     * @param consecutivo the consecutivo to set
     */
    public void setConsecutivo(BigDecimal consecutivo) {
        this.consecutivo = consecutivo;
    }

    /**
     * @return the fechaReporte
     */
    public Date getFechaReporte() {
        return fechaReporte;
    }

    /**
     * @param fechaReporte the fechaReporte to set
     */
    public void setFechaReporte(Date fechaReporte) {
        this.fechaReporte = fechaReporte;
    }

    /**
     * @return the poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * @param poblacion the poblacion to set
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * @return the proveedor
     */
    public Proveedor getProveedor() {
        return proveedor;
    }

    /**
     * @param proveedor the proveedor to set
     */
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * @return the totalLineasActivas
     */
    public BigDecimal getTotalLineasActivas() {
        return totalLineasActivas;
    }

    /**
     * @param totalLineasActivas the totalLineasActivas to set
     */
    public void setTotalLineasActivas(BigDecimal totalLineasActivas) {
        this.totalLineasActivas = totalLineasActivas;
    }

    /**
     * @return the totalLineasActivasCpp
     */
    public BigDecimal getTotalLineasActivasCpp() {
        return totalLineasActivasCpp;
    }

    /**
     * @param totalLineasActivasCpp the totalLineasActivasCpp to set
     */
    public void setTotalLineasActivasCpp(BigDecimal totalLineasActivasCpp) {
        this.totalLineasActivasCpp = totalLineasActivasCpp;
    }

    /**
     * @return the totalLineasActivasFijas
     */
    public BigDecimal getTotalLineasActivasFijas() {
        return totalLineasActivasFijas;
    }

    /**
     * @param totalLineasActivasFijas the totalLineasActivasFijas to set
     */
    public void setTotalLineasActivasFijas(BigDecimal totalLineasActivasFijas) {
        this.totalLineasActivasFijas = totalLineasActivasFijas;
    }

    /**
     * @return the totalLineasActivasMpp
     */
    public BigDecimal getTotalLineasActivasMpp() {
        return totalLineasActivasMpp;
    }

    /**
     * @param totalLineasActivasMpp the totalLineasActivasMpp to set
     */
    public void setTotalLineasActivasMpp(BigDecimal totalLineasActivasMpp) {
        this.totalLineasActivasMpp = totalLineasActivasMpp;
    }

    /**
     * @return the totalAsignadas
     */
    public BigDecimal getTotalAsignadas() {
        return totalAsignadas;
    }

    /**
     * @param totalAsignadas the totalAsignadas to set
     */
    public void setTotalAsignadas(BigDecimal totalAsignadas) {
        this.totalAsignadas = totalAsignadas;
    }

    /**
     * @return the totalAsignadasCpp
     */
    public BigDecimal getTotalAsignadasCpp() {
        return totalAsignadasCpp;
    }

    /**
     * @param totalAsignadasCpp the totalAsignadasCpp to set
     */
    public void setTotalAsignadasCpp(BigDecimal totalAsignadasCpp) {
        this.totalAsignadasCpp = totalAsignadasCpp;
    }

    /**
     * @return the totalAsignadasFijas
     */
    public BigDecimal getTotalAsignadasFijas() {
        return totalAsignadasFijas;
    }

    /**
     * @param totalAsignadasFijas the totalAsignadasFijas to set
     */
    public void setTotalAsignadasFijas(BigDecimal totalAsignadasFijas) {
        this.totalAsignadasFijas = totalAsignadasFijas;
    }

    /**
     * @return the totalAsignadasMpp
     */
    public BigDecimal getTotalAsignadasMpp() {
        return totalAsignadasMpp;
    }

    /**
     * @param totalAsignadasMpp the totalAsignadasMpp to set
     */
    public void setTotalAsignadasMpp(BigDecimal totalAsignadasMpp) {
        this.totalAsignadasMpp = totalAsignadasMpp;
    }

    /**
     * @return the abn
     */
    public BigDecimal getAbn() {
        return abn;
    }

    /**
     * @param abn the abn to set
     */
    public void setAbn(BigDecimal abn) {
        this.abn = abn;
    }
}
