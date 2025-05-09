package mx.ift.sns.web.backend.ac.abn;

import java.io.Serializable;

/** Mantiene información de la selección de una Serie en tablas PrimeFaces. */
public class SeleccionSerie implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Código de Nir asociado a la serie. */
    private String cdgNir;

    /** Valor de la primera serie del nir. */
    private String inicioSeries;

    /** Valor de la última serie del nir. */
    private String finalSeries;

    /** Indica si la serie está ya creada. */
    private boolean seleccionada;

    /** Indica si la serie es seleccionable. */
    private boolean seleccionable = true;

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("serie={").append("cdgNir=").append(cdgNir).append(", ");
        sBuilder.append("inicioSeries=").append(inicioSeries).append(", ");
        sBuilder.append("finalSeries=").append(finalSeries).append("}");
        return sBuilder.toString();
    }

    // GETTERS & SETTERS

    /**
     * Código de Nir asociado a la serie.
     * @return String
     */
    public String getCdgNir() {
        return cdgNir;
    }

    /**
     * Código de Nir asociado a la serie.
     * @param cdgNir String
     */
    public void setCdgNir(String cdgNir) {
        this.cdgNir = cdgNir;
    }

    /**
     * Valor de la primera serie del nir.
     * @return String
     */
    public String getInicioSeries() {
        return inicioSeries;
    }

    /**
     * Valor de la primera serie del nir.
     * @param inicioSeries String
     */
    public void setInicioSeries(String inicioSeries) {
        this.inicioSeries = inicioSeries;
    }

    /**
     * Valor de la última serie del nir.
     * @return String
     */
    public String getFinalSeries() {
        return finalSeries;
    }

    /**
     * Valor de la última serie del nir.
     * @param finalSeries String
     */
    public void setFinalSeries(String finalSeries) {
        this.finalSeries = finalSeries;
    }

    /**
     * Indica si la serie es seleccionable.
     * @return boolean
     */
    public boolean isSeleccionable() {
        return seleccionable;
    }

    /**
     * Indica si la serie es seleccionable.
     * @param seleccionable boolean
     */
    public void setSeleccionable(boolean seleccionable) {
        this.seleccionable = seleccionable;
    }

    /**
     * Indica si la serie está ya creada.
     * @return boolean
     */
    public boolean isSeleccionada() {
        return seleccionada;
    }

    /**
     * Indica si la serie está ya creada.
     * @param seleccionada boolean
     */
    public void setSeleccionada(boolean seleccionada) {
        this.seleccionada = seleccionada;
    }
}
