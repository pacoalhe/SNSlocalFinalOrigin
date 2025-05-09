package mx.ift.sns.modelo.ng;

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
 * Representa una cesión ejecutada. Contiene la información del trámite que la generó.
 */
@Entity
@Table(name = "NG_CESION")
@SequenceGenerator(name = "SEQ_ID_CESION", sequenceName = "SEQ_ID_CESION", allocationSize = 1)
@NamedQuery(name = "Cesion.findAll", query = "SELECT c FROM Cesion c")
public class Cesion implements Serializable {

    /** Serialización . */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_CESION")
    @Column(name = "ID_NG_CESION", unique = true, nullable = false)
    private BigDecimal id;

    /** Relación: Muchas cesiones pueden pertencer a una misma solicitud de cesión. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL", nullable = false)
    private SolicitudCesionNg solicitudCesion;

    /** Relación: Una Cesión solo puede pertenecer a una misma cesión solicitada. */
    @ManyToOne
    @JoinColumn(name = "ID_CESION_SOLICITADA", nullable = false)
    private CesionSolicitadaNg cesionSolicitada;

    /** Índice de final de rango. */
    @Column(name = "FIN_RANGO", nullable = false, length = 4)
    private String finRango;

    /** Índice de inicio de rango. */
    @Column(name = "INICIO_RANGO", nullable = false, length = 4)
    private String inicioRango;

    /** Constructor, por defecto vacío. */
    public Cesion() {
    }

    /**
     * Identificador de Liberación.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Identificador de Liberación.
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Índice de final de rango.
     * @return String
     */
    public String getFinRango() {
        return this.finRango;
    }

    /**
     * Devuelve el final de rango como un valor int.
     * @return int
     * @throws Exception si no se puede parsear el String.
     */
    public int getFinRangoAsInt() throws Exception {
        return (Integer.valueOf(finRango).intValue());
    }

    /**
     * Índice de final de rango.
     * @param finRango String
     */
    public void setFinRango(String finRango) {
        this.finRango = finRango;
    }

    /**
     * Índice de inicio de rango.
     * @return String
     */
    public String getInicioRango() {
        return this.inicioRango;
    }

    /**
     * Devuelve el inicio de rango como un valor int.
     * @return int
     * @throws Exception si no se puede parsear el String.
     */
    public int getInicioRangoAsInt() throws Exception {
        return (Integer.valueOf(inicioRango).intValue());
    }

    /**
     * Índice de inicio de rango.
     * @param inicioRango String
     */
    public void setInicioRango(String inicioRango) {
        this.inicioRango = inicioRango;
    }

    /**
     * Asociación de la cesión con la solicitud que la originó.
     * @return SolicitudCesionNg
     */
    public SolicitudCesionNg getSolicitudCesion() {
        return solicitudCesion;
    }

    /**
     * Asociación de la cesión con la solicitud que la originó.
     * @param solicitudCesion SolicitudCesionNg
     */
    public void setSolicitudCesion(SolicitudCesionNg solicitudCesion) {
        this.solicitudCesion = solicitudCesion;
    }

    /**
     * Asociación con la Cesión Solicitada.
     * @return CesionSolicitadaNg
     */
    public CesionSolicitadaNg getCesionSolicitada() {
        return cesionSolicitada;
    }

    /**
     * Asociación con la Cesión Solicitada.
     * @param cesionSolicitada CesionSolicitadaNg
     */
    public void setCesionSolicitada(CesionSolicitadaNg cesionSolicitada) {
        this.cesionSolicitada = cesionSolicitada;
    }
}
