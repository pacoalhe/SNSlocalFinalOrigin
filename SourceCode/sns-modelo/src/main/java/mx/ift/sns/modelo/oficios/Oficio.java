package mx.ift.sns.modelo.oficios;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import mx.ift.sns.modelo.solicitud.Solicitud;

/**
 * Representa un Oficio de un trámite. Contiene la información de la documentación generada al finalizar un trámite.
 */
@Entity
@Table(name = "SOL_OFICIO")
@SequenceGenerator(name = "SEQ_ID_OFICIOS", sequenceName = "SEQ_ID_OFICIOS", allocationSize = 1)
@NamedQuery(name = "Oficio.findAll", query = "SELECT o FROM Oficio o")
public class Oficio implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Longitud del numero de oficio: 40 Prefijo + 5 Núm + 4 año + 2'/'. */
    public static final int LONGITUD_NUM_OFICIO = 51;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_OFICIOS")
    @Column(name = "ID_SOL_OFICIO", unique = true, nullable = false)
    private BigDecimal id;

    /** Fecha de creación. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_OFICIO")
    private Date fechaOficio;

    /** Número de oficio. */
    @Column(name = "NUM_OFICIO", length = 55)
    private String numOficio;

    /** Fecha de actualización. */
    @Temporal(TemporalType.DATE)
    @Column(name = "UPDATE_TIME")
    private Date fechaActualizacion;

    /** Relación: Muchos oficios pueden ser tener el mismo tipo de destinatario. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_DESTINATARIO", nullable = false)
    private TipoDestinatario tipoDestinatario;

    /** Relación: Muchos oficios pueden pertenecer a una misma solicitud. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL", nullable = false)
    private Solicitud solicitud;

    /** Documento BLOB asociado al oficio. */
    @Column(name = "ID_SOL_OFICIO_DOC", unique = true, nullable = true)
    private BigDecimal idOficioBlob;

    /** Constructor, por defecto vacío. */
    public Oficio() {
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
     * Fecha de creación.
     * @return Date
     */
    public Date getFechaOficio() {
        return this.fechaOficio;
    }

    /**
     * Fecha de creación.
     * @param fechaOficio Date
     */
    public void setFechaOficio(Date fechaOficio) {
        this.fechaOficio = fechaOficio;
    }

    /**
     * Número de oficio.
     * @return String
     */
    public String getNumOficio() {
        return this.numOficio;
    }

    /**
     * Número de oficio.
     * @param numOficio String
     */
    public void setNumOficio(String numOficio) {
        this.numOficio = numOficio;
    }

    /**
     * Fecha de actualización.
     * @return Date
     */
    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    /**
     * Fecha de actualización.
     * @param fechaActualizacion Date
     */
    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    /**
     * Tipo de Destinatario del Oficio.
     * @return TipoDestinatario
     */
    public TipoDestinatario getTipoDestinatario() {
        return tipoDestinatario;
    }

    /**
     * Tipo de Destinatario del Oficio.
     * @param tipoDestinatario TipoDestinatario
     */
    public void setTipoDestinatario(TipoDestinatario tipoDestinatario) {
        this.tipoDestinatario = tipoDestinatario;
    }

    /**
     * Solicitud asociada al oficio.
     * @return Solicitud
     */
    public Solicitud getSolicitud() {
        return solicitud;
    }

    /**
     * Solicitud asociada al oficio.
     * @param solicitud Solicitud
     */
    public void setSolicitud(Solicitud solicitud) {
        this.solicitud = solicitud;
    }

    /**
     * Identificador del Documento serializado asociado al oficio.
     * @return BigDecimal
     */
    public BigDecimal getIdOficioBlob() {
        return idOficioBlob;
    }

    /**
     * Identificador del Documento serializado asociado al oficio.
     * @param idOficioBlob BigDecimal
     */
    public void setIdOficioBlob(BigDecimal idOficioBlob) {
        this.idOficioBlob = idOficioBlob;
    }
}
