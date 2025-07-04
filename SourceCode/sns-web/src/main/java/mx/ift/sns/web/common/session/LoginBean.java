package mx.ift.sns.web.common.session;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import mx.ift.sns.modelo.usu.Usuario;
import mx.ift.sns.negocio.IConfiguracionFacade;
import mx.ift.sns.negocio.usu.IUsuariosService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean que controla el usuario y su sesion.
 */
@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Mensaje cuando no se encuentra el usuario en BBDD. */
    private static final String USUARIO_DESCONOCIDO = "Usuario desconocido";

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginBean.class);

    /** Usuario en sesion. */
    public static final String ATT_UID = "uid";

    /** Servicio de usuarios. */
    @EJB(mappedName = "UsuariosService")
    private IUsuariosService usuariosService;

    /** Servicio de configuración. */
    @EJB(mappedName = "ConfiguracionFacade")
    private IConfiguracionFacade configuracionFacade;

    /** Usuario logado. */
    private Usuario usuario;

    /** Nombre del usuario. */
    private String username;

    /** timeout inactivo 30m. */
    private Integer idleTimeout = 30 * 60 * 1000;

    /** Tiempo inactivo en la sessión. */
    private long tiempoInactivo = 0;

    /** Flag que indica si hay que iniciarlizar el contador de tiempo inactivo. */
    private boolean inicializarInactividad = false;

    /** Tiempo en segundos entre cada consulta para checkear la sesión. */
    private long intervaloChequeoSesion = 60;

    /** Inicialización de Variables. JSR260, Método invocado al cargar terminar de instanciar el bean. */
    //FJAH 08052025 -- Emulación de usuario real: Paco DEV
    @PostConstruct
    public void init() {
        // Cargar parámetro de tiempo de chequeo
        try {
            String param = configuracionFacade.getParamByName("intervaloChequeoSesion");
            if (param != null) {
                intervaloChequeoSesion = Long.parseLong(param);
            }
        } catch (Exception e) {
            LOGGER.warn("Error cargando intervaloChequeoSesion, usando valor por defecto", e);
        }

        // Emular login local si no hay Principal (solo para entorno local o QA sin contenedor)
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) ec.getRequest();

        if (request.getUserPrincipal() == null) {
            String uid = "dgtic.dds.ext196";

            try {
                usuario = usuariosService.findUsuario(uid);
                if (usuario != null) {
                    username = usuario.getNombre();
                    usuario = usuariosService.login(usuario);

                    HttpSession session = (HttpSession) ec.getSession(true);
                    session.setAttribute(ATT_UID, uid);
                    seteaRolesEnSesion(usuario);

                    LOGGER.info("Usuario real emulado en entorno local: {} ({})", uid, username);
                } else {
                    LOGGER.warn("Usuario '{}' no encontrado en la BD", uid);
                }
            } catch (Exception e) {
                LOGGER.error("Error emulando login con usuario '{}'", uid, e);
            }
        }
    }

    /**
     * Devuelve el nombre del usuario.
     * @return nombre
     */
    public String getUsername() {
        inicializarInactividad = true;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se inicializa la inactividad: ");
        }

        if (usuario == null) {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();

            String uid = null;

            if (((HttpServletRequest) ec.getRequest()).getUserPrincipal() != null) {
                uid = ((HttpServletRequest) ec.getRequest()).getUserPrincipal().getName();
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("uid='{}'", uid);
            }

            if (uid != null) {
                try {

                    usuario = usuariosService.findUsuario(uid);
                    if (usuario != null) {
                        username = usuario.getNombre();

                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("uid='{}' nombre '{}'", uid, username);
                        }

                        usuario = usuariosService.login(usuario);

                        HttpSession session = (HttpSession) ec.getSession(false);
                        session.setAttribute(ATT_UID, uid);
                        seteaRolesEnSesion(usuario);

                        // logueado = true;
                    }else{
                    	LOGGER.info("<--Usuario no encontrado-->");
                    }
                    
                } catch (Exception e) {
                    usuario = null;
                    username = USUARIO_DESCONOCIDO;

                    LOGGER.warn("Usuario '{}' autorizado no existe en BBDD", uid);
                }
            }
        }

        return username;
    }

    /**
     * Invalida la session del usuario logueado.
     * @throws IOException error en la operacion
     */
    public void logout() throws IOException {

        if (usuario != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("logout {}", usuario.getUserid());
            }
            usuariosService.logout(usuario);
        }

        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();

        HttpSession session = (HttpSession) ec.getSession(false);
        session.setAttribute(LoginBean.ATT_UID, null);
        session.invalidate();

        ec.redirect(ec.getRequestContextPath() + "/logout.xhtml");
    }

    /**
     * Usuario inactivo.
     * @throws IOException error
     */
    public void idle() throws IOException {
        LOGGER.debug("idle");

        // MensajesBean.notificacionPendiente("MSG_Notificacion", "Sesion inactiva.");

        try {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect(ec.getRequestContextPath() + "/errores/sesion-expirada.xhtml");

            if (usuario != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("logout timeout {}", usuario.getUserid());
                }

                usuariosService.logout(usuario);
            }

            HttpSession session = (HttpSession) ec.getSession(false);

            session.invalidate();
        } catch (Exception e) {
            LOGGER.error("error", e);
        }
    }

    /**
     * usuario.
     * @return usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * @return the timeout
     */
    public Integer getIdleTimeout() {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("idle timeout {}ms {}s {}m",
                    idleTimeout, idleTimeout / 1000, idleTimeout / (1000 * 60));
        }

        return idleTimeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setIdleTimeout(Integer timeout) {
        this.idleTimeout = timeout;
    }

    /**
     * Refresca el usuario debido a cambios.
     */
    public void refresh() {
        usuario = usuariosService.findUsuario(usuario.getUserid());
    }

    /**
     * FJAH 09052025 refactorización cierre de conexiones
     */
    /**
     * Método que comprueba si ha expirado la sesión.
     * @throws IOException excepción
     */
    /*
    public void isSesionExpirada() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context == null) return;

        ExternalContext ec = context.getExternalContext();
        HttpSession sesion = (HttpSession) ec.getSession(false);

        // Si la sesión ya no existe o el usuario ya es nulo, no hacemos nada
        if (sesion == null || usuario == null) {
            LOGGER.debug("isSesionExpirada: sesión o usuario nulo, no se realiza verificación.");
            return;
        }

        Date fecha = new Date();
        long tiempoSinAccion = (fecha.getTime() - sesion.getLastAccessedTime()) / 1000;

        if (inicializarInactividad || tiempoSinAccion != intervaloChequeoSesion) {
            tiempoInactivo = tiempoSinAccion;
        } else {
            tiempoInactivo += tiempoSinAccion;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Comprobando la sesión: Último acceso hace " + tiempoInactivo
                    + " sg. Tiempo máximo de espera: " + this.idleTimeout + " sg.");
        }

        if (tiempoInactivo >= (sesion.getMaxInactiveInterval())) {
            idle();
        }

        inicializarInactividad = false;
    }

     */

    public void isSesionExpirada() throws IOException {
        HttpSession sesion = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true);

        Date fecha = new Date();
        long tiempoSinAccion = (fecha.getTime() - sesion.getLastAccessedTime()) / 1000;

        if (inicializarInactividad || tiempoSinAccion != intervaloChequeoSesion) {
            tiempoInactivo = tiempoSinAccion;
        } else {
            tiempoInactivo += tiempoSinAccion;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Comprobando la sesión: Último acceso hace " + tiempoInactivo
                    + " sg. Tiempo máximo de espera: " + this.idleTimeout + " sg.");
        }

        if (tiempoInactivo >= (sesion.getMaxInactiveInterval())) {
            idle();
        }

        inicializarInactividad = false;
    }


    /**
     * @return the intervaloChequeoSesion
     */
    public long getIntervaloChequeoSesion() {
        return intervaloChequeoSesion;
    }

    /**
     * FJAH 09052025 refactorización cierre de conexiones
     */
    /*
    @PreDestroy
    public void onDestroy() {
        LOGGER.info("LoginBean destruido");
    }
     */

    /**
     * Guarda en sesión los roles, el id_usuario y el usuario emulado.
     * Puede llamarse después del login forzado/emulado.
     */
    private void seteaRolesEnSesion(Usuario usuario) {
        if (usuario == null) {
            LOGGER.error("No se puede setear roles en sesión porque el usuario es null");
            return;
        }
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session = (HttpSession) ec.getSession(true);

        session.setAttribute("ID_USUARIO", usuario.getId());
        session.setAttribute("ROLES_USUARIO", usuario.getRoles());
        session.setAttribute("USUARIO_OBJ", usuario);

        LOGGER.info("Seteados en sesión: id_usuario={}, roles={}", usuario.getId(), usuario.getRoles());
    }


}
