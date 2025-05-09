package mx.ift.sns.modelo.series;

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
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Representa un movimiento sobre Series de Numeración Geográfica. Contiene información sobre inserciones,
 * modificacicnes y eliminaciones de Series.
 */
@Entity
@Table(name = "HISTORICO_SERIE_VM")
@Cacheable(false)
@ReadOnly
public class HistoricoSerie implements Serializable {
    /** Serial. */
    private static final long serialVersionUID = 1L;

    /** Identificador del consecutivo de la solicitud. */
    @Id
    private BigDecimal id;

    /** Consecutivo. */
    @Column(name = "consecutivo")
    private BigDecimal consecutivo;

    /** Nombre del ABN. */
    @Column(name = "ABN")
    private String abn;

    /** Identificador del ABN. */
    @Column(name = "ID_ABN")
    private BigDecimal idAbn;

    /** Identificador del estado. */
    @Column(name = "ID_ESTADO")
    private String idEstado;

    /** Estado. */
    @Column(name = "ESTADO")
    private String estado;

    /** Fecha de Asignación. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_ASIGNACION")
    private Date fechaAsignacion;

    /** Fin del rango. */
    @Column(name = "FIN_RANGO")
    private String finRango;

    /** Identificador del NIR. */
    @Column(name = "ID_NIR")
    private BigDecimal idNir;

    /** Identificador del código de NIR. */
    @Column(name = "CDG_NIR")
    private BigDecimal cdgNir;

    /** Identificador del sna. */
    @Column(name = "ID_SERIE")
    private BigDecimal idSerie;

    /** Tipo de Modalidad. */
    @Column(name = "ID_TIPO_MODALIDAD")
    private String idTipoModalidad;

    /** Tipo de Red. */
    @Column(name = "ID_TIPO_RED")
    private String idTipoRed;

    /** IDA. */
    @Column(name = "IDA")
    private BigDecimal ida;

    /** IDO. */
    @Column(name = "IDO")
    private BigDecimal ido;

    /** Inegi. */
    @Column(name = "INEGI")
    private String inegi;

    /** Inicio del rango. */
    @Column(name = "INICIO_RANGO")
    private String inicioRango;

    /** Identificador del Municipio. */
    @Column(name = "ID_MUNICIPIO")
    private String idMunicipio;

    /** Municipio. */
    @Column(name = "MUNICIPIO")
    private String municipio;

    /** Número de oficio. */
    @Column(name = "NUM_OFICIO", length = 55)
    private String numOficio;

    /** Nombre de la población. */
    @Column(name = "POBLACION")
    private String poblacion;

    /** Id del Proveedor. */
    @Column(name = "ID_PST")
    private BigDecimal idPst;

    /** Nombre del Pst. */
    @Column(name = "NOMBRE_PST")
    private String nombrePst;

    /** Tipo de Solicitud. */
    @Column(name = "TIPO_SOLICITUD")
    private String tiposolicitud;

    /** Id Tipo de Solicitud. */
    @Column(name = "ID_TIPO_SOLICITUD")
    private String idTipoSolicitud;
    
    @Transient
    private String idSerieAsString;

    /**
     * Código del ABN.
     * @return String
     */
    public String getAbn() {
        return abn;
    }

    /**
     * Código del ABN.
     * @param abn String
     */
    public void setAbn(String abn) {
        this.abn = abn;
    }

    /**
     * Consecutivo.
     * @return BigDecimal
     */
    public BigDecimal getConsecutivo() {
        return consecutivo;
    }

    /**
     * Consecutivo.
     * @param consecutivo BigDecimal
     */
    public void setConsecutivo(BigDecimal consecutivo) {
        this.consecutivo = consecutivo;
    }

    /**
     * Estado.
     * @return String
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Estado.
     * @param estado String
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Fecha de Asignación.
     * @return Date
     */
    public Date getFechaAsignacion() {
        return fechaAsignacion;
    }

    /**
     * Fecha de Asignación.
     * @param fechaAsignacion Date
     */
    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    /**
     * Fin del rango.
     * @return the finRango
     */
    public String getFinRango() {
        return finRango;
    }

    /**
     * Fin del rango.
     * @param finRango the finRango to set
     */
    public void setFinRango(String finRango) {
        this.finRango = finRango;
    }

    /**
     * Identificador del NIR.
     * @return BigDecimal
     */
    public BigDecimal getIdNir() {
        return idNir;
    }

    /**
     * Identificador del NIR.
     * @param idNir BigDecimal
     */
    public void setIdNir(BigDecimal idNir) {
        this.idNir = idNir;
    }

    /**
     * Identificador del sna.
     * @return BigDecimal
     */
    public BigDecimal getIdSerie() {
        return idSerie;
    }

    /**
     * Identificador del sna.
     * @param idSerie BigDecimal
     */
    public void setIdSerie(BigDecimal idSerie) {
        this.idSerie = idSerie;
    }

    /**
     * Tipo de Modalidad.
     * @return String
     */
    public String getIdTipoModalidad() {
        return idTipoModalidad;
    }

    /**
     * Tipo de Modalidad.
     * @param idTipoModalidad String
     */
    public void setIdTipoModalidad(String idTipoModalidad) {
        this.idTipoModalidad = idTipoModalidad;
    }

    /**
     * Tipo de Red.
     * @return String
     */
    public String getIdTipoRed() {
        return idTipoRed;
    }

    /**
     * Tipo de Red.
     * @param idTipoRed String
     */
    public void setIdTipoRed(String idTipoRed) {
        this.idTipoRed = idTipoRed;
    }

