package mx.ift.sns.modelo.usu;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Superclase que audita las entidades de catalogo.
 * @author X50880SA
 */
@MappedSuperclass
public class Auditoria implements Serializable {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;
    /** Usuario de creación. */
    @ManyToOne
    @JoinColumn(name = "ID_USUARIO_CREA", nullable = false)
    private Usuario usuarioCreacion;

    /** Fecha de creación del objeto de catalogo. **/
    @Column(name = "FECHA_CREACION", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    /** Usuario de modificación. */
    @ManyToOne
    @JoinColumn(name = "ID_USUARIO_MOD", nullable = true)
    private Usuario usuarioModificacion;

    /** Fecha de modificación del catalogo. */
    @Column(name = "FECHA_MODIFICACION", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;

    /**
     * Constructor, vacio por defecto.
     */
    public Auditoria() {
    }

    /**
     * Usuario de creación.
     * @return Usuario
     */
    public Usuario getUsuarioCreacion() {
        return usuarioCreacion;
    }

    /**
     * Usuario de creación.
     * @param usuarioCreacion Usuario
     */
    public void setUsuarioCreacion(Usuario usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
    }

    /**
     * Fecha de creación del objeto de catalogo.
     * @return Date
     */
    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * Fecha de creación del objeto de catalogo.
     * @param fechaCreacion Date
     */
    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * Usuario de modificación.
     * @return Usuario
     */
    public Usuario getUsuarioModificacion() {
        return usuarioModificacion;
    }

    /**
     * Usuario de modificación.
     * @param usuarioModificacion Usuario
     */
    public void setUsuarioModificacion(Usuario usuarioModificacion) {
        this.usuarioModificacion = usuarioModificacion;
    }

    /**
     * Fecha de modificación del catalogo.
     * @return Date
     */
    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    /**
     * Fecha de modificación del catalogo.
     * @param fechaModificacion Date
     */
    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    /**
     * Actualiza los datos de auditoría de cada entidad.
     * @param pUsuario Información del usuario logeado en sesión.
     */
    public final void updateAuditableValues(Usuario pUsuario) {
        if ((this.getUsuarioCreacion()) != null) {
            this.setUsuarioModificacion(pUsuario);
            this.setFechaModificacion(new Date());
        } else {
            this.setUsuarioCreacion(pUsuario);
            this.setFechaCreacion(new Date());
        }
    }

}
