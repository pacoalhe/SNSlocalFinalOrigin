package mx.ift.sns.modelo.central;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Representa el tipo de medio de transmisión de la central.
 */
@Entity
@Table(name = "CAT_TIPO_MEDIO_TRANSMISION")
@NamedQuery(name = "TipoMedioTransmision.findAll", query = "SELECT t FROM TipoMedioTransmision t")
public class TipoMedioTransmision implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @Column(name = "ID_TIPO_MEDIO_TRANSMISION", length = 2)
    private String cdg;

    /** Descripción. */
    @Column(name = "DESCRIPCION", nullable = false, length = 100)
    private String descripcion;

    /** Constructor, por defecto vacío. */
    public TipoMedioTransmision() {
    }

    /**
     * Identificador de Tipo de Medio.
     * @return String
     */
    public String getCdg() {
        return this.cdg;
    }

    /**
     * Identificador de Tipo de Medio.
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
}
