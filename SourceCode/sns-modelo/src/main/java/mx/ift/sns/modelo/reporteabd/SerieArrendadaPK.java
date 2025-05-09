package mx.ift.sns.modelo.reporteabd;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the REP_ABD_SERIE_ARRENDADA database table.
 */
@Embeddable
public class SerieArrendadaPK implements Serializable {

    /** Serial ID. */
    private static final long serialVersionUID = 1L;

    /** Proveedor arrendador. */
    @Column(name = "ID_PST_ASIGNATARIO")
    private BigDecimal idPstAsignatario;

    /** Numero from. */
    @Column(name = "NUMBER_FROM")
    private String numFrom;

    /** Number to. */
    @Column(name = "NUMBER_TO")
    private String numTo;

    /** NumberFrom leido del archivo de arrendador. */
    @Column(name = "NUMBERFROM_FILE")
    private String numberFromFile;

    /** NumerTo leido del archivo de arrendador. */
    @Column(name = "NUMBERTO_FILE")
    private String numberToFile;

    /**
     * @return the idPstAsignatario
     */
    public BigDecimal getIdPstAsignatario() {
        return idPstAsignatario;
    }

    /**
     * @param idPstAsignatario the idPstAsignatario to set
     */
    public void setIdPstAsignatario(BigDecimal idPstAsignatario) {
        this.idPstAsignatario = idPstAsignatario;
    }

    /**
     * Numero from.
     * @return String
     */
    public String getNumberFrom() {
        return numFrom;
    }

    /**
     * Numero from.
     * @param numIni String
     */
    public void setNumberFrom(String numIni) {
        this.numFrom = numIni;
    }

    /**
     * Number to.
     * @return String
     */
    public String getNumberTo() {
        return numTo;
    }

    /**
     * Number to.
     * @param numFinal String
     */
    public void setNumberTo(String numFinal) {
        this.numTo = numFinal;
    }

    /**
     * @return the numberFromFile
     */
    public String getNumberFromFile() {
        return numberFromFile;
    }

    /**
     * @param numberFromFile the numberFromFile to set
     */
    public void setNumberFromFile(String numberFromFile) {
        this.numberFromFile = numberFromFile;
    }

    /**
     * @return the numberToFile
     */
    public String getNumberToFile() {
        return numberToFile;
    }

    /**
     * @param numberToFile the numberToFile to set
     */
    public void setNumberToFile(String numberToFile) {
        this.numberToFile = numberToFile;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idPstAsignatario == null) ? 0 : idPstAsignatario.hashCode());
        result = prime * result + ((numTo == null) ? 0 : numTo.hashCode());
        result = prime * result + ((numFrom == null) ? 0 : numFrom.hashCode());
        result = prime * result + ((numberFromFile == null) ? 0 : numberFromFile.hashCode());
        result = prime * result + ((numberToFile == null) ? 0 : numberToFile.hashCode());
        return result;
    }

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
        SerieArrendadaPK other = (SerieArrendadaPK) obj;
        if (idPstAsignatario == null) {
            if (other.idPstAsignatario != null) {
                return false;
            }
        } else if (!idPstAsignatario.equals(other.idPstAsignatario)) {
            return false;
        }
        if (numTo == null) {
            if (other.numTo != null) {
                return false;
            }
        } else if (!numTo.equals(other.numTo)) {
            return false;
        }
        if (numFrom == null) {
            if (other.numFrom != null) {
                return false;
            }
        } else if (!numFrom.equals(other.numFrom)) {
            return false;
        }
        return true;
    }
}
