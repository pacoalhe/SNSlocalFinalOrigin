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
 * Reresenta la solicitud de códigos al UIT.
 */
@Entity
@Table(name = "CPSI_SOLICITUD_UIT")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("18")
@NamedQuery(name = "SolicitudCpsiUit.findAll", query = "SELECT s FROM SolicitudCpsiUit s")
public class SolicitudCpsiUit extends mx.ift.sns.modelo.solicitud.Solicitud implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Número de asignaciones solicitadas por el trámite. */
    @Column(name = "NUM_CPSI_SOLICITADOS")
    private Integer numCpsiSolicitados;

    /** Códigos de CPSI de la solicitud. */
    @OneToMany(mappedBy = "solicitudUit", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<CpsiUitEntregado> cpsiUitEntregado;

    /** Documentos asociados a la solicitud. */
    @OneToMany(mappedBy = "solicitudUit", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<CpsiUitDocumento> cpsiUitDocumentos;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor, por defecto vacío. */
    public SolicitudCpsiUit() {
    }

    // GETTERS & SETTERS

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
     * Códigos de CPSI de la solicitud.
     * @return the cpsiUitEntregado
     */
    public List<CpsiUitEntregado> getCpsiUitEntregado() {
        return cpsiUitEntregado;
    }

    /**
     * Códigos de CPSI de la solicitud.
     * @param cpsiUitEntregado the cpsiUitEntregado to set
     */
    public void setCpsiUitEntregado(List<CpsiUitEntregado> cpsiUitEntregado) {
        this.cpsiUitEntregado = cpsiUitEntregado;
    }

    /**
     * Asocia un CPSI entregado con la solicitud.
     * @param cpsiUitEntregado Información del CPSI
     * @return CpsiUitEntregado
     */
    public CpsiUitEntregado addCpsiUitEntregado(CpsiUitEntregado cpsiUitEntregado) {
        getCpsiUitEntregado().add(cpsiUitEntregado);
        cpsiUitEntregado.setSolicitudUit(this);
        return cpsiUitEntregado;
    }

    /**
     * Desasocia un CPSI entregado con la solicitud.
     * @param cpsiUitEntregado @param cpsiAsignado Información del CPSI
     * @return CpsiUitEntregado
     */
    public CpsiUitEntregado removeCpsiEntregado(CpsiUitEntregado cpsiUitEntregado) {
        getCpsiUitEntregado().remove(cpsiUitEntregado);
        cpsiUitEntregado.setSolicitudUit(null);
        return cpsiUitEntregado;
    }

    /**
     * Documentos asociados a la solicitud.
     * @return the cpsiUitDocumentos
     */
    public List<CpsiUitDocumento> getCpsiUitDocumentos() {
        return cpsiUitDocumentos;
    }

    /**
     * Documentos asociados a la solicitud.
     * @param cpsiUitDocumentos the cpsiUitDocumentos to set
     */
    public void setCpsiUitDocumentos(List<CpsiUitDocumento> cpsiUitDocumentos) {
        this.cpsiUitDocumentos = cpsiUitDocumentos;
    }

}
