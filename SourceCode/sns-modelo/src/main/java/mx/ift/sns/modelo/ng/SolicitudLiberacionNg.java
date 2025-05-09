package mx.ift.sns.modelo.ng;

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
 * Representa una Solicitud de Liberación de Numeración Geográfica. Contiene la información del trámite, información de
 * las liberaciones solicitadas y el estatus del proceso.
 */
@Entity
@Table(name = "NG_SOLICITUD_LIBERACION")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("3")
@NamedQuery(name = "SolicitudLiberacionNg.findAll", query = "SELECT s FROM SolicitudLiberacionNg s")
public class SolicitudLiberacionNg extends Solicitud implements Serializable {

    /** Serializacion. */
    private static final long serialVersionUID = 1L;

    /** Relación: Una solicitud de liberación puede tener muchas liberaciones aplicadas. */
    @OneToMany(mappedBy = "solicitudLiberacion", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<Liberacion> liberacionesAplicadas;

    /** Relación: Una solicitud de liberación puede tener muchas liberaciones solicitadas. */
    @OneToMany(mappedBy = "solicitudLiberacion", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<LiberacionSolicitadaNg> liberacionesSolicitadas;

    /** Indicador de Liberación solicitada por IFT. */
    @Column(name = "LIBERACION_IFT", length = 1)
    private Boolean liberacionIft;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor, por defecto vacío. */
    public SolicitudLiberacionNg() {
    }

    /**
     * Lista de liberaciones finalizadas asociadas a la solicitud.
     * @return List
     */
    public List<Liberacion> getLiberaciones() {
        return liberacionesAplicadas;
    }

    /**
     * Lista de liberaciones finalizadas asociadas a la solicitud.
     * @param liberacionesAplicadas List
     */
    public void setLiberaciones(List<Liberacion> liberacionesAplicadas) {
        this.liberacionesAplicadas = liberacionesAplicadas;
    }

    /**
     * Lista de liberaciones inclidas en ésta misma solicitud.
     * @return List
     */
    public List<LiberacionSolicitadaNg> getLiberacionesSolicitadas() {
        return liberacionesSolicitadas;
    }

    /**
     * Lista de liberaciones inclidas en ésta misma solicitud.
     * @param liberacionesSolicitadas List
     */
    public void setLiberacionesSolicitadas(List<LiberacionSolicitadaNg> liberacionesSolicitadas) {
        this.liberacionesSolicitadas = liberacionesSolicitadas;
    }

    /**
     * Asocia una liberación aplicada con la solicitud que la originó.
     * @param pLiberacion Liberacion
     * @return Liberacion
     */
    public Liberacion addLiberacion(Liberacion pLiberacion) {
        this.getLiberaciones().add(pLiberacion);
        pLiberacion.setSolicitudLiberacion(this);
        return pLiberacion;
    }

    /**
     * Asocia una liberación aplicada con la solicitud que la originó.
     * @param pLiberacion Liberacion
     * @return Liberacion
     */
    public Liberacion removeLiberacion(Liberacion pLiberacion) {
        this.getLiberaciones().remove(pLiberacion);
        pLiberacion.setSolicitudLiberacion(null);
        return pLiberacion;
    }

    /**
     * Asocia la información de liberación de una numeración con la solicitud que lo ha requerido.
     * @param pLiberacionSolicitada LiberacionSolicitadaNg
     * @return LiberacionSolicitadaNg
     */
    public LiberacionSolicitadaNg addLiberacionSolicitada(LiberacionSolicitadaNg pLiberacionSolicitada) {
        this.getLiberacionesSolicitadas().add(pLiberacionSolicitada);
        pLiberacionSolicitada.setSolicitudLiberacion(this);
        return pLiberacionSolicitada;
    }

    /**
     * Asocia la información de liberación de una numeración con la solicitud que lo ha requerido.
     * @param pLiberacionSolicitada LiberacionSolicitadaNg
     * @return LiberacionSolicitadaNg
     */
    public LiberacionSolicitadaNg removeLiberacionSolicitada(LiberacionSolicitadaNg pLiberacionSolicitada) {
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
        builder.append("Solicitud Liberación NG = {");
        builder.append("id: ").append(this.getId()).append(", ");
        if (this.getProveedorSolicitante() != null) {
            builder.append("solicitante: ").append(this.getProveedorSolicitante().getNombreCorto()).append("}");
        }
        return builder.toString();
    }
}
