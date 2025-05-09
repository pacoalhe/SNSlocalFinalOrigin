package mx.ift.sns.modelo.ot;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import mx.ift.sns.modelo.usu.Auditoria;

/**
 * Representa un Estado del Catálogo de Estados de la Organización Territorial.
 */
@Entity
@Table(name = "CAT_ESTADO")
@NamedQuery(name = "Estado.findAll", query = "SELECT e FROM Estado e")
public class Estado extends Auditoria implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del Estado. */
    @Id
    @Column(name = "ID_ESTADO", unique = true, nullable = false, length = 2)
    private String codEstado;

    /** Abreviatura. */
    @Column(name = "ABREVIATURA", length = 5)
    private String abreviatura;

    /** Capital. */
    @Column(name = "CAPITAL", length = 70)
    private String capital;

    /** Nombre. */
    @Column(name = "NOMBRE", nullable = false, length = 60)
    private String nombre;

    /** Relación: Un estado tiene muchos municipio. */
    @OneToMany(mappedBy = "estado")
    private List<Municipio> municipios;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor, por defecto vacío. */
    public Estado() {
    }

    /**
     * Abreviatura de Estado.
     * @return String
     */
    public String getAbreviatura() {
        return this.abreviatura;
    }

    /**
     * Abreviatura de Estado.
     * @param abreviatura String
     */
    public void setAbreviatura(String abreviatura) {
        this.abreviatura = abreviatura;
    }

    /**
     * Capital del Estado.
     * @return String
     */
    public String getCapital() {
        return this.capital;
    }

    /**
     * Capital del Estado.
     * @param capital String
     */
    public void setCapital(String capital) {
        this.capital = capital;
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
     * Código de Estado.
     * @return String
     */
    public String getCodEstado() {
        return codEstado;
    }

    /**
     * Código de Estado.
     * @param codEstado String
     */
    public void setCodEstado(String codEstado) {
        this.codEstado = codEstado;
    }

    /**
     * Lista de municipios que pertenecen al Estado.
     * @return List
     */
    public List<Municipio> getMunicipios() {
        return municipios;
    }

    /**
     * Lista de municipios que pertenecen al Estado.
     * @param municipios List
     */
    public void setMunicipios(List<Municipio> municipios) {
        this.municipios = municipios;
    }

    /**
     * Asocia un municipio con el estado.
     * @param pMunicipio Municipio
     * @return Municipio
     */
    public Municipio addMunicipio(Municipio pMunicipio) {
        this.getMunicipios().add(pMunicipio);
        pMunicipio.setEstado(this);
        return pMunicipio;
    }

    /**
     * Elimina la relación entre un municipio y el estado.
     * @param pMunicipio Municipio
     * @return Municipio
     */
    public Municipio removeMunicipio(Municipio pMunicipio) {
        this.getMunicipios().remove(pMunicipio);
        pMunicipio.setEstado(null);
        return pMunicipio;
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof Estado) && (codEstado != null)
                ? codEstado.equals(((Estado) other).codEstado)
                : (other == this);
    }

    @Override
    public int hashCode() {
        return (codEstado != null)
                ? (this.getClass().hashCode() + codEstado.hashCode())
                : super.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Estado={").append(nombre).append("'}");
        return b.toString();
    }

}
