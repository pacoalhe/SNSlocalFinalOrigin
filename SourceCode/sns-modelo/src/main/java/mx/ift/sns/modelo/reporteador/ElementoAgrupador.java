package mx.ift.sns.modelo.reporteador;

import java.io.Serializable;

/**
 * Comprende el código y descripción de elementos por los que se agrupa la consulta que generan los reportes que
 * utilizan los filtros por PST, Abn, Estado, Municipio y Poblacion.
 * @author 66765439
 */
public class ElementoAgrupador implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Código literal Pst. */
    public static final String COD_PST = "ID_PST";

    /** Descripción literal Pst. */
    public static final String DESC_PST = "PST";

    /** Código literal Abn. */
    public static final String COD_ABN = "ID_ABN";

    /** Descripción literal Abn. */
    public static final String DESC_ABN = "ABN";

    /** Código literal Estado. */
    public static final String COD_ESTADO = "ID_ESTADO";

    /** Descripción literal Estado. */
    public static final String DESC_ESTADO = "Estado";

    /** Código literal Municipio. */
    public static final String COD_MUNICIPIO = "ID_MUNICIPIO";

    /** Descripción literal Municipio. */
    public static final String DESC_MUNICIPIO = "Municipio";

    /** Código literal Población. */
    public static final String COD_POBLACION = "INEGI";

    /** Descripción literal Población. */
    public static final String DESC_POBLACION = "Población";

    /** Código literal Mes. */
    public static final String COD_MES = "M";

    /** Descripción literal mes. */
    public static final String DESC_MES = "Mes";

    /** Código literal Año. */
    public static final String COD_ANYO = "A";

    /** Descripción literal año. */
    public static final String DESC_ANYO = "Año";

    /**
     * Identificador del campo que utiliza la consulta de la vista V_HISTORICO_SERIE.
     */
    private String codigo;

    /**
     * Literal utilizado para mostrar en el seleccionable de la pantalla de filtrado.
     */
    private String descripcion;

    /**
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
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
        ElementoAgrupador other = (ElementoAgrupador) obj;
        if (codigo == null) {
            if (other.codigo != null) {
                return false;
            }
        } else if (!codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }
}
