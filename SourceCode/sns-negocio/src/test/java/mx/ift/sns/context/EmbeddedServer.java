package mx.ift.sns.context;

import java.io.File;
import java.util.HashMap;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servidor Embebido para pruebas. Facilita un contenedor EJB y Datasources para utilizar con las pruebas JUnit.
 */
public final class EmbeddedServer {

    /** Logger de la clase. */
    private static Logger logger = LoggerFactory.getLogger(EmbeddedServer.class);

    /** Instancia Singleton. */
    private static EmbeddedServer instancia = null;

    /** Contexto Persistente. */
    private Context context;

    /** Contenedor de EJBs. */
    private EJBContainer ejbContainer;

    /** Numero de llamadas a init. */
    private static int cuenta = 0;

    /**
     * Recupera la instancia singleton de la clase.
     * @return Instancia única EmbeddedServer
     * @throws Exception En caso de no poder iniciar el servidor
     */
    public static synchronized EmbeddedServer getInstance() throws Exception {
        if (instancia == null) {
            instancia = new EmbeddedServer();
        }
        return instancia;
    }

    /**
     * Inicializa el Servidor Embebido.
     * @throws Exception En caso de no poder iniciar el servidor
     */
    private EmbeddedServer() throws Exception {

        File f = new File("src/test/resources/log4j.properties");
        System.out.println("f" + f.getAbsolutePath());
        PropertyConfigurator.configure(f.getAbsolutePath());

        logger = LoggerFactory.getLogger(EmbeddedServer.class);

        HashMap<String, Object> containerProperties = new HashMap<String, Object>(1);
        // containerProperties.put(EJBContainer.PROVIDER, "tomee-embedded");
        containerProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.core.LocalInitialContextFactory");
        containerProperties.put("jdbc/SNS", "new://Resource?type=DataSource");
        containerProperties.put("jdbc/SNS.JdbcDriver", "oracle.jdbc.driver.OracleDriver");

        // BBDD DESA
        //containerProperties.put("jdbc/SNS.JdbcUrl", "jdbc:oracle:thin:@10.228.166.142:1521:ORCL");
        //containerProperties.put("jdbc/SNS.UserName", "SNS");
        //containerProperties.put("jdbc/SNS.Password", "password01");

        // BBDD DESA - PARAGUAY
        // containerProperties.put("jdbc/SNS.JdbcUrl", "jdbc:oracle:thin:@10.228.152.170:1547:PARAGUAY");
        // containerProperties.put("jdbc/SNS.UserName", "XCEDOC");
        // containerProperties.put("jdbc/SNS.Password", "XCEDOCPY");

        // BBDD QA
        //containerProperties.put("jdbc/SNS.JdbcUrl", "jdbc:oracle:thin:@10.34.144.114:1521:sns");
        //containerProperties.put("jdbc/SNS.UserName", "sns");
        //containerProperties.put("jdbc/SNS.Password", "5n5Num3r&");
        //containerProperties.put("jdbc/SNS.JtaManaged", "true");

        // BBDD QA
//        containerProperties.put("jdbc/SNS.JdbcUrl", "jdbc:oracle:thin:@10.34.144.114:1521:sns");
//        containerProperties.put("jdbc/SNS.JdbcUrl", "jdbc:oracle:thin:@10.34.159.59:1521:envqa");
        containerProperties.put("jdbc/SNS.JdbcUrl", "jdbc:oracle:thin:@172.17.42.88:1521:qaora");
        containerProperties.put("jdbc/SNS.UserName", "sns");
//        containerProperties.put("jdbc/SNS.Password", "5n5Num3r&");
        containerProperties.put("jdbc/SNS.Password", "b002cd73acd90cd0b62d08658");
        containerProperties.put("jdbc/SNS.JtaManaged", "true");
        
        
        // BBDD PRO
        //containerProperties.put("jdbc/SNS.JdbcUrl", "jdbc:oracle:thin:@10.34.159.51:1521:snsprod");
        //containerProperties.put("jdbc/SNS.UserName", "sns");
        //containerProperties.put("jdbc/SNS.Password", "dd3e99260f33642e3fe6800b4beb07");
        //containerProperties.put("jdbc/SNS.JtaManaged", "true");
        
        // PRUEBAS LOCAL
        // containerProperties.put("jdbc/SNS.JdbcUrl", "jdbc:oracle:thin:@127.0.0.1:1521:XE");
        // containerProperties.put("openejb.validation.output.level", " medium");

        /*
         * containerProperties.put("log4j.logger.mx.ift.sns", "debug"); containerProperties.put("log4j.rootLogger",
         * "debug,C"); containerProperties.put("log4j.category.OpenEJB", "warn");
         * containerProperties.put("log4j.category.OpenEJB.options", "info");
         * containerProperties.put("log4j.category.OpenEJB.server", "info");
         * containerProperties.put("log4j.category.OpenEJB.startup", "info");
         * containerProperties.put("log4j.category.OpenEJB.startup.service", "warn");
         * containerProperties.put("log4j.category.OpenEJB.startup.config", "info");
         * containerProperties.put("log4j.category.OpenEJB.hsql", "info");
         * containerProperties.put("log4j.category.CORBA-Adapter", "info");
         * containerProperties.put("log4j.category.Transaction", "warn");
         * containerProperties.put("log4j.category.org.apache.activemq", "error");
         * containerProperties.put("log4j.category.org.apache.geronimo", "error");
         * containerProperties.put("log4j.category.openjpa", "error");
         * containerProperties.put("log4j.category.mx.ift.sns", "debug"); containerProperties.put("log4j.logger.mx",
         * "debug"); containerProperties.put("log4j.category.mx", "debug"); containerProperties.put("log4j.appender.C",
         * "org.apache.log4j.ConsoleAppender"); containerProperties.put("log4j.appender.C.layout",
         * "org.apache.log4j.SimpleLayout");
         */

        // SMTP properties
        containerProperties.put("mail/snsMailSession", "new://Resource?type=javax.mail.Session");
        containerProperties.put("mail/snsMailSession.mail.transport.protocol", "smtp");
        containerProperties.put("mail/snsMailSession.mail.smtp.host", "172.27.1.5");
        containerProperties.put("mail/snsMailSession.mail.smtp.port", "25");
        containerProperties.put("mail/snsMailSession.mail.smtp.auth", "true");
        containerProperties.put("mail/snsMailSession.mail.from", "sns@ift.org.mx");
//        containerProperties.put("mail/snsMailSession.mail.smtp.password", "c0rt31ngl3s");
        containerProperties.put("mail/snsMailSession.mail.smtp.password", "bad");
//        containerProperties.put("mail/snsMailSession.mail.smtp.user", "sns");
        containerProperties.put("mail/snsMailSession.mail.smtp.user", "sns_prueba");
        containerProperties.put("mail/snsMailSession.mail.smtp.starttls.enable", "false");

        ejbContainer = EJBContainer.createEJBContainer(containerProperties);
        context = ejbContainer.getContext();
    }

