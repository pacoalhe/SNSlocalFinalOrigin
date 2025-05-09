package mx.ift.sns.modelo.nng;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Representa una Solicitud de Liberación de Numeración No Geográfica. Contiene la información del trámite, información
 * de las liberaciones solicitadas y el estatus del proceso.
 */
@Entity
@Table(name = "NNG_SOLICITUD_LIBERACION")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("9")
@NamedQuery(name = "SolicitudLiberacionNng.findAll", query = "SELECT s FROM SolicitudLiberacionNng s")
public class SolicitudLiberacionNng extends mx.ift.sns.modelo.solicitud.Solicitud implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Relación: Una solicitud de liberación puede tener muchas liberaciones solicitadas. */
    @OneToMany(mappedBy = "solicitudLiberacion", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<LiberacionSolicitadaNng> liberacionesSolicitadas;

    /** Indicador de Liberación solicitada por IFT. */
    @Column(name = "LIBERACION_IFT", length = 1)
    private Boolean liberacionIft;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor, por defecto vacío. */
    public SolicitudLiberacionNng() {
    }

    /** GETTERS & SETTERS */

    /**
     * Lista de liberaciones solicitidas.
     * @return List
     */
    public List<LiberacionSolicitadaNng> getLiberacionesSolicitadas() {
        return liberacionesSolicitadas;
    }

    /**
     * Lista de liberaciones solicitidas.
     * @param liberacionesSolicitadas List
     */
    public void setLiberacionesSolicitadas(List<LiberacionSolicitadaNng> liberacionesSolicitadas) {
        this.liberacionesSolicitadas = liberacionesSolicitadas;
    }

    /**
     * Asocia la información de liberación de una numeración con la solicitud que lo ha requerido.
     * @param pLiberacionSolicitada LiberacionSolicitadaNng
     * @return LiberacionSolicitadaNng
     */
    public LiberacionSolicitadaNng addLiberacionSolicitada(LiberacionSolicitadaNng pLiberacionSolicitada) {
        this.getLiberacionesSolicitadas().add(pLiberacionSolicitada);
        pLiberacionSolicitada.setSolicitudLiberacion(this);
        return pLiberacionSolicitada;
    }

    /**
     * Asocia la información de liberación de una numeración con la solicitud que lo ha requerido.
     * @param pLiberacionSolicitada LiberacionSolicitadaNng
     * @return LiberacionSolicitadaNng
     */
    public LiberacionSolicitadaNng removeLiberacionSolicitada(LiberacionSolicitadaNng pLiberacionSolicitada) {
        this.getLiberacionesSolicitadas().remove(pLiberacionSolicitada);
        pLiberacionSolicitada.setSolicitudLiberacion(null);
        return pLiberacionSolicitada;
    }

    /**
     * Indicador de Liberación solicitada por IFT.
     * @return Boolean
     */
    public Boolean isLiberacionIft() {
        return liberacionIft;
    }

    /**
     * Indicador de Liberación solicitada por IFT.
     * @param liberacionIft Boolean
     */
    public void setLiberacionIft(Boolean liberacionIft) {
        this.liberacionIft = liberacionIft;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Solicitud Liberación NNG = {");
        builder.append("id: ").append(this.getId()).append(", ");
        if (this.getProveedorSolicitante() != null) {
            builder.append("solicitante: ").append(this.getProveedorSolicitante().getNombreCorto()).append("}");
        }
        return builder.toString();
    }

}
