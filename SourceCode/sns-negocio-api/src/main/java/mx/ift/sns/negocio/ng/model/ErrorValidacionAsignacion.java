package mx.ift.sns.negocio.ng.model;

import java.io.Serializable;
import java.util.List;

import mx.ift.sns.modelo.ng.NumeracionSolicitada;

/**
 * Clase que almacenará los errores.
 */
public class ErrorValidacionAsignacion extends ErrorValidacion implements Serializable {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * constructor.
     */
    public ErrorValidacionAsignacion() {
    }

    /**
     * Número de solicitud.
     */
    private NumeracionSolicitada numSoli;

    /**
     * lista.
     */
    private List<AvisoValidacionCentral> listaAviso;

    /**
     * lista de avisos que se pueden o no resolver.
     */
    private List<AvisoValidacionCentral> listaAvisoNoObligatorios;

    /**
     * @return the numSoli
     */
    public NumeracionSolicitada getNumSoli() {
        return numSoli;
    }

    /**
     * @param numSoli the numSoli to set
     */
    public void setNumSoli(NumeracionSolicitada numSoli) {
        this.numSoli = numSoli;
    }

    /**
     * @return the listaAviso
     */
    public List<AvisoValidacionCentral> getListaAviso() {
        return listaAviso;
    }

    /**
     * @param listaAviso the listaAviso to set
     */
    public void setListaAviso(List<AvisoValidacionCentral> listaAviso) {
        this.listaAviso = listaAviso;
    }

    /**
     * @return the listaAvisoNoObligatorios
     */
    public List<AvisoValidacionCentral> getListaAvisoNoObligatorios() {
        return listaAvisoNoObligatorios;
    }

    /**
     * @param listaAvisoNoObligatorios the listaAvisoNoObligatorios to set
     */
    public void setListaAvisoNoObligatorios(List<AvisoValidacionCentral> listaAvisoNoObligatorios) {
        this.listaAvisoNoObligatorios = listaAvisoNoObligatorios;
    }

}
