package mx.ift.sns.negocio.consultas.model;

/** Informacion sobre la numeracion de mexico. */

public class MexicoInfo {

    /** valor de la clave de larga distancia. */
    private String nir;

    /** nombre del operador local. */
    private String operadorLocal;

    /** cantidad de numeros asignados. */
    private String numeracionAsignada;

    /** valor de la clave inegi. */
    private String inegi;

    /**
     * @return the nir
     */
    public String getNir() {
        return nir;
    }

    /**
     * @param nir the nir to set
     */
    public void setNir(String nir) {
        this.nir = nir;
    }

    /**
     * @return the operadorLocal
     */
    public String getOperadorLocal() {
        return operadorLocal;
    }

    /**
     * @param operadorLocal the operadorLocal to set
     */
    public void setOperadorLocal(String operadorLocal) {
        this.operadorLocal = operadorLocal;
    }

    /**
     * @return the numeracionAsignada
     */
    public String getNumeracionAsignada() {
        return numeracionAsignada;
    }

    /**
     * @param numeracionAsignada the numeracionAsignada to set
     */
    public void setNumeracionAsignada(String numeracionAsignada) {
        this.numeracionAsignada = numeracionAsignada;
    }

    /**
     * @return the inegi
     */
    public String getInegi() {
        return inegi;
    }

    /**
     * @param inegi the inegi to set
     */
    public void setInegi(String inegi) {
        this.inegi = inegi;
    }

}
