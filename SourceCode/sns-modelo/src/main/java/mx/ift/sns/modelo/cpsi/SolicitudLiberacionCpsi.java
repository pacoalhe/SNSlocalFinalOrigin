package mx.ift.sns.modelo.cpsi;

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
 * Representa una Solicitud de Liberación de CPSI. Contiene la información del trámite, información de las liberaciones
 * solicitadas y el estatus del proceso.
 */
@Entity
@Table(name = "CPSI_SOLICITUD_LIBERACION")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("16")
@NamedQuery(name = "SolicitudLiberacionCpsi.findAll", query = "SELECT s FROM SolicitudLiberacionCpsi s")
public class SolicitudLiberacionCpsi extends mx.ift.sns.modelo.solicitud.Solicitud implements Serializable {

    /** Serializacion. */
    private static final long serialVersionUID = 1L;

    /** Relación: Una solicitud de liberación CPSI puede tener muchas liberaciones solickitadas CPSI. */
    @OneToMany(mappedBy = "solicitudLiberacion", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<LiberacionSolicitadaCpsi> liberacionesSolicitadas;

    /** Indicador de Liberación solicitada por IFT. */
    @Column(name = "LIBERACION_IFT", length = 1)
    private Boolean liberacionIft;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor, por defecto vacío. */
    public SolicitudLiberacionCpsi() {
    }

    /**
     * Lista de Liberaciones de CPSI solicitadas por el trámite.
     * @return List
     */
    public List<LiberacionSolicitadaCpsi> getLiberacionesSolicitadas() {
        return liberacionesSolicitadas;
    }

    /**
     * Lista de Liberaciones de CPSI solicitadas por el trámite.
     * @param liberacionesSolicitadas List
     */
    public void setLiberacionesSolicitadas(List<LiberacionSolicitadaCpsi> liberacionesSolicitadas) {
        this.liberacionesSolicitadas = liberacionesSolicitadas;
    }

    /**
     * Asocia la información de liberación de un cpsn con el trámite que ha generado la solicitud.
     * @param pLiberacionSolicitadaCpsi LiberacionSolicitada CPSI
     * @return LiberacionSolicitadaCpsi
     */
    public LiberacionSolicitadaCpsi addLiberacionSolicitada(LiberacionSolicitadaCpsi pLiberacionSolicitadaCpsi) {
        this.getLiberacionesSolicitadas().add(pLiberacionSolicitadaCpsi);
        pLiberacionSolicitadaCpsi.setSolicitudLiberacion(this);
        return pLiberacionSolicitadaCpsi;
    }

    /**
     * Desasocia la información de liberación de un cpsn con el trámite que ha generado la solicitud.
     * @param pLiberacionSolicitadaCpsi LiberacionSolicitada CPSI
     * @return LiberacionSolicitadaCpsi
     */
    public LiberacionSolicitadaCpsi removeLiberacionSolicitada(LiberacionSolicitadaCpsi pLiberacionSolicitadaCpsi) {
        this.getLiberacionesSolicitadas().remove(pLiberacionSolicitadaCpsi);
        pLiberacionSolicitadaCpsi.setSolicitudLiberacion(null);
        return pLiberacionSolicitadaCpsi;
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
