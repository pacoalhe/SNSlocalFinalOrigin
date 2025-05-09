package mx.ift.sns.modelo.cpsi;

import java.io.Serializable;

/** Bean encargado de almacenar los datos relativos al estudio de equipos de señalización CPSN. */
public class EstudioEquipoCpsi implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Número de códigos utilizados. */
    private Integer numUtilizados;

    /** Número de códigos asignados. */
    private Integer numAsignados;

    // /** Porcentaje de utilización. */
    // private String porcentaje;

    /** Constructor. */
    public EstudioEquipoCpsi() {
        numUtilizados = 0;
        numAsignados = 0;
        // porcentaje = null;
    }

    /**
     * Número de códigos utilizados.
     * @return the numUtilizados
     */
    public Integer getNumUtilizados() {
        return numUtilizados;
    }

    /**
     * Número de códigos utilizados.
     * @param numUtilizados the numUtilizados to set
     */
    public void setNumUtilizados(Integer numUtilizados) {
        this.numUtilizados = numUtilizados;
    }

    /**
     * Método que añade un valor a numUtilizados.
     * @param valor a sumar
     */
    public void addNumUtilizados(Integer valor) {
        this.numUtilizados = numUtilizados + valor;
    }

    /**
     * Constructor.
     * @return the numAsignados
     */
    public Integer getNumAsignados() {
        return numAsignados;
    }

    /**
     * Constructor.
     * @param numAsignados the numAsignados to set
     */
    public void setNumAsignados(Integer numAsignados) {
        this.numAsignados = numAsignados;
    }

    /**
     * Método que añade un valor a numAsignados.
     * @param valor a sumar
     */
    public void addNumAsignados(Integer valor) {
        this.numAsignados = numAsignados + valor;
    }

    /**
     * @return the porcentaje
     */
    public String getPorcentaje() {
        return (numAsignados == 0 || numUtilizados == 0)
                ? "0 %" : String.format("%.2f", numUtilizados * 100.0 / numAsignados) + " %";
    }

    // /**
    // * @param porcentaje the porcentaje to set
    // */
    // public void setPorcentaje(String porcentaje) {
    // this.porcentaje = porcentaje;
    // }

}
