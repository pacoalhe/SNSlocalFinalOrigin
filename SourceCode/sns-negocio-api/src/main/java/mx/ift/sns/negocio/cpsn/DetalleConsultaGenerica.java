package mx.ift.sns.negocio.cpsn;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Locale;

import mx.ift.sns.modelo.cpsn.VConsultaGenericaCpsn;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;

import org.apache.commons.lang3.StringUtils;

/**
 * Clase de utilidad para mostrar el restulado de la consulta genérica.
 * @author X23016PE
 */
public class DetalleConsultaGenerica implements Serializable {

    /** Localización en Castellano. */
    private static Locale localeES = new Locale("es", "ES");

    /** Parseador de fechas con formato simple. */
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", localeES);

    /** Constante para las múltiples fechas de implementación. */
    private static final String MULTIPLES = "Múltiples";

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Consecutivo de la solicitud. */
    private BigDecimal consecutivo;

    /** Identificador del tipo de solicitud. */
    private Integer idTipoSolicitud;

    /** Desscripción del tipo de trámite. */
    private String tipoTramite;

    /** Descripción del pst origen. */
    private String pstOrigen;

    /** Descripción del pst destino. */
    private String pstDestino;

    /** Fecha de la solicitud. */
    private String fechaSolicitud;

    /** Fecha de implementación. */
    private String fechaImplementacion;

    /** Número de oficio del solicitante. */
    private String numOficioSolicitante;

    /** Estatus de la solicitud. */
    private EstadoSolicitud estatusSolicitud;

    /** Constructor. */
    public DetalleConsultaGenerica() {
    }

    /**
     * Constructor.
     * @param data para cargar los datos.
     */
    public DetalleConsultaGenerica(VConsultaGenericaCpsn data) {
        this.consecutivo = data.getConsecutivo();
        this.idTipoSolicitud = data.getIdTipoSolicitud();
        this.tipoTramite = data.getTipoTramite();
        this.pstOrigen = data.getPstOrigen();
        this.pstDestino = data.getPstDestino();

        this.fechaSolicitud = (data.getFechaSolicitud() != null) ? sdf.format(data.getFechaSolicitud()) : "";
        this.fechaImplementacion = (data.getFechaImplementacion() != null) ? sdf.format(data.getFechaImplementacion())
                : "";
        this.numOficioSolicitante = data.getNumOficioSolicitante();
        this.estatusSolicitud = data.getEstatusSolicitud();
    }

    /**
     * @return the consecutivo
     */
    public BigDecimal getConsecutivo() {
        return consecutivo;
    }

    /**
     * @param consecutivo the consecutivo to set
     */
    public void setConsecutivo(BigDecimal consecutivo) {
        this.consecutivo = consecutivo;
    }

    /**
     * @return the tipoTramite
     */
    public String getTipoTramite() {
        return tipoTramite;
    }

    /**
     * @param tipoTramite the tipoTramite to set
     */
    public void setTipoTramite(String tipoTramite) {
        this.tipoTramite = tipoTramite;
    }

    /**
     * @return the pstOrigen
     */
    public String getPstOrigen() {
        return pstOrigen;
    }

    /**
     * @param pstOrigen the pstOrigen to set
     */
    public void setPstOrigen(String pstOrigen) {
        this.pstOrigen = pstOrigen;
    }

    /**
     * @return the pstDestino
     */
    public String getPstDestino() {
        return pstDestino;
    }

    /**
     * @param pstDestino the pstDestino to set
     */
    public void setPstDestino(String pstDestino) {
        this.pstDestino = pstDestino;
    }

    /**
     * @return the fechaSolicitud
     */
    public String getFechaSolicitud() {
        return fechaSolicitud;
    }

    /**
     * @param fechaSolicitud the fechaSolicitud to set
     */
    public void setFechaSolicitud(String fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    /**
     * @return the fechaImplementacion
     */
    public String getFechaImplementacion() {
        return fechaImplementacion;
    }

    /**
     * @param fechaImplementacion the fechaImplementacion to set
     */
    public void setFechaImplementacion(String fechaImplementacion) {
        this.fechaImplementacion = fechaImplementacion;
    }

    /**
     * @return the numOficioSolicitante
     */
    public String getNumOficioSolicitante() {
        return numOficioSolicitante;
    }

    /**
     * @param numOficioSolicitante the numOficioSolicitante to set
     */
    public void setNumOficioSolicitante(String numOficioSolicitante) {
        this.numOficioSolicitante = numOficioSolicitante;
    }

    /**
     * @return the estatusSolicitud
     */
    public EstadoSolicitud getEstatusSolicitud() {
        return estatusSolicitud;
    }

    /**
     * @param estatusSolicitud the estatusSolicitud to set
     */
    public void setEstatusSolicitud(EstadoSolicitud estatusSolicitud) {
        this.estatusSolicitud = estatusSolicitud;
    }

    /**
     * @return the idTipoSolicitud
     */
    public Integer getIdTipoSolicitud() {
        return idTipoSolicitud;
    }

    /**
     * @param idTipoSolicitud the idTipoSolicitud to set
     */
    public void setIdTipoSolicitud(Integer idTipoSolicitud) {
        this.idTipoSolicitud = idTipoSolicitud;
    }

    /**
     * Método encargado de añadir la fecha de implementación.
     * @param data a añadir
     */
    public void addFechaImplementacion(VConsultaGenericaCpsn data) {
        if (StringUtils.isEmpty(fechaImplementacion)) {
            fechaImplementacion = data.getFechaImplementacion() != null
                    ? sdf.format(data.getFechaImplementacion()) : "";
        } else {
            if (!fechaImplementacion.equals(sdf.format(data.getFechaImplementacion()))) {
                fechaImplementacion = MULTIPLES;
            }
        }
    }
}