    /**
     * Libera el contenedor de EJBs.
     * @throws Exception En caso de no poder cerrar el contenedor
     */
    public synchronized void init() throws Exception {
        logger.info("");
        logger.info("Inicialización del Servidor Embebido y Contexto #{}", cuenta);
        logger.info("");
        logger.info("");
        cuenta++;
    }

    /**
     * Libera el contenedor de EJBs.
     * @throws Exception En caso de no poder cerrar el contenedor
     */
    public synchronized void cerrarServidor() throws Exception {
        // cuenta--;

        // if (cuenta == 0) {

        // context.close();
        // if (ejbContainer != null) {
        // ejbContainer.close();
        // }

        // instancia = null;
        // }
    }

    /**
     * Recupera el EJB del contexto según los parámetros facilitados.
     * @param pNombreBean Nombre de la clase
     * @param pNombreInterfaz Nombre del Interfaz que implementa el Bean
     * @param pPaquete Nombre del paquete donde se encuentra el bean
     * @return Object casteable al Interfaz implementado por el bean
     * @throws Exception en caso de no encontrar el bean indicado
     */
    public Object getEjbBean(String pNombreBean, String pNombreInterfaz, String pPaquete) throws Exception {

        // String jndiEjBean =
        // "java:global/sns-negocio/GeneracionOficioService!mx.ift.sns.negocio.ng.IGeneracionOficioService";

        StringBuffer jndiName = new StringBuffer();
        jndiName.append("java:global"); // Entorno Java
        jndiName.append("/sns-negocio/"); // Módulo
        jndiName.append(pNombreBean); // Bean
        jndiName.append("!").append(pPaquete).append("."); // package del interfaz
        jndiName.append(pNombreInterfaz); // Bean

        logger.info("{}", jndiName);

        return context.lookup(jndiName.toString());
    }

}
