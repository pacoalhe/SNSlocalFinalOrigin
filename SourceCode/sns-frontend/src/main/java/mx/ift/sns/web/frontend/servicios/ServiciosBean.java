package mx.ift.sns.web.frontend.servicios;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import mx.ift.sns.modelo.usu.Usuario;
import mx.ift.sns.negocio.IConsultaPublicaFacade;
import mx.ift.sns.negocio.usu.IUsuariosService;
import mx.ift.sns.web.common.session.LoginBean;
import mx.ift.sns.web.frontend.common.MensajesFrontBean;

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador de acciones vinculadas al logueo: Chequeo de primer ingreso y chequeo de caducidad de contraseña.
 * @author X51461MO
 */
@ManagedBean(name = "serviciosBean")
@ViewScoped
public class ServiciosBean implements Serializable {

    /** Servicio de Busqueda Publica. */
    @EJB(mappedName = "ConsultaPublicaFacade")
    private IConsultaPublicaFacade ngPublicService;

    /** Servicio de usuarios. */
    @EJB(mappedName = "UsuariosService")
    private IUsuariosService usuariosService;

    /** Bean LoginBean. */
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiciosBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultaFront";
    /**
     * idUsuario introducido.
     */
    private String idUsuario;

    /**
     * Clave nueva.
     */
    private String nuevaClave;
    /**
     * Clave nueva para chequear valided.
     */
    private String nuevaClaveRep;
    /**
     * Flag de activación de descarga planes.
     */
    private boolean descargaActivate = true;
    /**
     * Flag de activación de cambio de clave.
     */
    private boolean nuevaClaveActivate = false;
    /**
     * Usuario registrado.
     */
    private Usuario usuario;
    /**
     * Token de validación para protección CSRF.
     */
    private String token;

