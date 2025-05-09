package mx.ift.sns.negocio.ng.model;

import java.io.Serializable;

/**
 * Indica los detalles de un error en la presuscripcion.
 */
public class RangoNoAsignadoError implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Error Numeración no existe. */
    public static final String ERROR_1 = "No existe la Numeración solicitada.";

    /** Error Asignatario no corresponde. */
    public static final String ERROR_2 = "El Proveedor Asignatario no corresponde con el de la Numeración solicitada.";

    /** Error Tipo de red no es fijo. */
    public static final String ERROR_3 = "Tipo de red no es fijo.";

    /** Error Tipo de red no es fijo. */
    public static final String ERROR_4 = "Arrendatario no corresponde.";

    /** pst asignatario 0. */
    private String nir0;

    /** sna 0. */
    private String sna0;

    /** numero inicial 0. */
    private String numeroInicial0;

    /** numero final 0. */
    private String numeroFinal0;

    /** IDO 0. */
    private String ido0;

    /** Nombre pst 0. */
    private String pst0;

    /** Tipo de red 0. */
    private String tipoRed0;

    /** pst asignatario 0. */
    private String nir1;

    /** sna 0. */
    private String sna1;

    /** numero inicial 0. */
    private String numeroInicial1;

    /** numero final 0. */
    private String numeroFinal1;

    /** IDO 0. */
    private String ido1;

    /** Tipo de red 1. */
    private String tipoRed1;

    /** Nombre pst 0. */
    private String pst1;

    /** Tipo de error. */
    private String descripcion;

    /**
     * @return the nir0
     */
    public String getNir0() {
        return nir0;
    }

    /**
     * @param nir0 the nir0 to set
     */
    public void setNir0(String nir0) {
        this.nir0 = nir0;
    }

    /**
     * @return the sna0
     */
    public String getSna0() {
        return sna0;
    }

    /**
     * @param sna0 the sna0 to set
     */
    public void setSna0(String sna0) {
        this.sna0 = sna0;
    }

    /**
     * @return the numeroInicial0
     */
    public String getNumeroInicial0() {
        return numeroInicial0;
    }

    /**
     * @param numeroInicial0 the numeroInicial0 to set
     */
    public void setNumeroInicial0(String numeroInicial0) {
        this.numeroInicial0 = numeroInicial0;
    }

    /**
     * @return the numeroFinal0
     */
    public String getNumeroFinal0() {
        return numeroFinal0;
    }

    /**
     * @param numeroFinal0 the numeroFinal0 to set
     */
    public void setNumeroFinal0(String numeroFinal0) {
        this.numeroFinal0 = numeroFinal0;
    }

    /**
     * @return the ido0
     */
    public String getIdo0() {
        return ido0;
    }

    /**
     * @param ido0 the ido0 to set
     */
    public void setIdo0(String ido0) {
        this.ido0 = ido0;
    }

    /**
     * @return the pst0
     */
    public String getPst0() {
        return pst0;
    }

    /**
     * @param pst0 the pst0 to set
     */
    public void setPst0(String pst0) {
        this.pst0 = pst0;
    }

    /**
     * @return the nir1
     */
    public String getNir1() {
        return nir1;
    }

    /**
     * @param nir1 the nir1 to set
     */
    public void setNir1(String nir1) {
        this.nir1 = nir1;
    }

    /**
     * @return the sna1
     */
    public String getSna1() {
        return sna1;
    }

    /**
     * @param sna1 the sna1 to set
     */
    public void setSna1(String sna1) {
        this.sna1 = sna1;
    }

    /**
     * @return the numeroInicial1
     */
    public String getNumeroInicial1() {
        return numeroInicial1;
    }

    /**
     * @param numeroInicial1 the numeroInicial1 to set
     */
    public void setNumeroInicial1(String numeroInicial1) {
        this.numeroInicial1 = numeroInicial1;
    }

    /**
     * @return the numeroFinal1
     */
    public String getNumeroFinal1() {
        return numeroFinal1;
    }

    /**
     * @param numeroFinal1 the numeroFinal1 to set
     */
    public void setNumeroFinal1(String numeroFinal1) {
        this.numeroFinal1 = numeroFinal1;
    }

    /**
     * @return the ido1
     */
    public String getIdo1() {
        return ido1;
    }

    /**
     * @param ido1 the ido1 to set
     */
    public void setIdo1(String ido1) {
        this.ido1 = ido1;
    }

    /**
     * @return the pst1
     */
    public String getPst1() {
        return pst1;
    }

    /**
     * @param pst1 the pst1 to set
     */
    public void setPst1(String pst1) {
        this.pst1 = pst1;
    }

    /**
     * @return the tipoRed0
     */
    public String getTipoRed0() {
        return tipoRed0;
    }

    /**
     * @param tipoRed0 the tipoRed0 to set
     */
    public void setTipoRed0(String tipoRed0) {
        this.tipoRed0 = tipoRed0;
    }

    /**
     * @return the tipoRed1
     */
    public String getTipoRed1() {
        return tipoRed1;
    }

    /**
     * @param tipoRed1 the tipoRed1 to set
     */
    public void setTipoRed1(String tipoRed1) {
        this.tipoRed1 = tipoRed1;
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("error={nir=");

        builder.append(nir0);

        builder.append(" sna=");

        builder.append(sna0);

        builder.append(" num_ini=");
        builder.append(numeroInicial0);

        builder.append(" num_final=");
        builder.append(numeroFinal0);

        builder.append(" ido_arrendatario=");
        builder.append(ido0);

        builder.append("  arrendatario=");
        builder.append(pst0);

        builder.append("}");

        return builder.toString();
    }
}
