package mx.ift.sns.modelo.usu;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Rol de un usuario.
 */
@Entity
@Table(name = "CAT_ROL")
@ReadOnly
public class Rol implements Serializable {

    /** Serial id. */
    private static final long serialVersionUID = 1L;

    /** Constante. */
    public static final int ANALISTA = 1;

    /** Constante. */
    public static final int ABD_PRE = 2;

    /** Constante. */
    public static final int IFT = 3;

    /** Constante. */
    public static final int PST = 4;

    /** Constante. */
    public static final int ABD = 5;

    /** id del rol. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_ROL", unique = true, nullable = false, length = 20)
    private int id;

    /** Descripcion del rol. */
    @Column(nullable = false, length = 100)
    private String descripcion;

    /** nombre del rol. */
    @Column(nullable = false, length = 25)
    private String nombre;

    /** usuarios con este rol. */
    @ManyToMany(mappedBy = "roles")
    private List<Usuario> usuarios;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the usuarios
     */
    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    /**
     * @param usuarios the usuarios to set
     */
    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }
}
