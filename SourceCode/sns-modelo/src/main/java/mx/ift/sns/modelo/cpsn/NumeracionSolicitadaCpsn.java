package mx.ift.sns.modelo.cpsn;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Representa el bloque solicitado.
 */
@Entity
@Table(name = "CPSN_BLOQUE_SOLICITADO")
@SequenceGenerator(name = "SEQ_ID_BLOQUE_SOL_CPSN", sequenceName = "SEQ_ID_BLOQUE_SOL_CPSN", allocationSize = 1)
@NamedQuery(name = "NumeracionSolicitadaCpsn.findAll", query = "SELECT n FROM NumeracionSolicitadaCpsn n")
public class NumeracionSolicitadaCpsn implements Serializable {
    /**
     * Serial ID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_BLOQUE_SOL_CPSN")
    @Column(name = "ID_CODIGO_SOLICITADO", unique = true, nullable = false)
    private BigDecimal id;

    /** Cantidad solicitada. */
    @Column(name = "CANTIDAD", nullable = false)
    private BigDecimal cantSolicitada;

    /** Solicitud. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL_SOLICITUD", nullable = false)
    private SolicitudAsignacionCpsn solicitudAsignacion;

    /** Tipo de Bloque. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_BLOQUE", nullable = false)
    private TipoBloqueCPSN tipoBloque;

    /** Constructor. */
    public NumeracionSolicitadaCpsn() {
    }

    /**
     * ID.
     * @return the id
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * ID.
     * @param id the id to set
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Cantidad solicitada.
     * @return the cantSolicitada
     */
    public BigDecimal getCantSolicitada() {
        return cantSolicitada;
    }

    /**
     * Cantidad solicitada.
     * @param cantSolicitada the cantSolicitada to set
     */
    public void setCantSolicitada(BigDecimal cantSolicitada) {
        this.cantSolicitada = cantSolicitada;
    }

    /**
     * Solicitud.
     * @return the solicitudAsignacion
     */
    public SolicitudAsignacionCpsn getSolicitudAsignacion() {
        return solicitudAsignacion;
    }

    /**
     * Solicitud.
     * @param solicitudAsignacion the solicitudAsignacion to set
     */
    public void setSolicitudAsignacion(SolicitudAsignacionCpsn solicitudAsignacion) {
        this.solicitudAsignacion = solicitudAsignacion;
    }

    /**
     * Tipo de Bloque.
     * @return the tipoBloque
     */
    public TipoBloqueCPSN getTipoBloque() {
        return tipoBloque;
    }

    /**
     * Tipo de Bloque.
     * @param tipoBloque the tipoBloque to set
     */
    public void setTipoBloque(TipoBloqueCPSN tipoBloque) {
        this.tipoBloque = tipoBloque;
    }

}
