package mx.ift.sns.negocio.consultas.model;

/** Informacion sobre un numero consultado publicamente. */

public class NumeroConsultadoInfo {

    /** clave de larga distancia. */
    private String nir;

    /** poblacion a la que pertenece. */
    private String poblacion;

    /** marcacion local. */
    private String marcacionLocal;

    /** marcacion nacional. */
    private String marcacionNacional;

    /** marcacion para estados unidos. */
    private String marcacionUsa;

    /** marcacion para el resto del mundo. */
    private String marcacionRestoMundo;

    /** operador que presta el servicio. */
    private String operador;

    /** direccion, representate legal y datos de numeracion. */
    private String datosNumeracion;

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
     * @return the poblacion
     */
    public String getPoblacion() {
        return poblacion;
    }

    /**
     * @param poblacion the poblacion to set
     */
    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * @return the marcacionLocal
     */
    public String getMarcacionLocal() {
        return marcacionLocal;
    }

    /**
     * @param marcacionLocal the marcacionLocal to set
     */
    public void setMarcacionLocal(String marcacionLocal) {
        this.marcacionLocal = marcacionLocal;
    }

    /**
     * @return the marcacionNacional
     */
    public String getMarcacionNacional() {
        return marcacionNacional;
    }

    /**
     * @param marcacionNacional the marcacionNacional to set
     */
    public void setMarcacionNacional(String marcacionNacional) {
        this.marcacionNacional = marcacionNacional;
    }

    /**
     * @return the marcacionUsa
     */
    public String getMarcacionUsa() {
        return marcacionUsa;
    }

    /**
     * @param marcacionUsa the marcacionUsa to set
     */
    public void setMarcacionUsa(String marcacionUsa) {
        this.marcacionUsa = marcacionUsa;
    }

    /**
     * @return the marcacionRestoMundo
     */
    public String getMarcacionRestoMundo() {
        return marcacionRestoMundo;
    }

    /**
     * @param marcacionRestoMundo the marcacionRestoMundo to set
     */
    public void setMarcacionRestoMundo(String marcacionRestoMundo) {
        this.marcacionRestoMundo = marcacionRestoMundo;
    }

    /**
     * @return the operador
     */
    public String getOperador() {
        return operador;
    }

    /**
     * @param operador the operador to set
     */
    public void setOperador(String operador) {
        this.operador = operador;
    }

    /**
     * @return the datosNumeracion
     */
    public String getDatosNumeracion() {
        return datosNumeracion;
    }

    /**
     * @param datosNumeracion the datosNumeracion to set
     */
    public void setDatosNumeracion(String datosNumeracion) {
        this.datosNumeracion = datosNumeracion;
    }

}
