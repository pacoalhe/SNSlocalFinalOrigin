package mx.ift.sns.negocio.consultas.model;

/** Informacion del area del servicio local para un numero consultado. */
public class ServicioLocalInfo {

    /** valor de la clave de larga distancia. */
    private String nir;

    /** numero de digitos del numero local. */
    private String longitudNumeroLocal;

    /** marcacion local a celular. */
    private String marcacionLocalToCelular;

    /** marcacion nacional. */
    private String marcacionNacional;

    /** marcacion para estados unidos. */
    private String marcacionUsa;

    /** marcacion para el resto del mundo. */
    private String marcacionRestoMundo;

    /** nombre de la ciudad con mayor marcacion asignada. */
    private String ciudadConMayorMarcacionAsignada;

    /** numero de poblaciones con servicio de telefonoia local. */
    private String poblacionesServicioLocal;

    /** numero de municipios que forman el area. */
    private String municipiosArea;

    /** operador que presta el servicio te telefonia local. */
    private String operadorServicioLocal;

    /** muestra si el numero tiene presuscripcion a larga distancia. */
    private boolean presuscripcionLargaDistancia;

    /** cantidad de numeracion asignada. */
    private String numeracionAsignada;

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
     * @return the longitudNumeroLocal
     */
    public String getLongitudNumeroLocal() {
        return longitudNumeroLocal;
    }

    /**
     * @param longitudNumeroLocal the longitudNumeroLocal to set
     */
    public void setLongitudNumeroLocal(String longitudNumeroLocal) {
        this.longitudNumeroLocal = longitudNumeroLocal;
    }

    /**
     * @return the marcacionLocalToCelular
     */
    public String getMarcacionLocalToCelular() {
        return marcacionLocalToCelular;
    }

    /**
     * @param marcacionLocalToCelular the marcacionLocalToCelular to set
     */
    public void setMarcacionLocalToCelular(String marcacionLocalToCelular) {
        this.marcacionLocalToCelular = marcacionLocalToCelular;
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
     * @return the ciudadConMayorMarcacionAsignada
     */
    public String getCiudadConMayorMarcacionAsignada() {
        return ciudadConMayorMarcacionAsignada;
    }

    /**
     * @param ciudadConMayorMarcacionAsignada the ciudadConMayorMarcacionAsignada to set
     */
    public void setCiudadConMayorMarcacionAsignada(String ciudadConMayorMarcacionAsignada) {
        this.ciudadConMayorMarcacionAsignada = ciudadConMayorMarcacionAsignada;
    }

    /**
     * @return the poblacionesServicioLocal
     */
    public String getPoblacionesServicioLocal() {
        return poblacionesServicioLocal;
    }

    /**
     * @param poblacionesServicioLocal the poblacionesServicioLocal to set
     */
    public void setPoblacionesServicioLocal(String poblacionesServicioLocal) {
        this.poblacionesServicioLocal = poblacionesServicioLocal;
    }

    /**
     * @return the municipiosArea
     */
    public String getMunicipiosArea() {
        return municipiosArea;
    }

    /**
     * @param municipiosArea the municipiosArea to set
     */
    public void setMunicipiosArea(String municipiosArea) {
        this.municipiosArea = municipiosArea;
    }

    /**
     * @return the operadorServicioLocal
     */
    public String getOperadorServicioLocal() {
        return operadorServicioLocal;
    }

    /**
     * @param operadorServicioLocal the operadorServicioLocal to set
     */
    public void setOperadorServicioLocal(String operadorServicioLocal) {
        this.operadorServicioLocal = operadorServicioLocal;
    }

    /**
     * @return the presuscripcionLargaDistancia
     */
    public boolean getPresuscripcionLargaDistancia() {
        return presuscripcionLargaDistancia;
    }

    /**
     * @param presuscripcionLargaDistancia the presuscripcionLargaDistancia to set
     */
    public void setPresuscripcionLargaDistancia(boolean presuscripcionLargaDistancia) {
        this.presuscripcionLargaDistancia = presuscripcionLargaDistancia;
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

}
