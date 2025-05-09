package mx.ift.sns.web.frontend.servicios;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 * Clase para validar un form protegido contra ataques de tipo CSRF. Valida si el token introducido en un form protegido
 * manualmente es igual valor del token generando automáticamente en la sesión.
 * @author X51461MO
 */
public final class ProteccionCsrf {
    /**
     * Constructor por defecto.
     */
    private ProteccionCsrf() {
    }

    /**
     * Método que devuelve true si el valor del token del form y el valor del token de sesión son iguales.
     * @param idComponente String
     * @return boolean
     */
    public static boolean checkCsrfToken(String idComponente) {
        boolean seguro = true;
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();

        // Obtenemos el valor del input hidden (token) del form protegido
        String value = externalContext.getRequestParameterMap().get(idComponente);

        // Obtenemos el valor del token generado de la session.
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        String token = (String) session.getAttribute("CSRFTOKEN_NAME");

        // Chequeamos si existe el valor. Si es existe, chequeamos si el token de sesión y el de input son iguales.
        if (value == null || "".equals(value)) {
            seguro = false;
        } else {
            if (!value.equalsIgnoreCase(token)) {
                seguro = false;
            }
        }
        return seguro;
    }

}