    // //////////////POSTCONSTRUCT///////////////////
    /**
     * Postconstructor.
     */
    @PostConstruct
    public void init() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("PostConstruct Servicios: está activado");
        }
        try {
            if (!this.chequeaPrimerIngreso()) {
                this.chequeaCaducidadContrasena();
            }
        } catch (Exception e) {
            LOGGER.error("Error inseperado al chequear primer ingreso y caducidad de contraseña" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }

    }

    // //////////////////////MÉTODOS////////////////////////

    /**
     * Chequea si el usuario logeado ha ingresado por primera vez.
     * @return boolean
     */
    private boolean chequeaPrimerIngreso() {
        boolean esPrimera = false;
        String primeraConexion = null;
        Usuario usu = null;
        try {
            usu = loginBean.getUsuario();
            primeraConexion = usu.getPrimeraConexion();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("El valor de la primera conexión es: {} para el usuario {}", primeraConexion, usu.getId());
            }
            this.setUsuario(usu);
            if (primeraConexion != null) {
                if (primeraConexion.compareTo("1") == 0) {
                    this.setDescargaActivate(false);
                    this.setNuevaClaveActivate(true);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Entrando en cambio de contraseña");
                    }
                    esPrimera = true;

                } else {
                    this.setDescargaActivate(true);
                    this.setNuevaClaveActivate(false);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Entrando descarga de planes");
                    }
                    esPrimera = false;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error al chequear primer ingreso " + e.getMessage());
        }
        return esPrimera;
    }

    /**
     * Crea nueva contraseña cuando se accede por primera vez al login.
     */
    public void crearNuevaClave() {
        try {
            int result = this.usuariosService.crearNuevaClave(this.nuevaClave, this.nuevaClaveRep, this.usuario);
            loginBean.refresh();
            if (result == 0) {
                MensajesFrontBean.addInfoMsg(MSG_ID, "La contraseña se ha actualizado con exito");
                this.setDescargaActivate(true);
                this.setNuevaClaveActivate(false);
            } else if (result == 1) {
                MensajesFrontBean.addErrorMsg(MSG_ID, "La contraseña contiene caracteres no válidos");
            } else if (result == 2) {
                MensajesFrontBean.addErrorMsg(MSG_ID, "Las contraseñas deben ser iguales en ambos campos");
            } else {
                MensajesFrontBean.addErrorMsg(MSG_ID, "ERROR");
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado al crear nueva clave" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }
    }

    /**
     * Método que chequea si la contraseña ha caducado.
     */
    public void chequeaCaducidadContrasena() {
        Usuario usu = null;
        try {
            usu = loginBean.getUsuario();
            Date fechaCadudicad = usu.getFechaCaducidadContrasenna();
            if (fechaCadudicad != null) {
                SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
                Calendar fechaCaducidadCal = Calendar.getInstance();
                fechaCaducidadCal.setTime(fechaCadudicad);
                fechaCaducidadCal.add(Calendar.MONTH, 1);
                Date curDate = new Date();
                Calendar curDateCal = Calendar.getInstance();
                curDateCal.setTime(curDate);
                if (fechaCaducidadCal.equals(curDateCal) || fechaCaducidadCal.before(curDateCal)) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Contraseña caducada. Fecha caducidad: {}, fecha de hoy: {}",
                                formatoFecha.format(fechaCaducidadCal.getTime()),
                                formatoFecha.format(curDateCal.getTime()));
                    }
                    MensajesFrontBean.addErrorMsg(MSG_ID,
                            "Contraseña caducada. Por favor, ingrese una nueva contraseña.");
                    RequestContext.getCurrentInstance().update(":FORM_planes:MSG_ConsultaWebCad");
                    this.setDescargaActivate(false);
                    this.setNuevaClaveActivate(true);
                } else {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("La contraseña no ha caducado. Fecha caducidad: {}, "
                                + "fecha de hoy: {}", formatoFecha.format(fechaCaducidadCal.getTime()),
                                formatoFecha.format(curDateCal.getTime()));
                    }
                    this.setDescargaActivate(true);
                    this.setNuevaClaveActivate(false);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error al chequear caducidad de contraseña" + e.getMessage());
        }
    }

    /**
     * Redirecciona a la página de recuperación de contraseña. Redirección protegida contra CSRF.
     */
    public void goTo() {
        try {
            if (ProteccionCsrf.checkCsrfToken("FORM_recover:csrfTokenRecover")) {
                ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
                context.redirect(context.getRequestContextPath() + "/servicios/recover.xhtml");
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Protección CSRF ok");
                }
            } else {
                LOGGER.warn("Excepción de seguridad CSRF detectada");
            }
        } catch (Exception e) {
            MensajesFrontBean.addErrorMsg(MSG_ID,
                    "Error inesperado al redirigir");
            LOGGER.error("Error inesperado al redirigir");
        }
    }

    // //////////////////////GETTERS Y SETTERS///////////////////////////

    /**
     * Bean LoginBean.
     * @return the loginBean
     */
    public LoginBean getLoginBean() {
        return loginBean;
    }

    /**
     * Bean LoginBean.
     * @param loginBean the loginBean to set
     */
    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    /**
     * Usuario registrado.
     * @return the usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Usuario registrado.
     * @param usuario the usuario to set
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Flag de activación de descarga planes.
     * @return the descargaActivate
     */
    public boolean isDescargaActivate() {
        return descargaActivate;
    }

    /**
     * Flag de activación de descarga planes.
     * @param descargaActivate the descargaActivate to set
     */
    public void setDescargaActivate(boolean descargaActivate) {
        this.descargaActivate = descargaActivate;
    }

    /**
     * Flag de activación de cambio de clave.
     * @return the nuevaClaveActivate
     */
    public boolean isNuevaClaveActivate() {
        return nuevaClaveActivate;
    }

    /**
     * Flag de activación de cambio de clave.
     * @param nuevaClaveActivate the nuevaClaveActivate to set
     */
    public void setNuevaClaveActivate(boolean nuevaClaveActivate) {
        this.nuevaClaveActivate = nuevaClaveActivate;
    }

    /**
     * Clave nueva.
     * @return the nuevaClave
     */
    public String getNuevaClave() {
        return nuevaClave;
    }

    /**
     * Clave nueva.
     * @param nuevaClave the nuevaClave to set
     */
    public void setNuevaClave(String nuevaClave) {
        this.nuevaClave = nuevaClave;
    }

    /**
     * Clave nueva para chequear valided.
     * @return the nuevaClaveRep
     */
    public String getNuevaClaveRep() {
        return nuevaClaveRep;
    }

    /**
     * Clave nueva para chequear valided.
     * @param nuevaClaveRep the nuevaClaveRep to set
     */
    public void setNuevaClaveRep(String nuevaClaveRep) {
        this.nuevaClaveRep = nuevaClaveRep;
    }

    /**
     * idUsuario introducido.
     * @return the idUsuario
     */
    public String getIdUsuario() {
        return idUsuario;
    }

    /**
     * idUsuario introducido.
     * @param idUsuario the idUsuario to set
     */
    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * Token de validación para protección CSRF.
     * @return the token
     */
    public String getToken() {
        FacesContext context = FacesContext.getCurrentInstance();
        // get the session (don't create a new one!)
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        // get the token from the session
        this.token = (String) session.getAttribute("CSRFTOKEN_NAME");
        return this.token;
    }

    /**
     * Token de validación para protección CSRF.
     * @param token the token to set
     */
    public void setToken(String token) {
        this.token = token;
    }

}
