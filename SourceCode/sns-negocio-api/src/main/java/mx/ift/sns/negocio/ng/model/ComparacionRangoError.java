package mx.ift.sns.negocio.ng.model;

import java.io.Serializable;

/**
 * Indica los detalles de un error en la presuscripcion.
 */
public class ComparacionRangoError implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** IDO arren datario. */
    private String idoArrendatario;

    /** Arrendatario. */
    private String arrendatario;

    /** numero from arrendatario. */
    private String numeroFromArrendatario;

    /** numero to arrendatario. */
    private String numeroToArrendatario;

    /** ID arrendador. */
    private String idoArrendador;

    /** numero from arrendador. */
    private String arrendador;

    /** numero from arrendador. */
    private String numeroFromArrendador;

    /** numero to arrendador. */
    private String numeroToArrendador;

    /**
     * @return the idoArrendatario
     */
    public String getIdoArrendatario() {
        return idoArrendatario;
    }

    /**
     * @param idoArrendatario the idoArrendatario to set
     */
    public void setIdoArrendatario(String idoArrendatario) {
        this.idoArrendatario = idoArrendatario;
    }

    /**
     * @return the arrendatario
     */
    public String getArrendatario() {
        return arrendatario;
    }

    /**
     * @param arrendatario the arrendatario to set
     */
    public void setArrendatario(String arrendatario) {
        this.arrendatario = arrendatario;
    }

    /**
     * @return the numeroFromArrendatario
     */
    public String getNumeroFromArrendatario() {
        return numeroFromArrendatario;
    }

    /**
     * @param numeroFromArrendatario the numeroFromArrendatario to set
     */
    public void setNumeroFromArrendatario(String numeroFromArrendatario) {
        this.numeroFromArrendatario = numeroFromArrendatario;
    }

    /**
     * @return the numeroToArrendatario
     */
    public String getNumeroToArrendatario() {
        return numeroToArrendatario;
    }

    /**
     * @param numeroToArrendatario the numeroToArrendatario to set
     */
    public void setNumeroToArrendatario(String numeroToArrendatario) {
        this.numeroToArrendatario = numeroToArrendatario;
    }

    /**
     * @return the idoArrendador
     */
    public String getIdoArrendador() {
        return idoArrendador;
    }

    /**
     * @param idoArrendador the idoArrendador to set
     */
    public void setIdoArrendador(String idoArrendador) {
        this.idoArrendador = idoArrendador;
    }

    /**
     * @return the arrendador
     */
    public String getArrendador() {
        return arrendador;
    }

    /**
     * @param arrendador the arrendador to set
     */
    public void setArrendador(String arrendador) {
        this.arrendador = arrendador;
    }

    /**
     * @return the numeroFromArrendador
     */
    public String getNumeroFromArrendador() {
        return numeroFromArrendador;
    }

    /**
     * @param numeroFromArrendador the numeroFromArrendador to set
     */
    public void setNumeroFromArrendador(String numeroFromArrendador) {
        this.numeroFromArrendador = numeroFromArrendador;
    }

    /**
     * @return the numeroToArrendador
     */
    public String getNumeroToArrendador() {
        return numeroToArrendador;
    }

    /**
     * @param numeroToArrendador the numeroToArrendador to set
     */
    public void setNumeroToArrendador(String numeroToArrendador) {
        this.numeroToArrendador = numeroToArrendador;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("error={");

        builder.append("  arrendador={");
        builder.append(arrendador);
        builder.append(" ido_arrendador=");
        builder.append(idoArrendador);
        builder.append(", ");
        builder.append(numeroFromArrendador);
        builder.append(", ");
        builder.append(numeroToArrendador);
        builder.append("}");

        builder.append(" arrendatario=");
        builder.append(arrendatario);
        builder.append(" ido_arrendador=");
        builder.append(idoArrendatario);
        builder.append(", ");
        builder.append(numeroFromArrendatario);
        builder.append(", ");
        builder.append(numeroToArrendatario);
        builder.append("},");

        builder.append("}");
        return builder.toString();
    }
}
