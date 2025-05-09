package mx.ift.sns.modelo.central;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Representa la vista para reportes de central.
 */
@Entity
@Table(name = "REPORTE_CENTRAL_VM")
@NamedQuery(name = "ReporteCentralVm.findAll", query = "SELECT r FROM ReporteCentralVm r")
public class ReporteCentralVm implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @Column(name = "rownum")
    private BigDecimal id;

    /** Descripcion. */
    private String descripcion;

    /** EstadoNombre. */
    @Column(name = "ESTADO_NOMBRE")
    private String estadoNombre;

    /** idAbn. */
    @Column(name = "ID_ABN")
    private BigDecimal idAbn;

    /** idCentral. */
    @Column(name = "ID_CENTRAL")
    private BigDecimal idCentral;

    /** idEstado. */
    @Column(name = "ID_ESTADO")
    private String idEstado;

    /** idEstatus. */
    @Column(name = "ID_ESTATUS")
    private BigDecimal idEstatus;

    /** idInegi. */
    @Column(name = "ID_INEGI")
    private String idInegi;

    /** idMunicipio. */
    @Column(name = "ID_MUNICIPIO")
    private String idMunicipio;

    /** idPst. */
    @Column(name = "ID_PST")
    private BigDecimal idPst;

    /** municipioNombre. */
    @Column(name = "MUNICIPIO_NOMBRE")
    private String municipioNombre;

    /** nombreCentral. */
    @Column(name = "NOMBRE_CENTRAL")
    private String nombreCentral;

    /** poblacionNombre. */
    @Column(name = "POBLACION_NOMBRE")
    private String poblacionNombre;

    /** pstNombre. */
    @Column(name = "PST_NOMBRE")
    private String pstNombre;

    /** Constructor, por defecto vacío. */
    public ReporteCentralVm() {
    }

    /**
     * Identificador.
     * @return the id
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Identificador.
     * @param id the id to set
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Descripcion.
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Descripcion.
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * EstadoNombre.
     * @return the estadoNombre
     */
    public String getEstadoNombre() {
        return estadoNombre;
    }

    /**
     * EstadoNombre.
     * @param estadoNombre the estadoNombre to set
     */
    public void setEstadoNombre(String estadoNombre) {
        this.estadoNombre = estadoNombre;
    }

    /**
     * idAbn.
     * @return the idAbn
     */
    public BigDecimal getIdAbn() {
        return idAbn;
    }

    /**
     * idAbn.
     * @param idAbn the idAbn to set
     */
    public void setIdAbn(BigDecimal idAbn) {
        this.idAbn = idAbn;
    }

    /**
     * idCentral.
     * @return the idCentral
     */
    public BigDecimal getIdCentral() {
        return idCentral;
    }

    /**
     * idCentral.
     * @param idCentral the idCentral to set
     */
    public void setIdCentral(BigDecimal idCentral) {
        this.idCentral = idCentral;
    }

    /**
     * idEstado.
     * @return the idEstado
     */
    public String getIdEstado() {
        return idEstado;
    }

    /**
     * idEstado.
     * @param idEstado the idEstado to set
     */
    public void setIdEstado(String idEstado) {
        this.idEstado = idEstado;
    }

    /**
     * idEstatus.
     * @return the idEstatus
     */
    public BigDecimal getIdEstatus() {
        return idEstatus;
    }

    /**
     * idEstatus.
     * @param idEstatus the idEstatus to set
     */
    public void setIdEstatus(BigDecimal idEstatus) {
        this.idEstatus = idEstatus;
    }

    /**
     * idInegi.
     * @return the idInegi
     */
    public String getIdInegi() {
        return idInegi;
    }

    /**
     * idInegi.
     * @param idInegi the idInegi to set
     */
    public void setIdInegi(String idInegi) {
        this.idInegi = idInegi;
    }

    /**
     * idMunicipio.
     * @return the idMunicipio
     */
    public String getIdMunicipio() {
        return idMunicipio;
    }

    /**
     * idMunicipio.
     * @param idMunicipio the idMunicipio to set
     */
    public void setIdMunicipio(String idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    /**
     * idPst.
     * @return the idPst
     */
    public BigDecimal getIdPst() {
        return idPst;
    }

    /**
     * idPst.
     * @param idPst the idPst to set
     */
    public void setIdPst(BigDecimal idPst) {
        this.idPst = idPst;
    }

    /**
     * municipioNombre.
     * @return the municipioNombre
     */
    public String getMunicipioNombre() {
        return municipioNombre;
    }

    /**
     * municipioNombre.
     * @param municipioNombre the municipioNombre to set
     */
    public void setMunicipioNombre(String municipioNombre) {
        this.municipioNombre = municipioNombre;
    }

    /**
     * nombreCentral.
     * @return the nombreCentral
     */
    public String getNombreCentral() {
        return nombreCentral;
    }

    /**
     * nombreCentral.
     * @param nombreCentral the nombreCentral to set
     */
    public void setNombreCentral(String nombreCentral) {
        this.nombreCentral = nombreCentral;
    }

    /**
     * poblacionNombre.
     * @return the poblacionNombre
     */
    public String getPoblacionNombre() {
        return poblacionNombre;
    }

    /**
     * poblacionNombre.
     * @param poblacionNombre the poblacionNombre to set
     */
    public void setPoblacionNombre(String poblacionNombre) {
        this.poblacionNombre = poblacionNombre;
    }

    /**
     * pstNombre.
     * @return the pstNombre
     */
    public String getPstNombre() {
        return pstNombre;
    }

    /**
     * pstNombre.
     * @param pstNombre the pstNombre to set
     */
    public void setPstNombre(String pstNombre) {
        this.pstNombre = pstNombre;
    }
}
