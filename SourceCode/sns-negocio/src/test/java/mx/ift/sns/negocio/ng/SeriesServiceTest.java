package mx.ift.sns.negocio.ng;

import java.util.List;

import mx.ift.sns.context.TestCaseContext;
import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.reporteabd.SerieArrendada;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.negocio.num.INumeracionService;
import mx.ift.sns.negocio.num.NumeracionService;
import mx.ift.sns.negocio.num.model.Numero;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Casos unitarios para pruebas de generacion de plan de numeracion de ABD Presuscripcion.
 */
public class SeriesServiceTest extends TestCaseContext {

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(SeriesServiceTest.class);

    /** Servicio de Series De Numeración Geográfica. */
    private static ISeriesService servicio;

    /** Servicio De Numeración Geográfica. */
    private static INumeracionService servicioNum;

    /**
     * Constructor, inicializa la clase de la que se hereda.
     * @throws Exception Excepción.
     */
    public SeriesServiceTest() throws Exception {
        // IMPORTANTE: Es necesario llamar al constructor de la clase
        // 'TestCaseContext' para que se inicialice el Entorno
        super();

        servicio = (ISeriesService) getEjbBean(SeriesService.class);
        servicioNum = (INumeracionService) getEjbBean(NumeracionService.class);
    }

    /**
     * @throws Exception Excepción
     */
    @Ignore
    @Test
    public void testCreacionSerieArrendada() throws Exception {
        LOGGER.info("init");
        try {
            // INumeracionGeograficaService s =
            // (INumeracionGeograficaService) getEjbBean(NumeracionGeograficaService.class);

            // TipoModalidad tipo = s.getTipoModalidadById(TipoModalidad.CPP);
            // TipoRed tipoRed = s.getTipoRedById(TipoRed.MOVIL);
            SerieArrendada serie = new SerieArrendada();

            /*
             * serie.setArrendador(new BigDecimal(7)); serie.setArrendatario(new BigDecimal(7));
             * serie.getId().setNumIni("12"); serie.getId().setNumFinal("999"); serie.setTipoModalidad(tipo);
             * serie.setTipoRed(tipoRed);
             */
            servicio.create(serie);
        } catch (Exception e) {
            LOGGER.error("", e);
            throw e;
        }

        LOGGER.info("fin");
    }

    /**
     * @throws Exception Excepción
     */
    @Ignore
    @Test
    public void testRangosAsignados() throws Exception {
        LOGGER.info("init");

        try {
            // BigDecimal n = servicio.getNumRangosAsignados();

            List<RangoSerie> l = servicio.getRangosAsignados(1, 20);

            LOGGER.debug("lista paginada {}", l.size());

        } catch (Exception e) {
            LOGGER.error("", e);
            throw e;
        }
        LOGGER.info("fin");
    }

    /**
     * @throws Exception Excepción
     */
    @Ignore
    @Test
    public void testNumeroLocal() throws Exception {
        LOGGER.info("init");

        try {
            Numero numeroLocal = servicioNum.parseNumeracion("3719700000");
            LOGGER.debug("num {}", numeroLocal);

            servicio.getRangoPertenece(numeroLocal);
            List<Nir> nirs = servicio.findNirsNumeroLocal(numeroLocal);

            LOGGER.debug("lista paginada {}", nirs.size());

        } catch (Exception e) {
            LOGGER.error("", e);
            throw e;
        }
        LOGGER.info("fin");
    }

    /**
     * @throws Exception Excepción
     */
    @Ignore
    @Test
    public void testNumeroLocal2() throws Exception {
        LOGGER.info("init");

        try {
            Numero numeroLocal = servicioNum.parseNumeracion("197" + "0000");
            LOGGER.debug("num {}", numeroLocal);

        } catch (Exception e) {
            LOGGER.error("", e);
            throw e;
        }
        LOGGER.info("fin");
    }

}
