package mx.ift.sns.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Version del modulo.
 */
public final class Version {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Version.class);

    /** Version del SNS. Formato: x.y.z : x versión, y modulo, z parches. */
    private static String version = null;

    /** Modulo del sns. */
    private static String modulo = null;

    /** Nombre del fichero del modulo. */
    private static final String FICHERO_CONFIGURACION = "/module.properties";

    /** Clave que indica que SEIE es el activo. */
    private static final String KEY_VERSION = "version";

    /** Clave que indica el c�digo EIC del SEIE activo. */
    private static final String KEY_MODULO = "modulo";

    /*
     * Accedemos al fichero de configuración en un bloque estático, los valores están así disponibles sin necesidad de
     * instanciar clase alguna.
     */
    static {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream isProps = loader.getResourceAsStream(FICHERO_CONFIGURACION);
        if (isProps == null) {
            LOGGER.error("No es posible leer el fichero de configuración " + FICHERO_CONFIGURACION);
        } else {
            try {
                Properties props = new Properties();
                props.load(isProps);
                isProps.close();

                version = props.getProperty(KEY_VERSION);
                if (version == null) {
                    LOGGER.error("No hay definida clave " + KEY_VERSION + " en el fichero de configuración "
                            + FICHERO_CONFIGURACION);
                }

                modulo = props.getProperty(KEY_MODULO);
                if (modulo == null) {
                    LOGGER.error("No hay definida clave " + KEY_MODULO + " en el fichero de configuración "
                            + FICHERO_CONFIGURACION);
                }

                StringBuilder sb = new StringBuilder();
                sb.append("version ").append(version).append(" modulo ").append(modulo);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(sb.toString());
                }

            } catch (IOException ex) {
                LOGGER.error("No es posible acceder al fichero de configuración " + FICHERO_CONFIGURACION, ex);
            }
        }
    }

    /**
     * Constructor privado.
     */
    private Version() {

    }

    /**
     * Devuelve la version.
     * @return version
     */
    public static String getVersion() {
        return version;
    }

    /**
     * Devuelve la version.
     * @return version
     */
    public static String getModulo() {
        return modulo;
    }
}
