package mx.ift.sns.negocio.ng.model;

import java.io.Serializable;
import java.util.List;

import mx.ift.sns.modelo.ng.SolicitudAsignacion;

/** RetornoProcesaFichero. */
public class RetornoProcesaFicheroAsignacion implements Serializable {

    /** serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** lErrores. */
    private List<ErrorValidacion> lErrores;
    /** solicitudAsignacion. */
    private SolicitudAsignacion solicitudAsignacion;
    /** lAviso. */
    private List<AvisoValidacionCentral> lAviso;
    /** Aviso especial. */
    private List<AvisoValidacionCentral> lAvisoNoOblig;
    /** Mensaje de error. */
    private String mensajeError;

    /**
     * @return the lErrores
     */
    public List<ErrorValidacion> getlErrores() {
        return lErrores;
    }

    /**
     * @param lErrores the lErrores to set
     */
    public void setlErrores(List<ErrorValidacion> lErrores) {
        this.lErrores = lErrores;
    }

    /**
     * @return the solicitudAsignacion
     */
    public SolicitudAsignacion getSolicitudAsignacion() {
        return solicitudAsignacion;
    }

    /**
     * @param solicitudAsignacion the solicitudAsignacion to set
     */
    public void setSolicitudAsignacion(SolicitudAsignacion solicitudAsignacion) {
        this.solicitudAsignacion = solicitudAsignacion;
    }

    /**
     * @return the lAviso
     */
    public List<AvisoValidacionCentral> getlAviso() {
        return lAviso;
    }

    /**
     * @param lAviso the lAviso to set
     */
    public void setlAviso(List<AvisoValidacionCentral> lAviso) {
        this.lAviso = lAviso;
    }

    /**
     * @return the lAvisoNoOblig
     */
    public List<AvisoValidacionCentral> getlAvisoNoOblig() {
        return lAvisoNoOblig;
    }

    /**
     * @param lAvisoNoOblig the lAvisoNoOblig to set
     */
    public void setlAvisoNoOblig(List<AvisoValidacionCentral> lAvisoNoOblig) {
        this.lAvisoNoOblig = lAvisoNoOblig;
    }

    /**
     * @return the mensajeError
     */
    public String getMensajeError() {
        return mensajeError;
    }

    /**
     * @param mensajeError the mensajeError to set
     */
    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }

}
