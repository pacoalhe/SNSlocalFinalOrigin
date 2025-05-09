package mx.ift.sns.negocio;

import java.io.Serializable;

/** Contiene información relativa a la petición de cancelación sobre una Solicitud. */
public class PeticionCancelacion implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Indica si es posible cancelar una solicitud. */
    private boolean cancelacionPosible = false;

    /** Indica el motivo por el cual no se ha podido realizar la cancelación. */
    private String mensajeError = "";

    /**
     * Indica si es posible cancelar una solicitud.
     * @return boolean
     */
    public boolean isCancelacionPosible() {
        return cancelacionPosible;
    }

    /**
     * Indica si es posible cancelar una solicitud.
     * @param cancelacionPosible boolean
     */
    public void setCancelacionPosible(boolean cancelacionPosible) {
        this.cancelacionPosible = cancelacionPosible;
    }

    /**
     * Indica el motivo por el cual no se ha podido realizar la cancelación.
     * @return String
     */
    public String getMensajeError() {
        return mensajeError;
    }

    /**
     * Indica el motivo por el cual no se ha podido realizar la cancelación.
     * @param mensajeError String
     */
    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }
}
