package mx.ift.sns.modelo.cpsn;

import java.io.Serializable;

/** Bean encargado de almacenar los datos relativos al estudio de equipos de señalización CPSN. */
public class EstudioEquipoCPSN implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** COnstante de la posición de los datos de estudio del bloque 2048. */
    public static final String TXT_BLOQUE_2048 = "Bloque 2048";
    /** COnstante de la posición de los datos de estudio del bloque 128. */
    public static final String TXT_BLOQUE_128 = "Bloque 128";
    /** COnstante de la posición de los datos de estudio del bloque 8. */
    public static final String TXT_BLOQUE_8 = "Bloque 8";
    /** COnstante de la posición de los datos de estudio del bloque individual. */
    public static final String TXT_BLOQUE_INDIVIDUAL = "Individual";
    /** COnstante de la posición de los datos de estudio del bloque totales. */
    public static final String TXT_BLOQUE_TOTALES = "TOTAL";
    /** COnstante de la posición de los datos de estudio del bloque 2048. */
    public static final int POSICION_BLOQUE_2048 = 0;
    /** COnstante de la posición de los datos de estudio del bloque 128. */
    public static final int POSICION_BLOQUE_128 = 1;
    /** COnstante de la posición de los datos de estudio del bloque 8. */
    public static final int POSICION_BLOQUE_8 = 2;
    /** COnstante de la posición de los datos de estudio del bloque individual. */
    public static final int POSICION_BLOQUE_INDIVIDUAL = 3;
    /** COnstante de la posición de los datos de estudio del bloque totales. */
    public static final int POSICION_BLOQUE_TOTALES = 4;

    /** Descripci´on del bloque. */
    private String tipoBloque;

    /** Número de códigos utilizados. */
    private Integer numUtilizados;

    /** Número de códigos asignados. */
    private Integer numAsignados;

    // /** Porcentaje de utilización. */
    // private String porcentaje;

    /** Número de bloques. */
    private Integer totalBloques;

    /** Cantidad Solicitada. */
    private Integer cantidadSolicitada;

    /** Cantidad Asignada. */
    private Integer cantidadSeleccionada;

    /** Constructor. */
    public EstudioEquipoCPSN() {
        tipoBloque = null;
        numUtilizados = 0;
        numAsignados = 0;
        // porcentaje = null;
        totalBloques = 0;
        cantidadSolicitada = 0;
        cantidadSeleccionada = 0;
    }

    /**
     * @return the numUtilizados
     */
    public Integer getNumUtilizados() {
        return numUtilizados;
    }

    /**
     * @param numUtilizados the numUtilizados to set
     */
    public void setNumUtilizados(Integer numUtilizados) {
        this.numUtilizados = numUtilizados;
    }

    /**
     * Método que añade un valor a numUtilizados.
     * @param valor a sumar
     */
    public void addNumUtilizados(Integer valor) {
        this.numUtilizados = numUtilizados + valor;
    }

    /**
     * @return the numAsignados
     */
    public Integer getNumAsignados() {
        return numAsignados;
    }

    /**
     * @param numAsignados the numAsignados to set
     */
    public void setNumAsignados(Integer numAsignados) {
        this.numAsignados = numAsignados;
    }

    /**
     * Método que añade un valor a numAsignados.
     * @param valor a sumar
     */
    public void addNumAsignados(Integer valor) {
        this.numAsignados = numAsignados + valor;
    }

    /**
     * @return the porcentaje
     */
    public String getPorcentaje() {
        return (numAsignados == 0 || numUtilizados == 0)
                ? "0 %" : String.format("%.2f", numUtilizados * 100.0 / numAsignados).replace(",", ".") + " %";
    }

    // /**
    // * @param porcentaje the porcentaje to set
    // */
    // public void setPorcentaje(String porcentaje) {
    // this.porcentaje = porcentaje;
    // }

    /**
     * @return the tipoBloque
     */
    public String getTipoBloque() {
        return tipoBloque;
    }

    /**
     * @param tipoBloque the tipoBloque to set
     */
    public void setTipoBloque(String tipoBloque) {
        this.tipoBloque = tipoBloque;
    }

    /**
     * Método encargado de establecer la descrición del bloque a cada estudio según su posición dentro del array.
     * @param posicion dentro del array
     */
    public void setDescripcion(int posicion) {
        if (posicion == POSICION_BLOQUE_2048) {
            tipoBloque = TXT_BLOQUE_2048;
        } else if (posicion == POSICION_BLOQUE_128) {
            tipoBloque = TXT_BLOQUE_128;
        } else if (posicion == POSICION_BLOQUE_8) {
            tipoBloque = TXT_BLOQUE_8;
        } else if (posicion == POSICION_BLOQUE_INDIVIDUAL) {
            tipoBloque = TXT_BLOQUE_INDIVIDUAL;
        } else if (posicion == POSICION_BLOQUE_TOTALES) {
            tipoBloque = TXT_BLOQUE_TOTALES;
        }
    }

    /**
     * @return the totalBloques
     */
    public Integer getTotalBloques() {
        return totalBloques;
    }

    /**
     * @param totalBloques the totalBloques to set
     */
    public void setTotalBloques(Integer totalBloques) {
        this.totalBloques = totalBloques;
    }

    /**
     * @return the cantidadSolicitada
     */
    public Integer getCantidadSolicitada() {
        return cantidadSolicitada;
    }

    /**
     * @param cantidadSolicitada the cantidadSolicitada to set
     */
    public void setCantidadSolicitada(Integer cantidadSolicitada) {
        this.cantidadSolicitada = cantidadSolicitada;
    }

    /**
     * @return the cantidadSeleccionada
     */
    public Integer getCantidadSeleccionada() {
        return cantidadSeleccionada;
    }

    /**
     * @param cantidadSeleccionada the cantidadSeleccionada to set
     */
    public void setCantidadSeleccionada(Integer cantidadSeleccionada) {
        this.cantidadSeleccionada = cantidadSeleccionada;
    }

}
