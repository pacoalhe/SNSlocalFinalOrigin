package mx.ift.sns.modelo.cpsi;

import java.io.Serializable;
import java.math.BigDecimal;

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
import javax.persistence.Transient;

import mx.ift.sns.modelo.cps.EstatusAsignacionCps;

/**
 * Representa un Código de Puntos de Señalización Internacional asignado a un proveedor de servicios por una Solicitud
 * de Asignación. Contiene la información del código seleccionado y el estatus del proceso.
 */
@Entity
@Table(name = "CPSI_ASIGNADO")
@SequenceGenerator(name = "SEQ_ID_CPSI_ASIGNADO", sequenceName = "SEQ_ID_CPSI_ASIGNADO", allocationSize = 1)
@NamedQuery(name = "CpsiAsignado.findAll", query = "SELECT c FROM CpsiAsignado c")
public class CpsiAsignado implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_CPSI_ASIGNADO")
    @Column(name = "ID_CPSI_ASIGNADO", nullable = false)
    private BigDecimal id;

    /** Representación del código CPSI en binario (con 14 bits). */
    @Column(name = "BINARIO", length = 14)
    private String binario;

    /** Identificador del código de puntos de señalización internacional. */
    @Column(name = "ID_CPSI", nullable = false)
    private BigDecimal idCpsi;

    /** Representación decimal de las 3 partes que forman un código CPSI, separadas con un guión. */
    @Transient
    private String formatoDecimal;

    /** Solicitud de Asignación asociada al CPSI Asignado. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL_SOLICITUD", nullable = false)
    private SolicitudAsignacionCpsi solicitudAsignacion;

    /** Estatus de la Asignación Solicitada. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS_ASIGNACION", nullable = false)
    private EstatusAsignacionCps estatus;

    /** Constructor, por defecto vacío. */
    public CpsiAsignado() {
    }

    // GETTERS & SETTERS

    /**
     * Identificador de CPSI Asignado.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Identificador de CPSI Asignado.
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Identificador del código de puntos de señalización internacional.
     * @return BigDecimal
     */
    public BigDecimal getIdCpsi() {
        return idCpsi;
    }

    /**
     * Identificador del código de puntos de señalización internacional.
     * @param idCpsi BigDecimal
     */
    public void setIdCpsi(BigDecimal idCpsi) {
        this.idCpsi = idCpsi;
    }

    /**
     * Representación del código CPSI en binario (con 14 bits).
     * @return String
     */
    public String getBinario() {
        return binario;
    }

    /**
     * Representación del código CPSI en binario (con 14 bits).
     * @param binario String
     */
    public void setBinario(String binario) {
        this.binario = binario;
    }

    /**
     * Solicitud de Asignación asociada al CPSI Asignado.
     * @return SolicitudAsignacionCpsi
     */
    public SolicitudAsignacionCpsi getSolicitudAsignacion() {
        return solicitudAsignacion;
    }

    /**
     * Solicitud de Asignación asociada al CPSI Asignado.
     * @param solicitudAsignacion SolicitudAsignacionCpsi
     */
    public void setSolicitudAsignacion(SolicitudAsignacionCpsi solicitudAsignacion) {
        this.solicitudAsignacion = solicitudAsignacion;
    }

    /**
     * Representación decimal de las 3 partes que forman un código CPSI, separadas con un guion.
     * @return String
     */
    public String getFormatoDecimal() {
        if (formatoDecimal == null) {
            String binario = this.getBinario();

            // Id de Región los 3 primers bits (0-3)
            String idRegion = binario.substring(0, CodigoCPSI.ID_REGION_BITS);

            // Id de Red los 8 siguientes bits (3-11)
            String idRed = binario.substring(CodigoCPSI.ID_REGION_BITS,
                    (CodigoCPSI.ID_REGION_BITS + CodigoCPSI.ID_RED_BITS));

            // Ide de codigo CPSI los 3 siguientes bis (11-14)
            String idPunto = binario.substring((CodigoCPSI.ID_REGION_BITS + CodigoCPSI.ID_RED_BITS),
                    (CodigoCPSI.ID_REGION_BITS + CodigoCPSI.ID_RED_BITS + CodigoCPSI.ID_CPSI_BITS));

            // Formato decimal
            StringBuilder sbDecimal = new StringBuilder();
            sbDecimal.append(Integer.parseInt(idRegion, 2)).append("-");
            sbDecimal.append(Integer.parseInt(idRed, 2)).append("-");
            sbDecimal.append(Integer.parseInt(idPunto, 2));

            formatoDecimal = sbDecimal.toString();
        }
        return formatoDecimal;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CpsiAsignado = {");
        builder.append("id = ").append(this.id);
        builder.append(", Cpsi = ").append(idCpsi);
        builder.append(", Binario = ").append(binario);
        builder.append("}");
        return builder.toString();
    }

    /**
     * Estatus de la Asignación Solicitada.
     * @return EstatusAsignacionCps
     */
    public EstatusAsignacionCps getEstatus() {
        return estatus;
    }

    /**
     * Estatus de la Asignación Solicitada.
     * @param estatus EstatusAsignacionCps
     */
    public void setEstatus(EstatusAsignacionCps estatus) {
        this.estatus = estatus;
    }

}
