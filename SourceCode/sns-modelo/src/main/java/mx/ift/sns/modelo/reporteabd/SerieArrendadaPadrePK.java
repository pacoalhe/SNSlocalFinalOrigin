package mx.ift.sns.modelo.reporteabd;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the REP_ABD_SERIE_ARRENDADA_PADRE database table.
 */
@Embeddable
public class SerieArrendadaPadrePK implements Serializable {

    /** Serial ID. */
    private static final long serialVersionUID = 1L;

    /** Asignatario. */
    @Column(name = "ID_PST_ASIGNATARIO")
    private BigDecimal idPstAsignatario;

    /** Código de ABN. */
    @Column(name = "ID_ABN")
    private BigDecimal idAbn;

    /** Numero from. */
    @Column(name = "NUMBER_FROM", length = 10)
    private String numberFrom;

    /**
     * Asignatario.
     * @return BigDecimal
     */
    public BigDecimal getIdPstAsignatario() {
        return idPstAsignatario;
    }

    /**
     * Asignatario.
     * @param idPstAsignatario BigDecimal
     */
    public void setIdPstAsignatario(BigDecimal idPstAsignatario) {
        this.idPstAsignatario = idPstAsignatario;
    }

    /**
     * Código de ABN.
     * @return BigDecimal
     */
    public BigDecimal getIdAbn() {
        return idAbn;
    }

    /**
     * Código de ABN.
     * @param idAbn BigDecimal
     */
    public void setIdAbn(BigDecimal idAbn) {
        this.idAbn = idAbn;
    }

    /**
     * Numero from.
     * @return String
     */
    public String getNumberFrom() {
        return numberFrom;
    }

    /**
     * Numero from.
     * @param numberFrom String
     */
    public void setNumberFrom(String numberFrom) {
        this.numberFrom = numberFrom;
    }

    /** Se sobreescribe el metodo hash. */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idAbn == null) ? 0 : idAbn.hashCode());
        result = prime * result + ((idPstAsignatario == null) ? 0 : idPstAsignatario.hashCode());
        result = prime * result + ((numberFrom == null) ? 0 : numberFrom.hashCode());
        return result;
    }

    /** Se sobreescribe el método equals de la pk. */
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
        SerieArrendadaPadrePK other = (SerieArrendadaPadrePK) obj;
        if (idAbn == null) {
            if (other.idAbn != null) {
                return false;
            }
        } else if (!idAbn.equals(other.idAbn)) {
            return false;
        }
        if (idPstAsignatario == null) {
            if (other.idPstAsignatario != null) {
                return false;
            }
        } else if (!idPstAsignatario.equals(other.idPstAsignatario)) {
            return false;
        }
        if (numberFrom == null) {
            if (other.numberFrom != null) {
                return false;
            }
        } else if (!numberFrom.equals(other.numberFrom)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.idPstAsignatario.toString()).append("-").append(this.idAbn.toString()).append("-")
                .append(this.numberFrom);
        return sb.toString();
    }

}
