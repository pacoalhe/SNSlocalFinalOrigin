package mx.ift.sns.modelo.cpsn;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Representa la vista de estudio de CPSN.
 */
@Entity
@Cacheable(false)
@ReadOnly
@Table(name = "ESTUDIO_CPSN_VM")
@NamedQuery(name = "VEstudioCPSN.findAll", query = "SELECT ves FROM VEstudioCPSN ves")
public class VEstudioCPSN implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @Column(name = "ID_TIPO_BLOQUE")
    private BigDecimal id;

    /** Descripcion del bloque. */
    @Column(name = "descripcion")
    private String descripcion;

    /** Total. */
    @Column(name = "TOTAL")
    private Integer total;

    /** Libres. */
    @Column(name = "LIBRES")
    private Integer libres;

    /** Reservados. */
    @Column(name = "RESERVADOS")
    private Integer reservados;

    /** Asignados. */
    @Column(name = "ASIGNADOS")
    private Integer asignados;

    /** Planificados. */
    @Column(name = "PLANIFICADOS")
    private Integer planificados;

    /** Cuarentena. */
    @Column(name = "CUARENTENA")
    private Integer cuarentena;

    /** Porcentaje de asignados. */
    @Column(name = "PORCENTAJE")
    private String porcentaje;

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
     * Descripcion del bloque.
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Descripcion del bloque.
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Total.
     * @return the total
     */
    public Integer getTotal() {
        return total;
    }

    /**
     * Total.
     * @param total the total to set
     */
    public void setTotal(Integer total) {
        this.total = total;
    }

    /**
     * Libres.
     * @return the libres
     */
    public Integer getLibres() {
        return libres;
    }

    /**
     * Libres.
     * @param libres the libres to set
     */
    public void setLibres(Integer libres) {
        this.libres = libres;
    }

    /**
     * Reservados.
     * @return the reservados
     */
    public Integer getReservados() {
        return reservados;
    }

    /**
     * Reservados.
     * @param reservados the reservados to set
     */
    public void setReservados(Integer reservados) {
        this.reservados = reservados;
    }

    /**
     * Asignados.
     * @return the asignados
     */
    public Integer getAsignados() {
        return asignados;
    }

    /**
     * Asignados.
     * @param asignados the asignados to set
     */
    public void setAsignados(Integer asignados) {
        this.asignados = asignados;
    }

    /**
     * Cuarentena.
     * @return the cuarentena
     */
    public Integer getCuarentena() {
        return cuarentena;
    }

    /**
     * Cuarentena.
     * @param cuarentena the cuarentena to set
     */
    public void setCuarentena(Integer cuarentena) {
        this.cuarentena = cuarentena;
    }

    /**
     * Porcentaje de asignados.
     * @return the porcentaje
     */
    public String getPorcentaje() {
        return porcentaje != null ? porcentaje.replace(",", ".") : null;
    }

    /**
     * Porcentaje de asignados.
     * @param porcentaje the porcentaje to set
     */
    public void setPorcentaje(String porcentaje) {
        this.porcentaje = porcentaje;
    }

    /**
     * Planificados.
     * @return the planificados
     */
    public Integer getPlanificados() {
        return planificados;
    }

    /**
     * Planificados.
     * @param planificados the planificados to set
     */
    public void setPlanificados(Integer planificados) {
        this.planificados = planificados;
    }

}
