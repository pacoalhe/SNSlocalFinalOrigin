package mx.ift.sns.modelo.ng;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
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
 * Representa una Solicitud de Asignación de Numeración Geográfica. Contiene la información del trámite, información de
 * las asignaciones solicitadas y el estatus del proceso.
 */
@Entity
@Table(name = "NG_SOLICITUD_ASIGNACION")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("1")
@NamedQuery(name = "SolicitudAsignacion.findAll", query = "SELECT s FROM SolicitudAsignacion s")
public class SolicitudAsignacion extends Solicitud implements Serializable {

    /**
     * Serial id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * bi-directional many-to-one association to NumeracionAsignada.
     */
    @OneToMany(mappedBy = "solicitudAsignacion", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<NumeracionAsignada> numeracionAsignadas;
    /**
     * bi-directional many-to-one association to NumeracionSolicitada.
     */
    @OneToMany(mappedBy = "solicitudAsignacion", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<NumeracionSolicitada> numeracionSolicitadas;

    /** Version JPA. */
    @Version
    private long version;

    /**
     * Constructor SolicitudAsignacion.
     */
    public SolicitudAsignacion() {
    }

    /**
     * Getter numeracionAsignadas.
     * @return lista NumeracionAsignada
     */
    public List<NumeracionAsignada> getNumeracionAsignadas() {
        return this.numeracionAsignadas;
    }

    /**
     * Setter numeracionAsignadas.
     * @param numeracionAsignadas lista NumeracionAsignada
     */
    public void setNumeracionAsignadas(List<NumeracionAsignada> numeracionAsignadas) {
        this.numeracionAsignadas = numeracionAsignadas;
    }

    /**
     * Add numeracionAsignada.
     * @param numeracionAsignada NumeracionAsignada
     * @return NumeracionAsignada
     */
    public NumeracionAsignada addNumeracionAsignada(NumeracionAsignada numeracionAsignada) {
        getNumeracionAsignadas().add(numeracionAsignada);
        numeracionAsignada.setSolicitudAsignacion(this);

        return numeracionAsignada;
    }

    /**
     * Remove numeracionAsignada.
     * @param numeracionAsignada lista NumeracionAsignada
     * @return NumeracionAsignada
     */
    public NumeracionAsignada removeNumeracionAsignada(NumeracionAsignada numeracionAsignada) {
        getNumeracionAsignadas().remove(numeracionAsignada);
        numeracionAsignada.setSolicitudAsignacion(null);

        return numeracionAsignada;
    }

    /**
     * Getter numeracionSolicitadas.
     * @return lista NumeracionSolicitada
     */
    public List<NumeracionSolicitada> getNumeracionSolicitadas() {
        return this.numeracionSolicitadas;
    }

    /**
     * Setter numeracionSolicitadas.
     * @param numeracionSolicitadas lista NumeracionSolicitada
     */
    public void setNumeracionSolicitadas(List<NumeracionSolicitada> numeracionSolicitadas) {
        this.numeracionSolicitadas = numeracionSolicitadas;
    }

    /**
     * Add numeracionSolicitadas.
     * @param numeracionSolicitada NumeracionSolicitada
     * @return numeracionSolicitada
     */
    public NumeracionSolicitada addNumeracionSolicitadas(NumeracionSolicitada numeracionSolicitada) {
        getNumeracionSolicitadas().add(numeracionSolicitada);
        numeracionSolicitada.setSolicitudAsignacion(this);

        return numeracionSolicitada;
    }

    /**
     * Remove numeracionSolicitadas.
     * @param numeracionSolicitada NumeracionSolicitada
     * @return numeracionSolicitada
     */
    public NumeracionSolicitada removeNumeracionSolicitadas(NumeracionSolicitada numeracionSolicitada) {
        getNumeracionSolicitadas().remove(numeracionSolicitada);
        numeracionSolicitada.setSolicitudAsignacion(null);

        return numeracionSolicitada;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Solicitud Asignación {Id: ").append(getId());
        b.append(", Versión: ").append(version).append("}");
        return b.toString();
    }

}
