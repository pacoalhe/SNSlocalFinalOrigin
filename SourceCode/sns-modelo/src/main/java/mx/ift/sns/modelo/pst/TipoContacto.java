package mx.ift.sns.modelo.pst;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Representa un tipo de Representante Legal de un Proveedor de Servicios.
 */
@Entity
@Table(name = "CAT_TIPO_CONTACTO")
@ReadOnly
@NamedQuery(name = "TipoContacto.findAll", query = "SELECT t FROM TipoContacto t")
public class TipoContacto implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Contacto Administrativo. */
    public static final String CONTACTO_ADMINISTRATIVO = "CA";

    /** Contacto Jurídico. */
    public static final String CONTACTO_JURIDICO = "CJ";

    /** Contacto Técnico. */
    public static final String CONTACTO_TECNICO = "CT";

    /** Representante Legal. */
    public static final String REPRESENTANTE_LEGAL = "RL";

    /** Otros representantes legales. */
    public static final String OTROS_REPRESENTANTES_LEGALES = "RL1";

    /** Identificador interno. */
    @Id
    @Column(name = "ID_TIPO_CONTACTO", length = 3, insertable = false)
    private String cdg;

    /** Descripción del tipo. */
    @Column(name = "DESCRIPCION", length = 100)
    private String descripcion;

    /** Constructor, por defecto vacío. */
    public TipoContacto() {
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

    @Override
    public String toString() {
        return new String(cdg + " - " + descripcion);
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
        result = prime * result + ((descripcion == null) ? 0 : descripcion.hashCode());
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
        TipoContacto other = (TipoContacto) obj;
        if (cdg == null) {
            if (other.cdg != null) {
                return false;
            }
        } else if (!cdg.equals(other.cdg)) {
            return false;
        }
        if (descripcion == null) {
            if (other.descripcion != null) {
                return false;
            }
        } else if (!descripcion.equals(other.descripcion)) {
            return false;
        }
        return true;
    }
}
