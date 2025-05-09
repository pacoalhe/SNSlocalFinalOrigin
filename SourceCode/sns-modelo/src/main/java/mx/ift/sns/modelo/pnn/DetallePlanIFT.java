/**
 * 
 */
package mx.ift.sns.modelo.pnn;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import mx.ift.sns.modelo.pst.TipoModalidad;

import org.apache.commons.lang3.StringUtils;

/**
 * @author X23016PE
 */
public class DetallePlanIFT implements Serializable {

    /** Serial Id. */
    private static final long serialVersionUID = 1L;

    private String inegi;
    private String nomPoblacion;
    private String nomMunicipio;
    private String nomEstado;
    private BigDecimal codigoAbn;
    private int codigoNir;
    private BigDecimal serie;
    private String numInicio;
    private String numFinal;
    private String descTipoRed;
    private TipoModalidad tipoModalidad;
    private String descEstatusRango;
    private String nomProveedor;
    private String nomCProveedor;
    private String numOficio;
    private Date fechaAsignacion;
    private BigDecimal idoPnn;
    private BigDecimal idaPnn;
    private BigDecimal idClaveServicio;
    private String tipoPst;
    private BigDecimal abc;

    /**
     * @param inegi
     * @param nomPoblacion
     * @param nomMunicipio
     * @param nomEstado
     * @param codigoAbn
     * @param codigoNir
     * @param serie
     * @param numInicio
     * @param numFinal
     * @param descTipoRed
     * @param tipoModalidad
     * @param descEstatusRango
     * @param nomProveedor
     * @param nomCProveedor
     * @param numOficio
     * @param fechaAsignacion
     * @param idoPnn
     * @param idaPnn
     */
    public DetallePlanIFT(String inegi, String nomPoblacion, String nomMunicipio, String nomEstado,
            BigDecimal codigoAbn,
            int codigoNir, BigDecimal serie, String numInicio, String numFinal, String descTipoRed,
            TipoModalidad tipoModalidad, String descEstatusRango, String nomProveedor, String nomCProveedor,
            String numOficio,
            Date fechaAsignacion, BigDecimal idoPnn, BigDecimal idaPnn) {

        this.inegi = inegi;
        this.nomPoblacion = nomPoblacion;
        this.nomMunicipio = nomMunicipio;
        this.nomEstado = nomEstado;
        this.codigoAbn = codigoAbn;
        this.codigoNir = codigoNir;
        this.serie = serie;
        this.numInicio = numInicio;
        this.numFinal = numFinal;
        this.descTipoRed = descTipoRed;
        this.tipoModalidad = tipoModalidad;
        this.descEstatusRango = descEstatusRango;
        this.nomProveedor = nomProveedor;
        this.nomCProveedor = nomCProveedor;
        this.numOficio = numOficio;
        this.fechaAsignacion = fechaAsignacion;
        this.idoPnn = idoPnn;
        this.idaPnn = idaPnn;
    }

    /**
     * @param claveServicio
     * @param serie
     * @param numInicio
     * @param numFinal
     * @param descEstatusRango
     * @param nomProveedor
     * @param nomCProveedor
     * @param numOficio
     * @param fechaAsignacion
     * @param idaPnn
     * @param abc
     * @param tipoPst
     */
    public DetallePlanIFT(BigDecimal idClaveServicio, BigDecimal serie, String numInicio, String numFinal,
            String descEstatusRango, String nomProveedor, String nomCProveedor,
            String numOficio, Date fechaAsignacion, BigDecimal idaPnn, BigDecimal abc, String tipoPst) {

        this.idClaveServicio = idClaveServicio;
        this.serie = serie;
        this.numInicio = numInicio;
        this.numFinal = numFinal;
        this.descEstatusRango = descEstatusRango;
        this.nomProveedor = nomProveedor;
        this.nomCProveedor = nomCProveedor;
        this.numOficio = numOficio;
        this.fechaAsignacion = fechaAsignacion;
        this.idaPnn = idaPnn;
        this.abc = abc;
        this.tipoPst = tipoPst;
    }

    /**
     * @return the inegi
     */
    public String getInegi() {
        return inegi;
    }

    /**
     * @param inegi the inegi to set
     */
    public void setInegi(String inegi) {
        this.inegi = inegi;
    }

    /**
     * @return the nomPoblacion
     */
    public String getNomPoblacion() {
        return nomPoblacion;
    }

    /**
     * @param nomPoblacion the nomPoblacion to set
     */
    public void setNomPoblacion(String nomPoblacion) {
        this.nomPoblacion = nomPoblacion;
    }

    /**
     * @return the nomMunicipio
     */
    public String getNomMunicipio() {
        return nomMunicipio;
    }

    /**
     * @param nomMunicipio the nomMunicipio to set
     */
    public void setNomMunicipio(String nomMunicipio) {
        this.nomMunicipio = nomMunicipio;
    }

