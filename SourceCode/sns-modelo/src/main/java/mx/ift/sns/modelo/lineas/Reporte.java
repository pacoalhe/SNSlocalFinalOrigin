package mx.ift.sns.modelo.lineas;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import mx.ift.sns.modelo.ng.SolicitudLineasActivas;

/**
 * Representa el reporte.
 */
@Entity
@Table(name = "REP_REPORTE")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "ID_TIPO_REPORTE")
@SequenceGenerator(name = "SEQ_ID_REPORTE", sequenceName = "SEQ_ID_REPORTE", allocationSize = 1)
@NamedQuery(name = "Reporte.findAll", query = "SELECT r FROM Reporte r")
public class Reporte implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** ID Reporte. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_REPORTE")
    @Column(name = "ID_REP_REPORTE", unique = true, nullable = false)
    private BigDecimal id;

    /** Referencia. */
    @Column(name = "NUM_REF")
    private String referencia;

    /** Total registros. */
    @Column(name = "TOTAL_REGISTROS")
    private BigDecimal totalRegistros;

    /** Tipo de reporte. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_REPORTE")
    private TipoReporte tipoReporte;

    /** Solicitud. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL_SOLICITUD", nullable = false)
    private SolicitudLineasActivas solicitudLineasActivas;

    /** Contructor, vacio por defecto. */
    public Reporte() {
    }

    /**Referencia.
     * @return the referencia
     */
    public String getReferencia() {
        return referencia;
    }

    /**Referencia.
     * @param referencia the referencia to set
     */
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    /**ID Reporte.
     * @return the id
     */
    public BigDecimal getId() {
        return id;
    }

    /**ID Reporte.
     * @param id the id to set
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**Total registros.
     * @return the totalRegistros
     */
    public BigDecimal getTotalRegistros() {
        return totalRegistros;
    }

    /**Total registros.
     * @param totalRegistros the totalRegistros to set
     */
    public void setTotalRegistros(BigDecimal totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    /**Tipo de reporte.
     * @return the tipoReporte
     */
    public TipoReporte getTipoReporte() {
        return tipoReporte;
    }

    /**Tipo de reporte.
     * @param tipoReporte the tipoReporte to set
     */
    public void setTipoReporte(TipoReporte tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    /**Solicitud.
     * @return the solicitudLineasActivas
     */
    public SolicitudLineasActivas getSolicitudLineasActivas() {
        return solicitudLineasActivas;
    }

    /**Solicitud.
     * @param solicitudLineasActivas the solicitudLineasActivas to set
     */
    public void setSolicitudLineasActivas(SolicitudLineasActivas solicitudLineasActivas) {
        this.solicitudLineasActivas = solicitudLineasActivas;
    }
}
