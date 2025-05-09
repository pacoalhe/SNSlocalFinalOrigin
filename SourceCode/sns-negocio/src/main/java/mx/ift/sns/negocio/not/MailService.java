package mx.ift.sns.negocio.not;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio de correo.
 */
@Stateless(mappedName = "MailService")
@Remote(IMailService.class)
public class MailService implements IMailService {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);

    /** Parametro from. */
    private static final String PARAM_MAIL_FROM = "mail.from";

    /** Parametro host. */
    private static final String PARAM_MAIL_HOST = "mail.smtp.host";

    /** Parametro puerto. */
    private static final String PARAM_MAIL_PORT = "mail.smtp.port";

    /** Parametro si usa auth. */
    private static final String PARAM_MAIL_AUTH = "mail.smtp.auth";

    /** Parametro tls. */
    private static final String PARAM_MAIL_TLS = "mail.smtp.starttls.enable";

    /** Parametro usuario. */
    private static final String PARAM_MAIL_USER = "mail.smtp.user";

    /** Parametro clave. */
    private static final String PARAM_MAIL_PWD = "mail.smtp.password";

    /** Acceso al mail. */
    @Resource(mappedName = "mail/snsMailSession")
    private Session mailSession;

    /**
     * Chequeo de los parámetros de configuración del servicio de Mail.
     * @return True si la configuración es correcta.
     */
    private boolean checkConfig() {
        boolean res = true;
        String from = "";
        String host = "";
        String port = "";
        String auth = "";
        String tls = "";
        String login = "";
        String pass = "";

        if (mailSession == null) {
            LOGGER.error("No existe mail/snsMailSession. Revise la configuración del servicio de Mail en el Servidor.");
            return false;
        }

        try {
            from = mailSession.getProperty(PARAM_MAIL_FROM);
            host = mailSession.getProperty(PARAM_MAIL_HOST);
            port = mailSession.getProperty(PARAM_MAIL_PORT);
            auth = mailSession.getProperty(PARAM_MAIL_AUTH);
            tls = mailSession.getProperty(PARAM_MAIL_TLS);
            login = mailSession.getProperty(PARAM_MAIL_USER);
            pass = mailSession.getProperty(PARAM_MAIL_PWD);
        } catch (Exception e) {
            LOGGER.error("Error de configuración del Servicio de Mail. Existen parámetros sin definir.");
            res = false;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(PARAM_MAIL_FROM + "=" + from);
            LOGGER.debug(PARAM_MAIL_HOST + "=" + host);
            LOGGER.debug(PARAM_MAIL_PORT + "=" + port);
            LOGGER.debug(PARAM_MAIL_AUTH + "=" + auth);
            LOGGER.debug(PARAM_MAIL_TLS + "=" + tls);
            LOGGER.debug(PARAM_MAIL_USER + "=" + login);
        }

        if (StringUtils.isEmpty(from)) {
            LOGGER.error("No esta definido " + PARAM_MAIL_FROM);
            res = false;
        }

        if (StringUtils.isEmpty(host)) {
            LOGGER.error("No esta definido " + PARAM_MAIL_HOST);
            res = false;
        }

        if (StringUtils.isEmpty(port)) {
            LOGGER.error("No esta definido " + PARAM_MAIL_PORT);
            res = false;
        }

        if (StringUtils.isEmpty(auth)) {
            LOGGER.error("No esta definido " + PARAM_MAIL_AUTH);
            res = false;
        }

        if (StringUtils.isEmpty(tls)) {
            LOGGER.error("No esta definido " + PARAM_MAIL_TLS);
            res = false;
        }

        if (StringUtils.isEmpty(login)) {
            LOGGER.error("No esta definido " + PARAM_MAIL_USER);
            res = false;
        }

        if (StringUtils.isEmpty(pass)) {
            LOGGER.error("No esta definido " + PARAM_MAIL_PWD);
            res = false;
        }

        return res;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void sendEmail(String to, String subject, String body) {
        sendEmailAsync(to, subject, body);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @Asynchronous
    public void sendEmailAsync(String to, String subject, String body) {
        if (checkConfig()) {
            Properties mailProps = new Properties();
            mailProps.put(PARAM_MAIL_FROM, mailSession.getProperty(PARAM_MAIL_FROM));
            mailProps.put(PARAM_MAIL_HOST, mailSession.getProperty(PARAM_MAIL_HOST));
            mailProps.put(PARAM_MAIL_PORT, mailSession.getProperty(PARAM_MAIL_PORT));
            mailProps.put(PARAM_MAIL_AUTH, mailSession.getProperty(PARAM_MAIL_AUTH));
            mailProps.put(PARAM_MAIL_TLS, mailSession.getProperty(PARAM_MAIL_TLS));
            mailProps.put(PARAM_MAIL_USER, mailSession.getProperty(PARAM_MAIL_USER));
            mailProps.put(PARAM_MAIL_PWD, mailSession.getProperty(PARAM_MAIL_PWD));

            final String login = mailSession.getProperty(PARAM_MAIL_USER);
            final String pass = mailSession.getProperty(PARAM_MAIL_PWD);
            mailSession = Session.getInstance(mailProps, new Authenticator() {

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(login, pass);
                }
            });

            MimeMessage message = new MimeMessage(mailSession);
            try {
                message.setFrom(new InternetAddress(mailSession.getProperty(PARAM_MAIL_FROM)));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

                message.setSubject(subject, "UTF-8");
                message.setSentDate(new Date());
                message.setContent(message, "text/plain");
                message.setText(body, "UTF-8", "html");

                Transport.send(message);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("correo enviado a '{}'", to);
                }

            } catch (MessagingException ex) {
                LOGGER.error("Error enviando mail", ex.getMessage());
            }
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @Asynchronous
    public void sendEmailAsyncFront(String to, String subject, String body, InputStream stream) {
        if (checkConfig()) {
            Properties mailProps = new Properties();
            mailProps.put(PARAM_MAIL_FROM, mailSession.getProperty(PARAM_MAIL_FROM));
            mailProps.put(PARAM_MAIL_HOST, mailSession.getProperty(PARAM_MAIL_HOST));
            mailProps.put(PARAM_MAIL_PORT, mailSession.getProperty(PARAM_MAIL_PORT));
            mailProps.put(PARAM_MAIL_AUTH, mailSession.getProperty(PARAM_MAIL_AUTH));
            mailProps.put(PARAM_MAIL_TLS, mailSession.getProperty(PARAM_MAIL_TLS));
            mailProps.put(PARAM_MAIL_USER, mailSession.getProperty(PARAM_MAIL_USER));
            mailProps.put(PARAM_MAIL_PWD, mailSession.getProperty(PARAM_MAIL_PWD));

            final String login = mailSession.getProperty(PARAM_MAIL_USER);
            final String pass = mailSession.getProperty(PARAM_MAIL_PWD);
            mailSession = Session.getInstance(mailProps, new Authenticator() {

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(login, pass);
                }
            });

            MimeMessage message = new MimeMessage(mailSession);
            try {
                message.setFrom(new InternetAddress(mailSession.getProperty(PARAM_MAIL_FROM)));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

                message.setSubject(subject);
                message.setSentDate(new Date());
                MimeMultipart multipart = new MimeMultipart("related");

                // Asunto en html
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent(body, "text/html");
                // Añadimos el asunto al body
                multipart.addBodyPart(messageBodyPart);

                // Segunda parte(la imagen)
                messageBodyPart = new MimeBodyPart();
                DataSource fds = new ByteArrayDataSource(stream, "image/*");
                messageBodyPart.setDataHandler(new DataHandler(fds));
                messageBodyPart.setHeader("Content-ID", "<image>");
                // Añadimos la imagen al body
                multipart.addBodyPart(messageBodyPart);
                // Juntamos el asunto y la imagen
                message.setContent(multipart);
                Transport.send(message);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("correo enviado a '{}'", to);
                }

            } catch (MessagingException ex) {
                LOGGER.error("Error enviando mail", ex.getMessage());
            } catch (Exception e) {
                LOGGER.error("error inesperado al mandar el email de recuperación de contraseña", e.getMessage());
            }
        }
    }

}
