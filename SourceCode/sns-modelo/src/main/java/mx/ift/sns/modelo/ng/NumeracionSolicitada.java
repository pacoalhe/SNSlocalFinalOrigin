package mx.ift.sns.modelo.ng;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import mx.ift.sns.modelo.central.Central;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoModalidad;
import mx.ift.sns.modelo.pst.TipoRed;

import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Representa la numeración solicitada de numeración geográfica.
 */
@Entity
@Table(name = "NG_NUM_SOLICITADA")
@SequenceGenerator(name = "SEQ_ID_NUMS_SOLI", sequenceName = "SEQ_ID_NUMS_SOLI", allocationSize = 1)
@NamedQuery(name = "NumeracionSolicitada.findAll", query = "SELECT n FROM NumeracionSolicitada n")
public class NumeracionSolicitada implements Serializable {
    /**
     * Serial ID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_NUMS_SOLI")
    @Column(name = "ID_NG_NUM_SOLICITADA", unique = true, nullable = false)
    private BigDecimal id;

    /** Cantidad asignada. */
    @Column(name = "CANT_ASIGNADA", precision = 10)
    private BigDecimal cantAsignada;

    /** Cantidad solicitada. */
    @Column(name = "CANT_SOLICITADA", nullable = false, precision = 5)
    private BigDecimal cantSolicitada;

    /** Numeraciones asignadas. */
    @OneToMany(mappedBy = "numeracionSolicitada")
    @PrivateOwned
    private List<NumeracionAsignada> numsAsignadas;

    /** Central de Origen. */
    @ManyToOne
    @JoinColumn(name = "ID_CENTRAL_ORIGEN", nullable = false)
    private Central centralOrigen;

    /** Central destino. */
    @ManyToOne
    @JoinColumn(name = "ID_CENTRAL_DESTINO", nullable = false)
    private Central centralDestino;

    /** Solicitud. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL", nullable = false)
    private SolicitudAsignacion solicitudAsignacion;

    /** Poblacion. */
    @ManyToOne
    @JoinColumn(name = "ID_POBLACION", nullable = false)
    private Poblacion poblacion;

    /** Concesionario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_CONCESIONARIO", nullable = false)
    private Proveedor concesionario;

    /** Arrendatario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_ARRENDATARIO", nullable = false)
    private Proveedor arrendatario;

    /** Tipo modalidad. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_MODALIDAD", nullable = false)
    private TipoModalidad tipoModalidad;

    /** Tipo red. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_RED", nullable = false)
    private TipoRed tipoRed;

    /** Código IDA. */
    @Column(name = "IDA_PNN", precision = 3)
    private BigDecimal idaPnn;

    /** Código IDO. */
    @Column(name = "IDO_PNN", precision = 3)
    private BigDecimal idoPnn;

    /** Relación: Una numeración solicitada puede contener muchos rangos. */
    // bi-directional many-to-one association to RangoSerie
    @OneToMany(mappedBy = "numSolicitada", cascade = CascadeType.REFRESH)
    private List<RangoSerie> rangos = new ArrayList<RangoSerie>();

    /** Constructor. */
    public NumeracionSolicitada() {
    }

    /**
     * @return the concesionario
     */
    public Proveedor getConcesionario() {
        return concesionario;
    }

    /**
     * @param concesionario the concesionario to set
     */
    public void setConcesionario(Proveedor concesionario) {
        this.concesionario = concesionario;
    }

    /**
     * Relación: Una numeración solicitada puede contener muchos rangos.
     * @return the rangos
     */
    public List<RangoSerie> getRangos() {
        return rangos;
    }

