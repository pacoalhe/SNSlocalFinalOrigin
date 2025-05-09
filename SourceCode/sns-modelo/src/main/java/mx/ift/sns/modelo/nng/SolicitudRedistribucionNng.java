package mx.ift.sns.modelo.nng;

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
 * Representa una Solicitud de Redistribución de Numeración No Geográfica. Contiene la información del trámite,
 * información de las redistribuciones solicitadas y el estatus del proceso.
 */
@Entity
@Table(name = "NNG_SOLICITUD_REDISTRIBUCION")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("10")
@NamedQuery(name = "SolicitudRedistribucionNng.findAll", query = "SELECT s FROM SolicitudRedistribucionNng s")
public class SolicitudRedistribucionNng extends Solicitud implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Relación: Una solicitud de redistribución puede tener muchas redistribuciones aplicadas. */
    @OneToMany(mappedBy = "solicitudRedistribucion", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<RedistribucionSolicitadaNng> redistribucionesSolicitadas;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor, por defecto vacío. */
    public SolicitudRedistribucionNng() {
    }

    /**
     * Lista de Redistribuciones de Numeración solicitadas.
     * @return List
     */
    public List<RedistribucionSolicitadaNng> getRedistribucionesSolicitadas() {
        return redistribucionesSolicitadas;
    }

    /**
     * Lista de Redistribuciones de Numeración solicitadas.
     * @param redistribucionesSolicitadas List
     */
    public void setRedistribucionesSolicitadas(List<RedistribucionSolicitadaNng> redistribucionesSolicitadas) {
        this.redistribucionesSolicitadas = redistribucionesSolicitadas;
    }

    /**
     * Agrega una Redistribución Solicitada a la lista de solicitudes.
     * @param pRedistribucionSolicitada Información de la redistribución solicitada
     * @return RedistribucionSolicitadaNg
     */
    public RedistribucionSolicitadaNng addRedistribucionSolicitada(
            RedistribucionSolicitadaNng pRedistribucionSolicitada) {
        getRedistribucionesSolicitadas().add(pRedistribucionSolicitada);
        pRedistribucionSolicitada.setSolicitudRedistribucion(this);
        return pRedistribucionSolicitada;
    }

    /**
     * Elimina una Redistribución Solicitada de la lista de solicitudes.
     * @param pRedistribucionSolicitada Información de la redistribución solicitada
     * @return RedistribucionSolicitadaNg
     */
    public RedistribucionSolicitadaNng removeRedistribucionSolicitada(
            RedistribucionSolicitadaNng pRedistribucionSolicitada) {
        getRedistribucionesSolicitadas().remove(pRedistribucionSolicitada);
        pRedistribucionSolicitada.setSolicitudRedistribucion(null);
        return pRedistribucionSolicitada;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Solicitud Redistribución NNG = {");
        builder.append("id: ").append(this.getId()).append(", ");
        if (this.getProveedorSolicitante() != null) {
            builder.append("solicitante: ").append(this.getProveedorSolicitante().getNombreCorto()).append(", ");
        }
        return builder.toString();
    }

}
