package mx.ift.sns.modelo.ng;

import java.io.Serializable;
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
import javax.persistence.Version;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.solicitud.Solicitud;

import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Representa una Solicitud de Consolidación de ABNs de Numeración Geográfica. Contiene la información del trámite,
 * información de las consolidaciones solicitadas y el estatus del proceso.
 */
@Entity
@Table(name = "NG_SOLICITUD_CONSOLIDACION")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("5")
@NamedQuery(name = "SolicitudConsolidacion.findAll", query = "SELECT s FROM SolicitudConsolidacion s")
public class SolicitudConsolidacion extends Solicitud implements Serializable {

    /** Serializacion. */
    private static final long serialVersionUID = 1L;

    /** Constante para la consolidacion total. */
    public static final String CONSOLIDACION_TOTAL = "T";

    /** Constante para la consolidacion parcial. */
    public static final String CONSOLIDACION_PARCIAL = "P";

    /** Relación: Una solicitud de consolidacion puede tener muchas abn´s consolidaciones. */
    @OneToMany(mappedBy = "solicitudConsolidacion", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<AbnConsolidar> abnsConsolidados;

    /** Relación: Una solicitud de consolidacion puede tener muchos abn´s de entrega. */
    @ManyToOne
    @JoinColumn(name = "ID_ABN_ENTREGA", nullable = false)
    private Abn abnEntrega;

    /** Relación: Una solicitud de consolidacion puede tener muchos abn´s recibe. */
    @ManyToOne
    @JoinColumn(name = "ID_ABN_RECIBE", nullable = false)
    private Abn abnRecibe;

    /** Identificador del tipo de consolidacion (T: total, P: parcial). */
    @Column(name = "TIPO_CONSOLIDACION", length = 1)
    private String tipoConsolidacion;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor, por defecto vacío. */
    public SolicitudConsolidacion() {
    }

    /**
     * Relación: Una solicitud de consolidacion puede tener muchos abn´s de entrega.
     * @return the abnEntrega
     */
    public Abn getAbnEntrega() {
        return abnEntrega;
    }

    /**
     * Relación: Una solicitud de consolidacion puede tener muchos abn´s de entrega.
     * @param abnEntrega the abnEntrega to set
     */
    public void setAbnEntrega(Abn abnEntrega) {
        this.abnEntrega = abnEntrega;
    }

    /**
     * Relación: Una solicitud de consolidacion puede tener muchos abn´s recibe.
     * @return the abnRecibe
     */
    public Abn getAbnRecibe() {
        return abnRecibe;
    }

    /**
     * Relación: Una solicitud de consolidacion puede tener muchos abn´s recibe.
     * @param abnRecibe the abnRecibe to set
     */
    public void setAbnRecibe(Abn abnRecibe) {
        this.abnRecibe = abnRecibe;
    }

    /**
     * Devuelve la lista de AbnConsolidar.
     * @return List<AbnConsolidar>
     */
    public List<AbnConsolidar> getAbnsConsolidados() {
        return this.abnsConsolidados;
    }

    /**
     * Set de la lista.
     * @param abnsConsolidados abnsConsolidados
     */
    public void setAbnsConsolidados(List<AbnConsolidar> abnsConsolidados) {
        this.abnsConsolidados = abnsConsolidados;
    }

    /**
     * Identificador del tipo de consolidacion (T: total, P: parcial).
     * @return the tipoConso
     */
    public String getTipoConsolidacion() {
        return tipoConsolidacion;
    }

    /**
     * Identificador del tipo de consolidacion (T: total, P: parcial).
     * @param tipoConsolidacion the tipoConso to set
     */
    public void setTipoConsolidacion(String tipoConsolidacion) {
        this.tipoConsolidacion = tipoConsolidacion;
    }

    /**
     * Añadir un AbnConsolidar.
     * @param abnConsolidado abnConsolidado
     * @return AbnConsolidar
     */
    public AbnConsolidar addAbnConsolidado(AbnConsolidar abnConsolidado) {
        getAbnsConsolidados().add(abnConsolidado);
        abnConsolidado.setSolicitudConsolidacion(this);

        return abnConsolidado;
    }

    /**
     * Eliminar un AbnConsolidar.
     * @param abnConsolidado abnConsolidado
     * @return AbnConsolidar
     */
    public AbnConsolidar removeAbnConsolidado(AbnConsolidar abnConsolidado) {
        getAbnsConsolidados().remove(abnConsolidado);
        abnConsolidado.setSolicitudConsolidacion(null);

        return abnConsolidado;
    }

}
