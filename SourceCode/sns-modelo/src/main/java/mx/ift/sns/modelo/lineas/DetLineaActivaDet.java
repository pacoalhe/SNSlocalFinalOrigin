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

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.ot.Poblacion;

/**
 * Representa el detalle de lineas activas.
 */
@Entity
@Table(name = "REP_DET_LINEA_ACTIVA_DET")
@NamedQuery(name = "DetLineaActivaDet.findAll", query = "SELECT d FROM DetLineaActivaDet d")
public class DetLineaActivaDet implements Serializable {

    /**
     * Serial ID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * ID.
     */
    @Id
    @SequenceGenerator(name = "REP_DET_LINEAS_ACTIVAS_DET_ID_GENERATOR", sequenceName = "SEQ_ID_DET_LINEAS_ACTIVAS_DET",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REP_DET_LINEAS_ACTIVAS_DET_ID_GENERATOR")
    @Column(name = "ID_REP_REPORTE", unique = true, nullable = false)
    private long id;

    /** Poblacion. */
    @ManyToOne
    @JoinColumn(name = "ID_INEGI", nullable = false)
    private Poblacion poblacion;

    /** ABN. */
    @ManyToOne
    @JoinColumn(name = "ID_ABN", nullable = false)
    private Abn abn;

    /** Total numeros asignados. */
    @Column(name = "TOTAL_NUM_ASIGANDOS")
    private BigDecimal totalNumAsigandos;

    /** Total numeros en cuerentena CPP. */
    @Column(name = "TOTAL_NUM_CUARENTENA_CPP")
    private BigDecimal totalNumCuarentenaCpp;

    /** Total numeros en cuarentena Fijo. */
    @Column(name = "TOTAL_NUM_CUARENTENA_FIJO")
    private BigDecimal totalNumCuarentenaFijo;

    /** Total numeros en cuarentena MPP. */
    @Column(name = "TOTAL_NUM_CUARENTENA_MPP")
    private BigDecimal totalNumCuarentenaMpp;

    /** Total numeros portados CPP. */
    @Column(name = "TOTAL_NUM_PORTADOS_CPP")
    private BigDecimal totalNumPortadosCpp;

    /** Total numeros portados Fijo. */
    @Column(name = "TOTAL_NUM_PORTADOS_FIJO")
    private BigDecimal totalNumPortadosFijo;

    /** Total numeros portados MPP. */
    @Column(name = "TOTAL_NUM_PORTADOS_MPP")
    private BigDecimal totalNumPortadosMpp;

    /** Total numeros en servicio CPP. */
    @Column(name = "TOTAL_NUM_SERVICIO_CPP")
    private BigDecimal totalNumServicioCpp;

    /** Total numeros en servicio Fijo. */
    @Column(name = "TOTAL_NUM_SERVICIO_FIJO")
    private BigDecimal totalNumServicioFijo;

    /** Total numeros en servicio MPP. */
    @Column(name = "TOTAL_NUM_SERVICIO_MPP")
    private BigDecimal totalNumServicioMpp;

    /** Total numeros telefonia publica CPP. */
    @Column(name = "TOTAL_NUM_TEL_PUB_CPP")
    private BigDecimal totalNumTelPubCpp;

    /** Total numeros telefonia publica Fijo. */
    @Column(name = "TOTAL_NUM_TEL_PUB_FIJO")
    private BigDecimal totalNumTelPubFijo;

    /** Total numeros telefonia publica MPP. */
    @Column(name = "TOTAL_NUM_TEL_PUB_MPP")
    private BigDecimal totalNumTelPubMpp;

    /** Total numeros de uso interno CPP. */
    @Column(name = "TOTAL_NUM_USO_INTERNO_CPP")
    private BigDecimal totalNumUsoInternoCpp;

    /** Total numeros de uso interno Fijo. */
    @Column(name = "TOTAL_NUM_USO_INTERNO_FIJO")
    private BigDecimal totalNumUsoInternoFijo;

    /** Total numeros de uso interno MPP. */
    @Column(name = "TOTAL_NUM_USO_INTERNO_MPP")
    private BigDecimal totalNumUsoInternoMpp;

    /** Reporte de linea activa detallado. */
    @ManyToOne
    @JoinColumn(name = "ID_REPOR")
    private LineaActivaDet lineasActivasDet;

    /** Constructor vacio por defecto. */
    public DetLineaActivaDet() {
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
     * Poblacion.
     * @return the poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * Poblacion.
     * @param poblacion the poblacion to set
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * ABN.
     * @return the abn
     */
    public Abn getAbn() {
        return abn;
    }

    /**
     * ABN.
     * @param abn the abn to set
     */
    public void setAbn(Abn abn) {
        this.abn = abn;
    }

    /**
     * Total numeros asignados.
     * @return the totalNumAsigandos
     */
    public BigDecimal getTotalNumAsigandos() {
        return totalNumAsigandos;
    }

    /**
     * Total numeros asignados.
     * @param totalNumAsigandos the totalNumAsigandos to set
     */
    public void setTotalNumAsigandos(BigDecimal totalNumAsigandos) {
        this.totalNumAsigandos = totalNumAsigandos;
    }

    /**
     * Total numeros en cuerentena CPP.
     * @return the totalNumCuarentenaCpp
     */
    public BigDecimal getTotalNumCuarentenaCpp() {
        return totalNumCuarentenaCpp;
    }

    /**
     * Total numeros en cuerentena CPP.
     * @param totalNumCuarentenaCpp the totalNumCuarentenaCpp to set
     */
    public void setTotalNumCuarentenaCpp(BigDecimal totalNumCuarentenaCpp) {
        this.totalNumCuarentenaCpp = totalNumCuarentenaCpp;
    }

    /**
     * Total numeros en cuarentena Fijo.
     * @return the totalNumCuarentenaFijo
     */
    public BigDecimal getTotalNumCuarentenaFijo() {
        return totalNumCuarentenaFijo;
    }

    /**
     * Total numeros en cuarentena Fijo.
     * @param totalNumCuarentenaFijo the totalNumCuarentenaFijo to set
     */
    public void setTotalNumCuarentenaFijo(BigDecimal totalNumCuarentenaFijo) {
        this.totalNumCuarentenaFijo = totalNumCuarentenaFijo;
    }

    /**
     * Total numeros en cuarentena MPP.
     * @return the totalNumCuarentenaMpp
     */
    public BigDecimal getTotalNumCuarentenaMpp() {
        return totalNumCuarentenaMpp;
    }

    /**
     * Total numeros en cuarentena MPP.
     * @param totalNumCuarentenaMpp the totalNumCuarentenaMpp to set
     */
    public void setTotalNumCuarentenaMpp(BigDecimal totalNumCuarentenaMpp) {
        this.totalNumCuarentenaMpp = totalNumCuarentenaMpp;
    }

    /**
     * Total numeros portados CPP.
     * @return the totalNumPortadosCpp
     */
    public BigDecimal getTotalNumPortadosCpp() {
        return totalNumPortadosCpp;
    }

    /**
     * Total numeros portados CPP.
     * @param totalNumPortadosCpp the totalNumPortadosCpp to set
     */
    public void setTotalNumPortadosCpp(BigDecimal totalNumPortadosCpp) {
        this.totalNumPortadosCpp = totalNumPortadosCpp;
    }

    /**
     * Total numeros portados Fijo.
     * @return the totalNumPortadosFijo
     */
    public BigDecimal getTotalNumPortadosFijo() {
        return totalNumPortadosFijo;
    }

    /**
     * Total numeros portados Fijo.
     * @param totalNumPortadosFijo the totalNumPortadosFijo to set
     */
    public void setTotalNumPortadosFijo(BigDecimal totalNumPortadosFijo) {
        this.totalNumPortadosFijo = totalNumPortadosFijo;
    }

    /**
     * Total numeros portados MPP.
     * @return the totalNumPortadosMpp
     */
    public BigDecimal getTotalNumPortadosMpp() {
        return totalNumPortadosMpp;
    }

    /**
     * Total numeros portados MPP.
     * @param totalNumPortadosMpp the totalNumPortadosMpp to set
     */
    public void setTotalNumPortadosMpp(BigDecimal totalNumPortadosMpp) {
        this.totalNumPortadosMpp = totalNumPortadosMpp;
    }

    /**
     * Total numeros en servicio CPP.
     * @return the totalNumServicioCpp
     */
    public BigDecimal getTotalNumServicioCpp() {
        return totalNumServicioCpp;
    }

    /**
     * Total numeros en servicio CPP.
     * @param totalNumServicioCpp the totalNumServicioCpp to set
     */
    public void setTotalNumServicioCpp(BigDecimal totalNumServicioCpp) {
        this.totalNumServicioCpp = totalNumServicioCpp;
    }

    /**
     * Total numeros en servicio Fijo.
     * @return the totalNumServicioFijo
     */
    public BigDecimal getTotalNumServicioFijo() {
        return totalNumServicioFijo;
    }

    /**
     * Total numeros en servicio Fijo.
     * @param totalNumServicioFijo the totalNumServicioFijo to set
     */
    public void setTotalNumServicioFijo(BigDecimal totalNumServicioFijo) {
        this.totalNumServicioFijo = totalNumServicioFijo;
    }

    /**
     * Total numeros en servicio MPP.
     * @return the totalNumServicioMpp
     */
    public BigDecimal getTotalNumServicioMpp() {
        return totalNumServicioMpp;
    }

    /**
     * Total numeros en servicio MPP.
     * @param totalNumServicioMpp the totalNumServicioMpp to set
     */
    public void setTotalNumServicioMpp(BigDecimal totalNumServicioMpp) {
        this.totalNumServicioMpp = totalNumServicioMpp;
    }

    /**
     * Total numeros telefonia publica CPP.
     * @return the totalNumTelPubCpp
     */
    public BigDecimal getTotalNumTelPubCpp() {
        return totalNumTelPubCpp;
    }

    /**
     * Total numeros telefonia publica CPP.
     * @param totalNumTelPubCpp the totalNumTelPubCpp to set
     */
    public void setTotalNumTelPubCpp(BigDecimal totalNumTelPubCpp) {
        this.totalNumTelPubCpp = totalNumTelPubCpp;
    }

    /**
     * Total numeros telefonia publica Fijo.
     * @return the totalNumTelPubFijo
     */
    public BigDecimal getTotalNumTelPubFijo() {
        return totalNumTelPubFijo;
    }

    /**
     * Total numeros telefonia publica Fijo.
     * @param totalNumTelPubFijo the totalNumTelPubFijo to set
     */
    public void setTotalNumTelPubFijo(BigDecimal totalNumTelPubFijo) {
        this.totalNumTelPubFijo = totalNumTelPubFijo;
    }

    /**
     * Total numeros telefonia publica MPP.
     * @return the totalNumTelPubMpp
     */
    public BigDecimal getTotalNumTelPubMpp() {
        return totalNumTelPubMpp;
    }

    /**
     * Total numeros telefonia publica MPP.
     * @param totalNumTelPubMpp the totalNumTelPubMpp to set
     */
    public void setTotalNumTelPubMpp(BigDecimal totalNumTelPubMpp) {
        this.totalNumTelPubMpp = totalNumTelPubMpp;
    }

    /**
     * Total numeros de uso interno CPP.
     * @return the totalNumUsoInternoCpp
     */
    public BigDecimal getTotalNumUsoInternoCpp() {
        return totalNumUsoInternoCpp;
    }

    /**
     * Total numeros de uso interno CPP.
     * @param totalNumUsoInternoCpp the totalNumUsoInternoCpp to set
     */
    public void setTotalNumUsoInternoCpp(BigDecimal totalNumUsoInternoCpp) {
        this.totalNumUsoInternoCpp = totalNumUsoInternoCpp;
    }

    /**
     * Total numeros de uso interno Fijo.
     * @return the totalNumUsoInternoFijo
     */
    public BigDecimal getTotalNumUsoInternoFijo() {
        return totalNumUsoInternoFijo;
    }

    /**
     * Total numeros de uso interno Fijo.
     * @param totalNumUsoInternoFijo the totalNumUsoInternoFijo to set
     */
    public void setTotalNumUsoInternoFijo(BigDecimal totalNumUsoInternoFijo) {
        this.totalNumUsoInternoFijo = totalNumUsoInternoFijo;
    }

    /**
     * Total numeros de uso interno MPP.
     * @return the totalNumUsoInternoMpp
     */
    public BigDecimal getTotalNumUsoInternoMpp() {
        return totalNumUsoInternoMpp;
    }

    /**
     * Total numeros de uso interno MPP.
     * @param totalNumUsoInternoMpp the totalNumUsoInternoMpp to set
     */
    public void setTotalNumUsoInternoMpp(BigDecimal totalNumUsoInternoMpp) {
        this.totalNumUsoInternoMpp = totalNumUsoInternoMpp;
    }

    /**
     * lineasActivasDet.
     * @return the lineasActivasDet
     */
    public LineaActivaDet getLineasActivasDet() {
        return lineasActivasDet;
    }

    /**
     * lineasActivasDet.
     * @param lineasActivasDet the lineasActivasDet to set
     */
    public void setLineasActivasDet(LineaActivaDet lineasActivasDet) {
        this.lineasActivasDet = lineasActivasDet;
    }
}
