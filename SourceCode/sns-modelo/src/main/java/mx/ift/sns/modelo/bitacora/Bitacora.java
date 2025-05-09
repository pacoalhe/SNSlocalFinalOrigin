package mx.ift.sns.modelo.bitacora;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import mx.ift.sns.modelo.usu.Usuario;

/**
 * Representa Bitacora Log.
 */
@Entity
@Table(name = "BIT_LOG")
public class Bitacora implements Serializable {

    /** Serial ID. */
    private static final long serialVersionUID = 1L;

    /** Fecha. */
    @Id
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    /** Usuario. */

    @ManyToOne
    @JoinColumn(name = "ID_USUARIO", nullable = true)
    private Usuario usuario;

    /** Descripcion. */
    private String descripcion;

    /**
     * Descripcion.
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Descripcion.
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Fecha.
     * @return the fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * Fecha.
     * @param fecha the fecha to set
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /**
     * Usuario.
     * @return the usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Usuario.
     * @param usuario the idUsuario to set
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
