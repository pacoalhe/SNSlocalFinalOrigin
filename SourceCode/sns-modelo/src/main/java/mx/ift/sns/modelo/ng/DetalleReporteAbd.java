package mx.ift.sns.modelo.ng;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.NamedStoredProcedureQuery;
import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Representa la vista del detalle del reporte ABD.
 */

@NamedStoredProcedureQuery(name = "generarPnnAux", procedureName = "generarPnnAux")
@Entity
@Table(name = "REPORTE_ABD_VM")
@ReadOnly
@Cacheable(false)
public class DetalleReporteAbd implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Pk de la vista. */
    @Id
    @Column(name = "N_INICIO")
    private String id;

    /** N_FINAL. */
    @Column(name = "N_FINAL")
    private String numFinal;

    /** Ido. */
    @Column(name = "IDO")
    private BigDecimal ido;

    /** Ida. */
    @Column(name = "IDA")
    private BigDecimal ida;

    /** Id del abn. */
    @Column(name = "ID_ABN")
    private BigDecimal idAbn;

    /** Identificador del tipo de red. */
    @Column(name = "ID_TIPO_RED")
    private String idTipoRed;

    /** ID_TIPO_MODALIDAD. */
    @Column(name = "ID_TIPO_MODALIDAD")
    private String idTipoModalidad;

    /** Constructor por defecto. */
    public DetalleReporteAbd() {
    }

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
     * @return the numFinal
     */
    public String getNumFinal() {
        return numFinal;
    }

    /**
     * @param numFinal the numFinal to set
     */
    public void setNumFinal(String numFinal) {
        this.numFinal = numFinal;
    }

    /**
     * @return the ido
     */
    public BigDecimal getIdo() {
        return ido;
    }

    /**
     * @param ido the ido to set
     */
    public void setIdo(BigDecimal ido) {
        this.ido = ido;
    }

    /**
     * @return the ida
     */
    public BigDecimal getIda() {
        return ida;
    }

    /**
     * @param ida the ida to set
     */
    public void setIda(BigDecimal ida) {
        this.ida = ida;
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
     * @return the idTipoRed
     */
    public String getIdTipoRed() {
        return idTipoRed;
    }

    /**
     * @param idTipoRed the idTipoRed to set
     */
    public void setIdTipoRed(String idTipoRed) {
        this.idTipoRed = idTipoRed;
    }

    /**
     * @return the idTipoModalidad
     */
    public String getIdTipoModalidad() {
        return idTipoModalidad;
    }

    /**
     * @param idTipoModalidad the idTipoModalidad to set
     */
    public void setIdTipoModalidad(String idTipoModalidad) {
        this.idTipoModalidad = idTipoModalidad;
    }

}
