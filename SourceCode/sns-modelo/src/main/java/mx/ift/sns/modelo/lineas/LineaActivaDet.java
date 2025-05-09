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
 * Representa el detalle del reporte de una línea activa.
 */
@Entity
@Table(name = "REP_LINEA_ACTIVA_DET")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("2")
@NamedQuery(name = "LineaActivaDet.findAll", query = "SELECT l FROM LineaActivaDet l")
public class LineaActivaDet extends Reporte implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Suma de lineas en cuarentena fijo. */
    @Column(name = "SUMA_LINEAS_CUARENTENA_FIJO")
    private BigDecimal sumaLineasCuarentenaFijo;

    /** Suma de lineas portada fijo. */
    @Column(name = "SUMA_LINEAS_PORTADAS_FIJO")
    private BigDecimal sumaLineasPortadasFijo;

    /** Suma de lineas de telefonia publica. */
    @Column(name = "SUMA_LINEAS_TEL_PUBLICA")
    private BigDecimal sumaLineasTelPublica;

    /** Suma de lineas de uso interno. */
    @Column(name = "SUMA_LINEAS_USO_INTERNO")
    private BigDecimal sumaLineasUsoInterno;

    /** Suma de lineas en servicio. */
    @Column(name = "SUMA_NUM_EN_SERVICIO")
    private BigDecimal sumaNumEnServicio;

    /**
     * Suma numeracion asiganda.
     */
    @Column(name = "SUMA_NUM_ASIGNADA")
    private BigDecimal sumaNumAsignada;

    /** Detalle del reporte de lineas activas dellado. */
    @OneToMany(mappedBy = "lineasActivasDet")
    private List<DetLineaActivaDet> repDetLineasActivasDets;

    /** Constructor, vacio por defecto. */
    public LineaActivaDet() {
    }

    /**
     * Suma de lineas en cuarentena fijo.
     * @return the sumaLineasCuarentenaFijo
     */
    public BigDecimal getSumaLineasCuarentenaFijo() {
        return sumaLineasCuarentenaFijo;
    }

    /**
     * Suma de lineas en cuarentena fijo.
     * @param sumaLineasCuarentenaFijo the sumaLineasCuarentenaFijo to set
     */
    public void setSumaLineasCuarentenaFijo(BigDecimal sumaLineasCuarentenaFijo) {
        this.sumaLineasCuarentenaFijo = sumaLineasCuarentenaFijo;
    }

    /**
     * Suma de lineas portada fijo.
     * @return the sumaLineasPortadasFijo
     */
    public BigDecimal getSumaLineasPortadasFijo() {
        return sumaLineasPortadasFijo;
    }

    /**
     * Suma de lineas portada fijo.
     * @param sumaLineasPortadasFijo the sumaLineasPortadasFijo to set
     */
    public void setSumaLineasPortadasFijo(BigDecimal sumaLineasPortadasFijo) {
        this.sumaLineasPortadasFijo = sumaLineasPortadasFijo;
    }

    /**
     * Suma de lineas de telefonia publica.
     * @return the sumaLineasTelPublica
     */
    public BigDecimal getSumaLineasTelPublica() {
        return sumaLineasTelPublica;
    }

    /**
     * Suma de lineas de telefonia publica.
     * @param sumaLineasTelPublica the sumaLineasTelPublica to set
     */
    public void setSumaLineasTelPublica(BigDecimal sumaLineasTelPublica) {
        this.sumaLineasTelPublica = sumaLineasTelPublica;
    }

    /**
     * Suma de lineas de uso interno.
     * @return the sumaLineasUsoInterno
     */
    public BigDecimal getSumaLineasUsoInterno() {
        return sumaLineasUsoInterno;
    }

    /**
     * Suma de lineas de uso interno.
     * @param sumaLineasUsoInterno the sumaLineasUsoInterno to set
     */
    public void setSumaLineasUsoInterno(BigDecimal sumaLineasUsoInterno) {
        this.sumaLineasUsoInterno = sumaLineasUsoInterno;
    }

    /**
     * Suma de lineas en servicio.
     * @return the sumaNumEnServicio
     */
    public BigDecimal getSumaNumEnServicio() {
        return sumaNumEnServicio;
    }

    /**
     * Suma de lineas en servicio.
     * @param sumaNumEnServicio the sumaNumEnServicio to set
     */
    public void setSumaNumEnServicio(BigDecimal sumaNumEnServicio) {
        this.sumaNumEnServicio = sumaNumEnServicio;
    }

    /**
     * Detalle del reporte de lineas activas dellado.
     * @return the repDetLineasActivasDets
     */
    public List<DetLineaActivaDet> getRepDetLineasActivasDets() {
        return repDetLineasActivasDets;
    }

    /**
     * Detalle del reporte de lineas activas dellado.
     * @param repDetLineasActivasDets the repDetLineasActivasDets to set
     */
    public void setRepDetLineasActivasDets(
            List<DetLineaActivaDet> repDetLineasActivasDets) {
        this.repDetLineasActivasDets = repDetLineasActivasDets;
    }

    /**
     * Añade un detalle al reporte.
     * @param repDetLineasActivasDet detalle
     * @return detalle
     */
    public DetLineaActivaDet addRepDetLineasActivasDet(DetLineaActivaDet repDetLineasActivasDet) {
        getRepDetLineasActivasDets().add(repDetLineasActivasDet);
        repDetLineasActivasDet.setLineasActivasDet(this);

        return repDetLineasActivasDet;
    }

    /**
     * Elimina un detalle al reporte.
     * @param repDetLineasActivasDet detalle
     * @return detalle
     */
    public DetLineaActivaDet removeRepDetLineasActivasDet(DetLineaActivaDet repDetLineasActivasDet) {
        getRepDetLineasActivasDets().remove(repDetLineasActivasDet);
        repDetLineasActivasDet.setLineasActivasDet(null);

        return repDetLineasActivasDet;
    }

    /**
     * Suma numeracion asiganda.
     * @return the sumaNumAsignada
     */
    public BigDecimal getSumaNumAsignada() {
        return sumaNumAsignada;
    }

    /**
     * Suma numeracion asiganda.
     * @param sumaNumAsignada the sumaNumAsignada to set
     */
    public void setSumaNumAsignada(BigDecimal sumaNumAsignada) {
        this.sumaNumAsignada = sumaNumAsignada;
    }

}
