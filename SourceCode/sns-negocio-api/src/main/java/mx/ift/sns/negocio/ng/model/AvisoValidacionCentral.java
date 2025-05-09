package mx.ift.sns.negocio.ng.model;

import java.io.Serializable;

import mx.ift.sns.modelo.central.Central;

/**
 * Clase que almacenará los errores.
 */
public class AvisoValidacionCentral extends ErrorValidacion implements Serializable {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * constructor.
     */
    public AvisoValidacionCentral() {
    }

    /**
     * Central.
     */
    private Central central;

    /**
     * @return the central
     */
    public Central getCentral() {
        return central;
    }

    /**
     * @param central the central to set
     */
    public void setCentral(Central central) {
        this.central = central;
    }

}