    /**
     * IDA.
     * @return BigDecimal
     */
    public BigDecimal getIda() {
        return ida;
    }

    /**
     * IDA.
     * @param ida BigDecimal
     */
    public void setIda(BigDecimal ida) {
        this.ida = ida;
    }

    /**
     * IDO.
     * @return BigDecimal
     */
    public BigDecimal getIdo() {
        return ido;
    }

    /**
     * IDO.
     * @param ido BigDecimal
     */
    public void setIdo(BigDecimal ido) {
        this.ido = ido;
    }

    /**
     * Inegi.
     * @return String
     */
    public String getInegi() {
        return inegi;
    }

    /**
     * Inegi.
     * @param inegi String
     */
    public void setInegi(String inegi) {
        this.inegi = inegi;
    }

    /**
     * Inicio del rango.
     * @return String
     */
    public String getInicioRango() {
        return inicioRango;
    }

    /**
     * Inicio del rango.
     * @param inicioRango String
     */
    public void setInicioRango(String inicioRango) {
        this.inicioRango = inicioRango;
    }

    /**
     * Municipio.
     * @return String
     */
    public String getMunicipio() {
        return municipio;
    }

    /**
     * Municipio.
     * @param municipio String
     */
    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    /**
     * Número de oficio.
     * @return String
     */
    public String getNumOficio() {
        return numOficio;
    }

    /**
     * Número de oficio.
     * @param numOficio String
     */
    public void setNumOficio(String numOficio) {
        this.numOficio = numOficio;
    }

    /**
     * Nombre de la población.
     * @return String
     */
    public String getPoblacion() {
        return poblacion;
    }

    /**
     * Nombre de la población.
     * @param poblacion String
     */
    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * Tipo de Solicitud.
     * @return String
     */
    public String getTiposolicitud() {
        return tiposolicitud;
    }

    /**
     * Tipo de Solicitud.
     * @param tiposolicitud String
     */
    public void setTiposolicitud(String tiposolicitud) {
        this.tiposolicitud = tiposolicitud;
    }

    /**
     * Identificador del ABN.
     * @return BigDecimal
     */
    public BigDecimal getIdAbn() {
        return idAbn;
    }

    /**
     * Identificador del ABN.
     * @param idAbn BigDecimal
     */
    public void setIdAbn(BigDecimal idAbn) {
        this.idAbn = idAbn;
    }

    /**
     * Id del Proveedor.
     * @return BigDecimal
     */
    public BigDecimal getIdPst() {
        return idPst;
    }

    /**
     * Id del Proveedor.
     * @param idPst BigDecimal
     */
    public void setIdPst(BigDecimal idPst) {
        this.idPst = idPst;
    }

    /**
     * Nombre del Pst.
     * @return the nombrePst
     */
    public String getNombrePst() {
        return nombrePst;
    }

    /**
     * Nombre del Pst.
     * @param nombrePst the nombrePst to set
     */
    public void setNombrePst(String nombrePst) {
        this.nombrePst = nombrePst;
    }

    /**
     * Identificador del estado.
     * @return String
     */
    public String getIdEstado() {
        return idEstado;
    }

    /**
     * Identificador del estado.
     * @param idEstado String
     */
    public void setIdEstado(String idEstado) {
        this.idEstado = idEstado;
    }

    /**
     * Identificador del código de NIR.
     * @return BigDecimal
     */
    public BigDecimal getCdgNir() {
        return cdgNir;
    }

    /**
     * Identificador del código de NIR.
     * @param cdgNir BigDecimal
     */
    public void setCdgNir(BigDecimal cdgNir) {
        this.cdgNir = cdgNir;
    }

    /**
     * Identificador del Municipio.
     * @return String
     */
    public String getIdMunicipio() {
        return idMunicipio;
    }

    /**
     * Identificador del Municipio.
     * @param idMunicipio String
     */
    public void setIdMunicipio(String idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    /**
     * Id Tipo de Solicitud.
     * @return String
     */
    public String getIdTipoSolicitud() {
        return idTipoSolicitud;
    }

    /**
     * Id Tipo de Solicitud.
     * @param idTipoSolicitud String
     */
    public void setIdTipoSolicitud(String idTipoSolicitud) {
        this.idTipoSolicitud = idTipoSolicitud;
    }
    
    

    public String getIdSerieAsString() {
		return idSerieAsString;
	}

	public void setIdserieAsString(String IdSerieAsString) {
		this.idSerieAsString = IdSerieAsString;
	}

	/**
     * Obtiene el ida con 0 delante.
     * @return ida
     */
    public String getIdaAsString() {
        return this.ida != null ? String.format("%03d", this.ida.intValue()) : "";
    }

    /**
     * Obtiene el ido con 0 delante.
     * @return ido
     */
    public String getIdoAsString() {
        return this.ido != null ? String.format("%03d", this.ido.intValue()) : "";
    }

    /**
     * Método que añade al SNA tantos 0 a la izquierda dependiendo del tamaño del NIR.
     * @return String
     * @throws Exception Exception
     */
    public String getSnaAsString() throws Exception {
        if (String.valueOf(this.cdgNir).length() == 3) {
            return StringUtils.leftPad(this.idSerie.toString(), 3, '0');
        } else if (String.valueOf(this.cdgNir).length() == 2) {
            return StringUtils.leftPad(this.idSerie.toString(), 4, '0');
        } else {
            return StringUtils.leftPad(this.idSerie.toString(), 5, '0');
        }
    }

}
