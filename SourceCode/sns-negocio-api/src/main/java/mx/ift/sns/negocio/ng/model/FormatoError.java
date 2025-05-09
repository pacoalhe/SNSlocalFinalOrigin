package mx.ift.sns.negocio.ng.model;

import java.io.Serializable;

/** Bean encargado de almacenar el registro de carga de arrendamientos que contiene un error de formato. */
public class FormatoError implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** NumberFrom. */
    private String numberFrom;

    /** NumberTo. */
    private String numberTo;

    /** Ido Arrendatario. */
    private String idoArrendatario;

    /** Ido Arrendador. */
    private String idoArrendador;

    /** Nombre Archivo. */
    private String nombreFichero;

    /** Constructor por defecto. */
    public FormatoError() {
    }

    /**
     * Constructor con los campos.
     * @param numberFrom del rango
     * @param numberTo del rango
     * @param idoArrendatario arrendatario
     * @param idoArrendador arrendador
     * @param nombreFichero de carga
     */
    public FormatoError(String numberFrom, String numberTo, String idoArrendatario, String idoArrendador,
            String nombreFichero) {
        this.numberFrom = numberFrom;
        this.numberTo = numberTo;
        this.idoArrendatario = idoArrendatario;
        this.idoArrendador = idoArrendador;
        this.nombreFichero = nombreFichero;
    }

    /**
     * NumberFrom.
     * @return the numberFrom
     */
    public String getNumberFrom() {
        return numberFrom;
    }

    /**
     * NumberFrom.
     * @param numberFrom the numberFrom to set
     */
    public void setNumberFrom(String numberFrom) {
        this.numberFrom = numberFrom;
    }

    /**
     * NumberTo.
     * @return the numberTo
     */
    public String getNumberTo() {
        return numberTo;
    }

    /**
     * NumberTo.
     * @param numberTo the numberTo to set
     */
    public void setNumberTo(String numberTo) {
        this.numberTo = numberTo;
    }

    /**
     * Ido Arrendatario.
     * @return the idoArrendatario
     */
    public String getIdoArrendatario() {
        return idoArrendatario;
    }

    /**
     * Ido Arrendatario.
     * @param idoArrendatario the idoArrendatario to set
     */
    public void setIdoArrendatario(String idoArrendatario) {
        this.idoArrendatario = idoArrendatario;
    }

    /**
     * Ido Arrendador.
     * @return the idoArrendador
     */
    public String getIdoArrendador() {
        return idoArrendador;
    }

    /**
     * Ido Arrendador.
     * @param idoArrendador the idoArrendador to set
     */
    public void setIdoArrendador(String idoArrendador) {
        this.idoArrendador = idoArrendador;
    }

    /**
     * Nombre Archivo.
     * @return the nombreFichero
     */
    public String getNombreFichero() {
        return nombreFichero;
    }

    /**
     * Nombre Archivo.
     * @param nombreFichero the nombreFichero to set
     */
    public void setNombreFichero(String nombreFichero) {
        this.nombreFichero = nombreFichero;
    }

}
