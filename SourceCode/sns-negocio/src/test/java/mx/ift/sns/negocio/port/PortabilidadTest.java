package mx.ift.sns.negocio.port;

import mx.ift.sns.context.TestCaseContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Casos unitarios para pruebas de envio de correo.
 */
public class PortabilidadTest extends TestCaseContext {

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(PortabilidadTest.class);

    private static IPortabilidadService servicio;

    /**
     * Constructor, inicializa la clase de la que se hereda.
     * @throws Exception
     */
    public PortabilidadTest() throws Exception {
        // IMPORTANTE: Es necesario llamar al constructor de la clase
        // 'TestCaseContext' para que se inicialice el Entorno
        super();

        servicio = (IPortabilidadService) getEjbBean(PortabilidadService.class);
    }
    
    @Ignore
    @Test
    public void test1syncBDDPortabilidadWithOutArguments() {

        LOGGER.info("init");

        try {
            servicio.syncBDDPortabilidad();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        LOGGER.info("fin");
    }
    /**
     * The the ported file
     */
    @Ignore
    @Test
    public void test1syncBDDPortabilidadWithArguments() {

        LOGGER.info("<--inicia el Test: "+getActualDate()+" --->");
        String filePath="/home/jparanda/tmp/NumbersPorted-20161231.xml";
        try {
            servicio.syncBDDPortabilidad(filePath, TipoFicheroPort.TIPO_DIARIO_PORTED, null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        LOGGER.info("<---finaliza el test: "+getActualDate()+" --->");
    }
    
    private String getActualDate(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.getTime());
	}
}
