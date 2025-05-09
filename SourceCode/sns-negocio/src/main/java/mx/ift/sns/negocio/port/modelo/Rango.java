package mx.ift.sns.negocio.port.modelo;

/**
 * Rango de numeros.
 */
public class Rango {

    /** Numero inicial del rango. */
    private String numberFrom;

    /** Numero final del rango. */
    private String numberTo;

    /** Indica si es MPP. */
    private boolean mpp;

    /**
     * @return the numberFrom
     */
    public String getNumberFrom() {
        return numberFrom;
    }

    /**
     * @param numberFrom the numberFrom to set
     */
    public void setNumberFrom(String numberFrom) {
        this.numberFrom = numberFrom;
    }

    /**
     * @return the numberTo
     */
    public String getNumberTo() {
        return numberTo;
    }

    /**
     * @param numberTo the numberTo to set
     */
    public void setNumberTo(String numberTo) {
        this.numberTo = numberTo;
    }

    /**
     * @return the mpp
     */
    public boolean isMpp() {
        return mpp;
    }

    /**
     * @param mpp the mpp to set
     */
    public void setMpp(boolean mpp) {
        this.mpp = mpp;
    }
}
