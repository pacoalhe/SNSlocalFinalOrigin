package mx.ift.sns.negocio.not;

import java.io.InputStream;

/**
 * Interfaz del Servicio de Mail.
 */
public interface IMailService {

    /**
     * Envía un correo.
     * @param to destinatario
     * @param subject asunto
     * @param body correo
     */
    void sendEmail(String to, String subject, String body);

    /**
     * Envía emails. El cuerpo es un string, no es html.
     * @param to String
     * @param subject String
     * @param body String
     */
    void sendEmailAsync(String to, String subject, String body);

    /**
     * Método que envía el email de recuperación de contrasña en el frontend. El String body es código html.
     * @param to String
     * @param subject String
     * @param body String
     * @param stream InputStream
     */

    void sendEmailAsyncFront(String to, String subject, String body, InputStream stream);
}
