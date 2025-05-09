/**
 * .
 */
package mx.ift.sns.modelo.series;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Vista sobre el Catálogo de Series.
 */
@Entity
@Table(name = "CATALOGO_SERIE_VM")
@Cacheable(false)
@ReadOnly
public class VCatalogoSerie implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @Column(name = "ID")
    private BigDecimal id;

    /** Identificador de Serie. */
    @Column(name = "ID_SERIE")
    private String idSna;

    /** Código de Nir. */
    @Column(name = "CDG_NIR")
    private String cdgNir;

    /** Código de ABN. */
    @Column(name = "ID_ABN")
    private String idAbn;

    /** Estatus de la Serie. */
    @Column(name = "ID_ESTATUS")
    private String estatus;

    /** Descripción del estatus.. */
    @Column(name = "ESTATUS")
    private String nombreEstatus;

    /** Inicio de la serie. */
    @Column(name = "NUMINICIAL")
    private String numInicial;

    /** Final de la serie. */
    @Column(name = "NUMFINAL")
    private String numFinal;

    /**
     * Constructor.
     */
    public VCatalogoSerie() {
    }

    /**
     * Identificador.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Identificador.
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Identificador de Serie.
     * @return String
     */
    public String getIdSna() {
        return idSna;
    }

    /**
     * Identificador de Serie.
     * @param idSna String
     */
    public void setIdSna(String idSna) {
        this.idSna = idSna;
    }

    /**
     * Código de Nir.
     * @return String
     */
    public String getCdgNir() {
        return cdgNir;
    }

    /**
     * Código de Nir.
     * @param cdgNir String
     */
    public void setCdgNir(String cdgNir) {
        this.cdgNir = cdgNir;
    }

    /**
     * Código de ABN.
     * @return String
     */
    public String getIdAbn() {
        return idAbn;
    }

    /**
     * Código de ABN.
     * @param idAbn String
     */
    public void setIdAbn(String idAbn) {
        this.idAbn = idAbn;
    }

    /**
     * Estatus de la Serie.
     * @return the estatus
     */
    public String getEstatus() {
        return estatus;
    }

    /**
     * Estatus de la Serie.
     * @param estatus the estatus to set
     */
    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    /**
     * Descripción del estatus.
     * @return String
     */
    public String getNombreEstatus() {
        return nombreEstatus;
    }

    /**
     * Descripción del estatus.
     * @param nombreEstatus String
     */
    public void setNombreEstatus(String nombreEstatus) {
        this.nombreEstatus = nombreEstatus;
    }

    /**
     * Inicio de la serie.
     * @return String
     */
    public String getNumInicial() {
        return numInicial;
    }

    /**
     * Inicio de la serie.
     * @param numInicial String
     */
    public void setNumInicial(String numInicial) {
        this.numInicial = numInicial;
    }

    /**
     * Final de la serie.
     * @return String
     */
    public String getNumFinal() {
        return numFinal;
    }

    /**
     * Final de la serie.
     * @param numFinal String
     */
    public void setNumFinal(String numFinal) {
        this.numFinal = numFinal;
    }

    /**
     * Método que añade al SNA tantos 0 a la izquierda dependiendo del tamaño del NIR.
     * @return String
     * @throws Exception Exception
     */
    public String getSnaAsString() throws Exception {
        if (cdgNir.length() == 3) {
            return StringUtils.leftPad(this.getIdSna(), 3, '0');
        } else if (cdgNir.length() == 2) {
            return StringUtils.leftPad(this.getIdSna(), 4, '0');
        } else {
            return StringUtils.leftPad(this.getIdSna(), 5, '0');
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        VCatalogoSerie other = (VCatalogoSerie) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
