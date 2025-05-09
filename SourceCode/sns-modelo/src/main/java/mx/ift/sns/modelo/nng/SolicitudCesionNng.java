package mx.ift.sns.modelo.nng;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import mx.ift.sns.modelo.pst.Contacto;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.solicitud.Solicitud;

import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Representa una Solicitud de Cesión de Numeración No Geográfica. Contiene la información del trámite, información de
 * las cesiones solicitadas y el estatus del proceso.
 */
@Entity
@Table(name = "NNG_SOLICITUD_CESION")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("8")
@NamedQuery(name = "SolicitudCesionNng.findAll", query = "SELECT n FROM SolicitudCesionNng n")
public class SolicitudCesionNng extends Solicitud implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Fecha de Autorización. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_AUTORIZACION")
    private Date fechaAutorizacion;

    /** Relación: Muchas solicitudes de cesión pueden ser para mismo Proveedor Cesionario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_CESIONARIO")
    private Proveedor proveedorCesionario;

    /** Relación: Muchas solicitudes de cesión pueden tener el mismo representante Legal del Cesionario. */
    @ManyToOne
    @JoinColumn(name = "ID_REP_LEGAL_CESIONARIO", nullable = true)
    private Contacto representanteLegalCesionario;

    /** Relación: Muchas solicitudes de cesión pueden tener el mismo representante Suplente del Cesionario. */
    @ManyToOne
    @JoinColumn(name = "ID_REP_SUPLENTE_CESIONARIO", nullable = true)
    private Contacto representanteSuplenteCesionario;

    /** Relación: Una solicitud de cesión puede tener muchas cesiones solicitadas. */
    @OneToMany(mappedBy = "solicitudCesion", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<CesionSolicitadaNng> cesionesSolicitadas;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor, por defecto vacío. */
    public SolicitudCesionNng() {
    }

    // GETTERS & SETTERS

    /**
     * Fecha de Autorización.
     * @return Date
     */
    public Date getFechaAutorizacion() {
        return fechaAutorizacion;
    }

    /**
     * Fecha de Autorización.
     * @param fechaAutorizacion Date
     */
    public void setFechaAutorizacion(Date fechaAutorizacion) {
        this.fechaAutorizacion = fechaAutorizacion;
    }

    /**
     * Proveedor Cesionario de la Numeración.
     * @return Proveedor
     */
    public Proveedor getProveedorCesionario() {
        return proveedorCesionario;
    }

    /**
     * Proveedor Cesionario de la Numeración.
     * @param proveedorCesionario Proveedor
     */
    public void setProveedorCesionario(Proveedor proveedorCesionario) {
        this.proveedorCesionario = proveedorCesionario;
    }

    /**
     * Representante Legal del Proveedor Cesionario.
     * @return Contacto
     */
    public Contacto getRepresentanteLegalCesionario() {
        return representanteLegalCesionario;
    }

    /**
     * Representante Legal del Proveedor Cesionario.
     * @param representanteLegalCesionario Contacto
     */
    public void setRepresentanteLegalCesionario(Contacto representanteLegalCesionario) {
        this.representanteLegalCesionario = representanteLegalCesionario;
    }

    /**
     * Representante Suplente del Proveedor Cesionario.
     * @return Contacto
     */
    public Contacto getRepresentanteSuplenteCesionario() {
        return representanteSuplenteCesionario;
    }

    /**
     * Representante Suplente del Proveedor Cesionario.
     * @param representanteSuplenteCesionario Contacto
     */
    public void setRepresentanteSuplenteCesionario(Contacto representanteSuplenteCesionario) {
        this.representanteSuplenteCesionario = representanteSuplenteCesionario;
    }

    /**
     * Lista de Cesiones Solicitadas por la Solicitud de Cesión.
     * @return List
     */
    public List<CesionSolicitadaNng> getCesionesSolicitadas() {
        return cesionesSolicitadas;
    }

    /**
     * Lista de Cesiones Solicitadas por la Solicitud de Cesión.
     * @param cesionesSolicitadas List
     */
    public void setCesionesSolicitadas(List<CesionSolicitadaNng> cesionesSolicitadas) {
        this.cesionesSolicitadas = cesionesSolicitadas;
    }

    /**
     * Añade una Cesión Solicitada a la Solicitud de Cesión.
     * @param cesionSolicitadaNng Información de la Cesión Solicitada.
     * @return CesionSolicitadaNng
     */
    public CesionSolicitadaNng addCesionSolicitada(CesionSolicitadaNng cesionSolicitadaNng) {
        this.getCesionesSolicitadas().add(cesionSolicitadaNng);
        cesionSolicitadaNng.setSolicitudCesion(this);
        return cesionSolicitadaNng;
    }

    /**
     * Elimina una Cesión Solicitada a la Solicitud de Cesión.
     * @param cesionSolicitadaNng Información de la Cesión Solicitada.
     * @return CesionSolicitadaNng
     */
    public CesionSolicitadaNng removeCesionSolicitada(CesionSolicitadaNng cesionSolicitadaNng) {
        this.getCesionesSolicitadas().remove(cesionSolicitadaNng);
        cesionSolicitadaNng.setSolicitudCesion(null);
        return cesionSolicitadaNng;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SolicitudCesionNng = {");
        builder.append("id: ").append(this.getId()).append(", ");
        if (this.getProveedorSolicitante() != null) {
            builder.append("solicitante: ").append(this.getProveedorSolicitante().getNombreCorto()).append(", ");
        }
        builder.append("fecha: ").append(this.getFechaSolicitud()).append("}");
        return builder.toString();
    }
}