    /**
     * @return the nomEstado
     */
    public String getNomEstado() {
        return nomEstado;
    }

    /**
     * @param nomEstado the nomEstado to set
     */
    public void setNomEstado(String nomEstado) {
        this.nomEstado = nomEstado;
    }

    /**
     * @return the codigoAbn
     */
    public BigDecimal getCodigoAbn() {
        return codigoAbn;
    }

    /**
     * @param codigoAbn the codigoAbn to set
     */
    public void setCodigoAbn(BigDecimal codigoAbn) {
        this.codigoAbn = codigoAbn;
    }

    /**
     * @return the codigoNir
     */
    public int getCodigoNir() {
        return codigoNir;
    }

    /**
     * @param codigoNir the codigoNir to set
     */
    public void setCodigoNir(int codigoNir) {
        this.codigoNir = codigoNir;
    }

    /**
     * @return the serie
     */
    public BigDecimal getSerie() {
        return serie;
    }

    /**
     * @param serie the serie to set
     */
    public void setSerie(BigDecimal serie) {
        this.serie = serie;
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
     * @return the descTipoRed
     */
    public String getDescTipoRed() {
        return descTipoRed;
    }

    /**
     * @param descTipoRed the descTipoRed to set
     */
    public void setDescTipoRed(String descTipoRed) {
        this.descTipoRed = descTipoRed;
    }

    /**
     * @return the tipoModalidad
     */
    public TipoModalidad getTipoModalidad() {
        return tipoModalidad;
    }

    /**
     * @param tipoModalidad the tipoModalidad to set
     */
    public void setTipoModalidad(TipoModalidad tipoModalidad) {
        this.tipoModalidad = tipoModalidad;
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
     * @return the nomProveedor
     */
    public String getNomProveedor() {
        return nomProveedor;
    }

    /**
     * @param nomProveedor the nomProveedor to set
     */
    public void setNomProveedor(String nomProveedor) {
        this.nomProveedor = nomProveedor;
    }

    /**
     * @return the nomCProveedor
     */
    public String getNomCProveedor() {
        return nomCProveedor;
    }

    /**
     * @param nomCProveedor the nomCProveedor to set
     */
    public void setNomCProveedor(String nomCProveedor) {
        this.nomCProveedor = nomCProveedor;
    }

    /**
     * @return the numOficio
     */
    public String getNumOficio() {
        return numOficio;
    }

    /**
     * @param numOficio the numOficio to set
     */
    public void setNumOficio(String numOficio) {
        this.numOficio = numOficio;
    }

    /**
     * @return the fechaAsignacion
     */
    public Date getFechaAsignacion() {
        return fechaAsignacion;
    }

    /**
     * @param fechaAsignacion the fechaAsignacion to set
     */
    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    /**
     * @return the idoPnn
     */
    public BigDecimal getIdoPnn() {
        return idoPnn;
    }

    /**
     * @param idoPnn the idoPnn to set
     */
    public void setIdoPnn(BigDecimal idoPnn) {
        this.idoPnn = idoPnn;
    }

    /**
     * @return the idaPnn
     */
    public BigDecimal getIdaPnn() {
        return idaPnn;
    }

    /**
     * @param idaPnn the idaPnn to set
     */
    public void setIdaPnn(BigDecimal idaPnn) {
        this.idaPnn = idaPnn;
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
     * @return the tipoPst
     */
    public String getTipoPst() {
        return tipoPst;
    }

    /**
     * @param tipoPst the tipoPst to set
     */
    public void setTipoPst(String tipoPst) {
        this.tipoPst = tipoPst;
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
     * Obtiene el ido con 0 delante.
     * @return ido
     */
    public String getIdoPnnAsString() {
        return this.idoPnn != null ? String.format("%03d", this.idoPnn.intValue()) : "";
    }

    /**
     * Obtiene el ida con 0 delante.
     * @return ida
     */
    public String getIdaPnnAsString() {
        return this.idaPnn != null ? String.format("%03d", this.idaPnn.intValue()) : "";
    }

    /**
     * Método que añade al SNA tantos 0 a la izquierda dependiendo del tamaño del NIR.
     * @return String
     * @throws Exception Exception
     */
    public String getSnaAsString() throws Exception {
        if (String.valueOf(this.codigoNir).length() == 3) {
            return StringUtils.leftPad(this.serie.toString(), 3, '0');
        } else if (String.valueOf(this.codigoNir).length() == 2) {
            return StringUtils.leftPad(this.getSerie().toString(), 4, '0');
        } else {
            return StringUtils.leftPad(this.getSerie().toString(), 5, '0');
        }
    }

    /**
     * Obtiene el abc con 0 delante.
     * @return abc
     */
    public String getAbcAsString() {
        return this.abc != null ? String.format("%03d", this.abc.intValue()) : "";
    }

}
