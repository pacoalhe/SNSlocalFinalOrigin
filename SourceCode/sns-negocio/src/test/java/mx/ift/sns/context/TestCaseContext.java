package mx.ift.sns.context;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Proporciona los métodos comunes para la generación de Test JUnit en un Contexto propio.
 * @author X53490DE
 */
public abstract class TestCaseContext {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TestCaseContext.class);

    /** Constructor, vacío por defecto. */
    public TestCaseContext() {
    }

    /**
     * Inicializa el Servidor Embebido y el contexto.
     * @throws Exception en caso de error.
     */
    @BeforeClass
    public static void inicializar() throws Exception {
        LOGGER.info("Inicialización del Servidor Embebido y Contexto");
        EmbeddedServer.getInstance().init();
    }

    /**
     * Cierra el Servidor Embebido y el contexto.
     * @throws Exception en caso de error.
     */
    @AfterClass
    public static void finalizar() throws Exception {
        LOGGER.info("Cierre de Contexto");
        EmbeddedServer.getInstance().cerrarServidor();
    }

    /**
     * Recupera un bean del contexto.
     * @param pBeanClass Nombre de la clase anotada como EJB
     * @return Instancia EJB de la clase
     * @throws Exception 'NamingException' si no encuentra el bean en el contexto
     */
    @SuppressWarnings("rawtypes")
    protected static Object getEjbBean(Class pBeanClass) throws Exception {

        String beanName = pBeanClass.getSimpleName();
        String packageName = pBeanClass.getPackage().toString().replace("package ", "");

        Class[] interfaces = pBeanClass.getInterfaces();
        if (interfaces.length == 0) {
            throw new Exception("El Bean '" + beanName + "' requiere un Interfaz.");
        }
        if (interfaces.length > 1) {
            throw new Exception("El Bean '" + beanName + "' implementa múltiples interfaces.");
        }
        String interfaceName = interfaces[0].getSimpleName();

        LOGGER.debug("n {} {} {}", beanName, interfaceName, packageName);
        return EmbeddedServer.getInstance().getEjbBean(beanName, interfaceName, packageName);
    }
    
    protected static Object getEjbBean(Class pBeanClass,String beanName,String packageName) throws Exception {

//      String beanName = pBeanClass.getSimpleName();
//      String packageName = pBeanClass.getPackage().toString().replace("package ", "");

      Class[] interfaces = pBeanClass.getInterfaces();
      if (interfaces.length == 0) {
          throw new Exception("El Bean '" + beanName + "' requiere un Interfaz.");
      }
      if (interfaces.length > 1) {
          throw new Exception("El Bean '" + beanName + "' implementa múltiples interfaces.");
      }
      String interfaceName = interfaces[0].getSimpleName();

      LOGGER.debug("n {} {} {}", beanName, interfaceName, packageName);
      return EmbeddedServer.getInstance().getEjbBean(beanName, interfaceName, packageName);
  }
}
