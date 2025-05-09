package mx.ift.sns.modelo.lineas;

import java.io.Serializable;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Representa el reporte de línea de arrendador.
 */
@Entity
@Table(name = "REP_LINEA_ARRENDADOR")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("4")
@NamedQuery(name = "LineaArrendada.findAll", query = "SELECT l FROM LineaArrendada l")
public class LineaArrendada extends Reporte implements Serializable {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public LineaArrendada() {

    }

    /**
     * bi-directional many-to-one association to DetLineaActiva.
     */
    @OneToMany(mappedBy = "lineaArrendada")
    private List<DetLineaArrendada> repDetLineasArrendada;

    /**
     * bi-directional many-to-one association to DetLineaActiva.
     * @return the repDetLineasArrendada
     */
    public List<DetLineaArrendada> getRepDetLineasArrendada() {
        return repDetLineasArrendada;
    }

    /**
     * bi-directional many-to-one association to DetLineaActiva.
     * @param repDetLineasArrendada the repDetLineasArrendada to set
     */
    public void setRepDetLineasArrendada(List<DetLineaArrendada> repDetLineasArrendada) {
        this.repDetLineasArrendada = repDetLineasArrendada;
    }

    /**
     * Add repDetLineasArrendada.
     * @param detLineasArrendada detalle
     * @return DetRepLineaArrendada
     */
    public DetLineaArrendada addRepDetLineasArrendada(DetLineaArrendada detLineasArrendada) {
        getRepDetLineasArrendada().add(detLineasArrendada);
        detLineasArrendada.setLineaArrendada(this);

        return detLineasArrendada;
    }

    /**
     * Remove repDetLineasArrendada.
     * @param detLineasArrendada detalle
     * @return DetRepLineaArrendada
     */
    public DetLineaArrendada removeRepDetLineasActiva(DetLineaArrendada detLineasArrendada) {
        getRepDetLineasArrendada().remove(detLineasArrendada);
        detLineasArrendada.setLineaArrendada(null);

        return detLineasArrendada;
    }

}
