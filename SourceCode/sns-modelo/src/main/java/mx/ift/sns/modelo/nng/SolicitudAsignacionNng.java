package mx.ift.sns.modelo.nng;

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

import mx.ift.sns.modelo.solicitud.Solicitud;

import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Representa una Solicitud de Asignación de Numeración No Geográfica. Contiene la información del trámite, información
 * de las asignaciones solicitadas y el estatus del proceso.
 */
@Entity
@Table(name = "NNG_SOLICITUD_ASIGNACION")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("7")
@NamedQuery(name = "SolicitudAsignacionNng.findAll", query = "SELECT s FROM SolicitudAsignacionNng s")
public class SolicitudAsignacionNng extends Solicitud implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Tipo de Asignacion. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_ASIGNACION")
    private TipoAsignacion tipoAsignacion;

    /** Numeraciones Asignadas. */
    @OneToMany(mappedBy = "solicitudAsignacion", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<NumeracionSolicitadaNng> numeracionesSolicitadas;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor, por defecto vacío. */
    public SolicitudAsignacionNng() {
    }

    /**
     * Numeraciones Asignadas.
     * @return the numeracionesSolicitadas
     */
    public List<NumeracionSolicitadaNng> getNumeracionesSolicitadas() {
        return numeracionesSolicitadas;
    }

    /**
     * Numeraciones Asignadas.
     * @param numeracionesSolicitadas the numeracionesSolicitadas to set
     */
    public void setNumeracionesSolicitadas(List<NumeracionSolicitadaNng> numeracionesSolicitadas) {
        this.numeracionesSolicitadas = numeracionesSolicitadas;
    }

    /**
     * Añade una numeracion solicitada a numeracionesSolicitadas.
     * @param numeracionSolicitada NumeracionSolicitadaNng
     * @return numeracionSolicitada
     */
    public NumeracionSolicitadaNng addNumeracionSolicitada(NumeracionSolicitadaNng numeracionSolicitada) {
        this.numeracionesSolicitadas.add(numeracionSolicitada);
        numeracionSolicitada.setSolicitudAsignacion(this);
        return numeracionSolicitada;
    }

    /**
     * Elimina una numeracion solicitada a la Solicitud .
     * @param numeracionSolicitada NumeracionSolicitadaNng
     * @return numeracionSolicitada
     */
    public NumeracionSolicitadaNng removeNumeracionSolicitada(NumeracionSolicitadaNng numeracionSolicitada) {
        this.numeracionesSolicitadas.remove(findNumeracionInList(numeracionSolicitada));
        numeracionSolicitada.setSolicitudAsignacion(null);
        return numeracionSolicitada;
    }

    private int findNumeracionInList(NumeracionSolicitadaNng numeracionSolicitada){
        int index = 0;
        for( NumeracionSolicitadaNng numeracionSolicitadaNng : this.numeracionesSolicitadas ){

            if( numeracionSolicitadaNng == numeracionSolicitada ){
                return index;
            }
            index++;
        }

        return -1;
    }

    /**
     * Tipo de Asignacion.
     * @return the tipoAsignacion
     */
    public TipoAsignacion getTipoAsignacion() {
        return tipoAsignacion;
    }

    /**
     * Tipo de Asignacion.
     * @param tipoAsignacion the tipoAsignacion to set
     */
    public void setTipoAsignacion(TipoAsignacion tipoAsignacion) {
        this.tipoAsignacion = tipoAsignacion;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Solicitud Asignación {Id: ").append(getId());
        b.append(", Versión: ").append(version).append("}");
        return b.toString();
    }

}
