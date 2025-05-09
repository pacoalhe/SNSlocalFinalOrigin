package mx.ift.sns.negocio.port;

/**
 * Parametros del ABD.
 */
public final class ParametrosABD {

    /** Clave del parametro de configuracion del host del ABD. */
    public static final String ABD_HOST = "sns.port.abd.host";

    /** Clave del parametro de configuracion del puerto del ABD. */
    public static final String ABD_PORT = "sns.port.abd.port";

    /** Clave del parametro de configuracion del usuario del ABD. */
    public static final String ABD_USER = "sns.port.abd.user";

    /** Clave del parametro de configuracion de la clave del ABD. */
    public static final String ABD_PASSWORD = "sns.port.abd.password";

    /** Clave del parametro de configuracion del path del ABD. */
    public static final String ABD_PATH = "sns.port.abd.path";

    /** Clave del parametro de configuracion del path del PMN. */
    public static final String PMN_PATH = "sns.port.pmn.path";

    /** Clave del parametro de configuracion de la lista de mail. */
    public static final String ABD_MAIL_LIST = "sns.port.abd.email";

    /** Clave del parámetro de notificación de error de conexión con el servidor del ABD. */
    public static final String ABD_ERROR_SERVIDOR = "portABD.error.conexion.servABD";

    /** Clave del parámetro de notificación de error del fichero de numeraciones portadas. */
    public static final String ABD_ERROR_FICHERO_PORTADAS = "portABD.error.NumPortadas";

    /** Clave del parámetro de notificación de error del fichero de numeraciones canceladas. */
    public static final String ABD_ERROR_FICHERO_CANCELADAS = "portABD.error.PortCanceladas";

    public static final String PORTACION_MANUAL_PORTADOS = "portMANUAL.path.portados";

    public static final String PORTACION_MANUAL_CANCELADOS = "portMANUAL.path.cancelados";

    public static final String PORTACION_MANUAL_ERROR = "portMANUAL.path.error";

    /** host del ABD. */
    private String host;

    /** puerto del ABD. */
    private String port;

    /** usuario del ABD. */
    private String user;

    /** clave del ABD. */
    private String pwd;

    /** path del ABD. */
    private String path;

    /** la lista de mail. */
    private String mailList;

    /**
     * Constructor privado.
     */
    private ParametrosABD() {
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the pwd
     */
    public String getPwd() {
        return pwd;
    }

    /**
     * @param pwd the pwd to set
     */
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the mailList
     */
    public String getMailList() {
        return mailList;
    }

    /**
     * @param mailList the mailList to set
     */
    public void setMailList(String mailList) {
        this.mailList = mailList;
    }
}
