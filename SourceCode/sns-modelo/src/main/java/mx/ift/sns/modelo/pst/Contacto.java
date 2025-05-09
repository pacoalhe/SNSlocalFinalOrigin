package mx.ift.sns.modelo.pst;

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
import javax.persistence.Version;

import mx.ift.sns.modelo.usu.Auditoria;

/**
 * Representa un Representante Legal, principal o suplente, de un Proveedor de Servicios de Telecomunicación (PST).
 */
@Entity
@Table(name = "CAT_CONTACTO")
@SequenceGenerator(name = "SEQ_ID_PROV_CONTACT", sequenceName = "SEQ_ID_PROV_CONTACT", allocationSize = 1)
@NamedQuery(name = "Contacto.findAll", query = "SELECT c FROM Contacto c")
public class Contacto extends Auditoria implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_PROV_CONTACT")
    @Column(name = "ID_CONTACTO", unique = true, nullable = false)
    private BigDecimal id;

    /** Email. */
    @Column(name = "EMAIL", length = 100)
    private String email;

    /** Nombre. */
    @Column(name = "NOMBRE", length = 100)
    private String nombre;

    /** Teléfono principal. */
    @Column(name = "TELEFONO1", length = 20)
    private String telefono1;

    /** Teléfono. */
    @Column(name = "TELEFONO2", length = 20)
    private String telefono2;

    /** Teléfono. */
    @Column(name = "TELEFONO3", length = 20)
    private String telefono3;

    /** Relación: Varios contactos pueden estar asociados al mismo proveedor. */
    @ManyToOne
    @JoinColumn(name = "ID_PST", nullable = false)
    private Proveedor proveedor;

    /** Relación: Varios contactos pueden ser del mismo tipo. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_CONTACTO", nullable = false)
    private TipoContacto tipoContacto;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor, por defecto vacío. */
    public Contacto() {
    }

    /**
     * Identificador interno.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return this.id;
    }

    /**
     * Identificador interno.
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Email.
     * @return String
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Email.
     * @param email String
     */
    public void setEmail(String email) {
        this.email = email;
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
     * Teléfono principal.
     * @return String
     */
    public String getTelefono1() {
        return this.telefono1;
    }

    /**
     * Teléfono principal.
     * @param telefono1 String
     */
    public void setTelefono1(String telefono1) {
        this.telefono1 = telefono1;
    }

    /**
     * Teléfono.
     * @return String
     */
    public String getTelefono2() {
        return this.telefono2;
    }

    /**
     * Teléfono.
     * @param telefono2 String
     */
    public void setTelefono2(String telefono2) {
        this.telefono2 = telefono2;
    }

    /**
     * Teléfono.
     * @return String
     */
    public String getTelefono3() {
        return this.telefono3;
    }

    /**
     * Teléfono.
     * @param telefono3 String
     */
    public void setTelefono3(String telefono3) {
        this.telefono3 = telefono3;
    }

    /**
     * Proveedor asociado al Contacto.
     * @return Proveedor
     */
    public Proveedor getProveedor() {
        return proveedor;
    }

    /**
     * Proveedor asociado al Contacto.
     * @param proveedor Proveedor
     */
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * Tipo de Contacto.
     * @return TipoContacto
     */
    public TipoContacto getTipoContacto() {
        return tipoContacto;
    }

    /**
     * Tipo de Contacto.
     * @param tipoContacto TipoContacto
     */
    public void setTipoContacto(TipoContacto tipoContacto) {
        this.tipoContacto = tipoContacto;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof Contacto) {
            Contacto other = (Contacto) obj;
            if (id != null) {
                return id.equals(other.id);
            } else {
                // Comparación campo a campo
                if (email == null) {
                    if (other.email != null) {
                        return false;
                    }
                } else if (!email.equals(other.email)) {
                    return false;
                }
                if (nombre == null) {
                    if (other.nombre != null) {
                        return false;
                    }
                } else if (!nombre.equals(other.nombre)) {
                    return false;
                }
                if (proveedor == null) {
                    if (other.proveedor != null) {
                        return false;
                    }
                } else if (!proveedor.equals(other.proveedor)) {
                    return false;
                }
                if (telefono1 == null) {
                    if (other.telefono1 != null) {
                        return false;
                    }
                } else if (!telefono1.equals(other.telefono1)) {
                    return false;
                }
                if (telefono2 == null) {
                    if (other.telefono2 != null) {
                        return false;
                    }
                } else if (!telefono2.equals(other.telefono2)) {
                    return false;
                }
                if (telefono3 == null) {
                    if (other.telefono3 != null) {
                        return false;
                    }
                } else if (!telefono3.equals(other.telefono3)) {
                    return false;
                }
                if (tipoContacto == null) {
                    if (other.tipoContacto != null) {
                        return false;
                    }
                } else if (!tipoContacto.equals(other.tipoContacto)) {
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (id != null)
                ? (this.getClass().hashCode() + id.hashCode())
                : super.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Contacto: ").append(id).append(" - ").append(nombre);
        return sb.toString();
    }
}
