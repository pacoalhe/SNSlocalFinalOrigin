package mx.ift.sns.modelo.oficios;

import java.io.Serializable;

/**
 * Representa un rol dentro de un Tipo de Destinatario para la generación de oficios.
 */
public class TipoRol implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Tipo de Rol Cedente. */
    public static final String TIPO_ROL_CEDENTE = "A";

    /** Tipo de Rol Cesionario. */
    public static final String TIPO_ROL_CESIONARIO = "B";

    /** Código Tipo Rol. */
    private String cdg;

    /** Descripción Tipo Rol. */
    private String descripcion;

    /**
     * Constructor.
     * @param pCdgTipoRol Código del Tipo de Rol.
     */
    public TipoRol(String pCdgTipoRol) {
        if (pCdgTipoRol.equals(TIPO_ROL_CEDENTE)) {
            cdg = TIPO_ROL_CEDENTE;
            descripcion = "Cedente";
        } else if (pCdgTipoRol.equals(TIPO_ROL_CESIONARIO)) {
            cdg = TIPO_ROL_CESIONARIO;
            descripcion = "Cesionario";
        } else {
            cdg = "";
            descripcion = "";
        }
    }

    // GETTERS & SETTERS

    /**
     * Código Tipo Rol.
     * @return String
     */
    public String getCdg() {
        return cdg;
    }

    /**
     * Código Tipo Rol.
     * @param cdg String
     */
    public void setCdg(String cdg) {
        this.cdg = cdg;
    }

    /**
     * Descripción Tipo Rol.
     * @return String
     */
    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return new String(cdg + " - " + descripcion);
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof TipoRol) && (cdg != null)
                ? cdg.equals(((TipoRol) other).cdg)
                : (other == this);
    }

    @Override
    public int hashCode() {
        return (cdg != null)
                ? (this.getClass().hashCode() + cdg.hashCode())
                : super.hashCode();
    }
}
