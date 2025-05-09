package mx.ift.sns.modelo.solicitud;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Tipo de solicitud.
 */
@Entity
@Table(name = "CAT_TIPO_SOLICITUD")
@ReadOnly
@NamedQuery(name = "TipoSolicitud.findAll", query = "SELECT t FROM TipoSolicitud t")
public class TipoSolicitud implements Serializable {

    /** Serializacion. */
    private static final long serialVersionUID = 1L;

    /** Solicitud de asignación de numeración geografica. */
    public static final Integer ASIGNACION = new Integer(1);

    /** Solicitud de cersión de derechos numeracion geografica. */
    public static final Integer CESION_DERECHOS = new Integer(2);

    /** Solicitud de liberación de numeración geografica. */
    public static final Integer LIBERACION = new Integer(3);

    /** Solicitud de redistribución de numeración geografica. */
    public static final Integer REDISTRIBUCION = new Integer(4);

    /** Solicitud de consolidacion de numeración geografica. */
    public static final Integer CONSOLIDACION = new Integer(5);

    /** Solicitud de lineas activas. */
    public static final Integer LINEAS_ACTIVAS = new Integer(6);

    /** Solicitud de asignación de numeración no geografica. */
    public static final Integer ASIGNACION_NNG = new Integer(7);

    /** Solicitud de cesión de derechos de numeración no geografica. */
    public static final Integer CESION_DERECHOS_NNG = new Integer(8);

    /** Solicitud de liberación de numeración no geografica. */
    public static final Integer LIBERACION_NNG = new Integer(9);

    /** Solicitud de redistribución de numeración no geografica. */
    public static final Integer REDISTRIBUCION_NNG = new Integer(10);

    /** Solicitud de asignación de códigos de puntos de señalización nacionales. */
    public static final Integer ASIGNACION_CPSN = new Integer(11);

    /** Solicitud de cesión de códigos de puntos de señalización nacionales. */
    public static final Integer CESION_CPSN = new Integer(12);

    /** Solicitud de liberación de códigos de puntos de señalización nacionales. */
    public static final Integer LIBERACION_CPSN = new Integer(13);

    /** Solicitud de lineas activas. */
    public static final Integer LINEAS_ACTIVAS_NNG = new Integer(17);

    /** Solicitud de asignación de códigos de puntos de señalización internacionales. */
    public static final Integer ASIGNACION_CPSI = new Integer(14);

    /** Solicitud de cesión de códigos de puntos de señalización internacionales. */
    public static final Integer CESION_CPSI = new Integer(15);

    /** Solicitud de liberación de códigos de puntos de señalización internacionales. */
    public static final Integer LIBERACION_CPSI = new Integer(16);

    /** Solicitud de códigos a la UIT. */
    public static final Integer SOLICITUD_CPSI_UIT = new Integer(18);

    /** Identificador interno. */
    @Id
    @Column(name = "ID_TIPO_SOLICITUD", insertable = false)
    private Integer cdg;

    /** Descripción del tipo. */
    @Column(name = "DESCRIPCION", length = 100)
    private String descripcion;

    @Override
    public boolean equals(Object other) {
        return (other instanceof TipoSolicitud) && (cdg != null)
                ? cdg.equals(((TipoSolicitud) other).cdg)
                : (other == this);
    }

    @Override
    public int hashCode() {
        return (cdg != null)
                ? (this.getClass().hashCode() + cdg.hashCode())
                : super.hashCode();
    }

    /**
     * Código de tipo.
     * @return Integer
     */
    public Integer getCdg() {
        return this.cdg;
    }

    /**
     * Código de tipo.
     * @param cdg String
     */
    public void setCdg(Integer cdg) {
        this.cdg = cdg;
    }

    /**
     * Descripción de tipo.
     * @return String
     */
    public String getDescripcion() {
        return this.descripcion;
    }

    /**
     * Descripción de tipo.
     * @param descripcion String
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(cdg);
        builder.append(" - ");
        builder.append(descripcion);
        return builder.toString();
    }
}
