package mx.ift.sns.negocio.cpsi;

import java.io.Serializable;
import java.util.List;

/**
 * Clase que almacena el resultado de la importación de los equipos de señalización recibidos en el fichero excel.
 */
public class DetalleImportacionEquiposCpsi implements Serializable {

    /** Serializable. */
    private static final long serialVersionUID = 1L;

    /** Nobre del archivo de importación. */
    private String nombreFichero;

    /** Número de registros leidos. */
    private int numEquiposLeidos;

    /** Número de registros erróneos. */
    private int numEquiposErroneos;

    /** Número de registros procesados. */
    private int numEquiposProcesados;

    /** Listado de errores/warnings de todas las columnas del registro. */
    private List<ErrorRegistro> erroresValidacion;

    /** Mensaje de error de formato. */
    private String msgError;

    /** Constructor. */
    public DetalleImportacionEquiposCpsi() {
    }

    /**
     * @return the nombreFichero
     */
    public String getNombreFichero() {
        return nombreFichero;
    }

    /**
     * @param nombreFichero the nombreFichero to set
     */
    public void setNombreFichero(String nombreFichero) {
        this.nombreFichero = nombreFichero;
    }

    /**
     * @return the numEquiposLeidos
     */
    public int getNumEquiposLeidos() {
        return numEquiposLeidos;
    }

    /**
     * @param numEquiposLeidos the numEquiposLeidos to set
     */
    public void setNumEquiposLeidos(int numEquiposLeidos) {
        this.numEquiposLeidos = numEquiposLeidos;
    }

    /**
     * @return the numEquiposErroneos
     */
    public int getNumEquiposErroneos() {
        return numEquiposErroneos;
    }

    /**
     * @param numEquiposErroneos the numEquiposErroneos to set
     */
    public void setNumEquiposErroneos(int numEquiposErroneos) {
        this.numEquiposErroneos = numEquiposErroneos;
    }

    /**
     * @return the numEquiposProcesados
     */
    public int getNumEquiposProcesados() {
        return numEquiposProcesados;
    }

    /**
     * @param numEquiposProcesados the numEquiposProcesados to set
     */
    public void setNumEquiposProcesados(int numEquiposProcesados) {
        this.numEquiposProcesados = numEquiposProcesados;
    }

    /**
     * @return the erroresValidacion
     */
    public List<ErrorRegistro> getErroresValidacion() {
        return erroresValidacion;
    }

    /**
     * @param erroresValidacion the erroresValidacion to set
     */
    public void setErroresValidacion(List<ErrorRegistro> erroresValidacion) {
        this.erroresValidacion = erroresValidacion;
    }

    /**
     * @return the msgError
     */
    public String getMsgError() {
        return msgError;
    }

    /**
     * @param msgError the msgError to set
     */
    public void setMsgError(String msgError) {
        this.msgError = msgError;
    }
}
