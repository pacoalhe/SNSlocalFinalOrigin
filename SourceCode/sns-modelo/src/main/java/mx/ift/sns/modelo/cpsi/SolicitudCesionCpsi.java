package mx.ift.sns.modelo.cpsi;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import mx.ift.sns.modelo.pst.Contacto;
import mx.ift.sns.modelo.pst.Proveedor;

import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Representa una Solicitud de Cesión de CPSI. Contiene la información del trámite, información de las cesiones
 * solicitadas y el estatus del proceso.
 */
@Entity
@Table(name = "CPSI_SOLICITUD_CESION")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("15")
@NamedQuery(name = "SolicitudCesionCpsi.findAll", query = "SELECT s FROM SolicitudCesionCpsi s")
public class SolicitudCesionCpsi extends mx.ift.sns.modelo.solicitud.Solicitud implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Relación: Muchas solicitudes de cesión pueden ser para mismo Proveedor Cesionario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_CESIONARIO", nullable = true)
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
    private List<CesionSolicitadaCpsi> cesionesSolicitadas;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor. */
    public SolicitudCesionCpsi() {
    }

    // GETTERS & SETTERS

    /**
     * Proveedor Cesionario de la Solicitud de Cesión.
     * @return Proveedor
     */
    public Proveedor getProveedorCesionario() {
        return proveedorCesionario;
    }

    /**
     * Proveedor Cesionario de la Solicitud de Cesión.
     * @param proveedorCesionario Proveedor
     */
    public void setProveedorCesionario(Proveedor proveedorCesionario) {
        this.proveedorCesionario = proveedorCesionario;
    }

    /**
     * Representante Legal del Proveedor Cesionario de la Solicitud.
     * @return Contacto
     */
    public Contacto getRepresentanteLegalCesionario() {
        return representanteLegalCesionario;
    }

    /**
     * Representante Legal del Proveedor Cesionario de la Solicitud.
     * @param representanteLegalCesionario Contacto
     */
    public void setRepresentanteLegalCesionario(Contacto representanteLegalCesionario) {
        this.representanteLegalCesionario = representanteLegalCesionario;
    }

    /**
     * Representante Suplente del Proveedor Cesionario de la Solicitud.
     * @return Contacto
     */
    public Contacto getRepresentanteSuplenteCesionario() {
        return representanteSuplenteCesionario;
    }

    /**
     * Representante Suplente del Proveedor Cesionario de la Solicitud.
     * @param representanteSuplenteCesionario Contacto
     */
    public void setRepresentanteSuplenteCesionario(Contacto representanteSuplenteCesionario) {
        this.representanteSuplenteCesionario = representanteSuplenteCesionario;
    }

    /**
     * Lista de Cesiones Solicitadas por el trámite.
     * @return List
     */
    public List<CesionSolicitadaCpsi> getCesionesSolicitadas() {
        return cesionesSolicitadas;
    }

    /**
     * Lista de Cesiones Solicitadas por el trámite.
     * @param cesionesSolicitadas List
     */
    public void setCesionesSolicitadas(List<CesionSolicitadaCpsi> cesionesSolicitadas) {
        this.cesionesSolicitadas = cesionesSolicitadas;
    }

    /**
     * Asocia la información de cesión de un CPSI con el trámite que ha generado la solicitud.
     * @param pCesionSolicitadaCpsi CesionSolicitada CPSI
     * @return CesionSolicitadaCpsi
     */
    public CesionSolicitadaCpsi addCesionSolicitada(CesionSolicitadaCpsi pCesionSolicitadaCpsi) {
        this.getCesionesSolicitadas().add(pCesionSolicitadaCpsi);
        pCesionSolicitadaCpsi.setSolicitudCesion(this);
        return pCesionSolicitadaCpsi;
    }

    /**
     * Desasocia la información de cesión de un CPSI con el trámite que ha generado la solicitud.
     * @param pCesionSolicitadaCpsi CesionSolicitada CPSI
     * @return CesionSolicitadaCpsi
     */
    public CesionSolicitadaCpsi removeCesionSolicitada(CesionSolicitadaCpsi pCesionSolicitadaCpsi) {
        this.getCesionesSolicitadas().remove(pCesionSolicitadaCpsi);
        pCesionSolicitadaCpsi.setSolicitudCesion(null);
        return pCesionSolicitadaCpsi;
    }

}
