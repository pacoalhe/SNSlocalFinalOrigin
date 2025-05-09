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
 * Representa una Solicitud de Asignación de CPSN. Contiene la información del trámite, información de las asignaciones
 * solicitadas y el estatus del proceso.
 */
@Entity
@Table(name = "CPSN_SOLICITUD_ASIGNACION")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("11")
@NamedQuery(name = "SolicitudAsignacionCpsn.findAll", query = "SELECT s FROM SolicitudAsignacionCpsn s")
public class SolicitudAsignacionCpsn extends Solicitud implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /**
     * bi-directional many-to-one association to NumeracionAsignada.
     */
    @OneToMany(mappedBy = "solicitudAsignacion", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<NumeracionAsignadaCpsn> numeracionAsignadas;
    /**
     * bi-directional many-to-one association to NumeracionSolicitada.
     */
    @OneToMany(mappedBy = "solicitudAsignacion", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<NumeracionSolicitadaCpsn> numeracionSolicitadas;

    /** dias aplicación. */
    @Column(name = "DIAS_APLICACION")
    private Integer diasAplicacion;

    /** Version JPA. */
    @Version
    private long version;

    /**
     * Constructor SolicitudAsignacion.
     */
    public SolicitudAsignacionCpsn() {
    }

    /**
     * dias aplicación.
     * @return the diasAplicacion
     */
    public Integer getDiasAplicacion() {
        return diasAplicacion;
    }

    /**
     * dias aplicación.
     * @param diasAplicacion the diasAplicacion to set
     */
    public void setDiasAplicacion(Integer diasAplicacion) {
        this.diasAplicacion = diasAplicacion;
    }

    /**
     * Getter numeracionAsignadas.
     * @return lista NumeracionAsignada
     */
    public List<NumeracionAsignadaCpsn> getNumeracionAsignadas() {
        return this.numeracionAsignadas;
    }

    /**
     * Setter numeracionAsignadas.
     * @param numeracionAsignadas lista NumeracionAsignada
     */
    public void setNumeracionAsignadas(List<NumeracionAsignadaCpsn> numeracionAsignadas) {
        this.numeracionAsignadas = numeracionAsignadas;
    }

    /**
     * Add numeracionAsignada.
     * @param numeracionAsignada NumeracionAsignada
     * @return NumeracionAsignada
     */
    public NumeracionAsignadaCpsn addNumeracionAsignada(NumeracionAsignadaCpsn numeracionAsignada) {
        getNumeracionAsignadas().add(numeracionAsignada);
        numeracionAsignada.setSolicitudAsignacion(this);

        return numeracionAsignada;
    }

    /**
     * Remove numeracionAsignada.
     * @param numeracionAsignada lista NumeracionAsignada
     * @return NumeracionAsignada
     */
    public NumeracionAsignadaCpsn removeNumeracionAsignada(NumeracionAsignadaCpsn numeracionAsignada) {
        getNumeracionAsignadas().remove(numeracionAsignada);
        numeracionAsignada.setSolicitudAsignacion(null);

        return numeracionAsignada;
    }

    /**
     * Getter numeracionSolicitadas.
     * @return lista NumeracionSolicitada
     */
    public List<NumeracionSolicitadaCpsn> getNumeracionSolicitadas() {
        return this.numeracionSolicitadas;
    }

    /**
     * Setter numeracionSolicitadas.
     * @param numeracionSolicitadas lista NumeracionSolicitada
     */
    public void setNumeracionSolicitadas(List<NumeracionSolicitadaCpsn> numeracionSolicitadas) {
        this.numeracionSolicitadas = numeracionSolicitadas;
    }

    /**
     * Add numeracionSolicitadas.
     * @param numeracionSolicitada NumeracionSolicitada
     * @return numeracionSolicitada
     */
    public NumeracionSolicitadaCpsn addNumeracionSolicitadas(NumeracionSolicitadaCpsn numeracionSolicitada) {
        getNumeracionSolicitadas().add(numeracionSolicitada);
        numeracionSolicitada.setSolicitudAsignacion(this);

        return numeracionSolicitada;
    }

    /**
     * Remove numeracionSolicitadas.
     * @param numeracionSolicitada NumeracionSolicitada
     * @return numeracionSolicitada
     */
    public NumeracionSolicitadaCpsn removeNumeracionSolicitadas(NumeracionSolicitadaCpsn numeracionSolicitada) {
        getNumeracionSolicitadas().remove(numeracionSolicitada);
        numeracionSolicitada.setSolicitudAsignacion(null);

        return numeracionSolicitada;
    }

    @Override
    public String toString() {

        StringBuilder b = new StringBuilder();

        b.append("solicitud asignacion {id=)");
        b.append(getId());
        b.append("}");

        return b.toString();

    }
}
