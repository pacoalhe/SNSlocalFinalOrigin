package mx.ift.sns.modelo.oficios;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Representa un documento asociado a un oficio. Contiene el documento Word de un oficio serializado.
 */
@Entity
@Table(name = "SOL_OFICIO_DOC")
@SequenceGenerator(name = "SEQ_ID_SOL_OFICIO_DOC", sequenceName = "SEQ_ID_SOL_OFICIO_DOC", allocationSize = 1)
@NamedQuery(name = "OficioBlob.findAll", query = "SELECT o FROM OficioBlob o")
@Cacheable(false)
public class OficioBlob implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_SOL_OFICIO_DOC")
    @Column(name = "ID_SOL_OFICIO_DOC", unique = true, nullable = false)
    private BigDecimal id;

    /** Documento serializado. */
    @Lob
    @Column(name = "DOCUMENTO", nullable = false)
    private byte[] documento;

    /**
     * Identificador de Solicitud.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return this.id;
    }

    /**
     * Identificador de Solicitud.
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Documento serializado.
     * @return byte[]
     */
    public byte[] getDocumento() {
        return this.documento;
    }

    /**
     * Documento serializado.
     * @param documento byte[]
     */
    public void setDocumento(byte[] documento) {
        this.documento = documento;
    }
}
