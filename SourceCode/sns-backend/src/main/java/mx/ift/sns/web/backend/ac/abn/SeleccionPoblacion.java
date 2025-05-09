package mx.ift.sns.web.backend.ac.abn;

import java.io.Serializable;

/** Mantiene información de la selección de una población en tablas PrimeFaces. */
public class SeleccionPoblacion implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Indica si la población seleccionada es población Ancla del ABN. */
    private boolean poblacionAncla;

    /** Inegi de la Población. */
    private String inegi;

    /** Nombre de la Población. */
    private String nombre;

    /** Código de Población. */
    private String cdgPoblacion;

    /** Nombre del Municipio al que pertenece la población. */
    private String municipio;

    /** Índice de correspondencia en la lista de poblaciones. */
    private int index;

    /** Indica si la población está seleccionada. */
    private boolean seleccionada;

    /** Indica si la población es seleccionable. */
    private boolean seleccionable = true;

    /** ABN al que pertenece la población. */
    private String abn;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SeleccionPoblacion) {
            return (this.inegi.equals(((SeleccionPoblacion) obj).getInegi()));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        // Redefinimos HashCode para que devuelva el mismo valor cuando se tiene el mismo código de población
        // al almacenar/obtener de las HashMaps.
        return this.getClass().hashCode() + cdgPoblacion.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("poblacion={").append(nombre).append("}");
        return sBuilder.toString();
    }

    // GETTERS & SETTERS

    /**
     * Inegi de la población.
     * @return String
     */
    public String getInegi() {
        return inegi;
    }

    /**
     * Inegi de la población.
     * @param inegi String
     */
    public void setInegi(String inegi) {
        this.inegi = inegi;
    }

    /**
     * Nombre de la población.
     * @return String
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Nombre de la población.
     * @param nombre String
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Nombre del Municipio al que pertenece la población.
     * @return String
     */
    public String getMunicipio() {
        return municipio;
    }

    /**
     * Nombre del Municipio al que pertenece la población.
     * @param municipio String
     */
    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    /**
     * Código de Población.
     * @return String cdgPoblacion
     */
    public String getCdgPoblacion() {
        return cdgPoblacion;
    }

    /**
     * Código de Población.
     * @param cdgPoblacion String cdgPoblacion
     */
    public void setCdgPoblacion(String cdgPoblacion) {
        this.cdgPoblacion = cdgPoblacion;
    }

    /**
     * Indica si la población seleccionada es población Ancla del ABN.
     * @return boolean
     */
    public boolean isPoblacionAncla() {
        return poblacionAncla;
    }

    /**
     * Indica si la población seleccionada es población Ancla del ABN.
     * @param poblacionAncla boolean
     */
    public void setPoblacionAncla(boolean poblacionAncla) {
        this.poblacionAncla = poblacionAncla;
    }

    /**
     * Índice de correspondencia en la lista de poblaciones.
     * @return int
     */
    public int getIndex() {
        return index;
    }

    /**
     * Índice de correspondencia en la lista de poblaciones.
     * @param index int
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Indica si la población está seleccionada.
     * @return boolean
     */
    public boolean isSeleccionada() {
        return seleccionada;
    }

    /**
     * Indica si la población está seleccionada.
     * @param seleccionada boolean
     */
    public void setSeleccionada(boolean seleccionada) {
        this.seleccionada = seleccionada;
    }

    /**
     * Indica si la población es seleccionable.
     * @return boolean
     */
    public boolean isSeleccionable() {
        return seleccionable;
    }

    /**
     * Indica si la población es seleccionable.
     * @param seleccionable boolean
     */
    public void setSeleccionable(boolean seleccionable) {
        this.seleccionable = seleccionable;
    }

    /**
     * ABN al que pertenece la población.
     * @return String
     */
    public String getAbn() {
        return abn;
    }

    /**
     * ABN al que pertenece la población.
     * @param abn String
     */
    public void setAbn(String abn) {
        this.abn = abn;
    }
}
