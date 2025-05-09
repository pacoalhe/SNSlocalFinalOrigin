package mx.ift.sns.modelo.nng;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Representa la vista del detalle de planes de rangos asignados.
 */
@Entity
@Table(name = "DETALLE_RANGO_ASIGNADO_NNG_VM")
@ReadOnly
@Cacheable(false)
public class DetalleRangoAsignadoNng implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Pk de la vista. */
    @Id
    @Column(name = "ID_DET_RANG_ASIG_NNG")
    private String id;

    /** CLAVE SERVICIO. */
    @Column(name = "ID_CLAVE_SERVICIO")
    private BigDecimal idClaveServicio;

    /** Id de la serie. */
    @Column(name = "SNA")
    private BigDecimal idSerie;

    /** NUM_INICIO. */
    @Column(name = "NUM_INICIO")
    private String numInicio;

    /** NUM_FINAL. */
    @Column(name = "NUM_FINAL")
    private String numFinal;

    /** Codigo del estado. */
    @Column(name = "COD_ESTATUS_RANGO")
    private String codEstatusRango;

    /** Descripcion del estado. */
    @Column(name = "DESC_ESTATUS_RANGO")
    private String descEstatusRango;

    /** Nombre del Pst. */
    @Column(name = "NOM_PST")
    private String nombrePst;

    /** Nombre Corto del Pst. */
    @Column(name = "NOMC_PST")
    private String nombreCortoPst;

    /** OFICIO DE LA SOLICITUD DE ASIGNACION. */
    @Column(name = "OFICIO_SOL_ASIG")
    private String oficioSolAsig;

    /** Fecha de la solicitud de asignacion. */
    @Column(name = "FECHA_ASIGNACION")
    @Temporal(TemporalType.DATE)
    private Date fechaSolAsig;

    /** Ida. */
    @Column(name = "IDA")
    private BigDecimal ida;

    /** Abc. */
    @Column(name = "ABC")
    private BigDecimal abc;

    /** Bcd. */
    @Column(name = "BCD")
    private BigDecimal bcd;

    /** DESCRIPCION TIPO PST. */
    @Column(name = "COD_TIPO_PST")
    private String codTipoPst;

    /** CLIENTE. */
    @Column(name = "CLIENTE")
    private String cliente;

    /** Codigo del Pst. */
    @Column(name = "CDG_PST")
    private BigDecimal codigoPst;

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
     * @return the idClaveServicio
     */
    public BigDecimal getIdClaveServicio() {
        return idClaveServicio;
    }

    /**
     * @param idClaveServicio the idClaveServicio to set
     */
    public void setIdClaveServicio(BigDecimal idClaveServicio) {
        this.idClaveServicio = idClaveServicio;
    }

    /**
     * @return the idSerie
     */
    public BigDecimal getIdSerie() {
        return idSerie;
    }

    /**
     * @param idSerie the idSerie to set
     */
    public void setIdSerie(BigDecimal idSerie) {
        this.idSerie = idSerie;
    }

    /**
     * @return the numInicio
     */
    public String getNumInicio() {
        return numInicio;
    }

    /**
     * @param numInicio the numInicio to set
     */
    public void setNumInicio(String numInicio) {
        this.numInicio = numInicio;
    }

    /**
     * @return the codEstatusRango
     */
    public String getCodEstatusRango() {
        return codEstatusRango;
    }

    /**
     * @param codEstatusRango the codEstatusRango to set
     */
    public void setCodEstatusRango(String codEstatusRango) {
        this.codEstatusRango = codEstatusRango;
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
     * @return the descEstatusRango
     */
    public String getDescEstatusRango() {
        return descEstatusRango;
    }

    /**
     * @param descEstatusRango the descEstatusRango to set
     */
    public void setDescEstatusRango(String descEstatusRango) {
        this.descEstatusRango = descEstatusRango;
    }

    /**
     * @return the nombrePst
     */
    public String getNombrePst() {
        return nombrePst;
    }

    /**
     * @param nombrePst the nombrePst to set
     */
    public void setNombrePst(String nombrePst) {
        this.nombrePst = nombrePst;
    }

    /**
     * @return the nombreCortoPst
     */
    public String getNombreCortoPst() {
        return nombreCortoPst;
    }

    /**
     * @param nombreCortoPst the nombreCortoPst to set
     */
    public void setNombreCortoPst(String nombreCortoPst) {
        this.nombreCortoPst = nombreCortoPst;
    }

    /**
     * @return the oficioSolAsig
     */
    public String getOficioSolAsig() {
        return oficioSolAsig;
    }

    /**
     * @param oficioSolAsig the oficioSolAsig to set
     */
    public void setOficioSolAsig(String oficioSolAsig) {
        this.oficioSolAsig = oficioSolAsig;
    }

    /**
     * @return the fechaSolAsig
     */
    public Date getFechaSolAsig() {
        return fechaSolAsig;
    }

    /**
     * @param fechaSolAsig the fechaSolAsig to set
     */
    public void setFechaSolAsig(Date fechaSolAsig) {
        this.fechaSolAsig = fechaSolAsig;
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
     * @return the abc
     */
    public BigDecimal getAbc() {
        return abc;
    }

    /**
     * @param abc the abc to set
     */
    public void setAbc(BigDecimal abc) {
        this.abc = abc;
    }

    /**
     * @return the codTipoPst
     */
    public String getCodTipoPst() {
        return codTipoPst;
    }

    /**
     * @param codTipoPst the codTipoPst to set
     */
    public void setCodTipoPst(String codTipoPst) {
        this.codTipoPst = codTipoPst;
    }

    /**
     * @return the bcd
     */
    public BigDecimal getBcd() {
        return bcd;
    }

    /**
     * @param bcd the bcd to set
     */
    public void setBcd(BigDecimal bcd) {
        this.bcd = bcd;
    }

    /**
     * @return the cliente
     */
    public String getCliente() {
        return cliente;
    }

    /**
     * @param cliente the cliente to set
     */
    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    /**
     * @return the codigoPst
     */
    public BigDecimal getCodigoPst() {
        return codigoPst;
    }

    /**
     * @param codigoPst the codigoPst to set
     */
    public void setCodigoPst(BigDecimal codigoPst) {
        this.codigoPst = codigoPst;
    }

    /**
     * Obtiene el ida con 0 delante.
     * @return ida
     */
    public String getIdaAsString() {
        return this.ida != null ? String.format("%03d", this.ida.intValue()) : "";
    }

    /**
     * Método que añade al SNA tantos 0 a la izquierda dependiendo del tamaño del NIR.
     * @return String
     * @throws Exception Exception
     */
    public String getSnaAsString() throws Exception {
        return StringUtils.leftPad(this.idSerie.toString(), 3, '0');
    }

    /**
     * Obtiene el abc con 0 delante.
     * @return abc
     */
    public String getAbcAsString() {
        return this.abc != null ? String.format("%03d", this.abc.intValue()) : "";
    }

    /**
     * Obtiene el bcd con 0 delante.
     * @return bcd
     */
    public String getBcdAsString() {
        return this.bcd != null ? String.format("%03d", this.bcd.intValue()) : "";
    }
}
