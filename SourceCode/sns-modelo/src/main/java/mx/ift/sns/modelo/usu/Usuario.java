package mx.ift.sns.modelo.usu;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Usuario del SNS.
 */
@Entity
@Table(name = "CAT_USUARIO")
@SequenceGenerator(name = "SEQ_ID_USUARIO", sequenceName = "SEQ_ID_USUARIO", allocationSize = 1)
public class Usuario extends Auditoria implements Serializable {

    /** Serial id. */
    private static final long serialVersionUID = 1L;

    /** Constante de no primera conexión. */
    public static final String NOPRIMERA = "0";

    /** Constante de primera conexión. */
    public static final String PRIMERA = "1";

    /** Id del usuario. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_USUARIO")
    @Column(name = "ID_USUARIO", unique = true, nullable = false, length = 20)
    private BigDecimal id;

    /** clave. */
    @Column(nullable = false, length = 40)
    private String contrasenna;

    /** email. */
    @Column(nullable = false, length = 30)
    private String email;

    /** fecha en la que caduca la clave. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_CADUCIDAD_CONTRASENNA")
    private Date fechaCaducidadContrasenna;

    /** ultima conexion. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_CONEXION")
    private Date fechaConexion;

    /** ultima desconexion. ?? */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_DESCONEXION")
    private Date fechaDesconexion;

    /** nombre del usuario. */
    @Column(nullable = false, length = 60)
    private String nombre;

    /** indica si es la primera vez. */
    @Column(name = "PRIMERA_CONEXION", length = 1)
    private String primeraConexion;

    /** uid. */
    @Column(nullable = false, length = 20)
    private String userid;

    /** Roles del usuario. */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "USUARIO_ROL",
            joinColumns = {
                    @JoinColumn(name = "ID_USUARIO", nullable = false)
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "ID_ROL", nullable = false)
            })
    private List<Rol> roles;

    // /** Version JPA. */
    // @Version
    // private long version;

    /**
     * Id del usuario.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Id del usuario.
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Clave de usuario.
     * @return String
     */
    public String getContrasenna() {
        return contrasenna;
    }

    /**
     * Clave de usuario.
     * @param contrasenna String
     */
    public void setContrasenna(String contrasenna) {
        this.contrasenna = contrasenna;
    }

    /**
     * Email.
     * @return String
     */
    public String getEmail() {
        return email;
    }

    /**
     * Email.
     * @param email String
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Fecha de caducidad de la contraseña.
     * @return Date
     */
    public Date getFechaCaducidadContrasenna() {
        return fechaCaducidadContrasenna;
    }

    /**
     * Fecha de caducidad de la contraseña.
     * @param fechaCaducidadContrasenna Date
     */
    public void setFechaCaducidadContrasenna(Date fechaCaducidadContrasenna) {
        this.fechaCaducidadContrasenna = fechaCaducidadContrasenna;
    }

    /**
     * Fecha de la última conexión del usuario.
     * @return Date
     */
    public Date getFechaConexion() {
        return fechaConexion;
    }

    /**
     * Fecha de la última conexión del usuario.
     * @param fechaConexion Date
     */
    public void setFechaConexion(Date fechaConexion) {
        this.fechaConexion = fechaConexion;
    }

    /**
     * Fecha de la última desconexión del usuario.
     * @return Date
     */
    public Date getFechaDesconexion() {
        return fechaDesconexion;
    }

    /**
     * Fecha de la última desconexión del usuario.
     * @param fechaDesconexion Date
     */
    public void setFechaDesconexion(Date fechaDesconexion) {
        this.fechaDesconexion = fechaDesconexion;
    }

    /**
     * Nombre de usuario.
     * @return String
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Nombre de usuario.
     * @param nombre String
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Indica si es la primera conexión del usuario.
     * @return String
     */
    public String getPrimeraConexion() {
        return primeraConexion;
    }

    /**
     * Indica si es la primera conexión del usuario.
     * @param primeraConexion String
     */
    public void setPrimeraConexion(String primeraConexion) {
        this.primeraConexion = primeraConexion;
    }

    /**
     * Identificador de usuario.
     * @return String
     */
    public String getUserid() {
        return userid;
    }

    /**
     * Identificador de usuario.
     * @param userid String
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     * Lista de roles asociadas al usuario.
     * @return List<Rol>
     */
    public List<Rol> getRoles() {
        return roles;
    }

    /**
     * Lista de roles asociadas al usuario.
     * @param roles List<Rol>
     */
    public void setRoles(List<Rol> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("id='");
        b.append(id);
        b.append("' uid='");
        b.append(userid);
        b.append("' nombre='");
        b.append(nombre);
        // b.append("' primeraConexion='");
        // b.append(primeraConexion);
        b.append("'");

        return b.toString();
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
        Usuario other = (Usuario) obj;
        if (contrasenna == null) {
            if (other.contrasenna != null) {
                return false;
            }
        } else if (!contrasenna.equals(other.contrasenna)) {
            return false;
        }
        if (email == null) {
            if (other.email != null) {
                return false;
            }
        } else if (!email.equals(other.email)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (nombre == null) {
            if (other.nombre != null) {
                return false;
            }
        } else if (!nombre.equals(other.nombre)) {
            return false;
        }
        if (userid == null) {
            if (other.userid != null) {
                return false;
            }
        } else if (!userid.equals(other.userid)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return (id != null)
                ? (this.getClass().hashCode() + id.hashCode())
                : super.hashCode();
    }
}
