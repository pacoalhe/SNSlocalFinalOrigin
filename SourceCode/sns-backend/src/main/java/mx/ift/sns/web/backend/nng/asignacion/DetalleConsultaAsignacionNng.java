package mx.ift.sns.web.backend.nng.asignacion;

import java.io.Serializable;

import mx.ift.sns.modelo.nng.SolicitudAsignacionNng;

/**
 * Representa la información de consulta de una solicitud de liberación adaptada al front.
 */
public class DetalleConsultaAsignacionNng implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Solictud. */
    private SolicitudAsignacionNng solicitud;

    /** Cliente. */
    private String cliente;

    /**
     * Solictud.
     * @return solicitud
     */
    public SolicitudAsignacionNng getSolicitud() {
        return solicitud;
    }

    /**
     * Solictud.
     * @param solicitud solicitud to set
     */
    public void setSolicitud(SolicitudAsignacionNng solicitud) {
        this.solicitud = solicitud;
    }

    /**
     * Cliente.
     * @return cliente
     */
    public String getCliente() {
        return cliente;
    }

    /**
     * Cliente.
     * @param cliente cliente to set
     */
    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

}
