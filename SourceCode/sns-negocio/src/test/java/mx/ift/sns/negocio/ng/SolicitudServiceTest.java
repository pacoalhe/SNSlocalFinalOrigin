package mx.ift.sns.negocio.ng;

import mx.ift.sns.context.TestCaseContext;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolicitudServiceTest extends TestCaseContext {

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudServiceTest.class);

    /** Servicio de Series De Numeración Geográfica. */
    private static ISolicitudesService servicio;

    
    /**
     * Constructor, inicializa la clase de la que se hereda.
     * @throws Exception Excepción.
     */
    public SolicitudServiceTest() throws Exception {
        // IMPORTANTE: Es necesario llamar al constructor de la clase
        // 'TestCaseContext' para que se inicialice el Entorno
        super();

        servicio = (ISolicitudesService) getEjbBean(SolicitudesService.class);
        
    }

    /**
     * @throws Exception Excepción
     */
    @Ignore
    @Test
    public void testEjecucionCederNumeracionesProgramadas() throws Exception {
        LOGGER.info("init");
        try {
            
            
            servicio.applyCesionesPendientes();
        } catch (Exception e) {
            LOGGER.error("", e);
            throw e;
        }

        LOGGER.info("fin");
    }
}
