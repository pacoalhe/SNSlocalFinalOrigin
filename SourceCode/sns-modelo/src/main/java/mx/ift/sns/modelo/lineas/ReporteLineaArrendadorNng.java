package mx.ift.sns.modelo.lineas;

import java.io.Serializable;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Representa el reporte de líneas de numeración no geográfica de arrendador.
 */
@Entity
@Table(name = "NNG_REP_LINEA_ARRENDADOR")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("4")
@NamedQuery(name = "ReporteLineaArrendadorNng.findAll", query = "SELECT r FROM ReporteLineaArrendadorNng r")
public class ReporteLineaArrendadorNng extends ReporteNng implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Detalles del reporte de lineas del arrendador. */
    @OneToMany(mappedBy = "reporteLineaArrendador")
    private List<DetalleLineaArrendadorNng> detallesLineaArrendador;

    /** Constructor, vacio por defecto. */
    public ReporteLineaArrendadorNng() {
    }

    /**
     * Detalles del reporte de lineas del arrendador.
     * @return the detallesLineaArrendador
     */
    public List<DetalleLineaArrendadorNng> getDetallesLineaArrendador() {
        return detallesLineaArrendador;
    }

    /**
     * Detalles del reporte de lineas del arrendador.
     * @param detallesLineaArrendador the detallesLineaArrendador to set
     */
    public void setDetallesLineaArrendador(List<DetalleLineaArrendadorNng> detallesLineaArrendador) {
        this.detallesLineaArrendador = detallesLineaArrendador;
    }

    /**
     * Añade un detalle al reporte.
     * @param detalle detalle
     * @return detalle
     */
    public DetalleLineaArrendadorNng addNngRepLinArrendadorDet(DetalleLineaArrendadorNng detalle) {
        getDetallesLineaArrendador().add(detalle);
        detalle.setReporteLineaArrendador(this);

        return detalle;
    }

    /**
     * Elmina un detalle al reporte.
     * @param detalle detalle
     * @return detalle
     */
    public DetalleLineaArrendadorNng removeNngRepLinArrendadorDet(DetalleLineaArrendadorNng detalle) {
        getDetallesLineaArrendador().remove(detalle);
        detalle.setReporteLineaArrendador(null);

        return detalle;
    }

}
