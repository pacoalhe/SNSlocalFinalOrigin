package mx.ift.sns.web.backend.ng.lineasactivas.model;

import mx.ift.sns.modelo.lineas.DetalleReporte;

/**
 * Clase que muestra el resumen del reporte de lineas activas.
 * @author X36155QU
 */
public class ResumenConsultaLineasActivas {

    /** Detalle Repòrte. */
    private DetalleReporte detalleReporte;

    /**
     * Detalle Repòrte.
     * @return detalleReporte
     */
    public DetalleReporte getDetalleReporte() {
        return detalleReporte;
    }

    /**
     * Detalle Repòrte.
     * @param detalleReporte detalleReporte to set
     */
    public void setDetalleReporte(DetalleReporte detalleReporte) {
        this.detalleReporte = detalleReporte;
    }

    /**
     * Porcentaje de utilizacion fijo.
     * @return porcentajeUtilizacionFijo
     */
    public String getPorcentajeUtilizacionFijo() {
        return (detalleReporte.getTotalAsignadasFijas().intValue() == 0 || detalleReporte.getTotalLineasActivasFijas()
                .intValue() == 0)
                ? "0 %" : String.format("%.2f", detalleReporte.getTotalLineasActivasFijas().intValue() * 100.0
                        / detalleReporte.getTotalAsignadasFijas().intValue())
                        + " %";
    }

    /**
     * Porcentaje utilizacion CPP.
     * @return porcentajeUtilizacionCpp
     */
    public String getPorcentajeUtilizacionCpp() {
        return (detalleReporte.getTotalAsignadasMpp().intValue() == 0 || detalleReporte.getTotalLineasActivasCpp()
                .intValue() == 0)
                ? "0 %" : String.format("%.2f", detalleReporte.getTotalLineasActivasCpp().intValue() * 100.0
                        / detalleReporte.getTotalAsignadasMpp().intValue())
                        + " %";
    }

    /**
     * Porcentaje utilizacion MPP.
     * @return porcentajeUtilizacionMpp
     */
    public String getPorcentajeUtilizacionMpp() {
        return (detalleReporte.getTotalAsignadasMpp().intValue() == 0 || detalleReporte.getTotalLineasActivasMpp()
                .intValue() == 0)
                ? "0 %" : String.format("%.2f", detalleReporte.getTotalLineasActivasMpp().intValue() * 100.0
                        / detalleReporte.getTotalAsignadasMpp().intValue())
                        + " %";
    }

    /**
     * Total asignado movil.
     * @return totalAsignadoMovil
     */
    public Integer getTotalAsignadoMovil() {
        return detalleReporte.getTotalAsignadasMpp().intValue()
                + detalleReporte.getTotalAsignadasCpp().intValue();
    }

    /**
     * Porcentaje utilizacion Movil.
     * @return porcentajeUtilizacionMovil
     */
    public String getPorcentajeUtilizacionMovil() {
        return ((detalleReporte.getTotalAsignadasMpp().intValue()
                + detalleReporte.getTotalAsignadasCpp().intValue() == 0)
                || (detalleReporte.getTotalLineasActivasCpp().intValue()
                + detalleReporte.getTotalLineasActivasMpp().intValue() == 0)) ? "0 %" : String.format("%.2f",
                (detalleReporte.getTotalLineasActivasCpp().intValue() + detalleReporte.getTotalLineasActivasMpp()
                        .intValue())
                        * 100.0
                        / (detalleReporte.getTotalAsignadasMpp().intValue() + detalleReporte.getTotalAsignadasCpp()
                                .intValue()))
                + " %";
    }

    /**
     * Porcentaje utilizacion total.
     * @return porcentajeTotal
     */
    public String getPorcentajeTotal() {
        return (detalleReporte.getTotalAsignadas().intValue() == 0 || detalleReporte.getTotalLineasActivas()
                .intValue() == 0)
                ? "0 %" : String.format("%.2f", detalleReporte.getTotalLineasActivas().intValue() * 100.0
                        / detalleReporte.getTotalAsignadas().intValue())
                        + " %";
    }

}
