package mx.ift.sns.modelo.central;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
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

import mx.ift.sns.modelo.usu.Auditoria;

/**
 * Representa la marca de la central.
 */
@Entity
@Table(name = "CAT_MARCA_CENTRAL")
@SequenceGenerator(name = "SEQ_ID_MARCA", sequenceName = "SEQ_ID_MARCA", allocationSize = 1)
@NamedQuery(name = "Marca.findAll", query = "SELECT m FROM Marca m")
public class Marca extends Auditoria implements Serializable {

    /** Serializacion. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_MARCA")
    @Column(name = "ID_MARCA_CENTRAL")
    private BigDecimal id;

    /** Nombre de Marca. */
    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;

    /** Relación: Una Marca puede estar asociada a muchos modelos. */
    @OneToMany(mappedBy = "marca", cascade = {CascadeType.PERSIST})
    private List<Modelo> modelos;

    /** Relación: Muchas marcas pueden tener el mismo estatus. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS", nullable = false)
    private Estatus estatus;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor, por defecto vacío. */
    public Marca() {
    }

    /**
     * Identificador de marca.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return this.id;
    }

    /**
     * Identificador de marca.
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Nombre de marca.
     * @return String
     */
    public String getNombre() {
        return this.nombre;
    }

    /**
     * Nombre de marca.
     * @param nombre String
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Relación: Muchas marcas pueden tener el mismo estatus.
     * @return the estatus
     */
    public Estatus getEstatus() {
        return estatus;
    }

    /**
     * Relación: Muchas marcas pueden tener el mismo estatus.
     * @param estatus the estatus to set
     */
    public void setEstatus(Estatus estatus) {
        this.estatus = estatus;
    }

    /**
     * Lista de modelos asociados a la marca.
     * @return List
     */
    public List<Modelo> getModelos() {
        return modelos;
    }

    /**
     * Lista de modelos asociados a la marca.
     * @param modelos List
     */
    public void setModelos(List<Modelo> modelos) {
        this.modelos = modelos;
    }

    /**
     * Asocia modelos a la marca.
     * @param pModelo pModelo
     * @return Modelo
     */
    public Modelo addModelos(Modelo pModelo) {
        if (this.getModelos() == null) {
            this.modelos = new ArrayList<Modelo>();
        }
        this.getModelos().add(pModelo);
        pModelo.setMarca(this);
        return pModelo;
    }

    /**
     * Asocia modelos a la marca.
     * @param pModelo Modelo
     * @return Modelo
     */
    public Modelo removeModelos(Modelo pModelo) {
        this.getModelos().remove(pModelo);
        pModelo.setMarca(null);
        return pModelo;
    }
}
