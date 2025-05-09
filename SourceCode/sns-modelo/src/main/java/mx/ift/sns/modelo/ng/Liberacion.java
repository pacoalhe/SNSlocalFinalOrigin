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
 * Representa una liberación ejecutada. Contiene la información del trámite que la generó.
 */
@Entity
@Table(name = "NG_LIBERACION")
@SequenceGenerator(name = "SEQ_ID_LIBERACION", sequenceName = "SEQ_ID_LIBERACION", allocationSize = 1)
@NamedQuery(name = "Liberacion.findAll", query = "SELECT l FROM Liberacion l")
public class Liberacion implements Serializable {

    /** Serializacion . */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_LIBERACION")
    @Column(name = "ID_NG_LIBERACION", unique = true, nullable = false)
    private BigDecimal id;

    /** Relación: Una solicitud puede tener muchas liberaciones aplicadas. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL", nullable = false)
    private SolicitudLiberacionNg solicitudLiberacion;

    /** Relación: Una liberación solo puede pertenecer a un liberación solicitada. */
    @ManyToOne
    @JoinColumn(name = "ID_LIBERACIONES_SOLICITADAS", nullable = false)
    private LiberacionSolicitadaNg liberacionSolicitada;

    /** Índice de final de rango. */
    @Column(name = "FIN_RANGO", nullable = false, length = 4)
    private String finRango;

    /** Índice de inicio de rango. */
    @Column(name = "INICIO_RANGO", nullable = false, length = 4)
    private String inicioRango;

    /** Constructor, por defecto vacío. */
    public Liberacion() {
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
     * Índice de inicio de rango.
     * @param inicioRango String
     */
    public void setInicioRango(String inicioRango) {
        this.inicioRango = inicioRango;
    }

    /**
     * Asociación de la liberación con la solicitud que la originó.
     * @return solicitudLiberacion
     */
    public SolicitudLiberacionNg getSolicitudLiberacion() {
        return solicitudLiberacion;
    }

    /**
     * Asociación de la liberación con la solicitud que la originó.
     * @param solicitudLiberacion SolicitudLiberacionNg
     */
    public void setSolicitudLiberacion(SolicitudLiberacionNg solicitudLiberacion) {
        this.solicitudLiberacion = solicitudLiberacion;
    }

    /**
     * Asociación de la liberación con la Liberación solicitada.
     * @return liberacionSolicitada
     */
    public LiberacionSolicitadaNg getLiberacionSolicitada() {
        return liberacionSolicitada;
    }

    /**
     * Asociación de la liberación con la Liberación solicitada.
     * @param liberacionSolicitada LiberacionSolicitadaNg
     */
    public void setLiberacionSolicitada(LiberacionSolicitadaNg liberacionSolicitada) {
        this.liberacionSolicitada = liberacionSolicitada;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("liberacion={");
        builder.append("id=").append(this.id);
        builder.append(", numInicio=").append(this.inicioRango);
        builder.append(", numFinal=").append(this.finRango);
        builder.append("}");
        return builder.toString();
    }
}
