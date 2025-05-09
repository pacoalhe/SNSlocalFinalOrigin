package mx.ift.sns.modelo.series;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import mx.ift.sns.modelo.ng.Serie;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;

@Entity
@Table(name = "SOLICITUD_SERIE_VM")
public class VSolicitudSerie {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @Column(name = "ID_SOLICITUD_SERIE")
    private BigDecimal id;

    /** Solicitud. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL_SOLICITUD", nullable = false)
    private Solicitud solicitud;
    
    /** Relación: Una serie puede tener muchos identificadores de región. */
    @ManyToOne
    @JoinColumn(name = "ID_NIR", nullable = false)
    private Nir nir;
    
    /** Relación: Muchas solicitudes pueden pertenecer al mismo NIR y Serie. */
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ID_NIR", referencedColumnName = "ID_NIR",
                    nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "ID_SERIE", referencedColumnName = "ID_SERIE",
                    nullable = false, insertable = false, updatable = false)
    })
    private Serie serie;

    /** Estatus de solicitud. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTADO_SOLICITUD", nullable = false)
    private EstadoSolicitud estatus;

    /** Tipo de solicitud. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_TIPO_SOLICITUD", nullable = false)
    private TipoSolicitud tipoSolicitud;

}
