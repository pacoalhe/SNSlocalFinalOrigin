package mx.ift.sns.negocio;

import java.util.List;

import mx.ift.sns.context.TestCaseContext;
import mx.ift.sns.modelo.abn.PoblacionAbn;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.series.Nir;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Casos unitarios para pruebas de generacion de plan de numeracion de ABD Presuscripcion.
 */
public class ConsultaPublicaTest extends TestCaseContext {

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultaPublicaTest.class);

    /** Servicio a probar. */
    private static IConsultaPublicaFacade servicio;

    /**
     * Constructor, inicializa la clase de la que se hereda.
     * @throws Exception error
     */
    public ConsultaPublicaTest() throws Exception {
        // IMPORTANTE: Es necesario llamar al constructor de la clase
        // 'TestCaseContext' para que se inicialice el Entorno
        super();
        servicio = (IConsultaPublicaFacade) getEjbBean(ConsultaPublicaFacade.class);

    }

    /**
     * test 1.
     * @throws Exception error
     */
    @Ignore
    @Test
    public void testFicherosNoValidos1() throws Exception {
        LOGGER.info("init");

        List<Poblacion> poblaciones = servicio.findAllPoblacionesLikeNombre("aguas");

        LOGGER.debug("num pob " + poblaciones.size());

        if ((poblaciones != null) && (poblaciones.size() > 0)) {
            LOGGER.debug("pob {}", poblaciones.get(0));

            if (poblaciones.get(0).getPoblacionesAbn() != null) {
                for (PoblacionAbn p : poblaciones.get(0).getPoblacionesAbn()) {
                    if (p.getAbn().getNirs() != null) {
                        for (Nir nir : p.getAbn().getNirs()) {
                            LOGGER.debug("nir {}", nir);
                        }
                    }
                }
            }
        }

        LOGGER.info("fin");
    }

    /**
     * test 2.
     * @throws Exception error
     */
    @Ignore
    @Test
    public void testFicherosNoValidos2() throws Exception {
        LOGGER.info("init");

        List<Poblacion> poblaciones = servicio.findAllPoblacionesLikeNombre("a");

        LOGGER.debug("num pob " + poblaciones.size());

        LOGGER.info("fin");
    }

    /**
     * test 2.
     * @throws Exception error
     */
    @Ignore
    @Test
    public void testFicherosNoValidos3() throws Exception {
        LOGGER.info("init");

        List<Poblacion> poblaciones = servicio.findAllPoblacionesLikeNombre("azxy");

        LOGGER.debug("num pob " + poblaciones.size());

        LOGGER.info("fin");
    }
}
