package mx.ift.sns.negocio.not;

import mx.ift.sns.context.TestCaseContext;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Casos unitarios para pruebas de envio de correo.
 */
public class MailServiceTest extends TestCaseContext {

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceTest.class);

    /** Servicio de Mail. */
    private static IMailService servicio;

    /**
     * Constructor, inicializa la clase de la que se hereda.
     * @throws Exception Exception
     */
    public MailServiceTest() throws Exception {
        // IMPORTANTE: Es necesario llamar al constructor de la clase
        // 'TestCaseContext' para que se inicialice el Entorno
        super();

        servicio = (IMailService) getEjbBean(MailService.class);
    }

    /**
     * @throws InterruptedException Excepción
     */
    @Ignore
    @Test
    public void test1() throws InterruptedException {

        LOGGER.info("init");

        servicio.sendEmail("jparanda@iecisa.com.mx", "Test Mail SNS",
                "dfadf");

        Thread.sleep(5 * 1000);
        LOGGER.info("fin");
    }

    /**
     * @throws InterruptedException Excepción
     */
    @Ignore
    @Test
    public void test2() throws InterruptedException {

        LOGGER.info("init");

        servicio.sendEmail("emiliano_martincandelario@ieci.es", "mail de21 prueba",
                "dfadf");

        Thread.sleep(5 * 1000);
        LOGGER.info("fin");
    }

}
