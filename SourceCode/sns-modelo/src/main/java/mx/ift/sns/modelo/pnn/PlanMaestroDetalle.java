package mx.ift.sns.modelo.pnn;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Tipo de plan de numeracion.
 */
@Entity
@Table(name = "PNN_DETALLE")
public class PlanMaestroDetalle implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Id. */
    @EmbeddedId
    private PlanMaestroDetallePK id;

    @Column(name = "TIPO_SERVICIO")
    private char tipoServicio;

    @Column(name = "MPP")
    private char mpp;

    @Column(name = "AREA_SERVICIO")
    private Integer areaServicio;

    @Column(name = "IDO")
    private Integer ido;

    @Column(name = "IDA")
    private Integer ida;

    @Column(name = "ZONA")
    private Integer zona;

    @Column(name = "NUMERO_INICIAL")
    private BigDecimal numeroInicial;

    @Column(name = "NUMERO_FINAL")
    private BigDecimal numeroFinal;

    /**
     * @return the id
     */
    public PlanMaestroDetallePK getId() {
	return id;
    }

    /**
     * @param ido the id to set
     */
    public void setId(PlanMaestroDetallePK id) {
	this.id = id;
    }

    /**
     * @return the tipoServicio
     */
    public char getTipoServicio() {
	return tipoServicio;
    }

    /**
     * @param tipoServicio the tipoServicio to set
     */
    public void setTipoServicio(char tipoServicio) {
	this.tipoServicio = tipoServicio;
    }

    /**
     * @return the mpp
     */
    public char getMpp() {
	return mpp;
    }

    /**
     * @param mpp the mpp to set
     */
    public void setMpp(char mpp) {
	this.mpp = mpp;
    }

    /**
     * @return the areaServicio
     */
    public Integer getAreaServicio() {
	return areaServicio;
    }

    /**
     * @return the ido
     */
    public Integer getIdo() {
	return ido;
    }

    /**
     * @param ido the ido to set
     */
    public void setIdo(Integer ido) {
	this.ido = ido;
    }

    /**
     * @return the ida
     */
    public Integer getIda() {
	return ida;
    }

    /**
     * @param ida the ida to set
     */
    public void setIda(Integer ida) {
	this.ida = ida;
    }

    /**
     * @param areaServicio the areaServicio to set
     */
    public void setAreaServicio(Integer areaServicio) {
	this.areaServicio = areaServicio;
    }

    /**
     * @return zona the zona to set
     */
    public Integer getZona() {return zona; }

    /**
     * @param zona the zona to set
     */
    public void setZona(Integer zona) { this.zona = zona; }

    public Long getNumeroInicial() {
        return numeroInicial != null ? numeroInicial.longValue() : null;
    }

    public void setNumeroInicial(BigDecimal numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public Long getNumeroFinal() {
        return numeroFinal != null ? numeroFinal.longValue() : null;
    }

    public void setNumeroFinal(BigDecimal numeroFinal) {
        this.numeroFinal = numeroFinal;
    }


    @Override
    public String toString() {
	String message = "Ido: " + getIdo() + ", NoInicial:" + getId().getNumeroInicial() + ", NoFinal: "
		+ getId().getNumeroFinal() + ", TipoServicio: " + getTipoServicio() + ", Mpp: " + getMpp() + ", Ida: "
		+ getIda() + ", AreaServicio: " + getAreaServicio() + ", Zona: " + getZona();
	return message;
    }
}

