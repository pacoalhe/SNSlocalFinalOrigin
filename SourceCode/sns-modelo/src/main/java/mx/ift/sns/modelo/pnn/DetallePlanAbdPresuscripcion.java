/**
 * 
 */
package mx.ift.sns.modelo.pnn;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

/**
 * @author X23016PE
 */
public class DetallePlanAbdPresuscripcion implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private int codigoNir;
    private BigDecimal serie;
    private String numInicio;
    private String numFinal;
    private String inegi;
    private String nomPoblacion;
    private String nomEstado;
    private BigDecimal idoPnn;
    private String nomProveedor;
    private Date fechaAsignacion;
    private BigDecimal codigoAbn;
    private String descTipoRed;
    private BigDecimal idaPnn;

    /**
     * @param codigoNir
     * @param serie
     * @param numInicio
     * @param numFinal
     * @param inegi
     * @param nomPoblacion
     * @param nomEstado
     * @param idoPnn
     * @param nomProveedor
     * @param fechaAsignacion
     * @param codigoAbn
     * @param descTipoRed
     * @param idaPnn
     */
    public DetallePlanAbdPresuscripcion(int codigoNir, BigDecimal serie, String numInicio, String numFinal,
            String inegi,
            String nomPoblacion, String nomEstado, BigDecimal idoPnn, String nomProveedor, Date fechaAsignacion,
            BigDecimal codigoAbn, String descTipoRed, BigDecimal idaPnn) {
        this.codigoNir = codigoNir;
        this.serie = serie;
        this.numInicio = numInicio;
        this.numFinal = numFinal;
        this.inegi = inegi;
        this.nomPoblacion = nomPoblacion;
        this.nomEstado = nomEstado;
        this.idoPnn = idoPnn;
        this.nomProveedor = nomProveedor;
        this.fechaAsignacion = fechaAsignacion;
        this.codigoAbn = codigoAbn;
        this.descTipoRed = descTipoRed;
        this.idaPnn = idaPnn;
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

}
