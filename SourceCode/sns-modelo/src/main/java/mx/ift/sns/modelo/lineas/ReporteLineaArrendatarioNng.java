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
 * Representa el reporte de líneas de arrendatario de numeración no geográfica.
 */
@Entity
@Table(name = "NNG_REP_LINEA_ARRENDATARIO")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("3")
@NamedQuery(name = "ReporteLineaArrendatarioNng.findAll", query = "SELECT r FROM ReporteLineaArrendatarioNng r")
public class ReporteLineaArrendatarioNng extends ReporteNng implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Total numeracion activa. */
    @Column(name = "TOTAL_NUM_ACTIVA")
    private BigDecimal totalNumActiva;

    /** Total numeracion rentada. */
    @Column(name = "TOTAL_NUM_RENTADA")
    private BigDecimal totalNumRentada;

    /** Detalles del reporte de lineas del arrendador. */
    @OneToMany(mappedBy = "reporteLineaArrendatario")
    private List<DetalleLineaArrendatarioNng> detallesLineaArrendatario;

    /** Constructor, vacio por defecto. */
    public ReporteLineaArrendatarioNng() {
    }

    /**
     * totalNumActiva.
     * @return the totalNumActiva
     */
    public BigDecimal getTotalNumActiva() {
        return totalNumActiva;
    }

    /**
     * totalNumActiva.
     * @param totalNumActiva the totalNumActiva to set
     */
    public void setTotalNumActiva(BigDecimal totalNumActiva) {
        this.totalNumActiva = totalNumActiva;
    }

    /**
     * Total numeracion rentada.
     * @return the totalNumRentada
     */
    public BigDecimal getTotalNumRentada() {
        return totalNumRentada;
    }

    /**
     * Total numeracion rentada.
     * @param totalNumRentada the totalNumRentada to set
     */
    public void setTotalNumRentada(BigDecimal totalNumRentada) {
        this.totalNumRentada = totalNumRentada;
    }

    /**
     * Detalles del reporte de lineas del arrendador.
     * @return the detallesLineaArrendatario
     */
    public List<DetalleLineaArrendatarioNng> getDetallesLineaArrendatario() {
        return detallesLineaArrendatario;
    }

    /**
     * Detalles del reporte de lineas del arrendador.
     * @param detallesLineaArrendatario the detallesLineaArrendatario to set
     */
    public void setDetallesLineaArrendatario(List<DetalleLineaArrendatarioNng> detallesLineaArrendatario) {
        this.detallesLineaArrendatario = detallesLineaArrendatario;
    }

    /**
     * Añade un detalle al reporte.
     * @param detalle detalle
     * @return detalle
     */
    public DetalleLineaArrendatarioNng addNngRepDetLinArrtario(DetalleLineaArrendatarioNng detalle) {
        getDetallesLineaArrendatario().add(detalle);
        detalle.setReporteLineaArrendatario(this);

        return detalle;
    }

    /**
     * Elmina un detalle al reporte.
     * @param detalle detalle
     * @return detalle
     */
    public DetalleLineaArrendatarioNng removeNngRepDetLinArrtario(DetalleLineaArrendatarioNng detalle) {
        getDetallesLineaArrendatario().remove(detalle);
        detalle.setReporteLineaArrendatario(null);

        return detalle;
    }

}
