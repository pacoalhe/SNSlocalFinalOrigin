package mx.ift.sns.modelo.nng;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Representa un movimiento sobre Rangos de Numeración No Geográfica. Contiene información sobre inserciones,
 * modificaciones y eliminaciones de Rangos.
 */
@Entity
@Table(name = "HIST_RANGO_SERIE_NNG")
@SequenceGenerator(name = "SEQ_ID_HIST_RGO_SER_NNG", sequenceName = "SEQ_ID_HIST_RGO_SER_NNG", allocationSize = 1)
@NamedQuery(name = "HistoricoRangoSerieNng.findAll", query = "SELECT h FROM HistoricoRangoSerieNng h")
public class HistoricoRangoSerieNng implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Acción de Inserción en el histórico. */
    public static final String ACCION_INSERCION = "A";

    /** Acción de Modificación en el histórico. */
    public static final String ACCION_MODIFICACION = "M";

    /** Acción de Eliminado en el histórico. */
    public static final String ACCION_BORRADO = "B";

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_HIST_RGO_SER_NNG")
    @Column(name = "ID_HIST_RANGO_SERIE_NNG", unique = true, nullable = false)
    private BigDecimal id;

    /** Identificador de código ABC. */
    @Column(name = "ABC", precision = 3)
    private BigDecimal abc;

    /** Identificador de código BCD. */
    @Column(name = "BCD", precision = 3)
    private BigDecimal bcd;

    /** Tipo de acción sobre la tabla RangoSerieNng. */
    @Column(name = "ACCION", nullable = false, length = 1)
    private String accion;

    /** Cliente. */
    @Column(name = "CLIENTE", length = 128)
    private String cliente;

    /** Fecha de Asignación de la Numeración. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_ASIGNACION")
    private Date fechaAsignacion;

    // /** Fecha de fin de reserva de la Numeración. */
    // @Temporal(TemporalType.DATE)
    // @Column(name = "FECHA_FIN_RESERVA")
    // private Date fechaFinReserva;

    /** Fecha de generación del registro de histórico. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_HISTORICO", nullable = false)
    private Date fechaHistorico;

    /** Identificador de la Clave de Servicio. */
    @Column(name = "ID_CLAVE_SERVICIO", nullable = false)
    private BigDecimal idClaveServicio;

    /** Identificador del Estatus del Rango. */
    @Column(name = "ID_ESTATUS_RANGO", nullable = false, length = 1)
    private String idEstatusRango;

    /** Identificador de la Numeración Solicitada que generó la numeración. */
    @Column(name = "ID_NNG_NUM_SOLI")
    private BigDecimal idNumeracionSolicitada;

    /** Identificador del RangoSerieNng asociado al movimiento del histórico. */
    @Column(name = "ID_NNG_RANGO_SERIE", nullable = false)
    private BigDecimal idRangoSerie;

    /** Identificador del Proveedor Arrendatario de la numeración. */
    @Column(name = "ID_PST_ARRENDATARIO")
    private BigDecimal idPstArrendatario;

    /** Identificador del Proveedor Asignatario de la numeración. */
    @Column(name = "ID_PST_ASIGNATARIO", nullable = false)
    private BigDecimal idPstAsignatario;

    /** Identificador del Proveedor Concesionario de la numeración. */
    @Column(name = "ID_PST_CONCESIONARIO")
    private BigDecimal idPstConcesionario;

    /** Identificador de la Solicitud de Asignación que generó la numeración. */
    @Column(name = "ID_SOL_ASIG")
    private BigDecimal idSolicitudAsignacion;

    /** Identificador de la Solicitud que modificó la numeración. */
    @Column(name = "ID_SOL_SOLICITUD", nullable = false)
    private BigDecimal idSolicitud;

    /** Final del Rango. */
    @Column(name = "NUM_FINAL", length = 4)
    private String numFinal;

    /** Inicio del Rango. */
    @Column(name = "NUM_INICIO", length = 4)
    private String numInicio;

    // /** Observaciones del movimiento sobre la numeración. */
    // @Column(length = 700)
    // private String observaciones;

    // @Column(name = "OFICIO_SOL_ASIG", length = 55)
    // private String oficioSolAsig;

    // @Column(precision = 5)
    // private BigDecimal rentado;

    /** Identificador de la Serie a la que pertenece al rango modificado. */
    @Column(nullable = false, precision = 4)
    private BigDecimal sna;

    /** Constructor, por defecto vacío. */
    public HistoricoRangoSerieNng() {
    }

    /**
     * Identificador del movimiento.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Identificador del movimiento.
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Identificador de código ABC.
     * @return BigDecimal
     */
    public BigDecimal getAbc() {
        return abc;
    }

    /**
     * Identificador de código ABC.
     * @param abc BigDecimal
     */
    public void setAbc(BigDecimal abc) {
        this.abc = abc;
    }

    /**
     * Identificador de código BCD.
     * @return BigDecimal
     */
    public BigDecimal getBcd() {
        return bcd;
    }

    /**
     * Identificador de código BCD.
     * @param bcd BigDecimal
     */
    public void setBcd(BigDecimal bcd) {
        this.bcd = bcd;
    }

    /**
     * Tipo de acción sobre la tabla RangoSerieNng.
     * @return String
     */
    public String getAccion() {
        return accion;
    }

    /**
     * Tipo de acción sobre la tabla RangoSerieNng.
     * @param accion String
     */
    public void setAccion(String accion) {
        this.accion = accion;
    }

    /**
     * Cliente.
     * @return String
     */
    public String getCliente() {
        return cliente;
    }

    /**
     * Cliente.
     * @param cliente String
     */
    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    /**
     * Fecha de Asignación de la Numeración.
     * @return Date
     */
    public Date getFechaAsignacion() {
        return fechaAsignacion;
    }

    /**
     * Fecha de Asignación de la Numeración.
     * @param fechaAsignacion Date
     */
    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    /**
     * Fecha de generación del registro de histórico.
     * @return Date
     */
    public Date getFechaHistorico() {
        return fechaHistorico;
    }

    /**
     * Fecha de generación del registro de histórico.
     * @param fechaHistorico Date
     */
    public void setFechaHistorico(Date fechaHistorico) {
        this.fechaHistorico = fechaHistorico;
    }

    /**
     * Identificador de la Clave de Servicio.
     * @return BigDecimal
     */
    public BigDecimal getIdClaveServicio() {
        return idClaveServicio;
    }

    /**
     * Identificador de la Clave de Servicio.
     * @param idClaveServicio BigDecimal
     */
    public void setIdClaveServicio(BigDecimal idClaveServicio) {
        this.idClaveServicio = idClaveServicio;
    }

    /**
     * Identificador del Estatus del Rango.
     * @return the idEstatusRango
     */
    public String getIdEstatusRango() {
        return idEstatusRango;
    }

    /**
     * Identificador del Estatus del Rango.
     * @param idEstatusRango the idEstatusRango to set
     */
    public void setIdEstatusRango(String idEstatusRango) {
        this.idEstatusRango = idEstatusRango;
    }

    /**
     * Identificador de la Numeración Solicitada que generó la numeración.
     * @return BigDecimal
     */
    public BigDecimal getIdNumeracionSolicitada() {
        return idNumeracionSolicitada;
    }

    /**
     * Identificador de la Numeración Solicitada que generó la numeración.
     * @param idNumeracionSolicitada BigDecimal
     */
    public void setIdNumeracionSolicitada(BigDecimal idNumeracionSolicitada) {
        this.idNumeracionSolicitada = idNumeracionSolicitada;
    }

    /**
     * Identificador del RangoSerieNng asociado al movimiento del histórico.
     * @return BigDecimal
     */
    public BigDecimal getIdRangoSerie() {
        return idRangoSerie;
    }

    /**
     * Identificador del RangoSerieNng asociado al movimiento del histórico.
     * @param idRangoSerie BigDecimal
     */
    public void setIdRangoSerie(BigDecimal idRangoSerie) {
        this.idRangoSerie = idRangoSerie;
    }

    /**
     * Identificador del Proveedor Arrendatario de la numeración.
     * @return BigDecimal
     */
    public BigDecimal getIdPstArrendatario() {
        return idPstArrendatario;
    }

    /**
     * Identificador del Proveedor Arrendatario de la numeración.
     * @param idPstArrendatario BigDecimal
     */
    public void setIdPstArrendatario(BigDecimal idPstArrendatario) {
        this.idPstArrendatario = idPstArrendatario;
    }

    /**
     * Identificador del Proveedor Asignatario de la numeración.
     * @return BigDecimal
     */
    public BigDecimal getIdPstAsignatario() {
        return idPstAsignatario;
    }

    /**
     * Identificador del Proveedor Asignatario de la numeración.
     * @param idPstAsignatario BigDecimal
     */
    public void setIdPstAsignatario(BigDecimal idPstAsignatario) {
        this.idPstAsignatario = idPstAsignatario;
    }

    /**
     * Identificador del Proveedor Concesionario de la numeración.
     * @return BigDecimal
     */
    public BigDecimal getIdPstConcesionario() {
        return idPstConcesionario;
    }

    /**
     * Identificador del Proveedor Concesionario de la numeración.
     * @param idPstConcesionario BigDecimal
     */
    public void setIdPstConcesionario(BigDecimal idPstConcesionario) {
        this.idPstConcesionario = idPstConcesionario;
    }

    /**
     * Identificador de la Solicitud de Asignación que generó la numeración.
     * @return BigDecimal
     */
    public BigDecimal getIdSolicitudAsignacion() {
        return idSolicitudAsignacion;
    }

    /**
     * Identificador de la Solicitud de Asignación que generó la numeración.
     * @param idSolicitudAsignacion BigDecimal
     */
    public void setIdSolicitudAsignacion(BigDecimal idSolicitudAsignacion) {
        this.idSolicitudAsignacion = idSolicitudAsignacion;
    }

    /**
     * Identificador de la Solicitud que modificó la numeración.
     * @return BigDecimal
     */
    public BigDecimal getIdSolicitud() {
        return idSolicitud;
    }

    /**
     * Identificador de la Solicitud que modificó la numeración.
     * @param idSolicitud BigDecimal
     */
    public void setIdSolicitud(BigDecimal idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    /**
     * Final del Rango.
     * @return String
     */
    public String getNumFinal() {
        return numFinal;
    }

    /**
     * Final del Rango.
     * @param numFinal String
     */
    public void setNumFinal(String numFinal) {
        this.numFinal = numFinal;
    }

    /**
     * Inicio del Rango.
     * @return String
     */
    public String getNumInicio() {
        return numInicio;
    }

    /**
     * Inicio del Rango.
     * @param numInicio String
     */
    public void setNumInicio(String numInicio) {
        this.numInicio = numInicio;
    }

    /**
     * Identificador de la Serie a la que pertenece al rango modificado.
     * @return BigDecimal
     */
    public BigDecimal getSna() {
        return sna;
    }

    /**
     * Identificador de la Serie a la que pertenece al rango modificado.
     * @param sna BigDecimal
     */
    public void setSna(BigDecimal sna) {
        this.sna = sna;
    }

    @Override
    public String toString() {
        StringBuilder sbTraza = new StringBuilder();
        sbTraza.append("HistoricoRangoSerieNng = {");
        sbTraza.append("id: ").append(this.id);
        sbTraza.append(", idSolicitud ").append(this.idSolicitud);
        sbTraza.append(", idClaveServicio: ").append(this.idClaveServicio);
        sbTraza.append(", idSna:  ").append(this.sna);
        sbTraza.append(", nInicio: ").append(this.numInicio);
        sbTraza.append(", nFin: ").append(this.numFinal);
        sbTraza.append(", Accion: ").append(this.accion);
        sbTraza.append(", idAsignatario: ").append(this.idPstAsignatario);
        return sbTraza.toString();
    }
}
