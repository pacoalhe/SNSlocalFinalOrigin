package mx.ift.sns.negocio.ng;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.context.TestCaseContext;
import mx.ift.sns.modelo.ng.NumeracionSolicitada;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Casos unitarios para pruebas del serivio de análisis de solicitudes.
 */
public class AnalisisSolicitudTest extends TestCaseContext {

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalisisSolicitudTest.class);

    /** Constructor, inicializa la clase de la que se hereda. */
    public AnalisisSolicitudTest() {
        // IMPORTANTE: Es necesario llamar al constructor de la clase
        // 'TestCaseContext' para que se inicialice el Entorno
        super();
    }

    /** Método ejecutado después de todos los test de clase. Libera las variables y recursos comunes a los test */
    @AfterClass
    public static void liberarParametros() {

    }

    /**
     * Test para recuperar las numeraciones solicitadas.
     */
    @Test
    public void cargarNumeracionSolicitadas() {

        LOGGER.info("Caso de Test: 'cargarNumeracionSolicitadas'");

        BigDecimal codSolicitud = new BigDecimal(1);

        try {

            INumeracionGeograficaService numService =
                    (INumeracionGeograficaService) getEjbBean(NumeracionGeograficaService.class);
            List<NumeracionSolicitada> numSolicitadas = numService.getNumeracionesSolicitadas(codSolicitud);
            assertNotNull(numSolicitadas);

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

    }

    /**
     * Test para recuperar vacio las numeraciones solicitadas.
     */
    @Test
    public void cargarNumeracionSolicitadasVacio() {

        LOGGER.info("Caso de Test: 'cargarNumeracionSolicitadasVacio'");

        BigDecimal codSolicitud = new BigDecimal(0);

        try {

            INumeracionGeograficaService numService =
                    (INumeracionGeograficaService) getEjbBean(NumeracionGeograficaService.class);
            List<NumeracionSolicitada> numSolicitadas = numService.getNumeracionesSolicitadas(codSolicitud);
            assertEquals(0, numSolicitadas.size());

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

    }

}
