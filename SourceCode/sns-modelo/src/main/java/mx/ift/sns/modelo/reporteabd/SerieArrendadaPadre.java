package mx.ift.sns.modelo.reporteabd;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoModalidad;
import mx.ift.sns.modelo.pst.TipoRed;

/**
 * The persistent class for the REP_ABD_SERIE_ARRENDADA_PADRE database table.
 */
@Entity
@Table(name = "REP_ABD_SERIE_ARRENDADA_PADRE")
@NamedQuery(name = "SerieArrendadaPadre.findAll", query = "SELECT s FROM SerieArrendadaPadre s")
public class SerieArrendadaPadre implements Serializable {
    /** Serial ID. */
    private static final long serialVersionUID = 1L;

    /** Pk de la clase. */
    @EmbeddedId
    private SerieArrendadaPadrePK id;

    /** Proovedor arrendatario del rango. Se lo pide al arrendador. */
    @JoinColumn(name = "ID_PST_ASIGNATARIO", insertable = false, updatable = false)
    private Proveedor asignatario;

    /** Number to. */
    @Column(name = "NUMBER_TO", length = 10)
    private String numberTo;

    /** Si tiene mpp. */
    private Boolean mpp;

    /** Tipo de red. */
    @Column(name = "ID_TIPO_RED")
    private String idTipoRed;

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
     * Constructor.
     */
    public SerieArrendadaPadre() {
        id = new SerieArrendadaPadrePK();
    }

    /**
     * Pk de la clase.
     * @return SerieArrendadaPadrePK
     */
    public SerieArrendadaPadrePK getId() {
        return id;
    }

    /**
     * Pk de la clase.
     * @param id SerieArrendadaPadrePK
     */
    public void setId(SerieArrendadaPadrePK id) {
        this.id = id;
    }

    /**
     * Proovedor arrendatario del rango. Se lo pide al arrendador.
     * @return Proveedor
     */
    public Proveedor getAsignatario() {
        return asignatario;
    }

    /**
     * Proovedor arrendatario del rango. Se lo pide al arrendador.
     * @param asignatario Proveedor
     */
    public void setAsignatario(Proveedor asignatario) {
        this.asignatario = asignatario;
        id.setIdPstAsignatario(asignatario.getId());
    }

    /**
     * Number to.
     * @return String
     */
    public String getNumberTo() {
        return numberTo;
    }

    /**
     * Number to.
     * @param numberTo String
     */
    public void setNumberTo(String numberTo) {
        this.numberTo = numberTo;
    }

    /**
     * Si tiene mpp.
     * @return Boolean
     */
    public Boolean getMpp() {
        return mpp;
    }

    /**
     * Si tiene mpp.
     * @param mpp Boolean
     */
    public void setMpp(Boolean mpp) {
        this.mpp = mpp;
    }

    /**
     * Tipo de red.
     * @return String
     */
    public String getIdTipoRed() {
        return idTipoRed;
    }

    /**
     * Tipo de red.
     * @param idTipoRed String
     */
    public void setIdTipoRed(String idTipoRed) {
        this.idTipoRed = idTipoRed;
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

    /**
     * Getter de la descripción del tipo de red.
     * @return descripción del tipo de red
     */
    public String getIdTipoRedDesc() {
        return (TipoRed.FIJA.equals(idTipoRed)) ? TipoRed.FIJA_DESC
                : (TipoRed.MOVIL.equals(idTipoRed)) ? TipoRed.MOVIL_DESC
                        : (TipoRed.AMBAS.equals(idTipoRed)) ? TipoRed.AMBAS_DESC : "Desconocido";
    }

    /**
     * Getter de la descripción de la modalidad.
     * @return descripción de la modalidad
     */
    public String getIdTipoModalidadDesc() {
        return mpp ? TipoModalidad.MPP : "";
    }
}
