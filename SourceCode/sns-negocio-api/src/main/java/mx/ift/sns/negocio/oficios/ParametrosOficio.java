package mx.ift.sns.negocio.oficios;

import java.io.Serializable;
import java.util.Date;

import mx.ift.sns.modelo.oficios.Oficio;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.oficios.TipoRol;
import mx.ift.sns.modelo.solicitud.Solicitud;

/**
 * Contiene las variables del sistema e introducidas por el usuario para la generación de oficios.
 */
public class ParametrosOficio implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Número de Oficio introducido en el formulario. */
    private String numOficio;

    /** Solicitud del Oficio. */
    private Solicitud solicitud;

    /** Tipo de destinatario seleccionado en el formulario. */
    private TipoDestinatario tipoDestinatario;

    /** Fecha de oficio seleccionada en el formulario. */
    private Date fechaOficio;

    /** Texto adicional introducido en el formulario. */
    private String textoAdicional;

    /** Tipo de Rol seleccionado para solicitudes de cesión. */
    private TipoRol tipoRol;

    /** Número de días de aplicación para solicitudes de asignación. */
    private int diasAplicacion;

    /** Oficio actual para actualizar. */
    private Oficio oficio;

    // GETTERS & SETTERS

    /**
     * Número de Oficio introducido en el formulario.
     * @return String
     */
    public String getNumOficio() {
        return numOficio;
    }

    /**
     * Número de Oficio introducido en el formulario.
     * @param numOficio String
     */
    public void setNumOficio(String numOficio) {
        this.numOficio = numOficio;
    }

    /**
     * Solicitud del Oficio.
     * @return Solicitud
     */
    public Solicitud getSolicitud() {
        return solicitud;
    }

    /**
     * Solicitud del Oficio.
     * @param solicitud Solicitud
     */
    public void setSolicitud(Solicitud solicitud) {
        this.solicitud = solicitud;
    }

    /**
     * Tipo de destinatario seleccionado en el formulario.
     * @return TipoDestinatario
     */
    public TipoDestinatario getTipoDestinatario() {
        return tipoDestinatario;
    }

    /**
     * Tipo de destinatario seleccionado en el formulario.
     * @param tipoDestinatario TipoDestinatario
     */
    public void setTipoDestinatario(TipoDestinatario tipoDestinatario) {
        this.tipoDestinatario = tipoDestinatario;
    }

    /**
     * Fecha de oficio seleccionada en el formulario.
     * @return Date
     */
    public Date getFechaOficio() {
        return fechaOficio;
    }

    /**
     * Fecha de oficio seleccionada en el formulario.
     * @param fechaOficio Date
     */
    public void setFechaOficio(Date fechaOficio) {
        this.fechaOficio = fechaOficio;
    }

    /**
     * Texto adicional introducido en el formulario.
     * @return String
     */
    public String getTextoAdicional() {
        return textoAdicional;
    }

    /**
     * Texto adicional introducido en el formulario.
     * @param textoAdicional String
     */
    public void setTextoAdicional(String textoAdicional) {
        this.textoAdicional = textoAdicional;
    }

    /**
     * Tipo de Rol seleccionado para solicitudes de cesión.
     * @return TipoRol
     */
    public TipoRol getTipoRol() {
        return tipoRol;
    }

    /**
     * Tipo de Rol seleccionado para solicitudes de cesión.
     * @param tipoRol TipoRol
     */
    public void setTipoRol(TipoRol tipoRol) {
        this.tipoRol = tipoRol;
    }

    /**
     * Número de días de aplicación para solicitudes de asignación.
     * @return int
     */
    public int getDiasAplicacion() {
        return diasAplicacion;
    }

    /**
     * Número de días de aplicación para solicitudes de asignación.
     * @param diasAplicacion int
     */
    public void setDiasAplicacion(int diasAplicacion) {
        this.diasAplicacion = diasAplicacion;
    }

    /**
     * Oficio actual para actualizar.
     * @return Oficio
     */
    public Oficio getOficio() {
        return oficio;
    }

    /**
     * Oficio actual para actualizar.
     * @param oficio Oficio
     */
    public void setOficio(Oficio oficio) {
        this.oficio = oficio;
    }

}
