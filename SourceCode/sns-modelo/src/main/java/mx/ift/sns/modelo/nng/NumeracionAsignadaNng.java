package mx.ift.sns.modelo.nng;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import mx.ift.sns.modelo.series.EstadoRango;

import org.apache.commons.lang3.StringUtils;

/**
 * Representa la numeración asignada de numeración no geográfica.
 */
@Entity
@Table(name = "NNG_NUM_ASIGNADA")
@SequenceGenerator(name = "SEQ_ID_NNG_NUM_ASIG", sequenceName = "SEQ_ID_NNG_NUM_ASIG", allocationSize = 1)
@NamedQuery(name = "NumeracionAsignadaNng.findAll", query = "SELECT n FROM NumeracionAsignadaNng n")
public class NumeracionAsignadaNng implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_NNG_NUM_ASIG")
    @Column(name = "ID_NNG_NUM_ASIG", unique = true, nullable = false)
    private BigDecimal id;

    /** Fin rango. */
    @Column(name = "FIN_RANGO", nullable = false, length = 4)
    private String finRango;

    /** Referencia a la clave de Servicio asociada a la numeración. */
    @ManyToOne
    @JoinColumn(name = "ID_CLAVE_SERVICIO", nullable = false)
    private ClaveServicio claveServicio;

    /** Numeracion solicitada. */
    @ManyToOne
    @JoinColumn(name = "ID_NNG_NUM_SOLI", nullable = false)
    private NumeracionSolicitadaNng numeracionSolicitada;

    /** Inicio Rango. */
    @Column(name = "INICIO_RANGO", nullable = false, length = 4)
    private String inicioRango;

    /** SNA. */
    @Column(nullable = false, precision = 4)
    private BigDecimal sna;

    /** Relación: Muchas numeraciones pueden tener el mismo estatus. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS_RANGO", nullable = false)
    private EstadoRango estatus;

    /** Constructor por defecto vacio. */
    public NumeracionAsignadaNng() {
    }

    /**
     * Constructor utilizando un rango.
     * @param rango rango
     */
    public NumeracionAsignadaNng(RangoSerieNng rango) {
        this.claveServicio = rango.getClaveServicio();
        this.sna = rango.getSna();
        this.inicioRango = rango.getNumInicio();
        this.finRango = rango.getNumFinal();
        this.numeracionSolicitada = rango.getNumeracionSolicitada();
        this.estatus = rango.getEstatus();
    }

    /**
     * Identificador.
     * @return the id
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Identificador.
     * @param id the id to set
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Fin rango.
     * @return the finRango
     */
    public String getFinRango() {
        return finRango;
    }

    /**
     * Fin rango.
     * @param finRango the finRango to set
     */
    public void setFinRango(String finRango) {
        this.finRango = finRango;
    }

    /**
     * Referencia a la clave de Servicio asociada a la numeración.
     * @return the claveServicio
     */
    public ClaveServicio getClaveServicio() {
        return claveServicio;
    }

    /**
     * Referencia a la clave de Servicio asociada a la numeración.
     * @param claveServicio the claveServicio to set
     */
    public void setClaveServicio(ClaveServicio claveServicio) {
        this.claveServicio = claveServicio;
    }

    /**
     * Numeracion solicitada.
     * @return the numeracionSolicitada
     */
    public NumeracionSolicitadaNng getNumeracionSolicitada() {
        return numeracionSolicitada;
    }

    /**
     * Numeracion solicitada.
     * @param numeracionAsignadaNng the numeracionSolicitada to set
     */
    public void setNumeracionSolicitada(NumeracionSolicitadaNng numeracionAsignadaNng) {
        this.numeracionSolicitada = numeracionAsignadaNng;
    }

    /**
     * Inicio Rango.
     * @return the inicioRango
     */
    public String getInicioRango() {
        return inicioRango;
    }

    /**
     * Inicio Rango.
     * @param inicioRango the inicioRango to set
     */
    public void setInicioRango(String inicioRango) {
        this.inicioRango = inicioRango;
    }

    /**
     * SNA.
     * @return the sna
     */
    public BigDecimal getSna() {
        return sna;
    }

    /**
     * SNA.
     * @param sna the sna to set
     */
    public void setSna(BigDecimal sna) {
        this.sna = sna;
    }

    /**
     * Estatus.
     * @return estatus
     */
    public EstadoRango getEstatus() {
        return estatus;
    }

    /**
     * Estatus.
     * @param estatus estatus to set
     */
    public void setEstatus(EstadoRango estatus) {
        this.estatus = estatus;
    }

    /**
     * Método que añade al SNA 0 a la izquierda.
     * @return String
     * @throws Exception Exception
     */
    public String getSnaAsString() throws Exception {

        return StringUtils.leftPad(this.sna.toString(), 3, '0');

    }

    /**
     * Inicio rango como int.
     * @return finRango
     */
    public int getInicioRangoAsInt() {
        return Integer.parseInt(inicioRango);
    }

    /**
     * Fin rango como int.
     * @return finRango
     */
    public int getFinRangoAsInt() {
        return Integer.parseInt(finRango);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NumeracionAsignadaNng)) {
            return false;
        }
        return this.id.compareTo(((NumeracionAsignadaNng) obj).getId()) == 0;
    }

}
