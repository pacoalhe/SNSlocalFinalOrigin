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
 * Representa una Solicitud de Redistribución de Numeración Geográfica. Contiene la información del trámite, información
 * de las redistribuciones solicitadas y el estatus del proceso.
 */
@Entity
@Table(name = "NG_SOLICITUD_REDISTRIBUCION")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("4")
@NamedQuery(name = "SolicitudRedistribucionNg.findAll", query = "SELECT s FROM SolicitudRedistribucionNg s")
public class SolicitudRedistribucionNg extends Solicitud implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Relación: Una solicitud de redistribución puede tener muchas redistribuciones aplicadas. */
    @OneToMany(mappedBy = "solicitudRedistribucion", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<NumeracionRedistribuida> numeracionesRedistribuidas;

    /** Relación: Una solicitud de redistribución puede tener muchas redistribuciones aplicadas. */
    @OneToMany(mappedBy = "solicitudRedistribucion", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<RedistribucionSolicitadaNg> redistribucionesSolicitadas;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor, por defecto vacío. */
    public SolicitudRedistribucionNg() {
    }

    /**
     * Lista de Numeraciones Redistribuidas.
     * @return List
     */
    public List<NumeracionRedistribuida> getNumeracionesRedistribuidas() {
        return numeracionesRedistribuidas;
    }

    /**
     * Lista de Numeraciones Redistribuidas.
     * @param numeracionesRedistribuidas List
     */
    public void setNumeracionesRedistribuidas(List<NumeracionRedistribuida> numeracionesRedistribuidas) {
        this.numeracionesRedistribuidas = numeracionesRedistribuidas;
    }

    /**
     * Lista de Redistribuciones de Numeración solicitadas.
     * @return List
     */
    public List<RedistribucionSolicitadaNg> getRedistribucionesSolicitadas() {
        return redistribucionesSolicitadas;
    }

    /**
     * Lista de Redistribuciones de Numeración solicitadas.
     * @param redistribucionesSolicitadas List
     */
    public void setRedistribucionesSolicitadas(List<RedistribucionSolicitadaNg> redistribucionesSolicitadas) {
        this.redistribucionesSolicitadas = redistribucionesSolicitadas;
    }

    /**
     * Agrega una Numeración Redistribuida a la Lista de redistribuciones aplicadas.
     * @param pNumeracionRedistribuida Información de la numeración redistribuida
     * @return NumeracionRedistribuida
     */
    public NumeracionRedistribuida addNumeracionRedistribuida(NumeracionRedistribuida pNumeracionRedistribuida) {
        getNumeracionesRedistribuidas().add(pNumeracionRedistribuida);
        pNumeracionRedistribuida.setSolicitudRedistribucion(this);
        return pNumeracionRedistribuida;
    }

    /**
     * Elimina una Numeración Redistribuida de la Lista de redistribuciones aplicadas.
     * @param pNumeracionRedistribuida Información de la numeración redistribuida
     * @return NumeracionRedistribuida
     */
    public NumeracionRedistribuida removeNumeracionRedistribuida(NumeracionRedistribuida pNumeracionRedistribuida) {
        getNumeracionesRedistribuidas().remove(pNumeracionRedistribuida);
        pNumeracionRedistribuida.setSolicitudRedistribucion(null);
        return pNumeracionRedistribuida;
    }

    /**
     * Agrega una Redistribución Solicitada a la lista de solicitudes.
     * @param pRedistribucionSolicitada Información de la redistribución solicitada
     * @return RedistribucionSolicitadaNg
     */
    public RedistribucionSolicitadaNg addRedistribucionSolicitada(RedistribucionSolicitadaNg pRedistribucionSolicitada) {
        getRedistribucionesSolicitadas().add(pRedistribucionSolicitada);
        pRedistribucionSolicitada.setSolicitudRedistribucion(this);
        return pRedistribucionSolicitada;
    }

    /**
     * Elimina una Redistribución Solicitada de la lista de solicitudes.
     * @param pRedistribucionSolicitada Información de la redistribución solicitada
     * @return RedistribucionSolicitadaNg
     */
    public RedistribucionSolicitadaNg removeRedistribucionSolicitada(
            RedistribucionSolicitadaNg pRedistribucionSolicitada) {
        getRedistribucionesSolicitadas().remove(pRedistribucionSolicitada);
        pRedistribucionSolicitada.setSolicitudRedistribucion(null);
        return pRedistribucionSolicitada;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Solicitud Redistribución = {");
        builder.append("id: ").append(this.getId()).append(", ");
        if (this.getProveedorSolicitante() != null) {
            builder.append("solicitante: ").append(this.getProveedorSolicitante().getNombreCorto()).append(", ");
        }
        builder.append("fecha: ").append(this.getFechaSolicitud()).append("}");
        return builder.toString();
    }
}
