package mx.ift.sns.negocio.cpsi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de almacenar el resultado del procesamiento de los equipos de señalización internacional. Para ello
 * almacena en distintos listados los siguientes datos:
 * <ul>
 * <li>Nombre del Fichero de Carga</li>
 * <li>Cabecera del fichero Excel</li>
 * <li>Listado de equipos de señalización</li>
 * <li>Número de equipos leidos</li>
 * <li>Llistado de equipos procesados con algún error</li>
 * <li>Listado de equipos procesados sin error</li>
 * <li>Listado de errores y warnings de todos los equipos procesados</li>
 * </ul>
 */
public class CargaEquiposExcel implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;
    /** Nombre del fichero excel a procesar. */
    private String nombreFichero;
    /** Listado de lineas referentes a la cabecera del documento. */
    private List<EquipoCpsiExcel> cabecera;
    /** Listado de líneas referentes a los equipos de señalización internacional. */
    private List<EquipoCpsiExcel> equiposExcel;
    /** Número total de equipos leidos. */
    private int numEquiposLeidos;
    /** Listado de líneas referentes a los equipos de señalización erróneos. */
    private List<EquipoCpsiExcel> equiposExcelErroneos;
    /** Listado de líneas referentes a los equipos de señalización válidos. */
    private List<EquipoCpsiExcel> equiposExcelValidos;

    /** Listado de errores/warnings de todos los equipos del fichero excel procesados. */
    private List<ErrorRegistro> erroresValidacion;

    /** Constructor vacío. */
    public CargaEquiposExcel() {
    }

    /**
     * Método que devuelve el listado de errores de validación al procesar el fichero excel.
     * @return the erroresValidacion
     */
    public List<ErrorRegistro> getErroresValidacion() {
        return erroresValidacion;
    }

    /**
     * Método encargado de establecer el listado de errores de validación.
     * @param erroresValidacion the erroresValidacion to set
     */
    public void setErroresValidacion(List<ErrorRegistro> erroresValidacion) {
        this.erroresValidacion = erroresValidacion;
    }

    /**
     * Método encargado de añadir un registro al listado de cabecera.
     * @param registro a añadir
     */
    public void addCabecera(EquipoCpsiExcel registro) {
        if (this.cabecera == null) {
            this.cabecera = new ArrayList<EquipoCpsiExcel>();
        }

        this.cabecera.add(registro);
    }

    /**
     * Método encargado de añadir un equipo al listado de equipos.
     * @param equipo a añadir
     */
    public void addRegistro(EquipoCpsiExcel equipo) {
        if (this.equiposExcel == null) {
            this.equiposExcel = new ArrayList<EquipoCpsiExcel>();
        }

        this.equiposExcel.add(equipo);
    }

    /**
     * Método encargado de añadir un equipo al listado de equipos procesados sin errores. Además, añade al listado de
     * errores general, el listado de warnings generados al procesar dicho equipo.
     * @param equipo a añadir
     */
    public void addRegistroValido(EquipoCpsiExcel equipo) {
        if (this.equiposExcelValidos == null) {
            this.equiposExcelValidos = new ArrayList<EquipoCpsiExcel>();
        }

        this.equiposExcelValidos.add(equipo);
        if (equipo.getErroresValidacion() != null) {
            addErroresValidacion(equipo.getErroresValidacion());
        }
    }

    /**
     * Método encargado de añadir un equipo al listado de equipos procesados con errores. Además, añade al listado de
     * errores general, el listado de warnings y errores generados al procesar dicho equipo.
     * @param equipo a añadir
     */
    public void addRegistroErroneo(EquipoCpsiExcel equipo) {
        if (this.equiposExcelErroneos == null) {
            this.equiposExcelErroneos = new ArrayList<EquipoCpsiExcel>();
        }

        this.equiposExcelErroneos.add(equipo);
        if (equipo.getErroresValidacion() != null) {
            addErroresValidacion(equipo.getErroresValidacion());
        }
    }

    /**
     * Método encargado de añadir los errores o warnings generados al procesar un equipo al listado general de errores y
     * warnings.
     * @param errores o warnings
     */
    public void addErroresValidacion(List<ErrorRegistro> errores) {
        if (this.erroresValidacion == null) {
            this.erroresValidacion = new ArrayList<ErrorRegistro>();
        }

        this.erroresValidacion.addAll(errores);
    }

    /**
     * Método que devuelve el listado de registros de la cabecera del fichero excel de importación de equipos.
     * @return the cabecera
     */
    public List<EquipoCpsiExcel> getCabecera() {
        return cabecera;
    }

    /**
     * Método que establece el listado de registros de cabecera del fichero excel de importación de equipos.
     * @param cabecera the cabecera to set
     */
    public void setCabecera(List<EquipoCpsiExcel> cabecera) {
        this.cabecera = cabecera;
    }

    /**
     * Método que devuelve el listado de equipos leidos del fichero excel de importación de equipos.
     * @return the equiposExcel
     */
    public List<EquipoCpsiExcel> getEquiposExcel() {
        return equiposExcel;
    }

    /**
     * Método que establece el listado de equipos leidos del fichero excel de importación de equipos.
     * @param equiposExcel the equiposExcel to set
     */
    public void setEquiposExcel(List<EquipoCpsiExcel> equiposExcel) {
        this.equiposExcel = equiposExcel;
    }

    /**
     * Método que devuelve el listado de equipos procesados con errores.
     * @return the equiposExcelErroneos
     */
    public List<EquipoCpsiExcel> getEquiposExcelErroneos() {
        return equiposExcelErroneos;
    }

    /**
     * Método que establece el listado de equipos procesados con errores.
     * @param equiposExcelErroneos the equiposExcelErroneos to set
     */
    public void setEquiposExcelErroneos(List<EquipoCpsiExcel> equiposExcelErroneos) {
        this.equiposExcelErroneos = equiposExcelErroneos;
    }

    /**
     * Método que devuelve el listado de equipos procesados sin errores.
     * @return the equiposExcelValidos
     */
    public List<EquipoCpsiExcel> getEquiposExcelValidos() {
        return equiposExcelValidos;
    }

    /**
     * Método que establece el listado de equipos procesados sin errores.
     * @param equiposExcelValidos the equiposExcelValidos to set
     */
    public void setEquiposExcelValidos(List<EquipoCpsiExcel> equiposExcelValidos) {
        this.equiposExcelValidos = equiposExcelValidos;
    }

    /**
     * Método que devuelve el nombre del fichero excel de importación de equipos.
     * @return the nombreFichero
     */
    public String getNombreFichero() {
        return nombreFichero;
    }

    /**
     * Método que establece el nombre del fichero excel de importación de equipos.
     * @param nombreFichero the nombreFichero to set
     */
    public void setNombreFichero(String nombreFichero) {
        this.nombreFichero = nombreFichero;
    }

    /**
     * Método que devuelve el número de equipos leidos del fichero excel.
     * @return the numEquiposLeidos
     */
    public int getNumEquiposLeidos() {
        return numEquiposLeidos;
    }

    /**
     * Método que establece el número de equipos leidos del fichero excel.
     * @param numEquiposLeidos the numEquiposLeidos to set
     */
    public void setNumEquiposLeidos(int numEquiposLeidos) {
        this.numEquiposLeidos = numEquiposLeidos;
    }

}
