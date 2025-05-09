package mx.ift.sns.modelo.ng;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.modelo.usu.Auditoria;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.annotations.Direction;
import org.eclipse.persistence.annotations.NamedStoredProcedureQuery;
import org.eclipse.persistence.annotations.StoredProcedureParameter;

/**
 * Representa una Serie de Numeración Geográfica. Contiene información básica de la Serie y la numeración contenida.
 */
@NamedStoredProcedureQuery(name = "actualizarSerie_pa", procedureName = "ACTUALIZARSERIE_PA", parameters = {
        @StoredProcedureParameter(queryParameter = "serie_original", direction = Direction.IN,
                name = "serie_original", type = Number.class),
        @StoredProcedureParameter(queryParameter = "nir_original", direction = Direction.IN,
                name = "nir_original", type = Number.class),
        @StoredProcedureParameter(queryParameter = "serie_nueva", direction = Direction.IN,
                name = "serie_nueva", type = Number.class),
        @StoredProcedureParameter(queryParameter = "usuario", direction = Direction.IN,
                name = "usuario", type = Number.class),
        @StoredProcedureParameter(queryParameter = "salida", direction = Direction.OUT,
                name = "salida", type = String.class)})
@Entity
@Table(name = "CAT_SERIE")
@NamedQuery(name = "Serie.findAll", query = "SELECT s FROM Serie s")
public class Serie extends Auditoria implements Serializable {

    /** Seralización. */
    private static final long serialVersionUID = 1L;

    /** Tamaño maximo del campo sna. */
    public static final int TAM_SNA = 5;

    /** Minimo codigo sna posible. No puede empezar por 0. */
    public static final int MIN_SERIE = 100;

    /** Maximo codigo sna posible. */
    public static final int MAX_SERIE = 9999;

    /** Clave primaria compuesta. */
    @EmbeddedId
    private SeriePK id;

    /** Relación: Una serie puede tener muchos rangos. */
    @OneToMany(mappedBy = "serie")
    private List<RangoSerie> rangos;

    /** Relación: Una serie puede tener muchos identificadores de región. */
    @ManyToOne
    @JoinColumn(name = "ID_NIR", nullable = false)
    private Nir nir;

    /** Relación: Una serie puede tener varios estados. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS", nullable = false)
    private Estatus estatus;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor, por defecto vacío. */
    public Serie() {
    }

    /**
     * Clave primaria.
     * @return SeriePK
     */
    public SeriePK getId() {
        return this.id;
    }

    /**
     * Clave primaria.
     * @param id SeriePK
     */
    public void setId(SeriePK id) {
        this.id = id;
    }

    /**
     * Lista de rangos asociados a la serie.
     * @return List
     */
    public List<RangoSerie> getRangos() {
        return rangos;
    }

    /**
     * Lista de rangos asociados a la serie.
     * @param rangos List
     */
    public void setRangos(List<RangoSerie> rangos) {
        this.rangos = rangos;
    }

    /**
     * Asocia un rango con la serie.
     * @param rango RangoSerie
     * @return RangoSerie
     */
    public RangoSerie addRango(RangoSerie rango) {
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
     * Identificador de Región.
     * @return Nir
     */
    public Nir getNir() {
        return nir;
    }

    /**
     * Identificador de Región.
     * @param nir Nir
     */
    public void setNir(Nir nir) {
        this.nir = nir;
    }

    /**
     * Estado de la serie.
     * @return Estatus
     */
    public Estatus getEstatus() {
        return estatus;
    }

    /**
     * Estado de la serie.
     * @param estatus Estatus
     */
    public void setEstatus(Estatus estatus) {
        this.estatus = estatus;
    }

    @Override
    public boolean equals(Object obj) {
        if (this.id.equals(((Serie) obj).getId())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Método que añade al SNA tantos 0 a la izquierda dependiendo del tamaño del NIR.
     * @return String
     * @throws Exception Exception
     */
    public String getSnaAsString() throws Exception {
        if (String.valueOf(this.nir.getCodigo()).length() == 3) {
            return StringUtils.leftPad(this.getId().getSna().toString(), 3, '0');
        } else if (String.valueOf(this.nir.getCodigo()).length() == 2) {
            return StringUtils.leftPad(this.getId().getSna().toString(), 4, '0');
        } else {
            return StringUtils.leftPad(this.getId().getSna().toString(), 5, '0');
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("serie={idnir=");
        builder.append(id.getIdNir());

        builder.append("  sna=");
        builder.append(id.getSna());

        if (estatus != null) {
            builder.append("  estado=");
            builder.append(estatus.getCdg());
        }

        builder.append("}");

        return builder.toString();
    }
}
