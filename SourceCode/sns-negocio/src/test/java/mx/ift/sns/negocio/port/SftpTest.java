package mx.ift.sns.negocio.port;

import java.nio.file.FileSystemException;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.ift.sns.context.TestCaseContext;

/**
 * Casos unitarios para pruebas de envio de correo.
 */
public class SftpTest extends TestCaseContext {

	/** Logger de la clase . */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SftpTest.class);

	// private static IMailService servicio;

	/**
	 * Constructor, inicializa la clase de la que se hereda.
	 * 
	 * @throws Exception
	 *             en caso de error.
	 */
	public SftpTest() throws Exception {
		// IMPORTANTE: Es necesario llamar al constructor de la clase
		// 'TestCaseContext' para que se inicialice el Entorno
		super();

		// servicio = (IMailService) getEjbBean(MailService.class);
	}

	@Ignore
	@Test
	public void test1() throws FileSystemException {

		LOGGER.info("init");

		ConexionSFTP sftp = new ConexionSFTP();
		try {
			sftp.download("10.228.152.163", "21", "portaflow", "portaflow",
					"c:\\tmp\\sns-test.txt", "sns-test.txt");
		} catch (org.apache.commons.vfs2.FileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOGGER.info("fin");
		LOGGER.info("fin");
	}
}
