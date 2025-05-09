package mx.ift.sns.modelo.series;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.lineas.DetLineaArrendatario;
import mx.ift.sns.modelo.ng.NumeracionAsignada;
import mx.ift.sns.modelo.ng.Serie;
import mx.ift.sns.modelo.usu.Auditoria;

/**
 * Representa un Número de Identificación de Región.
 */
@Entity
@Table(name = "CAT_NIR")
@SequenceGenerator(name = "SEQ_ID_ABN_NIR", sequenceName = "SEQ_ID_ABN_NIR", allocationSize = 1)
@NamedQuery(name = "Nir.findAll", query = "SELECT n FROM Nir n")
public class Nir extends Auditoria implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Minimo codigo nir posible. */
    public static final int MIN_NIR = 1;

    /** Maximo codigo nir posible. */
    public static final int MAX_NIR = 999;

    /** PK de la tabla. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_ABN_NIR")
    @Column(name = "ID_NIR", unique = true, nullable = false)
    private BigDecimal id;

    /** Código Nir. */
    @Column(name = "CDG_NIR", nullable = false, precision = 3)
    private int codigo;

    /** Relación: Muchos NIRs pueden estar asociados con el mismo ABN. */
    @ManyToOne
    @JoinColumn(name = "ID_ABN", nullable = false)
    private Abn abn;

    /** Relación: Mcuhos NIRs pueden tener el mismo Estatus. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS", nullable = false)
    private Estatus estatus;

    /** Relación: Un NIR puede tener muchas numeraciones asignadas. */
    @OneToMany(mappedBy = "nir")
    private List<NumeracionAsignada> numsAsignadas;

    /** Relación: Un Nir puede tener muchos detalles de líneas activas de arrendatario. */
    @OneToMany(mappedBy = "nir")
    private List<DetLineaArrendatario> repoDetRepLinArrendatarios;

    /** Relación: Un Nir puede estar asociado a muchas Series. */
    @OneToMany(mappedBy = "nir")
    private List<Serie> series;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor por defecto. */
    public Nir() {
    }

    /**
     * Identificador interno de NIR.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return this.id;
    }

    /**
     * Identificador interno de NIR.
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Código Nir.
     * @return int
     */
    public int getCodigo() {
        return codigo;
    }

    /**
     * Código Nir.
     * @param codigoNir int
     */
    public void setCodigo(int codigoNir) {
        this.codigo = codigoNir;
    }

    /**
     * ABN asociado al NIR.
     * @return Abn
     */
    public Abn getAbn() {
        return abn;
    }

    /**
     * ABN asociado al NIR.
     * @param abn Abn
     */
    public void setAbn(Abn abn) {
        this.abn = abn;
    }

    /**
     * Numeración Asignadas asociadas al Nir.
     * @return List<NumeracionAsignada>
     */
    public List<NumeracionAsignada> getNumsAsignadas() {
        return numsAsignadas;
    }

    /**
     * Numeración Asignadas asociadas al Nir.
     * @param numsAsignadas List<NumeracionAsignada>
     */
    public void setNumsAsignadas(List<NumeracionAsignada> numsAsignadas) {
        this.numsAsignadas = numsAsignadas;
    }

    /**
     * Lista de Series asociadas al Nir.
     * @return List<Serie>
     */
    public List<Serie> getSeries() {
        return series;
    }

    /**
     * Lista de Series asociadas al Nir.
     * @param series List<Serie>
     */
    public void setSeries(List<Serie> series) {
        this.series = series;
    }

    /**
     * Asocia una Numeración Asignada con el Nir.
     * @param ngNumsAsignada Información de la Numeración Asignada.
     * @return NumeracionAsignada
     */
    public NumeracionAsignada addNgNumsAsignada(NumeracionAsignada ngNumsAsignada) {
        getNumsAsignadas().add(ngNumsAsignada);
        ngNumsAsignada.setNir(this);

        return ngNumsAsignada;
    }

    /**
     * Desasocia una Numeración Asignada con el Nir.
     * @param ngNumsAsignada Información de la Numeración Asignada.
     * @return NumeracionAsignada
     */
    public NumeracionAsignada removeNgNumsAsignada(NumeracionAsignada ngNumsAsignada) {
        getNumsAsignadas().remove(ngNumsAsignada);
        ngNumsAsignada.setNir(null);

        return ngNumsAsignada;
    }

    /**
     * Obtiene el listado de lineas de arrendatarios.
     * @return List
     */
    public List<DetLineaArrendatario> getRepoDetRepLinArrendatarios() {
        return this.repoDetRepLinArrendatarios;
    }

    /**
     * Establece el listado de lineas de arrendatarios.
     * @param repoDetRepLinArrendatarios List<DetLineaArrendatario>
     */
    public void setRepoDetRepLinArrendatarios(List<DetLineaArrendatario> repoDetRepLinArrendatarios) {
        this.repoDetRepLinArrendatarios = repoDetRepLinArrendatarios;
    }

    /**
     * Añade una linea de arrendatario.
     * @param repoDetRepLinArrendatario DetLineaArrendatario
     * @return DetLineaArrendatario
     */
    public DetLineaArrendatario addRepoDetRepLinArrendatario(DetLineaArrendatario repoDetRepLinArrendatario) {
        getRepoDetRepLinArrendatarios().add(repoDetRepLinArrendatario);
        repoDetRepLinArrendatario.setNir(this);

        return repoDetRepLinArrendatario;
    }

    /**
     * Elimina una linea de arrendatario.
     * @param repoDetRepLinArrendatario DetLineaArrendatario
     * @return DetLineaArrendatario
     */
    public DetLineaArrendatario removeRepoDetRepLinArrendatario(DetLineaArrendatario repoDetRepLinArrendatario) {
        getRepoDetRepLinArrendatarios().remove(repoDetRepLinArrendatario);
        repoDetRepLinArrendatario.setNir(null);

        return repoDetRepLinArrendatario;
    }

    /**
     * Asocia una Serie al Nir.
     * @param serie Información de la Serie.
     * @return Serie
     */
    public Serie addSerie(Serie serie) {
        getSeries().add(serie);
        serie.setNir(this);

        return serie;
    }

    /**
     * Dessocia una Serie al Nir.
     * @param serie Información de la Serie.
     * @return Serie
     */
    public Serie removeSerie(Serie serie) {
        getSeries().remove(serie);
        serie.setNir(null);

        return serie;
    }

    /**
     * Estatus del NIR.
     * @return Estatus
     */
    public Estatus getEstatus() {
        return estatus;
    }

    /**
     * Estatus del NIR.
     * @param estatus Estatus
     */
    public void setEstatus(Estatus estatus) {
        this.estatus = estatus;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("nir={id=");
        builder.append(id);
        builder.append(" codigo=");
        builder.append(codigo);
        if (estatus != null) {
            builder.append(" estado=");
            builder.append(estatus.getCdg());
        }
        builder.append("}");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((abn == null) ? 0 : abn.hashCode());
        result = prime * result + codigo;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Nir) {
            Nir other = (Nir) obj;
            return (other.getId().intValue() == this.getId().intValue() && other.getCodigo() == this.getCodigo());
        } else {
            return false;
        }
    }
}
