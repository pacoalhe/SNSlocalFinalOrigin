package mx.ift.sns.modelo.oficios;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.modelo.usu.Auditoria;

/**
 * Representa una plantilla para la generación de un Oficio. Contiene el documento Word de plantilla serializado.
 */
@Entity
@Table(name = "CAT_PLANTILLA")
@NamedQuery(name = "Plantilla.findAll", query = "SELECT p FROM Plantilla p")
public class Plantilla extends Auditoria implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Clave primaria compuesta. */
    @EmbeddedId
    private PlantillaPK id;

    /** Descripción de Plantilla. */
    @Column(name = "DESCRIPCION", length = 100)
    private String descripcion;

    /** Documento serializado. */
    @Lob
    @Column(name = "PLANTILLA", nullable = false)
    private byte[] plantilla;

    /** Relación: Muchas plantillas pueden tener el mismo tipo de destinatario. */
    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "ID_TIPO_DESTINATARIO", nullable = false)
    private TipoDestinatario tipoDestinatario;

    /** Relación: Muchas plantillas pueden ser del mismo tipo de solicitud. */
    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "ID_TIPO_SOLICITUD", nullable = false)
    private TipoSolicitud tipoSolicitud;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor, por defecto vacío. */
    public Plantilla() {
    }

    /**
     * Clave primaria compuesta.
     * @return PlantillaPK
     */
    public PlantillaPK getId() {
        return this.id;
    }

    /**
     * Clave primaria compuesta.
     * @param id PlantillaPK
     */
    public void setId(PlantillaPK id) {
        this.id = id;
    }

    /**
     * Descripción de plantilla.
     * @return String
     */
    public String getDescripcion() {
        return this.descripcion;
    }

    /**
     * Descripción de plantilla.
     * @param descripcion String
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Documento serializado.
     * @return byte[]
     */
    public byte[] getPlantilla() {
        return this.plantilla;
    }

    /**
     * Documento serializado.
     * @param plantilla byte[]
     */
    public void setPlantilla(byte[] plantilla) {
        this.plantilla = plantilla;
    }

    /**
     * Tipo de destinatario.
     * @return TipoDestinatario
     */
    public TipoDestinatario getTipoDestinatario() {
        return tipoDestinatario;
    }

    /**
     * Tipo de destinatario.
     * @param tipoDestinatario TipoDestinatario
     */
    public void setTipoDestinatario(TipoDestinatario tipoDestinatario) {
        this.tipoDestinatario = tipoDestinatario;
    }

    /**
     * Tipo de solicitud.
     * @return TipoSolicitud
     */
    public TipoSolicitud getTipoSolicitud() {
        return tipoSolicitud;
    }

    /**
     * Tipo de solicitud.
     * @param tipoSolicitud TipoSolicitud
     */
    public void setTipoSolicitud(TipoSolicitud tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }
}
