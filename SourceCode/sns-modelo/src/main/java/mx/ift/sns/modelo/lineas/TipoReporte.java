package mx.ift.sns.modelo.lineas;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Representa el tipo de Reporte.
 */
@Entity
@Table(name = "CAT_TIPO_REPORTE")
@ReadOnly
@NamedQuery(name = "TipoReporte.findAll", query = "SELECT t FROM TipoReporte t")
public class TipoReporte implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Reporte Lineas Activas. */
    public static final String LINEAS_ACTIVAS = "1";

    /** Reporte Detalle Lineas Activas. */
    public static final String LINEAS_ACTIVAS_DET = "2";

    /** Reporte Lineas Activas Arrendatario. */
    public static final String LINEAS_ACTIVAS_ARRENDATARIO = "3";

    /** Reporte Lineas arrendadas. */
    public static final String LINEAS_ARRENDADAS = "4";

    /** Código. */
    @Id
    @Column(name = "ID_TIPO_REPORTE")
    private String codigo;

    /** Descripcion. */
    private String descripcion;

    /**
     * Código.
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Código.
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * descripcion.
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * descripcion.
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof TipoReporte) && (codigo != null)
                ? codigo.equals(((TipoReporte) other).codigo)
                : (other == this);
    }

    @Override
    public int hashCode() {
        return (codigo != null)
                ? (this.getClass().hashCode() + codigo.hashCode())
                : super.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("tipo reporte={codigo=");
        builder.append(codigo);

        builder.append(" descripcion='");
        builder.append(descripcion);
        builder.append("'");

        builder.append("}");

        return builder.toString();
    }
}
