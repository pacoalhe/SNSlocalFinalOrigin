package mx.ift.sns.modelo.nng;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.ng.RangoSerie;

/**
 * Representa una Serie de Numeración No Geográfica. Contiene información básica de la Serie y la numeración contenida.
 */
@Entity
@Table(name = "CAT_SERIE_NNG")
@NamedQuery(name = "SerieNng.findAll", query = "SELECT s FROM SerieNng s")
public class SerieNng implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Tamaño maximo del SNA. */
    public static final int TAM_SNA = 3;

    /** Minimo codigo sna no geografica posible. */
    public static final int MIN_SERIE_NNG = 0;

    /** Maximo codigo sna no geografica posible. */
    public static final int MAX_SERIE_NNG = 999;

    /** Clave Primaria. */
    @EmbeddedId
    private SerieNngPK id;

    /** Relación: Una serie puede tener varios estados. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS", nullable = false)
    private Estatus estatus;

    /** Relación: Una serie puede tener muchos identificadores de región. */
    @ManyToOne
    @JoinColumn(name = "ID_CLAVE_SERVICIO", nullable = false)
    private ClaveServicio claveServicio;

    /** Relación: Una serie puede tener muchos rangos. */
    @OneToMany(mappedBy = "serie")
    private List<RangoSerieNng> rangos;

    /** Constructor. Por defecto vacío. */
    public SerieNng() {

    }

    // GETTERS & SETTERS

    /**
     * Clave Primaria.
     * @return SerieNngPK
     */
    public SerieNngPK getId() {
        return id;
    }

    /**
     * Clave Primaria.
     * @param id SerieNngPK
     */
    public void setId(SerieNngPK id) {
        this.id = id;
    }

    /**
     * Estatus de la Serie.
     * @return Estatus
     */
    public Estatus getEstatus() {
        return estatus;
    }

    /**
     * Estatus de la Serie.
     * @param estatus Estatus
     */
    public void setEstatus(Estatus estatus) {
        this.estatus = estatus;
    }

    /**
     * Clave de servicio asociada a la serie.
     * @return ClaveServicio
     */
    public ClaveServicio getClaveServicio() {
        return claveServicio;
    }

    /**
     * Clave de servicio asociada a la serie.
     * @param claveServicio ClaveServicio
     */
    public void setClaveServicio(ClaveServicio claveServicio) {
        this.claveServicio = claveServicio;
    }

    /**
     * Lista de rangos asociados a la serie.
     * @return List
     */
    public List<RangoSerieNng> getRangos() {
        return rangos;
    }

    /**
     * Lista de rangos asociados a la serie.
     * @param rangos List
     */
    public void setRangos(List<RangoSerieNng> rangos) {
        this.rangos = rangos;
    }

    /**
     * Asocia un rango con la serie.
     * @param rango RangoSerie
     * @return RangoSerie
     */
    public RangoSerieNng addRango(RangoSerieNng rango) {
        getRangos().add(rango);
        rango.setSerie(this);
        return rango;
    }

    /**
     * Elimina la relación entre un rango y la serie.
     * @param rango RangoSerie
     * @return RangoSerie
     */
    public RangoSerie removeRango(RangoSerie rango) {
        getRangos().remove(rango);
        rango.setSerie(null);
        return rango;
    }

    /**
     * Método que añade al SNA ceros.
     * @return String
     * @throws Exception Exception
     */
    public String getSnaAsString() throws Exception {

        return String.format("%03d", this.getId().getSna().intValue());

    }

    @Override
    public boolean equals(Object other) {

        if (!(other instanceof SerieNng)) {
            return false;
        }
        SerieNng castOther = (SerieNng) other;
        return (this.claveServicio.getCodigo().intValue() == castOther.claveServicio.getCodigo().intValue())
                && (this.id.getSna().intValue() == castOther.id.getSna().intValue());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SerieNNG = {ClaveServicio=");
        builder.append(claveServicio.getCodigo());
        builder.append(", Sna=");
        builder.append(id.getSna());
        if (estatus != null) {
            builder.append("  estado=");
            builder.append(estatus.getCdg());
        }
        builder.append("}");
        return builder.toString();
    }

}
