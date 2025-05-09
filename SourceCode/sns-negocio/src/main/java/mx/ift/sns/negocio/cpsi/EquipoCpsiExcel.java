package mx.ift.sns.negocio.cpsi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Clase que almacena un registro de equipos de señalización y su resultado de validación. */
public class EquipoCpsiExcel implements Serializable {

    /** Serializable. */
    private static final long serialVersionUID = 1L;
    /** Posicion en el excel de importación. */
    public static final int POS_REGISTRO = 0;
    /** Posicion en el excel de importación. */
    public static final int POS_ABN = 1;
    /** Posicion en el excel de importación. */
    public static final int POS_NOM_LOCALIDAD = 2;
    /** Posicion en el excel de importación. */
    public static final int POS_COD_LOCALIDAD = 3;
    /** Posicion en el excel de importación. */
    public static final int POS_NOM_MUNICIPIO = 4;
    /** Posicion en el excel de importación. */
    public static final int POS_COD_MUNICIPIO = 5;
    /** Posicion en el excel de importación. */
    public static final int POS_NOM_ESTADO = 6;
    /** Posicion en el excel de importación. */
    public static final int POS_COD_ESTADO = 7;
    /** Posicion en el excel de importación. */
    public static final int POS_LONGITUD = 8;
    /** Posicion en el excel de importación. */
    public static final int POS_LATITUD = 9;
    /** Posicion en el excel de importación. */
    public static final int POS_NOMBRE = 10;
    /** Posicion en el excel de importación. */
    public static final int POS_CLAVE = 11;
    /** Posicion en el excel de importación. */
    public static final int POS_TIPO = 12;
    /** Posicion en el excel de importación. */
    public static final int POS_TIPO_CPS = 13;
    /** Posicion en el excel de importación. */
    public static final int POS_CPS = 14;

    /** Valor de las columnas del registro. */
    private List<String> columnas;
    /** Flag que indica si el registro ha de ser procesado o no. */
    private boolean procesarRegistro;
    /** Listado de errores/warnings de todas las columnas del registro. */
    private List<ErrorRegistro> erroresValidacion;

    /** Constructor. */
    public EquipoCpsiExcel() {
        procesarRegistro = true;
    }

    /**
     * Constructo con campos.
     * @param columnas del equipo de señalización
     * @param procesarRegistro true or false
     * @param erroresValidacion errores o warnings del registro
     */
    public EquipoCpsiExcel(List<String> columnas, boolean procesarRegistro, List<ErrorRegistro> erroresValidacion) {
        this.columnas = columnas;
        this.procesarRegistro = procesarRegistro;
        this.erroresValidacion = erroresValidacion;
    }

    /**
     * @return the columnas
     */
    public List<String> getColumnas() {
        return columnas;
    }

    /**
     * @param columnas the columnas to set
     */
    public void setColumnas(List<String> columnas) {
        this.columnas = columnas;
    }

    /**
     * @return the procesarRegistro
     */
    public boolean isProcesarRegistro() {
        return procesarRegistro;
    }

    /**
     * @param procesarRegistro the procesarRegistro to set
     */
    public void setProcesarRegistro(boolean procesarRegistro) {
        this.procesarRegistro = procesarRegistro;
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
     * Método encargado de añadir el valor de una columna al registro.
     * @param valor de la columna
     */
    public void addColumna(String valor) {
        if (this.columnas == null) {
            this.columnas = new ArrayList<String>();
        }

        this.columnas.add(valor);
    }

    /**
     * Método encargado de añadir un error o warning al registro.
     * @param error o warning
     */
    public void addErrorValidacion(ErrorRegistro error) {
        if (this.erroresValidacion == null) {
            this.erroresValidacion = new ArrayList<ErrorRegistro>();
        }

        this.erroresValidacion.add(error);
        this.procesarRegistro = (ErrorRegistro.TIPO_AVISO_ERROR.equals(error.getTipoAviso())) ? false
                : this.procesarRegistro;
    }
}
