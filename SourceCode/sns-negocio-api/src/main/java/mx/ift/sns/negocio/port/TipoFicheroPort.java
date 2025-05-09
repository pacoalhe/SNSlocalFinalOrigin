package mx.ift.sns.negocio.port;

import java.io.Serializable;

/**
 * Tipo de fichero de portabilidad.
 */
public class TipoFicheroPort implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Estatus Vigente. */
    public static final String DIARIO_PORTED = "0";

    /** Estatus No Vigente. */
    public static final String DIARIO_DELETED = "1";

    /** Constante para historico. */
    public static final TipoFicheroPort TIPO_DIARIO_PORTED =
            new TipoFicheroPort(DIARIO_PORTED, "Archivo de números portados");

    /** Constante para ultimo reporte. */
    public static final TipoFicheroPort TIPO_DIARIO_DELETED =
            new TipoFicheroPort(DIARIO_DELETED, "Archivo de números cancelados");

    /** Codigo de tipo consulta. */
    private String cdg;

    /** Descipcion de tipo consulta. */
    private String descripcion;

    /**
     * Constructor, por defecto vacío.
     */
    public TipoFicheroPort() {
    }

    /**
     * Constructor.
     * @param cdg codigo
     * @param desc descripcion
     */
    public TipoFicheroPort(String cdg, String desc) {
        this.cdg = cdg;
        this.descripcion = desc;
    }

    /**
     * Código de tipo.
     * @return String
     */
    public String getCdg() {
        return this.cdg;
    }

    /**
     * Código de tipo.
     * @param cdg String
     */
    public void setCdg(String cdg) {
        this.cdg = cdg;
    }

    /**
     * Descripción de tipo.
     * @return String
     */
    public String getDescripcion() {
        return this.descripcion;
    }

    /**
     * Descripción de tipo.
     * @param descripcion String
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cdg == null) ? 0 : cdg.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        TipoFicheroPort other = (TipoFicheroPort) obj;
        if (cdg == null) {
            if (other.cdg != null) {
                return false;
            }
        } else if (!cdg.equals(other.cdg)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();

        b.append("tipo={cdg='");
        b.append(cdg);
        b.append("' desc='");
        b.append(descripcion);
        b.append("'}");

        return b.toString();
    }
}
