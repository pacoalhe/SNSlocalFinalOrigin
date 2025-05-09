package mx.ift.sns.modelo.pnn;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import mx.ift.sns.modelo.nng.ClaveServicio;

/**
 * Plan de numeración nacional.
 */
@Entity
@Table(name = "PNN_PLAN")
@NamedQuery(name = "Plan.findAll", query = "SELECT p FROM Plan p")
@SequenceGenerator(name = "SEQ_ID_PNN_PLAN", sequenceName = "SEQ_ID_PNN_PLAN", allocationSize = 1)
public class Plan implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Id del plan. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_PNN_PLAN")
    @Column(name = "ID_PNN_PLAN", unique = true, nullable = false)
    private long id;

    /** Fecha de generación del plan. */
    @Column(name = "FECHA_GENERACION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaGeneracion;

    /** Nombre del plan. */
    @Column(nullable = false, length = 100)
    private String nombre;

    /** Tipo de plan. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_PLAN", nullable = false)
    private TipoPlan tipoPlan;

    /** Documento serializado. */
    @Lob
    @Column(name = "FICHERO", nullable = true)
    private byte[] fichero;

    /** Clave de servicio. */
    @ManyToOne
    @JoinColumn(name = "ID_CLAVE_SERVICIO", nullable = true)
    private ClaveServicio claveServicio;

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the fechaGeneracion
     */
    public Date getFechaGeneracion() {
        return fechaGeneracion;
    }

    /**
     * @param fechaGeneracion the fechaGeneracion to set
     */
    public void setFechaGeneracion(Date fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the tipoPlan
     */
    public TipoPlan getTipoPlan() {
        return tipoPlan;
    }

    /**
     * @param tipoPlan the tipoPlan to set
     */
    public void setTipoPlan(TipoPlan tipoPlan) {
        this.tipoPlan = tipoPlan;
    }

    /**
     * @return the fichero
     */
    public byte[] getFichero() {
        return fichero;
    }

    /**
     * @param fichero the fichero to set
     */
    public void setFichero(byte[] fichero) {
        this.fichero = fichero;
    }

    /**
     * @return the claveServicio
     */
    public ClaveServicio getClaveServicio() {
        return claveServicio;
    }

    /**
     * @param claveServicio the claveServicio to set
     */
    public void setClaveServicio(ClaveServicio claveServicio) {
        this.claveServicio = claveServicio;
    }

}
