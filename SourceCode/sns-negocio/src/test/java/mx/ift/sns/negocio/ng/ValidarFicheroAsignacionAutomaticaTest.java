package mx.ift.sns.negocio.ng;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.ift.sns.context.TestCaseContext;

public class ValidarFicheroAsignacionAutomaticaTest extends TestCaseContext{
	
	 /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidarFicheroAsignacionAutomaticaTest.class);

    private IValidacionFicheroAsignacionAutomatica validarFicheroService;
    
    
    /** Constructor, inicializa la clase de la que se hereda. */
    public ValidarFicheroAsignacionAutomaticaTest() {
        // IMPORTANTE: Es necesario llamar al constructor de la clase
        // 'TestCaseContext' para que se inicialice el Entorno
        super();
    }
    
    @Before
    public void initTest(){
    	
    	//Creamos una instancia del servicio
    	try {
			this.validarFicheroService= (IValidacionFicheroAsignacionAutomatica) getEjbBean(ValidacionFicheroAsignacionAutomatica.class);
		} catch (Exception e) {
			LOGGER.info("<---Error cargando el EJB del servicio del validadador de carga automatica-->");
			e.printStackTrace();
		}
    	
    }
    
    /**
     * Valida un archivo formado correctamente
     */
    @Test
    public void testValidarFichero(){
    	
    	ClassLoader classLoader = getClass().getClassLoader();
    	File fileToValidar = new File(classLoader.getResource("TestCargaAutomatica.xlsx").getFile());
    	
    	try {
			this.validarFicheroService.validar(fileToValidar);
		} catch (Exception e) {
			LOGGER.info("<---Se presentó un error al validar el archivo {}",fileToValidar.getAbsolutePath());
			e.printStackTrace();
		}
    	
    }
    
 }
