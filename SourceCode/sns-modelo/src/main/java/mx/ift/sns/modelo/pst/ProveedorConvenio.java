package mx.ift.sns.modelo.pst;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import mx.ift.sns.modelo.usu.Auditoria;

/**
 * Representa un Convenio entre dos Proveedores de Servicios de Telecomunicación.
 */
@Entity
@Table(name = "PST_CONVENIO")
@SequenceGenerator(name = "SEQ_ID_PROV_CONVENIO", sequenceName = "SEQ_ID_PROV_CONVENIO", allocationSize = 1)
@NamedQuery(name = "ProveedorConvenio.findAll", query = "SELECT p FROM ProveedorConvenio p")
public class ProveedorConvenio extends Auditoria implements Serializable, Cloneable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_PROV_CONVENIO")
    @Column(name = "ID_CONVENIO", unique = true, nullable = false)
    private BigDecimal id;

    /** Identificador del Contrato. */
    @Column(name = "CONTRATO", length = 100, nullable = false)
    private String contrato;

    /** Fecha de incio del convenio. */
    @Column(name = "FECHA_INICIO_CONVENIO", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;

    /** Fecha de fin del convenio. */
    @Column(name = "FECHA_FIN_CONVENIO", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaFin;

    /** Observaciones adicionales del convenio. */
    @Column(name = "OBSERVACIONES", nullable = true, length = 500)
    private String observaciones;

    /** Referencia Baja de Convenio. */
    @Column(name = "REFERENCIA_BAJA", nullable = true, length = 30)
    private String referenciaBaja;

    /** Relación: Muchos convenios pueden estar asociados a un mismo proveedor. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_CONVENIO", nullable = false)
    private Proveedor proveedorConvenio;

    /** Relación: Muchos convenios pueden estar asociados a un mismo proveedor concesionario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_CONCESIONARIO", nullable = false)
    private Proveedor proveedorConcesionario;

    /** Relación: Muchos convenios pueden estar asociados al mismo tipo de red. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_RED", nullable = false)
    private TipoRed tipoRed;

    /** Relación: Muchos convenios pueden tener el mismo estatus. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS_CONVENIO", nullable = false)
    private EstadoConvenio estatus;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor, por defecto vacío. */
    public ProveedorConvenio() {
    }

    /**
     * Identificador interno.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return this.id;
    }

    /**
     * Identificador interno.
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Proveedor comercializador. Es el beneficiario del convenio.
     * @return Proveedor comercializador.
     */
    public Proveedor getProveedorConvenio() {
        return proveedorConvenio;
    }

    /**
     * Proveedor comercializador. Es el beneficiario del convenio.
     * @param proveedorConvenio Proveedor comercializador.
     */
    public void setProveedorConvenio(Proveedor proveedorConvenio) {
        this.proveedorConvenio = proveedorConvenio;
    }

    /**
     * Proveedor cesionario. Es el arrendador del convenio que firma con el comercializador.
     * @return Proveedor arrendador
     */
    public Proveedor getProveedorConcesionario() {
        return proveedorConcesionario;
    }

    /**
     * Proveedor cesionario. Es el arrendador del convenio que firma con el comercializador.
     * @param proveedorConcesionario Proveedor arrendador
     */
    public void setProveedorConcesionario(Proveedor proveedorConcesionario) {
        this.proveedorConcesionario = proveedorConcesionario;
    }

    /**
     * Tipo de red.
     * @return TipoRed
     */
    public TipoRed getTipoRed() {
        return tipoRed;
    }

    /**
     * Tipo de red.
     * @param tipoRed TipoRed
     */
    public void setTipoRed(TipoRed tipoRed) {
        this.tipoRed = tipoRed;
    }

    /**
     * Identificador del contrato.
     * @return String
     */
    public String getContrato() {
        return contrato;
    }

    /**
     * Identificador del contrato.
     * @param contrato String
     */
    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    /**
     * Fecha de inicio del Convenio.
     * @return Date
     */
    public Date getFechaInicio() {
        return fechaInicio;
    }

    /**
     * Fecha de inicio del Convenio.
     * @param fechaInicio Date
     */
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * Observaciones adicionales.
     * @return String
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * Observaciones adicionales.
     * @param observaciones String
     */
    public void setObservaciones(String observaciones) {
        // Se eliminan los retornos de carro
        this.observaciones = observaciones.replace("\r", "");
    }

    /**
     * Estatus actual del convenio.
     * @return EstadoConvenio
     */
    public EstadoConvenio getEstatus() {
        return estatus;
    }

    /**
     * Estatus actual del convenio.
     * @param estatus EstadoConvenio
     */
    public void setEstatus(EstadoConvenio estatus) {
        this.estatus = estatus;
    }

    /**
     * Fecha de Final de Convenio.
     * @return Date
     */
    public Date getFechaFin() {
        return fechaFin;
    }

    /**
     * Fecha de Final de Convenio.
     * @param fechaFin Date
     */
    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    /**
     * Referencia de Baja de Convenio.
     * @return String
     */
    public String getReferenciaBaja() {
        return referenciaBaja;
    }

    /**
     * Referencia de Baja de Convenio.
     * @param referenciaBaja String
     */
    public void setReferenciaBaja(String referenciaBaja) {
        this.referenciaBaja = referenciaBaja;
    }

    // @Override
    // public boolean equals(Object other) {
    // return (other instanceof ProveedorConvenio) && (id != null)
    // ? id.equals(((ProveedorConvenio) other).id)
    // : (other == this);
    // }

    @Override
    public int hashCode() {
        return (id != null)
                ? (this.getClass().hashCode() + id.hashCode())
                : super.hashCode();
    }

    /**
     * Método para generar copias del objeto.
     * @return ProveedorConvenio convenio
     * @throws CloneNotSupportedException ex
     */

    @Override
    public ProveedorConvenio clone() throws CloneNotSupportedException {
        return (ProveedorConvenio) super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ProveedorConvenio other = (ProveedorConvenio) obj;
        if (contrato == null) {
            if (other.contrato != null) {
                return false;
            }
        } else if (!contrato.equals(other.contrato)) {
            return false;
        }
        if (estatus == null) {
            if (other.estatus != null) {
                return false;
            }
        }
        if (fechaFin == null) {
            if (other.fechaFin != null) {
                return false;
            }
        } else if (!fechaFin.equals(other.fechaFin)) {
            return false;
        }
        if (fechaInicio == null) {
            if (other.fechaInicio != null) {
                return false;
            }
        } else if (!fechaInicio.equals(other.fechaInicio)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (observaciones == null) {
            if (other.observaciones != null) {
                return false;
            }
        } else if (!observaciones.equals(other.observaciones)) {
            return false;
        }
        if (proveedorConcesionario == null) {
            if (other.proveedorConcesionario != null) {
                return false;
            }
        } else if (!proveedorConcesionario.equals(other.proveedorConcesionario)) {
            return false;
        }
        if (proveedorConvenio == null) {
            if (other.proveedorConvenio != null) {
                return false;
            }
        } else if (!proveedorConvenio.equals(other.proveedorConvenio)) {
            return false;
        }
        if (referenciaBaja == null) {
            if (other.referenciaBaja != null) {
                return false;
            }
        } else if (!referenciaBaja.equals(other.referenciaBaja)) {
            return false;
        }
        if (tipoRed == null) {
            if (other.tipoRed != null) {
                return false;
            }
        } else if (!tipoRed.equals(other.tipoRed)) {
            return false;
        }
        return true;
    }

    /**
     * Método que copia en el proveedorConvenio los datos del proveedorConvenio recibido.
     * @param convenio ProveedorConvenio
     */
    public void copy(ProveedorConvenio convenio) {
        id = convenio.getId();
        contrato = convenio.getContrato();
        estatus = convenio.getEstatus();
        fechaFin = convenio.getFechaFin();
        fechaInicio = convenio.getFechaInicio();
        observaciones = convenio.getObservaciones();
        proveedorConcesionario = convenio.getProveedorConcesionario();
        referenciaBaja = convenio.getReferenciaBaja();
        tipoRed = convenio.getTipoRed();
    }
}
