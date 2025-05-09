package mx.ift.sns.modelo.nng;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import mx.ift.sns.modelo.solicitud.Solicitud;

/**
 * Representa la solicitud de líneas activas de numración no geográfica.
 */
@Entity
@Table(name = "NNG_SOLICITUD_LINEAS_ACT")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("17")
@NamedQuery(name = "SolicitudLineasActivasNng.findAll", query = "SELECT s FROM SolicitudLineasActivasNng s")
public class SolicitudLineasActivasNng extends Solicitud implements Serializable {
    /** Serial uid. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor vacio.
     */
    public SolicitudLineasActivasNng() {
    }
}
