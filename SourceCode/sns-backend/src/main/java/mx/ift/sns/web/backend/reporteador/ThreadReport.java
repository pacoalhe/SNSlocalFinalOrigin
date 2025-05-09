package mx.ift.sns.web.backend.reporteador;

import mx.ift.sns.negocio.IReporteadorFacade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author T3500028 Hilo para reporte asincrono debido al tiempo en generar el reporte
 */
public class ThreadReport implements Runnable {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadReport.class);

    /**
     * Bean del reporte.
     */
    private ReporteadorNGFechasBean reporteBean;

    /**
     * Servicios del reporteador.
     */
    private IReporteadorFacade reporteadorFacade;
    /**
     * parado.
     */
    boolean parado = true;

    @Override
    public void run() {
        try {
            parado = false;
            byte[] file = reporteadorFacade.getReporteNGPorFechas(reporteBean.getFiltros());
            if (!Thread.currentThread().isInterrupted()) {
                reporteBean.setFile(file);
            }

        } catch (Exception e) {
            LOGGER.error("error", e);
            reporteBean.setAcabado(true);
            reporteBean.setExistePeticion(false);
        }
    }
    /**
     * stop.
     */
    public void stop() {
        parado = true;
    }

    /**
     * @return the reporteBean
     */
    public ReporteadorNGFechasBean getReporteBean() {
        return reporteBean;
    }

    /**
     * @param reporteBean the reporteBean to set
     */
    public void setReporteBean(ReporteadorNGFechasBean reporteBean) {
        this.reporteBean = reporteBean;
    }

    /**
     * @return the reporteadorFacade
     */
    public IReporteadorFacade getReporteadorFacade() {
        return reporteadorFacade;
    }

    /**
     * @param reporteadorFacade the reporteadorFacade to set
     */
    public void setReporteadorFacade(IReporteadorFacade reporteadorFacade) {
        this.reporteadorFacade = reporteadorFacade;
    }

}
