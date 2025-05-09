package mx.ift.sns.modelo.abn;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import mx.ift.sns.modelo.ng.SolicitudConsolidacion;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.modelo.usu.Auditoria;

/**
 * Representa un Área Básica de Numeración.
 */
@Entity
@Table(name = "CAT_ABN")
@NamedQuery(name = "Abn.findAll", query = "SELECT a FROM Abn a")
public class Abn extends Auditoria implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Minimo código abn posible. */
    public static final int MIN_ABN = 1;

    /** Maximo código abn posible. */
    public static final int MAX_ABN = 999;

    /** Identificador de ABN. */
    @Id
    // @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_ABN", unique = true, nullable = false, precision = 3)
    private BigDecimal codigoAbn;

    /** Fecha consolidación. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_CONSOLIDACION")
    private Date fechaConsolidacion;

    /** Descripción de ABN. */
    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    /** Relación: Muchos estatus de ABN pueden tener el mismo estatus. */
    @ManyToOne
    @JoinColumn(name = "CAT_ESTATUS_ABN_ID_ESTATUS_ABN", nullable = false)
    private EstadoAbn estadoAbn;

    /** Relación: Muchas poblaciones pueden ser población ancla de un ABN. */
    @ManyToOne
    @JoinColumn(name = "CAT_POBLACION_ID_INEGI", nullable = false)
    private Poblacion poblacionAncla;

    /** Relación: Un mismo ABN puede estar relacionado con muchas Centrales. */
    @OneToMany(mappedBy = "abn")
    private List<AbnCentral> abnsCentrales;

    /** Relación: Un mismo ABN puede estar relacionado con muchos NIRs. */
    @OneToMany(mappedBy = "abn")
    private List<Nir> nirs;

    /** Relación: Un mismo ABN puede puede estar relacionado con muchas Solicitudes de Consolidación. */
    @OneToMany(mappedBy = "abnEntrega")
    private List<SolicitudConsolidacion> solicitudesConsolidacionEntrega;

    /** Relación: Un mismo ABN puede puede estar relacionado con muchas Solicitudes de Consolidación. */
    @OneToMany(mappedBy = "abnRecibe")
    private List<SolicitudConsolidacion> solicitudesConsolidacionRecibe;

    /** Relación: Un mismo ABN puede puede estar relacionado con muchas Poblaciones. */
    @OneToMany(mappedBy = "abn")
    private List<PoblacionAbn> poblaciones;

    /** Presuscripción. */
    @Column(name = "PRESUSCRIPCION", length = 1)
    private String presuscripcion;

    /** Version JPA. */
    @Version
    private long version;

    /**
     * Constructor.
     */
    public Abn() {
    }

    /**
     * Presuscripción.
     * @return String
     */
    public String getPresuscripcion() {
        return presuscripcion;
    }

    /**
     * Presuscripción.
     * @param presuscripcion String
     */
    public void setPresuscripcion(String presuscripcion) {
        this.presuscripcion = presuscripcion;
    }

    /**
     * Listado de NIRs asociados al ABN.
     * @return List<Nir>
     */
    public List<Nir> getNirs() {
        return nirs;
    }

    /**
     * Listado de NIRs asociados al ABN.
     * @param nirs List<Nir>
     */
    public void setNirs(List<Nir> nirs) {
        this.nirs = nirs;
    }

    /**
     * Código de ABN.
     * @return BigDecimal
     */
    public BigDecimal getCodigoAbn() {
        return codigoAbn;
    }

    /**
     * Código de ABN.
     * @param codigoAbn BigDecimal
     */
    public void setCodigoAbn(BigDecimal codigoAbn) {
        this.codigoAbn = codigoAbn;
    }

    /**
     * Fecha de la última consolidación del ABN.
     * @return Date
     */
    public Date getFechaConsolidacion() {
        return fechaConsolidacion;
    }

    /**
     * Fecha de la última consolidación del ABN.
     * @param fechaConsolidacion Date
     */
    public void setFechaConsolidacion(Date fechaConsolidacion) {
        this.fechaConsolidacion = fechaConsolidacion;
    }

    /**
     * Descripción de ABN.
     * @return String
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Descripción de ABN.
     * @param nombre String
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Estatus del ABN en el catálogo de ABNs.
     * @return EstadoAbn
     */
    public EstadoAbn getEstadoAbn() {
        return estadoAbn;
    }

    /**
     * Estatus del ABN en el catálogo de ABNs.
     * @param estadoAbn EstadoAbn
     */
    public void setEstadoAbn(EstadoAbn estadoAbn) {
        this.estadoAbn = estadoAbn;
    }

    /**
     * Población ancla del ABN.
     * @return Poblacion
     */
    public Poblacion getPoblacionAncla() {
        return poblacionAncla;
    }

    /**
     * Población ancla del ABN.
     * @param poblacionAncla Poblacion
     */
    public void setPoblacionAncla(Poblacion poblacionAncla) {
        this.poblacionAncla = poblacionAncla;
    }

    /**
     * Listado de Centrales asociadas al ABN.
     * @return lList<AbnCentral>
     */
    public List<AbnCentral> getAbnsCentrales() {
        return this.abnsCentrales;
    }

    /**
     * Listado de Centrales asociadas al ABN.
     * @param abnsCentrales List<AbnCentral>
     */
    public void setAbnsCentrales(List<AbnCentral> abnsCentrales) {
        this.abnsCentrales = abnsCentrales;
    }

    /**
     * Asocia una central con el ABN.
     * @param abnCentral Información de la Central.
     * @return abnCentral AbnCentral
     */
    public AbnCentral addAbnCentral(AbnCentral abnCentral) {
        getAbnsCentrales().add(abnCentral);
        abnCentral.setAbn(this);
        return abnCentral;
    }

    /**
     * Desasocia una central con el ABN.
     * @param abnCentral Información de la Central.
     * @return abnCentral AbnCentral
     */
    public AbnCentral removeAbnCentral(AbnCentral abnCentral) {
        getAbnsCentrales().remove(abnCentral);
        abnCentral.setAbn(null);
        return abnCentral;
    }

    /**
     * Lista de solicitudes de consolidación como ABN que entrega numeración.
     * @return List<SolicitudConsolidacion>
     */
    public List<SolicitudConsolidacion> getSolicitudesConsolidacionEntrega() {
        return solicitudesConsolidacionEntrega;
    }

    /**
     * Lista de solicitudes de consolidación como ABN que entrega numeración.
     * @param solicitudesConsolidacionEntrega List<SolicitudConsolidacion>
     */
    public void setSolicitudesConsolidacionEntrega(
            List<SolicitudConsolidacion> solicitudesConsolidacionEntrega) {
        this.solicitudesConsolidacionEntrega = solicitudesConsolidacionEntrega;
    }

    /**
     * Lista de solicitudes de consolidación como ABN que recibe numeración.
     * @param solicitudesConsolidacionRecibe List<SolicitudConsolidacion>
     */
    public void setSolicitudesConsolidacionRecibe(
            List<SolicitudConsolidacion> solicitudesConsolidacionRecibe) {
        this.solicitudesConsolidacionRecibe = solicitudesConsolidacionRecibe;
    }

    /**
     * Lista de solicitudes de consolidación como ABN que recibe numeración.
     * @return List<SolicitudConsolidacion>
     */
    public List<SolicitudConsolidacion> getSolicitudesConsolidacionRecibe() {
        return solicitudesConsolidacionRecibe;
    }

    /**
     * Asocia una Solicitud de consolidación con el ABN como abn que entrega numeración.
     * @param solicitudesConsolidacion Información de la solicitud de consolidación.
     * @return SolicitudConsolidacion
     */
    public SolicitudConsolidacion addSolicitudesConsolidacionEntrega(SolicitudConsolidacion solicitudesConsolidacion) {
        getSolicitudesConsolidacionEntrega().add(solicitudesConsolidacion);
        solicitudesConsolidacion.setAbnEntrega(this);

        return solicitudesConsolidacion;
    }

    /**
     * Desasocia una Solicitud de consolidación con el ABN como abn que entrega numeración.
     * @param solicitudesConsolidacion Información de la solicitud de consolidación.
     * @return SolicitudConsolidacion
     */
    public SolicitudConsolidacion removeSolicitudesConsolidacionEntrega(
            SolicitudConsolidacion solicitudesConsolidacion) {
        getSolicitudesConsolidacionEntrega().remove(solicitudesConsolidacion);
        solicitudesConsolidacion.setAbnEntrega(null);
        return solicitudesConsolidacion;
    }

    /**
     * Asocia una Solicitud de consolidación con el ABN como abn que recibe numeración.
     * @param solicitudesConsolidacion Información de la solicitud de consolidación.
     * @return SolicitudConsolidacion
     */
    public SolicitudConsolidacion addSolicitudesConsolidacionRecibe(SolicitudConsolidacion solicitudesConsolidacion) {
        getSolicitudesConsolidacionRecibe().add(solicitudesConsolidacion);
        solicitudesConsolidacion.setAbnRecibe(this);
        return solicitudesConsolidacion;
    }

    /**
     * Desasocia una Solicitud de consolidación con el ABN como abn que recibe numeración.
     * @param solicitudesConsolidacion Información de la solicitud de consolidación.
     * @return SolicitudConsolidacion
     */
    public SolicitudConsolidacion removeSolicitudesConsolidacion(SolicitudConsolidacion solicitudesConsolidacion) {
        getSolicitudesConsolidacionRecibe().remove(solicitudesConsolidacion);
        solicitudesConsolidacion.setAbnRecibe(null);
        return solicitudesConsolidacion;
    }

    /**
     * Lista de poblaciones asociadas al ABN.
     * @return List<PoblacionAbn>
     */
    public List<PoblacionAbn> getPoblaciones() {
        return poblaciones;
    }

    /**
     * Lista de poblaciones asociadas al ABN.
     * @param poblaciones List<PoblacionAbn>
     */
    public void setPoblaciones(List<PoblacionAbn> poblaciones) {
        this.poblaciones = poblaciones;
    }

    /**
     * Asocia una población con el ABN.
     * @param poblacionAbn Información de la población.
     * @return PoblacionAbn
     */
    public PoblacionAbn addPoblacion(PoblacionAbn poblacionAbn) {
        getPoblaciones().add(poblacionAbn);
        poblacionAbn.setAbn(this);

        return poblacionAbn;
    }

    /**
     * Desasocia una población con el ABN.
     * @param poblacion Información de la población.
     * @return PoblacionAbn
     */
    public PoblacionAbn removePoblacion(PoblacionAbn poblacion) {
        getPoblaciones().remove(poblacion);
        poblacion.setAbn(null);

        return poblacion;
    }

    /**
     * Asocia un NIR con el ABN.
     * @param nir Información del NIR.
     * @return Nir
     */
    public Nir addNir(Nir nir) {
        getNirs().add(nir);
        nir.setAbn(this);
        return nir;
    }

    /**
     * Dessocia un NIR con el ABN.
     * @param nir Información del NIR.
     * @return Nir
     */
    public Nir removeNir(Nir nir) {
        getNirs().remove(nir);
        nir.setAbn(null);
        return nir;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("abn={ ");
        b.append(codigoAbn);
        b.append(" ");
        b.append(nombre);
        b.append("}");
        return b.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((codigoAbn == null) ? 0 : codigoAbn.hashCode());
        result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Abn) {
            Abn other = (Abn) obj;
            return (other.codigoAbn.intValue() == this.codigoAbn.intValue());
        } else {
            return false;
        }
    }

}
