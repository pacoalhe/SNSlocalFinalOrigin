package mx.ift.sns.web.backend.ng.lineasactivas.model;

import mx.ift.sns.modelo.lineas.DetLineaActivaDet;
import mx.ift.sns.modelo.lineas.DetLineaArrendada;
import mx.ift.sns.modelo.lineas.DetLineaArrendatario;
import mx.ift.sns.modelo.lineas.DetalleLineaActiva;

/**
 * Clase que muestra el resumen del reporte de lineas activas.
 * @author X36155QU
 */
public class ResumenReporteLineasActivas {

    /** Detalle Linea Activa. */
    private DetalleLineaActiva detLineaActiva;

    /** Detalle Linea Activa Detallado. */
    private DetLineaActivaDet detLineaActivaDet;

    /** Detalle Linea Arrendatario. */
    private DetLineaArrendatario detLineaArrendatario;

    /** Detalle Linea Arrendador. */
    private DetLineaArrendada detLineaArrendada;

    /** Total asignado en fijo. */
    private Integer totalAsignadoFijo;

    /** Porcentaje utilizacion fijo. */
    private Float porcentajeUtilizacionFijo;

    /** Total asignado en Cpp. */
    private Integer totalAsignadoCpp;

    /** Porcentaje utilizacion Cpp. */
    private Float porcentajeUtilizacionCpp;

    /** Total asignado en Mpp. */
    private Integer totalAsignadoMpp;

    /** Porcentaje utilizacion Mpp. */
    private Float porcentajeUtilizacionMpp;

    /** Total asignado en Movil. */
    private Integer totalAsignadoMovil;

    /** Porcentaje utilizacion Movil. */
    private Float porcentajeUtilizacionMovil;

    /** Porcentaje total de utilización. */
    private Float porcentajeTotal;

    /**
     * @return the totalAsignadoFijo
     */
    public Integer getTotalAsignadoFijo() {
        return totalAsignadoFijo;
    }

    /**
     * @param totalAsignadoFijo the totalAsignadoFijo to set
     */
    public void setTotalAsignadoFijo(Integer totalAsignadoFijo) {
        this.totalAsignadoFijo = totalAsignadoFijo;
    }

    /**
     * @return the porcentajeUtilizacionFijo
     */
    public Float getPorcentajeUtilizacionFijo() {
        return porcentajeUtilizacionFijo;
    }

    /**
     * @param porcentajeUtilizacionFijo the porcentajeUtilizacionFijo to set
     */
    public void setPorcentajeUtilizacionFijo(Float porcentajeUtilizacionFijo) {
        this.porcentajeUtilizacionFijo = porcentajeUtilizacionFijo;
    }

    /**
     * @return the totalAsignadoCpp
     */
    public Integer getTotalAsignadoCpp() {
        return totalAsignadoCpp;
    }

    /**
     * @param totalAsignadoCpp the totalAsignadoCpp to set
     */
    public void setTotalAsignadoCpp(Integer totalAsignadoCpp) {
        this.totalAsignadoCpp = totalAsignadoCpp;
    }

    /**
     * @return the porcentajeUtilizacionCpp
     */
    public Float getPorcentajeUtilizacionCpp() {
        return porcentajeUtilizacionCpp;
    }

    /**
     * @param porcentajeUtilizacionCpp the porcentajeUtilizacionCpp to set
     */
    public void setPorcentajeUtilizacionCpp(Float porcentajeUtilizacionCpp) {
        this.porcentajeUtilizacionCpp = porcentajeUtilizacionCpp;
    }

    /**
     * @return the totalAsignadoMpp
     */
    public Integer getTotalAsignadoMpp() {
        return totalAsignadoMpp;
    }

    /**
     * @param totalAsignadoMpp the totalAsignadoMpp to set
     */
    public void setTotalAsignadoMpp(Integer totalAsignadoMpp) {
        this.totalAsignadoMpp = totalAsignadoMpp;
    }

    /**
     * @return the porcentajeUtilizacionMpp
     */
    public Float getPorcentajeUtilizacionMpp() {
        return porcentajeUtilizacionMpp;
    }

    /**
     * @param porcentajeUtilizacionMpp the porcentajeUtilizacionMpp to set
     */
    public void setPorcentajeUtilizacionMpp(Float porcentajeUtilizacionMpp) {
        this.porcentajeUtilizacionMpp = porcentajeUtilizacionMpp;
    }

    /**
     * @return the detLineaActiva
     */
    public DetalleLineaActiva getDetLineaActiva() {
        return detLineaActiva;
    }

    /**
     * @param detLineaActiva the detLineaActiva to set
     */
    public void setDetLineaActiva(DetalleLineaActiva detLineaActiva) {
        this.detLineaActiva = detLineaActiva;
    }

    /**
     * @return the totalAsignadoMovil
     */
    public Integer getTotalAsignadoMovil() {
        return totalAsignadoMovil;
    }

    /**
     * @param totalAsignadoMovil the totalAsignadoMovil to set
     */
    public void setTotalAsignadoMovil(Integer totalAsignadoMovil) {
        this.totalAsignadoMovil = totalAsignadoMovil;
    }

    /**
     * @return the porcentajeUtilizacionMovil
     */
    public Float getPorcentajeUtilizacionMovil() {
        return porcentajeUtilizacionMovil;
    }

    /**
     * @param porcentajeUtilizacionMovil the porcentajeUtilizacionMovil to set
     */
    public void setPorcentajeUtilizacionMovil(Float porcentajeUtilizacionMovil) {
        this.porcentajeUtilizacionMovil = porcentajeUtilizacionMovil;
    }

    /**
     * @return the detLineaActivaDet
     */
    public DetLineaActivaDet getDetLineaActivaDet() {
        return detLineaActivaDet;
    }

    /**
     * @param detLineaActivaDet the detLineaActivaDet to set
     */
    public void setDetLineaActivaDet(DetLineaActivaDet detLineaActivaDet) {
        this.detLineaActivaDet = detLineaActivaDet;
    }

    /**
     * @return the porcentajeTotal
     */
    public Float getPorcentajeTotal() {
        return porcentajeTotal;
    }

    /**
     * @param porcentajeTotal the porcentajeTotal to set
     */
    public void setPorcentajeTotal(Float porcentajeTotal) {
        this.porcentajeTotal = porcentajeTotal;
    }

    /**
     * @return the detLineaArrendatario
     */
    public DetLineaArrendatario getDetLineaArrendatario() {
        return detLineaArrendatario;
    }

    /**
     * @param detLineaArrendatario the detLineaArrendatario to set
     */
    public void setDetLineaArrendatario(DetLineaArrendatario detLineaArrendatario) {
        this.detLineaArrendatario = detLineaArrendatario;
    }

    /**
     * @return the detLineaArrendada
     */
    public DetLineaArrendada getDetLineaArrendada() {
        return detLineaArrendada;
    }

    /**
     * @param detLineaArrendada the detLineaArrendada to set
     */
    public void setDetLineaArrendada(DetLineaArrendada detLineaArrendada) {
        this.detLineaArrendada = detLineaArrendada;
    }
}
