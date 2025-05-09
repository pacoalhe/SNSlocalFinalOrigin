package mx.ift.sns.modelo.ng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import mx.ift.sns.modelo.pst.Contacto;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.solicitud.Solicitud;

import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Representa una Solicitud de Cesión de Numeración Geográfica. Contiene la información del trámite, información de las
 * cesiones solicitadas y el estatus del proceso.
 */
@Entity
@Table(name = "NG_SOLICITUD_CESION")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("2")
@NamedQuery(name = "SolicitudCesionNg.findAll", query = "SELECT s FROM SolicitudCesionNg s")
public class SolicitudCesionNg extends Solicitud implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Relación: Una solicitud de cesión puede tener muchas cesiones aplicadas. */
    @OneToMany(mappedBy = "solicitudCesion", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<Cesion> cesionesAplicadas;

    /** Relación: Una solicitud de cesión puede tener muchas cesiones solicitadas. */
    @OneToMany(mappedBy = "solicitudCesion", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<CesionSolicitadaNg> cesionesSolicitadas;

    /** Relación: Muchas solicitudes de cesión pueden ser para mismo Proveedor Cesionario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_CESIONARIO", nullable = true)
    private Proveedor proveedorCesionario;

    /** Relación: Muchas solicitudes de cesión pueden tener el mismo representante Legal del Cesionario. */
    @ManyToOne
    @JoinColumn(name = "ID_REP_LEGAL_CESIONARIO", nullable = true)
    private Contacto representanteLegalCesionario;

    /** Relación: Muchas solicitudes de cesión pueden tener el mismo representante Suplente del Cesionario. */
    @ManyToOne
    @JoinColumn(name = "ID_REP_SUPLENTE_CESIONARIO", nullable = true)
    private Contacto representanteSuplenteCesionario;

    /** Fecha de Autorización. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_AUTORIZACION")
    private Date fechaAutorizacion;

    /** Indicador de Fusionado de Proveedores. */
    @Column(name = "FUSION_PSTS", nullable = true, length = 1)
    private String fusionPsts;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor, por defecto vacío. */
    public SolicitudCesionNg() {
    }

    // GETTERS & SETTERS

    /**
     * Lista de cesiones aplicadas.
     * @return List
     */
    public List<Cesion> getCesionesAplicadas() {
        return cesionesAplicadas;
    }

    /**
     * Lista de cesiones aplicadas.
     * @param cesionesAplicadas List
     */
    public void setCesionesAplicadas(List<Cesion> cesionesAplicadas) {
        this.cesionesAplicadas = cesionesAplicadas;
    }

    /**
     * Asocia una cesión con la solicitud que la originó.
     * @param pCesion Cesion
     * @return Cesion
     */
    public Cesion addCesionAplicada(Cesion pCesion) {
        this.getCesionesAplicadas().add(pCesion);
        pCesion.setSolicitudCesion(this);
        return pCesion;
    }

    /**
     * Elimina una cesión de la solicitud que la originó.
     * @param pCesion Cesion
     * @return Cesion
     */
    public Cesion removeCesionAplicada(Cesion pCesion) {
        this.getCesionesAplicadas().remove(pCesion);
        pCesion.setSolicitudCesion(null);
        return pCesion;
    }

    /**
     * Lista de cesiones solicitdas por ésta solicitud de cesión.
     * @return List
     */
    public List<CesionSolicitadaNg> getCesionesSolicitadas() {
        return cesionesSolicitadas;
    }

    /**
     * Lista de cesiones solicitdas por ésta solicitud de cesión.
     * @param cesionesSolicitadas List
     */
    public void setCesionesSolicitadas(List<CesionSolicitadaNg> cesionesSolicitadas) {
        this.cesionesSolicitadas = cesionesSolicitadas;
    }

    /**
     * Asocia una cesión solicitada a ésta solicitud de cesión.
     * @param pCesionSolicitada CesionSolicitadaNg
     * @return CesionSolicitadaNg
     */
    public CesionSolicitadaNg addCesionSolicitada(CesionSolicitadaNg pCesionSolicitada) {
        this.getCesionesSolicitadas().add(pCesionSolicitada);
        pCesionSolicitada.setSolicitudCesion(this);
        return pCesionSolicitada;
    }

    /**
     * Asocia una cesión solicitada a ésta solicitud de cesión.
     * @param pCesionSolicitada CesionSolicitadaNg
     * @return CesionSolicitadaNg
     */
    public CesionSolicitadaNg removeCesionSolicitada(CesionSolicitadaNg pCesionSolicitada) {
        this.getCesionesSolicitadas().remove(pCesionSolicitada);
        pCesionSolicitada.setSolicitudCesion(null);
        return pCesionSolicitada;
    }

    /**
     * Proveedor Cesionario. Recibe la numeración.
     * @return Proveedor Cesionario
     */
    public Proveedor getProveedorCesionario() {
        return proveedorCesionario;
    }

    /**
     * Proveedor Cesionario. Recibe la numeración.
     * @param proveedorCesionario roveedor Cesionario
     */
    public void setProveedorCesionario(Proveedor proveedorCesionario) {
        this.proveedorCesionario = proveedorCesionario;
    }

    /**
     * Representante Legal del Proveedor Cesionario para la solicitud.
     * @return Contacto
     */
    public Contacto getRepresentanteLegalCesionario() {
        return representanteLegalCesionario;
    }

    /**
     * Representante Legal del Proveedor Cesionario para la solicitud.
     * @param representanteLegalCesionario Contacto
     */
    public void setRepresentanteLegalCesionario(Contacto representanteLegalCesionario) {
        this.representanteLegalCesionario = representanteLegalCesionario;
    }

    /**
     * Representante Suplente del Proveedor Cesionario para la solicitud.
     * @return Contacto
     */
    public Contacto getRepresentanteSuplenteCesionario() {
        return representanteSuplenteCesionario;
    }

    /**
     * Representante Suplente del Proveedor Cesionario para la solicitud.
     * @param representanteSuplenteCesionario Contacto
     */
    public void setRepresentanteSuplenteCesionario(Contacto representanteSuplenteCesionario) {
        this.representanteSuplenteCesionario = representanteSuplenteCesionario;
    }

    /**
     * Fecha de Autorización.
     * @return Date
     */
    public Date getFechaAutorizacion() {
        return fechaAutorizacion;
    }

    /**
     * Fecha de Autorización.
     * @param fechaAutorizacion Date
     */
    public void setFechaAutorizacion(Date fechaAutorizacion) {
        this.fechaAutorizacion = fechaAutorizacion;
    }

    /**
     * Es necesario el parameto cesionesToAplicar para evitar
     * el error uninstantiated LAZY relationship
     * @param pCesion
     * @param cesionesToAplicar
     * @return
     */
    public Cesion addCesion(Cesion pCesion,List<Cesion> cesionesToAplicar) {
    	cesionesToAplicar.add(pCesion);
    	this.setCesionesAplicadas(cesionesToAplicar);
    	//this.getCesionesAplicadas().add(pCesion);
        pCesion.setSolicitudCesion(this);
        return pCesion;
    }

    /**
     * Elimina una cesión de la solicitud que la originó.
     * @param pCesion Cesion aplicada.
     * @return Cesion
     */
    public Cesion removeCesion(Cesion pCesion) {
        this.getCesionesAplicadas().remove(pCesion);
        pCesion.setSolicitudCesion(null);
        return pCesion;
    }

    /**
     * Indicador de Fusionado de Proveedores.
     * @return 'S' si es una fusión de Proveedores.
     */
    public String getFusionPsts() {
        return fusionPsts;
    }

    /**
     * Indicador de Fusionado de Proveedores.
     * @param fusionPsts 'S' si es una fusión de Proveedores.
     */
    public void setFusionPsts(String fusionPsts) {
        this.fusionPsts = fusionPsts;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SolicitudCesionNg = {");
        builder.append("id: ").append(this.getId()).append(", ");
        if (this.getProveedorSolicitante() != null) {
            builder.append("solicitante: ").append(this.getProveedorSolicitante().getNombreCorto()).append(", ");
        }
        builder.append("fecha: ").append(this.getFechaSolicitud()).append("}");

        ;
        return builder.toString();
    }
}
