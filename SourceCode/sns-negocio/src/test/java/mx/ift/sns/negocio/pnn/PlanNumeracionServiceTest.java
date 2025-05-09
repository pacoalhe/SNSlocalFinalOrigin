package mx.ift.sns.negocio.pnn;

import java.util.Calendar;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.ift.sns.context.TestCaseContext;
import mx.ift.sns.modelo.pnn.TipoPlan;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.negocio.ng.NumeracionGeograficaService;
import mx.ift.sns.negocio.nng.ISeriesNngService;
import mx.ift.sns.negocio.nng.SeriesNngService;

/**
 * Casos unitarios para pruebas de generacion de plan de numeracion de ABD
 * Presuscripcion.
 */
public class PlanNumeracionServiceTest extends TestCaseContext {

	// /** Logger de la clase . */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PlanNumeracionServiceTest.class);

	private static IPlanNumeracionService servicio;
	private static IPlanNumeracionJob servicioJob;
	private static INumeracionGeograficaService numGeoService;
	private static ISeriesNngService seriesNngService;

	/**
	 * Constructor, inicializa la clase de la que se hereda.
	 * 
	 * @throws Exception
	 *             en caso de error.
	 */
	public PlanNumeracionServiceTest() throws Exception {
		// IMPORTANTE: Es necesario llamar al constructor de la clase
		// 'TestCaseContext' para que se inicialice el Entorno
		super();

		servicioJob = (IPlanNumeracionJob) getEjbBean(PlanNumeracionJob.class);
		servicio = (IPlanNumeracionService) getEjbBean(PlanNumeracionService.class);
		//seriesNngService = (ISeriesNngService) getEjbBean(SeriesNngService.class);
    	numGeoService = (INumeracionGeograficaService) getEjbBean(NumeracionGeograficaService.class);
	}

	@Ignore
	@Test
	public void test1() throws Exception {

		LOGGER.info("init");
		TipoPlan tipoPlan = servicio
				.getTipoPlanbyId(TipoPlan.TIPO_PLAN_ABD_PORTABILIDAD);
		servicio.getNombre(tipoPlan, Calendar.getInstance());
		LOGGER.info("fin");
	}

	@Ignore
	@Test
	public void testPlanABD() throws Exception {
		LOGGER.info("init");
		try {
			servicioJob.generarReporteABDPortabilidad();
			// servicioJob.generarPlanNumeracionABDPresuscripcion();
			//servicioJob.generarPlanNumeracionGeograficaPST();
			//servicioJob.generarPlanNumeracionGeograficaPublico();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("", e);
			throw e;
		}
		LOGGER.info("fin");
	}

	@Ignore
	@Test
	public void testIdOperador() throws Exception {
		LOGGER.info("init");

		try {
			servicioJob.generarIdentificadoresOperadores();

		} catch (Exception e) {
			LOGGER.error("", e);
			throw e;
		}
		LOGGER.info("fin");
	}

	@Ignore
	@Test
	public void testPlanIFT() throws Exception {
		LOGGER.info("init");

		try {
			servicioJob.generarPlanIFT();

		} catch (Exception e) {
			LOGGER.error("", e);
			throw e;
		}
		LOGGER.info("fin");
	}

	@Ignore
	@Test
	public void testPlanNng() throws Exception {
		LOGGER.info("init");
		try {
			// servicioJob.generarPlanNngEspecifica();
			servicioJob.generarPlanNngEspecificaPst();
			// servicioJob.generarPlanNngEspecificaIFT();
			//servicioJob.generarPlanNngPst();

			// List<ClaveServicio> listaClaves =
			// seriesNngService.findAllClaveServicioAsignadas();
			// for (ClaveServicio clave : listaClaves) {
			// servicioJob.generarPlanNngPublico(clave);
			// }

			// servicioJob.generarPlanNngIFT();
		} catch (Exception e) {
			LOGGER.error("", e);
			throw e;
		}
		LOGGER.info("fin");
	}

	@Ignore
	@Test
	public void testPlanNg() throws Exception {
		LOGGER.info("init");

		try {
			servicioJob.generarPlanNumeracionGeograficaPST();
			//servicioJob.generarPlanNumeracionGeograficaPublico();
		} catch (Exception e) {
			LOGGER.error("", e);
			throw e;
		}
		LOGGER.info("fin");
	}

	@Ignore
	@Test
	public void testDeletePlanes() throws Exception {
		LOGGER.info("init");

		try {
			servicio.deletePlanesViejos();

		} catch (Exception e) {
			LOGGER.error("", e);
			throw e;
		}
		LOGGER.info("fin");
	}

}
