package mx.ift.sns.negocio;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.ift.sns.context.TestCaseContext;
import mx.ift.sns.negocio.sched.IScheduler;


public class SchedulerTest extends TestCaseContext
{
	

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerTest.class);

//    private static IScheduler servicio;

    /**
     * Constructor, inicializa la clase de la que se hereda.
     * @throws Exception
     */
    public SchedulerTest() throws Exception {
        // IMPORTANTE: Es necesario llamar al constructor de la clase
        // 'TestCaseContext' para que se inicialice el Entorno
        super();

//        servicio = (IScheduler) getEjbBean(Scheduler.class);
    }
    
    @Ignore
    @Test
    public void test1SchedulerSyncABD() {

        LOGGER.info("init");

        try {
//            servicio.fireCuarentena();
//            servicio.fireSincronizarABD();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        LOGGER.info("fin");
    }
    
    @Test
    public void testSchedulerSyncABD() {

        LOGGER.info("testSchedulerSyncABD");

        try {
        	IScheduler servicio = (IScheduler) getEjbBean(Scheduler.class,Scheduler.class.getSimpleName(),Scheduler.class.getPackage().toString().replace("package ", "")+".sched");
            servicio.fireSincronizarABD();
        } catch (Exception e) {
        	System.out.println("error: "+e);
            LOGGER.error(e.getMessage());
        }

        LOGGER.info("fin");
    }

}
