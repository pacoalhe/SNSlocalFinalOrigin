package mx.ift.sns.web.common.session;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.ejb.EJB;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import mx.ift.sns.modelo.usu.Usuario;
import mx.ift.sns.negocio.usu.IUsuariosService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Control de sesiones.
 */
@WebListener
public class SessionListener implements HttpSessionListener {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionListener.class);

    /** Servicio de usuarios. */
    @EJB(mappedName = "UsuariosService")
    private IUsuariosService usuariosService;

    /** att de sesion. */
    private static final String ATT_UID = "uid";

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        String randomId = generateRandomId();
        session.setAttribute("CSRFTOKEN_NAME", randomId);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("sesion creada id '{}' token '{}'", event.getSession().getId(), event.getSession()
                    .getAttribute("CSRFTOKEN_NAME"));
        }
    }

    /**
     * Método que genera un id aleatorio para poder chequear los formularios protegicos contra ataques CSRF.
     * @return String
     */
    private String generateRandomId() {
        String token = "";
        try {
            // Se inicializa en secureRandom
            SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");

            // Generamos un numero aleatorio
            String randomNum = new Integer(prng.nextInt()).toString();

            // Creamos el digest
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte[] result = sha.digest(randomNum.getBytes());

            token = hexEncode(result);
            return token;
        } catch (NoSuchAlgorithmException ex) {
            System.err.println(ex);
        }
        return token;
    }

    /**
     * Método que decodifica a String un valor de tipo byte.
     * @param aInput byte[]
     * @return String
     */
    private static String hexEncode(byte[] aInput) {
        StringBuilder result = new StringBuilder();
        char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        for (int idx = 0; idx < aInput.length; ++idx) {
            byte b = aInput[idx];
            result.append(digits[(b & 0xf0) >> 4]);
            result.append(digits[b & 0x0f]);
        }
        return result.toString();
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("sesion destruida id '{}'", event.getSession().getId());
        }

        try {
            String uid = (String) event.getSession().getAttribute(ATT_UID);

            event.getSession().setAttribute(ATT_UID, null);
            if (StringUtils.isNotEmpty(uid)) {

                Usuario usuario = null;
                if (usuariosService != null) {
                    usuario = usuariosService.findUsuario(uid);
                    usuariosService.logout(usuario);
                } else {
                    LOGGER.debug("usuariosservice es null");
                }
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("sesion usuario {} timeout", uid);
            }
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("error cerrando sesion", e);
            }
        }
    }
}
