package mx.ift.sns.modelo.ng;

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
@Table(name = "DETALLE_RANGO_ASIGNADO_NG_VM")
@ReadOnly
@Cacheable(false)
public class DetalleRangoAsignadoNg implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Pk de la vista. */
    @Id
    @Column(name = "ID_DET_RANG_ASIG_NG")
    private String id;

    /** N_INICIO. */
    @Column(name = "N_INICIO")
    private String numInicio;

    /** N_FINAL. */
    @Column(name = "N_FINAL")
    private String numFinal;

    /** ID_TIPO_MODALIDAD. */
    @Column(name = "ID_TIPO_MODALIDAD")
    private String idTipoModalidad;

    /** OFICIO DE LA SOLICITUD DE ASIGNACION. */
    @Column(name = "OFICIO_SOL_ASIG")
    private String oficioSolAsig;

    /** Fecha de la solicitud de asignacion. */
    @Column(name = "FECHA_SOL_ASIG")
    @Temporal(TemporalType.DATE)
    private Date fechaSolAsig;

    /** Ido. */
    @Column(name = "IDO_PNN")
    private BigDecimal idoPnn;

    /** Ida. */
    @Column(name = "IDA_PNN")
    private BigDecimal idaPnn;

    /** Abc. */
    @Column(name = "ABC")
    private BigDecimal abc;

    /** Inegi. */
    @Column(name = "ID_INEGI")
    private String inegi;

    /** Nombre de la población. */
    @Column(name = "NOM_POBLACION")
    private String nombrePoblacion;

    /** Descripcion de la region. */
    @Column(name = "DESC_REGION")
    private String descRegion;

    /** Fecha de la migracion. */
    @Column(name = "FECHA_MIGRACION")
    @Temporal(TemporalType.DATE)
    private Date fechaMigracion;

    /** Nir anterior. */
    @Column(name = "ID_NIR_ANTERIOR")
    private BigDecimal idNirAnterior;

    /** Nombre del municipio. */
    @Column(name = "NOM_MUNICIPIO")
    private String nombreMunicipio;

    /** Nombre del estado. */
    @Column(name = "NOM_ESTADO")
    private String nombreEstado;

    /** Id del abn. */
    @Column(name = "ID_ABN")
    private BigDecimal idAbn;

    /** Presuscripcion. */
    @Column(name = "PRESUSCRIPCION")
    private String presuscripcion;

    /** Fecha de consolidacion. */
    @Column(name = "FECHA_CONSOLIDACION")
    @Temporal(TemporalType.DATE)
    private Date fechaConsolidacion;

    /** Codigo del nir. */
    @Column(name = "CDG_NIR")
    private Integer codigoNir;

    /** Id de la serie. */
    @Column(name = "ID_SERIE")
    private BigDecimal idSerie;

    /** Nombre del Pst. */
    @Column(name = "NOM_PST")
    private String nombrePst;

    /** Nombre Corto del Pst. */
    @Column(name = "NOMC_PST")
    private String nombreCortoPst;

    /** Codigo del Pst. */
    @Column(name = "CDG_PST")
    private BigDecimal codigoPst;

    /** Codigo del tipo de red. */
    @Column(name = "COD_TIPO_RED")
    private String codTipoRed;

    /** Descripcion del tipo de red. */
    @Column(name = "DESC_TIPO_RED")
    private String descTipoRed;

    /** Nombre de la central de origen. */
    @Column(name = "NOM_CEN_ORIGEN")
    private String nombreCentralOrigen;

    /** Nombre de la central destino. */
    @Column(name = "NOM_CEN_DESTINO")
    private String nombreCentralDestino;

    /** Numero de oficio solicitante. */
    @Column(name = "NUM_OFICIO_SOLICITANTE")
    private String oficioSolicitante;

    /** Fecha de la solicitud. */
    @Column(name = "FECHA_SOLICITUD")
    @Temporal(TemporalType.DATE)
    private Date fechaSolicitud;

    /** Codigo del estado. */
    @Column(name = "COD_ESTATUS_RANGO")
    private String codEstatusRango;

    /** Descripcion del estado. */
    @Column(name = "DESC_ESTATUS_RANGO")
    private String descEstatusRango;

    /** Numero de oficio de la reserva. */
    @Column(name = "OFICIO_RESERVA")
    private String oficioReserva;

    /** Fecha de la reserva. */
    @Column(name = "FECHA_RESERVA")
    @Temporal(TemporalType.DATE)
    private Date fechaReserva;
    
    /** Id Operador del Pst. */
    @Column(name = "ID_OPERADOR")
    private BigDecimal idOperador;

    /** Constructor por defecto. */
    public DetalleRangoAsignadoNg() {
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
     * @return the descRegion
     */
    public String getDescRegion() {
        return descRegion;
    }

    /**
     * @param descRegion the descRegion to set
     */
    public void setDescRegion(String descRegion) {
        this.descRegion = descRegion;
    }

    /**
     * @return the fechaMigracion
     */
    public Date getFechaMigracion() {
        return fechaMigracion;
    }

    /**
     * @param fechaMigracion the fechaMigracion to set
     */
    public void setFechaMigracion(Date fechaMigracion) {
        this.fechaMigracion = fechaMigracion;
    }

    /**
     * @return the idNirAnterior
     */
    public BigDecimal getIdNirAnterior() {
        return idNirAnterior;
    }

    /**
     * @param idNirAnterior the idNirAnterior to set
     */
    public void setIdNirAnterior(BigDecimal idNirAnterior) {
        this.idNirAnterior = idNirAnterior;
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
     * @return the fechaConsolidacion
     */
    public Date getFechaConsolidacion() {
        return fechaConsolidacion;
    }

    /**
     * @param fechaConsolidacion the fechaConsolidacion to set
     */
    public void setFechaConsolidacion(Date fechaConsolidacion) {
        this.fechaConsolidacion = fechaConsolidacion;
    }

    /**
     * @return the codigoNir
     */
    public Integer getCodigoNir() {
        return codigoNir;
    }

    /**
     * @param codigoNir the codigoNir to set
     */
    public void setCodigoNir(Integer codigoNir) {
        this.codigoNir = codigoNir;
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
     * @return the nombreCentralOrigen
     */
    public String getNombreCentralOrigen() {
        return nombreCentralOrigen;
    }

    /**
     * @param nombreCentralOrigen the nombreCentralOrigen to set
     */
    public void setNombreCentralOrigen(String nombreCentralOrigen) {
        this.nombreCentralOrigen = nombreCentralOrigen;
    }

    /**
     * @return the nombreCentralDestino
     */
    public String getNombreCentralDestino() {
        return nombreCentralDestino;
    }

    /**
     * @param nombreCentralDestino the nombreCentralDestino to set
     */
    public void setNombreCentralDestino(String nombreCentralDestino) {
        this.nombreCentralDestino = nombreCentralDestino;
    }

    /**
     * @return the oficioSolicitante
     */
    public String getOficioSolicitante() {
        return oficioSolicitante;
    }

    /**
     * @param oficioSolicitante the oficioSolicitante to set
     */
    public void setOficioSolicitante(String oficioSolicitante) {
        this.oficioSolicitante = oficioSolicitante;
    }

    /**
     * @return the fechaSolicitud
     */
    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    /**
     * @param fechaSolicitud the fechaSolicitud to set
     */
    public void setFechaSolicitud(Date fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
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
     * @return the codTipoRed
     */
    public String getCodTipoRed() {
        return codTipoRed;
    }

    /**
     * @param codTipoRed the codTipoRed to set
     */
    public void setCodTipoRed(String codTipoRed) {
        this.codTipoRed = codTipoRed;
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
     * @return the oficioReserva
     */
    public String getOficioReserva() {
        return oficioReserva;
    }

    /**
     * @param oficioReserva the oficioReserva to set
     */
    public void setOficioReserva(String oficioReserva) {
        this.oficioReserva = oficioReserva;
    }

    /**
     * @return the fechaReserva
     */
    public Date getFechaReserva() {
        return fechaReserva;
    }
    
    public BigDecimal getIdOperador() {
		return idOperador;
	}

	public void setIdOperador(BigDecimal idOperador) {
		this.idOperador = idOperador;
	}

	/**
     * @param fechaReserva the fechaReserva to set
     */
    public void setFechaReserva(Date fechaReserva) {
        this.fechaReserva = fechaReserva;
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
            return StringUtils.leftPad(this.idSerie.toString(), 3, '0');
        } else if (String.valueOf(this.codigoNir).length() == 2) {
            return StringUtils.leftPad(this.idSerie.toString(), 4, '0');
        } else {
            return StringUtils.leftPad(this.idSerie.toString(), 5, '0');
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
