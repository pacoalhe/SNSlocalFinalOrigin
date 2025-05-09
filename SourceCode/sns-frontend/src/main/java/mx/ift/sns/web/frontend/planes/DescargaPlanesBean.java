package mx.ift.sns.web.frontend.planes;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import mx.ift.sns.modelo.pnn.Plan;
import mx.ift.sns.modelo.pnn.TipoPlan;
import mx.ift.sns.modelo.usu.Rol;
import mx.ift.sns.modelo.usu.Usuario;
import mx.ift.sns.negocio.IConsultaPublicaFacade;
import mx.ift.sns.web.frontend.common.MensajesFrontBean;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controla la descarga de planes de numeración, tanto públicos, como privados.
 * @author X51461MO
 */
@ManagedBean(name = "descargaPlanesBean")
@ViewScoped
public class DescargaPlanesBean implements Serializable {

    /** Servicio de Busqueda Publica. */
    @EJB(mappedName = "ConsultaPublicaFacade")
    private IConsultaPublicaFacade ngPublicService;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DescargaPlanesBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultaFront";

    /**
     * Listado de Tipos de planes por rol.
     */
    private List<String> tipoPlan;

    /**
     * Listado de tipos de planes para mostrar.
     */
    private List<String> tipoPlanMostrar = new ArrayList<String>();

    /**
     * Listado con los planes de un rol para mostrar.
     */
    private List<Plan> planesRolMostrar = new ArrayList<Plan>();

    /**
     * Flag de activación de botón de descarga.
     */
    private boolean btnDescargaActivado = false;

