package mx.ift.sns.modelo.lineas;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Representa el reporte de Lineas Activas.
 */
@Entity
@Table(name = "REP_LINEA_ACTIVA")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("1")
@NamedQuery(name = "LineaActiva.findAll", query = "SELECT l FROM ReporteLineasActivas l")
public class ReporteLineasActivas extends Reporte implements Serializable {

    /** Serial id. */
    private static final long serialVersionUID = 1L;

    /** Suma lineas activas cpp. */
    @Column(name = "SUMA_LINEAS_ACTIVAS_CPP")
    private BigDecimal sumaLineasActivasCpp;

    /** suma lineas activas fijo. */
    @Column(name = "SUMA_LINEAS_ACTIVAS_FIJO")
    private BigDecimal sumaLineasActivasFijo;

    /** suma lineas activas mpp. */
    @Column(name = "SUMA_LINEAS_ACTIVAS_MPP")
    private BigDecimal sumaLineasActivasMpp;

    /** suma numeracion asiganda. */
    @Column(name = "SUMA_NUM_ASIGNADA")
    private BigDecimal sumaNumAsignada;

    /** suma total lineas activas. */
    @Column(name = "SUMA_TOTAL_LINEAS_ACTIVAS")
    private BigDecimal sumaTotalLineasActivas;

    /** detalle reporte lineas activas. */
    @OneToMany(mappedBy = "lineaActiva")
    private List<DetalleLineaActiva> repDetLineasActivas;

    /** Constructor, vacio por defecto. */
    public ReporteLineasActivas() {
    }

    /**
     * Suma lineas activas cpp.
     * @return the sumaLineasActivasCpp
     */
    public BigDecimal getSumaLineasActivasCpp() {
        return sumaLineasActivasCpp;
    }

    /**
     * Suma lineas activas cpp.
     * @param sumaLineasActivasCpp the sumaLineasActivasCpp to set
     */
    public void setSumaLineasActivasCpp(BigDecimal sumaLineasActivasCpp) {
        this.sumaLineasActivasCpp = sumaLineasActivasCpp;
    }

    /**
     * sumaLineasActivasFijo.
     * @return the sumaLineasActivasFijo
     */
    public BigDecimal getSumaLineasActivasFijo() {
        return sumaLineasActivasFijo;
    }

    /**
     * sumaLineasActivasFijo.
     * @param sumaLineasActivasFijo the sumaLineasActivasFijo to set
     */
    public void setSumaLineasActivasFijo(BigDecimal sumaLineasActivasFijo) {
        this.sumaLineasActivasFijo = sumaLineasActivasFijo;
    }

    /**
     * suma lineas activas mpp.
     * @return the sumaLineasActivasMpp
     */
    public BigDecimal getSumaLineasActivasMpp() {
        return sumaLineasActivasMpp;
    }

    /**
     * suma lineas activas mpp.
     * @param sumaLineasActivasMpp the sumaLineasActivasMpp to set
     */
    public void setSumaLineasActivasMpp(BigDecimal sumaLineasActivasMpp) {
        this.sumaLineasActivasMpp = sumaLineasActivasMpp;
    }

    /**
     * suma numeracion asiganda.
     * @return the sumaNumAsignada
     */
    public BigDecimal getSumaNumAsignada() {
        return sumaNumAsignada;
    }

    /**
     * suma numeracion asiganda.
     * @param sumaNumAsignada the sumaNumAsignada to set
     */
    public void setSumaNumAsignada(BigDecimal sumaNumAsignada) {
        this.sumaNumAsignada = sumaNumAsignada;
    }

    /**
     * suma total lineas activas.
     * @return the sumaTotalLineasActivas
     */
    public BigDecimal getSumaTotalLineasActivas() {
        return sumaTotalLineasActivas;
    }

    /**
     * suma total lineas activas.
     * @param sumaTotalLineasActivas the sumaTotalLineasActivas to set
     */
    public void setSumaTotalLineasActivas(BigDecimal sumaTotalLineasActivas) {
        this.sumaTotalLineasActivas = sumaTotalLineasActivas;
    }

    /**
     * repDetLineasActivas.
     * @return the repDetLineasActivas
     */
    public List<DetalleLineaActiva> getRepDetLineasActivas() {
        return repDetLineasActivas;
    }

    /**
     * repDetLineasActivas.
     * @param repDetLineasActivas the repDetLineasActivas to set
     */
    public void setRepDetLineasActivas(List<DetalleLineaActiva> repDetLineasActivas) {
        this.repDetLineasActivas = repDetLineasActivas;
    }

    /**
     * Añade un detalle al reporte.
     * @param repDetLineasActiva detalle
     * @return detalle
     */
    public DetalleLineaActiva addRepDetLineasActiva(DetalleLineaActiva repDetLineasActiva) {
        getRepDetLineasActivas().add(repDetLineasActiva);
        repDetLineasActiva.setLineaActiva(this);

        return repDetLineasActiva;
    }

    /**
     * Elimina un detalle al reporte.
     * @param repDetLineasActiva detalle
     * @return detalle
     */
    public DetalleLineaActiva removeRepDetLineasActiva(DetalleLineaActiva repDetLineasActiva) {
        getRepDetLineasActivas().remove(repDetLineasActiva);
        repDetLineasActiva.setLineaActiva(null);

        return repDetLineasActiva;
    }

}
