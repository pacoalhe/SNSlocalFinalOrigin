package mx.ift.sns.web.backend.ac.abn;

import java.io.Serializable;

/** Mantiene información de la selección de un Municipio en tablas PrimeFaces. */
public class SeleccionMunicipio implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Código de Municipio. */
    private String cdgMunicipio;

    /** Código de Estado. */
    private String cdgEstado;

    /** Nombre de Numinicipio. */
    private String nombre;

    /** Número de poblaciones seleccionadas en el Municipio. */
    private int poblacionesSeleccionadas;

    /** Número de poblaciones totales en el municipio. */
    private int poblacionesMunicipio;

    /** Indica si el municipio ha sido seleccionado. */
    private boolean seleccionado = false;

    /** Indica si el municipio es seleccionable. */
    private boolean seleccionable = true;

    /** Índice de correspondencia en la lista de municipios. */
    private int index;

    /**
     * Método invocado por PrimeFaces para obtener la ocupación de un Municipio.
     * @return Literal de Ocupación del Municipio.
     */
    public String getOcupacion() {
        StringBuilder sbOcupacion = new StringBuilder();
        sbOcupacion.append(poblacionesSeleccionadas).append(" / ");
        if (poblacionesMunicipio != 0) {
            sbOcupacion.append(poblacionesMunicipio);
        } else {
            sbOcupacion.append("-");
        }
        return sbOcupacion.toString();
    }

    @Override
    public boolean equals(Object pOther) {
        if (pOther instanceof SeleccionMunicipio) {
            SeleccionMunicipio selMun = (SeleccionMunicipio) pOther;
            return (this.cdgMunicipio.equals(selMun.getCdgMunicipio())
            && this.cdgEstado.equals(selMun.getCdgEstado()));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        // Redefinimos HashCode para que devuelva el mismo valor cuando se tiene el mismo código de Municipio
        // al almacenar/obtener de las HashMaps.
        return this.getClass().hashCode() + cdgMunicipio.hashCode() + cdgEstado.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("municipio={").append(nombre).append("}");
        return sBuilder.toString();
    }

    // GETTERS & SETTERS

    /**
     * Nombre de Numinicipio.
     * @return String
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Nombre de Numinicipio.
     * @param nombre String
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Indica si el municipio ha sido seleccionado.
     * @return boolean
     */
    public boolean isSeleccionado() {
        return seleccionado;
    }

    /**
     * Indica si el municipio ha sido seleccionado.
     * @param seleccionado boolean
     */
    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    /**
     * Código de Municipio.
     * @return String
     */
    public String getCdgMunicipio() {
        return cdgMunicipio;
    }

    /**
     * Código de Municipio.
     * @param cdgMunicipio String
     */
    public void setCdgMunicipio(String cdgMunicipio) {
        this.cdgMunicipio = cdgMunicipio;
    }

    /**
     * Código de Estado.
     * @return String
     */
    public String getCdgEstado() {
        return cdgEstado;
    }

    /**
     * Código de Estado.
     * @param cdgEstado String
     */
    public void setCdgEstado(String cdgEstado) {
        this.cdgEstado = cdgEstado;
    }

    /**
     * Número de poblaciones seleccionadas en el Municipio.
     * @return int
     */
    public int getPoblacionesSeleccionadas() {
        return poblacionesSeleccionadas;
    }

    /**
     * Número de poblaciones seleccionadas en el Municipio.
     * @param poblacionesSeleccionadas int
     */
    public void setPoblacionesSeleccionadas(int poblacionesSeleccionadas) {
        this.poblacionesSeleccionadas = poblacionesSeleccionadas;
    }

    /**
     * Número de poblaciones totales en el municipio.
     * @return int poblacionesMunicipio
     */
    public int getPoblacionesMunicipio() {
        return poblacionesMunicipio;
    }

    /**
     * Número de poblaciones totales en el municipio.
     * @param poblacionesMunicipio int poblacionesMunicipio
     */
    public void setPoblacionesMunicipio(int poblacionesMunicipio) {
        this.poblacionesMunicipio = poblacionesMunicipio;
    }

    /**
     * Índice de correspondencia en la lista de Municipios.
     * @param index int
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Índice de correspondencia en la lista de Municipios.
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Indica si el municipio es seleccionable.
     * @return boolean
     */
    public boolean isSeleccionable() {
        return seleccionable;
    }

    /**
     * Indica si el municipio es seleccionable.
     * @param seleccionable boolean
     */
    public void setSeleccionable(boolean seleccionable) {
        this.seleccionable = seleccionable;
    }
}
