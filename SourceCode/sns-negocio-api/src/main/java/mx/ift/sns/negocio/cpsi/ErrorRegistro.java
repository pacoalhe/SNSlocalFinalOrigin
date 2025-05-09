package mx.ift.sns.negocio.cpsi;

import java.io.Serializable;

/** Clase que almacena el resultado de la validación de un registro de equipos de señalización. */
public class ErrorRegistro implements Serializable {

    /** Serializable. */
    private static final long serialVersionUID = 1L;

    /** Constantes para definir el tipo de error. */
    public static final String TIPO_AVISO_WARN = "Warning";
    /** Constantes para definir el tipo de error. */
    public static final String TIPO_AVISO_ERROR = "Error";
    /** tipo del error. */
    private String tipoAviso;
    /** Nº registro del error. */
    private String numRegistro;
    /** Columna del error. */
    private int columna;
    /** Detalle del error. */
    private String descripcion;
    /** Cps en binario. */
    private String cps;

    /** Constructor. */
    public ErrorRegistro() {
    }

    /**
     * Constructo con los campos.
     * @param numRegistro del registro.
     * @param tipoAviso del error.
     * @param columna del error.
     * @param cps en binario
     * @param descripcion del error.
     */
    public ErrorRegistro(String numRegistro, String tipoAviso, int columna, String cps, String descripcion) {
        this.numRegistro = numRegistro;
        this.tipoAviso = tipoAviso;
        this.columna = columna;
        this.cps = cps;
        this.descripcion = descripcion;
    }

    /**
     * @return the tipoAviso
     */
    public String getTipoAviso() {
        return tipoAviso;
    }

    /**
     * @param tipoAviso the tipoAviso to set
     */
    public void setTipoAviso(String tipoAviso) {
        this.tipoAviso = tipoAviso;
    }

    /**
     * @return the numRegistro
     */
    public String getNumRegistro() {
        return numRegistro;
    }

    /**
     * @param numRegistro the numRegistro to set
     */
    public void setNumRegistro(String numRegistro) {
        this.numRegistro = numRegistro;
    }

    /**
     * @return the columna
     */
    public int getColumna() {
        return columna;
    }

    /**
     * @param columna the columna to set
     */
    public void setColumna(int columna) {
        this.columna = columna;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the cps
     */
    public String getCps() {
        return cps;
    }

    /**
     * @param cps the cps to set
     */
    public void setCps(String cps) {
        this.cps = cps;
    }

}
