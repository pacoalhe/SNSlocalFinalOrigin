package mx.ift.sns.modelo.oficios;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Representa un destinatario específico de un Oficio.
 */
@Entity
@Table(name = "CAT_TIPO_DESTINATARIO")
@ReadOnly
@NamedQuery(name = "TipoDestinatario.findAll", query = "SELECT t FROM TipoDestinatario t")
public class TipoDestinatario implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Destinatario pst solicitante. */
    public static final String PST_SOLICITANTE = "1";

    /** Destinatario resto pst. */
    public static final String RESTO_PST = "2";

    /** Destinatario Cedula notificación. */
    public static final String CEDULA_NOTIFICACION = "3";

    /** Destinatario Acta Circustanciada. */
    public static final String ACTA_CIRCUNSTANCIADA = "4";

    /** Pruebas de JUnit. */
    public static final String JUNIT_TEST = "T";

    /** Identificador interno. */
    @Id
    @Column(name = "ID_TIPO_DESTINATARIO", length = 1, insertable = false)
    private String cdg;

    /** Descripción del tipo. */
    @Column(name = "DESCRIPCION", length = 100)
    private String descripcion;

    /** Descripción del Destinatario del Oficio. */
    @Transient
    private String destinatario;

    /** Constructor, por defecto vacío. */
    public TipoDestinatario() {
    }

    /**
     * Código de tipo.
     * @return String
     */
    public String getCdg() {
        return this.cdg;
    }

    /**
     * Código de tipo.
     * @param cdg String
     */
    public void setCdg(String cdg) {
        this.cdg = cdg;
    }

    /**
     * Descripción del tipo.
     * @return String
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Descripción del tipo.
     * @param descripcion String
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Descripción del Destinatario del Oficio.
     * @return String
     */
    public String getDestinatario() {
        if (destinatario == null) {
            switch (cdg) {
            case PST_SOLICITANTE:
                destinatario = "Solicitante";
                break;

            case RESTO_PST:
                destinatario = "Resto PSTs";
                break;

            case CEDULA_NOTIFICACION:
                destinatario = "Cédula de Notificación";
                break;

            case ACTA_CIRCUNSTANCIADA:
                destinatario = "Acta Circunstanciada";
                break;

            default:
                destinatario = "";
                break;
            }
        }
        return destinatario;
    }

    @Override
    public String toString() {
        return new String(cdg + " - " + descripcion);
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof TipoDestinatario) && (cdg != null)
                ? cdg.equals(((TipoDestinatario) other).cdg)
                : (other == this);
    }

    @Override
    public int hashCode() {
        return (cdg != null)
                ? (this.getClass().hashCode() + cdg.hashCode())
                : super.hashCode();
    }
}
