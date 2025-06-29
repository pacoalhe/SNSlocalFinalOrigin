package mx.ift.sns.modelo.ot;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.usu.Auditoria;

/**
 * Representa un Municipio del Catálogo de Municipios de la Organización Territorial.
 */
@Entity
@Table(name = "CAT_MUNICIPIO")
@NamedQuery(name = "Municipio.findAll", query = "SELECT m FROM Municipio m")
public class Municipio extends Auditoria implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Clave primaria compuesta. */
    @EmbeddedId
    private MunicipioPK id;

    /** Nombre. */
    @Column(name = "NOMBRE", nullable = false, length = 80)
    private String nombre;

    /** Relación: Muchos municipios pueden pertenecer al mismo estado. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTADO", nullable = false, insertable = false, updatable = false)
    private Estado estado;

    /** Relación: Un municipio puede tener muchas poblaciones. */
    @OneToMany(mappedBy = "municipio")
    private List<Poblacion> poblaciones;

    /** Región celular. */
    @ManyToOne
    @JoinColumn(name = "REGION_CELULAR", nullable = true)
    private Region regionCelular;

    /** Región PCS. */
    @ManyToOne
    @JoinColumn(name = "REGION_PCS", nullable = true)
    private Region regionPcs;

    /** Estado del municipio. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS", nullable = false)
    private Estatus estatus;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor, por defecto vacío. */
    public Municipio() {
    }

    /**
     * @return the id
     */
    public MunicipioPK getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(MunicipioPK id) {
        this.id = id;
    }

    /**
     * Nombre.
     * @return String
     */
    public String getNombre() {
        return this.nombre;
    }

    /**
     * Nombre.
     * @param nombre String
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Estado (OT) al que pertenece el municipio.
     * @return Estado
     */
    public Estado getEstado() {
        return estado;
    }

    /**
     * Estado (OT) al que pertenece el municipio.
     * @param estado Estado
     */
    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    /**
     * Lista de poblaciones que pertenecen al municipio.
     * @return List
     */
    public List<Poblacion> getPoblaciones() {
        return poblaciones;
    }

    /**
     * Lista de poblaciones que pertenecen al municipio.
     * @param poblaciones List
     */
    public void setPoblaciones(List<Poblacion> poblaciones) {
        this.poblaciones = poblaciones;
    }

    /**
     * Asocia un una población al municipio.
     * @param pPoblacion Poblacion
     * @return Poblacion
     */
    public Poblacion addPoblacion(Poblacion pPoblacion) {
        this.getPoblaciones().add(pPoblacion);
        pPoblacion.setMunicipio(this);
        return pPoblacion;
    }

    /**
     * Elimina la relación entre población y municipio.
     * @param pPoblacion Poblacion
     * @return Poblacion
     */
    public Poblacion removepoblacion(Poblacion pPoblacion) {
        this.getPoblaciones().remove(pPoblacion);
        pPoblacion.setMunicipio(null);
        return pPoblacion;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Municipio) {
            Municipio other = (Municipio) obj;
            return (this.id.getCodEstado().equals(other.getId().getCodEstado())
            && this.id.getCodMunicipio().equals(other.getId().getCodMunicipio()));
        } else {
            return false;
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("municipio={");
        sb.append(this.nombre).append(", ");
        sb.append("Cod: ").append(getId().getCodMunicipio()).append(", ");
        sb.append("Estado: ").append(getId().getCodEstado()).append("}");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return (id != null)
                ? (this.getClass().hashCode() + id.hashCode())
                : super.hashCode();
    }

    /**
     * @return the regionCelular
     */
    public Region getRegionCelular() {
        return regionCelular;
    }

    /**
     * @param regionCelular the regionCelular to set
     */
    public void setRegionCelular(Region regionCelular) {
        this.regionCelular = regionCelular;
    }

    /**
     * @return the regionPcs
     */
    public Region getRegionPcs() {
        return regionPcs;
    }

    /**
     * @param regionPcs the regionPcs to set
     */
    public void setRegionPcs(Region regionPcs) {
        this.regionPcs = regionPcs;
    }

    /**
     * @return the estatus
     */
    public Estatus getEstatus() {
        return estatus;
    }

    /**
     * @param estatus the estatus to set
     */
    public void setEstatus(Estatus estatus) {
        this.estatus = estatus;
    }

    /**
     * FJAH 27.06.2025
     * Campos Transitorios no mapeados en la BD JPA
     */
    @Transient
    private String inegi;

    @Transient
    private String poblacion;

    @Transient
    private String claveInegi5;

    public String getInegi() {
        return inegi;
    }

    public void setInegi(String inegi) {
        this.inegi = inegi;
    }

    public String getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    public String getClaveInegi5() {
        return claveInegi5;
    }

    public void setClaveInegi5(String claveInegi5) {
        this.claveInegi5 = claveInegi5;
    }



}
