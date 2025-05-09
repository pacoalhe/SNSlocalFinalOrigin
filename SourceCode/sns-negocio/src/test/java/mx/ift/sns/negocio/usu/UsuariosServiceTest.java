package mx.ift.sns.negocio.usu;

import mx.ift.sns.context.TestCaseContext;
import mx.ift.sns.modelo.usu.Usuario;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Casos unitarios para pruebas del servicio de usuarios.
 */
public class UsuariosServiceTest extends TestCaseContext {

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(UsuariosServiceTest.class);

    /** Servicio para la generación de oficios. */
    private static IUsuariosService service;

    /**
     * Constructor, inicializa la clase de la que se heredada.
     * @throws Exception error
     */
    public UsuariosServiceTest() throws Exception {
        super();

        service = (IUsuariosService) getEjbBean(UsuariosService.class);
    }

    /**
     * Prueba de usuario que no existe.
     */
    @Ignore
    @Test
    public void testBuscarUsuarioNoExiste() {
        LOGGER.info("init");
        Usuario usu=service.findUsuario("rrr");
        Assert.assertNull(usu);
    }

    /**
     * Prueba de usuario existente.
     */
    @Ignore
    @Test
    public void testBuscarUsuarioExiste() {
        LOGGER.info("init");
        Usuario usu=service.findUsuario("sns");
        Assert.assertNotNull(usu);
        
    }
    @Ignore
    @Test
    public void validarUsuarioAccessPubicWeb(){
    	LOGGER.info("<---Inicia la prueba de validacion del usuario");
    	//Boolean response=service.validaUsuario("AT&T");
    	//Assert.assertTrue(response);
    	LOGGER.info("<--Termino la pruebas--->>");
    	
    }
    
    @Ignore
    @Test
    public void validarNuevaContrasenna(){
    	
    	Boolean result=service.validaContrasenna("12345jF$");
    	Assert.assertTrue(result);
    	
    }
}
