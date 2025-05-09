package mx.ift.sns.modelo.central;

import java.io.Serializable;
import java.math.BigDecimal;
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
 * Representa el modelo de la central.
 */
@Entity
@Table(name = "CAT_MODELO_CENTRAL")
@SequenceGenerator(name = "SEQ_ID_MODELO_CENT", sequenceName = "SEQ_ID_MODELO_CENT", allocationSize = 1)
@NamedQuery(name = "Modelo.findAll", query = "SELECT m FROM Modelo m")
public class Modelo extends Auditoria implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_MODELO_CENT")
    @Column(name = "ID_MODELO_CENTRAL")
    private BigDecimal id;

    /** Descripción. */
    @Column(name = "DESCRIPCION", nullable = false, length = 70)
    private String descripcion;

    /** Tipo de Modelo. */
    @Column(name = "TIPO_MODELO", length = 20)
    private String tipoModelo;

    /** Version JPA. */
    @Version
    private long version;

    /** Relación: Muchos modelos pueden tener el mismo estatus. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS", nullable = false)
    private Estatus estatus;

    /** Relación: Varios modelos pueden pertenecer a la misma marca. */
    @ManyToOne
    @JoinColumn(name = "ID_MARCA", nullable = false)
    private Marca marca;
    
    /** Relación: Un modelo puede estar asociada a muchas centrales. */
    @OneToMany(mappedBy = "modelo", cascade = {CascadeType.PERSIST})
    private List<Central> centrales; 

    /** Constructor, por defecto vacío. */
    public Modelo() {
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
        Modelo other = (Modelo) obj;
        if (descripcion == null) {
            if (other.descripcion != null) {
                return false;
            }
        } else if (!descripcion.equals(other.descripcion)) {
            return false;
        }
        if (estatus == null) {
            if (other.estatus != null) {
                return false;
            }
        } else if (!estatus.equals(other.estatus)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (tipoModelo == null) {
            if (other.tipoModelo != null) {
                return false;
            }
        } else if (!tipoModelo.equals(other.tipoModelo)) {
            return false;
        }
        return true;
    }

    /**
     * Identificador de Modelo.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return this.id;
    }

    /**
     * Identificador de Modelo.
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Descripción.
     * @return String
     */
    public String getDescripcion() {
        return this.descripcion;
    }

    /**
     * Descripción.
     * @param descripcion String
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Descripción del tipo de modelo.
     * @return String
     */
    public String getTipoModelo() {
        return this.tipoModelo;
    }

    /**
     * Descripción del tipo de modelo.
     * @param tipoModelo String
     */
    public void setTipoModelo(String tipoModelo) {
        this.tipoModelo = tipoModelo;
    }

    /**
     * Relación: Muchos modelos pueden tener el mismo estatus.
     * @return the estatus
     */
    public Estatus getEstatus() {
        return estatus;
    }

    /**
     * Relación: Muchos modelos pueden tener el mismo estatus.
     * @param estatus the estatus to set
     */
    public void setEstatus(Estatus estatus) {
        this.estatus = estatus;
    }

    /**
     * Marca asociada al modelo.
     * @return Marca
     */
    public Marca getMarca() {
        return marca;
    }

    /**
     * Marca asociada al modelo.
     * @param marca Marca
     */
    public void setMarca(Marca marca) {
        this.marca = marca;
    }

	public List<Central> getCentrales() {
		return centrales;
	}

	public void setCentrales(List<Central> centrales) {
		this.centrales = centrales;
	}
    
    
}
