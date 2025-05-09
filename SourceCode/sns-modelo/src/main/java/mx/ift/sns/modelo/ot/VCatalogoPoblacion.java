package mx.ift.sns.modelo.ot;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Vista sobre el Catálogo de Poblaciones.
 */
@Entity
@Table(name = "CATALOGO_POBLACION_VM")
@Cacheable(false)
@ReadOnly
public class VCatalogoPoblacion implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @Column(name = "CLAVE_CENSAL")
    private String id;

    /** ID del estado. */
    @Column(name = "ID_ESTADO", length = 2)
    private String idEstado;

    /** Nombre del Estado. */
    @Column(name = "NOMBRE_ESTADO", length = 60)
    private String nombreEstado;

    /** ID del municipio. */
    @Column(name = "ID_MUNICIPIO", length = 3)
    private String idMunicipio;

    /** Nombre del Municipio. */
    @Column(name = "NOMBRE_MUNICIPIO", length = 80)
    private String nombreMunicipio;

    /** Nombre de la Poblacion. */
    @Column(name = "NOMBRE_POBLACION", length = 100)
    private String nombrePoblacion;

    /** Numeración Asignada. */
    @Column(name = "NUMERACION_ASIGNADA")
    private String numeracionAsignada;

    /** I del ABN. */
    @Column(name = "ID_ABN")
    private String idAbn;

    /** Nirs. */
    @Column(name = "NIRS")
    private String nirs;

    /** Presuscripcion. */
    @Column(name = "PRESUSCRIPCION")
    private String presuscripcion;

    /** Región Antigua. */
    @Column(name = "REGION_ANTIGUA")
    private String regionAntigua;

    /** Región Celular. */
    @Column(name = "REGION_CELULAR")
    private String regionCelular;

    /** Región PCS. */
    @Column(name = "REGION_PCS")
    private String regionPcs;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
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
     * @return the idAbn
     */
    public String getIdAbn() {
        return idAbn;
    }

    /**
     * @param idAbn the idAbn to set
     */
    public void setIdAbn(String idAbn) {
        this.idAbn = idAbn;
    }

    /**
     * @return the nirs
     */
    public String getNirs() {
        return nirs;
    }

    /**
     * @param nirs the nirs to set
     */
    public void setNirs(String nirs) {
        this.nirs = nirs;
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
     * @return the regionAntigua
     */
    public String getRegionAntigua() {
        return regionAntigua;
    }

    /**
     * @param regionAntigua the regionAntigua to set
     */
    public void setRegionAntigua(String regionAntigua) {
        this.regionAntigua = regionAntigua;
    }

    /**
     * @return the regionCelular
     */
    public String getRegionCelular() {
        return regionCelular;
    }

    /**
     * @param regionCelular the regionCelular to set
     */
    public void setRegionCelular(String regionCelular) {
        this.regionCelular = regionCelular;
    }

    /**
     * @return the regionPcs
     */
    public String getRegionPcs() {
        return regionPcs;
    }

    /**
     * @param regionPcs the regionPcs to set
     */
    public void setRegionPcs(String regionPcs) {
        this.regionPcs = regionPcs;
    }

}
