package mx.ift.sns.modelo.cpsn;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Representa el tipo de bloque.
 */
@Entity
@Table(name = "CPSN_TIPO_BLOQUE")
@ReadOnly
@NamedQuery(name = "TipoBloqueCPSN.findAll", query = "SELECT tb FROM TipoBloqueCPSN tb")
public class TipoBloqueCPSN implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** COnstante del bloque de totales. */
    public static final String TOTALES = "Totales";

    /** Bloque Individual. */
    public static final String INDIVIDUAL = "0";

    /** Bloque 8. */
    public static final String BLOQUE_8 = "1";

    /** Bloque 128. */
    public static final String BLOQUE_128 = "2";

    /** Bloque 2048. */
    public static final String BLOQUE_2048 = "3";

    /** Bloque 2048. */
    public static final String TIPO_BLOQUE_2048 = "2048";

    /** Bloque 128. */
    public static final String TIPO_BLOQUE_128 = "128";

    /** Bloque 8. */
    public static final String TIPO_BLOQUE_8 = "8";

    /** Bloque Individual. */
    public static final String TIPO_INDIVIDUAL = "Individual";

    /** Bloque Individual. */
    public static final int TAM_INDIVIDUAL = 14;

    /** Bloque 8. */
    public static final int TAM_BLOQUE_8 = 11;

    /** Bloque 128. */
    public static final int TAM_BLOQUE_128 = 7;

    /** Bloque 2048. */
    public static final int TAM_BLOQUE_2048 = 3;

    /** Registros a generar desde bloque 2048. */
    public static final int NUM_REG_2048 = 16;

    /** Registros a generar desde bloque 128. */
    public static final int NUM_REG_128 = 16;

    /** Registros a generar desde bloque 8. */
    public static final int NUM_REG_8 = 8;

    /** Constante de la posición de los datos de estudio del bloque 2048. */
    public static final String TXT_BLOQUE_2048 = "Bloque 2048";

    /** Constante de la posición de los datos de estudio del bloque 128. */
    public static final String TXT_BLOQUE_128 = "Bloque 128";

    /** Constante de la posición de los datos de estudio del bloque 8. */
    public static final String TXT_BLOQUE_8 = "Bloque 8";

    /** Constante de la posición de los datos de estudio del bloque individual. */
    public static final String TXT_BLOQUE_INDIVIDUAL = "Individual";

    /** Identificador. */
    @Id
    @Column(name = "ID_TIPO_BLOQUE", length = 1)
    private String id;

    /** Descripción del tipo. */
    @Column(name = "DESCRIPCION", length = 100)
    private String descripcion;

    /** Constructor, por defecto vacío. */
    public TipoBloqueCPSN() {
    }

    /**
     * Constructor.
     * @param tipo tipo de bloque
     */
    public TipoBloqueCPSN(String tipo) {
        this.id = tipo;
    }

    /**
     * Identificador.
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Identificador.
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Descripción del tipo.
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Descripción del tipo.
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
        result = prime * result + ((descripcion == null) ? 0 : descripcion.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        TipoBloqueCPSN other = (TipoBloqueCPSN) obj;
        if (descripcion == null) {
            if (other.descripcion != null) {
                return false;
            }
        } else if (!descripcion.equals(other.descripcion)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

}