    // ///////////POSTCONSTRUCT/////////////////////
    /**
     * Postconstructor.
     */
    @PostConstruct
    public void init() {
        try {
            // Listado de claves de servicio
            if (ngPublicService.getPlanByTipo(TipoPlan.TIPO_PLAN_NG_PUBLICO) == null) {
                this.setBtnDescargaActivado(true);
                MensajesFrontBean.addInfoMsg(MSG_ID, "Plan público no disponible");
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error insperado");
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("PostConstruct Descarga Planes: está activado");
        }
    }

    // ////////////////////////MÉTODOS///////////////////////
    /**
     * Método invocado al pulsar sobre el botón 'Descargar' dentro de la zona privada (usario password requerido).
     * Realiza la descarga del plan de nuemración gegráfica privado.
     * @param planDescarga Plan
     * @return StreamedContent Fichero a Descargar
     */
    public StreamedContent descargarPlan(Plan planDescarga) {
        try {
            if (planDescarga != null) {
                InputStream stream = new ByteArrayInputStream(planDescarga.getFichero());
                StringBuffer docName = new StringBuffer();
                docName.append(planDescarga.getNombre());
                // Definición del tipo de archivo
                String wordMimeType = "application/zip";
                StreamedContent downFile = new DefaultStreamedContent(stream, wordMimeType, docName.toString());
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Archivo descargado correctamente");
                }
                MensajesFrontBean.addInfoMsg(MSG_ID, "Plan descargado correctamente");
                return downFile;
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("No existe achivo para la descarga");
                }
                MensajesFrontBean.addErrorMsg(MSG_ID, "No existe el plan seleccionado");
            }

        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }
        return null;
    }

    /**
     * Método invocado al pulsar sobre el botón de descarga pública. Realiza la descarga del plan de numeración
     * geográfica público.
     * @return StreamedContent Fichero a Descargar
     */
    public StreamedContent descargarPlanPublico() {
        Plan planPublico = null;
        try {
            planPublico = ngPublicService.getPlanByTipo(TipoPlan.TIPO_PLAN_NG_PUBLICO);
            if (planPublico != null) {
                InputStream stream = new ByteArrayInputStream(planPublico.getFichero());
                StringBuffer docName = new StringBuffer();
                docName.append(planPublico.getNombre());
                // Definición del tipo de archivo a descarcar.
                String wordMimeType = "application/zip";
                StreamedContent downFile = new DefaultStreamedContent(stream, wordMimeType, docName.toString());
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Archivo {} de longitud {} descargado correctamente", docName.toString(),
                            docName.length());
                }
                MensajesFrontBean.addInfoMsg(MSG_ID, "Plan descargado correctamente");
                this.setBtnDescargaActivado(false);
                return downFile;

            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("No existe achivo para la descarga");
                    this.setBtnDescargaActivado(true);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado: el archivo de descarga es nulo o no existe " + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error en la descarga");
        }
        return null;
    }

    /**
     * Busca el usuario logueado en sesión y obtiene sus roles.
     */
    private void getUserRol() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        String uid = null;
        if (((HttpServletRequest) ec.getRequest()).getUserPrincipal() != null) {
            uid = ((HttpServletRequest) ec.getRequest()).getUserPrincipal().getName();
        }
        Usuario usu = ngPublicService.findUsuario(uid);
        List<Rol> roles = usu.getRoles();
        for (Rol r : roles) {
            getTipoPlanes("" + r.getId());

        }
    }

    /**
     * Setea la lista de tipos de planes en función del rol del usuario logueado.
     * @param idRol String
     */
    private void getTipoPlanes(String idRol) {
        try {
            this.setTipoPlan(ngPublicService.findAllTipoPlanByRol(idRol));

            for (String p : this.tipoPlan) {
                if (!this.tipoPlanMostrar.contains(p)) {
                    this.tipoPlanMostrar.add(p);
                }
            }

        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error en la búsqueda de tipos de planes");
            }
        }

    }

    /**
     * Devuelve las lista de planes para descargar en función del rol de usuario logeado.
     * @return List<Plan>
     */
    public List<Plan> getPlanes() {
        try {
            this.getUserRol();
            Plan plan = null;
            for (String t : this.tipoPlanMostrar) {
                if (t != null) {
                    plan = ngPublicService.getPlanByTipo(t);
                    if (plan != null) {
                        this.planesRolMostrar.add(plan);
                    }
                }
            }
            this.setPlanesRolMostrar(this.planesRolMostrar);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al optener los planes por rol " + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }
        return this.planesRolMostrar;

    }

    /**
     * Obtiene la fecha de generación del plan público listo para descargar.
     * @return String
     */
    public String getFechaPlanPublicoGenerado() {
        String fechaPlanPublicoString = "";
        try {
            Plan planPublico = ngPublicService.getPlanByTipo(TipoPlan.TIPO_PLAN_NG_PUBLICO);
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
            fechaPlanPublicoString = formatoFecha.format(planPublico.getFechaGeneracion());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("La fecha de generación del plan público es: {}", fechaPlanPublicoString);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado al obtener la fecha  de generación del plan público" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }
        return fechaPlanPublicoString;
    }

    /**
     * Obtiene la fecha de generación del plan privado listo para descargar.
     * @param planPrivado Plan
     * @return String
     */
    public String getFechaPlanPrivadoGenerado(Plan planPrivado) {
        String fechaPlanPrivadoString = "";
        try {
            if (planPrivado != null) {
                SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
                fechaPlanPrivadoString = formatoFecha.format(planPrivado.getFechaGeneracion());
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("La fecha de generación del plan privado es: {}", fechaPlanPrivadoString);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado al obtener la fecha  de generación del plan público" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }
        return fechaPlanPrivadoString;
    }

    // ///////////////////////////////////GETTERS Y SETTERS//////////////////////////////

    /**
     * Flag de activación de botón de descarga.
     * @return the btnDescargaActivado
     */
    public boolean isBtnDescargaActivado() {
        return btnDescargaActivado;
    }

    /**
     * Flag de activación de botón de descarga.
     * @param btnDescargaActivado the btnDescargaActivado to set
     */
    public void setBtnDescargaActivado(boolean btnDescargaActivado) {
        this.btnDescargaActivado = btnDescargaActivado;
    }

    /**
     * Listado de Tipos de planes por rol.
     * @return the tipoPlan
     */
    public List<String> getTipoPlan() {
        return tipoPlan;
    }

    /**
     * Listado de Tipos de planes por rol.
     * @param tipoPlan the tipoPlan to set
     */
    public void setTipoPlan(List<String> tipoPlan) {
        this.tipoPlan = tipoPlan;
    }

    /**
     * Listado con los planes de un rol para mostrar.
     * @return the planesRolMostrar
     */
    public List<Plan> getPlanesRolMostrar() {
        return planesRolMostrar;
    }

    /**
     * Listado con los planes de un rol para mostrar.
     * @param planesRolMostrar the planesRolMostrar to set
     */
    public void setPlanesRolMostrar(List<Plan> planesRolMostrar) {
        this.planesRolMostrar = planesRolMostrar;
    }

}