    /**
     * Relación: Una numeración solicitada puede contener muchos rangos.
     * @param rangos the rangos to set
     */
    public void setRangos(List<RangoSerie> rangos) {
        this.rangos = rangos;
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
     * Cantidad asignada.
     * @return the cantAsignada
     */
    public BigDecimal getCantAsignada() {
        return cantAsignada;
    }

    /**
     * Cantidad asignada.
     * @param cantAsignada the cantAsignada to set
     */
    public void setCantAsignada(BigDecimal cantAsignada) {
        this.cantAsignada = cantAsignada;
    }

    /**
     * Cantidad asignada.
     * @return the cantSolicitada
     */
    public BigDecimal getCantSolicitada() {
        return cantSolicitada;
    }

    /**
     * Cantidad asignada.
     * @param cantSolicitada the cantSolicitada to set
     */
    public void setCantSolicitada(BigDecimal cantSolicitada) {
        this.cantSolicitada = cantSolicitada;
    }

    /**
     * Numeraciones asignadas.
     * @return the numsAsignadas
     */
    public List<NumeracionAsignada> getNumsAsignadas() {
        return numsAsignadas;
    }

    /**
     * Numeraciones asignadas.
     * @param numsAsignadas the numsAsignadas to set
     */
    public void setNumsAsignadas(List<NumeracionAsignada> numsAsignadas) {
        this.numsAsignadas = numsAsignadas;
    }

    /**
     * Añade una numeracion asignada a la numeracion solicitada.
     * @param numsAsignada NumeracionAsignada
     * @return NumeracionAsignada
     */
    public NumeracionAsignada addNumsAsignada(NumeracionAsignada numsAsignada) {
        getNumsAsignadas().add(numsAsignada);
        numsAsignada.setNumeracionSolicitada(this);

        return numsAsignada;
    }

    /**
     * Elimina una numeracion asignada a la numeracion solicitada.
     * @param numsAsignada NumeracionAsignada
     * @return NumeracionAsignada
     */
    public NumeracionAsignada removeNumsAsignada(NumeracionAsignada numsAsignada) {
        getNumsAsignadas().remove(numsAsignada);
        numsAsignada.setNumeracionSolicitada(null);

        return numsAsignada;
    }

    /**
     * Central de Origen.
     * @return the centralOrigen
     */
    public Central getCentralOrigen() {
        return centralOrigen;
    }

    /**
     * Central de Origen.
     * @param centralOrigen the centralOrigen to set
     */
    public void setCentralOrigen(Central centralOrigen) {
        this.centralOrigen = centralOrigen;
    }

    /**
     * Central destino.
     * @return the centralDestino
     */
    public Central getCentralDestino() {
        return centralDestino;
    }

    /**
     * Central destino.
     * @param centralDestino the centralDestino to set
     */
    public void setCentralDestino(Central centralDestino) {
        this.centralDestino = centralDestino;
    }

    /**
     * Solicitud.
     * @return the solicitudAsignacion
     */
    public SolicitudAsignacion getSolicitudAsignacion() {
        return solicitudAsignacion;
    }

    /**
     * Solicitud.
     * @param solicitudAsignacion the solicitudAsignacion to set
     */
    public void setSolicitudAsignacion(SolicitudAsignacion solicitudAsignacion) {
        this.solicitudAsignacion = solicitudAsignacion;
    }

    /**
     * Poblacion.
     * @return the poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * Poblacion.
     * @param poblacion the poblacion to set
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * Arrendatario.
     * @return the arrendatario
     */
    public Proveedor getArrendatario() {
        return arrendatario;
    }

    /**
     * Arrendatario.
     * @param arrendatario the arrendatario to set
     */
    public void setArrendatario(Proveedor arrendatario) {
        this.arrendatario = arrendatario;
    }

    /**
     * Tipo modalidad.
     * @return the tipoModalidad
     */
    public TipoModalidad getTipoModalidad() {
        return tipoModalidad;
    }

    /**
     * Tipo modalidad.
     * @param tipoModalidad the tipoModalidad to set
     */
    public void setTipoModalidad(TipoModalidad tipoModalidad) {
        this.tipoModalidad = tipoModalidad;
    }

    /**
     * Tipo red.
     * @return the tipoRed
     */
    public TipoRed getTipoRed() {
        return tipoRed;
    }

    /**
     * Tipo red.
     * @param tipoRed the tipoRed to set
     */
    public void setTipoRed(TipoRed tipoRed) {
        this.tipoRed = tipoRed;
    }

    /**
     * Código IDA.
     * @return the idaPnn
     */
    public BigDecimal getIdaPnn() {
        return idaPnn;
    }

    /**
     * Código IDA.
     * @param idaPnn the idaPnn to set
     */
    public void setIdaPnn(BigDecimal idaPnn) {
        this.idaPnn = idaPnn;
    }

    /**
     * Código IDO.
     * @return the idoPnn
     */
    public BigDecimal getIdoPnn() {
        return idoPnn;
    }

    /**
     * Código IDO.
     * @param idoPnn the idoPnn to set
     */
    public void setIdoPnn(BigDecimal idoPnn) {
        this.idoPnn = idoPnn;
    }

    /**
     * Añade un nuevo rango a la solicitud.
     * @param rango RangoSerie
     * @return RangoSerie
     */
    public RangoSerie addRango(RangoSerie rango) {
        this.getRangos().add(rango);
        rango.setNumSolicitada(this);
        return rango;
    }

    /**
     * Elimina la referencia del rango a la solicitud.
     * @param rango RangoSerie
     * @return RangoSerie
     */
    public RangoSerie removeRango(RangoSerie rango) {
        this.getRangos().remove(rango);
        rango.setNumSolicitada(null);
        return rango;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        NumeracionSolicitada numSol = null;
        if (!(obj instanceof NumeracionSolicitada)) {
            return false;
        } else {
            numSol = (NumeracionSolicitada) obj;
        }

        String codArrendatario = "null";
        if (numSol.getArrendatario() != null) {
            codArrendatario = numSol.getArrendatario().getCdgPst().toString();
        }
        String codConcesionario = "null";
        if (numSol.getConcesionario() != null) {
            codConcesionario = numSol.getConcesionario().getCdgPst().toString();
        }
        String tipoModalidad = "null";
        if (numSol.getTipoModalidad() != null) {
            tipoModalidad = numSol.getTipoModalidad().getCdg();
        }

        String key = numSol.getTipoRed().getCdg() + "-" + numSol.getCantSolicitada().toString() + "-"
                + numSol.getPoblacion().getInegi() + "-" + numSol.getCentralOrigen().getId().toString() + "-"
                + numSol.getCentralDestino().getId().toString() + "-" + codArrendatario + "-" + codConcesionario
                + "-" + tipoModalidad;

        String[] clave = key.split("-");

        if (this.getTipoRed().getCdg().equals(clave[0])
                && this.getCantSolicitada().toString().equals(clave[1])
                && this.getPoblacion().getInegi().equals(clave[2])
                && this.getCentralOrigen().getId().toString().equals(clave[3])
                && this.getCentralDestino().getId().toString().equals(clave[4])) {
            boolean encontradoArrendatario = false;
            if (this.getArrendatario() == null && clave[5].equals("null")) {
                encontradoArrendatario = true;
            } else if (this.getArrendatario() != null) {
                if (this.getArrendatario().getCdgPst().toString().equals(clave[5])) {
                    encontradoArrendatario = true;
                }
            }
            boolean encontradoConcesionario = false;
            if (this.getConcesionario() == null && clave[6].equals("null")) {
                encontradoConcesionario = true;
            } else if (this.getConcesionario() != null) {
                if (this.getConcesionario().getCdgPst().toString().equals(clave[6])) {
                    encontradoConcesionario = true;
                }
            }

            boolean encontradoModalidad = false;
            if (this.getTipoModalidad() == null && clave[7].equals("null")) {
                encontradoModalidad = true;
            } else if (this.getTipoModalidad() != null) {
                if (this.getTipoModalidad().getCdg().equals(clave[7])) {
                    encontradoModalidad = true;
                }
            }

            return encontradoArrendatario && encontradoConcesionario && encontradoModalidad;

        } else {
            return false;
        }

    }

}
