package mx.ift.sns.modelo.nng;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.usu.Auditoria;

/**
 * Representa una Clave de Servicio de Numeración No Geográfica.
 */
@Entity
@Table(name = "CAT_CLAVE_SERVICIO")
@NamedQuery(name = "ClaveServicio.findAll", query = "SELECT c FROM ClaveServicio c")
public class ClaveServicio extends Auditoria implements Serializable {

    /** Tamaño maximo de la clave de servicio. */
    public static final int TAM_CLAVE = 3;

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Codigo clave servicio. */
    @Id
    @Column(name = "ID_CLAVE_SERVICIO", length = 3)
    private BigDecimal codigo;

    /** Descripcion clave servicio. */
    @Column(name = "DESCRIPCION", length = 50)
    private String descripcion;

    /** Visible Web. */
    @Column(name = "VISIBLE_WEB", length = 1)
    private Boolean visibleWeb;

    /** Estatus clave de servicio. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS")
    private Estatus estatus;

    /** Mensaje de error de la validación de la clave de servicio. */
    @Transient
    private String errorValidacion;

    /** Descripcion larga de clave servicio. */
    @Column(name = "DESCRIPCION_LARGA", length = 512)
    private String descripcionLarga;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor. */
    public ClaveServicio() {
    }

    /**
     * Codigo clave servicio.
     * @return the codigo
     */
    public BigDecimal getCodigo() {
        return codigo;
    }

    /**
     * Codigo clave servicio.
     * @param codigo the codigo to set
     */
    public void setCodigo(BigDecimal codigo) {
        this.codigo = codigo;
    }

    /**
     * Descripcion clave servicio.
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Descripcion clave servicio.
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Estatus clave de servicio.
     * @return the estatus
     */
    public Estatus getEstatus() {
        return estatus;
    }

    /**
     * Estatus clave de servicio.
     * @param estatus the estatus to set
     */
    public void setEstatus(Estatus estatus) {
        this.estatus = estatus;
    }

    /**
     * Mensaje de error de la validación de la clave de servicio.
     * @return the errorValidacion
     */
    public String getErrorValidacion() {
        return errorValidacion;
    }

    /**
     * Mensaje de error de la validación de la clave de servicio.
     * @param errorValidacion the errorValidacion to set
     */
    public void setErrorValidacion(String errorValidacion) {
        this.errorValidacion = errorValidacion;
    }

    /**
     * Visible Web.
     * @return the visibleWeb
     */
    public Boolean getVisibleWeb() {
        return visibleWeb;
    }

    /**
     * Visible Web.
     * @param visibleWeb the visibleWeb to set
     */
    public void setVisibleWeb(Boolean visibleWeb) {
        this.visibleWeb = visibleWeb;
    }

    /**
     * Método que devuelve en String el valor del campo visible Web.
     * @return String vivibleWeb
     */
    public String getVisibleWebString() {
        return (visibleWeb) ? "SI" : "NO";
    }

    /**
     * Descripcion larga de clave servicio.
     * @return the descripcionLarga
     */
    public String getDescripcionLarga() {
        return descripcionLarga;
    }

    /**
     * Descripcion larga de clave servicio.
     * @param descripcionLarga the descripcionLarga to set
     */
    public void setDescripcionLarga(String descripcionLarga) {
        this.descripcionLarga = descripcionLarga;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ClaveServicio)) {
            return false;
        }
        ClaveServicio other = (ClaveServicio) obj;
        if (codigo == null) {
            if (other.codigo != null) {
                return false;
            }
        } else if (!codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("clave={codigo=");
        builder.append(codigo);
        builder.append(" estado=");
        builder.append(estatus);
        builder.append("}");
        return builder.toString();
    }
}
