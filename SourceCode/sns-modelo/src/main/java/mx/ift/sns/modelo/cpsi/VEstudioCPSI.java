package mx.ift.sns.modelo.cpsi;

import java.io.Serializable;

/**
 * Contiene la información del Estudio de Códigos CPSI.
 * @author X50880SA
 */
public class VEstudioCPSI implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Estudio de CPSI por Proveedor. */
    private Linea1EstudioCPSI linea1EstudioCPSI;

    /** Estudio de CPSI general. */
    private Linea2EstudioCPSI linea2EstudioCPSI;

    /**
     * Se muestra o no la primera linea del estudio.
     */
    private boolean mostrarLinea1;

    /** Constructor, por defecto vacío. */
    public VEstudioCPSI() {
    }

    // GETTERS & SETTERS

    /**
     * Estudio de CPSI por Proveedor.
     * @return Linea1EstudioCPSI
     */
    public Linea1EstudioCPSI getLinea1EstudioCPSI() {
        return linea1EstudioCPSI;
    }

    /**
     * Estudio de CPSI por Proveedor.
     * @param linea1EstudioCPSI Linea1EstudioCPSI
     */
    public void setLinea1EstudioCPSI(Linea1EstudioCPSI linea1EstudioCPSI) {
        this.linea1EstudioCPSI = linea1EstudioCPSI;
    }

    /**
     * Estudio de CPSI general.
     * @return Linea2EstudioCPSI
     */
    public Linea2EstudioCPSI getLinea2EstudioCPSI() {
        return linea2EstudioCPSI;
    }

    /**
     * Estudio de CPSI general.
     * @param linea2EstudioCPSI Linea2EstudioCPSI
     */
    public void setLinea2EstudioCPSI(Linea2EstudioCPSI linea2EstudioCPSI) {
        this.linea2EstudioCPSI = linea2EstudioCPSI;
    }

    /**
     * Se muestra o no la primera linea del estudio.
     * @return boolean
     */
    public boolean isMostrarLinea1() {
        return mostrarLinea1;
    }

    /**
     * Se muestra o no la primera linea del estudio.
     * @param mostrarLinea1 indica si se va a mostrar la primera línea del estudio
     */
    public void setMostrarLinea1(boolean mostrarLinea1) {
        this.mostrarLinea1 = mostrarLinea1;
    }
}
