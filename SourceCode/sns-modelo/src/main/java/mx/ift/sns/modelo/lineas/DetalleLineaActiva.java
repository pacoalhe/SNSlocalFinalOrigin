package mx.ift.sns.modelo.lineas;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import mx.ift.sns.modelo.ot.Poblacion;

/**
 * Detalle de una linea del reporte de lineas activas.
 */
@Entity
@Table(name = "REP_DET_LINEA_ACTIVA")
@NamedQuery(name = "DetLineaActiva.findAll", query = "SELECT d FROM DetalleLineaActiva d")
public class DetalleLineaActiva implements Serializable {

    /**
     * Serial ID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * ID.
     */
    @Id
    @SequenceGenerator(name = "SEQ_ID_DET_LINEAS_ACTIVAS", sequenceName = "SEQ_ID_DET_LINEAS_ACTIVAS",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_DET_LINEAS_ACTIVAS")
    @Column(name = "ID_REP_DET_LINEA_ACTIVA", unique = true, nullable = false)
    private long id;

    /**
     * poblacion.
     */
    @ManyToOne
    @JoinColumn(name = "ID_INEGI", nullable = false)
    private Poblacion poblacion;

    /**
     * totalLineasActivas.
     */
    @Column(name = "TOTAL_LINEAS_ACTIVAS")
    private BigDecimal totalLineasActivas;

    /**
     * totalLineasActivasCpp.
     */
    @Column(name = "TOTAL_LINEAS_ACTIVAS_CPP")
    private BigDecimal totalLineasActivasCpp;

    /**
     * totalLineasActivasFijas.
     */
    @Column(name = "TOTAL_LINEAS_ACTIVAS_FIJAS")
    private BigDecimal totalLineasActivasFijas;

    /**
     * totalLineasActivasMpp.
     */
    @Column(name = "TOTAL_LINEAS_ACTIVAS_MPP")
    private BigDecimal totalLineasActivasMpp;

    /**
     * totalNumerosAsignados.
     */
    @Column(name = "TOTAL_NUMEROS_ASIGNADOS")
    private BigDecimal totalNumerosAsignados;

    /**
     * Nº Registro.
     */
    @Column
    private String registro;

    /**
     * bi-directional many-to-one association to LineasActiva.
     */
    @ManyToOne
    @JoinColumn(name = "ID_REPOR")
    private ReporteLineasActivas lineaActiva;

    /**
     * Constructor.
     */
    public DetalleLineaActiva() {
    }

    /**
     * ID.
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * ID.
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * totalLineasActivas.
     * @return the totalLineasActivas
     */
    public BigDecimal getTotalLineasActivas() {
        return totalLineasActivas;
    }

    /**
     * totalLineasActivas.
     * @param totalLineasActivas the totalLineasActivas to set
     */
    public void setTotalLineasActivas(BigDecimal totalLineasActivas) {
        this.totalLineasActivas = totalLineasActivas;
    }

    /**
     * totalLineasActivasCpp.
     * @return the totalLineasActivasCpp
     */
    public BigDecimal getTotalLineasActivasCpp() {
        return totalLineasActivasCpp;
    }

    /**
     * totalLineasActivasCpp.
     * @param totalLineasActivasCpp the totalLineasActivasCpp to set
     */
    public void setTotalLineasActivasCpp(BigDecimal totalLineasActivasCpp) {
        this.totalLineasActivasCpp = totalLineasActivasCpp;
    }

    /**
     * totalLineasActivasFijas.
     * @return the totalLineasActivasFijas
     */
    public BigDecimal getTotalLineasActivasFijas() {
        return totalLineasActivasFijas;
    }

    /**
     * totalLineasActivasFijas.
     * @param totalLineasActivasFijas the totalLineasActivasFijas to set
     */
    public void setTotalLineasActivasFijas(BigDecimal totalLineasActivasFijas) {
        this.totalLineasActivasFijas = totalLineasActivasFijas;
    }

    /**
     * totalLineasActivasMpp.
     * @return the totalLineasActivasMpp
     */
    public BigDecimal getTotalLineasActivasMpp() {
        return totalLineasActivasMpp;
    }

    /**
     * totalLineasActivasMpp.
     * @param totalLineasActivasMpp the totalLineasActivasMpp to set
     */
    public void setTotalLineasActivasMpp(BigDecimal totalLineasActivasMpp) {
        this.totalLineasActivasMpp = totalLineasActivasMpp;
    }

    /**
     * totalNumerosAsignados.
     * @return the totalNumerosAsignados
     */
    public BigDecimal getTotalNumerosAsignados() {
        return totalNumerosAsignados;
    }

    /**
     * totalNumerosAsignados.
     * @param totalNumerosAsignados the totalNumerosAsignados to set
     */
    public void setTotalNumerosAsignados(BigDecimal totalNumerosAsignados) {
        this.totalNumerosAsignados = totalNumerosAsignados;
    }

    /**
     * poblacion.
     * @return the poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * poblacion.
     * @param poblacion the poblacion to set
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * bi-directional many-to-one association to LineasActiva.
     * @return the lineaActiva
     */
    public ReporteLineasActivas getLineaActiva() {
        return lineaActiva;
    }

    /**
     * bi-directional many-to-one association to LineasActiva.
     * @param lineaActiva the lineaActiva to set
     */
    public void setLineaActiva(ReporteLineasActivas lineaActiva) {
        this.lineaActiva = lineaActiva;
    }

    /**
     * Nº Registro.
     * @return the registro
     */
    public String getRegistro() {
        return registro;
    }

    /**
     * Nº Registro.
     * @param registro the registro to set
     */
    public void setRegistro(String registro) {
        this.registro = registro;
    }

}
