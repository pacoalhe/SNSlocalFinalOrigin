package mx.ift.sns.web.backend.util;

import javax.el.ELContext;
import javax.faces.context.FacesContext;

import mx.ift.sns.modelo.usu.Usuario;
import mx.ift.sns.web.common.session.LoginBean;

/**
 * Clase de utilidad de usuario.
 * @author X23016PE
 */
public final class UsuarioUtil {

    /** Constante del bean manejado de login. */
    private static final String LOGIN_BEAN = "loginBean";

    /** Constructor por defecto. */
    private UsuarioUtil() {
    }

    /**
     * Función encargada de obtener el usuario de sesión.
     * @return Usuario usuario de sesión
     */
    public static Usuario getUsuario() {
        LoginBean bean = null;
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc != null) {
            ELContext elContext = fc.getELContext();
            bean = (LoginBean) elContext.getELResolver().getValue(elContext, null, LOGIN_BEAN);
        }

        return (bean != null) ? bean.getUsuario() : null;
    }
}
