package mx.ift.sns.modelo.cpsi;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Representa los documentos asociados a la solicitud UIT.
 */
@Entity
@Table(name = "CPSI_SOL_UIT_DOC")
@SequenceGenerator(name = "SEQ_ID_SOL_UIT_DOC", sequenceName = "SEQ_ID_SOL_UIT_DOC", allocationSize = 1)
@NamedQuery(name = "CpsiUitDocumento.findAll", query = "SELECT c FROM CpsiUitDocumento c")
public class CpsiUitDocumento implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_SOL_UIT_DOC")
    @Column(name = "ID_SOL_UIT_DOC", nullable = false)
    private BigDecimal id;

    /** Solicitud de Asignación asociada al CPSI Asignado. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL_SOLICITUD", nullable = false)
    private SolicitudCpsiUit solicitudUit;

    /** Nombre del documento. */
    @Column(name = "NOMBRE_DOC", nullable = false)
    private String nombre;

    /** Documento serializado. */
    @Lob
    @Column(name = "DOCUMENTO", nullable = false)
    private byte[] documento;

    /** Constructor, por defecto vacío. */
    public CpsiUitDocumento() {
    }

    // GETTERS & SETTERS

    /**
     * Identificador de CPSI Asignado.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Identificador de CPSI Asignado.
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CpsiAsignado = {");
        builder.append("id = ").append(this.id);
        builder.append("}");
        return builder.toString();
    }

    /**
     * Solicitud de Asignación asociada al CPSI Asignado.
     * @return the solicitudUit
     */
    public SolicitudCpsiUit getSolicitudUit() {
        return solicitudUit;
    }

    /**
     * Solicitud de Asignación asociada al CPSI Asignado.
     * @param solicitudUit the solicitudUit to set
     */
    public void setSolicitudUit(SolicitudCpsiUit solicitudUit) {
        this.solicitudUit = solicitudUit;
    }

    /**
     * Nombre del documento.
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Nombre del documento.
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Documento serializado.
     * @return the documento
     */
    public byte[] getDocumento() {
        return documento;
    }

    /**
     * Documento serializado.
     * @param documento the documento to set
     */
    public void setDocumento(byte[] documento) {
        this.documento = documento;
    }

}
