package mx.ift.sns.negocio.num;

import mx.ift.sns.context.TestCaseContext;
import mx.ift.sns.negocio.num.model.Numero;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Casos unitarios para pruebas de generacion de plan de numeracion de ABD Presuscripcion.
 */
public class NumeracionServiceTest extends TestCaseContext {

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(NumeracionServiceTest.class);

    private static INumeracionService servicio;

    /**
     * Constructor, inicializa la clase de la que se hereda.
     * @throws Exception error
     */
    public NumeracionServiceTest() throws Exception {
        super();

        servicio = (INumeracionService) getEjbBean(NumeracionService.class);
    }

    @Ignore
    @Test
    public void testNumeroLocal() throws Exception {
        LOGGER.info("init");

        try {
            Numero numeroLocal = servicio.parseNumeracion("3719700000");
            LOGGER.debug("num {}", numeroLocal);

        } catch (Exception e) {
            LOGGER.error("", e);
            throw e;
        }
        LOGGER.info("fin");
    }

    @Ignore
    @Test
    public void testNumeroLocal2() throws Exception {
        LOGGER.info("init");

        try {
            Numero numeroLocal = servicio.parseNumeracion("197" + "0000");
            LOGGER.debug("num {}", numeroLocal);

        } catch (Exception e) {
            LOGGER.error("", e);
            throw e;
        }
        LOGGER.info("fin");
    }

    @Ignore
    @Test(expected = Exception.class)
    public void testNumeracionInvalida1() throws Exception {
        LOGGER.info("init");

        Numero num = servicio.parseNumeracion("333");
        LOGGER.debug("{}", num);

        LOGGER.info("fin");
    }

    @Ignore
    @Test(expected = Exception.class)
    public void testNumeracionInvalida2() throws Exception {
        LOGGER.info("init");

        Numero num = servicio.parseNumeracion("333000000");
        LOGGER.debug("{}", num);

        LOGGER.info("fin");
    }

    @Ignore
    @Test(expected = Exception.class)
    public void testNumeracionInvalida3() throws Exception {
        LOGGER.info("init");

        Numero num = servicio.parseNumeracion("3330000099900");
        LOGGER.debug("{}", num);

        LOGGER.info("fin");
    }

    @Ignore
    @Test
    public void testNumeracionInvalida4() throws Exception {
        LOGGER.info("init");

        Numero num = servicio.parseNumeracion("27" + "2112" + "1234");

        LOGGER.debug("{}", num);
        LOGGER.info("fin");
    }

    @Ignore
    @Test
    public void testNumeracionValida4() throws Exception {
        LOGGER.info("init");

        Numero num = servicio.parseNumeracion("55" + "2112" + "1234");

        LOGGER.debug("{}", num);
        LOGGER.info("fin");
    }

    @Ignore
    @Test
    public void testNumeracionValida1() {
        LOGGER.info("init");

        try {
            String nir = "371";
            String sna = "970";
            String num = "0000";

            Numero n = servicio.parseNumeracion(nir + sna + num);

            Assert.assertTrue(nir.equals(n.getCodigoNir()));
            Assert.assertTrue(sna.equals(n.getSna()));
            Assert.assertTrue(num.equals(n.getNumeroInterno()));

        } catch (Exception e) {
            LOGGER.error("", e);
            throw e;
        }

        LOGGER.info("fin");
    }

    @Ignore
    @Test
    public void testNumeracionValida5() {
        LOGGER.info("init");

        try {
            String nir = "371";
            String sna = "197";
            String num = "0000";

            Numero n = servicio.parseNumeracion(nir + sna + num);

            Assert.assertTrue(nir.equals(n.getCodigoNir()));
            Assert.assertTrue(sna.equals(n.getSna()));
            Assert.assertTrue(num.equals(n.getNumeroInterno()));

        } catch (Exception e) {
            LOGGER.error("", e);
            throw e;
        }

        LOGGER.info("fin");
    }
}
