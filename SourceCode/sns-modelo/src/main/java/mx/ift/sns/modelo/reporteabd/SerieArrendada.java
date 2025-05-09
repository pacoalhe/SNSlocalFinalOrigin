package mx.ift.sns.modelo.reporteabd;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoModalidad;
import mx.ift.sns.modelo.pst.TipoRed;

/**
 * The persistent class for the REP_ABD_SERIE_ARRENDADA database table.
 */
@Entity
@Table(name = "REP_ABD_SERIE_ARRENDADA")
@NamedQuery(name = "SerieArrendada.findAll", query = "SELECT s FROM SerieArrendada s")
public class SerieArrendada implements Serializable {

    /** Serial ID. */
    private static final long serialVersionUID = 1L;

    /** Id. */
    @EmbeddedId
    private SerieArrendadaPK id;

    /** Proveedor arrendador del rango. Es el dueño del rango. */
    @JoinColumn(name = "ID_PST_ASIGNATARIO", insertable = false, updatable = false)
    private Proveedor asignatario;

    /** Código del abn asociado al nir del rango. */
    @Column(name = "ID_ABN", nullable = false, precision = 3)
    private BigDecimal codigoAbn;

    /** Tipo Red. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_RED", nullable = false)
    private TipoRed tipoRed;

    /** Tipo de modalidad de red Móvil. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_MODALIDAD", nullable = true)
    private TipoModalidad tipoModalidad;

    /** Escenario a aplicar para generar los registros de la tabla padre. */
    @Column(name = "ESCENARIO")
    private Integer escenario;

    /** Identificador del rango serie a aplicar. */
    @Column(name = "ID_RANGO_SERIE", unique = true, nullable = false)
    private BigDecimal idRangoSerie;

    /** Identificador de región numérica. */
    @Column(name = "ID_NIR", unique = true, nullable = false)
    private BigDecimal idNir;

    /** Identificador de serie. */
    @Column(name = "ID_SERIE", unique = true, nullable = false, precision = 5)
    private BigDecimal sna;

    /**
     * Constructor de la clase.
     */
    public SerieArrendada() {
        id = new SerieArrendadaPK();
    }

    /**
     * Id.
     * @return SerieArrendadaPK
     */
    public SerieArrendadaPK getId() {
        return id;
    }

    /**
     * Id.
     * @param id SerieArrendadaPK
     */
    public void setId(SerieArrendadaPK id) {
        this.id = id;
    }

    /**
     * @return the asignatario
     */
    public Proveedor getAsignatario() {
        return asignatario;
    }

    /**
     * @param asignatario the asignatario to set
     */
    public void setAsignatario(Proveedor asignatario) {
        this.asignatario = asignatario;
    }

    /**
     * @return the tipoRed
     */
    public TipoRed getTipoRed() {
        return tipoRed;
    }

    /**
     * @param tipoRed the tipoRed to set
     */
    public void setTipoRed(TipoRed tipoRed) {
        this.tipoRed = tipoRed;
    }

    /**
     * @return the tipoModalidad
     */
    public TipoModalidad getTipoModalidad() {
        return tipoModalidad;
    }

    /**
     * @param tipoModalidad the tipoModalidad to set
     */
    public void setTipoModalidad(TipoModalidad tipoModalidad) {
        this.tipoModalidad = tipoModalidad;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("serie arrendada{");
        b.append("num ini=");
        b.append(id.getNumberFrom());
        b.append("num fin=");
        b.append(id.getNumberTo());
        b.append("pst arrendador=");
        b.append(getAsignatario().getId());
        b.append("}");
        return b.toString();
    }

    /**
     * @return the escenario
     */
    public Integer getEscenario() {
        return escenario;
    }

    /**
     * @param escenario the escenario to set
     */
    public void setEscenario(Integer escenario) {
        this.escenario = escenario;
    }

    /**
     * @return the codigoAbn
     */
    public BigDecimal getCodigoAbn() {
        return codigoAbn;
    }

    /**
     * @param codigoAbn the codigoAbn to set
     */
    public void setCodigoAbn(BigDecimal codigoAbn) {
        this.codigoAbn = codigoAbn;
    }

    /**
     * @return the idRangoSerie
     */
    public BigDecimal getIdRangoSerie() {
        return idRangoSerie;
    }

    /**
     * @param idRangoSerie the idRangoSerie to set
     */
    public void setIdRangoSerie(BigDecimal idRangoSerie) {
        this.idRangoSerie = idRangoSerie;
    }

    /**
     * @return the idNir
     */
    public BigDecimal getIdNir() {
        return idNir;
    }

    /**
     * @param idNir the idNir to set
     */
    public void setIdNir(BigDecimal idNir) {
        this.idNir = idNir;
    }

    /**
     * @return the sna
     */
    public BigDecimal getSna() {
        return sna;
    }

    /**
     * @param sna the sna to set
     */
    public void setSna(BigDecimal sna) {
        this.sna = sna;
    }
}
