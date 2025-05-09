package mx.ift.sns.modelo.cpsi;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Representa una Solicitud de Asignación de CPSI. Contiene la información del trámite, información de las asignaciones
 * solicitadas y el estatus del proceso.
 */
@Entity
@Table(name = "CPSI_SOLICITUD_ASIGNACION")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("14")
@NamedQuery(name = "SolicitudAsignacionCpsi.findAll", query = "SELECT s FROM SolicitudAsignacionCpsi s")
public class SolicitudAsignacionCpsi extends mx.ift.sns.modelo.solicitud.Solicitud implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Número de días hasta la aplicación de la asignación. */
    @Column(name = "DIAS_APLICACION")
    private Integer diasAplicacion;

    /** Número de asignaciones solicitadas por el trámite. */
    @Column(name = "NUM_CPSI_SOLICITADOS")
    private Integer numCpsiSolicitados;

    /** Asignaciones de CPSI del el trámite. */
    @OneToMany(mappedBy = "solicitudAsignacion", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<CpsiAsignado> cpsiAsignados;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor, por defecto vacío. */
    public SolicitudAsignacionCpsi() {
    }

    // GETTERS & SETTERS

    /**
     * Número de días hasta la aplicación de la asignación.
     * @return Integer
     */
    public Integer getDiasAplicacion() {
        return diasAplicacion;
    }

    /**
     * Número de días hasta la aplicación de la asignación.
     * @param diasAplicacion Integer
     */
    public void setDiasAplicacion(Integer diasAplicacion) {
        this.diasAplicacion = diasAplicacion;
    }

    /**
     * Número de asignaciones solicitadas por el trámite.
     * @return Integer
     */
    public Integer getNumCpsiSolicitados() {
        return numCpsiSolicitados;
    }

    /**
     * Número de asignaciones solicitadas por el trámite.
     * @param numCpsiSolicitados Integer
     */
    public void setNumCpsiSolicitados(Integer numCpsiSolicitados) {
        this.numCpsiSolicitados = numCpsiSolicitados;
    }

    /**
     * Lista de Asignaciones de CPSI realizadas por el trámite.
     * @return List
     */
    public List<CpsiAsignado> getCpsiAsignados() {
        return this.cpsiAsignados;
    }

    /**
     * Lista de Asignaciones de CPSI realizadas por el trámite.
     * @param cpsiAsignados List
     */
    public void setCpsiAsignados(List<CpsiAsignado> cpsiAsignados) {
        this.cpsiAsignados = cpsiAsignados;
    }

    /**
     * Asocia un CPSI Asignado con el trámite que ha generado la asignación.
     * @param cpsiAsignado Información del CPSI
     * @return CpsiAsignado
     */
    public CpsiAsignado addCpsiAsignado(CpsiAsignado cpsiAsignado) {
        getCpsiAsignados().add(cpsiAsignado);
        cpsiAsignado.setSolicitudAsignacion(this);
        return cpsiAsignado;
    }

    /**
     * Desasocia un CPSI Asignado con el trámite que ha generado la asignación.
     * @param cpsiAsignado @param cpsiAsignado Información del CPSI
     * @return CpsiAsignado
     */
    public CpsiAsignado removeCpsiAsignado(CpsiAsignado cpsiAsignado) {
        getCpsiAsignados().remove(cpsiAsignado);
        cpsiAsignado.setSolicitudAsignacion(null);
        return cpsiAsignado;
    }

}
