package mx.ift.sns.modelo.ot;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.abn.PoblacionAbn;
import mx.ift.sns.modelo.central.Central;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.usu.Auditoria;

/**
 * Representa una Población del Catálogo de Poblaciones de la Organización Territorial.
 */
@Entity
@Table(name = "CAT_POBLACION")
@NamedQuery(name = "Poblacion.findAll", query = "SELECT p FROM Poblacion p")
public class Poblacion extends Auditoria implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @Column(name = "ID_INEGI", unique = true, nullable = false, length = 7)
    private String inegi;

    /** Código de población. */
    @Column(name = "ID_POBLACION", nullable = false, length = 4)
    private String cdgPoblacion;

    /** Relación: Muchas poblaciones pueden pertener al mismo municipio. */
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ID_MUNICIPIO", referencedColumnName = "ID_MUNICIPIO", nullable = false),
            @JoinColumn(name = "ID_ESTADO", referencedColumnName = "ID_ESTADO", nullable = false)
    })
    private Municipio municipio;

    /** Nombre. */
    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    /** Región. */
    @ManyToOne
    @JoinColumn(name = "REGION", nullable = true)
    private Region region;

    /** Fecha de migración. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_MIGRACION")
    private Date fechaMigracion;

    /** Relación: Muchas poblaciones pueden tener el mismo estatus. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS", nullable = false)
    private Estatus estatus;

    /** Relación: Una población puede ser población ancla de muchas áreas de numeración. */
    @OneToMany(mappedBy = "poblacionAncla", fetch = FetchType.LAZY)
    private List<Abn> abns;

    /** Relación: Una población puede estar asociada con muchas centrales. */
    @OneToMany(mappedBy = "poblacion", fetch = FetchType.LAZY)
    private List<Central> centrales;

    /** Relación: Una población puede estar asociada con muchos rangos de series. */
    @OneToMany(mappedBy = "poblacion")
    private List<RangoSerie> rangosSeries;

    /** Código de ABN. */
    @OneToMany(mappedBy = "inegi", fetch = FetchType.EAGER)
    private List<PoblacionAbn> poblacionesAbn;

    /** Código de ABN. */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_INEGI", insertable = false, updatable = false)
    private PoblacionAbn poblacionAbn;

    /** Identificador del NIR anterior al que pertenecía el Rango. */
    @Column(name = "ID_NIR_ANTERIOR", nullable = true, length = 3)
    private BigDecimal idNirAnterior;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor, por defecto vacío. */
    public Poblacion() {
    }

    /**
     * Código INEGI.
     * @return String
     */
    public String getInegi() {
        return inegi;
    }

    /**
     * Código INEGI.
     * @param inegi String
     */
    public void setInegi(String inegi) {
        this.inegi = inegi;
    }

    /**
     * Código de Población.
     * @return String
     */
    public String getCdgPoblacion() {
        return cdgPoblacion;
    }

    /**
     * Código de Población.
     * @param cdgPoblacion String
     */
    public void setCdgPoblacion(String cdgPoblacion) {
        this.cdgPoblacion = cdgPoblacion;
    }

    /**
     * Fecha de migración.
     * @return Date
     */
    public Date getFechaMigracion() {
        return fechaMigracion;
    }

    /**
     * Fecha de migración.
     * @param fechaMigracion Date
     */
    public void setFechaMigracion(Date fechaMigracion) {
        this.fechaMigracion = fechaMigracion;
    }

    /**
     * Estado del registro.
     * @return the estatus
     */
    public Estatus getEstatus() {
        return estatus;
    }

    /**
     * Estado del registro.
     * @param estatus the estatus to set
     */
    public void setEstatus(Estatus estatus) {
        this.estatus = estatus;
    }

    /**
     * Nombre de población.
     * @return String
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Nombre de población.
     * @param nombre String
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Código de Región.
     * @return Region
     */
    public Region getRegion() {
        return region;
    }

    /**
     * Código de Región.
     * @param region Region
     */
    public void setRegion(Region region) {
        this.region = region;
    }

    /**
     * Lista de áreas de numeración a las que pertecene la población como ancla.
     * @return List
     */
    public List<Abn> getAbns() {
        return abns;
    }

    /**
     * Lista de áreas de numeración a las que pertecene la población como ancla.
     * @param abns List
     */
    public void setAbns(List<Abn> abns) {
        this.abns = abns;
    }

    /**
     * Asocia un área de numeración con la población como ancla.
     * @param pAbn Abn
     * @return Abn
     */
    public Abn addAbn(Abn pAbn) {
        this.getAbns().add(pAbn);
        pAbn.setPoblacionAncla(this);
        return pAbn;
    }

    /**
     * Elimina la relación entre el área de numeración y la población ancla.
     * @param pAbn Abn
     * @return Abn
     */
    public Abn removeabn(Abn pAbn) {
        this.getAbns().remove(pAbn);
        pAbn.setPoblacionAncla(null);
        return pAbn;
    }

    /**
     * Municipio al que pertenece la población.
     * @return Municipio
     */
    public Municipio getMunicipio() {
        return municipio;
    }

    /**
     * Municipio al que pertenece la población.
     * @param municipio Municipio
     */
    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    /**
     * Lista de centrales asociadas a la población.
     * @return List
     */
    public List<Central> getCentrales() {
        return this.centrales;
    }

    /**
     * Lista de centrales asociadas a la población.
     * @param centrales List
     */
    public void setCentrales(List<Central> centrales) {
        this.centrales = centrales;
    }

    /**
     * Asocia una central con la población.
     * @param pCentral Central
     * @return Central
     */
    public Central addCentral(Central pCentral) {
        this.getCentrales().add(pCentral);
        pCentral.setPoblacion(this);
        return pCentral;
    }

    /**
     * Elimina la asociación de una central con la población.
     * @param pCentral Central
     * @return Central
     */
    public Central removecentral(Central pCentral) {
        this.getCentrales().remove(pCentral);
        pCentral.setPoblacion(null);
        return pCentral;
    }

    /**
     * Lista de rangos de serie asociados a la población.
     * @return List
     */
    public List<RangoSerie> getRangosSeries() {
        return rangosSeries;
    }

    /**
     * Lista de rangos de serie asociados a la población.
     * @param rangosSeries List
     */
    public void setRangosSeries(List<RangoSerie> rangosSeries) {
        this.rangosSeries = rangosSeries;
    }

    /**
     * Asocia un rango de una serie con la población.
     * @param pRangoSerie RangoSerie
     * @return RangoSerie
     */
    public RangoSerie addRangosSerie(RangoSerie pRangoSerie) {
        this.getRangosSeries().add(pRangoSerie);
        pRangoSerie.setPoblacion(this);
        return pRangoSerie;
    }

    /**
     * Elimina la asociación entre el rango de una serie y la población.
     * @param pRangoSerie RangoSerie
     * @return RangoSerie
     */
    public RangoSerie removeSerRangosSery(RangoSerie pRangoSerie) {
        this.getRangosSeries().remove(pRangoSerie);
        pRangoSerie.setPoblacion(null);
        return pRangoSerie;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Poblacion) && (inegi != null)
                ? inegi.equals(((Poblacion) obj).inegi)
                : (obj == this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((inegi == null) ? 0 : Integer.parseInt(inegi));
        return result;
    }

    /**
     * Método get PoblacionesAbn.
     * @return List<PoblacionAbn>
     */
    public List<PoblacionAbn> getPoblacionesAbn() {
        return poblacionesAbn;
    }

    /**
     * @param poblacionesAbn the poblacionesAbn to set
     */
    public void setPoblacionesAbn(List<PoblacionAbn> poblacionesAbn) {
        this.poblacionesAbn = poblacionesAbn;
    }

    /**
     * Método get del PoblacionAbn.
     * @return poblacionAbn
     */
    public PoblacionAbn getPoblacionAbn() {
        return poblacionAbn;
    }

    /**
     * Método get del Abn.
     * @return Abn
     */
    public Abn getAbn() {
        if (poblacionesAbn.isEmpty()) {
            return null;
        } else {
            return poblacionesAbn.get(0).getAbn();
        }
    }

    /**
     * Método set del Abn.
     * @param poblacionAbn poblacionAbn
     */
    public void setPoblacionAbn(PoblacionAbn poblacionAbn) {
        this.poblacionAbn = poblacionAbn;
        poblacionAbn.setInegi(this);
    }

    /**
     * Método set del Abn.
     * @param abn abn
     */
    public void setAbn(Abn abn) {

        if (poblacionesAbn == null || poblacionesAbn.size() == 0) {
            poblacionesAbn = new ArrayList<PoblacionAbn>();
            PoblacionAbn pobAbn = new PoblacionAbn();
            pobAbn.setAbn(abn);
            poblacionesAbn.add(pobAbn);
        } else {
            poblacionesAbn.get(0).setAbn(abn);
        }
    }

    @Override
    public String toString() {

        StringBuilder b = new StringBuilder();

        b.append("poblacion={ ");
        b.append(inegi);
        b.append(" '");
        b.append(nombre);
        b.append("'}");

        return b.toString();
    }

    /**
     * @return the idNirAnterior
     */
    public BigDecimal getIdNirAnterior() {
        return idNirAnterior;
    }

    /**
     * @param idNirAnterior the idNirAnterior to set
     */
    public void setIdNirAnterior(BigDecimal idNirAnterior) {
        this.idNirAnterior = idNirAnterior;
    }
}
