package mx.ift.sns.negocio.port;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.ift.sns.context.TestCaseContext;
import mx.ift.sns.negocio.port.modelo.ResultadoParser;

/**
 * Casos unitarios para pruebas de envio de correo.
 */
public class ParseTest extends TestCaseContext {

	/** Logger de la clase . */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ParseTest.class);

	/** Servicio de Portabilidad. */
	private static IPortabilidadService servicio;

	/**
	 * Constructor, inicializa la clase de la que se hereda.
	 * 
	 * @throws Exception
	 *             Excepción
	 */
	public ParseTest() throws Exception {
		// IMPORTANTE: Es necesario llamar al constructor de la clase
		// 'TestCaseContext' para que se inicialice el Entorno
		super();

		this.servicio = (IPortabilidadService) getEjbBean(PortabilidadService.class);
	}

	/**
	 * Test unitario del servicio de portabilidad.
	 */
	@Ignore
	@Test
	public void test1NumerosPortadosXmlParser() {

		LOGGER.info("<--init test parse Numbers Ported xml file--->");

		NumerosPortadosXmlParser parser = new NumerosPortadosXmlParser();
		ResultadoParser res = new ResultadoParser();
		System.out.println("<--Tiempo de inicio:"+getActualDate());
		
		parser.parse("file:/home/jparanda/tmp/NumbersPorted-20161027.xml",
				new File("/home/jparanda/tmp/salida_portados_20161027.txt"), res);
		
		System.out.println("<--Tiempo de de fin:"+getActualDate());
		LOGGER.info("<--Fin test parse Numbers Ported xml file--->");
	}

	/**
	 * Test unitario del servicio de portabilidad.
	*/
	@Ignore
	@Test
	public void test2NumerosDeletedXmlParser() {

		LOGGER.info("<--init test parse Numbers Deleted xml file--->");

		NumerosDeletedXmlParser parser = new NumerosDeletedXmlParser();
		ResultadoParser res = new ResultadoParser();
		parser.parse("file:/home/jparanda/tmp/NumbersDeleted-20160203.xml",
				new File("/home/jparanda/tmp/salida_deleted.txt"), res);

		LOGGER.info("<--init test parse Numbers Deleted xml file--->");
	}
	
	/**
	 * This method get the actual date
	 * @return actual date in format yyyy/MM/dd HH:mm:ss
	 */
	private String getActualDate(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.getTime());
	}
}
