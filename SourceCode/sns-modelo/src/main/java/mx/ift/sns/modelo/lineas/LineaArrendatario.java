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
 * Representa el reporte de línea de arrendatario.
 */
@Entity
@Table(name = "REP_LINEA_ARRENDATARIO")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("3")
@NamedQuery(name = "LineaArrendatario.findAll", query = "SELECT l FROM LineaArrendatario l")
public class LineaArrendatario extends Reporte implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Suma numeracion rentada. */
    @Column(name = "SUMA_NUM_RENTADA")
    private BigDecimal sumaNumRentada;

    /** Suma numeracion rentada activa. */
    @Column(name = "SUMA_NUM_RENTADA_ACTIVA")
    private BigDecimal sumaNumRentadaActiva;

    /** Suma numeracion rentada activa CPP. */
    @Column(name = "SUMA_NUM_RENTADA_ACTIVA_CPP")
    private BigDecimal sumaNumRentadaActivaCpp;

    /** Suma numeracion rentada activa Movil. */
    @Column(name = "SUMA_NUM_RENTADA_ACTIVA_MOVIL")
    private BigDecimal sumaNumRentadaActivaMovil;

    /** Suma numeracion rentada activa MPP. */
    @Column(name = "SUMA_NUM_RENTADA_ACTIVA_MPP")
    private BigDecimal sumaNumRentadaActivaMpp;

    /** Suma numeracion rentada activa Fijo. */
    @Column(name = "SUMA_NUM_RENTADA_FIJO")
    private BigDecimal sumaNumRentadaFijo;

    /**
     * bi-directional many-to-one association to DetLineaActiva.
     */
    @OneToMany(mappedBy = "lineaArrendatario")
    private List<DetLineaArrendatario> repDetLineasArrendatario;

    /**
     * Constructor.
     */
    public LineaArrendatario() {
    }

    /**
     * @return the sumaNumRentadaActiva
     */
    public BigDecimal getSumaNumRentadaActiva() {
        return sumaNumRentadaActiva;
    }

    /**
     * @param sumaNumRentadaActiva the sumaNumRentadaActiva to set
     */
    public void setSumaNumRentadaActiva(BigDecimal sumaNumRentadaActiva) {
        this.sumaNumRentadaActiva = sumaNumRentadaActiva;
    }

    /**
     * @return the repDetLineasArrendatario
     */
    public List<DetLineaArrendatario> getRepDetLineasArrendatario() {
        return repDetLineasArrendatario;
    }

    /**
     * @param repDetLineasArrendatario the repDetLineasArrendatario to set
     */
    public void setRepDetLineasArrendatario(List<DetLineaArrendatario> repDetLineasArrendatario) {
        this.repDetLineasArrendatario = repDetLineasArrendatario;
    }

    /**
     * Add repDetLineasArrendatario.
     * @param detLineasArrendatario detalle
     * @return DetRepLineaArrendatario
     */
    public DetLineaArrendatario addRepDetLineasArrendatario(DetLineaArrendatario detLineasArrendatario) {
        getRepDetLineasArrendatario().add(detLineasArrendatario);
        detLineasArrendatario.setLineaArrendatario(this);

        return detLineasArrendatario;
    }

    /**
     * Remove repDetLineasArrendatario.
     * @param detLineasArrendatario detalle
     * @return DetRepLineaArrendatario
     */
    public DetLineaArrendatario removeRepDetLineasActiva(DetLineaArrendatario detLineasArrendatario) {
        getRepDetLineasArrendatario().remove(detLineasArrendatario);
        detLineasArrendatario.setLineaArrendatario(null);

        return detLineasArrendatario;
    }

    /**
     * Suma numeracion rentada.
     * @return the sumaNumRentada
     */
    public BigDecimal getSumaNumRentada() {
        return sumaNumRentada;
    }

    /**
     * Suma numeracion rentada.
     * @param sumaNumRentada the sumaNumRentada to set
     */
    public void setSumaNumRentada(BigDecimal sumaNumRentada) {
        this.sumaNumRentada = sumaNumRentada;
    }

    /**
     * Suma numeracion rentada activa CPP.
     * @return the sumaNumRentadaActivaCpp
     */
    public BigDecimal getSumaNumRentadaActivaCpp() {
        return sumaNumRentadaActivaCpp;
    }

    /**
     * Suma numeracion rentada activa CPP.
     * @param sumaNumRentadaActivaCpp the sumaNumRentadaActivaCpp to set
     */
    public void setSumaNumRentadaActivaCpp(BigDecimal sumaNumRentadaActivaCpp) {
        this.sumaNumRentadaActivaCpp = sumaNumRentadaActivaCpp;
    }

    /**
     * Suma numeracion rentada activa Movil.
     * @return the sumaNumRentadaActivaMovil
     */
    public BigDecimal getSumaNumRentadaActivaMovil() {
        return sumaNumRentadaActivaMovil;
    }

    /**
     * Suma numeracion rentada activa Movil.
     * @param sumaNumRentadaActivaMovil the sumaNumRentadaActivaMovil to set
     */
    public void setSumaNumRentadaActivaMovil(BigDecimal sumaNumRentadaActivaMovil) {
        this.sumaNumRentadaActivaMovil = sumaNumRentadaActivaMovil;
    }

    /**
     * Suma numeracion rentada activa MPP.
     * @return the sumaNumRentadaActivaMpp
     */
    public BigDecimal getSumaNumRentadaActivaMpp() {
        return sumaNumRentadaActivaMpp;
    }

    /**
     * Suma numeracion rentada activa MPP.
     * @param sumaNumRentadaActivaMpp the sumaNumRentadaActivaMpp to set
     */
    public void setSumaNumRentadaActivaMpp(BigDecimal sumaNumRentadaActivaMpp) {
        this.sumaNumRentadaActivaMpp = sumaNumRentadaActivaMpp;
    }

    /**
     * Suma numeracion rentada activa Fijo.
     * @return the sumaNumRentadaFijo
     */
    public BigDecimal getSumaNumRentadaFijo() {
        return sumaNumRentadaFijo;
    }

    /**
     * Suma numeracion rentada activa Fijo.
     * @param sumaNumRentadaFijo the sumaNumRentadaFijo to set
     */
    public void setSumaNumRentadaFijo(BigDecimal sumaNumRentadaFijo) {
        this.sumaNumRentadaFijo = sumaNumRentadaFijo;
    }
}
