package mx.ift.sns.modelo.ng;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import mx.ift.sns.modelo.solicitud.Solicitud;

/**
 * Solicitud de lineas activas.
 */
@Entity
@Table(name = "NG_SOLICITUD_LINEAS_ACT")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("6")
@NamedQuery(name = "SolicitudLineasActivas.findAll", query = "SELECT s FROM SolicitudLineasActivas s")
public class SolicitudLineasActivas extends Solicitud implements Serializable {

    /** Serial uid. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor vacio.
     */
    public SolicitudLineasActivas() {
    }
}
