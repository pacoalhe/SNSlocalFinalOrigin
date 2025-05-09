package mx.ift.sns.modelo.cpsn;

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

import mx.ift.sns.modelo.solicitud.Solicitud;

import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Representa una Solicitud de Liberación de CPSN. Contiene la información del trámite, información de las liberaciones
 * solicitadas y el estatus del proceso.
 */
@Entity
@Table(name = "CPSN_SOLICITUD_LIBERACION")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("13")
@NamedQuery(name = "SolicitudLiberacionCpsn.findAll", query = "SELECT s FROM SolicitudLiberacionCpsn s")
public class SolicitudLiberacionCpsn extends Solicitud implements Serializable {

    /** Serializacion. */
    private static final long serialVersionUID = 1L;

    /** Relación: Una solicitud de liberación CPSN puede tener muchas liberaciones solicitadas CPSN. */
    @OneToMany(mappedBy = "solicitudLiberacion", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<LiberacionSolicitadaCpsn> liberacionesSolicitadas;

    /** Indicador de Liberación solicitada por IFT. */
    @Column(name = "LIBERACION_IFT", length = 1)
    private Boolean liberacionIft;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor, por defecto vacío. */
    public SolicitudLiberacionCpsn() {
    }

    /**
     * Lista de Liberaciones de CPSN solicitadas por el trámite.
     * @return List
     */
    public List<LiberacionSolicitadaCpsn> getLiberacionesSolicitadas() {
        return liberacionesSolicitadas;
    }

    /**
     * Lista de Liberaciones de CPSN solicitadas por el trámite.
     * @param liberacionesSolicitadas List
     */
    public void setLiberacionesSolicitadas(List<LiberacionSolicitadaCpsn> liberacionesSolicitadas) {
        this.liberacionesSolicitadas = liberacionesSolicitadas;
    }

    /**
     * Asocia la información de liberación de un cpsn con el trámite que ha generado la solicitud.
     * @param pLiberacionSolicitadaCpsn LiberacionSolicitada CPSN
     * @return LiberacionSolicitadaCpsn
     */
    public LiberacionSolicitadaCpsn addLiberacionSolicitada(LiberacionSolicitadaCpsn pLiberacionSolicitadaCpsn) {
        this.getLiberacionesSolicitadas().add(pLiberacionSolicitadaCpsn);
        pLiberacionSolicitadaCpsn.setSolicitudLiberacion(this);
        return pLiberacionSolicitadaCpsn;
    }

    /**
     * Desasocia la información de liberación de un cpsn con el trámite que ha generado la solicitud.
     * @param pLiberacionSolicitadaCpsn LiberacionSolicitada CPSN
     * @return LiberacionSolicitadaCpsn
     */
    public LiberacionSolicitadaCpsn removeLiberacionSolicitada(LiberacionSolicitadaCpsn pLiberacionSolicitadaCpsn) {
        this.getLiberacionesSolicitadas().remove(pLiberacionSolicitadaCpsn);
        pLiberacionSolicitadaCpsn.setSolicitudLiberacion(null);
        return pLiberacionSolicitadaCpsn;
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
}
