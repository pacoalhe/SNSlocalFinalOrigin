package mx.ift.sns.modelo.cpsi;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Representa la entrega de los códigos.
 */
@Entity
@Table(name = "CPSI_UIT_ENTREGADO")
@SequenceGenerator(name = "SEQ_ID_UIT_ENTREGADO", sequenceName = "SEQ_ID_UIT_ENTREGADO", allocationSize = 1)
@NamedQuery(name = "CpsiUitEntregado.findAll", query = "SELECT c FROM CpsiUitEntregado c")
public class CpsiUitEntregado implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_UIT_ENTREGADO")
    @Column(name = "ID_UIT_ENTREGADO", nullable = false)
    private BigDecimal id;

    /** Identificador del código de puntos de señalización internacional. */
    @Column(name = "ID_CPSI", nullable = false)
    private BigDecimal idCpsi;

    /** Solicitud de Asignación asociada al CPSI Asignado. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL_SOLICITUD", nullable = false)
    private SolicitudCpsiUit solicitudUit;

    /** Lista de documentos asociados a los códigos cpsi entregados. */
    @OneToMany(mappedBy = "cpsiUitEntregado", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @PrivateOwned
    private List<CpsiUitEntregadoDoc> cpsiUitEntregadoDoc;

    /** Estatus de la Asignación Solicitada. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS_SOL_UIT", nullable = false)
    private EstatusSolicitudCpsiUit estatus;

    /** Referencia del código. */
    @Column(name = "REFERENCIA_UIT", length = 60)
    private String referenciaUit;

    /** Representación decimal de las 3 partes que forman un código CPSI, separadas con un guión. */
    @Transient
    private String formatoDecimal;

    /** Representación del código CPSI en binario (con 14 bits). */
    @Transient
    private String binario;

    /** Constructor, por defecto vacío. */
    public CpsiUitEntregado() {
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
     * @return the binario
     */
    public String getBinario() {
        if (idCpsi != null) {
            this.binario = StringUtils.leftPad(Integer.toBinaryString(idCpsi.intValue()), 14, '0');
            return this.binario;
        }

        return "";
    }

    /**
     * Representación del código CPSI en binario (con 14 bits).
     * @param binario the binario to set
     */
    public void setBinario(String binario) {
        this.binario = binario;
    }

    /**
     * Solicitud de Asignación asociada al CPSI Asignado.
     * @return the solicitudUit
     */
    public SolicitudCpsiUit getSolicitudUit() {
        return solicitudUit;
    }

    /**
     * Solicitud de Asignación asociada al CPSI Asignado.
     * @param solicitudUit the solicitudUit to set
     */
    public void setSolicitudUit(SolicitudCpsiUit solicitudUit) {
        this.solicitudUit = solicitudUit;
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

    /**
     * Representación decimal de las 3 partes que forman un código CPSI, separadas con un guión.
     * @param formatoDecimal the formatoDecimal to set
     */
    public void setFormatoDecimal(String formatoDecimal) {
        this.formatoDecimal = formatoDecimal;
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
     * @return the estatus
     */
    public EstatusSolicitudCpsiUit getEstatus() {
        return estatus;
    }

    /**
     * Estatus de la Asignación Solicitada.
     * @param estatus the estatus to set
     */
    public void setEstatus(EstatusSolicitudCpsiUit estatus) {
        this.estatus = estatus;
    }

    /**
     * Referencia del código.
     * @return the referenciaUit
     */
    public String getReferenciaUit() {
        return referenciaUit;
    }

    /**
     * Referencia del código.
     * @param referenciaUit the referenciaUit to set
     */
    public void setReferenciaUit(String referenciaUit) {
        this.referenciaUit = referenciaUit;
    }

    /**
     * Lista de documentos asociados a los códigos cpsi entregados.
     * @return the cpsiUitEntregadoDoc
     */
    public CpsiUitEntregadoDoc getCpsiUitEntregadoDoc() {
        CpsiUitEntregadoDoc entregadoDoc = new CpsiUitEntregadoDoc();
        if (cpsiUitEntregadoDoc.isEmpty()) {
            return entregadoDoc;
        } else {
            return cpsiUitEntregadoDoc.get(0);
        }
    }

    /**
     * Lista de documentos asociados a los códigos cpsi entregados.
     * @param cpsiUitEntregadoDoc the cpsiUitEntregadoDoc to set
     */
    public void setCpsiUitEntregadoDoc(List<CpsiUitEntregadoDoc> cpsiUitEntregadoDoc) {
        this.cpsiUitEntregadoDoc = cpsiUitEntregadoDoc;
    }

}
