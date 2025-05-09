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
 * Representa los documentos entregados asociados a la solicitud UIT.
 */
@Entity
@Table(name = "CPSI_UIT_ENTREGADO_DOC")
@SequenceGenerator(name = "SEQ_ID_CPSI_UIT_EDOC", sequenceName = "SEQ_ID_CPSI_UIT_EDOC", allocationSize = 1)
@NamedQuery(name = "CpsiUitEntregadoDoc.findAll", query = "SELECT c FROM CpsiUitEntregadoDoc c")
public class CpsiUitEntregadoDoc implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_CPSI_UIT_EDOC")
    @Column(name = "ID_CPSI_UIT_ENT_DOC", nullable = false)
    private BigDecimal id;

    /** Solicitud Cpsi a UIt entregada. */
    @ManyToOne
    @JoinColumn(name = "ID_UIT_ENTREGADO", nullable = false)
    private CpsiUitEntregado cpsiUitEntregado;

    /** Nombre del documento. */
    @Column(name = "NOMBRE_DOC", nullable = false)
    private String nombre;

    /** Documento serializado. */
    @Lob
    @Column(name = "DOCUMENTO", nullable = false)
    private byte[] documento;

    /** Constructor, por defecto vacío. */
    public CpsiUitEntregadoDoc() {
    }

    // GETTERS & SETTERS

    /**
     * Identificador.
     * @return the id
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Identificador.
     * @param id the id to set
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Solicitud Cpsi a UIt entregada.
     * @return the cpsiUitEntregado
     */
    public CpsiUitEntregado getCpsiUitEntregado() {
        return cpsiUitEntregado;
    }

    /**
     * Solicitud Cpsi a UIt entregada.
     * @param cpsiUitEntregado the cpsiUitEntregado to set
     */
    public void setCpsiUitEntregado(CpsiUitEntregado cpsiUitEntregado) {
        this.cpsiUitEntregado = cpsiUitEntregado;
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
