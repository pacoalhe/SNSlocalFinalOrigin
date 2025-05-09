package mx.ift.sns.web.backend;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import mx.ift.sns.negocio.cpsi.ICodigoCPSIFacade;
import mx.ift.sns.negocio.cpsn.ICodigoCPSNFacade;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;
import mx.ift.sns.web.backend.common.MensajesBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean que controla las notificaciones del usuario.
 */
@ManagedBean(name = "notBean")
@SessionScoped
public class NotificacionesBean implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificacionesBean.class);

    /** Constante html para añadir nuevas líneas. */
    private static final String NUEVA_LINEA = "<br>";

    /** Facade de Numeración Geográfica. */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngFacade;

    /** Facade de Numeración No Geográfica. */
    @EJB(mappedName = "NumeracionNoGeograficaFacade")
    private INumeracionNoGeograficaFacade nngFacade;

    /** Facade de Códigos de Puntos de Señalización Nacional. */
    @EJB(mappedName = "CodigoCPSNFacade")
    private ICodigoCPSNFacade cpsnFacade;

    /** Facade de Códigos de Puntos de Señalización Internacional. */
    @EJB(mappedName = "CodigoCPSIFacade")
    private ICodigoCPSIFacade cpsiFacade;

    /** Indica si es la primera conexión. */
    private boolean primera = true;

    /**
     * Metodo que informa al analista sobre una cesión o liberación próxima (1 o 2 días antes de la ejecución).
     * @param notificaciones
     * @return nada
     **/
    public String getNotPendientes() {
        if (primera) {
            try {

                HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance()
                        .getExternalContext().getRequest();

                if (origRequest.getServletPath().contains("index.xhtml")) {
                    StringBuilder sbf = new StringBuilder();
                    sbf.append("NOTIFICACIONES PENDIENTES").append(NUEVA_LINEA);

                    boolean nuevasNotificaciones = false;
                    boolean hayNotificaciones = false;

                    // Lista de notificación Liberación (Numeración Geográfica)
                    nuevasNotificaciones = addNotificaciones(sbf, "Liberaciones Pendientes (NG)",
                            ngFacade.getNotificacionesLoginLiberacion());
                    hayNotificaciones = hayNotificaciones || nuevasNotificaciones;

                    // Lista de notificación Cesión (Numeración Geográfica)
                    nuevasNotificaciones = addNotificaciones(sbf, "Cesiones Pendientes (NG)",
                            ngFacade.getNotificacionesLoginCesion());
                    hayNotificaciones = hayNotificaciones || nuevasNotificaciones;

                    // Lista de notificaciones de Liberación (Numeración No Geográfica)
                    nuevasNotificaciones = addNotificaciones(sbf, "Liberaciones Pendientes (NNG)",
                            nngFacade.getNotificacionesLoginLiberacion());
                    hayNotificaciones = hayNotificaciones || nuevasNotificaciones;

                    // Lista de notificación Cesión (Numeración No Geográfica)
                    nuevasNotificaciones = addNotificaciones(sbf, "Cesiones Pendientes (NNG)",
                            nngFacade.getNotificacionesLoginCesion());
                    hayNotificaciones = hayNotificaciones || nuevasNotificaciones;

                    // Lista de notificación de Liberaciones de CPSN
                    nuevasNotificaciones = addNotificaciones(sbf, "Liberaciones Pendientes (CPSN)",
                            cpsnFacade.getNotificacionesLoginLiberacion());
                    hayNotificaciones = hayNotificaciones || nuevasNotificaciones;

                    // Lista de notificación de Cesiones de CPSN
                    nuevasNotificaciones = addNotificaciones(sbf, "Cesiones Pendientes (CPSN)",
                            cpsnFacade.getNotificacionesLoginCesion());
                    hayNotificaciones = hayNotificaciones || nuevasNotificaciones;

                    // Lista de notificación de Cesiones de CPSI
                    nuevasNotificaciones = addNotificaciones(sbf, "Cesiones Pendientes (CPSI)",
                            cpsiFacade.getNotificacionesLoginCesion());
                    hayNotificaciones = hayNotificaciones || nuevasNotificaciones;

                    // Lista de notificación de Liberaciones de CPSI
                    nuevasNotificaciones = addNotificaciones(sbf, "Liberaciones Pendientes (CPSI)",
                            cpsiFacade.getNotificacionesLoginLiberacion());
                    hayNotificaciones = hayNotificaciones || nuevasNotificaciones;

                    // Actualizamos el growl con la información
                    if (hayNotificaciones) {
                        MensajesBean.notificacionPendiente("MSG_Notificacion", sbf.toString());
                    }
                } else {
                    LOGGER.info("Se solicita la siguiente ruta: " + origRequest.getServletPath()
                            + ".  No se comprueban las notificaciones pendientes.");
                }

            } catch (Exception e) {
                LOGGER.error("no se puede acceder a notificaciones.", e);
            }
            primera = false;
        }

        return "";
    }

    /**
     * Agrega las notificaiones al buffer de avisos si existen.
     * @param pListaNoficicaciones Lista de nofiticaciones.
     * @param pBuffer Buffer de avisos
     * @param pTitulo Título del bloque de avisos.
     * @return 'True' si existen notificaciones añadidas.
     * @throws Exception en caso de error.
     */
    private boolean addNotificaciones(StringBuilder pBuffer, String pTitulo, List<String> pListaNoficicaciones)
            throws Exception {
        if (!pListaNoficicaciones.isEmpty()) {
            pBuffer.append(NUEVA_LINEA).append(NUEVA_LINEA);
            pBuffer.append("** ").append(pTitulo).append(" **").append(NUEVA_LINEA);
            for (String notificacion : pListaNoficicaciones) {
                pBuffer.append(notificacion).append(NUEVA_LINEA);
            }
            pBuffer.append(NUEVA_LINEA);
        }

        return (!pListaNoficicaciones.isEmpty());
    }
}
