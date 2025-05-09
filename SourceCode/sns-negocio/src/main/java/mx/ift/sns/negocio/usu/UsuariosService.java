package mx.ift.sns.negocio.usu;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import mx.ift.sns.dao.usu.IUsuarioDAO;
import mx.ift.sns.modelo.usu.Usuario;
import mx.ift.sns.negocio.IBitacoraService;
import mx.ift.sns.negocio.not.IMailService;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio de usuarios del SNS.
 */
@Stateless(mappedName = "UsuariosService")
@Remote(IUsuariosService.class)
public class UsuariosService implements IUsuariosService {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(UsuariosService.class);

    /** Usuario Por defecto mientras no se está logueado. */
//    public static final String DEFAULT_USER = "sns";
    public static final String DEFAULT_USER = "pruebas";

    /** Usuario Devuelto por WebLogic cuando no hay permisos o no se está logueado. */
    public static final String SCHEDULER_USER = "<anonymous>";

    /** DAO de usuarios. */
    @Inject
    private IUsuarioDAO usuarioDAO;

    /** EJB de bitacora. */
    @EJB(mappedName="BitacoraService")
    private IBitacoraService bitacoraService;

    /** Contexto de la sesión. */
    @Resource
    private SessionContext sc;

    /** Servicio de mail. */
    @EJB(mappedName = "MailService")
    private IMailService mailService;

    /** random para genrar contraseña. */
    private static final Random RANDOM = new SecureRandom();

    /** Longitud de la contraseña a generar. */
    public static final int PASSWORD_LENGTH = 10;

