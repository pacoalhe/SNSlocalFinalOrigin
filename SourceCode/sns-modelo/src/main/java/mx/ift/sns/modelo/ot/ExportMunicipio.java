package mx.ift.sns.modelo.ot;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * The persistent class for the EXP_MUNICIPIO_VM database view.
 */
@Entity
@Table(name = "EXP_MUNICIPIO_VM")
@Cacheable(false)
@ReadOnly
public class ExportMunicipio implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Id interno vista. */
    @Id
    private BigDecimal identificador;

    /** Clave Censal. */

    @Column(name = "CLAVE_CENSAL")
    private String claveCensal;

    /** ABN. */
    @Column(name = "ID_ABN")
    private BigDecimal idAbn;

    /** Codigo Estado. */
    @Column(name = "ID_ESTADO")
    private String idEstado;

    /** Codigo Municipio. */
    @Column(name = "ID_MUNICIPIO")
    private String idMunicipio;

    /** Nombre Estado. */
    @Column(name = "NOMBRE_ESTADO")
    private String nombreEstado;

    /** Nombre Municipio. */
    @Column(name = "NOMBRE_MUNICIPIO")
    private String nombreMunicipio;

    /** Nombre Poblacion. */
    @Column(name = "NOMBRE_POBLACION")
    private String nombrePoblacion;

    /** Numeracion Asignada. */
    @Column(name = "NUMERACION_ASIGNADA")
    private String numeracionAsignada;

    /** Presuscripcion. */
    private String presuscripcion;

    /** Region Celular. */
    @Column(name = "REGION_CELULAR")
    private BigDecimal regionCelular;

    /** Region PCS. */
    @Column(name = "REGION_PCS")
    private BigDecimal regionPcs;

    /**
     * Constructor.
     */
    public ExportMunicipio() {
    }

    /**
     * @return the claveCensal
     */
    public String getClaveCensal() {
        return claveCensal;
    }

    /**
     * @param claveCensal the claveCensal to set
     */
    public void setClaveCensal(String claveCensal) {
        this.claveCensal = claveCensal;
    }

    /**
     * @return the idAbn
     */
    public BigDecimal getIdAbn() {
        return idAbn;
    }

    /**
     * @param idAbn the idAbn to set
     */
    public void setIdAbn(BigDecimal idAbn) {
        this.idAbn = idAbn;
    }

    /**
     * @return the idEstado
     */
    public String getIdEstado() {
        return idEstado;
    }

    /**
     * @param idEstado the idEstado to set
     */
    public void setIdEstado(String idEstado) {
        this.idEstado = idEstado;
    }

    /**
     * @return the idMunicipio
     */
    public String getIdMunicipio() {
        return idMunicipio;
    }

    /**
     * @param idMunicipio the idMunicipio to set
     */
    public void setIdMunicipio(String idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    /**
     * @return the nombreEstado
     */
    public String getNombreEstado() {
        return nombreEstado;
    }

    /**
     * @param nombreEstado the nombreEstado to set
     */
    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }

    /**
     * @return the nombreMunicipio
     */
    public String getNombreMunicipio() {
        return nombreMunicipio;
    }

    /**
     * @param nombreMunicipio the nombreMunicipio to set
     */
    public void setNombreMunicipio(String nombreMunicipio) {
        this.nombreMunicipio = nombreMunicipio;
    }

    /**
     * @return the nombrePoblacion
     */
    public String getNombrePoblacion() {
        return nombrePoblacion;
    }

    /**
     * @param nombrePoblacion the nombrePoblacion to set
     */
    public void setNombrePoblacion(String nombrePoblacion) {
        this.nombrePoblacion = nombrePoblacion;
    }

    /**
     * @return the numeracionAsignada
     */
    public String getNumeracionAsignada() {
        return numeracionAsignada;
    }

    /**
     * @param numeracionAsignada the numeracionAsignada to set
     */
    public void setNumeracionAsignada(String numeracionAsignada) {
        this.numeracionAsignada = numeracionAsignada;
    }

    /**
     * @return the presuscripcion
     */
    public String getPresuscripcion() {
        return presuscripcion;
    }

    /**
     * @param presuscripcion the presuscripcion to set
     */
    public void setPresuscripcion(String presuscripcion) {
        this.presuscripcion = presuscripcion;
    }

    /**
     * @return the regionCelular
     */
    public BigDecimal getRegionCelular() {
        return regionCelular;
    }

    /**
     * @param regionCelular the regionCelular to set
     */
    public void setRegionCelular(BigDecimal regionCelular) {
        this.regionCelular = regionCelular;
    }

    /**
     * @return the regionPcs
     */
    public BigDecimal getRegionPcs() {
        return regionPcs;
    }

    /**
     * @param regionPcs the regionPcs to set
     */
    public void setRegionPcs(BigDecimal regionPcs) {
        this.regionPcs = regionPcs;
    }

    /**
     * @return the identificador
     */
    public BigDecimal getIdentificador() {
        return identificador;
    }

    /**
     * @param identificador the identificador to set
     */
    public void setIdentificador(BigDecimal identificador) {
        this.identificador = identificador;
    }

}
