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
 * Representa el reporte de líneas activas de numeración no geográfica.
 */
@Entity
@Table(name = "NNG_REP_LINEA_ACTIVA")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("1")
@NamedQuery(name = "ReporteLineaActivaNng.findAll", query = "SELECT r FROM ReporteLineaActivaNng r")
public class ReporteLineaActivaNng extends ReporteNng implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Total numeracion activa. */
    @Column(name = "TOTAL_NUM_ACTIVA")
    private BigDecimal totalNumActiva;

    /** Detalles del reporte de linea actva. */
    @OneToMany(mappedBy = "reporteLineaActiva")
    private List<DetalleLineaActivaNng> detallesLineaActiva;

    /** Constructor, vacio por defecto. */
    public ReporteLineaActivaNng() {
    }

    /**
     * Total numeracion activa.
     * @return the totalNumActiva
     */
    public BigDecimal getTotalNumActiva() {
        return totalNumActiva;
    }

    /**
     * Total numeracion activa.
     * @param totalNumActiva the totalNumActiva to set
     */
    public void setTotalNumActiva(BigDecimal totalNumActiva) {
        this.totalNumActiva = totalNumActiva;
    }

    /**
     * Detalles del reporte de linea actva.
     * @return the detallesLineaActiva
     */
    public List<DetalleLineaActivaNng> getDetallesLineaActiva() {
        return detallesLineaActiva;
    }

    /**
     * Detalles del reporte de linea actva.
     * @param detallesLineaActiva the detallesLineaActiva to set
     */
    public void setDetallesLineaActiva(List<DetalleLineaActivaNng> detallesLineaActiva) {
        this.detallesLineaActiva = detallesLineaActiva;
    }

    /**
     * Añade un detalle al reporte.
     * @param detalle detalle
     * @return detalle
     */
    public DetalleLineaActivaNng addDetallesLineaActiva(DetalleLineaActivaNng detalle) {
        getDetallesLineaActiva().add(detalle);
        detalle.setReporteLineaActiva(this);

        return detalle;
    }

    /**
     * Elmina un detalle al reporte.
     * @param detalle detalle
     * @return detalle
     */
    public DetalleLineaActivaNng removeNngRepDetLineaActiva(DetalleLineaActivaNng detalle) {
        getDetallesLineaActiva().remove(detalle);
        detalle.setReporteLineaActiva(null);

        return detalle;
    }

}
