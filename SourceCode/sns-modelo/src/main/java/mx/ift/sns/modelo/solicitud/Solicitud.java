package mx.ift.sns.modelo.solicitud;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.nng.RangoSerieNng;
import mx.ift.sns.modelo.oficios.Oficio;
import mx.ift.sns.modelo.pst.Contacto;
import mx.ift.sns.modelo.pst.Proveedor;

import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * The persistent class for the SOL_SOLICITUD database table.
 */
@Entity
@Table(name = "SOL_SOLICITUD")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "ID_TIPO_SOLICITUD")
@SequenceGenerator(name = "SEQ_ID_SOLICITUDES", sequenceName = "SEQ_ID_SOLICITUDES", allocationSize = 1)
@NamedQuery(name = "Solicitud.findAll", query = "SELECT s FROM Solicitud s")
public class Solicitud implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_SOLICITUDES")
    @Column(name = "ID_SOL_SOLICITUD", unique = true, nullable = false)
    private BigDecimal id;

    /** Comentarios. */
    @Column(length = 700)
    private String comentarios;

    /** Fecha de Inicio de Pruebas. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_INI_PRUEBAS")
    private Date fechaIniPruebas;

    /** Fecha de Inicio de Utilización. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_INI_UTILIZACION")
    private Date fechaIniUtilizacion;

    /** Fecha de Solicitud. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_SOLICITUD", nullable = false)
    private Date fechaSolicitud;

    /** Referencia de Solicitud. */
    @Column(name = "REF_SOLICITUD", nullable = false, length = 60)
    private String referencia;

    /** Fecha de asignacion solicitud. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_ASIGNACION")
    private Date fechaAsignacion;

    /** Relación: Una solicitud puede contener muchos oficios. */
    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.MERGE)
    @PrivateOwned
    private List<Oficio> oficios = new ArrayList<Oficio>();

    /** Relación: Una solicitud puede contener muchos rangos. */
    // bi-directional many-to-one association to RangoSerie
    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.MERGE)
    private List<RangoSerie> rangos = new ArrayList<RangoSerie>();

    /** Relación: Una solicitud puede contener muchos rangos nng. */
    // bi-directional many-to-one association to RangoSerie
    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.MERGE)
    private List<RangoSerieNng> rangosNng = new ArrayList<RangoSerieNng>();

    /** Relación: Un proveedor puede ser solicitante de muchas solicitudes. */
    // bi-directional many-to-one association to Proveedor
    @ManyToOne
    @JoinColumn(name = "ID_PST_SOLICITANTE", nullable = false)
    private Proveedor proveedorSolicitante;

    /** Relación: Muchas solicitudes pueden tener el mismo represnetante legal. */
    @ManyToOne
    @JoinColumn(name = "ID_REPRESENTANTE_LEGAL", nullable = true)
    private Contacto representanteLegal;

    /** Relación: Muchas solicitudes pueden tener el mismo represnetante suplente. */
    @ManyToOne
    @JoinColumn(name = "ID_REPRESENTANTE_SUPLENTE", nullable = true)
    private Contacto representanteSuplente;

    /** Relación: Muchas solicitudes pueden tener el mismo estado. */
    // bi-directional many-to-one association to EstadoSolicitud
    @ManyToOne
    @JoinColumn(name = "ID_ESTADO_SOLICITUD", nullable = false)
    private EstadoSolicitud estadoSolicitud;

    /** Relación: Muchas solicitudes pueden tener el mismo tipo de solicitud. */
    // bi-directional many-to-one association to TipoSolicitud
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_TIPO_SOLICITUD", nullable = false)
    private TipoSolicitud tipoSolicitud;

    /** Número de oficio generado para el Proveedor Solicitante. */
    @Column(name = "NUM_OFICIO_SOLICITANTE", nullable = true, length = 55)
    private String numOficioSolicitante;

    /** Constructor, por defecto vacío. */
    public Solicitud() {
    }

    /**
     * Lista de rangos de numeración no geográfica asociados a la Solicitud.
     * @return List<RangoSerieNng>
     */
    public List<RangoSerieNng> getRangosNng() {
        return rangosNng;
    }

    /**
     * Lista de rangos de numeración no geográfica asociados a la Solicitud.
     * @param rangosNng List<RangoSerieNng>
     */
    public void setRangosNng(List<RangoSerieNng> rangosNng) {
        this.rangosNng = rangosNng;
    }

    /**
     * Lista de rangos de numeración geográfica asociados a la Solicitud.
     * @return List<RangoSerie>
     */
    public List<RangoSerie> getRangos() {
        return rangos;
    }

    /**
     * Lista de rangos de numeración geográfica asociados a la Solicitud.
     * @param rangos List<RangoSerie>
     */
    public void setRangos(List<RangoSerie> rangos) {
        this.rangos = rangos;
    }

    /**
     * Identificador de Solicitud.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return this.id;
    }

    /**
     * Identificador de Solicitud.
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Comentarios de la solicitud.
     * @return String
     */
    public String getComentarios() {
        return this.comentarios;
    }

    /**
     * Comentarios de la solicitud.
     * @param comentarios String
     */
    public void setComentarios(String comentarios) {
        // Se eliminan los retornos de carro
        this.comentarios = comentarios.replace("\r", "");
    }

    /**
     * Fecha de inicio de pruebas.
     * @return Date
     */
    public Date getFechaIniPruebas() {
        return this.fechaIniPruebas;
    }

    /**
     * Fecha de inicio de pruebas.
     * @param fechaIniPruebas Date
     */
    public void setFechaIniPruebas(Date fechaIniPruebas) {
        this.fechaIniPruebas = fechaIniPruebas;
    }

    /**
     * Fecha de inicio de utilización.
     * @return Date
     */
    public Date getFechaIniUtilizacion() {
        return this.fechaIniUtilizacion;
    }

    /**
     * Fecha de inicio de utilización.
     * @param fechaIniUtilizacion Date
     */
    public void setFechaIniUtilizacion(Date fechaIniUtilizacion) {
        this.fechaIniUtilizacion = fechaIniUtilizacion;
    }

    /**
     * Fecha de creación de la solicitud.
     * @return Date
     */
    public Date getFechaSolicitud() {
        return this.fechaSolicitud;
    }

    /**
     * Fecha de creación de la solicitud.
     * @param fechaSolicitud Date
     */
    public void setFechaSolicitud(Date fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    /**
     * Referencia de Solicitud.
     * @return String
     */
    public String getReferencia() {
        return referencia;
    }

    /**
     * Referencia de Solicitud.
     * @param referencia String
     */
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    /**
     * Lista de oficios asociada a la solicitud.
     * @return List
     */
    public List<Oficio> getOficios() {
        return oficios;
    }

    /**
     * Lista de oficios asociada a la solicitud.
     * @param oficios List
     */
    public void setOficios(List<Oficio> oficios) {
        this.oficios = oficios;
    }

    /**
     * Añade un nuevo rango a la solicitud.
     * @param rango rango
     * @return Oficio
     */
    public RangoSerie addRango(RangoSerie rango) {
        this.getRangos().add(rango);
        rango.setSolicitud(this);
        return rango;
    }

    /**
     * Elimina la referencia del rango a la solicitud.
     * @param rango RangoSerie
     * @return RangoSerie
     */
    public RangoSerie removeRango(RangoSerie rango) {
        this.getRangos().remove(rango);
        rango.setSolicitud(null);
        return rango;
    }

    /**
     * Añade un nuevo rango Nng a la solicitud.
     * @param rango rango
     * @return rango
     */
    public RangoSerieNng addRangoNng(RangoSerieNng rango) {
        this.getRangosNng().add(rango);
        rango.setSolicitud(this);
        return rango;
    }

    /**
     * Elimina la referencia del rango Nng a la solicitud.
     * @param rango RangoSerie
     * @return RangoSerie
     */
    public RangoSerieNng removeRango(RangoSerieNng rango) {
        this.getRangosNng().remove(rango);
        rango.setSolicitud(null);
        return rango;
    }

    /**
     * Añade un nuevo oficio a la solicitud.
     * @param pOficio Oficio
     * @return Oficio
     */
    public Oficio addOficio(Oficio pOficio) {
        this.getOficios().add(pOficio);
        pOficio.setSolicitud(this);
        return pOficio;
    }

    /**
     * Elimina la referencia del oficio a la solicitud.
     * @param pOficio Oficio
     * @return Oficio
     */
    public Oficio removeOficio(Oficio pOficio) {
        this.getOficios().remove(pOficio);
        pOficio.setSolicitud(null);
        return pOficio;
    }

    /**
     * Proveedor Solicitante.
     * @return Proveedor
     */
    public Proveedor getProveedorSolicitante() {
        return proveedorSolicitante;
    }

    /**
     * Proveedor Solicitante.
     * @param proveedorSolicitante Proveedor
     */
    public void setProveedorSolicitante(Proveedor proveedorSolicitante) {
        this.proveedorSolicitante = proveedorSolicitante;
    }

    /**
     * Estado de la solicitud.
     * @return EstadoSolicitud
     */
    public EstadoSolicitud getEstadoSolicitud() {
        return estadoSolicitud;
    }

    /**
     * Estado de la solicitud.
     * @param estadoSolicitud EstadoSolicitud
     */
    public void setEstadoSolicitud(EstadoSolicitud estadoSolicitud) {
        this.estadoSolicitud = estadoSolicitud;
    }

    /**
     * Tipo de la solicitud.
     * @return TipoSolicitud
     */
    public TipoSolicitud getTipoSolicitud() {
        return tipoSolicitud;
    }

    /**
     * Tipo de la solicitud.
     * @param tipoSolicitud TipoSolicitud
     */
    public void setTipoSolicitud(TipoSolicitud tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    /**
     * Fecha de asignacion solicitud.
     * @return Date
     */
    public Date getFechaAsignacion() {
        return fechaAsignacion;
    }

    /**
     * Fecha de asignacion solicitud.
     * @param fechaAsignacion Date
     */
    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    /**
     * Representante legal del Proveedor solicitante asociado a la solcitud.
     * @return Contacto contacto
     */
    public Contacto getRepresentanteLegal() {
        return representanteLegal;
    }

    /**
     * Representante legal del Proveedor solicitante asociado a la solcitud.
     * @param representanteLegal Contacto
     */
    public void setRepresentanteLegal(Contacto representanteLegal) {
        this.representanteLegal = representanteLegal;
    }

    /**
     * Representante suplente del Proveedor solicitante asociado a la solcitud.
     * @return Contacto
     */
    public Contacto getRepresentanteSuplente() {
        return representanteSuplente;
    }

    /**
     * Representante suplente del Proveedor solicitante asociado a la solcitud.
     * @param representanteSuplente Contacto
     */
    public void setRepresentanteSuplente(Contacto representanteSuplente) {
        this.representanteSuplente = representanteSuplente;
    }

    /**
     * Número de oficio generado para el Proveedor Solicitante.
     * @return String
     */
    public String getNumOficioSolicitante() {
        return numOficioSolicitante;
    }

    /**
     * Número de oficio generado para el Proveedor Solicitante.
     * @param numOficioSolicitante String
     */
    public void setNumOficioSolicitante(String numOficioSolicitante) {
        this.numOficioSolicitante = numOficioSolicitante;
    }
}
