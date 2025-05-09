package mx.ift.sns.modelo.ng;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Represental la consolidación de abn de numeración geográfica.
 */
@Entity
@Table(name = "NG_ABN_CONSOLIDAR")
@SequenceGenerator(name = "SEQ_ID_ABN_CONSO", sequenceName = "SEQ_ID_ABN_CONSO", allocationSize = 1)
@NamedQuery(name = "AbnConsolidar.findAll", query = "SELECT a FROM AbnConsolidar a")
public class AbnConsolidar implements Serializable {

    /** Serializacion . */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_ABN_CONSO")
    @Column(name = "ID_ABN_CONSOLIDAR", unique = true, nullable = false)
    private BigDecimal id;

    /** Presuscripcion. */
    @Column(name = "PRESUSCRIPCION", nullable = false, length = 1)
    private String presuscripcion;

    /** Relación: Un abnConsolidar puede tener muchas Solicitud de Consolidacion. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL", nullable = false)
    private SolicitudConsolidacion solicitudConsolidacion;

    /** Fecha de consolidacion. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_CONSOLIDACION", nullable = false)
    private Date fechaConsolidacion;

    /** Relación: Muchos Abn's consolidados pueden tener el mismo estado. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTADO_ABN_CONSOLIDAR", nullable = false)
    private EstadoAbnConsolidar estado;

    /** Relación: Una abnConsolidar puede tener muchas poblaciones a consolidar. */
    @OneToMany(mappedBy = "abnConsolidar", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<PoblacionConsolidar> poblacionConsolidar;

    /** Relación: Una abnConsolidar puede tener muchos Nir's a consolidar. */
    @OneToMany(mappedBy = "abnConsolidar", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<NirConsolidar> nirConsolidar;

    /** Constructor, por defecto vacío. */
    public AbnConsolidar() {
    }

    /**
     * Devuelve el Id.
     * @return long
     */
    public BigDecimal getId() {
        return this.id;
    }

    /**
     * Setea el id.
     * @param id id
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Devuelve la presuscripcion.
     * @return String
     */
    public String getPresuscripcion() {
        return this.presuscripcion;
    }

    /**
     * Setea la presuscripcion.
     * @param presuscripcion presuscripcion
     */
    public void setPresuscripcion(String presuscripcion) {
        this.presuscripcion = presuscripcion;
    }

    /**
     * solicitudConsolidacion.
     * @return the solicitudConsolidacion
     */
    public SolicitudConsolidacion getSolicitudConsolidacion() {
        return solicitudConsolidacion;
    }

    /**
     * solicitudConsolidacion.
     * @param solicitudConsolidacion the solicitudConsolidacion to set
     */
    public void setSolicitudConsolidacion(
            SolicitudConsolidacion solicitudConsolidacion) {
        this.solicitudConsolidacion = solicitudConsolidacion;
    }

    /**
     * Fecha consolidacion.
     * @return Date
     */
    public Date getFechaConsolidacion() {
        return this.fechaConsolidacion;
    }

    /**
     * Fecha consolidacion.
     * @param fechaConsolidacion fechaConsolidacion
     */
    public void setFechaConsolidacion(Date fechaConsolidacion) {
        this.fechaConsolidacion = fechaConsolidacion;
    }

    /**Relación: Muchos Abn's consolidados pueden tener el mismo estado.
     * @return the estado
     */
    public EstadoAbnConsolidar getEstado() {
        return estado;
    }

    /**Relación: Muchos Abn's consolidados pueden tener el mismo estado.
     * @param estado the estado to set
     */
    public void setEstado(EstadoAbnConsolidar estado) {
        this.estado = estado;
    }

    /**Relación: Una abnConsolidar puede tener muchas poblaciones a consolidar.
     * @return the poblacionConsolidar
     */
    public List<PoblacionConsolidar> getPoblacionConsolidar() {
        return poblacionConsolidar;
    }

    /**Relación: Una abnConsolidar puede tener muchas poblaciones a consolidar.
     * @param poblacionConsolidar the poblacionConsolidar to set
     */
    public void setPoblacionConsolidar(List<PoblacionConsolidar> poblacionConsolidar) {
        this.poblacionConsolidar = poblacionConsolidar;
    }

    /**Relación: Una abnConsolidar puede tener muchos Nir's a consolidar.
     * @return the nirConsolidar
     */
    public List<NirConsolidar> getNirConsolidar() {
        return nirConsolidar;
    }

    /**Relación: Una abnConsolidar puede tener muchos Nir's a consolidar.
     * @param nirConsolidar the nirConsolidar to set
     */
    public void setNirConsolidar(List<NirConsolidar> nirConsolidar) {
        this.nirConsolidar = nirConsolidar;
    }

}
