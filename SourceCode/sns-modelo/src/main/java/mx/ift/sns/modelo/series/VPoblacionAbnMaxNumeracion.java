package mx.ift.sns.modelo.series;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.ot.Poblacion;

/**
 * Vista para obtener la población que tiene el mayor número de numeraciones asignadas por ABN.
 */
@Entity
@Table(name = "POB_ABN_MAXNASIG_VM")
public class VPoblacionAbnMaxNumeracion implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Abn. */
    @Id
    @JoinColumn(name = "ID_ABN", unique = true, nullable = false)
    private Abn abn;

    /** Numeración asignada. */
    @JoinColumn(name = "NUM_ASIGNADA", nullable = false)
    private Long numeracionAsignada;

    /** Población. */
    @JoinColumn(name = "ID_INEGI")
    private Poblacion poblacion;

    /** Constructor vacio. */
    public VPoblacionAbnMaxNumeracion() {
    }

    /**
     * Abn.
     * @return Abn
     */
    public Abn getAbn() {
        return abn;
    }

    /**
     * Abn.
     * @param abn Abn
     */
    public void setAbn(Abn abn) {
        this.abn = abn;
    }

    /**
     * Numeración asignada.
     * @return Longa
     */
    public Long getNumeracionAsignada() {
        return numeracionAsignada;
    }

    /**
     * Numeración asignada.
     * @param numeracionAsignada Long
     */
    public void setNumeracionAsignada(Long numeracionAsignada) {
        this.numeracionAsignada = numeracionAsignada;
    }

    /**
     * Población.
     * @return Poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * Población.
     * @param poblacion Poblacion
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }
}
