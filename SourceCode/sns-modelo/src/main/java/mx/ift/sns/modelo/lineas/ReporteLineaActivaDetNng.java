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
 * Representa el detalle del reporte de línas activas de numeración no geográfica.
 */
@Entity
@Table(name = "NNG_REP_LINEA_ACT_DET")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("2")
@NamedQuery(name = "ReporteLineaActivaDetNng.findAll", query = "SELECT r FROM ReporteLineaActivaDetNng r")
public class ReporteLineaActivaDetNng extends ReporteNng implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Total numeracion activa. */
    @Column(name = "TOTAL_NUM_ACTIVA")
    private BigDecimal totalNumActiva;

    /** total numeracion cuarentena. */
    @Column(name = "TOTAL_NUM_CUARENTENA")
    private BigDecimal totalNumCuarentena;

    /** total numeracion portada. */
    @Column(name = "TOTAL_NUM_PORTADA")
    private BigDecimal totalNumPortada;

    /** total numeracion servicio. */
    @Column(name = "TOTAL_NUM_SERVICIO")
    private BigDecimal totalNumServicio;

    /** Detalles del reporte de linea activa detallada. */
    @OneToMany(mappedBy = "reporteLineasActivasDet")
    private List<DetalleLineaActivaDetNng> detallesLineaActivaDet;

    /** Constructor, vacio por defecto. */
    public ReporteLineaActivaDetNng() {
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
     * total numeracion cuarentena.
     * @return the totalNumCuarentena
     */
    public BigDecimal getTotalNumCuarentena() {
        return totalNumCuarentena;
    }

    /**
     * total numeracion cuarentena.
     * @param totalNumCuarentena the totalNumCuarentena to set
     */
    public void setTotalNumCuarentena(BigDecimal totalNumCuarentena) {
        this.totalNumCuarentena = totalNumCuarentena;
    }

    /**
     * totalNumPortada.
     * @return the totalNumPortada
     */
    public BigDecimal getTotalNumPortada() {
        return totalNumPortada;
    }

    /**
     * totalNumPortada.
     * @param totalNumPortada the totalNumPortada to set
     */
    public void setTotalNumPortada(BigDecimal totalNumPortada) {
        this.totalNumPortada = totalNumPortada;
    }

    /**
     * total numeracion servicio.
     * @return the totalNumServicio
     */
    public BigDecimal getTotalNumServicio() {
        return totalNumServicio;
    }

    /**
     * total numeracion servicio.
     * @param totalNumServicio the totalNumServicio to set
     */
    public void setTotalNumServicio(BigDecimal totalNumServicio) {
        this.totalNumServicio = totalNumServicio;
    }

    /**
     * detallesLineaActivaDet.
     * @return the detallesLineaActivaDet
     */
    public List<DetalleLineaActivaDetNng> getDetallesLineaActivaDet() {
        return detallesLineaActivaDet;
    }

    /**
     * detallesLineaActivaDet.
     * @param detallesLineaActivaDet the detallesLineaActivaDet to set
     */
    public void setDetallesLineaActivaDet(List<DetalleLineaActivaDetNng> detallesLineaActivaDet) {
        this.detallesLineaActivaDet = detallesLineaActivaDet;
    }

    /**
     * Elmina un detalle al reporte.
     * @param detalle detalle
     * @return detalle
     */
    public DetalleLineaActivaDetNng addNngRepDetLinActDet(DetalleLineaActivaDetNng detalle) {
        getDetallesLineaActivaDet().add(detalle);
        detalle.setReporteLineasActivasDet(this);

        return detalle;
    }

    /**
     * Elmina un detalle al reporte.
     * @param detalle detalle
     * @return detalle
     */
    public DetalleLineaActivaDetNng removeNngRepDetLinActDet(DetalleLineaActivaDetNng detalle) {
        getDetallesLineaActivaDet().remove(detalle);
        detalle.setReporteLineasActivasDet(null);

        return detalle;
    }

}
