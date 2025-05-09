package mx.ift.sns.modelo.oficios;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Identificador de una Plantilla de Oficio.
 */
@Embeddable
public class PlantillaPK implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del tipo de solicitud. */
    @Column(name = "ID_TIPO_SOLICITUD",
            insertable = false, updatable = false, unique = true, nullable = false, length = 1)
    private Integer cdgTipoSolicitud;

    /** Identificador del tipo de destinatario. */
    @Column(name = "ID_TIPO_DESTINATARIO",
            insertable = false, updatable = false, unique = true, nullable = false, length = 1)
    private String cdgTipoDestinatario;

    /** Constructor, por defecto vacío. */
    public PlantillaPK() {
    }

    /**
     * Identificador del tipo de solicitud.
     * @return String
     */
    public Integer getCdgTipoSolicitud() {
        return this.cdgTipoSolicitud;
    }

    /**
     * Identificador del tipo de solicitud.
     * @param cdgTipoSolicitud String
     */
    public void setCdgTipoSolicitud(Integer cdgTipoSolicitud) {
        this.cdgTipoSolicitud = cdgTipoSolicitud;
    }

    /**
     * Identificador del tipo de destinatario.
     * @return String
     */
    public String getCdgTipoDestinatario() {
        return this.cdgTipoDestinatario;
    }

    /**
     * Identificador del tipo de destinatario.
     * @param cdgTipoDestinatario String
     */
    public void setCdgTipoDestinatario(String cdgTipoDestinatario) {
        this.cdgTipoDestinatario = cdgTipoDestinatario;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PlantillaPK)) {
            return false;
        }
        PlantillaPK castOther = (PlantillaPK) other;
        return this.cdgTipoSolicitud.equals(castOther.cdgTipoSolicitud)
                && this.cdgTipoDestinatario.equals(castOther.cdgTipoDestinatario);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.cdgTipoSolicitud.hashCode();
        hash = hash * prime + this.cdgTipoDestinatario.hashCode();

        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sbKey = new StringBuilder();
        sbKey.append(cdgTipoSolicitud).append("-").append(cdgTipoDestinatario);
        return sbKey.toString();
    }
}