    @Override
    public Usuario findUsuario(String uid) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("buscando usuario {}", uid);
        }
        return usuarioDAO.findUsuario(uid);
    }

    @Override
    public Usuario findUsuarioById(String idUsuario) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("buscando usuario {}", idUsuario);
        }
        return usuarioDAO.findUsuarioById(idUsuario);
    }

    @Override
    public List<Usuario> findAllUsuarios() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("buscando todos usuarios");
        }

        return usuarioDAO.findAllUsuarios();
    }

    @Override
    public List<Usuario> findAllAnalistas() {
        return usuarioDAO.findAllAnalistas();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Usuario login(Usuario usuario) {

        usuario.setFechaConexion(new Date());
        usuario.setFechaDesconexion(null);
        
        usuario = usuarioDAO.update(usuario);
        bitacoraService.add(usuario, "login");
        return usuario;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void logout(Usuario usuario) {

        usuario.setFechaDesconexion(new Date());
        usuarioDAO.update(usuario);

        bitacoraService.add(usuario, "logout");
    }

    @Override
    public boolean existeUsuario(String idUsuario) {
        Long numUsuarios = usuarioDAO.existeUsuario(idUsuario);
        return (numUsuarios.longValue() > 0);
    }

    @Override
    public boolean validaContrasenna(String password) {
        String patron = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W])(?=\\S+$).{8,}";
        return password.matches(patron);
    }

    @Override
    public boolean existeEmailUsuario(String idUsuario, String email) {
        return usuarioDAO.existeEmailUsuario(idUsuario, email);
    }

    @Override
    public int recuperaContrasena(String idUsuario, String email, InputStream stream) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("usuario {}, email {}", idUsuario, email);
        }
        if (this.validaUsuario(idUsuario)) {
            if (this.validaEmail(email)) {
                if (this.existeEmailUsuario(idUsuario, email)) {
                    Usuario usu = (this.findUsuario(idUsuario));
                    String password = "";
                    String passwordEncriptada = "";
                    password = this.generaContrasenna();
                    passwordEncriptada = this.encriptaContrasena(password);
                    usu.setContrasenna(passwordEncriptada);
                    usu.setPrimeraConexion(Usuario.PRIMERA);
                    Date curDate = new Date();
                    Calendar curDateCal = Calendar.getInstance();
                    curDateCal.setTime(curDate);
                    curDateCal.add(Calendar.MONTH, 1);
                    usu.setFechaCaducidadContrasenna(curDateCal.getTime());
                    usuarioDAO.update(usu);
                    String asunto = "Clave de acceso recuperada";
                    String body = "<html ><head>"
                            + "</head>"
                            + "<body><div>Hola!</div>"
                            + "<div><p>Usted ha recibo este correo electrónico "
                            + "debido a su solicitud de recuperación de contraseña. "
                            + "El SNS le ha asignado la siguiente contraseña: <strong>"
                            + password
                            + "</strong>, es recomendable que una vez ingrese nuevamente "
                            + "realice el cambio de la misma.</p></div><br></br><br></br><br></br>"
                            + "<div style=\"text-align: center\"><h3>Unidad de concesiones y servicios</h3>"
                            + "<img src=\"cid:image\"></div>"
                            + "</body></html>";
                    // String path = FacesContext.getCurrentInstance().getExternalContext().
                    // getRealPath(File.separator + "img" + File.separator + "ift_logo.png");
                    try {
                        mailService.sendEmailAsyncFront(email, asunto, body, stream);
                    } catch (Exception e) {
                        LOGGER.error("Envío de email fallido " + e.getMessage());
                    }
                    return 0;
                } else {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Error de datos");
                    }
                    return 3;
                }

            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Error de email");
                }
                return 2;
            }
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error de usuario");
            }
            return 1;
        }

    }

    /**
     * Valida los caracteres del idUsuario.
     * @param idUsuario String
     * @return boolean
     */
    private boolean validaUsuario(String idUsuario) {
        String usuarioRegex = "[A-Za-z0-9-_ñÑ&.-]+";
        return idUsuario.matches(usuarioRegex);

    }

    /**
     * Valida los caracteres del email.
     * @param email String
     * @return boolean
     */
    private boolean validaEmail(String email) {
        String emailRegex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return email.matches(emailRegex);
    }

    /**
     * Genera un contraseña aleatoria.
     * @return String.
     */
    private String generaContrasenna() {
        String letters = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789+@";
        String newpass = "";
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = (int) (RANDOM.nextDouble() * letters.length());
            newpass += letters.substring(index, index + 1);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Contraseña generada con éxito");
        }
        return newpass;
    }

    /**
     * Encripta la contaseña nueva creada.
     * @param pass String
     * @return String
     */
    @Override
    public String encriptaContrasena(String pass) {
        String encript = "";
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] hexByte = sha1.digest((pass).getBytes());
            String s = Base64.encodeBase64String(hexByte);
            encript = "{SHA-1}" + s;
        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("password encriptada");
        }
        return encript;
    }

    @Override
    public boolean validaClave(String clave) {
        String claveRegex = "[A-Za-z0-9-_]+";
        return (clave.matches(claveRegex));
    }

    @Override
    public int crearNuevaClave(String clave, String claveRep, Usuario pUsuario) {

        if (clave.compareTo(claveRep) == 0) {

            if (this.validaContrasenna(clave)) {
                String encripPass = this.encriptaContrasena(clave);
                pUsuario.setContrasenna(encripPass);
                pUsuario.setPrimeraConexion(Usuario.NOPRIMERA);
                Date curDate = new Date();
                Calendar curDateCal = Calendar.getInstance();
                curDateCal.setTime(curDate);
                curDateCal.add(Calendar.MONTH, 1);
                pUsuario.setFechaCaducidadContrasenna(curDateCal.getTime());
                usuarioDAO.update(pUsuario);
                bitacoraService.add(pUsuario, "Cambio de contraseña");
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Contraseña actualizada");
                }
                return 0;
            } else {
                return 1;
            }
        } else {
            return 2;
        }

    }

    @Override
    public Usuario getCurrentUser() {
        // El Weblogic y LDAP se encargan de validar al usuario logueado. Si estamos logueados existirá usuario, si no,
        // éste método es requerido tambiñén por el planificador de tareas nocturno (scheduler) y necesita el usuario
        // por defecto para aplicar las operaciones. Según la configuración de Weblogic, en el caso del planificador,
        // el usuario puede ser nulo o 'anonymous'
        String currentUser = sc.getCallerPrincipal().getName();
        if (!StringUtils.isEmpty(currentUser)) {
            if (currentUser.equals(SCHEDULER_USER)) {
                // Ejecuciones del planificador.
                return findUsuario(DEFAULT_USER);
            }
            // Estamos logueados con un usuario.
            return findUsuario(currentUser);
        } else {
            // Ejecuciones del planificador.
            return findUsuario(DEFAULT_USER);
        }
    }
}
