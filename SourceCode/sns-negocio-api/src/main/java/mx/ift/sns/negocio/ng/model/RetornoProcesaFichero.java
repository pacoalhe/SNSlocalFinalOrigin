package mx.ift.sns.negocio.ng.model;

import java.io.Serializable;
import java.util.List;

/** RetornoProcesaFichero. */
public class RetornoProcesaFichero implements Serializable {

    /** serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** lErrores. */
    private List<ErrorValidacion> lErrores;

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

}
