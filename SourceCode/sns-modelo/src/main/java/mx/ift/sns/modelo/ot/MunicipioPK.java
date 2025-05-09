package mx.ift.sns.modelo.ot;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Identificador de un Municipio específico del Catálogo de Municipios.
 */
@Embeddable
public class MunicipioPK implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador de Municipio. */
    @Column(name = "ID_MUNICIPIO", unique = true, nullable = false, length = 3)
    private String codMunicipio;

    /** Identificador del Estado al que pertenece el municipio. */
    @Column(name = "ID_ESTADO", unique = true, nullable = false, length = 2)
    private String codEstado;

    /** Constructor, por defecto vacío. */
    public MunicipioPK() {
    }

    /**
     * @return the codMunicipio
     */
    public String getCodMunicipio() {
        return codMunicipio;
    }

    /**
     * @param codMunicipio the codMunicipio to set
     */
    public void setCodMunicipio(String codMunicipio) {
        this.codMunicipio = codMunicipio;
    }

    /**
     * @return the codEstado
     */
    public String getCodEstado() {
        return codEstado;
    }

    /**
     * @param codEstado the codEstado to set
     */
    public void setCodEstado(String codEstado) {
        this.codEstado = codEstado;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MunicipioPK)) {
            return false;
        }
        MunicipioPK castOther = (MunicipioPK) other;
        return this.codEstado.equals(castOther.codEstado)
                && this.codMunicipio.equals(castOther.codMunicipio);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.codEstado.hashCode();
        hash = hash * prime + this.codMunicipio.hashCode();

        return hash;
    }

    @Override
    public String toString() {
        return codEstado + "-" + codMunicipio;
    }
}
